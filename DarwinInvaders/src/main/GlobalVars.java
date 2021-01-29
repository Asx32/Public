/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

/**
 * Klasa grupująca kluczowe "stałe" wartości wykorzystywane w programie.
 * 
 * @author Asx
 */
public class GlobalVars {
    
    public static final int gameHeight = 800;
    public static final int gameWidth = 480;
    
    //Game Variables

    //początkowa pozycja "statku" gracza
    public static int playerPosX = gameWidth/2;
    public static int playerPosY = gameHeight -80;
    
    //rozmiar "statku" gracza
    public static int playerSizeX = 32;
    public static int playerSizeY = 32;
    
    //rozmiar wrogów
    public static int enemySizeX = 16;
    public static int enemySizeY = 16;
    
    //rozmiar pocisków
    public static int bulletSizeX = 8;
    public static int bulletSizeY = 8;
    
    //szybkość ruchu obiektów w grze
    public static int playerSpeed = 3;
    public static int enemySpeed = 1;
    public static int bulletSpeed = 4;
    
    //kluczowe atrybuty "statku"
    public static int cooldownPlayerWeapon = 60;
    public static int playerHP = 3;
    
    //czasy odnowienia dla wrogów
    public static int cooldownEnemyCycle = 120;
    public static int cooldownEnemyWeapon = 50;
    public static int cooldownEnemySpawn = 60;

    //Liczba wrogów w fali
    public static int enemyCount = 5;
    
    //do algorytmu gen.
    public static int behaviourSize = 3;
    public static int probMutation = 10;
    public static int probMix = 50;
    //public static int mixRatio = 50;//?? piwot krzyżowania? NIE! Będzie losowy!
    
    //do alg. gen. fal
    public static int wavesTillCross = 4; //co ile fal dokonywać krzyżowania?
}
