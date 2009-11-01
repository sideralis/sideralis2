package fr.dox.sideralis.projection.sphere;

import fr.dox.sideralis.object.MessierObject;

/**
 *
 * @author Bernard
 */
public class MessierProj extends Projection {
    /**
     * Construtor of the messier projection
     * @param object the object describing the messier object (name, mag, ...)
     * @param pos the position of the user
     */
    public MessierProj(MessierObject object) {
        super(object);
    }
}
