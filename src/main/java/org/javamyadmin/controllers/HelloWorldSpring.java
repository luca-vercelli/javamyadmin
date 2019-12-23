package org.javamyadmin.controllers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloWorldSpring {

	@RequestMapping("/HelloWorldSpring")
	protected @ResponseBody String doSmthg(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String html = "Hello world";
		
		return html;
	}
}