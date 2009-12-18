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
    /** The height of the screen */
    protected int heightDisplay;
    /** the width of the screen */
    protected int widthDisplay;
    /** The height of the view */
    protected int getHeight;
    /** The width of the view */
    protected int getWidth;
    /** X offset of view inside display */
    protected int shiftXView;
    /** Y offset of view inside display */
    protected int shiftYView;
    /** The zoom of the screen */
    protected float zoom;
    /** The rotation of the screen */
    protected float rot;

    /** A reference to the sky */
    protected Sky mySky;
    /** A reference to my midlet */
    protected Sideralis myMidlet;


    public ScreenProj(Sideralis myMidlet,int hD,int wD) {
        heightDisplay = hD;
        widthDisplay = wD;
        this.myMidlet = myMidlet;
        mySky = myMidlet.getMySky();
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

    public abstract void left();
    public abstract void right();
    public abstract void up();
    public abstract void down();
    public abstract void scrollHor(float val);
    public abstract void scrollVer(float val);
    public abstract void incZoom();
    public abstract void decZoom();
    public abstract void project();
    public abstract void init();
    public abstract ScreenCoord[] getScreenCoordMessier();
    public abstract ScreenCoord[] getScreenCoordStars();
    public abstract ScreenCoord[] getScreenCoordPlanets();
    public abstract ScreenCoord getScreenCoordMoon();
    public abstract ScreenCoord getScreenCoordSun();
    public abstract void drawHorizon(Graphics g);
    public abstract void setView();


    /**
     * Called in case of display change (rotation, ...)
     * @param heightDisplay the new height of the display
     */
    public void setHeightDisplay(int heightDisplay) {
        this.heightDisplay = heightDisplay;
    }
    /**
     * Called in case of display change (rotation, ...)
     * @param widthDisplay the new width of the display
     */
    public void setWidthDisplay(int widthDisplay) {
        this.widthDisplay = widthDisplay;
    }

    public double getRot() {
        return rot;
    }

}
