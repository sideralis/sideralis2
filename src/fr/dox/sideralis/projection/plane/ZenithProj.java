package fr.dox.sideralis.projection.plane;

import fr.dox.sideralis.LocalizationSupport;
import fr.dox.sideralis.Sideralis;
import fr.dox.sideralis.data.MessierCatalog;
import fr.dox.sideralis.data.Sky;
import fr.dox.sideralis.object.ScreenCoord;
import fr.dox.sideralis.view.color.Color;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

/**
 * This is the projection class. It is used to project the coordinate of the sky objects
 * on the screen
 *
 * @author Bernard
 */
public class ZenithProj extends ScreenProj {
    /** The y shift of the screen */
    private float shiftY;
    /** Table to store x and y position on screen of stars */
    private ScreenCoord[] screenCoordStars;
    private ScreenCoord[] screenCoordMessier;
    private ScreenCoord screenCoordSun;
    private ScreenCoord screenCoordMoon;
    private ScreenCoord[] screenCoordPlanets;
    /** The zoom of the screen */
    private float zoom;
    /** The height of the view */
    private int heightView;
    /** The width of the view */
    private int widthView;
    /** The horizontal and vertical shift of the view so it appears in the midle of the display */
    private int shiftXView, shiftYView;
    /** The string used to indicat the cardinate pole */
    private static String northString, southString, eastString, westString;


    /**
     * Constructor
     * @param myMidlet the calling midlet
     * @param wD width of display
     * @param hD height of display
     */
    public ZenithProj(Sideralis myMidlet, int wD, int hD) {
        super(myMidlet,wD,hD);
        setView(wD,hD);
        rot = 0;
        shiftY = 0;
        zoom = 1.0F;
        northString = LocalizationSupport.getMessage("NORTH");
        westString = LocalizationSupport.getMessage("WEST");
        southString = LocalizationSupport.getMessage("SOUTH");
        eastString = LocalizationSupport.getMessage("EAST");
    }
    /**
     * Initialization of all variables
     */
    public void init() {
        screenCoordMessier = new ScreenCoord[MessierCatalog.getNumberOfObjects()];
        for (int k = 0; k < MessierCatalog.getNumberOfObjects(); k++)
            screenCoordMessier[k] = new ScreenCoord();

        screenCoordStars = new ScreenCoord[mySky.getStarsProj().length];
        for (int k = 0; k < getScreenCoordStars().length; k++)
            screenCoordStars[k] = new ScreenCoord();

        screenCoordSun = new ScreenCoord();
        screenCoordMoon = new ScreenCoord();
        screenCoordPlanets = new ScreenCoord[Sky.NB_OF_PLANETS];
        for (int k=0;k<Sky.NB_OF_PLANETS;k++)
            screenCoordPlanets[k] = new ScreenCoord();
    }
    /**
     * Set the new rotation of the view
     * @param rot the new angle rotation
     */
    public void setRot(float rot) {
        this.rot = rot;
    }
    /**
     * Increase the angle of rotation by PI/20
     */
    public void left() {
        rot += Math.PI / 20;
        if (rot > 2 * Math.PI) {
            rot -= 2 * Math.PI;
        }
    }
    /**
     * Decrease the angle of rotation by PI/20
     */
    public void right() {
        rot -= Math.PI / 20;
        if (rot < 0) {
            rot += 2 * Math.PI;
        }
    }
    /**
     * Scroll the view up
     */
    public void down() {
        shiftY -= (zoom-1)/10;
        if (shiftY < (1 - zoom)) {
            shiftY = 1 - zoom;
        }
    }
    /**
     * Scroll the view down
     */
    public void up() {
        shiftY += (zoom-1)/10;
        if (shiftY > (zoom - 1)) {
            shiftY = zoom - 1;
        }
    }
    /**
     * Scroll horizontally by val
     * @param val the amplitude of horizontal rotation
     */
    public void scrollHor(float val) {
        rot += val / 20 / zoom;
        if (rot<0)
            rot += 2 * Math.PI;
        else if (rot>2*Math.PI)
            rot -= 2 * Math.PI;
    }
    /**
     * Scroll vertically by val
     * @param val the amplitude of scroll     
     */
    public void scrollVer(float val) {
        shiftY += val/60*zoom;
        if (shiftY > (zoom - 1)) {
            shiftY = zoom - 1;
        }
        if (shiftY < (1 - zoom)) {
            shiftY = 1 - zoom;
        }
    }
    /**
     * Increment the zoom by 0.5 (limited to 5)
     */
    public void incZoom() {
        if (zoom < 5)
            zoom += 0.5;

        if (shiftY > (zoom - 1)) {
            shiftY = zoom - 1;
        }

        if (shiftY < (1 - zoom)) {
            shiftY = 1 - zoom;
        }
    }
    /**
     * Decrement zoom by 0.5 (limited to 1)
     * Recenter view
     */
    public void decZoom() {
        if (zoom > 1)
            zoom -= 0.5;

        if (shiftY > (zoom - 1)) {
            shiftY = zoom - 1;
        }

        if (shiftY < (1 - zoom)) {
            shiftY = 1 - zoom;
        }
    }
    public float getRotV() {
        return shiftY;
    }
    /**
     * Return the real x value on the screen from a x from a virtual display.
     * Take into account zoom, shift and size of display.
     * @param virtualX a value between -1 and 1
     * @return the x coordinate on the real screen
     */
    public int getX(double virtualX) {
        double x;

        x = 1 + virtualX * zoom;
        x = x * widthView/2 + shiftXView;

        return (int)x;
    }
    /**
     * Return the real y value on the screen from a y from a virtual display.
     * Take into account zoom, shift and size of display.
     * @param virtualY a value between -1 and 1
     * @return the y coordinate on the real screen
     */
    public int getY(double virtualY) {
        double y;

        y = 1 + virtualY * zoom + shiftY;
        y = y * heightView/2 + shiftYView;

        return (int)y;
    }
    /**
     * Return the projection of the object on the x axis
     * @param az azimuth of the object
     * @param hau height of the object
     * @return A value between -1 and 1
     */
    public double getVirtualX(double az, double hau) {
        double distL;
        double x;

        distL = 1 - hau / (Math.PI / 2);
        x = -distL * Math.cos(az + rot);                                        // To have west on east and vice versa

        return x;
    }

