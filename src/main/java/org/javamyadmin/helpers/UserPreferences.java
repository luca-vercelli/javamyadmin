package org.javamyadmin.helpers;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.javamyadmin.helpers.config.ConfigFile;
import org.javamyadmin.php.Globals;
import org.javamyadmin.php.Php.SessionMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.javamyadmin.php.Php.*;


/**
 * Functions for displaying user preferences pages
 *
 * @package PhpMyAdmin
 */
@Service
public class UserPreferences {
    /**
     * @var Relation
     */
    // TODO private Relation $relation;

    /**
     * @var Template
     */
	@Autowired
    public Template template;

	@Autowired
	private Core core;
    @Autowired
    private Url url;

    /**
     * Constructor
     */
    public UserPreferences()
    {
        //this.relation = new Relation($GLOBALS["dbi"]);
    }

    /**
     * Common initialization for user preferences modification pages
     *
     * @param ConfigFile $cf Config file instance
     *
     * @return void
     */
    public void pageInit(ConfigFile $cf)
    {
    	//TODO
    	/*
        $forms_all_keys = UserFormList.getFields();
        $cf.resetConfigData(); // start with a clean instance
        $cf.setAllowedKeys($forms_all_keys);
        $cf.setCfgUpdateReadMapping(
            [
                "Server/hide_db" => "Servers/1/hide_db",
                "Server/only_db" => "Servers/1/only_db",
            ]
        );
        $cf.updateWithGlobalConfig(Globals.getConfig());
        */
    }

    /**
     * Loads user preferences
     *
     * Returns an array:
     * * config_data - path => value pairs
     * * mtime - last modification time
     * * type - "db" (config read from pmadb) or "session" (read from user session)
     *
     * @return array
     */
    public Map load()
    {
    	return new HashMap<>();
    	/* TODO
        $cfgRelation = this.relation.getRelationsParam();
        if (! $cfgRelation["userconfigwork"]) {
            // no pmadb table, use session storage
            if (! isset($_SESSION["userconfig"])) {
                $_SESSION["userconfig"] = [
                    "db" => [],
                    "ts" => time(),
                ];
            }
            return [
                "config_data" => $_SESSION["userconfig"]["db"],
                "mtime" => $_SESSION["userconfig"]["ts"],
                "type" => "session",
            ];
        }
        // load configuration from pmadb
        $query_table = Util.backquote($cfgRelation["db"]) + "."
            + Util.backquote($cfgRelation["userconfig"]);
        $query = "SELECT `config_data`, UNIX_TIMESTAMP(`timevalue`) ts"
            + " FROM " + $query_table
            + " WHERE `username` = \""
            + $GLOBALS["dbi"].escapeString($cfgRelation["user"])
            + "\"";
        $row = $GLOBALS["dbi"].fetchSingleRow($query, "ASSOC", DatabaseInterface.CONNECT_CONTROL);

        return [
            "config_data" => $row ? json_decode($row["config_data"], true) : [],
            "mtime" => $row ? $row["ts"] : time(),
            "type" => "db",
        ];*/
    }

    /**
     * Saves user preferences
     *
     * @param array $config_array configuration array
     *
     * @return true|Message
     */
    public Message save(Map $config_array)
    {
    	return null; //TODO
    	/*
        $cfgRelation = this.relation.getRelationsParam();
        $server = isset($GLOBALS["server"])
            ? $GLOBALS["server"]
            : Globals.getConfig()["ServerDefault"];
        $cache_key = "server_" + $server;
        if (! $cfgRelation["userconfigwork"]) {
            // no pmadb table, use session storage
            $_SESSION["userconfig"] = [
                "db" => $config_array,
                "ts" => time(),
            ];
            if (isset($_SESSION["cache"][$cache_key]["userprefs"])) {
                unset($_SESSION["cache"][$cache_key]["userprefs"]);
            }
            return true;
        }

        // save configuration to pmadb
        $query_table = Util.backquote($cfgRelation["db"]) + "."
            + Util.backquote($cfgRelation["userconfig"]);
        $query = "SELECT `username` FROM " + $query_table
            + " WHERE `username` = \""
            + $GLOBALS["dbi"].escapeString($cfgRelation["user"])
            + "\"";

        $has_config = $GLOBALS["dbi"].fetchValue(
            $query,
            0,
            0,
            DatabaseInterface.CONNECT_CONTROL
        );
        $config_data = json_encode($config_array);
        if ($has_config) {
            $query = "UPDATE " + $query_table
                + " SET `timevalue` = NOW(), `config_data` = \""
                + $GLOBALS["dbi"].escapeString($config_data)
                + "\""
                + " WHERE `username` = \""
                + $GLOBALS["dbi"].escapeString($cfgRelation["user"])
                + "\"";
        } else {
            $query = "INSERT INTO " + $query_table
                + " (`username`, `timevalue`,`config_data`) "
                + "VALUES (\""
                + $GLOBALS["dbi"].escapeString($cfgRelation["user"]) + "\", NOW(), "
                + "\"" + $GLOBALS["dbi"].escapeString($config_data) + "\")";
        }
        if (isset($_SESSION["cache"][$cache_key]["userprefs"])) {
            unset($_SESSION["cache"][$cache_key]["userprefs"]);
        }
        if (! $GLOBALS["dbi"].tryQuery($query, DatabaseInterface.CONNECT_CONTROL)) {
            $message = Message.error(__("Could not save configuration"));
            $message.addMessage(
                Message.rawError(
                    $GLOBALS["dbi"].getError(DatabaseInterface.CONNECT_CONTROL)
                ),
                "<br><br>"
            );
            return $message;
        }
        return true;*/
    }

