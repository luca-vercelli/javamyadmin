
package org.javamyadmin.php;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
	 * Add to dest all entries coming from src
	 * 
	 * @param dest
	 * @param src
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> array_replace_recursive(Map<String, Object> dest, Map<String, Object> src) {
		for (Entry<String, Object> entry : src.entrySet()) {
			if (!dest.containsKey(entry.getKey())) {
				dest.put(entry.getKey(), entry.getValue());
				continue;
			}
			Object orig = dest.get(entry.getKey());

			// FIXME What about Lists / Collections !?!

			if (orig instanceof Map) {
				if (entry.getValue() instanceof Map) {
					Map<String, Object> m1 = (Map<String, Object>) orig;
					Map<String, Object> m2 = (Map<String, Object>) entry.getValue();
					array_replace_recursive(m1, m2);
				} else {
					dest.put(entry.getKey(), entry.getValue());
				}
			}
		}
		return dest;
	}
}
