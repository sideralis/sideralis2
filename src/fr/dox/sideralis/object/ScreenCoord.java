/*
 * ScreenCoord.java
 *
 * Created on 14 février 2007, 16:55
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package fr.dox.sideralis.object;

/**
 *
 * @author Bernard
 */
public class ScreenCoord {
    /** X coordinate */
    public short x;
    /** Y coordinate */
    public short y;
    /** Is the object visible ? */
    private boolean visible;
        
    /** Creates a new instance of ScreenCoord */
    public ScreenCoord() {
        visible = true;
    }
    /**
     * Is the object visible ?
     * @return true if visible, false else
     */
    public boolean isVisible() {
        return visible;
    }
    /** 
     * Tells if the object is visible or not
     * @param f true if visible, false else
     */
    public void setVisible(boolean f) {
        visible = f;
    }
}
