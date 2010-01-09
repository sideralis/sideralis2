/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.dox.sideralis.projection.plane;

import fr.dox.sideralis.Sideralis;
import fr.dox.sideralis.data.MessierCatalog;
import fr.dox.sideralis.data.Sky;
import fr.dox.sideralis.object.ScreenCoord;
import java.util.Random;
import javax.microedition.lcdui.Graphics;
import javax.microedition.m3g.Appearance;
import javax.microedition.m3g.Camera;
import javax.microedition.m3g.Graphics3D;
import javax.microedition.m3g.Mesh;
import javax.microedition.m3g.PolygonMode;
import javax.microedition.m3g.Transform;
import javax.microedition.m3g.TriangleStripArray;
import javax.microedition.m3g.VertexArray;
import javax.microedition.m3g.VertexBuffer;

/**
 * This class is used to calculate the screen coordinate of all sky objects
 * @author Bernard
 */
public class EyeProj extends ScreenProj {

    /** The screen coordinate of the object, map to display*/
    private ScreenCoord[] screenCoordPlanets,screenCoordStars,screenCoordMessiers;
    /** Positions of object */
    private short[] positionsPlanets, positionsStars, positionsMessiers;
    /** The screen coordinate of the objects, just after transform */
    private float[] coord2DPlanets, coord2DStars, coord2DMessiers;

    /** The sky */
    private Mesh planetMesh,starMesh,messierMesh;
    /** The horizon */
    private Mesh horizon;
    /** The lights */
//    private Light lightSun, lightMoon;
    /** The camera */
    private Camera camera;
    /** The Camera Transform */
    private Transform cameraTransform, invertedCameraTransform;
    /** 3D graphics singleton used for rendering. */
    private Graphics3D graphics3D;

    /** The vertical rotation */
    private float rotV;

    /** Viewport aspect ration */
    private float aspectRatio;
    /** Camera near and far parameters */
    private float near,far;
    /** The field of vision */
    private float fov;

    /** Global variable (to avoid multiple new) */
    private VertexBuffer genericVertexBuffer;
    private VertexArray genericVertexPositions;

    private static final int SUN = Sky.NB_OF_PLANETS;
    private static final int MOON = SUN + 1;
    /**
     * The constructor
     * @param myMidlet a reference to the calling midlet
     * @param wD the width of the display
     * @param hD the height of the display
     */
    public EyeProj(Sideralis myMidlet, int wD, int hD) {
        super(myMidlet, wD, hD);
        setView(wD,hD);
    }
    /**
     * Intialization
     */
    public void init() {
        graphics3D = Graphics3D.getInstance();

        fov = 70;
        near = 1;
        far = 1000;
        rotV = (float)Math.toRadians(20);

        positionsPlanets = new short[(Sky.NB_OF_PLANETS +2) * 3];               // 2 for Sun and Moon
        positionsStars = new short[mySky.getStarsProj().length * 3];
        positionsMessiers = new short[MessierCatalog.getNumberOfObjects() * 3];

        // Create coordinate object for all objects in the sky (planets & Sun & Moon)
        screenCoordPlanets = new ScreenCoord[Sky.NB_OF_PLANETS + 2];
        for (int i = 0; i < screenCoordPlanets.length; i++) {
            screenCoordPlanets[i] = new ScreenCoord();
        }

        screenCoordStars = new ScreenCoord[mySky.getStarsProj().length];
        for (int k = 0; k < screenCoordStars.length; k++)
            screenCoordStars[k] = new ScreenCoord();

        screenCoordMessiers = new ScreenCoord[MessierCatalog.getNumberOfObjects()];
        for (int i=0;i<screenCoordMessiers.length;i++) {
            screenCoordMessiers[i] = new ScreenCoord();
        }
        // Create 2d coordinates
        coord2DPlanets = new float[(Sky.NB_OF_PLANETS+2) * 4];                  // 2 for Sun and Moon
        coord2DStars = new float[mySky.getStarsProj().length * 4];
        coord2DMessiers = new float[MessierCatalog.getNumberOfObjects() *4];


        // Create a sky
        createSky();

        // Create the lights
//        if (mySky.getSun().getHeight()>0) {
//            lightSun = new Light();
//            lightSun.setMode(Light.DIRECTIONAL);
//            lightSun.setIntensity(10);
//            x = Math.cos(mySky.getSun().getAzimuth()) * Math.cos(mySky.getSun().getHeight());
//            y = Math.sin(mySky.getSun().getHeight());
//            z = Math.sin(mySky.getSun().getAzimuth()) * Math.cos(mySky.getSun().getHeight());
//            lightSun.translate((float)x, (float)y, (float)z);
//            graphics3D.addLight(lightSun, null);
//        }

        // Create an horizon
        horizon = createHorizon();
        addRandomColors(horizon);
        PolygonMode polygonMode = new PolygonMode();
        polygonMode.setPerspectiveCorrectionEnable(true);
        polygonMode.setCulling(PolygonMode.CULL_NONE);
//        polygonMode.setShading(PolygonMode.SHADE_FLAT);
        horizon.getAppearance(0).setPolygonMode(polygonMode);

        // Create a camera
        camera = new Camera();
        aspectRatio = (float)getWidth/(float)getHeight;
        cameraTransform = new Transform();
        invertedCameraTransform = new Transform();
        setCamera();

    }

