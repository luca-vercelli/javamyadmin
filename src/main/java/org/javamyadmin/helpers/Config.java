package org.javamyadmin.helpers;

import static org.javamyadmin.php.Php.E_FATAL;
import static org.javamyadmin.php.Php.E_USER_ERROR;
import static org.javamyadmin.php.Php.__;
import static org.javamyadmin.php.Php.array_replace_recursive;
import static org.javamyadmin.php.Php.empty;
import static org.javamyadmin.php.Php.is_numeric;
import static org.javamyadmin.php.Php.md5;
import static org.javamyadmin.php.Php.trigger_error;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.javamyadmin.php.GLOBALS;

public class Config {

    /**
     * @var String  default config source
     */
    public String default_source;

    /**
     * @var array   default configuration settings
     */
    public Map<String, Object> defaults =new HashMap<>();

    /**
     * @var array   configuration settings, without user preferences applied
     */
    public Map<String, Object> base_settings =new HashMap<>();

    /**
     * @var array   configuration settings
     */
    public Map<String, Object> settings =new HashMap<>();

    /**
     * @var String  config source
     */
    public String source = "";

    /**
     * @var int     source modification time
     */
    public long source_mtime = 0;
    public long default_source_mtime = 0;
    public long set_mtime = 0;

    /**
     * @var boolean
     */
    public boolean error_config_file = false;

    /**
     * @var boolean
     */
    public boolean error_config_default_file = false;

    /**
     * @var array
     */
    public Map default_server = new HashMap<>();

    /**
     * @var boolean whether init is done or not
     * set this to false to force some initial checks
     * like checking for required functions
     */
    public boolean done = false;
    
    /**
     * constructor
     *
     * @param String source source to read config from
     */
    public Config(String source /* may be null */)
    {
    	
    	this.default_source = GLOBALS.ROOT_PATH + "libraries/config.default.php";
    			
        this.settings.put("is_setup" , false);

        // functions need to refresh in case of config file changed goes in
        // PhpMyAdmin\Config.load()
        this.load(source);

        // other settings, independent from config file, comes in
        this.checkSystem();

        this.base_settings = this.settings;
    }

    /**
     * sets system and application settings
     *
     * @return void
     */
    public void checkSystem()
    {
        this.set("PMA_VERSION", "5.1.0-dev");
        /* Major version */
        this.set("PMA_MAJOR_VERSION", "5.1");

        this.checkWebServerOs();
        this.checkWebServer();
        this.checkGd2();
        this.checkClient();
        this.checkUpload();
        this.checkUploadSize();
        this.checkOutputCompression();
    }

    /**
     * whether to use gzip output compression or not
     *
     * @return void
     */
    public void checkOutputCompression()
    {
    	this.set("OBGzip", false);
    }

    /**
     * Sets the client platform based on user agent
     *
     * @param String user_agent the user agent
     *
     * @return void
     */
    private void _setClientPlatform(String user_agent)
    {
        if (user_agent.contains("Win")) {
            this.set("PMA_USR_OS", "Win");
        } else if (user_agent.contains("Mac")) {
            this.set("PMA_USR_OS", "Mac");
        } else if (user_agent.contains("Linux")) {
            this.set("PMA_USR_OS", "Linux");
        } else if (user_agent.contains("Unix")) {
            this.set("PMA_USR_OS", "Unix");
        } else if (user_agent.contains("OS/2")) {
            this.set("PMA_USR_OS", "OS/2");
        } else {
            this.set("PMA_USR_OS", "Other");
        }
    }

