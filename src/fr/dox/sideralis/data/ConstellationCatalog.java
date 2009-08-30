package fr.dox.sideralis.data;

import fr.dox.sideralis.LocalizationSupport;
import fr.dox.sideralis.object.ConstellationObject;

/**
 * This class contains all data for constellations definitions
 * @author Bernard
 */
public class ConstellationCatalog {
    // The optimised constellations where the hr number of stars are replaced by an index
//    private ConstellationObject[] optConstellations;    // The various field in the table below
    public static short ABBR = 0;
    public static short LATIN = 1;
    public static short NAME = 2;
    public static short HIST = 3;                   
    
    // The definition of constellations
    static private ConstellationObject[] constellations = {
        new ConstellationObject(new short[]{348, 357, 357, 377, 377, 366, 366, 347, 347, 357}, 4, (short)366),
        new ConstellationObject(new short[]{0, 13, 13, 32}, 0, (short)13),
        new ConstellationObject(5, (short)304),
        new ConstellationObject(48, (short)242),
        new ConstellationObject(new short[]{38, 39, 39, 36, 36, 35, 35, 37, 37, 38, 36, 29, 29, 6, 6, 3, 3, 15, 15, 23, 23, 34, 34, 36, 3, 12, 12, 21, 21, 6}, 20, (short)23),
        new ConstellationObject(68, (short)155),
        new ConstellationObject(new short[]{225, 229, 229, 241, 241, 250, 250, 246, 246, 233, 233, 232, 232, 229}, 8, (short)246),
        new ConstellationObject(9, (short)69),
        new ConstellationObject(new short[]{33, 27, 27, 26}, 6, (short)27),
        new ConstellationObject(21, (short)152),
        new ConstellationObject(11, (short)154),
        new ConstellationObject(new short[]{378, 379, 379, 393, 393, 397, 397, 403, 403, 406, 406, 399, 399, 390, 390, 387, 387, 382, 382, 379}, 15, (short)392),
        new ConstellationObject(new short[]{115, 148, 148, 151, 151, 160, 160, 167, 167, 159, 159, 173}, 16, (short)160),
        new ConstellationObject(new short[]{1, 5, 5, 8, 8, 16, 16, 25}, 17, (short)8),
        new ConstellationObject(new short[]{236, 220, 220, 226, 220, 224, 224, 204, 204, 220, 204, 191, 191, 185, 191, 180, 224, 222, 222, 227, 227, 231, 231, 222, 222, 216, 222, 234, 234, 245}, 18, (short)222),
        new ConstellationObject(24, (short)215),
        new ConstellationObject(12, (short)211),
        new ConstellationObject(new short[]{82, 105, 105, 106, 106, 88, 88, 75, 75, 77, 77, 82}, 7, (short)85),
        new ConstellationObject(23, (short)0),
        new ConstellationObject(22, (short)237),
        new ConstellationObject(27, (short)195),
        new ConstellationObject(28, (short)183),
        new ConstellationObject(25, (short)349),
        new ConstellationObject(26, (short)259),
        new ConstellationObject(new short[]{198, 199, 192, 206}, 29, (short)206),
        new ConstellationObject(new short[]{394, 388, 388, 381, 381, 386, 381, 372, 372, 359, 381, 363, 363, 360, 360, 353}, 30, (short)372),
        new ConstellationObject(new short[]{396, 401, 401, 441, 441, 427, 427, 414, 396, 414}, 19, (short)427),
        new ConstellationObject(31, (short)384),
        new ConstellationObject(32, (short)66),
        new ConstellationObject(new short[]{319, 306, 306, 308, 308, 317, 317, 319, 317, 352, 352, 369, 369, 330, 330, 299, 299, 279, 279, 267, 267, 257, 257, 228, 228, 201, 201, 184}, 33, (short)369),
        new ConstellationObject(new short[]{361, 364, 364, 362, 364, 375}, 70, (short)364),
        new ConstellationObject(36, (short)42),
        new ConstellationObject(10, (short)76),
        new ConstellationObject(new short[]{122, 119, 119, 114, 119, 117, 119, 127, 127, 132, 132, 122, 127, 129, 129, 112, 129, 123, 129, 130, 130, 135, 135, 137, 137, 142, 135, 132, 122, 128, 128, 134, 134, 126, 126, 128}, 13, (short)132),
        new ConstellationObject(new short[]{221, 217, 217, 208, 208, 193, 193, 190, 190, 178, 178, 179, 179, 193}, 82, (short)193),
        new ConstellationObject(new short[]{426, 422, 422, 417, 417, 410, 417, 407}, 38, (short)422),
        new ConstellationObject(new short[]{121, 138, 138, 139, 139, 131, 131, 118, 139, 144, 144, 146, 144, 147, 144, 140, 140, 136, 136, 143, 136, 124, 136, 120, 120, 116, 120, 113, 113, 110}, 37, (short)140),
        new ConstellationObject(new short[]{315, 301, 301, 296, 296, 286, 286, 281, 281, 300, 281, 275, 286, 289, 289, 274, 289, 302, 302, 318, 318, 312, 302, 296}, 39, (short)286),
        new ConstellationObject(40, (short)57),
        new ConstellationObject(41, (short)164),
        new ConstellationObject(42, (short)30),
        new ConstellationObject(43, (short)383),
        new ConstellationObject(54, (short)111),
        new ConstellationObject(new short[]{171, 182, 182, 188, 188, 181, 181, 174, 174, 169, 169, 171, 169, 172, 172, 168, 168, 166}, 45, (short)174),
        new ConstellationObject(new short[]{80, 79, 79, 89, 89, 91, 91, 80, 91, 100, 100, 104, 104, 108, 108, 102, 102, 99, 99, 89, 80, 84, 80, 81}, 47, (short)91),
        new ConstellationObject(new short[]{252, 258, 258, 254, 254, 248, 248, 235, 235, 238, 238, 244, 244, 252, 252, 253, 258, 264, 264, 268, 254, 251, 251, 249, 238, 230}, 49, (short)254),
        new ConstellationObject(50, (short)162),
        new ConstellationObject(new short[]{333, 335, 335, 338, 338, 344, 344, 342, 342, 335}, 51, (short)335),
        new ConstellationObject(44, (short)420),
        new ConstellationObject(1, (short)176),
        new ConstellationObject(53, (short)391),
        new ConstellationObject(new short[]{187, 196, 196, 202, 202, 212, 202, 200}, 55, (short)202),
        new ConstellationObject(57, (short)402),
        new ConstellationObject(new short[]{239, 283, 283, 276, 276, 239}, 2, (short)239),
        new ConstellationObject(new short[]{316, 313, 313, 309, 309, 294, 294, 282, 282, 269, 269, 272, 272, 278, 278, 285, 285, 297, 297, 303}, 58, (short)269),
        new ConstellationObject(new short[]{101, 98, 98, 103, 103, 93, 93, 87, 87, 90, 90, 86, 86, 83, 98, 95, 95, 90, 103, 87, 103, 107, 107, 109, 87, 70, 74, 71, 71, 70, 70, 72, 72, 73, 98, 94, 94, 95}, 59, (short)97),
        new ConstellationObject(new short[]{380, 376, 376, 385, 385, 398, 376, 371, 371, 332, 332, 339, 339, 376, 339, 337, 337, 325, 325, 320, 320, 311}, 60, (short)339),
        new ConstellationObject(64, (short)125),
        new ConstellationObject(new short[]{40, 43, 43, 41, 43, 48, 48, 52, 52, 51}, 62, (short)48),
        new ConstellationObject(34, (short)395),
        new ConstellationObject(14, (short)145),
        new ConstellationObject(46, (short)177),
        new ConstellationObject(87, (short)358),
        new ConstellationObject(new short[]{17, 322, 322, 295, 295, 261, 261, 277, 277, 256, 256, 243, 243, 261}, 83, (short)261),
        new ConstellationObject(63, (short)4),
        new ConstellationObject(new short[]{433, 421, 421, 404, 404, 412, 412, 418, 418, 429, 432, 433}, 66, (short)418),
        new ConstellationObject(86, (short)153),
        new ConstellationObject(new short[]{18, 22, 22, 31, 31, 20, 20, 11, 11, 7, 7, 443, 443, 440, 440, 442, 442, 438, 438, 437, 437, 439, 439, 440}, 65, (short)443),
        new ConstellationObject(67, (short)149),
        new ConstellationObject(new short[]{405, 413, 413, 436, 436, 2, 2, 0, 0, 434, 434, 424, 434, 425, 425, 411, 434, 436}, 61, (short)434),
        new ConstellationObject(56, (short)271),
        new ConstellationObject(69, (short)58),
        new ConstellationObject(new short[]{323, 329, 329, 326, 326, 327, 327, 324, 324, 321, 321, 323, 321, 326, 326, 334, 334, 341, 341, 350, 341, 345, 345, 356, 356, 354, 354, 368, 368, 374, 374, 346, 346, 341, 341, 343, 343, 350, 350, 355, 350, 341}, 71, (short)350),
        new ConstellationObject(new short[]{263, 273, 273, 265, 273, 266, 273, 280, 280, 284, 284, 290, 290, 292, 292, 293, 293, 298, 298, 307, 307, 314, 314, 310, 310, 305}, 72, (short)290),
        new ConstellationObject(73, (short)10),
        new ConstellationObject(75, (short)260),
        new ConstellationObject(76, (short)170),
        new ConstellationObject(new short[]{44, 45, 45, 46, 45, 53, 53, 59, 59, 60, 60, 62, 62, 68, 68, 88, 68, 78, 78, 96, 96, 64, 64, 62, 64, 59, 64, 67, 67, 65, 65, 56, 56, 54, 54, 47, 47, 44, 68, 61, 61, 55, 55, 50, 55, 62}, 77, (short)62),
        new ConstellationObject(81, (short)415),
        new ConstellationObject(80, (short)288),
        new ConstellationObject(78, (short)328),
        new ConstellationObject(new short[]{408, 400, 400, 389, 408, 416, 416, 430, 430, 431}, 3, (short)408),
        new ConstellationObject(new short[]{218, 214, 214, 219, 214, 205, 205, 209, 209, 213, 205, 197, 197, 189, 189, 186}, 85, (short)209),
        new ConstellationObject(new short[]{157, 150, 150, 156, 156, 157, 157, 163, 163, 156, 165, 163}, 84, (short)157),
        new ConstellationObject(74, (short)331),
        new ConstellationObject(35, (short)19),
    };
    // The text associated with constellations
    public static final String[][] constNames = {
        {"And", "Andromeda", LocalizationSupport.getMessage("CONST_AND"), LocalizationSupport.getMessage("CONST_HIS_AND")},
        {"Ant", "Antlia", LocalizationSupport.getMessage("CONST_ANT"), LocalizationSupport.getMessage("CONST_HIS_ANT")},
        {"Aps", "Apus", LocalizationSupport.getMessage("CONST_APS"), LocalizationSupport.getMessage("CONST_HIS_APS")},
        {"Aqr", "Aquarius", LocalizationSupport.getMessage("CONST_AQR"), LocalizationSupport.getMessage("CONST_HIS_AQR")},
        {"Aql", "Aquila", LocalizationSupport.getMessage("CONST_AQL"), LocalizationSupport.getMessage("CONST_HIS_AQL")},
        {"Ara", "Ara", LocalizationSupport.getMessage("CONST_ARA"), LocalizationSupport.getMessage("CONST_HIS_ARA")},
        {"Ari", "Aries", LocalizationSupport.getMessage("CONST_ARI"), LocalizationSupport.getMessage("CONST_HIS_ARI")},
        {"Aur", "Auriga", LocalizationSupport.getMessage("CONST_AUR"), LocalizationSupport.getMessage("CONST_HIS_AUR")},
        {"Boo", "Bootes", LocalizationSupport.getMessage("CONST_BOO"), LocalizationSupport.getMessage("CONST_HIS_BOO")},
        {"Cae", "Caelum", LocalizationSupport.getMessage("CONST_CAE"), LocalizationSupport.getMessage("CONST_HIS_CAE")},
        {"Cam", "Camelopardalis", LocalizationSupport.getMessage("CONST_CAM"), LocalizationSupport.getMessage("CONST_HIS_CAM")},
        {"Cnc", "Cancer", LocalizationSupport.getMessage("CONST_CNC"), LocalizationSupport.getMessage("CONST_HIS_CNC")},
        {"CVn", "Canes Venatici", LocalizationSupport.getMessage("CONST_CVN"), LocalizationSupport.getMessage("CONST_HIS_CVN")},
        {"CMa", "Canis Major", LocalizationSupport.getMessage("CONST_CMA"), LocalizationSupport.getMessage("CONST_HIS_CMA")},
        {"CMi", "Canis Minor", LocalizationSupport.getMessage("CONST_CMI"), LocalizationSupport.getMessage("CONST_HIS_CMI")},
        {"Cap", "Capricornus", LocalizationSupport.getMessage("CONST_CAP"), LocalizationSupport.getMessage("CONST_HIS_CAP")},
        {"Car", "Carina", LocalizationSupport.getMessage("CONST_CAR"), LocalizationSupport.getMessage("CONST_HIS_CAR")},
        {"Cas", "Cassiopeia", LocalizationSupport.getMessage("CONST_CAS"), LocalizationSupport.getMessage("CONST_HIS_CAS")},
        {"Cen", "Centaurus", LocalizationSupport.getMessage("CONST_CEN"), LocalizationSupport.getMessage("CONST_HIS_CEN")},
        {"Cep", "Cepheus", LocalizationSupport.getMessage("CONST_CEP"), LocalizationSupport.getMessage("CONST_HIS_CEP")},
        {"Cet", "Cetus", LocalizationSupport.getMessage("CONST_CET"), LocalizationSupport.getMessage("CONST_HIS_CET")},
        {"Cha", "Chamaeleon", LocalizationSupport.getMessage("CONST_CHA"), LocalizationSupport.getMessage("CONST_HIS_CHA")},
        {"Cir", "Circinus", LocalizationSupport.getMessage("CONST_CIR"), LocalizationSupport.getMessage("CONST_HIS_CIR")},
        {"Col", "Columba", LocalizationSupport.getMessage("CONST_COL"), LocalizationSupport.getMessage("CONST_HIS_COL")},
        {"Com", "Coma Berenices", LocalizationSupport.getMessage("CONST_COM"), LocalizationSupport.getMessage("CONST_HIS_COM")},
        {"CrA", "Corona Austrina", LocalizationSupport.getMessage("CONST_CRA"), LocalizationSupport.getMessage("CONST_HIS_CRA")},
        {"CrB", "Corona Borealis", LocalizationSupport.getMessage("CONST_CRB"), LocalizationSupport.getMessage("CONST_HIS_CRB")},
        {"Crv", "Corvus", LocalizationSupport.getMessage("CONST_CRV"), LocalizationSupport.getMessage("CONST_HIS_CRV")},
        {"Crt", "Crater", LocalizationSupport.getMessage("CONST_CRT"), LocalizationSupport.getMessage("CONST_HIS_CRT")},
        {"Cru", "Crux", LocalizationSupport.getMessage("CONST_CRU"), LocalizationSupport.getMessage("CONST_HIS_CRU")},
        {"Cyg", "Cygnus", LocalizationSupport.getMessage("CONST_CYG"), LocalizationSupport.getMessage("CONST_HIS_CYG")},
        {"Del", "Delphinus", LocalizationSupport.getMessage("CONST_DEL"), LocalizationSupport.getMessage("CONST_HIS_DEL")},
        {"Dor", "Dorado", LocalizationSupport.getMessage("CONST_DOR"), LocalizationSupport.getMessage("CONST_HIS_DOR")},
        {"Dra", "Draco", LocalizationSupport.getMessage("CONST_DRA"), LocalizationSupport.getMessage("CONST_HIS_DRA")},
        {"Equ", "Equuleus", LocalizationSupport.getMessage("CONST_EQU"), LocalizationSupport.getMessage("CONST_HIS_EQU")},
        {"Eri", "Eridanus", LocalizationSupport.getMessage("CONST_ERI"), LocalizationSupport.getMessage("CONST_HIS_ERI")},
        {"For", "Fornax", LocalizationSupport.getMessage("CONST_FOR"), LocalizationSupport.getMessage("CONST_HIS_FOR")},
        {"Gem", "Gemini", LocalizationSupport.getMessage("CONST_GEM"), LocalizationSupport.getMessage("CONST_HIS_GEM")},
        {"Gru", "Grus", LocalizationSupport.getMessage("CONST_GRU"), LocalizationSupport.getMessage("CONST_HIS_GRU")},
        {"Her", "Hercules", LocalizationSupport.getMessage("CONST_HER"), LocalizationSupport.getMessage("CONST_HIS_HER")},
        {"Hor", "Horologium", LocalizationSupport.getMessage("CONST_HOR"), LocalizationSupport.getMessage("CONST_HIS_HOR")},
        {"Hya", "Hydra", LocalizationSupport.getMessage("CONST_HYA"), LocalizationSupport.getMessage("CONST_HIS_HYA")},
        {"Hyi", "Hydrus", LocalizationSupport.getMessage("CONST_HYI"), LocalizationSupport.getMessage("CONST_HIS_HYI")},
        {"Ind", "Indus", LocalizationSupport.getMessage("CONST_IND"), LocalizationSupport.getMessage("CONST_HIS_IND")},
        {"Lac", "Lacerta", LocalizationSupport.getMessage("CONST_LAC"), LocalizationSupport.getMessage("CONST_HIS_LAC")},
        {"Leo", "Leo", LocalizationSupport.getMessage("CONST_LEO"), LocalizationSupport.getMessage("CONST_HIS_LEO")},
        {"LMi", "Leo Minor", LocalizationSupport.getMessage("CONST_LMI"), LocalizationSupport.getMessage("CONST_HIS_LMI")},
        {"Lep", "Lepus", LocalizationSupport.getMessage("CONST_LEP"), LocalizationSupport.getMessage("CONST_HIS_LEP")},
        {"Lib", "Libra", LocalizationSupport.getMessage("CONST_LIB"), LocalizationSupport.getMessage("CONST_HIS_LIB")},
        {"Lup", "Lupus", LocalizationSupport.getMessage("CONST_LUP"), LocalizationSupport.getMessage("CONST_HIS_LUP")},
        {"Lyn", "Lynx", LocalizationSupport.getMessage("CONST_LYN"), LocalizationSupport.getMessage("CONST_HIS_LYN")},
        {"Lyr", "Lyra", LocalizationSupport.getMessage("CONST_LYR"), LocalizationSupport.getMessage("CONST_HIS_LYR")},
        {"Men", "Mensa", LocalizationSupport.getMessage("CONST_MEN"), LocalizationSupport.getMessage("CONST_HIS_MEN")},
        {"Mic", "Microscopium", LocalizationSupport.getMessage("CONST_MIC"), LocalizationSupport.getMessage("CONST_HIS_MIC")},
        {"Mon", "Monoceros", LocalizationSupport.getMessage("CONST_MON"), LocalizationSupport.getMessage("CONST_HIS_MON")},
        {"Mus", "Musca", LocalizationSupport.getMessage("CONST_MUS"), LocalizationSupport.getMessage("CONST_HIS_MUS")},
        {"Nor", "Norma", LocalizationSupport.getMessage("CONST_NOR"), LocalizationSupport.getMessage("CONST_HIS_NOR")},
        {"Oct", "Octans", LocalizationSupport.getMessage("CONST_OCT"), LocalizationSupport.getMessage("CONST_HIS_OCT")},
        {"Oph", "Ophiuchus", LocalizationSupport.getMessage("CONST_OPH"), LocalizationSupport.getMessage("CONST_HIS_OPH")},
        {"Ori", "Orion", LocalizationSupport.getMessage("CONST_ORI"), LocalizationSupport.getMessage("CONST_HIS_ORI")},
        {"Pav", "Pavo", LocalizationSupport.getMessage("CONST_PAV"), LocalizationSupport.getMessage("CONST_HIS_PAV")},
        {"Peg", "Pegasus", LocalizationSupport.getMessage("CONST_PEG"), LocalizationSupport.getMessage("CONST_HIS_PEG")},
        {"Per", "Perseus", LocalizationSupport.getMessage("CONST_PER"), LocalizationSupport.getMessage("CONST_HIS_PER")},
        {"Phe", "Phoenix", LocalizationSupport.getMessage("CONST_PHE"), LocalizationSupport.getMessage("CONST_HIS_PHE")},
        {"Pic", "Pictor", LocalizationSupport.getMessage("CONST_PIC"), LocalizationSupport.getMessage("CONST_HIS_PIC")},
        {"Psc", "Pisces", LocalizationSupport.getMessage("CONST_PSC"), LocalizationSupport.getMessage("CONST_HIS_PSC")},
        {"PsA", "Piscis Austrinus", LocalizationSupport.getMessage("CONST_PSA"), LocalizationSupport.getMessage("CONST_HIS_PSA")},
        {"Pup", "Puppis", LocalizationSupport.getMessage("CONST_PUP"), LocalizationSupport.getMessage("CONST_HIS_PUP")},
        {"Pyx", "Pyxis", LocalizationSupport.getMessage("CONST_PYX"), LocalizationSupport.getMessage("CONST_HIS_PYX")},
        {"Ret", "Reticulum", LocalizationSupport.getMessage("CONST_RET"), LocalizationSupport.getMessage("CONST_HIS_RET")},
        {"Sge", "Sagitta", LocalizationSupport.getMessage("CONST_SGE"), LocalizationSupport.getMessage("CONST_HIS_SGE")},
        {"Sgr", "Sagittarius", LocalizationSupport.getMessage("CONST_SGR"), LocalizationSupport.getMessage("CONST_HIS_SGR")},
        {"Sco", "Scorpius", LocalizationSupport.getMessage("CONST_SCO"), LocalizationSupport.getMessage("CONST_HIS_SCO")},
        {"Scl", "Sculptor", LocalizationSupport.getMessage("CONST_SCL"), LocalizationSupport.getMessage("CONST_HIS_SCL")},
        {"Sct", "Scutum", LocalizationSupport.getMessage("CONST_SCT"), LocalizationSupport.getMessage("CONST_HIS_SCT")},
        {"Ser", "Serpens", LocalizationSupport.getMessage("CONST_SER"), LocalizationSupport.getMessage("CONST_HIS_SER")},
        {"Sex", "Sextans", LocalizationSupport.getMessage("CONST_SEX"), LocalizationSupport.getMessage("CONST_HIS_SEX")},
        {"Tau", "Taurus", LocalizationSupport.getMessage("CONST_TAU"), LocalizationSupport.getMessage("CONST_HIS_TAU")},
        {"Tel", "Telescopium", LocalizationSupport.getMessage("CONST_TEL"), LocalizationSupport.getMessage("CONST_HIS_TEL")},
        {"Tri", "Triangulum", LocalizationSupport.getMessage("CONST_TRI"), LocalizationSupport.getMessage("CONST_HIS_TRI")},
        {"TrA", "Triangulum Australe", LocalizationSupport.getMessage("CONST_TRA"), LocalizationSupport.getMessage("CONST_HIS_TRA")},
        {"Tuc", "Tucana", LocalizationSupport.getMessage("CONST_TUC"), LocalizationSupport.getMessage("CONST_HIS_TUC")},
        {"UMa", "Ursa Major", LocalizationSupport.getMessage("CONST_UMA"), LocalizationSupport.getMessage("CONST_HIS_UMA")},
        {"UMi", "Ursa Minor", LocalizationSupport.getMessage("CONST_UMI"), LocalizationSupport.getMessage("CONST_HIS_UMI")},
        {"Vel", "Vela", LocalizationSupport.getMessage("CONST_VEL"), LocalizationSupport.getMessage("CONST_HIS_VEL")},
        {"Vir", "Virgo", LocalizationSupport.getMessage("CONST_VIR"), LocalizationSupport.getMessage("CONST_HIS_VIR")},
        {"Vol", "Volans", LocalizationSupport.getMessage("CONST_VOL"), LocalizationSupport.getMessage("CONST_HIS_VOL")},
        {"Vul", "Vulpecula", LocalizationSupport.getMessage("CONST_VUL"), LocalizationSupport.getMessage("CONST_HIS_VUL")},
        {"No name", "No name", LocalizationSupport.getMessage("CONST_NON"), ""},
    };

    /**
     * Return the number of constellation in my catalog
     * @return number of constellations
     */
    public static int getNumberOfConstellations() {
        return constellations.length;
    }

    /**
     * Return a reference to a constellation from the index in the constellation table
     * @param i index in the table
     * @return a reference to a constellation
     */
    public static ConstellationObject getConstellation(int i) {
        return constellations[i];
    }

    /**
     * Return the abbrevation name of the constellation
     * @param i the index number of the constellation
     * @return the abbrevation name
     */
    public static String getAbbrName(int i) {
        return constNames[i][0];
    }

    /**
     * Return the name of the constellation
     * @return the name of the constellation
     */
    public static String getLatinName(int i) {
        return constNames[i][LATIN];
    }

    /**
     * Return the history text of the constellation
     * @param i the index number of the constellation
     * @return the history of the constellation
     */
    public static String getHistory(int i) {
        return constNames[i][HIST];
    }

    /**
     * Return the number of constellations names
     * @return Number of constellations
     */
    public static int size() {
        return constNames.length;
    }
}
