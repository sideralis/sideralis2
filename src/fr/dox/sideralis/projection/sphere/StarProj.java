package fr.dox.sideralis.projection.sphere;

import fr.dox.sideralis.location.Position;
import fr.dox.sideralis.object.StarObject;

/**
 *
 * @author Bernard
 */
public class StarProj extends Projection {
    /**
     * Creates a new instance of Star
     * @param s
     * @param pos
     */
    public StarProj(StarObject s, Position pos) {
        super(s,pos);
    }
    /** 
     * Calculate horizontal coordinate of the star
     */
    public void calculate() {
        calculateHorizontalCoordinate(getObject().getAscendance(),getObject().getDeclinaison(),true);
    }
}
