package fr.dox.sideralis.data;

import fr.dox.sideralis.LocalizationSupport;
import fr.dox.sideralis.location.Position;
import fr.dox.sideralis.object.PlanetObject;
import fr.dox.sideralis.object.SkyObject;
import fr.dox.sideralis.projection.sphere.MessierProj;
import fr.dox.sideralis.projection.sphere.MoonProj;
import fr.dox.sideralis.projection.sphere.PlanetProj;
import fr.dox.sideralis.projection.sphere.Projection;
import fr.dox.sideralis.projection.sphere.StarProj;
import fr.dox.sideralis.projection.sphere.SunProj;


/**
 * This class calculates manages the position of all objects in the sky (from stars to moon, ...).
 * Input are the catalog and position. Output is the X and Y coordinate on a virtual screen of each stars.
 * @author Bernard
 */
public class Sky implements Runnable {
    private int progress;
    /** The projection (height, azimuth) of all my stars */
    private StarProj[] starsProj;
    /** The projection of the moon */
    private MoonProj moonProj;
    /** The projection of the sun */
    private SunProj sunProj;
    /** The projection of planets */
    private PlanetProj mercuryProj,venusProj,marsProj,jupiterProj,saturnProj;
    /** The Messier objects */
    private MessierProj[] messierProj;
    
    /** a reference to my position and time */
    private Position myPosition;

    /** The moon description */
    private SkyObject moonObject;
    /** The sun description */
    private SkyObject sunObject;
    /** The Mercury planet description */
    private PlanetObject mercuryObject;
    /** The Venus planet description */
    private PlanetObject venusObject;
    /** The Mars planet description */
    private PlanetObject marsObject;
    /** The Saturn planet description */
    private PlanetObject saturnObject;
    /** The Juptier planet description */
    private PlanetObject jupiterObject;

    private PlanetObject[] planetObjects;
    private PlanetProj[] planetProj;

    private boolean calculationDone;

    public static final short MERCURY = 0;
    public static final short VENUS = 1;
    public static final short MARS = 2;
    public static final short JUPITER = 3;
    public static final short SATURN = 4;
    public static final short NB_OF_SYSTEM_SOLAR_OBJECTS = 5;
    /**
     * Creates a new instance of Sky (so all objects in the sky)
     * @param pos the position of the user
     */
    public Sky(Position myPosition) {
        int i,i1,i2;
        // Create all stars - We cumulate the catalog
        starsProj = new StarProj[StarCatalogConst.getNumberOfStars()+StarCatalogMag.getNumberOfStars()];
        for (i=i1=i2=0;i<starsProj.length;i++) {
            if (i<StarCatalogConst.getNumberOfStars())
                starsProj[i] = new StarProj(StarCatalogConst.getStar(i1++), myPosition);
            else
                starsProj[i] = new StarProj(StarCatalogMag.getStar(i2++), myPosition);
        }

        // Creates a moon
        moonObject = new SkyObject(0F, 0F, LocalizationSupport.getMessage("NAME_MOON"), (short)-120);
        moonProj = new MoonProj(moonObject,myPosition);

        // create a sun
        sunObject = new SkyObject(0F, 0F, LocalizationSupport.getMessage("NAME_SUN"), (short)-267);
        sunProj = new SunProj(sunObject, myPosition);

        // Create mercury
        mercuryObject = new PlanetObject(0F, 0F, LocalizationSupport.getMessage("NAME_MERCURY"), (short)-10,
                178.179078, 149474.07078, 0.0003011, 0, 0.3870986, 0.20561421, 0.00002046, -0.000000030, 0,
                7.002881, 0.0018608, -0.0000183, 0, 28.753753, 0.3702806, 0.0001208, 0,
                47.145944, 1.1852083, 0.0001739, 0, 102.27938, 149472.51529, 0.000007);
        mercuryProj = new PlanetProj(mercuryObject,myPosition);

        // Create venus
        venusObject = new PlanetObject(0F,0F,LocalizationSupport.getMessage("NAME_VENUS"),(short)-40,
                342.767053, 58519.21191, 0.0003097, 0, 0.7233316, 0.00682069, -0.00004774, 0.000000091, 0,
                3.393631, 0.0010058, -0.0000010, 0, 54.384186, 0.5081861, -0.0013864, 0,
                75.779647, 0.8998500, 0.0004100, 0, 212.60322, 58517.80387, 0.001286);
        venusProj = new PlanetProj(venusObject,myPosition);

        // Create Mars
        marsObject = new PlanetObject(0F,0F,LocalizationSupport.getMessage("NAME_MARS"),(short)15,
                293.737334, 19141.69551, 0.0003107, 0, 1.5236883, 0.09331290, 0.000092064, -0.000000077, 0,
                1.850333, -0.0006750, 0.0000126, 0, 285.431761, 1.0697667, 0.0001313, 0.00000414,
                48.786442, 0.7709917, -0.0000014, -0.00000533, 319.51913, 19139.85475, 0.000181);
        marsProj = new PlanetProj(marsObject, myPosition);

        // Create Jupiter
        jupiterObject = new PlanetObject(0F,0F,LocalizationSupport.getMessage("NAME_JUPITER"),(short)-16,
                238.049257, 3036.301986, 0.0003347, -0.00000165, 5.202561, 0.04833475, 0.000164180, -0.0000004676, -0.000000017,
                1.308736, -0.0056961, 0.0000039, 0,273.277558, 0.5994317, 0.00070405, 0.00000508,
                99.443414, 1.0105300, 0.00035222, -0.00000851, 225.32833, 3034.69202, -0.000722);
        jupiterProj = new PlanetProj(jupiterObject, myPosition);

        // Create Saturn
        saturnObject = new PlanetObject(0F,0F,LocalizationSupport.getMessage("NAME_SATURN"),(short)-7,
                266.564377, 1223.509884, 0.0003245, -0.0000058, 9.554747, 0.05589232, -0.00034550, -0.000000728, 0.00000000074,
                2.492519, -0.0039189, -0.00001549, 0.00000004, 338.307800, 1.0852207, 0.00097854, 0.00000992,
                112.790414, 0.8731951, -0.00015218, -0.00000531, 175.46622, 1221.55147, -0.000502);
        saturnProj = new PlanetProj(saturnObject, myPosition);

        // Create all solar system objects
        planetObjects = new PlanetObject[NB_OF_SYSTEM_SOLAR_OBJECTS];
        planetObjects[0] = mercuryObject;
        planetObjects[1] = venusObject;
        planetObjects[2] = marsObject;
        planetObjects[3] = jupiterObject;
        planetObjects[4] = saturnObject;

        planetProj = new PlanetProj[NB_OF_SYSTEM_SOLAR_OBJECTS];
        planetProj[0] = mercuryProj;
        planetProj[1] = venusProj;
        planetProj[2] = marsProj;
        planetProj[3] = jupiterProj;
        planetProj[4] = saturnProj;

        // Create the messier objects
        messierProj = new MessierProj[MessierCatalog.getNumberOfObjects()];
        for (i=0;i<messierProj.length;i++) {
            messierProj[i] = new MessierProj(MessierCatalog.getObject(i), myPosition);
        }
        
        this.myPosition = myPosition;

    }
    /**
     * Return one of the star from all stars
     * @param i the index of the star required
     * @return the ith star in the list of star
     */
    public StarProj getStar(int i) {
        return starsProj[i];
    }
    /**
     * Return all stars
     * @return a reference to the starsProj object
     */
    public StarProj[] getStarsProj() {
        return starsProj;
    }

