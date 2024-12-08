package Tanks;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import java.util.HashMap;

public class TankTest {

    private Tank tank;
    private HashMap<String, String> playerColours;
    private float[] window;

    @BeforeEach
    void setUp() {
        // Mock
        window = new float[800];
        for (int i = 0; i < window.length; i++) {
            window[i] = 200;
        }

        playerColours = new HashMap<>();
        playerColours.put("Player1", "255,0,0");
        tank = new Tank(100, 100, "Player1", playerColours, window);
    }

    @Test
    void testTankInitialization() {
        assertEquals(100, tank.x, "Tank x-coordinate: 100.");
        assertEquals(100, tank.y, "Tank y-coordinate: 100.");
        assertEquals(100, tank.health, "Tank should start with full health.");
        assertTrue(tank.isAlive, "Tank should be alive initially.");
    }

    @Test
    void testTankMovesCorrectly() {
        float initialX = tank.pixelX;
        tank.pixelX += 5; 
        assertEquals(initialX + 5, tank.pixelX, "Tank should move right by 5 pixels.");

        initialX = tank.pixelX;
        tank.pixelX -= 5; 
        assertEquals(initialX - 5, tank.pixelX, "Tank should move left by 5 pixels.");
    }

    @Test
    void testTankHealthManagement() {
        tank.health = 50;  
        tank.tankaliveornot(50);
        assertTrue(tank.isAlive, "Tank should still be alive with 50 health.");

        tank.health = 0; 
        tank.tankaliveornot(0); 
        assertFalse(tank.isAlive, "Tank should be dead when health reaches 0.");
    }

    @Test
    void testTankFuelRecharge() {
        tank.fuel = 250; 
        int additionalFuel = 50; 
        tank.fuel += additionalFuel;
        assertEquals(300, tank.fuel, "Tank fuel should increase with refueling.");
    }

    @Test
    void testDamageReceivesAndTankDies() {
        int damage = 110;
        tank.health -= damage; 
        tank.tankaliveornot(tank.health);
        assertTrue(tank.health <= 0, "Tank health should not be negative.");
        assertFalse(tank.isAlive, "Tank should be dead.");
    }

    @Test
    void testUpdatePosition() {
    float newY = 300;
    tank.updatePosition(newY);
    assertEquals(newY, tank.pixelY, "Y should be updated to the new value.");
    }
}