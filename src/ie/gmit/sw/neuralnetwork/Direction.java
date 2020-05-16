package ie.gmit.sw.neuralnetwork;

public class Direction {
	private static Direction down;
	private static Direction up;

	public static Direction determineDirection(final double d) {
		if (d < 0) {
			return Direction.down;
		} else {
			return Direction.up;
		}
	}
}
