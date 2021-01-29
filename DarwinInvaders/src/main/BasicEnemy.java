/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.Random;

/**
 *
 * @author Asx
 */
public class BasicEnemy extends GameObject {
    
    private ObjHandler projectiles;
    private int value;
    private int cycleCooldown;
    private int behaviourStep;
    private LinkedList<Integer> behaviour;
    
    private Random r;
    
    public BasicEnemy(int _x, int _y, ObjHandler handler)
    {
        super(_x, _y);
        this.projectiles = handler;
        value = 0;
        cycleCooldown = 0;
        behaviourStep = 0;
        
        r = new Random();
        
        behaviour = new LinkedList<Integer>();
        // To już robimy gdzie indziej...
        //for(int i=0; i<GlobalVars.behaviourSize; i++) behaviour.add(r.nextInt(3));
    }
    
    public int getValue()
    {
        return value;
    }
    
    public void setValue(int _value)
    {
        this.value = _value;
    }
    
    public LinkedList<Integer> getBehaviour()
    {
        LinkedList<Integer> list = new LinkedList<Integer>();
        int n = behaviour.size();
        for(int i=0; i<n; i++)
        {
            list.add(behaviour.get(i));
        }
        return list;
    }
    
    public void setBehaviour(LinkedList<Integer> list)
    {
        behaviour.clear();
        int n = list.size();
        for(int i=0; i<n; i++)
        {
            behaviour.add(list.get(i));
        }
    }
    
    public void shoot()
    {
        projectiles.add(new Bullet(x+((GlobalVars.enemySizeX/2)-(GlobalVars.bulletSizeX/2)), y+GlobalVars.bulletSizeY-1, GlobalVars.bulletSpeed, true));
    }
    
    @Override
    public Rectangle getHitbox()
    {
        return new Rectangle(x, y, GlobalVars.enemySizeX, GlobalVars.enemySizeY);
    }

    @Override
    public void tick() 
    {
        if(cycleCooldown < GlobalVars.cooldownEnemyCycle)
            cycleCooldown++;
        else
        {
            cycleCooldown = 0;
            value++;
            //zmień zachowanie - następny krok
            behaviourStep++;
            if(behaviourStep >= GlobalVars.behaviourSize) behaviourStep = 0;
            
            if(behaviourStep == 0)
            {
                this.speedX = GlobalVars.enemySpeed;
                this.speedY = 0;
            }
            else
                if(behaviourStep == 1)
                {
                    this.speedX = -GlobalVars.enemySpeed;
                    this.speedY = 0;
                }
                else
                    {
                        this.speedY = GlobalVars.enemySpeed;
                        this.speedX = 0;
                    }
        }
        //ruch - ogranicz w poziomie; jeśli dotrze do dołu ekranu - ...
        x += speedX;
        y += speedY;
        
        if(x <0) x = 0;
        else
            if(x > GlobalVars.gameWidth - GlobalVars.enemySizeX)
                x = GlobalVars.gameWidth - GlobalVars.enemySizeX;
        if(y > GlobalVars.gameHeight) this.kill();
    }

    @Override
    public void render(Graphics g) 
    {
       g.setColor(Color.MAGENTA);
       g.fillRect(x, y, GlobalVars.enemySizeX, GlobalVars.enemySizeY);
    }
    
}