    /**
     * Return the projection of the object on the y axis
     * @param az azimuth of the object
     * @param hau height of the object
     * @return A value between -1 and 1
     */
    public double getVirtualY(double az, double hau) {
        double distL;
        double y;

        distL = 1 - hau / (Math.PI / 2);
        y = distL * Math.sin(az + rot);

        return y;
    }
    /**
     * Project all the objects of the sky on the screen.
     * Fill the screenCoordXXX variables
     */
    public void project() {
        // === Stars ===
        for (int k = 0; k < getScreenCoordStars().length; k++) {
            getScreenCoordStars()[k].setVisible(false);
                // For a zenith view
            if (mySky.getStar(k).getHeight() > 0) {
                // Star is above horizon
                getScreenCoordStars()[k].setVisible(true);
                screenCoordStars[k].x = (short)getX(getVirtualX(mySky.getStar(k).getAzimuth(), mySky.getStar(k).getHeight()));
                screenCoordStars[k].y = (short)getY(getVirtualY(mySky.getStar(k).getAzimuth(), mySky.getStar(k).getHeight()));
            }
        }

        // === Messiers ===
        for (int k = 0; k < screenCoordMessier.length; k++) {
            screenCoordMessier[k].setVisible(false);
                // For a zenith view
            if (mySky.getMessier(k).getHeight() > 0) {
                // Star is above horizon
                screenCoordMessier[k].setVisible(true);
                screenCoordMessier[k].x = (short)getX(getVirtualX(mySky.getMessier(k).getAzimuth(), mySky.getMessier(k).getHeight()));
                screenCoordMessier[k].y = (short)getY(getVirtualY(mySky.getMessier(k).getAzimuth(), mySky.getMessier(k).getHeight()));
            }
        }
        // === Sun ===
        screenCoordSun.setVisible(false);
        if (mySky.getSun().getHeight() > 0) {
            screenCoordSun.setVisible(true);
            screenCoordSun.x = (short)getX(getVirtualX(mySky.getSun().getAzimuth(), mySky.getSun().getHeight()));
            screenCoordSun.y = (short)getY(getVirtualY(mySky.getSun().getAzimuth(), mySky.getSun().getHeight()));
        }

        // === Moon ===
        screenCoordMoon.setVisible(false);
        if (mySky.getMoon().getHeight() > 0) {
            screenCoordMoon.setVisible(true);
            screenCoordMoon.x = (short)getX(getVirtualX(mySky.getMoon().getAzimuth(), mySky.getMoon().getHeight()));
            screenCoordMoon.y = (short)getY(getVirtualY(mySky.getMoon().getAzimuth(), mySky.getMoon().getHeight()));
        }

        // === Planets ===
        for (int k = 0; k < screenCoordPlanets.length; k++) {
            screenCoordPlanets[k].setVisible(false);
                // For a zenith view
            if (mySky.getPlanet(k).getHeight() > 0) {
                // Star is above horizon
                screenCoordPlanets[k].setVisible(true);
                screenCoordPlanets[k].x = (short)getX(getVirtualX(mySky.getPlanet(k).getAzimuth(), mySky.getPlanet(k).getHeight()));
                screenCoordPlanets[k].y = (short)getY(getVirtualY(mySky.getPlanet(k).getAzimuth(), mySky.getPlanet(k).getHeight()));
            }
        }
    }
    /**
     * Return the reference to the screen coordinates of the stars
     * @return the screenCoordStars
     */
    public ScreenCoord[] getScreenCoordStars() {
        return screenCoordStars;
    }

