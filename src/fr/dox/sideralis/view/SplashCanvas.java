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
 *
 * @author gautier
 */
public class SplashCanvas extends Canvas implements Runnable {
    private int getHeight;
    private int getWidth;
    private boolean running;
    /** Number of frames per second */
    private static final int MAX_CPS = 5;
    /** Time in millisecond between 2 frames */
    private static final int MS_PER_FRAME = 1000 / MAX_CPS;
    /** A counter for the splash screen */
    private short counter;
    /** Different values for state machine of the splash screen display */
    static final short COUNT0 = -5;                                             // Constant color red screen
    static final short COUNT1 = 0;                                              // Decreasing color from red to black
    static final short COUNT2 = 5;                                             // Constant color logo DoX
    static final short COUNT3 = 15;
    static final short COUNT4 = 25;
    private Image introImg;
    private Image logoImg;
    private final Sideralis myMidlet;
    private final Font myFontBold, myFontNormal;

    /**
     *
     */
    public SplashCanvas(Sideralis myMidlet) {
        this.myMidlet = myMidlet;

        counter = COUNT0;
        myFontBold = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_LARGE);
        myFontNormal = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
        try {
            introImg = Image.createImage("/iya_logo.png");
            logoImg = Image.createImage("/DoX.png");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     *
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

    protected void hideNotify() {
        super.hideNotify();
    }

    protected void showNotify() {
        super.showNotify();
        if (running == false) {
            running = true;
            new Thread(this).start();
        }
    }

    /**
     *
     * @param g
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
            g.drawString(LocalizationSupport.getMessage("PLEASE_WAIT"), getWidth / 2, getHeight / 2, Graphics.HCENTER | Graphics.TOP);
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
            g.drawString(" " + Sideralis.VERSION, getWidth / 2, getHeight - myFontNormal.getHeight(), Graphics.HCENTER | Graphics.BOTTOM);
        } else {
            g.setColor(0, 0, 0);
            g.fillRect(0, 0, getWidth, getHeight);
            g.drawImage(introImg, getWidth / 2, getHeight / 2, Graphics.HCENTER | Graphics.VCENTER);
            g.setColor(166, 34, 170);
            g.setFont(myFontBold);
            g.drawString("SIDERALIS", getWidth / 2, 0, Graphics.HCENTER | Graphics.TOP);
            g.setFont(myFontNormal);
            g.drawString(" " + Sideralis.VERSION, getWidth / 2, getHeight - myFontNormal.getHeight(), Graphics.HCENTER | Graphics.BOTTOM);
        }

        if (!myMidlet.isStarting()) {
            g.setFont(myFontNormal);
            g.setColor(166, 34, 170);
            g.drawString(LocalizationSupport.getMessage("A1"), getWidth / 2, getHeight, Graphics.HCENTER | Graphics.BOTTOM);
        }
    }
    /**
     *
     * @param keyCode
     */
    protected void keyPressed(int keyCode) {
        super.keyPressed(keyCode);
        if (!myMidlet.isStarting()) {
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
}
