package org.sybila.parasim.support.visualization;

import processing.core.PApplet;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class Grid2D extends PApplet {
    
    private static int windowHeight;
    private static int windowWidth;
    private static int zeroX;
    private static int zeroY;
   
    public float normalizeX(float coord) {
        return  (float) zeroX + coord;
    }
    
    public float normalizeY(float coord) {
        return  (float)zeroY - coord;
    }
    
    @Override
    public void setup() {
        size(windowWidth, windowHeight);
        background(255);
        stroke(200);
        line(zeroX, 0, zeroX, windowHeight);
        line(0, zeroY, windowWidth, zeroY);
    }
        
    public static void setupGrid(int windowWidth, int windowHeight, int zeroX, int zeroY) {
        Grid2D.windowWidth = windowWidth;
        Grid2D.windowHeight = windowHeight;
        Grid2D.zeroX = zeroX;
        Grid2D.zeroY = zeroY;
    }
}
