package org.javamyadmin.controllers;

import static org.javamyadmin.php.Php.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.javamyadmin.helpers.Core;
import org.javamyadmin.helpers.DatabaseInterface;
import org.javamyadmin.helpers.Language;
import org.javamyadmin.helpers.LanguageManager;
import org.javamyadmin.helpers.Message;
import org.javamyadmin.helpers.Response;
import org.javamyadmin.helpers.Sanitize;
import org.javamyadmin.helpers.Scripts;
import org.javamyadmin.helpers.Template;
import org.javamyadmin.helpers.ThemeManager;
import org.javamyadmin.helpers.Util;
import org.javamyadmin.php.Globals;
import org.javamyadmin.php.Php.SessionMap;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractController {

	@Autowired
	protected Globals GLOBALS;
	
	@Autowired
	protected Response response;
	
	@Autowired
	protected SessionMap $_SESSION;
	
	@Autowired
	protected HttpServletRequest httpRequest; // TODO : use $_REQUEST map instead
	
	@Autowired
	protected HttpServletResponse httpResponse;
	
	@Autowired
	protected DatabaseInterface dbi;
	
    @Autowired
    protected Template template;
	
	/**
	 * Prepare global variables.
	 * 
	 * This could be an Interceptor. 
	 * 
	 * @param httpRequest
	 * @param httpResponse
	 * @throws ServletException
	 * @throws IOException
	 * @throws NamingException 
	 * @throws SQLException 
	 */
	public void prepareResponse() throws ServletException, IOException, SQLException, NamingException {
		
		// cfr. commons.inc.php

		// init some variables LABEL_variables_init

		// Security fix: disallow accessing serious server files via "?goto="
		if (!empty(httpRequest.getParameter("goto")) && Core.checkPageValidity(httpRequest.getParameter("goto"))) {
			GLOBALS.setGoto(httpRequest.getParameter("goto"));
			GLOBALS.getUrlParameters().put("goto", httpRequest.getParameter("goto"));
		} else {
			GLOBALS.setGoto(httpRequest.getParameter(""));
			Globals.getConfig().removeCookie("goto", httpRequest, httpResponse);
			// TODO unset($_REQUEST['goto'], $_GET['goto'], $_POST['goto']);
		}

		// returning page
		if (!empty(httpRequest.getParameter("back")) && Core.checkPageValidity(httpRequest.getParameter("back"))) {
			GLOBALS.setBack(httpRequest.getParameter("back"));
		} else {
			Globals.getConfig().removeCookie("back", httpRequest, httpResponse);
		    // TODO unset($_REQUEST['back'], $_GET['back'], $_POST['back']);
		}

		
		/**
		 * Check whether user supplied token is valid, if not remove any possibly
		 * dangerous stuff from request.
		 *
		 * remember that some objects in the session with session_start and __wakeup()
		 * could access this variables before we reach this point
		 * f.e. PhpMyAdmin\Config: fontsize
		 *
		 * Check for token mismatch only if the Request method is POST
		 * GET Requests would never have token and therefore checking
		 * mis-match does not make sense
		 *
		 * @todo variables should be handled by their respective owners (objects)
		 * f.e. lang, server in PhpMyAdmin\Config
		 */

		boolean $token_mismatch = true;
		boolean $token_provided = false;
		if (httpRequest.getMethod().equals("POST")) {
		    if (Core.isValid(httpRequest.getParameter("token"))) {
		        $token_provided = true;
		        $token_mismatch = ! httpRequest.getParameter("token").equals(httpRequest.getSession().getAttribute(" PMA_token "));
		    }
		    if ($token_mismatch) {
		        // Warn in case the mismatch is result of failed setting of session cookie
		        if (httpRequest.getParameter("set_session") != null &&  !httpRequest.getParameter("set_session").equals(httpRequest.getSession().getId())) {
		            trigger_error(
		                __(
		                    "Failed to set session cookie. Maybe you are using "
		                    + "HTTP instead of HTTPS to access phpMyAdmin."
		                ),
		                E_USER_ERROR
		            );
		        }
		        // We don"t allow any POST operation parameters if the token is mismatched
		        // or is not provided
		        String[] $whitelist = new String[] {"ajax_request"};
		        Sanitize.removeRequestVars($whitelist);
		    }
		}
		
		// current selected database
		Core.setGlobalDbOrTable("db");
		
		// current selected table
		Core.setGlobalDbOrTable("table");
		
		// Store currently selected recent table.
		// Affect GLOBALS.db"] and GLOBALS.table"]
		if (!empty(httpRequest.getParameter("selected_recent_table")) && Core.isValid(httpRequest.getParameter("selected_recent_table"))) {
		    Map $recent_table = (Map)json_decode(httpRequest.getParameter("selected_recent_table"));
		    GLOBALS.setDb($recent_table.containsKey("db") && ($recent_table.get("db") instanceof String) ? (String) $recent_table.get("db") : "");
		    GLOBALS.setDb($recent_table.containsKey("table") && ($recent_table.get("table") instanceof String) ? (String) $recent_table.get("table") : "");
		    /* TODO ?!? 
		    GLOBALS.getUrlParameters().put(
		        "db", $containerBuilder.getParameter("db")
		    );
		    GLOBALS.getUrlParameters().put(
		        "table", $containerBuilder.getParameter("table")
		    );*/
		}
		
		// SQL query to be executed
		if (Core.isValid(httpRequest.getParameter("sql_query"))) {
			GLOBALS.setSqlQuery(httpRequest.getParameter("sql_query"));
		}
		
		// lang detection is done here
		Language $language = LanguageManager.getInstance().selectLanguage(httpRequest, httpResponse);
		$language.activate(GLOBALS);
		
		// check for errors occurred while loading configuration
		// this check is done here after loading language files to present errors in locale
		Globals.getConfig().checkPermissions();
		Globals.getConfig().checkErrors(httpRequest, httpResponse, GLOBALS, response);
		
		// Check server configuration
		Core.checkConfiguration();

		// Check request for possible attacks
		Core.checkRequest(httpRequest, httpResponse, GLOBALS, response);

		/******************************************************************************/
		/* setup servers                                       LABEL_setup_servers    */

		Globals.getConfig().checkServers();

		// current server
		GLOBALS.setServer(Globals.getConfig().selectServer(httpRequest));
		// ?!? TODO $diMigration->setGlobal('url_params', ['server' => $containerBuilder->getParameter('server')] + $containerBuilder->getParameter('url_params'));
		
		// BC - enable backward compatibility
		// exports all configuration settings into $GLOBALS ($GLOBALS['cfg'])
		//$GLOBALS['PMA_Config']->enableBc();

		/******************************************************************************/
		/* setup themes                                          LABEL_theme_setup    */

		ThemeManager.initializeTheme(httpRequest, GLOBALS, $_SESSION);
		
		if (!(GLOBALS.get_PMA_MINIMUM_COMMON())) {
		    /**
		     * save some settings in cookies
		     * @todo should be done in PhpMyAdmin\Config
		     */
		    Globals.getConfig().setCookie("pma_lang", GLOBALS.getLang(), httpRequest, httpResponse);
		    GLOBALS.getThemeManager().setThemeCookie(httpRequest, httpResponse);
		    if (! empty(Globals.getConfig().get("Server"))) {
		        /**
		         * Loads the proper database interface for this server
		         */
		        // $containerBuilder.set(DatabaseInterface.class, DatabaseInterface.load());
		        // $containerBuilder.setAlias("dbi", DatabaseInterface.class);
		    	
		        // get LoginCookieValidity from preferences cache
		        // no generic solution for loading preferences from cache as some settings
		        // need to be kept for processing in
		        // PhpMyAdmin\Config.loadUserPreferences()
		        
		    	String $cache_key = "server_" + GLOBALS.getServer();
		        if (!empty(httpRequest.getSession().getAttribute("cache." + $cache_key + ".userprefs.LoginCookieValidity"))
		        ) {
		            String $value
		                = (String) httpRequest.getSession().getAttribute("cache." + $cache_key + ".userprefs.LoginCookieValidity");
		            Globals.getConfig().set("LoginCookieValidity", $value);
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
		        Connection $controllink = null;
		        if (!empty(((Map) Globals.getConfig().get("Server")).get("controluser"))) {
		            $controllink = GLOBALS.getDbi().connect(
		                DatabaseInterface.CONNECT_CONTROL
		            );
		        }
		        // Connects to the server (validates user"s login)
		        // @var DatabaseInterface $userlink
		        Connection $userlink = GLOBALS.getDbi().connect(DatabaseInterface.CONNECT_USER);
		        if ($userlink == null) {
		        	/* TODO
		            $auth_plugin.showFailure("mysql-denied");
		            */
		        }
		        if ($controllink == null) {
//		             * Open separate connection for control queries, this is needed
//		             * to avoid problems with table locking used in main connection
//		             * and phpMyAdmin issuing queries to configuration storage, which
//		             * is not locked by that time.
		            $controllink = GLOBALS.getDbi().connect(
		                DatabaseInterface.CONNECT_USER,
		                null,
		                DatabaseInterface.CONNECT_CONTROL
		            );
		        }
		        // $auth_plugin.rememberCredentials();
		        // $auth_plugin.checkTwoFactor();
		        
		        // Log success
		        // Logging.logUser(cfg.get("Server.user"));

		        // Sets the default delimiter (if specified).
		        /*if (! empty(request.getParameter("sql_delimiter"))) {
		            Lexer.$DEFAULT_DELIMITER = request.getParameter("sql_delimiter");
		        }*/
		        // TODO: Set SQL modes too.
		    } else { // end server connecting
		        response.getHeader().disableMenuAndConsole();
		        response.getFooter().setMinimal();
		    }
		    /**
		     * check if profiling was requested and remember it
		     * (note: when GLOBALS.cfg["ServerDefault"] = 0, constant is not defined)
		     */
		    if (! empty(httpRequest.getParameter("profiling"))
		        && Util.profilingSupported(GLOBALS, $_SESSION)
		    ) {
		    	httpRequest.getSession().setAttribute("profiling", true);
		    } else if (! empty(httpRequest.getParameter("profiling_form"))) {
		        // the checkbox was unchecked
		    	httpRequest.getSession().removeAttribute("profiling");
		    }
		    /**
		     * Inclusion of profiling scripts is needed on various
		     * pages like sql, tbl_sql, db_sql, tbl_select
		     */
		    if (! empty (httpRequest.getSession().getAttribute("profiling"))) {
		        Scripts $scripts  = response.getHeader().getScripts();
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
		    if (response.isAjax() && httpRequest.getMethod().equals("POST") && $token_mismatch) {
		    	response.setRequestStatus(false);
		    	response.addJSON(
		            "message",
		            Message.error(__("Error: Token mismatch"))
		        );
		        //exit();	// FIXME
		    }
		    //$containerBuilder.set("response", Response.getInstance());
		}
		// load user preferences
		Globals.getConfig().loadUserPreferences(GLOBALS, $_SESSION, httpRequest, httpResponse);

		/*if (empty(GLOBALS.PMA_MINIMUM_COMMON)) {
			pmaResponse.response();
		}*/
	}

	public DatabaseInterface getDbi() {
		return dbi;
	}

}
