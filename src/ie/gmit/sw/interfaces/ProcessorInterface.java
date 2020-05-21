package ie.gmit.sw.interfaces;

import ie.gmit.sw.language.Language;

/**
 * @author Kevin Niland
 * @category Interface
 * @version 1.0
 * 
 *          ProcessorInterface - Interface for VectorProcessor.java
 *
 */
public interface ProcessorInterface {
	public void parse() throws Exception;

	public void process(String line) throws Exception;

	public double[] toVector(Language language) throws Exception;
}
