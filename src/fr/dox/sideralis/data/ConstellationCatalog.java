package fr.dox.sideralis.data;

import fr.dox.sideralis.LocalizationSupport;
import fr.dox.sideralis.object.ConstellationObject;

/**
 * This class contains all data for constellations definitions
 * @author Bernard
 */
public class ConstellationCatalog {
    public static final short ABBR = 0;
    public static final short LATIN = 1;
    public static final short NAME = 2;
    public static final short HIST = 3;
    
    // The definition of constellations
    private static final ConstellationObject[] constellations = {
        new ConstellationObject(new short[]{533, 546, 546, 571, 571, 557, 557, 532, 532, 546}, 4, (short)557),
        new ConstellationObject(new short[]{0, 23, 23, 48}, 0, (short)23),
        new ConstellationObject(new short[]{461, 462, 461, 456, 461, 473, 475, 476, 473, 477}, 5, (short)473),
        new ConstellationObject(new short[]{407, 395, 395, 381, 381, 388}, 48, (short)381),
        new ConstellationObject(new short[]{61, 63, 63, 55, 55, 52, 52, 56, 56, 61, 55, 44, 44, 12, 12, 4, 4, 26, 26, 36, 36, 51, 51, 55, 4, 22, 22, 34, 34, 12}, 20, (short)36),
        new ConstellationObject(new short[]{49, 40, 40, 39}, 6, (short)40),
        new ConstellationObject(new short[]{243, 249, 249, 254}, 68, (short)249),
        new ConstellationObject(new short[]{362, 367, 367, 380, 380, 393, 393, 386, 386, 371, 371, 370, 370, 367}, 8, (short)386),
        new ConstellationObject(9, (short)116),
        new ConstellationObject(new short[]{237, 292, 292, 296}, 21, (short)237),
        new ConstellationObject(new short[]{251, 244, 244, 247, 247, 234, 247, 255}, 11, (short)247),
        new ConstellationObject(new short[]{573, 574, 574, 596, 596, 604, 604, 610, 610, 613, 613, 606, 606, 592, 592, 588, 588, 577, 577, 574}, 15, (short)595),
        new ConstellationObject(new short[]{176, 259, 259, 273, 273, 293, 293, 284, 284, 259, 273, 261, 261, 236, 236, 227}, 16, (short)261),
        new ConstellationObject(new short[]{1, 10, 10, 15, 15, 27, 27, 38}, 17, (short)15),
        new ConstellationObject(new short[]{374, 357, 357, 363, 357, 361, 361, 338, 338, 357, 338, 319, 319, 312, 319, 303, 361, 359, 359, 365, 365, 369, 369, 359, 359, 353, 359, 372, 372, 385}, 18, (short)359),
        new ConstellationObject(new short[]{603, 608, 608, 663, 663, 644, 644, 622, 603, 622}, 19, (short)644),
        new ConstellationObject(24, (short)351),
        new ConstellationObject(new short[]{345, 333}, 12, (short)345),
        new ConstellationObject(new short[]{133, 163, 163, 164, 164, 139, 139, 124, 124, 127, 127, 133}, 7, (short)136),
        new ConstellationObject(new short[]{174, 165, 165, 159, 159, 152, 152, 142}, 23, (short)152),
        new ConstellationObject(new short[]{397, 375, 375, 391}, 22, (short)375),
        new ConstellationObject(new short[]{330, 325, 325, 321, 321, 320, 321, 334, 334, 330}, 27, (short)325),
        new ConstellationObject(new short[]{307, 299, 299, 304, 304, 309, 309, 307}, 28, (short)307),
        new ConstellationObject(new short[]{527, 535, 535, 537, 537, 534, 534, 529}, 25, (short)535),
        new ConstellationObject(new short[]{406, 403, 403, 409, 409, 411, 411, 417, 417, 424}, 26, (short)409),
        new ConstellationObject(new short[]{329, 331, 322, 340}, 29, (short)340),
        new ConstellationObject(new short[]{598, 590, 590, 576, 576, 586, 576, 564, 564, 549, 576, 554, 554, 550, 550, 542}, 30, (short)564),
        new ConstellationObject(new short[]{589, 584, 584, 581, 589, 587, 587, 582, 582, 578}, 31, (short)582),
        new ConstellationObject(new short[]{99, 112, 112, 149}, 32, (short)112),
        new ConstellationObject(new short[]{494, 479, 479, 481, 481, 491, 491, 494, 491, 541, 541, 560, 560, 508, 508, 468, 468, 444, 444, 430, 430, 402, 402, 366, 366, 335, 335, 310}, 33, (short)560),
        new ConstellationObject(new short[]{517, 510, 510, 509}, 74, (short)510),
        new ConstellationObject(new short[]{32, 43, 43, 50, 50, 53, 53, 54, 54, 62, 62, 101, 101, 111, 111, 107, 107, 87, 87, 83, 83, 76, 76, 70, 70, 65, 65, 59, 59, 57, 57, 60, 60, 69, 69, 75, 75, 80, 80, 82, 82, 89, 89, 94, 94, 110, 110, 117, 117, 130}, 35, (short)0),
        new ConstellationObject(new short[]{552, 555, 555, 553, 555, 567}, 70, (short)555),
        new ConstellationObject(new short[]{67, 58}, 36, (short)67),
        new ConstellationObject(new short[]{184, 208, 208, 210, 210, 198, 198, 180, 210, 218, 218, 222, 218, 223, 218, 212, 212, 203, 203, 217, 203, 189, 203, 183, 183, 177, 183, 172, 172, 169}, 37, (short)212),
        new ConstellationObject(new short[]{118, 125}, 10, (short)125),
        new ConstellationObject(new short[]{185, 181, 181, 173, 181, 179, 181, 194, 194, 199, 199, 185, 194, 196, 196, 171, 196, 188, 196, 197, 197, 202, 202, 207, 207, 214, 202, 199, 185, 195, 195, 201, 201, 193, 193, 195}, 13, (short)199),
        new ConstellationObject(new short[]{358, 354, 354, 342, 342, 323, 323, 318, 318, 300, 300, 301, 301, 323}, 82, (short)323),
        new ConstellationObject(new short[]{643, 639, 639, 629, 629, 618, 629, 614}, 38, (short)639),
        new ConstellationObject(new short[]{489, 470, 470, 465, 465, 451, 451, 446, 446, 469, 446, 439, 451, 455, 455, 438, 455, 471, 471, 492, 492, 486, 471, 465}, 39, (short)451),
        new ConstellationObject(40, (short)96),
        new ConstellationObject(new short[]{364, 352, 352, 349, 349, 317, 317, 311, 311, 302, 302, 295, 295, 287, 287, 282, 282, 277, 277, 274, 274, 266, 266, 271, 271, 246, 246, 252, 252, 241, 241, 245, 245, 242, 242, 252}, 41, (short)266),
        new ConstellationObject(new short[]{45, 6, 6, 86, 86, 45}, 42, (short)45),
        new ConstellationObject(new short[]{579, 602, 602, 593, 602, 615}, 43, (short)579),
        new ConstellationObject(new short[]{627, 635, 635, 628, 628, 631, 631, 626, 626, 634, 634, 623}, 44, (short)635),
        new ConstellationObject(new short[]{231, 221, 221, 204, 204, 187, 187, 186, 187, 175, 204, 178, 178, 170}, 54, (short)204),
        new ConstellationObject(new short[]{131, 129, 129, 140, 140, 143, 143, 131, 143, 155, 155, 162, 162, 167, 167, 158, 158, 154, 154, 140, 131, 135, 131, 132}, 47, (short)143),
        new ConstellationObject(new short[]{281, 306, 306, 315, 315, 305, 305, 285, 285, 279, 279, 281, 279, 283, 283, 275, 275, 272}, 45, (short)285),
        new ConstellationObject(new short[]{396, 405, 405, 399, 399, 389, 389, 373, 373, 377, 377, 384, 384, 396, 396, 398, 405, 425, 425, 431, 399, 394, 394, 390, 377, 368}, 49, (short)399),
        new ConstellationObject(new short[]{263, 260, 260, 235}, 50, (short)263),
        new ConstellationObject(new short[]{513, 515, 515, 519, 519, 528, 528, 524, 524, 515}, 51, (short)515),
        new ConstellationObject(new short[]{290, 268, 290, 298}, 1, (short)290),
        new ConstellationObject(53, (short)594),
        new ConstellationObject(new short[]{314, 327, 327, 336, 336, 347, 336, 332}, 55, (short)336),
        new ConstellationObject(57, (short)609),
        new ConstellationObject(new short[]{378, 448, 448, 440, 440, 378}, 2, (short)378),
        new ConstellationObject(new short[]{493, 490, 490, 487, 487, 482, 482, 463, 463, 447, 447, 432, 432, 436, 436, 443, 443, 450, 450, 466, 466, 472}, 58, (short)432),
        new ConstellationObject(new short[]{156, 151, 151, 161, 161, 145, 145, 138, 138, 141, 141, 137, 137, 134, 151, 147, 147, 141, 161, 138, 161, 166, 166, 168, 138, 119, 123, 120, 120, 119, 119, 121, 121, 122, 151, 146, 146, 147}, 59, (short)150),
        new ConstellationObject(new short[]{575, 569, 569, 585, 585, 605, 569, 562, 562, 512, 512, 520, 520, 569, 520, 518, 518, 500, 500, 495, 495, 485}, 60, (short)520),
        new ConstellationObject(new short[]{612, 621, 621, 653, 653, 3, 3, 0, 0, 651, 651, 641, 651, 642, 642, 619, 651, 653}, 61, (short)651),
        new ConstellationObject(new short[]{190, 160, 160, 157}, 64, (short)190),
        new ConstellationObject(new short[]{64, 71, 71, 66, 71, 78, 78, 88, 88, 85}, 62, (short)78),
        new ConstellationObject(new short[]{600, 599, 599, 597}, 34, (short)600),
        new ConstellationObject(new short[]{219, 215}, 14, (short)219),
        new ConstellationObject(new short[]{297, 288, 288, 278, 278, 270}, 46, (short)288),
        new ConstellationObject(new short[]{540, 547, 547, 563, 563, 568, 568, 572}, 87, (short)547),
        new ConstellationObject(new short[]{28, 497, 497, 464, 464, 420, 420, 442, 442, 401, 401, 383, 383, 420}, 83, (short)420),
        new ConstellationObject(new short[]{7, 8, 8, 2, 8, 11, 11, 20, 20, 25, 25, 13, 25, 31, 31, 42, 31, 29, 29, 20}, 63, (short)20),
        new ConstellationObject(new short[]{650, 638, 638, 611, 611, 620, 620, 632, 632, 646, 649, 650}, 66, (short)632),
        new ConstellationObject(new short[]{256, 239, 239, 211, 211, 206, 206, 225, 225, 233, 233, 239}, 86, (short)239),
        new ConstellationObject(new short[]{30, 35, 35, 47, 47, 33, 33, 19, 19, 14, 14, 666, 666, 662, 662, 664, 664, 659, 659, 657, 657, 660, 660, 662}, 65, (short)666),
        new ConstellationObject(new short[]{228, 216, 216, 192, 192, 176, 176, 182, 182, 209, 209, 228, 209, 224, 224, 226, 226, 230, 230, 228}, 67, (short)228),
        new ConstellationObject(new short[]{441, 434, 434, 427, 427, 428, 428, 441}, 56, (short)434),
        new ConstellationObject(new short[]{97, 102, 102, 91, 91, 84, 84, 97}, 69, (short)97),
        new ConstellationObject(new short[]{498, 507, 507, 501, 501, 503, 503, 499, 499, 496, 496, 498, 496, 501, 501, 514, 514, 522, 522, 538, 522, 530, 530, 545, 545, 543, 543, 559, 559, 566, 566, 531, 531, 522, 522, 526, 526, 538, 538, 544, 538, 522}, 71, (short)538),
        new ConstellationObject(new short[]{423, 437, 437, 426, 437, 429, 437, 445, 445, 449, 449, 457, 457, 459, 459, 460, 460, 467, 467, 480, 480, 488, 488, 484, 484, 478}, 72, (short)457),
        new ConstellationObject(new short[]{17, 665, 665, 658, 658, 661}, 73, (short)17),
        new ConstellationObject(new short[]{415, 414, 414, 422, 414, 408, 408, 412, 412, 418, 418, 416, 416, 432, 525, 502, 502, 493, 493, 483, 483, 466}, 75, (short)412),
        new ConstellationObject(76, (short)280),
        new ConstellationObject(new short[]{72, 73, 73, 74, 73, 90, 90, 100, 100, 103, 103, 105, 105, 115, 115, 139, 115, 128, 128, 148, 148, 108, 108, 105, 108, 100, 108, 114, 114, 109, 109, 95, 95, 92, 92, 77, 77, 72, 115, 104, 104, 93, 93, 81, 93, 105}, 77, (short)105),
        new ConstellationObject(new short[]{504, 506, 506, 523, 523, 570, 570, 551, 551, 504}, 78, (short)504),
        new ConstellationObject(new short[]{624, 655, 655, 9, 655, 5}, 81, (short)655),
        new ConstellationObject(new short[]{453, 419, 419, 404, 404, 392, 392, 453}, 80, (short)453),
        new ConstellationObject(new short[]{616, 607, 607, 591, 616, 625, 625, 630, 637, 647, 647, 648, 648, 654}, 3, (short)616),
        new ConstellationObject(new short[]{355, 350, 350, 356, 350, 339, 339, 343, 343, 348, 339, 328, 328, 316, 316, 313}, 85, (short)343),
        new ConstellationObject(new short[]{257, 232, 232, 228, 232, 227, 227, 253, 253, 265, 265, 276, 276, 294, 294, 269, 269, 257}, 84, (short)257),
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
