package org.javamyadmin.helpers;

import java.util.HashMap;
import java.util.Map;

import org.javamyadmin.php.GLOBALS;

// intended for use with header.twig
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
    private Console _console;
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
    private String userPreferences;

    /**
     * @var Template
     */
    private Template template;

    /**
     * @var Navigation
     */
    private String navigation;

    private GLOBALS GLOBALS;
    
    /**
     * Creates a new class instance
     */
    public Header(GLOBALS GLOBALS)
    {
    	this.GLOBALS = GLOBALS;
    	
        this.template = new Template();

        this._isEnabled = true;
        this._isAjax = false;
        this._bodyId = "";
        this._title = "";
        this._console = new Console();
        String $db = GLOBALS.db; // FIXME col xxx che sono globali
        String $table = GLOBALS.table;
        this._menu = new Menu(
            $db,
            $table
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
        if (GLOBALS.PMA_Config.get("user_preferences") == "session"
            && ! isset($_SESSION["userprefs_autoload"])
        ) {
            this._userprefsOfferImport = true;
        }

        this.userPreferences = new UserPreferences();
        this.navigation = new Navigation(
            this.template,
            new Relation(GLOBALS.dbi),
            GLOBALS.dbi
        );
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
        if ($GLOBALS["cfg"]["AllowThirdPartyFraming"] === false) {
            this._scripts.addFile("cross_framing_protection.js");
        }

        this._scripts.addFile("rte.js");
        if ($GLOBALS["cfg"]["SendErrorReports"] !== "never") {
            this._scripts.addFile("vendor/tracekit.js");
            this._scripts.addFile("error_report.js");
        }

        // Here would not be a good place to add CodeMirror because
        // the user preferences have not been merged at this point

        this._scripts.addFile("messages.php", ["l" => $GLOBALS["lang"]]);
        this._scripts.addFile("config.js");
        this._scripts.addFile("doclinks.js");
        this._scripts.addFile("functions.js");
        this._scripts.addFile("navigation.js");
        this._scripts.addFile("indexes.js");
        this._scripts.addFile("common.js");
        this._scripts.addFile("page_settings.js");
        if ($GLOBALS["cfg"]["enable_drag_drop_import"] === true) {
            this._scripts.addFile("drag_drop_import.js");
        }
        if (! $GLOBALS["PMA_Config"].get("DisableShortcutKeys")) {
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
    	Map<String, Object> params = new HashMap<String, Object>();
    	
    	throw new IllegalStateException("serve la request qui");
        /*$db = strlen($GLOBALS["db"]) ? $GLOBALS["db"] : "";
        $table = strlen($GLOBALS["table"]) ? $GLOBALS["table"] : "";
        $pftext = isset($_SESSION["tmpval"]["pftext"])
            ? $_SESSION["tmpval"]["pftext"] : "";

        $params = [
            "common_query" => Url.getCommonRaw(),
            "opendb_url" => Util.getScriptNameForOption(
                $GLOBALS["cfg"]["DefaultTabDatabase"],
                "database"
            ),
            "lang" => $GLOBALS["lang"],
            "server" => $GLOBALS["server"],
            "table" => $table,
            "db" => $db,
            "token" => $_SESSION[" PMA_token "],
            "text_dir" => $GLOBALS["text_dir"],
            "show_databases_navigation_as_tree" => $GLOBALS["cfg"]["ShowDatabasesNavigationAsTree"],
            "pma_text_default_tab" => Util.getTitleForTarget(
                $GLOBALS["cfg"]["DefaultTabTable"]
            ),
            "pma_text_left_default_tab" => Util.getTitleForTarget(
                $GLOBALS["cfg"]["NavigationTreeDefaultTabTable"]
            ),
            "pma_text_left_default_tab2" => Util.getTitleForTarget(
                $GLOBALS["cfg"]["NavigationTreeDefaultTabTable2"]
            ),
            "LimitChars" => $GLOBALS["cfg"]["LimitChars"],
            "pftext" => $pftext,
            "confirm" => $GLOBALS["cfg"]["Confirm"],
            "LoginCookieValidity" => $GLOBALS["cfg"]["LoginCookieValidity"],
            "session_gc_maxlifetime" => (int) ini_get("session.gc_maxlifetime"),
            "logged_in" => isset($GLOBALS["dbi"]) ? $GLOBALS["dbi"].isUserType("logged") : false,
            "is_https" => $GLOBALS["PMA_Config"].isHttps(),
            "rootPath" => $GLOBALS["PMA_Config"].getRootPath(),
            "arg_separator" => Url.getArgSeparator(),
            "PMA_VERSION" => PMA_VERSION,
        ];
        if (isset($GLOBALS["cfg"]["Server"], $GLOBALS["cfg"]["Server"]["auth_type"])) {
            $params["auth_type"] = $GLOBALS["cfg"]["Server"]["auth_type"];
            if (isset($GLOBALS["cfg"]["Server"]["user"])) {
                $params["user"] = $GLOBALS["cfg"]["Server"]["user"];
            }
        }

        return $params;*/
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
        for (String entry: params.entrySet()) {
            if (entry.getValue() instanceof Boolean) {
                $params.put(entry.getKey(), entry.getKey() + ":" + (((Boolean)entry.getValue()) ? "true" : "false") + "");
            } else {
                $params.put(entry.getKey(), entry.getKey() + ":"" + Sanitize.escapeJsString($value) + """);
            }
        }
        return "CommonParams.setAll({" + implode(",", $params) + "});";
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
        this._console.setAjax($isAjax);
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
        this._console.disable();
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
        this.setTitle(__("Print view") + " - phpMyAdmin " + GLOBALS.PMA_VERSION);
        this._isPrintView = true;
    }

    /**
     * Generates the header
     *
     * @return String The header
     */
    public Map<String, Object> getDisplay()
    {
        if (! this._headerIsSent) {
            if (! this._isAjax && this._isEnabled) {
                this.sendHttpHeaders();

                String $baseDir = GLOBALS.PMA_PATH_TO_BASEDIR;
                String $uniqueValue = GLOBALS.PMA_Config.getThemeUniqueValue();
                String $themePath = GLOBALS.pmaThemePath;
                String $version = getVersionParameter();

                // The user preferences have been merged at this point
                // so we can conditionally add CodeMirror
                if ($GLOBALS["cfg"]["CodemirrorEnable"]) {
                    this._scripts.addFile("vendor/codemirror/lib/codemirror.js");
                    this._scripts.addFile("vendor/codemirror/mode/sql/sql.js");
                    this._scripts.addFile("vendor/codemirror/addon/runmode/runmode.js");
                    this._scripts.addFile("vendor/codemirror/addon/hint/show-hint.js");
                    this._scripts.addFile("vendor/codemirror/addon/hint/sql-hint.js");
                    if ($GLOBALS["cfg"]["LintEnable"]) {
                        this._scripts.addFile("vendor/codemirror/addon/lint/lint.js");
                        this._scripts.addFile(
                            "codemirror/addon/lint/sql-lint.js"
                        );
                    }
                }
                this._scripts.addCode(
                    "ConsoleEnterExecutes="
                    + ($GLOBALS["cfg"]["ConsoleEnterExecutes"] ? "true" : "false")
                );
                this._scripts.addFiles(this._console.getScripts());
                if (this._userprefsOfferImport) {
                    this._scripts.addFile("config.js");
                }

                if (this._menuEnabled && $GLOBALS["server"] > 0) {
                    $navigation = this.navigation.getDisplay();
                }

                $customHeader = Config.renderHeader();

                // offer to load user preferences from localStorage
                if (this._userprefsOfferImport) {
                    $loadUserPreferences = this.userPreferences.autoloadGetHeader();
                }

                if (this._menuEnabled && $GLOBALS["server"] > 0) {
                    $menu = this._menu.getDisplay();
                }
                $console = this._console.getDisplay();
                $messages = this.getMessage();
            }
            if (this._isEnabled && empty($_REQUEST["recent_table"])) {
                $recentTable = this._addRecentTable(
                    $GLOBALS["db"],
                    $GLOBALS["table"]
                );
            }
            
            Map<String, Object> model = new HashMap<>();
            model.put("is_ajax", this._isAjax);
            model.put("is_enabled", this._isEnabled);
            model.put("lang", GLOBALS.lang);
            model.put("allow_third_party_framing", GLOBALS.cfg.get("AllowThirdPartyFraming"));
            
            
            return this.template.render("header", [
                "is_ajax" => this._isAjax,
                "is_enabled" => this._isEnabled,
                "lang" => $GLOBALS["lang"],
                "allow_third_party_framing" => $GLOBALS["cfg"]["AllowThirdPartyFraming"],
                "is_print_view" => this._isPrintView,
                "base_dir" => $baseDir ?? "",
                "unique_value" => $uniqueValue ?? "",
                "theme_path" => $themePath ?? "",
                "version" => $version ?? "",
                "text_dir" => $GLOBALS["text_dir"],
                "server" => $GLOBALS["server"] ?? null,
                "title" => this.getPageTitle(),
                "scripts" => this._scripts.getDisplay(),
                "body_id" => this._bodyId,
                "navigation" => $navigation ?? "",
                "custom_header" => $customHeader ?? "",
                "load_user_preferences" => $loadUserPreferences ?? "",
                "show_hint" => $GLOBALS["cfg"]["ShowHint"],
                "is_warnings_enabled" => this._warningsEnabled,
                "is_menu_enabled" => this._menuEnabled,
                "menu" => $menu ?? "",
                "console" => $console ?? "",
                "messages" => $messages ?? "",
                "has_recent_table" => empty($_REQUEST["recent_table"]),
                "recent_table" => $recentTable ?? "",
            ]);
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
        if (! empty($GLOBALS["message"])) {
            $message = $GLOBALS["message"];
            unset($GLOBALS["message"]);
        } else if (! empty($_REQUEST["message"])) {
            $message = $_REQUEST["message"];
        }
        if (! empty($message)) {
            if (isset($GLOBALS["buffer_message"])) {
                $buffer_message = $GLOBALS["buffer_message"];
            }
            $retval += Util.getMessage($message);
            if (isset($buffer_message)) {
                $GLOBALS["buffer_message"] = $buffer_message;
            }
        }
        return $retval;
    }

    /**
     * Sends out the HTTP headers
     *
     * @return void
     */
    public void sendHttpHeaders()
    {
        if (defined("TESTSUITE")) {
            return;
        }
        $map_tile_urls = " *.tile.openstreetmap.org";

        /**
         * Sends http headers
         */
        $GLOBALS["now"] = gmdate("D, d M Y H:i:s") . " GMT";
        if (! empty($GLOBALS["cfg"]["CaptchaLoginPrivateKey"])
            && ! empty($GLOBALS["cfg"]["CaptchaLoginPublicKey"])
        ) {
            $captcha_url
                = " https://apis.google.com https://www.google.com/recaptcha/"
                . " https://www.gstatic.com/recaptcha/ https://ssl.gstatic.com/ ";
        } else {
            $captcha_url = "";
        }
        /* Prevent against ClickJacking by disabling framing */
        if (strtolower((String) $GLOBALS["cfg"]["AllowThirdPartyFraming"]) === "sameorigin") {
            header(
                "X-Frame-Options: SAMEORIGIN"
            );
        } else if ($GLOBALS["cfg"]["AllowThirdPartyFraming"] !== true) {
            header(
                "X-Frame-Options: DENY"
            );
        }
        header(
            "Referrer-Policy: no-referrer"
        );
        header(
            "Content-Security-Policy: default-src "self" "
            . $captcha_url
            . $GLOBALS["cfg"]["CSPAllow"] . ";"
            . "script-src "self" "unsafe-inline" "unsafe-eval" "
            . $captcha_url
            . $GLOBALS["cfg"]["CSPAllow"] . ";"
            . "style-src "self" "unsafe-inline" "
            . $captcha_url
            . $GLOBALS["cfg"]["CSPAllow"]
            . ";"
            . "img-src "self" data: "
            . $GLOBALS["cfg"]["CSPAllow"]
            . $map_tile_urls
            . $captcha_url
            . ";"
            . "object-src "none";"
        );
        header(
            "X-Content-Security-Policy: default-src "self" "
            . $captcha_url
            . $GLOBALS["cfg"]["CSPAllow"] . ";"
            . "options inline-script eval-script;"
            . "referrer no-referrer;"
            . "img-src "self" data: "
            . $GLOBALS["cfg"]["CSPAllow"]
            . $map_tile_urls
            . $captcha_url
            . ";"
            . "object-src "none";"
        );
        header(
            "X-WebKit-CSP: default-src "self" "
            . $captcha_url
            . $GLOBALS["cfg"]["CSPAllow"] . ";"
            . "script-src "self" "
            . $captcha_url
            . $GLOBALS["cfg"]["CSPAllow"]
            . " "unsafe-inline" "unsafe-eval";"
            . "referrer no-referrer;"
            . "style-src "self" "unsafe-inline" "
            . $captcha_url
            . ";"
            . "img-src "self" data: "
            . $GLOBALS["cfg"]["CSPAllow"]
            . $map_tile_urls
            . $captcha_url
            . ";"
            . "object-src "none";"
        );
        // Re-enable possible disabled XSS filters
        // see https://www.owasp.org/index.php/List_of_useful_HTTP_headers
        header(
            "X-XSS-Protection: 1; mode=block"
        );
        // "nosniff", prevents Internet Explorer and Google Chrome from MIME-sniffing
        // a response away from the declared content-type
        // see https://www.owasp.org/index.php/List_of_useful_HTTP_headers
        header(
            "X-Content-Type-Options: nosniff"
        );
        // Adobe cross-domain-policies
        // see https://www.adobe.com/devnet/articles/crossdomain_policy_file_spec.html
        header(
            "X-Permitted-Cross-Domain-Policies: none"
        );
        // Robots meta tag
        // see https://developers.google.com/webmasters/control-crawl-index/docs/robots_meta_tag
        header(
            "X-Robots-Tag: noindex, nofollow"
        );
        Core.noCacheHeader();
        if (! defined("IS_TRANSFORMATION_WRAPPER")) {
            // Define the charset to be used
            header("Content-Type: text/html; charset=utf-8");
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
        if ((empty(this._title)) {
            if ($GLOBALS["server"] > 0) {
                if (strlen($GLOBALS["table"])) {
                    $temp_title = $GLOBALS["cfg"]["TitleTable"];
                } else if (strlen($GLOBALS["db"])) {
                    $temp_title = $GLOBALS["cfg"]["TitleDatabase"];
                } else if (strlen($GLOBALS["cfg"]["Server"]["host"])) {
                    $temp_title = $GLOBALS["cfg"]["TitleServer"];
                } else {
                    $temp_title = $GLOBALS["cfg"]["TitleDefault"];
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
        $retval = "";
        if (this._menuEnabled
            && strlen($table) > 0
            && ((Integer)GLOBALS.cfg.get("NumRecentTables")) > 0
        ) {
            $tmp_result = RecentFavoriteTable.getInstance("recent").add(
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
        return $retval;
    }

    /**
     * Returns the phpMyAdmin version to be appended to the url to avoid caching
     * between versions
     *
     * @return String urlenocded pma version as a parameter
     */
    public static String getVersionParameter()
    {
        return "v=" + urlencode(GLOBALS.PMA_VERSION);
    }

}
