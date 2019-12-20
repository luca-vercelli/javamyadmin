
package org.javamyadmin.php;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.text.StringEscapeUtils;

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
	public final static String E_USER_NOTICE = "USER_NOTICE";
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
	 * URL-encodes string. This function is convenient when encoding a string to be
	 * used in a query part of a URL, as a convenient way to pass variables to the
	 * next page
	 * 
	 * @param s
	 * @return
	 */
	public static String urlencode(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("Not an UTF-8 string", e);
		}
	}

	/**
	 * First character to uppercase
	 * 
	 * @return
	 */
	public static String ucfirst(String str) {
		if (str == null || str.length() == 0) {
			return str;
		}
		return Character.toUpperCase(str.charAt(0)) + str.substring(1);
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
	 * What is this?!?
	 * 
	 * @param s
	 * @return
	 */
	public static String _pgettext(String s, String context) {
		return Gettext.__(s); // FIXME
	}

	/**
	 * Convert special characters to HTML entities.<br/>
	 * 
	 * <code>htmlentities</code> will encode ANY character that has an HTML entity
	 * equivalent. <code>htmlspecialchars</code> ONLY encodes a small set of the
	 * most problematic characters. It�s generally recommended to use
	 * htmlspecialchars because htmlentities can cause display problems with your
	 * text depending on what characters are being output.
	 * 
	 * @see https://johnmorrisonline.com/prevent-xss-attacks-escape-strings-in-php/
	 * 
	 * @param s
	 * @return
	 */
	public static String htmlspecialchars(String s) {

		return StringEscapeUtils.escapeHtml4(s);
	}

	/**
	 * Convert special characters to HTML entities.<br/>
	 * 
	 * <code>htmlentities</code> will encode ANY character that has an HTML entity
	 * equivalent. <code>htmlspecialchars</code> ONLY encodes a small set of the
	 * most problematic characters. It�s generally recommended to use
	 * htmlspecialchars because htmlentities can cause display problems with your
	 * text depending on what characters are being output.
	 * 
	 * @see https://johnmorrisonline.com/prevent-xss-attacks-escape-strings-in-php/
	 * @see https://www.php.net/manual/en/function.htmlentities.php
	 * 
	 * @param s
	 * @return
	 */
	public static String htmlentities(String s) {
		StringBuilder builder = new StringBuilder();
		for (char c : s.toCharArray()) {
			switch (c) {
			case '<':
				builder.append("&lt;");
				break;
			case '>':
				builder.append("&gt;");
				break;
			case '&':
				builder.append("&amp;");
				break;
			case '"':
				builder.append("&quot;");
				break;
			default:
				builder.append(c);
			}
		}
		return builder.toString();
	}

	/**
	 * Generates a URL-encoded query string from the associative (or indexed) array
	 * provided
	 */
	public static String http_build_query(Map<String, String> params) {
		return http_build_query(params, "&");
	}

	/**
	 * Generates a URL-encoded query string from the associative (or indexed) array
	 * provided
	 */
	public static String http_build_query(Map<String, String> params, String separator) {

		// TODO use Apache commons or sim.

		StringBuilder s = new StringBuilder();
		boolean firstGone = false;
		for (Entry<String, String> entry : params.entrySet()) {
			if (firstGone) {
				s.append(separator);
				firstGone = true;
			}
			s.append(entry.getKey()).append("=").append(urlencode(entry.getValue()));
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map array_replace_recursive(Map dest, Map src) {
		Set<Entry> entries = src.entrySet();
		for (Entry entry : entries) {
			if (!dest.containsKey(entry.getKey())) {
				dest.put(entry.getKey(), entry.getValue());
				continue;
			}
			Object orig = dest.get(entry.getKey());

			// FIXME What about Lists / Collections !?!

			if (orig instanceof Map) {
				if (entry.getValue() instanceof Map) {
					Map m1 = (Map) orig;
					Map m2 = (Map) entry.getValue();
					array_replace_recursive(m1, m2);
				} else {
					dest.put(entry.getKey(), entry.getValue());
				}
			}
		}
		return dest;
	}

	/**
	 * Merge one or more arrays
	 * 
	 * @param map
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map array_merge(Map... maps) {
		Map result = new HashMap();
		for (Map map : maps) {
			result.putAll(map);
		}
		return result;

	}

	/**
	 * Return true if Object represents a number
	 * 
	 * @param x
	 * @return
	 * @see gettype
	 */
	public static boolean is_numeric(Object x) {
		if (x == null || x instanceof Integer || x instanceof Long || x instanceof Float || x instanceof Double)
			return true;
		try {
			new Double(x.toString());
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
	public static boolean is_scalar(Object x) {

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
	 * @param str The string to be escaped
	 * @param charlist A list of characters to be escaped. If charlist contains
	 * characters \n, \r etc., they are converted in C-like style, while other
	 * non-alphanumeric characters with ASCII codes lower than 32 and higher than
	 * 126 converted to octal representation.
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

	/**
	 * Return array entries that match the pattern
	 * 
	 * @param pattern
	 * @param array
	 * @return
	 */
	public static List<String> preg_grep(String pattern, List<String> array) {
		Pattern p = Pattern.compile(pattern);
		List<String> result = new ArrayList<>();
		for (String s : array) {
			if (p.matcher(s).matches()) {
				result.add(s);
			}
		}
		return result;
	}

	/**
	 * Return array entries that match the pattern
	 * 
	 * @param pattern
	 * @param array
	 * @return
	 */
	public static <U> Map<U, String> preg_grep(String pattern, Map<U, String> array) {
		Pattern p = Pattern.compile(pattern);
		Map<U, String> result = new HashMap<>();
		for (Entry<U, String> entry : array.entrySet()) {
			if (p.matcher(entry.getValue()).matches()) {
				result.put(entry.getKey(), entry.getValue());
			}
		}
		return result;
	}

	/**
	 * Perform a regular expression search and replace using a callback
	 */
	public static String preg_replace_callback(String $pattern, Function<String[], String> $callback, String $subject) {
		Pattern pc = Pattern.compile($pattern);
		Matcher matcher = pc.matcher($subject);

		StringBuilder sb = new StringBuilder();
		int lastMatchPos = 0;
		while (matcher.find()) {
			sb.append($subject.substring(lastMatchPos, matcher.start()));
			String[] matches = new String[matcher.groupCount()];
			for (int i = 0; i < matches.length; ++i) {
				matches[i] = matcher.group(i+1); // group(0) is full match
			}
			lastMatchPos = matcher.end();
			sb.append($callback.apply(matches));
		}
		sb.append($subject.substring(lastMatchPos));
		return sb.toString();
	}

	/**
	 * This is NOT a PHP function, this correspond to PHP construct:
	 * 
	 * array[k1][k2][k3]
	 */
	@SuppressWarnings({ "rawtypes" })
	public static Object multiget(Map map, Object... keys) {

		if (keys.length < 1) {
			throw new IllegalArgumentException("At least one key required");
		}

		// Hashmaps part
		for (int i = 0; i < keys.length - 1; ++i) {
			Object key = keys[i];
			if (map.containsKey(key)) {
				map = (Map) map.get(key);
			} else {
				return null;
			}
		}

		// value part
		return map.get(keys[keys.length - 1]);
	}

	/**
	 * This is NOT a PHP function, this correspond to PHP construct:
	 * 
	 * unset(array[k1][k2][k3])
	 */
	@SuppressWarnings({ "rawtypes" })
	public static void multiremove(Map map, Object... keys) {

		if (keys.length < 1) {
			throw new IllegalArgumentException("At least one key required");
		}

		// Hashmaps part
		for (int i = 0; i < keys.length - 1; ++i) {
			Object key = keys[i];
			if (map.containsKey(key)) {
				map = (Map) map.get(key);
			} else {
				return;
			}
		}

		// value part
		map.remove(keys[keys.length - 1]);
	}

	/**
	 * This is NOT a PHP function, this correspond to PHP construct:
	 * 
	 * array[k1][k2][k3] = val
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void multiput(Map map, Object... keysAndValue) {

		if (keysAndValue.length < 2) {
			throw new IllegalArgumentException("At least one key and one value required");
		}

		// Hashmaps part
		for (int i = 0; i < keysAndValue.length - 2; ++i) {
			Object key = keysAndValue[i];
			if (map.containsKey(key)) {
				map = (Map) map.get(key);
			} else {
				Map newmap = new HashMap();
				map.put(key, newmap);
				map = newmap;
			}
		}

		// value part
		map.put(keysAndValue[keysAndValue.length - 2], keysAndValue[keysAndValue.length - 1]);
	}

	/**
	 * Read-only Map of POST/GET parameters.
	 * 
	 * This method filter 1 result per parameter. Use request.getParameterMap() to
	 * have all results.
	 */
	public static Map<String, String> $_REQUEST(HttpServletRequest request) {
		Map<String, String> map = new HashMap<>();
		Enumeration<String> names = request.getParameterNames();
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			map.put(name, request.getParameter(name));
		}
		return map;
	}

	/**
	 * Map of session attributes.
	 * 
	 * The map is connected with the underlying HttpSession object.
	 * 
	 * @see $_SESSION
	 * @author lucav
	 *
	 */
	public static class SessionMap extends HashMap<String, Object> {

		private static final long serialVersionUID = -5777091141669867338L;
		private HttpSession session;

		public SessionMap(HttpSession session) {
			this.session = session;
			Enumeration<String> names = session.getAttributeNames();
			while (names.hasMoreElements()) {
				String name = names.nextElement();
				put(name, session.getAttribute(name));
			}
		}

		@Override
		public Object put(String key, Object value) {
			session.setAttribute(key, value);
			return super.put(key, value);
		}

		@Override
		public Object remove(Object key) {
			session.removeAttribute((String) key);
			return super.remove(key);
		}
	}

	/**
	 * Read-only Map of session attributes.
	 */
	public static SessionMap $_SESSION(HttpSession session) {
		return new SessionMap(session);
	}

	/**
	 * Create an array containing a range of elements
	 * 
	 * @param start
	 * @param end
	 * @param step
	 * @return
	 */
	public static List<Integer> range(int start, int end, int step) {
		if (step < 0)
			throw new IllegalArgumentException("Given negative step");
		List<Integer> array = new ArrayList<>(((end - start) / step) + 1);
		int i = 0;
		int value = start;
		while (value <= end) {
			array.set(i, value);
			value += step;
		}
		return array;
	}

	public static List<Integer> range(int start, int end) {
		return range(start, end, 1);
	}
}
