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
    protected Image zoomInIcon,zoomOutIcon,maxIcon,minIcon,zenithIcon,horizonIcon,histIcon,debugIcon,exitIcon;
    /** direction of the floating bar (vertical or horizontal) */
    protected boolean vertical;
    /** All variables needed when dragging with touch screen */
    protected boolean scroll;
    /** Horizontal rotation and vertical scroll values */
    protected float rotDir,yDir;

    /** Default size of icon */
    protected static final int SIZE_ICON = 48;
    /** Number of icon */
    protected static final int NB_ICON = 4;

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
    /** To display history of constellation */
    public static final short CONST = 4;
    /** To display debug info */
    public static final short DEBUG = 5;
    /** To exit debug screen or const history screen */
    public static final short EXIT = 6;
    /** Cursor to be displayed */
    public static final short CURSOR_ON = 10;
    /** Screen to be moved */
    public static final short MOVE = 20;

    /** Reset value before tool bar disapears when nothing is done */
    protected static final int COUNTER_VANISH_ICON = 100;
    /** Default sensitivity touch screen (between drag & click) */
    public static final int SENSITIVITY_DRAG_CLICK = 15;
    /** Default value of inertia touch screen */
    public static final float INERTIA = 1.5f;
    /** Scroll speed default settings */
    public static final int SCROLL_SPEED_HORIZON = 70;
    public static final int SCROLL_SPEED_HOR_ZENITH = 20;
    public static final int SCROLL_SPEED_VER_ZENITH = 60;

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
            histIcon = Image.createImage("/constInfo.png");
            debugIcon = Image.createImage("/debug.png");
            exitIcon = Image.createImage("/exit.png");
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
            x = xBar + SIZE_ICON/2;
            y = yBar + SIZE_ICON/2;

            if (myParameter.isHistoryOfConstellationDisplayed() || myParameter.isDebug()) {
                // In this case only exit icon to display
                g.drawImage(exitIcon, x, y, Graphics.HCENTER | Graphics.VCENTER);
            } else if (vertical) {
                // Draw zoom in
                g.drawImage(zoomInIcon, x, y, Graphics.HCENTER | Graphics.VCENTER);
                // Draw zoom out
                g.drawImage(zoomOutIcon, x, y+SIZE_ICON, Graphics.HCENTER | Graphics.VCENTER);
                if (myParameter.isCursorDisplayed()) {
                    // Draw const info
                    g.drawImage(histIcon, x, y+SIZE_ICON*2, Graphics.HCENTER | Graphics.VCENTER);
                    // Draw debug icon
                    g.drawImage(debugIcon, x, y+SIZE_ICON*3, Graphics.HCENTER | Graphics.VCENTER);
                } else {
                    // Draw full screen <-> normal screen
                    if (myParameter.isFullScreen())
                        g.drawImage(minIcon, x, y+SIZE_ICON*2, Graphics.HCENTER | Graphics.VCENTER);
                    else
                        g.drawImage(maxIcon, x, y+SIZE_ICON*2, Graphics.HCENTER | Graphics.VCENTER);
                    // Draw horizontal <-> zenith view
                    if (myParameter.isHorizontalView())
                        g.drawImage(zenithIcon, x, y+SIZE_ICON*3, Graphics.HCENTER | Graphics.VCENTER);
                    else
                        g.drawImage(horizonIcon, x, y+SIZE_ICON*3, Graphics.HCENTER | Graphics.VCENTER);
                }
            } else {
                g.drawImage(zoomInIcon, x, y, Graphics.HCENTER | Graphics.VCENTER);
                g.drawImage(zoomOutIcon, x+SIZE_ICON, y, Graphics.HCENTER | Graphics.VCENTER);
                if (myParameter.isCursorDisplayed()) {
                    // Draw const info
                    g.drawImage(histIcon, x+SIZE_ICON*2, y, Graphics.HCENTER | Graphics.VCENTER);
                    // Draw debug icon
                    g.drawImage(debugIcon, x+SIZE_ICON*3, y, Graphics.HCENTER | Graphics.VCENTER);
                } else {                    
                    if (myParameter.isFullScreen())
                        g.drawImage(minIcon, x+SIZE_ICON*2, y, Graphics.HCENTER | Graphics.VCENTER);
                    else
                        g.drawImage(maxIcon, x+SIZE_ICON*2, y, Graphics.HCENTER | Graphics.VCENTER);
                    if (myParameter.isHorizontalView())
                        g.drawImage(zenithIcon, x+SIZE_ICON*3, y, Graphics.HCENTER | Graphics.VCENTER);
                    else
                        g.drawImage(horizonIcon, x+SIZE_ICON*3, y, Graphics.HCENTER | Graphics.VCENTER);
                }
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
        // Get the number of the button
        if (vertical)
            ret = (y-yBar)/SIZE_ICON;
        else
            ret = (x-xBar)/SIZE_ICON;

        // Button 0, 2 and 3 have multiple meanings, return the right one
        if (myParameter.isCursorDisplayed() && (ret >= 2)) {
            ret += (CONST-MIN_MAX);
        }
        if ((myParameter.isHistoryOfConstellationDisplayed() || myParameter.isDebug()) && (ret == 0))
            ret = EXIT;
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
            widthBar = SIZE_ICON;
            heightBar = NB_ICON*widthBar;
        } else {
            heightBar = SIZE_ICON;
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
     * Return the view rotation angle
     * @return the horizontal view rotation angle as float
     */
    public float getRotDir() {
        return rotDir;
    }
    /**
     * Return the vertical view offset (as angle in horizontal view, as offset in zenith view)
     * @return the vertical view offset
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
            if (dist > myParameter.getSensitivityTouchScreen()) {
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
     * Called when the screen is pressed. Set barPressed to true if the floating bar is touched
     * else set screenPressed to true.
     * @param x horizontal coordinate of the touch
     * @param y vertical coordinate of the touch
     */
    public void pointerPressed(int x,int y) {
        int w,h;
        if (counterVanishIcon != 0) {
            // In some case we have only one icon so bar size is smaller
            if (myParameter.isHistoryOfConstellationDisplayed() || myParameter.isDebug()) {
                w = h = SIZE_ICON;
            } else {
                w = widthBar;
                h = heightBar;
            }
            // Check if pointer is clicked on bar
            if (x>xBar && x<(xBar+w)) {
                if (y>yBar && y<(yBar+h)) {
                    // The user has touched the touch screen bar
                    barPressed = true;
                    xPressed = x;                                               // Store the position of the touch
                    yPressed = y;
                    xBarOrigine = xBar;                                         // Store the position of the touch screen bar
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
