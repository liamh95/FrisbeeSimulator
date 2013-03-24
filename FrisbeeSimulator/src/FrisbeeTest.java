/**
 * EULER
 *  Y: simulate()
 *  Y: simulate(params)
 *  Y: solveAngle(params)
 *  Y: solveVelocity(params)
 *  Y: solveDistance(params)
 * PRECISE
 *  
 * @author Liam
 */
public class FrisbeeTest{
	public static void main(String[] args){
		final double height = 0.762;
		final double speed = 14;
		final double angle = 13.95;
		final double xDist = 5.4864;
		final double[] goal = {2.64478, 2.94957};

		EulerSim e = new EulerSim(height, speed, angle, xDist, goal, .001);
		PreciseSim p = new PreciseSim(height, speed, angle, xDist, goal, .001);
		FrisbeeSim j = new FrisbeeSim();
		
		
                
		FrisbeeGraph eulerG = new FrisbeeGraph("Euler.csv", e.graph());
		FrisbeeGraph preciseG = new FrisbeeGraph("Precise.csv", p.graph());
		
	}
}