/**
 * source of inspiration web.mit.edu/womens-ult/www/smite/frisbee_physics.pdf
 * @author Liam Hardiman: liam.hardiman@gmail.com
 * @author Caleb Nelson: calebnelson@gmail.com
 * @author Art Kalb: art.kalb14@bcp.org
 * LAST EDITED 2/15 6:00 PM
 */


/**
 * One thing this simulator assumes is that the frisbee is given enough spin to maintain stable flight.
 * Does not account for the frisbee curving to the left or right (when viewed from behind).
 */

import java.lang.Math;
import java.util.*;
import java.io.*;
import java.awt.*;
import javax.swing.*;

class FrisbeeSim{
    //EVERYTHING IS IN SI UNITS EXCEPT FOR ANGLES. THOSE ARE IN DEGREES
    private static double y0;//initial height
    private static double v0;//magnitude of initial velocity vector
    private static double vx0;//initial x velocity
    private static double vy0;//initial y velocity
    private static double a;//angle of attack
    private static double targetX;//how far the target is in the x direction
    private static double[] hitbox;//range of the target in the y direction
    private static double xErr;//how off the frisbee lands in the x direction
    private static double yErr;//how off the frisbee lands in the y direction
    //position n velocity
    private static double x;//current x location
    private static double y;//current y location
    private static double vx;//current x velocity
    private static double vy;//current y velocity
    private static double t;//time
    //Properties of Frisbee and environment
    private static final double MASS = .180;//Frisbee mass according to handbook
    private static final double CL0 = .1;//coefficient used for determining Coefficient of Lift
    private static final double CD0 = .08;//coefficient used for determining Coefficient of Drag
    private static final double CDA = 2.72;//other coefficient used for determining Coefficient of Drag
    private static final double CLA = 1.4;//other coefficient used for determining Coefficient of Lift
    private static final double GRAV = 9.80;//gravitational acceleration
    private static final double AREA = 0.06131160497;//area of Frisbee top
    private static final double RHO = 1.2041;//air density
    private static final double A0 = -4;//angle at which drag is minimized
    private static double CX;//x coefficients bundled into one
    private static double CY;//y coefficients bundled into one
    //Desirables
    private static double maxHeight=0;//Maximum height achieved
    private static double at=0;//distance at which that height is achieved
    //Testers: increase INC or EULER_STEP values for less accuracy but faster solution
    private static final double MAX_DIST = 16.4592;//maximum length of the field.
    private static final double MIN_SPEED = .5;//minimum velocity tested when solving for velocity
    private static final double MAX_SPEED = 25;//maximum velocity tested when solving for velocity
    private static final double VEL_INC = .1;//amount by which velocity gets incremented when solving for velocity
    private static final double ANGLE_INC = .15;//amount by which angle of attack gets increased when solving for angle
    private static final double X_INC = .1;//amount by which distance gets increased when solving for distance
    private static final double EULER_STEP = .001;//time EULER_STEP in Euler's method
	
