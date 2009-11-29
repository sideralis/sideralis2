/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.dox.sideralis.view;

import fr.dox.sideralis.Sideralis;
import fr.dox.sideralis.data.Sky;
import java.util.Random;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.m3g.Appearance;
import javax.microedition.m3g.Camera;
import javax.microedition.m3g.Graphics3D;
import javax.microedition.m3g.Group;
import javax.microedition.m3g.Mesh;
import javax.microedition.m3g.PolygonMode;
import javax.microedition.m3g.TriangleStripArray;
import javax.microedition.m3g.VertexArray;
import javax.microedition.m3g.VertexBuffer;
import javax.microedition.m3g.World;

/**
 *
 * @author Bernard
 */
public class Sideralis3DCanvas extends GameCanvas implements Runnable {
    /** The calling midlet */
    private final Sideralis myMidlet;
    /** Width and height of the display */
    private int getHeight,getWidth;
    /** Flag for stopping the animation thread.*/
    private boolean running;
    /** Counter used to decide when to recalculate all positons */
    private int counter;
    /** The sky object */
    private final Sky mySky;

    /** Object that represents the 3D world. */
    private World world;
    /** Planet meshes. */
    private Mesh[] meshPlanets;
    /** Sun mesh */
    private Mesh meshSun;
    /** Group of planets */
    private Group group;
    /** 3D graphics singleton used for rendering. */
    private Graphics3D graphics3d;
    /** 2D graphics singleton used for rendering. */
    private Graphics graphics;
    /** Zoom */
    private float zoom,zoomInc;


    /** All objects positions are recalculated every COUNTER calls to run() */
    private static final int COUNTER = 1000;
    /** Number of frames per second */
    private static final int MAX_CPS = 10;
    /** Time in millisecond between 2 frames */
    private static final int MS_PER_FRAME = 1000 / MAX_CPS;


    public Sideralis3DCanvas(Sideralis myMidlet) {
        super(false);
        running = false;
        this.myMidlet = myMidlet;
        mySky = myMidlet.getMySky();
    }

    public void init() {
        graphics3d = Graphics3D.getInstance();
        graphics = getGraphics();

        zoom = 1;
        zoomInc = 0;

        // Create a world
        world = new World();

        // Create a camera
        Camera camera = new Camera();
        float aspect = (float) getWidth() / (float) getHeight();
        camera.setPerspective(90, aspect, 1, 1000);
        camera.setTranslation(0,0,150);
        world.addChild(camera);
        world.setActiveCamera(camera);

        // Create a group
        group = new Group();
        world.addChild(group);

        // Create the planets objects
        meshPlanets = new Mesh[Sky.NB_OF_PLANETS];
        for (int i=0;i<Sky.NB_OF_PLANETS;i++) {
            meshPlanets[i] = createSphere(8, 8, true, true);
            addRandomColors(meshPlanets[i]);
            PolygonMode polygonMode = new PolygonMode();
            polygonMode.setShading(PolygonMode.SHADE_FLAT);
            meshPlanets[i].getAppearance(0).setPolygonMode(polygonMode);
            group.addChild(meshPlanets[i]);
        }

        for (int i=0;i<Sky.NB_OF_PLANETS;i++) {
            meshPlanets[i].setTranslation((float)mySky.getPlanet(i).getX(), (float)mySky.getPlanet(i).getY(), (float)mySky.getPlanet(i).getZ());
        }

        meshPlanets[0].setScale(0.5f,0.5f,0.5f);
        meshPlanets[1].setScale(1.5f,1.5f,1.5f);
        meshPlanets[2].setScale(0.75F,0.75F,0.75F);
        meshPlanets[3].setScale(5,5,5);
        meshPlanets[4].setScale(4,4,4);
//        meshPlanets[0].setScale(0.24F, 0.24F, 0.24F);
//        meshPlanets[1].setScale(0.61F, 0.61F, 0.61F);
//        meshPlanets[2].setScale(0.33F, 0.33F, 0.33F);
//        meshPlanets[3].setScale(7.15F, 7.15F, 7.15F);
//        meshPlanets[4].setScale(6.03F, 6.03F, 6.03F);

        // Create the mesh sun
        meshSun = createSphere(8,8,true, true);
        meshSun.setScale(2F,2F,2F);
        group.addChild(meshSun);


//        Transform cameraTransform = new Transform();
//
//        cameraTransform.postTranslate(32, 16, 32);
//        cameraTransform.postRotate(45, 0, 1, 0);
//        cameraTransform.postRotate(-20, 1, 0, 0);

        graphics3d.setCamera(camera, null);


    }

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

            // Move scene
            group.postRotate(2, 0, 0, 1);
            if (zoom<0.5F) {
                zoomInc = 0;
                zoom = 0.5F;
            } else if (zoom >6) {
                zoomInc = 0;
                zoom = 6.0F;
            }
            zoom += zoomInc;
            group.setScale(zoom, zoom, zoom);

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
    protected void render(Graphics graphics) {
        graphics3d.bindTarget(graphics);
        graphics3d.setViewport(0, 0, getWidth, getHeight);
        graphics3d.clear(null);
        graphics3d.render(world);
        graphics3d.releaseTarget();
    }

    protected void keyPressed(int keyCode) {

        if (keyCode == KEY_NUM1) {
            // Zoom out
            zoomInc = -0.1F;
        } else if (keyCode == KEY_NUM3) {
            // Zoom in
            zoomInc = 0.1F;
        } else {
            // Next display
            running = false;
            myMidlet.nextDisplay();
        }
    }


