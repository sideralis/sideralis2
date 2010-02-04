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
import javax.microedition.m3g.Light;
import javax.microedition.m3g.Material;
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
    /** The lights from Sun and Moon */
//    private Light lightSun, lightMoon;
    /** The global light */
    private Light lightAmbient;
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
    private static final float AMBIENT_VALUE = 0.1f;
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
        initLights();
        setLights();

        // Create an horizon
        horizon = createHorizon2();
//        addRandomColors(horizon);
        PolygonMode polygonMode = new PolygonMode();
        polygonMode.setPerspectiveCorrectionEnable(true);
        polygonMode.setCulling(PolygonMode.CULL_NONE);
//        polygonMode.setShading(PolygonMode.SHADE_FLAT);
        horizon.getAppearance(0).setPolygonMode(polygonMode);
        
        Material material = new Material();
        material.setVertexColorTrackingEnable(true);
        material.setColor(Material.SPECULAR, 0x00ffffff);
        material.setColor(Material.DIFFUSE, 0x00666666);
        material.setColor(Material.AMBIENT, 0x00ffffff);
        horizon.getAppearance(0).setMaterial(material);


        // Create a camera
        camera = new Camera();
        aspectRatio = (float)getWidth/(float)getHeight;
        cameraTransform = new Transform();
        invertedCameraTransform = new Transform();
        setCamera();

    }
    /**
     * 
     */
    public void initLights() {
        lightAmbient = new Light();
        lightAmbient.setMode(Light.AMBIENT);
        lightAmbient.setColor(0x00ffffff);
        lightAmbient.setIntensity(AMBIENT_VALUE);

//        lightSun = new Light();
//        lightSun.setMode(Light.OMNI);
//        lightSun.setColor(0x00ffffff);
//
//        lightMoon = new Light();
//        lightMoon.setMode(Light.OMNI);
//        lightMoon.setColor(0x00ffffff);
    }
    /**
     *
     */
    public void setLights() {
        double x,y,z;
        float scale = Short.MAX_VALUE/64;
        Transform tr;

        // Create the lights
        graphics3D.resetLights();

        float val = AMBIENT_VALUE;

        if (mySky.getSun().getHeight()>0) {
            val += (float)mySky.getSun().getHeight()/2;
//            lightSun.setIntensity((float)mySky.getSun().getHeight()/2);
//            x = Math.cos(Math.PI/2-mySky.getSun().getAzimuth()) * Math.cos(mySky.getSun().getHeight());
//            y = Math.sin(mySky.getSun().getHeight());
//            z = Math.sin(Math.PI/2-mySky.getSun().getAzimuth()) * Math.cos(mySky.getSun().getHeight());
//            tr = new Transform();
//            tr.setIdentity();
//            tr.postRotate((float)Math.toDegrees(-mySky.getSun().getHeight()), (float) Math.cos(Math.PI/2-mySky.getSun().getAzimuth()), 0, (float) Math.sin(Math.PI/2-mySky.getSun().getAzimuth()));
//            tr.postRotate((float)Math.toDegrees(Math.PI/2-mySky.getSun().getAzimuth()), 0, 1, 0);
//            tr.postTranslate((float)(scale*x), (float)(scale*4*y), (float)(scale*z));
//            System.out.println("Sun x= "+(scale*x)+" y="+(scale*y)+" z="+(scale*z)+" rot="+Math.toDegrees(mySky.getSun().getAzimuth()));
//            graphics3D.addLight(lightSun, tr);
        }
        if (mySky.getMoon().getHeight()>0) {
            val += (float)mySky.getMoon().getHeight()/20;
//            lightMoon.setIntensity((float)mySky.getMoon().getHeight()/20);
//            x = Math.cos(Math.PI/2-mySky.getMoon().getAzimuth()) * Math.cos(mySky.getMoon().getHeight());
//            y = Math.sin(mySky.getMoon().getHeight());
//            z = Math.sin(Math.PI/2-mySky.getMoon().getAzimuth()) * Math.cos(mySky.getMoon().getHeight());
//            tr = new Transform();
//            tr.setIdentity();
//            tr.postRotate((float)Math.toDegrees(-mySky.getMoon().getHeight()), (float) Math.cos(Math.PI/2-mySky.getMoon().getAzimuth()), 0, (float) Math.sin(Math.PI/2-mySky.getMoon().getAzimuth()));
//            tr.postRotate((float)Math.toDegrees(Math.PI/2-mySky.getMoon().getAzimuth()), 0, 1, 0);
//            tr.postTranslate((float)(scale*x), (float)(scale*4*y), (float)(scale*z));
//            System.out.println("Moon x= "+(scale*x)+" y="+(scale*y)+" z="+(scale*z)+" i="+(float)mySky.getMoon().getHeight());
//            graphics3D.addLight(lightMoon, tr);
        }
        lightAmbient.setIntensity(val);
        graphics3D.addLight(lightAmbient,null);

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
    private Mesh createHorizon2() {
        Random random = new Random();
        final short scale = Short.MAX_VALUE/64;                                 // The size of the horizon
        short dim = 63;                                                         // The number of square defining the horizon
        int height = 24;
        double heightOffset = -32;
        final short step = (short)(2*scale/dim);                                // The size of square

        short[] positions = new short[(dim+1) * (dim+1) * 3];
//        short[] normals = new short[(dim+1) * (dim+1) * 3];
        byte[] colors = new byte[(dim+1) * (dim+1) * 3];
        int[] triangleIndices = new int[((dim+1)*2)*dim];
        int[] triangleLengths = new int[dim];

        short[] xt,yt,zt;
        xt = new short[(dim+1) * (dim+1)];
        yt = new short[(dim+1) * (dim+1)];
        zt = new short[(dim+1) * (dim+1)];
        short[] xnt,ynt,znt;
        xnt = new short[(dim+1) * (dim+1)];
        ynt = new short[(dim+1) * (dim+1)];
        znt = new short[(dim+1) * (dim+1)];
        short[] vxt,vyt,vzt;
        vxt = new short[6];
        vyt = new short[6];
        vzt = new short[6];
        short vx1,vy1,vz1,vx2,vy2,vz2;

        int i,j,index,tk,indexNor, indexCol;

        index = indexNor = indexCol = 0;
        tk = 0;

        // Calculate all points
        for (i=0;i<=dim;i++) {
            for (j=0;j<=dim;j++) {
                int k = i*(dim+1)+j;
                xt[k] = (short)(-scale + j*step);
                zt[k] = (short)(-scale + i*step);
                double a,b;
                a = (double)xt[k]/(double)scale*3*Math.PI;
                b = (double)zt[k]/(double)scale*3*Math.PI;
                yt[k] = (short)(heightOffset*(1+1*Math.sin(a)*Math.cos(b))-random.nextInt(height));
            }
        }
        // Average altitude
        for (i=1;i<dim;i++) {
            for (j=1;j<dim;j++) {
                int k = i*(dim+1)+j;
                yt[k] = (short)((yt[k-1]+yt[k+dim]+yt[k+dim+1]+yt[k+1]+yt[k-dim]+yt[k-dim-1])/6);
                if (i>5*(dim+1)/16 && i<6*(dim+1)/16 && j>9*(dim+1)/32 && j<13*(dim+1)/32)
                    yt[k] = (short)(2*heightOffset - height);
            }
        }
        short min = 200;
        short max = -200;
        for (i=0;i<yt.length;i++) {
            if (yt[i]<min)
                min = yt[i];
            if (yt[i]>max)
                max = yt[i];
        }
        // Calculate all normals
//        for (i=0;i<=dim;i++) {
//            for (j=0;j<=dim;j++) {
//                int k = i*(dim+1)+j;
//                if (i==0 && j==0) {
//                    // 1 triangle
//                    vx1 = (short) (xt[dim+1]-xt[0]);
//                    vy1 = (short) (yt[dim+1]-yt[0]);
//                    vz1 = (short) (zt[dim+1]-zt[0]);
//                    vx2 = (short) (xt[1]-xt[0]);
//                    vy2 = (short) (yt[1]-yt[0]);
//                    vz2 = (short) (zt[1]-zt[0]);
//                    xnt[0] = (short)(vy1*vz2-vz1*vy2);
//                    ynt[0] = (short)(vz1*vx2-vx1*vz2);
//                    znt[0] = (short)(vx1*vy2-vy1*vx2);
//                } else if (i==dim && j==dim) {
//                    // 1 triangle
//                    vx1 = (short) (xt[k-(dim+1)]-xt[k]);
//                    vy1 = (short) (yt[k-(dim+1)]-yt[k]);
//                    vz1 = (short) (zt[k-(dim+1)]-zt[k]);
//                    vx2 = (short) (xt[k-1]-xt[k]);
//                    vy2 = (short) (yt[k-1]-yt[k]);
//                    vz2 = (short) (zt[k-1]-zt[k]);
//                    xnt[k] = (short)(vy1*vz2-vz1*vy2);
//                    ynt[k] = (short)(vz1*vx2-vx1*vz2);
//                    znt[k] = (short)(vx1*vy2-vy1*vx2);
//                } else if (i==dim && j==0) {
//                    // 2 triangles
//                    vx1 = (short) (xt[k+1]-xt[k]);
//                    vy1 = (short) (yt[k+1]-yt[k]);
//                    vz1 = (short) (zt[k+1]-zt[k]);
//                    vx2 = (short) (xt[k-dim]-xt[k]);
//                    vy2 = (short) (yt[k-dim]-yt[k]);
//                    vz2 = (short) (zt[k-dim]-zt[k]);
//                    vxt[0] = (short)(vy1*vz2-vz1*vy2);
//                    vyt[0] = (short)(vz1*vx2-vx1*vz2);
//                    vzt[0] = (short)(vx1*vy2-vy1*vx2);
//
//                    vx1 = vx2;
//                    vy1 = vy2;
//                    vz1 = vz2;
//                    vx2 = (short) (xt[k-(dim+1)]-xt[k]);
//                    vy2 = (short) (yt[k-(dim+1)]-yt[k]);
//                    vz2 = (short) (zt[k-(dim+1)]-zt[k]);
//                    vxt[1] = (short)(vy1*vz2-vz1*vy2);
//                    vyt[1] = (short)(vz1*vx2-vx1*vz2);
//                    vzt[1] = (short)(vx1*vy2-vy1*vx2);
//
//                    xnt[k] = (short)((vxt[0]+vxt[1])/2);
//                    ynt[k] = (short)((vyt[0]+vyt[1])/2);
//                    znt[k] = (short)((vzt[0]+vzt[1])/2);
//
//                } else if (i==0 && j==dim) {
//                    // 2 triangles
//                    vx1 = (short) (xt[k-1]-xt[k]);
//                    vy1 = (short) (yt[k-1]-yt[k]);
//                    vz1 = (short) (zt[k-1]-zt[k]);
//                    vx2 = (short) (xt[k+dim]-xt[k]);
//                    vy2 = (short) (yt[k+dim]-yt[k]);
//                    vz2 = (short) (zt[k+dim]-zt[k]);
//                    vxt[0] = (short)(vy1*vz2-vz1*vy2);
//                    vyt[0] = (short)(vz1*vx2-vx1*vz2);
//                    vzt[0] = (short)(vx1*vy2-vy1*vx2);
//
//                    vx1 = vx2;
//                    vy1 = vy2;
//                    vz1 = vz2;
//                    vx2 = (short) (xt[k+dim+1]-xt[k]);
//                    vy2 = (short) (yt[k+dim+1]-yt[k]);
//                    vz2 = (short) (zt[k+dim+1]-zt[k]);
//                    vxt[1] = (short)(vy1*vz2-vz1*vy2);
//                    vyt[1] = (short)(vz1*vx2-vx1*vz2);
//                    vzt[1] = (short)(vx1*vy2-vy1*vx2);
//
//                    xnt[k] = (short)((vxt[0]+vxt[1])/2);
//                    ynt[k] = (short)((vyt[0]+vyt[1])/2);
//                    znt[k] = (short)((vzt[0]+vzt[1])/2);
//                } else if (i==0) {
//                    // 3 triangles
//                    vx1 = (short) (xt[k-1]-xt[k]);
//                    vy1 = (short) (yt[k-1]-yt[k]);
//                    vz1 = (short) (zt[k-1]-zt[k]);
//                    vx2 = (short) (xt[k+dim]-xt[k]);
//                    vy2 = (short) (yt[k+dim]-yt[k]);
//                    vz2 = (short) (zt[k+dim]-zt[k]);
//                    vxt[0] = (short)(vy1*vz2-vz1*vy2);
//                    vyt[0] = (short)(vz1*vx2-vx1*vz2);
//                    vzt[0] = (short)(vx1*vy2-vy1*vx2);
//
//                    vx1 = vx2;
//                    vy1 = vy2;
//                    vz1 = vz2;
//                    vx2 = (short) (xt[k+dim+1]-xt[k]);
//                    vy2 = (short) (yt[k+dim+1]-yt[k]);
//                    vz2 = (short) (zt[k+dim+1]-zt[k]);
//                    vxt[1] = (short)(vy1*vz2-vz1*vy2);
//                    vyt[1] = (short)(vz1*vx2-vx1*vz2);
//                    vzt[1] = (short)(vx1*vy2-vy1*vx2);
//
//                    vx1 = vx2;
//                    vy1 = vy2;
//                    vz1 = vz2;
//                    vx2 = (short) (xt[k+1]-xt[k]);
//                    vy2 = (short) (yt[k+1]-yt[k]);
//                    vz2 = (short) (zt[k+1]-zt[k]);
//                    vxt[2] = (short)(vy1*vz2-vz1*vy2);
//                    vyt[2] = (short)(vz1*vx2-vx1*vz2);
//                    vzt[2] = (short)(vx1*vy2-vy1*vx2);
//
//                    xnt[k] = (short)((vxt[0]+vxt[1]+vxt[2])/3);
//                    ynt[k] = (short)((vyt[0]+vyt[1]+vyt[2])/3);
//                    znt[k] = (short)((vzt[0]+vzt[1]+vzt[2])/3);
//
//                } else if (i==dim) {
//                    // 3 triangles
//                    vx1 = (short) (xt[k+1]-xt[k]);
//                    vy1 = (short) (yt[k+1]-yt[k]);
//                    vz1 = (short) (zt[k+1]-zt[k]);
//                    vx2 = (short) (xt[k-dim]-xt[k]);
//                    vy2 = (short) (yt[k-dim]-yt[k]);
//                    vz2 = (short) (zt[k-dim]-zt[k]);
//                    vxt[0] = (short)(vy1*vz2-vz1*vy2);
//                    vyt[0] = (short)(vz1*vx2-vx1*vz2);
//                    vzt[0] = (short)(vx1*vy2-vy1*vx2);
//
//                    vx1 = vx2;
//                    vy1 = vy2;
//                    vz1 = vz2;
//                    vx2 = (short) (xt[k-(dim+1)]-xt[k]);
//                    vy2 = (short) (yt[k-(dim+1)]-yt[k]);
//                    vz2 = (short) (zt[k-(dim+1)]-zt[k]);
//                    vxt[1] = (short)(vy1*vz2-vz1*vy2);
//                    vyt[1] = (short)(vz1*vx2-vx1*vz2);
//                    vzt[1] = (short)(vx1*vy2-vy1*vx2);
//
//                    vx1 = vx2;
//                    vy1 = vy2;
//                    vz1 = vz2;
//                    vx2 = (short) (xt[k-1]-xt[k]);
//                    vy2 = (short) (yt[k-1]-yt[k]);
//                    vz2 = (short) (zt[k-1]-zt[k]);
//                    vxt[2] = (short)(vy1*vz2-vz1*vy2);
//                    vyt[2] = (short)(vz1*vx2-vx1*vz2);
//                    vzt[2] = (short)(vx1*vy2-vy1*vx2);
//
//                    xnt[k] = (short)((vxt[0]+vxt[1]+vxt[2])/3);
//                    ynt[k] = (short)((vyt[0]+vyt[1]+vyt[2])/3);
//                    znt[k] = (short)((vzt[0]+vzt[1]+vzt[2])/3);
//
//                } else if (j==0) {
//                    // 3 triangles
//                    vx1 = (short) (xt[k+dim+1]-xt[k]);
//                    vy1 = (short) (yt[k+dim+1]-yt[k]);
//                    vz1 = (short) (zt[k+dim+1]-zt[k]);
//                    vx2 = (short) (xt[k+1]-xt[k]);
//                    vy2 = (short) (yt[k+1]-yt[k]);
//                    vz2 = (short) (zt[k+1]-zt[k]);
//                    vxt[0] = (short)(vy1*vz2-vz1*vy2);
//                    vyt[0] = (short)(vz1*vx2-vx1*vz2);
//                    vzt[0] = (short)(vx1*vy2-vy1*vx2);
//
//                    vx1 = vx2;
//                    vy1 = vy2;
//                    vz1 = vz2;
//                    vx2 = (short) (xt[k-dim]-xt[k]);
//                    vy2 = (short) (yt[k-dim]-yt[k]);
//                    vz2 = (short) (zt[k-dim]-zt[k]);
//                    vxt[1] = (short)(vy1*vz2-vz1*vy2);
//                    vyt[1] = (short)(vz1*vx2-vx1*vz2);
//                    vzt[1] = (short)(vx1*vy2-vy1*vx2);
//
//                    vx1 = vx2;
//                    vy1 = vy2;
//                    vz1 = vz2;
//                    vx2 = (short) (xt[k-(dim+1)]-xt[k]);
//                    vy2 = (short) (yt[k-(dim+1)]-yt[k]);
//                    vz2 = (short) (zt[k-(dim+1)]-zt[k]);
//                    vxt[2] = (short)(vy1*vz2-vz1*vy2);
//                    vyt[2] = (short)(vz1*vx2-vx1*vz2);
//                    vzt[2] = (short)(vx1*vy2-vy1*vx2);
//
//                    xnt[k] = (short)((vxt[0]+vxt[1]+vxt[2])/3);
//                    ynt[k] = (short)((vyt[0]+vyt[1]+vyt[2])/3);
//                    znt[k] = (short)((vzt[0]+vzt[1]+vzt[2])/3);
//
//                } else if (j==dim) {
//                    // 3 triangles
//                    vx1 = (short) (xt[k-(dim+1)]-xt[k]);
//                    vy1 = (short) (yt[k-(dim+1)]-yt[k]);
//                    vz1 = (short) (zt[k-(dim+1)]-zt[k]);
//                    vx2 = (short) (xt[k-1]-xt[k]);
//                    vy2 = (short) (yt[k-1]-yt[k]);
//                    vz2 = (short) (zt[k-1]-zt[k]);
//                    vxt[0] = (short)(vy1*vz2-vz1*vy2);
//                    vyt[0] = (short)(vz1*vx2-vx1*vz2);
//                    vzt[0] = (short)(vx1*vy2-vy1*vx2);
//
//                    vx1 = vx2;
//                    vy1 = vy2;
//                    vz1 = vz2;
//                    vx2 = (short) (xt[k+dim]-xt[k]);
//                    vy2 = (short) (yt[k+dim]-yt[k]);
//                    vz2 = (short) (zt[k+dim]-zt[k]);
//                    vxt[1] = (short)(vy1*vz2-vz1*vy2);
//                    vyt[1] = (short)(vz1*vx2-vx1*vz2);
//                    vzt[1] = (short)(vx1*vy2-vy1*vx2);
//
//                    vx1 = vx2;
//                    vy1 = vy2;
//                    vz1 = vz2;
//                    vx2 = (short) (xt[k+dim+1]-xt[k]);
//                    vy2 = (short) (yt[k+dim+1]-yt[k]);
//                    vz2 = (short) (zt[k+dim+1]-zt[k]);
//                    vxt[2] = (short)(vy1*vz2-vz1*vy2);
//                    vyt[2] = (short)(vz1*vx2-vx1*vz2);
//                    vzt[2] = (short)(vx1*vy2-vy1*vx2);
//
//                    xnt[k] = (short)((vxt[0]+vxt[1]+vxt[2])/3);
//                    ynt[k] = (short)((vyt[0]+vyt[1]+vyt[2])/3);
//                    znt[k] = (short)((vzt[0]+vzt[1]+vzt[2])/3);
//                } else {
//                    // 6 triangles
//                    vx1 = (short) (xt[k-(dim+1)]-xt[k]);
//                    vy1 = (short) (yt[k-(dim+1)]-yt[k]);
//                    vz1 = (short) (zt[k-(dim+1)]-zt[k]);
//                    vx2 = (short) (xt[k-1]-xt[k]);
//                    vy2 = (short) (yt[k-1]-yt[k]);
//                    vz2 = (short) (zt[k-1]-zt[k]);
//                    vxt[0] = (short)(vy1*vz2-vz1*vy2);
//                    vyt[0] = (short)(vz1*vx2-vx1*vz2);
//                    vzt[0] = (short)(vx1*vy2-vy1*vx2);
//
//                    vx1 = vx2;
//                    vy1 = vy2;
//                    vz1 = vz2;
//                    vx2 = (short) (xt[k+dim]-xt[k]);
//                    vy2 = (short) (yt[k+dim]-yt[k]);
//                    vz2 = (short) (zt[k+dim]-zt[k]);
//                    vxt[1] = (short)(vy1*vz2-vz1*vy2);
//                    vyt[1] = (short)(vz1*vx2-vx1*vz2);
//                    vzt[1] = (short)(vx1*vy2-vy1*vx2);
//
//                    vx1 = vx2;
//                    vy1 = vy2;
//                    vz1 = vz2;
//                    vx2 = (short) (xt[k+dim+1]-xt[k]);
//                    vy2 = (short) (yt[k+dim+1]-yt[k]);
//                    vz2 = (short) (zt[k+dim+1]-zt[k]);
//                    vxt[2] = (short)(vy1*vz2-vz1*vy2);
//                    vyt[2] = (short)(vz1*vx2-vx1*vz2);
//                    vzt[2] = (short)(vx1*vy2-vy1*vx2);
//
//                    vx1 = vx2;
//                    vy1 = vy2;
//                    vz1 = vz2;
//                    vx2 = (short) (xt[k+1]-xt[k]);
//                    vy2 = (short) (yt[k+1]-yt[k]);
//                    vz2 = (short) (zt[k+1]-zt[k]);
//                    vxt[3] = (short)(vy1*vz2-vz1*vy2);
//                    vyt[3] = (short)(vz1*vx2-vx1*vz2);
//                    vzt[3] = (short)(vx1*vy2-vy1*vx2);
//
//                    vx1 = vx2;
//                    vy1 = vy2;
//                    vz1 = vz2;
//                    vx2 = (short) (xt[k-dim]-xt[k]);
//                    vy2 = (short) (yt[k-dim]-yt[k]);
//                    vz2 = (short) (zt[k-dim]-zt[k]);
//                    vxt[4] = (short)(vy1*vz2-vz1*vy2);
//                    vyt[4] = (short)(vz1*vx2-vx1*vz2);
//                    vzt[4] = (short)(vx1*vy2-vy1*vx2);
//
//                    vx1 = vx2;
//                    vy1 = vy2;
//                    vz1 = vz2;
//                    vx2 = (short) (xt[k-(dim+1)]-xt[k]);
//                    vy2 = (short) (yt[k-(dim+1)]-yt[k]);
//                    vz2 = (short) (zt[k-(dim+1)]-zt[k]);
//                    vxt[5] = (short)(vy1*vz2-vz1*vy2);
//                    vyt[5] = (short)(vz1*vx2-vx1*vz2);
//                    vzt[5] = (short)(vx1*vy2-vy1*vx2);
//
//                    xnt[k] = (short)((vxt[0]+vxt[1]+vxt[2]+vxt[3]+vxt[4]+vxt[5])/6);
//                    ynt[k] = (short)((vyt[0]+vyt[1]+vyt[2]+vyt[3]+vyt[4]+vyt[5])/6);
//                    znt[k] = (short)((vzt[0]+vzt[1]+vzt[2]+vzt[3]+vzt[4]+vzt[5])/6);
//                }
////                System.out.println("xn="+xt[k]+" yn="+yt[k]+" znt="+zt[k]);
////                if (yt[k]<min)
////                    min = yt[k];
////                if (yt[k]>max)
////                    max = yt[k];
//
//            }
//        }
//        System.out.println("min="+min+" max="+max);
        // Create vertices
        for (i=0;i<=dim;i++) {
            for (j=0;j<=dim;j++) {
                positions[index++] = xt[i*(dim+1)+j];
                positions[index++] = yt[i*(dim+1)+j];
                positions[index++] = zt[i*(dim+1)+j];
//                normals[indexNor++] = (short)(xnt[i*(dim+1)+j]);
//                normals[indexNor++] = (short)(ynt[i*(dim+1)+j]);
//                normals[indexNor++] = (short)(znt[i*(dim+1)+j]);
                if (yt[i*(dim+1)+j]>-15) {
                    colors[indexCol++] = (byte)0xff;
                    colors[indexCol++] = (byte)0xff;
                    colors[indexCol++] = (byte)0xff;
//                } else if (yt[i*(dim+1)+j]==(short)(2*heightOffset - height)) {
//                    colors[indexCol++] = (byte)0x00;
//                    colors[indexCol++] = (byte)0x00;
//                    colors[indexCol++] = (byte)0xff;
                } else {
                    float r = (float)(max - yt[i*(dim+1)+j])/(float)(min-max);
                    colors[indexCol++] = (byte)(0xa0*(1+r));
                    colors[indexCol++] = (byte)(0xa0+0x3f*(1+r));
//                    colors[indexCol++] = (byte)(0x90);
                    colors[indexCol++] = (byte)(0x21*(1+r));
                }
            }
            if (i!=0) {
                for (j=0;j<=dim;j++) {
                    triangleIndices[tk++] = (i-1)*(dim+1) + j;
                    triangleIndices[tk++] = (i)*(dim+1) + j;
                }
                triangleLengths[i-1] = (dim+1)*2;
            }
        }

        VertexBuffer planeVertexData = new VertexBuffer();

//        VertexArray vertexNormals = new VertexArray(normals.length/3, 3, 2);
//        vertexNormals.set(0, normals.length / 3, normals);

        VertexArray vertexPositions = new VertexArray(positions.length / 3, 3, 2);
        vertexPositions.set(0, positions.length / 3, positions);

        VertexArray vertexColors = new VertexArray(colors.length / 3, 3, 1);
        vertexColors.set(0, colors.length / 3, colors);

        planeVertexData.setPositions(vertexPositions, 1, null);
//        planeVertexData.setNormals(vertexNormals);
        planeVertexData.setColors(vertexColors);

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
            if (i%3 == 1)
                colors[i] = (byte)255;//(byte) (64+random.nextInt(128));
            else
                colors[i] = (byte) 64;
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
