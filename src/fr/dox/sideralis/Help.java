/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.dox.sideralis;

import fr.dox.sideralis.view.color.Color;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

/**
 * The help class. Used to display the help when pressing #
 * @author gautier
 */
public class Help {

    /** The text associated to each keys */
    private String[] keyText;
    /** The height and width of the display */
    private int getHeight, getWidth;
    /** The position and size of the virtual keypad. The size of each key of this virtual keypad */
    private int x,y,w,h,keyHeight,keyWidth;
    /** boolean indicating if help must be displayed or not*/
    private boolean displayed;
    /** The calling midlet */
    private final Sideralis myMidlet;

    /** All keys 0-9 + '*' + '#'+ 4 directions */
    private static final short NB_OF_KEYS = 12;
    /** The index of keys */
    public static final int ONE = 0;
    public static final int TWO = 1;
    public static final int THREE = 2;
    public static final int FOUR = 3;
    public static final int FIVE = 4;
    public static final int SIX = 5;
    public static final int SEVEN = 6;
    public static final int EIGHT = 7;
    public static final int NINE = 8;
    public static final int STAR = 9;
    public static final int ZERO = 10;
    public static final int HASH = 11;

    /**
     * The basic constructor
     * All help text is initialized by empty string
     */
    public Help(Sideralis myMidlet) {
        keyText = new String[NB_OF_KEYS];
        for (int i = 0; i < NB_OF_KEYS; i++) {
            keyText[i] = "";
        }
        displayed = false;
        this.myMidlet = myMidlet;
    }
    /**
     * The constructor
     * @param text A table of string defining the meaning of each key
     */
    public Help(Sideralis myMidlet, String[] text) {
        keyText = new String[NB_OF_KEYS];
        for (int i = 0; i < NB_OF_KEYS; i++) {
            keyText[i] = new String(text[i]);
        }
        displayed = false;
        this.myMidlet = myMidlet;
    }
    /**
     * Set the help text of a given key
     * @param i the index of the key
     * @param text the help text of this key
     */
    public void setText(int i, String text) {
        keyText[i] = new String(text);
    }
    /**
     * Give and set dimension of the display
     * @param width the width of the display
     * @param height the height of the display
     */
    public void setView(int width, int height) {
        getWidth = width;
        getHeight = height;

        h = getHeight * 9 / 10;                                                 // The height of the help drawing is taking 9/10 of the whole screen
        w = h * 3 / 4;                                                          // The width of the help drawing is equal to 3/4 of the height

        x = getWidth / 2 - w / 2;                                               // x origin of the help draw
        y = getHeight / 2 - h / 2;                                              // y origin of the help draw

        keyWidth = w / 3;
        keyHeight = h / 4;
    }
    /**
     * Indicate if the help on key meaning is displayed or not
     * @return true if help for keys meaning is displayed, else false
     */
    public boolean isDisplayed() {
        return displayed;
    }
    /**
     * To set or unset the display of the help
     * @param displayed true to make help appears, else false
     */
    public void setDisplayed(boolean displayed) {
        this.displayed = displayed;
    }

    /**
     * Draw the virtual keypad with the meaning of each key.
     * @param g the graphics object on which we draw
     */
    public void draw(Graphics g) {
        String s;
        Font myFont;

        myFont = g.getFont();
        // Clear background of virtual keypad
        g.setColor(0);
        g.fillRect(x, y, w, h);

        // Draw the 12 keys: 0-9 + # + *
        g.setColor(myMidlet.getMyParameter().getColor()[Color.COL_KEYBOARD]);
        for (int i = 0; i < 12; i++) {
            g.drawRoundRect(x + (i % 3) * keyWidth, y + (i / 3) * keyHeight, keyWidth, keyHeight,15,15);
            if (i == STAR) {
                s = new String("*");
            } else if (i == HASH) {
                s = new String("#");
            } else if (i == ZERO) {
                s = new String("0");
            } else {
                s = new Integer((i + 1) % 10).toString();
            }
            // Draw the key
            g.drawString(s, x + (i % 3) * keyWidth + keyWidth / 2, y + (i / 3) * keyHeight, Graphics.HCENTER | Graphics.TOP);
            // Draw the help text associated to the key
            if (myFont.stringWidth(keyText[i])<keyWidth)
                g.drawString(keyText[i], x + (i % 3) * keyWidth + keyWidth / 2, y + (i / 3) * keyHeight + keyHeight / 2, Graphics.HCENTER | Graphics.TOP);
            else {
                int pos1 = keyText[i].indexOf(' ');
                int pos2 = keyText[i].indexOf(' ',pos1+1);
                if ((pos2 == -1) & (pos1 != -1)) {
                    g.drawString(keyText[i].substring(0, pos1), x + (i % 3) * keyWidth + keyWidth / 2, y + (i / 3) * keyHeight + keyHeight / 2, Graphics.HCENTER | Graphics.BOTTOM);
                    g.drawString(keyText[i].substring(pos1+1, keyText[i].length()), x + (i % 3) * keyWidth + keyWidth / 2, y + (i / 3) * keyHeight + keyHeight / 2 + myFont.getHeight(), Graphics.HCENTER | Graphics.BOTTOM);
                } else if (pos2 != -1) {
                    g.drawString(keyText[i].substring(0, pos1), x + (i % 3) * keyWidth + keyWidth / 2, y + (i / 3) * keyHeight + myFont.getHeight(), Graphics.HCENTER | Graphics.TOP);
                    g.drawString(keyText[i].substring(pos1+1, pos2), x + (i % 3) * keyWidth + keyWidth / 2, y + (i / 3) * keyHeight + 2*myFont.getHeight(), Graphics.HCENTER | Graphics.TOP);
                    g.drawString(keyText[i].substring(pos2+1, keyText[i].length()), x + (i % 3) * keyWidth + keyWidth / 2, y + (i / 3) * keyHeight + 3*myFont.getHeight(), Graphics.HCENTER | Graphics.TOP);
                }
            }
        }
    }
}
