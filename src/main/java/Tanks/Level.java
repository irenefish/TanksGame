package Tanks;

import processing.core.PImage;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Represents a level in the game, including its layout, background, and additional graphical elements like trees.
 * This class is responsible for loading and managing the level data from files.
 */
public class Level{

    public char layout[][];
    public PImage background;
    public PImage tree;
    public int[] foregroundcolour;
    public String DEFAULT_PATH = "src/main/resources/Tanks/";

    /**
     * Constructs a Level object, loading its layout, background, and optional tree elements.
     *
     * @param filename The filename of the level layout file.
     * @param backImage The filename of the background image.
     * @param forecolor A comma-separated string representing the RGB values of the foreground color.
     * @param treeImage The filename of the tree image, or null if no trees are needed.
     * @param app The PApplet context used for loading images (usually the main app instance).
     */
    public Level(String filename, String backImage, String forecolor, String treeImage, App app){
        this.layout = new char[20][28];
        for (int i = 0; i < 20; i++){
            for (int j = 0; j < 28; j++){
                layout[i][j] = ' ';
            }
        }
        readFile(filename);
        this.background = app.loadImage(DEFAULT_PATH + backImage);
        String[] colors = forecolor.split(",");
        this.foregroundcolour = new int[3];
        foregroundcolour[0] = Integer.parseInt(colors[0]);
        foregroundcolour[1] = Integer.parseInt(colors[1]);
        foregroundcolour[2] = Integer.parseInt(colors[2]);

        if (treeImage == null){
            this.tree = app.loadImage(DEFAULT_PATH + "tree1.png");
        }else{
            this.tree = app.loadImage(DEFAULT_PATH + treeImage);
        }
    }

    /**
     * Reads the level layout from a file.
     *
     * @param filename The file path to read the layout from.
     */
    private void readFile(String filename){
        try{
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line = null;
            int lineNumber = 0;
            while ((line = br.readLine()) != null){
                for (int i = 0; i < line.length(); i++){
                    layout[lineNumber][i] = line.charAt(i);
                }
                lineNumber += 1;
                if(lineNumber == 20){
                    break;
                }
            }br.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

