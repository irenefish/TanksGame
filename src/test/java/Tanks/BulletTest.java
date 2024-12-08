package Tanks;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import java.util.*;

public class BulletTest {
    private Bullet bullet;
    private float[] window;
    private ArrayList<Tank> tanks;
    private Tank tank;

    @BeforeEach
    void setUp() {
        window = new float[800]; // Mock terrain heights
        tanks = new ArrayList<>();
        tank = new Tank(100, 100, "Player1", new HashMap<String, String>() {{
            put("Player1", "255,0,0");
        }}, window);
        bullet = new Bullet(10, 100, 100, new int[]{255, 0, 0}, (float)Math.PI / 4, tank);
    }

    @Test
    void testBulletMovement() {
        bullet.vx = 1; 
        bullet.vy = 1; 
        float initialX = bullet.pixelX;
        float initialY = bullet.pixelY;

        bullet.pixelX += bullet.vx;
        bullet.pixelY += bullet.vy;

        assertEquals(initialX + 1, bullet.pixelX, "Should move by 1 units.");
        assertEquals(initialY + 1, bullet.pixelY, "Should move by 1 units.");
    }
}
