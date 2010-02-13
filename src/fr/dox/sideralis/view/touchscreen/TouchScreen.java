/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.dox.sideralis.view.touchscreen;

import fr.dox.sideralis.ConfigParameters;
import fr.dox.sideralis.Sideralis;
import java.io.IOException;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 *
 * @author Bernard
 */
public abstract class TouchScreen {
    /** My counter used to make icons vanish */
    protected int counterVanishIcon;
    /** Sensitivity drag vs click */
    protected ConfigParameters myParameter;
    /** Position of floating bar for pointer */
    protected int xBar, yBar;
    /** Size of floating bar for pointer */
    protected int heightBar, widthBar;
    /** Original position of touch screen bar when dragged */
    protected int xBarOrigine, yBarOrigine;

    /** Status of touch screen bar */
    protected boolean barPressed,screenPressed;
    protected boolean barDragged,screenDragged;
    /** The action to be done after releasing the pointer (or stop dragging) */
    protected int action;

    /** Position of initial pointer pressed */
    protected int xPressed,yPressed;
    /** My icons */
    protected Image zoomInIcon,zoomOutIcon,maxIcon,minIcon,zenithIcon,horizonIcon;
    /** direction of the floating bar (vertical or horizontal) */
    protected boolean vertical;
    /** All variables needed when dragging with touch screen */
    protected boolean scroll;
    /** Horizontal rotation and vertical scroll values */
    protected float rotDir,yDir;

    /** Size of icon */
    protected int sizeIcon;

    /** Default size of icon */
    protected static final int SIZE_ICON = 48;
    /** Number of icon */
    protected static final int NB_ICON = 4;
    /** Reset value before tool bar disapears when nothing is done */
    protected static final int COUNTER_VANISH_ICON = 100;

    /** Nothing to do  after releasing or stoping dragging the pointer */
    public static final short NOTHING = 30;
    /** Zoom in to be done */
    public static final short ZOOM_IN = 0;
    /** Zoom out to be done */
    public static final short ZOOM_OUT = 1;
    /** Switch between full screen and not full screen */
    public static final short MIN_MAX = 2;
    /** To switch between horizontal and zenith view */
    public static final short VIEW = 3;
    /** Cursor to be displayed */
    public static final short CURSOR_ON = 10;
    /** Screen to be moved */
    public static final short MOVE = 20;
    /** Not yet used */
    public static final short DEBUG = 30;
    /** Not yet used */
    public static final short CONST = 40;

