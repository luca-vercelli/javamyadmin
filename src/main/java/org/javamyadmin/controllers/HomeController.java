package org.javamyadmin.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.javamyadmin.helpers.Message;
import org.javamyadmin.helpers.Response;
import org.javamyadmin.jtwig.JtwigFactory;
import org.javamyadmin.php.GLOBALS;
import org.jtwig.web.servlet.JtwigRenderer;

import static org.javamyadmin.php.Php.*;

@WebServlet(urlPatterns = "/", name = "HomeController")
public class HomeController extends HttpServlet {

	private static final long serialVersionUID = 2766674644118792082L;
	private final JtwigRenderer renderer = JtwigFactory.getRenderer();

	/**
	 * Poor people's session beans injection
	 * 
	 * @param req
	 */
	private void initSession(HttpServletRequest req) {
	}

	/**
	 * GET handler
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		if (Response.isAjax(request) && !empty(request.getParameter("access_time"))) {
			return;
		}

		initSession(request);

		System.out.println("HERE GLOBALS.server=" + GLOBALS.server);
		if (GLOBALS.server > 0) {
			// TODO include ROOT_PATH . "libraries/server_common.inc.php";
		}

		String displayMessage = "";
		if (!empty(GLOBALS.message)) {
			displayMessage = GLOBALS.message; // Util.getMessage(message);
			GLOBALS.message = null;
		}

		String partialLogout = "";
		if (request.getSession().getAttribute("partial_logout") != null) {
			partialLogout = Message.success(__("You were logged out from one server, to logout completely "
					+ "from phpMyAdmin, you need to logout from all servers.")).getDisplay();
			request.getSession().removeAttribute("partial_logout");
		}

		String syncFavoriteTables = "";
		// TODO syncFavoriteTables = RecentFavoriteTable.getInstance("favorite")
		// .getHtmlSyncFavoriteTables();

		boolean hasServer = false;
		boolean hasServerSelection = false;
		String serverSelection = "";
		String changePassword = "";
		List<String> charsetsList = new ArrayList<>();
		String userPreferences = "";
		String languageSelector = "";
		String themeSelection = "";
		Map<String, Object> webServer = new HashMap<>();
		List<String> databaseServer = new ArrayList<>();

		// hasServer = GLOBALS.server > 0 || ((List)GLOBALS.cfg.get("Servers")).size() >
		// 1;

		// TODO

		Map<String, Object> model = new HashMap<>();
		model.put("message", displayMessage);
		model.put("partial_logout", partialLogout);
		model.put("is_git_revision", false);
		model.put("server", GLOBALS.server);
		model.put("sync_favorite_tables", syncFavoriteTables);
		model.put("has_server", hasServer);
		model.put("is_demo", GLOBALS.cfg.get("DBG.demo"));
		model.put("has_server_selection", hasServerSelection);
		model.put("server_selection", serverSelection != null ? serverSelection : "");
		model.put("change_password", changePassword);
		model.put("charsets", charsetsList);
		model.put("language_selector", languageSelector);
		model.put("theme_selection", themeSelection);
		model.put("user_preferences", userPreferences);
		model.put("database_server", databaseServer);
		model.put("web_server", webServer);
		model.put("php_info", null);
		model.put("is_version_checked", GLOBALS.cfg.get("VersionCheck"));
		model.put("phpmyadmin_version", GLOBALS.PMA_VERSION);
		model.put("config_storage_message", null);

		renderer.dispatcherFor("/WEB-INF/templates/home/index.twig") //
				.with(model) //
				.render(request, response);
	}

	/**
	 * POST handler
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doGet(request, response);
	}
}