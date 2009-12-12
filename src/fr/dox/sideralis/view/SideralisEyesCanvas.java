/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.dox.sideralis.view;

import fr.dox.sideralis.ConfigParameters;
import fr.dox.sideralis.LocalizationSupport;
import fr.dox.sideralis.Sideralis;
import fr.dox.sideralis.data.ConstellationCatalog;
import fr.dox.sideralis.data.Sky;
import fr.dox.sideralis.object.ConstellationObject;
import fr.dox.sideralis.object.ScreenCoord;
import fr.dox.sideralis.object.SkyObject;
import fr.dox.sideralis.projection.sphere.MoonProj;
import fr.dox.sideralis.view.color.Color;
import java.util.Random;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.m3g.Appearance;
import javax.microedition.m3g.Camera;
import javax.microedition.m3g.Graphics3D;
import javax.microedition.m3g.Image2D;
import javax.microedition.m3g.Loader;
import javax.microedition.m3g.Mesh;
import javax.microedition.m3g.PolygonMode;
import javax.microedition.m3g.Texture2D;
import javax.microedition.m3g.Transform;
import javax.microedition.m3g.TriangleStripArray;
import javax.microedition.m3g.VertexArray;
import javax.microedition.m3g.VertexBuffer;

/**
 *
 * @author Bernard
 */
public class SideralisEyesCanvas extends GameCanvas implements Runnable {

    /** The calling midlet */
    private final Sideralis myMidlet;
    /** Width and height of the display */
    private int getHeight, getWidth;
    /** Flag for stopping the animation thread.*/
    private boolean running;
    /** Counter used to decide when to recalculate all positons */
    private int counter;
    /** The sky object */
    private final Sky mySky;
    /** 2D graphics singleton used for rendering. */
    private Graphics graphics;
    /** 3D graphics singleton used for rendering. */
    private Graphics3D graphics3D;
    /** The sky */
    private Mesh planetMesh,starMesh;
    /** The horizon */
    private Mesh horizon;
    /** The texture of Horizon */
    private Texture2D texture;
    /** The camera */
    private Camera camera;
    /** The Camera Transform */
    private Transform cameraTransform, invertedCameraTransform;
    /** The rotations */
    private float rotV, rot;
    /** The fov */
    private float fov;
    /** Viewport aspect ration */
    private float aspectRatio;
    /** The screen coordinate of the object, map to display*/
    private ScreenCoord[] screenCoordPlanets,screenCoordStars;
    /** The screen coordinate of the objects, just after transform */
    private float[] coord2DPlanets,coord2DStars;
    /** Positions of object */
    private short[] positionsPlanets, positionsStars;
    /** Global variable (to avoid multiple new) */
    private VertexBuffer genericVertexBuffer;
    private VertexArray genericVertexPositions;
    private float[] scaleBias;

    private final Font myFont;
    private int idxClosestConst;
    private int idxClosestObject;
    private int typeClosestObject;
    private int colorClosestConst;

    private float near,far;


    /** All objects positions are recalculated every COUNTER calls to run() */
    private static final int COUNTER = 1000;
    /** Number of frames per second */
    private static final int MAX_CPS = 10;
    /** Time in millisecond between 2 frames */
    private static final int MS_PER_FRAME = 1000 / MAX_CPS;

    private int SUN = Sky.NB_OF_PLANETS;
    private int MOON = SUN + 1;

