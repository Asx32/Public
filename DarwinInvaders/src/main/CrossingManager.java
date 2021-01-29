/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.LinkedList;
import java.util.Random;

/**
 * Klasa odpowiadajaca za krzyżowanie wzorców zachowań wrogów
 * @author Asx
 */
public class CrossingManager {
    private LinkedList<LinkedList<Integer>> behaviours;
    private LinkedList<Integer> values;
    
    public CrossingManager()
    {
        //System.out.println("Tworzę obsługę krzyżowania");
        behaviours = new LinkedList<LinkedList<Integer>>();
        values = new LinkedList<Integer>();
        
        Random r = new Random();
        for(int i=0; i<GlobalVars.enemyCount; i++)
        {
            behaviours.add(new LinkedList<Integer>());
            values.add(100);
            for(int j=0; j<GlobalVars.behaviourSize; j++)
            {
                behaviours.get(i).add(r.nextInt(3));
            }
        }
        //System.out.println("Liczba zachowań: " +behaviours.size());
    }
    
    /**
     * Pobiera "martwych" wrogów i zapisuje ich zachowanie do późniejszego krzyżowania
     * @param handler Kontener przechowujący wrogów
     */
    public void collectSpecimens(ObjHandler handler)
    {
        //System.out.println("Pobieram osobniki");
        int s = 0;
        int n = handler.getSize();
        for(int i=0; i<n; i++)
        {
            GameObject spec = handler.get(i);
            if(!spec.isAlive()) //pobieramy tylko te, które już odpadły z gry
            {
                behaviours.add(((BasicEnemy)spec).getBehaviour());
                values.add(((BasicEnemy)spec).getValue());
                s++;
            }
        }
        //if(s>0) System.out.println("Pobrano " +s+ " osobników");
    }
    
//    public void injectGenes(ObjHandler handler)
//    {
//        int n = behaviours.size(); //powinno być równe handler.getSize()
//        for(int i=0; i<n; i++)
//        {
//            ((BasicEnemy)handler.get(i)).setBehaviour(behaviours.get(i));
//        }
//        behaviours.clear();
//        values.clear();
//    }

    /**
     * Wprowadza do podanego obiektu listę zachowań ze wskazanej pozycji
     * @param spec Obiekt do którego zostaną wprowadzone zachowania
     * @param no Nr obiektu/pozycji z listy zachowań
     */ 
    public void injectGenes(GameObject spec, int no)
    {
        //System.out.println("Wprowadzam geny do osobnika " +no+ " z " +behaviours.size());
        ((BasicEnemy) spec).setBehaviour(behaviours.get(no));
    }
    
    /**
     * Usuwa z list stare elementy
     */
    public void clear()
    {
        //System.out.println("Czyszczenie list zachowań");
        int n = behaviours.size();
        //if(n < GlobalVars.enemyCount) n = GlobalVars.enemyCount;
        
        for(int i=0; i<n; i++) 
        {
            behaviours.get(0).clear();
            behaviours.remove(0);
            values.remove(0);
        }
        //behaviours.clear();
        //values.clear();
    }
    
    /**
     * Krzyżowanie w aktualnej populacji
     */
    public void crossSpecimens()
    {
        //System.out.println("Krzyżuję osobniki");
        LinkedList<LinkedList<Integer>> tmpPop = new LinkedList<LinkedList<Integer>>();
        LinkedList<Integer> tmpVal = new LinkedList<Integer>();
        
        Random rand = new Random();
        
        int n = behaviours.size();
        while(n>1)
        {
            //losuj dwa klucze
            int x1 = rand.nextInt(n);
            int x2 = rand.nextInt(n);
            if(x1!=x2)	//jeśli różne...
            {
                LinkedList<Integer> a = behaviours.get(x1);
                LinkedList<Integer> b = behaviours.get(x2);
                //losuj czy krzyżują
                if((rand.nextInt(100)+1)<=GlobalVars.probMix)
                {
                    //znacznik pozycji
                    int pos =0;
                    //miejsce cięcia
                    int cut = rand.nextInt(GlobalVars.behaviourSize-1)+1;
                    //listy tymczasowe
                    LinkedList<Integer> na = new LinkedList<Integer>();
                    LinkedList<Integer> nb = new LinkedList<Integer>();
                    //przeniesienie elementów...
                    for(int i=0; i<GlobalVars.behaviourSize; i++)
                    {
                        if(i<cut)
                        {
                            //a -> na, b-> nb
                            na.add(a.get(i));
                            nb.add(b.get(i));
                        }
                        else
                        {
                            //a -> nb, b-> na
                            na.add(b.get(i));
                            nb.add(a.get(i));
                        }
                    }
                    a.clear();
                    a = na;
                    b.clear();
                    b = nb;
                    tmpPop.add(a);
                    tmpVal.add(values.get(x1));
                    tmpPop.add(b);
                    tmpVal.add(values.get(x2));
                    //dodaj do tab. tymczasowej, usuń z populacji, zmniejsz zakres
                    behaviours.remove(x1);
                    values.remove(x1);
                    if(x1<x2) x2--;	//korekta, bo drugą wartość usuwamy z pomniejszonej tablicy
                    behaviours.remove(x2);
                    values.remove(x2);
                    n -= 2;
                }
            }
        }
        //przepisz nowe osobniki z powrotem do populacji
        n = tmpPop.size();
        for(int k=0; k<n; k++)
        {
            behaviours.add(tmpPop.get(k));
            values.add(tmpVal.get(k));
        }
        tmpPop.clear();
        tmpVal.clear();
        //mutacja
        n = behaviours.size();
        for(int i=0; i<n; i++) //dla każdego osobnika
        {
            for(int j=0; j<GlobalVars.behaviourSize; j++)  //dla każdego genu
                if((rand.nextInt(100)+1)<=GlobalVars.probMutation)
                {
                    behaviours.get(i).set(j, rand.nextInt(3));
                }
        }
        //selekcja
        double[] chances = new double[n];
        double valSum = 0.0;
        for(int i=0; i<n; i++)
        {
            chances[i] = values.get(i);
            valSum += chances[i];
        }
        //oblicz prawdopodobieństwo wylosowania
        for(int i=0; i<n; i++)
            chances[i] /= valSum;
        //przekształć na dystrybuantę
        for(int i=1; i<n; i++)
            chances[i] += chances[i-1]; 
        //losowanie do następnej populacji/pokolenia(?)
        for(int i=0; i<n; i++)
        {
            double attempt = rand.nextDouble(); //losuj 0..1
            int pick =-1;	//wybrany element
            int j = 0;		//aktuajnie rozpatrywany element
            //wybierz pierwszy element, którego wart. dystrybuanty > od wylosowanej wart.
            //przeglądaj kolejne elementy do momentu wyboru lub do końca tablicy
            while((pick<0)&&(j<n))
            {
                    if(chances[j]>=attempt) pick = j;
                    j++;
            }
            //jeśli nie dokonano wyboru - przypisz ostatni element
            //Może zajść, jeśli dystrybuanta nie zsumuje się do 1.0
            if(pick<0) pick = n-1;
            tmpPop.add(behaviours.get(pick));
        }
        //wyczyść populacje i przepisz elementy z tab. pomocniczej
        behaviours.clear();
        for(int i=0; i<n; i++) behaviours.add(tmpPop.get(i));
        tmpPop.clear();
    }
}
