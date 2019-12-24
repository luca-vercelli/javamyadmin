package org.javamyadmin.helpers.server;

import static org.javamyadmin.php.Php.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.javamyadmin.helpers.Url;
import org.javamyadmin.helpers.Util;
import org.javamyadmin.php.Globals;
import org.javamyadmin.php.Php.SessionMap;

/**
 * Displays the MySQL servers choice form
 *
 * @package PhpMyAdmin
 */
public class Select {
	
    /**
     * Renders the server selection in list or selectbox form, or option tags only
     * @param $_SESSION 
     * @param request 
     *
     * @param boolean $not_only_options whether to include form tags or not
     * @param boolean $omit_fieldset    whether to omit fieldset tag or not
     *
     * @return string
     */
    public static String render(boolean $not_only_options, boolean $omit_fieldset, Globals GLOBALS, SessionMap $_SESSION, HttpServletRequest request)
    {
        String $retval = "";

        // Show as list?
        boolean $list = false;
        if ($not_only_options) {
            $list = "true".equals(Globals.getConfig().get("DisplayServersList"));
            $not_only_options = ! $list;
        }

        if ($not_only_options) {
            $retval += "<form method='post' action='"
                + Util.getScriptNameForOption(
                	(String) Globals.getConfig().get("DefaultTabServer"),
                    "server", request, GLOBALS
                )
                + "' class='disableAjax'>";

            if (! $omit_fieldset) {
                $retval += "<fieldset>";
            }

            $retval += Url.getHiddenFields(null, $_SESSION);
            $retval += "<label for='select_server'>"
                + __("Current server:") + "</label> ";

            $retval += "<select name='server' id='select_server' class='autosubmit'>";
            $retval += "<option value=''>(" + __("Servers") + ") ...</option>" + '\n';
        } else if ($list) {
            $retval += __("Current server:") + "<br>";
            $retval += "<ul id='list_server'>";
        }

        Map<String, Object> servers = (Map<String, Object>) Globals.getConfig().get("Servers");
        for (Entry<String, Object> entry : servers.entrySet()) {
        	String $key = entry.getKey();
        	Map $server = (Map) entry.getValue();
            if (empty($server.get("host"))) {
                continue;
            }

            boolean $selected;
            if (GLOBALS.getServer() != null && GLOBALS.getServer().equals(new Integer($key))) {
                $selected = true;
            } else {
                $selected = false;
            }
            String $label; 
            if (! empty($server.get("verbose"))) {
                $label = (String) $server.get("verbose");
            } else {
                $label = (String) $server.get("host");
                if (! empty($server.get("port"))) {
                    $label += ":" + $server.get("port");
                }
            }
            if (! empty($server.get("only_db"))) {
                if (! is_array($server.get("only_db"))) {
                    $label += " - " + $server.get("only_db");
                    // try to avoid displaying a too wide selector
                } else if (((Map)$server.get("only_db")).size() < 4) {
                    $label += " - " + String.join(", ", (String)$server.get("only_db"));
                }
            }
            if (! empty($server.get("user")) && "config".equals($server.get("auth_type"))) {
                $label += "  (" + $server.get("user") + ")";
            }

            if ($list) {
                $retval += "<li>";
                if ($selected) {
                    $retval += "<strong>" + htmlspecialchars($label) + "</strong>";
                } else {
                    String $scriptName = Util.getScriptNameForOption(
                    	(String)Globals.getConfig().get("DefaultTabServer"),
                        "server", request, GLOBALS
                    );
                    Map<String, String> serverMap = new HashMap<>();
                    serverMap.put("server", $key);
                    
                    $retval += "<a class='disableAjax item' href='"
                        + $scriptName
                        + Url.getCommon(serverMap, $scriptName.contains("?") ? "&" : "?", request, GLOBALS)
                        + "' >" + htmlspecialchars($label) + "</a>";
                }
                $retval += "</li>";
            } else {
                $retval += "<option value='" + $key + "' "
                    + ($selected ? " selected='selected'" : "") + ">"
                    + htmlspecialchars($label) + "</option>" + '\n';
            }
        } // end while

        if ($not_only_options) {
            $retval += "</select>";
            if (! $omit_fieldset) {
                $retval += "</fieldset>";
            }
            $retval += "</form>";
        } else if ($list) {
            $retval += "</ul>";
        }

        return $retval;
    }

}