    /**
     * Determines platform (OS), browser and version of the user
     * Based on a phpBuilder article:
     *
     * @see http://www.phpbuilder.net/columns/tim20000821.php
     *
     * @return void
     */
    public void checkClient(/*HttpServletRequest req*/)
    {
    	/*String HTTP_USER_AGENT;
        if (Core.getenv("HTTP_USER_AGENT")) {
            HTTP_USER_AGENT = req.getHeader("User-Agent");
        } else {
            HTTP_USER_AGENT = "";
        }

        // 1. Platform
        this._setClientPlatform(HTTP_USER_AGENT);

        // 2. browser and version
        // (must check everything else before Mozilla)

        boolean is_mozilla = preg_match(
            "@Mozilla/([0-9]\\.[0-9]{1,2})@",
            HTTP_USER_AGENT,
            mozilla_version
        );

        if (preg_match(
            "@Opera(/| )([0-9]\\.[0-9]{1,2})@",
            HTTP_USER_AGENT,
            log_version
        )) {
            this.set("PMA_USR_BROWSER_VER", log_version[2]);
            this.set("PMA_USR_BROWSER_AGENT", "OPERA");
        } else if (preg_match(
            "@(MS)?IE ([0-9]{1,2}\\.[0-9]{1,2})@",
            HTTP_USER_AGENT,
            log_version
        )) {
            this.set("PMA_USR_BROWSER_VER", log_version[2]);
            this.set("PMA_USR_BROWSER_AGENT", "IE");
        } else if (preg_match(
            "@Trident/(7)\\.0@",
            HTTP_USER_AGENT,
            log_version
        )) {
            this.set("PMA_USR_BROWSER_VER", intval(log_version[1]) + 4);
            this.set("PMA_USR_BROWSER_AGENT", "IE");
        } else if (preg_match(
            "@OmniWeb/([0-9]{1,3})@",
            HTTP_USER_AGENT,
            log_version
        )) {
            this.set("PMA_USR_BROWSER_VER", log_version[1]);
            this.set("PMA_USR_BROWSER_AGENT", "OMNIWEB");
            // Konqueror 2.2.2 says Konqueror/2.2.2
            // Konqueror 3.0.3 says Konqueror/3
        } else if (preg_match(
            "@(Konqueror/)(.*)(;)@",
            HTTP_USER_AGENT,
            log_version
        )) {
            this.set("PMA_USR_BROWSER_VER", log_version[2]);
            this.set("PMA_USR_BROWSER_AGENT", "KONQUEROR");
            // must check Chrome before Safari
        } else if (is_mozilla
            && preg_match("@Chrome/([0-9.]*)@", HTTP_USER_AGENT, log_version)
        ) {
            this.set("PMA_USR_BROWSER_VER", log_version[1]);
            this.set("PMA_USR_BROWSER_AGENT", "CHROME");
            // newer Safari
        } else if (is_mozilla
            && preg_match("@Version/(.*) Safari@", HTTP_USER_AGENT, log_version)
        ) {
            this.set(
                "PMA_USR_BROWSER_VER",
                log_version[1]
            );
            this.set("PMA_USR_BROWSER_AGENT", "SAFARI");
            // older Safari
        } else if (is_mozilla
            && preg_match("@Safari/([0-9]*)@", HTTP_USER_AGENT, log_version)
        ) {
            this.set(
                "PMA_USR_BROWSER_VER",
                mozilla_version[1] . "." . log_version[1]
            );
            this.set("PMA_USR_BROWSER_AGENT", "SAFARI");
            // Firefox
        } else if (! mb_strstr(HTTP_USER_AGENT, "compatible")
            && preg_match("@Firefox/([\\w.]+)@", HTTP_USER_AGENT, log_version)
        ) {
            this.set(
                "PMA_USR_BROWSER_VER",
                log_version[1]
            );
            this.set("PMA_USR_BROWSER_AGENT", "FIREFOX");
        } else if (preg_match("@rv:1\\.9(.*)Gecko@", HTTP_USER_AGENT)) {
            this.set("PMA_USR_BROWSER_VER", "1.9");
            this.set("PMA_USR_BROWSER_AGENT", "GECKO");
        } else if (is_mozilla) {
            this.set("PMA_USR_BROWSER_VER", mozilla_version[1]);
            this.set("PMA_USR_BROWSER_AGENT", "MOZILLA");
        } else {
            this.set("PMA_USR_BROWSER_VER", 0);
            this.set("PMA_USR_BROWSER_AGENT", "OTHER");
        }*/
        this.set("PMA_USR_BROWSER_VER", 0);
        this.set("PMA_USR_BROWSER_AGENT", "OTHER");
    }

    /**
     * Whether GD2 is present
     *
     * @return void
     */
    public void checkGd2()
    {
    	//unsupported
    	this.set("PMA_IS_GD2", 0);
    }

    /**
     * Whether the Web server php is running on is IIS
     *
     * @return void
     */
    public void checkWebServer()
    {
        // unsupported
        this.set("PMA_IS_IIS", 0);
    }

