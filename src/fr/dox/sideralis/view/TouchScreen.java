/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.dox.sideralis.view;

import java.io.IOException;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 *
 * @author Bernard
 */
public class TouchScreen {
    /** Position of floating bar for pointer */
    private int xTouchScreen, yTouchScreen;
    /** Size of floating bar for pointer */
    private int heightTouchScreen, widthTouchScreen;
    /** Size of icon */
    private int sizeIcon;
    /** direction of the floating bar (vertical or horizontal) */
    private boolean vertical;

    /** Original position of touch screen bar when dragged */
    private int xTouchScreenDrag, yTouchScreenDrag;
    /** Status of touch screen bar */
    private boolean barPressed,screenPressed;
    private boolean barDragged,screenDragged;
    /** Position of pressed */
    private int xPressed,yPressed;
    /** My icons */
    private Image zoomInIcon,zoomOutIcon,rotateLeftIcon,rotateRightIcon;
    /** My counter used to make icons vanish */
    private int counterVanishIcon;
    /** Time variable used when dragging screen */
    private long timeOffset, timeBase;
    /** All variables needed when dragging with touch screen */
    private boolean scroll;
    private float rotDir,yDir;
    private int xOrg,yOrg;

    private static final int COUNTER_VANISH_ICON = 100;
    /** The result of the action on the touch screen */
    public static final short CURSOR_ON = 10;
    public static final short MOVE = 20;
    public static final short NOTHING = 30;
    public static final short ZOOM_IN = 0;
    public static final short ZOOM_OUT = 1;
    public static final short ROT_LEFT = 2;
    public static final short ROT_RIGHT =3;
    /** Default size of icon */
    private static final int SIZE_ICON = 32;
    /** Number of icon */
    private static final int NB_ICON = 2;
    /** Sensitivity drag vs click */
    private static final int SENSITIVITY = 14;
    /** Sensitivity for dragging - If dragging event are too much time separated, dragging is not taken into acount */
    private long MAX_TIME_BETWEEN_DRAG = 20;

