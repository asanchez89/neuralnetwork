package delta.matrix;

import org.junit.Test;

public class MatrixTest {

	@Test
	public void constructionTest() {
		Matrix m = new  Matrix(3, 4, i -> i*2);
		System.out.println(m);
		
	}

}
