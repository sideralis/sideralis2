/*
 * Test.java
 * JMUnit based test
 *
 * Created on 1 janv. 2010, 18:31:14
 */
package test;

import fr.dox.sideralis.location.Temps;
import fr.dox.sideralis.math.MathFunctions;
import java.util.Calendar;
import java.util.Date;
import jmunit.framework.cldc10.*;

/**
 * @author Bernard
 */
public class Test extends TestCase {

    public Test() {
        //The first parameter of inherited constructor is the number of test cases
        super(5, "Test");
    }

    public void test(int testNumber) throws Throwable {
        switch (testNumber) {
            case 0:
                test000();
                break;
            case 1:
                test001();
                break;
            case 2:
                test002();
                break;
            case 3:
                test003();
                break;
            case 4:
                test004();
                break;
        }
    }

    /**
     * Test the function convert2deg from MathFunction class
     * @throws Exception
     */
    private void test000() throws Exception {
        String resS;
        int cmp;

        resS = MathFunctions.convert2deg(10.06D, true);
        cmp = resS.compareTo("10\u00B03\'36\" (10.060\u00B0)");
        assertEquals(cmp, 0);
    }

    /**
     * Test the function arcsin from MathFunction class
     * @throws Exception
     */
    private void test001() throws Exception {
        double resD;
        boolean ok = true;
        double[] testValue = new double[] { 0.2D, 0.5D, 0.9D, -0.8D, -0.5D, -0.1D};
        double[] expValue = new double[] {  0.20135792079033079145512555221762D,
                                            0.52359877559829887307710723054658D,
                                            1.1197695149986341866866770558454D,
                                            -0.92729521800161223242851246292243D,
                                            -0.52359877559829887307710723054658D,
                                            -0.10016742116155979634552317945269D};
        double prec = 0.000001D;
        // -------------------
        // --- arcsin test ---
        for (int i=0; i<testValue.length;i++) {
            resD = MathFunctions.arcsin(testValue[i]);
            if (Math.abs(resD - expValue[i]) > prec) {
                ok = false;
            }
        }

        assertTrue(ok);
    }
    /**
     * Test the function arctan from MathFunction class
     * @throws Exception
     */
    private void test002() throws Exception {
        double resD;
        boolean ok = true;
        double[] testValue = new double[] { 0.1D, 0.5D, 0.93D, 0.95D, 0.97D, 0.99D,2D,4D};
        boolean[] testValueSign = new boolean[] {true,true,true,true,true,true,true,true};
        double[] expValue = new double[] {  0.099668652491162027378446119878021D,
                                            0.46364760900080611621425623146121D,
                                            0.74914462460601721032422782543075D,
                                            0.75976275487577082892296119539998D,
                                            0.77017091402033100725861937873632D,
                                            0.78037308006663589889787151727255D,
                                            1.1071487177940905030170654601785D,
                                            1.3258176636680324650592392104285D};
        double prec = 0.000001D;
        // -------------------
        // --- arctan test ---
        for (int i=0; i<testValue.length;i++) {
            resD = MathFunctions.arctan(testValue[i],testValueSign[i]);
            if (Math.abs(resD - expValue[i]) > prec) {
                System.out.println(""+i+": "+(Math.abs(resD - expValue[i])));
                ok = false;
            }
        }

        assertTrue(ok);
    }
    /**
     * Test the function log from MathFunction class
     * @throws Exception
     */
    private void test003() throws Exception {
        double resD;
        boolean ok = true;
        double[] testValue = new double[] { 1, 1.5D, 2, 2.5D, 3, 3.5D, 4};
        double[] expValue = new double[] {  0D,
                                            0.40546510810816438197801311546435D,
                                            0.69314718055994530941723212145818D,
                                            0.91629073187415506518352721176801D,
                                            1.0986122886681096913952452369225D,
                                            1.252762968495367995688120621985D,
                                            1.3862943611198906188344642429164D};
        double prec = 0.000001D;
        // -------------------
        // --- log test ---
        for (int i=0; i<testValue.length;i++) {
            resD = MathFunctions.log(testValue[i]);
            if (Math.abs(resD - expValue[i]) > prec) {
                System.out.println(""+i+": "+(Math.abs(resD - expValue[i])));
                ok = false;
            }
        }

        assertTrue(ok);
    }
    /**
     * Test the temps class
     * Test the calculation of Julian day
     * @throws Exception
     */
    private void test004() throws Exception {
        Temps t;
        Date d;
        double jj;
        boolean ok = true;

        t = new Temps();
        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.YEAR, 2010);
        cal.set(Calendar.MONTH,Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH,30);
        cal.set(Calendar.HOUR_OF_DAY,17);
        cal.set(Calendar.MINUTE, 40);
        cal.set(Calendar.SECOND,31);
        d = cal.getTime();

        t.calculateTimeOffset(d);
        t.adjustDate();

        t.calculateJourJulien();
        jj = t.getJJ();

        if (Math.abs(jj-2455227.1948032407D)>0.000000001D)
            ok = false;

        cal.set(Calendar.YEAR, 2020);
        cal.set(Calendar.MONTH,Calendar.NOVEMBER);
        cal.set(Calendar.DAY_OF_MONTH,30);
        cal.set(Calendar.HOUR_OF_DAY,03);
        cal.set(Calendar.MINUTE, 40);
        cal.set(Calendar.SECOND,31);
        d = cal.getTime();

        t.calculateTimeOffset(d);
        t.adjustDate();

        t.calculateJourJulien();
        jj = t.getJJ();

        if (Math.abs(jj-2459183.611469907D)>0.000000001D)
            ok = false;

        assertTrue(ok);
    }

}

