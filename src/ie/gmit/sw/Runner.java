package ie.gmit.sw;

import ie.gmit.sw.ui.Menu;

/**
 * @author Kevin Niland
 * @category Main
 * @version 1.0
 * 
 *          Runner - Runs the application. Displays the menu as defined in
 *          Menu.java
 */
public class Runner {
	public static void main(String[] args) throws Exception {
		new Menu().menu();
	}
}