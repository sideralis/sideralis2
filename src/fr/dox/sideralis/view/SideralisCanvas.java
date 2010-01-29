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
import fr.dox.sideralis.Help;
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
import java.util.Calendar;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * A class derived from a Canvas to display the stars
 * Input is the sky with all virtual x and y coordinate of the stars.
 * Output is a display of these stars on the real screen. It depends of course of zoom, rotation and height shift.
 *
 * @author Bernard
 */
public class SideralisCanvas extends Canvas implements Runnable {

    /** Flag for stopping the animation thread.*/
    private boolean runningCmd;
    /** Flag indicating if the animation thread has stopped */
    private boolean running;
    /** The calling midlet */
    private final Sideralis myMidlet;
    /** The sky object */
    private final Sky mySky;
    /** The projection used to display the objects on the screen */
    private ScreenProj myProjection;
    /** Table to store x and y position on screen of stars */
    protected ScreenCoord[] screenCoordStars;
    /** Table to store x and y position on screen of Messier objects */
    protected ScreenCoord[] screenCoordMessier;
    /** To store x and y position on screen of Sun */
    protected ScreenCoord screenCoordSun;
    /** To store x and y position on screen of Moon */
    protected ScreenCoord screenCoordMoon;
    /** Table to store x and y position on screen of planets */
    protected ScreenCoord[] screenCoordPlanets;
    /** The font used */
    private final Font myFont;
    /** The height of the font used */
    private int heightFont;
    // ------------------
    // --- For cursor ---
    /** Index of the closest constellation */
    private int idxClosestConst;
    /** Index of the closest object */
    private int idxClosestObject;
    /** Type of the closest object (Planet, Star, Messier, ...) */
    private int typeClosestObject;
    /** Toggling color of the closest constellation */
    private int colorClosestConst;
    /** The counter associated to this action: if the cursor does not move for sometime, it is not displayed anymore */
    private short displayCursor;
    /** The image for the cursor */
    private Image cursorImage;
    /** Position of cursor */
    private int xCursor, yCursor;
    /** All informations about object */
    private String objectName, magName, distName, azName, heiName;
    /** The string used for azimuth and height string - Defined at beginning to avoid multiple call the Localization class */
    private String AZ_NAME, HEI_NAME;
    /** Number of lines which will be used to display informations on star */
    private static final short NB_OF_LINES = 5;
    /** An variable used to modify the display of the const name */
    private int shiftConstName;
    private int colorConstName;
    /** Name of the constellation which is shown by the cursor */
    private String constName1, constName2;
    /** The offset of the text containing the info text */
    private int xInfoDest, xInfo;
    private int yInfoDest, yInfo;
    /** The offset of the text containing the debug text */
    private int xInfoText,yInfoText;
    /** The String containing the history of Constellation */
    private String constText;
    /** The offset of the text containing the constellation history */
    private int yConstText;

    /** Default size in pixel for Moon and Sun */
    private static final short SIZE_MOON_SUN = 2;
    /** Counter used to decide when to recalculate all positons */
    private int counter;
    /** All objects positions are recalculated every COUNTER calls to run() */
    private static final int COUNTER = 1000;
    /** The reset value of display Cursor before it disappears */
    private static final short COUNTER_CURSOR = 10;
    /** The object used to manage touch screen */
    private TouchScreen touchScreen;
    /** The position of the circle around the selected or highlighted objects */
    private short angle;
    private short angleHighlight;
    /** The Help object */
    private Help myHelp;

    /** Timer for performance calculation */
    private long timeCalculate, timeDisplay;

    /** Number of frames per second */
    private static final int MAX_CPS = 10;                                      
    /** Time in millisecond between 2 frames */
    private static final int MS_PER_FRAME = 1000 / MAX_CPS;

    /**
     * Constructor of the class
     * @param myMidlet a reference to the midlet creating the canvas
     */
    public SideralisCanvas(Sideralis myMidlet) {
        //super(false);                                                         // GameCanvas
        setFullScreenMode(true);

        this.myMidlet = myMidlet;
        runningCmd = running = false;
        mySky = myMidlet.getMySky();
        myFont = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL);
        heightFont = myFont.getHeight();
        AZ_NAME = new String(LocalizationSupport.getMessage("CURSOR_AZIMUTH_ABBR"));
        HEI_NAME = new String(LocalizationSupport.getMessage("CURSOR_HEIGHT_ABBR"));
        counter = COUNTER;

