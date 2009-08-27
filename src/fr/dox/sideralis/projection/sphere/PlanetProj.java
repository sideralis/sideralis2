package fr.dox.sideralis.projection.sphere;

import fr.dox.sideralis.location.Position;
import fr.dox.sideralis.math.MathFunctions;
import fr.dox.sideralis.object.PlanetObject;

/**
 *
 * @author Bernard
 */
public class PlanetProj extends Projection {
    
    /** Creates a new instance of Planet */
    public PlanetProj(PlanetObject planet, Position pos) {
        super(planet,pos);
    }
    
    public void calculate() {
        int k;
        double L,e,i,omega2,M1;
        double T;
        double alpha,delta;
        PlanetObject planet = (PlanetObject)getObject();


        // Calcul de T
        T = myPosition.getTemps().getT();
        // Calcul de ...
        L = (planet.La0 + planet.La1*T + planet.La2*T*T)%360;
        e = (planet.ea0 + planet.ea1*T + planet.ea2*T*T)%360;
        i = (planet.ia0 + planet.ia1*T + planet.ia2*T*T)%360;
        //omega = (planet.oa0 + planet.oa1*T + planet.oa2*T*T)%360;
        omega2 = (planet.wa0 + planet.wa1*T + planet.wa2*T*T)%360;
        M1 = (planet.Ma0 + planet.Ma1*T + planet.Ma2*T*T)%360;
        
        // Calcul de E
        double E1,E0,e0;
        E0 = M1;
        e0 = Math.toDegrees(e);
        for (k=0;k<5;k++) {
            E0 = E0+(M1+e0*Math.sin(Math.toRadians(E0))-E0)/(1-e*Math.cos(Math.toRadians(E0)));
        }
        E1 = E0;
        // Calcul de v
        double v;
        v = (1+e)/(1-e);
        v = Math.sqrt(v)*Math.tan(Math.toRadians(E1/2));
        v = 2*MathFunctions.arctan(v, true);
        // Calcul de r
        double r;
        r = planet.a*(1-e*e)/(1+e*Math.cos(v));
        r = planet.a*(1-e*Math.cos(Math.toRadians(E1)));
        // Calcul de u (radian)
        double u;
        u = Math.toRadians(L) + v - Math.toRadians(M1) - Math.toRadians(omega2);
        // Calcul de l
        double l;
        l = Math.cos(Math.toRadians(i))*Math.sin(u);
        l = MathFunctions.arctan(l/Math.cos(u), l>=0?true:false);
        l = Math.toRadians(omega2) + l;
        // Calcul de b;
        double b;
        b = Math.sin(u)*Math.sin(Math.toRadians(i));
        b = MathFunctions.arcsin(b);
        
        // Calcul de R et theta du soleil        
        RSoleil = Projection.getRSun();
        //theta = Projection.getThetaSun();
        
        // Calcul de alpha
        double lambda;
        double N,D;
        N = r * Math.cos(b)*Math.sin(l-theta);
        D = r * Math.cos(b)*Math.cos(l-theta)+RSoleil;
        lambda = MathFunctions.arctan(N/D, N>=0?true:false);
        lambda += theta;
        // Calcul de delta;
        dist = N*N + D*D + r*Math.sin(b)*r*Math.sin(b);
        dist = Math.sqrt(dist);
        // Calcul de beta
        double beta;
        beta = MathFunctions.arcsin(r/dist*Math.sin(b));
        // Calcul de alpha et delta
        double sinD, tanA,tanAp;
        double eps;
        eps = 23.452994 - 0.0130125*T - 0.00000164*T*T + 0.000000503*T*T*T;
        sinD = Math.sin(beta)*Math.cos(Math.toRadians(eps)) + Math.cos(beta)*Math.sin(Math.toRadians(eps))*Math.sin(lambda);
        tanAp = (Math.sin(lambda)*Math.cos(Math.toRadians(eps))-Math.tan(beta)*Math.sin(Math.toRadians(eps)));
        tanA = tanAp/Math.cos(lambda);
        alpha = MathFunctions.arctan(tanA, tanAp>=0?true:false);    
        delta = MathFunctions.arcsin(sinD);

        planet.setAscendance((float)Math.toDegrees(alpha)/15);
        planet.setDeclinaison((float)Math.toDegrees(delta));
        calculateHorizontalCoordinate(planet.getAscendance(),planet.getDeclinaison(),false);
    }
}