    /**
     * Whether the os php is running on is windows or not
     *
     * @return void
     */
    public void checkWebServerOs()
    {
        // Default to Unix or Equiv
        this.set("PMA_IS_WINDOWS", 0);
        // If PHP_OS is defined then continue
        String os = System.getProperty("os.name");
        if (os != null) {
            if (os.contains( "win") && !os.contains( "darwin")) {
                // Is it some version of Windows
                this.set("PMA_IS_WINDOWS", 1);
            } else if (os.contains( "OS/2")) {
                // Is it OS/2 (No file permissions like Windows)
                this.set("PMA_IS_WINDOWS", 1);
            }
        }
    }

    /**
     * detects if Git revision
     * @param String git_location (optional) verified git directory
     * @return boolean
     */
    public boolean isGitRevision(String git_location )
    {
    	return false; //not supported
    }

    /**
     * detects Git revision, if running inside repo
     *
     * @return void
     */
    public void checkGitRevision()
    {
    	// unsupported
    }

    /**
     * loads default values from default source
     *
     * @return boolean     success
     */
    public boolean loadDefaults()
    {
        Map<String, Object> cfg = new HashMap<>();
        if (! new File(this.default_source).exists()) {
            this.error_config_default_file = true;
            return false;
        }
       
        GLOBALS.pma_config_loading = true;
        String eval_result = "";
        // eval_result = include this.default_source; TODO
        GLOBALS.pma_config_loading = false;
       

        if ( empty(eval_result )) {
            this.error_config_default_file = true;
            return false;
        }

        this.default_source_mtime = new File(this.default_source).lastModified();

        this.default_server = ((List<Map>)cfg.get("Servers")).get(0);
        cfg.remove("Servers");

        this.defaults = cfg;
        this.settings = array_replace_recursive(this.settings, cfg);

        this.error_config_default_file = false;

        return true;
    }

    /**
     * loads configuration from source, usually the config file
     * should be called on object creation
     *
     * @param String source config file
     *
     * @return boolean
     */
    public boolean load(String source)
    {
        this.loadDefaults();

        if (null != source) {
            this.setSource(source);
        }

        if (! this.checkConfigSource()) {
            return false;
        }

        /**
         * Parses the configuration file, we throw away any errors or
         * output.
         */
        GLOBALS.pma_config_loading = true;
        String eval_result = "";
        //eval_result = include this.getSource(); TODO
        GLOBALS.pma_config_loading = false;
        
        //error_reporting(old_error_reporting);

        if (empty (eval_result ) ) {
            this.error_config_file = true;
        } else {
            this.error_config_file = false;
            this.source_mtime = new File(this.getSource()).lastModified();
        }

        /**
         * Ignore keys with / as we do not use these
         *
         * These can be confusing for user configuration layer as it
         * flatten array using / and thus don"t see difference between
         * cfg["Export/method"] and cfg["Export"]["method"], while rest
         * of thre code uses the setting only in latter form.
         *
         * This could be removed once we consistently handle both values
         * in the functional code as well.
         *
         * It could use array_filter(...ARRAY_FILTER_USE_KEY), but it"s not
         * supported on PHP 5.5 and HHVM.
         */
        /*matched_keys = array_filter(
            array_keys(cfg),
            void (key) {
                return strpos(key, "/") === false;
            }
        );

        cfg = array_intersect_key(cfg, array_flip(matched_keys));*/
        
        for (Object key: GLOBALS.cfg.keySet()) {
        	if (key instanceof String && ((String)key).contains("/")) {
        		GLOBALS.cfg.remove(key);
        	}
        }

        this.settings = array_replace_recursive(this.settings, (Map)GLOBALS.cfg);

        return true;
    }

    /**
     * Sets the connection collation
     *
     * @return void
     */
    private void _setConnectionCollation()
    {
    	throw new IllegalStateException("Not implemented");/*
        collation_connection = this.get("DefaultConnectionCollation");
        if (! empty(collation_connection)
            && collation_connection != GLOBALS["collation_connection"]
        ) {
            GLOBALS["dbi"].setCollation(collation_connection);
        }*/
    }

