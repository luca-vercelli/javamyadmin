package org.javamyadmin.helpers;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.javamyadmin.java.SmartMap;
import org.javamyadmin.jtwig.JtwigFactory;
import org.javamyadmin.php.Globals;
import static org.javamyadmin.php.Php.*;

/**
 * Class used to output the HTTP and HTML headers
 *
 * @package PhpMyAdmin
 */
public class Header {

    /**
     * Scripts instance
     *
     * @access private
     * @var Scripts
     */
    private Scripts _scripts;
    /**
     * PhpMyAdmin\Console instance
     *
     * @access private
     * @var Console
     */
    // TODO private Console _console;
    /**
     * Menu instance
     *
     * @access private
     * @var Menu
     */
    private Menu _menu;
    /**
     * Whether to offer the option of importing user settings
     *
     * @access private
     * @var boolean
     */
    private boolean _userprefsOfferImport;
    /**
     * The page title
     *
     * @access private
     * @var String
     */
    private String _title;
    /**
     * The value for the id attribute for the body tag
     *
     * @access private
     * @var String
     */
    private String _bodyId;
    /**
     * Whether to show the top menu
     *
     * @access private
     * @var boolean
     */
    private boolean _menuEnabled;
    /**
     * Whether to show the warnings
     *
     * @access private
     * @var boolean
     */
    private boolean _warningsEnabled;
    /**
     * Whether the page is in "print view" mode
     *
     * @access private
     * @var boolean
     */
    private boolean _isPrintView;
    /**
     * Whether we are servicing an ajax request.
     *
     * @access private
     * @var boolean
     */
    private boolean _isAjax;
    /**
     * Whether to display anything
     *
     * @access private
     * @var boolean
     */
    private boolean _isEnabled;
    /**
     * Whether the HTTP headers (and possibly some HTML)
     * have already been sent to the browser
     *
     * @access private
     * @var boolean
     */
    private boolean _headerIsSent;

    /**
     * @var UserPreferences
     */
    // TODO private UserPreferences userPreferences;

    /**
     * @var Template
     */
    //private Template template;

    /**
     * @var Navigation
     */
    // TODO private Navigation navigation;

    private Globals GLOBALS;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private SessionMap session;
    private SmartMap cfg;
    
    /**
     * Creates a new class instance
     */
    public Header(HttpServletRequest request, HttpServletResponse response, Globals GLOBALS, SessionMap session)
    {
    	this.request = request;
    	this.response = response;
    	this.GLOBALS = GLOBALS;
    	this.session = session;
    	this.cfg = Globals.getConfig().settings;
    	
        //this.template = new Template();

        this._isEnabled = true;
        this._isAjax = false;
        this._bodyId = "";
        this._title = "";
        // TODO this._console = new Console();
        String $db = GLOBALS.getDb(); // FIXME col xxx che sono globali
        String $table = GLOBALS.getTable();
        this._menu = new Menu(
            $db,
            $table,
            request,
            GLOBALS,
            session
        );
        this._menuEnabled = true;
        this._warningsEnabled = true;
        this._isPrintView = false;
        this._scripts = new Scripts(GLOBALS);
        this._addDefaultScripts();
        this._headerIsSent = false;
        // if database storage for user preferences is transient,
        // offer to load exported settings from localStorage
        // (detection will be done in JavaScript)
        this._userprefsOfferImport = false;
        if ("session".equals(cfg.get("user_preferences"))
            && ! empty(request.getSession().getAttribute("userprefs_autoload"))
        ) {
            this._userprefsOfferImport = true;
        }

        /* TODO
        this.userPreferences = new UserPreferences();
        this.navigation = new Navigation(
            this.template,
            new Relation(GLOBALS.getDbi()),
            GLOBALS.getDbi()
        );*/
    }

