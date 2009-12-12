/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.dox.sideralis.projection.plane;

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
    protected int heightView;
    /** The width of the view */
    protected int widthtView;
    /** X offset of view inside display */
    protected int shiftXView;
    /** Y offset of view inside display */
    protected int shiftYView;
    /** The zoom of the screen */
    protected float zoom;

    public ScreenProj(int hD,int wD) {
        heightDisplay = hD;
        widthDisplay = wD;
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

    public abstract int getX(double virtualX);
    public abstract int getY(double virtualY);
    public abstract double getVirtualX(double az, double hau);
    public abstract double getVirtualY(double az, double hau);

    public abstract void left();
    public abstract void right();
    public abstract void up();
    public abstract void down();
    public abstract void scrollHor(float val);
    public abstract void scrollVer(float val);
    public abstract void incZoom();
    public abstract void decZoom();
    public abstract double getRot();
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
     * Set the new dimension of the view (a view size differs from the display size
     * in order to avoid deformation of the view)
     */
    public void setView() {
        heightView = Math.min(heightDisplay, widthDisplay);
        widthtView = Math.min(heightDisplay, widthDisplay);
        shiftXView = (widthDisplay>heightDisplay?(widthDisplay-heightDisplay)/2:0);
        shiftYView = (heightDisplay>widthDisplay?(heightDisplay-widthDisplay)/2:0);
    }

}
