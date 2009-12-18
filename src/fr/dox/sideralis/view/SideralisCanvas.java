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
import fr.dox.sideralis.math.MathFunctions;
import fr.dox.sideralis.object.ConstellationObject;
import fr.dox.sideralis.object.ScreenCoord;
import fr.dox.sideralis.object.SkyObject;
import fr.dox.sideralis.object.StarObject;
import fr.dox.sideralis.projection.plane.*;
import fr.dox.sideralis.projection.sphere.MoonProj;
import fr.dox.sideralis.projection.sphere.Projection;
import fr.dox.sideralis.view.color.Color;
import java.io.IOException;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;

/**
 * A class derived from a Canvas to display the stars
 * Input is the sky with all virtual x and y coordinate of the stars.
 * Output is a display of these stars on the real screen. It depends of course of zoom, rotation and height shift.
 *
 * @author Bernard
 */
public class SideralisCanvas extends GameCanvas implements Runnable {

    /** Width and height of the display */
    private int getHeight, getWidth;
    /** Flag for stopping the animation thread.*/
    private boolean running;
    /** 2D graphics singleton used for rendering. */
    private Graphics graphics;
    private static String northString, southString, eastString, westString;
    /** The calling midlet */
    private final Sideralis myMidlet;
    /** The sky object */
    private final Sky mySky;
    /** The projection used to display the objects on the screen */
    private final ScreenProj myProjection;
    /** Table to store x and y position on screen of stars */
    protected ScreenCoord[] screenCoordStars;
    protected ScreenCoord[] screenCoordMessier;
    protected ScreenCoord screenCoordSun;
    protected ScreenCoord screenCoordMoon;
    protected ScreenCoord[] screenCoordPlanets;
    private final Font myFont;
    // ------------------
    // --- For cursor ---
    private int idxClosestConst;
    private int idxClosestObject;
    private int typeClosestObject;
    private int colorClosestConst;
    /** The counter associated to this action: if the cursor does not move for sometime, it is not displayed anymore */
    private short displayCursor;
    /** The image for the cursor */
    private Image cursorImage;
    /** Position of cursor */
    private int xCursor, yCursor;
    /** All informations about object */
    private String objectName, magName, distName, azName, heiName;
    private String AZ_NAME, HEI_NAME;
    /** Number of lines which will be used to display informations on star */
    private final short NB_OF_LINES = 5;
    /** An variable used to modify the display of the const name */
    private int shiftConstName;
    private int colorConstName;
    /** Name of the constellation which is shown by the cursor */
    private String constName1, constName2;
    /** The offset of the text containing the info text */
    private int xInfoText, yInfoText;
    private int xInfoDest, xInfo;
    private int yInfoDest, yInfo;
    /** Default size in pixel for Moon and Sun */
    private static final short SIZE_MOON_SUN = 2;
    /** Counter used to decide when to recalculate all positons */
    private int counter;
    /** All objects positions are recalculated every COUNTER calls to run() */
    private static final int COUNTER = 1000;
    /** The reset value of display Cursor before it disappears */
    private static final short COUNTER_CURSOR = 10;
    private final TouchScreen touchScreen;
    /** The position of the circle around the selected or highlighted objects */
    private short angle;
    private short angleHighlight;
    /** To indicate if help is displayed or not. TODO use parameter instead*/
    private boolean help;
    /** Help text */
    private String helpText;
    /** The counter used to do transformation */
    private int step;
    /** An img object used to make transition between screen */
    private Image imgOrg, imgRet;
    /** The graphic object associated to the image above */
    private Graphics gImg;
    /** Number of frames per second */
    private static final int MAX_CPS = 10;
    /** Time in millisecond between 2 frames */
    private static final int MS_PER_FRAME = 1000 / MAX_CPS;