    /**
     * Loads common scripts
     *
     * @return void
     */
    private void _addDefaultScripts()
    {
        // Localised Strings
        this._scripts.addFile("vendor/jquery/jquery.min.js");
        this._scripts.addFile("vendor/jquery/jquery-migrate.js");
        this._scripts.addFile("whitelist.php");
        this._scripts.addFile("vendor/sprintf.js");
        this._scripts.addFile("ajax.js");
        this._scripts.addFile("keyhandler.js");
        this._scripts.addFile("vendor/bootstrap/bootstrap.bundle.min.js");
        this._scripts.addFile("vendor/jquery/jquery-ui.min.js");
        this._scripts.addFile("vendor/js.cookie.js");
        this._scripts.addFile("vendor/jquery/jquery.mousewheel.js");
        this._scripts.addFile("vendor/jquery/jquery.event.drag-2.2.js");
        this._scripts.addFile("vendor/jquery/jquery.validate.js");
        this._scripts.addFile("vendor/jquery/jquery-ui-timepicker-addon.js");
        this._scripts.addFile("vendor/jquery/jquery.ba-hashchange-1.3.js");
        this._scripts.addFile("vendor/jquery/jquery.debounce-1.0.5.js");
        this._scripts.addFile("menu_resizer.js");

        // Cross-framing protection
        if ("false".equals(cfg.get("AllowThirdPartyFraming"))) {
            this._scripts.addFile("cross_framing_protection.js");
        }

        this._scripts.addFile("rte.js");
        if (!"never".equals(cfg.get("SendErrorReports"))) {
            this._scripts.addFile("vendor/tracekit.js");
            this._scripts.addFile("error_report.js");
        }

        // Here would not be a good place to add CodeMirror because
        // the user preferences have not been merged at this point

        Map<String, Object> params = new HashMap<>();
        params.put("l", GLOBALS.getLang());
        this._scripts.addFile("messages.php", params);
        this._scripts.addFile("config.js");
        this._scripts.addFile("doclinks.js");
        this._scripts.addFile("functions.js");
        this._scripts.addFile("navigation.js");
        this._scripts.addFile("indexes.js");
        this._scripts.addFile("common.js");
        this._scripts.addFile("page_settings.js");
        if ("true".equals(cfg.get("enable_drag_drop_import"))) {
            this._scripts.addFile("drag_drop_import.js");
        }
        if (empty(Globals.getConfig().get("DisableShortcutKeys"))) {
            this._scripts.addFile("shortcuts_handler.js");
        }
        this._scripts.addCode(this.getJsParamsCode());
    }

    /**
     * Returns, as an array, a list of parameters
     * used on the client side
     *
     * @return array
     */
    public Map<String, Object> getJsParams()
    {
    	Map<String, Object> $params = new HashMap<String, Object>();
    	
    	String $db = !empty(GLOBALS.getDb()) ? GLOBALS.getDb() : "";
    	String $table = !empty(GLOBALS.getTable()) ? GLOBALS.getTable() : "";
        String $pftext = (String) multiget(session, "tmpval", "pftext");

        $params.put("common_query", Url.getCommonRaw(request, GLOBALS));
        $params.put("opendb_url", Util.getScriptNameForOption(
                (String) cfg.get("DefaultTabDatabase"),
                "database", request, GLOBALS
            ));
        $params.put("lang", GLOBALS.getLang());
        $params.put("table", $table);
        $params.put("db", $db);
        $params.put("token", session.get(" PMA_token "));
        $params.put("text_dir", GLOBALS.getTextDir());
        $params.put("show_databases_navigation_as_tree", cfg.get("ShowDatabasesNavigationAsTree"));
        $params.put("pma_text_default_tab", Util.getTitleForTarget(
                (String) cfg.get("DefaultTabTable")
            ));
        $params.put("pma_text_left_default_tab", Util.getTitleForTarget(
                (String) cfg.get("NavigationTreeDefaultTabTable")
            ));
        $params.put("pma_text_left_default_tab2", Util.getTitleForTarget(
                (String) cfg.get("NavigationTreeDefaultTabTable2")
            ));
        $params.put("text_dir", GLOBALS.getTextDir());
        $params.put("text_dir", GLOBALS.getTextDir());
        $params.put("text_dir", GLOBALS.getTextDir());
        $params.put("text_dir", GLOBALS.getTextDir());
        $params.put("text_dir", GLOBALS.getTextDir());
        $params.put("LimitChars", cfg.get("LimitChars"));
        $params.put("pftext", $pftext);
        $params.put("confirm", cfg.get("Confirm"));
        $params.put("LoginCookieValidity", cfg.get("LoginCookieValidity"));
        $params.put("session_gc_maxlifetime", -1); // Java Unsupported
        $params.put("logged_in", GLOBALS.getDbi() != null && GLOBALS.getDbi().isUserType("logged"));
        $params.put("is_https", Globals.getConfig().isHttps());
        $params.put("rootPath", Globals.getConfig().getRootPath(request));
        $params.put("arg_separator", Url.getArgSeparator());
        $params.put("PMA_VERSION", Globals.getPmaVersion());
        
        $params.put("auth_type", multiget(cfg, "Server", "auth_type"));
        $params.put("user", multiget(cfg, "Server", "user"));

        return $params;
    }

