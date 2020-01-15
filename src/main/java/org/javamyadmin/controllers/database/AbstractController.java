package org.javamyadmin.controllers.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.servlet.ServletException;

public abstract class AbstractController  extends org.javamyadmin.controllers.AbstractController {

	Connection connection;
	
	String db;
	
	
	@Override
	public void prepareResponse() throws ServletException, IOException, SQLException, NamingException {
		
		connection =  null; //TODO
		db = null;
		
		super.prepareResponse();
	}

}
