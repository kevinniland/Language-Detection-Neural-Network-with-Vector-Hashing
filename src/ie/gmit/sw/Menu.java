package ie.gmit.sw;

import java.util.Scanner;

/**
 * @author Kevin Niland
 *
 */
public class Menu {
	private Scanner scanner = new Scanner(System.in);
	private boolean keepAlive = true;
	private int menuChoice, ngramSize, vectorSize;

	public void menu() {
		while (keepAlive) {
			System.out.println("Language Detection Neural Network with Vector Hashing");
			System.out.println("=====================================================");
			System.out.println("Enter 1 to specify n-gram size (default vector size will be set),");
			System.out.println("Enter 2 to specify vector size (default n-gram size will be set),");
			System.out.println("Enter 3 to quit the program:");
			menuChoice = scanner.nextInt();

			switch (menuChoice) {
			case 1:
				System.out.println("Enter n-gram size: ");
				ngramSize = scanner.nextInt();

				break;

			case 2:
				System.out.println("Enter vector size: ");
				vectorSize = scanner.nextInt();

				break;

			case 3:
				keepAlive = false;
				break;
			default:
				System.out.println("ERROR: Invalid input");
			}
		}
	}
}
