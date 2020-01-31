package org.javamyadmin.helpers.navigation.nodes;

import org.javamyadmin.helpers.Url;
import org.javamyadmin.helpers.Util;
import org.javamyadmin.php.Array;
import org.javamyadmin.php.Globals;

import static org.javamyadmin.php.Php.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * Represents a columns node in the navigation tree
 *
 * @package PhpMyAdmin-Navigation
 */
public class NodeTable extends NodeDatabaseChild {
    /**
     * Initialises the class
     *
     * @param string $name    An identifier for the new node
     * @param int    $type    Type of node, may be one of CONTAINER or OBJECT
     * @param bool   $isGroup Whether this object has been created
     *                        while grouping nodes
     */
    public NodeTable(String $name, int $type /*= Node.OBJECT*/, boolean $isGroup /*= false*/, HttpServletRequest request, Globals GLOBALS)
    {
        super($name, $type, $isGroup, request, GLOBALS);
        this.icon = new ArrayList<>();
        this.addIcon(
            Util.getScriptNameForOption(
            	(String)Globals.getConfig().get("NavigationTreeDefaultTabTable"),
                "table", request, GLOBALS
            ), request, GLOBALS
        );
        this.addIcon(
            Util.getScriptNameForOption(
                (String)Globals.getConfig().get("NavigationTreeDefaultTabTable2"),
                "table", request, GLOBALS
            ), request, GLOBALS
        );
        String $title = Util.getTitleForTarget(
        	(String)Globals.getConfig().get("DefaultTabTable")
        );
        this.title = $title;

        String $scriptName = Util.getScriptNameForOption(
        	(String)Globals.getConfig().get("DefaultTabTable"),
            "table", request, GLOBALS
        );
        String $firstIconLink = Util.getScriptNameForOption(
        	(String)Globals.getConfig().get("NavigationTreeDefaultTabTable"),
            "table", request, GLOBALS
        );
        String $secondIconLink = Util.getScriptNameForOption(
            (String)Globals.getConfig().get("NavigationTreeDefaultTabTable2"),
            "table", request, GLOBALS
        );
        this.links = new HashMap<>();
        this.links.put(
            "text", $scriptName + (!$scriptName.contains("?") ? "?" : "&")
                + "server=" + GLOBALS.getServer()
                + "&amp;db=%2$s&amp;table=%1$s"
                + "&amp;pos=0");
        List<String> icons = new ArrayList<>();
        this.links.put("icon" , icons);
        icons.add($firstIconLink + (!$firstIconLink.contains("?") ? "?" : "&")
                + "server=" + GLOBALS.getServer()
                + "&amp;db=%2$s&amp;table=%1$s");
        icons.add($secondIconLink + (!$secondIconLink.contains("?") ? "?" : "&")
                + "server=" + GLOBALS.getServer()
                + "&amp;db=%2$s&amp;table=%1$s");
        this.links.put("title" , this.title);
        this.classes = "table";
    }

    /**
     * Returns the number of children of type $type present inside this container
     * This method is overridden by the PhpMyAdmin\Navigation\Nodes\NodeDatabase
     * and PhpMyAdmin\Navigation\Nodes\NodeTable classes
     *
     * @param string $type         The type of item we are looking for
     *                             ("columns" or "indexes")
     * @param string $searchClause A string used to filter the results of the query
     *
     * @return int
     * @throws SQLException 
     */
    @Override
    public int getPresence(String $type /*= ""*/, String $searchClause /*= ""*/, Globals GLOBALS) throws SQLException
    {
    	// Warning: $searchClause is ignored, both in PMA and JMA
        int $retval = 0;
        String $db = this.realParent().realName;
        String $table = this.realName;
        switch ($type) {
            case "columns":
                ResultSet rs = GLOBALS.getDbi().getColumns($db, $table);
                while(rs.next()) ++$retval;
                rs.close();
                break;
            case "indexes":
            	$retval = -1; // TODO
                break;
            case "triggers":
            	$retval = -1; // TODO
                break;
            default:
                break;
        }

        return $retval;
    }
    
    /**
     * Returns the names of children of type $type present inside this container
     * This method is overridden by the PhpMyAdmin\Navigation\Nodes\NodeDatabase
     * and PhpMyAdmin\Navigation\Nodes\NodeTable classes
     *
     * @param string $type         The type of item we are looking for
     *                             ("tables", "views", etc)
     * @param int    $pos          The offset of the list within the results
     * @param string $searchClause A string used to filter the results of the query
     *
     * @return array
     * @throws SQLException 
     */
    public Array getData(String $type, int $pos, String $searchClause /*= ""*/, Globals GLOBALS) throws SQLException
    {
        long $maxItems = !empty(Globals.getConfig().get("MaxNavigationItems")) ? new Long((String)Globals.getConfig().get("MaxNavigationItems")) : -1;
        Array $retval = new Array();
        String $db = this.realParent().realName;
        String $table = this.realName;
        switch ($type) {
            case "columns":
            	// FIXME we are ignoring $maxItems
            	ResultSet rs = GLOBALS.getDbi().getColumns($db, $table);
                $retval = GLOBALS.getDbi().fetchResult(rs);
                break;
            case "indexes":
                //TODO
                break;
            case "triggers":
                //TODO
                break;
            default:
                break;
        }

        return $retval;
    }

    /**
     * Returns the type of the item represented by the node.
     *
     * @return string type of the item
     */
    @Override
    protected String getItemType()
    {
        return "table";
    }

    /**
     * Add an icon to navigation tree
     * @param req 
     * @param GLOBALS 
     *
     * @param string $page Page name to redirect
     *
     * @return void
     */
    private void addIcon(String $page, HttpServletRequest req, Globals GLOBALS)
    {
        if (empty($page)) {
            return;
        }

        if ($page.equals(Url.getFromRoute("/table/structure", req, GLOBALS))) {
        	this.icon.add(Util.getImage("b_props", __("Structure")));
        } else if ($page.equals(Url.getFromRoute("/table/search", req, GLOBALS))) {
            this.icon.add(Util.getImage("b_search", __("Search")));
        } else if ($page.equals(Url.getFromRoute("/table/change", req, GLOBALS))) {
            this.icon.add(Util.getImage("b_insrow", __("Insert")));
        } else if ($page.equals(Url.getFromRoute("/table/sql", req, GLOBALS))) {
            this.icon.add( Util.getImage("b_sql", __("SQL")));
        } else if ($page.equals(Url.getFromRoute("/sql", req, GLOBALS))) {
            this.icon.add(Util.getImage("b_browse", __("Browse")));
        }
    }
}