package org.javamyadmin.controllers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.javamyadmin.helpers.Core;
import org.javamyadmin.helpers.Response;
import org.javamyadmin.php.Globals;
import org.javamyadmin.php.Php.SessionMap;

/**
 * @see whitelist.php
 *
 */
@WebServlet(urlPatterns = "/js/whitelist.php" , name = "WhitelistJs")
public class WhitelistJs extends AbstractController {

	private static final long serialVersionUID = -5104865747885536865L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response, Response pmaResponse,
			SessionMap $_SESSION, Globals GLOBALS) throws ServletException, IOException {

		GLOBALS.PMA_MINIMUM_COMMON = "true";

		response.setHeader("Content-Type", "text/javascript; charset=UTF-8");
		//TODO header('Expires: ' . gmdate('D, d M Y H:i:s', time() + 3600) . ' GMT');
		
		response.getWriter().write("var GotoWhitelist = [];\n");
		int $i = 0;
		for(String $one_whitelist : Core.$goto_whitelist) {
			response.getWriter().write("GotoWhitelist[" + $i + "] = '" + $one_whitelist + "';\n");
		}
	}
}