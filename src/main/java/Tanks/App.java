package Tanks;

import org.checkerframework.checker.units.qual.A;
import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import java.io.*;
import java.util.*;

public class App extends PApplet {
    
    // initial
    public static final int CELLSIZE = 32;
    public static final int CELLHEIGHT = 32;
    public static final int CELLAVG = 32;
    public static final int TOPBAR = 0;
    public static int WIDTH = 864; //CELLSIZE*BOARD_WIDTH;
    public static int HEIGHT = 640; //BOARD_HEIGHT*CELLSIZE+TOPBAR;
    public static final int BOARD_WIDTH = WIDTH/CELLSIZE;
    public static final int BOARD_HEIGHT = 20;
    public static final int INITIAL_PARACHUTES = 1;
    public static final int FPS = 30;
    public String configPath;
    public static Random random = new Random();

    // mine
    private HashMap<String, String> playerColours = new HashMap<String, String>();
    private Level[] levels;
    private char[][] layout;
    private int[] foregroundcolour;
    private PImage background;
    private PImage tree;

    private float window[];
    private int frameCount = 0;
	private int currentPlayer = 0;
    private int currentlevel = 0;

    private ArrayList<Tank> tanks = new ArrayList<>();
    private ArrayList<Bullet> bullets = new ArrayList<>();

    private int windSpeed;

    private boolean isLeft = false;
    private boolean isRight = false;
    private boolean isUp = false;
    private boolean isDown = false;
    private boolean powerUp = false;
    private boolean powerDown = false;

    private boolean endLevel = false;
    private boolean endGame = false;
    private boolean finalDisplayed = true;
    private int endLevelFrameCounter = 0;
    private final int endLevelDelayFrames = 30; // Delay for 1 second at 30 FPS

    // private int scoreDisplayIndex = 0;
    // private int scoreDisplayDelay = 21;
    // private int scoreDisplayCounter = 0;

    
    /**
     * Main class for the Tanks game application built.
     */
    public App() {
        this.configPath = "config.json";
    }

    /**
     * Initialise the setting of the window size.
     */
	@Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * Initializes game components, loads resources, and sets up the initial game environment.
     */
	@Override
    public void setup() {
        frameRate(FPS);
        
        window = new float[896];
        currentPlayer = 0;
        readConfig();
        loadLevels(levels[currentlevel]);

        for (int i = 0; i < 20; i++){
            for (int j = 0; j < 28; j++){
                if (layout[i][j] == 'X'){
                    for (int k = 0; k < CELLSIZE; k++){
                        window[j*CELLSIZE + k] = i * CELLSIZE;
                    }
                }
            }
        }
        for (int i = 0; i < 864; i++){
            window[i] = cal(i, window, CELLSIZE);
        }
        for (int i = 0; i < 864; i++){
            window[i] = cal(i, window, CELLSIZE);
        }

        windSpeed = random.nextInt(71) - 35;
    }