    /**
     * Create the sky mesh
     */
    private void createSky() {
        short[] positions;
        VertexBuffer vertexBuffer;
        VertexArray vertexPositions;
        TriangleStripArray triangles;

        // ===================================
        // === Create planets & Sun & Moon ===
        // ===================================
        positions = new short[(Sky.NB_OF_PLANETS+2) * 3];                       // 2 for Sun and Moon
        vertexBuffer = new VertexBuffer();
        vertexPositions = new VertexArray(positions.length / 3, 3, 2);
        vertexPositions.set(0, positions.length / 3, positions);
        vertexBuffer.setPositions(vertexPositions, 1, null);

        triangles = new TriangleStripArray(0, new int[]{Sky.NB_OF_PLANETS+2});  // 2 for Sun and Moon

        planetMesh = new Mesh(vertexBuffer, triangles, new Appearance());

        // ====================
        // === Create stars ===
        // ====================
        positions = new short[mySky.getStarsProj().length * 3];
        vertexBuffer = new VertexBuffer();
        vertexPositions = new VertexArray(positions.length / 3, 3, 2);
        vertexPositions.set(0, positions.length / 3, positions);
        vertexBuffer.setPositions(vertexPositions, 1, null);

        triangles = new TriangleStripArray(0, new int[]{mySky.getStarsProj().length});

        starMesh = new Mesh(vertexBuffer, triangles, new Appearance());

        // =======================
        // === Create Messiers ===
        // =======================
        positions = new short[MessierCatalog.getNumberOfObjects() * 3];
        vertexBuffer = new VertexBuffer();
        vertexPositions = new VertexArray(positions.length / 3, 3, 2);
        vertexPositions.set(0, positions.length / 3, positions);
        vertexBuffer.setPositions(vertexPositions, 1, null);

        triangles = new TriangleStripArray(0, new int[]{MessierCatalog.getNumberOfObjects()});

        messierMesh = new Mesh(vertexBuffer, triangles, new Appearance());
    }
    /**
     * Create the horizon 3D object
     * @return the mesh representing the horizon
     */
    private Mesh createHorizon() {
        Random random = new Random();
        final short scale = Short.MAX_VALUE/10;
        short dim = 63;
        int height = 128;
        short heightOffset = -128;
        final short step = (short)(2*scale/dim);
        double dist;

        short[] positions = new short[(dim+1) * (dim+1) * 3];
        int[] triangleIndices = new int[((dim+1)*2)*dim];
        int[] triangleLengths = new int[dim];

        short x,y,z;
        int i,j,index,tk;

        x = -scale;
        y = (short)(heightOffset-random.nextInt(height));
        z = -scale;
        index = 0;
        tk = 0;
        // 0
        positions[index++] = x;
        positions[index++] = y;
        positions[index++] = z;

        // Create first line
        for (j=0;j<dim;j++) {
            dist = (float)(x*x + z*z)/(float)(scale*scale)*Math.PI;
            y = (short) (heightOffset*Math.sin(dist)-random.nextInt(height));
            x = (short) (x + step);
            positions[index++] = x;
            positions[index++] = y;
            positions[index++] = z;
        }
        // Create next lines
        for (i = 1; i <= dim; i++) {
            z = (short) (z + step);
            x = -scale;
            // First vertice in line
            positions[index++] = x;
            positions[index++] = y;
            positions[index++] = z;
            for (j = 1; j <= dim; j++) {
                //  Next vertices
                dist = (float)(x*x + z*z)/(float)(scale*scale)*Math.PI;
                y = (short) (heightOffset*Math.sin(dist)-random.nextInt(height));
                x = (short) (x + step);
//                if (x<step && x>0) System.out.println(""+x+" "+y+" "+z);
                positions[index++] = x;
                positions[index++] = y;
                positions[index++] = z;

            }
            for (j=0;j<=dim;j++) {
                triangleIndices[tk++] = (i-1)*(dim+1) + j;
                triangleIndices[tk++] = (i)*(dim+1) + j;
            }
            triangleLengths[i-1] = (dim+1)*2;
        }
        VertexBuffer planeVertexData = new VertexBuffer();

        VertexArray vertexPositions = new VertexArray(positions.length / 3, 3, 2);
        vertexPositions.set(0, positions.length / 3, positions);
        planeVertexData.setPositions(vertexPositions, 1, null);

        TriangleStripArray planeTriangles = new TriangleStripArray(triangleIndices, triangleLengths);

        return new Mesh(planeVertexData, planeTriangles, new Appearance());
    }
    /**
     * Apply rotation to camera and set new field of view
     */
    private void setCamera() {
        cameraTransform.setIdentity();
        cameraTransform.postRotate((float)Math.toDegrees(rotV), (float) Math.cos(rot), 0, (float) Math.sin(rot));
        cameraTransform.postRotate(-(float)Math.toDegrees(rot), 0, 1, 0);

        invertedCameraTransform = new Transform(cameraTransform);
        invertedCameraTransform.invert();

        camera.setPerspective(fov, aspectRatio, near, far);
        //DebugOutput.store("Ratio "+aspectRatio);
        graphics3D.setCamera(camera, cameraTransform);
    }

