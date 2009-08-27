package fr.dox.sideralis.location;

import java.util.*;

/**
 * This class represents an object representing the Temps used in this midlet.
 * Once the object is created, it is possible to get the jour julien or the sideral hour.
 * @author Bernard
 */
public class Temps {    
    /** The jour julien */
    private double jourJulien;
    /** Sideral hour */
    private double sideralHour;
    /** The date */
    private Calendar myDate;
    /** The offset between current time and local time */
    private long offsetTime;

    /** 
     * Creates a new instance of Temps 
     */
    public Temps() {        
        //        myDate = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        myDate = Calendar.getInstance(/*TimeZone.getDefault()*/);
        offsetTime = 0;
    }

//    public String getInfo() {
//        String ret;
//        TimeZone tz;
//        tz = TimeZone.getDefault();
//        ret = "ID:"+tz.getID() +" DayLight:"+tz.useDaylightTime()+" Offset:"+tz.getRawOffset();
//        return ret;
//    }
    /** 
     * Return the Jour Julien 
     * @return The jour julien
     */
    public double getJJ() {
        return jourJulien;
    }
    /** 
     * Return the Greenwitch sideral hour
     * @return the sideral hour
     */
    public double getHS() {
        return sideralHour;
    }
    /**
     * Return the local sideral hour
     * @param pos the position of user
     * @return the local sideral hour
     */
    public double getLocalHS(Position pos) {
        double hs = sideralHour;
        hs += pos.getLongitude()/15;               // Local sidereal time
        hs = hs%24;
        if (hs<0)
            hs+=24;
        return hs;
    }
    /** 
     * Return the Temps des Ephemerides
     * T = (JJ-2415020)/36525
     * @return temps des ephemerides
     */
    public double getT() {
        double T;
        T = (jourJulien-2415020.0)/36525.0;
        return T;
    }
    /**
     * return the temps as String
     * @return a string representing the temps
     */
    public String toString() {
        String res;
        res = new String(myDate.get(Calendar.DAY_OF_MONTH)+"/"+(myDate.get(Calendar.MONTH)+1)+"/"+myDate.get(Calendar.YEAR)+" "+myDate.get(Calendar.HOUR_OF_DAY)+":"+myDate.get(Calendar.MINUTE)+":"+myDate.get(Calendar.SECOND));
        return res;
    }
    /**
     * Return the variable representing the date and time of the application.
     * @return the myDate field
     */
    public Calendar getDate() {        
        return myDate;
    }
    /** 
     * Return the time offset
     * @return time offset
     */
    public long getTimeOffset() {
        return offsetTime;
    }
    /** 
     * set the new value of the time offset
     * @value the new time offset
     */
    public void setTimeOffset(long v) {
        offsetTime = v;
    }
    /**
     * Calculate the time offset between current time and requested time
     * @param d the requested time.
     */
    public void calculateTimeOffset(Date d) {
        myDate = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        Date date = myDate.getTime();
        long lDate = date.getTime();
        offsetTime = d.getTime()-lDate;
        //System.out.println("Time offset: "+offsetTime);                         // DBG
    }
    /**
     * Calculate date and time according to time offset and current date and time.
     */
    public void adjustDate() {
        myDate = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        Date date = myDate.getTime();
        long lDate = date.getTime();
        lDate += offsetTime ;                                                   // Add time offset from current date.
        date.setTime(lDate);
        myDate.setTime(date);          // update calendar        
//        System.out.println("Date/time: "+myDate.getTime().toString());          // DBG
    }
    /**
     * Calculates the Jour Julien
     */
    public void calculateJourJulien() {
        int A,B,C,D;
        int year,month;
        double hour;

        // Calcul du jour julien
        year = myDate.get(Calendar.YEAR);
        month = myDate.get(Calendar.MONTH)+12-Calendar.DECEMBER;                // returned values is starting from 0
        hour = myDate.get(Calendar.HOUR_OF_DAY)+myDate.get(Calendar.MINUTE)/60.0+myDate.get(Calendar.SECOND)/3600.0;
        if (month<3) {
            month+=12;
            year-=1;
        }
        A = year/100;
        B = 2-A+A/4;
        C = (int)((float)365.25*(float)year);
        D = (int)((float)30.6001 * (float)(month+1));
        jourJulien = B + C + D + myDate.get(Calendar.DAY_OF_MONTH) + hour/24 + 1720994.5;        
    }
    /**
     * Calculates the sideral hour
     */
    public void calculateHS() {
        double T,HSH;
        double h;
        double m;
        double jj0;
        
        h = myDate.get(Calendar.HOUR_OF_DAY);   
        m = myDate.get(Calendar.MINUTE)+myDate.get(Calendar.SECOND)/60.0;

        jj0 = (int)(jourJulien-0.5)+0.5;
        // Temps sideral a Greenwich pour 0h UT
        T = (jj0-2415020.0)/36525.0;
        HSH = 0.276919398+(0.000001075*T+100.0021359)*T;
        sideralHour = (HSH-(int)HSH)*24;
        
        // Temps sideral � Greenwich pour une heure donn�e
        sideralHour = (h+m/60.0)*1.002737908 + sideralHour;
        while (sideralHour>24.0)
            sideralHour -= 24.0;
    }
    /**
     * Add minutes to the current time
     * @param m number of minute to add
     */
    public void addMinute(int m) {
         Date date = myDate.getTime();
         long lDate = date.getTime();
         lDate += m*60*1000 ;           // Add m minute from current date.
         date.setTime(lDate);
         myDate.setTime(date);          // update calendar
    }
}