    /**
     * Reads the game configuration from a JSON file and initializes game levels.
     */
    private void readConfig(){
        try{
            FileReader fr = new FileReader(this.configPath);
            JSONObject jobj1 = new JSONObject(fr);
            JSONArray ja = jobj1.getJSONArray("levels");

            levels = new Level[ja.size()];
            for (int i = 0; i < ja.size(); i++){
                JSONObject jobj2 = ja.getJSONObject(i);
                if (jobj2.hasKey("trees")){
                    levels[i] = new Level(jobj2.getString("layout"), jobj2.getString("background"), jobj2.getString("foreground-colour"), jobj2.getString("trees"), this);
               
                }else{
                    levels[i] = new Level(jobj2.getString("layout"), jobj2.getString("background"), jobj2.getString("foreground-colour"), null, this);
                }
            }

            JSONObject jobj3 = jobj1.getJSONObject("player_colours");
            for (Object s: jobj3.keys()){
                String k = (String) s;
                String v = jobj3.getString(k);
                playerColours.put(k, v);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Configures and loads the specified level with terrain, obstacles, and tank positions.
     * @param level Level object containing level-specific configurations.
     */
    private void loadLevels(Level level){
        this.background = level.background;
        this.layout = level.layout;
        this.tree = level.tree;
        this.foregroundcolour = level.foregroundcolour;
        
        HashMap<String, Integer> previousScores = new HashMap<>();
        for (Tank t : tanks) {
            previousScores.put(t.type, t.score);
        }

        tanks.clear();

        for (int j = 0; j < 28; j++){
            for (int i = 0; i < 20; i++){
                char layoutChar = layout[i][j];
                String tankType = Character.toString(layoutChar).trim();

                if (!tankType.isEmpty() && layoutChar != ' ' && layoutChar != 'T' && layoutChar != 'X') {
                    if (playerColours.containsKey(tankType)) {
                        Tank t = new Tank(j, i, tankType, playerColours, window);

                        if (previousScores.containsKey(tankType)) {
                            t.score = previousScores.get(tankType); // Restore score for the new level
                        }
                        tanks.add(t);
                    }
                }
            }
        }

        if (!tanks.isEmpty()) {
            tanks.get(0).selected = true;
        }
    }

    /**
     * Receive key pressed signal from the keyboard to control player actions such as moving and firing.
     */
	@Override
    public void keyPressed(KeyEvent event){
        if (event.getKeyCode() == 37){
            isLeft = true;
        }else if (event.getKeyCode() == 39){
            isRight = true;  
        }else if (event.getKeyCode() == 38){
            isUp = true;
        }else if (event.getKeyCode() == 40){
            isDown = true; 
        }else if (event.getKeyCode() == 87){
            powerUp = true;   
        }else if (event.getKeyCode() == 83){
            powerDown = true; 
        }

        Tank t = tanks.get(currentPlayer);
        if (event.getKeyCode() == 32 && tanks.get(currentPlayer).isAlive){
            if (endLevel) {
                NextLevel();
            } else {                
                float v = 1 + t.power/100 * 8;

                float turretLength = 15;
                float bulletpixelX = t.pixelX + turretLength*(float)Math.sin(t.radian);
                float bulletpixelY = t.pixelY - turretLength*(float)Math.cos(t.radian);
    
                Bullet b = new Bullet(v, bulletpixelX, bulletpixelY, t.colors, t.radian, t);
                bullets.add(b);
    
            }
        }

        if (event.getKeyCode() == 82) {
            if (endGame) {
                restartGame();
            }
            if (t.score >= 20 && t.health < 100) {
                t.health = Math.min(100, t.health + 20);
                t.score -= 20;
            }
        }

        if (event.getKeyCode() == 70) {
            if (t.score >= 10) {
                t.fuel += 200;
                t.score -= 10;
            }
        }

        if (event.getKeyCode() == 80) {
            if (t.score >= 15) {
                t.parachutes += 1;
                t.score -= 15;
            }
        }
        
        // if (event.getKeyCode() == 80) {
        //     if (t.score >= 15) {
        //         t.parachutes += 1;
        //         t.score -= 15;
        //     }
        // }
    }


    /**
     * Receive key released signal from the keyboard.
     */
	@Override
    public void keyReleased(KeyEvent event){
        if (event.getKeyCode() == 37){
            isLeft = false;
        }else if (event.getKeyCode() == 39){
            isRight = false;  
        }else if (event.getKeyCode() == 38){
            isUp = false;
        }else if (event.getKeyCode() == 40){
            isDown = false; 
        }else if (event.getKeyCode() == 87){
            powerUp = false;   
        }else if (event.getKeyCode() == 83){
            powerDown = false; 
        }
    }

    // @Override
    // public void mousePressed(MouseEvent e) {
    //     //TODO - powerups, like repair and extra fuel and teleport
    // }

    // @Override
    // public void mouseReleased(MouseEvent e) {

    // }


    /**
     * Calculates a weighted average of terrain height over a specified range to smooth out terrain rendering.
     * @param start Index to start averaging from.
     * @param window Array of terrain heights.
     * @param size Number of elements to include in the average.
     * @return The calculated average height.
     */
    private float cal(int start, float[] window, int size){
        float result = 0;

        for (int i = 0; i < size; i++){
            if (start + i < window.length) {
                result += window[start + i];
            }
        }
        result = result/size;
        return result;
    }
    
    /**
     * Updates the state of each tank.
     */
    public void updateGameState() {
        for (Tank tank : tanks) {
            if (tank.health <= 0 && tank.isAlive) {
                tank.tankaliveornot(tank.health);
            }
        }
        taketurn();
    }

    /**
     * Processes player's turn transitions, ensuring that each player gets a turn if they are still alive in the game.
     */
    private void taketurn(){
        if (!tanks.isEmpty() && currentPlayer < tanks.size()) {
            tanks.get(currentPlayer).selected = false;
        }

        int attempts = 0;
        currentPlayer = (currentPlayer + 1) % tanks.size(); 
    
        while (!tanks.get(currentPlayer).isAlive && attempts < tanks.size()) {
            currentPlayer = (currentPlayer + 1) % tanks.size(); 
            attempts++;
        }        if (tanks.get(currentPlayer).isAlive && !tanks.isEmpty()) {
            tanks.get(currentPlayer).selected = true;
            windSpeed += random.nextInt(11) - 5;
        }
    }

    /**
     * Draws the in-game scoreboard displaying player scores and tank status.
     * @param tanks List of all tanks participating in the game.
     */
    public void drawScoreboard(ArrayList<Tank> tanks){
        Collections.sort(tanks, new Comparator<Tank>() {
            @Override
            public int compare(Tank t1, Tank t2) {
                return t1.type.compareTo(t2.type);
            }
        });

        int x1 = 710, y1 = 50, width = 145, height = 20;
        stroke(0);
        strokeWeight(4);
        noFill();
        rect(x1, y1, width, height);
        textSize(18);
        fill(0);
        text("Scores", x1 + 5, y1 + 17);

        noFill();
        int x2 = 710, y2 = 70;
        rect(x2, y2, width, height * 4);

        for (Tank t: tanks){
            y2 += 18;
            fill(t.colors[0], t.colors[1], t.colors[2]);
            textSize(18);
            text("Player " + t.type, x2 + 5, y2);

            fill(0);
            text(t.score, x2 + 100, y2);

        }
        noStroke();
    }

    /**
     * Displays the current wind conditions affecting bullet trajectories.
     * @param wind Current wind speed and direction.
     */
    public void drawWind(Integer wind){ 
        PImage windImage;

        if (wind < 0){
            windImage = loadImage("src/main/resources/Tanks/wind-1.png");
        }else{
            windImage = loadImage("src/main/resources/Tanks/wind.png");
        }
        image(windImage, 765, 0, 48, 48);
        text(wind.toString(), 820, 30);
    }

    /**
     * Draws the fuel status for the current player, showing remaining fuel levels.
     */
    public void drawFuel() {
        PImage fuelImage;
        fuelImage = loadImage("src/main/resources/Tanks/fuel.png");

        image(fuelImage, 170, 5, 24, 24);
        fill(0);
        text(tanks.get(currentPlayer).fuel, 200, 25);
    }

    /**
     * Draws the parachute status indicating the number of parachutes left for the current player.
     */
    public void drawParachute() {
        PImage parachuteImage;
        parachuteImage = loadImage("src/main/resources/Tanks/parachute.png");

        image(parachuteImage, 170, 35, 24, 24);
        fill(0);
        text(tanks.get(currentPlayer).parachutes, 200, 55);
    }

    /**
     * Renders the health and power bars for the active tank, providing visual feedback on tank status.
     * @param tanks List of tanks to display status bars for.
     */
    public void drawBar(ArrayList<Tank> tanks){
        int barlength = 150;

        textSize(18);
        fill(0);
        text("Health:", 370, 30);
        text("Power:", 370, 60);

        for (Tank t: tanks){
            if (t.selected && t.isAlive){
                //health bar
                fill(255, 255, 255);
                stroke(0);
                strokeWeight(3);
                rect(440, 10, barlength, 25);

                float healthWidth = (t.health / 100.0f) * barlength;

                strokeWeight(2);
                fill(t.colors[0], t.colors[1], t.colors[2]);
                rect(440, 10, healthWidth, 25);

                fill(0);
                text(t.health, 595, 30);
                text((int)t.power, 440, 60);
                // player's turn
                text("Player " + t.type + "'s turn", 15,25);

                // powerbar
                float powerWidth = (t.power / 100.0f) * barlength;

                noFill();
                strokeWeight(4);
                stroke(144,141,137);
                rect(440, 10, powerWidth, 25);

                fill(255, 0, 0);
                strokeWeight(1);
                noStroke();
                rect(440+powerWidth, 5, 1, 35);
            }
        }        
        noStroke(); 
    }

    /**
     * Checks if the win condition has been met, which typically involves one tank remaining.
     * @return true if the win condition is met, false otherwise.
     */
    public boolean checkwin(){

        int alive_tank = 0;
        for (Tank t : tanks) {
            if (t.isAlive) {
                alive_tank++;
            }
        }       
        return (alive_tank <= 1);
    }
    
    /**
     * Primary loop for updating game mechanics each frame, handling movement, actions, and game progression.
     */
    private void gameUpdateAndDraw() {
        frameCount += 1;
        if (frameCount % 60 == 0) {
            updateGameState();
            frameCount = 0;
        }
        Tank t = tanks.get(currentPlayer);
    
        handleTankActions(t);
    
        drawGameElements();
    }    

    /**
     * Manages the actions and movements of a tank based on player inputs.
     * @param t The tank to manage.
     */
    private void handleTankActions(Tank t) {
        if (t.isAlive) {
            if (isLeft && t.fuel > 0) {
                t.pixelX = Math.max(0, t.pixelX - 2);
                t.fuel--;
            } else if (isRight && t.fuel > 0) {
                t.pixelX = Math.min(window.length - CELLSIZE, t.pixelX + 2);
                t.fuel--;
            }

            handleTankRotation(t);
            adjustTankPower(t);
        }
    }
    
    /**
     * Adjusts the tank's turret angle based on player input.
     * @param t The tank whose turret angle is being adjusted.
     */
    private void handleTankRotation(Tank t) {
        if (isUp) {
            t.radian -= 0.1;
            t.radian = max(t.radian, -PI / 2);
        } else if (isDown) {
            t.radian += 0.1;
            t.radian = min(t.radian, PI / 2);
        }
    }
    
    /**
     * Adjusts the tank's power setting, used for firing projectiles.
     * @param t The tank to adjust power for.
     */
    private void adjustTankPower(Tank t) {
        if (powerUp) {
            t.power += 1.2;
        } else if (powerDown) {
            t.power = max(0, (int)(t.power - 1.2));
        }
        t.power = constrain(t.power, 0, t.health);
    }

    /**
     * Draws all dynamic and static game elements including the terrain, tanks, and any on-screen projectiles.
     */
    private void drawGameElements() {
        fill(foregroundcolour[0], foregroundcolour[1], foregroundcolour[2]);
        for (int x = 0; x < BOARD_WIDTH * CELLSIZE; x++) {
            int height = (int) window[x];
            rect(x, height, 1, HEIGHT - height);
        }
        drawTreesAndTanks();

        for (Bullet bullet : bullets) {
            bullet.draw(this, windSpeed);
            bullet.collide(window, tanks);
        }
    }
    
    /**
     * Draws trees and tanks onto the game field.
     */
    private void drawTreesAndTanks() {
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 28; j++) {
                if (layout[i][j] == 'T') {
                    this.image(tree, j * CELLSIZE - CELLSIZE / 2, window[j * CELLSIZE] - CELLSIZE, CELLSIZE, CELLSIZE);
                }
            }
        }
    
        for (Tank tank : tanks) {
            if (tank.isAlive) {
                int baseX = (int) Math.max(0, Math.min(tank.pixelX, window.length - 1));
                float terrainHeight = window[baseX];
                tank.updatePosition(terrainHeight);
                tank.draw(this);
            }
        }
    }

    /**
     * Displays the final scores at the end of the game, highlighting the winner.
     */
    private void displayFinalScores() {
        if (endGame) {    
            tanks.sort((t1, t2) -> Integer.compare(t2.score, t1.score));
            Tank winner = tanks.get(0);    
            
            int x1 = 240, y1 = 150, width = 384, height = 40;
    
            // Draw the score displaybackground
            fill(winner.colors[0], winner.colors[1], winner.colors[2], 60);  // Slightly transparent
            rect(x1, y1, width, height * 4);
                        
            stroke(0);
            strokeWeight(4);
            noFill();
            rect(x1, y1, width, height);
            textSize(26);
            fill(0);
            text("Final Scores", x1 + 20, y1 + 32);
    
            noFill();
            int x2 = 240, y2 = 190;
            rect(x2, y2, width, height * 3);
    
            // Display each player's score
            for (Tank tank : tanks) {
                y2 += 28;
                fill(tank.colors[0], tank.colors[1], tank.colors[2]);
                textSize(24);
                text("Player " + tank.type, x1 + 20, y2);
    
                fill(0);
                text(tank.score, x2 + 330, y2);    
            }

            textSize(26);
            fill(winner.colors[0], winner.colors[1], winner.colors[2]);
            text("Player " + winner.type + " wins!", x1 + 20, 120);  
            
        }
    }

    /**
     * Displays a message when a level is completed and prepares for transition to the next level.
     */
    private void LevelCompleted() {
        fill(0, 0, 0, 127);  // Semi-transparent black
        rect(0, 0, width, height);

        // Display transition message
        fill(255);
        textSize(32);
        text("Level Complete! Next Level!", 230, height / 2);
        noFill();
    }

    /**
     * Proceed to the next level or concludes the game if all levels have been completed.
     */
    private void NextLevel() {
        if (currentlevel < 2) {
            currentlevel++;
            setup();  // Setup the next level
            endLevel = false;  // Reset the flag
            endLevelFrameCounter = 0;
        } else {
            endGame = true; // Set the game as ended
            println("Game Completed!");
            displayFinalScores(); // Display final scores at the end
        }
    }

    /**
     * Resets the game to initial settings.
     */
    private void restartGame() {
        endGame = false;
        currentlevel = 0;
        endLevel = false;
        setup();  // Reset all initial game settings
        tanks.forEach(tank -> tank.score = 0); //Reset score
    }

    
    /**
     * Main Drawing Function. Draws the entire game scene including tanks, terrain, bullets, and UI elements.
     */
    @Override
    public void draw() {
        if (!endGame) {
            clear();
            this.image(background, 0, 0);
            
            drawGameElements();  // Draw game elements
    
            if (endLevel) {
                endLevelFrameCounter++;
                if (endLevelFrameCounter < endLevelDelayFrames) {
                    LevelCompleted();

                } else {
                    NextLevel(); 
                }
            } else {
                if (checkwin()) {
                    endLevel = true;
                    endLevelFrameCounter = 0;
                } else {
                    gameUpdateAndDraw();
                }
            }
    
            // UI elements
            drawScoreboard(tanks);
            drawWind(windSpeed);
            drawBar(tanks);
            drawFuel();
            drawParachute();

        } else {
            if (finalDisplayed) {
                displayFinalScores();  // Final scores only update once
                finalDisplayed = false;  // Disable further updates
            }
        }
    }

    /**
     * The main method to start the game application.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        PApplet.main("Tanks.App");
    }

}