package Tanks;

import java.util.HashMap;

/**
 * Represents a tank in the game with all associated properties and behaviors.
 */
public class Tank {
    public int x, y;
    public float pixelX, pixelY;
    public int[] colors;
    public String type;
    public boolean selected;
    public float radian;
    public int health;
    public float power;
    public int parachutes;
    public int fuel;
    // public boolean deswithpara = false;
    // public boolean deswithoutpara = false;
    public float[] window;
    public boolean isAlive = true;
    public int score;
    public boolean isHit = false;
    public boolean isUsingLargeProjectile = false;

    /**
     * Constructs a new Tank object with specified properties.
     * 
     * @param x Horizontal grid coordinate of the tank
     * @param y Vertical grid coordinate of the tank
     * @param type Type of the tank, used for identifying the tank type
     * @param playerColours HashMap containing color settings for different tank types
     * @param window Array representing the terrain heights across the game map
     */
    public Tank(int x, int y, String type, HashMap<String, String> playerColours, float window[]){
        this.x = x;
        this.y = y;
        this.pixelX = x*32;
        this.pixelY = window[x];
        this.type = type;
        this.window = window;
        this.score = 0;

        colors = new int[3];
        String colorStr = playerColours.get(type);
        String[] c = colorStr.split(",");
        colors[0] = Integer.parseInt(c[0]);
        colors[1] = Integer.parseInt(c[1]);
        colors[2] = Integer.parseInt(c[2]);
        this.selected = false;

        this.radian = 0;
        this.health = 100;
        this.power = 50;
        this.parachutes = 3; 
        this.fuel = 250;
    }
    
    /**
     * Updates the vertical position of the tank based on new terrain height.
     * 
     * @param newY New vertical position for the tank
     */
    public void updatePosition(float newY) {
        this.pixelY = newY;
    }

    /**
     * Determines whether the tank is alive based on its health.
     * 
     * @param health Current health of the tank
     * @return true if the tank is alive, false otherwise
     */
    public boolean tankaliveornot(int health){
        if (this.health <= 0){
            this.isAlive = false;
        }
        return this.isAlive;
    }

    // /**
    //  * Checks if the tank is airborne by comparing its position with the terrain height.
    //  * 
    //  * @param window Array representing the terrain heights
    //  * @return true if the tank is airborne, false otherwise
    //  */
    // public boolean inair(float[] window){
    //     if (this.pixelY < window[(int)pixelX]){
    //         return true;
    //     }
    //     return false;
    // }

    /**
     * Draws the tank on the game canvas.
     * 
     * @param app Reference to the main application drawing this tank
     */
    public void draw(App app){
        app.noStroke();

        if (isAlive){
            // tank base
            app.fill(colors[0], colors[1], colors[2]);
            app.rect(pixelX - 13, pixelY, 26, 5);
            app.rect(pixelX - 8, pixelY - 5, 16, 5);

            // turret
            app.fill(0, 0, 0);
            app.pushMatrix();
            app.translate(pixelX, pixelY);
            app.rotate(radian);
            app.rect(-2, -20, 4, 15);
            app.popMatrix();

            //arrow 
            if (selected == true){
                app.fill(0, 0, 0);
                app.rect(pixelX, pixelY - 160, 2, 60);
                app.stroke(0);
                app.strokeWeight(2);
                app.line(pixelX - 11, pixelY - 120, pixelX + 1, pixelY - 100);
                app.line(pixelX + 1, pixelY - 100, pixelX + 13, pixelY - 120);
            }    

            // if (deswithpara == true){
            //     PImage paraImage = app.loadImage("src/main/resources/Tanks/parachute.png");
            //     this.pixelY += 2;
            //     if (this.pixelY >= window[(int)pixelX]){
            //         deswithpara = false;
            //     }
            //     app.image(paraImage, pixelX, pixelY, 32, 32);
            // }

            // if (deswithoutpara == true){
            //     this.pixelY += 4;
            //     this.health -= 4;
            //     if (this.pixelY >= window[(int)pixelX]){
            //         deswithoutpara = false;
            //     }
            // }

        }
    }
}
