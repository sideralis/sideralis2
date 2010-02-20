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
 * v0.8.5   Add more constellations                                             DONE
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
 * v1.5.0   GUI Code full rewrite                                               Done
 *          Support of multiple keys press                                      Done
 *          Add Uranus and Neptune                                              Done
 *          Improve precision for large planet                                  Done
 *          Add atmoshpere refraction                                           Done
 *          Add drawing of missing constellations                               Done
 *          Add more stars                                                      Done
 *          Add support for touch screen                                        Done
 *          Add possibility to zoom and scroll in horizontal view               Done
 *          Add new help system                                                 Done
 * v1.6.0
 *          Support for sensors
 *          Add 3D system solar view
 *          Display moon phase.
 *          Add altitude in position information/selection              
 *          Add Messier object pictures
 *          Add zoom centered on cursor
 *          Stars are displayed only during days
 *          Add light in 3D view
 *          Add object in horizon view
 * v1.7.0   Add milky way
 *          All corrections (nutation (p48), aberration (p50), parallaxe, ... ?)
 */

/*
 * v?.?.?   
 *          Keep backligh on
 *          Identify the moon of Jupiter
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
 *          Add ISS position
 *          Add horizontal and vertical compass
 *          Add real sphere view
 *          Improve precision of moon by using elp82b
 */

/*
 *  CODE IMPROVEMENT:
 */

/*
 * TODO: Test on all Nokia phones
 * TODO: Perf and memory profiling
 * TODO: Correct scrolling when using upper part
 * TODO: Define test set
 *
 */

/*
 * TODO: Later: Add horizon line
 * TODO: Later: Add moon phase
 * TODO: Later: sunrise, noon and sunset
 * TODO: Later: Add light in system solar view
 * TODO: Later: Add orbit in system solar view
 * TODO: Later: Add ring to saturn
 * TODO: Later: Add cursor in system solar view
 * TODO: Later: Add touch screen support in system solar view
 * TODO: Later: add latitude and longitude to know where we are looking
 *
 */

import fr.dox.sideralis.data.ConstellationCatalog;
import fr.dox.sideralis.data.MessierCatalog;
import fr.dox.sideralis.data.Sky;
import fr.dox.sideralis.data.StarCatalogConst;
import fr.dox.sideralis.data.StarCatalogMag;
//#ifdef JSR179
import fr.dox.sideralis.location.LocateMe;
//#endif
import fr.dox.sideralis.location.Position;
import fr.dox.sideralis.object.CityObject;
import fr.dox.sideralis.view.GlobeCanvas;
//#ifdef JSR184
//import fr.dox.sideralis.view.Sideralis3DCanvas;
//#endif
import fr.dox.sideralis.view.SideralisCanvas;
import fr.dox.sideralis.view.SplashCanvas;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStore;

/**
 * The MIDlet class. This is the first class called.
 * This class will also define all main objects: Position, Sky, Canvas
 * All the forms are declared in this class. Only the canvas are declared in external class.
 *
 * @author  Bernard
 */
public class Sideralis extends MIDlet implements CommandListener, ItemCommandListener, ItemStateListener {
    /** Version number */
    public String version,build;
    /** How often the calculation of position is refreshed */
    public static final long REFRESH_TIME_CALC = 1 * 10 * 1000;
    /** The position of the user */
    private Position myPosition;
    /** The sky object coantaining all objects from the sky */
    private Sky mySky;
    /** The main canvas */
    private SideralisCanvas myCanvas;
    /** The intro canvas */
    private SplashCanvas mySplashCanvas;
    /** The globe canvas */
    private GlobeCanvas globeCanvas;
//#ifdef JSR184
    //private Sideralis3DCanvas my3DCanvas;
//#endif
    /** The display */
    private Display myDisplay;
    /** A boolean indicating if all objects have been created */
    private boolean allObjectsCreated;
    /** An object describing all the parameters that the user can set or unset */
    private ConfigParameters myParameter;

    /** The position form */
    private Form positionForm;
    /** The form used to select a city */
    private Form cityForm;
    /** The form used to display display options */
    private Form displayOptionsForm;
    /** The help form */
    private Form helpForm;
    /** The info form */
    private Form infoForm;
    /** The dico form */
    private Form dicoForm;
    /** The language form */
    private Form langForm;
    /** The locate me form */
    private Form locateMeForm;
    /** The search form */
    private Form searchForm;
    /** The touch screen form */
    private Form touchScreenForm;

    /** Some commands */
    private Command exitCommand;
    private Command displayCommand;
    private Command positionCommand;
    private Command cancelCommand;
    private Command globeCommand;
    private Command cityCommand;
    private Command autoPositionCommand;
    private Command okCommand;
    private Command helpCommand;
    private Command infoCommand;
    private Command backCommand;
    private Command dicoCommand;
    private Command langCommand;
    private Command searchCommand;
    private Command touchScreenCommand;

    /** Some item in the position Form */
    private TextField latTextField;
    private TextField longTextField;
    private TextField posName;
    private StringItem posStore,pos1Restore,pos2Restore;
    private DateField dateField;
    private StringItem realTimeButton;
    private Gauge positionGauge;
    /** Some items in the city form */
    private StringItem cityStringItem;
    private ChoiceGroup cityChoiceGroup;
    /** Some items in the display options form */
    private StringItem displayOptionsStringItem;
    private TextField displayOptionsTextMaxMag;
    private TextField searchTextSearch;
    private StringItem searchItemSearch;
    private StringItem searchItemCancelSearch;
    private ChoiceGroup displayOptionsChoiceGroup;
    /** Some items for the help form */
    private StringItem[] helpStringItem;
    /** Some items for the info form */
    private StringItem[] infoStringItem;
    /** The string indicating how much free memory we have */
    private StringItem freeMemStringItem;
    /** To indicate how long does it take to draw the display */
    private StringItem timeCalculationStringItem;
    /** To indicate how does it take to calculate the position of the sky objects */
    private StringItem timeDisplayStringItem;
    /** Some items for the dico form */
    private StringItem[] dicoStringItem;
    /** Some items for the language form */
    private StringItem langStringItem;
    private ChoiceGroup langChoiceGroup;
    private long myOffsetForCancellation;
    /** Some items for the touch screen configuration form */
    private Gauge sensitivityGauge;
    private Gauge inertiaGauge;
    private Gauge scrollSpeedHorizonGauge;
    private Gauge scrollSpeedHorZenithGauge;
    private Gauge scrollSpeedVerZenithGauge;
    private Gauge skipDragEventGauge;


