package org.javamyadmin.controllers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.javamyadmin.jtwig.JtwigFactory;
import org.jtwig.web.servlet.JtwigRenderer;

public abstract class AbstractController extends HttpServlet {

	private static final long serialVersionUID = 1L;
	protected final JtwigRenderer renderer = JtwigFactory.getRenderer();

	/**
	 * GET handler
	 */
	@Override
	protected abstract void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException;

	/**
	 * POST handler
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doGet(request, response);
	}
}