    /**
     * Update the sky objects spatial coordinate
     */
    private void project3D() {
        int index;
        float scale = Short.MAX_VALUE;
        double x, y, z;

        // ============================
        // === Planets & Sun & Moon ===
        // ============================
        genericVertexBuffer = planetMesh.getVertexBuffer();
        genericVertexPositions = genericVertexBuffer.getPositions(null);
        // Planets
        index = 0;
        for (int i = 0; i < Sky.NB_OF_PLANETS; i++) {
            x = Math.cos(mySky.getPlanet(i).getAzimuth()) * Math.cos(mySky.getPlanet(i).getHeight());
            y = Math.sin(mySky.getPlanet(i).getHeight());
            z = Math.sin(mySky.getPlanet(i).getAzimuth()) * Math.cos(mySky.getPlanet(i).getHeight());
            positionsPlanets[index++] = (short) (scale * x);
            positionsPlanets[index++] = (short) (scale * y);
            positionsPlanets[index++] = (short) (scale * z);
        }
        // Sun
        x = Math.cos(mySky.getSun().getAzimuth()) * Math.cos(mySky.getSun().getHeight());
        y = Math.sin(mySky.getSun().getHeight());
        z = Math.sin(mySky.getSun().getAzimuth()) * Math.cos(mySky.getSun().getHeight());
        positionsPlanets[index++] = (short) (scale * x);
        positionsPlanets[index++] = (short) (scale * y);
        positionsPlanets[index++] = (short) (scale * z);
        // Moon
        x = Math.cos(mySky.getMoon().getAzimuth()) * Math.cos(mySky.getMoon().getHeight());
        y = Math.sin(mySky.getMoon().getHeight());
        z = Math.sin(mySky.getMoon().getAzimuth()) * Math.cos(mySky.getMoon().getHeight());
        positionsPlanets[index++] = (short) (scale * x);
        positionsPlanets[index++] = (short) (scale * y);
        positionsPlanets[index++] = (short) (scale * z);

        genericVertexPositions.set(0, positionsPlanets.length / 3, positionsPlanets);
        genericVertexBuffer.setPositions(genericVertexPositions, 1, null);

        // ===============
        // === Stars ===
        // ===============
        genericVertexBuffer = starMesh.getVertexBuffer();
        genericVertexPositions = genericVertexBuffer.getPositions(null);

        index = 0;
        for (int i = 0; i < mySky.getStarsProj().length; i++) {
            x = Math.cos(mySky.getStar(i).getAzimuth()) * Math.cos(mySky.getStar(i).getHeight());
            y = Math.sin(mySky.getStar(i).getHeight());
            z = Math.sin(mySky.getStar(i).getAzimuth()) * Math.cos(mySky.getStar(i).getHeight());
            positionsStars[index++] = (short) (scale * x);
            positionsStars[index++] = (short) (scale * y);
            positionsStars[index++] = (short) (scale * z);
        }
        genericVertexPositions.set(0, positionsStars.length / 3, positionsStars);
        genericVertexBuffer.setPositions(genericVertexPositions, 1, null);

        // ================
        // === Messiers ===
        // ================
        genericVertexBuffer = messierMesh.getVertexBuffer();
        genericVertexPositions = genericVertexBuffer.getPositions(null);

        index = 0;
        for (int i = 0; i < MessierCatalog.getNumberOfObjects(); i++) {
            x = Math.cos(mySky.getMessier(i).getAzimuth()) * Math.cos(mySky.getMessier(i).getHeight());
            y = Math.sin(mySky.getMessier(i).getHeight());
            z = Math.sin(mySky.getMessier(i).getAzimuth()) * Math.cos(mySky.getMessier(i).getHeight());
            positionsMessiers[index++] = (short) (scale * x);
            positionsMessiers[index++] = (short) (scale * y);
            positionsMessiers[index++] = (short) (scale * z);
        }
        genericVertexPositions.set(0, positionsMessiers.length / 3, positionsMessiers);
        genericVertexBuffer.setPositions(genericVertexPositions, 1, null);

    }

