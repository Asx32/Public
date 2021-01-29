/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.util.Random;

/**
 * Główna klasa programu, odpowiedzialna za działanie gry.
 * @author Asx
 */
public class GameController extends Canvas implements Runnable {
    
    private static final int WIDTH = GlobalVars.gameWidth, HEIGHT = GlobalVars.gameHeight;
    private Thread thread;
    private boolean running = false;
    
    private Player player;
    private ObjHandler enemyHdlr;
    private ObjHandler playerProjectileHdlr;
    private ObjHandler enemyProjectileHdlr;
    private HUD hud;
    private CrossingManager crMan;
    private Deployer deployer;
    
    private int enemyWeaponCd;
    private int enemySpawnCd;
    private int enemySpawnCounter;
    private boolean newWave;
    private boolean newGame;
    
    private GameState gameState;
    
    private Menu mainMenu;
    private Menu pauseMenu;
    
    public GameController()
    {
        enemyHdlr = new ObjHandler();
        playerProjectileHdlr = new ObjHandler();
        enemyProjectileHdlr = new ObjHandler();
        player = new Player(GlobalVars.playerPosX, GlobalVars.playerPosY, playerProjectileHdlr);
        
        hud = new HUD();
        
        crMan = new CrossingManager();
        deployer = new Deployer();
        
        enemyWeaponCd =0;
        enemySpawnCd = 0;
        enemySpawnCounter = 0;
        newWave = false;
        newGame = true;
        
        gameState = GameState.MENU;
        mainMenu = new Menu(this, Menu.MenuType.MAIN);
        pauseMenu = new Menu(this, Menu.MenuType.PAUSE);
        
        this.addKeyListener(new KeyInput(player));
        this.addMouseListener(mainMenu);
        this.addMouseListener(pauseMenu);
        
        new Window(WIDTH, HEIGHT, "Darwin Invaders", this);
    }
    
    /**
     * Metoda dostępowa zwracająca stan gry
     * @return Stan gry: GAME, MENU, PAUSE lub DEAD
     */
    public GameState getState()
    {
        return this.gameState;
    }
    
    /**
     * Metoda dostępowa ustawiajaca stan gry
     * @param state Nowy stan gry
     */
    public void setState(GameState state)
    {
        this.gameState = state;
    }
    
    public synchronized void start()
    {
        thread = new Thread(this);
        thread.start();
        running = true;
        
        this.requestFocus();
    }
    
