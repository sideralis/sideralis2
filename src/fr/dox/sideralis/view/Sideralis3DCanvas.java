/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.dox.sideralis.view;

import fr.dox.sideralis.Sideralis;
import fr.dox.sideralis.data.Sky;
import fr.dox.sideralis.object.PlanetObject;
import fr.dox.sideralis.object.ScreenCoord;
import fr.dox.sideralis.view.color.Color;
import java.io.IOException;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.m3g.Appearance;
import javax.microedition.m3g.Camera;
import javax.microedition.m3g.Graphics3D;
import javax.microedition.m3g.Group;
import javax.microedition.m3g.Image2D;
import javax.microedition.m3g.Light;
import javax.microedition.m3g.Material;
import javax.microedition.m3g.Mesh;
import javax.microedition.m3g.PolygonMode;
import javax.microedition.m3g.Sprite3D;
import javax.microedition.m3g.Transform;
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
    /** The camera used to see the world */
    private Camera camera;
    /** Object that represents the 3D world. */
    private World world;
    /** Planet meshes. */
    private Mesh[] meshPlanets;
    /** Sun mesh */
    private Mesh meshSun;
    /** Arrow mesh */
    private Mesh meshArrow;
    /** Group of planets */
    private Group group;
    /** 3D graphics singleton used for rendering. */
    private Graphics3D graphics3d;
    /** 2D graphics singleton used for rendering. */
    private Graphics graphics;
    /** Zoom */
    private float zoom,zoomInc;
    /** The 2D coordinates for planets */
    private float[] coord2DPlanets;
    private ScreenCoord[] screenCoordPlanets;
    /** The index of the planet arrow is pointing to */
    private int indexArrow;

    /** All objects positions are recalculated every COUNTER calls to run() */
    private static final int COUNTER = 1000;
    /** Number of frames per second */
    private static final int MAX_CPS = 10;
    /** Time in millisecond between 2 frames */
    private static final int MS_PER_FRAME = 1000 / MAX_CPS;
    /** Number of slices in the sphere */
    private static final int NB_OF_SLICES = 16;
    /** Number of stacks in the sphere */
    private static final int NB_OF_STACKS = 16;

    private Image2D image2D;
    /**
     * The constructor
     * @param myMidleta reference to the calling midlet
     */
    public Sideralis3DCanvas(Sideralis myMidlet) {
        super(false);
        running = false;
        this.myMidlet = myMidlet;
        mySky = myMidlet.getMySky();
//        Image image = null;
//        try {
//            image = Image.createImage("/Cursor.png");
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
        Font myFont = Font.getDefaultFont();
        Image image = Image.createImage(myFont.stringWidth("Venus"),myFont.getHeight());
        Graphics g = image.getGraphics();
        g.setFont(myFont);
        g.drawString("Venus",0,0,Graphics.TOP|Graphics.LEFT);

        image2D = new Image2D(Image2D.RGBA,image);

    }
    /**
     * Initialize main variables
     */
    public void init() {
        graphics3d = Graphics3D.getInstance();
        graphics = getGraphics();

        System.out.println("Hints: "+graphics3d.getHints());

        zoom = 1;
        zoomInc = 0;
        indexArrow = Sky.NB_OF_PLANETS;                                         // Earth

        screenCoordPlanets = new ScreenCoord[1];
        screenCoordPlanets[0] = new ScreenCoord();
        coord2DPlanets = new float[(NB_OF_SLICES+1)*NB_OF_STACKS*4];


        // Create a world
        world = new World();

        // Create a camera
        camera = new Camera();
        float aspect = (float) getWidth() / (float) getHeight();
        camera.setPerspective(90, aspect, 1, 1000);
        camera.setTranslation(0,0,150);
        world.addChild(camera);
        world.setActiveCamera(camera);

        // Create light
        Light light = new Light();
        light.setMode(Light.DIRECTIONAL);
        light.setIntensity(1);
        world.addChild(light);

        // Create a group
        group = new Group();
        world.addChild(group);

        // Create the planets objects
        meshPlanets = new Mesh[Sky.NB_OF_PLANETS+1];
        for (int i=0;i<Sky.NB_OF_PLANETS+1;i++) {
            meshPlanets[i] = createSphere(NB_OF_SLICES, NB_OF_STACKS, true, false);
//            meshPlanets[i].getVertexBuffer().setDefaultColor((myMidlet.getMyParameter().getColor())[Color.COL_VENUS+i]);
            PolygonMode polygonMode = new PolygonMode();
            polygonMode.setShading(PolygonMode.SHADE_FLAT);
            meshPlanets[i].getAppearance(0).setPolygonMode(polygonMode);
            Material material = new Material();
            material.setColor(Material.SPECULAR,(myMidlet.getMyParameter().getColor())[Color.COL_VENUS+i] );
            meshPlanets[i].getAppearance(0).setMaterial(material);
            group.addChild(meshPlanets[i]);
        }

//        Sprite3D sprite = new Sprite3D(true, image2D, new Appearance());
//        sprite.setTranslation((float)mySky.getPlanet(0).getX(), (float)mySky.getPlanet(0).getY()+1, (float)mySky.getPlanet(0).getZ());
////        sprite.setScale(30,30,30);
//        group.addChild(sprite);

        meshArrow = createArrow();
        PolygonMode polygonMode = new PolygonMode();
        polygonMode.setCulling(PolygonMode.CULL_NONE);
        meshArrow.getAppearance(0).setPolygonMode(polygonMode);
        setArrowTranslation();

//        meshArrow.setScale(400, 400, 400);
        group.addChild(meshArrow);
        
        for (int i=0;i<Sky.NB_OF_PLANETS;i++) {
            meshPlanets[i].setTranslation((float)mySky.getPlanet(i).getX(), (float)mySky.getPlanet(i).getY(), (float)mySky.getPlanet(i).getZ());
        }
        meshPlanets[Sky.NB_OF_PLANETS].setTranslation((float)mySky.getEarth().getX(), (float)mySky.getEarth().getY(), (float)mySky.getEarth().getZ());

        meshPlanets[PlanetObject.MERCURE].setScale(1.0f,1.0f,1.0f);
        meshPlanets[PlanetObject.VENUS].setScale(1.0f,1.0f,1.0f);
        meshPlanets[PlanetObject.MARS].setScale(1.0f,1.0f,1.0f);
        meshPlanets[PlanetObject.JUPITER].setScale(1.0f,1.0f,1.0f);
        meshPlanets[PlanetObject.SATURNE].setScale(1.0f,1.0f,1.0f);
        meshPlanets[PlanetObject.URANUS].setScale(1.0f,1.0f,1.0f);
        meshPlanets[PlanetObject.NEPTUNE].setScale(1.0f,1.0f,1.0f);
        meshPlanets[PlanetObject.EARTH].setScale(1.0f,1.0f,1.0f);

//        meshPlanets[0].setScale(0.24F, 0.24F, 0.24F);
//        meshPlanets[1].setScale(0.61F, 0.61F, 0.61F);
//        meshPlanets[2].setScale(0.33F, 0.33F, 0.33F);
//        meshPlanets[3].setScale(7.15F, 7.15F, 7.15F);
//        meshPlanets[4].setScale(6.03F, 6.03F, 6.03F);

        // Create the mesh sun
        meshSun = createSphere(8,8,true, true);
        meshSun.setScale(2F,2F,2F);
        meshSun.getVertexBuffer().setDefaultColor(0x00ffff00);
        group.addChild(meshSun);

    }
    /**
     * Called when the display is going to be shown
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

            // Move scene
//            group.postRotate(2, 0, 0, 1);
            if (zoom<0.5F) {
                zoomInc = 0;
                zoom = 0.5F;
            } else if (zoom >12) {
                zoomInc = 0;
                zoom = 12.0F;
            }
            zoom += zoomInc;
            camera = world.getActiveCamera();
            camera.setTranslation(0, 0, zoom*20);
            //camera.postRotate(2, 0, 0, 1);
            world.setActiveCamera(camera);
            meshArrow.postRotate(2, 0, 1, 0);
//            group.setScale(zoom, zoom, zoom);

            // Display scene.
            render(graphics);
//            project2D();
//            graphics.setColor(0x00);
//            graphics.drawString("Venus", screenCoordPlanets[0].x, screenCoordPlanets[0].y, Graphics.BOTTOM|Graphics.HCENTER);
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
    private void setArrowTranslation() {
        if (indexArrow < Sky.NB_OF_PLANETS)
            meshArrow.setTranslation((float)mySky.getPlanet(indexArrow).getX(), (float)mySky.getPlanet(indexArrow).getY()+3, (float)mySky.getPlanet(indexArrow).getZ());
        else
            meshArrow.setTranslation((float)mySky.getEarth().getX(), (float)mySky.getEarth().getY()+3, (float)mySky.getEarth().getZ());
    }
    /**
     * Calculate the 2D display coordinate of all objects
     */
    private void project2D() {
        int i;
        Transform transform = new Transform();
        VertexArray vertexArray3D;
        float w;
        float[] scaleBias = new float[4];
        float[] trCamera = new float[16];

        // ============================
        // === Planets & Sun & Moon ===
        // ============================
        vertexArray3D = meshPlanets[0].getVertexBuffer().getPositions(scaleBias);

        camera = world.getActiveCamera();
        camera.getProjection(transform);

//        Transform invertedCameraTransform = new Transform();
        camera.getTranslation(trCamera);
        Transform invertedCameraTransform = new Transform();
        invertedCameraTransform.set(trCamera);
//        invertedCameraTransform.get(trCamera);

//        camera.getTranslation(trCamera);
        System.out.println(" "+trCamera[0]+" "+trCamera[1]+" "+trCamera[2]+" "+trCamera[3]);
        System.out.println(" "+trCamera[4]+" "+trCamera[5]+" "+trCamera[6]+" "+trCamera[7]);
        System.out.println(" "+trCamera[8]+" "+trCamera[9]+" "+trCamera[10]+" "+trCamera[11]);
        System.out.println(" "+trCamera[12]+" "+trCamera[13]+" "+trCamera[14]+" "+trCamera[15]);

        transform.postMultiply(invertedCameraTransform);
        transform.postTranslate(scaleBias[1], scaleBias[2], scaleBias[3]);
        transform.postScale(scaleBias[0], scaleBias[0], scaleBias[0]);

        transform.transform(vertexArray3D, coord2DPlanets, true);
        // Planets
        for (i = 0; i < 1; i++) {
            w = 1.0f/coord2DPlanets[i * 4 + 3];
            coord2DPlanets[i * 4 + 0] *= w;
            coord2DPlanets[i * 4 + 1] *= w;

            screenCoordPlanets[i].x = (short) ((coord2DPlanets[i * 4 + 0] * getWidth + getWidth) / 2);
            screenCoordPlanets[i].y = (short) ((-coord2DPlanets[i * 4 + 1] * getHeight + getHeight) / 2);
        }
        System.out.println("x="+screenCoordPlanets[0].x+" y="+screenCoordPlanets[0].y);
//        // Sun
//        w = 1.0f/coord2DPlanets[i * 4 + 3];
//        coord2DPlanets[i * 4 + 0] *= w;
//        coord2DPlanets[i * 4 + 1] *= w;
//
//        screenCoordPlanets[i].x = (short) ((coord2DPlanets[i * 4 + 0] * getWidth + getWidth) / 2);
//        screenCoordPlanets[i].y = (short) ((-coord2DPlanets[i * 4 + 1] * getHeight + getHeight) / 2);
//        if (coord2DPlanets[i * 4 + 2] <= 0 || mySky.getSun().getHeight() < 0) {
//            screenCoordPlanets[i].setVisible(false);
//        } else {
//            screenCoordPlanets[i].setVisible(true);
//        }
//        // Moon
//        i += 1;
//        w = 1.0f/coord2DPlanets[i * 4 + 3];
//        coord2DPlanets[i * 4 + 0] *= w;
//        coord2DPlanets[i * 4 + 1] *= w;
//
//        screenCoordPlanets[i].x = (short) ((coord2DPlanets[i * 4 + 0] * getWidth + getWidth) / 2);
//        screenCoordPlanets[i].y = (short) ((-coord2DPlanets[i * 4 + 1] * getHeight + getHeight) / 2);
//        if (coord2DPlanets[i * 4 + 2] <= 0 || mySky.getMoon().getHeight() < 0) {
//            screenCoordPlanets[i].setVisible(false);
//        } else {
//            screenCoordPlanets[i].setVisible(true);
//        }

    }
    /**
     * Renders the sample.
     *
     * @param graphics graphics context for rendering.
     */
    protected void render(Graphics graphics) {
        graphics3d.bindTarget(graphics);
        graphics3d.setViewport(0, 0, getWidth, getHeight);
        graphics3d.render(world);
        graphics3d.releaseTarget();
    }

    /** 
     * A key is pressed
     * @param keyCode id of the key
     */
    protected void keyPressed(int keyCode) {

        if (keyCode == KEY_NUM1) {
            // Zoom out
            zoomInc = -0.1F;
        } else if (keyCode == KEY_NUM3) {
            // Zoom in
            zoomInc = 0.1F;
        } else if (keyCode == KEY_NUM7) {
            indexArrow--;
            if (indexArrow<0)
                indexArrow += (Sky.NB_OF_PLANETS+1);
            setArrowTranslation();
        } else if (keyCode == KEY_NUM9) {
            indexArrow++;
            indexArrow %= (Sky.NB_OF_PLANETS+1);
            setArrowTranslation();
//        } else if (keyCode == KEY_NUM0) {
//            camera = world.getActiveCamera();
//            camera.scale(5F, 5F, 5F);
//            world.setActiveCamera(camera);
//        } else if (keyCode == KEY_NUM8) {
//            camera = world.getActiveCamera();
//            camera.scale(2F, 2F, 2F);
//            world.setActiveCamera(camera);
//        } else if (keyCode == KEY_NUM5) {
//            camera = world.getActiveCamera();
//            camera.scale(1F, 1F, 1F);
//            world.setActiveCamera(camera);
//        } else if (keyCode == KEY_NUM2) {
//            camera = world.getActiveCamera();
//            camera.scale(0.5F, 0.5F, 0.5F);
//            world.setActiveCamera(camera);
        } else {
            // Next display
            running = false;
            myMidlet.nextDisplay();
        }
    }


    // ====================================================================================

    private static Mesh createArrow() {
        byte[] positions = new byte[] {-1,0,0, -1,1,0, 1,0,0, 1,1,0, -2,0,0, 2,0,0, 0,-1,0} ;
        VertexBuffer arrowVertexData = new VertexBuffer();

        VertexArray vertexPositions = new VertexArray(positions.length/3, 3, 1);
        vertexPositions.set(0,positions.length/3,positions);
        arrowVertexData.setPositions(vertexPositions, 1, null);

        int[] indices = {0,1,2,3,4,5,6};

        TriangleStripArray arrowTriangles = new TriangleStripArray(indices, new int[] {4,3});

        return new Mesh(arrowVertexData, arrowTriangles, new Appearance());
    }
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
    private static Mesh createSphere(int slices, int stacks, boolean addNormals, boolean addTexCoords) {
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

    /**
     * Add ramdon color
     * @param mesh the mesh on which to add color
     */
//    public static void addRandomColors(Mesh mesh) {
//        Random random = new Random();
//        int vertexCount = mesh.getVertexBuffer().getVertexCount();
//        byte[] colors = new byte[vertexCount * 3];
//
//        for (int i = 0; i < colors.length; i++) {
//            colors[i] = (byte) random.nextInt(256);
//        }
//
//        VertexArray vertexColors = new VertexArray(vertexCount, 3, 1);
//        vertexColors.set(0, vertexCount, colors);
//        mesh.getVertexBuffer().setColors(vertexColors);
//    }

    /**
     * Creates a cube with one vertex per corner. The edge length is
     * two units, the center of the cube is at the origin.
     *
     *
     * @return cube mesh.
     */
//    public static Mesh createMinimalCube() {
//        byte[] positions = {
//            -1, -1, 1, 1, -1, 1, -1, 1, 1, 1, 1, 1,
//            -1, -1, -1, 1, -1, -1, -1, 1, -1, 1, 1, -1
//        };
//
//        int[] triangleIndices = {
//            0, 1, 2, 3, 7, 1, 5, 4, 7, 6, 2, 4, 0, 1
//        };
//
//        int[] triangleLengths = {triangleIndices.length};
//        VertexBuffer vertexBuffer = new VertexBuffer();
//
//        VertexArray vertexPositions = new VertexArray(positions.length / 3, 3, 1);
//        vertexPositions.set(0, positions.length / 3, positions);
//        vertexBuffer.setPositions(vertexPositions, 1, null);
//
//        TriangleStripArray triangles = new TriangleStripArray(
//                triangleIndices, triangleLengths);
//
//        return new Mesh(vertexBuffer, triangles, new Appearance());
//    }
    /**
     * Called when the drawable area of the Canvas has been changed
     * @param w the new width in pixels of the drawable area of the Canvas
     * @param h w - the new width in pixels of the drawable area of the Canvas
     */
    protected void sizeChanged(int w, int h) {
        getHeight = h;
        getWidth = w;
        camera = world.getActiveCamera();
        float aspect = (float) getWidth / (float) getHeight;
        camera.setPerspective(90, aspect, 1, 1000);
        world.setActiveCamera(camera);
    }
}
