package fr.dox.sideralis.projection.plane;

/**
 * This is the projection class. It is used to project the coordinate of the sky objects
 * on the screen
 *
 * @author Bernard
 */
public class Zenith {

    /** The rotation of the screen */
    private double rot;
    /** The zoom of the screen */
    private float zoom;
    /** the x shift of the screen */
    private float shiftX;
    /** The y shift of the screen */
    private float shiftY;
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
    /** All variables needed when dragging with touch screen */
    private boolean scroll;
    private float xDir,yDir;
    private int xOrg,yOrg;

    /**
     * Constructor
     * @param hD height of display
     * @param wD width of display
     */
    public Zenith(int hD, int wD) {
        rot = 0;
        shiftX = shiftY = 0;
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
    public void incRot() {
        rot += Math.PI / 20;
        if (rot > 2 * Math.PI) {
            rot -= 2 * Math.PI;
        }
    }
    /**
     * Decrease the angle of rotation by PI/20
     */
    public void decRot() {
        rot -= Math.PI / 20;
        if (rot < 0) {
            rot += 2 * Math.PI;
        }
    }
    /**
     * Scroll the view left
     */
    public void decShiftX() {
        shiftX -= (zoom-1)/10;
    }
    /**
     * Scroll the view right
     */
    public void incShiftX() {
        shiftX += (zoom-1)/10;
    }
    /**
     * Scroll the view up
     */
    public void decShiftY() {
        shiftY -= (zoom-1)/10;
    }
    /**
     * Scroll the view down
     */
    public void incShiftY() {
        shiftY += (zoom-1)/10;
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
    // =========================================================================
    /**
     * Activate or update scroll parameters (called by pointerDragged)
     * @param x the last position of dragging
     * @param y the last position of dragging
     * @param xPressed the initial position of dragging
     * @param yPressed the initial position of dragging
     */
    public void setScroll(int x, int y, int xPressed, int yPressed) {
        if (scroll == false) {
            scroll = true;
            xDir = x-xPressed;
            yDir = y-yPressed;
            xOrg = xPressed;
            yOrg = yPressed;
        } else {
            xDir = x-xOrg;
            yDir = y-yOrg;
            xOrg = x;
            yOrg = y;
        }
        //System.out.println("Init   - xDir: "+xDir+ " / yDir: "+yDir + " / TimeOffset: "+ timeOffset);
    }
    /**
     * Scroll the display (used only with touch screen)
     * @param screenPressed true if used has yet released his touch, false if released
     */
    public void scroll(boolean screenPressed) {
        shiftY += yDir/60*zoom;
        shiftX += xDir/60*zoom;
        yDir /= 1.5F;
        xDir /= 1.5F;
        if ((yDir < 0.2F) && (xDir <0.2F) && (screenPressed == false))          // If the user has released its touch and scroll is neglictible then scroll is stopped
            scroll = false;
        //System.out.println("Scroll - xDir: "+xDir+ " / yDir: "+yDir);
    }
    /**
     * Return true if scroll is active (meaning that scroll inc is still > 0.2)
     * @return true if scroll is active
     */
    public boolean isScroll() {
        return scroll;
    }
    /**
     * Set scroll (reset when user is pressing down the screen)
     * @param scroll the new value of scroll
     */
    public void setScroll(boolean scroll) {
        this.scroll = scroll;
        //System.out.println("---------------------");
    }
    // =========================================================================
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
