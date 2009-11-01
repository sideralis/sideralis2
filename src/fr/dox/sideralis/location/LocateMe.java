/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.dox.sideralis.location;

import fr.dox.sideralis.Sideralis;
import javax.microedition.location.Coordinates;
import javax.microedition.location.Criteria;
import javax.microedition.location.Location;
import javax.microedition.location.LocationProvider;


/**
 *
 * @author Bernard
 */
public class LocateMe extends Thread {
    private final Position rMyPosition;
    private boolean ended;
    private final Sideralis myMidlet;

    /**
     * Constructor
     * @param pos the position of the user
     * @param app the calling midlet
     */
    public LocateMe(Position pos,Sideralis app) {
        rMyPosition = pos; 
        ended = false;
        myMidlet = app;        
    }
    /**
     * This class has to be a thread to avoid blocking calling thread
     */
    public void run() {
        try {

            // Create a Criteria object for defining desired selection criteria
            Criteria cr = new Criteria();
            // Specify horizontal accuracy of 500 meters, leave other parameters 
            // at default values.
            cr.setPreferredResponseTime(30000);

            LocationProvider lp = LocationProvider.getInstance(cr);

            // get the location, one minute timeout
            Location l = lp.getLocation(60);

            Coordinates c = l.getQualifiedCoordinates();

            if (c != null) {
                // use coordinate information
                rMyPosition.setLatitude(c.getLatitude());
                rMyPosition.setLongitude(c.getLongitude());
            }
            ended = true;
        } catch (Exception e) {
            // not able to retrive location information
            e.printStackTrace();
        }
        if (ended)
            myMidlet.EndOfLocateMe();
    }    
}
