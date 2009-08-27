package fr.dox.sideralis.data;

import fr.dox.sideralis.LocalizationSupport;
import fr.dox.sideralis.object.ConstellationObject;

/**
 * This class contains all data for constellations definitions
 * @author Bernard
 */
public class ConstellationCatalog {
    // The optimised constellations where the hr number of stars are replaced by an index
    private ConstellationObject[] optConstellations;    // The various field in the table below
    public static short ABBR = 0;
    public static short LATIN = 1;
    public static short NAME = 2;
    public static short HIST = 3;                   
    
    // The definition of constellations
    static private ConstellationObject[] constellations = {
        // Aigle / Aquila
        new ConstellationObject(new short[]{7236, 7377, 7377, 7710, 7710, 7557, 7557, 7235, 7235, 7377}, 4, (short) 7557),
        // Andromede / Andromeda
        new ConstellationObject(new short[]{15, 337, 337, 603}, 0, (short) 337),
        // Autel / Ara
        new ConstellationObject(5, (short) 6461),
        // Balance / Libra
        new ConstellationObject(48, (short) 5531),
        // Baleine / Cetus
        new ConstellationObject(new short[]{896, 911, 911, 804, 804, 718, 718, 813, 813, 896, 804, 585, 585, 188, 188, 74, 74, 402, 402, 539, 539, 681, 681, 804, 74, 334, 334, 509, 509, 188}, 20, (short) 539),
        // Boussole /Pyxis
        new ConstellationObject(68, (short) 3468),
        // Bouvier
        new ConstellationObject(new short[]{5235, 5340, 5340, 5506, 5506, 5681, 5681, 5602, 5602, 5435, 5435, 5429, 5429, 5340}, 8, (short) 5602),
        // Burin
        new ConstellationObject(9, (short) 1502),
        // Belier
        new ConstellationObject(new short[]{617, 553, 553, 546}, 6, (short) 553),
        // Cameleon
        new ConstellationObject(21, (short) 3318),
        // Cancer
        new ConstellationObject(11, (short) 3461),
        // Capricorne
        new ConstellationObject(new short[]{7754, 7776, 7776, 8075, 8075, 8167, 8167, 8278, 8278, 8322, 8322, 8204, 8204, 7980, 7980, 7936, 7936, 7822, 7822, 7776}, 15, (short) 8060),
        // Carene
        new ConstellationObject(new short[]{2326, 3117, 3117, 3307, 3307, 3699, 3699, 3890, 3890, 3685, 3685, 4037}, 16, (short) 3699),
        // Cassiopee
        new ConstellationObject(new short[]{21, 168, 168, 264, 264, 403, 403, 542}, 17, (short) 264),
        // Centaure
        new ConstellationObject(new short[]{5460, 5132, 5132, 5267, 5132, 5231, 5231, 4819, 4819, 5132, 4819, 4621, 4621, 4467, 4621, 4337, 5231, 5193, 5193, 5288, 5288, 5367, 5367, 5193, 5193, 5028, 5193, 5440, 5440, 5576}, 18, (short) 5193),
        // Chevelure de Berenice
        new ConstellationObject(24, (short) 4983),
        // Chiens de chasse
        new ConstellationObject(12, (short) 4915),
        // Cocher
        new ConstellationObject(new short[]{1708, 2088, 2088, 2095, 2095, 1791, 1791, 1577, 1577, 1605, 1605, 1708}, 7, (short) 1773),
        // Colombe
        new ConstellationObject(23, (short) 1965),
        // Compas
        new ConstellationObject(22, (short) 5463),
        // Corbeau
        new ConstellationObject(27, (short) 4662),
        // Coupe
        new ConstellationObject(28, (short) 4382),
        // Couronne Australe
        new ConstellationObject(25, (short) 7254),
        // Couronne Boreale
        new ConstellationObject(26, (short) 5793),
        // La croix
        new ConstellationObject(new short[]{4730, 4763, 4656, 4853}, 29, (short) 4853),
        // Le cygne
        new ConstellationObject(new short[]{8115, 7949, 7949, 7796, 7796, 7924, 7796, 7615, 7615, 7417, 7796, 7528, 7528, 7420, 7420, 7328}, 30, (short) 7615),
        // Cephee
        new ConstellationObject(new short[]{8162, 8238, 8238, 8974, 8974, 8694, 8694, 8465, 8162, 8465}, 19, (short) 8694),
        // Dauphin
        new ConstellationObject(31, (short) 7882),
        // Dorade
        new ConstellationObject(32, (short) 1465),
        // Dragon
        new ConstellationObject(new short[]{6705, 6536, 6536, 6554, 6554, 6688, 6688, 6705, 6688, 7310, 7310, 7582, 7582, 6927, 6927, 6396, 6396, 6132, 6132, 5986, 5986, 5744, 5744, 5291, 5291, 4787, 4787, 4434}, 33, (short) 7582),
        // Fleche
        new ConstellationObject(new short[]{7479, 7536, 7536, 7488, 7536, 7635}, 70, (short) 7536),
        // Fourneau
        new ConstellationObject(36, (short) 963),
        // Girafe
        new ConstellationObject(10, (short) 1603),
        // Grand Chien 
        new ConstellationObject(new short[]{2491, 2429, 2429, 2294, 2429, 2387, 2429, 2580, 2580, 2653, 2653, 2491, 2580, 2618, 2618, 2282, 2618, 2538, 2618, 2646, 2646, 2693, 2693, 2749, 2749, 2827, 2693, 2653, 2491, 2596, 2596, 2657, 2657, 2574, 2574, 2596}, 13, (short) 2653),
        // Grande ourse
        new ConstellationObject(new short[]{5191, 5054, 5054, 4905, 4905, 4660, 4660, 4554, 4554, 4295, 4295, 4301, 4301, 4660}, 82, (short) 4660),
        // Grue
        new ConstellationObject(new short[]{8675, 8636, 8636, 8556, 8556, 8425, 8556, 8353}, 38, (short) 8636),
        // Gemeaux
        new ConstellationObject(new short[]{2484, 2763, 2763, 2777, 2777, 2650, 2650, 2421, 2777, 2905, 2905, 2985, 2905, 2990, 2905, 2821, 2821, 2697, 2697, 2890, 2697, 2540, 2697, 2473, 2473, 2343, 2473, 2286, 2286, 2216}, 37, (short) 2821),
        // Hercules
        new ConstellationObject(new short[]{6623, 6410, 6410, 6324, 6324, 6212, 6212, 6148, 6148, 6406, 6148, 6095, 6212, 6220, 6220, 6092, 6220, 6418, 6418, 6695, 6695, 6588, 6418, 6324}, 39, (short) 6212),
        // Horloge
        new ConstellationObject(40, (short) 1326),
        // Hydre
        new ConstellationObject(41, (short) 3748),
        // Hydre Male
        new ConstellationObject(42, (short) 591),
        // Indien
        new ConstellationObject(43, (short) 7869),
        // Licorne
        new ConstellationObject(54, (short) 2227),
        // Lion
        new ConstellationObject(new short[]{3982, 4359, 4359, 4534, 4534, 4357, 4357, 4057, 4057, 3975, 3975, 3982, 3975, 4031, 4031, 3905, 3905, 3873}, 45, (short) 4057),
        // Lievre        
        new ConstellationObject(new short[]{1702, 1654, 1654, 1829, 1829, 1865, 1865, 1702, 1865, 1998, 1998, 2085, 2085, 2155, 2155, 2035, 2035, 1983, 1983, 1829, 1702, 1756, 1702, 1705}, 47, (short) 1865),
        // Le loup
        new ConstellationObject(new short[]{5695, 5776, 5776, 5708, 5708, 5605, 5605, 5453, 5453, 5469, 5469, 5571, 5571, 5695, 5695, 5705, 5776, 5948, 5948, 5987, 5708, 5683, 5683, 5649, 5469, 5354}, 49, (short) 5708),
        // Lynx
        new ConstellationObject(50, (short) 3705),
        // Lyre
        new ConstellationObject(new short[]{7001, 7056, 7056, 7106, 7106, 7178, 7178, 7139, 7139, 7056}, 51, (short) 7056),
        // Lezard
        new ConstellationObject(44, (short) 8585),
        // Machine Pneumatique
        new ConstellationObject(1, (short) 4104),
        // Microscope
        new ConstellationObject(53, (short) 8039),
        // Mouche
        new ConstellationObject(new short[]{4520, 4671, 4671, 4798, 4798, 4923, 4798, 4773}, 55, (short) 4798),
        // Octant
        new ConstellationObject(57, (short) 8254),
        // Oiseau de paradis
        new ConstellationObject(new short[]{5470, 6163, 6163, 6102, 6102, 5470}, 2, (short) 5470),
        // Orphiucius
        new ConstellationObject(new short[]{6629, 6603, 6603, 6556, 6556, 6299, 6299, 6149, 6149, 6056, 6056, 6075, 6075, 6129, 6129, 6175, 6175, 6378, 6378, 6453}, 58, (short) 6056),
        // Orion
        new ConstellationObject(new short[]{2004, 1948, 1948, 2061, 2061, 1876, 1876, 1790, 1790, 1852, 1852, 1788, 1788, 1713, 1948, 1903, 1903, 1852, 2061, 1790, 2061, 2124, 2124, 2199, 1790, 1543, 1570, 1544, 1544, 1543, 1543, 1552, 1552, 1567, 1948, 1899, 1899, 1903}, 59, (short) 1934),
        // Paon
        new ConstellationObject(new short[]{7790, 7665, 7665, 7913, 7913, 8181, 7665, 7590, 7590, 6982, 6982, 7107, 7107, 7665, 7107, 7074, 7074, 6855, 6855, 6745, 6745, 6582}, 60, (short) 7107),
        // Peintre
        new ConstellationObject(64, (short) 2550),
        // Persee
        new ConstellationObject(new short[]{915, 1017, 1017, 936, 1017, 1122, 1122, 1220, 1220, 1203}, 62, (short) 1122),
        // Petit cheval
        new ConstellationObject(34, (short) 8131),
        // Petit chien
        new ConstellationObject(14, (short) 2943),
        // Petit lion
        new ConstellationObject(46, (short) 4247),
        // Petit Renard
        new ConstellationObject(87, (short) 7405),
        // Petite ourse
        new ConstellationObject(new short[]{424, 6789, 6789, 6322, 6322, 5903, 5903, 6116, 6116, 5735, 5735, 5563, 5563, 5903}, 83, (short) 5903),
        // Phenix
        new ConstellationObject(63, (short) 99),
        // Poisson austral
        new ConstellationObject(new short[]{8728, 8628, 8628, 8305, 8305, 8431, 8431, 8576, 8576, 8695, 8720, 8728}, 66, (short) 8576),
        // Poisson volant
        new ConstellationObject(86, (short) 3347),
        // Poissons
        new ConstellationObject(new short[]{437, 510, 510, 596, 596, 489, 489, 294, 294, 224, 224, 9072, 9072, 8969, 8969, 8984, 8984, 8911, 8911, 8852, 8852, 8916, 8916, 8969}, 65, (short) 9072),
        // Poupe
        new ConstellationObject(67, (short) 3165),
        // Pegase
        new ConstellationObject(new short[]{8308, 8450, 8450, 8781, 8781, 39, 39, 15, 15, 8775, 8775, 8650, 8775, 8667, 8667, 8430, 8775, 8781}, 61, (short) 8775),
        // Regle
        new ConstellationObject(56, (short) 6072),
        // Reticule
        new ConstellationObject(69, (short) 1336),
        // Sagittaire
        new ConstellationObject(new short[]{6812, 6913, 6913, 6859, 6859, 6879, 6879, 6832, 6832, 6746, 6746, 6812, 6746, 6859, 6859, 7039, 7039, 7121, 7121, 7264, 7121, 7194, 7194, 7348, 7348, 7337, 7337, 7581, 7581, 7623, 7623, 7234, 7234, 7121, 7121, 7150, 7150, 7264, 7264, 7340, 7264, 7121}, 71, (short) 7264),
        // Scorpion
        new ConstellationObject(new short[]{5944, 6084, 6084, 5953, 6084, 5984, 6084, 6134, 6134, 6165, 6165, 6241, 6241, 6247, 6247, 6262, 6262, 6380, 6380, 6553, 6553, 6615, 6615, 6580, 6580, 6527}, 72, (short) 6241),
        // Sculpteur
        new ConstellationObject(73, (short) 280),
        // Serpent
        new ConstellationObject(75, (short) 5854),
        // Sextant
        new ConstellationObject(76, (short) 3981),
        // Table
        // Taureau
        new ConstellationObject(new short[]{1030, 1038, 1038, 1066, 1038, 1239, 1239, 1346, 1346, 1389, 1389, 1409, 1409, 1497, 1497, 1791, 1497, 1620, 1620, 1910, 1910, 1457, 1457, 1409, 1457, 1346, 1457, 1473, 1473, 1458, 1458, 1320, 1320, 1251, 1251, 1101, 1101, 1030, 1497, 1392, 1392, 1256, 1256, 1142, 1256, 1409}, 77, (short) 1409),
        // Toucan
        new ConstellationObject(81, (short) 8502),
        // Triangle Austral
        new ConstellationObject(80, (short) 6217),
        // Telescope
        new ConstellationObject(78, (short) 6897),
        // Verseau TO BE COMPLETED
        new ConstellationObject(new short[]{8414, 8232, 8232, 7950, 8414, 8518, 8518, 8698, 8698, 8709}, 3, (short) 8414),
        // Vierge
        new ConstellationObject(new short[]{5056, 4963, 4963, 5107, 4963, 4826, 4826, 4910, 4910, 4932, 4826, 4689, 4689, 4540, 4540, 4517}, 85, (short) 4910),
        // Voiles
        new ConstellationObject(new short[]{3634, 3206, 3206, 3485, 3485, 3634, 3634, 3734, 3734, 3485, 3803, 3734}, 84, (short) 3634),
        // Ecu de Sobiesky
        new ConstellationObject(74, (short) 6973),
        // Eridan
        new ConstellationObject(35, (short) 472),
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
     * Constructor
     */
    public ConstellationCatalog() {
        int hr;

        // Store Constellation in an optimized way (replace hr id by index of star in stars variable)
        optConstellations = new ConstellationObject[constellations.length];
        for (int i = 0; i < constellations.length; i++) {
            // For all constellations
            short c = 0;
            short[] v = new short[constellations[i].getSizeOfConstellation()];
            for (int j = 0; j < constellations[i].getSizeOfConstellation(); j++) {
                // For all stars in constellation
                hr = constellations[i].getHR(j);
                for (short k = 0; k < StarCatalog.getNumberOfStars(); k++) {
                    if (StarCatalog.getStar(k).getHR() == hr) {
                        v[j] = k;
                        break;
                    }
                }
            }
            // Find the id of 
            hr = constellations[i].getRefStar4ConstellationName();
            for (short k = 0; k < StarCatalog.getNumberOfStars(); k++) {
                if (StarCatalog.getStar(k).getHR() == hr) {
                    c = k;
                    break;
                }
            }

            optConstellations[i] = new ConstellationObject(v, constellations[i].getIdxName(), c);
        }
    }

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
    public ConstellationObject getConstellation(int i) {
        return constellations[i];
    }

    /**
     * Return a reference to an optimised constellation from the index in the constellation table
     * @param i index in the table
     * @return a reference to a constellation
     */
    public ConstellationObject getOptConstellation(int i) {
        return optConstellations[i];
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
    public String getLatinName(int i) {
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
