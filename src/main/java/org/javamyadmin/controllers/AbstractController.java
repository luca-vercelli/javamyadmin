package org.javamyadmin.controllers;

import static org.javamyadmin.php.Php.*;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.javamyadmin.helpers.Core;
import org.javamyadmin.helpers.Language;
import org.javamyadmin.helpers.LanguageManager;
import org.javamyadmin.helpers.Message;
import org.javamyadmin.helpers.Response;
import org.javamyadmin.helpers.Sanitize;
import org.javamyadmin.helpers.Scripts;
import org.javamyadmin.helpers.ThemeManager;
import org.javamyadmin.helpers.Util;
import org.javamyadmin.jtwig.JtwigFactory;
import org.javamyadmin.php.Globals;
import org.javamyadmin.php.Php.SessionMap;
import org.jtwig.web.servlet.JtwigRenderer;

public abstract class AbstractController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected final JtwigRenderer renderer = JtwigFactory.getRenderer();

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Globals GLOBALS = new Globals(getServletContext());
		SessionMap $_SESSION = $_SESSION(request.getSession());
		Response pmaResponse = new Response(request, response, GLOBALS, $_SESSION);
		
		// cfr. commons.inc.php
		
		// ContainerBuilder.
		// $containerBuilder = new ContainerBuilder();
		// GLOBALS.error_handler = $containerBuilder.get("error_handler");
		// GLOBALS.PMA_Config = $containerBuilder.get("config");
		if (! GLOBALS.PMA_NO_SESSION == true) {
		    // TODO Session.setUp(GLOBALS.PMA_Config, GLOBALS.error_handler);
		}
		
		boolean $token_provided = false, $token_mismatch = false;
		if (request.getMethod().equals("POST")) {
		    if (Core.isValid(request.getParameter("token"))) {
		        $token_provided = true;
		        $token_mismatch = ! request.getParameter("token").equals(request.getSession().getAttribute("PMA_token"));
		    }
		    if ($token_mismatch) {
		        /* Warn in case the mismatch is result of failed setting of session cookie */
		        if (request.getParameter("set_session") != null &&  !request.getParameter("set_session").equals(request.getSession().getId())) {
		            trigger_error(
		                __(
		                    "Failed to set session cookie. Maybe you are using "
		                    + "HTTP instead of HTTPS to access phpMyAdmin."
		                ),
		                E_USER_ERROR
		            );
		        }
		        /*
		         * We don"t allow any POST operation parameters if the token is mismatched
		         * or is not provided
		         */
		        String[] $whitelist = new String[] {"ajax_request"};
		        Sanitize.removeRequestVars($whitelist);
		    }
		}
		
		Core.setGlobalDbOrTable("db");
		Core.setGlobalDbOrTable("table");
		
		/*
		 * Store currently selected recent table.
		 * Affect GLOBALS.db"] and GLOBALS.table"]
		 *
		 * TODO, maybe
		if (!empty(request.getParameter("selected_recent_table")) && Core.isValid(request.getParameter("selected_recent_table"))) {
		    String $recent_table = json_decode(request.getParameter("selected_recent_table"));
		    $diMigration.setGlobal(
		        "db",
		        (array_key_exists("db", $recent_table) && is_string($recent_table["db"])) ? $recent_table["db"] : ""
		    );
		    $diMigration.setGlobal(
		        "url_params",
		        ["db" => $containerBuilder.getParameter("db")] + $containerBuilder.getParameter("url_params")
		    );
		    $diMigration.setGlobal(
		        "table",
		        (array_key_exists("table", $recent_table) && is_string($recent_table["table"])) ? $recent_table["table"] : ""
		    );
		    $diMigration.setGlobal(
		        "url_params",
		        ["table" => $containerBuilder.getParameter("table")] + $containerBuilder.getParameter("url_params")
		    );
		}
		
		/*
		 * SQL query to be executed
		 * @global String GLOBALS.sql_query"]
		 *
		$diMigration.setGlobal("sql_query", "");
		if (Core.isValid($_POST["sql_query"])) {
		    $diMigration.setGlobal("sql_query", $_POST["sql_query"]);
		}
		
		/*
		 * lang detection is done here
		 */
		Language $language = LanguageManager.getInstance().selectLanguage(request, response);
		$language.activate(GLOBALS);
		
		/*
		 * check for errors occurred while loading configuration
		 * this check is done here after loading language files to present errors in locale
		 */
		GLOBALS.PMA_Config.checkPermissions();
		GLOBALS.PMA_Config.checkErrors(request, response, GLOBALS, pmaResponse);
		
		/* setup themes                                          LABEL_theme_setup    */

		ThemeManager.initializeTheme(request, GLOBALS);
		
		if (empty(GLOBALS.PMA_MINIMUM_COMMON)) {
		    /**
		     * save some settings in cookies
		     * @todo should be done in PhpMyAdmin\Config
		     */
		    GLOBALS.PMA_Config.setCookie("pma_lang", GLOBALS.lang, request, response);
		    GLOBALS.themeManager.setThemeCookie(request, response);
		    if (! empty(GLOBALS.PMA_Config.get("Server"))) {
		        /**
		         * Loads the proper database interface for this server
		         */
		        // $containerBuilder.set(DatabaseInterface.class, DatabaseInterface.load());
		        // $containerBuilder.setAlias("dbi", DatabaseInterface.class);
		    	
		        // get LoginCookieValidity from preferences cache
		        // no generic solution for loading preferences from cache as some settings
		        // need to be kept for processing in
		        // PhpMyAdmin\Config.loadUserPreferences()
		        
		    	String $cache_key = "server_" + GLOBALS.server;
		        if (!empty(request.getSession().getAttribute("cache." + $cache_key + ".userprefs.LoginCookieValidity"))
		        ) {
		            String $value
		                = (String) request.getSession().getAttribute("cache." + $cache_key + ".userprefs.LoginCookieValidity");
		            GLOBALS.PMA_Config.set("LoginCookieValidity", $value);
		        }
		        // Gets the authentication library that fits the GLOBALS.cfg["Server"] settings
		        // and run authentication
		        /**
		         * the required auth type plugin
		         */
		        /* TODO
		        $auth_class = "PhpMyAdmin\\Plugins\\Auth\\Authentication" . ucfirst(strtolower(GLOBALS.cfg["Server"]["auth_type"]));
		        if (! @class_exists($auth_class)) {
		            Core.fatalError(
		                __("Invalid authentication method set in configuration:")
		                + " " . GLOBALS.cfg.get("Server.auth_type")
		            );
		        }
		        if (isset($_POST["pma_password"]) && strlen($_POST["pma_password"]) > 256) {
		            $_POST["pma_password"] = substr($_POST["pma_password"], 0, 256);
		        }
		        $auth_plugin = new $auth_class();
		        $auth_plugin.authenticate();*/
		        
		        // Try to connect MySQL with the control user profile (will be used to
		        // get the privileges list for the current user but the true user link
		        // must be open after this one so it would be default one for all the
		        // scripts)
		        /* TODO
		        boolean $controllink = false;
		        if (GLOBALS.cfg.get("Server"]["controluser"] != "") {
		            $controllink = GLOBALS.dbi.connect(
		                DatabaseInterface.CONNECT_CONTROL
		            );
		        }*/
		        // Connects to the server (validates user"s login)
		        /** @var DatabaseInterface $userlink */
		        /* TODO
		        $userlink = GLOBALS.dbi.connect(DatabaseInterface.CONNECT_USER);
		        if ($userlink == null) {
		            $auth_plugin.showFailure("mysql-denied");
		        }
		        if (! $controllink) {
//		             * Open separate connection for control queries, this is needed
//		             * to avoid problems with table locking used in main connection
//		             * and phpMyAdmin issuing queries to configuration storage, which
//		             * is not locked by that time.
		            $controllink = GLOBALS.dbi.connect(
		                DatabaseInterface.CONNECT_USER,
		                null,
		                DatabaseInterface.CONNECT_CONTROL
		            );
		        }
		        $auth_plugin.rememberCredentials();
		        $auth_plugin.checkTwoFactor();
		        
		        // Log success
		        Logging.logUser(cfg.get("Server.user"));
		        if (GLOBALS.dbi.getVersion() < GLOABLS.cfg.get("MysqlMinVersion.internal") {
		            Core.fatalError(
		                __("You should upgrade to %s %s or later."),
		                [
		                    "MySQL",
		                    GLOBALS.cfg["MysqlMinVersion"]["human"],
		                ]
		            );
		        }
		        // Sets the default delimiter (if specified).
		        if (! empty(request.getParameter("sql_delimiter"))) {
		            Lexer.$DEFAULT_DELIMITER = request.getParameter("sql_delimiter");
		        }*/
		        // TODO: Set SQL modes too.
		    } else { // end server connecting
		        pmaResponse.getHeader().disableMenuAndConsole();
		        pmaResponse.getFooter().setMinimal();
		    }
		    /**
		     * check if profiling was requested and remember it
		     * (note: when GLOBALS.cfg["ServerDefault"] = 0, constant is not defined)
		     */
		    if (! empty(request.getParameter("profiling"))
		        && Util.profilingSupported(GLOBALS, $_SESSION)
		    ) {
		    	request.getSession().setAttribute("profiling", true);
		    } else if (! empty(request.getParameter("profiling_form"))) {
		        // the checkbox was unchecked
		    	request.getSession().removeAttribute("profiling");
		    }
		    /**
		     * Inclusion of profiling scripts is needed on various
		     * pages like sql, tbl_sql, db_sql, tbl_select
		     */
		    if (! empty (request.getSession().getAttribute("profiling"))) {
		        Scripts $scripts  = pmaResponse.getHeader().getScripts();
		        $scripts.addFile("chart.js");
		        $scripts.addFile("vendor/jqplot/jquery.jqplot.js");
		        $scripts.addFile("vendor/jqplot/plugins/jqplot.pieRenderer.js");
		        $scripts.addFile("vendor/jqplot/plugins/jqplot.highlighter.js");
		        $scripts.addFile("vendor/jquery/jquery.tablesorter.js");
		    }
		    /*
		     * There is no point in even attempting to process
		     * an ajax request if there is a token mismatch
		     */
		    if (pmaResponse.isAjax() && request.getMethod().equals("POST") && $token_mismatch) {
		    	pmaResponse.setRequestStatus(false);
		    	pmaResponse.addJSON(
		            "message",
		            Message.error(__("Error: Token mismatch"), request, GLOBALS)
		        );
		        //exit();	// FIXME
		    }
		    //$containerBuilder.set("response", Response.getInstance());
		}
		// load user preferences
		GLOBALS.PMA_Config.loadUserPreferences();
		
		// We override standard service request! Always call doGet
		this.doGet(request, response, pmaResponse, $_SESSION, GLOBALS);

		pmaResponse.response();
	}

	/**
	 * GET handler. Must be defined.
	 */
	protected abstract void doGet(HttpServletRequest request, HttpServletResponse response, Response pmaResponse, SessionMap $_SESSION, Globals GLOBALS)
			throws ServletException, IOException;


}
