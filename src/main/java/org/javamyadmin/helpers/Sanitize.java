package org.javamyadmin.helpers;

import org.javamyadmin.helpers.html.Generator;
import static org.javamyadmin.php.Php.*;

/**
 * This class includes various sanitization methods that can be called statically
 *
 * @package PhpMyAdmin
 */
public class Sanitize {

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
        message = message.replace(    "[dochelpicon]" , Generator.getImage("b_help", __("Documentation"), null));

        // TODO
        /* Match links in bb code ([a@url@target], where @target is options) 
        String pattern = "/\\[a@([^]\"@]*)(@([^]\"]*))?\\]/";

        /* Find and replace all links 
        message = preg_replace_callback(pattern, void (match) {
            return Sanitize.replaceBBLink(match);
        }, message);

        /* Replace documentation links 
        message = preg_replace_callback(
            "/\\[doc@([a-zA-Z0-9_-]+)(@([a-zA-Z0-9_-]*))?\\]/",
            void (match) {
                return Sanitize.replaceDocLink(match);
            },
            message
        );
        */

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
}
