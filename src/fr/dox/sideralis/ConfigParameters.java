/*
 * ConfigParameters.java
 *
 * Created on 30 septembre 2006, 09:55
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package fr.dox.sideralis;

import fr.dox.sideralis.object.CityObject;

/**
 *
 * @author Bernard
 */
public class ConfigParameters {
    // ----------------------
    // --- All parameters ---
    /** All my display options parameters */
    private boolean[] parameters;
    /** Flag indicating if we move the cursor to display information on object or if pressing keys change the sky (rotation, zoom, ...) */
    private boolean cursor;
    /** Flag indicating if history of constellation are displayed or not */
    private boolean constStory;
    /** Flag to indicate if we are in horizontal view or not */
    private boolean horizontalView;
    /** Filter for star according to value of Magnitude */
    private float maxMag;
    /** */
    private CityObject city1,city2;
    
    /** A table storing the names of all parameters */
    private static String[] paramNames = new String[] {LocalizationSupport.getMessage("PARAM_CONST"),
            LocalizationSupport.getMessage("PARAM_CONSTN"),LocalizationSupport.getMessage("PARAM_CONSTNL"),
            LocalizationSupport.getMessage("PARAM_PLA"),LocalizationSupport.getMessage("PARAM_PLANA"),
            LocalizationSupport.getMessage("PARAM_STCIR"),LocalizationSupport.getMessage("PARAM_COLSTA"),
            LocalizationSupport.getMessage("PARAM_HELP"),LocalizationSupport.getMessage("PARAM_NIGHT"),
            LocalizationSupport.getMessage("PARAM_MESSIER"),LocalizationSupport.getMessage("PARAM_MESSIERNA")};
    private static String[] valueNames = new String[] {LocalizationSupport.getMessage("PARAM_MAX_MAG"),
            LocalizationSupport.getMessage("PARAM_SID_TIME")};
    // Parameters - boolean values
    public static final short CONSTELLATIONS_DISPLAYED = 0;
    public static final short CONSTELLATIONS_NAME_DISPLAYED = 1;
    public static final short CONSTELLATIONS_NAME_LATIN_DISPLAYED = 2; 
    public static final short PLANETS_DISPLAYED = 3;
    public static final short PLANETS_NAME_DISPLAYED = 4;
    public static final short STARS_AS_FILLED_CIRCLES = 5;
    public static final short STARS_COLORED = 6;
    public static final short ON_SCREEN_HELP = 7;
    public static final short NIGHT_VIEW = 8;
    public static final short MESSIER_DISPLAYED = 9;
    public static final short MESSIER_NAME_DISPLAYED = 10;
    
    public static final short NB_PARAM = 11;                                    // Total number of boolean parameters
    // Values - non boolean values
    public static final short MAX_MAG = NB_PARAM+0;                             // maxMag
    public static final short SID_TIME =NB_PARAM+1;                             // No variable
    // Total number of values and parameters
    public static final short NB_OPT = NB_PARAM+2;
    /** */
    public static final short TIMER_POINTER = 100;
        