    /**
     * Return the reference to the screen coordinates of the Messier objects
     * @return the screenCoordMessier
     */
    public ScreenCoord[] getScreenCoordMessier() {
        return screenCoordMessier;
    }

    /**
     * Return the reference to the screen coordinates of the Sun
     * @return the screenCoordSun
     */
    public ScreenCoord getScreenCoordSun() {
        return screenCoordSun;
    }

    /**
     * Return the reference to the screen coordinates of the Moon
     * @return the screenCoordMoon
     */
    public ScreenCoord getScreenCoordMoon() {
        return screenCoordMoon;
    }

    /**
     * Return the reference to the screen coordinates of the planets
     * @return the screenCoordPlanets
     */
    public ScreenCoord[] getScreenCoordPlanets() {
        return screenCoordPlanets;
    }
    /**
     * Return the zoom value
     * @return the zoom value
     */
    public float getZoom() {
        return zoom;
    }
    /**
     * Set a value to the zoom
     * @param zoom the new value of the zoom
     */
    public void setZoom(float zoom) {
        this.zoom = zoom;
    }
    /**
     * Draw the horizon
     * @param g the graphic object on which we draw
     */
    public void drawHorizon(Graphics g) {
        double x1,y1,x2,y2,x3,y3,x4,y4;
        Font myFont;

        myFont = g.getFont();

        // ----------------------------
        // -----   Clear screen   -----
        g.setColor((myMidlet.getMyParameter().getColor())[Color.COL_BACKGROUND]);
        g.fillRect(0, 0, getWidth, getHeight);
        // ----------------------
        // ---- Draw horizon ----
        g.setColor((myMidlet.getMyParameter().getColor())[Color.COL_ZENITH_BACKGROUND]);

        x1 = getX(-1);
        y1 = getY(-1);
        x2 = getX(1) - x1;
        y2 = getY(1) - y1;

        g.fillArc((int) x1, (int) y1, (int) x2, (int) y2, 0, 360);

        // -----------------------------
        // --- Draw Cardinate points ---
        x1 = getX(Math.cos(-rot) * .95);
        y1 = getY(Math.sin(-rot) * .95);
        x2 = getX(Math.cos(-rot + Math.PI) * .95);
        y2 = getY(Math.sin(-rot + Math.PI) * .95);
        x3 = getX(Math.cos(-rot + Math.PI / 2) * .95);
        y3 = getY(Math.sin(-rot + Math.PI / 2) * .95);
        x4 = getX(Math.cos(-rot + 3 * Math.PI / 2) * .95);
        y4 = getY(Math.sin(-rot + 3 * Math.PI / 2) * .95);
        g.setColor((myMidlet.getMyParameter().getColor())[Color.COL_CROSS]);
        g.setStrokeStyle(Graphics.DOTTED);
        g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
        g.drawLine((int) x3, (int) y3, (int) x4, (int) y4);
        g.setStrokeStyle(Graphics.SOLID);
        g.setColor((myMidlet.getMyParameter().getColor())[Color.COL_N_S_E_O]);
        g.drawString(westString, (int) x1, (int) y1 - myFont.getHeight() / 2, Graphics.HCENTER | Graphics.TOP);
        g.drawString(eastString, (int) x2, (int) y2 - myFont.getHeight() / 2, Graphics.HCENTER | Graphics.TOP);
        g.drawString(southString, (int) x3, (int) y3 - myFont.getHeight() / 2, Graphics.HCENTER | Graphics.TOP);
        g.drawString(northString, (int) x4, (int) y4 - myFont.getHeight() / 2, Graphics.HCENTER | Graphics.TOP);

    }
    /**
     * Set the new dimension of the view (a view size differs from the display size
     * in order to avoid deformation of the view)
     * @param w the width of the display
     * @param h the height of the display
     */
    public void setView(int w, int h) {
        getHeight = h;
        getWidth = w;
        heightView = Math.min(getHeight, getWidth);
        widthView = Math.min(getHeight, getWidth);
        shiftXView = (getWidth>getHeight?(getWidth-getHeight)/2:0);
        shiftYView = (getHeight>getWidth?(getHeight-getWidth)/2:0);
    }
    /**
     * Indicates that it is a zenith view
     * @return false
     */
    public boolean is3D() {
        return false;
    }
}