    /**
     * Constructor
     * @param width the width of the display
     * @param height the height of the display
     * @param myMidlet the calling midlet
     */
    public TouchScreen(int width, int height, Sideralis myMidlet) {
        counterVanishIcon = 0;
        setSize(width, height);
        xBar = 0;
        yBar = 0;
        barPressed = false;
        myParameter = myMidlet.getMyParameter();
        try {
            zoomInIcon = Image.createImage("/View-zoom-in.png");
            zoomOutIcon = Image.createImage("/View-zoom-out.png");
            maxIcon = Image.createImage("/max.png");
            minIcon = Image.createImage("/min.png");
            horizonIcon = Image.createImage("/HorView.png");
            zenithIcon = Image.createImage("/ZenithView.png");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    /**
     * Paint the touch screen bar
     * @param g the graphic object
     */
    public void paint(Graphics g) {
        if (counterVanishIcon>0) {
            counterVanishIcon--;

            int x,y;
            x = xBar + sizeIcon/2;
            y = yBar + sizeIcon/2;
            if (vertical) {
                g.drawImage(zoomInIcon, x, y, Graphics.HCENTER | Graphics.VCENTER);
                g.drawImage(zoomOutIcon, x, y+sizeIcon, Graphics.HCENTER | Graphics.VCENTER);
                if (myParameter.isFullScreen())
                    g.drawImage(minIcon, x, y+sizeIcon*2, Graphics.HCENTER | Graphics.VCENTER);
                else
                    g.drawImage(maxIcon, x, y+sizeIcon*2, Graphics.HCENTER | Graphics.VCENTER);
                if (myParameter.isHorizontalView())
                    g.drawImage(zenithIcon, x, y+sizeIcon*3, Graphics.HCENTER | Graphics.VCENTER);
                else
                    g.drawImage(horizonIcon, x, y+sizeIcon*3, Graphics.HCENTER | Graphics.VCENTER);
            } else {
                g.drawImage(zoomInIcon, x, y, Graphics.HCENTER | Graphics.VCENTER);
                g.drawImage(zoomOutIcon, x+sizeIcon, y, Graphics.HCENTER | Graphics.VCENTER);
                if (myParameter.isFullScreen())
                    g.drawImage(minIcon, x+sizeIcon*2, y, Graphics.HCENTER | Graphics.VCENTER);
                else
                    g.drawImage(maxIcon, x+sizeIcon*2, y, Graphics.HCENTER | Graphics.VCENTER);
                if (myParameter.isHorizontalView())
                    g.drawImage(zenithIcon, x+sizeIcon*3, y, Graphics.HCENTER | Graphics.VCENTER);
                else
                    g.drawImage(horizonIcon, x+sizeIcon*3, y, Graphics.HCENTER | Graphics.VCENTER);
            }
        }
    }
    /**
     * Return the button associate with the pointer pressed
     * @param x horizontal coordinate of the pointer released
     * @param y vertical coordinate of the pointer released
     * @return the action associate to the icon released
     */
    protected int getButton(int x,int y) {
        int ret;
        if (vertical)
            ret = (y-yBar)/sizeIcon;
        else
            ret = (x-xBar)/sizeIcon;
        return ret;
    }
    /**
     *
     * @param w
     * @param h
     */
    public void setSize(int w, int h) {
        if (w>h)
            vertical = true;
        else
            vertical = false;

        if (vertical) {
            sizeIcon = SIZE_ICON;
            widthBar = sizeIcon;
            heightBar = NB_ICON*widthBar;
        } else {
            sizeIcon = SIZE_ICON;
            heightBar = sizeIcon;
            widthBar = NB_ICON*heightBar;
        }
    }
    /**
     * Return true if scroll is active (meaning that scroll inc is still > 0.2)
     * @return true if scroll is active
     */
    public boolean isScroll() {
        return scroll;
    }
    /**
     * Set scroll (reset when user is pressing down the screen)
     * @param scroll the new value of scroll
     */
    public void setScroll(boolean scroll) {
        this.scroll = scroll;
    }
    /**
     *
     * @return
     */
    public float getRotDir() {
        return rotDir;
    }
    /**
     *
     * @return
     */
    public float getYScroll() {
        return yDir;
    }
    /**
     * Return the action related to what the user want when either the user is dragging or releasing the pointer
     * @return a action to be done after releasing or dragging the pointer
     */
    public int getAction() {
        return action;
    }

    /**
     * Drag the screen
     * @param x horizontal coordinate of the dragging
     * @param y vertical coordinate of the dragging
     */
    public void pointerDragged(int x,int y) {
        int dist;
        if (counterVanishIcon != 0) {
            dist = (x-xPressed)*(x-xPressed)+(y-yPressed)*(y-yPressed);
            if (dist > myParameter.getSensitivity()) {
                if (barPressed) {
                    xBar = xBarOrigine + x - xPressed;
                    yBar = yBarOrigine + y - yPressed;
                    barDragged = true;
                } else {
                    screenDragged = true;
                    setScrollParameters(x, y);
                }
            }
        }
    }
    /**
     *
     * @param x
     * @param y
     */
    /**
     * Called when the screen is pressed. Set barPressed to true if the floating bar is touched
     * else set screenPressed to true.
     * @param x horizontal coordinate of the touch
     * @param y vertical coordinate of the touch
     */
    public void pointerPressed(int x,int y) {
        if (counterVanishIcon != 0) {
            if (x>xBar && x<(xBar+widthBar)) {
                if (y>yBar && y<(yBar+heightBar)) {
                    // The user has touched the touch screen bar
                    barPressed = true;
                    xPressed = x;                                               // Store the position of the touch
                    yPressed = y;
                    xBarOrigine = xBar;                                    // Store the position of the touch screen bar
                    yBarOrigine = yBar;
                }
            }
            if (!barPressed) {
                // The user has touched outside the touch screen bar
                screenPressed = true;
                xPressed = x;                                                   // Store the position of the touch
                yPressed = y;
            }
        }
        scroll = false;
    }
    /**
     * Called when screen touch is released
     * Will return an action associated with the press (unless dragging)
     * @param x horizontal coordinate of the touch
     * @param y vertical coordinate of the touch
     * @return the name of the button of the floating bar which were clicked, or CURSOR_ON to indicate
     * that the cursor has been moved, or -1 if it was a released after a drag.
     */
    public void pointerReleased(int x,int y) {
        action = NOTHING;
        if (counterVanishIcon != 0) {
            // Clicks are only active if bar is displayed
            counterVanishIcon = COUNTER_VANISH_ICON;
            if (barPressed && !barDragged) {
                // The user has pressed the bar without dragging
                action = getButton(x,y);
            }
            if (screenPressed && !screenDragged) {
                action = CURSOR_ON;
            }
            barDragged = screenDragged = false;
            barPressed = screenPressed = false;
        }
       counterVanishIcon = COUNTER_VANISH_ICON;
    }

    /**
     * 
     */
    public abstract void updateScrollParameters();

    /**
     *
     * @param x
     * @param y
     */
    public abstract void setScrollParameters(int x, int y);

}
