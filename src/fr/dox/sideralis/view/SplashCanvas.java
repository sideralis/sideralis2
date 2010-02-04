/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.dox.sideralis.view;

import fr.dox.sideralis.LocalizationSupport;
import fr.dox.sideralis.Sideralis;
import java.io.IOException;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * This is the Canvas for the intro screen
 * @author gautier
 */
public class SplashCanvas extends Canvas implements Runnable {
    /** Height of the display */
    private int getHeight;
    /** Width of the display */
    private int getWidth;
    /** Boolean indicating if the task associated to this canvas is running or not */
    private boolean running;
    /** String used to store text and to avoid recurrent calls to LocalizationSupport */
    private String pleaseWait,pressAnyKey;

    /** Number of frames per second */
    private static final int MAX_CPS = 5;
    /** Time in millisecond between 2 frames */
    private static final int MS_PER_FRAME = 1000 / MAX_CPS;
    /** A counter for the splash screen */
    private short counter;
    /** Different values for state machine of the splash screen display */
    static final short COUNT0 = -5;                                             // Constant color red screen
    static final short COUNT1 = 0;                                              // Decreasing color from red to black
    static final short COUNT2 = 5;                                              // Constant color logo DoX
    static final short COUNT3 = 15;
    static final short COUNT4 = 25;
    /** The object to store the main image of the splash screen */
    private Image introImg;
    /** The object to store the DoX logo of the splash screen */
    private Image logoImg;
    /** The reference to the calling midlet */
    private final Sideralis myMidlet;
    /** The 2 fonts used */
    private final Font myFontBold, myFontNormal;

    /**
     * The constructor
     * @param myMidlet the reference to the calling midlet
     */
    public SplashCanvas(Sideralis myMidlet) {
        this.myMidlet = myMidlet;
        setFullScreenMode(true);
        
        counter = COUNT0;
        myFontBold = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_LARGE);
        myFontNormal = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
        try {
            introImg = Image.createImage("/iya_logo.png");
            logoImg = Image.createImage("/DoX.png");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        pleaseWait = LocalizationSupport.getMessage("PLEASE_WAIT");
        pressAnyKey = LocalizationSupport.getMessage("A1");
    }

    /**
     * The main task, used to update the display
     */
    public void run() {
        long cycleStartTime;

        getHeight = getHeight();                                                // Due to bug of 6680 and 6630
        getWidth = getWidth();                                                  // Due to bug of 6680 and 6630
        while (running) {
            cycleStartTime = System.currentTimeMillis();

            repaint();

            /* Thread is set to sleep if it remains some time before next frame */
            long timeSinceStart = (System.currentTimeMillis() - cycleStartTime);
            if (timeSinceStart < MS_PER_FRAME) {
                try {
                    Thread.sleep(MS_PER_FRAME - timeSinceStart);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * Called when hide
     */
    protected void hideNotify() {
        super.hideNotify();
    }
    /**
     * Called when displayed
     * Here it is used to start the main task
     */
    protected void showNotify() {
        super.showNotify();
        if (running == false) {
            running = true;
            new Thread(this).start();
        }
    }

    /**
     * The paint method
     * @param g the graphics object on which we draw
     */
    protected void paint(Graphics g) {
        if (counter<COUNT4+10)
            counter++;
        // =================================
        // ====== Display intro Image ======
        // =================================
        if (counter < COUNT1) {
            g.setColor(166, 34, 170);
            g.fillRect(0, 0, getWidth, getHeight);
            g.setColor(0, 0, 0);
            g.drawString(pleaseWait, getWidth / 2, getHeight / 2, Graphics.HCENTER | Graphics.TOP);
        } else if (counter < COUNT2) {
            g.setColor(166 - counter * 166 / COUNT2, 34 - counter * 34 / COUNT2, 170 - counter * 170 / COUNT2);
            g.fillRect(0, 0, getWidth, getHeight);
            g.drawImage(logoImg, getWidth / 2, getHeight / 2, Graphics.HCENTER | Graphics.VCENTER);

        } else if (counter < COUNT3) {
            g.setColor(0, 0, 0);
            g.fillRect(0, 0, getWidth, getHeight);
            g.drawImage(logoImg, getWidth / 2, getHeight / 2, Graphics.HCENTER | Graphics.VCENTER);
        } else if (counter == COUNT3) {
            g.setColor(0, 0, 0);
            g.fillRect(0, 0, getWidth, getHeight);
            g.drawImage(introImg, getWidth / 2, getHeight / 2, Graphics.HCENTER | Graphics.VCENTER);
        } else if (counter < COUNT4) {
            int c = counter - COUNT3;
            g.setColor(c * 166 / (COUNT4 - COUNT3), c * 34 / (COUNT4 - COUNT3), c * 170 / (COUNT4 - COUNT3));
            g.setFont(myFontBold);
            g.drawString("SIDERALIS", getWidth / 2, 0, Graphics.HCENTER | Graphics.TOP);
            g.setFont(myFontNormal);
            g.drawString("" + myMidlet.version+" b"+myMidlet.build, getWidth / 2, getHeight - myFontNormal.getHeight(), Graphics.HCENTER | Graphics.BOTTOM);
        } else {
            g.setColor(0, 0, 0);
            g.fillRect(0, 0, getWidth, getHeight);
            g.drawImage(introImg, getWidth / 2, getHeight / 2, Graphics.HCENTER | Graphics.VCENTER);
            g.setColor(166, 34, 170);
            g.setFont(myFontBold);
            g.drawString("SIDERALIS", getWidth / 2, 0, Graphics.HCENTER | Graphics.TOP);
            g.setFont(myFontNormal);
            g.drawString("" + myMidlet.version+" b"+myMidlet.build, getWidth / 2, getHeight - myFontNormal.getHeight(), Graphics.HCENTER | Graphics.BOTTOM);
        }

        if (myMidlet.isAllObjectsCreated()) {
            g.setFont(myFontNormal);
            g.setColor(166, 34, 170);
            g.drawString(pressAnyKey, getWidth / 2, getHeight, Graphics.HCENTER | Graphics.BOTTOM);
        }
    }
    /**
     * The method called when a key is pressed
     * @param keyCode the code of the key
     */
    protected void keyPressed(int keyCode) {
        super.keyPressed(keyCode);
        if (myMidlet.isAllObjectsCreated()) {
            running = false;
            myMidlet.endOfSplash();
        }
    }
    /**
     * The method called when the user clicks on the display
     * @param x the x coordinate to the clicked point
     * @param y the y coordinate to the clicked point
     */
    protected void pointerPressed(int x, int y) {
        super.pointerPressed(x, y);
        if (myMidlet.isAllObjectsCreated()) {
            running = false;
            myMidlet.endOfSplash();
        }
    }

    /**
     * Called when the drawable area of the Canvas has been changed
     * @param w the new width in pixels of the drawable area of the Canvas
     * @param h w - the new width in pixels of the drawable area of the Canvas
     */
    protected void sizeChanged(int w, int h) {
        getHeight = h;
        getWidth = w;
    }

    public boolean supportTouchScreen() {
        return hasPointerEvents();
    }
}
