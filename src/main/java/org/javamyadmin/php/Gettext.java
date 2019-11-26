package org.javamyadmin.php;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Gettext {
	
	//see https://www.gnu.org/software/gettext/manual/html_node/Java.html
	private static ResourceBundle myResources = ResourceBundle.getBundle("translations");

	public static String __(String s) {
		try {
			
			// FIXME
			
			return myResources.getString(s);
		} catch(MissingResourceException exc) {
			return s;
		}
	}
}
