package delta.neuralnetwork;

import java.util.Random;

import delta.matrix.Matrix;

public class Util {

	private static Random random = new Random();
	
	public static Matrix generateInputMatrix(int rows, int cols) {
		return new Matrix(rows, cols, i -> random.nextGaussian());
	}
	
	public static Matrix generateExpetedMatrix(int rows, int cols) {
		Matrix expected = new Matrix(rows, cols, i -> 0);

		for (int col = 0; col < cols; col++) {
			int randomRow = random.nextInt(rows);
			expected.set(randomRow, col, 1);
		}
		
		return expected;
	}
}