        try {
            cursorImage = Image.createImage("/Cursor.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create and initialize all objects needed to display the sky objects at the right position
     */
    public void init() {

        if (hasPointerEvents()) {
            touchScreen = new TouchScreen(getWidth(), getHeight(),myMidlet);
        } else {
            touchScreen = null;
        }

        myHelp = new Help(myMidlet,new String[]{LocalizationSupport.getMessage("ZO"),
                    LocalizationSupport.getMessage("UP"),
                    LocalizationSupport.getMessage("ZI"),
                    LocalizationSupport.getMessage("LFT"), "",
                    LocalizationSupport.getMessage("RIT"), "",
                    LocalizationSupport.getMessage("DWN"), "",
                    LocalizationSupport.getMessage("ZEV"),
                    LocalizationSupport.getMessage("DIC"),
                    LocalizationSupport.getMessage("DIH")});
        myHelp.setView(getWidth(), getHeight());

        if (!myMidlet.getMyParameter().isSupport3D()) {
            myHelp.setText(Help.STAR, "");
        }

        idxClosestObject = idxClosestConst = -1;
        colorClosestConst = 0;

        if (myMidlet.getMyParameter().isSupport3D()) {
        //#ifdef JSR184
            myProjection = new EyeProj(myMidlet, getWidth(), getHeight());
        //#endif
        } else {
            myProjection = new ZenithProj(myMidlet, getWidth(), getHeight());
            myHelp.setText(Help.STAR, "");
        }

        myProjection.init();
        screenCoordMessier = myProjection.getScreenCoordMessier();
        screenCoordMoon = myProjection.getScreenCoordMoon();
        screenCoordPlanets = myProjection.getScreenCoordPlanets();
        screenCoordStars = myProjection.getScreenCoordStars();
        screenCoordSun = myProjection.getScreenCoordSun();

        xCursor = getWidth() / 2;
        yCursor = getHeight() / 2;
    }

    /**
     * Set the counter indicating when the position of objects must be calculated again
     * @param counter
     */
    public void setCounter(int counter) {
        this.counter = counter;
    }
    /**
     * Render the graphic scene
     * @param g
     */
    public void paint(Graphics g) {
        // =============================================================
        // === Check if we need to center screen to searched object ====
        // =============================================================
        if (!myMidlet.getSearch().isFlagCentered()) {
            double diffAz=0, diffHe=0;
            short x=0,y=0;
            // Get information about searched object
            switch (myMidlet.getSearch().getTypeHighlight()) {
                case SkyObject.STAR:
                    diffAz = mySky.getStar(myMidlet.getSearch().getIdxHighlight()).getAzimuth() + Math.PI/2;
                    diffHe = myProjection.getRotV() - mySky.getStar(myMidlet.getSearch().getIdxHighlight()).getHeight();
                    x = screenCoordStars[myMidlet.getSearch().getIdxHighlight()].x;
                    y = screenCoordStars[myMidlet.getSearch().getIdxHighlight()].y;
                    break;
                case SkyObject.MESSIER:
                    diffAz = mySky.getMessier(myMidlet.getSearch().getIdxHighlight()).getAzimuth() + Math.PI/2;
                    diffHe = myProjection.getRotV() - mySky.getMessier(myMidlet.getSearch().getIdxHighlight()).getHeight();
                    x = screenCoordMessier[myMidlet.getSearch().getIdxHighlight()].x;
                    y = screenCoordMessier[myMidlet.getSearch().getIdxHighlight()].y;
                    break;
                case SkyObject.PLANET:
                    diffAz = mySky.getPlanet(myMidlet.getSearch().getIdxHighlight()).getAzimuth() + Math.PI/2;
                    diffHe = myProjection.getRotV() - mySky.getPlanet(myMidlet.getSearch().getIdxHighlight()).getHeight();
                    x = screenCoordPlanets[myMidlet.getSearch().getIdxHighlight()].x;
                    y = screenCoordPlanets[myMidlet.getSearch().getIdxHighlight()].y;
                    break;
                case SkyObject.SUN:
                    diffAz = mySky.getSun().getAzimuth() + Math.PI/2;
                    diffHe = myProjection.getRotV() - mySky.getSun().getHeight();
                    x = screenCoordSun.x;
                    y = screenCoordSun.y;
                    break;
                case SkyObject.MOON:
                    diffAz = mySky.getMoon().getAzimuth() + Math.PI/2;
                    diffHe = myProjection.getRotV() - mySky.getMoon().getHeight();
                    x = screenCoordMoon.x;
                    y = screenCoordMoon.y;
                    break;
            }
            // Calculate distance to searched object and move toward it
            if (myProjection.is3D()) {
                diffAz = (2*Math.PI + diffAz) % (2*Math.PI);
                diffAz = myProjection.getRot() - diffAz;
                if (diffAz>Math.PI)
                    diffAz -= 2*Math.PI;
                if (diffAz<-Math.PI)
                    diffAz += 2*Math.PI;

                diffHe = (2*Math.PI + diffHe) % (2*Math.PI);
                if (diffHe>Math.PI)
                    diffHe -= 2*Math.PI;
                if (diffHe<-Math.PI)
                    diffHe += 2*Math.PI;

                if (Math.abs(diffAz) > 0.05 || Math.abs(diffHe) > 0.05) {
                    myProjection.scrollHor((float)diffAz*4);
                    myProjection.scrollVer(-(float)diffHe*4);
                    project();
                } else {
                    myMidlet.getSearch().setFlagCentered(true);
                }
            } else {
                if (Math.abs(myProjection.getWidth()/2-x)> 10 
                        || ((myProjection.getRotV() != (myProjection.getZoom()-1)) && (myProjection.getRotV() != (1-myProjection.getZoom())) && Math.abs(myProjection.getHeight()/2-y)>10)) {
                    // as long as we are far from the center or as long as we have not reached the edge on height
                    myProjection.scrollHor((float)(myProjection.getWidth()/2-x)/64F);
                    myProjection.scrollVer((float)(myProjection.getHeight()/2-y)/64F);
                    project();
                } else {
                    myMidlet.getSearch().setFlagCentered(true);
                }
            }
        }
        
        if (myMidlet.getMyParameter().isDebug())
            paintDebug(g);
        else
            paintMain(g);
    }

    /**
     * Draw the main screen
     * @param g the graphics object on which we draw
     */
    private void paintMain(Graphics g) {
        // ==============================================
        // ====== Display Stars and Constellations ======
        // ==============================================

        if (mySky.isCalculationDone()) {
            // -----------------------------------------------------------------------------
            // ---- Print wait message when projection on screen is ongoing ---
            if (timeCalculate != 0)                                             // Because paint is called before run
                myMidlet.getMyParameter().addTimeCalculate(System.currentTimeMillis()-timeCalculate);
            mySky.setCalculationDone(false);
            mySky.setProgress(0);
            g.setColor(0x000000FF);
            g.drawString(LocalizationSupport.getMessage("PLEASE_WAIT"), myProjection.getWidth() / 2, myProjection.getHeight() / 2, Graphics.HCENTER | Graphics.BASELINE);
            project();
            // Recalculate the lights position
            if (myProjection.is3D())
                ((EyeProj)myProjection).setLights();
        } else {
            timeDisplay = System.currentTimeMillis();
            // ------------------------------------------------
            // --- Scroll the screen if user is dragging it ---
            if (touchScreen != null && touchScreen.isScroll()) {
                myProjection.scrollHor(touchScreen.getRotDir());
                myProjection.scrollVer(touchScreen.getYScroll());
                touchScreen.scroll(touchScreen.isScreenPressed());
                project();
            }
            // ----------------------------
            // -----   Draw horizon   -----
            drawHorizon(g);

            // ----------------
            // --- Set Font ---
            g.setFont(myFont);

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
            g.setColor(0x00ff);                                                 // TODO set right color
            if (mySky.getProgress() != 0) {
                g.drawLine(0, 0, mySky.getProgress() * myProjection.getWidth() / 100, 0);
            }

            // ------------------------------
            // ------ Draw directions -------
            drawDirections(g);

            // ------------------------------------------
            // ------ Draw constellations history -------
            if (myMidlet.getMyParameter().isHistoryOfConstellationDisplayed())
                drawHistoryOfConstellations(g);

            // --------------------------------
            // ------- Draw Help --------------
            if (myHelp.isDisplayed()) {
                myHelp.draw(g);
            }
            myMidlet.getMyParameter().addTimeDisplay(System.currentTimeMillis()-timeDisplay);
        }
    }
    /**
     * Paint the debug screen with all informations on selected objects
     * @param g the graphic object
     * TODO optimisation should be possible
     */
    private void paintDebug(Graphics g) {
        String nameObject, nameConst;
        float mag;
        double RA2000, DE2000;
        double hau, az;
        double jj, hs;
        double T, alpha, delta;
        double angHour;
        boolean flagDisplay;

        // ----------------
        // --- Set Font ---
        g.setFont(myFont);

        // -------------------------------
        // --- Recalculate hei and az ----
        if (counter % MAX_CPS == 0) {
            getUpdatedHeightAndAzimuth();
        }

        // Get global variable
        jj = myMidlet.getMyPosition().getTemps().getJJ();               // Julian day
        hs = myMidlet.getMyPosition().getTemps().getLocalHS(myMidlet.getMyPosition());               // Local Sideral hour

        nameConst = nameObject = "";
        mag = 0.0F;
        RA2000 = 0.0;
        DE2000 = 0.0;
        hau = az = 0.0;
        alpha = delta = angHour = 0.0;
        flagDisplay = false;

        // Get id of object selected
        switch (typeClosestObject) {
            case SkyObject.STAR:
                // ======================
                // === This is a star ===
                // ======================
                flagDisplay = true;
                nameConst = ((StarObject)mySky.getStar(idxClosestObject).getObject()).getConstellationName();
                nameObject = mySky.getStar(idxClosestObject).getObject().getName();
                if (nameObject.length() == 0) {
                    nameObject = ((StarObject)mySky.getStar(idxClosestObject).getObject()).getBayerName();
                }
                mag = mySky.getStar(idxClosestObject).getObject().getMag();
                RA2000 = mySky.getStar(idxClosestObject).getObject().getAscendance();
                DE2000 = mySky.getStar(idxClosestObject).getObject().getDeclinaison();

                T = (myMidlet.getMyPosition().getTemps().getCalendar().get(Calendar.YEAR) - 2000);
                alpha = RA2000 + mySky.getStar(idxClosestObject).getDAlpha() * T / 3600;                                                 // dAlpha is given in s
                delta = DE2000 + mySky.getStar(idxClosestObject).getDDelta() * T / 3600;                                                 // dDelta is given in s too
                angHour = mySky.getStar(idxClosestObject).getH();
                angHour = Math.toDegrees(angHour) / 15;                               // From radian to hms
                if (angHour < 0) {
                    angHour += 24;
                }
                hau = mySky.getStar(idxClosestObject).getHeight();
                az = mySky.getStar(idxClosestObject).getAzimuth() + Math.PI / 2;
                break;
            case SkyObject.MESSIER:
                // ================================
                // === This is a Messier object ===
                // ================================
                flagDisplay = true;
                nameConst = "";
                nameObject = MessierCatalog.getObject(idxClosestObject).getName();
                mag = MessierCatalog.getObject(idxClosestObject).getMag();
                RA2000 = MessierCatalog.getObject(idxClosestObject).getAscendance();
                DE2000 = MessierCatalog.getObject(idxClosestObject).getDeclinaison();

                T = (myMidlet.getMyPosition().getTemps().getCalendar().get(Calendar.YEAR) - 2000);
                alpha = RA2000 + mySky.getMessier(idxClosestObject).getDAlpha() * T / 3600;                                                 // dAlpha is given in s
                delta = DE2000 + mySky.getMessier(idxClosestObject).getDDelta() * T / 3600;                                                 // dDelta is given in s too
                angHour = mySky.getMessier(idxClosestObject).getH();
                angHour = Math.toDegrees(angHour) / 15;                               // From radian to hms
                if (angHour < 0) {
                    angHour += 24;
                }
                hau = mySky.getMessier(idxClosestObject).getHeight();
                az = mySky.getMessier(idxClosestObject).getAzimuth() + Math.PI / 2;
                break;
            case SkyObject.PLANET:
                // ========================
                // === This is a planet ===
                // ========================
                nameConst = nameObject = "";
                mag = 0.0F;
                RA2000 = 0.0;
                DE2000 = 0.0;
                hau = az = 0.0;
                alpha = delta = angHour = 0.0;
                flagDisplay = false;
                nameObject = mySky.getPlanet(idxClosestObject).getObject().getName();
                alpha = mySky.getPlanet(idxClosestObject).getObject().getAscendance();
                delta = mySky.getPlanet(idxClosestObject).getObject().getDeclinaison();
                angHour = mySky.getPlanet(idxClosestObject).getH();
                angHour = Math.toDegrees(angHour) / 15;                         // From radian to hms
                hau = mySky.getPlanet(idxClosestObject).getHeight();
                az = mySky.getPlanet(idxClosestObject).getAzimuth() + Math.PI / 2;
                break;
            case SkyObject.SUN:
                // ========================
                // === This is a planet ===
                // ========================
                nameObject = mySky.getSun().getObject().getName();
                alpha = mySky.getSun().getObject().getAscendance();
                delta = mySky.getSun().getObject().getDeclinaison();
                angHour = mySky.getSun().getH();
                angHour = Math.toDegrees(angHour) / 15;                         // From radian to hms
                hau = mySky.getSun().getHeight();
                az = mySky.getSun().getAzimuth() + Math.PI / 2;
                break;
            case SkyObject.MOON:
                // ========================
                // === This is a planet ===
                // ========================
                nameObject = mySky.getMoon().getObject().getName();
                alpha = mySky.getMoon().getObject().getAscendance();
                delta = mySky.getMoon().getObject().getDeclinaison();
                angHour = mySky.getMoon().getH();
                angHour = Math.toDegrees(angHour) / 15;                         // From radian to hms
                hau = mySky.getMoon().getHeight();
                az = mySky.getMoon().getAzimuth() + Math.PI / 2;
                break;
        }

        // ----------------------------
        // -----   Clear screen   -----
        g.setColor(myMidlet.getMyParameter().getColor()[Color.COL_BACKGROUND]);
        g.fillRect(0, 0, myProjection.getWidth(), myProjection.getHeight());
        g.setColor(myMidlet.getMyParameter().getColor()[Color.COL_BOX_TEXT]);
        int pos = yInfoText;
        g.drawString("Date: " + myMidlet.getMyPosition().getTemps().getCalendar().getTime(), xInfoText, pos, Graphics.LEFT | Graphics.TOP);
        pos += heightFont;
        g.drawString("Jul. day: " + jj, xInfoText, pos, Graphics.LEFT | Graphics.TOP);
        pos += heightFont;
        g.drawString("Sideral hour: " + MathFunctions.convert2hms(hs,false), xInfoText, pos, Graphics.LEFT | Graphics.TOP);
        pos += heightFont;
        if (flagDisplay) {
            g.drawString("Name: " + nameObject + " - Const: " + nameConst, xInfoText, pos, Graphics.LEFT | Graphics.TOP);
            pos += heightFont;
            g.drawString("Mag: " + mag, xInfoText, pos, Graphics.LEFT | Graphics.TOP);
            pos += heightFont;
            g.drawString("RA2000: " + MathFunctions.convert2hms(RA2000,true), xInfoText, pos, Graphics.LEFT | Graphics.TOP);
            pos += heightFont;
            g.drawString("DE2000: " + MathFunctions.convert2deg(DE2000,true), xInfoText, pos, Graphics.LEFT | Graphics.TOP);
            pos += heightFont;
        } else {
            g.drawString("Name: " + nameObject, xInfoText, pos, Graphics.LEFT | Graphics.TOP);
            pos += heightFont;
        }
        g.drawString("RA(now): " + MathFunctions.convert2hms(alpha,true), xInfoText, pos, Graphics.LEFT | Graphics.TOP);
        pos += heightFont;
        g.drawString("DE(now): " + MathFunctions.convert2deg(delta,true), xInfoText, pos, Graphics.LEFT | Graphics.TOP);
        pos += heightFont;
        g.drawString("Hour ang: " + MathFunctions.convert2hms(angHour,true), xInfoText, pos, Graphics.LEFT | Graphics.TOP);
        pos += heightFont;
        g.drawString("Dec: " + MathFunctions.convert2deg(delta,true), xInfoText, pos, Graphics.LEFT | Graphics.TOP);
        pos += heightFont;
        g.drawString("Az: " + MathFunctions.convert2deg(Math.toDegrees(az),true), xInfoText, pos, Graphics.LEFT | Graphics.TOP);
        pos += heightFont;
        g.drawString("Hei: " + MathFunctions.convert2deg(Math.toDegrees(hau),true), xInfoText, pos, Graphics.LEFT | Graphics.TOP);

        // --------------------------------
        // ------- Draw Help --------------
        if (myHelp.isDisplayed()) {
            myHelp.draw(g);
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
                int sco = co.getSizeOfConstellation();
                // For all constellations
                int col = 0;                                                    // The color of the constellation drawing
                for (j = 0; j < sco; j += 2) {
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
                            if ( (screenCoordStars[kStar1].x>=0 && screenCoordStars[kStar1].x<myProjection.getWidth() && screenCoordStars[kStar1].y>=0 && screenCoordStars[kStar1].y<myProjection.getHeight())
                                 || (screenCoordStars[kStar2].x>=0 && screenCoordStars[kStar2].x<myProjection.getWidth() && screenCoordStars[kStar2].y>=0 && screenCoordStars[kStar2].y<myProjection.getHeight()) )
                            g.drawLine(screenCoordStars[kStar1].x, screenCoordStars[kStar1].y, screenCoordStars[kStar2].x, screenCoordStars[kStar2].y);
                        }
                    }
                }
                if (rMyParam.isConstNamesDisplayed() || rMyParam.isConstNamesLatinDisplayed()) {
                    g.setColor(color[Color.COL_CONST_NAME_MAX] / 2);
                    // Display the name of the constellation
                    kStar1 = co.getRefStar4ConstellationName();
                    if (!screenCoordStars[kStar1].isVisible()) {
                        for (j = 0; j < sco; j += 2) {
                            kStar1 = co.getIdx(j);               // Get index of stars in constellation
                            if (screenCoordStars[kStar1].isVisible()) {
                                break;
                            }
                        }
                    }
                    if (screenCoordStars[kStar1].isVisible()) {
                        if (rMyParam.isConstNamesDisplayed() && rMyParam.isConstNamesLatinDisplayed()) {
                            g.drawString(ConstellationCatalog.getConstellation(i).getName(), screenCoordStars[kStar1].x, screenCoordStars[kStar1].y - heightFont / 2, Graphics.TOP | Graphics.HCENTER);
                            g.drawString(ConstellationCatalog.getConstellation(i).getLatinName(), screenCoordStars[kStar1].x, screenCoordStars[kStar1].y + heightFont / 2, Graphics.TOP | Graphics.HCENTER);
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
     *
     * @param g
     */
    private void drawHistoryOfConstellations(Graphics g) {
        String s1;
        int beg, end, oldEnd, endret;
        int lg;
        int yct = yConstText;

        g.setColor(myMidlet.getMyParameter().getColor()[Color.COL_HISTORY]);
        beg = end = endret = lg = 0;
        do {
            do {
                oldEnd = end;
                end = constText.indexOf(" ", oldEnd + 1);             // Search for the next space
                if (end != -1) {
                    s1 = constText.substring(beg, end);              // s1 = constText from beg to end
                    lg = myFont.stringWidth(s1);
                }
            } while ((lg < myProjection.getWidth() - 4) && (end != -1));
            s1 = constText.substring(beg, oldEnd);
            endret = s1.indexOf("\n");
            if (endret != -1) {
                s1 = s1.substring(0, endret);
                beg = beg + endret + 1;
                end = beg;
            } else {
                beg = oldEnd + 1;
            }
            if (yct < myProjection.getHeight() && yct >= 0) {
                g.drawString(s1, 2, yct, Graphics.TOP | Graphics.LEFT);
            }
            yct += heightFont;

        } while ((yct < (myProjection.getHeight() - 2 * heightFont)) && end != -1);

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
                        if (screenCoordStars[k].x>=0 && screenCoordStars[k].x<myProjection.getWidth() && screenCoordStars[k].y>=0 && screenCoordStars[k].y<getHeight()) {
                            if (size < 1) {
                                g.drawLine(screenCoordStars[k].x, screenCoordStars[k].y, screenCoordStars[k].x, screenCoordStars[k].y);
                            } else {
                                g.fillArc(screenCoordStars[k].x - size, screenCoordStars[k].y - size, 2 * size, 2 * size, 0, 360);
                            }
                        }
                    } else {
                        // Or as a dot
                        if (screenCoordStars[k].x>=0 && screenCoordStars[k].x<myProjection.getWidth() && screenCoordStars[k].y>=0 && screenCoordStars[k].y<getHeight())
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
        boolean messDisp,messNameDisp;

        messDisp = rMyParam.isMessierDisplayed();
        messNameDisp = rMyParam.isMessierNameDisplayed();

        if (messDisp || messNameDisp) {
            for (int k = 0; k < MessierCatalog.getNumberOfObjects(); k++) {
                if (screenCoordMessier[k].isVisible()) {
                    // Messier object is above horizon
                    int mag = (int) mySky.getMessier(k).getObject().getMag();
                    mag = (mag-31) /17;                                         // TODO should not be hard coded
                    if (mag > 5) {
                        mag = 5;
                    }
                    int col = color[Color.COL_MESSIER_MAG0 + mag];
                    g.setColor(col);

                    if (messDisp) {
                        if (screenCoordMessier[k].x>=0 && screenCoordMessier[k].x<myProjection.getWidth() && screenCoordMessier[k].y>=0 && screenCoordMessier[k].y<getHeight())
                            g.drawLine(screenCoordMessier[k].x, screenCoordMessier[k].y, screenCoordMessier[k].x, screenCoordMessier[k].y);
                    }
                    if (messNameDisp) {
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
                g.drawString(LocalizationSupport.getMessage("NAME_MOON"), (int) screenCoordMoon.x, (int) screenCoordMoon.y - heightFont - z, Graphics.TOP | Graphics.HCENTER);
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
                g.drawString(LocalizationSupport.getMessage("NAME_SUN"), (int) screenCoordSun.x, (int) screenCoordSun.y - heightFont - z, Graphics.TOP | Graphics.HCENTER);
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
                    g.drawString(mySky.getPlanet(k).getObject().getName(), (int) screenCoordPlanets[k].x, (int) screenCoordPlanets[k].y - heightFont, Graphics.TOP | Graphics.HCENTER);
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
        int vSize = NB_OF_LINES * heightFont + 8;

        // ----------------------------------
        // --- Display constellation name ---
        if (typeClosestObject == SkyObject.STAR) {
            if (colorConstName < color[Color.COL_CONST_NAME_MAX]) {
                col = (colorConstName > color[Color.COL_CONST_NAME_MAX] / 2) ? color[Color.COL_CONST_NAME_MAX] - colorConstName : colorConstName;
                g2.setColor(color[Color.COL_CONST_NAME_MIN] + col);
                colorConstName += color[Color.COL_CONST_NAME_INC];

                if (yCursor < myProjection.getHeight() / 4 || (yCursor >= myProjection.getHeight() / 2 && yCursor < 3 * myProjection.getHeight() / 4)) { // Displayed below the cursor
                    g2.drawString(constName2, xCursor, yCursor + 2 * heightFont, Graphics.HCENTER | Graphics.TOP);
                } else {// Displayed above the cursor
                    g2.drawString(constName2, xCursor, yCursor - 3 * heightFont, Graphics.HCENTER | Graphics.TOP);
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
        if (yCursor < myProjection.getHeight() / 2) {
            yInfoDest = myProjection.getHeight() - vSize - 4;
        } else {
            yInfoDest = 6;
        }

        if (xCursor < myProjection.getWidth() / 2) {
            xInfoDest = myProjection.getWidth() - maxl - 4;
        } else {
            xInfoDest = 6;
        }

        xInfo = xInfo + (xInfoDest - xInfo) / 4;
        yInfo = yInfo + (yInfoDest - yInfo) / 4;
        if ((xInfo + maxl + 4) > myProjection.getWidth()) {
            xInfo = myProjection.getWidth() - maxl - 4;        // Get background image (behing info box) to do transparence
        }

        if ((yInfo + vSize) > myProjection.getHeight()) {
            vSize = myProjection.getHeight() - yInfo;
        }

// Display text in info box
        g2.setColor(color[Color.COL_BOX_TEXT]);
        g2.drawString(objectName, xInfo + 4, yInfo + 4, Graphics.LEFT | Graphics.TOP);
        g2.drawString(magName, xInfo + 4, yInfo + 1 * heightFont + 4, Graphics.LEFT | Graphics.TOP);
        g2.drawString(distName, xInfo + 4, yInfo + 2 * heightFont + 4, Graphics.LEFT | Graphics.TOP);
        g2.drawString(azName, xInfo + 4, yInfo + 3 * heightFont + 4, Graphics.LEFT | Graphics.TOP);
        g2.drawString(heiName, xInfo + 4, yInfo + 4 * heightFont + 4, Graphics.LEFT | Graphics.TOP);
        g2.setColor(color[Color.COL_BOX]);
        g2.drawRoundRect(xInfo - 1, yInfo - 1, maxl + 1, vSize + 1, 8, 8);
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
     * Draw the direction of where we are looking at
     * @param g
     */
    private void drawDirections(Graphics g) {
        g.setColor(0xff0000);
        if (myProjection.is3D()) {
            g.drawArc(2, myProjection.getHeight() - 22, 20, 20, 0, 360);
            g.drawArc(25, myProjection.getHeight() - 22, 20, 20, 0, 360);
            double x1, y1;
            x1 = 12 - 9 * Math.cos(myProjection.getRot() + Math.PI / 2);
            y1 = myProjection.getHeight() - 12 - 9 * Math.sin(myProjection.getRot() + Math.PI / 2);
            g.drawLine(12, myProjection.getHeight() - 12, (int) x1, (int) y1);
            x1 = 35 + 9 * Math.cos(myProjection.getRotV());
            y1 = myProjection.getHeight() - 12 - 9 * Math.sin(myProjection.getRotV());
            g.drawLine(35, myProjection.getHeight() - 12, (int) x1, (int) y1);
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
        double min = myProjection.getWidth() + myProjection.getHeight();

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
                type =SkyObject.SUN;
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
        if (keyCode == TouchScreen.MIN_MAX) {
            touchScreen.toggleFullScreen();
            setFullScreenMode(touchScreen.isFullScreen());
        }

        if (keyCode == TouchScreen.CURSOR_ON) {
            xCursor = x;
            yCursor = y;
            int tmp1, tmp2;
            tmp1 = idxClosestObject;
            tmp2 = typeClosestObject;
            findClosestObject();
            if (myMidlet.getMyParameter().isCursorDisplayed()) {
                if ((tmp1 == idxClosestObject) && (tmp2 == typeClosestObject) ) {
                    myMidlet.getMyParameter().setCursor(false);
                    typeClosestObject = SkyObject.NONE;
                    idxClosestConst = idxClosestObject = -1;
                }
            } else {
                myMidlet.getMyParameter().setCursor(true);
            }
        }
    }

    /**
     *
     * @param keyCode
     * TODO add synchronized key word ?
     */
    protected void keyPressed(int keyCode) {
        // # = Display help
        if (keyCode == KEY_POUND && myHelp != null) {
            if (myHelp.isDisplayed()) {
                myHelp.setDisplayed(false);
            } else {
                myHelp.setDisplayed(true);
            }
        }
        if (myMidlet.getMyParameter().isDebug()) {
            // ================================
            // === Debug data are displayed ===
            if (getGameAction(keyCode) == UP) {
                if (yInfoText < 0) {
                    yInfoText += heightFont;
                }
            }
            if (getGameAction(keyCode) == DOWN) {
                yInfoText -= heightFont;
            }
            if (getGameAction(keyCode) == RIGHT) {
                xInfoText -=10;
            }
            if (getGameAction(keyCode) == LEFT) {
                if (xInfoText<0){
                    xInfoText +=10;
                }
            }  
            if (keyCode == KEY_STAR) {
                myMidlet.getMyParameter().setDebug(false);
                myHelp.setText(Help.FIVE, LocalizationSupport.getMessage("CHLP"));
                myHelp.setText(Help.STAR,LocalizationSupport.getMessage("DDBG"));
            }
        } else if (myMidlet.getMyParameter().isHistoryOfConstellationDisplayed()) {
            // ==========================================
            // === History of constellation displayed ===
            if (getGameAction(keyCode) == UP) {
                if (yConstText < 0) {
                    yConstText += heightFont;
                }
            }
            if (getGameAction(keyCode) == DOWN) {
                yConstText -= heightFont;
            }
            if (getGameAction(keyCode) == FIRE) {
                myMidlet.getMyParameter().setDisplayConstHistory(false);
                myHelp.setText(Help.FOUR,LocalizationSupport.getMessage("LFT"));
                myHelp.setText(Help.FIVE,LocalizationSupport.getMessage("CHLP"));
                myHelp.setText(Help.SIX,LocalizationSupport.getMessage("RIT"));
                myHelp.setText(Help.ONE,LocalizationSupport.getMessage("ZO"));
                myHelp.setText(Help.THREE, LocalizationSupport.getMessage("ZI"));
                myHelp.setText(Help.ZERO,LocalizationSupport.getMessage("DIC2"));

                myMidlet.getMyParameter().setCursor(true);
                displayCursor = COUNTER_CURSOR;
                myHelp.setText(Help.FIVE, LocalizationSupport.getMessage("CHLP"));
                findClosestObject();
            }
        } else if (!myMidlet.getMyParameter().isCursorDisplayed()) {
            // =================================================================
            // ===================== Default mode ==============================
            // * = Next display
            if (keyCode == KEY_STAR) {
//#ifdef JSR184
                if (myMidlet.getMyParameter().isSupport3D()) {
                    runningCmd = false;
                    if (myProjection.is3D() == true) {
                        // Switch to zenith view
                        myHelp.setText(Help.STAR, LocalizationSupport.getMessage("PARAM_HOR"));
                        try {
                            Thread.sleep(MS_PER_FRAME);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }

                        while (running == true) {
                        }                                                       // Wait for the end of the thread before starting it again
                        myProjection = new ZenithProj(myMidlet, getWidth(), getHeight());
                        myProjection.init();
                        screenCoordMessier = myProjection.getScreenCoordMessier();
                        screenCoordMoon = myProjection.getScreenCoordMoon();
                        screenCoordPlanets = myProjection.getScreenCoordPlanets();
                        screenCoordStars = myProjection.getScreenCoordStars();
                        screenCoordSun = myProjection.getScreenCoordSun();
                        myProjection.project();
                        runningCmd = true;
                        new Thread(this).start();
                    } else {
                        // Switch to horizontal 3D view
                        myHelp.setText(Help.STAR, LocalizationSupport.getMessage("ZEV"));
                        try {
                            Thread.sleep(MS_PER_FRAME);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }

                        while (running == true) {
                        }                                                       // Wait for the end of the thread before starting it again
                        myProjection = new EyeProj(myMidlet, getWidth(), getHeight());
                        myProjection.init();
                        screenCoordMessier = myProjection.getScreenCoordMessier();
                        screenCoordMoon = myProjection.getScreenCoordMoon();
                        screenCoordPlanets = myProjection.getScreenCoordPlanets();
                        screenCoordStars = myProjection.getScreenCoordStars();
                        screenCoordSun = myProjection.getScreenCoordSun();
                        myProjection.project();
                        runningCmd = true;
                        new Thread(this).start();
                    }
                }
//#endif
            }
            // 0 = Show cursor
            if (keyCode == KEY_NUM0) {
                myMidlet.getMyParameter().setCursor(true);
                displayCursor = COUNTER_CURSOR;
                myHelp.setText(Help.FIVE, LocalizationSupport.getMessage("CHLP"));
                myHelp.setText(Help.ZERO, LocalizationSupport.getMessage("DIC2"));
                myHelp.setText(Help.STAR, LocalizationSupport.getMessage("DDBG"));
                findClosestObject();
            }
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
            // <- = Rotation
            if (getGameAction(keyCode) == LEFT) {
                myProjection.left();
                project();
            }
            // -> = Rotation
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
        } else if (myMidlet.getMyParameter().isCursorDisplayed()) {
            // =================================================================
            // ========================= Cursor mode ===========================
            // 0 = Hide cursor
            if (keyCode == KEY_NUM0) {
                myMidlet.getMyParameter().setCursor(false);
                typeClosestObject = SkyObject.NONE;
                idxClosestConst = idxClosestObject = -1;
                myHelp.setText(Help.FIVE, "");
                myHelp.setText(Help.ZERO, LocalizationSupport.getMessage("DIC"));
            }
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
            // UP
            if (getGameAction(keyCode) == UP) {
                decYCursor();
                displayCursor = COUNTER_CURSOR;
                findClosestObject();
            }
            // DOWN
            if (getGameAction(keyCode) == DOWN) {
                incYCursor();
                displayCursor = COUNTER_CURSOR;
                findClosestObject();
            }
            // LEFT
            if (getGameAction(keyCode) == LEFT) {
                decXCursor();
                displayCursor = COUNTER_CURSOR;
                findClosestObject();
            }
            // RIGHT
            if (getGameAction(keyCode) == RIGHT) {
                incXCursor();
                displayCursor = COUNTER_CURSOR;
                findClosestObject();
            }
            // FIRE
            if (getGameAction(keyCode) == FIRE) {
                // Display history of constellation
                if (idxClosestConst != -1) {
                    myHelp.setText(Help.FOUR, "");
                    myHelp.setText(Help.FIVE, LocalizationSupport.getMessage("CHLP2"));
                    myHelp.setText(Help.SIX, "");
                    myHelp.setText(Help.ONE,"");
                    myHelp.setText(Help.THREE, "");
                    myHelp.setText(Help.ZERO,"");
                    myMidlet.getMyParameter().setDisplayConstHistory(true);
                    myMidlet.getMyParameter().setCursor(false);
                    yConstText = 0;
                    constText = ConstellationCatalog.getHistory(idxClosestConst);
                }
            }
            if (keyCode == KEY_STAR) {
                myMidlet.getMyParameter().setDebug(true);
                myHelp.setText(Help.FIVE, "");
                myHelp.setText(Help.ONE,"");
                myHelp.setText(Help.THREE, "");
                myHelp.setText(Help.ZERO,"");
                myHelp.setText(Help.STAR,LocalizationSupport.getMessage("HDBG"));
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
        keyPressed(keyCode);
    }

    /**
     * Inc x coordinate of the cursor
     */
    public void incXCursor() {
        if (xCursor < myProjection.getWidth() - myProjection.getZoom() * 2) {
            xCursor += myProjection.getZoom() * 2;
        } else {
            myProjection.right();
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
            myProjection.left();
            project();
        }
    }

    /**
     * Inc y coordinate of the cursor
     */
    public void incYCursor() {
        if (yCursor < myProjection.getHeight() - 2 * myProjection.getZoom()) {
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
        runningCmd = false;
    }

    /**
     * The main loop:
     * Every COUNTER calls, recalculate the position of all objects
     * Every call, repaint.
     */
    public void run() {
        long cycleStartTime;

        running = true;
        while (runningCmd) {
            cycleStartTime = System.currentTimeMillis();
            // Check if we need to calculate position
            if (counter == 0) {
                // Yes, do it in a separate thread
                timeCalculate = cycleStartTime;
                new Thread(mySky).start();
                //System.out.println("Start Sky Thread");
                counter = COUNTER;
            } else {
                // No, decrease counter
                counter--;
            }
            // Display scene.
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
        running = false;
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
        if (runningCmd == false) {
            runningCmd = true;
            new Thread(this).start();
        }
    }

    /**
     * Called when the drawable area of the Canvas has been changed
     * @param w the new width in pixels of the drawable area of the Canvas
     * @param h w - the new width in pixels of the drawable area of the Canvas
     */
    protected void sizeChanged(int w, int h) {
        super.sizeChanged(w, h);

        // Set screen new dimension
        if (myProjection != null) {
            myProjection.setView(w,h);
            project();
        }
        // Rearrange the toolbar
        if (touchScreen != null) {
            touchScreen.setSize(w, h);
        }
        // Set dimension of help
        if (myHelp != null)
            myHelp.setView(w, h);
    }
}
