package delta.neuralnetwork;

import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

import delta.matrix.Matrix;

public class NeuralNetTest {
	private Random random = new Random();
	
	@Test
	public void testTrainEngine() {
		int inputRows = 5;
		int cols = 6;
		int outputRows = 7;
		
		Matrix input = Util.generateExpetedMatrix(inputRows, cols);
		Matrix expected = Util.generateTrainableExpectedMatrix(outputRows, input);
		
		Engine engine = new Engine();
		engine.add(Transform.DENSE, 6, inputRows);
		engine.add(Transform.RELU);
		engine.add(Transform.DENSE, outputRows);
		engine.add(Transform.SOFTMAX);
		
		BatchResult batchResult = engine.runFowards(input);
		engine.evaluate(batchResult, expected);
		
		double loss1 =batchResult.getLoss();
		
		engine.runBackwards(batchResult, expected);
		engine.adjust(batchResult, 0.01);
		batchResult = engine.runFowards(input);
		engine.evaluate(batchResult, expected);
		
		double loss2 = batchResult.getLoss();
		double percentCorrect = batchResult.getPercentCorrect();
		System.out.println(loss1+" "+loss2+" "+percentCorrect);
		
	}

	@Test
	public void testWeightGradient() {
		int inputRows = 4;
		int outputRows = 5;
		Matrix weights = new Matrix(outputRows, inputRows, i -> random.nextGaussian());
		Matrix input = Util.generateInputMatrix(inputRows, 1);
		Matrix expected = Util.generateExpetedMatrix(outputRows, 1);

		Matrix output = weights.multiply(input).softmax();
		Matrix loss = LossFunctions.crossEntropy(expected, output);

		Matrix calculatedError = output.apply((index, value) -> value - expected.get(index));

		Matrix calculatedWeightGradients = calculatedError.multiply(input.transpose());

		Matrix approximatedWeightGradients = Approximator.weightGradient(weights, w -> {
			Matrix out = w.multiply(input).softmax();
			return LossFunctions.crossEntropy(expected, out);
		});
		
		calculatedWeightGradients.setTolerance(0.01);
		assertTrue(calculatedWeightGradients.equals(approximatedWeightGradients));
	}

	// @Test
	public void testEngine() {
		int inputRows = 5;
		int cols = 6;
		int outputROws = 4;

		Engine engine = new Engine();

		engine.add(Transform.DENSE, 8, 5);
		// engine.add(Transform.RELU);
		engine.add(Transform.DENSE, 5);
		// engine.add(Transform.RELU);
		engine.add(Transform.DENSE, 4);

		engine.add(Transform.SOFTMAX);
		engine.setStoreInputError(true);

		Matrix input = Util.generateInputMatrix(inputRows, cols);
		Matrix expected = Util.generateExpetedMatrix(outputROws, cols);

		Matrix approximatedError = Approximator.gradient(input, in -> {
			BatchResult batchResult = engine.runFowards(in);
			return LossFunctions.crossEntropy(expected, batchResult.getOutput());
		});

		BatchResult batchResult = engine.runFowards(input);
		engine.runBackwards(batchResult, expected);

		Matrix calculatedError = batchResult.getInputError();

		calculatedError.setTolerance(0.01);

		assertTrue(calculatedError.equals(approximatedError));

	}

	// @Test
	public void testBackprop() {

		interface NeuralNet {
			Matrix apply(Matrix m);
		}

		final int inputRows = 4;
		final int cols = 5;
		final int outputRows = 4;

		Matrix input = new Matrix(inputRows, cols, i -> random.nextGaussian());
		Matrix expected = new Matrix(outputRows, cols, i -> 0);

		Matrix weights = new Matrix(outputRows, inputRows, i -> random.nextGaussian());
		Matrix biases = new Matrix(outputRows, 1, i -> random.nextGaussian());

		for (int col = 0; col < cols; col++) {
			int randomRow = random.nextInt(outputRows);
			expected.set(randomRow, col, 1);
		}

		NeuralNet neuralNet = m -> {
			Matrix out = m.apply((index, value) -> value > 0 ? value : 0);

			out = weights.multiply(out);// weights
			out.modify((row, col, value) -> value + biases.get(row));// biases
			out = out.softmax(); // softmax activation function
			return out;
		};
		Matrix softmaxOutput = neuralNet.apply(input);

		Matrix approximatedResult = Approximator.gradient(input, in -> {
			Matrix out = neuralNet.apply(in);
			return LossFunctions.crossEntropy(expected, out);
		});

		Matrix calculatedResult = softmaxOutput.apply((index, value) -> value - expected.get(index));
		calculatedResult = weights.transpose().multiply(calculatedResult);
		calculatedResult = calculatedResult.apply((index, value) -> input.get(index) > 0 ? value : 0);

		assertTrue(approximatedResult.equals(calculatedResult));
	}