    /**
     * Simulates flight path of a frisbee using Euler's method to solve the differential equations
     * MIT code makes an error in figuring change in X velocity.
     * @param height: height from which the frisbee is launched.
     * @param v: magnitude of initial velocity vector.
     * @param angle: angle at which the frisbee is launched
     * @param maxX: maximum X distance the frisbee is allowed to go
     * @param EULER_STEP: time EULER_STEP for use in Euler's method (smaller = more accurate)
     * @param targetY: bounds on target height
     */
    public static boolean simulate(double height, double v, double angle, double maxX, double[] targetY){
        if(parameterCheck(height, v, angle, maxX, targetY)==false){//is the input bad?
            return false;//stop
        }
        y0 = height;
        v0 = v;
        a = angle;
        targetX = maxX;
        hitbox = targetY;
        double CD = CD0 + CDA*Math.pow((angle-A0)*Math.PI/180, 2);//Figures Drag Coefficient
        double CL = CL0 + CLA*angle*Math.PI/180;//Figures Lift Coefficient
        //Initial
        reset();
        y=height;//frisbee leaves from height
        vx=v*Math.cos(angle*Math.PI/180);//Initial X velocity
        vy=v*Math.sin(angle*Math.PI/180);//Initial Y velocity
        
        while(y>0 && x<maxX){//while it hasn't hit the ground and hasn't reached the target X distance
            double dVY = ( RHO * Math.pow(vx,2) * AREA * CL / 2 / MASS - GRAV ) * EULER_STEP;//change in Y velocity.
            double dVX = ( RHO * Math.pow(vx, 2) * AREA * CD / 2 / MASS ) * -EULER_STEP;//change in X velocity.
            vx += dVX;
            vy += dVY;
            x += vx * EULER_STEP;
            y += vy * EULER_STEP;
            if(y>maxHeight){//checks to see if it peaks here
                maxHeight=y;//if so, this is the peak
                at=x;//peak occurred at this x value
            }
            t += EULER_STEP;//keeps track of flight time  
        }
        if(y<0){//if it goes "underground" because of an extra iteration, say it hit the ground
            y=0;
        }
        xErr = x-maxX;
        if(xErr<.1 && xErr>0){//If it goes past the x bound because of an extra iteration, say it hit the wall
            xErr=0;
            x=maxX;
        }
        if(y>targetY[1]){//positive error if it lands above the upper boundary of the hitbox
            yErr=y-targetY[1];
        }
        else if(y<targetY[0]){//negative error if it lands below the lower boundary of the hitbox
            yErr=y-targetY[0];
        }
        else{yErr=0;}//in the hitbox.
        return true; 
    }
    
    /**
     * Simulate the flight exactly using math!
     * @param height: height from which the Frisbee is launched
     * @param v: initial velocity
     * @param angle: angle at which the Frisbee is launched
     * @param maxX: wall's x position
     * @param targetY: goal range
     * @return: true if the simulation is possible, false otherwise
     */
    /*public static boolean exactSimulate(double height, double v, double angle, double maxX, double[] targetY){
        if(parameterCheck(height, v, angle, maxX, targetY)==false){//is the input bad?
            return false;//stop
        }
        y0 = height;
        v0 = v;
        a = angle;
        targetX = maxX;
        hitbox = targetY;
        double CD = CD0 + CDA*Math.pow((angle-A0)*Math.PI/180, 2);//Figures Drag Coefficient
        double CL = CL0 + CLA*angle*Math.PI/180;//Figures Lift Coefficient
        //Initial
        reset();
        y=height;//frisbee leaves from height
        vx0=v*Math.cos(angle*Math.PI/180);//Initial X velocity
        vy0=v*Math.sin(angle*Math.PI/180);//Initial Y velocity
        
        CX = ( RHO * AREA * CD / 2 / MASS );//drag coefficient combined with other constants
        CY = ( RHO * AREA * CL / 2 / MASS );//lift coefficient combined with other constants
    }*/
    
    /**
     * x velocity as a function of time
     * @param t: at what time
     * @param init: initial x velocity
     * @return: x velocity at that particular time 
     */
    public static double xVelocity(double t){
        if(t<0 || CX<0 || vx0<0)//bad input
            return -1;
        return vx0 / (CX*vx0*t + 1);
    }
    
    /**
     * y velocity as a function of time (depends on x velocity)
     * @param t: at what time
     * @return: y velocity at that particular time
     */
    public static double yVelocity(double t){
        return (GRAV/CX/vx0) + (CY*vx0/CX) + vy0 - ( GRAV * ( CX*vx0*t + 1 ) /CX/vx0 ) - ( CY*vx0 / CX / ( CX*vx0*t + 1 ) );
    }
    
    /**
     * x position as a function of time
     * @param t: at what time
     * @return: x position at that time 
     */
    public static double xPosition(double t){
        return (1/CX) * Math.log(CX * vx0 * t + 1);
    }
    
    /**
     * y position as a function of time. RIP hand and brain
     * @param t: at what time
     * @return: y position at that time 
     */
    public static double yPosition(double t){
        double q = 1/CX/vx0;
        double b = (GRAV/CX/vx0) + (CY*vx0/CX) + vy0;
        double c = CX*vx0*t + 1;
        double d = GRAV*Math.pow(c, 2)/2/CX/vx0;
        double e = CY*vx0*Math.log(c)/CX;
        double j = (CX*vx0*y0) + (GRAV/2/CX/vx0) - (GRAV/CX/vx0) - (CY*vx0/CX) - vy0;
        return q * (b*c - d - e + j);
    }
    