    /**
     * Loads user preferences and merges them with current config
     * must be called after control connection has been established
     *
     * @return void
     */
    public void loadUserPreferences()
    {
    	throw new IllegalStateException("Not implemented");/*
        userPreferences = new UserPreferences();
        // index.php should load these settings, so that phpmyadmin.css.php
        // will have everything available in session cache
        server = isset(GLOBALS["server"])
            ? GLOBALS["server"]
            : (! empty(GLOBALS["cfg"]["ServerDefault"])
                ? GLOBALS["cfg"]["ServerDefault"]
                : 0);
        cache_key = "server_" . server;
        if (server > 0 && ! defined("PMA_MINIMUM_COMMON")) {
            config_mtime = max(this.default_source_mtime, this.source_mtime);
            // cache user preferences, use database only when needed
            if (! isset(_SESSION["cache"][cache_key]["userprefs"])
                || _SESSION["cache"][cache_key]["config_mtime"] < config_mtime
            ) {
                prefs = userPreferences.load();
                _SESSION["cache"][cache_key]["userprefs"]
                    = userPreferences.apply(prefs["config_data"]);
                _SESSION["cache"][cache_key]["userprefs_mtime"] = prefs["mtime"];
                _SESSION["cache"][cache_key]["userprefs_type"] = prefs["type"];
                _SESSION["cache"][cache_key]["config_mtime"] = config_mtime;
            }
        } else if (server == 0
            || ! isset(_SESSION["cache"][cache_key]["userprefs"])
        ) {
            this.set("user_preferences", false);
            return;
        }
        config_data = _SESSION["cache"][cache_key]["userprefs"];
        // type is "db" or "session"
        this.set(
            "user_preferences",
            _SESSION["cache"][cache_key]["userprefs_type"]
        );
        this.set(
            "user_preferences_mtime",
            _SESSION["cache"][cache_key]["userprefs_mtime"]
        );

        // load config array
        this.settings = array_replace_recursive(this.settings, config_data);
        GLOBALS["cfg"] = array_replace_recursive(GLOBALS["cfg"], config_data);
        if (defined("PMA_MINIMUM_COMMON")) {
            return;
        }

        // settings below start really working on next page load, but
        // changes are made only in index.php so everything is set when
        // in frames

        // save theme
        // @var ThemeManager tmanager
        tmanager = ThemeManager.getInstance();
        if (tmanager.getThemeCookie() || isset(_REQUEST["set_theme"])) {
            if ((! isset(config_data["ThemeDefault"])
                && tmanager.theme.getId() != "original")
                || isset(config_data["ThemeDefault"])
                && config_data["ThemeDefault"] != tmanager.theme.getId()
            ) {
                // new theme was set in common.inc.php
                this.setUserValue(
                    null,
                    "ThemeDefault",
                    tmanager.theme.getId(),
                    "original"
                );
            }
        } else {
            // no cookie - read default from settings
            if (this.settings["ThemeDefault"] != tmanager.theme.getId()
                && tmanager.checkTheme(this.settings["ThemeDefault"])
            ) {
                tmanager.setActiveTheme(this.settings["ThemeDefault"]);
                tmanager.setThemeCookie();
            }
        }

        // save language
        if (this.issetCookie("pma_lang") || isset(_POST["lang"])) {
            if ((! isset(config_data["lang"])
                && GLOBALS["lang"] != "en")
                || isset(config_data["lang"])
                && GLOBALS["lang"] != config_data["lang"]
            ) {
                this.setUserValue(null, "lang", GLOBALS["lang"], "en");
            }
        } else {
            // read language from settings
            if (isset(config_data["lang"])) {
                language = LanguageManager.getInstance().getLanguage(
                    config_data["lang"]
                );
                if (language !== false) {
                    language.activate();
                    this.setCookie("pma_lang", language.getCode());
                }
            }
        }

        // set connection collation
        this._setConnectionCollation();*/
    }

    /**
     * Sets config value which is stored in user preferences (if available)
     * or in a cookie.
     *
     * If user preferences are not yet initialized, option is applied to
     * global config and added to a update queue, which is processed
     * by {@link loadUserPreferences()}
     *
     * @param String|null cookie_name   can be null
     * @param String      cfg_path      configuration path
     * @param mixed       new_cfg_value new value
     * @param mixed       default_value default value
     *
     * @return true|Message
     */
    public Message setUserValue(
        String cookie_name,
        String cfg_path,
        String new_cfg_value,
        String default_value 
    ) {
    	UserPreferences userPreferences = new UserPreferences();
    	Message result = null;
        // use permanent user preferences if possible
        Object prefs_type = this.get("user_preferences");
        if (prefs_type != null) {
            if (default_value == null) {
                default_value = (String) Core.arrayRead(cfg_path, this.defaults);
            }
            result = userPreferences.persistOption(cfg_path, new_cfg_value, default_value);
        }
        if (prefs_type != "db" && cookie_name != null) {
            // fall back to cookies
            if (default_value == null) {
                default_value = (String) Core.arrayRead(cfg_path, this.settings);
            }
            this.setCookie(cookie_name, new_cfg_value, default_value, null, false, null, null);
        }
        Core.arrayWrite(cfg_path, GLOBALS.cfg, new_cfg_value);
        Core.arrayWrite(cfg_path, this.settings, new_cfg_value);
        return result;
    }

