package org.javamyadmin.controllers;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloWorldSpring {

	@RequestMapping(value = "/HelloWorldSpring", produces = MediaType.TEXT_PLAIN_VALUE)
	protected @ResponseBody String doSmthg(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		StringBuilder html = new StringBuilder("Hello world! This object is ").append(this.hashCode())
				.append(", request is ").append(request).append(", response is ").append(response).append("\r\n");

		html.append("\r\nRequest parameters:\r\n");
		Enumeration<String> enm = request.getParameterNames();
		while (enm.hasMoreElements()) {
			String key = enm.nextElement();
			Object value = request.getParameter(key);
			html.append(key).append("=").append(value).append("\r\n");
		}

		html.append("\r\nSession attributes:\r\n");
		HttpSession session = request.getSession();
		enm = session.getAttributeNames();
		while (enm.hasMoreElements()) {
			String key = enm.nextElement();
			Object value = session.getAttribute(key);
			html.append(key).append("=").append(value).append("\r\n");
		}

		html.append("\r\nRequest attributes:\r\n");
		enm = request.getAttributeNames();
		while (enm.hasMoreElements()) {
			String key = enm.nextElement();
			Object value = request.getAttribute(key);
			html.append(key).append("=").append(value).append("\r\n");
		}

		return html.toString();
	}
}