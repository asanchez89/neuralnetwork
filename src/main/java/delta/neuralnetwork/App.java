package delta.neuralnetwork;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	double[] x = {0, 1};//inputs
    	double[] w = {0.5, 0.5};//weights
    	double b = 0.5; //bias
    	double z = 0.0; // weighted sum
    	
    	for (int i = 0; i < x.length; i++) {
			z += x[i] * w[i];
		}
    	
    	z += b;
    	
    	double a = z>0? 1.0:0.0;//activation function
    	
    	System.out.println(a);//output
    	
    }
}