    /**
     * Returns a user preferences array filtered by $cfg["UserprefsDisallow"]
     * (blacklist) and keys from user preferences form (whitelist)
     *
     * @param array $config_data path => value pairs
     *
     * @return array
     */
    public Map apply(Map $config_data)
    {
    	return new HashMap();
    	/* TODO
        $cfg = [];
        $blacklist = array_flip(Globals.getConfig()["UserprefsDisallow"]);
        $whitelist = array_flip(UserFormList.getFields());
        // whitelist some additional fields which are custom handled
        $whitelist["ThemeDefault"] = true;
        $whitelist["lang"] = true;
        $whitelist["Server/hide_db"] = true;
        $whitelist["Server/only_db"] = true;
        $whitelist["2fa"] = true;
        foreach ($config_data as $path => $value) {
            if (! isset($whitelist[$path]) || isset($blacklist[$path])) {
                continue;
            }
            Core.arrayWrite($path, $cfg, $value);
        }
        return $cfg;*/
    }

    /**
     * Updates one user preferences option (loads and saves to database).
     *
     * No validation is done!
     *
     * @param String $path          configuration
     * @param mixed  $value         value
     * @param mixed  $default_value default value
     *
     * @return true|Message
     */
    public Message persistOption(String $path, Object $value, Object $default_value)
    {
    	return null;
    	/* TODO
        $prefs = this.load();
        if ($value.equals($default_value)) {
            if (isset($prefs["config_data"][$path])) {
                unset($prefs["config_data"][$path]);
            } else {
                return true;
            }
        } else {
            $prefs["config_data"][$path] = $value;
        }
        return this.save($prefs["config_data"]);*/
    }

    /**
     * Redirects after saving new user preferences
     * @param request 
     * @param GLOBALS 
     * @param response 
     *
     * @param String     $file_name Filename
     * @param array|null $params    URL parameters
     * @param String     $hash      Hash value
     *
     * @return void
     */
    public void redirect(
        String $file_name,
        Map<String, String> $params /*= null*/,
        String $hash, HttpServletRequest request, HttpServletResponse response, Globals GLOBALS
    ) {
    	Map<String, String> $url_params = new HashMap<>();
    	$url_params.put("saved", "1");
    	
        // redirect
        if ($params == null) {
        	$url_params.putAll($params);
        }
        
        if ($hash != null) {
            $hash = "#" + urlencode($hash);
        }
        core.sendHeaderLocation("./" + $file_name
            + url.getCommonRaw($url_params, ($file_name.contains( "?") ? "&" : "?") + $hash),
            false, request, response);
    }
    
    public void redirect(String $file_name, HttpServletRequest request, HttpServletResponse response, Globals GLOBALS) {
    	redirect($file_name, null, null, request, response, GLOBALS);
    }
    
    /**
     * Shows form which allows to quickly load
     * settings stored in browser"s local storage
     * @param $_SESSION 
     *
     * @return String
     */
    public String autoloadGetHeader(HttpServletRequest request, Globals GLOBALS, SessionMap $_SESSION)
    {
        if ("hide".equals(request.getParameter("prefs_autoload"))) {
            request.getSession().setAttribute("userprefs_autoload", true);
            return "";
        }

        String $script_name = Globals.get_PMA_PHP_SELF().getParentFile().getParent();
        
        String $return_url = $script_name + "?" + http_build_query($_REQUEST(request), "&");

        Map<String, Object> model = new HashMap<>();
        model.put("hidden_inputs", url.getHiddenInputs(request, GLOBALS, $_SESSION));
        model.put("return_url", $return_url);
        
        return template.render("preferences/autoload", model);
    }
}
