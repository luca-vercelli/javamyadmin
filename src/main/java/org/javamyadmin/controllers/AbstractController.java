package org.javamyadmin.controllers;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.javamyadmin.helpers.Response;
import org.javamyadmin.jtwig.JtwigFactory;
import org.javamyadmin.php.GLOBALS;
import org.jtwig.web.servlet.JtwigRenderer;

public abstract class AbstractController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected final JtwigRenderer renderer = JtwigFactory.getRenderer();
	protected Response pmaResponse;
	protected GLOBALS GLOBALS;

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.GLOBALS = new GLOBALS();
		this.pmaResponse = new Response(request, response, GLOBALS);
		super.service(request, response);
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

	/**
	 * Render index.j.twig.
	 * 
	 * Subclasses should call this as final step, to perform rendering.
	 * 
	 * @param contentTemplate
	 * @param model
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void render(String contentTemplate, Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		model.put("contentTemplate", contentTemplate);
		model.putAll(pmaResponse.getHeader().getDisplay());
		model.putAll(pmaResponse.getFooter().getDisplay());

		renderer.dispatcherFor("/WEB-INF/templates/index.j.twig") //
				.with(model) //
				.render(request, response);
	}

}
