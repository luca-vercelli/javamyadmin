package org.javamyadmin.helpers.navigation.nodes;

import org.javamyadmin.php.Globals;

import static org.javamyadmin.php.Php.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.javamyadmin.helpers.Url;
import org.javamyadmin.helpers.Util;
import org.javamyadmin.helpers.navigation.NodeFactory;

/**
 * Represents a container for database nodes in the navigation tree
 *
 * @package PhpMyAdmin-Navigation
 */
public class NodeDatabaseContainer extends Node {
    /**
     * Initialises the class
     * @param req 
     *
     * @param string $name An identifier for the new node
     */
    public NodeDatabaseContainer(String $name, HttpServletRequest req, Globals GLOBALS)
    {
        super($name, Node.CONTAINER, false, req, GLOBALS);

        /* TODO ? $checkUserPrivileges = new CheckUserPrivileges($GLOBALS['dbi']);
        $checkUserPrivileges->getPrivileges();*/
        
        if (GLOBALS.getIsCreateDbPriv()
            && "true".equals(Globals.getConfig().get("ShowCreateDb"))
        ) {
            Node $new = NodeFactory.getInstance(
                "Node",
                _pgettext("Create new database", "New"), req, GLOBALS
            );
            $new.isNew = true;
            $new.icon = new ArrayList<>();
            $new.icon.add(Util.getImage("b_newdb", ""));
            
            Map<String, String> serverMap = new HashMap<>();
            serverMap.put("server", Integer.toString(GLOBALS.getServer()));
            $new.links.put("text", Url.getFromRoute("/server/databases", serverMap));
            $new.links.put("icon", Url.getFromRoute("/server/databases", serverMap));
            $new.classes = "new_database italics";
            this.addChild($new);
        }
    }
}