    /**
     * Returns, as a String, a list of parameters
     * used on the client side
     *
     * @return String
     */
    public String getJsParamsCode()
    {
        Map<String, Object> $params = this.getJsParams();
        for (Entry<String, Object> entry: $params.entrySet()) {
        	String key = entry.getKey();
        	String value = entry.getValue() == null ? null : entry.getValue().toString();
            if (entry.getValue() instanceof Boolean) {
                $params.put(entry.getKey(), key + ":" + (((Boolean)entry.getValue()) ? "true" : "false") + "");
            } else {
                $params.put(entry.getKey(), key + ":'" + Sanitize.escapeJsString(value) + "'");
            }
        }
        return "CommonParams.setAll({" + String.join(",", (Collection)$params.values()) + "});";
    }

    /**
     * Disables the rendering of the header
     *
     * @return void
     */
    public void disable()
    {
        this._isEnabled = false;
    }

    /**
     * Set the ajax flag to indicate whether
     * we are servicing an ajax request
     *
     * @param boolean $isAjax Whether we are servicing an ajax request
     *
     * @return void
     */
    public void setAjax(boolean $isAjax)
    {
        this._isAjax = $isAjax;
        // TODO this._console.setAjax($isAjax);
    }

    /**
     * Returns the Scripts object
     *
     * @return Scripts object
     */
    public Scripts getScripts()
    {
        return this._scripts;
    }

    /**
     * Returns the Menu object
     *
     * @return Menu object
     */
    public Menu getMenu()
    {
        return this._menu;
    }

    /**
     * Setter for the ID attribute in the BODY tag
     *
     * @param String $id Value for the ID attribute
     *
     * @return void
     */
    public void setBodyId(String $id)
    {
        this._bodyId = htmlspecialchars($id);
    }

    /**
     * Setter for the title of the page
     *
     * @param String $title New title
     *
     * @return void
     */
    public void setTitle(String $title)
    {
        this._title = htmlspecialchars($title);
    }

    /**
     * Disables the display of the top menu
     *
     * @return void
     */
    public void disableMenuAndConsole()
    {
        this._menuEnabled = false;
        // TODO this._console.disable();
    }

    /**
     * Disables the display of the top menu
     *
     * @return void
     */
    public void disableWarnings()
    {
        this._warningsEnabled = false;
    }

    /**
     * Turns on "print view" mode
     *
     * @return void
     */
    public void enablePrintView()
    {
        this.disableMenuAndConsole();
        this.setTitle(__("Print view") + " - phpMyAdmin " + Globals.getPmaVersion());
        this._isPrintView = true;
    }

