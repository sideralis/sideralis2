/*
 * Test.java
 * JMUnit based test
 *
 * Created on 1 janv. 2010, 18:31:14
 */

package test;


import fr.dox.sideralis.math.MathFunctions;
import jmunit.framework.cldc10.*;

/**
 * @author Bernard
 */
public class Test extends TestCase {
    
    public Test() {
        //The first parameter of inherited constructor is the number of test cases
        super(1,"Test");
    }            

    public void test(int testNumber) throws Throwable {
        switch(testNumber) {
            case 0:
                testOne();
                break;

        }
    }
    /**
     * Test the function MathFunction convert2deg
     * @throws Exception
     */
    private void testOne() throws Exception {
        String res;

        res = MathFunctions.convert2deg(10.06D, true);
        assertEquals(res, "10\u00B03\'36\" (10.060\u00B0)" );
    }
}