    /**
     * Constructor
     * @param myMidlet the calling midlet
     */
    public SideralisEyesCanvas(Sideralis myMidlet) {
        super(false);
        this.myMidlet = myMidlet;
        mySky = myMidlet.getMySky();
        myFont = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL);
    }

    /**
     * Init of all main parameters
     */
    public void init() {
        running = false;

        fov = 70;
        near = 1;
        far = 1000;
        rotV = 20;

        graphics3D = Graphics3D.getInstance();
        graphics = getGraphics();

        // Load textures
        try {
            Image2D image2D = (Image2D) Loader.load("/checkerboard_4x4_256.png")[0];
            texture = new Texture2D(image2D);
            texture.setBlending(Texture2D.FUNC_REPLACE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create 3D intermediate variables (to avoid multiple use of new)
        positionsPlanets = new short[(Sky.NB_OF_PLANETS +2) * 3];               // 2 for Sun and Moon
        positionsStars = new short[mySky.getStarsProj().length * 3];
        scaleBias = new float[4];

        // Create a sky
        createSky();

        // Create an horizon
        horizon = createHorizon();
//        addRandomColors(horizon);
        horizon.getVertexBuffer().setDefaultColor(0x000000ff);
        
//        horizon.getAppearance(0).setTexture(0, texture);
        PolygonMode polygonMode = new PolygonMode();
        polygonMode.setPerspectiveCorrectionEnable(true);
        polygonMode.setCulling(PolygonMode.CULL_NONE);
//        texture.setWrapping(Texture2D.WRAP_REPEAT, Texture2D.WRAP_REPEAT);
        horizon.getAppearance(0).setPolygonMode(polygonMode);

        // Create a camera
        camera = new Camera();
        aspectRatio = (float) getWidth() / (float) getHeight();
        cameraTransform = new Transform();
        invertedCameraTransform = new Transform();
        setCamera();

        // Create
        coord2DPlanets = new float[(Sky.NB_OF_PLANETS+2) * 4];                  // 2 for Sun and Moon
        coord2DStars = new float[mySky.getStarsProj().length * 4];

        // Create coordinate object for all objects in the sky (planets & Sun & Moon)
        screenCoordPlanets = new ScreenCoord[Sky.NB_OF_PLANETS + 2];
        for (int i = 0; i < screenCoordPlanets.length; i++) {
            screenCoordPlanets[i] = new ScreenCoord();
        }

        screenCoordStars = new ScreenCoord[mySky.getStarsProj().length];
        for (int k = 0; k < screenCoordStars.length; k++)
            screenCoordStars[k] = new ScreenCoord();

    }

    /**
     * Called when display is shown
     * Used to start the thread if not already running
     */
    protected void showNotify() {
        super.showNotify();
        if (running == false) {
            running = true;
            new Thread(this).start();
        }
    }

    /**
     * Destroys the sample.
     */
    public void destroy() {
        running = false;
    }

    /**
     * Drives the animation.
     */
    public void run() {
        long cycleStartTime;

        getHeight = getHeight();                                                // Due to bug of 6680 and 6630
        getWidth = getWidth();                                                  // Due to bug of 6680 and 6630
        while (running) {
            cycleStartTime = System.currentTimeMillis();
            // Check if we need to calculate position
            if (counter == 0) {
                // Yes, do it in a separate thread
                new Thread(mySky).start();
                //System.out.println("Start Sky Thread");
                counter = COUNTER;
            } else {
                // No, decrease counter
                counter--;
            }

            // Display scene.
            render(graphics);
            flushGraphics();

            /* Thread is set to sleep if it remains some time before next frame */
            long timeSinceStart = (System.currentTimeMillis() - cycleStartTime);
            if (timeSinceStart < MS_PER_FRAME) {
                try {
                    Thread.sleep(MS_PER_FRAME - timeSinceStart);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Renders the sample.
     *
     * @param graphics graphics context for rendering.
     */
    protected void render(Graphics g) {
        // =======================
        // === Display 3D part ===
        // =======================
        graphics3D.bindTarget(graphics);
        graphics3D.setViewport(0, 0, getWidth, getHeight);
        graphics3D.clear(null);
        graphics3D.render(horizon, null);
        graphics3D.releaseTarget();

        if (mySky.isCalculationDone()) {
            mySky.setCalculationDone(false);
            mySky.setProgress(0);
            project3D();
            project2D();
        } else {
            // =======================
            // === Display 2D part ===
            // =======================
            // ----------------------------------
            // ----- Display constellations -----
            drawConstellations(g);

            // --------------------------------
            // -----   Display all stars  -----
            drawStars(g);

            // --------------------------------------
            // ------ Draw system solar objects -----
            drawSystemSolarObjects(g);

            int[] color = myMidlet.getMyParameter().getColor();
            g.setColor(color[Color.COL_N_S_E_O]);
            g.drawString(""+rot+"/"+rotV, getWidth/2, getHeight-2, Graphics.BASELINE|Graphics.HCENTER);
        }
    }
    /**
     * Create the horizon
     * @return the mesh representing the horizon
     */
    public Mesh createHorizon() {
        final float scale = Short.MAX_VALUE;
        short height = -50;
        int slices = 16;
        double angle = 2 * Math.PI / slices;
        short[] positions = new short[(slices + 1) * 3];
        int[] triangleIndices = new int[slices * 3];
        int[] triangleLengths = new int[slices];
//        short[] texCoords = new short[(slices + 1) * 2];

        positions[slices * 3 + 0] = 0;
        positions[slices * 3 + 1] = height;
        positions[slices * 3 + 2] = 0;

        for (int i = 0; i < slices; i++) {
            positions[i * 3 + 0] = (short) (scale * Math.cos(-i * angle));
            positions[i * 3 + 1] = height;
            positions[i * 3 + 2] = (short) (scale * Math.sin(-i * angle));

            triangleIndices[i * 3 + 0] = slices;
            triangleIndices[i * 3 + 1] = i;
            triangleIndices[i * 3 + 2] = (i + 1) % slices;
            triangleLengths[i] = 3;

//            texCoords[i * 2 + 0] = (short) (scale * i / slices);
//            texCoords[i * 2 + 1] = (short) (0);
        }
//        texCoords[slices * 2 + 0] = (short) (scale / 2);
//        texCoords[slices * 2 + 1] = (short) scale;

        VertexBuffer planeVertexData = new VertexBuffer();

        VertexArray vertexPositions = new VertexArray(positions.length / 3, 3, 2);
        vertexPositions.set(0, positions.length / 3, positions);
        planeVertexData.setPositions(vertexPositions, 1, null);

        TriangleStripArray planeTriangles = new TriangleStripArray(triangleIndices, triangleLengths);

//        VertexArray vertexTexCoords = new VertexArray(texCoords.length/2, 2, 2);
//        vertexTexCoords.set(0, texCoords.length/2, texCoords);
//        planeVertexData.setTexCoords(0, vertexTexCoords, 1, null);

        return new Mesh(planeVertexData, planeTriangles, new Appearance());
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
//        transform.postTranslate(scaleBias[1], scaleBias[2], scaleBias[3]);
//        transform.postScale(scaleBias[0], scaleBias[0], scaleBias[0]);

        transform.transform(vertexArray3D, coord2DPlanets, true);
        // Planets
//        float w = 1.0f / Short.MAX_VALUE;
        //float w = 1.0f;
        for (i = 0; i < Sky.NB_OF_PLANETS; i++) {
            w = 1.0f/coord2DPlanets[i * 4 + 3];
            coord2DPlanets[i * 4 + 0] *= w;
            coord2DPlanets[i * 4 + 1] *= w;
            //coord2DPlanets[i * 4 + 2] *= w;

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
        //coord2DPlanets[i * 4 + 2] *= w;

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
        //coord2DPlanets[i * 4 + 2] *= w;

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
//        transform.postTranslate(scaleBias[1], scaleBias[2], scaleBias[3]);
//        transform.postScale(scaleBias[0], scaleBias[0], scaleBias[0]);

        transform.transform(vertexArray3D, coord2DStars, true);

        for (i = 0; i < numVertices; i++) {
            w = 1.0f/coord2DStars[i * 4 + 3];
            coord2DStars[i * 4 + 0] *= w;
            coord2DStars[i * 4 + 1] *= w;
            //coord2DStars[i * 4 + 2] *= w;

            screenCoordStars[i].x = (short) ((coord2DStars[i * 4 + 0] * getWidth + getWidth) / 2);
            screenCoordStars[i].y = (short) ((-coord2DStars[i * 4 + 1] * getHeight + getHeight) / 2);
            if (coord2DStars[i * 4 + 2] <= 0 || mySky.getStar(i).getHeight() < 0) {
                screenCoordStars[i].setVisible(false);
            } else {
                screenCoordStars[i].setVisible(true);
            }
        }
    }
    /**
     * A key is pressed
     * @param keyCode the code of the pressed key
     */
    protected void keyPressed(int keyCode) {
//        if (keyCode == KEY_NUM7) {
//            near -= 0.2f;
//            if (near < 0.1f)
//                near = 0.1f;
//        }
//        if (keyCode == KEY_NUM9) {
//            near += 0.2f;
//        }
        if (keyCode == KEY_NUM7) {
            far -= 50;
            if (far < 50)
                far = 50;
        }
        if (keyCode == KEY_NUM9) {
            far += 50;
        }

        if (keyCode == KEY_NUM3) {
            fov -= 10;
            if (fov < 1) {
                fov = 1;
            }
        }
        if (keyCode == KEY_NUM1) {
            fov += 10;
            if (fov >= 180) {
                fov = 179;
            }
        }
        if (getGameAction(keyCode) == UP) {
            rotV += 5;
        }
        if (getGameAction(keyCode) == DOWN) {
            rotV -= 5;
        }
        if (getGameAction(keyCode) == LEFT) {
            rot -= 5;
        }
        if (getGameAction(keyCode) == RIGHT) {
            rot += 5;
        }
        if (keyCode == KEY_STAR) {
            running = false;
            myMidlet.nextDisplay();
            return;
        }
        rotV %= 360;
        rot %= 360;
//        System.out.println("rot=" + rot + " rotV=" + rotV + " fov="+fov);
        setCamera();
        project3D();
        project2D();
    }
    /**
     * A key is repeated
     * @param keyCode the code of the repeated key
     */
    protected void keyRepeated(int keyCode) {
        keyPressed(keyCode);
    }

    /**
     * Apply rotation to camera and set new field of view
     */
    public void setCamera() {
        cameraTransform.setIdentity();
        cameraTransform.postRotate(rotV, (float) Math.cos(Math.toRadians(rot)), 0, (float) Math.sin(Math.toRadians(rot)));
        cameraTransform.postRotate(-rot, 0, 1, 0);

        //System.out.println("rotX="+(float) Math.cos(Math.toRadians(rot))+" rotY=0 RotZ="+(float) Math.sin(Math.toRadians(rot)));

        invertedCameraTransform = new Transform(cameraTransform);
        invertedCameraTransform.invert();

//        camera.setParallel(fov, aspectRatio, 1, 100);
        camera.setPerspective(fov, aspectRatio, near, far);
        graphics3D.setCamera(camera, cameraTransform);
    }
    /**
     * Draw stars
     * @param g the Graphics object
     */
    private void drawStars(Graphics g) {
        int nbOfStars = mySky.getStarsProj().length;
        boolean isStarColored = myMidlet.getMyParameter().isStarColored();
        boolean isStarShownAsCircle = myMidlet.getMyParameter().isStarShownAsCircle();
        float maxMag = myMidlet.getMyParameter().getMaxMag();
        int[] color = myMidlet.getMyParameter().getColor();

        for (int k = 0; k < nbOfStars; k++) {
            if (screenCoordStars[k].isVisible()) {
                // Star is above horizon
                float magf = mySky.getStar(k).getObject().getMag();
                if (magf < maxMag) {
                    int mag = (int) (magf-0.3F);
                    if (mag > 5) {
                        mag = 5;
                    }
                    if (mag < 0) {
                        mag = 0;
                    // Select color of star
                    }
                    if (isStarColored) {
                        int col = color[Color.COL_STAR_MAG0+mag];
                        g.setColor(col);
                    } else {
                        g.setColor(color[Color.COL_STAR_MAG0]);
                    }
                    // Represent star as a filled circle or as a dot
                    if (isStarShownAsCircle) {
                        // As a circle
                        int size = 5 - mag;
                        if (size > 4) {
                            size = 4;
                        }
                        if (size < 1) {
                            g.drawLine(screenCoordStars[k].x, screenCoordStars[k].y, screenCoordStars[k].x, screenCoordStars[k].y);
                        } else {
                            g.fillArc(screenCoordStars[k].x - size, screenCoordStars[k].y - size, 2 * size, 2 * size, 0, 360);
                        }
                    } else {
                        // Or as a dot
                        g.drawLine(screenCoordStars[k].x, screenCoordStars[k].y, screenCoordStars[k].x, screenCoordStars[k].y);
                    }
                }
            }
        }
    }
    /**
     *
     * @param g
     */
    private void drawSystemSolarObjects(Graphics g) {
        ConfigParameters rMyParam = myMidlet.getMyParameter();
        int[] color = rMyParam.getColor();

        // -------------------------
        // ------ Display moon -----
        if (screenCoordPlanets[MOON].isVisible()) {
            g.setColor(color[Color.COL_MOON]);
            int z = 2;
            if (rMyParam.isPlanetDisplayed()) {
                switch (mySky.getMoon().getPhase()) {
                    case MoonProj.FIRST:
                        g.fillArc((int) screenCoordPlanets[MOON].x - z, (int) screenCoordPlanets[MOON].y - z, 2 * z, 2 * z, -100, 200);
                        break;
                    case MoonProj.LAST:
                        g.fillArc((int) screenCoordPlanets[MOON].x - z, (int) screenCoordPlanets[MOON].y - z, 2 * z, 2 * z, 80, 200);
                        break;
                    case MoonProj.NEW:
                        g.drawArc((int) screenCoordPlanets[MOON].x - z, (int) screenCoordPlanets[MOON].y - z, 2 * z, 2 * z, 0, 360);
                        break;
                    case MoonProj.FULL:
                        g.fillArc((int) screenCoordPlanets[MOON].x - z, (int) screenCoordPlanets[MOON].y - z, 2 * z, 2 * z, 0, 360);
                        break;
                }
            }
            if (rMyParam.isPlanetNameDisplayed()) {
                g.drawString(LocalizationSupport.getMessage("NAME_MOON"), (int) screenCoordPlanets[MOON].x, (int) screenCoordPlanets[MOON].y - myFont.getHeight() - z, Graphics.TOP | Graphics.HCENTER);
            }
        }
        // -------------------------
        // ------ Display sun ------
        if (screenCoordPlanets[SUN].isVisible()) {
            g.setColor(color[Color.COL_SUN]);
            int z = 2;
            if (rMyParam.isPlanetDisplayed()) {
                g.fillArc((int) screenCoordPlanets[SUN].x - z, (int) screenCoordPlanets[SUN].y - z, 2 * z, 2 * z, 0, 360);
            }
            if (rMyParam.isPlanetNameDisplayed()) {
                g.drawString(LocalizationSupport.getMessage("NAME_SUN"), (int) screenCoordPlanets[SUN].x, (int) screenCoordPlanets[SUN].y - myFont.getHeight() - z, Graphics.TOP | Graphics.HCENTER);
            }
        }
        // ---------------------------
        // ------ Display planets -----
        for (int k=0;k<Sky.NB_OF_PLANETS;k++) {
            if (screenCoordPlanets[k].isVisible()) {
                g.setColor(color[(Color.COL_PLANET)+k]);
                if (rMyParam.isPlanetDisplayed()) {
                    g.fillArc((int) screenCoordPlanets[k].x - 2, (int) screenCoordPlanets[k].y - 2, 4, 4, 0, 360);
                }
                if (rMyParam.isPlanetNameDisplayed()) {
                    g.drawString(mySky.getPlanet(k).getObject().getName(), (int) screenCoordPlanets[k].x, (int) screenCoordPlanets[k].y - myFont.getHeight(), Graphics.TOP | Graphics.HCENTER);
                }
            }
        }
    }
    /**
     * Draw constellations
     * @param g2 the Graphics object
     */
    private void drawConstellations(Graphics g) {
        short kStar1 = 0;
        short kStar2 = 0;
        boolean flagInc = false;                                                // To calculate the color of the constellation only one time for all branches of the constellation
        int i,  j;
        ConstellationObject co;
        ConfigParameters rMyParam = myMidlet.getMyParameter();
        int[] color = rMyParam.getColor();


        if (rMyParam.isConstDisplayed() || rMyParam.isConstNamesDisplayed() || rMyParam.isConstNamesLatinDisplayed()) {
            // If constellations should be displayed
            for (i = 0; i < ConstellationCatalog.getNumberOfConstellations(); i++) {
                co = ConstellationCatalog.getConstellation(i);
                // For all constellations
                int col = 0;                                                    // The color of the constellation drawing
                for (j = 0; j < co.getSizeOfConstellation(); j += 2) {
                    kStar1 = co.getIdx(j);               // Get index of stars in constellation
                    kStar2 = co.getIdx(j + 1);             // Get index of stars in constellation
                    // Draw one part of the constellation
                    if (screenCoordStars[kStar1].isVisible() && screenCoordStars[kStar2].isVisible()) {
                        // A line between 2 stars is displayed only if the 2 stars are visible.
                        if (typeClosestObject == SkyObject.STAR && co.getIdxName() == idxClosestConst) {
                            // This constellation is blinking (cursor close to it)
                            if (!flagInc) {
                                // Calculate the color of the constellation. This is done only for the first branch of the constellation.
                                col = colorClosestConst > color[Color.COL_CONST_MAX] / 2 ? color[Color.COL_CONST_MAX] - colorClosestConst : colorClosestConst;
                                colorClosestConst += color[Color.COL_CONST_INC];
                                if (colorClosestConst > color[Color.COL_CONST_MAX]) {
                                    colorClosestConst = 0;
                                }
                                flagInc = true;
                            }
                            g.setColor(col + color[Color.COL_CONST_MIN]);
                        } else {
                            g.setColor(color[Color.COL_CONST_MAX] / 2);
                        }
                        if (rMyParam.isConstDisplayed())
                            g.drawLine(screenCoordStars[kStar1].x, screenCoordStars[kStar1].y, screenCoordStars[kStar2].x, screenCoordStars[kStar2].y);
                    }
                }
                if (rMyParam.isConstNamesDisplayed() || rMyParam.isConstNamesLatinDisplayed()) {
                    g.setColor(color[Color.COL_CONST_NAME_MAX] / 2);
                    // Display the name of the constellation
                    kStar1 = co.getRefStar4ConstellationName();
                    if (!screenCoordStars[kStar1].isVisible()) {
                        for (j = 0; j < co.getSizeOfConstellation(); j += 2) {
                            kStar1 = co.getIdx(j);               // Get index of stars in constellation
                            if (screenCoordStars[kStar1].isVisible()) {
                                break;
                            }
                        }
                    }
                    if (screenCoordStars[kStar1].isVisible()) {
                        if (rMyParam.isConstNamesDisplayed() && rMyParam.isConstNamesLatinDisplayed()) {
                            g.drawString(ConstellationCatalog.getConstellation(i).getName(), screenCoordStars[kStar1].x, screenCoordStars[kStar1].y-myFont.getHeight()/2, Graphics.TOP | Graphics.HCENTER);
                            g.drawString(ConstellationCatalog.getConstellation(i).getLatinName(), screenCoordStars[kStar1].x, screenCoordStars[kStar1].y+myFont.getHeight()/2, Graphics.TOP | Graphics.HCENTER);
                        }
                        else if (rMyParam.isConstNamesDisplayed())
                            g.drawString(ConstellationCatalog.getConstellation(i).getName(), screenCoordStars[kStar1].x, screenCoordStars[kStar1].y, Graphics.TOP | Graphics.HCENTER);
                        else
                            g.drawString(ConstellationCatalog.getConstellation(i).getLatinName(), screenCoordStars[kStar1].x, screenCoordStars[kStar1].y, Graphics.TOP | Graphics.HCENTER);
                    }
                }
            }
        }
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

}
