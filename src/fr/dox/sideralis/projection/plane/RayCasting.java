/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.dox.sideralis.projection.plane;

/**
 *
 * @author Bernard
 */
public class RayCasting extends ScreenProj {
    /** The horizontal and vertical rotation */
    private double rotH,rotV;
    /** The distance of objects - arbitrarly set */
    private double distance = 100;


    public RayCasting(int hD,int wD) {
        super(hD, wD);
        rotH = 0;
        rotV = 0;
        zoom = 100;
    }
    /**
     * Return the real x value on the screen from a x from a virtual display.
     * Take into account zoom, shift and size of display.
     * @param virtualX a value between -1 and 1
     * @return the x coordinate on the real screen
     */
    public int getX(double virtualX) {
        double x;

        x = 1 + virtualX / distance * zoom ;
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

        y = 1 - virtualY / distance * zoom ;
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

        x = -Math.cos(az + rotH);                                        // To have west on east and vice versa

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

        y = Math.sin(hau + rotV);

        return y;
    }

    public void left() {
        rotH += Math.PI / 20;
        if (rotH > 2 * Math.PI) {
            rotH -= 2 * Math.PI;
        }
    }
    public void right() {
        rotH -= Math.PI / 20;
        if (rotH < 0) {
            rotH += 2 * Math.PI;
        }
    }
    public void up() {
        rotV += Math.PI / 20;
        if (rotV > 2 * Math.PI) {
            rotV -= 2 * Math.PI;
        }
    }
    public void down() {
        rotV -= Math.PI / 20;
        if (rotV < 0) {
            rotV += 2 * Math.PI;
        }
    }

    public void scrollHor(float val) {
        rotH += val / 20 / zoom;
        if (rotH<0)
            rotH += 2 * Math.PI;
        else if (rotH>2*Math.PI)
            rotH -= 2 * Math.PI;
    }

    public void scrollVer(float val) {
        rotV += val / 20 / zoom;
        if (rotV<0)
            rotV += 2 * Math.PI;
        else if (rotV>2*Math.PI)
            rotV -= 2 * Math.PI;
    }

    public void decZoom() {
        zoom += 1;
    }

    public void incZoom() {
        zoom -= 1;
    }

    /**
     * Return the rotation of the view
     * @return the angle of rotation of the view
     */
    public double getRot() {
        return rotH;
    }

}
