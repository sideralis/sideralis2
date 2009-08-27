package fr.dox.sideralis.view;
/*
 * SideralisCanvas.java
 *
 * Created on 9 octobre 2005, 17:53
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

import fr.dox.sideralis.ConfigParameters;
import fr.dox.sideralis.LocalizationSupport;
import fr.dox.sideralis.Sideralis;
import fr.dox.sideralis.data.ConstellationCatalog;
import fr.dox.sideralis.data.MessierCatalog;
import fr.dox.sideralis.data.Sky;
import fr.dox.sideralis.data.StarCatalog;
import fr.dox.sideralis.object.ConstellationObject;
import fr.dox.sideralis.object.ScreenCoord;
import fr.dox.sideralis.projection.plane.Zenith;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

/**
 * A class derived from a Canvas to display the stars
 * Input is the sky with all virtual x and y coordinate of the stars.
 * Output is a display of these stars on the real screen. It depends of course of zoom, rotation and height shift.
 *
 * @author Bernard
 */
public class SideralisCanvas extends Canvas implements Runnable {

    private int getHeight;
    private int getWidth;

    private boolean running;
    /** Number of frames per second */
    private static final int MAX_CPS = 10;
    /** Time in millisecond between 2 frames */
    private static final int MS_PER_FRAME = 1000 / MAX_CPS;

    private final Sideralis myMidlet;

    private final Sky mySky;
    private final Zenith myProjection;
    /** Table to store x and y position on screen of stars */
    private ScreenCoord[] screenCoordStar;
    private ScreenCoord[] screenCoordMessier;

    private final Font myFont;
    
    private int idxClosestConst;
    private int idxClosestStar;
    private int colorClosestConst;