    /**
     * Reads value stored by {@link setUserValue()}
     * @param req 
     * @param resp 
     *
     * @param String cookie_name cookie name
     * @param mixed  cfg_value   config value
     *
     * @return mixed
     */
    public Object getUserValue(String cookie_name, Object cfg_value, HttpServletRequest req, HttpServletResponse resp)
    {
        boolean cookie_exists = this.getCookie(cookie_name, req) != null;
        String prefs_type = (String) this.get("user_preferences");
        if ("db".equals(prefs_type)) {
            // permanent user preferences value exists, remove cookie
            if (cookie_exists) {
                this.removeCookie(cookie_name, req, resp);
            }
        } else if (cookie_exists) {
            return this.getCookie(cookie_name, req);
        }
        // return value from cfg array
        return cfg_value;
    }

    /**
     * set source
     *
     * @param String source source
     *
     * @return void
     */
    public void setSource(String source)
    {
        this.source = source.trim();
    }

    /**
     * check config source
     *
     * @return boolean whether source is valid or not
     */
    public boolean checkConfigSource()
    {
        if (this.getSource() == null) {
            // no configuration file set at all
            return false;
        }

        if (! new File(this.getSource()).exists()) {
            this.source_mtime = 0;
            return false;
        }

        if (! new File(this.getSource()).canRead()) {
            this.source_mtime = 0;
                trigger_error(
                    String.format(
                        __("Existing configuration file (%s) is not readable."),
                        this.getSource()
                    ),
                    E_FATAL
                );
                return false;
        }

        return true;
    }

    /**
     * verifies the permissions on config file (if asked by configuration)
     * (must be called after config.inc.php has been merged)
     *
     * @return void
     */
    public void checkPermissions()
    {
        // Not supported by Java ?!?
    	// Check for permissions (on platforms that support it):
    	/*
    	File source = new File(this.getSource());
        if (this.get("CheckConfigurationPermissions").equals("true") && source.exists()) {
            String perms = @fileperms(this.getSource());
            if (! (perms === false) && (perms & 2)) {
                // This check is normally done after loading configuration
                this.checkWebServerOs();
                if (this.get("PMA_IS_WINDOWS").equals("0")) {
                    this.source_mtime = 0;
                    Core.fatalError(
                        __(
                            "Wrong permissions on configuration file, "
                            + "should not be world writable!"
                        )
                    );
                }
            }
        }*/
    }

    /**
     * Checks for errors
     * (must be called after config.inc.php has been merged)
     * @param request 
     * @param response
	* @param GLOBALS GLOBALS 
     *
     * @return void
     */
    public void checkErrors(HttpServletRequest request, HttpServletResponse response, GLOBALS GLOBALS, Response pmaResponse)
    {
        if (this.error_config_default_file) {
            Core.fatalError(
                request, response, GLOBALS, pmaResponse, String.format(
                    __("Could not load default configuration from: %1s"),
                    this.default_source
                )
            );
        }

        if (this.error_config_file) {
            String error = "[strong]" + __("Failed to read configuration file!") + "[/strong]"
                + "[br][br]"
                + __(
                    "This usually means there is a syntax error in it, "
                    + "please check any errors shown below."
                )
                + "[br][br]"
                + "[conferr]";
            trigger_error(error, E_USER_ERROR);
        }
    }

    /**
     * returns specific config setting
     *
     * @param String setting config setting
     *
     * @return mixed value
     */
    public Object get(String setting)
    {
        return this.settings.get(setting);
    }

    /**
     * sets configuration variable
     *
     * @param String setting configuration option
     * @param mixed  value   new value for configuration option
     *
     * @return void
     */
    public void set(String setting, Object value)
    {
        if (!this.settings.containsKey(setting)
            || !this.settings.get(setting).equals(value)
        ) {
            this.settings.put(setting, value);
            this.set_mtime = new Date().getTime();
        }
    }

    /**
     * returns source for current config
     *
     * @return String  config source
     */
    public String getSource()
    {
        return this.source;
    }

