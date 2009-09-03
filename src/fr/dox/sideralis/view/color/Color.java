/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.dox.sideralis.view.color;

/**
 *
 * @author Bernard
 */
public abstract class Color {

    public static final short COL_BACKGROUND = 0,
            COL_HELP = COL_BACKGROUND + 1,
            COL_MOON = COL_HELP + 1,
            COL_SUN = COL_MOON + 1,
            COL_PLANET = COL_SUN + 1,
            COL_VENUS = COL_PLANET + 1,
            COL_MARS = COL_VENUS + 1,
            COL_JUPITER = COL_MARS + 1,
            COL_SATURN = COL_JUPITER + 1,
            COL_HISTORY = COL_SATURN + 1,
            COL_INFO = COL_HISTORY + 1,
            COL_ANGLE = COL_INFO + 1,
            COL_ZENITH_BACKGROUND = COL_ANGLE + 1,
            COL_ZENITH_EDGE = COL_ZENITH_BACKGROUND + 1,
            COL_CROSS = COL_ZENITH_EDGE + 1,
            COL_N_S_E_O = COL_CROSS + 1,
            COL_CONST_MIN = COL_N_S_E_O + 1,
            COL_CONST_MAX = COL_CONST_MIN + 1, // Should be a multiple of 2 and a multiple of INC
            COL_CONST_INC = COL_CONST_MAX + 1,
            COL_CONST_NAME_MIN = COL_CONST_INC + 1,
            COL_CONST_NAME_MAX = COL_CONST_NAME_MIN + 1,
            COL_CONST_NAME_INC = COL_CONST_NAME_MAX + 1,
            COL_STAR_MAX = COL_CONST_NAME_INC + 1,
            COL_STAR_INC = COL_STAR_MAX + 1,
            COL_CURSOR = COL_STAR_INC + 1,
            COL_BOX_TEXT = COL_CURSOR + 1,
            COL_BOX = COL_BOX_TEXT + 1,
            COL_MENUBAR = COL_BOX + 1,
            COL_MENUBAR2 = COL_MENUBAR + 1,
            COL_MESSIER = COL_MENUBAR2 + 1,
            COL_HIGHLIGHT = COL_MESSIER + 1;
    public static final int colorDay[] = {
            /* BACKGROUND */0x00000000,
            /* HELP       */ 0x00ffffff,
            /* MOON       */ 0x00dcdcdc,
            /* SUN        */ 0x00ffff00,
            /* MERCURY    */ 0x00ff00ff,
            /* VENUS      */ 0x0000ff00,
            /* MARS       */ 0x00ff0000,
            /* JUPITER    */ 0x00ffff00,
            /* SATURN     */ 0x0000ffff,
            /* HISTORY    */ 0x00ffffff,
            /* INFO       */ 0x00ff0000,
            /* HORIZON    */ 0x00ff0000,
            /* ZENITH_BCK */ 0x00000023,
            /* ZENITH_EDGE*/ 0x00000005,
            /* CROSS      */ 0x003c3c3c,
            /* N_S_E_O    */ 0x007f00ff,
            /* CONST_MIN  */ 0x00000030,
            /* CONST_MAX  */ 0x008c8c5c,
            /* CONST_INC  */ 0x00040403,
            /* CONST_NAME_MIN */ 0x00000023,
            /* CONST_NAME_MAX */ 0x0046d223,
            /* CONST_NAME_INC */ 0x00020601,
            /* STAR_MAX   */ 0x00ffffff,
            /* STAR_INC   */ 0x00303030,
            /* CURSOR     */ 0x00ff0000,
            /* BOX_TEXT   */ 0x00ffffff,
            /* BOX        */ 0x000000c8,
            /* MENUBAR    */ 0x00FB16FF,
            /* MENUBAR2   */ 0x00803A7D,
            /* MESSIER    */ 0x00ffff00,
            /* HIGHLIGHT  */ 0x000080ff,
        };
    public static final int colorNight[] = {
            /* BACKGROUND */0x00000000,
            /* HELP       */ 0x00ff0000,
            /* MOON       */ 0x00ff0000,
            /* SUN        */ 0x00ff0000,
            /* MERCURY    */ 0x00ff0000,
            /* VENUS      */ 0x00ff0000,
            /* MARS       */ 0x00ff0000,
            /* JUPITER    */ 0x00ff0000,
            /* SATURN     */ 0x00ff0000,
            /* HISTORY    */ 0x00ff0000,
            /* INFO       */ 0x00ff0000,
            /* HORIZON    */ 0x00ff0000,
            /* ZENITH_BCK */ 0x00180000,
            /* ZENITH_EDGE*/ 0x00050000,
            /* CROSS      */ 0x00ff0000,
            /* N_S_E_O    */ 0x00ff0000,
            /* CONST_MIN  */ 0x00300000,
            /* CONST_MAX  */ 0x00a00000,
            /* CONST_INC  */ 0x00040000,
            /* CONST_NAME_MIN */ 0x00180000,
            /* CONST_NAME_MAX */ 0x00a00000,
            /* CONST_NAME_INC */ 0x00040000,
            /* STAR_MAX   */ 0x00ff0000,
            /* STAR_INC   */ 0x00300000,
            /* CURSOR     */ 0x00ff0000,
            /* BOX_TEXT   */ 0x00ff0000,
            /* BOX        */ 0x00ff0000,
            /* MENUBAR    */ 0x00B00000,
            /* MENUBAR2   */ 0x00800000,
            /* MESSIER    */ 0x00800000,
            /* HIGHLIGHT  */ 0x00800000,
        };
}