    /**
     * 
     */
    public SideralisCanvas(Sideralis myMidlet) {
        this.myMidlet = myMidlet;
        running = false;
        mySky = myMidlet.getMySky();
        myFont = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL);
        myProjection = new Zenith(getHeight(),getWidth());
    }

    /**
     *
     */
    public void init() {
        idxClosestStar = idxClosestConst = -1;
        colorClosestConst = 0;
        screenCoordStar = new ScreenCoord[StarCatalog.getNumberOfStars()];
        screenCoordMessier = new ScreenCoord[MessierCatalog.getNumberOfObjects()];
        for (int k = 0; k < StarCatalog.getNumberOfStars(); k++) {
            screenCoordStar[k] = new ScreenCoord();
        }
        for (int k = 0; k < MessierCatalog.getNumberOfObjects(); k++) {
            screenCoordMessier[k] = new ScreenCoord();
        }

    }

    /**
     *
     * @param g
     */
    protected void paint(Graphics g) {
        // ----------------------------
        // -----   Clear screen   -----
        g.setColor(color[COL_BACKGROUND]);
        g.fillRect(0, 0, getWidth, getHeight);
        
        if (mySky.isCalculationDone()) {
            mySky.setCalculationDone(false);
            g.drawString("Wait",10,10,Graphics.TOP|Graphics.LEFT);
            project();
        } else {
            // ----------------------------
            // -----   Draw horizon   -----
            drawHorizon(g);

            // ----------------------------------
            // ----- Display constellations -----
            drawConstellations(g);

            // --------------------------------
            // -----   Display all stars  -----
            drawStars(g);

            // ------------------------------------
            // ------ Display Messier objects -----
            drawMessier(g);
         }

    }

    /**
     * Draw the horizon
     * @param g the Graphics object
     */
    private void drawHorizon(Graphics g) {
        double x1,  y1,  x2,  y2,  x3,  y3,  x4,  y4;

        // Zenith view
        // Draw circle
        g.setColor(color[COL_ZENITH_BACKGROUND]);

        x1 = myProjection.getX(-1);
        y1 = myProjection.getY(-1);
        x2 = myProjection.getX(1) - x1;
        y2 = myProjection.getY(1) - y1;

        g.fillArc((int) x1 , (int) y1 , (int) x2 , (int) y2 , 0, 360);
        // Draw 'points cardinaux'
        double rot = -myProjection.getRot();
        x1 = myProjection.getX(Math.cos(rot) * .95);
        y1 = myProjection.getY(Math.sin(rot) * .95);
        x2 = myProjection.getX(Math.cos(rot + Math.PI) * .95);
        y2 = myProjection.getY(Math.sin(rot + Math.PI) * .95);
        x3 = myProjection.getX(Math.cos(rot + Math.PI / 2) * .95);
        y3 = myProjection.getY(Math.sin(rot + Math.PI / 2) * .95);
        x4 = myProjection.getX(Math.cos(rot + 3 * Math.PI / 2) * .95);
        y4 = myProjection.getY(Math.sin(rot + 3 * Math.PI / 2) * .95);
        g.setColor(color[COL_CROSS]);
        g.setStrokeStyle(Graphics.DOTTED);
        g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
        g.drawLine((int) x3, (int) y3, (int) x4, (int) y4);
        g.setStrokeStyle(Graphics.SOLID);
        g.setColor(color[COL_N_S_E_O]);
        g.drawString(LocalizationSupport.getMessage("WEST"), (int) x1, (int) y1 - myFont.getHeight() / 2, Graphics.HCENTER | Graphics.TOP);
        g.drawString(LocalizationSupport.getMessage("EAST"), (int) x2, (int) y2 - myFont.getHeight() / 2, Graphics.HCENTER | Graphics.TOP);
        g.drawString(LocalizationSupport.getMessage("SOUTH"), (int) x3, (int) y3 - myFont.getHeight() / 2, Graphics.HCENTER | Graphics.TOP);
        g.drawString(LocalizationSupport.getMessage("NORTH"), (int) x4, (int) y4 - myFont.getHeight() / 2, Graphics.HCENTER | Graphics.TOP);
    }
    /**
     * Draw constellations
     * @param g2 the Graphics object
     */
    private void drawConstellations(Graphics g) {
        short kStar1 = 0;
        short kStar2 = 0;
        boolean flagInc = false;                                                // To calculate the color of the constellation only one time for all branches of the constellation
        int i,  j;
        ConstellationObject co;
        ConfigParameters rMyParam = myMidlet.getMyParameter();
        ConstellationCatalog rMyConstellations = mySky.getConstellations();


        if (rMyParam.isConstDisplayed() || rMyParam.isConstNamesDisplayed() || rMyParam.isConstNamesLatinDisplayed()) {
            // If constellations should be displayed
            for (i = 0; i < ConstellationCatalog.getNumberOfConstellations(); i++) {
                co = rMyConstellations.getOptConstellation(i);
                // For all constellations
                int col = 0;                                                    // The color of the constellation drawing
                for (j = 0; j < co.getSizeOfConstellation(); j += 2) {
                    kStar1 = co.getIdx(j);               // Get index of stars in constellation
                    kStar2 = co.getIdx(j + 1);             // Get index of stars in constellation
                    // Draw one part of the constellation
                    if (screenCoordStar[kStar1].isVisible() && screenCoordStar[kStar2].isVisible()) {
                        // A line between 2 stars is displayed only if the 2 stars are visible.
                        if (co.getIdxName() == idxClosestConst && rMyParam.isCursorDisplayed() && idxClosestStar >= 0 && idxClosestStar < screenCoordStar.length) {
                            // This constellation is blinking (cursor close to it)
                            if (!flagInc) {
                                // Calculate the color of the constellation. This is done only for the first branch of the constellation.
                                col = colorClosestConst > color[COL_CONST_MAX] / 2 ? color[COL_CONST_MAX] - colorClosestConst : colorClosestConst;
                                colorClosestConst += color[COL_CONST_INC];
                                if (colorClosestConst > color[COL_CONST_MAX]) {
                                    colorClosestConst = 0;
                                }
                                flagInc = true;
                            }
                            g.setColor(col + color[COL_CONST_MIN]);
                        } else {
                            g.setColor(color[COL_CONST_MAX] / 2);
                        }
                        if (rMyParam.isConstDisplayed())
                            g.drawLine(screenCoordStar[kStar1].x, screenCoordStar[kStar1].y, screenCoordStar[kStar2].x, screenCoordStar[kStar2].y);
                    }
                }
                if (rMyParam.isConstNamesDisplayed() || rMyParam.isConstNamesLatinDisplayed()) {
                    g.setColor(color[COL_CONST_NAME_MAX] / 2);
                    // Display the name of the constellation
                    kStar1 = co.getRefStar4ConstellationName();
                    if (!screenCoordStar[kStar1].isVisible()) {
                        for (j = 0; j < co.getSizeOfConstellation(); j += 2) {
                            kStar1 = co.getIdx(j);               // Get index of stars in constellation
                            if (screenCoordStar[kStar1].isVisible()) {
                                break;
                            }
                        }
                    }
                    if (screenCoordStar[kStar1].isVisible()) {
                        if (rMyParam.isConstNamesDisplayed() && rMyParam.isConstNamesLatinDisplayed()) {
                            g.drawString(rMyConstellations.getConstellation(i).getName(), screenCoordStar[kStar1].x, screenCoordStar[kStar1].y-myFont.getHeight()/2, Graphics.TOP | Graphics.HCENTER);
                            g.drawString(rMyConstellations.getConstellation(i).getLatinName(), screenCoordStar[kStar1].x, screenCoordStar[kStar1].y+myFont.getHeight()/2, Graphics.TOP | Graphics.HCENTER);
                        }
                        else if (rMyParam.isConstNamesDisplayed())
                            g.drawString(rMyConstellations.getConstellation(i).getName(), screenCoordStar[kStar1].x, screenCoordStar[kStar1].y, Graphics.TOP | Graphics.HCENTER);
                        else
                            g.drawString(rMyConstellations.getConstellation(i).getLatinName(), screenCoordStar[kStar1].x, screenCoordStar[kStar1].y, Graphics.TOP | Graphics.HCENTER);
                    }
                }
            }
        }
    }
    /**
     * Draw stars
     * @param g the Graphics object
     */
    private void drawStars(Graphics g) {
        int nbOfStars = StarCatalog.getNumberOfStars();
        boolean isStarColored = myMidlet.getMyParameter().isStarColored();
        boolean isStarShownAsCircle = myMidlet.getMyParameter().isStarShownAsCircle();
        float maxMag = myMidlet.getMyParameter().getMaxMag();

        for (int k = 0; k < nbOfStars; k++) {
            if (screenCoordStar[k].isVisible()) {
                // Star is above horizon
                if (StarCatalog.getStar(k).getMag() < maxMag) {
                    int mag = (int) (StarCatalog.getStar(k).getMag());
                    if (mag > 5) {
                        mag = 5;
                    }
                    if (mag < 0) {
                        mag = 0;
                    // Select color of star
                    }
                    if (isStarColored) {
                        int col = color[COL_STAR_MAX] - mag * color[COL_STAR_INC];
                        g.setColor(col);
                    } else {
                        g.setColor(color[COL_STAR_MAX]);
                    }
                    // Represent star as a filled circle or as a dot
                    if (isStarShownAsCircle) {
                        // As a circle
                        int size = 5 - mag;
                        if (size > 4) {
                            size = 4;
                        }
                        if (size < 1) {
                            g.drawLine(screenCoordStar[k].x, screenCoordStar[k].y, screenCoordStar[k].x, screenCoordStar[k].y);
                        } else {
                            g.fillArc(screenCoordStar[k].x - size, screenCoordStar[k].y - size, 2 * size, 2 * size, 0, 360);
                        }
                    } else {
                        // Or as a dot
                        g.drawLine(screenCoordStar[k].x, screenCoordStar[k].y, screenCoordStar[k].x, screenCoordStar[k].y);
                    }
                }
            }
        }
    }

    /**
     * Draw the messier objects
     * @param g the graphic object
     */
    private void drawMessier(Graphics g) {
        ConfigParameters rMyParam = myMidlet.getMyParameter();

        if (rMyParam.isMessierDisplayed() || rMyParam.isMessierNameDisplayed()) {
            for (int k = 0; k < MessierCatalog.getNumberOfObjects(); k++) {
                if (screenCoordMessier[k].isVisible()) {
                    // Messier object is above horizon
                    g.setColor(color[COL_MESSIER]);
                    if (rMyParam.isMessierDisplayed())
                        g.drawLine(screenCoordMessier[k].x, screenCoordMessier[k].y, screenCoordMessier[k].x, screenCoordMessier[k].y);
                    if (rMyParam.isMessierNameDisplayed())
                        g.drawString(mySky.getMessier(k).getObject().getName(), screenCoordMessier[k].x, screenCoordMessier[k].y, Graphics.TOP | Graphics.HCENTER);
                }
            }
        }
    }
    /**
     *
     * @param keyCode
     */
    protected void keyPressed(int keyCode) {
        if (keyCode == KEY_NUM7) {
            myProjection.incRot();
            project();
            repaint();
        }
        if (keyCode == KEY_NUM9) {
            myProjection.decRot();
            project();
            repaint();
        }
        if (keyCode == KEY_NUM1) {
            myProjection.decZoom();
            project();
            repaint();
        }
        if (keyCode == KEY_NUM3) {
            myProjection.incZoom();
            project();
            repaint();
        }
        if (getGameAction(keyCode) == UP) {
            myProjection.incShiftY();
            project();
            repaint();
        }
        if (getGameAction(keyCode) == DOWN) {
            myProjection.decShiftY();
            project();
            repaint();
        }
        if (getGameAction(keyCode) == LEFT) {
            myProjection.incShiftX();
            project();
            repaint();
        }
        if (getGameAction(keyCode) == RIGHT) {
            myProjection.decShiftX();
            project();
            repaint();
        }
    }

    protected void keyReleased(int keyCode) {
        super.keyReleased(keyCode);
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
    /**
     * Calculate the the x and y positions of all objects on screen
     * Used to accelerate display because calculation is not needed at each display
     */
    public void project() {
        // === Stars ===
        for (int k = 0; k < StarCatalog.getNumberOfStars(); k++) {
            screenCoordStar[k].setVisible(false);
                // For a zenith view
            if (mySky.getStar(k).getHeight() > 0) {
                // Star is above horizon
                screenCoordStar[k].setVisible(true);
                screenCoordStar[k].x = (short)myProjection.getX(myProjection.getVirtualX(mySky.getStar(k).getAzimuth(), mySky.getStar(k).getHeight()));
                screenCoordStar[k].y = (short)myProjection.getY(myProjection.getVirtualY(mySky.getStar(k).getAzimuth(), mySky.getStar(k).getHeight()));
            }
        }

        // === Messiers ===
        for (int k = 0; k < MessierCatalog.getNumberOfObjects(); k++) {
            screenCoordMessier[k].setVisible(false);
                // For a zenith view
            if (mySky.getMessier(k).getHeight() > 0) {
                // Star is above horizon
                screenCoordMessier[k].setVisible(true);
                screenCoordMessier[k].x = (short)myProjection.getX(myProjection.getVirtualX(mySky.getMessier(k).getAzimuth(), mySky.getMessier(k).getHeight()));
                screenCoordMessier[k].y = (short)myProjection.getY(myProjection.getVirtualY(mySky.getMessier(k).getAzimuth(), mySky.getMessier(k).getHeight()));
            }
        }
    }

    /**
     *
     */
    protected void showNotify() {
        super.showNotify();
        if (running == false) {
            running = true;
            new Thread(this).start();
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
        myProjection.setHeightDisplay(h);
        myProjection.setWidthDisplay(w);
        myProjection.setView();
        project();
        repaint();
    }

    // -------------------------------------------------------------------------
    /** TODO should be moved to a property file */
    /** Color reference */
    /** TODO use enum */
//    public enum COL { BACKGOUND,
//               HELP,
//               MOON,
//               SUN,
//               MERCURY,
//               VENUS,
//               MARS,
//               JUPITER,
//               SATURN,
//    };
    public static final  short COL_BACKGROUND = 0,
                    COL_HELP = COL_BACKGROUND + 1,
                    COL_MOON = COL_HELP + 1,
                    COL_SUN = COL_MOON + 1,
                    COL_MERCURY = COL_SUN + 1,
                    COL_VENUS = COL_MERCURY + 1,
                    COL_MARS = COL_VENUS + 1,
                    COL_JUPITER = COL_MARS + 1,
                    COL_SATURN = COL_JUPITER + 1,
                    COL_HISTORY = COL_SATURN + 1,
                    COL_INFO = COL_HISTORY + 1,
                    COL_ANGLE = COL_INFO + 1,
                    COL_ZENITH_BACKGROUND = COL_ANGLE + 1,
                    COL_ZENITH_EDGE = COL_ZENITH_BACKGROUND + 1,
                    COL_CROSS = COL_ZENITH_EDGE + 1,
                    COL_N_S_E_O = COL_CROSS + 1,
                    COL_CONST_MIN = COL_N_S_E_O + 1,
                    COL_CONST_MAX = COL_CONST_MIN + 1, // Should be a multiple of 2 and a multiple of INC
                    COL_CONST_INC = COL_CONST_MAX + 1,
                    COL_CONST_NAME_MIN = COL_CONST_INC + 1,
                    COL_CONST_NAME_MAX = COL_CONST_NAME_MIN + 1,
                    COL_CONST_NAME_INC = COL_CONST_NAME_MAX + 1,
                    COL_STAR_MAX = COL_CONST_NAME_INC + 1,
                    COL_STAR_INC = COL_STAR_MAX + 1,
                    COL_CURSOR = COL_STAR_INC + 1,
                    COL_BOX_TEXT = COL_CURSOR + 1,
                    COL_BOX = COL_BOX_TEXT + 1,
                    COL_MENUBAR = COL_BOX + 1,
                    COL_MENUBAR2 = COL_MENUBAR + 1,
                    COL_MESSIER = COL_MENUBAR2 + 1,
                    COL_HIGHLIGHT = COL_MESSIER + 1;
    /** Definition of color */
    private static final int[] color = {
        /* /\/\/\/\ Normal simplified color /\/\/\/\*/
            /* BACKGROUND */    0x00000000,
            /* HELP       */    0x00ffffff,
            /* MOON       */    0x00dcdcdc,
            /* SUN        */    0x00ffff00,
            /* MERCURY    */    0x00ff00ff,
            /* VENUS      */    0x0000ff00,
            /* MARS       */    0x00ff0000,
            /* JUPITER    */    0x00ffff00,
            /* SATURN     */    0x0000ffff,
            /* HISTORY    */    0x00ffffff,
            /* INFO       */    0x00ff0000,
            /* HORIZON    */    0x00ff0000,
            /* ZENITH_BCK */    0x00000030,
            /* ZENITH_EDGE*/    0x00000005,
            /* CROSS      */    0x003c3c3c,
            /* N_S_E_O    */    0x007f00ff,
            /* CONST_MIN  */    0x00000000,
            /* CONST_MAX  */    0x008c8c8c,
            /* CONST_INC  */    0x00040404,
            /* CONST_NAME_MIN */0x00000000,
            /* CONST_NAME_MAX */0x0046d246,
            /* CONST_NAME_INC */0x00020602,
            /* STAR_MAX   */    0x00ffffff,
            /* STAR_INC   */    0x00303030,
            /* CURSOR     */    0x00ff0000,
            /* BOX_TEXT   */    0x00ffffff,
            /* BOX        */    0x000000c8,
            /* MENUBAR    */    0x00FB16FF,
            /* MENUBAR2   */    0x00803A7D,
            /* MESSIER    */    0x00ffff00,
            /* HIGHLIGHT  */    0x000080ff,
        };

}