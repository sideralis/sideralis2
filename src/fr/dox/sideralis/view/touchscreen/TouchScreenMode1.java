/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.dox.sideralis.view.touchscreen;

import fr.dox.sideralis.Sideralis;

/**
 *
 * @author Bernard
 */
public class TouchScreenMode1 extends TouchScreen {

    /** Time variable used when dragging screen */
    private long timeOffset, timeBase;
    /** */
    private int xOrg,yOrg;

    /** Sensitivity for dragging - If dragging event are too much time separated, dragging is not taken into acount */
    public static final int MAX_TIME_BETWEEN_DRAG = 100;


    /**
     * Constructor
     */
    public TouchScreenMode1(int width, int height, Sideralis myMidlet) {
        super(width,height,myMidlet);
    }

    /**
     * Called when the screen is pressed. Set barPressed to true if the floating bar is touched
     * else set screenPressed to true.
     * @param x horizontal coordinate of the touch
     * @param y vertical coordinate of the touch
     */
    public void pointerPressed(int x,int y) {
        timeBase = System.currentTimeMillis();
        super.pointerPressed(x,y);
    }
    /**
     * Activate or update scroll parameters (called by pointerDragged)
     * @param x the last position of dragging
     * @param y the last position of dragging
     */
    public void setScrollParameters(int x, int y) {
        timeOffset = System.currentTimeMillis() - timeBase;
        timeBase = System.currentTimeMillis();
        if (timeOffset <= myParameter.getMaxTimeDragEventTouchScreen()) {
            if (scroll == false) {
                // First scroll movement
                scroll = true;
                rotDir = x-xPressed;
                yDir = y-yPressed;
                xOrg = xPressed;
                yOrg = yPressed;
            } else {
                // Next scroll movement
                rotDir = x-xOrg;
                yDir = y-yOrg;
                xOrg = x;
                yOrg = y;
            }
        }
        //System.out.println("x="+x+" y="+y+" off="+timeOffset);
    }
    /**
     * Scroll the display (used only with touch screen)
     */
    public void updateScrollParameters() {
        if (myParameter.getInertiaTouchScreen() != 0) {
            yDir /= myParameter.getInertiaTouchScreen();
            rotDir /= myParameter.getInertiaTouchScreen();
        } else {
            yDir = rotDir = 0;
        }
        if ((yDir < 0.2F) && (rotDir <0.2F) && (screenPressed == false))          // If the user has released its touch and scroll is neglictible then scroll is stopped
            scroll = false;
    }
}