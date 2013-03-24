/**
 *
 * @author Liam
 */
public abstract class Simulation {
    //EVERYTHING IS IN SI UNITS EXCEPT FOR ANGLES. THOSE ARE IN DEGREES
    protected static double y0;                             //initial height
    protected static double v0;                             //magnitude of initial velocity vector
    protected static double vx0;                            //initial x velocity
    protected static double vy0;                            //initial y velocity
    protected static double angle;                          //angle of attack
    protected static double targetX;                        //how far the target is in the x direction
    protected static double[] hitbox;                       //range of the target in the y direction
    protected static double xErr;                           //how off the frisbee lands in the x direction
    protected static double yErr;                           //how off the frisbee lands in the y direction
    //position n velocity
    protected static double x;                              //current x location
    protected static double y;                              //current y location
    protected static double vx;                             //current x velocity
    protected static double vy;                             //current y velocity
    protected static double t;                              //time
    //Properties of Frisbee and environment
    protected static final double MASS = .180;              //Frisbee mass according to handbook
    protected static final double CL0 = .1;                 //coefficient used for determining Coefficient of Lift
    protected static final double CD0 = .08;                //coefficient used for determining Coefficient of Drag
    protected static final double CDA = 2.72;               //other coefficient used for determining Coefficient of Drag
    protected static final double CLA = 1.4;                //other coefficient used for determining Coefficient of Lift
    protected static final double GRAV = 9.80;              //gravitational acceleration
    protected static final double AREA = 0.06131160497;     //area of Frisbee top
    protected static final double RHO = 1.2041;             //air density
    protected static final double A0 = -4;                  //angle at which drag is minimized
    protected static double CX;                             //x coefficients bundled into one
    protected static double CY;                             //y coefficients bundled into one
    //Desirables
    protected static double maxHeight=0;                    //Maximum height achieved
    protected static double at=0;                           //distance at which that height is achieved
    //Testers
    protected static final double MAX_DIST = 16.4592;       //maximum length of the field.
    protected static final double MIN_SPEED = .5;           //minimum velocity tested when solving for velocity
    protected static final double MAX_SPEED = 25;           //maximum velocity tested when solving for velocity
    protected static final double VEL_INC = .1;             //amount by which velocity gets incremented when solving for velocity
    protected static final double ANGLE_INC = .15;          //amount by which angle of attack gets increased when solving for angle
    protected static final double X_INC = .1;               //amount by which distance gets increased when solving for distance
    
    public Simulation(double height, double v, double ang, double maxX, double[] targetY){
        y0=height;
        y=height;
        v0=v;
        vx0=v*Math.cos(ang*Math.PI/180);
        vx=vx0;
        vy0=v*Math.sin(ang*Math.PI/180);
        vy=vy0;
        angle=ang;
        targetX=maxX;
        hitbox=targetY;
    }
    
    public abstract boolean simulate();
    
    public boolean simulate(double height, double v, double ang, double maxX, double[] targetY){
        y0=height;
        y=height;
        v0=v;
        vx0=v*Math.cos(ang*Math.PI/180);
        vx=vx0;
        vy0=v*Math.sin(ang*Math.PI/180);
        vy=vy0;
        angle=ang;
        targetX=maxX;
        hitbox=targetY;
        return simulate();
    }
    
    public boolean parameterCheck(double testH, double testV, double testA, double testX, double[] testY){
        if( testH<0 || testV<0 || testA>90 || testA<0 || testX<0 || testY[0]>testY[1] || testY[0]<0 || testY[1]<0 ){
            return false;
        }
        return true;
    }
    
    public void reset(){
        x=0;
        maxHeight = 0;
        t=0;
    }
    
    public void calcError(){
        if(y<0){//if it goes "underground" because of an extra iteration, say it hit the ground
            y=0;
        }
        xErr = x-targetX;
        if(xErr<.1 && xErr>0){//If it goes past the x bound because of an extra iteration, say it hit the wall
            xErr=0;
            x=targetX;
        }
        if(y>hitbox[1]){//positive error if it lands above the upper boundary of the hitbox
            yErr=y-hitbox[1];
        }
        else if(y<hitbox[0]){//negative error if it lands below the lower boundary of the hitbox
            yErr=y-hitbox[0];
        }
        else{yErr=0;}//in the hitbox.
    }
    
    public abstract boolean graph();
    
