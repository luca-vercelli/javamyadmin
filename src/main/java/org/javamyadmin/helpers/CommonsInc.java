package org.javamyadmin.helpers;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.javamyadmin.php.Globals;
import org.javamyadmin.php.Php.SessionMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.javamyadmin.php.Php.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * 
 * @see commons.inc.php
 *
 */
@Service
public class CommonsInc {

	@Autowired
	private Globals GLOBALS;
	@Autowired
	private SessionMap $_SESSION;
	@Autowired
	private HttpServletRequest httpRequest; // TODO : use $_REQUEST map instead
	@Autowired
	private HttpServletResponse httpResponse;
	
	@Autowired
	private Response response;
    @Autowired
    private Session session;
	@Autowired
	private LanguageManager languageManager;
	@Autowired
	private ThemeManager themeManager;
	@Autowired
	private Config config;
    @Autowired
    private Sanitize sanitize;
    @Autowired
    private Core core;

    
    /**
     * Common stuff (TODO this could be put inside a WebFilter).
     * 
     * @return false ifrequest must not be processed (es. token mismatch). 
     * @throws SQLException
     * @throws NamingException
     */
	public boolean execute() throws SQLException, NamingException {

		// init some variables LABEL_variables_init

		if (! GLOBALS.get_PMA_NO_SESSION()) {
		    session.setUp(httpRequest.getSession());
		}
		 
		// Security fix: disallow accessing serious server files via "?goto="
		if (!empty(httpRequest.getParameter("goto")) && core.checkPageValidity(httpRequest.getParameter("goto"))) {
			GLOBALS.setGoto(httpRequest.getParameter("goto"));
			GLOBALS.getUrlParameters().put("goto", httpRequest.getParameter("goto"));
		} else {
			GLOBALS.setGoto(httpRequest.getParameter(""));
			config.removeCookie("goto", httpRequest, httpResponse);
			// TODO unset($_REQUEST['goto'], $_GET['goto'], $_POST['goto']);
		}

		// returning page
		if (!empty(httpRequest.getParameter("back")) && core.checkPageValidity(httpRequest.getParameter("back"))) {
			GLOBALS.setBack(httpRequest.getParameter("back"));
		} else {
			config.removeCookie("back", httpRequest, httpResponse);
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
		    if (core.isValid(httpRequest.getParameter("token"))) {
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
		        sanitize.removeRequestVars($whitelist);
		    }
		}
		
		// current selected database
		core.setGlobalDbOrTable("db");
		
		// current selected table
		core.setGlobalDbOrTable("table");
		
		// Store currently selected recent table.
		// Affect GLOBALS.db"] and GLOBALS.table"]
		if (!empty(httpRequest.getParameter("selected_recent_table")) && core.isValid(httpRequest.getParameter("selected_recent_table"))) {
		    Map $recent_table = (Map)json_decode(httpRequest.getParameter("selected_recent_table"));
		    GLOBALS.setDb($recent_table.containsKey("db") && ($recent_table.get("db") instanceof String) ? (String) $recent_table.get("db") : "");
		    GLOBALS.setDb($recent_table.containsKey("table") && ($recent_table.get("table") instanceof String) ? (String) $recent_table.get("table") : "");
		    GLOBALS.getUrlParameters().put("db", GLOBALS.getDb());
		    GLOBALS.getUrlParameters().put("table", GLOBALS.getTable());
		}
		
		// SQL query to be executed
		if (core.isValid(httpRequest.getParameter("sql_query"))) {
			GLOBALS.setSqlQuery(httpRequest.getParameter("sql_query"));
		}
		
		// lang detection is done here
		Language $language = languageManager.selectLanguage(httpRequest, httpResponse);
		$language.activate(GLOBALS);
		
		// check for errors occurred while loading configuration
		// this check is done here after loading language files to present errors in locale
		config.checkPermissions();
		config.checkErrors(httpRequest, httpResponse, GLOBALS, response);
		
		// Check server configuration
		core.checkConfiguration();

		// Check request for possible attacks
		core.checkRequest(httpRequest, httpResponse, GLOBALS, response);

		/******************************************************************************/
		/* setup servers                                       LABEL_setup_servers    */

		config.checkServers();

		// current server
		GLOBALS.setServer(config.selectServer(httpRequest));
		if (GLOBALS.getServer() != null) {
			GLOBALS.getUrlParameters().put("server", Integer.toString(GLOBALS.getServer()));
		}
		
		// BC - enable backward compatibility
		// exports all configuration settings into $GLOBALS ($GLOBALS['cfg'])
		//$GLOBALS['PMA_Config']->enableBc();

		/******************************************************************************/
		/* setup themes                                          LABEL_theme_setup    */

		themeManager.initializeTheme();
		
		if (!(GLOBALS.get_PMA_MINIMUM_COMMON())) {
		    /**
		     * save some settings in cookies
		     * @todo should be done in PhpMyAdmin\Config
		     */
		    config.setCookie("pma_lang", GLOBALS.getLang(), httpRequest, httpResponse);
		    GLOBALS.getThemeManager().setThemeCookie(httpRequest, httpResponse);
		    if (! empty(config.get("Server"))) {
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
		            config.set("LoginCookieValidity", $value);
		        }
		        // Gets the authentication library that fits the GLOBALS.cfg["Server"] settings
		        // and run authentication
		        /**
		         * the required auth type plugin
		         */
		        /* TODO ?
		        $auth_class = "PhpMyAdmin\\Plugins\\Auth\\Authentication" . ucfirst(strtolower(GLOBALS.cfg["Server"]["auth_type"]));
		        if (! @class_exists($auth_class)) {
		            core.fatalError(
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
		        if (!empty(((Map) config.get("Server")).get("controluser"))) {
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
		    // check if profiling was requested and remember it
		    // (note: when GLOBALS.cfg["ServerDefault"] = 0, constant is not defined)
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
		    // There is no point in even attempting to process
		    // an ajax request if there is a token mismatch
		    if (response.isAjax() && httpRequest.getMethod().equals("POST") && $token_mismatch) {
		    	response.setRequestStatus(false);
		    	response.addJSON(
		            "message",
		            Message.error(__("Error: Token mismatch"))
		        );
		        return false;
		    }
		}
		// load user preferences
		config.loadUserPreferences(GLOBALS, $_SESSION, httpRequest, httpResponse, languageManager);
		return true;
	}
}
