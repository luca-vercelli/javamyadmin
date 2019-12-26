package org.javamyadmin.controllers.database;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;

public abstract class AbstractController  extends org.javamyadmin.controllers.AbstractController {

	Connection connection;
	
	String db;
	
	
	@Override
	public void prepareResponse() throws ServletException, IOException {
		
		connection =  null; //TODO
		db = null;
	}

}
