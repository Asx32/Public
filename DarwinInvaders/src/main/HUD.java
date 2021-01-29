/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Do wyświetlania grafiki pomocniczej w trakcie gry.
 * Do rozbudowy.
 * @author Asx
 */
public class HUD {
    
    private int playerHP;
    
    public HUD()
    {
        playerHP = GlobalVars.playerHP;
    }
    
    /**
     * Aktualizacja HUD.
     * Obecnie wykorzystujemy tylko dane (HP) "statku" gracza.
     * @param p
     */
    public void update(Player p)
    {
        playerHP = p.getHP();
    }
    
    public void tick()
    {
        
    }
    
    public void render(Graphics g)
    {
        g.setColor(Color.GREEN);
        for(int i=0; i<playerHP; i++)
        {
            g.fillRect(15+25*i, 15, 16, 16);
        }
    }
}
