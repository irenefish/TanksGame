package Tanks;

import java.util.*;

/**
 * Represents a projectile in the game, handling its dynamics, collision detection, and rendering.
 */
public class Bullet {
    public double v, a;
    public double vx, vy;

    public float pixelX, pixelY;
    public int[] color;
    public float r;
    public boolean alive;
    public boolean isExplosion = false;
    public int count = 0;
    public Tank ownself;
    
    /**
     * Constructs a bullet and calculates its initial velocity components.
     *
     * @param v Initial speed of the bullet.
     * @param pixelX Initial x-coordinate of the bullet.
     * @param pixelY Initial y-coordinate of the bullet.
     * @param color RGB color array for the bullet.
     * @param radian Angle of the turret firing the bullet, in radians.
     * @param ownself The tank that fired the bullet.
     */
    public Bullet(float v, float pixelX, float pixelY, int[] color, float radian, Tank ownself){
        this.v = v;
        this.vx = v * Math.sin(radian);
        this.vy = -v * Math.cos(radian);
        this.pixelX = pixelX;
        this.pixelY = pixelY;
        this.color = color;
        this.r = 3;
        this.alive = true;
        this.a = 0.12;
        this.ownself = ownself;
    }

    /**
     * Handles collision detection and effects between the bullet and the terrain or tanks.
     *
     * @param window Array representing the height of the terrain across the map.
     * @param tanks List of all tanks in the game for checking potential hits.
     */
    public void collide(float[] window, ArrayList<Tank> tanks) {
        if (!alive) return;
    
        float prevPixelX = pixelX - (float)vx;
        float prevPixelY = pixelY - (float)vy;
    
        int nearestX = Math.round(pixelX);
    
        // Check collision
        if (checkCollision(prevPixelX, pixelX, prevPixelY, pixelY, window)) {
            triggerCollision(nearestX, window, tanks);
        }
    }
    
    private boolean checkCollision(float startX, float endX, float startY, float endY, float[] window) {
        int stepX = startX < endX ? 1 : -1;
        float deltaY = (endY - startY) / (Math.abs(endX - startX) + 1);
    
        float y = startY;
        for (int x = Math.round(startX); x != Math.round(endX) + stepX; x += stepX) {
            int roundedY = Math.round(y);
            if (x >= 0 && x < window.length && Math.abs(window[x] - roundedY) < 1) {
                return true;
            }
            y += deltaY;
        }
        return false;
    }
    
    private void triggerCollision(int collisionX, float[] window, ArrayList<Tank> tanks) {
        isExplosion = true;
        alive = false;
    
        float impactDepth = 30.0f; 
        applyExplosion(collisionX, impactDepth, window);
    
        // Additional explosion effects
        for (Tank tank : tanks) {
            if (tank.isAlive){
                double distance = Math.sqrt((collisionX - tank.pixelX) * (collisionX - tank.pixelX) + (pixelY - tank.pixelY) * (pixelY - tank.pixelY));
                if (distance <= 30) {
                    int damage = 60 - (int)distance * 2;
                    tank.health -= damage;
                    if (tank.health < 0) {
                        damage += tank.health;  // Adjust damage to not exceed tank's remaining health
                    }
                    if (damage > 0 && tank != ownself) {
                        ownself.score += damage; // Update score only if damage is positive
                    }
                    tank.tankaliveornot(tank.health);
                }
            }
        }
    }
    
    /**
     * Applies an explosive impact at the point of bullet collision, modifying the terrain.
     *
     * @param impactCenterX X-coordinate of the impact center.
     * @param impactDepth Depth of the impact on the terrain.
     * @param window Array representing the height of the terrain across the map.
     */
    public void applyExplosion(int impactCenterX, float impactDepth, float[] window) {
        int impactRadius = 30;
        
        for (int i = Math.max(0, impactCenterX - impactRadius); i <= Math.min(window.length - 1, impactCenterX + impactRadius); i++) {
            double distance = Math.abs(i - impactCenterX);
            double depthFactor = (impactRadius - distance) / impactRadius;
            window[i] += impactDepth * Math.pow(depthFactor, 2);
        }

        // Ensure no floating terrain
        for (int i = impactCenterX - impactRadius; i <= impactCenterX + impactRadius && i < window.length - 1; i++) {
            if (i > 0 && window[i] > window[i + 1]) {
                window[i] = window[i + 1];  // To ensure no floating parts
            }
        }    
    }

    /**
     * Renders the bullet or its explosion on the screen.
     *
     * @param app Reference to the main application window.
     * @param windSpeed Current wind speed affecting the bullet's trajectory.
     */
    public void draw(App app, int windSpeed) {
        int totalExplosionFrames = 6;
    
        if (isExplosion == true) {
            float growthFactor = (float) count / totalExplosionFrames;
    
            float redRadius = 30 * growthFactor;   
            float orangeRadius = 15 * growthFactor; 
            float yellowRadius = 6 * growthFactor;  
    
            app.noStroke();
    
            app.fill(255, 0, 0);
            app.ellipse(pixelX, pixelY, redRadius * 2, redRadius * 2);
    
            app.fill(250, 95, 0);
            app.ellipse(pixelX, pixelY, orangeRadius * 2, orangeRadius * 2);
    
            app.fill(255, 255, 0);
            app.ellipse(pixelX, pixelY, yellowRadius * 2, yellowRadius * 2);
    
            count += 1;
            if (count >= totalExplosionFrames) {
                count = 0;
                isExplosion = false;
            }
        }
    
        if (alive == true) {
            app.noStroke();
            
            app.fill(color[0], color[1], color[2]);
            app.ellipse(pixelX, pixelY, 2 * r, 2 * r);
    
            app.fill(0);
            app.ellipse(pixelX, pixelY, 1, 1);

            double ax = windSpeed * 0.03/30;
    
            // Projectile Motion
            pixelX += vx;
            pixelY += vy;
            vy += a;
            vx += ax;
        }
    }
    
}