    /**
     * Creates a new instance of ConfigParameters 
     */
    public ConfigParameters() {
        // Internal parameters (not known from user(
        cursor = false;
        constStory = false;
        horizontalView = true;

        // Boolean parameters
        parameters = new boolean[paramNames.length];
        parameters[CONSTELLATIONS_NAME_DISPLAYED] = false;
        parameters[CONSTELLATIONS_NAME_LATIN_DISPLAYED] = false;
        parameters[CONSTELLATIONS_DISPLAYED] = true;
        parameters[PLANETS_DISPLAYED] = true;
        parameters[PLANETS_NAME_DISPLAYED] = false;        
        parameters[STARS_AS_FILLED_CIRCLES]= false;
        parameters[STARS_COLORED] = true;
        parameters[ON_SCREEN_HELP] = true;
        parameters[NIGHT_VIEW] = false;
        parameters[MESSIER_DISPLAYED] = true;
        parameters[MESSIER_NAME_DISPLAYED] = false;

        // Values parameters
        maxMag = 5.0F;
        city1 = new CityObject(LocalizationSupport.getMessage("CITY_EMPTY"),0,0);
        city2 = new CityObject(LocalizationSupport.getMessage("CITY_EMPTY"),0,0);
    }
    public CityObject getCity1() {
        return city1;
    }
    public CityObject getCity2() {
        return city2;
    }
    public void setCity1(CityObject c) {
        city1 = c;
    }
    public void setCity2(CityObject c) {
        city2 = c;
    }
    /**
     * Return true if planets should be displayed 
     * @return true if planets should be displayed
     */
    public boolean isPlanetDisplayed() {
        return parameters[PLANETS_DISPLAYED];
    }
    /**
     * Return true if planets names should be displayed 
     * @return true if planets names should be displayed
     */
    public boolean isPlanetNameDisplayed() {
        return parameters[PLANETS_NAME_DISPLAYED];
    }
    /**
     * Return true if Messier object should be displayed
     * @return true if Messier objects should be displayed
     */
    public boolean isMessierDisplayed() {
        return parameters[MESSIER_DISPLAYED];
    }
    /**
     * Return true if Messier names should be displayed
     * @return true if Messier names should be displayed
     */
    public boolean isMessierNameDisplayed() {
        return parameters[MESSIER_NAME_DISPLAYED];
    }
    /** 
     * Select if constellations should be displayed or not
     * @param val true if constellations should be displayed, false else
     */
    public void displayConst(boolean val) {
        parameters[CONSTELLATIONS_DISPLAYED] = val;        
    }
    /**
     * Return a boolean value which indicates if constellations should be displayed or not
     * @return true if constellations should be displayed, false else
     */  
    public boolean isConstDisplayed() {
        return parameters[CONSTELLATIONS_DISPLAYED];
    }
    /** 
     * Select if constellation names should be displayed or not
     * @param val true if constellation names should be displayed, false else
     */
    public void displayConstNames(boolean val) {
        parameters[CONSTELLATIONS_NAME_DISPLAYED] = val;        
        if (val == true) 
            parameters[CONSTELLATIONS_NAME_LATIN_DISPLAYED] = false;            // Only one name can be active at a time
    }
    /**
     * Return a boolean value which indicates if constellation names should be displayed or not
     * @return true if constellation names should be displayed, false else
     */  
    public boolean isConstNamesDisplayed() {
        return parameters[CONSTELLATIONS_NAME_DISPLAYED];
    }
    /** 
     * Select if constellation names in latin should be displayed or not
     * @param val true if constellation names should be displayed, false else
     */
    public void displayConstNamesLatin(boolean val) {
        parameters[CONSTELLATIONS_NAME_LATIN_DISPLAYED] = val;
        if (val == true) 
            parameters[CONSTELLATIONS_NAME_DISPLAYED] = false;                  // Only one name can be active at a time
            
    }
    /**
     * Return a boolean value which indicates if constellation names in latin should be displayed or not
     * @return true if constellation names should be displayed, false else
     */  
    public boolean isConstNamesLatinDisplayed() {
        return parameters[CONSTELLATIONS_NAME_LATIN_DISPLAYED];
    }
    /** 
     * Return true if cursor used to get information on objects is displayed
     * @return true if cursor displayed
     */
    public boolean isCursorDisplayed() {
        return cursor;
    }
    /**
     * Set value of cursor in order to display a cursor used to get information on object
     * @param b the new value of cursor
     */
    public void setCursor(boolean b) {
        cursor = b;
    } 
    /**
     * Return true if history of constellation is displayed, else return false
     * @return true or false
     */
    public boolean isHistoryOfConstellationDisplayed() {
        return constStory;
    }
    /**
     * Set or unset the value indicating if the history of constellation should be displayed or not
     * @param b the new value of constStory
     */
    public void setDisplayConstHistory(boolean b) {
        constStory = b;
    }
    /** 
     * Set or unset the display of help on screen
     * @param b true or false
     */
    public void setHelp(boolean b) {
       parameters[ON_SCREEN_HELP] = b; 
    }
    /**
     * Return true if help is displayed on screen
     * @return true or false depending if help is displayed or not on main screen
     */
    public boolean isHelpDisplayed() {
        return parameters[ON_SCREEN_HELP];
    }
    /** 
     * Check if stars should be displayed as filled circles which size depends on magnitude
     * @return true if displayed as circles and not as dot
     */
    public boolean isStarShownAsCircle() {
        return parameters[STARS_AS_FILLED_CIRCLES];
    } 
    /**
     * Set or unset the star display as filled circle or as dot
     * @param b true if seen as filled circle, false is stars are seen as single dot
     */
    public void setStarsAsFilledCircle(boolean b) {
        parameters[STARS_AS_FILLED_CIRCLES] = b;
    }
    /**
     * Check if stars should be displayed as a colored star. The color depends on the magnitude
     * @return true if stars should be displayed as a colored star.
     */
    public boolean isStarColored() {
        return parameters[STARS_COLORED];
    }
    /**
     * Return false if display is zenith, true if display is horizon
     * @return displayMode
     */
    public boolean isHorizontalView() {
        return horizontalView;
    }
    /** 
     * Set the display mode, false for a zenith view, true for a horizontal view
     * @param f the new view mode
     */
    public void setHorizontalView(boolean f) {
        horizontalView = f;
    }
    /** 
     * Return true if night view is active (red display to facilitate night vision)
     * @return true if night view, false else.
     */
    public boolean isNightView() {
        return parameters[NIGHT_VIEW];
    }
    /**
     * Return the color mode the display should use
     * @return COLOR_NORMAL or COLOR_RED
     */
//    public short getColor() {
//        short color;
//        if (isNightView() == true) {
//            if (isHorizontalView())
//                color = SideralisCanvas.COLOR_RED;
//            else
//                color = SideralisCanvas.COLOR_IMP_RED;
//        } else {
//            if (isHorizontalView())
//                color = SideralisCanvas.COLOR_NORMAL;                           // @todo: maybe was COLOR_NORMAL
//            else
//                color = SideralisCanvas.COLOR_IMP_NORMAL;
//        }
//        return color;
//    }
    /**
     * Return the names of the parameters. For use in ChoiceGroup
     * @return a table of String representing the names of all parameters
     */
    public static String[] getParamNames() {
        return paramNames;
    }
    /**
     * Return the name of one value
     * @param idx index of the value
     * @return the value name
     */
    public static String getName(int idx) {
        if (idx < NB_PARAM)
            return paramNames[idx];
        else
            return valueNames[idx-NB_PARAM];
    }
    /** 
     * Return a table of boolean indicating if the parameters are set or not set. Order must be the same as the one used for getParamNames
     * @return table of boolean indicating the values of all parameters.
     */
    public boolean[] getSelectedFlags() {
        return parameters;
    }
    /**
     * Set the values of parameters according the table of boolean given
     * @param idx the index of the values to be changed.
     */
    public void toggleSelectedFlags(int idx) {
        parameters[idx] = !parameters[idx];
        // Check validity
//        checkValidity();
    }
    /**
     * Set the values of parameters according the table of boolean given
     * @param b a table of boolean giving the value of the parameters.
     */
    public void setSelectedFlags(boolean b[]) {
        // Copy
        for (int i=0;i<b.length;i++)
            parameters[i] = b[i];
        // Check validity
//        checkValidity();
    }
    /**
     * Check validity of all parameters
     */
    private void checkValidity() {
        if (parameters[PLANETS_NAME_DISPLAYED] == true)
            parameters[PLANETS_DISPLAYED] = true;
        if (parameters[CONSTELLATIONS_NAME_DISPLAYED] == true || parameters[CONSTELLATIONS_NAME_LATIN_DISPLAYED] == true)
            parameters[CONSTELLATIONS_DISPLAYED] = true;
        if (parameters[MESSIER_NAME_DISPLAYED] == true)
            parameters[MESSIER_DISPLAYED] = true;        
    }
    /**
     * Return the value of parameter maximum magnitude
     * @return maxMag
     */
    public float getMaxMag() {
        return maxMag;
    }
    /**
     * Set the value of maximum magnitude
     * @param maxMag the maximum magnitude for stars to be displayed
     */
    public void setMaxMag(float maxMag) {
        maxMag *= 10;
        maxMag = (int)(maxMag+0.5);
        maxMag /= 10;
        this.maxMag = maxMag;
    }
 }
