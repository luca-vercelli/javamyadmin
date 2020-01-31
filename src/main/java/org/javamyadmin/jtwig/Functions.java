package org.javamyadmin.jtwig;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.javamyadmin.helpers.Message;
import org.javamyadmin.helpers.Sanitize;
import org.javamyadmin.helpers.Url;
import org.javamyadmin.helpers.Util;
import org.jtwig.functions.JtwigFunction;

import static org.javamyadmin.php.Php.*;

/**
 * Long list of PMA-specific twig functions
 * 
 * @see https://github.com/phpmyadmin/phpmyadmin/tree/master/libraries/classes/Twig
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
			functions.add(new JtwigFunction1Ary("notice", x -> Message.notice(x == null? null : x.toString()).getDisplay()));
			functions.add(new JtwigFunction1Ary("error", x -> Message.error(x == null? null : x.toString()).getDisplay()));
			functions.add(new JtwigFunction1Ary("raw_success", x -> Message.rawSuccess(x == null? null : x.toString()).getDisplay()));
			functions.add(new JtwigFunction1Ary("url", x -> x)); // TODO
			functions.add(new JtwigFunction1Ary("link", x -> x)); // TODO
			functions.add(new JtwigFunction1Ary("sanitize", x -> x)); // TODO
			functions.add(new JtwigFunction1Ary("trans", x -> __((String)x)));
			functions.add(new JtwigFunction1Ary("show_php_docu", x -> "")); // DON'T !
			functions.add(new JtwigFunction1Ary("show_mysql_docu", x -> "")); // DON'T !
			functions.add(new JtwigFunction1Ary("get_mysql_docu_url", x -> "")); // DON'T !
			functions.add(new JtwigFunction1Ary("show_hint", x -> "")); // TODO
			functions.add(
					new JtwigFunction2Ary("get_docu_link", (x, y) -> Util.getDocuLink((String) x, (String) y), "", ""));
			functions.add(new JtwigFunction1Ary("escape_js_string", x -> Sanitize.escapeJsString((String) x)));
			functions.add(new JtwigFunction1Ary("get_hidden_inputs", db -> "")); //TODO
			//functions.add(new JtwigFunction1Ary("get_hidden_inputs", db -> Url.getHiddenInputs(db, GLOBALS, $_SESSION)));
			functions.add(
					new JtwigFunction2Ary("sortable_table_header", ($title, $sort) -> 
					Util.sortableTableHeader((String)$title, (String)$sort), "", ""));
			functions.add(new JtwigFunctionVarargs("link_or_button",
					getMethod(Util.class, "linkOrButton",
							new Class[] { String.class, String.class, Map.class, String.class, String.class }),
					"", "", null, "", ""));
			functions.add(new JtwigFunctionVarargs("get_image",
					getMethod(Util.class, "getImage", new Class[] { String.class, String.class, Map.class }), "",
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
