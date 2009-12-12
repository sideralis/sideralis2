package fr.dox.sideralis.projection.plane;

/**
 * This is the projection class. It is used to project the coordinate of the sky objects
 * on the screen
 *
 * @author Bernard
 */
public class Zenith extends ScreenProj {

    /** The rotation of the screen */
    private double rot;
    /** the x shift of the screen */
    private float shiftX;
    /** The y shift of the screen */
    private float shiftY;

    /**
     * Constructor
     * @param hD height of display
     * @param wD width of display
     */
    public Zenith(int hD, int wD) {
        super(hD,wD);
        rot = 0;
        shiftX = shiftY = 0;
        zoom = 1.0F;
        setView();
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
    public void setRot(double rot) {
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
}