    /**
     * returns a unique value to force a CSS reload if either the config
     * or the theme changes
     *
     * @return int Summary of unix timestamps, to be unique on theme parameters
     *             change
     */
    public String getThemeUniqueValue(GLOBALS GLOBALS)
    {
        return (
            "" + this.source_mtime +
            this.default_source_mtime +
            this.get("user_preferences_mtime") +
            GLOBALS.PMA_Theme.mtime_info +
            GLOBALS.PMA_Theme.filesize_info
        );
    }

    /**
     * checks if upload is enabled
     *
     * @return void
     */
    public boolean checkUpload()
    {
        /*if (! ini_get("file_uploads")) {
            this.set("enable_upload", false);
            return;
        }

        this.set("enable_upload", true);
        // if set "php_admin_value file_uploads Off" in httpd.conf
        // ini_get() also returns the String "Off" in this case:
        if ("off" == strtolower(ini_get("file_uploads"))) {
            this.set("enable_upload", false);
        }*/
    	return true; //TODO
    }

    /**
     * Maximum upload size as limited by PHP
     * Used with permission from Moodle (https://moodle.org/) by Martin Dougiamas
     *
     * this section generates max_upload_size in bytes
     *
     * @return void
     */
    public void checkUploadSize()
    {
        /*if (! filesize = ini_get("upload_max_filesize")) {
            filesize = "5M";
        }

        if (postsize = ini_get("post_max_size")) {
            this.set(
                "max_upload_size",
                min(Core.getRealSize(filesize), Core.getRealSize(postsize))
            );
        } else {
            this.set("max_upload_size", Core.getRealSize(filesize));
        }*/
    }

    /**
     * Checks if protocol is https
     *
     * This void checks if the https protocol on the active connection.
     *
     * @return boolean
     */
    public boolean isHttps()
    {
        if (null != this.get("is_https")) {
            return (Boolean)this.get("is_https");
        }

        String url = (String) this.get("PmaAbsoluteUri");

        boolean is_https = false;
        if (url != null && url.startsWith("https")) {
            is_https = true;
        }/* else if (strtolower(Core.getenv("HTTP_SCHEME")) == "https") {
            is_https = true;
        } else if (strtolower(Core.getenv("HTTPS")) == "on") {
            is_https = true;
        } else if (strtolower(substr(Core.getenv("REQUEST_URI"), 0, 6)) == "https:") {
            is_https = true;
        } else if (strtolower(Core.getenv("HTTP_HTTPS_FROM_LB")) == "on") {
            // A10 Networks load balancer
            is_https = true;
        } else if (strtolower(Core.getenv("HTTP_FRONT_END_HTTPS")) == "on") {
            is_https = true;
        } else if (strtolower(Core.getenv("HTTP_X_FORWARDED_PROTO")) == "https") {
            is_https = true;
        } else if (Core.getenv("SERVER_PORT") == 443) {
            is_https = true;
        }*/ //TODO

        this.set("is_https", is_https);

        return is_https;
    }

    /**
     * Get phpMyAdmin root path
     *
     * @return String
     */
    public String getRootPath(HttpServletRequest req)
    {
        /*String cookie_path = null;

        if (null != cookie_path && ! defined("TESTSUITE")) {
            return cookie_path;
        }

        url = this.get("PmaAbsoluteUri");

        if (! empty(url)) {
            path = parse_url(url, PHP_URL_PATH);
            if (! empty(path)) {
                if (substr(path, -1) != "/") {
                    return path . "/";
                }
                return path;
            }
        }

        parsed_url = parse_url(GLOBALS["PMA_PHP_SELF"]);

        parts = explode(
            "/",
            rtrim(str_replace("\\", "/", parsed_url["path"]), "/")
        );

        // Remove filename
        if (substr(parts[count(parts) - 1], -4) == ".php") {
            parts = array_slice(parts, 0, count(parts) - 1);
        }

        // Remove extra path from javascript calls
        if (defined("PMA_PATH_TO_BASEDIR")) {
            parts = array_slice(parts, 0, count(parts) - 1);
        }

        parts[] = "";

        return implode("/", parts);*/
    	return "/";
    }

    /**
     * enables backward compatibility
     *
     * @return void
     */
    public void enableBc()
    {
    	// unsupported
    }

