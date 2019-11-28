package org.javamyadmin.controllers;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.javamyadmin.jtwig.JtwigFactory;
import org.jtwig.web.servlet.JtwigRenderer;

public abstract class AbstractController extends HttpServlet {

	private static final long serialVersionUID = 1L;
	protected final JtwigRenderer renderer = JtwigFactory.getRenderer();

	// Response
	// Header
	// Footer

	public void render(String contentTemplate, Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		model.put("contentTemplate", contentTemplate);
		// TODO: model.put header e footer

		renderer.dispatcherFor("/WEB-INF/templates/index.j.twig") //
				.with(model) //
				.render(request, response);
	}

	/**
	 * GET handler. Must be defined.
	 */
	@Override
	protected abstract void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException;

	/**
	 * POST handler. Equal to GET.
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doGet(request, response);
	}
}
