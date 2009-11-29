/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.dox.sideralis.projection.plane;

/**
 *
 * @author Bernard
 */
public class Stereographique {

    /** The rotation of the screen */
    private double rotH,rotV;
    /** The zoom of the screen */
    private float zoom;
    /** The height of the screen */
    private int heightDisplay;
    /** the width of the screen */
    private int widthDisplay;
    /** The height of the view */
    private int heightView;
    /** The width of the view */
    private int widthtView;
    /** X offset of view inside display */
    private int shiftXView;
    /** Y offset of view inside display */
    private int shiftYView;

    /**
     * Constructor
     * @param hD height of display
     * @param wD width of display
     */
    public Stereographique(int hD, int wD) {
        rotH = rotV = 0;
        zoom = 1.0F;
        heightDisplay = hD;
        widthDisplay = wD;
        setView();
    }
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
    public double getRot() {
        return rotH;
    }
    /**
     * Increase the angle of rotation by PI/20
     */
    public void incShiftX() {
        rotH += Math.PI / 20;
        if (rotH > 2 * Math.PI) {
            rotH -= 2 * Math.PI;
        }
    }
    /**
     * Decrease the angle of rotation by PI/20
     */
    public void decShiftX() {
        rotH -= Math.PI / 20;
        if (rotH < 0) {
            rotH += 2 * Math.PI;
        }
    }
    /**
     * Scroll horizontally by val
     * @param val the amplitude of scroll
     */
    public void addShiftX(float val) {
        rotH += val;
        if (rotH > 2 * Math.PI) {
            rotH -= 2 * Math.PI;
        }
    }
    /**
     * Increase the angle of rotation by PI/20
     */
    public void incShiftY() {
        rotV += Math.PI / 20;
        if (rotV > 2 * Math.PI) {
            rotV -= 2 * Math.PI;
        }
    }
    /**
     * Decrease the angle of rotation by PI/20
     */
    public void decShiftY() {
        rotV -= Math.PI / 20;
        if (rotV < 0) {
            rotV += 2 * Math.PI;
        }
    }
    /**
     * Scroll vertically by val
     * @param val the amplitude of scroll
     */
    public void addShiftY(float val) {
        rotV += val;
        if (rotV > 2 * Math.PI) {
            rotV -= 2 * Math.PI;
        }
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
     * Increment the zoom by 0.5 (limited to 5) and scroll view ?
     */
    public void incZoom() {
        if (zoom < 5)
            zoom += 0.5;

    }
    /**
     * Decrement zoom by 0.5 (limited to 1)
     * Recenter view
     */
    public void decZoom() {
        if (zoom > 1)
            zoom -= 0.5;

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
        x = x * widthtView/2 + shiftXView;

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

        y = 1 + virtualY * zoom;
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
        double x;

        x = Math.cos(az + rotH)*Math.tan((hau+rotV)/2);

        return x;
    }

    /**
     * Return the projection of the object on the y axis
     * @param az azimuth of the object
     * @param hau height of the object
     * @return A value between -1 and 1
     */
    public double getVirtualY(double az, double hau) {
        double y;

        y = Math.sin(az + rotH)*Math.tan((hau+rotV)/2);

        return y;
    }

}
