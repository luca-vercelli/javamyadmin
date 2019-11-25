
package org.javamyadmin.php;

import java.util.Map;

/**
 * Mimic some PHP stuff
 * 
 * @author lucav
 *
 */
public class Php {

	public final static String E_USER_ERROR = "USER ERROR";
	public final static String E_USER_WARNING = "USER WARNING";
	public final static String E_FATAL = "FATAL";

	/**
	 * Currently, just print on sysout
	 * 
	 * @param message
	 * @param level
	 */
	public static void trigger_error(String message, String level) {
		// TODO
		System.out.println("" + level + " " + message);

	}

	/**
	 * Convert special characters to HTML entities
	 * 
	 * @param s
	 * @return
	 */
	public static String htmlspecialchars(String s) {
		return s; // TODO
	}

	/**
	 * true if not null and not blank
	 * 
	 * @param s
	 * @return
	 */
	public static boolean empty(String s) {
		return s == null || s.isEmpty();
	}

	/**
	 * Get singular or plural translation, according to num
	 * 
	 * @param s
	 * @return
	 */
	public static String _ngettext(String sing, String plur, int num) {
		if (num <= 1) {
			return gettext(sing);
		} else {
			return gettext(plur);
		}
	}

	/**
	 * Get translation
	 * 
	 * @param s
	 * @return
	 */
	public static String gettext(String s) {
		return s; // TODO
	}

	/**
	 * Get translation
	 * 
	 * @param s
	 * @return
	 */
	public static String __(String msg) {
		return gettext(msg);
	}

	/**
	 * Generates a URL-encoded query string from the associative (or indexed) array
	 * provided
	 */
	public static String http_build_query(Map<String, Object> params) {
		return http_build_query(params, "&");
	}

	/**
	 * Generates a URL-encoded query string from the associative (or indexed) array
	 * provided
	 */
	public static String http_build_query(Map<String, Object> params, String separator) {

		// TODO use Apache commons or sim.

		StringBuilder s = new StringBuilder();
		boolean firstGone = false;
		for (String key : params.keySet()) {
			if (firstGone) {
				s.append(separator);
				firstGone = true;
			}
			s.append(key).append("=").append(params.get(key).toString());
		}
		return s.toString();
	}
}
