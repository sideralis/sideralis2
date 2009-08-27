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
import java.util.TimerTask;


/**
 * This class calculates manages the position of all objects in the sky (from stars to moon, ...).
 * Input are the catalog and position. Output is the X and Y coordinate on a virtual screen of each stars.
 * @author Bernard
 */
public class Sky extends TimerTask {
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
    
    /** The constellations */
    private ConstellationCatalog myConstellations;
    /** a reference to my position and time */
    private Position myPosition;
    
    /** flag to indicate calculation on going */
    boolean flagCalcul;

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

    private PlanetObject[] systemSolarObjects;
    private PlanetProj[] systemSolarProj;

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
    public Sky(Position pos) {
        // Create all stars
        starsProj = new StarProj[StarCatalog.getNumberOfStars()];
        for (int i=0;i<starsProj.length;i++)
            starsProj[i] = new StarProj(StarCatalog.getStar(i), pos);

        // Create the constellations
        myConstellations = new ConstellationCatalog();

        // Creates a moon
        moonObject = new SkyObject(0F, 0F, LocalizationSupport.getMessage("NAME_MOON"), (short)-120);
        moonProj = new MoonProj(moonObject,pos);

        // create a sun
        sunObject = new SkyObject(0F, 0F, LocalizationSupport.getMessage("NAME_SUN"), (short)-267);
        sunProj = new SunProj(sunObject, pos);

        // Create mercury
        mercuryObject = new PlanetObject(0F, 0F, LocalizationSupport.getMessage("NAME_MERCURY"), (short)-10,
                178.179078, 149474.07078, 0.0003011, 0, 0.3870986, 0.20561421, 0.00002046, -0.000000030, 0,
                7.002881, 0.0018608, -0.0000183, 0, 28.753753, 0.3702806, 0.0001208, 0,
                47.145944, 1.1852083, 0.0001739, 0, 102.27938, 149472.51529, 0.000007);
        mercuryProj = new PlanetProj(mercuryObject,pos);

        // Create venus
        venusObject = new PlanetObject(0F,0F,LocalizationSupport.getMessage("NAME_VENUS"),(short)-40,
                342.767053, 58519.21191, 0.0003097, 0, 0.7233316, 0.00682069, -0.00004774, 0.000000091, 0,
                3.393631, 0.0010058, -0.0000010, 0, 54.384186, 0.5081861, -0.0013864, 0,
                75.779647, 0.8998500, 0.0004100, 0, 212.60322, 58517.80387, 0.001286);
        venusProj = new PlanetProj(venusObject,pos);

        // Create Mars
        marsObject = new PlanetObject(0F,0F,LocalizationSupport.getMessage("NAME_MARS"),(short)15,
                293.737334, 19141.69551, 0.0003107, 0, 1.5236883, 0.09331290, 0.000092064, -0.000000077, 0,
                1.850333, -0.0006750, 0.0000126, 0, 285.431761, 1.0697667, 0.0001313, 0.00000414,
                48.786442, 0.7709917, -0.0000014, -0.00000533, 319.51913, 19139.85475, 0.000181);
        marsProj = new PlanetProj(marsObject, pos);

        // Create Jupiter
        jupiterObject = new PlanetObject(0F,0F,LocalizationSupport.getMessage("NAME_JUPITER"),(short)-16,
                238.049257, 3036.301986, 0.0003347, -0.00000165, 5.202561, 0.04833475, 0.000164180, -0.0000004676, -0.000000017,
                1.308736, -0.0056961, 0.0000039, 0,273.277558, 0.5994317, 0.00070405, 0.00000508,
                99.443414, 1.0105300, 0.00035222, -0.00000851, 225.32833, 3034.69202, -0.000722);
        jupiterProj = new PlanetProj(jupiterObject, pos);

        // Create Saturn
        saturnObject = new PlanetObject(0F,0F,LocalizationSupport.getMessage("NAME_SATURN"),(short)-7,
                266.564377, 1223.509884, 0.0003245, -0.0000058, 9.554747, 0.05589232, -0.00034550, -0.000000728, 0.00000000074,
                2.492519, -0.0039189, -0.00001549, 0.00000004, 338.307800, 1.0852207, 0.00097854, 0.00000992,
                112.790414, 0.8731951, -0.00015218, -0.00000531, 175.46622, 1221.55147, -0.000502);
        saturnProj = new PlanetProj(saturnObject, pos);

        // Create all solar system objects
        systemSolarObjects = new PlanetObject[NB_OF_SYSTEM_SOLAR_OBJECTS];
        systemSolarObjects[0] = mercuryObject;
        systemSolarObjects[1] = venusObject;
        systemSolarObjects[2] = marsObject;
        systemSolarObjects[3] = jupiterObject;
        systemSolarObjects[4] = saturnObject;

        systemSolarProj = new PlanetProj[NB_OF_SYSTEM_SOLAR_OBJECTS];
        systemSolarProj[0] = mercuryProj;
        systemSolarProj[1] = venusProj;
        systemSolarProj[2] = marsProj;
        systemSolarProj[3] = jupiterProj;
        systemSolarProj[4] = saturnProj;

        // Create the messier objects
        messierProj = new MessierProj[MessierCatalog.getNumberOfObjects()];
        for (int i=0;i<messierProj.length;i++) {
            messierProj[i] = new MessierProj(MessierCatalog.getObject(i), pos);
        }
        
        this.myPosition = pos;
        flagCalcul = false;

    }
    /**
     * Return the constellations object
     * @return the constellations object
     */
    public ConstellationCatalog getConstellations() {
        return myConstellations;
    }
    /**
     * Return the number of projected stars
     * @deprecated
     * @return the number of stars
     */
    public int getNumberOfStars() {
        return starsProj.length;
    }
    /**
     * Return the number of Messier objects
     * @deprecated
     * @return the number of Messier objects
     */
    public int getNumberOfMessierObjects() {
        return messierProj.length;
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
        return systemSolarProj;
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
     * Return a planet object
     * @return a reference to the mercure object
     * @deprecated
     */
    public PlanetProj getMercure() {
        return mercuryProj;
    }
    /**
     * Return a planet object
     * @return a reference to the venus object
     * @deprecated
     */
    public PlanetProj getVenus() {
        return venusProj;
    }
    /**
     * Return a planet object
     * @return a reference to the mars object
     * @deprecated
     */
    public PlanetProj getMars() {
        return marsProj;
    }
    /**
     * Return a planet object
     * @return a reference to the jupiter object
     * @deprecated
     */
    public PlanetProj getJupiter() {
        return jupiterProj;
    }
    /**
     * Return a planet object
     * @return a reference to the saturne object
     * @deprecated
     */
    public PlanetProj getSaturne() {
        return saturnProj;
    }
    /**
     * Return the flag calcul which indicates if the mobile is doing star position calculation
     * @return true if the system is doing calculation, else false
     */
    public boolean getFlagCalcul() {
        return flagCalcul;
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
        flagCalcul = true;
        // ---------------------------------------------------
        // --- Recalculate global variables linked to time ---
        myPosition.getTemps().adjustDate();
        myPosition.getTemps().calculateJourJulien();
        myPosition.getTemps().calculateHS();
        
        Projection.calculateParamSun();
        
        // -----------------------------------
        // --- Calculate position of stars ---
        for (int i=0;i<starsProj.length;i++)
            starsProj[i].calculate();

        // ----------------------------------
        // --- Calculate position of moon ---
        moonProj.calculate();

        // ---------------------------------
        // --- Calculate position of sun ---
        sunProj.calculate();

        // -------------------------------------
        // --- Calculate position of planets ---
        for (int i=0;i<systemSolarProj.length;i++) {
            systemSolarProj[i].calculate();
        }

        // ---------------------------------------------
        // --- Calculate position of Messier objects ---
        for (int i=0;i<messierProj.length;i++)
            messierProj[i].calculate();
        
        flagCalcul = false;
    }

}