    /**
     * removes cookie
     * @param resp 
     *
     * @param String cookieName name of cookie to remove
     *
     * @return boolean result of setcookie()
     */
    public boolean removeCookie(String cookieName, HttpServletRequest req, HttpServletResponse resp)
    {
        String httpCookieName = this.getCookieName(cookieName);

        Cookie c = new Cookie(httpCookieName, "");
        c.setDomain("");
        c.setMaxAge((int) (new Date().getTime() - 3600));
        c.setPath(this.getRootPath(req));
        c.setSecure(this.isHttps());
        resp.addCookie(c);
        return true;
    }

    /**
     * sets cookie if value is different from current cookie value,
     * or removes if value is equal to default
     *
     * @param String cookie   name of cookie to remove
     * @param mixed  value    new cookie value
     * @param String default  default value
     * @param int    validity validity of cookie in seconds (default is one month)
     * @param boolean   httponly whether cookie is only for HTTP (and not for scripts)
     *
     * @return boolean result of setcookie()
     */
    public boolean setCookie(
        String cookie,
        String value,
        String defaultval,
        Integer validity,
        boolean httponly,
        HttpServletRequest req,
        HttpServletResponse resp
    ) {
        if (value.length() > 0 && null != defaultval && value == defaultval
        ) {
            // default value is used
            if (this.issetCookie(cookie, req)) {
                // remove cookie
                return this.removeCookie(cookie, req, resp);
            }
            return false;
        }

        if (value.length() == 0 && this.issetCookie(cookie, req)) {
            // remove cookie, value is empty
            return this.removeCookie(cookie, req, resp);
        }

        String httpCookieName = this.getCookieName(cookie);

        if (! this.issetCookie(cookie, req) ||  this.getCookie(cookie, req) != value) {
            // set cookie with new value
            /* Calculate cookie validity */
            if (validity == null) {
                /* Valid for one month */
                validity = (int) (new Date().getTime() + 2592000);
            } else if (validity == 0) {
                /* Valid for session */
                validity = 0;
            } else {
                validity = (int) (new Date().getTime() + validity);
            }
            Cookie c = new Cookie(httpCookieName, value);
            c.setDomain("");
            c.setMaxAge(validity);
            c.setPath(this.getRootPath(req));
            c.setHttpOnly(httponly);
            c.setSecure(this.isHttps());
            resp.addCookie(c);
            return true;
        }

        // cookie has already value as value
        return true;
    }

	public boolean setCookie(String cookie, String value) {
		return setCookie(cookie, value, null, null, false, null, null);
	}

    /**
     * get cookie
     *
     * @param String cookieName The name of the cookie to get
     *
     * @return mixed result of getCookie()
     */
    public String getCookie(String cookieName, HttpServletRequest req)
    {
    	for (Cookie c: req.getCookies()) {
    		if (c.getName().equals(this.getCookieName(cookieName))) {
    			return c.getValue();
    		}
    	}
    	return null;
    }

    /**
     * Get the real cookie name
     *
     * @param String cookieName The name of the cookie
     * @return String
     */
    public String getCookieName(String cookieName)
    {
        return cookieName + ( (this.isHttps()) ? "_https" : "" );
    }

    /**
     * isset cookie
     *
     * @param String cookieName The name of the cookie to check
     *
     * @return boolean result of issetCookie()
     */
    public boolean issetCookie(String cookieName, HttpServletRequest req)
    {
    	for (Cookie c: req.getCookies()) {
    		if (c.getName().equals(this.getCookieName(cookieName))) {
    			return true;
    		}
    	}
    	return false;
    	
    }

    /**
     * Error handler to catch fatal errors when loading configuration
     * file
     *
     * @return void
     */
    public static void fatalErrorHandler()
    {
    	throw new IllegalStateException("Not implemented");
        /*if (! isset(GLOBALS["pma_config_loading"])
            || ! GLOBALS["pma_config_loading"]
        ) {
            return;
        }

        error = error_get_last();
        if (error === null) {
            return;
        }

        Core.fatalError(
            String.format(
                "Failed to load phpMyAdmin configuration (%s:%s): %s",
                Error.relPath(error["file"]),
                error["line"],
                error["message"]
            )
        );*/
    }

    /**
     * Wrapper for footer/header rendering
     *
     * @param String filename File to check and render
     * @param String id       Div ID
     *
     * @return String
     */
    private static String _renderCustom(String filename, String id)
    {
        String retval = "";
        if (new File(filename).exists()) {
            retval += "<div id='" + id + "'>";
            // TODO include filename;
            retval += "</div>";
        }
        return retval;
    }

