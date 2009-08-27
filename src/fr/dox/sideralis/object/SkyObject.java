package fr.dox.sideralis.object;

/**
 * The SkyObject is the parent class for all objects in the sky.
 * They are mainly defined by their ascendance and declinaison
 * @author Bernard
 */
public class SkyObject {
    /** Right ascension expressed in h m s */
    protected float asc;
    /** Declinaison expressed in d m s */
    protected float dec;
    /** Reference ID */
    protected String name;
    /** Magnitude apparente */
    protected short mag;

    /**
     * Constructor for SkyObject
     * @param asc
     * @param dec
     * @param name
     * @param mag
     */
    public SkyObject(float asc, float dec, String name, short mag) {
        this.asc = asc;
        this.dec = dec;
        this.name = name;
        this.mag = mag;
    }
    /**
     * Return the ascendance of the object
     * @return ascendance of the object
     */
    public double getAscendance() {
        return (double)asc;
    }
    /**
     * Return the declinaison of the object
     * @return declinaison of the object
     */
    public double getDeclinaison() {
        return (double)dec;
    }
    /**
     * Set ascendance
     * @param asc new value of ascendance
     */
    public void setAscendance(float asc) {
        this.asc = asc;
    }
    /**
     * Set declinaison
     * @param dec new value of declinaison
     */
    public void setDeclinaison(float dec) {
        this.dec = dec;
    }
    /**
     * Return the name of the object
     * @return the name of the object
     */
    public String getName() {
        return name;
    }


}
