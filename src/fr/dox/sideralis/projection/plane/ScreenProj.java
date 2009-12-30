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
    /** The rotation of the screen */
    protected float rot;

    /** A reference to the sky */
    protected Sky mySky;
    /** A reference to my midlet */
    protected Sideralis myMidlet;

    /**
     * The constructor of this abstract class
     * @param myMidlet a reference to the calling midlet
     * @param hD Height of the display
     * @param wD Width of the display
     */
    public ScreenProj(Sideralis myMidlet,int hD,int wD) {
        heightDisplay = hD;
        widthDisplay = wD;
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
    public abstract void setView();
    public abstract boolean is3D();

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
    /**
     * Return the horizontal rotation in radian
     * @return the angle of the horizontal rotation
     */
    public double getRot() {
        return rot;
    }

}