    public synchronized void stop()
    {
        try
        {
            thread.join();
            running = false;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Głóna pętla programu.
     * Działąnie gry niezależne od częstotliwości odświeżania.
     */
    @Override
    public void run()
    {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        while(running)
        {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while(delta >= 1)
            {
                tick();
                delta--;
            }
            if(running)
                render();
            frames++;
            
            if(System.currentTimeMillis() - timer >1000)
            {
                timer +=1000;
                //System.out.println("FPS: " +frames);
                frames = 0;
            }
        }
        stop();
    }
    
    /**
     * "Zeruje" pola obiektu na potrzeby ponownego rozpoczęcia gry od nowa w ramach danego uruchomienia programu.
     */
    public void reset()
    {
        if(newGame) newGame = false;
        else
        {
            player.reset();
            enemyHdlr.reset();
            playerProjectileHdlr.reset();
            enemyProjectileHdlr.reset();

            crMan.clear();
            crMan = new CrossingManager();
            deployer.reset();

            enemyWeaponCd =0;
            enemySpawnCd = 0;
            enemySpawnCounter = 0;
            newWave = false;
        }
    }
    
    private void tick()
    {
        if(gameState == GameState.MENU)
        {
            mainMenu.tick();
            //to trzeba zmienić
            //gameState = GameState.GAME;
        }
        else
        if(gameState == GameState.GAME)
        {
            Random r = new Random(); //do wyrzucenia?
            if(enemyHdlr.getSize() == 0)
            {
                if(!newWave)
                {
                    newWave = true;
                    enemySpawnCounter = 0;
                    crMan.crossSpecimens();
                    deployer.nextWave();
                    //System.out.println("Nowa fala");
                }
            }
            else //enemy attack
            {
                int s = enemyHdlr.getSize();
                enemyWeaponCd++;
                if(enemyWeaponCd >= s*GlobalVars.cooldownEnemyWeapon)
                {
                    enemyWeaponCd = 0;
                    ((BasicEnemy)enemyHdlr.get(r.nextInt(s))).shoot();
                }
            }

            if(enemySpawnCounter < GlobalVars.enemyCount)
            {
                //System.out.println("Liczba zespawnowanych wrogów: " +enemySpawnCounter);
                enemySpawnCd++;
                if(enemySpawnCd == GlobalVars.cooldownEnemySpawn)
                {
                    //System.out.println("Tworzenie wroga nr " +enemySpawnCounter);
                    enemySpawnCd = 0;
                    BasicEnemy newEnemy = new BasicEnemy(deployer.getX(enemySpawnCounter),0, enemyProjectileHdlr);
                    crMan.injectGenes(newEnemy, enemySpawnCounter);
                    enemyHdlr.add(newEnemy);  
                    enemySpawnCounter++;
                    //Aktualizacja wartości fali
                    //Tu zakładamy, że wartość ta ma być równa maksymalnej liczbie wrogów obecnych w grze *10
                    int v = enemyHdlr.getSize()*10;
                    if(v > deployer.getValue()) deployer.setValue(v);
                }
            }
            else
                if(newWave)
                {
                    newWave = false;
                    crMan.clear();
                }

            //główna część...
            player.tick();
            enemyHdlr.tick();
            enemyProjectileHdlr.tick();
            playerProjectileHdlr.tick();

            //kolizje!
            //kolizje z graczem
            boolean colDetected = false;
            int n = enemyHdlr.getSize();
            int i = 0;
            while((i<n) && !colDetected)
            {
                GameObject tmpObj = enemyHdlr.get(i);
                if(player.getHitbox().intersects(tmpObj.getHitbox()))
                {
                    player.kill();
                    tmpObj.kill();
                    hud.update(player);
                    colDetected = true;
                }
                i++;
            }

            n =  enemyProjectileHdlr.getSize();
            i = 0;
            while((i<n) && !colDetected)
            {
                GameObject tmpObj = enemyProjectileHdlr.get(i);
                if(player.getHitbox().intersects(tmpObj.getHitbox()))
                {
                    player.kill();
                    tmpObj.kill();
                    hud.update(player);
                    colDetected = true;
                }
                i++;
            }

            //kolizja wróg-pocisk
            n = playerProjectileHdlr.getSize();
            for(int j=0; j<n; j++)
            {
                GameObject tmpBullet = playerProjectileHdlr.get(j);
                colDetected = false;
                int m = enemyHdlr.getSize();
                i = 0;
                while((i<m) && !colDetected)
                {
                    GameObject tmpObj = enemyHdlr.get(i);
                    if(tmpBullet.getHitbox().intersects(tmpObj.getHitbox()))
                    {
                        tmpBullet.kill();
                        tmpObj.kill();
                        colDetected = true;
                    }
                    i++;
                }
            }

            //czyszczenie list z "martwych" obiektów
            if(enemyHdlr.getSize() >0) crMan.collectSpecimens(enemyHdlr);
            enemyHdlr.clearDead(); //najpierw dodaj do krzyżowania!
            enemyProjectileHdlr.clearDead();
            playerProjectileHdlr.clearDead();        
            //tu: obsługa śmierci "gracza"
            if(!player.isAlive()) gameState = GameState.DEAD;
            //pauza... na około...
            if(player.pauseRequested())
            {
                gameState = GameState.PAUSE;
                //player.requestPause();
            }
        }
        else
        if(gameState == GameState.PAUSE)
        {
            pauseMenu.tick();
            if(player.pauseRequested()) gameState = GameState.GAME;
        }
        else
        if(gameState == GameState.DEAD)
        {
            //tu coś się zadzieje, a potem...
            gameState = GameState.MENU;
        }
    }
    
    private void render()
    {
        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null)
        {
            this.createBufferStrategy(3);
            return;
        }
        
        Graphics graphics = bs.getDrawGraphics();
        //tu napisz zawartość do wyświetlania!
        graphics.setColor(Color.black);
        graphics.fillRect(0, 0, WIDTH, HEIGHT);
            
        if(gameState != null)
        switch (gameState) {
            case MENU:
                mainMenu.render(graphics);
                break;
            case GAME:
                player.render(graphics);
                enemyHdlr.render(graphics);
                enemyProjectileHdlr.render(graphics);
                playerProjectileHdlr.render(graphics);
                hud.render(graphics);
                break;
            case PAUSE:
                pauseMenu.render(graphics);
                break;
            case DEAD:
                //renderuj komunikat...
                break;
            default:
                break;
        }
        
        graphics.dispose();
        bs.show();
    }
    
    public static void main(String args[])
    {
        new GameController();
    }
}
