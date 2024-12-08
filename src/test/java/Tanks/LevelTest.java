package Tanks;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

class LevelTest {
    Level level;
    App app; // Assuming App class has necessary methods for loading

    @BeforeEach
    void setUp() {
        app = new App(); // Mock the app context needed for loading resources
        level = new Level("level1.txt", "background.png", "255,255,255", "tree.png", app);
    }

}





