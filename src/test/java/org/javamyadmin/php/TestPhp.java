package org.javamyadmin.php;

import static org.junit.jupiter.api.Assertions.*;

import java.util.function.Function;

import org.junit.jupiter.api.Test;
import static org.javamyadmin.php.Php.*;

class TestPhp {

	@Test
	void test_preg_replace_callback() {
		String s = "abc12def34g5";
		Function<String, String> callback = new Function<String, String>() {

			@Override
			public String apply(String t) {
				return "x";
			}

		};

		String repl = preg_replace_callback("[0-9]+", callback, s);
		assertEquals("abcxdefxgx", repl);
	}

}
