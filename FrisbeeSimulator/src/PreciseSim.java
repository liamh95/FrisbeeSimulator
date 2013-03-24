
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 *
 * @author Liam
 */
public class PreciseSim extends Simulation {
    private double PRECISION;
    
    /**
     * Creates a simulator that simulates a frisbee's path by using positions
     * and velocities as functions of time. Because I don't know how to invert
     * these functions, it estimates for a given time by going along the path in
     * increments and choosing the closest one.
     * @param height height of launch
     * @param v initial velocity
     * @param angle angle of attack
     * @param maxX distance to wall
     * @param targetY goal range
     * @param prec size of time increment 
     */
    public PreciseSim(double height, double v, double ang, double maxX, double[] targetY, double prec){
        super(height, v, ang, maxX, targetY);
        PRECISION=prec;
    }
    
    @Override
    public boolean simulate(){
        if(parameterCheck(y0, v0, angle, targetX, hitbox)==false){//is the input bad?
            return false;//stop
        }
        double CD = CD0 + CDA*Math.pow((angle-A0)*Math.PI/180, 2);//Figures Drag Coefficient
        double CL = CL0 + CLA*angle*Math.PI/180;//Figures Lift Coefficient
        //Initial
        reset();
        CX = ( RHO * AREA * CD / 2 / MASS );//drag coefficient combined with other constants
        CY = ( RHO * AREA * CL / 2 / MASS );//lift coefficient combined with other constants
        while(y>0 && x<targetX){
            x=xPosition(t);
            y=yPosition(t);
            vx=xVelocity(t);
            vy=yVelocity(t);
            if(y>maxHeight){
                maxHeight=y;
                at=x;
            }
            t+=PRECISION;
        }
        calcError();
        return true;
    }
    
    @Override
    public boolean graph(){
        if(parameterCheck(y0, v0, angle, targetX, hitbox)==false){//is the input bad?
            return false;//stop
        }
        double CD = CD0 + CDA*Math.pow((angle-A0)*Math.PI/180, 2);//Figures Drag Coefficient
        double CL = CL0 + CLA*angle*Math.PI/180;//Figures Lift Coefficient
        //Initial
        reset();
        CX = ( RHO * AREA * CD / 2 / MASS );//drag coefficient combined with other constants
        CY = ( RHO * AREA * CL / 2 / MASS );//lift coefficient combined with other constants
        try{
            int j=0;
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("Precise.csv")));
            while(y>0){
                x=xPosition(t);
                y=yPosition(t);
                vx=xVelocity(t);
                vy=yVelocity(t);
                if(y>maxHeight){
                    maxHeight=y;
                    at=x;
                }
                t+=PRECISION;
                if(j%10==0){
                    out.print(x + "," + y + "," + targetX + "," + y);
                    out.println();
                    out.flush();
                }
                j++;
            }
            out.close();
        }
        catch(Exception e){
            System.out.println("Error, file in use.");
        }
        return true;
    }
    
    /**
     * x velocity as a function of time
     * @param t: at what time
     * @param init: initial x velocity
     * @return: x velocity at that particular time 
     */
    public double xVelocity(double t){
        if(t<0 || CX<0 || vx0<0) {
            return -1;
        }
        return vx0 / (CX*vx0*t + 1);
    }
    
    /**
     * y velocity as a function of time (depends on x velocity)
     * @param t: at what time
     * @return: y velocity at that particular time
     */
    public double yVelocity(double t){
        return (GRAV/CX/vx0) + (CY*vx0/CX) + vy0 - ( GRAV * ( CX*vx0*t + 1 ) /CX/vx0 ) - ( CY*vx0 / CX / ( CX*vx0*t + 1 ) );
    }
    
    /**
     * x position as a function of time
     * @param t: at what time
     * @return: x position at that time 
     */
    public double xPosition(double t){
        return (1/CX) * Math.log(CX * vx0 * t + 1);
    }
    
    /**
     * y position as a function of time. RIP hand and brain
     * @param t: at what time
     * @return: y position at that time 
     */
    public double yPosition(double t){
        double q = 1/CX/vx0;
        double b = (GRAV/CX/vx0) + (CY*vx0/CX) + vy0;
        double c = CX*vx0*t + 1;
        double d = GRAV*Math.pow(c, 2)/2/CX/vx0;
        double e = CY*vx0*Math.log(c)/CX;
        double j = (CX*vx0*y0) + (GRAV/2/CX/vx0) - (GRAV/CX/vx0) - (CY*vx0/CX) - vy0;
        return q * (b*c - d - e + j);
    }
    
    @Override
    public void print(){
        System.out.println("Precision Simulation");
        super.print();
    }
    
    @Override
    public void testAngle(double height, double v, double targX, double[] yBox){
    	System.out.println("Angle Solver: Precise");
    	super.testAngle(height, v, targX, yBox);
    }
    
    @Override
    public void testVelocity(double height, double ang, double targX, double[] yBox){
    	System.out.println("Velocity Solver: Precise");
    	super.testVelocity(height, ang, targX, yBox);
    }
    
    @Override
    public void testDistance(double height, double v, double ang, double[] yBox){
    	System.out.println("Distance Solver: Precise");
    	super.testDistance(height, v, ang, yBox);
    }
    
}