    /**
     * Constructor of the class
     * @param myMidlet a reference to the midlet creating the canvas
     */
    public SideralisCanvas(Sideralis myMidlet) {
        super(false);
        this.myMidlet = myMidlet;
        running = false;
        mySky = myMidlet.getMySky();
        myFont = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL);
//        myProjection = new ZenithProj(myMidlet, getHeight(), getWidth());
        myProjection = new EyeProj(myMidlet, getHeight(), getWidth());
        if (hasPointerEvents()) {
            touchScreen = new TouchScreen(getWidth(), getHeight());
        } else {
            touchScreen = null;
        }
        northString = LocalizationSupport.getMessage("NORTH");
        westString = LocalizationSupport.getMessage("WEST");
        southString = LocalizationSupport.getMessage("SOUTH");
        eastString = LocalizationSupport.getMessage("EAST");
        helpText = LocalizationSupport.getMessage("HELP_ONSCREEN");
        AZ_NAME = new String(LocalizationSupport.getMessage("CURSOR_AZIMUTH_ABBR"));
        HEI_NAME = new String(LocalizationSupport.getMessage("CURSOR_HEIGHT_ABBR"));

    }

    /**
     * Create and initialize all objects needed to display the sky objects at the right position
     */
    public void init() {
        graphics = getGraphics();

        idxClosestObject = idxClosestConst = -1;
        colorClosestConst = 0;

        myProjection.init();
        screenCoordMessier = myProjection.getScreenCoordMessier();
        screenCoordMoon = myProjection.getScreenCoordMoon();
        screenCoordPlanets = myProjection.getScreenCoordPlanets();
        screenCoordStars = myProjection.getScreenCoordStars();
        screenCoordSun = myProjection.getScreenCoordSun();

        try {
            cursorImage = Image.createImage("/Cursor.png");
        } catch (IOException e) {
            e.printStackTrace();
        }

        xCursor = getWidth() / 2;
        yCursor = getHeight() / 2;
        help = false;

    }

    /**
     *
     * @param counter
     */
    public void setCounter(int counter) {
        this.counter = counter;
    }

    /**
     *
     * @param g
     */
    protected void render(Graphics g) {
        // =============================================================
        // === Check if we need to center screen to searched object ====
        // =============================================================
        if (!myMidlet.getSearch().isFlagCentered()) {
            if (myProjection.getZoom() > 1) {
                myProjection.decZoom();
                project();
            }
            if (myProjection.getZoom() == 1) {
                myMidlet.getSearch().setFlagCentered(true);
            }
        }
        paintMain(g);

    }

    /**
     *
     * @param g
     */
    private void paintMain(Graphics g) {
        // ==============================================
        // ====== Display Stars and Constellations ======
        // ==============================================

        if (mySky.isCalculationDone()) {
            // -----------------------------------------------------------------------------
            // ---- Print wait message when projection on screen is ongoing ---
            mySky.setCalculationDone(false);
            mySky.setProgress(0);
            g.setColor(0x000000FF);
            g.drawString("Wait", getWidth / 2, getHeight / 2, Graphics.HCENTER | Graphics.BASELINE);
            project();
        } else {
            // ------------------------------------------------
            // --- Scroll the screen if user is dragging it ---
            if (touchScreen != null && touchScreen.isScroll()) {
                myProjection.scrollHor(touchScreen.getRotDir());
                myProjection.scrollVer(touchScreen.getYScroll());
                touchScreen.scroll(touchScreen.isScreenPressed());
                project();
            }
            g.setFont(myFont);
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

            // --------------------------------------
            // ------ Draw system solar objects -----
            drawSystemSolarObjects(g);

            // -----------------------------------
            // ------ Draw touch screen bar ------
            if (touchScreen != null) {
                touchScreen.paint(g);
            }

            // -----------------------------------
            // ------ Display searched object ----
            drawSearched(g);

            // -----------------------------------
            // ----- Display cursor and info -----
            if (myMidlet.getMyParameter().isCursorDisplayed()) {
                drawCursor(g);
            }

            // -------------------------------
            // ------ Draw progress bar ------
            g.setColor(0x00ff);
            if (mySky.getProgress() != 0) {
                g.drawLine(0, 0, mySky.getProgress() * getHeight / 100, 0);
            }

            // -------------------------
            // ------ Display help -----
            if (myMidlet.getMyParameter().isHelpDisplayed()) {
                g.setColor((myMidlet.getMyParameter().getColor())[Color.COL_HELP]);
                g.drawString(helpText, 0, getHeight - myFont.getHeight(), Graphics.TOP | Graphics.LEFT);
            }
            // --------------------------------
            // ------- Draw Help --------------
            if (help) {
                drawHelp(g);
            }
        }
    }

    /**
     * Draw the horizon
     * @param g the Graphics object
     */
    private void drawHorizon(Graphics g) {

        myProjection.drawHorizon(g);

    }

    /**
     * Draw constellations
     * @param g2 the Graphics object
     */
    private void drawConstellations(Graphics g) {
        short kStar1 = 0;
        short kStar2 = 0;
        boolean flagInc = false;                                                // To calculate the color of the constellation only one time for all branches of the constellation
        int i, j;
        ConstellationObject co;
        ConfigParameters rMyParam = myMidlet.getMyParameter();
        int[] color = rMyParam.getColor();


        if (rMyParam.isConstDisplayed() || rMyParam.isConstNamesDisplayed() || rMyParam.isConstNamesLatinDisplayed()) {
            // If constellations should be displayed
            for (i = 0; i < ConstellationCatalog.getNumberOfConstellations(); i++) {
                co = ConstellationCatalog.getConstellation(i);
                // For all constellations
                int col = 0;                                                    // The color of the constellation drawing
                for (j = 0; j < co.getSizeOfConstellation(); j += 2) {
                    kStar1 = co.getIdx(j);               // Get index of stars in constellation
                    kStar2 = co.getIdx(j + 1);             // Get index of stars in constellation
                    // Draw one part of the constellation
                    if (screenCoordStars[kStar1].isVisible() && screenCoordStars[kStar2].isVisible()) {
                        // A line between 2 stars is displayed only if the 2 stars are visible.
                        if (typeClosestObject == SkyObject.STAR && co.getIdxName() == idxClosestConst) {
                            // This constellation is blinking (cursor close to it)
                            if (!flagInc) {
                                // Calculate the color of the constellation. This is done only for the first branch of the constellation.
                                col = colorClosestConst > color[Color.COL_CONST_MAX] / 2 ? color[Color.COL_CONST_MAX] - colorClosestConst : colorClosestConst;
                                colorClosestConst += color[Color.COL_CONST_INC];
                                if (colorClosestConst > color[Color.COL_CONST_MAX]) {
                                    colorClosestConst = 0;
                                }
                                flagInc = true;
                            }
                            g.setColor(col + color[Color.COL_CONST_MIN]);
                        } else {
                            g.setColor(color[Color.COL_CONST_MAX] / 2);
                        }
                        if (rMyParam.isConstDisplayed()) {
                            g.drawLine(screenCoordStars[kStar1].x, screenCoordStars[kStar1].y, screenCoordStars[kStar2].x, screenCoordStars[kStar2].y);
                        }
                    }
                }
                if (rMyParam.isConstNamesDisplayed() || rMyParam.isConstNamesLatinDisplayed()) {
                    g.setColor(color[Color.COL_CONST_NAME_MAX] / 2);
                    // Display the name of the constellation
                    kStar1 = co.getRefStar4ConstellationName();
                    if (!screenCoordStars[kStar1].isVisible()) {
                        for (j = 0; j < co.getSizeOfConstellation(); j += 2) {
                            kStar1 = co.getIdx(j);               // Get index of stars in constellation
                            if (screenCoordStars[kStar1].isVisible()) {
                                break;
                            }
                        }
                    }
                    if (screenCoordStars[kStar1].isVisible()) {
                        if (rMyParam.isConstNamesDisplayed() && rMyParam.isConstNamesLatinDisplayed()) {
                            g.drawString(ConstellationCatalog.getConstellation(i).getName(), screenCoordStars[kStar1].x, screenCoordStars[kStar1].y - myFont.getHeight() / 2, Graphics.TOP | Graphics.HCENTER);
                            g.drawString(ConstellationCatalog.getConstellation(i).getLatinName(), screenCoordStars[kStar1].x, screenCoordStars[kStar1].y + myFont.getHeight() / 2, Graphics.TOP | Graphics.HCENTER);
                        } else if (rMyParam.isConstNamesDisplayed()) {
                            g.drawString(ConstellationCatalog.getConstellation(i).getName(), screenCoordStars[kStar1].x, screenCoordStars[kStar1].y, Graphics.TOP | Graphics.HCENTER);
                        } else {
                            g.drawString(ConstellationCatalog.getConstellation(i).getLatinName(), screenCoordStars[kStar1].x, screenCoordStars[kStar1].y, Graphics.TOP | Graphics.HCENTER);
                        }
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
        int nbOfStars = mySky.getStarsProj().length;
        boolean isStarColored = myMidlet.getMyParameter().isStarColored();
        boolean isStarShownAsCircle = myMidlet.getMyParameter().isStarShownAsCircle();
        float maxMag = myMidlet.getMyParameter().getMaxMag();
        int[] color = myMidlet.getMyParameter().getColor();

        for (int k = 0; k < nbOfStars; k++) {
            if (screenCoordStars[k].isVisible()) {
                // Star is above horizon
                float magf = mySky.getStar(k).getObject().getMag();
                if (magf < maxMag) {
                    int mag = (int) (magf - 0.3F);
                    if (mag > 5) {
                        mag = 5;
                    }
                    if (mag < 0) {
                        mag = 0;
                        // Select color of star
                    }
                    if (isStarColored) {
                        int col = color[Color.COL_STAR_MAG0 + mag];
                        g.setColor(col);
                    } else {
                        g.setColor(color[Color.COL_STAR_MAG0]);
                    }
                    // Represent star as a filled circle or as a dot
                    if (isStarShownAsCircle) {
                        // As a circle
                        int size = 5 - mag;
                        if (size > 4) {
                            size = 4;
                        }
                        if (size < 1) {
                            g.drawLine(screenCoordStars[k].x, screenCoordStars[k].y, screenCoordStars[k].x, screenCoordStars[k].y);
                        } else {
                            g.fillArc(screenCoordStars[k].x - size, screenCoordStars[k].y - size, 2 * size, 2 * size, 0, 360);
                        }
                    } else {
                        // Or as a dot
                        g.drawLine(screenCoordStars[k].x, screenCoordStars[k].y, screenCoordStars[k].x, screenCoordStars[k].y);
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
        int[] color = rMyParam.getColor();

        if (rMyParam.isMessierDisplayed() || rMyParam.isMessierNameDisplayed()) {
            for (int k = 0; k < MessierCatalog.getNumberOfObjects(); k++) {
                if (screenCoordMessier[k].isVisible()) {
                    // Messier object is above horizon
                    g.setColor(color[Color.COL_MESSIER]);
                    if (rMyParam.isMessierDisplayed()) {
                        g.drawLine(screenCoordMessier[k].x, screenCoordMessier[k].y, screenCoordMessier[k].x, screenCoordMessier[k].y);
                    }
                    if (rMyParam.isMessierNameDisplayed()) {
                        g.drawString(mySky.getMessier(k).getObject().getName(), screenCoordMessier[k].x, screenCoordMessier[k].y, Graphics.TOP | Graphics.HCENTER);
                    }
                }
            }
        }
    }

    /**
     *
     * @param g
     */
    private void drawSystemSolarObjects(Graphics g) {
        ConfigParameters rMyParam = myMidlet.getMyParameter();
        int[] color = rMyParam.getColor();

        // -------------------------
        // ------ Display moon -----
        if (screenCoordMoon.isVisible()) {
            g.setColor(color[Color.COL_MOON]);
            int z = (int) (myProjection.getZoom() * SIZE_MOON_SUN);
            if (rMyParam.isPlanetDisplayed()) {
                switch (mySky.getMoon().getPhase()) {
                    case MoonProj.FIRST:
                        g.fillArc((int) screenCoordMoon.x - z, (int) screenCoordMoon.y - z, 2 * z, 2 * z, -100, 200);
                        break;
                    case MoonProj.LAST:
                        g.fillArc((int) screenCoordMoon.x - z, (int) screenCoordMoon.y - z, 2 * z, 2 * z, 80, 200);
                        break;
                    case MoonProj.NEW:
                        g.drawArc((int) screenCoordMoon.x - z, (int) screenCoordMoon.y - z, 2 * z, 2 * z, 0, 360);
                        break;
                    case MoonProj.FULL:
                        g.fillArc((int) screenCoordMoon.x - z, (int) screenCoordMoon.y - z, 2 * z, 2 * z, 0, 360);
                        break;
                }
            }
            if (rMyParam.isPlanetNameDisplayed()) {
                g.drawString(LocalizationSupport.getMessage("NAME_MOON"), (int) screenCoordMoon.x, (int) screenCoordMoon.y - myFont.getHeight() - z, Graphics.TOP | Graphics.HCENTER);
            }
        }
        // -------------------------
        // ------ Display sun -----
        if (screenCoordSun.isVisible()) {
            g.setColor(color[Color.COL_SUN]);
            int z = (int) (myProjection.getZoom() * SIZE_MOON_SUN);
            if (rMyParam.isPlanetDisplayed()) {
                g.fillArc((int) screenCoordSun.x - z, (int) screenCoordSun.y - z, 2 * z, 2 * z, 0, 360);
            }
            if (rMyParam.isPlanetNameDisplayed()) {
                g.drawString(LocalizationSupport.getMessage("NAME_SUN"), (int) screenCoordSun.x, (int) screenCoordSun.y - myFont.getHeight() - z, Graphics.TOP | Graphics.HCENTER);
            }
        }
        // ---------------------------
        // ------ Display planets -----
        for (int k = 0; k < Sky.NB_OF_PLANETS; k++) {
            if (screenCoordPlanets[k].isVisible()) {
                g.setColor(color[(Color.COL_PLANET) + k]);
                if (rMyParam.isPlanetDisplayed()) {
                    g.fillArc((int) screenCoordPlanets[k].x - 2, (int) screenCoordPlanets[k].y - 2, 4, 4, 0, 360);
                }
                if (rMyParam.isPlanetNameDisplayed()) {
                    g.drawString(mySky.getPlanet(k).getObject().getName(), (int) screenCoordPlanets[k].x, (int) screenCoordPlanets[k].y - myFont.getHeight(), Graphics.TOP | Graphics.HCENTER);
                }
            }
        }
    }

    /**
     * Draw cursors
     * @param g2 the Graphics object
     */
    private void drawCursor(Graphics g2) {
        int z;
        ConfigParameters rMyParam = myMidlet.getMyParameter();
        int[] color = rMyParam.getColor();

        // -------------------------------
        // --- Recalculate hei and az ----
        if (counter % MAX_CPS == 0) {
            getUpdatedHeightAndAzimuth();
        }

        // -------------------------------
        // --- Highlight selected star ---
        g2.setColor(color[Color.COL_CURSOR]);
        angle = (short) ((angle + 15) % 360);

        azName = AZ_NAME;
        heiName = HEI_NAME;
        switch (typeClosestObject) {
            case SkyObject.STAR:
                g2.drawArc(screenCoordStars[idxClosestObject].x - 4, screenCoordStars[idxClosestObject].y - 4, 8, 8, angle, 90);
                g2.drawArc(screenCoordStars[idxClosestObject].x - 4, screenCoordStars[idxClosestObject].y - 4, 8, 8, angle + 180, 90);
                azName += mySky.getStar(idxClosestObject).getRealAzimuthAsString();
                heiName += mySky.getStar(idxClosestObject).getRealHeightAsString();
                break;
            case SkyObject.SUN:
                z = (int) (myProjection.getZoom() * SIZE_MOON_SUN) + 2;
                g2.drawArc((int) screenCoordSun.x - z, (int) screenCoordSun.y - z, 2 * z, 2 * z, angle, 90);
                g2.drawArc((int) screenCoordSun.x - z, (int) screenCoordSun.y - z, 2 * z, 2 * z, angle + 180, 90);
                azName += mySky.getSun().getRealAzimuthAsString();
                heiName += mySky.getSun().getRealHeightAsString();
                break;
            case SkyObject.MOON:
                z = (int) (myProjection.getZoom() * SIZE_MOON_SUN) + 2;
                g2.drawArc((int) screenCoordMoon.x - z, (int) screenCoordMoon.y - z, 2 * z, 2 * z, angle, 90);
                g2.drawArc((int) screenCoordMoon.x - z, (int) screenCoordMoon.y - z, 2 * z, 2 * z, angle + 180, 90);
                azName += mySky.getMoon().getRealAzimuthAsString();
                heiName += mySky.getMoon().getRealHeightAsString();
                break;
            case SkyObject.MESSIER:
                g2.drawArc(screenCoordMessier[idxClosestObject].x - 4, screenCoordMessier[idxClosestObject].y - 4, 8, 8, angle, 90);
                g2.drawArc(screenCoordMessier[idxClosestObject].x - 4, screenCoordMessier[idxClosestObject].y - 4, 8, 8, angle + 180, 90);
                azName += mySky.getMessier(idxClosestObject).getRealAzimuthAsString();
                heiName += mySky.getMessier(idxClosestObject).getRealHeightAsString();
                break;
            case SkyObject.PLANET:
                g2.drawArc(screenCoordPlanets[idxClosestObject].x - 4, screenCoordPlanets[idxClosestObject].y - 4, 8, 8, angle, 90);
                g2.drawArc(screenCoordPlanets[idxClosestObject].x - 4, screenCoordPlanets[idxClosestObject].y - 4, 8, 8, angle + 180, 90);
                azName += mySky.getPlanet(idxClosestObject).getRealAzimuthAsString();
                heiName += mySky.getPlanet(idxClosestObject).getRealHeightAsString();
                break;
        }
        // -------------------
        // --- Draw Cursor ---
        if (displayCursor > 0) {
            g2.drawImage(cursorImage, xCursor, yCursor, Graphics.HCENTER | Graphics.VCENTER);
            displayCursor--;
        }
        // --------------------------
        // --- Display info text  ---
        int col = 0;
        int maxl;
        int vSize = NB_OF_LINES * myFont.getHeight() + 8;

        // ----------------------------------
        // --- Display constellation name ---
        if (typeClosestObject == SkyObject.STAR) {
            if (colorConstName < color[Color.COL_CONST_NAME_MAX]) {
                col = (colorConstName > color[Color.COL_CONST_NAME_MAX] / 2) ? color[Color.COL_CONST_NAME_MAX] - colorConstName : colorConstName;
                g2.setColor(color[Color.COL_CONST_NAME_MIN] + col);
                colorConstName += color[Color.COL_CONST_NAME_INC];

                if (yCursor < getHeight / 4 || (yCursor >= getHeight / 2 && yCursor < 3 * getHeight / 4)) { // Displayed below the cursor
                    g2.drawString(constName2, xCursor, yCursor + 2 * myFont.getHeight(), Graphics.HCENTER | Graphics.TOP);
                } else {// Displayed above the cursor
                    g2.drawString(constName2, xCursor, yCursor - 3 * myFont.getHeight(), Graphics.HCENTER | Graphics.TOP);
                }

            } else {
                // We have reached the point where we have to move from local language name to latin name or vice-versa
                colorConstName = 0;
                int oldPos = shiftConstName;
                shiftConstName = constName1.indexOf("/", shiftConstName) + 1;
                if (shiftConstName == 0) {
                    oldPos = 0;
                    shiftConstName = constName1.indexOf("/", shiftConstName) + 1;
                }
                constName2 = constName1.substring(oldPos, shiftConstName - 1);
            }

        }

        // Find maximum length in pixel of text to be displayed;
        maxl = 0;
        if (myFont.stringWidth(magName) > maxl) {
            maxl = myFont.stringWidth(magName);
        }

        if (myFont.stringWidth(distName) > maxl) {
            maxl = myFont.stringWidth(distName);
        }

        if (myFont.stringWidth(azName) > maxl) {
            maxl = myFont.stringWidth(azName);
        }

        if (myFont.stringWidth(heiName) > maxl) {
            maxl = myFont.stringWidth(heiName);
        }

        if (myFont.stringWidth(objectName) > maxl) {
            maxl = myFont.stringWidth(objectName);
        }

        maxl += 8;

        // Calculate position of info box
        if (yCursor < getHeight / 2) {
            if (rMyParam.isHelpDisplayed()) {
                yInfoDest = getHeight - vSize - myFont.getHeight() - 4;
            } else {
                yInfoDest = getHeight - vSize - 4;
            }
        } else {
            yInfoDest = 6;
        }

        if (xCursor < getWidth / 2) {
            xInfoDest = getWidth - maxl - 4;
        } else {
            xInfoDest = 6;
        }

        xInfo = xInfo + (xInfoDest - xInfo) / 4;
        yInfo = yInfo + (yInfoDest - yInfo) / 4;
        if ((xInfo + maxl + 4) > getWidth) {
            xInfo = getWidth - maxl - 4;        // Get background image (behing info box) to do transparence
        }

        if ((yInfo + vSize) > getHeight) {
            vSize = getHeight - yInfo;
        }

        // Display text in info box
        g2.setColor(color[Color.COL_BOX_TEXT]);
        g2.drawString(objectName, xInfo + 4, yInfo + 4, Graphics.LEFT | Graphics.TOP);
        g2.drawString(magName, xInfo + 4, yInfo + 1 * myFont.getHeight() + 4, Graphics.LEFT | Graphics.TOP);
        g2.drawString(distName, xInfo + 4, yInfo + 2 * myFont.getHeight() + 4, Graphics.LEFT | Graphics.TOP);
        g2.drawString(azName, xInfo + 4, yInfo + 3 * myFont.getHeight() + 4, Graphics.LEFT | Graphics.TOP);
        g2.drawString(heiName, xInfo + 4, yInfo + 4 * myFont.getHeight() + 4, Graphics.LEFT | Graphics.TOP);
        g2.setColor(color[Color.COL_BOX]);
        g2.drawRoundRect(xInfo - 1, yInfo - 1, maxl + 1, vSize + 1, 8, 8);
    }

    /**
     * 
     * @param g
     */
    private void drawHelp(Graphics g) {
        int x, y, w, h;
        String s;

        h = getHeight * 9 / 10;
        w = h * 3 / 4;
        x = getWidth / 2 - w / 2;
        y = getHeight / 2 - h / 2;

        g.setColor(0);
        g.fillRect(x, y, w, h);
        g.setColor(0xff);
        for (int i = 0; i < 12; i++) {
            g.drawRect(x + (i % 3) * w / 3, y + (i / 3) * h / 4, w / 3, h / 4);
            if (i == 9) {
                s = new String("*");
            } else if (i == 11) {
                s = new String("#");
            } else if (i == 10) {
                s = new String("0");
            } else {
                s = new Integer((i + 1) % 10).toString();
            }
            g.drawString(s, x + (i % 3) * w / 3, y + (i / 3) * h / 4, Graphics.LEFT | Graphics.TOP);
        }
    }

    /**
     * Highlight the searched object if visible
     * @param g the graphic object
     */
    private void drawSearched(Graphics g) {
        if (myMidlet.getSearch().getTypeHighlight() != SkyObject.NONE) {
            int z;
            int[] color = myMidlet.getMyParameter().getColor();
            int idx = myMidlet.getSearch().getIdxHighlight();
            angleHighlight = (short) ((angleHighlight + 15) % 360);
            z = (int) (myProjection.getZoom() * SIZE_MOON_SUN) + 2;

            g.setColor(color[Color.COL_HIGHLIGHT]);
            switch (myMidlet.getSearch().getTypeHighlight()) {
                case SkyObject.STAR:
                    if (screenCoordStars[idx].isVisible()) {
                        g.drawArc(screenCoordStars[idx].x - 4, screenCoordStars[idx].y - 4, 8, 8, angleHighlight, 90);
                        g.drawArc(screenCoordStars[idx].x - 4, screenCoordStars[idx].y - 4, 8, 8, angleHighlight + 180, 90);
                    }
                    break;
                case SkyObject.MESSIER:
                    if (screenCoordMessier[idx].isVisible()) {
                        g.drawArc(screenCoordMessier[idx].x - 4, screenCoordMessier[idx].y - 4, 8, 8, angleHighlight, 90);
                        g.drawArc(screenCoordMessier[idx].x - 4, screenCoordMessier[idx].y - 4, 8, 8, angleHighlight + 180, 90);
                    }
                    break;
                case SkyObject.SUN:
                    if (screenCoordSun.isVisible()) {
                        g.drawArc(screenCoordSun.x - z, screenCoordSun.y - z, 2 * z, 2 * z, angleHighlight, 90);
                        g.drawArc(screenCoordSun.x - z, screenCoordSun.y - z, 2 * z, 2 * z, angleHighlight + 180, 90);
                    }
                    break;
                case SkyObject.MOON:
                    if (screenCoordMoon.isVisible()) {
                        g.drawArc(screenCoordMoon.x - z, screenCoordMoon.y - z, 2 * z, 2 * z, angleHighlight, 90);
                        g.drawArc(screenCoordMoon.x - z, screenCoordMoon.y - z, 2 * z, 2 * z, angleHighlight + 180, 90);
                    }
                    break;
                case SkyObject.PLANET:
                    if (screenCoordPlanets[idx].isVisible()) {
                        g.drawArc(screenCoordPlanets[idx].x - 4, screenCoordPlanets[idx].y - 4, 8, 8, angleHighlight, 90);
                        g.drawArc(screenCoordPlanets[idx].x - 4, screenCoordPlanets[idx].y - 4, 8, 8, angleHighlight + 180, 90);
                    }
                    break;
                case SkyObject.CONSTELLATION:
                    idx = ConstellationCatalog.getConstellation(idx).getRefStar4ConstellationName();
                    if (screenCoordStars[idx].isVisible()) {
                        g.drawArc(screenCoordStars[idx].x - 4, screenCoordStars[idx].y - 4, 8, 8, angleHighlight, 90);
                        g.drawArc(screenCoordStars[idx].x - 4, screenCoordStars[idx].y - 4, 8, 8, angleHighlight + 180, 90);
                    }
                    break;
            }
        }
    }

    /**
     * Update height and Azimuth for object pointed by cursor
     */
    private void getUpdatedHeightAndAzimuth() {
        mySky.calculateTimeVariables();

        switch (typeClosestObject) {
            case SkyObject.STAR:
                mySky.getStar(idxClosestObject).calculate();
                break;
            case SkyObject.MESSIER:
                mySky.getMessier(idxClosestObject).calculate();
                break;
            case SkyObject.SUN:
                mySky.getSun().calculate();
                break;
            case SkyObject.MOON:
                mySky.getMoon().calculate();
                break;
            case SkyObject.PLANET:
                mySky.getPlanet(idxClosestObject).calculate();
                break;

        }
    }

    /**
     * Find the closest star to the cursor
     */
    public void findClosestObject() {
        int i, idx, type;
        float maxMag;
        double d;
        double min = getWidth + getHeight;

        idx = -1;
        // Look for closest star.
        maxMag = myMidlet.getMyParameter().getMaxMag();
        type = SkyObject.NONE;
        for (i = 0; i < screenCoordStars.length; i++) {
            if (screenCoordStars[i].isVisible() && mySky.getStar(i).getObject().getMag() < maxMag) {
                d = Math.abs(xCursor - screenCoordStars[i].x) + Math.abs(yCursor - screenCoordStars[i].y);
                if (d < min) {
                    min = d;
                    idx = i;
                    type = SkyObject.STAR;
                }
            }
        }
        // Look for closest messier object.
        if (myMidlet.getMyParameter().isMessierDisplayed()) {
            for (i = 0; i < screenCoordMessier.length; i++) {
                if (screenCoordMessier[i].isVisible()) {
                    d = Math.abs(xCursor - screenCoordMessier[i].x) + Math.abs(yCursor - screenCoordMessier[i].y);
                    if (d < min) {
                        min = d;
                        idx = i;
                        type = SkyObject.MESSIER;
                    }
                }
            }
        }
        if (myMidlet.getMyParameter().isPlanetDisplayed()) {
            // Sun
            d = Math.abs(xCursor - screenCoordSun.x) + Math.abs(yCursor - screenCoordSun.y);
            if (d < min && screenCoordSun.isVisible()) {
                min = d;
                type = SkyObject.SUN;
            }
            // Moon
            d = Math.abs(xCursor - screenCoordMoon.x) + Math.abs(yCursor - screenCoordMoon.y);
            if (d < min && screenCoordMoon.isVisible()) {
                min = d;
                type = SkyObject.MOON;
            }
            // Planets
            for (i = 0; i < Sky.NB_OF_PLANETS; i++) {
                d = Math.abs(xCursor - screenCoordPlanets[i].x) + Math.abs(yCursor - screenCoordPlanets[i].y);
                if (d < min && screenCoordPlanets[i].isVisible()) {
                    min = d;
                    idx = i;
                    type = SkyObject.PLANET;
                }
            }
        }
        // Display star information
        if (idx == -1) {
            myMidlet.getMyParameter().setCursor(false);                                          // Remove cursor is not close object found. Not too clean
        } else if (idx != idxClosestObject || type != typeClosestObject) {
            // The closest star is a new star
            idxClosestObject = (short) idx;
            typeClosestObject = type;
            switch (typeClosestObject) {
                case SkyObject.SUN:
                    objectName = LocalizationSupport.getMessage("NAME_SUN");
                    magName = new String(LocalizationSupport.getMessage("CURSOR_MAGNITUDE_ABBR") + "-26.7");
                    distName = new String(LocalizationSupport.getMessage("CURSOR_DISTANCE_ABBR") + (int) MathFunctions.toMKm(Projection.getRSun()) + " mKm");
                    break;
                case SkyObject.MOON:
                    objectName = LocalizationSupport.getMessage("NAME_MOON");
                    magName = new String(LocalizationSupport.getMessage("CURSOR_MAGNITUDE_ABBR") + "-12");
                    distName = new String(LocalizationSupport.getMessage("CURSOR_DISTANCE_ABBR") + (int) mySky.getMoon().getDistance() + " km");
                    break;
                case SkyObject.PLANET:
                    objectName = mySky.getPlanet(idx).getObject().getName();
                    magName = new String(LocalizationSupport.getMessage("CURSOR_MAGNITUDE_ABBR") + mySky.getPlanet(idx).getObject().getMag());
                    distName = new String(LocalizationSupport.getMessage("CURSOR_DISTANCE_ABBR") + (int) MathFunctions.toMKm(mySky.getPlanet(idx).getDistance()) + " mKm");
                    break;
                case SkyObject.MESSIER:
                    objectName = new String(MessierCatalog.getObject(idx).getName());
                    magName = new String(LocalizationSupport.getMessage("CURSOR_MAGNITUDE_ABBR") + MessierCatalog.getObject(idx).getMag());
                    distName = new String(LocalizationSupport.getMessage("CURSOR_DISTANCE_ABBR") + MessierCatalog.getObject(idx).getDist() + " k" + LocalizationSupport.getMessage("CURSOR_LIGHTYEAR_ABBR"));
                    break;
                case SkyObject.STAR:
                    objectName = new String(((StarObject) (mySky.getStar(idx).getObject())).getBayerName() + " " + ((StarObject) (mySky.getStar(idx).getObject())).getConstellationName());
                    if (mySky.getStar(idx).getObject().getName().length() != 0) {
                        objectName += new String(" - " + mySky.getStar(idx).getObject().getName());
                    }

                    magName = new String(LocalizationSupport.getMessage("CURSOR_MAGNITUDE_ABBR") + mySky.getStar(idx).getObject().getMag());
                    distName = new String(LocalizationSupport.getMessage("CURSOR_DISTANCE_ABBR") + ((StarObject) (mySky.getStar(idx).getObject())).getDistance() + " " + LocalizationSupport.getMessage("CURSOR_LIGHTYEAR_ABBR"));
                    // Is this star belongs to a new constellation ?

                    if (((StarObject) (mySky.getStar(idx).getObject())).getConstellation() != idxClosestConst) {
                        idxClosestConst = ((StarObject) (mySky.getStar(idx).getObject())).getConstellation();
                        constName1 = ConstellationCatalog.constNames[idxClosestConst][ConstellationCatalog.NAME] + "/" + ConstellationCatalog.constNames[idxClosestConst][ConstellationCatalog.LATIN] + "/";
                        constName2 = ConstellationCatalog.constNames[idxClosestConst][ConstellationCatalog.NAME];
                        shiftConstName = constName1.indexOf("/", 0) + 1;
                        colorClosestConst = 0;//color[rMyParam.getColor()][COL_CONST_MAX]/2;
                        colorConstName = 0;//color[rMyParam.getColor()][COL_CONST_NAME]/2;
                    }
                    break;
            }
        }
    }

    /**
     * The pointer is dragged
     * @param x
     * @param y
     */
    protected void pointerDragged(int x, int y) {
        int keyCode;
        keyCode = touchScreen.drag(x, y);

        if (keyCode == TouchScreen.MOVE) {
            touchScreen.setScroll(x, y, touchScreen.getxPressed(), touchScreen.getyPressed());
        }
    }

    /**
     * The pointer is pressed
     * @param x
     * @param y
     */
    protected void pointerPressed(int x, int y) {
        touchScreen.setTimeBaseForScroll(System.currentTimeMillis());
        touchScreen.setPressed(x, y);
        touchScreen.setScroll(false);
    }

    /**
     * The pointer is released
     * @param x
     * @param y
     */
    protected void pointerReleased(int x, int y) {
        int keyCode;
        keyCode = touchScreen.setReleased(x, y);

        if (keyCode == TouchScreen.ZOOM_IN) {
            myProjection.incZoom();
            project();
        }
        if (keyCode == TouchScreen.ZOOM_OUT) {
            myProjection.decZoom();
            project();
        }
        if (keyCode == TouchScreen.ROT_LEFT) {
            myProjection.left();
            project();
        }
        // Rotation
        if (keyCode == TouchScreen.ROT_RIGHT) {
            myProjection.right();
            project();
        }

        if (keyCode == TouchScreen.CURSOR_ON) {
            xCursor = x;
            yCursor = y;
            int tmp1, tmp2;
            tmp1 = idxClosestObject;
            tmp2 = typeClosestObject;
            findClosestObject();
            if (myMidlet.getMyParameter().isCursorDisplayed()) {
                myMidlet.getMyParameter().setCursor((tmp1 != idxClosestObject) || (tmp2 != typeClosestObject));
            } else {
                myMidlet.getMyParameter().setCursor(true);
            }
        }
    }

    /**
     *
     * @param keyCode
     */
    protected void keyPressed(int keyCode) {
        // =====================================================================
        // ============================ All modes ==============================
        // 1 = Zoom out
        if (keyCode == KEY_NUM1) {
            myProjection.decZoom();
            project();
        }
        // 3 = Zoom in
        if (keyCode == KEY_NUM3) {
            myProjection.incZoom();
            project();
        }
        if (!myMidlet.getMyParameter().isCursorDisplayed()) {
            // =================================================================
            // ===================== Default mode ==============================
            // * = Next display
            if (keyCode == KEY_STAR) {
                if (myMidlet.getMyParameter().isSupport3D()) {
                    running = false;
                    myMidlet.nextDisplay();
                }
            }
            // 0 = Show cursor
            if (keyCode == KEY_NUM0) {
                myMidlet.getMyParameter().setCursor(true);
                displayCursor = COUNTER_CURSOR;
                findClosestObject();
            }
            // <- = Rotation
            if (getGameAction(keyCode) == LEFT) {
                myProjection.left();
                project();
            }
            // -> Rotation
            if (getGameAction(keyCode) == RIGHT) {
                myProjection.right();
                project();
            }
            // ^
            // | Shift Y
            if (getGameAction(keyCode) == UP) {
                myProjection.up();
                project();
            }
            // |
            // " Shift Y
            if (getGameAction(keyCode) == DOWN) {
                myProjection.down();
                project();
            }
//            // Shift X
//            if (getGameAction(keyCode) == LEFT) {
//                myProjection.incShiftX();
//                project();
//            }
//            // Shift X
//            if (getGameAction(keyCode) == LEFT) {
//                myProjection.decShiftX();
//                project();
//            }
        } else {
            // =================================================================
            // ========================= Cursor mode ===========================
            // 0 = Hide cursor
            if (keyCode == KEY_NUM0) {
                myMidlet.getMyParameter().setCursor(false);
            }
            if (getGameAction(keyCode) == UP) {
                decYCursor();
                displayCursor = COUNTER_CURSOR;
                findClosestObject();
            }
            if (getGameAction(keyCode) == DOWN) {
                incYCursor();
                displayCursor = COUNTER_CURSOR;
                findClosestObject();
            }
            if (getGameAction(keyCode) == LEFT) {
                decXCursor();
                displayCursor = COUNTER_CURSOR;
                findClosestObject();
            }
            if (getGameAction(keyCode) == RIGHT) {
                incXCursor();
                displayCursor = COUNTER_CURSOR;
                findClosestObject();
            }
        }
    }

    /**
     * A key is released
     * @param keyCode the code of the key
     */
    protected void keyReleased(int keyCode) {
        super.keyReleased(keyCode);
    }

    /**
     * A key is repeated
     * @param keyCode
     */
    protected void keyRepeated(int keyCode) {
        if (keyCode == KEY_POUND) {
            help = !help;
        } else {
            keyPressed(keyCode);
        }
    }

    /**
     * Inc x coordinate of the cursor
     */
    public void incXCursor() {
        if (xCursor < getWidth - myProjection.getZoom() * 2) {
            xCursor += myProjection.getZoom() * 2;
        } else {
            myProjection.left();
            project();
        }
    }

    /**
     * Dec x coordinate of the cursor
     */
    public void decXCursor() {
        if (xCursor > 2 * myProjection.getZoom() - 1) {
            xCursor -= 2 * myProjection.getZoom();
        } else {
            myProjection.right();
            project();
        }
    }

    /**
     * Inc y coordinate of the cursor
     */
    public void incYCursor() {
        if (yCursor < getHeight - 2 * myProjection.getZoom()) {
            yCursor += 2 * myProjection.getZoom();
        } else {
            myProjection.down();
            project();
        }
    }

    /**
     * Dec y coordinate of the cursor
     */
    public void decYCursor() {
        if (yCursor > 2 * myProjection.getZoom() - 1) {
            yCursor -= 2 * myProjection.getZoom();
        } else {
            myProjection.up();
            project();
        }
    }

    /**
     * Stop the thread
     */
    public void stop() {
        running = false;
    }

    /**
     * The main loop:
     * Every COUNTER calls, recalculate the position of all objects
     * Every call, repaint.
     */
    public void run() {
        long cycleStartTime;

        getHeight = getHeight();                                                // Due to bug of 6680 and 6630
        getWidth = getWidth();                                                  // Due to bug of 6680 and 6630
        while (running) {
            cycleStartTime = System.currentTimeMillis();
            // Check if we need to calculate position
            if (counter == 0) {
                // Yes, do it in a separate thread
                new Thread(mySky).start();
                //System.out.println("Start Sky Thread");
                counter = COUNTER;
            } else {
                // No, decrease counter
                counter--;
            }
            // Display scene.
            render(graphics);
            flushGraphics();

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
        myProjection.project();
    }

    /**
     * Called when the display will be shown (just before)
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
        // Rearrange the toolbar
        if (touchScreen != null) {
            touchScreen.setSize(w, h);
        }
    }
    /**
     *
     * @param img
     * @param step
     * @return
     */
//    private Image transform(Image img,int step) {
//        Image imgRet;
//        int rgb[];
//        int val;
//        int mask;
//
//        mask = (0xff >> step) << step;
//        mask = (mask << 16) | (mask << 8) | (mask);
//        rgb = new int[img.getWidth()*img.getHeight()];
//        img.getRGB(rgb, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());
//        for (int  i=0;i<img.getWidth()*img.getHeight();i++) {
//            val = rgb[i] & mask;
//            val >>= step;
//            rgb[i] = val | 0xff000000;
//        }
//        imgRet = Image.createRGBImage(rgb, img.getWidth(), img.getHeight(), true);
//        return imgRet;
//    }
}
