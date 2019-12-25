package org.javamyadmin.controllers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.javamyadmin.helpers.Core;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @see whitelist.php
 *
 */
@Controller
public class WhitelistJs extends AbstractController {

	@Override
	@RequestMapping(value = "/js/whitelist.php")
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		super.service(request, response);
		
		GLOBALS.set_PMA_MINIMUM_COMMON("true");

		response.setHeader("Content-Type", "text/javascript; charset=UTF-8");
		//TODO header('Expires: ' . gmdate('D, d M Y H:i:s', time() + 3600) . ' GMT');
		
		response.getWriter().write("var GotoWhitelist = [];\n");
		int $i = 0;
		for(String $one_whitelist : Core.$goto_whitelist) {
			response.getWriter().write("GotoWhitelist[" + $i + "] = '" + $one_whitelist + "';\n");
		}
	}
}