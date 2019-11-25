package org.javamyadmin.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.javamyadmin.jtwig.JtwigFactory;
import org.jtwig.web.servlet.JtwigRenderer;

@WebServlet(name = "HelloWorld", urlPatterns = { "/HelloWorld" })
public class HelloWorld extends HttpServlet {

	private static final long serialVersionUID = 2766674644118792082L;
	private static JtwigRenderer renderer = JtwigFactory.getRenderer();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		List<String> list = new ArrayList<>();
		list.add("goofy");
		list.add("mickey");
		list.add("daisy");

		renderer.dispatcherFor("/WEB-INF/templates/helloworld.twig") //
				.with("greet", "Jtwig servlet example & demo.") //
				.with("bekind", list) //
				.with("forlist", list) //
				.render(request, response);
	}
}