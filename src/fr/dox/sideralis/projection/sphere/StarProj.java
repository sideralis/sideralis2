package fr.dox.sideralis.projection.sphere;

import fr.dox.sideralis.object.StarObject;

/**
 *
 * @author Bernard
 */
public class StarProj extends Projection {
    /**
     * Creates a new instance of Star
     * @param s the object describing the star (name, mag ,...)
     */
    public StarProj(StarObject s) {
        super(s);
    }
}
