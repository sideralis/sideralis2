package fr.dox.sideralis;
/*
 * Sideralis.java
 *
 * Created on 2 octobre 2005, 14:40
 *
 *
 * @history
 * v0.1.0   first version: 7 constellations, no user interface, bugs in calculation which impacts the precision
 * v0.1.1   Improve precision of calculation of arctan, improve performance calculation of arcsin
 *          Separate caculation and display. There are now 2 different threads.
 * v0.3     Exact calculation + 8 constellations + globe position selection (not finished)
 * v0.4.0   Possibility of moves and zoom on the main screen + 12 constellations + globe position finished + splash screen
 * v0.5.0   Optimized performance. Add info on stars with cursor.
 * v0.6.0   Add info on constellations. Change splash screen. Improve cosmetics.
 *          Correct key continue bug
 * v0.6.1   Add one more constellations
 *          Save parameters when exiting and load them when launching
 *          Bug correction:
 *          - Spelling in help and update in help.
 *          - Recenter sky when zooming in and out.
 *          - Cursor could not be moved anymore when constellations were no more displayed
 * v0.7.1   Add more constellations
 *          Add moon
 *          Bug correction
 *          - Stars were not correctly displayed according to user position
 *          - Real stars names were not correctly displayed
 * v0.7.2   Correct a bug which badly displays the moon.
 * v0.8.0   Added more constellations.                                          DONE
 *          Add all planets and sun.                                            DONE
 *          Add day, month and year selection                                   DONE
 *          Add key press continue on globe                                     DONE
 *          Add real time or select time display.                               DONE
 * v0.8.5   Add more constellations
 *          Add dictionnary                                                     DONE
 *          Add horizon view.                                                   DONE
 *          Add possibility to shift right and left.                            DONE
 *          Add info (nb of stars, nb of constellations)                        DONE
 *          Increase start time.                                                DONE
 *          30% more stars due to size optimisation                             DONE
 *          Bug: latitude and longitude are now always in the right range.      DONE
 * v0.9.0   English name for constellations.                                    DONE
 * v1.0.0   English official release.                                           DONE
 * v1.1.1   Change main engine for display: thread usage                        DONE
 *          Correct bug for full screen                                         CORRECTED
 *          Change time calculation                                             DONE
 * v1.1.2   Add night display (red)                                             DONE
 *          Add some more cities (Australian one)                               DONE
 *          Bug: Box around object information was a little too big             CORRECTED
 * v1.1.3   Bug correction - was not starting on Nokia.                         CORRECTED
 * v1.2.0   Support for different languages                                     DONE
 *          Add selection of star by magnitude                                  DONE
 *          Add display bar to quickly select what to display                   DONE
 *          Add messier objects                                                 DONE
 *          Display local sideral time                                          DONE
 *          Display right ascension and declination for stars                   DONE
 * v1.2.1   Corrected precision for planets, moon and sun                       DONE
 *          Add auto location                                                   DONE
 *          Add more stars                                                      DONE
 *          Add full information for planets                                    DONE
 * v1.2.4   Add German language                                                 Done
 *          Add basic support for touch screen                                  Done but full implementation postponed for future version
 *          Add display of constellation names in local language and Latin      Done
 * v1.3.0   Add search functionnality                                           Done
 *          Add magnitude info Messier Object                                   Done
 *          Use localization api                                                Done
 *          Add more stars                                                      Done
 *          Display right ascension and declination                             Done     
 *          Support for large screen                                            Done
 * v1.3.5   GUI Code full rewrite
 * v1.4.0   Add possibility to zoom and scroll in horizontal view
 *          Add altitude in position information/selection              
 *          Display moon phase. 
 *          Add Messier object pictures
 *          Add zoom centered on cursor
 *          Stars are displayed only during days
 * v1.5.0   Add milky way
 *          All corrections (nutation, parallaxe, refraction atmospherique, ... ?)
 *          Support of multiple keys press
 *          
 * v?.?.?   Support for sensors
 *          Keep backligh on
 *          Identify the moon of Jupiter
 *          Add Uranus and Neptune
 *          Ephemeride 
 *          Display orbit for planet
 *          a) press '0' then click on an object: the story of the constellation appears (translated in good Italian, though some typo errors exist): it would be nice to allow the automatic scroll when holding the up/down buttons (now there's the need to press N times these buttons to scroll the text by N lines)
 *          b) when selecting a planets or other objects, the currently displayed information are Magnitude, Distance, Azimut, Height: it would be nice to show also the rise/set time for the current day (see point b) under "Bugs or similar")
 *          c) position settings, planisphere mode: I'd like a Zoom mode, allowing a more precise selection, and also to display the coordinates of the cursor
 *          d) it would be better to draw Messier objects with a larger dot (now they resemble stars, although yellow instead of white), or with a small oval, although in this case I fear there could be problems in the available primitives
 *          e) when in planisphere mode, it would be nice that when the cursor is on the zooming/unzooming operations occur taking it as the center, while when the cursor is off the zoom/unzoom can occur like now
 *          f) search function: the search result consists of cyan brackets. As they're not very visible, maybe enabling automatically the cursor and placing it at these coordinates would allow a better detection (and the previous point would help the user zooming on it quickly)
 *          a chart of variable stars including their periods and types
 *          Add position of iss and other satellites.
 *          have Equatorial/ecliptic  line/grid option
 *          Add all missing constellations informations
 *          Add globular cluster , nebula , etc .
 *
 *  CODE IMPROVEMENT:
 *  - A SkyObject object father of all objects.                                 Done
 *  - Use of resources for color, ?
 *  - New class for support of touch screen
 *  - New canvas class for all views                                            On going
 *  - messages.properties in a new package                                      DONE
 *  - A better format of data (for star, constellation and Messier objects)
 */

