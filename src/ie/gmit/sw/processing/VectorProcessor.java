package ie.gmit.sw.processing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import ie.gmit.sw.helpers.Utilities;
import ie.gmit.sw.interfaces.ProcessorInterface;
import ie.gmit.sw.language.Language;

/**
 * @author Kevin Niland
 * @category Processing
 * @version 1.0
 *
 *          VectorProcessor - Parses and processes the WiLI language dataset,
 *          creating vector values from this. These vector values are normalized
 */
public class VectorProcessor implements ProcessorInterface {
	private BufferedReader bufferedReader;
	private DecimalFormat decimalFormatText = new DecimalFormat("###.###");
	private DecimalFormat decimalFormatLanguage = new DecimalFormat("#.#");
	private List<String> ngrams;
	private File csvFile = new File("data.csv");
	private File languageFile = new File("wili-2018-Small-11750-Edited.txt");
	private FileWriter fileWriter;
	private String line, text, language;
	private String[] record;
	private int i, j, ngramSize = 0;
	private final int NUMBER_OF_LANGUAGES = Language.values().length;
	private double[] index = new double[NUMBER_OF_LANGUAGES];
	private double[] vector;

	/**
	 * Constructor 
	 * 
	 * @param ngramSize  - Size of ngram as defined by user
	 * @param vectorSize - Size of vector as defined by user
	 */
	public VectorProcessor(int ngramSize, int vectorSize) {
		// Check if the CSV file already exists and deletes it if it does
		if (csvFile.exists()) {
			csvFile.delete();
		}

		this.ngramSize = ngramSize;
		vector = new double[vectorSize];
	}

	/**
	 * Parse WiLI dataset and pass it off to process()
	 * 
	 * @throws Exception
	 */
	@Override
	public void parse() throws Exception {
		System.out.print("\nReading WiLI language dataset...");

		try {
			bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(languageFile)));

			System.out.println("Done");
			System.out.print("Processing file...");

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
	@Override
	public void process(String line) throws Exception {
		record = line.split("@");

		if (record.length > 2) {
			return;
		}

		// Replace any irrelevant characters
		text = record[0].toUpperCase();
		language = record[1];

		/**
		 * Checks if the language is in the enum. Will return if not the case
		 * 
		 * Prevents an issue specific to Old English when attempting to parse the file
		 */
		if (!Language.inEnum(language, Language.class)) {
			return;
		}

		// Sets vector index i to 0 on each iteration
		for (i = 0; i < vector.length; i++) {
			vector[i] = 0;
		}

		ngrams = ngram(text, ngramSize);

		/**
		 * For each n-gram in the list ngrams, get the hash code of each n-gram and
		 * increment vector by the ngram's hash code modules the vector length
		 */
		for (String ngram : ngrams) {
			vector[ngram.hashCode() % vector.length]++;
		}

		// Normalize the vector values
		vector = Utilities.normalize(vector, 0.0, 1.0);

		index = toVector(Language.valueOf(language));

		// Save normalized vector values to CSV file
		try {
			fileWriter = new FileWriter(csvFile, true);

			for (i = 0; i < vector.length; i++) {
				fileWriter.append(decimalFormatText.format(vector[i]) + ",");
			}

			for (j = 0; j < index.length; j++) {
				fileWriter.append(decimalFormatLanguage.format(index[j]) + ",");
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			fileWriter.append("\n");
			fileWriter.flush();
			fileWriter.close();
		}
	}

	/**
	 * Creates an n-gram from text
	 * 
	 * @param text      - Line of text that will be made into an n-gram
	 * @param ngramSize - N-gram size as defined by the user
	 * 
	 * @return ngrams
	 */
	public static List<String> ngram(String text, int ngramSize) {
		List<String> ngrams = new ArrayList<String>();
		int i;

		for (i = 0; i < text.length() - ngramSize + 1; i++) {
			ngrams.add(text.substring(i, i + ngramSize));
		}

		return ngrams;
	}

	/**
	 * @param language
	 * @return languages
	 * @throws IOException
	 */
	@Override
	public double[] toVector(Language language) throws IOException {
		double[] languages = new double[Language.values().length];

		Language[] langs = Language.values();

		for (i = 0; i < langs.length; i++) {
			if (language == langs[i]) {
				languages[i] = 1.0;
			}
		}

		return languages;
	}
}