    // ====================================================================================
    /**
     * Creates a sphere that's subdivided with the given number of
     * slices and stacks. The minimum number is two and three for slices
     * and stacks respectively. The sphere has a radius of one unit and
     * the sphere's center is at the origin.
     *
     * @param slices subdivisions along the longitude.
     * @param stacks subdivisions along the latitude.
     * @param addNormals true to generate normals, false otherwise.
     * @param addTexCoords true to generate texture coordinates, false otherwise.
     * @return sphere.
     */
    public static Mesh createSphere(int slices, int stacks, boolean addNormals, boolean addTexCoords) {
        if (slices < 2) {
            throw new IllegalArgumentException("Minimum number of slices is 2.");
        }

        if (stacks < 3) {
            throw new IllegalArgumentException("Minimum number of stacks is 3.");
        }

        ////
        ////  Vertex positions.
        ////
        double deltaLongitude = 2 * Math.PI / slices;
        double deltaLatitude = Math.PI / (stacks - 1);
        float scale = Short.MAX_VALUE;              // because radius == 1
        short[] positions = new short[(slices + 1) * stacks * 3];

        for (int i = 0; i <= slices; i++) {
            double longitude = i * deltaLongitude;
            double clong = Math.cos(longitude);
            double slong = Math.sin(longitude);

            for (int j = 0; j < stacks; j++) {
                double colatitude = j * deltaLatitude;
                double scolat = Math.sin(colatitude);
                double ccolat = Math.cos(colatitude);

                double x = clong * scolat;  // cos(longitude)*sin(colatitude)
                double y = ccolat;          // cos(colatitude)
                double z = -slong * scolat; // -sin(longitude)*sin(colatitude)

                // Convert to short using scale.
                int index = (i * stacks + j) * 3;
                positions[index + 0] = (short) (scale * x);
                positions[index + 1] = (short) (scale * y);
                positions[index + 2] = (short) (scale * z);
            }
        }

        VertexBuffer vertexBuffer = new VertexBuffer();
        VertexArray vertexPositions = new VertexArray(positions.length / 3, 3, 2);
        vertexPositions.set(0, positions.length / 3, positions);
        vertexBuffer.setPositions(vertexPositions, 1.0f / scale, null);

        ////
        ////  Triangles.
        ////
        int indicesPerSlice = 2 * stacks;
        int[] triangleIndices = new int[indicesPerSlice * slices];
        for (int i = 0; i < slices; i++) {
            int index = i * indicesPerSlice;
            int pos = i * stacks;

            for (int j = 0; j < stacks; j++) {
                triangleIndices[index + j * 2 + 0] = pos + stacks + j;
                triangleIndices[index + j * 2 + 1] = pos + j;
            }
        }

        int[] triangleLengths = new int[slices];
        for (int i = 0; i < triangleLengths.length; i++) {
            triangleLengths[i] = indicesPerSlice;
        }

        TriangleStripArray triangles = new TriangleStripArray(triangleIndices, triangleLengths);

        ////
        ////  Normals.
        ////
        if (addNormals) {
            vertexBuffer.setNormals(vertexPositions);
        }

        ////
        ////  Texture coordinates.
        ////
        if (addTexCoords) {
            scale = Short.MAX_VALUE;
            short[] texCoords = new short[positions.length / 3 * 2];
            deltaLongitude = 1.0 / slices;
            deltaLatitude = 1.0 / (stacks - 1);

            for (int i = 0; i <= slices; i++) {
                double longitude = i * deltaLongitude;

                for (int j = 0; j < stacks; j++) {
                    double latitude = j * deltaLatitude;
                    int indexPosition = (i * stacks + j) * 2;

                    texCoords[indexPosition + 0] = (short) (scale * longitude);
                    texCoords[indexPosition + 1] = (short) (scale * latitude);
                }
            }

            VertexArray vertexTexCoords = new VertexArray(texCoords.length / 2, 2, 2);
            vertexTexCoords.set(0, texCoords.length / 2, texCoords);
            vertexBuffer.setTexCoords(0, vertexTexCoords, 1.0f / scale, null);
        }

        return new Mesh(vertexBuffer, triangles, new Appearance());
    }

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

    /**
     * Creates a cube with one vertex per corner. The edge length is
     * two units, the center of the cube is at the origin.
     *
     *
     * @return cube mesh.
     */
    public static Mesh createMinimalCube() {
        byte[] positions = {
            -1, -1, 1, 1, -1, 1, -1, 1, 1, 1, 1, 1,
            -1, -1, -1, 1, -1, -1, -1, 1, -1, 1, 1, -1
        };

        int[] triangleIndices = {
            0, 1, 2, 3, 7, 1, 5, 4, 7, 6, 2, 4, 0, 1
        };

        int[] triangleLengths = {triangleIndices.length};
        VertexBuffer vertexBuffer = new VertexBuffer();

        VertexArray vertexPositions = new VertexArray(positions.length / 3, 3, 1);
        vertexPositions.set(0, positions.length / 3, positions);
        vertexBuffer.setPositions(vertexPositions, 1, null);

        TriangleStripArray triangles = new TriangleStripArray(
                triangleIndices, triangleLengths);

        return new Mesh(vertexBuffer, triangles, new Appearance());
    }
    /**
     * Called when the drawable area of the Canvas has been changed
     * @param w the new width in pixels of the drawable area of the Canvas
     * @param h w - the new width in pixels of the drawable area of the Canvas
     */
    protected void sizeChanged(int w, int h) {
        getHeight = h;
        getWidth = w;
        // TODO set viewport
        Camera camera = world.getActiveCamera();
        float aspect = (float) getWidth / (float) getHeight;
        camera.setPerspective(90, aspect, 1, 1000);
        world.setActiveCamera(camera);
        graphics3d.setCamera(camera, null);
    }
}
