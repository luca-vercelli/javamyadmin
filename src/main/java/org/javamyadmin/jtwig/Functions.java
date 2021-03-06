package org.javamyadmin.jtwig;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.javamyadmin.helpers.Message;
import org.javamyadmin.helpers.Sanitize;
import org.javamyadmin.helpers.Url;
import org.javamyadmin.helpers.Util;
import org.javamyadmin.springmvc.ApplicationContextProvider;
import org.jtwig.functions.JtwigFunction;
import org.springframework.context.ApplicationContext;

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
		ApplicationContext context = ApplicationContextProvider.getApplicationContext();
		Util util = context.getBean(Util.class);
		Sanitize sanitize = context.getBean(Sanitize.class);
		Url url = context.getBean(Url.class);

		if (functions == null) {
			functions = new ArrayList<>();
			// Message extension ==========================
			functions.add(new JtwigFunction1Ary("notice", x -> Message.notice(x == null? null : x.toString()).getDisplay()));
			functions.add(new JtwigFunction1Ary("error", x -> Message.error(x == null? null : x.toString()).getDisplay()));
			functions.add(new JtwigFunction1Ary("raw_success", x -> Message.rawSuccess(x == null? null : x.toString()).getDisplay()));
			// URL Extension ==========================
			functions.add(new JtwigFunction1Ary("url", x -> url.getFromRoute((String) x)));
			functions.add(new JtwigFunction1Ary("get_hidden_inputs", x -> x /*url.getHiddenInputs()*/)); // TODO needs HttpRequest !
			functions.add(new JtwigFunction2Ary("get_common", (x, y) -> url.getCommon((Map) x, (String) y), null, ""));
			functions.add(new JtwigFunction2Ary("get_common_raw", (x, y) -> url.getCommonRaw((Map) x, (String) y), null, ""));
			functions.add(new JtwigFunctionVarargs("get_hidden_fields",
					getMethod(Url.class, "getHiddenFields",
							new Class[] { Map.class, String.class, String.class }), 
					null, "", "FIXME_pma_token"));
			// Sanitize extension ==========================
			functions.add(new JtwigFunction1Ary("sanitize", x -> x)); // TODO
			functions.add(new JtwigFunction1Ary("escape_js_string", x -> sanitize.escapeJsString((String) x)));
			functions.add(new JtwigFunction1Ary("js_format", x -> sanitize.jsFormat((String) x)));
			functions.add(new JtwigFunction2Ary("get_js_value", (x, y) -> sanitize.getJsValue((String) x, y), "", ""));
			// Translate Extension ==========================
			functions.add(new JtwigFunction1Ary("trans", x -> __((String)x)));
			// Util Extension ==========================
			functions.add(new JtwigFunction1Ary("show_php_docu", x -> "")); // DON'T !
			functions.add(new JtwigFunction1Ary("show_mysql_docu", x -> "")); // DON'T !
			functions.add(new JtwigFunction1Ary("get_mysql_docu_url", x -> "")); // DON'T !
			functions.add(new JtwigFunction1Ary("show_hint", x -> "")); // TODO
			functions.add(
					new JtwigFunction2Ary("get_docu_link", (x, y) -> util.getDocuLink((String) x, (String) y), "", ""));
			functions.add(
					new JtwigFunction2Ary("sortable_table_header", ($title, $sort) -> 
					util.sortableTableHeader((String)$title, (String)$sort), "", ""));
			functions.add(new JtwigFunctionVarargs("link_or_button",
					getMethod(Util.class, "linkOrButton",
							new Class[] { String.class, String.class, Map.class, String.class, String.class }),
					util,
					"", "", null, "", ""));
			functions.add(new JtwigFunctionVarargs("get_image",
					getMethod(Util.class, "getImage", new Class[] { String.class, String.class, Map.class }),
					util,
					"", "", null));
			// where the hell is defined this ?!? ==========================
			functions.add(new JtwigFunction1Ary("link", x -> x)); // TODO
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
