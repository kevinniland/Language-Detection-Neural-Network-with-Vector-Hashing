package ie.gmit.sw.processing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

import ie.gmit.sw.Utilities;
import ie.gmit.sw.language.Language;

/**
 * @author Kevin Niland
 * @category Processing
 * @version 1.0
 *
 */
public class VectorProcessor {
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
	private CharSequence kmer;
	private File languageFile = new File("wili-2018-Small-11750-Edited.txt");
	private File csvFile = new File("data.csv");
	private FileWriter fileWriter;
	private Language[] languages = Language.values();
	private DecimalFormat decimalFormat = new DecimalFormat("###.###");
	private String line = null;
	private int i, index, ngramSize, vectorSize;
	private static int NUMBER_OF_LANGUAGES = 235;
	private double[] languageIndex = new double[NUMBER_OF_LANGUAGES];
	private double[] vector = new double[100];

	public VectorProcessor(int ngramSize /*, int vectorSize */) {
		this.ngramSize = ngramSize;
//		this.vectorSize = vectorSize;
	}

	public void readFile() {
		System.out.println("Reading WiLI language dataset...");

		// Read in the language dataset
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(languageFile)));

			System.out.println("Done");
			System.out.println("Parsing file...");

			// Split the file and trim it
			while ((line = bufferedReader.readLine()) != null) {
				String[] fileRecord = line.trim().split("@");

				if (fileRecord.length != 2) {
					continue;
				}

				parse(fileRecord[0].toLowerCase().replaceFirst("\\p{P}", ""), fileRecord[1]);
			}

			System.out.println("Done");
			bufferedReader.close();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * Parses the text and languages of the subject file. Creates kmers from the
	 * subject text and add the kmer and language the kmer is in to the database
	 * 
	 * @param text - Subject text from the data set file
	 * @param lang - Language of subject text
	 * @throws IOException 
	 */
	private void parse(String text, String lang, int... ks) throws IOException {
//		Language language = Language.valueOf(lang);

//		for (i = 0; i < vector.length; i++) {
//			vector[i] = 0;
//		}

		/**
		 * Create the k-mers
		 * 
		 * Add the k-mer and language to the proxy database
		 */
		for (i = 0; i <= vector.length - ngramSize; i++) {
			vector[i] = 0;
			
			kmer = text.substring(i, i + ngramSize);

			index = kmer.hashCode() % vector.length;

			vector[index]++;
		}

		vector = Utilities.normalize(vector, -1, 1);
		
		fileWriter = new FileWriter(csvFile, true);
		bufferedWriter = new BufferedWriter(fileWriter);
		
		for (i = 0; i <= vector.length - ngramSize; i++) {
			bufferedWriter.write(decimalFormat.format(vector[i]) + ", ");
		}
		
		for (i = 0; i < languages.length; i++) {
			if (lang.equalsIgnoreCase(String.valueOf(languages[i]))) {
				languageIndex[i] = 1;
			}
			
			bufferedWriter.write(languageIndex[i] + ", ");
			
			languageIndex[i] = 0; 
		}
		
		bufferedWriter.newLine();
		bufferedWriter.close();
	}

	public static void main(String[] args) {
		new VectorProcessor(4).readFile();
	}
}
