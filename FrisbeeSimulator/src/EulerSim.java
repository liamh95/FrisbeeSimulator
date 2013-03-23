
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 *
 * @author Liam
 */
public class EulerSim extends Simulation {
    private static double STEP_SIZE;
    
    public EulerSim(double height, double v, double ang, double maxX, double[] targetY, double step){
        super(height, v, ang, maxX, targetY);
        STEP_SIZE=step;
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
        while(y>0 && x<targetX){//while it hasn't hit the ground and hasn't reached the target X distance
            double dVY = ( RHO * Math.pow(vx,2) * AREA * CL / 2 / MASS - GRAV ) * STEP_SIZE;//change in Y velocity.
            double dVX = ( RHO * Math.pow(vx, 2) * AREA * CD / 2 / MASS ) * -STEP_SIZE;//change in X velocity.
            vx += dVX;
            vy += dVY;
            x += vx * STEP_SIZE;
            y += vy * STEP_SIZE;
            if(y>maxHeight){//checks to see if it peaks here
                maxHeight=y;//if so, this is the peak
                at=x;//peak occurred at this x value
            }
            t += STEP_SIZE;//keeps track of flight time  
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
        reset();
        try{
            int j=0;
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("Euler.csv")));
            while(y>0){
                double dVY = (RHO * Math.pow(vx,2) * AREA * CL / 2 / MASS - GRAV) * STEP_SIZE;
                double dVX = ( RHO * Math.pow(vx, 2) * AREA * CD / 2 / MASS ) * -STEP_SIZE;
                vx += dVX;
                vy += dVY;
                x += vx * STEP_SIZE;
                y += vy * STEP_SIZE;
                if(y>maxHeight){//checks to see if it peaks here
                    maxHeight=y;//if so, this is the peak
                    at=x;//peak occurred at this x value
                }
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
    
    @Override
    public void print(){
        System.out.println("Euler Simulation");
        super.print();
    }
    
    @Override
    public void testAngle(double height, double v, double targX, double[] yBox){
        System.out.println("Angle Solver: Euler");
        super.testAngle(height, v, targX, yBox);
    }
    
    @Override
    public void testVelocity(double height, double ang, double targX, double[] yBox){
        System.out.println("Velocity Solver: Euler");
        super.testVelocity(height, ang, targX, yBox);
    }
    
    @Override
    public void testDistance(double height, double v, double ang, double[] yBox){
        System.out.println("Distance Solver: Euler");
        super.testDistance(height, v, ang, yBox);
    }
    
}