/** Supported phones 
 * Nokia 6233, E51, E65, N70, N73, N90
 * SE W300, W810i, K770i, K610i
 * Samsung SGH E-740, F480 
 */
/** Not supported phones
 * SE P910
 * Nokia 6680
 */
import fr.dox.sideralis.data.Sky;
import fr.dox.sideralis.location.Position;
import fr.dox.sideralis.view.SideralisCanvas;
import fr.dox.sideralis.view.SplashCanvas;
import java.io.IOException;
import java.util.Timer;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

/**
 * The MIDlet class. This is the first class called.
 * This class will also define all main objects: Position, BrightStars, HorizontalCoordinate, Sky
 * All the forms are declared in this class. Only the canvas are declared in external class.
 *
 * @author  Bernard
 */
public class Sideralis extends MIDlet implements CommandListener, ItemCommandListener, ItemStateListener {
    /** Version number */
    static final public String VERSION = "v1.3.5";
    
    private Position myPosition;
    private Sky mySky;
    private SideralisCanvas myCanvas;
    private SplashCanvas mySplashCanvas;
    private Display myDisplay;
    private boolean starting;
    private Timer myRefreshTimer;
    private ConfigParameters myParameter;

    /**
     *
     */
    public Sideralis() {
        System.out.println("Constructor");
        starting = true;
        LocalizationSupport.initLocalizationSupport();
        try {
            mySplashCanvas = new SplashCanvas(this);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        myDisplay = Display.getDisplay(this);
        myDisplay.setCurrent(mySplashCanvas);
    }
    /**
     *
     * @throws MIDletStateChangeException
     */
    protected void startApp() throws MIDletStateChangeException {
        System.out.println("Startup");
        // Load position and parameters
        myParameter = new ConfigParameters();

        myPosition = new Position();
        mySky = new Sky(myPosition);
        mySky.calculate();
        myCanvas = new SideralisCanvas(this);
        myCanvas.setFullScreenMode(true);
        myCanvas.init();
        myCanvas.project();
        starting = false;
        myRefreshTimer = new Timer();
        myRefreshTimer.schedule(mySky, 1 * 10 * 1000, 1 * 10 * 1000);           // Every 10s for now
    }

    protected void pauseApp() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void commandAction(Command c, Displayable d) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void commandAction(Command c, Item item) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void itemStateChanged(Item item) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     *
     */
    public void EndOfLocateMe() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     *
     */
    public void endOfSplash() {
        myDisplay.setCurrent(myCanvas);
    }

    /**
     *
     * @return
     */
    public boolean isStarting() {
        return starting;
    }

    /**
     *
     * @return
     */
    public Sky getMySky() {
        return mySky;
    }

    public ConfigParameters getMyParameter() {
        return myParameter;
    }
}