    /**
     * Project on display the sky objects
     */
    private void project2D() {
        int numVertices;
        int i;
        Transform transform = new Transform();
        VertexArray vertexArray3D;
        float w;

        // ============================
        // === Planets & Sun & Moon ===
        // ============================
        vertexArray3D = planetMesh.getVertexBuffer().getPositions(null);

        camera.getProjection(transform);

        transform.postMultiply(invertedCameraTransform);

        transform.transform(vertexArray3D, coord2DPlanets, true);
        // Planets
        for (i = 0; i < Sky.NB_OF_PLANETS; i++) {
            w = 1.0f/coord2DPlanets[i * 4 + 3];
            coord2DPlanets[i * 4 + 0] *= w;
            coord2DPlanets[i * 4 + 1] *= w;

            screenCoordPlanets[i].x = (short) ((coord2DPlanets[i * 4 + 0] * getWidth + getWidth) / 2);
            screenCoordPlanets[i].y = (short) ((-coord2DPlanets[i * 4 + 1] * getHeight + getHeight) / 2);
            if (coord2DPlanets[i * 4 + 2] <= 1 || mySky.getPlanet(i).getHeight() < 0) {
                screenCoordPlanets[i].setVisible(false);
            } else {
                screenCoordPlanets[i].setVisible(true);
            }
        }
        // Sun
        w = 1.0f/coord2DPlanets[i * 4 + 3];
        coord2DPlanets[i * 4 + 0] *= w;
        coord2DPlanets[i * 4 + 1] *= w;

        screenCoordPlanets[i].x = (short) ((coord2DPlanets[i * 4 + 0] * getWidth + getWidth) / 2);
        screenCoordPlanets[i].y = (short) ((-coord2DPlanets[i * 4 + 1] * getHeight + getHeight) / 2);
        if (coord2DPlanets[i * 4 + 2] <= 1 || mySky.getSun().getHeight() < 0) {
            screenCoordPlanets[i].setVisible(false);
        } else {
            screenCoordPlanets[i].setVisible(true);
        }
        // Moon
        i += 1;
        w = 1.0f/coord2DPlanets[i * 4 + 3];
        coord2DPlanets[i * 4 + 0] *= w;
        coord2DPlanets[i * 4 + 1] *= w;

        screenCoordPlanets[i].x = (short) ((coord2DPlanets[i * 4 + 0] * getWidth + getWidth) / 2);
        screenCoordPlanets[i].y = (short) ((-coord2DPlanets[i * 4 + 1] * getHeight + getHeight) / 2);
        if (coord2DPlanets[i * 4 + 2] <= 1 || mySky.getMoon().getHeight() < 0) {
            screenCoordPlanets[i].setVisible(false);
        } else {
            screenCoordPlanets[i].setVisible(true);
        }
        // =============
        // === Stars ===
        // =============
        numVertices = starMesh.getVertexBuffer().getVertexCount();
        vertexArray3D = starMesh.getVertexBuffer().getPositions(null);

        camera.getProjection(transform);

        transform.postMultiply(invertedCameraTransform);

        transform.transform(vertexArray3D, coord2DStars, true);

        for (i = 0; i < numVertices; i++) {
            w = 1.0f/coord2DStars[i * 4 + 3];
            coord2DStars[i * 4 + 0] *= w;
            coord2DStars[i * 4 + 1] *= w;

            screenCoordStars[i].x = (short) ((coord2DStars[i * 4 + 0] * getWidth + getWidth) / 2);
            screenCoordStars[i].y = (short) ((-coord2DStars[i * 4 + 1] * getHeight + getHeight) / 2);
            if (coord2DStars[i * 4 + 2] <= 1 || mySky.getStar(i).getHeight() < 0) {
                screenCoordStars[i].setVisible(false);
            } else {
                screenCoordStars[i].setVisible(true);
            }
        }

        // ================
        // === Messiers ===
        // ================
        numVertices = messierMesh.getVertexBuffer().getVertexCount();
        vertexArray3D = messierMesh.getVertexBuffer().getPositions(null);

        camera.getProjection(transform);

        transform.postMultiply(invertedCameraTransform);

        transform.transform(vertexArray3D, coord2DMessiers, true);

        for (i = 0; i < numVertices; i++) {
            w = 1.0f/coord2DMessiers[i * 4 + 3];
            coord2DMessiers[i * 4 + 0] *= w;
            coord2DMessiers[i * 4 + 1] *= w;

            screenCoordMessiers[i].x = (short) ((coord2DMessiers[i * 4 + 0] * getWidth + getWidth) / 2);
            screenCoordMessiers[i].y = (short) ((-coord2DMessiers[i * 4 + 1] * getHeight + getHeight) / 2);
            if (coord2DMessiers[i * 4 + 2] <= 1 || mySky.getMessier(i).getHeight() < 0) {
                screenCoordMessiers[i].setVisible(false);
            } else {
                screenCoordMessiers[i].setVisible(true);
            }
        }

    }
    /**
     * Go left  by some degrees
     */
    public void left() {
        rot -= Math.toRadians(5f/70f)*fov;
        if (rot<0)
            rot += 2*Math.PI;
        setCamera();
    }
    /**
     * Go right by some degrees
     */
    public void right() {
        rot += Math.toRadians(5f/70f)*fov;
        if (rot>2*Math.PI)
            rot -= 2*Math.PI;
        setCamera();
    }
    /**
     * Go up by some degrees
     */
    public void up() {
        rotV += Math.toRadians(5f/70f)*fov;
        if (rotV>2*Math.PI)
            rotV -= 2*Math.PI;
        setCamera();
    }
    /**
     * Go down by some degrees
     */
    public void down() {
        rotV -= Math.toRadians(5f/70f)*fov;
        if (rotV<0)
            rotV += 2*Math.PI;
        setCamera();
    }
    /**
     * Scroll horizontally
     * @param val
     */
    public void scrollHor(float val) {
        rot -= Math.toRadians(val/70f)*fov;
        if (rot<0)
            rot += 2*Math.PI;
        if (rot>2*Math.PI)
            rot -= 2*Math.PI;
        setCamera();
    }

