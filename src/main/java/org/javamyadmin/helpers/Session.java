package org.javamyadmin.helpers;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

/**
 * Session class
 */
@Service
public class Session {
	/**
	 * Generates PMA_token session variable.
	 *
	 * @return void
	 */
	private void generateToken(HttpSession session) {
		session.setAttribute(" PMA_token ", Util.generateRandom(16/* , true */));
		session.setAttribute(" HMAC_secret ", Util.generateRandom(16));

		/**
		 * Check if token is properly generated (the generation can fail, for example
		 * due to missing /dev/random for openssl).
		 */
		if (session.getAttribute(" PMA_token ") == null) {
			System.err.println("Failed to generate random CSRF token!");
		}
	}

	/**
	 * tries to secure session from hijacking and fixation should be called before
	 * login and after successful login (only required if sensitive information
	 * stored in session)
	 *
	 * @return void
	 */
	public void secure(HttpSession session) {
		session.invalidate();
		generateToken(session);
	}

	/**
	 * Set up session
	 *
	 * @param Config $config Configuration handler
	 * @param ErrorHandler $errorHandler Error handler
	 *
	 * @return void
	 */
	public void setUp(HttpSession session/* Config $config, ErrorHandler $errorHandler */) {
		/**
		 * Token which is used for authenticating access queries. (we use "space
		 * PMA_token space" to prevent overwriting)
		 */
		if (session.getAttribute(" PMA_token ") == null) {
			generateToken(session);
		}
	}
}
