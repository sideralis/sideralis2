package fr.dox.sideralis.object;

import fr.dox.sideralis.data.ConstellationCatalog;

/**
 *
 * @author Bernard
 */
public class StarObject extends SkyObject {
    /** Reference ID */
    private short HR;
    /** Distance from earth */
    private short dist;
    /** bayerName + constName */
    private short bayerNameConstName;
    
    public static final String STAR_NAME[] = {"","\u03B1 alf","\u03B1 alf1","\u03B1 alf2","Bayer","\u03B2 bet","\u03B2 bet1","\u03B2 bet2","\u03C7 chi","\u03C7 chi1","\u03C7 chi2","\u03B4 del","\u03B4 del1",
            "\u03B4 del2","\u03B4 del3","\u03B5 eps","\u03B5 eps1","\u03B7 eta","\u03B7 eta2","\u03B3 gam","\u03B3 gam1","\u03B3 gam2","\u03B9 iot","\u03B9 iot1","\u03B9 iot2","\u03BA kap","\u03BA kap1","\u03BA kap2","\u03BB lam","\u03BB lam1",
            "\u03BC mu","\u03BC mu1","\u03BC mu2","\u03BD nu","\u03BD nu1","\u03BD nu2","\u03BD nu3","\u03C9 omg","\u03C9 omg1","\u03C9 omg2","\u03BF omi","\u03BF omi1","\u03BF omi2","\u03C6 phi","\u03C6 phi1","\u03C6 phi2","\u03C6 phi3","\u03C0 pi",
            "\u03C0 pi1","\u03C0 pi2","\u03C0 pi3","\u03C0 pi4","\u03C0 pi5","\u03C0 pi6","\u03C8 psi","\u03C8 psi1","\u03C8 psi2","\u03C8 psi3","\u03C1 ro","\u03C1 ro1","\u03C3 sig","\u03C3 sig2","\u03C4 tau","\u03C4 tau1","\u03C4 tau2","\u03C4 tau3",
            "\u03C4 tau4","\u03C4 tau5","\u03C4 tau6","\u03C4 tau8","\u03C4 tau9","\u03B8 tet","\u03B8 tet1","\u03B8 tet2","\u03C5 ups","\u03C5 ups1","\u03C5 ups2","\u03C5 ups4","\u03BE xi","\u03BE xi1","\u03BE xi2","\u03B6 zet","\u03B6 zet1","\u03B6 zet2","",
    };
    /** For debug only */
//    static private short mMag,mHR,mBayerName,mConstName,mDist;

    /** 
     * Creates a new instance of Star 
     * @param a ascendance
     * @param d declinaison
     * @param m magnitude
     * @param HR HR reference
     * @param bN Bayer's name
     * @param cN Constellation's name
     * @param di Distance from earth
     * @param n Name
     */
    public StarObject(float asc, float dec, short mag, short HR,short bayerNameConstName, short dist, String name) {
        super(asc,dec,name,mag);
        this.HR = HR;
        this.dist = dist;
        this.bayerNameConstName = bayerNameConstName; //(short)((bN<<8)+cN);        
    }
    /**
     * Return the HR value of this star
     * @return HR id
     */
    public short getHR() {
        return HR;
    }
    /**
     * Return the magnitude of this star
     * @return magnitude of the star
     */
    public float getMag() {
        return ((float)mag)/100;
    }
    /** 
     * Return the bayer name of the star
     * @return name of the star
     */
    public String getBayerName() {
        return STAR_NAME[(bayerNameConstName>>8)];
    }
    /** 
     * Return the constellation index of the star 
     * @return constellation index
     */
    public short getConstellation() {
        return ((short)(bayerNameConstName&0xff));
    }
    /** 
     * Return the name of the constellation to which the star belongs to 
     * @return constellation's name
     */
    public String getConstellationName() {
        return ConstellationCatalog.constNames[(short)(bayerNameConstName&0xff)][ConstellationCatalog.ABBR];
    }
    /**
     * Return the distance from earth for this star
     * @return distance from earth in light year
     */
    public short getDistance() {
        return dist;
    }
}
