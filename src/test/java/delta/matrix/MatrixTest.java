package delta.matrix;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

import delta.neuralnetwork.Util;

public class MatrixTest {
	private Random random = new Random();
	
	@Test
	public void testGetGreatestRowNumber() {
		double[] values = {2, -6, 7, 7, 2, -6, 11, -1, 1};
		Matrix m = new Matrix(3, 3, i->values[i]);
		
		Matrix result = m.getGreatestRowNumbers();
		
		double[] expectedValues = {2, 1, 0};
		Matrix expected = new Matrix(1, 3, i->expectedValues[i]);
		
		assertTrue(expected.equals(result));
	}
	@Test
	public void testAverageColumn() {
		int rows = 7;
		int cols = 5;
		
		Matrix m = new Matrix(rows, cols, i->2*i-3);
		
		double averageIndex = (cols-1)/2.0;
		
		Matrix expected = new Matrix(rows, 1);
		expected.modify((row,col, value)->2*(row*cols + averageIndex)-3);
		
		Matrix result = m.averageColumn();
		assertTrue(expected.equals(result));
	}
	
	@Test
	public void testTranspose() {
		Matrix m = new Matrix(2,3,i->i);
		
		Matrix result = m.transpose();
		
		double [] expectedValues = {0, 3, 1, 4, 2, 5};
		Matrix expected = new Matrix(3, 2, i->expectedValues[i]);
		
//		System.out.println(m);
//		System.out.println(result);
		
		assertTrue(expected.equals(result));
	}
	
	@Test
	public void testMultiply() {
		Matrix m1 = new Matrix(2,3,i->i);
		Matrix m2 = new Matrix(3,2,i->i);
		
		double[] expectedValues = {10, 13, 28, 40};
		Matrix expected = new Matrix(2,2,i->expectedValues[i]);
		
		Matrix result = m1.multiply(m2);
		assertTrue(expected.equals(result));
	}
	
	@Test
	public void testMultiplySpeed() {
		int rows = 500;
		int cols = 500;
		int mid = 50;
		
		Matrix m1 = new Matrix(rows,mid,i->i);
		Matrix m2 = new Matrix(mid,cols,i->i);
		
		var start = System.currentTimeMillis();
		m1.multiply(m2);
		var end = System.currentTimeMillis();
		
		//System.out.printf("Tiempo de multiplicación de matrices: %dms\n",end-start);
	}
	
	@Test
	public void testEquals() {
		Matrix m1 = new  Matrix(3, 4, i -> 0.5*(i-6));
		Matrix m2 = new  Matrix(3, 4, i -> 0.5*(i-6));
		Matrix m3 = new  Matrix(3, 4, i -> 0.5*(i-6.2));
		
		assertTrue(m1.equals(m2));
		assertFalse(m1.equals(m3));
	}
	
	@Test
	public void testAddMatrices() {
		Matrix m1 = new Matrix(2,2,i->i);
		Matrix m2 = new Matrix(2,2,i->i*1.5);
		Matrix expected = new Matrix(2,2,i->i*2.5);
		
		Matrix result = m1.apply((index, value)->value + m2.get(index));
		
		assertTrue(expected.equals(result));
//		System.out.println(m1);
//		System.out.println(m2);
//		System.out.println(expected);
//		System.out.println(result);
	}

	@Test
	public void testMultiplyDouble() {
		Matrix m = new  Matrix(3, 4, i -> 0.5*(i-6));
		
		double x = 0.5;
		Matrix expected = new  Matrix(3, 4, i -> x*0.5*(i-6));
		
		Matrix result = m.apply((index, value)->x*value);
		
//		System.out.println(m);
//		System.out.println(result);
//		System.out.println(expected);
		
		assertTrue(result.equals(expected));
		assertTrue(Math.abs(result.get(1) + 1.25000) < 0.0001);
	}
	
	@Test
	public void testToString() {
		Matrix m = new  Matrix(3, 4, i -> i*2);
		
		String text = m.toString();
		double[] expected = new double[12];
		for (int i = 0; i < expected.length; i++) {
			expected[i] = i*2;
		}
		var rows = text.split("\n");
		
		assertTrue(rows.length == 3);
		
		int index = 0;
		for (var row : rows) {
			var values = row.split("\\s+");
			for (String textValues : values) {
				if(textValues.length() == 0)
					continue;
				
				var doubleValue = Double.valueOf(textValues.replace(",", "."));
				
				assertTrue(Math.abs(doubleValue - expected[index])<0.0001);
				index++;
			}
		}
	}

}