    /** The locate me object */
//#ifdef JSR179
    private LocateMe whereAmI;
//#endif
    /** A temporary position used by the locate me object */
    private Position tempPos;
    /** An object used to highlight some stars, Messier or planets objects*/
    private Search mySearch;
    /** A temp index for the search */
    private short tmpIndex;
    /** The index of language used during run time */
    private int lang;
    /** To indicate that the midlet is in pause and that at the next call of startApp, not all objects have to be restarted */
    private boolean pauseFlag;

    /**
     * All codes codes which should be executed before creating main objects
     */
    public Sideralis() {
        // Flag to indicate that all objects have not yet been created
        allObjectsCreated = false;
        pauseFlag = false;

        // =====================================
        // === Get versions and build number ===
        version = "v"+getAppProperty("MIDlet-Version");
        build = getAppProperty("build");
        int pos1 = build.indexOf('.');
        int pos2 = build.indexOf('.', pos1+1);
        String n1,n2,n3;
        n1 = build.substring(0, pos1);
        n2 = build.substring(pos1+1,pos2);
        n3 = build.substring(pos2+1,build.length());
        // Add 0 to n1 and n2 if needed
        if (n2.length() == 1)
            n2 = "0"+n2;
        if (n3.length() == 1)
            n3 = "0"+n3;
        // Create build number
        build = n1+n2+n3;
        // Remove leading 0 from build number
        boolean st = build.startsWith("0");
        while (st) {
            build = build.substring(1,build.length());
            st = build.startsWith("0");
        }

        // Select language
        if (loadLanguage()) {
            LocalizationSupport.initLocalizationSupport(getLang());         // Language was selected before
        } else {
            LocalizationSupport.initLocalizationSupport();                  // Language was never selected
            setLang();
        }
    }
    /**
     * Creation of main objects (it takes time)
     * @throws MIDletStateChangeException
     */
    protected void startApp() {
        if (pauseFlag == false) {
            // Create splash screen
            mySplashCanvas = new SplashCanvas(this);
            // Display splash screen
            myDisplay = Display.getDisplay(this);
            myDisplay.setCurrent(mySplashCanvas);
            // =============================
            // === Set position and time ===
            myPosition = new Position();
            // ====================================
            // === Load position and parameters ===
            myParameter = new ConfigParameters();
            loadData();
            // ==================================
            // === Check capability of mobile ===
            // Check support for 3D
            String m3gVersion = System.getProperty("microedition.m3g.version");
            if (m3gVersion != null)
                myParameter.setSupport3D(true);
            else
                myParameter.setSupport3D(false);
            // Check support for GPS
            if ((System.getProperty("microedition.location.version") != null) && (System.getProperty("microedition.location.version").equals("1.0"))) // If API location exists on the phone
                myParameter.setSupportGPS(true);
            else
                myParameter.setSupportGPS(false);
            // Check support for touch screen
            myParameter.setSupportTouchScreen(mySplashCanvas.supportTouchScreen());
            // ====================
            // === Create a sky ===
            mySky = new Sky(myPosition);
            // ==============================
            // === Create a search object ===
            mySearch = new Search(mySky);
            mySearch.createListOfObjets();
            // === Calculate position of all objects ===
            mySky.calculate();
            // === Display objects ===
            // === Create display ===
            myCanvas = new SideralisCanvas(this);
            myCanvas.init();
            //myCanvas.project();


//#ifdef JSR184
//            if (myParameter.isSupport3D()) {
//                my3DCanvas = new Sideralis3DCanvas(this);
//                my3DCanvas.setFullScreenMode(true);
//                my3DCanvas.init();
//            }
//#endif
            // ===============================
            // ==== Create user interface ====
            createGUI();

            // Flag to indicate that everything is ready (all objects have been created)
            allObjectsCreated = true;
        } else {
            pauseFlag = false;
         }
    }
    /**
     * Called when the application is paused (for ex incomming call)
     */
    protected void pauseApp() {
        pauseFlag = true;
        if (myCanvas != null) {
            myCanvas.stop();
        }
    }
    /**
     * To stop the application
     * @param unconditional
     * @throws MIDletStateChangeException
     */
    protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
        saveData();
        pauseFlag = true;
        if (myCanvas != null) {
            myCanvas.stop();
        }
   }
    /**
     * Create user interface
     * This will be called after having launch the splash screen in order to accelerate start time
     */
    public void createGUI() {
        // Initialize star Canvas

        // Create components
        exitCommand = new Command(LocalizationSupport.getMessage("CMD_EXIT"), Command.EXIT, 99);
        displayCommand = new Command(LocalizationSupport.getMessage("CMD_OPTIONS"), Command.SCREEN, 1);
        positionCommand = new Command(LocalizationSupport.getMessage("CMD_POS_TIME"), Command.SCREEN, 2);
        searchCommand = new Command(LocalizationSupport.getMessage("PARAM_SEARCH"),Command.SCREEN,3);
        dicoCommand = new Command(LocalizationSupport.getMessage("CMD_DICTIONARY"), Command.SCREEN, 4);
        infoCommand = new Command(LocalizationSupport.getMessage("CMD_INFO"), Command.SCREEN, 5);
        langCommand = new Command(LocalizationSupport.getMessage("CMD_LANGUAGE"), Command.SCREEN, 6);
        helpCommand = new Command(LocalizationSupport.getMessage("CMD_HELP"), Command.SCREEN, 7);
        touchScreenCommand = new Command(LocalizationSupport.getMessage("CMD_TOUCH"), Command.SCREEN, 8);

        myCanvas.addCommand(exitCommand);
        myCanvas.addCommand(displayCommand);
        myCanvas.addCommand(positionCommand);
        myCanvas.addCommand(searchCommand);
        myCanvas.addCommand(helpCommand);
        myCanvas.addCommand(infoCommand);
        myCanvas.addCommand(dicoCommand);
        myCanvas.addCommand(langCommand);
        if (myParameter.isSupportTouchScreen())
            myCanvas.addCommand(touchScreenCommand);
        myCanvas.setCommandListener(this);

        // Create a position form
        positionForm = new Form(LocalizationSupport.getMessage("POS_FORM_POSITION"));
        latTextField = new TextField(LocalizationSupport.getMessage("A2"), "0", 15, TextField.DECIMAL);
        longTextField = new TextField(LocalizationSupport.getMessage("A3"), "0", 15, TextField.DECIMAL);
        posStore = new StringItem("",LocalizationSupport.getMessage("POS_FORM_STORE"),StringItem.BUTTON);
        posName = new TextField("",LocalizationSupport.getMessage("POS_FORM_NAME"),15,TextField.NON_PREDICTIVE);
        posStore.setDefaultCommand(new Command(LocalizationSupport.getMessage("POS_FORM_STORE"),Command.ITEM,1));
        pos1Restore = new StringItem("",myParameter.getCity1().getName(),StringItem.BUTTON);
        pos1Restore.setDefaultCommand(new Command(LocalizationSupport.getMessage("POS_FORM_RESTORE"),Command.ITEM,1));
        pos2Restore = new StringItem("",myParameter.getCity2().getName(),StringItem.BUTTON);
        pos2Restore.setDefaultCommand(new Command(LocalizationSupport.getMessage("POS_FORM_RESTORE"),Command.ITEM,1));
        dateField = new DateField(LocalizationSupport.getMessage("A4"), DateField.DATE_TIME);
        realTimeButton = new StringItem("",LocalizationSupport.getMessage("A6"),StringItem.BUTTON);
        realTimeButton.setDefaultCommand(new Command(LocalizationSupport.getMessage("A6"),Command.ITEM,1));
        globeCommand = new Command(LocalizationSupport.getMessage("A8"), Command.SCREEN, 1);
        cityCommand = new Command(LocalizationSupport.getMessage("A9"), Command.SCREEN, 2);
        autoPositionCommand = new Command(LocalizationSupport.getMessage("POS_FORM_LOCATE"), Command.SCREEN, 3);
        cancelCommand = new Command(LocalizationSupport.getMessage("AA"), Command.CANCEL, 1);
        okCommand = new Command(LocalizationSupport.getMessage("OK"), Command.OK, 1);
        positionForm.append(latTextField);
        positionForm.append(longTextField);
        positionForm.append(new Spacer(400, 10));
        positionForm.append(posStore);
        positionForm.append(posName);
        positionForm.append(pos1Restore);
        positionForm.append(pos2Restore);
        positionForm.append(new Spacer(400, 10));
        positionForm.append(dateField);
        positionForm.append(realTimeButton);
        positionForm.addCommand(globeCommand);
        positionForm.addCommand(cityCommand);
        if (myParameter.isSupportGPS())                                         // If API location exists on the phone
            positionForm.addCommand(autoPositionCommand);
        positionForm.addCommand(cancelCommand);
        positionForm.addCommand(okCommand);
        positionForm.setCommandListener(this);
        posStore.setItemCommandListener(this);
        pos1Restore.setItemCommandListener(this);
        pos2Restore.setItemCommandListener(this);
        realTimeButton.setItemCommandListener(this);
        positionForm.setItemStateListener(this);

        // Create a locate me gauge form
        locateMeForm = new Form(LocalizationSupport.getMessage("LOCATE_FORM_LOC"));
        positionGauge = new Gauge(LocalizationSupport.getMessage("LOCATE_FORM_WAIT"), false, Gauge.INDEFINITE, Gauge.CONTINUOUS_RUNNING);
        locateMeForm.addCommand(cancelCommand);
        locateMeForm.append(positionGauge);
        locateMeForm.setCommandListener(this);

        // Create globe canvas
        globeCanvas = new GlobeCanvas(myPosition);
        globeCanvas.addCommand(cancelCommand);
        globeCanvas.addCommand(okCommand);
        globeCanvas.setCommandListener(this);

        // Create a city selection form
        cityForm = new Form(LocalizationSupport.getMessage("AB"));
        cityStringItem = new StringItem(LocalizationSupport.getMessage("AC"), "");
        cityChoiceGroup = new ChoiceGroup(LocalizationSupport.getMessage("AD"), ChoiceGroup.EXCLUSIVE, Position.getCityList(), null);
        cityForm.append(cityStringItem);
        cityForm.append(cityChoiceGroup);
        cityForm.addCommand(cancelCommand);
        cityForm.addCommand(okCommand);
        cityForm.setCommandListener(this);

        // Create a search form
        searchForm = new Form(LocalizationSupport.getMessage("PARAM_SEARCH"));
        searchTextSearch = new TextField(LocalizationSupport.getMessage("TEXT_SEARCH"), null, 30, TextField.NON_PREDICTIVE);
        searchItemSearch = new StringItem(LocalizationSupport.getMessage("PARAM_SEARCH"),"?",StringItem.BUTTON);
        searchItemSearch.setDefaultCommand(new Command(LocalizationSupport.getMessage("PARAM_SEARCH"),Command.ITEM,1));
        searchItemCancelSearch = new StringItem("",LocalizationSupport.getMessage("PARAM_SSEARCH"),StringItem.BUTTON);
        searchItemCancelSearch.setDefaultCommand(new Command(LocalizationSupport.getMessage("PARAM_SSEARCH"),Command.ITEM,1));
        searchForm.append(searchTextSearch);
        searchForm.append(searchItemSearch);
        searchForm.append(searchItemCancelSearch);
        searchItemSearch.setItemCommandListener(this);
        searchItemCancelSearch.setItemCommandListener(this);
        searchForm.setItemStateListener(this);

        // Create a display options form
        displayOptionsForm = new Form(LocalizationSupport.getMessage("AE"));
        displayOptionsStringItem = new StringItem(LocalizationSupport.getMessage("AF"), "");
        displayOptionsChoiceGroup = new ChoiceGroup(LocalizationSupport.getMessage("AG"), ChoiceGroup.MULTIPLE, ConfigParameters.getParamNames(), null);
        displayOptionsTextMaxMag = new TextField(ConfigParameters.getName(ConfigParameters.MAX_MAG), Float.toString(myParameter.getMaxMag()), 4, TextField.DECIMAL);
        displayOptionsForm.append(displayOptionsStringItem);
        displayOptionsForm.append(displayOptionsChoiceGroup);
        displayOptionsForm.append(displayOptionsTextMaxMag);
        displayOptionsForm.addCommand(cancelCommand);
        displayOptionsForm.addCommand(okCommand);
        displayOptionsForm.setCommandListener(this);
        displayOptionsForm.setItemStateListener(this);

        // Create the help form
        helpForm = new Form(LocalizationSupport.getMessage("AH"));
        helpStringItem = new StringItem[]{
                    new StringItem("Sideralis " + version+" b"+build, " © DoX 2005-2010"),
                    new StringItem(LocalizationSupport.getMessage("AJ"), LocalizationSupport.getMessage("AK")),
                    new StringItem("Sideralis ", LocalizationSupport.getMessage("AL")),
                    new StringItem(LocalizationSupport.getMessage("AO")+" 1:", LocalizationSupport.getMessage("AM")),
                    new StringItem(LocalizationSupport.getMessage("AO")+" 2:", LocalizationSupport.getMessage("AN")),
                    new StringItem(LocalizationSupport.getMessage("AX"), "-- sideralis@free.fr -- http://sideralis.free.fr --"),
                };
        for (int i = 0; i < helpStringItem.length; i++) {
            if (i == 3 || i == 5) {// Add a spacer before commands text.
                helpForm.append(new Spacer(400, 10));
            }
            helpForm.append(helpStringItem[i]);
        }
        backCommand = new Command(LocalizationSupport.getMessage("A0"), Command.BACK, 0);
        helpForm.addCommand(backCommand);
        helpForm.setCommandListener(this);

        // Create the info form
        infoForm = new Form(LocalizationSupport.getMessage("AY"));
        infoStringItem = new StringItem[]{
                    new StringItem(LocalizationSupport.getMessage("AZ"), (new Integer(StarCatalogConst.getNumberOfStars()+StarCatalogMag.getNumberOfStars()).toString())),
                    new StringItem(LocalizationSupport.getMessage("B0"), new Integer(ConstellationCatalog.getNumberOfConstellations()).toString()),
                    new StringItem(LocalizationSupport.getMessage("B1"), new Integer(Sky.NB_OF_PLANETS).toString()),
                    new StringItem(LocalizationSupport.getMessage("B2"), "1"),
                    new StringItem(LocalizationSupport.getMessage("B3"), new Integer(MessierCatalog.getNumberOfObjects()).toString()),
                    new StringItem(LocalizationSupport.getMessage("3D"),(myParameter.isSupport3D()==true)?LocalizationSupport.getMessage("YES"):LocalizationSupport.getMessage("NO")),
                    new StringItem(LocalizationSupport.getMessage("GPS"),(myParameter.isSupportGPS()==true)?LocalizationSupport.getMessage("YES"):LocalizationSupport.getMessage("NO")),
                };
        freeMemStringItem = new StringItem(LocalizationSupport.getMessage("FRM"),new Long(Runtime.getRuntime().freeMemory()).toString()+" "+LocalizationSupport.getMessage("BYTES"));
        timeCalculationStringItem = new StringItem("Avg time for calc:","NA");
        timeDisplayStringItem = new StringItem("Avg time for display:","NA");
        for (int i = 0; i < infoStringItem.length; i++) {
            if (i==5)
                infoForm.append(new Spacer(400,10));
            infoForm.append(infoStringItem[i]);
        }
        infoForm.append(freeMemStringItem);
        infoForm.append(timeCalculationStringItem);
        infoForm.append(timeDisplayStringItem);

        infoForm.addCommand(backCommand);
        infoForm.setCommandListener(this);

        // Create the dico form
        dicoForm = new Form(LocalizationSupport.getMessage("B4"));
        dicoStringItem = new StringItem[]{
                    new StringItem(LocalizationSupport.getMessage("B5"), LocalizationSupport.getMessage("B6")),
                    new StringItem(LocalizationSupport.getMessage("B7"), LocalizationSupport.getMessage("B8")),
                    new StringItem(LocalizationSupport.getMessage("B9"), LocalizationSupport.getMessage("BA")),
                    new StringItem(LocalizationSupport.getMessage("BB"), LocalizationSupport.getMessage("BC")),
                    new StringItem(LocalizationSupport.getMessage("BD"), LocalizationSupport.getMessage("BE")),
                    new StringItem(LocalizationSupport.getMessage("BF"), LocalizationSupport.getMessage("BG")),
                    new StringItem(LocalizationSupport.getMessage("BH"), LocalizationSupport.getMessage("BI")),
                    new StringItem(LocalizationSupport.getMessage("BJ"), LocalizationSupport.getMessage("BK")),
                    new StringItem(LocalizationSupport.getMessage("BL"), LocalizationSupport.getMessage("BM")),
                    new StringItem(LocalizationSupport.getMessage("BN"), LocalizationSupport.getMessage("BP")),
                    new StringItem(LocalizationSupport.getMessage("BQ"), LocalizationSupport.getMessage("BR")),
                    new StringItem(LocalizationSupport.getMessage("BS"), LocalizationSupport.getMessage("BU")),
                    new StringItem(LocalizationSupport.getMessage("BT"), LocalizationSupport.getMessage("BV")),
                    new StringItem(LocalizationSupport.getMessage("BW"), LocalizationSupport.getMessage("BX")),
                    new StringItem(LocalizationSupport.getMessage("BY"), LocalizationSupport.getMessage("BZ")),
                };
        for (int i = 0; i < dicoStringItem.length; i++) {
            dicoForm.append(dicoStringItem[i]);
        }
        dicoForm.addCommand(backCommand);
        dicoForm.setCommandListener(this);

        // Create the language form
        langForm = new Form(LocalizationSupport.getMessage("LANG_FORM_TITLE"));
        langStringItem = new StringItem(LocalizationSupport.getMessage("LANG_FORM_SELECT"), "");
        langChoiceGroup = new ChoiceGroup(LocalizationSupport.getMessage("LANG_FORM_LANG"), ChoiceGroup.EXCLUSIVE, LocalizationSupport.getLanguages(), null);
        langForm.append(langStringItem);
        langForm.append(langChoiceGroup);
        langForm.addCommand(cancelCommand);
        langForm.addCommand(okCommand);
        langForm.setCommandListener(this);

        // Create the touch screen form
        if (myParameter.isSupportTouchScreen()) {
            touchScreenForm = new Form(LocalizationSupport.getMessage("TOUCH_FORM_TITLE"));
            sensitivityGauge = new Gauge(LocalizationSupport.getMessage("TSSENSE"), true, 80, myParameter.getSensitivityTouchScreen());
            inertiaGauge = new Gauge(LocalizationSupport.getMessage("TSINERTIA"), true, 10, (int)(myParameter.getInertiaTouchScreen()*10)-10);
            scrollSpeedHorizonGauge = new Gauge(LocalizationSupport.getMessage("TSSPHOR"), true, 99, 100-myParameter.getScrollSpeedHorizonTouchScreen());
            scrollSpeedHorZenithGauge = new Gauge(LocalizationSupport.getMessage("TSSPHZEN"), true, 99, 100-myParameter.getScrollSpeedHorZenithTouchScreen());
            scrollSpeedVerZenithGauge = new Gauge(LocalizationSupport.getMessage("TSSPVZEN"), true, 99, 100-myParameter.getScrollSpeedVerZenithTouchScreen());
            skipDragEventGauge = new Gauge("To be tested", true, 200, myParameter.getMaxTimeDragEventTouchScreen());
            touchScreenForm.append(sensitivityGauge);
            touchScreenForm.append(inertiaGauge);
            touchScreenForm.append(scrollSpeedHorizonGauge);
            touchScreenForm.append(scrollSpeedHorZenithGauge);
            touchScreenForm.append(scrollSpeedVerZenithGauge);
            touchScreenForm.append(skipDragEventGauge);
            touchScreenForm.addCommand(cancelCommand);
            touchScreenForm.addCommand(okCommand);
            touchScreenForm.setCommandListener(this);
        }
    }
    /**
     * React to user's choice.
     * @param c the command of the user
     * @param d the display
     */
    public void commandAction(Command c, Displayable d) {
        // EXIT command
        if (c == exitCommand) {
            try {
                // Exit
                destroyApp(true);
                notifyDestroyed();
             } catch (MIDletStateChangeException ex) {
                ex.printStackTrace();
            }
        // === Form selection commands ===
        } else if (c == displayCommand) {
            // Display display options
            displayOptionsChoiceGroup.setSelectedFlags(myParameter.getSelectedFlags());
            displayOptionsTextMaxMag.setString(String.valueOf(myParameter.getMaxMag()));
            myDisplay.setCurrent(displayOptionsForm);
        } else if (c == searchCommand) {
            // Search command
            searchTextSearch.setString("");
            searchItemSearch.setText(mySearch.getNameOfSearchableObject(mySearch.getIndex()));
            myDisplay.setCurrent(searchForm);
        } else if (c == positionCommand) {
            try {
                // Store current time in case of cancellation.
                myOffsetForCancellation = myPosition.getTemps().getTimeOffset();
                // Update date before display
                myPosition.getTemps().adjustDate();
                dateField.setDate(myPosition.getTemps().getMyDate());
                latTextField.setString(new Double(myPosition.getLatitude()).toString());
                longTextField.setString(new Double(myPosition.getLongitude()).toString());

                myDisplay.setCurrent(positionForm);
            } catch (Exception e) {
                e.printStackTrace();
            }
//#ifdef JSR179
        } else if (c == autoPositionCommand) {
            tempPos = new Position();
            whereAmI = new LocateMe(tempPos, this);
            whereAmI.start();
            myDisplay.setCurrent(locateMeForm);
//#endif
        } else if (c == cityCommand) {
            // To select city selection
            cityChoiceGroup.setSelectedIndex(myPosition.getSelectedCity(), true);
            myDisplay.setCurrent(cityForm);
        } else if (c == globeCommand) {
            // To select position from globe
            globeCanvas.setPosition(myPosition);
            myDisplay.setCurrent(globeCanvas);
        } else if (c == helpCommand) {
            // To select help
            myDisplay.setCurrent(helpForm);
        } else if (c == infoCommand) {
            // To select info
            freeMemStringItem.setText(new Long(Runtime.getRuntime().freeMemory()).toString()+" "+LocalizationSupport.getMessage("BYTES"));
            timeCalculationStringItem.setText(new Long(myParameter.getTimeCalculate()).toString() + " ms");
            timeDisplayStringItem.setText(new Long(myParameter.getTimeDisplay()).toString() + " ms");
            myDisplay.setCurrent(infoForm);
        } else if (c == dicoCommand) {
            // To select info
            myDisplay.setCurrent(dicoForm);
        } else if (c == langCommand) {
            // To select language
            langChoiceGroup.setSelectedIndex(getLangAsInt(), true);
            myDisplay.setCurrent(langForm);
        } else if (c == touchScreenCommand) {
            // To configure touch screen
            sensitivityGauge.setValue(myParameter.getSensitivityTouchScreen());
            inertiaGauge.setValue((int)(myParameter.getInertiaTouchScreen()*10)-10);
            scrollSpeedHorizonGauge.setValue(100-myParameter.getScrollSpeedHorizonTouchScreen());
            scrollSpeedHorZenithGauge.setValue(100-myParameter.getScrollSpeedHorZenithTouchScreen());
            scrollSpeedVerZenithGauge.setValue(100-myParameter.getScrollSpeedVerZenithTouchScreen());
            skipDragEventGauge.setValue(myParameter.getMaxTimeDragEventTouchScreen());
            myDisplay.setCurrent(touchScreenForm);

        // === CANCEL command ===
        } else if (c == cancelCommand) {
            // To come back
            if (myDisplay.getCurrent() == globeCanvas) {
                myDisplay.setCurrent(positionForm);
            } else if (myDisplay.getCurrent() == positionForm) {
                myPosition.getTemps().setTimeOffset(myOffsetForCancellation);
                myDisplay.setCurrent(myCanvas);
            } else if (myDisplay.getCurrent() == cityForm) {
                myDisplay.setCurrent(positionForm);
            } else if (myDisplay.getCurrent() == displayOptionsForm) {
                myDisplay.setCurrent(myCanvas);
            } else if (myDisplay.getCurrent() == langForm) {
                myDisplay.setCurrent(myCanvas);
//#ifdef JSR179
            } else if (myDisplay.getCurrent() == locateMeForm) {
                whereAmI = null;
                myDisplay.setCurrent(positionForm);
//#endif
            } else if (myDisplay.getCurrent() == touchScreenForm) {
                myDisplay.setCurrent(myCanvas);
            }
        // === OK command ===
        } else if (c == okCommand) {
            if (myDisplay.getCurrent() == positionForm) {
                // Select lat and long from data.
                myPosition.setLatitude(Double.parseDouble(latTextField.getString()));
                myPosition.setLongitude(Double.parseDouble(longTextField.getString()));
                myCanvas.setCounter(0);                                         // To activate a calculation position
                myDisplay.setCurrent(myCanvas);
                saveData();
            } else if (myDisplay.getCurrent() == cityForm) {
                // Select lat and long from city
                int selCity;
                selCity = cityChoiceGroup.getSelectedIndex();
                myPosition.setCity(selCity);
                latTextField.setString(new Double(myPosition.getLatitude()).toString());
                longTextField.setString(new Double(myPosition.getLongitude()).toString());
                myDisplay.setCurrent(positionForm);
            } else if (myDisplay.getCurrent() == globeCanvas) {
                myPosition.setLatitude(globeCanvas.getLatitude());
                myPosition.setLongitude(globeCanvas.getLongitude());
                latTextField.setString(new Double(myPosition.getLatitude()).toString());
                longTextField.setString(new Double(myPosition.getLongitude()).toString());
                myDisplay.setCurrent(positionForm);
            } else if (myDisplay.getCurrent() == displayOptionsForm) {
                boolean[] b = new boolean[displayOptionsChoiceGroup.size()];
                displayOptionsChoiceGroup.getSelectedFlags(b);
                myParameter.setSelectedFlags(b);
                myCanvas.updateColorAndLight();
                float mm = 0;
                if (displayOptionsTextMaxMag.getString().length()!=0) {
                    mm = Float.parseFloat(displayOptionsTextMaxMag.getString());
                    if (mm>5)
                        mm = 5f;
                }
                myParameter.setMaxMag(mm);
                myDisplay.setCurrent(myCanvas);
                saveData();
            } else if (myDisplay.getCurrent() == langForm) {
                int selLang;
                selLang = langChoiceGroup.getSelectedIndex();
                setLang(selLang);
                Alert langAlert = new Alert(LocalizationSupport.getMessage("LANG_FORM_WARNING"), LocalizationSupport.getMessage("LANG_FORM_WARNING2"), null, AlertType.INFO);
                langAlert.setTimeout(Alert.FOREVER);
                myDisplay.setCurrent(langAlert, myCanvas);
                saveLanguage();
            } else if (myDisplay.getCurrent() == helpForm) {
                myDisplay.setCurrent(myCanvas);
            } else if (myDisplay.getCurrent() == touchScreenForm) {
                myParameter.setSensitivityTouchScreen(sensitivityGauge.getValue());
                myParameter.setInertiaTouchScreen((float)(inertiaGauge.getValue()+10)/10);
                myParameter.setScrollSpeedHorizonTouchScreen(100-scrollSpeedHorizonGauge.getValue());
                myParameter.setScrollSpeedHorZenithTouchScreen(100-scrollSpeedHorZenithGauge.getValue());
                myParameter.setScrollSpeedVerZenithTouchScreen(100-scrollSpeedVerZenithGauge.getValue());
                myParameter.setMaxTimeDragEventTouchScreen(skipDragEventGauge.getValue());
                myDisplay.setCurrent(myCanvas);
                saveData();
            }
        } else if (c == backCommand) {
            myDisplay.setCurrent(myCanvas);
        }
    }

    /**
     * React to user's choice.
     * @param c the command of the user
     * @param i the item
     */
    public void commandAction(Command c, Item i) {
        CityObject c1,c2;
        // Save position
        if (i==posStore) {
            // Store city 1 in city 2 and store new city in city 1
            c2 = myParameter.getCity1();
            myParameter.setCity2(c2);
            c1 = new CityObject(posName.getString(),Double.parseDouble(latTextField.getString()),Double.parseDouble(longTextField.getString()));
            myParameter.setCity1(c1);
            pos1Restore.setText(c1.getName());
            pos2Restore.setText(c2.getName());
        }
        // Restore saved position 1
        if (i==pos1Restore) {
            // Set new latitude and longitude according to position 1
            c1 = myParameter.getCity1();
            myPosition.setLatitude(c1.getLatitude());
            myPosition.setLongitude(c1.getLongitude());
            latTextField.setString(new Double(myPosition.getLatitude()).toString());
            longTextField.setString(new Double(myPosition.getLongitude()).toString());
        }
        // Restore saved position 2
        if (i==pos2Restore) {
            c2 = myParameter.getCity2();
            myPosition.setLatitude(c2.getLatitude());
            myPosition.setLongitude(c2.getLongitude());
            latTextField.setString(new Double(myPosition.getLatitude()).toString());
            longTextField.setString(new Double(myPosition.getLongitude()).toString());
        }
        // Set time as current time
        if (i==realTimeButton) {
            // The user pressed Current time
            myPosition.getTemps().setTimeOffset(0);
            myPosition.getTemps().adjustDate();
            dateField.setDate(myPosition.getTemps().getMyDate());
        }
        // Activate search
        if (i==searchItemSearch) {
            if (tmpIndex != -1) {
                short res;
                // Only if an object has been found
                mySearch.clearHighlight();
                mySearch.setIndex(tmpIndex);
                res = mySearch.setHighlight(searchItemSearch.getText());
                if (res==Search.FOUND_NOT_VISIBLE) {
                    Alert visibleAlert = new Alert("", LocalizationSupport.getMessage("POS_ALERT_VIS"), null, AlertType.INFO);
                    visibleAlert.setTimeout(Alert.FOREVER);
                    myDisplay.setCurrent(visibleAlert, searchForm);
                } else {
                    myDisplay.setCurrent(myCanvas);
                }
            }
        }
        // Cancel Search
        if (i==searchItemCancelSearch) {
            mySearch.setIndex(-1);
            tmpIndex = -1;
            mySearch.clearHighlight();
            searchTextSearch.setString("");
            searchItemSearch.setText(mySearch.getNameOfSearchableObject(-1));
            myDisplay.setCurrent(myCanvas);
        }
    }

    /**
     * Called when user change time or change of method of selecting time
     * @param i the item which has been changed
     */
    public void itemStateChanged(Item i) {
 
        if (i == dateField) {
            // ===========================================================
            // === The user changed time, so we are now in custom time ===
            myPosition.getTemps().calculateTimeOffset(dateField.getDate());
        }
        if (i == searchTextSearch) {
            // ========================================================================
            // === Get name of searched object (remove proposition if there is one) ===
            String tmp = searchTextSearch.getString().toLowerCase().trim();
            // If name is not empty
            if (!tmp.equals("")) {
                tmpIndex = mySearch.search(tmp);
            } else {
                tmpIndex = -1;
            }
            searchItemSearch.setText(mySearch.getNameOfSearchableObject(tmpIndex));
        }
    }