    /**
     * Scroll vertically
     * @param val
     */
    public void scrollVer(float val) {
        rotV += Math.toRadians(val/70f)*fov;
        if (rotV<0)
            rotV += 2*Math.PI;
        if (rotV>2*Math.PI)
            rotV -= 2*Math.PI;
        setCamera();
    }
    /**
     * Increase the field of vision by 10 degrees
     */
    public void decZoom() {
        fov += 10;
        if (fov >= 180) {
            fov = 179;
        }
        setCamera();
    }
    /**
     * Decrease the field of vision by 10 degrees
     */
    public void incZoom() {
        fov -= 10;
        if (fov < 1) {
            fov = 1;
        }
        setCamera();
    }
    /**
     * Return the zoom value
     * @return the zoom value
     */
    public float getZoom() {
        float tmp = 70/fov;
        if  (tmp<1F)
            tmp = 1;
        return tmp;
    }
    /**
     * Set a value to the zoom
     * @param zoom the new value of the zoom
     */
    public void setZoom(float zoom) {
        fov = zoom;
    }
    /**
     * Return the vertical rotation in radian
     * @return the rotV angle
     */
    public float getRotV() {
        return rotV;
    }
    /**
     * Return the reference to the screen coordinates of the Messier objects
     * @return the screenCoordMessier
     */
    public ScreenCoord[] getScreenCoordMessier() {
        return screenCoordMessiers;
    }
    /**
     * Return the reference to the screen coordinates of the stars
     * @return the screenCoordStars
     */
    public ScreenCoord[] getScreenCoordStars() {
        return screenCoordStars;
    }
    /**
     * Return the reference to the screen coordinates of the planets
     * @return the screenCoordPlanets
     */
    public ScreenCoord[] getScreenCoordPlanets() {
        return screenCoordPlanets;
    }
    /**
     * Return the reference to the screen coordinates of the Moon
     * @return the screenCoordMoon
     */
    public ScreenCoord getScreenCoordMoon() {
        return screenCoordPlanets[MOON];
    }
    /**
     * Return the reference to the screen coordinates of the Sun
     * @return the screenCoordSun
     */
    public ScreenCoord getScreenCoordSun() {
        return screenCoordPlanets[SUN];
    }
    /**
     *
     * @param mesh
     */
    public static void addRandomColors(Mesh mesh) {
        Random random = new Random();
        int vertexCount = mesh.getVertexBuffer().getVertexCount();
        byte[] colors = new byte[vertexCount * 3];

        for (int i = 0; i < colors.length; i++) {
            if (i%3 == 0)
                colors[i] = (byte) 200;
            else
                colors[i] = (byte) random.nextInt(256);
        }

        VertexArray vertexColors = new VertexArray(vertexCount, 3, 1);
        vertexColors.set(0, vertexCount, colors);
        mesh.getVertexBuffer().setColors(vertexColors);
    }
    /**
     * Project all the objects of the sky on the display
     */
    public void project() {
        project3D();
        project2D();
    }
    /**
     * Draw the horizon
     * @param g The graphic object on which to draw
     */
    public void drawHorizon(Graphics g) {
        graphics3D.bindTarget(g,true,Graphics3D.DITHER | Graphics3D.TRUE_COLOR);
        graphics3D.setViewport(0, 0, getWidth, getHeight);
//        DebugOutput.storeOnce("Render: "+getWidth+"/"+getHeight);
        graphics3D.clear(null);
        graphics3D.render(horizon, null);
        graphics3D.releaseTarget();

    }
    /**
     * Set the new dimension of the view (a view size differs from the display size
     * in order to avoid deformation of the view)
     * @param w the width of the display
     * @param h the height of the display
     */
    public void setView(int w,int h) {
        getHeight = h;
        getWidth = w;
        aspectRatio = (float)getWidth/(float)getHeight;

        if (camera != null)
            setCamera();
//        DebugOutput.store("Setview: "+getWidth+"/"+getHeight);
    }
    /**
     * Indicate that this projection is a 3D projection
     * @return true
     */
    public boolean is3D() {
        return true;
    }
}
