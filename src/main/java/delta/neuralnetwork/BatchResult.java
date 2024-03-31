package delta.neuralnetwork;

import java.util.LinkedList;

import delta.matrix.Matrix;

public class BatchResult {

	private LinkedList<Matrix> io = new LinkedList<>();
	
	public LinkedList<Matrix> getIo(){
		return io;
	}
	
	public void addIo(Matrix m) {
		io.add(m);
	}
}
