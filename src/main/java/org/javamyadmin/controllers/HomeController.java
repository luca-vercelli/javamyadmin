package org.javamyadmin.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.javamyadmin.jtwig.JtwigFactory;
import org.jtwig.web.servlet.JtwigRenderer;

@WebServlet(urlPatterns = "/", name = "HomeController")
public class HomeController extends HttpServlet {

	private static final long serialVersionUID = 2766674644118792082L;
	private final JtwigRenderer renderer = JtwigFactory.getRenderer();

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Map<String, Object> model = new HashMap<>();
		
		// TODO
		
		renderer.dispatcherFor("/WEB-INF/templates/home/index.twig") //
				.with(model) //
				.render(request, response);
	}
}