    /**
     * Constructor
     */
    public TouchScreen(int width, int height) {
        counterVanishIcon = 0;
        setSize(width, height);
        xTouchScreen = 0;
        yTouchScreen = 0;
        barPressed = false;
        try {
            zoomInIcon = Image.createImage("/View-zoom-in.png");
            zoomOutIcon = Image.createImage("/View-zoom-out.png");
//            rotateLeftIcon = Image.createImage("/rotate_left.png");
//            rotateRightIcon = Image.createImage("/rotate_right.png");
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
     * @param g the graphic object
     */
    public void paint(Graphics g) {
        if (counterVanishIcon>0) {
            counterVanishIcon--;

            int x,y;
            x = xTouchScreen + sizeIcon/2;
            y = yTouchScreen + sizeIcon/2;
            if (vertical) {
                g.drawImage(zoomInIcon, x, y, Graphics.HCENTER | Graphics.VCENTER);
                g.drawImage(zoomOutIcon, x, y+sizeIcon, Graphics.HCENTER | Graphics.VCENTER);
//                g.drawImage(rotateLeftIcon, x, y+sizeIcon*2, Graphics.HCENTER | Graphics.VCENTER);
//                g.drawImage(rotateRightIcon, x, y+sizeIcon*3, Graphics.HCENTER | Graphics.VCENTER);
            } else {
                g.drawImage(zoomInIcon, x, y, Graphics.HCENTER | Graphics.VCENTER);
                g.drawImage(zoomOutIcon, x+sizeIcon, y, Graphics.HCENTER | Graphics.VCENTER);
//                g.drawImage(rotateLeftIcon, x+sizeIcon*2, y, Graphics.HCENTER | Graphics.VCENTER);
//                g.drawImage(rotateRightIcon, x+sizeIcon*3, y, Graphics.HCENTER | Graphics.VCENTER);
            }
        }
    }

    /**
     * Called when the screen is pressed. Set barPressed to true if the floating bar is touched
     * else set screenPressed to true.
     * @param x horizontal coordinate of the touch
     * @param y vertical coordinate of the touch
     */
    public void setPressed(int x,int y) {
        if (counterVanishIcon != 0) {
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
    }
    /**
     * Called when screen touch is released
     * Will return an action associated with the press (unless dragging)
     * @param x horizontal coordinate of the touch
     * @param y vertical coordinate of the touch
     * @return the name of the button of the floating bar which were clicked, or CURSOR_ON to indicate
     * that the cursor has been moved, or -1 if it was a released after a drag.
     */
    public int setReleased(int x,int y) {
        if (counterVanishIcon != 0) {
            counterVanishIcon = COUNTER_VANISH_ICON;
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
       counterVanishIcon = COUNTER_VANISH_ICON;
       return NOTHING;
    }
    /**
     * Drag the screen
     * @param x horizontal coordinate of the dragging
     * @param y vertical coordinate of the dragging
     * @return -1 if the floating bar was dragger, MOVE if the screen was dragged
     */
    public int drag(int x,int y) {
        int dist;
        if (counterVanishIcon != 0) {
            dist = (x-xPressed)*(x-xPressed)+(y-yPressed)*(y-yPressed);
            if (dist > SENSITIVITY) {
                if (barPressed) {
                    xTouchScreen = xTouchScreenDrag + x - xPressed;
                    yTouchScreen = yTouchScreenDrag + y - yPressed;
                    barDragged = true;
                } else {
                    screenDragged = true;
                    return MOVE;
                }
            }
        }
        //System.out.println("Dragged: "+ barPressed + " "+barDragged+" / "+screenPressed+" "+screenDragged);
        return -1;
    }
    /**
     * Activate or update scroll parameters (called by pointerDragged)
     * @param x the last position of dragging
     * @param y the last position of dragging
     * @param xPressed the initial position of dragging
     * @param yPressed the initial position of dragging
     */
    public void setScroll(int x, int y, int xPressed, int yPressed) {
        timeOffset = System.currentTimeMillis() - timeBase;
        timeBase = System.currentTimeMillis();
        if (timeOffset <= MAX_TIME_BETWEEN_DRAG) {
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
    }
    public void setTimeBaseForScroll(long currentTimeMillis) {
        timeBase = currentTimeMillis;
    }
    /**
     * Scroll the display (used only with touch screen)
     * @param screenPressed true if used has yet released his touch, false if released
     */
    public void scroll(boolean screenPressed) {
        yDir /= 1.5F;
        rotDir /= 1.5F;
        if ((yDir < 0.2F) && (rotDir <0.2F) && (screenPressed == false))          // If the user has released its touch and scroll is neglictible then scroll is stopped
            scroll = false;
//        System.out.println("Scroll - rotDir: "+rotDir+ " / yDir: "+yDir);
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
        //System.out.println("---------------------");
    }
    public float getRotDir() {
        return rotDir;
    }
    public float getYScroll() {
        return yDir;
    }
    /**
     * Return the button associate with the pointer pressed
     * @param x horizontal coordinate of the pointer released
     * @param y vertical coordinate of the pointer released
     * @return the action associate to the icon released
     */
    private int getButton(int x,int y) {
        int ret;
        if (vertical)
            ret = (y-yTouchScreen)/sizeIcon;
        else
            ret = (x-xTouchScreen)/sizeIcon;
        return ret;
    }
    /**
     * Return the horizontal coordinate of the initial pointer pressed
     * @return the horizontal coordinate of the initial pointer pressed
     */
    public int getxPressed() {
        return xPressed;
    }
    /**
     * Returnt the vertical coordinate of the initial pointer pressed.
     * @return the vertical coordinate of the initial pointer pressed.
     */
    public int getyPressed() {
        return yPressed;
    }
    /**
     * Return the state of screen: pressed or released
     * @return true if not yet released, false if released.
     */
    public boolean isScreenPressed() {
        return screenPressed;
    }

    /**
     * 
     * @param w
     * @param h
     */
    void setSize(int w, int h) {
        if (w>h)
            vertical = true;
        else
            vertical = false;

        if (vertical) {
            sizeIcon = SIZE_ICON*w/240;
            widthTouchScreen = sizeIcon;
            heightTouchScreen = NB_ICON*widthTouchScreen;
        } else {
            sizeIcon = SIZE_ICON*h/320;
            heightTouchScreen = sizeIcon;
            widthTouchScreen = NB_ICON*heightTouchScreen;
        }
    }

}