    /**
     * Generates the header
     *
     * @return String The header
     * @throws SQLException 
     */
    public String getDisplay() throws SQLException
    {
        if (! this._headerIsSent) {
            String $baseDir = null;
            String $uniqueValue = null;
            String $themePath = null;
            String $version = null;
            String $messages = null;
            String $recentTable = null;
            String $customHeader = null;
            String $navigation = null;
            String $loadUserPreferences = null;
            String $menu = null;
            String $console = null;
            		
            if (! this._isAjax && this._isEnabled) {
                this.sendHttpHeaders();

                $baseDir = Globals.getPmaPathToBasedir();
                $uniqueValue = Globals.getConfig().getThemeUniqueValue(GLOBALS);
                $themePath = GLOBALS.getPmaThemeUrlPath();
                $version = getVersionParameter();

                // The user preferences have been merged at this point
                // so we can conditionally add CodeMirror
                if (!empty(cfg.get("CodemirrorEnable"))) {
                    this._scripts.addFile("vendor/codemirror/lib/codemirror.js");
                    this._scripts.addFile("vendor/codemirror/mode/sql/sql.js");
                    this._scripts.addFile("vendor/codemirror/addon/runmode/runmode.js");
                    this._scripts.addFile("vendor/codemirror/addon/hint/show-hint.js");
                    this._scripts.addFile("vendor/codemirror/addon/hint/sql-hint.js");
                    if (!empty(cfg.get("LintEnable"))) {
                        this._scripts.addFile("vendor/codemirror/addon/lint/lint.js");
                        this._scripts.addFile(
                            "codemirror/addon/lint/sql-lint.js"
                        );
                    }
                }
                this._scripts.addCode(
                    "ConsoleEnterExecutes="
                    + cfg.get("ConsoleEnterExecutes")
                );
                // TODO this._scripts.addFiles(this._console.getScripts());
                if (this._userprefsOfferImport) {
                    this._scripts.addFile("config.js");
                }

                if (this._menuEnabled && GLOBALS.getServer() > 0) {
                    // TODO $navigation = this.navigation.getDisplay();
                }

                $customHeader = Config.renderHeader();

                // offer to load user preferences from localStorage
                if (this._userprefsOfferImport) {
                    // TODO $loadUserPreferences = this.userPreferences.autoloadGetHeader();
                }

                if (this._menuEnabled && GLOBALS.getServer() > 0) {
                    $menu = this._menu.getDisplay(request, GLOBALS);
                }
                // TODO $console = this._console.getDisplay();
                $messages = this.getMessage();
            }
            if (this._isEnabled && empty(request.getParameter("recent_table"))) {
                $recentTable = this._addRecentTable(
                    GLOBALS.getDb(),
                    GLOBALS.getTable()
                );
            }
            
            Map<String, Object> model = new HashMap<>();
            model.put("is_ajax", this._isAjax);
            model.put("is_enabled", this._isEnabled);
            model.put("lang", GLOBALS.getLang());
            model.put("allow_third_party_framing", cfg.get("AllowThirdPartyFraming"));
            model.put("is_print_view" , this._isPrintView);
			model.put("base_dir", $baseDir);
			model.put("unique_value", $uniqueValue);
			model.put("theme_path", $themePath);
			model.put("version", $version);
			model.put("text_dir", GLOBALS.getTextDir());
			model.put("server", GLOBALS.getServer() > 0 ? GLOBALS.getServer() : null);
			model.put("title", this.getPageTitle());
			model.put("scripts", this._scripts.getDisplay());
			model.put("body_id", this._bodyId);
			model.put("navigation", $navigation);
			model.put("custom_header", $customHeader);
			model.put("load_user_preferences", $loadUserPreferences);
			model.put("show_hint", cfg.get("ShowHint"));
			model.put("is_warnings_enabled", this._warningsEnabled);
			model.put("is_menu_enabled", this._menuEnabled);
			model.put("menu", $menu);
			model.put("console", $console);
			model.put("messages", $messages);
			model.put("has_recent_table", empty(request.getParameter("recent_table")));
			model.put("recent_table", $recentTable);            
            
            return JtwigFactory.render("header", model);
        }
        return "";
    }

    /**
     * Returns the message to be displayed at the top of
     * the page, including the executed SQL query, if any.
     *
     * @return String
     */
    public String getMessage()
    {
        String $retval = "";
        String $message = "";
        if (! empty(GLOBALS.getMessage())) {
            $message = GLOBALS.getMessage();
            GLOBALS.setMessage(null);
        } else if (! empty(request.getParameter("message"))) {
            $message = request.getParameter("message");
        }
        if (! empty($message)) {
        	String $buffer_message = null;
            if (GLOBALS.getBufferMessage() != null) {
                $buffer_message = GLOBALS.getBufferMessage();
            }
            $retval += Util.getMessage($message);
            if (!empty($buffer_message)) {
                GLOBALS.setBufferMessage($buffer_message);
            }
        }
        return $retval;
    }

    private static SimpleDateFormat gmdate = new SimpleDateFormat("E, d M y H:m:s 'GMT'");
    