    /**
     * Readable version of the next method
     */
    public void testAngle(double height, double v, double targX, double[] yBox){
        System.out.println(height + "m starting height\n" + v + "m/s\nTarget X: " + targX + "m");
        System.out.println("Y box: " + yBox[0] + "m, " + yBox[1] + "m");
        double theAngle = solveAngle(height,v,targX,yBox);
        if(theAngle == -1){
            System.out.println("Either the target is unreachable or input was bad.");
        }
        else{
        	System.out.println("Estimated Angle: " + theAngle + " degrees\n");
        	simulate(height, v, theAngle, targX, yBox);
                print();
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
    public double solveAngle(double height, double v, double targX, double[] yBox){
        if( parameterCheck(height, v, 5, targX, yBox)==false )//if input is bad, return -1
            return -1;
        double test = 0;//stupidly small angle
        yErr = -.5;
        double bestYerr = Double.MAX_VALUE;
        boolean stop=false;//stops when best is found. time saver
        double bestA = 0;//angle that gets the frisbee closest to hitbox center (ascent)
        double target = (yBox[0]+yBox[1])/2;//center of hitbox
        while(test < 90 && stop==false){//While the test angle is acute and a good angle hasn't been found
            simulate(height, v, test, targX, yBox);//simulate throwing the frisbee at that angle
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
    public void testVelocity(double height, double ang, double targX, double[] yBox){
        
        System.out.println("__________Velocity Solver__________");
        System.out.println(height + "m starting height\n" + ang + " degrees\nTarget X: " + targX + "m");
        System.out.println("Y box: " + yBox[0] + "m, " + yBox[1] + "m");
        double theV = solveVelocity(height, ang, targX, yBox);
        if(theV == -1){
            System.out.println("Either the target is unreachable or the input was bad.");
        }
        else{
        	System.out.println("Estimated Velocity: " + theV + " meters per second\n");
        	simulate(height, theV, ang, targX, yBox );
        	print();
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
    public double solveVelocity(double height, double a, double targX, double[] yBox){       
    	if( parameterCheck(height, 5, a, targX, yBox)==false ) {
            return -1;
        }
        double test = MIN_SPEED;//minimum speed
        yErr = -.5;
        double bestErr = Double.MAX_VALUE;
        double bestV = 0;
        boolean stop = false;
        double target = (yBox[0]+yBox[1])/2;
        while(stop==false && test <= MAX_SPEED){//While the given speed doesn't produce a successful hit or the speed hits max speed     
            simulate(height, test, a, targX, yBox);//simulate throwing the frisbee at that speed
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
    public void testDistance(double height, double v, double a, double[] yBox){
        System.out.println("__________Distance Solver__________");
        System.out.println(height + "m starting height\n" + v + "m/s\n" + a + " degrees");
        System.out.println("Y box: " + yBox[0] + "m, " + yBox[1] + "m");
        double theX = solveDistance(height, v, a, yBox);
        if(theX == -1){
            System.out.println("Either the target is unreachable or the input was bad.");
        }
        else{
        	System.out.println("Estimated X Distance: " + theX + " meters\n");
        	simulate(height, v, a, theX, yBox );
        	print();
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
    public double solveDistance(double height, double v, double a, double[] yBox){       
    	if( parameterCheck(height, v, a, 5, yBox)==false ) {
            return -1;
        }
        double test = 1;//stupidly small distance
        yErr = -.5;
        double bestErr = Double.MAX_VALUE;
        boolean stop = false;
        double bestX = 0;
        double target = (yBox[0]+yBox[1])/2;
        while(test <= MAX_DIST && stop==false){//While the best distance hasn't been found or the distance reaches the end of the field    
            simulate(height, v, a, test, yBox);//simulate throwing the frisbee at that distance
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
     * Readable results from a sim
     */
    public void print(){
        System.out.println(y0 + "m starting height\n" + v0 + "m/s\n" + angle + " degrees\nTarget X: " + targetX + "m");
        System.out.println("Y box: " + hitbox[0] + "m, " + hitbox[1] + "m\n");
        System.out.println("__________End__________");
        System.out.println("(" + x + ", " + y + ")");
        System.out.println("Error: (" + xErr + ", " + yErr + ")");
        System.out.println("Max Height: " + maxHeight + "m at " + at + "m");
        System.out.println( t + "sec\n");
    }
    
}
