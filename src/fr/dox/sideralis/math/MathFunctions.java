package fr.dox.sideralis.math;

/**
 * A class which contains only static functions used for calculation of arcsin and arctan.
 * @author Bernard
 */
public class MathFunctions {    
    /**
     * Function arcsin: return the inverse sinus in radian
     * You can also use it to calculate the arccos as arccos(z) = PI/2 - arcsin(z)
     * @param z the input param
     * @return the radian angle corresponding to the inverse sin of z.
     */
    public static double arcsin(double z) {
        int k,i;
        /**
         * This number determines the precision, higher it is, higher the precision is.
         * Be careful, if you change this number you should also extend the constFact table
         */
        int N = 17;

        double res,tmp1,tmp2,tmp3;

        res = 0;
        if (Math.abs(z)<=0.5) {
            // k= 0
            tmp1 = tmp3 = 1;
            tmp2 = z;
            tmp1 *= tmp2;
            res += tmp1;
            // = sum(k=0 a n) de (produit de j=0 a k-1 de (0.5+j))*z exp 2k+1) div (k! * 2k+1)
            for (k=1;k<N;k++) {
                tmp3 *= (0.5+k-1);
                tmp1 = tmp3;
                tmp2 = 1;
                for (i=0;i<2*k+1;i++)
                    tmp2 *= z;
                tmp1 *= tmp2;
                tmp1 /= (2*k+1);
                tmp1 /= constFact[k];
                
                res += tmp1;
            }
        } else if (z>0.5) {
            // k = 0
            tmp3 = 1;
            tmp1 = tmp3;
            tmp2 = 1;
            res += tmp1;
            // = (Pi/2-Racine de 2*racine de 1-z)*Sum(k=0 a N) de (produit de j=0 a k-1 de (0.5+j))*(1-z) exp k) div (2exp k * k! * 2k+1)
            for (k=1;k<N;k++) {
                tmp3 *= (0.5 + k-1);
                tmp1 = tmp3;
                tmp2 = 1;
                for (i=0;i<k;i++)
                    tmp2 *= ((1-z)/2);
                tmp1 *= tmp2;
                tmp1 /= (2*k+1);
                tmp1 /= constFact[k];
                
                res += tmp1;
            }
            res *= Math.sqrt(2)*Math.sqrt(1-z);
            res = Math.PI/2 - res;
        } else {
            // = (-Pi/2 + Racine de 2*racine de z+1)*Sum(k=0 a N) de (produit de j=0 a k-1 de (0.5+j))*(z+1) exp k) div (2exp k * k! * 2k+1)
            for (k=0;k<N;k++) {
                tmp1 = PochHammer(0.5,k);
                tmp2 = 1;
                for (i=0;i<k;i++)
                    tmp2 *= ((z+1)/2);
                tmp1 *= tmp2;
                tmp1 /= (2*k+1);
                tmp1 /= constFact[k];
                
                res += tmp1;
            }
            res *= Math.sqrt(2)*Math.sqrt(z+1);
            res = res - Math.PI/2;
        }
        
        return res;
    }
    /** 
     * Function inverse tangent
     * @param z the value we want to know the arctan
     * @param signN the sign of the numerator, this information is needed to determine the correct quadran.
     * @return the arctan value of z in radian
     */
    public static double arctan(double z,boolean signN) {
        int k;
        int N = 40;                                 // This number determines the precision, higher it is, higher the precision is.
        double res;
        double tmp;
        
        if (Math.abs(z)>0.92)
            N *= 2;
        if (Math.abs(z)>0.94)
            N += N/2;
        if (Math.abs(z)>0.96)
            N += N/2;
        if (Math.abs(z)>0.98)
            N *= 2;
        
        if (Math.abs(z) < 1) {
            // Sum de k=0 a N de [(-1) exp k * z exp (2k+1) / (2k+1)]
            res = tmp = z;
            for (k=1;k<N;k++) {
                tmp *= z*z;
                if ((k%2) == 0)
                    res += tmp/(2*k+1);
                else
                    res -= tmp/(2*k+1);
            }
        } else {
            res = Math.PI * z / 2 / Math.abs(z) - 1/z;
            tmp = 1/z;
            for (k=1;k<N;k++) {
                tmp /= (z*z);
                if ((k%2) == 0)
                    res -= tmp/(2*k+1);
                else
                    res += tmp/(2*k+1);
            }
        }
        
        if (signN == true && z>=0)
            res += 0;
        if (signN == false && z>=0)
            res += Math.PI;
        if (signN == true && z<0)
            res += Math.PI;
        if (signN == false && z<0)
            res += 0;
        return res;
    }
    /**
     * Convert a double value representing a degree in a string
     * @param val in degre
     * @param flag to display the number also as a real number
     * @return a string representing a degree value
     */
    public static String convert2deg(double val, boolean flag) {
        String res;
        double tmp;
        int deg,m,s;
        int val2;
        
        //val = Math.toDegrees(val);
        res = "";
        val = (val+360)%360;
        deg = (int)(val);        
        tmp = (val-deg)*60;
        m = (int)tmp;
        tmp = (tmp - m)*60;
        s = (int)tmp;
        val2 = (int)((val - ((int)(val*1000))/1000)*1000);
        res = res + deg + "�"+m+"'"+s+"\"";
        if (flag)
            res += " (" + (int)val+"."+val2+"�)";

        return res;        
    }
    /**
     * Convert a double value representing an hour in a string
     * @param val the hour
     * @param flag to display the number also as a real number
     * @return a string like 11h23m56s
     */
    public static String convert2hms(double val, boolean flag) {
        String res;
        double tmp;
        int h,m,s;
        int val2;
        
        res = "";
        val = (val+24) %24;
        h = (int)val;
        tmp = (val-h)*60;
        m = (int)tmp;
        tmp = tmp-(int)tmp;
        tmp *= 60;
        s = (int)tmp;
        val2 = (int)((val - ((int)(val*1000))/1000)*1000);
        res = res + h+"h"+m+"m"+s+"s";
        if (flag)
            res += " ("+(int)val+"."+val2+"h)";
        return res;
    }
    
    /** 
     * Conversion from ua to billion of km
     */
    public static double toMKm(double l) {
        double res = (int)(l*149597870.691/1000000);
        return res;
    }
    /** This function calculates the pochhammer
     * For n=0 return 1. For n=1 return a. For a=0 return 0. For a=1 return n!
     * @param a
     * @param n
     * @return n = product (from k=0 to n-1) of (a+k) [= (a)*(a+1)*...*(a+n-1)]
     */
    private static double PochHammer(double a,int n) {
        int k;
        double res=1;
        
        for (k=0;k<n;k++)
            res *= (a+k);
        return res;
        
    }
    /** This function calcultes the factorial
     * For n=0 return 1
     * @param n the input parameter for the factorial
     * @return n*(n-1)*...*2
     * @deprecated
     */
    private static double fact(int n) {
        double res=1;
        for(int i=2;i<=n;i++)
            res *= i;
        return res;
    }

    /** A table containing the factoriel value of number between 0 and 16 */
    private static final double constFact[] = {1,1,2,6,24,120,720,5040,40320,362880,3628800,39916800,479001600,6227020800D,87178291200D,1307674368000D,20922789888000D};
}
