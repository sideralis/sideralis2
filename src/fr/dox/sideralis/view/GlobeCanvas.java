package fr.dox.sideralis.view;
/*
 * GlobeCanvas.java
 *
 * Created on 21 novembre 2005, 19:20
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

import java.io.IOException;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import fr.dox.sideralis.location.Position;
import fr.dox.sideralis.math.MathFunctions;

/**
 * A class derived from Canvas and used to display a globe to select graphically the position of the user
 *
 * @author Bernard
 */
public class GlobeCanvas extends Canvas {
    private Image img;                      // The image representing the globe
    private double x,y;                     // Coordinate in the image
    private Position myPosition;            // Real globe coordinate of the user
    private double lScreen,hScreen;         // Length and heigh of mobile screen
    private double lImage,hImage;           // Length and heigh of Image
    private double xc,yc;                   // Coordinate of cursor on mobile screen
    private double xi,yi;                   // Coordinate top left corner of screen in image
    private double x0,y0;                   // Coordinate of lat = long = 0 in the image
    private int xPressed,yPressed;          // For touch screen support
    private boolean drag;                   // For touch screen support
    
    private final double SIZE_LONG_PIXEL=14.3/10;                           // Number of pixel on the image for 1 degree in longitude
    private final double TAB_SIZE_POS[] = {15,14,16,16,18,20,23,29,38};     // Number of pixel for 10, 20, ... degrees in latitude
    private final double TAB_SIZE_NEG[] = {14,15,15,16,18,20,23,29,39};     // Idem but for negative degrees
    
    /**
     * Creates a new instance of GlobeCanvas
     * @param p a reference to the user position
     */
    public GlobeCanvas(Position p) {
        //setFullScreenMode(true);
        try {
            img = Image.createImage("/world.PNG");
        } catch (IOException e) {
            System.out.println(e.toString());
            System.out.println(e.getMessage());
        }
        lImage = img.getWidth();
        hImage = img.getHeight();
        lScreen = getWidth();
        hScreen = getHeight();
        myPosition = new Position();
        myPosition.setLatitude(p.getLatitude());
        myPosition.setLongitude(p.getLongitude());
        x0 = 243;                                           // Coordinate for long = 0, be carefull, this is not really the center of image
        y0 = 189;                                           // Coordinate for lat = 0
        
        getXYFromLatLong();
    }
    /**
     * Display the canvas
     * @param g the graphics object
     */
    public void paint(Graphics g) {
        // Draw background image (globe)
        g.drawImage(img, -(int)xi,-(int)yi, Graphics.LEFT | Graphics.TOP);
        // Draw cursor
        g.setColor(0, 0, 255);
        g.drawRect((int)xc,(int)yc,2,2);
        // Draw coordinate (lat and long)
        getLongLatFromXY();
        g.setColor(255,0,0);
        if (yc<getHeight()/2) {
            g.drawString(MathFunctions.convert2deg(myPosition.getLongitude(), false),0,getHeight(),Graphics.BOTTOM|Graphics.LEFT);
            g.drawString(MathFunctions.convert2deg(myPosition.getLatitude(), false),getWidth(),getHeight(),Graphics.BOTTOM|Graphics.RIGHT);
        } else {
            g.drawString(MathFunctions.convert2deg(myPosition.getLongitude(), false),0,0,Graphics.TOP|Graphics.LEFT);
            g.drawString(MathFunctions.convert2deg(myPosition.getLatitude(), false),getWidth(),0,Graphics.TOP|Graphics.RIGHT);
        }

    }
    /**
     * Function called each time a key is pressed
     * @param keyCode the key code of the pressed key
     */
    public void keyPressed(int keyCode) {
        if (getGameAction(keyCode) == UP ) {
            decYCursor();
        }
        if (getGameAction(keyCode) == DOWN ) {
            incYCursor();            
        }
        if (getGameAction(keyCode) == RIGHT ) {
            incXCursor();            
        }
        if (getGameAction(keyCode) == LEFT ) {
           decXCursor();
        }
        repaint();
    }
    /**
     * Function called each time a key is released
     * @param keyCode the key code of the pressed key
     */
    public void keyReleased(int keyCode) {
    }
    /**
     * Function called each time a key is kept pressed
     * @param keyCode the key code of the pressed key
     */
    protected void keyRepeated(int keyCode) {
        keyPressed(keyCode);
    }

