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
import fr.dox.sideralis.object.PlanetObject;
import fr.dox.sideralis.object.ScreenCoord;
import fr.dox.sideralis.object.SkyObject;
import fr.dox.sideralis.object.StarObject;
import fr.dox.sideralis.projection.plane.Zenith;
import fr.dox.sideralis.projection.sphere.MoonProj;
import fr.dox.sideralis.projection.sphere.Projection;
import fr.dox.sideralis.view.color.Color;
import java.io.IOException;
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
    private ScreenCoord screenCoordSun;
    private ScreenCoord screenCoordMoon;
    private ScreenCoord[] screenCoordPlanets;

    private final Font myFont;
    
    // ------------------
    // --- For cursor ---
    private int idxClosestConst;
    private int idxClosestStar;
    private int typeClosestObject;
    private int colorClosestConst;
    /** The counter associated to this action: if the cursor does not move for sometime, it is not displayed anymore */
    private short displayCursor;
    /** The image for the cursor */
    private Image cursorImage;
    /** Position of cursor */
    private int xCursor,  yCursor;
    /** All informations about object */
    private String starName,  magName,  distName,  azName,  heiName;
    /** Number of lines which will be used to display informations on star */
    private final short NB_OF_LINES = 5;
    /** An variable used to modify the display of the const name */
    private int shiftConstName;
    private int colorConstName;
    /** Name of the constellation which is shown by the cursor */
    private String constName1,  constName2;
    /** The offset of the text containing the info text */
    private int xInfoText,yInfoText;
    private int xInfoDest,  xInfo;
    private int yInfoDest,  yInfo;

    /** Default size in pixel for Moon and Sun */
    private final short SIZE_MOON_SUN = 4;
    private int counter;
    private static final int COUNTER = 1000;

    private TouchScreen touchScreen;

    /** The position of the circle around the selected or highlighted objects */
    private short angle;

    /**
     * 
     */
    public SideralisCanvas(Sideralis myMidlet) {
        this.myMidlet = myMidlet;
        running = false;
        mySky = myMidlet.getMySky();
        myFont = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL);
        myProjection = new Zenith(getHeight(),getWidth());
        if (hasPointerEvents())
            touchScreen = new TouchScreen();
        else
            touchScreen = null;
    }

    /**
     *
     */
    public void init() {
        idxClosestStar = idxClosestConst = -1;
        colorClosestConst = 0;
        screenCoordMessier = new ScreenCoord[MessierCatalog.getNumberOfObjects()];
        for (int k = 0; k < MessierCatalog.getNumberOfObjects(); k++)
            screenCoordMessier[k] = new ScreenCoord();

        screenCoordStar = new ScreenCoord[mySky.getStarsProj().length];
        for (int k = 0; k < screenCoordStar.length; k++)
            screenCoordStar[k] = new ScreenCoord();

        screenCoordSun = new ScreenCoord();
        screenCoordMoon = new ScreenCoord();
        screenCoordPlanets = new ScreenCoord[Sky.NB_OF_SYSTEM_SOLAR_OBJECTS];
        for (int k=0;k<Sky.NB_OF_SYSTEM_SOLAR_OBJECTS;k++)
            screenCoordPlanets[k] = new ScreenCoord();

        try {
            cursorImage = Image.createImage("/Images/Cursor.png");
        } catch (IOException e) {
            e.printStackTrace();
        }

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
    protected void paint(Graphics g) {

        // ----------------------------
        // -----   Clear screen   -----
        g.setColor((myMidlet.getMyParameter().getColor())[Color.COL_BACKGROUND]);
        g.fillRect(0, 0, getWidth, getHeight);
        
        if (mySky.isCalculationDone()) {
            mySky.setCalculationDone(false);
            mySky.setProgress(0);
            g.drawString("Wait",10,10,Graphics.TOP|Graphics.LEFT);
            System.out.println("Wait, projecting");
            project();
        } else {
            if (myProjection.isScroll()) {
                myProjection.scroll(touchScreen.isScreenPressed());
                project();
            }
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
            if (touchScreen != null)
                touchScreen.paint(g);

            // -----------------------------------
            // ----- Display cursor and info -----
            if (myMidlet.getMyParameter().isCursorDisplayed()) {
                drawCursor(g);
            }

            // -------------------------------
            // ------ Draw progress bar ------
            g.setColor(0x00ff0000);
            g.drawLine(0, 0, mySky.getProgress()*getHeight/100, 0);
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
        g.setColor((myMidlet.getMyParameter().getColor())[Color.COL_ZENITH_BACKGROUND]);

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
        g.setColor((myMidlet.getMyParameter().getColor())[Color.COL_CROSS]);
        g.setStrokeStyle(Graphics.DOTTED);
        g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
        g.drawLine((int) x3, (int) y3, (int) x4, (int) y4);
        g.setStrokeStyle(Graphics.SOLID);
        g.setColor((myMidlet.getMyParameter().getColor())[Color.COL_N_S_E_O]);
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
                    if (screenCoordStar[kStar1].isVisible() && screenCoordStar[kStar2].isVisible()) {
                        // A line between 2 stars is displayed only if the 2 stars are visible.
                        if (co.getIdxName() == idxClosestConst && rMyParam.isCursorDisplayed() && idxClosestStar >= 0 && idxClosestStar < screenCoordStar.length) {
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
                        if (rMyParam.isConstDisplayed())
                            g.drawLine(screenCoordStar[kStar1].x, screenCoordStar[kStar1].y, screenCoordStar[kStar2].x, screenCoordStar[kStar2].y);
                    }
                }
                if (rMyParam.isConstNamesDisplayed() || rMyParam.isConstNamesLatinDisplayed()) {
                    g.setColor(color[Color.COL_CONST_NAME_MAX] / 2);
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
                            g.drawString(ConstellationCatalog.getConstellation(i).getName(), screenCoordStar[kStar1].x, screenCoordStar[kStar1].y-myFont.getHeight()/2, Graphics.TOP | Graphics.HCENTER);
                            g.drawString(ConstellationCatalog.getConstellation(i).getLatinName(), screenCoordStar[kStar1].x, screenCoordStar[kStar1].y+myFont.getHeight()/2, Graphics.TOP | Graphics.HCENTER);
                        }
                        else if (rMyParam.isConstNamesDisplayed())
                            g.drawString(ConstellationCatalog.getConstellation(i).getName(), screenCoordStar[kStar1].x, screenCoordStar[kStar1].y, Graphics.TOP | Graphics.HCENTER);
                        else
                            g.drawString(ConstellationCatalog.getConstellation(i).getLatinName(), screenCoordStar[kStar1].x, screenCoordStar[kStar1].y, Graphics.TOP | Graphics.HCENTER);
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
            if (screenCoordStar[k].isVisible()) {
                // Star is above horizon
                float magf = mySky.getStar(k).getObject().getMag();
                if (magf < maxMag) {
                    int mag = (int) (magf);
                    if (mag > 5) {
                        mag = 5;
                    }
                    if (mag < 0) {
                        mag = 0;
                    // Select color of star
                    }
                    if (isStarColored) {
                        int col = color[Color.COL_STAR_MAX] - mag * color[Color.COL_STAR_INC];
                        g.setColor(col);
                    } else {
                        g.setColor(color[Color.COL_STAR_MAX]);
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
        int[] color = rMyParam.getColor();

        if (rMyParam.isMessierDisplayed() || rMyParam.isMessierNameDisplayed()) {
            for (int k = 0; k < MessierCatalog.getNumberOfObjects(); k++) {
                if (screenCoordMessier[k].isVisible()) {
                    // Messier object is above horizon
                    g.setColor(color[Color.COL_MESSIER]);
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
        for (int k=0;k<screenCoordPlanets.length;k++) {
            if (screenCoordPlanets[k].isVisible()) {
                g.setColor(color[(Color.COL_PLANET)+k]);
                if (rMyParam.isPlanetDisplayed()) {
                    g.fillArc((int) screenCoordPlanets[k].x - 2, (int) screenCoordPlanets[k].y - 2, 4, 4, 0, 360);
                }
                if (rMyParam.isPlanetNameDisplayed()) {
                    g.drawString(LocalizationSupport.getMessage("NAME_MERCURY"), (int) screenCoordPlanets[k].x, (int) screenCoordPlanets[k].y - myFont.getHeight(), Graphics.TOP | Graphics.HCENTER);
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
        if (counter % MAX_CPS == 0)
            getUpdatedHeightAndAzimuth();

        // -------------------------------
        // --- Highlight selected star ---
        g2.setColor(color[Color.COL_CURSOR]);
        angle = (short) ((angle + 15) % 360);

        azName = new String(LocalizationSupport.getMessage("CURSOR_AZIMUTH_ABBR"));
        heiName = new String(LocalizationSupport.getMessage("CURSOR_HEIGHT_ABBR"));
        switch (typeClosestObject) {
            case SkyObject.STAR:
                g2.drawArc(screenCoordStar[idxClosestStar].x - 4, screenCoordStar[idxClosestStar].y - 4, 8, 8, angle, 90);
                g2.drawArc(screenCoordStar[idxClosestStar].x - 4, screenCoordStar[idxClosestStar].y - 4, 8, 8, angle + 180, 90);
                azName += mySky.getStar(idxClosestStar).getRealAzimuthAsString();
                heiName += mySky.getStar(idxClosestStar).getRealHeightAsString();
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
                g2.drawArc(screenCoordMessier[idxClosestStar].x - 4, screenCoordMessier[idxClosestStar].y - 4, 8, 8, angle, 90);
                g2.drawArc(screenCoordMessier[idxClosestStar].x - 4, screenCoordMessier[idxClosestStar].y - 4, 8, 8, angle + 180, 90);
                azName += mySky.getMessier(idxClosestStar).getRealAzimuthAsString();
                heiName += mySky.getMessier(idxClosestStar).getRealHeightAsString();
                break;
            case SkyObject.PLANET:
                g2.drawArc(screenCoordPlanets[idxClosestStar].x - 4, screenCoordPlanets[idxClosestStar].y - 4, 8, 8, angle, 90);
                g2.drawArc(screenCoordPlanets[idxClosestStar].x - 4, screenCoordPlanets[idxClosestStar].y - 4, 8, 8, angle + 180, 90);
                azName += mySky.getPlanet(idxClosestStar).getRealAzimuthAsString();
                heiName += mySky.getPlanet(idxClosestStar).getRealHeightAsString();
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

        if (myFont.stringWidth(starName) > maxl) {
            maxl = myFont.stringWidth(starName);
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
        g2.drawString(starName, xInfo + 4, yInfo + 4, Graphics.LEFT | Graphics.TOP);
        g2.drawString(magName, xInfo + 4, yInfo + 1 * myFont.getHeight() + 4, Graphics.LEFT | Graphics.TOP);
        g2.drawString(distName, xInfo + 4, yInfo + 2 * myFont.getHeight() + 4, Graphics.LEFT | Graphics.TOP);
        g2.drawString(azName, xInfo + 4, yInfo + 3 * myFont.getHeight() + 4, Graphics.LEFT | Graphics.TOP);
        g2.drawString(heiName, xInfo + 4, yInfo + 4 * myFont.getHeight() + 4, Graphics.LEFT | Graphics.TOP);
        g2.setColor(color[Color.COL_BOX]);
        g2.drawRoundRect(xInfo - 1, yInfo - 1, maxl + 1, vSize + 1, 8, 8);
    }
    /**
     * Update height and Azimuth for object pointed by cursor
     */
    private void getUpdatedHeightAndAzimuth() {
        mySky.calculateTimeVariables();

        switch (typeClosestObject) {
            case SkyObject.STAR:
                mySky.getStar(idxClosestStar).calculate();
                break;
            case SkyObject.MESSIER:
                mySky.getMessier(idxClosestStar).calculate();
                break;
            case SkyObject.SUN:
                mySky.getSun().calculate();
                break;
            case SkyObject.MOON:
                mySky.getMoon().calculate();
                break;
            case SkyObject.PLANET:
                mySky.getPlanet(idxClosestStar).calculate();
                break;

        }
    }
    /**
     * Find the closest star to the cursor
     */
    public void findClosestStar() {
        int i,  j;
        float maxMag;

        double d;
        double min = getWidth + getHeight;
        j = -1;
        // Look for closest star.
        maxMag = myMidlet.getMyParameter().getMaxMag();
        typeClosestObject = SkyObject.STAR;
        for (i = 0; i < screenCoordStar.length; i++) {
            if (screenCoordStar[i].isVisible() && mySky.getStar(i).getObject().getMag()<maxMag) {
                d = Math.abs(xCursor - screenCoordStar[i].x) + Math.abs(yCursor - screenCoordStar[i].y);
                if (d < min) {
                    min = d;
                    j = i;
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
                        j = i;
                        typeClosestObject = SkyObject.MESSIER;
                    }
                }
            }
        }
        if (myMidlet.getMyParameter().isPlanetDisplayed()) {
            // Sun
            d = Math.abs(xCursor - screenCoordSun.x) + Math.abs(yCursor - screenCoordSun.y);
            if (d < min && screenCoordSun.isVisible()) {
                min = d;
                typeClosestObject = SkyObject.SUN;
            }
            // Moon
            d = Math.abs(xCursor - screenCoordMoon.x) + Math.abs(yCursor - screenCoordMoon.y);
            if (d < min && screenCoordMoon.isVisible()) {
                min = d;
                typeClosestObject = SkyObject.MOON;
            }
            // Planets
            for (i=0;i<screenCoordPlanets.length;i++) {
                d = Math.abs(xCursor - screenCoordPlanets[i].x) + Math.abs(yCursor - screenCoordPlanets[i].y);
                if (d < min && screenCoordPlanets[i].isVisible()) {
                    min = d;
                    j = i;
                    typeClosestObject = SkyObject.PLANET;
                }
            }
        }
        // Display star information
        if (j == -1) {
            myMidlet.getMyParameter().setCursor(false);                                          // Remove cursor is not close object found. Not too clean
        } else if (j != idxClosestStar) {
            // The closest star is a new star
            idxClosestStar = (short) j;
            switch (typeClosestObject) {
                case SkyObject.SUN:
                    starName = LocalizationSupport.getMessage("NAME_SUN");
                    magName = new String(LocalizationSupport.getMessage("CURSOR_MAGNITUDE_ABBR")+"-26.7");
                    distName = new String(LocalizationSupport.getMessage("CURSOR_DISTANCE_ABBR") + (int) MathFunctions.toMKm(Projection.getRSun()) + " mKm");
                    break;
                case SkyObject.MOON:
                    starName = LocalizationSupport.getMessage("NAME_MOON");
                    magName = new String(LocalizationSupport.getMessage("CURSOR_MAGNITUDE_ABBR")+"-12");
                    distName = new String(LocalizationSupport.getMessage("CURSOR_DISTANCE_ABBR") + (int) mySky.getMoon().getDistance() + " km");
                    break;
                case SkyObject.PLANET:
                    starName = mySky.getPlanet(j).getObject().getName();
                    magName = new String(LocalizationSupport.getMessage("CURSOR_MAGNITUDE_ABBR")+mySky.getPlanet(j).getObject().getMag());
                    distName = new String(LocalizationSupport.getMessage("CURSOR_DISTANCE_ABBR") + (int) mySky.getPlanet(j).getDistance() + " km");
                    break;
                case SkyObject.MESSIER:
                    starName = new String(MessierCatalog.getObject(j).getName());
                    magName = new String(LocalizationSupport.getMessage("CURSOR_MAGNITUDE_ABBR")+MessierCatalog.getObject(j).getMag());
                    distName = new String(LocalizationSupport.getMessage("CURSOR_DISTANCE_ABBR")+MessierCatalog.getObject(j).getDist() + " k" + LocalizationSupport.getMessage("CURSOR_LIGHTYEAR_ABBR"));
                    break;
                case SkyObject.STAR:
                    if (mySky.getStar(j).getObject().getName().length() == 0) {
                        starName = new String("- " + ((StarObject)(mySky.getStar(j).getObject())).getBayerName() + " -");
                    } else {
                        starName = new String(mySky.getStar(j).getObject().getName());
                    }

                    magName = new String(LocalizationSupport.getMessage("CURSOR_MAGNITUDE_ABBR") + mySky.getStar(j).getObject().getMag());
                    distName = new String(LocalizationSupport.getMessage("CURSOR_DISTANCE_ABBR") + ((StarObject)(mySky.getStar(j).getObject())).getDistance() + " " + LocalizationSupport.getMessage("CURSOR_LIGHTYEAR_ABBR"));
                    // Is this star belongs to a new constellation ?

                    if (((StarObject)(mySky.getStar(j).getObject())).getConstellation() != idxClosestConst) {
                        idxClosestConst = ((StarObject)(mySky.getStar(j).getObject())).getConstellation();
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
        keyCode = touchScreen.drag(x,y);

        if (keyCode == TouchScreen.MOVE) {
            myProjection.setScroll(x,y,touchScreen.getxPressed(),touchScreen.getyPressed());
        }
    }
    /**
     * The pointer is pressed
     * @param x
     * @param y
     */
    protected void pointerPressed(int x, int y) {
        touchScreen.setPressed(x,y);
        myProjection.setScroll(false);
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
            repaint();
        }
        if (keyCode == TouchScreen.ZOOM_OUT) {
            myProjection.decZoom();
            project();
            repaint();
        }
        if (keyCode == TouchScreen.ROT_RIGHT) {
            myProjection.incRot();
            project();
            repaint();
        }
        // Rotation
        if (keyCode == TouchScreen.ROT_LEFT) {
            myProjection.decRot();
            project();
            repaint();
        }

        if (keyCode == TouchScreen.CURSOR_ON) {
            xCursor = x;
            yCursor = y;
            findClosestStar();
            myMidlet.getMyParameter().setCursor(true);
        }
    }
    /**
     *
     * @param keyCode
     */
    protected void keyPressed(int keyCode) {
        // Rotation
        if (keyCode == KEY_NUM7) {
            myProjection.incRot();
            project();
            repaint();
        }
        // Rotation
        if (keyCode == KEY_NUM9) {
            myProjection.decRot();
            project();
            repaint();
        }
        // Zoom out
        if (keyCode == KEY_NUM1) {
            myProjection.decZoom();
            project();
            repaint();
        }
        // Zoom in
        if (keyCode == KEY_NUM3) {
            myProjection.incZoom();
            project();
            repaint();
        }
        // Shift Y
        if (getGameAction(keyCode) == UP) {
            myProjection.incShiftY();
            project();
            repaint();
        }
        // Shift Y
        if (getGameAction(keyCode) == DOWN) {
            myProjection.decShiftY();
            project();
            repaint();
        }
        // Shift X
        if (getGameAction(keyCode) == LEFT) {
            myProjection.incShiftX();
            project();
            repaint();
        }
        // Shift X
        if (getGameAction(keyCode) == RIGHT) {
            myProjection.decShiftX();
            project();
            repaint();
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
     *
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
                System.out.println("Start Sky Thread");
                counter = COUNTER;
            } else {
                // No, decrease counter
                counter--;
            }
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
        for (int k = 0; k < screenCoordStar.length; k++) {
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
        for (int k = 0; k < screenCoordMessier.length; k++) {
            screenCoordMessier[k].setVisible(false);
                // For a zenith view
            if (mySky.getMessier(k).getHeight() > 0) {
                // Star is above horizon
                screenCoordMessier[k].setVisible(true);
                screenCoordMessier[k].x = (short)myProjection.getX(myProjection.getVirtualX(mySky.getMessier(k).getAzimuth(), mySky.getMessier(k).getHeight()));
                screenCoordMessier[k].y = (short)myProjection.getY(myProjection.getVirtualY(mySky.getMessier(k).getAzimuth(), mySky.getMessier(k).getHeight()));
            }
        }
        // === Sun ===
        screenCoordSun.setVisible(false);
        if (mySky.getSun().getHeight() > 0) {
            screenCoordSun.setVisible(true);
            screenCoordSun.x = (short)myProjection.getX(myProjection.getVirtualX(mySky.getSun().getAzimuth(), mySky.getSun().getHeight()));
            screenCoordSun.y = (short)myProjection.getY(myProjection.getVirtualY(mySky.getSun().getAzimuth(), mySky.getSun().getHeight()));
        }

        // === Moon ===
        screenCoordMoon.setVisible(false);
        if (mySky.getMoon().getHeight() > 0) {
            screenCoordMoon.setVisible(true);
            screenCoordMoon.x = (short)myProjection.getX(myProjection.getVirtualX(mySky.getMoon().getAzimuth(), mySky.getMoon().getHeight()));
            screenCoordMoon.y = (short)myProjection.getY(myProjection.getVirtualY(mySky.getMoon().getAzimuth(), mySky.getMoon().getHeight()));
        }

        // === Planets ===
        for (int k = 0; k < screenCoordPlanets.length; k++) {
            screenCoordPlanets[k].setVisible(false);
                // For a zenith view
            if (mySky.getPlanet(k).getHeight() > 0) {
                // Star is above horizon
                screenCoordPlanets[k].setVisible(true);
                screenCoordPlanets[k].x = (short)myProjection.getX(myProjection.getVirtualX(mySky.getPlanet(k).getAzimuth(), mySky.getPlanet(k).getHeight()));
                screenCoordPlanets[k].y = (short)myProjection.getY(myProjection.getVirtualY(mySky.getPlanet(k).getAzimuth(), mySky.getPlanet(k).getHeight()));
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
}