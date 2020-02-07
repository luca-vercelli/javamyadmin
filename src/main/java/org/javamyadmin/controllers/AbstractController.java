package org.javamyadmin.controllers;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.javamyadmin.helpers.CommonsInc;
import org.javamyadmin.helpers.DatabaseInterface;
import org.javamyadmin.helpers.Response;
import org.javamyadmin.helpers.Template;
import org.javamyadmin.php.Globals;
import org.javamyadmin.php.Php.SessionMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Inject common stuff in all Controllers.
 * 
 * Most subclasses will need to call commons.execute() and response.response.
 * 
 * @author lucav
 *
 */
public abstract class AbstractController {

	@Autowired
	protected Globals GLOBALS;
	@Autowired
	protected HttpServletRequest httpRequest;
	@Autowired
	protected HttpServletResponse httpResponse;
	@Autowired
	@Qualifier("$_REQUEST")
	protected Map<String, String> $_REQUEST;
	@Autowired
	protected SessionMap $_SESSION;

	@Autowired
	protected Response response;
	@Autowired
	protected DatabaseInterface dbi;
	@Autowired
	protected Template template;
	@Autowired
	protected CommonsInc commons;

	public DatabaseInterface getDbi() {
		return dbi;
	}

}
