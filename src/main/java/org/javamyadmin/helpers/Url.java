package org.javamyadmin.helpers;

import static org.javamyadmin.php.Php.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.javamyadmin.php.Globals;
import org.javamyadmin.php.Php.SessionMap;

/**
 * Static methods for URL/hidden inputs generating
 *
 * @package PhpMyAdmin
 */
public class Url {

	public static String getHiddenInputs(HttpServletRequest req, Globals GLOBALS, SessionMap $_SESSION) {
		return getHiddenInputs(null, null, 0, null, req, GLOBALS, $_SESSION);
	}
	
    /**
     * Generates text with hidden inputs.
     * @param req 
     *
     * @param string|array db     optional database name
     *                             (can also be an array of parameters)
     * @param string       table  optional table name
     * @param int          indent indenting level
     * @param string|array skip   do not generate a hidden field for this parameter
     *                             (can be an array of strings)
     *
     * @see Url.getCommon()
     *
     * @return string   string with input fields
     *
     * @access  public
     */
    public static String getHiddenInputs(
        String db /*= "" */,
        String table /*= "" */,
        int indent /*= 0 */,
        List<String> skip /*= [] */,
        HttpServletRequest req, Globals GLOBALS, SessionMap $_SESSION
    ) {
        /** @var Config PMA_Config */

    	Map<String, Object> params;
        /*if (is_array(db)) {
            params  =& db;
        } else*/ {
            params = new HashMap<>();
            if (! empty(db)) {
                params.put("db", db);
            }
            if (! empty(table)) {
                params.put("table", table);
            }
        }

        if (GLOBALS.server != null
            && GLOBALS.server != Globals.PMA_Config.get("ServerDefault")
        ) {
            params.put("server", GLOBALS.server);
        }
        if (empty(Globals.PMA_Config.getCookie("pma_lang", req)) && ! empty(GLOBALS.lang)) {
            params.put("lang", GLOBALS.lang);
        }

        if (skip != null) {
	        for (String skipping:skip ) {
	            params.remove(skipping);
	        }
        }

        return Url.getHiddenFields(params, "", false, $_SESSION);
    }

    /**
     * create hidden form fields from array with name => value
     *
     * <code>
     * values = array(
     *     "aaa" => aaa,
     *     "bbb" => array(
     *          "bbb_0",
     *          "bbb_1",
     *     ),
     *     "ccc" => array(
     *          "a" => "ccc_a",
     *          "b" => "ccc_b",
     *     ),
     * );
     * echo Url.getHiddenFields(values);
     *
     * // produces:
     * <input type="hidden" name="aaa" Value="aaa">
     * <input type="hidden" name="bbb[0]" Value="bbb_0">
     * <input type="hidden" name="bbb[1]" Value="bbb_1">
     * <input type="hidden" name="ccc[a]" Value="ccc_a">
     * <input type="hidden" name="ccc[b]" Value="ccc_b">
     * </code>
     *
     * @param array  values   hidden values
     * @param string pre      prefix
     * @param bool   is_token if token already added in hidden input field
     *
     * @return string form fields of type hidden
     */
    public static String getHiddenFields(Map<String,Object> values, String pre /*= ""*/, boolean is_token /*= false*/, SessionMap $_SESSION)
    {
        String fields = "";
        
        if (values == null) {
        	values = new HashMap<>();
        }

        /* Always include token in plain forms */
        if (!is_token) {
            values.put("token", $_SESSION.get(" PMA_token "));
        }

        for (String name : values.keySet()) {
        	Object value = values.get(name);
            if (value == null) {
            	value = "";
            }
            
            if (! empty(pre)) {
                name = pre + "[" + name + "]";
            }
            
            if (value instanceof Map) {
                fields += Url.getHiddenFields((Map)value, name, true, $_SESSION);
            } else {
                // do not generate an ending "\n" because
                // Url.getHiddenInputs() is sometimes called
                // from a JS document.write()
                fields += "<input type='hidden' name='" + htmlspecialchars(name)
                    + "' value='" + htmlspecialchars( value.toString()) + "'>";
            }
        }

        return fields;
    }

    public static String getHiddenFields(Map<String,Object> values, String pre, SessionMap $_SESSION) {
    	return getHiddenFields(values, pre, false, $_SESSION);
    }
    
    public static String getHiddenFields(Map<String,Object> values, SessionMap $_SESSION) {
    	return getHiddenFields(values, "", false, $_SESSION);
    }
    
