package org.javamyadmin.helpers;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import org.javamyadmin.jtwig.JtwigFactory;
import org.jtwig.web.servlet.JtwigRenderer;

/**
 * Class Template
 *
 * Handle front end templating
 *
 * @package PhpMyAdmin
 */
public class Template {

	/**
	 * @var String
	 */
	public String BASE_PATH = "templates/";

	private JtwigRenderer renderer = JtwigFactory.getRenderer();
	/**
	 * Template constructor
	 */
	public Template() {
		/** @var Config config */
	}

	/**
	 * Loads a template.
	 *
	 * @param String
	 *            templateName Template path name
	 *
	 * @return Twig_TemplateWrapper
	 * @throws LoaderError
	 * @throws RuntimeError
	 * @throws SyntaxError
	 */
	public Object load(String templateName) {
		throw new IllegalStateException("Not implemented");
	}

	/**
	 * @param String
	 *            template Template path name
	 * @param array
	 *            data Associative array of template variables
	 *
	 * @return String
	 * @throws Throwable
	 * @throws Twig_Error_Loader
	 * @throws Twig_Error_Runtime
	 * @throws Twig_Error_Syntax
	 */
	public String render(String template, Map<String, Object> data) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		renderer.dispatcherFor(template).render(os);
		return os.toString();
	}

}
