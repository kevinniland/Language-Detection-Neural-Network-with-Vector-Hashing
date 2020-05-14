package ie.gmit.sw.processing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

import ie.gmit.sw.helpers.Utilities;
import ie.gmit.sw.language.Language;

/**
 * @author Kevin Niland
 * @category Processing
 * @version 1.0
 *
 * VectorProcessor - Parses and processes the WiLI language dataset, creating vector values from this. These 
 * vector values are normalized
 */
public class VectorProcessor {
	private Language[] languages = Language.values();
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
	private DecimalFormat decimalFormat = new DecimalFormat("###.###");
	private File languageFile = new File("wili-2018-Small-11750-Edited.txt");
	private File csvFile = new File("data.csv");
	private FileWriter fileWriter;
	private String line, text, language, ngram;
	private String[] record;
	private int i, ngramSize, vectorSize = 500;
	private final int NUMBER_OF_LANGUAGES = 235;
	private double[] vector = new double[100];
	private double[] index = new double[NUMBER_OF_LANGUAGES];

	/**
	 * 
	 * @param ngramSize
	 * @param vectorSize
	 */
	public VectorProcessor(int ngramSize, int vectorSize) {
		this.ngramSize = ngramSize;
		this.vectorSize = vectorSize;
	}

	/**
	 * Parse WiLI dataset and pass it of to process()
	 * 
	 * @throws Exception
	 */
	public void parse() throws Exception {
		System.out.println("Reading WiLI language dataset...");
		
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(languageFile)));
					
			System.out.println("Done");
			System.out.println("Parsing file...");
			
			while ((line = bufferedReader.readLine()) != null) {
				process(line);
			}
			
			System.out.println("Done");
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	/**
	 * Process WiLI dataset
	 * 
	 * @param line
	 * @throws Exception
	 */
	public void process(String line) throws Exception {
		record = line.trim().split("@");
		
		if (record.length > 2) {
			return;
		}

		// Replace any and all punctuation
		text = record[0].replaceAll("\\p{P}", "").toUpperCase();
		language = record[1];

		for (i = 0; i < vector.length; i++) {
			vector[i] = 0;
		}
		
		for (i = 0; i <= vector.length - ngramSize; i++) {
			// Set the vector index i to 0 on each iteration
//			vector[i] = 0;
			
			// Create an ngram from the language text
			ngram = text.substring(i, i + ngramSize);
//			vector[i] = ngram.hashCode() % vector.length;
			
//			vector[i]++;
			
			vector[ngram.hashCode() % vector.length]++;
		}
		
		// Normalize vector values
		vector = Utilities.normalize(vector, 0, 1);
		
		// Save normalized vector values to CSV file
		fileWriter = new FileWriter(csvFile, true);
		bufferedWriter = new BufferedWriter(fileWriter);
		
		/**
		 * For each ngram, format it accordingly
		 */
		for (i = 0; i < vector.length - ngramSize; i++) {
			bufferedWriter.write(decimalFormat.format(vector[i]) + ", ");
		}

		for (i = 0; i < languages.length; i++) {
			if (language.equalsIgnoreCase(String.valueOf(languages[i]))) {
				index[i] = 1;
//				bufferedWriter.write(index[i] + ", ");
			}
			
			bufferedWriter.write(index[i] + ", ");
			index[i] = 0;

		}
		
		bufferedWriter.newLine();
		bufferedWriter.close();
	}

//	public static void main(String[] args) throws Exception {
//		new VectorProcessor(4, 100).parse();
//	}
}
