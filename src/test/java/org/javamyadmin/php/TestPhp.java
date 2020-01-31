package org.javamyadmin.php;

import static org.junit.jupiter.api.Assertions.*;

import java.net.MalformedURLException;
import java.util.function.Function;

import org.javamyadmin.php.Php.UrlComponents;
import org.junit.jupiter.api.Test;
import static org.javamyadmin.php.Php.*;

class TestPhp {

	@Test
	void test_preg_replace_callback() {
		String s = "April fools day is 04/01/2002. Last christmas was 12/24/2001.";
		String yearPattern = "(\\d{2}/\\d{2}/)(\\d{4})";
		Function<String[], String> nextYear = new Function<String[], String>() {

			@Override
			public String apply(String[] t) {
				return t[0] + (new Integer(t[1]) + 1);
			}

		};

		String repl = preg_replace_callback(yearPattern, nextYear, s);
		assertEquals("April fools day is 04/01/2003. Last christmas was 12/24/2002.", repl);
	}

	@Test
	void test_parse_url() throws MalformedURLException {
		String url;
		UrlComponents components;

		// With Fragment
		url = "http://server.com:8080/some/path.jsp#chapter1";
		components = parse_url(url);
		assertEquals("http", components.scheme);
		assertEquals("server.com", components.host);
		assertEquals(new Integer(8080), components.port);
		assertEquals("/some/path.jsp", components.path);
		assertEquals("chapter1", components.fragment);

		// with parameters (both fragment and params not allowed)
		url = "http://server.com:8080/some/path.jsp?a=1&b=2";
		components = parse_url(url);
		assertEquals("http", components.scheme);
		assertEquals("server.com", components.host);
		assertEquals(new Integer(8080), components.port);
		assertEquals("/some/path.jsp", components.path);
		assertEquals("a=1&b=2", components.query);

		// no server
		url = "/some/path.jsp?a=1&b=2";
		components = parse_url(url);
		assertNull(components.scheme);
		assertNull(components.host);
		assertNull(components.port);
		assertEquals("/some/path.jsp", components.path);
		assertEquals("a=1&b=2", components.query);
	}
}
