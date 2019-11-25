package org.javamyadmin.helpers;

import org.javamyadmin.helpers.html.Generator;
import static org.javamyadmin.php.Php.*;

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
}
