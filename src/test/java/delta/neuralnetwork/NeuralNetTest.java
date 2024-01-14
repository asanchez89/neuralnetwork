package delta.neuralnetwork;

import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

import delta.matrix.Matrix;

public class NeuralNetTest {
	private Random random = new Random();

	@Test
	public void testSumColumns() {
		Matrix m = new Matrix(4, 5, i -> i);

		Matrix result = m.sumColumns();

		double[] expectedValues = { +30.00000, +34.00000, +38.00000, +42.00000, +46.00000 };
		Matrix expected = new Matrix(1, 5, i->expectedValues[i]);
		
		assertTrue(expected.equals(result));

	}

	@Test
	public void testAddBias() {
		Matrix input = new Matrix(3, 3, i -> (i + 1));
		Matrix weights = new Matrix(3, 3, i -> (i + 1));
		Matrix biases = new Matrix(3, 1, i -> (i + 1));

		Matrix result = weights.multiply(input).modify((row, col, value) -> value + biases.get(row));

		double[] expectedValues = { +31.00000, +37.00000, +43.00000, +68.00000, +83.00000, +98.00000, +105.00000,
				+129.00000, +153.00000 };
		Matrix expected = new Matrix(3, 3, i -> expectedValues[i]);

		assertTrue(expected.equals(result));

	}

	@Test
	public void testReLu() {

		final int numberNeurons = 5;
		final int numberInputs = 6;
		final int inputSize = 4;

		Matrix input = new Matrix(inputSize, numberInputs, i -> random.nextDouble());
		Matrix weights = new Matrix(numberNeurons, inputSize, i -> random.nextGaussian());
		Matrix biases = new Matrix(numberNeurons, 1, i -> random.nextGaussian());

		Matrix result1 = weights.multiply(input).modify((row, col, value) -> value + biases.get(row));
		Matrix result2 = weights.multiply(input).modify((row, col, value) -> value + biases.get(row))
				.modify(value -> value > 0 ? value : 0);

		result2.forEach((index, value) -> {
			double originalValue = result1.get(index);
			if (originalValue > 0) {
				assertTrue(Math.abs(originalValue - value) < 0.000001);
			} else {
				assertTrue(Math.abs(value) < 0.000001);
			}
		});

	}
}