    /**
     * Generates text with URL parameters.
     *
     * <code>
     * params["myparam"] = "myvalue";
     * params["db"]      = "mysql";
     * params["table"]   = "rights";
     * // note the missing ?
     * echo "script.php" . Url.getCommon(params);
     * // produces with cookies enabled:
     * // script.php?myparam=myvalue&amp;db=mysql&amp;table=rights
     * // with cookies disabled:
     * // script.php?server=1&amp;lang=en&amp;myparam=myvalue&amp;db=mysql
     * // &amp;table=rights
     *
     * // note the missing ?
     * echo "script.php" . Url.getCommon();
     * // produces with cookies enabled:
     * // script.php
     * // with cookies disabled:
     * // script.php?server=1&amp;lang=en
     * </code>
     * @param req 
     *
     * @param mixed  params  optional, Contains an associative array with url params
     * @param string divider optional character to use instead of "?"
     *
     * @return string   string with URL parameters
     * @access  public
     */
    public static String getCommon(Map<String, String> params /*= []*/, String divider /*= "?"*/, HttpServletRequest req, Globals GLOBALS)
    {
        return htmlspecialchars(
            Url.getCommonRaw(params, divider, req, GLOBALS)
        );
    }
    
    /**
     * Generates text with URL parameters.
     */
    public static String getCommon(Map<String, String> params, HttpServletRequest req, Globals GLOBALS) {
    	return getCommon(params, "?", req, GLOBALS);
    }

    /**
     * Generates text with URL parameters.
     *
     * <code>
     * params["myparam"] = "myvalue";
     * params["db"]      = "mysql";
     * params["table"]   = "rights";
     * // note the missing ?
     * echo "script.php" . Url.getCommon(params);
     * // produces with cookies enabled:
     * // script.php?myparam=myvalue&amp;db=mysql&amp;table=rights
     * // with cookies disabled:
     * // script.php?server=1&amp;lang=en&amp;myparam=myvalue&amp;db=mysql
     * // &amp;table=rights
     *
     * // note the missing ?
     * echo "script.php" . Url.getCommon();
     * // produces with cookies enabled:
     * // script.php
     * // with cookies disabled:
     * // script.php?server=1&amp;lang=en
     * </code>
     * @param req 
     *
     * @param mixed  params  optional, Contains an associative array with url params
     * @param string divider optional character to use instead of "?"
     *
     * @return string   string with URL parameters
     * @access  public
     */
    public static String getCommonRaw(Map<String, String> params /*= []*/, String divider /*= "?"*/, HttpServletRequest req, Globals GLOBALS)
    {
    	if (params == null) {
    		params = new HashMap<>();
    	}
    	
        /** @var Config PMA_Config */
        String separator = getArgSeparator("none");

        // avoid overwriting when creating navi panel links to servers
        if (GLOBALS.server != null
            && GLOBALS.server != Globals.PMA_Config.get("ServerDefault")
            && ! params.containsKey("server")
            && Globals.PMA_Config.get("is_setup").equals(false)
        ) {
            params.put("server", Integer.toString(GLOBALS.server));
        }

        if (empty(Globals.PMA_Config.getCookie("pma_lang", req)) && ! empty(GLOBALS.lang)) {
            params.put("lang", GLOBALS.lang);
        }

        String query = http_build_query(params, separator);

        if ((divider != "?" && divider != "&") || query.length() > 0) {
            return divider + query;
        }

        return "";
    }
    
    public static String getCommonRaw(HttpServletRequest req, Globals GLOBALS) {
    	return getCommonRaw(null, "?", req, GLOBALS);
    }
    
    private static String $separator = null;
    private static String $html_separator = null;
    
	/**
     * Returns url separator
     *
     * extracted from arg_separator.input as set in php.ini
     * we do not use arg_separator.output to avoid problems with &amp; and &
     *
     * @param string encode whether to encode separator or not,
     *                       currently "none" or "html"
     *
     * @return string  character used for separating url parts usually ; or &
     * @access  public
     */
    public static String getArgSeparator(String encode /*= "none"*/)
    {
        if ($separator == null) {
        	// Here. PhpMyAdmin uses separator defined in ini_get("arg_separator.input")
            $separator = ";";
            $html_separator = htmlentities($separator);
        }

        switch (encode) {
            case "html":
                return $html_separator;
            case "text":
            case "none":
            default:
                return $separator;
        }
    }

    public static String getArgSeparator() {
    	return getArgSeparator("none");
    }
    
    /**
     * @param req 
     * @param string route                Route to use
     * @param array  additionalParameters Additional URL parameters
     * @return string
     */
    public static String getFromRoute(String route, Map<String, String> additionalParameters /* = [] */, HttpServletRequest req, Globals GLOBALS)
    {
        return "index.php?route=" + route + getCommon(additionalParameters, "&", req, GLOBALS);
    }
    
    public static String getFromRoute(String route, HttpServletRequest req, Globals GLOBALS)
    {
        return getFromRoute(route, null, req, GLOBALS);
    }
}