    /**
     * Writes to a .csv file. (x,y,wallX,y) First two form the coordinates of the frisbee, second two form the coordinates of the wall
     * @param height: height from which the frisbee is tossed.
     * @param v: magnitude of initial velocity vector.
     * @param angle: angle at which the frisbee is tossed
     * @param maxX: maximum X distance the frisbee is allowed to go
     * @param targetY: bounds on target height
     */
    public static boolean graph(double height, double v, double angle, double maxX, double[] targetY){
        t=0;//set to 0 here so that it resets with each simulation
        if(parameterCheck(height, v, angle, maxX, targetY) == false){//if input is bad
            System.out.println("Input was bad!");
            return false;//stop
        }
        y0 = height;
        v0 = v;
        a = angle;
        targetX = maxX;
        double CD = CD0 + CDA*Math.pow((angle-A0)*Math.PI/180, 2);//Figures Drag Coefficient
        double CL = CL0 + CLA*angle*Math.PI/180;//Figures Lift Coefficient
        //Initial
        x=0;//always start from x=0
        y=height;//frisbee leaves from height
        vx=v*Math.cos(angle*Math.PI/180);//Initial X velocity
        vy=v*Math.sin(angle*Math.PI/180);//Initial Y velocity


        try{
            int j=0;
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("Frisbee.csv")));
            while(y>0){
                double dVY = (RHO * Math.pow(vx,2) * AREA * CL / 2 / MASS - GRAV) * EULER_STEP;
                double dVX = ( RHO * Math.pow(vx, 2) * AREA * CD / 2 / MASS ) * -EULER_STEP;
                vx += dVX;
                vy += dVY;
                x += vx * EULER_STEP;
                y += vy * EULER_STEP;
                if(y>maxHeight){//checks to see if it peaks here
                    maxHeight=y;//if so, this is the peak
                    at=x;//peak occurred at this x value
                }
                if(j%10==0){
                    out.print(x + "," + y + "," + maxX + "," + y);
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
     * Simulates throwing a frisbee with hopes of hitting a certain target.
     * @param height: height from which frisbee is launched.
     * @param v: magnitude of initial velocity vector.
     * @param angle: angle at which the frisbee is tossed
     * @param maxX: maximum X distance the frisbee is allowed to go
     * @param targetY: bounds on target height
     */
    public static boolean targetSimulate(double height, double v, double angle, double maxX, double[] targetY){
        return simulate(height, v, angle, maxX, targetY);//return false if it tried to simulate something silly
    }
    
    /**
     * Simulates throwing a frisbee and seeing how far away it lands
     * @param height: height from which frisbee is launched.
     * @param v: magnitude of initial velocity vector.
     * @param angle: angle at which the frisbee is tossed
     */
    public static boolean maxSimulate(double height, double v, double angle){
        double[] noTarg = {0,0};
        return simulate(height, v, angle, 0, noTarg);
    }

    public static void maxTest(double height, double v, double angle){
        if(maxSimulate(height, v, angle)==false)
            System.out.println("Input was bad!");
        else
            maxPrint();
    }
        
    /**
     * Readable results from a target sim
     */
    public static void targetPrint(){
        System.out.println("__________Start__________");
        System.out.println(y0 + "m starting height\n" + v0 + "m/s\n" + a + " degrees\nTarget X: " + targetX + "m");
        System.out.println("Y box: " + hitbox[0] + "m, " + hitbox[1] + "m\n");
        System.out.println("__________End__________");
        System.out.println("(" + x + ", " + y + ")");
        System.out.println("Error: (" + xErr + ", " + yErr + ")");
        System.out.println("Max Height: " + maxHeight + "m at " + at + "m");
        System.out.println( t + "sec\n");
    }
        
    /**
     * Readable results from a targetless sim
     */
    public static void maxPrint(){
        System.out.println("__________Start__________");
        System.out.println(y0 + "m starting height\n" + v0 + "m/s\n" + a + " degrees");
        System.out.println("__________End__________");
        System.out.println("(" + x + ", " + y + ")");
        System.out.println("Max Height: " + maxHeight + "m at " + at + "m");
        System.out.println( t + "sec");
    }
        
    /**
     * Readable version of the next method
     */
    public static void angleTest(double height, double v, double targX, double[] yBox){
        System.out.println("__________Angle Solver__________");
        System.out.println(height + "m starting height\n" + v + "m/s\nTarget X: " + targX + "m");
        System.out.println("Y box: " + yBox[0] + "m, " + yBox[1] + "m");
        double theAngle = solveAngle(height,v,targX,yBox);
        if(theAngle == -1){
            System.out.println("Either the target is unreachable or input was bad.");
        }
        else{
        	System.out.println("Estimated Angle: " + theAngle + " degrees\n");
        	targetSimulate(height, v, theAngle, targX, yBox);
            targetPrint();
        }
    }

    /**
     * Estimates the angle needed to reach the target when launched at the given speed
     * If no solution seems to exist, returns -1
     * Aims for the center of the hitbox to allow for some margin of error.
     * Preferable to hit on the ascent due to shorter flight time.
     * @param height: height from which the frisbee is launched
     * @param v: magnitude of initial velocity vector
     * @param targX: how far away the target is in the x direction
     * @param yBox: range of target in the y direction
     * @return: the angle that gets the frisbee closest to the hitbox center (prefers ascent)
     */
    public static double solveAngle(double height, double v, double targX, double[] yBox){
        if( parameterCheck(height, v, 5, targX, yBox)==false )//if input is bad, return -1
            return -1;
        double test = 0;//stupidly small angle
        yErr = -.5;
        double bestYerr = Double.MAX_VALUE;
        boolean stop=false;//stops when best is found. time saver
        double bestA = 0;//angle that gets the frisbee closest to hitbox center (ascent)
        double target = (yBox[0]+yBox[1])/2;//center of hitbox
        while(test < 90 && stop==false){//While the test angle is acute and a good angle hasn't been found
            targetSimulate(height, v, test, targX, yBox);//simulate throwing the frisbee at that angle
            if(yErr==0){//Does it hit at all?
                if(vy>=0){//On ascent?
                    if((Math.abs(y-target) < Math.abs(bestYerr))){//closer to center than before?
                        bestYerr=Math.abs(y-target);//this is the closest it's landed to the hitbox center
                        bestA=test;//this is the best angle so far
                    }
                    else{//if it's hitting but is getting further away, the best angle has been found already
                        stop=true;
                    }
                }
                else{//it it's hitting on descent, it must not have hit on ascent 
                	if((Math.abs(y-target) < Math.abs(bestYerr))){//better than before?
                		bestYerr=Math.abs(y-target);//this is the closest it's landed to the hitbox center
                        bestA=test;//this is the best angle so far
                    }
                    else{//if it's hitting but getting further away, best angle has been found already
                    	stop=true;
                    }
                }
            }
            test += ANGLE_INC;//increment test angle
        }
        if(bestA<90)//Was there a successful angle?
        	return bestA;
        else//if not, return -1
        	return -1;
    }
	 
    /**
     * Readable version of the next method
     */
    public static void velocityTest(double height, double a, double targX, double[] yBox){
        
        System.out.println("__________Velocity Solver__________");
        System.out.println(height + "m starting height\n" + a + " degrees\nTarget X: " + targX + "m");
        System.out.println("Y box: " + yBox[0] + "m, " + yBox[1] + "m");
        double theV = solveVelocity(height, a, targX, yBox);
        if(theV == -1){
            System.out.println("Either the target is unreachable or the input was bad.");
        }
        else{
        	System.out.println("Estimated Velocity: " + theV + " meters per second\n");
        	targetSimulate(height, theV, a, targX, yBox );
        	targetPrint();
        }
    }    
	 
	 /**
     * Estimates the velocity needed to reach the target when launched at the given angle
     * If no solution seems to exist, returns -1
     * @param height: height from which the frisbee is launched
     * @param a: angle of attack
     * @param targX: how far away the target is in the x direction
     * @param yBox: range of target in the y direction
     * @return: the speed that hits closest to the center of the hitbox
     */    
    public static double solveVelocity(double height, double a, double targX, double[] yBox){       
    	if( parameterCheck(height, 5, a, targX, yBox)==false )//if input is bad, return -1
            return -1;
        double test = MIN_SPEED;//minimum speed
        yErr = -.5;
        double bestErr = Double.MAX_VALUE;
        double bestV = 0;
        boolean stop = false;
        double target = (yBox[0]+yBox[1])/2;
        while(stop==false && test <= MAX_SPEED){//While the given speed doesn't produce a successful hit or the speed hits max speed     
            targetSimulate(height, test, a, targX, yBox);//simulate throwing the frisbee at that speed
            if(yErr==0){//Does the speed produce a hit?
            	if(Math.abs(y-target) < Math.abs(bestErr)){//Was it closer than before?
                    bestErr=Math.abs(y-target);//this is the closest it's been
                    bestV=test;//this is the best speed so far
                }
            	else{//if it hit but is worse than before, we've it the best speed already
            		stop=true;
            	}
            }
            test += VEL_INC;//increment test speed
        }
        if(bestV>MAX_SPEED){//if max speed couldn't do it, it's not reachable
            return -1;
        }
        return bestV;
    }    
    
    /**
     * Readable version of the next method
     */
    public static void xTest(double height, double v, double a, double[] yBox){
        System.out.println("__________Distance Solver__________");
        System.out.println(height + "m starting height\n" + v + "m/s\n" + a + " degrees");
        System.out.println("Y box: " + yBox[0] + "m, " + yBox[1] + "m");
        double theX = solveX(height, v, a, yBox);
        if(theX == -1){
            System.out.println("Either the target is unreachable or the input was bad.");
        }
        else{
        	System.out.println("Estimated X Distance: " + theX + " meters\n");
        	targetSimulate(height, v, a, theX, yBox );
        	targetPrint();
        }
    }

	 
	 
    /**
     * Estimates the X distance needed to reach the target when launched at the given speed and angle
     * If no solution seems to exist, returns -1
     * @param height: height from which the frisbee is launched
     * @param v: magnitude of initial velocity vector
     * @param a: angle of attack
     * @param yBox: range of target in the y direction
     * @return: the lowest angle of attack needed to hit the target at a given speed
     */
    public static double solveX(double height, double v, double a, double[] yBox){       
    	if( parameterCheck(height, v, a, 5, yBox)==false )//if input is bad, return -1
            return -1;
        double test = 1;//stupidly small distance
        yErr = -.5;
        double bestErr = Double.MAX_VALUE;
        boolean stop = false;
        double bestX = 0;
        double target = (yBox[0]+yBox[1])/2;
        while(test <= MAX_DIST && stop==false){//While the best distance hasn't been found or the distance reaches the end of the field    
            targetSimulate(height, v, a, test, yBox);//simulate throwing the frisbee at that distance
            if(yErr==0){//Does it hit?
            	if(Math.abs(y-target) < Math.abs(bestErr)){//better than before?
                    bestErr=Math.abs(y-target);//this is the closest it's been to the center
                    bestX=test;//this is the best distance
                }
            	else{//If it's hitting but getting further away from the center, the best distance has been found
            		stop = true;
            	}
            }
            test += X_INC;//increment test distance
        }
        if(test > MAX_DIST){
            return -1;
        }
        return bestX;
    }

    /**
     * Tests to see if input is valid
     * @param testH: height from which the frisbee is launched
     * @param testV: magnitude of initial velocity vector
     * @param testA: angle of attack
     * @param testX: how far away the target is in the x direction
     * @param testY: range of target in the y direction
     * @return: the lowest angle of attack needed to hit the target at a given speed
     */
    public static boolean parameterCheck(double testH, double testV, double testA, double testX, double[] testY){
        if( testH<0 || testV<0 || testA>90 || testA<0 || testX<0 || testY[0]>testY[1] || testY[0]<0 || testY[1]<0 ){
            return false;
        }
        return true;
    }

    /**
     * Resets time, position, and max height.
     */
    public static void reset(){
        x=0;
        maxHeight = 0;
        t=0;
    }
}