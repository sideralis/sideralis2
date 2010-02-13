/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.dox.sideralis.projection.plane;

import fr.dox.sideralis.Sideralis;
import fr.dox.sideralis.data.Sky;
import fr.dox.sideralis.object.ScreenCoord;
import javax.microedition.lcdui.Graphics;

/**
 *
 * @author Bernard
 */
public abstract class ScreenProj {
    /** The height of the display */
    protected int getHeight;
    /** The width of the display */
    protected int getWidth;
    /** The rotation of the screen */
    protected float rot;

    /** A reference to the sky */
    protected final Sky mySky;
    /** A reference to my midlet */
    protected final Sideralis myMidlet;

    /**
     * The constructor of this abstract class
     * @param myMidlet a reference to the calling midlet
     * @param wD Width of the display
     * @param hD Height of the display
     */
    public ScreenProj(Sideralis myMidlet,int wD,int hD) {
        getHeight = hD;
        getWidth = wD;
        this.myMidlet = myMidlet;
        mySky = myMidlet.getMySky();
    }

    public abstract void left();
    public abstract void right();
    public abstract void up();
    public abstract void down();
    public abstract void scrollHor(float val);
    public abstract void scrollVer(float val);
    public abstract void incZoom();
    public abstract void decZoom();
    public abstract float getZoom();
    public abstract void setZoom(float zoom);
    public abstract float getRotV();
    public abstract void project();
    public abstract void init();
    public abstract ScreenCoord[] getScreenCoordMessier();
    public abstract ScreenCoord[] getScreenCoordStars();
    public abstract ScreenCoord[] getScreenCoordPlanets();
    public abstract ScreenCoord getScreenCoordMoon();
    public abstract ScreenCoord getScreenCoordSun();
    public abstract void drawHorizon(Graphics g);
    public abstract void setView(int w,int h);
    public abstract boolean is3D();
    public abstract void setLights();
    public abstract void initLights();
    public abstract void stopLights();
    public abstract void setColors();

    /**
     * Return the horizontal rotation in radian
     * @return the angle of the horizontal rotation
     */
    public double getRot() {
        return rot;
    }

    public int getHeight() {
        return getHeight;
    }

    public int getWidth() {
        return getWidth;
    }

}
