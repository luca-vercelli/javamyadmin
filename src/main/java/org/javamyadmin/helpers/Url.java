package org.javamyadmin.helpers;

import static org.javamyadmin.php.Php.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.javamyadmin.php.GLOBALS;

public class Url {

	public static String getHiddenInputs(HttpServletRequest req, GLOBALS GLOBALS) {
		return getHiddenInputs(null, null, 0, null, req, GLOBALS);
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
        HttpServletRequest req, GLOBALS GLOBALS
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
            && GLOBALS.server != GLOBALS.cfg.get("ServerDefault")
        ) {
            params.put("server", GLOBALS.server);
        }
        if (empty(GLOBALS.PMA_Config.getCookie("pma_lang", req)) && ! empty(GLOBALS.lang)) {
            params.put("lang", GLOBALS.lang);
        }

        if (skip != null) {
	        for (String skipping:skip ) {
	            params.remove(skipping);
	        }
        }

        return Url.getHiddenFields(params, "", false, req.getSession());
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
    public static String getHiddenFields(Map<String,Object> values, String pre /*= ""*/, boolean is_token /*= false*/, HttpSession sess)
    {
        String fields = "";

        /* Always include token in plain forms */
        if (!is_token) {
            values.put("token", sess.getAttribute(" PMA_token "));
        }

        for (String name : values.keySet()) {
        	Object value = values.get(name);
            if (! empty(pre)) {
                name = pre + "[" + name + "]";
            }

            if (value instanceof Map) {
                fields += Url.getHiddenFields((Map)value, name, true, sess);
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
    public static String getCommon(Map<String, Object> params /*= []*/, String divider /*= "?"*/, HttpServletRequest req, GLOBALS GLOBALS)
    {
        return htmlspecialchars(
            Url.getCommonRaw(params, divider, req, GLOBALS)
        );
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
    public static String getCommonRaw(Map<String, Object> params /*= []*/, String divider /*= "?"*/, HttpServletRequest req, GLOBALS GLOBALS)
    {
    	if (params == null) {
    		params = new HashMap<>();
    	}
    	
        /** @var Config PMA_Config */
        String separator = getArgSeparator("none");

        // avoid overwriting when creating navi panel links to servers
        if (GLOBALS.server != null
            && GLOBALS.server != GLOBALS.cfg.get("ServerDefault")
            && ! params.containsKey("server")
            && GLOBALS.PMA_Config.get("is_setup").equals(false)
        ) {
            params.put("server", GLOBALS.server);
        }

        if (empty(GLOBALS.PMA_Config.getCookie("pma_lang", req)) && ! empty(GLOBALS.lang)) {
            params.put("lang", GLOBALS.lang);
        }

        String query = http_build_query(params, separator);

        if ((divider != "?" && divider != "&") || query.length() > 0) {
            return divider + query;
        }

        return "";
    }
    
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
    	return "&";
    	/*
        if (null === separator) {
            // use separators defined by php, but prefer ";"
            // as recommended by W3C
            // (see https://www.w3.org/TR/1999/REC-html401-19991224/appendix
            // /notes.html#h-B.2.2)
            arg_separator = ini_get("arg_separator.input");
            if (mb_strpos(arg_separator, ";") !== false) {
                separator = ";";
            } elseif (strlen(arg_separator) > 0) {
                separator = arg_separator[0];
            } else {
                separator = "&";
            }
            html_separator = htmlentities(separator);
        }

        switch (encode) {
            case "html":
                return html_separator;
            case "text":
            case "none":
            default:
                return separator;
        }*/
    }

    /**
     * @param req 
     * @param string route                Route to use
     * @param array  additionalParameters Additional URL parameters
     * @return string
     */
    public static String getFromRoute(String route, Map<String, Object> additionalParameters /* = [] */, HttpServletRequest req, GLOBALS GLOBALS)
    {
        return "index.php?route=" + route + getCommon(additionalParameters, "&", req, GLOBALS);
    }
}
