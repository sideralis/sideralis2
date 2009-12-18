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
 *
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


    /** Global variable (to avoid multiple new) */
    private VertexBuffer genericVertexBuffer;
    private VertexArray genericVertexPositions;

    private int SUN = Sky.NB_OF_PLANETS;
    private int MOON = SUN + 1;
    /**
     * 
     * @param mySky
     * @param hD
     * @param wD
     */
    public EyeProj(Sideralis myMidlet, int hD, int wD) {
        super(myMidlet, hD, wD);
    }
    /**
     *
     */
    public void init() {
        zoom = 70;
        near = 1;
        far = 1000;
        rotV = 20;

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


        graphics3D = Graphics3D.getInstance();

        // Create a sky
        createSky();

        // Create an horizon
        horizon = createHorizon();
        addRandomColors(horizon);
        PolygonMode polygonMode = new PolygonMode();
        polygonMode.setPerspectiveCorrectionEnable(true);
        polygonMode.setCulling(PolygonMode.CULL_NONE);
        horizon.getAppearance(0).setPolygonMode(polygonMode);

        // Create a camera
        camera = new Camera();
        aspectRatio = 1f;
        cameraTransform = new Transform();
        invertedCameraTransform = new Transform();
        setCamera();

    }

    /**
     * Create the sky
     */
    public void createSky() {
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
        final short scale = Short.MAX_VALUE;
        short dim = 127;
        int height = 128;
        short heightOffset = 0;
        final short step = (short)(2*scale/dim);

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
            y = (short) (heightOffset-random.nextInt(height));
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
                y = (short) (heightOffset-random.nextInt(height));
                x = (short) (x + step);
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
    public void setCamera() {
        cameraTransform.setIdentity();
        cameraTransform.postRotate(rotV, (float) Math.cos(Math.toRadians(rot)), 0, (float) Math.sin(Math.toRadians(rot)));
        cameraTransform.postRotate(-rot, 0, 1, 0);

        invertedCameraTransform = new Transform(cameraTransform);
        invertedCameraTransform.invert();

        camera.setPerspective(zoom, aspectRatio, near, far);
        graphics3D.setCamera(camera, cameraTransform);
    }

    /**
     * Update the sky objects spatial coordinate
     */
    public void project3D() {
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
    public void project2D() {
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
            if (coord2DPlanets[i * 4 + 2] <= 0 || mySky.getPlanet(i).getHeight() < 0) {
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
        if (coord2DPlanets[i * 4 + 2] <= 0 || mySky.getSun().getHeight() < 0) {
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
        if (coord2DPlanets[i * 4 + 2] <= 0 || mySky.getMoon().getHeight() < 0) {
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
            if (coord2DStars[i * 4 + 2] <= 0 || mySky.getStar(i).getHeight() < 0) {
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
            if (coord2DMessiers[i * 4 + 2] <= 0 || mySky.getMessier(i).getHeight() < 0) {
                screenCoordMessiers[i].setVisible(false);
            } else {
                screenCoordMessiers[i].setVisible(true);
            }
        }

    }

    public void left() {
        rot -= 5;
        rot %= 360;
        setCamera();
    }

    public void right() {
        rot += 5;
        rot %= 360;
        setCamera();
    }

    public void up() {
        rotV += 5;
        rotV %= 360;
        setCamera();
    }

    public void down() {
        rotV -= 5;
        rotV %= 360;
        setCamera();
    }

    public void scrollHor(float val) {
    }

    public void scrollVer(float val) {
    }

    public void incZoom() {
        zoom += 10;
        if (zoom >= 180) {
            zoom = 179;
        }
        setCamera();
    }

    public void decZoom() {
        zoom -= 10;
        if (zoom < 1) {
            zoom = 1;
        }
        setCamera();
    }

    public ScreenCoord[] getScreenCoordMessier() {
        return screenCoordMessiers;
    }

    public ScreenCoord[] getScreenCoordStars() {
        return screenCoordStars;
    }

    public ScreenCoord[] getScreenCoordPlanets() {
        return screenCoordPlanets;
    }

    public ScreenCoord getScreenCoordMoon() {
        return screenCoordPlanets[MOON];
    }

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
            colors[i] = (byte) random.nextInt(256);
        }

        VertexArray vertexColors = new VertexArray(vertexCount, 3, 1);
        vertexColors.set(0, vertexCount, colors);
        mesh.getVertexBuffer().setColors(vertexColors);
    }

    public void project() {
        project3D();
        project2D();
    }

    public void drawHorizon(Graphics g) {
        graphics3D.bindTarget(g);
        graphics3D.setViewport(0, 0, getWidth, getHeight);
        graphics3D.clear(null);
        graphics3D.render(horizon, null);
        graphics3D.releaseTarget();
    }
    /**
     * Set the new dimension of the view (a view size differs from the display size
     * in order to avoid deformation of the view)
     */
    public void setView() {
        getHeight = heightDisplay;
        getWidth = widthDisplay;
    }

}
