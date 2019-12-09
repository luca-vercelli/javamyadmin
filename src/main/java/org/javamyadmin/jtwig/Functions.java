package org.javamyadmin.jtwig;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.javamyadmin.helpers.Sanitize;
import org.javamyadmin.helpers.Util;
import org.javamyadmin.helpers.html.Generator;
import org.jtwig.functions.FunctionRequest;
import org.jtwig.functions.JtwigFunction;

/**
 * Long list of PMA-specific twig functions
 * 
 * @see https://github.com/phpmyadmin/phpmyadmin/blob/master/libraries/classes/Twig/UtilExtension.php
 *
 */
public class Functions {

	private static List<JtwigFunction> functions = null;

	/**
	 * Get list of Twig custom functions
	 * 
	 * @return
	 */
	public static List<JtwigFunction> getFunctions() {
		if (functions == null) {
			functions = new ArrayList<>();
			functions.add(new JtwigFunction1Ary("url", x -> x)); // TODO
			functions.add(new JtwigFunction1Ary("link", x -> x)); // TODO
			functions.add(new JtwigFunction1Ary("show_php_docu", x -> x)); // TODO (or not)
			functions.add(
					new JtwigFunction2Ary("get_docu_link", (x, y) -> Util.getDocuLink((String) x, (String) y), "", ""));
			functions.add(new JtwigFunction1Ary("escape_js_string", x -> Sanitize.escapeJsString((String) x)));
			functions.add(new JtwigFunctionVarargs("link_or_button",
					getMethod(Util.class, "linkOrButton",
							new Class[] { String.class, String.class, Map.class, String.class, String.class }),
					"", "", null, "", ""));
			functions.add(new JtwigFunctionVarargs("get_image",
					getMethod(Generator.class, "getImage", new Class[] { String.class, String.class, Map.class }), "",
					"", null));
		}
		return functions;
	}

	private static Method getMethod(Class clazz, String staticMethodName, Class... paramTypes) {
		try {
			return clazz.getMethod(staticMethodName, paramTypes);
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException(e);
		} catch (SecurityException e) {
			throw new IllegalArgumentException(e);
		}
	}
}
