/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.dox.sideralis;

import javax.microedition.lcdui.Graphics;

/**
 *
 * @author Bernard
 */
public class DebugOutput {
    private static final int SIZE = 20;
    private static String[] stringTable = new String[SIZE];
    private static int idx = 0;
    private static boolean once = true;

    public static void store (String s) {
        stringTable[idx++] = s;
        if (idx == SIZE) {
            for (int i=1;i<SIZE;i++)
                stringTable[i-1] = stringTable[i];
            idx = SIZE-1;
        }
    }

    public static void storeOnce(String s){
        if (once) {
            store(s);
            once = false;
        }
    }

    public static void reset() {
        once = true;
    }

    public static void println(Graphics g) {
        g.setColor(0xffffff);
        for (int i=0;i<SIZE;i++) {
            if (stringTable[i] != null)
                g.drawString(stringTable[i],0,i*12,Graphics.TOP|Graphics.LEFT);
        }
    }
}
