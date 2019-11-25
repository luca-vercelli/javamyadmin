package org.javamyadmin.helpers;

import javax.servlet.http.HttpServletRequest;

public class Response {

	public static boolean isAjax(HttpServletRequest req) {
		String ajax_request = req.getParameter("ajax_request");
		return ajax_request != null && !ajax_request .isEmpty();
	}

}