    /**
     * Renders user configured footer
     *
     * @return String
     */
    public static String renderFooter()
    {
    	throw new IllegalArgumentException("Not implemented");
    	// return _renderCustom(CUSTOM_FOOTER_FILE, "pma_footer"); // cfr. vendors.properties
    }

    /**
     * Renders user configured footer
     *
     * @return String
     */
    public static String renderHeader()
    {
    	throw new IllegalArgumentException("Not implemented");
        //return _renderCustom(CUSTOM_HEADER_FILE, "pma_header"); // cfr. vendors.properties
    }

    /**
     * Returns temporary dir path
     *
     * @param String name Directory name
     *
     * @return String|null
     */
    public String getTempDir(String name)
    {
    	//naive..
        return System.getProperty("java.io.tmpdir");
    }

    /**
     * Returns temporary directory
     *
     * @return String|null
     */
    public String getUploadTempDir()
    {
    	//naive..
    	return System.getProperty("java.io.tmpdir");
    }

    /**
     * Selects server based on request parameters.
     *
     * @return integer
     */
    public int selectServer(HttpServletRequest req)
    {
        String request = req.getParameter("server") == null ? "0" : req.getParameter("server");
        Integer req_number = null;
        try {
        	req_number = Integer.parseInt(request);
        } catch (NumberFormatException ec) {
        	//NOP
        }
        
        /**
         * Lookup server by name
         * (see FAQ 4.8)
         */
        if (req_number == null) {
        	int i = 0;
            String serverToLower = request.toLowerCase();
            Map<String, Map<String, String>> servers = (Map<String, Map<String, String>>) this.settings.get("Servers");
            for (Map<String, String> server: servers.values()) {
                String verboseToLower = server.get("verbose").toLowerCase();
                if (server.get("host").equals(request)
                    || server.get("verbose").equals(request)
                    || verboseToLower.equals(serverToLower)
                    || md5(verboseToLower).equals(serverToLower)
                ) {
                	req_number = i;
                    break;
                }
                ++i;
            }
            if (req_number == null) {
            	req_number = 0;
            }
        }

        /**
         * If no server is selected, make sure that this.settings.get("Server") is empty (so
         * that nothing will work), and skip server authentication.
         * We do NOT exit here, but continue on without logging into any server.
         * This way, the welcome page will still come up (with no server info) and
         * present a choice of servers in the case that there are multiple servers
         * and "this.settings["ServerDefault"] = 0" is set.
         */
        String server;
        if (is_numeric(request) && ! empty(request) && ! empty(((Map) this.settings.get("Servers")).get("request"))) {
            server = request;
            this.settings.put("Server", ((Map) this.settings.get("Servers")).get(server));
        } else {
            if (! empty(((Map) this.settings.get("Servers")).get(this.settings.get("ServerDefault")))) {
                server = (String) this.settings.get("ServerDefault");
                this.settings.put("Server", ((Map) this.settings.get("Servers")).get(server));
            } else {
                server = "0";
                this.settings.put("Server", new HashMap<>());
            }
        }

        return new Integer(server);
    }

    /**
     * Checks whether Servers configuration is valid and possibly apply fixups.
     *
     * @return void
     */
    public void checkServers()
    {
        // Do we have some server?
        if (! (this.settings.containsKey("Servers"))) {
        	this.settings.put("Servers", new ArrayList<Integer>());
        }
        
        if ( ((List)this.settings.get("Servers")).size() == 0  ) {
        	((List)this.settings.get("Servers")).add(this.default_server);
        } 
        
        /* TODO
        else {
            // We have server(s) => apply default configuration
            new_servers = [];

            for (this.settings.get("Servers") as server_index => each_server) {
                // Detect wrong configuration
                if (! is_int(server_index) || server_index < 1) {
                    trigger_error(
                        String.format(__("Invalid server index: %s"), server_index),
                        E_USER_ERROR
                    );
                }

                each_server = array_merge(this.default_server, each_server);

                // Final solution to bug #582890
                // If we are using a socket connection
                // and there is nothing in the verbose server name
                // or the host field, then generate a name for the server
                // in the form of "Server 2", localized of course!
                if (empty(each_server["host"]) && empty(each_server["verbose"])) {
                    each_server["verbose"] = String.format(__("Server %d"), server_index);
                }

                new_servers[server_index] = each_server;
            }
            this.settings.get("Servers") = new_servers;
        }*/
    }

}