//#ifdef JSR179
    /**
     * To finish gauge for locate me position
     */
    public void EndOfLocateMe() {
        latTextField.setString(new Double(tempPos.getLatitude()).toString());
        longTextField.setString(new Double(tempPos.getLongitude()).toString());
        myDisplay.setCurrent(positionForm);
        whereAmI = null;
    }
//#endif

    /**
     * Called when the user is pressing a key or when the user is touching the screen
     * Will move to the main canvas
     */
    public void endOfSplash() {
        myDisplay.setCurrent(myCanvas);
    }
    /**
     * Move to the next display
     */
    public void nextDisplay() {
//#ifdef JSR184
//        if (myDisplay.getCurrent() == my3DCanvas)
//            myDisplay.setCurrent(myCanvas);
//        else if (myDisplay.getCurrent() == myCanvas)
//            myDisplay.setCurrent(my3DCanvas);
//#endif
    }
    /**
     * Return a boolean indicating if all objects have been created
     * @return true if all objects have not yet been created (in startApp()), false if they have all been created
     */
    public boolean isAllObjectsCreated() {
        return allObjectsCreated;
    }

    /**
     * Return my sky object
     * @return mySky object
     */
    public Sky getMySky() {
        return mySky;
    }
    /**
     * Return the object describing all parameters
     * @return the parameter object
     */
    public ConfigParameters getMyParameter() {
        return myParameter;
    }
    /**
     * Return the search object
     * @return mySearch object
     */
    public Search getSearch() {
        return mySearch;
    }
    /**
     * Return the user position
     * @return the position of user
     */
    public Position getMyPosition() {
        return myPosition;
    }

    /**
     * Return the language selected during run time
     * @return an index in the table of all possible languages supported by Sideralis
     */
    public int getLangAsInt() {
        return lang;
    }
   /**
     * Return the language used to display all text messages
     * @return "" for default, "fr_FR" for French, "it_IT" for Italian
     */
    public String getLang() {
        String ret = "";

        switch (lang) {
            case 0:
                ret = "";
                break;
            case 1:
                ret = "fr_FR";
                break;
            case 2:
                ret = "it_IT";
                break;
            case 3:
                ret = "es_ES";
                break;
            case 4:
                ret = "pt_PT";
                break;
            case 5:
                ret = "de_DE";
                break;
            case 6:
                ret = "pl_PL";
                break;
            case 7:
                ret = "cs_CZ";
                break;
        }
        return ret;
    }
    /**
     * Set parameter language
     * @param selLang language used during run time
     */
    public void setLang(int selLang) {
        lang = selLang;
    }
    /**
     *
     */
    public void setLang() {
        String s = System.getProperty("microedition.locale");
        if (s.startsWith("fr")) {
            lang = 1;
        } else if (s.startsWith("it")) {
            lang = 2;
        } else if (s.startsWith("es")) {
            lang = 3;
        } else if (s.startsWith("pt")) {
            lang = 4;
        } else if (s.startsWith("de")) {
            lang = 5;
        } else if (s.startsWith("pl")) {
            lang = 6;
        } else if (s.startsWith("cs")) {
            lang = 7;
        } else {
            lang = 0;
        }
    }
    /**
     * Save position data in a record store so position will be used again when started again
     */
    private void saveData() {
        RecordStore rs;
        int count, i;
        byte[] b;
        boolean[] bParam;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(bout);

        try {
            dout.writeUTF(version);
            dout.writeDouble(myPosition.getLatitude());
            dout.writeDouble(myPosition.getLongitude());
            dout.writeLong((myPosition.getTemps().getTimeOffset()));
            dout.writeBoolean(myParameter.isHorizontalView());
            dout.writeFloat(myParameter.getMaxMag());
            dout.writeUTF(myParameter.getCity1().getName());
            dout.writeDouble(myParameter.getCity1().getLatitude());
            dout.writeDouble(myParameter.getCity1().getLongitude());
            dout.writeUTF(myParameter.getCity2().getName());
            dout.writeDouble(myParameter.getCity2().getLatitude());
            dout.writeDouble(myParameter.getCity2().getLongitude());
            dout.writeInt(myParameter.getSensitivityTouchScreen());
            dout.writeFloat(myParameter.getInertiaTouchScreen());
            dout.writeInt(myParameter.getScrollSpeedHorizonTouchScreen());
            dout.writeInt(myParameter.getScrollSpeedHorZenithTouchScreen());
            dout.writeInt(myParameter.getScrollSpeedVerZenithTouchScreen());
            dout.writeInt(myParameter.getMaxTimeDragEventTouchScreen());
            dout.writeFloat(myParameter.getRotView());
            bParam = myParameter.getSelectedFlags();
            for (i = 0; i < bParam.length; i++) {
                dout.writeBoolean(bParam[i]);
            }
            b = bout.toByteArray();
            dout.close();
            bout.close();

            rs = RecordStore.openRecordStore("position", true);
            count = rs.getNumRecords();
            if (count == 0) {
                rs.addRecord(b, 0, b.length);
            } else {
                rs.setRecord(1, b, 0, b.length);
            }

            rs.closeRecordStore();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }
    /**
     * Load position which were saved at last exit
     */
    private void loadData() {
        RecordStore rs;
        byte[] b;
        boolean[] bParam;
        boolean bb;
        double d,la,lo;
        float f;
        long l;
        String sv;
        CityObject c;

        int i;

        try {
            rs = RecordStore.openRecordStore("position", true);
            b = rs.getRecord(1);                                                // will go to InvalideRecordIDException if record has not been created
            ByteArrayInputStream bin = new ByteArrayInputStream(b);
            DataInputStream din = new DataInputStream(bin);
            sv = din.readUTF();
            if (sv.equals(version)) {
                d = din.readDouble();
                myPosition.setLatitude(d);                                      // Set latitude
                d = din.readDouble();
                myPosition.setLongitude(d);                                     // Set longitude
                l = din.readLong();
                myPosition.getTemps().setTimeOffset(l);                         // Set time offset
                bb = din.readBoolean();
                myParameter.setHorizontalView(bb);
                f = din.readFloat();                                            // Set max mag display
                myParameter.setMaxMag(f);
                sv = din.readUTF();
                la = din.readDouble();
                lo = din.readDouble();
                c = new CityObject(sv,la,lo);
                myParameter.setCity1(c);                                        // Set store city1
                sv = din.readUTF();
                la = din.readDouble();
                lo = din.readDouble();
                c = new CityObject(sv,la,lo);
                myParameter.setCity2(c);                                        // Set store city2
                i = din.readInt();
                myParameter.setSensitivityTouchScreen(i);                       // Set sensitivity
                f =din.readFloat();
                myParameter.setInertiaTouchScreen(f);                           // Set inertia
                i =din.readInt();
                myParameter.setScrollSpeedHorizonTouchScreen(i);                // Set scroll speed horizon view
                i =din.readInt();
                myParameter.setScrollSpeedHorZenithTouchScreen(i);              // Set scroll speed hor in zenith view
                i =din.readInt();
                myParameter.setScrollSpeedVerZenithTouchScreen(i);              // Set scroll speed ver in zenith view
                i =din.readInt();
                myParameter.setMaxTimeDragEventTouchScreen(i);                  //
                f = din.readFloat();
                myParameter.setRotView(f);
                bParam = myParameter.getSelectedFlags();
                for (i = 0; i < bParam.length; i++) {
                    bb = din.readBoolean();
                    bParam[i] = bb;
                }
                myParameter.setSelectedFlags(bParam);
            }

            din.close();
            bin.close();
            rs.closeRecordStore();
        } catch (Exception e) {
            // File not found - logical if it is the first launch of Sideralis
            //e.printStackTrace();
        }
    }
    /**
     * Load language
     * @return true if a language has been loaded, false if not language file stored previously
     */
    private boolean loadLanguage() {
        RecordStore rs;
        int i;
        byte[] b;
        String sv;

        try {
            rs = RecordStore.openRecordStore("language", true);
            b = rs.getRecord(1);                                                // will go to InvalideRecordIDException if record has not been created
            ByteArrayInputStream bin = new ByteArrayInputStream(b);
            DataInputStream din = new DataInputStream(bin);
            sv = din.readUTF();
            if (sv.equals(version)) {
                i = din.readInt();
                setLang(i);
            }

            din.close();
            bin.close();
            rs.closeRecordStore();
        } catch (InvalidRecordIDException e) {
            return false;
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return true;
    }

    /**
     * Save language used
     */
    private void saveLanguage() {
        RecordStore rs;
        int count;
        byte[] b;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(bout);

        try {
            dout.writeUTF(version);
            dout.writeInt(getLangAsInt());
            b = bout.toByteArray();
            dout.close();
            bout.close();

            rs = RecordStore.openRecordStore("language", true);
            count = rs.getNumRecords();
            if (count == 0) {
                rs.addRecord(b, 0, b.length);
            } else {
                rs.setRecord(1, b, 0, b.length);
            }
            rs.closeRecordStore();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }
}
