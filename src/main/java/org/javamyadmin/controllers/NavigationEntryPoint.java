package org.javamyadmin.controllers;

import org.javamyadmin.helpers.DatabaseInterface;
import org.javamyadmin.helpers.Message;
import org.javamyadmin.helpers.Response;
import org.javamyadmin.helpers.Util;
import org.javamyadmin.helpers.config.PageSettings;
import org.javamyadmin.helpers.navigation.Navigation;
import org.javamyadmin.php.Globals;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.javamyadmin.php.Php.*;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

/**
 * The navigation panel - displays server, db and table selection tree
 * @see libraries/entry_points/navigation.php
 *
 */
@RestController()
public class NavigationEntryPoint {

	@Autowired
	HttpServletRequest httpRequest;
	@Autowired
	SessionMap $_SESSION;
	@Autowired
	Globals $GLOBALS;
	@Autowired
	Response $response;
	@Autowired
	DatabaseInterface $dbi;
	@Autowired
	Navigation $navigation;
	@Autowired
	BeanFactory beanFactory;

	@RequestMapping(value="/navigation", produces=MediaType.APPLICATION_JSON_VALUE)
	public void navigation() throws SQLException {
		if (! $response.isAjax()) {
		    $response.addHTML(
		        Message.error(
		            __("Fatal error: The navigation can only be accessed via AJAX")
		        )
		    );
		    return;
		}
		

		if (!empty(httpRequest.getParameter("getNaviSettings"))) {
		    $response.addJSON("message", PageSettings.getNaviSettings(beanFactory));
		    return;
		}
		
		if (!empty(httpRequest.getParameter("reload"))) {
		    Util.cacheSet("dbs_to_test", false, $GLOBALS, $_SESSION); // Empty database list cache, see #14252
		}
		
		// @var Relation $relation
		// TODO $relation = $containerBuilder.get("relation");
		// $cfgRelation = $relation.getRelationsParam();
		// if ($cfgRelation["navwork"]) {
		    if (!empty(httpRequest.getParameter("hideNavItem"))) {
		        if (! empty(httpRequest.getParameter("itemName"))
		            && ! empty(httpRequest.getParameter("itemType"))
		            && ! empty(httpRequest.getParameter("dbName"))
		        ) {
		            $navigation.hideNavigationItem(
		            	httpRequest.getParameter("itemName"),
						httpRequest.getParameter("itemType"),
						httpRequest.getParameter("dbName"),
		                (! empty(httpRequest.getParameter("tableName")) ? httpRequest.getParameter("tableName") : null), $GLOBALS
		            );
		        }
		        return;
		    }
		
		    if (!empty(httpRequest.getParameter("unhideNavItem"))) {
		        if (!empty(httpRequest.getParameter("itemName"))
		            && !empty(httpRequest.getParameter("itemType"))
		            && !empty(httpRequest.getParameter("dbName"))
		        ) {
		            $navigation.unhideNavigationItem(
		            	httpRequest.getParameter("itemName"),
						httpRequest.getParameter("itemType"),
						httpRequest.getParameter("dbName"),
		                (! empty(httpRequest.getParameter("tableName")) ? httpRequest.getParameter("tableName") : null)
		            );
		        }
		        return;
		    }
		
		    if (!empty(httpRequest.getParameter("showUnhideDialog"))) {
		        if (!empty(httpRequest.getParameter("dbName"))) {
		            $response.addJSON(
		                "message",
		                $navigation.getItemUnhideDialog(httpRequest.getParameter("dbName"))
		            );
		        }
		        return;
		    }
		// }
		
		// Do the magic
		$response.addJSON("message", $navigation.getDisplay(httpRequest, $_SESSION, $GLOBALS));
	}
}
