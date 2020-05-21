package ie.gmit.sw.interfaces;

/**
 * @author Kevin Niland
 * @category Interface
 * @version 1.0
 * 
 *          PredictorInterface - Interface for VectorPredictor.java
 *
 */
public interface PredictorInterface {
	public void parse() throws Exception;

	public void process(String line) throws Exception;
}
