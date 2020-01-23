package org.javamyadmin.helpers;

import static org.javamyadmin.php.Php.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.servlet.http.HttpServletResponse;

import org.javamyadmin.php.Globals;

/**
 * This class includes various sanitization methods that can be called statically
 *
 * @package PhpMyAdmin
 */
public class Sanitize {


    /**
     * Checks whether given link is valid
     *
     * @param string  $url   URL to check
     * @param boolean $http  Whether to allow http links
     * @param boolean $other Whether to allow ftp and mailto links
     *
     * @return boolean True if string can be used as link
     */
    public static boolean checkLink(String $url, boolean $http /*= false*/, boolean $other /*= false*/)
    {
        $url = $url.toLowerCase();
        List<String> $valid_starts = Arrays.asList(new String[] {
            "https://",
            "./url.php?url=https%3a%2f%2f",
            "./doc/html/",
            "./index.php?",
        });
        boolean $is_setup = Globals.getConfig() != null && "true".equals(Globals.getConfig().get("is_setup"));
        // Adjust path to setup script location
        if ($is_setup) {
            /* TODO foreach ($valid_starts as $key => $value) {
                if (substr($value, 0, 2) === './') {
                    $valid_starts[$key] = '.' . $value;
                }
            }*/
        }
        if ($other) {
            $valid_starts.add("mailto:");
            $valid_starts.add("ftp://");
        }
        if ($http) {
            $valid_starts.add("http://");
        }
        if ($is_setup) {
            $valid_starts.add("?page=form&");
            $valid_starts.add("?page=servers&");
        }
        for (String $val : $valid_starts) {
            if ($url.startsWith($val)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean checkLink(String $url) {
    	return checkLink($url, false, false);
    }

    /**
     * Callback function for replacing [a@link@target] links in bb code.
     * @param request 
     * @param GLOBALS 
     *
     * @param array $found Array of preg matches
     *
     * @return string Replaced string
     */
    public static String replaceBBLink(String[] $found)
    {
        /* Check for valid link */
        if (! checkLink($found[1])) {
            return $found[0];
        }
        /* a-z and _ allowed in target */
        if (! empty($found[3]) && $found[3].matches("/[^a-z_]+/i")) {
            return $found[0];
        }

        /* Construct target */
        String $target = "";
        if (! empty($found[3])) {
            $target = " target='" + $found[3] + "'";
            if ("_blank".equals($found[3])) {
                $target += " rel='noopener noreferrer'";
            }
        }

        String $url;
        /* Construct url */
        if ($found[1].startsWith("http")) {
            $url = Core.linkURL($found[1]);
        } else {
            $url = $found[1];
        }

        return "<a href='" + $url + "'" + $target + ">";
    }

    /**
     * Sanitizes message, taking into account our special codes
     * for formatting.
     *
     * If you want to include result in element attribute, you should escape it.
     *
     * Examples:
     *
     * <p><?php echo Sanitize.sanitizeMessage(foo); ?></p>
     *
     * <a title="<?php echo Sanitize.sanitizeMessage(foo, true); ?>">bar</a>
     *
     * @param String  message the message
     * @param boolean escape  whether to escape html in result
     * @param boolean safe    whether String is safe (can keep < and > chars)
     *
     * @return String   the sanitized message
     */
    public static String sanitizeMessage(String message, boolean escape /*= false*/, boolean safe /*= false*/)
    {
    	if (message == null) {
    		message = "";
    	}
    	
        if (! safe) {
            message = message.replace("<", "&lt;").replace(">", "&gt;");
        }

        /* Interpret bb code */
        message = message.replace(    "[em]"      , "<em>");
        message = message.replace(    "[/em]"     , "</em>");
        message = message.replace(    "[strong]"  , "<strong>");
        message = message.replace(    "[/strong]" , "</strong>");
        message = message.replace(    "[code]"    , "<code>");
        message = message.replace(    "[/code]"   , "</code>");
        message = message.replace(    "[kbd]"     , "<kbd>");
        message = message.replace(    "[/kbd]"    , "</kbd>");
        message = message.replace(    "[br]"      , "<br>");
        message = message.replace(    "[/a]"      , "</a>");
        message = message.replace(    "[/doc]"      , "</a>");
        message = message.replace(    "[sup]"     , "<sup>");
        message = message.replace(    "[/sup]"    , "</sup>");
            // used in common.inc.php:
        message = message.replace(    "[conferr]" , "<iframe src='show_config_errors.php'><a href='show_config_errors.php'>show_config_errors.php</a></iframe>");
            // used in libraries/Util.php
        message = message.replace(    "[dochelpicon]" , Util.getImage("b_help", __("Documentation"), null));

        // Match links in bb code ([a@url@target], where @target is options) 
        String pattern = "/\\[a@([^]\"@]*)(@([^]\"]*))?\\]/";

        // Find and replace all links 
        message = preg_replace_callback(pattern, new Function<String[], String>() {

			@Override
			public String apply(String[] match) {
				return Sanitize.replaceBBLink(match);
			}
        }, message);

        // Replace documentation links 
        message = preg_replace_callback(
            "/\\[doc@([a-zA-Z0-9_-]+)(@([a-zA-Z0-9_-]*))?\\]/",
            new Function<String[], String>() {

				@Override
				public String apply(String[] match) {
					return ""; //Unsupported Sanitize.replaceDocLink(match);
				}
            },
            message
        );

        /* Possibly escape result */
        if (escape) {
            message = htmlspecialchars(message);
        }

        return message;
    }

	/**
     * Removes all variables from request except whitelisted ones.
     *
     * @param string[] $whitelist list of variables to allow
     *
     * @return possibly modified whitelist
     * @access public
     */
    public static String[] removeRequestVars(String[] $whitelist) {
		// TODO
    	// Really required ?!?
		return $whitelist;
	}

    /**
     * escapes a string to be inserted as string a JavaScript block
     * enclosed by <![CDATA[ ... ]]>
     * this requires only to escape ' with \' and end of script block
     *
     * We also remove NUL byte as some browsers (namely MSIE) ignore it and
     * inserting it anywhere inside </script would allow to bypass this check.
     *
     * @param string $string the string to be escaped
     *
     * @return string  the escaped string
     */
	public static String escapeJsString(String $string) {

		return $string == null ? null : $string.replace("\000", "")	//
				.replace("\\", "\\\\")	//
				.replace("'", "\\'")	//
				.replace("\"", "\\\"")	//
				.replace("\n", "\\n")	//
				.replace("\r", "\\r")	//
				.replaceAll("@</script@i", "</\" + \"script");
	}

    /**
     * Sanitize a filename by removing anything besides legit characters
     *
     * Intended usecase:
     *    When using a filename in a Content-Disposition header
     *    the value should not contain ; or "
     *
     *    When exporting, avoiding generation of an unexpected double-extension file
     *
     * @param string  $filename    The filename
     * @param boolean $replaceDots Whether to also replace dots
     *
     * @return string  the sanitized filename
     *
     */
	public static String sanitizeFilename(String $filename, boolean $replaceDots) {

        String $pattern = "/[^A-Za-z0-9_";
        // if we don't have to replace dots
        if (! $replaceDots) {
            // then add the dot to the list of legit characters
            $pattern += "\\.";
        }
        $pattern += "-]/";
        $filename = $filename.replaceAll($pattern, "_");
        return $filename;
	}
	
	public static String sanitizeFilename(String $filename) {
		return sanitizeFilename($filename, false);
	}

    /**
     * Formats a value for javascript code.
     *
     * @param string $value String to be formatted.
     *
     * @return string formatted value.
     */
    public static String formatJsVal(Object $value)
    {
        if ($value instanceof Boolean) {
            if ((Boolean)$value) {
                return "true";
            }

            return "false";
        }

        if (is_numeric($value)) {
            return $value.toString();
        }

        return '"' + escapeJsString($value.toString()) + '"';
    }

    /**
     * Formats an javascript assignment with proper escaping of a value
     * and support for assigning array of strings.
     *
     * @param string $key    Name of value to set
     * @param mixed  $value  Value to set, can be either string or array of strings
     * @param bool   $escape Whether to escape value or keep it as it is
     *                       (for inclusion of js code)
     *
     * @return string Javascript code.
     */
    @SuppressWarnings("rawtypes")
	public static String getJsValue(String $key, Object $value, boolean $escape /*= true*/)
    {
        String $result = $key + " = ";
        if (! $escape) {
            $result += $value;
        } else if ($value == null) {
        	 $result += "null";
        } else if ($value instanceof List) {
            $result += '[';
            for (Object $val : (List)$value) {
                $result += formatJsVal($val) + ",";
            }
            $result += "];\n";
        } else if ($value.getClass().isArray()) {
            $result += '[';
            for (Object $val : (Object[])$value) {
                $result += formatJsVal($val) + ",";
            }
            $result += "];\n";
        } else if ($value instanceof Map) {
            $result += '[';
            for (Object $val : ((Map)$value).values()) {
                $result += formatJsVal($val) + ",";
            }
            $result += "];\n";
        } else {
            $result += formatJsVal($value) + ";\n";
        }
        return $result;
    }
    
    public static String getJsValue(String $key, Object $value) {
    	return getJsValue($key, $value, true);
    }

	/**
     * Prints an javascript assignment with proper escaping of a value
     * and support for assigning array of strings.
     *
     * @param string $key   Name of value to set
     * @param mixed  $value Value to set, can be either string or array of strings
     *
     * @return void
	 * @throws IOException 
     */
    public static void printJsValue(String string, Object obj, HttpServletResponse response) throws IOException {
    	response.getWriter().write(getJsValue(string, obj));
	}

    /**
     * Formats javascript assignment for form validation api
     * with proper escaping of a value.
     *
     * @param string  $key   Name of value to set
     * @param string  $value Value to set
     * @param boolean $addOn Check if $.validator.format is required or not
     * @param boolean $comma Check if comma is required
     *
     * @return string Javascript code.
     */
    public static String getJsValueForFormValidation(String $key, String $value, boolean $addOn, boolean $comma)
    {
        String $result = $key + ": ";
        if ($addOn) {
            $result += "$.validator.format(";
        }
        $result += formatJsVal($value);
        if ($addOn) {
            $result += ')';
        }
        if ($comma) {
            $result += ", ";
        }
        return $result;
    }


    /**
     * Prints javascript assignment for form validation api
     * with proper escaping of a value.
     *
     * @param string  $key   Name of value to set
     * @param string  $value Value to set
     * @param boolean $addOn Check if $.validator.format is required or not
     * @param boolean $comma Check if comma is required
     *
     * @return void
     * @throws IOException 
     */
    public static void printJsValueForFormValidation(String $key, String $value, boolean $addOn /*= false*/, boolean $comma /*= true*/,
    		HttpServletResponse response) throws IOException
    {
    	response.getWriter().write(getJsValueForFormValidation($key, $value, $addOn, $comma));
    }

	public static void printJsValueForFormValidation(String $key, String $value, HttpServletResponse response) throws IOException {
		printJsValueForFormValidation($key, $value, false, true, response);
		
	}

	public static void printJsValueForFormValidation(String $key, String $value, boolean $addOn, HttpServletResponse response) throws IOException {
		printJsValueForFormValidation($key, $value, $addOn, true, response); 
	}
}
