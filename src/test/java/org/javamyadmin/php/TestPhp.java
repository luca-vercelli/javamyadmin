package org.javamyadmin.php;

import static org.junit.jupiter.api.Assertions.*;

import java.util.function.Function;

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

}
