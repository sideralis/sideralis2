package fr.dox.sideralis.projection.plane;

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
    private long timeOffset;
    private long timeBase;



    public Zenith(int hD, int wD) {
        rot = 0;
        shiftX = shiftY = 0;
        zoom = 1.0F;
        heightDisplay = hD;
        widthDisplay = wD;
        setView();
    }

    public void setHeightDisplay(int heightDisplay) {
        this.heightDisplay = heightDisplay;
    }
    public void setWidthDisplay(int widthDisplay) {
        this.widthDisplay = widthDisplay;
    }
    public void setView() {
        heightView = Math.min(heightDisplay, widthDisplay);
        widthtView = Math.min(heightDisplay, widthDisplay);
        shiftXView = (widthDisplay>heightDisplay?(widthDisplay-heightDisplay)/2:0);
        shiftYView = (heightDisplay>widthDisplay?(heightDisplay-widthDisplay)/2:0);
    }

    public double getRot() {
        return rot;
    }
    public void setRot(double rot) {
        this.rot = rot;
    }
    public void incRot() {
        rot += Math.PI / 20;
        if (rot > 2 * Math.PI) {
            rot -= 2 * Math.PI;
        }
    }
    public void decRot() {
        rot -= Math.PI / 20;
        if (rot < 0) {
            rot += 2 * Math.PI;
        }
    }

    public void decShiftX() {
        shiftX -= (zoom-1)/10;
    }
    public void incShiftX() {
        shiftX += (zoom-1)/10;
    }

    public void decShiftY() {
        shiftY -= (zoom-1)/10;
    }
    public void incShiftY() {
        shiftY += (zoom-1)/10;
    }
    public void setScroll(int x, int y, int xPressed, int yPressed) {
        if (scroll == false) {
            scroll = true;
            xDir = x-xPressed;
            yDir = y-yPressed;
            xOrg = xPressed;
            yOrg = yPressed;
            timeBase = System.currentTimeMillis();
            timeOffset = 0;
        } else {
            xDir = x-xOrg;
            yDir = y-yOrg;
            xOrg = x;
            yOrg = y;
            timeOffset = System.currentTimeMillis() - timeBase;
            timeBase = System.currentTimeMillis();
        }
        System.out.println("Init   - xDir: "+xDir+ " / yDir: "+yDir + " / TimeOffset: "+ timeOffset);
    }
    public void scroll(boolean screenPressed) {
        shiftY += yDir/60*zoom;
        shiftX += xDir/60*zoom;
        yDir /= 1.5F;
        xDir /= 1.5F;
        if ((yDir < 0.2F) && (xDir <0.2F) && (screenPressed == false))          // If the user has released its touch and scroll is neglictible then scroll is stopped
            scroll = false;
        System.out.println("Scroll - xDir: "+xDir+ " / yDir: "+yDir);
    }

    public boolean isScroll() {
        return scroll;
    }

    public void setScroll(boolean scroll) {
        this.scroll = scroll;
        System.out.println("---------------------");
    }


    public float getZoom() {
        return zoom;
    }
    public void setZoom(float zoom) {
        this.zoom = zoom;
    }
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
     * Return the real x value on the screen from a x from a virtual display. Take into account zoom, shift and size of display. 
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
     * Return the real y value on the screen from a y from a virtual display. Take into account zoom, shift and size of display.
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
