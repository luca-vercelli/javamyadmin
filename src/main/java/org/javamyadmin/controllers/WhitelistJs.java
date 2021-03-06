package org.javamyadmin.controllers;

import java.io.IOException;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.servlet.ServletException;

import org.javamyadmin.helpers.Core;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @see whitelist.php
 *
 */
@RestController
public class WhitelistJs extends AbstractController {


	@Autowired
	private Core core;
	
	@RequestMapping(value = "/js/whitelist.php")
	public void whitelist() throws ServletException, IOException, SQLException, NamingException {

		commons.execute();
		
		GLOBALS.set_PMA_MINIMUM_COMMON(true);

		httpResponse.setHeader("Content-Type", "text/javascript; charset=UTF-8");
		//TODO header('Expires: ' . gmdate('D, d M Y H:i:s', time() + 3600) . ' GMT');
		
		httpResponse.getWriter().write("var GotoWhitelist = [];\n");
		int $i = 0;
		for(String $one_whitelist : core.$goto_whitelist) {
			httpResponse.getWriter().write("GotoWhitelist[" + $i + "] = '" + $one_whitelist + "';\n");
		}
	}
}