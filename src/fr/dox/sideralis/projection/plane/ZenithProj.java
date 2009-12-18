package fr.dox.sideralis.projection.plane;

import fr.dox.sideralis.Sideralis;
import fr.dox.sideralis.data.MessierCatalog;
import fr.dox.sideralis.data.Sky;
import fr.dox.sideralis.object.ScreenCoord;
import fr.dox.sideralis.view.color.Color;
import javax.microedition.lcdui.Graphics;

/**
 * This is the projection class. It is used to project the coordinate of the sky objects
 * on the screen
 *
 * @author Bernard
 */
public class ZenithProj extends ScreenProj {

    /** the x shift of the screen */
    private float shiftX;
    /** The y shift of the screen */
    private float shiftY;
    /** Table to store x and y position on screen of stars */
    private ScreenCoord[] screenCoordStars;
    private ScreenCoord[] screenCoordMessier;
    private ScreenCoord screenCoordSun;
    private ScreenCoord screenCoordMoon;
    private ScreenCoord[] screenCoordPlanets;

    /**
     * Constructor
     * @param hD height of display
     * @param wD width of display
     */
    public ZenithProj(Sideralis myMidlet, int hD, int wD) {
        super(myMidlet,hD,wD);
        rot = 0;
        shiftX = shiftY = 0;
        zoom = 1.0F;
        setView();
    }
    /**
     *
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
     * Return the rotation of the view
     * @return the angle of rotation of the view
     */
    public double getRot() {
        return rot;
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
     * Increment the zoom by 0.5 (limited to 5) and scroll view ?
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

        if (shiftX > (zoom - 1)) {
            shiftX = zoom - 1;
        }

        if (shiftX < (1 - zoom)) {
            shiftX = 1 - zoom;
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

        if (shiftX > (zoom - 1)) {
            shiftX = zoom - 1;
        }

        if (shiftX < (1 - zoom)) {
            shiftX = 1 - zoom;
        }
    }
    /**
     * Return the real x value on the screen from a x from a virtual display.
     * Take into account zoom, shift and size of display.
     * @param virtualX a value between -1 and 1
     * @return the x coordinate on the real screen
     */
    public int getX(double virtualX) {
        double x;

        x = 1 + virtualX * zoom + shiftX;
        x = x * getWidth/2 + shiftXView;

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
        y = y * getHeight/2 + shiftYView;

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
     * @return the screenCoordStars
     */
    public ScreenCoord[] getScreenCoordStars() {
        return screenCoordStars;
    }

    /**
     * @return the screenCoordMessier
     */
    public ScreenCoord[] getScreenCoordMessier() {
        return screenCoordMessier;
    }

    /**
     * @return the screenCoordSun
     */
    public ScreenCoord getScreenCoordSun() {
        return screenCoordSun;
    }

    /**
     * @return the screenCoordMoon
     */
    public ScreenCoord getScreenCoordMoon() {
        return screenCoordMoon;
    }

    /**
     * @return the screenCoordPlanets
     */
    public ScreenCoord[] getScreenCoordPlanets() {
        return screenCoordPlanets;
    }

    public void drawHorizon(Graphics g) {
        // ----------------------------
        // -----   Clear screen   -----
        g.setColor((myMidlet.getMyParameter().getColor())[Color.COL_BACKGROUND]);
        g.fillRect(0, 0, getWidth, getHeight);
        // ----------------------
        // ---- Draw horizon ----
        g.setColor((myMidlet.getMyParameter().getColor())[Color.COL_ZENITH_BACKGROUND]);
        g.fillRect(0, 0, getWidth, getHeight);
    }
    /**
     * Set the new dimension of the view (a view size differs from the display size
     * in order to avoid deformation of the view)
     */
    public void setView() {
        getHeight = Math.min(heightDisplay, widthDisplay);
        getWidth = Math.min(heightDisplay, widthDisplay);
        shiftXView = (widthDisplay>heightDisplay?(widthDisplay-heightDisplay)/2:0);
        shiftYView = (heightDisplay>widthDisplay?(heightDisplay-widthDisplay)/2:0);
    }

}
