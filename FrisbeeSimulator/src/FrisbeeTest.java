public class FrisbeeTest{
	public static void main(String[] args){
		final double height = 0.762;
		final double speed = 14;
		final double angle = 13.95;
		final double xDist = 5.4864;
		final double[] goal = {2.64478, 2.94957};

		FrisbeeSim j = new FrisbeeSim();
		FrisbeeGraph graph = new FrisbeeGraph("frisbee.csv", FrisbeeSim.graph(height, speed, angle, xDist, goal) );
		j.angleTest(height, speed, xDist, goal);
		j.xTest(height, speed, angle, goal);
	}
}