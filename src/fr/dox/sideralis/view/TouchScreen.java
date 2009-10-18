/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.dox.sideralis.view;

import java.io.IOException;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 *
 * @author Bernard
 */
public class TouchScreen {
    /** Position of floating bar for pointer */
    private int xTouchScreen, yTouchScreen;
    /** Size if floating bar for pointer */
    private int heightTouchScreen, widthTouchScreen;
    /** Original position of touch screen bar when dragged */
    private int xTouchScreenDrag, yTouchScreenDrag;
    /** Status of touch screen bar */
    private boolean barPressed,screenPressed;
    private boolean barDragged,screenDragged;
    /** Position of pressed */
    private int xPressed,yPressed;
    /** My icons */
    private Image zoomInIcon,zoomOutIcon,rotateLeftIcon,rotateRightIcon;
    private Image zoomInIconCopy;

    public static final short CURSOR_ON = 10;
    public static final short MOVE = 20;
    public static final short ZOOM_IN = 0;
    public static final short ZOOM_OUT = 1;
    public static final short ROT_LEFT = 2;
    public static final short ROT_RIGHT =3;

    /**
     * Constructor
     */
    public TouchScreen() {
        xTouchScreen = 0;
        yTouchScreen = 0;
        widthTouchScreen = 32;
        heightTouchScreen = 4*32;
        barPressed = false;
        try {
            zoomInIcon = Image.createImage("/Images/View-zoom-in.png");
            zoomOutIcon = Image.createImage("/Images/View-zoom-out.png");
            rotateLeftIcon = Image.createImage("/Images/rotate_left.png");
            rotateRightIcon = Image.createImage("/Images/rotate_right.png");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
//        int[] rgbData = new int[zoomInIcon.getWidth() * zoomInIcon.getHeight()];
//        zoomInIcon.getRGB(rgbData, 0, zoomInIcon.getWidth(), 0, 0, zoomInIcon.getWidth(), zoomInIcon.getHeight());
//        for (int i=0;i<rgbData.length;i++) {
//            rgbData[i] = rgbData[i] - (((rgbData[i]&0xff000000)/2)&0xff000000);
//        }
//        zoomInIcon = Image.createRGBImage(rgbData, zoomInIcon.getWidth(), zoomInIcon.getHeight(), true);

    }
    /**
     * Paint the touch screen bar
     * @param g
     */
    public void paint(Graphics g) {
        g.drawImage(zoomInIcon, xTouchScreen, yTouchScreen, Graphics.TOP | Graphics.LEFT);
        g.drawImage(zoomOutIcon, xTouchScreen, yTouchScreen+32, Graphics.TOP | Graphics.LEFT);
        g.drawImage(rotateLeftIcon, xTouchScreen, yTouchScreen+64, Graphics.TOP | Graphics.LEFT);
        g.drawImage(rotateRightIcon, xTouchScreen, yTouchScreen+96, Graphics.TOP | Graphics.LEFT);
//        g.setColor(0xffff00);
//        g.drawRect(xTouchScreen, yTouchScreen, widthTouchScreen, heightTouchScreen);
    }

    /**
     *
     * @param state
     * @param x
     * @param y
     */
    public void setPressed(int x,int y) {
        if (x>xTouchScreen && x<(xTouchScreen+widthTouchScreen)) {
            if (y>yTouchScreen && y<(yTouchScreen+heightTouchScreen)) {
                // The user has touched the touch screen bar
                barPressed = true;
                xPressed = x;                                                   // Store the position of the touch
                yPressed = y;
                xTouchScreenDrag = xTouchScreen;                                // Store the position of the touch screen bar
                yTouchScreenDrag = yTouchScreen;
            } 
        }
        if (!barPressed) {
            // The user has touched outside the touch screen bar
            screenPressed = true;
            xPressed = x;                                                   // Store the position of the touch
            yPressed = y;
        }
        //System.out.println("Pressed: "+ barPressed + " "+barDragged+" / "+screenPressed+" "+screenDragged);
    }
    /**
     *
     * @param x
     * @param y
     * @return
     */
    public int setReleased(int x,int y) {
        int ret = -1;
        if (barPressed && !barDragged) {
            // The user has pressed the bar without dragging
            ret = getButton(x,y);
        }
        if (screenPressed && !screenDragged) {
            ret = CURSOR_ON;
        }
        barDragged = screenDragged = false;
        barPressed = screenPressed = false;
        //System.out.println("Released: "+ barPressed + " "+barDragged+" / "+screenPressed+" "+screenDragged);
        return ret;
    }
    /**
     * Drag the touch screen bar
     * @param x
     * @param y
     */
    public int drag(int x,int y) {
        int dist;

        dist = (x-xPressed)*(x-xPressed)+(y-yPressed)*(y-yPressed);
        if (dist > 4) {
            if (barPressed) {
                xTouchScreen = xTouchScreenDrag + x - xPressed;
                yTouchScreen = yTouchScreenDrag + y - yPressed;
                barDragged = true;
            } else {
                screenDragged = true;
                return MOVE;
            }
        }
        //System.out.println("Dragged: "+ barPressed + " "+barDragged+" / "+screenPressed+" "+screenDragged);
        return -1;
    }
    public int getButton(int x,int y) {
        int ret;

        ret = (y-yTouchScreen)/32;

        return ret;
    }

    public int getxPressed() {
        return xPressed;
    }

    public int getyPressed() {
        return yPressed;
    }

    public boolean isScreenPressed() {
        return screenPressed;
    }


}