package org.javamyadmin.controllers;

import java.io.IOException;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.javamyadmin.helpers.CommonsInc;
import org.javamyadmin.helpers.DatabaseInterface;
import org.javamyadmin.helpers.Response;
import org.javamyadmin.helpers.Template;
import org.javamyadmin.php.Globals;
import org.javamyadmin.php.Php.SessionMap;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractController {

	@Autowired
	protected Globals GLOBALS;
	@Autowired
	protected SessionMap $_SESSION;
	@Autowired
	protected HttpServletRequest httpRequest; // TODO : use $_REQUEST map instead
	@Autowired
	protected HttpServletResponse httpResponse;

	@Autowired
	protected Response response;
	@Autowired
	protected DatabaseInterface dbi;
	@Autowired
	protected Template template;
	@Autowired
	protected CommonsInc commons;

	/**
	 * Prepare global variables.
	 * 
	 * This could be an Interceptor.
	 * 
	 * @param httpRequest
	 * @param httpResponse
	 * @throws ServletException
	 * @throws IOException
	 * @throws NamingException
	 * @throws SQLException
	 */
	public void prepareResponse() throws ServletException, IOException, SQLException, NamingException {

		commons.execute();

		/*
		 * if (empty(GLOBALS.PMA_MINIMUM_COMMON)) { pmaResponse.response(); }
		 */
	}

	public DatabaseInterface getDbi() {
		return dbi;
	}

}
