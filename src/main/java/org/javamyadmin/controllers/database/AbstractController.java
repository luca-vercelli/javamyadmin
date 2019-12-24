package org.javamyadmin.controllers.database;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.javamyadmin.helpers.Response;
import org.javamyadmin.php.Globals;
import org.javamyadmin.php.Php.SessionMap;

public abstract class AbstractController  extends org.javamyadmin.controllers.AbstractController {

	/*@Override
	final protected void doGet(HttpServletRequest request, HttpServletResponse response, Response pmaResponse,
			SessionMap $_SESSION, Globals GLOBALS) throws ServletException, IOException {

		Connection connection =  null; //TODO
		
		doGet(request, response, pmaResponse, $_SESSION, GLOBALS, connection);
		
	}

	abstract protected void doGet(HttpServletRequest request, HttpServletResponse response, Response pmaResponse,
			SessionMap $_SESSION, Globals GLOBALS, Connection $db) throws ServletException, IOException;
	*/
}
