package delta.neuralnetwork;

import java.util.function.Function;

import delta.matrix.Matrix;

public class Approximator {

	public static Matrix gradient(Matrix input, Function<Matrix, Matrix> transform) {

		final double INC = 0.00000001;
		
		Matrix loss1 = transform.apply(input);
		
		assert loss1.getCols() == input.getCols():"Input/loss columnas no son iguales";
		assert loss1.getRows() == 1:"La transformada no retorna una sola fila";

		Matrix result = new Matrix(input.getRows(), input.getCols(), i->0);
		
		input.forEach((row, col, index, value) -> {
			Matrix incremented = input.addIncrement(row, col, INC);
			
			Matrix loss2 = transform.apply(incremented);
			
			double rate = (loss2.get(col)-loss1.get(col))/INC;
			result.set(row, col, rate);
		});

		return result;
	}
}
