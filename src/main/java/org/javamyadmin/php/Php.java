
package org.javamyadmin.php;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;

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
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("Not an UTF-8 string", e);
		}
	}

	/**
	 * true if not null not blank, and not empty
	 * 
	 * @param s
	 * @return
	 */
	public static boolean empty(Object s) {
		return s == null || "".equals(s) || (s instanceof List && ((List<?>) s).isEmpty())
				|| (s instanceof Map && ((Map<?, ?>) s).isEmpty());
	}

	/**
	 * Get singular or plural translation, according to num
	 * 
	 * @param s
	 * @return
	 */
	public static String _ngettext(String sing, String plur, int num) {
		if (num <= 1) {
			return Gettext.__(sing);
		} else {
			return Gettext.__(plur);
		}
	}

	/**
	 * Get translation
	 * 
	 * @param s
	 * @return
	 */
	public static String gettext(String s) {
		return Gettext.__(s);
	}

	/**
	 * Get translation
	 * 
	 * @param s
	 * @return
	 */
	public static String __(String s) {
		return Gettext.__(s);
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

	/**
	 * MD5 String encodig
	 * 
	 * @param s
	 * @return
	 */
	public static String md5(String s) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("MD5 algorithm not available");
		}
		byte[] messageDigest = md.digest(s.getBytes());
		BigInteger number = new BigInteger(1, messageDigest);
		return number.toString(16);
	}

	/**
	 * Return JSON encoded string.
	 * 
	 * @param obj
	 */
	public static String json_encode(Object obj) {

		// Using Google API

		Gson gson = new Gson();
		return gson.toJson(obj);
	}

	/**
	 * Parse JSON string into an object.
	 * 
	 * @param json
	 */
	public static Object json_decode(String json) {

		// Using Google API

		Gson gson = new Gson();
		return gson.fromJson(json, Object.class);
	}

	/**
	 * Add to dest all entries coming from src
	 * 
	 * @param dest
	 * @param src
	 */
	@SuppressWarnings("unchecked")
	public static <T, U> Map<T, U> array_replace_recursive(Map<T, U> dest, Map<T, U> src) {
		for (Entry<T, U> entry : src.entrySet()) {
			if (!dest.containsKey(entry.getKey())) {
				dest.put(entry.getKey(), entry.getValue());
				continue;
			}
			Object orig = dest.get(entry.getKey());

			// FIXME What about Lists / Collections !?!

			if (orig instanceof Map) {
				if (entry.getValue() instanceof Map) {
					Map<T, U> m1 = (Map<T, U>) orig;
					Map<T, U> m2 = (Map<T, U>) entry.getValue();
					array_replace_recursive(m1, m2);
				} else {
					dest.put(entry.getKey(), entry.getValue());
				}
			}
		}
		return dest;
	}

	/**
	 * Return true if string represents a number
	 * 
	 * @param x
	 * @return
	 */
	public static boolean is_numeric(String x) {
		try {
			new Double(x);
			return true;
		} catch (NumberFormatException exc) {
			return false;
		}
	}

	/**
	 * Return true if string represents an array, or a Map
	 * 
	 * @param x
	 * @return
	 */
	public static boolean is_array(Object x) {
		return x instanceof Collection || x instanceof Map;
	}

	private static List<String> scalarTypes = Arrays.asList(new String[] { "integer", "float", "string", "boolean" });

	/**
	 * Return true if string represents a scalar.
	 * 
	 * Scalar variables are those containing an integer, float, string or boolean.
	 * 
	 * @param x
	 * @return
	 */
	public static boolean is_scalar(String x) {

		return x == null || scalarTypes.contains(gettype(x));
	}

	/**
	 * Return PHP equivalent type
	 * 
	 * @param x
	 * @return
	 */
	public static String gettype(Object x) {
		if (x == null) {
			return "NULL";
		} else if (x instanceof Boolean) {
			return "boolean";
		} else if (x instanceof Integer || x instanceof Long) {
			return "integer";
		} else if (x instanceof Float || x instanceof Double) {
			return "double";
		} else if (x instanceof String || x instanceof Character) {
			return "string";
		} else if (x instanceof Collection || x instanceof Map) {
			return "array";
		} else {
			return "object";
		}
	}

	/**
	 * Quote string with slashes in a C style
	 * 
	 * @param str
	 *            The string to be escaped
	 * @param charlist
	 *            A list of characters to be escaped. If charlist contains
	 *            characters \n, \r etc., they are converted in C-like style, while
	 *            other non-alphanumeric characters with ASCII codes lower than 32
	 *            and higher than 126 converted to octal representation.
	 * @return
	 */
	public static String addcslashes(String str, CharSequence charlist) {
		for (int i = 0; i < charlist.length(); ++i) {
			Character ch = charlist.charAt(i);
			if (ch == '\n') {
				str = str.replace("\n", "\\n");
			} else if (ch == '\r') {
				str = str.replace("\r", "\\r");
			} else if (ch == '\t') {
				str = str.replace("\t", "\\t");
			} else if (ch == '\\') {
				str = str.replace("\\", "\\\\");
			} else if (ch < 32 || ch > 126) {
				str = str.replace(ch.toString(), "\\" + Integer.toOctalString(ch));
			} else {
				str = str.replace(ch.toString(), "\\" + ch);
			}
		}
		return str;
	}
}
