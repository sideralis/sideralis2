package fr.dox.sideralis.projection.sphere;

import fr.dox.sideralis.location.Position;
import fr.dox.sideralis.object.MessierObject;

/**
 *
 * @author Bernard
 */
public class MessierProj extends Projection {
    /**
     * 
     * @param pos
     */
    public MessierProj(MessierObject object, Position pos) {
        super(object,pos);
    }

    public void calculate() {
        calculateHorizontalCoordinate(getObject().getAscendance(), getObject().getDeclinaison(),true);
    }
}