	// @Test
	public void testSoftmaxCrossEntropyGradient() {
		final int rows = 4;
		final int cols = 5;

		Matrix input = new Matrix(rows, cols, i -> random.nextGaussian());
		Matrix expected = new Matrix(rows, cols, i -> 0);

		for (int col = 0; col < cols; col++) {
			int randomRow = random.nextInt(rows);
			expected.set(randomRow, col, 1);
		}

		Matrix softmaxOutput = input.softmax();

		Matrix result = Approximator.gradient(input, in -> {
			return LossFunctions.crossEntropy(expected, in.softmax());
		});

		result.forEach((index, value) -> {
			double softmaxValue = softmaxOutput.get(index);
			double expectedValue = expected.get(index);

			assertTrue(Math.abs(value - (softmaxValue - expectedValue)) < 0.01);
//			System.out.println(value+", "+(softmaxValue-expectedValue));
		});

	}

	// @Test
	public void testAddIncrement() {
		Matrix m = new Matrix(5, 8, i -> random.nextGaussian());

		int row = 3;
		int col = 2;
		double inc = 10;

		Matrix result = m.addIncrement(row, col, inc);

		double incrementedValue = result.get(row, col);
		double originalValue = m.get(row, col);

		assertTrue(Math.abs(incrementedValue - (originalValue + inc)) < 0.00001);

	}

	// @Test
	public void testApproximator() {
		final int rows = 4;
		final int cols = 5;

		Matrix input = new Matrix(rows, cols, i -> random.nextGaussian()).softmax();
		Matrix expected = new Matrix(rows, cols, i -> 0);

		for (int col = 0; col < cols; col++) {
			int randomRow = random.nextInt(rows);
			expected.set(randomRow, col, 1);
		}

		Matrix result = Approximator.gradient(input, in -> {
			return LossFunctions.crossEntropy(expected, in);
		});

		input.forEach((index, value) -> {
			double resultValue = result.get(index);
			double expectedValue = expected.get(index);

			if (expectedValue < 0.001) {
				assertTrue(Math.abs(resultValue) < 0.01);
			} else {
				assertTrue(Math.abs(resultValue + 1.0 / value) < 0.01);
			}
		});

	}

	// @Test
	public void testCrossEntropy() {
		double[] expectedValues = { 1, 0, 0, 0, 0, 1, 0, 1, 0 };
		Matrix expected = new Matrix(3, 3, i -> expectedValues[i]);

		Matrix actual = new Matrix(3, 3, i -> 0.05 * i * i).softmax();

		Matrix result = LossFunctions.crossEntropy(expected, actual);

		actual.forEach((row, col, index, value) -> {
			double expectedValue = expected.get(index);
			double loss = result.get(col);

			if (expectedValue > 0.9) {
				assertTrue(Math.abs(Math.log(value) + loss) < 0.001);
			}
		});
	}

	// @Test
	public void testTemp() {
		int inputSize = 5;
		int layer1Size = 6;
		int layer2Size = 4;

		Matrix input = new Matrix(inputSize, 1, i -> random.nextGaussian());
		Matrix layer1Weights = new Matrix(layer1Size, input.getRows(), i -> random.nextGaussian());
		Matrix layer1Biases = new Matrix(layer1Size, 1, i -> random.nextGaussian());

		Matrix layer2Weights = new Matrix(layer2Size, layer1Weights.getRows(), i -> random.nextGaussian());
		Matrix layer2Biases = new Matrix(layer2Size, 1, i -> random.nextGaussian());

		var output = input;
		System.out.println(output);

		output = layer1Weights.multiply(output);
		System.out.println(output);

		output = output.modify((row, col, value) -> value + layer1Biases.get(row));
		System.out.println(output);

		output = output.modify(value -> value > 0 ? value : 0);
		System.out.println(output);

		output = layer2Weights.multiply(output);
		System.out.println(output);

		output = output.modify((row, col, value) -> value + layer2Biases.get(row));
		System.out.println(output);

		output = output.softmax();
		System.out.println(output);
	}

	// @Test
	public void testSoftmax() {
		Matrix m = new Matrix(5, 8, i -> random.nextGaussian());

		Matrix result = m.softmax();

		double[] colSums = new double[8];

		result.forEach((row, col, value) -> {
			assertTrue(value >= 0 && value <= 1.0);

			colSums[col] += value;
		});

		for (var sum : colSums) {
			assertTrue(Math.abs(sum - 1.0) < 0.00001);
		}
	}

	// @Test
	public void testSumColumns() {
		Matrix m = new Matrix(4, 5, i -> i);

		Matrix result = m.sumColumns();

		double[] expectedValues = { +30.00000, +34.00000, +38.00000, +42.00000, +46.00000 };
		Matrix expected = new Matrix(1, 5, i -> expectedValues[i]);

		assertTrue(expected.equals(result));

	}

	// @Test
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

	// @Test
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
