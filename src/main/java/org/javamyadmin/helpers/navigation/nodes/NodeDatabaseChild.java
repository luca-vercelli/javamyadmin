package org.javamyadmin.helpers.navigation.nodes;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.javamyadmin.helpers.Url;
import org.javamyadmin.helpers.Util;
import org.javamyadmin.php.Globals;

import static org.javamyadmin.php.Php.*;

/**
 * Represents a node that is a child of a database node
 * This may either be a concrete child such as table or a container
 * such as table container
 *
 * @package PhpMyAdmin-Navigation
 */
public abstract class NodeDatabaseChild extends Node {

	public NodeDatabaseChild(String $name, int $type, boolean $isGroup, HttpServletRequest req, Globals GLOBALS) {
		super($name, $type, $isGroup, req, GLOBALS);
	}
	
    public NodeDatabaseChild(String $name, HttpServletRequest req, Globals GLOBALS) {
		super($name, req, GLOBALS);
	}

	/**
     * Returns the type of the item represented by the node.
     *
     * @return string type of the item
     */
    abstract protected String getItemType();

    /**
     * Returns HTML for control buttons displayed infront of a node
     * @param httpRequest 
     * @param GLOBALS 
     *
     * @return String HTML for control buttons
     */
    public String getHtmlForControlButtons(HttpServletRequest httpRequest, Globals GLOBALS)
    {
        String $ret = "";
        //$cfgRelation = this.relation.getRelationsParam();
        // FIXME if ($cfgRelation["navwork"]) {
            String $db = this.realParent().realName;
            String $item = this.realName;

            Map<String, String> $params = new HashMap<>();
            $params.put("hideNavItem", "true");
            $params.put("itemType", this.getItemType());
            $params.put("itemName", $item);
            $params.put("dbName", $db);

            $ret = "<span class='navItemControls'>"
                + "<a href='" + Url.getFromRoute("/navigation", httpRequest, GLOBALS) + "' data-post='"
                + Url.getCommon($params, "", httpRequest, GLOBALS) + "'"
                + " class='hideNavItem ajax'>"
                + Util.getImage("hide", __("Hide"))
                + "</a></span>";
        // }

        return $ret;
    }
}