    /**
     * Sends out the HTTP headers
     *
     * @return void
     */
    public void sendHttpHeaders()
    {
        String $map_tile_urls = " *.tile.openstreetmap.org";

        /**
         * Sends http headers
         */
        String $captcha_url;
        
        String now = gmdate.format(new Date());	// FIXME
        if (! empty(cfg.get("CaptchaLoginPrivateKey"))
            && ! empty(cfg.get("CaptchaLoginPublicKey"))
        ) {
            $captcha_url
                = " https://apis.google.com https://www.google.com/recaptcha/"
                + " https://www.gstatic.com/recaptcha/ https://ssl.gstatic.com/ ";
        } else {
            $captcha_url = "";
        }
        /* Prevent against ClickJacking by disabling framing */
        if ("sameorigin".equalsIgnoreCase((String) cfg.get("AllowThirdPartyFraming"))) {
            response.addHeader(
                "X-Frame-Options", "SAMEORIGIN"
            );
        } else if (!"true".equals(cfg.get("AllowThirdPartyFraming"))) {
            response.addHeader(
                "X-Frame-Options", "DENY"
            );
        }
        response.addHeader(
            "Referrer-Policy", "no-referrer"
        );
        response.addHeader(
            "Content-Security-Policy", "default-src 'self' "
            + $captcha_url
            + cfg.get("CSPAllow") + ";"
            + "script-src 'self' 'unsafe-inline' 'unsafe-eval' "
            + $captcha_url
            + cfg.get("CSPAllow") + ";"
            + "style-src 'self' 'unsafe-inline' "
            + $captcha_url
            + cfg.get("CSPAllow")
            + ";"
            + "img-src 'self' data: "
            + cfg.get("CSPAllow")
            + $map_tile_urls
            + $captcha_url
            + ";"
            + "object-src 'none';"
        );
        response.addHeader(
            "X-Content-Security-Policy", "default-src 'self' "
            + $captcha_url
            + cfg.get("CSPAllow") + ";"
            + "options inline-script eval-script;"
            + "referrer no-referrer;"
            + "img-src 'self' data: "
            + cfg.get("CSPAllow")
            + $map_tile_urls
            + $captcha_url
            + ";"
            + "object-src 'none';"
        );
        response.addHeader(
            "X-WebKit-CSP", "default-src 'self' "
            + $captcha_url
            + cfg.get("CSPAllow") + ";"
            + "script-src 'self' "
            + $captcha_url
            + cfg.get("CSPAllow")
            + " 'unsafe-inline' 'unsafe-eval';"
            + "referrer no-referrer;"
            + "style-src 'self' 'unsafe-inline' "
            + $captcha_url
            + ";"
            + "img-src 'self' data: "
            + cfg.get("CSPAllow")
            + $map_tile_urls
            + $captcha_url
            + ";"
            + "object-src 'none';"
        );
        // Re-enable possible disabled XSS filters
        // see https://www.owasp.org/index.php/List_of_useful_HTTP_headers
        response.addHeader(
            "X-XSS-Protection", "1; mode=block"
        );
        // "nosniff", prevents Internet Explorer and Google Chrome from MIME-sniffing
        // a response away from the declared content-type
        // see https://www.owasp.org/index.php/List_of_useful_HTTP_headers
        response.addHeader(
            "X-Content-Type-Options", "nosniff"
        );
        // Adobe cross-domain-policies
        // see https://www.adobe.com/devnet/articles/crossdomain_policy_file_spec.html
        response.addHeader(
            "X-Permitted-Cross-Domain-Policies", "none"
        );
        // Robots meta tag
        // see https://developers.google.com/webmasters/control-crawl-index/docs/robots_meta_tag
        response.addHeader(
            "X-Robots-Tag", "noindex, nofollow"
        );
        Core.noCacheHeader(response);
        if (!GLOBALS.isTransformationWrapper()) {
            // Define the charset to be used
            response.addHeader("Content-Type", "text/html; charset=utf-8");
        }
        this._headerIsSent = true;
    }

    /**
     * If the page is missing the title, this function
     * will set it to something reasonable
     *
     * @return String
     */
    public String getPageTitle()
    {
        if ((empty(this._title))) {
            if (GLOBALS.getServer() > 0) {
            	String $temp_title;
                if (! empty(GLOBALS.getTable())) {
                    $temp_title = (String) cfg.get("TitleTable");
                } else if (!empty(GLOBALS.getDb())) {
                    $temp_title = (String) cfg.get("TitleDatabase");
                } else if (!empty(((Map<String,Object>) cfg.get("Server")).get("host"))) {
                    $temp_title = (String) cfg.get("TitleServer");
                } else {
                    $temp_title = (String) cfg.get("TitleDefault");
                }
                this._title = htmlspecialchars(
                    Util.expandUserString($temp_title)
                );
            } else {
                this._title = "phpMyAdmin";
            }
        }
        return this._title;
    }

    /**
     * Add recently used table and reload the navigation.
     *
     * @param String $db    Database name where the table is located.
     * @param String $table The table name
     *
     * @return String
     */
    private String _addRecentTable(String $db, String $table)
    {
    	return ""; //TODO
    	/*
        String $retval = "";
        if (this._menuEnabled
            && !empty($table)
            && (new Integer((String) cfg.get("NumRecentTables")) > 0)
        ) {
            String $tmp_result = RecentFavoriteTable.getInstance("recent").add(
                $db,
                $table
            );
            if ($tmp_result === true) {
                $retval = RecentFavoriteTable.getHtmlUpdateRecentTables();
            } else {
                $error  = $tmp_result;
                $retval = $error.getDisplay();
            }
        }
        return $retval;*/
    }

    /**
     * Returns the phpMyAdmin version to be appended to the url to avoid caching
     * between versions
     *
     * @return String urlenocded pma version as a parameter
     */
    public static String getVersionParameter()
    {
        return "v=" + urlencode(Globals.getPmaVersion());
    }

}
