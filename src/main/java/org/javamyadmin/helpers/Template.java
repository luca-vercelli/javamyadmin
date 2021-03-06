package org.javamyadmin.helpers;

import java.util.HashMap;
import java.util.Map;

import org.javamyadmin.jtwig.JtwigFactory;
import org.springframework.stereotype.Service;

/**
 * Class Template
 *
 * Handle front end templating
 *
 * @package PhpMyAdmin
 */
@Service
public class Template {

	/**
	 * @param string $template Template path name
	 * @param array $data Associative array of template variables
	 *
	 * @return string
	 * @throws Throwable
	 * @throws Twig_Error_Loader
	 * @throws Twig_Error_Runtime
	 * @throws Twig_Error_Syntax
	 */
	public String render(String templateName, Map<String, Object> model) {
		return JtwigFactory.render(templateName, model);
	}

	public String render(String templateName) {
		return this.render(templateName, new HashMap<>());
	}
}
