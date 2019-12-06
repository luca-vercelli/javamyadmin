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
			functions.add(new JtwigFunction2Ary("get_docu_link", (x, y) -> Util.getDocuLink((String) x, (String) y), "", ""));
			functions.add(new JtwigFunction1Ary("escape_js_string", x -> Sanitize.escapeJsString((String) x)));
			functions.add(
					new JtwigFunctionVarargs("link_or_button", Util.class, "linkOrButton", "", "", null, ""));
			functions.add(new AbstractJtwigFunction("get_image") {

				@Override
				public Object execute(FunctionRequest args) {
					if (args.getNumberOfArguments() < 1 || args.getNumberOfArguments() > 3) {
						throw new IllegalArgumentException(
								String.format("'%s' expects at least 1 and at most 3 argument(s)", name));
					}
					String image = (String) args.get(0);
					String alternate = args.getNumberOfArguments() >= 2 ? (String) args.get(1) : null;
					@SuppressWarnings("unchecked")
					Map<String, String> attributes = args.getNumberOfArguments() >= 3
							? (Map<String, String>) args.get(2)
							: null;
					return Generator.getImage(image, alternate, (Map) attributes);
				}
			});
		}
		return functions;
	}
}
