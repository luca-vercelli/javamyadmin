package org.javamyadmin.php;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Gettext {
	
	//see https://www.gnu.org/software/gettext/manual/html_node/Java.html
	// May throw a MissingResourceException !
	private static ResourceBundle myResources = ResourceBundle.getBundle(Globals.LOCALES_BUNDLE);

	public static String __(String s) {
		try {
			return myResources.getString(s);
		} catch(MissingResourceException exc) {
			return s;
		}
	}
}
