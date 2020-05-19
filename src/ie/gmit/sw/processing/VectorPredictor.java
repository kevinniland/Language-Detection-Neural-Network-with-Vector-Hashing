package ie.gmit.sw.processing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.encog.neural.networks.BasicNetwork;
import org.encog.util.simple.EncogUtility;

import ie.gmit.sw.helpers.Utilities;
import ie.gmit.sw.neuralnetwork.NeuralNetwork;

public class VectorPredictor {
	private BasicNetwork basicNetwork;
	private BufferedReader bufferedReader;
	private String file, line, ngram;
	private int i, ngramSize;
	private double[] vector;
	
	public VectorPredictor(String file, int ngramSize, int vectorSize) {
		this.file = file;
		this.ngramSize = ngramSize;
		vector = new double[vectorSize];
	}
	
	/**
	 * Parse WiLI dataset and pass it of to process()
	 * 
	 * @throws Exception
	 */
	public void parse() throws Exception {
		System.out.print("\nReading file " + file + "...");
//		System.out.println(System.getProperty("user.dir"));
		
		for (i = 0; i < vector.length; i++) {
			vector[i] = 0; 
		}
		
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(file))));

			System.out.println("Done");
			System.out.print("Processing file...");

			while ((line = bufferedReader.readLine()) != null) {
				process(line);
			}

			System.out.println("Done");
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
		
		new NeuralNetwork().getPrediction(Utilities.normalize(vector, 0, 1));
//		
//		basicNetwork = Utilities.loadNeuralNetwork("./kfold.nn");
//		EncogUtility.evaluate(basicNetwork, new NeuralNetwork().getPrediction(Utilities.normalize(vector, 0, 1)));
	}
	
	/**
	 * Process file
	 * 
	 * @param line
	 * @throws Exception
	 */
	public void process(String line) throws Exception {
		line = line.toLowerCase().replaceAll("[0-9]", "");
		
		for (i = 0; i < line.length() - ngramSize; i++) {
			ngram = line.substring(i, i + ngramSize);
			
			vector[ngram.hashCode() % vector.length]++;
		}
	}
}