    protected void pointerDragged(int x, int y) {
        int dist;

        if (drag == false) {
            dist = (x-xPressed)*(x-xPressed)+(y-yPressed)*(y-yPressed);
            if (dist > 4) {
                drag = true;
            }
        }
        if (drag == true) {

        }
    }

    protected void pointerPressed(int x, int y) {
        xPressed = x;
        yPressed = y;
    }

    protected void pointerReleased(int x, int y) {
        if (drag == false) {
            this.x = xi+x;
            this.y = yi+y;
            getScreenAndCursorCoordinateFromXY();
            repaint();
        }
        drag = false;
    }

    /**
     * Increment x
     */
    public void incXCursor() {
        if (x<lImage)
            x++;
        getScreenAndCursorCoordinateFromXY();
    }
    /**
     * Decrement x
     */
    public void decXCursor() {
        if (x>0)
            x--;
        getScreenAndCursorCoordinateFromXY();
    }
    /**
     * Increment y
     */
    public void incYCursor() {
        if (y<hImage)
            y++;
        getScreenAndCursorCoordinateFromXY();
    }
    /**
     * Decrement y
     */
    public void decYCursor() {
        if (y>0)
            y--;
        getScreenAndCursorCoordinateFromXY();
    }
    /**
     * Calculates the position of the cursor and position of the screen in the image from position of the user on the globe
     */
    void getScreenAndCursorCoordinateFromXY() {
        // Calculate xc and xi
        if (x<lScreen/2) {
            xc = x;
            xi = 0;
        } else if (x>(lImage-lScreen/2)) {
            xc = x-lImage+lScreen;
            xi = lImage-lScreen;
        } else {
            xc = lScreen/2;
            xi = x - lScreen/2;
        }
        
        // Calculate yx and yi
        if (y<hScreen/2) {
            yc = y;
            yi = 0;
        } else if (y>(hImage-hScreen/2)) {
            yc = y-hImage+hScreen;
            yi = hImage-hScreen;
        } else {
            yc = hScreen/2;
            yi = y - hScreen/2;
        }
    }
    /**
     * Return the long and lat position from the x and y coordinate
     */
    private void getLongLatFromXY() {
        myPosition.setLongitude((x-x0)/SIZE_LONG_PIXEL);
        
        double yr;
        yr = y-y0;
        if (yr<0) {
            yr = -yr;
            int i=0;
            while(yr>TAB_SIZE_POS[i]) {
                yr -= TAB_SIZE_POS[i];
                i++;
            }
            myPosition.setLatitude(i*10 + yr/TAB_SIZE_POS[i]*10);
        } else {
            int i=0;
            while(yr>TAB_SIZE_NEG[i]) {
                yr -= TAB_SIZE_NEG[i];
                i++;
            }
            myPosition.setLatitude(-i*10 - yr/TAB_SIZE_POS[i]*10);
        }
    }
    /**
     * Return the x and y coordinate in the image from the lat and long position
     */
    private void getXYFromLatLong() {
        // X coordinate in the image
        x = x0 + myPosition.getLongitude()*SIZE_LONG_PIXEL;
        
        // Y coordinate in the image
        int i;
        y = y0;
        for (i=0;i<(int)(Math.abs(myPosition.getLatitude())/10);i++) {
            if (myPosition.getLatitude()<0)
                y += TAB_SIZE_NEG[i];
            else
                y -= TAB_SIZE_POS[i];
        }
        if (myPosition.getLatitude()<0)
            y -= (myPosition.getLatitude()%10)*TAB_SIZE_NEG[i]/10;
        else
            y -= (myPosition.getLatitude()%10)*TAB_SIZE_POS[i]/10;
        
        getScreenAndCursorCoordinateFromXY();
    }
    /**
     *  Set position
     * @param pos a reference to the new position
     */
    public void setPosition(Position pos) {
        myPosition.setLatitude(pos.getLatitude());
        myPosition.setLongitude(pos.getLongitude());
        getXYFromLatLong();
    }
    /**
     * Get Latitude
     * @return latitude
     */
    public double getLatitude() {
        return myPosition.getLatitude();
    }
    /**
     * Get Longitude
     * @return Longitude
     */
    public double getLongitude() {
        return myPosition.getLongitude();
    }
    
}