    /**
     * Return one of the Messier object from the catalog
     * @param i the index of the Messier object required
     * @return the ith Messier in the Messier catalog
     */
    public MessierProj getMessier(int i) {
        return messierProj[i];
    }
    /**
     * 
     * @return
     */
    public PlanetProj[] getSystemSolarProj() {
        return planetProj;
    }
    /**
     * Return a moon object
     * @return a reference to the moon
     */
    public MoonProj getMoon() {
        return moonProj;
    }
    /**
     * Return a sun object
     * @return a reference to the sun
     */
    public SunProj getSun() {
        return sunProj;
    }
    /**
     * Return a planet
     * @param i the index of the planet (see Sky static definition)
     * @return
     */
    public PlanetProj getPlanet(int i) {
        return planetProj[i];
    }
    /**
     *
     * @return
     */
    public boolean isCalculationDone() {
        return calculationDone;
    }
    /**
     *
     * @param calculationDone
     */
    public void setCalculationDone(boolean calculationDone) {
        this.calculationDone = calculationDone;
    }

    /**
     * This is the thread which is called every (xx s) and which add time, calculate new time variables and calculates the star position and other objects
     */
    public void run() {
        calculate();
        calculationDone = true;
    }
    /**
     * Add time, calculate new time variables and calculates the star position and other objects
     */
    public void calculate() {
    	setProgress(0);
        
        // ---------------------------------------------------
        // --- Recalculate global variables linked to time ---
        myPosition.getTemps().adjustDate();
        myPosition.getTemps().calculateJourJulien();
        myPosition.getTemps().calculateHS();
        Projection.calculateParamSun();
    	setProgress(10);

        // -----------------------------------
        // --- Calculate position of stars ---
        for (int i=0;i<starsProj.length;i++) {
            starsProj[i].calculate();
            if (i%100 == 0)
            	setProgress(10+60*i/starsProj.length);
        }

        // ----------------------------------
        // --- Calculate position of moon ---
        moonProj.calculate();

        // ---------------------------------
        // --- Calculate position of sun ---
        sunProj.calculate();

        // -------------------------------------
        // --- Calculate position of planets ---
        for (int i=0;i<planetProj.length;i++) {
            planetProj[i].calculate();
        }
    	setProgress(75);

        // ---------------------------------------------
        // --- Calculate position of Messier objects ---
        for (int i=0;i<messierProj.length;i++) {
            messierProj[i].calculate();
            if (i%20 == 0)
            	setProgress(75+25*i/messierProj.length);
        }
    	setProgress(100);
    }
    /**
     *
     * @return
     */
    public int getProgress() {
        return progress;
    }
    /**
     *
     * @param progress
     */
    public void setProgress(int progress) {
        this.progress = progress;
    }

}
