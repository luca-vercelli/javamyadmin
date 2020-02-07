package org.javamyadmin.helpers.navigation.nodes;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.javamyadmin.helpers.Url;
import org.javamyadmin.helpers.Util;
import org.javamyadmin.php.Globals;

import static org.javamyadmin.php.Php.*;

/**
 * Represents a database node in the navigation tree
 *
 * @package PhpMyAdmin-Navigation
 */
public class NodeDatabase extends Node {

    /**
     * The number of hidden items in this database
     *
     * @var int
     */
    protected int hiddenCount = 0;

    /**
     * Initialises the class
     * @param req 
     *
     * @param string $name    An identifier for the new node
     * @param int    $type    Type of node, may be one of CONTAINER or OBJECT
     * @param bool   $isGroup Whether this object has been created
     *                        while grouping nodes
     */
    public NodeDatabase(String $name, int $type /*= Node.OBJECT*/, boolean $isGroup /*= false*/, HttpServletRequest req, Globals GLOBALS)
    {
        super($name, $type, $isGroup, req, GLOBALS);
        this.icon.add(util.getImage(
            "s_db",
            __("Database operations")
        ));

        String $scriptName = util.getScriptNameForOption(
            (String)GLOBALS.getConfig().get("DefaultTabDatabase"),
            "database"
        );
        boolean $hasRoute = $scriptName.contains("?");
        this.links = new HashMap<>();
        this.links.put(
            "text", $scriptName + (!$hasRoute ? "?" : "&")
                + "server=" + GLOBALS.getServer()
                + "&amp;db=%1$s");
        this.links.put(
            "icon", Url.getFromRoute("/database/operations") + "&amp;server=" + GLOBALS.getServer()
                + "&amp;db=%1$s&amp;");
        this.links.put("title", __("Structure"));
        this.classes = "database";
    }

    /**
     * JMA: basic support for Catalog+Schema name
     * @return
     */
    private String getSchema() {
    	return this.realName.contains(".") ? this.realName.substring(this.realName.indexOf(".") + 1) : this.realName;
    }
    
    /**
     * JMA: basic support for Catalog+Schema name
     * @return
     */
    private String getCatalog() {
    	return this.realName.contains(".") ? this.realName.substring(0, this.realName.indexOf(".")) : null;
    }
    
    /**
     * Returns the number of children of type $type present inside this container
     * This method is overridden by the PhpMyAdmin\Navigation\Nodes\NodeDatabase
     * and PhpMyAdmin\Navigation\Nodes\NodeTable classes
     *
     * @param string  $type         The type of item we are looking for
     *                              ("tables", "views", etc)
     * @param string  $searchClause A string used to filter the results of
     *                              the query
     * @param boolean $singleItem   Whether to get presence of a single known
     *                              item or false in none
     *
     * @return int
     * @throws SQLException 
     */
    public int getPresence(String $type /*= ""*/, String $searchClause /*= ""*/, boolean $singleItem /*= false*/, Globals GLOBALS) throws SQLException
    {
        int $retval = 0;
        switch ($type) {
            case "tables":
                $retval = this.getTableCount($searchClause, $singleItem, GLOBALS);
                break;
            case "views":
                $retval = this.getViewCount($searchClause, $singleItem, GLOBALS);
                break;
            case "procedures":
                $retval = this.getProcedureCount($searchClause, $singleItem, GLOBALS);
                break;
            case "functions":
                $retval = this.getFunctionCount($searchClause, $singleItem, GLOBALS);
                break;
            case "events":
                $retval = this.getEventCount($searchClause, $singleItem, GLOBALS);
                break;
            default:
                break;
        }

        return $retval;
    }

    /**
     * Returns the number of tables or views present inside this database
     *
     * @param string  $which        tables|views
     * @param string  $searchClause A string used to filter the results of
     *                              the query
     * @param boolean $singleItem   Whether to get presence of a single known
     *                              item or false in none
     *
     * @return int
     * @throws SQLException 
     */
    private int getTableOrViewCount(String $which, String $searchClause, boolean $singleItem, Globals GLOBALS /* Connection would be enough */) throws SQLException
    {
        String $db = this.getSchema();
        String $catalog = this.getCatalog();
        
        String[] $types;
        if ($which == "tables") {
            $types = new String[] {"TABLE"};
        } else {
            $types = new String[] {"VIEW"};
        }
        if (!$singleItem) {
        	$searchClause = "%" + $searchClause + "%";
        }
        Connection connection = GLOBALS.getDbi().getLink();
        ResultSet rs = connection.getMetaData()
        		.getTables($catalog, $db, $searchClause, $types);
        
        int $retval = 0;
        while(rs.next()) {
        	++$retval;
        }
        
        return $retval;
    }

    /**
     * Returns the number of tables present inside this database
     *
     * @param string  $searchClause A string used to filter the results of
     *                              the query
     * @param boolean $singleItem   Whether to get presence of a single known
     *                              item or false in none
     *
     * @return int
     * @throws SQLException
     */
    private int getTableCount(String $searchClause, boolean $singleItem, Globals GLOBALS /* Connection would be enough */) throws SQLException
    {
        return this.getTableOrViewCount(
            "tables",
            $searchClause,
            $singleItem,
            GLOBALS
        );
    }

    /**
     * Returns the number of views present inside this database
     *
     * @param string  $searchClause A string used to filter the results of
     *                              the query
     * @param boolean $singleItem   Whether to get presence of a single known
     *                              item or false in none
     *
     * @return int
     * @throws SQLException 
     */
    private int getViewCount(String $searchClause, boolean $singleItem, Globals GLOBALS /* Connection would be enough */) throws SQLException
    {
        return this.getTableOrViewCount(
            "views",
            $searchClause,
            $singleItem,
            GLOBALS
        );
    }

    /**
     * Returns the number of procedures present inside this database
     *
     * @param string  $searchClause A string used to filter the results of
     *                              the query
     * @param boolean $singleItem   Whether to get presence of a single known
     *                              item or false in none
     *
     * @return int
     * @throws SQLException 
     */
    private int getProcedureCount(String $searchClause, boolean $singleItem, Globals GLOBALS) throws SQLException
    {
        String $db = this.getSchema();
        String $catalog = this.getCatalog();
        
        if (!$singleItem) {
        	$searchClause = "%" + $searchClause + "%";
        }
        Connection connection = GLOBALS.getDbi().getLink();
        ResultSet rs = connection.getMetaData()
        		.getProcedures($catalog, $db, $searchClause);
        
        int $retval = 0;
        while(rs.next()) {
        	++$retval;
        }
        
        return $retval;
    }
    
    /**
     * Returns the number of functions present inside this database
     *
     * @param string  $searchClause A string used to filter the results of
     *                              the query
     * @param boolean $singleItem   Whether to get presence of a single known
     *                              item or false in none
     *
     * @return int
     * @throws SQLException 
     */
    private int getFunctionCount(String $searchClause, boolean $singleItem, Globals GLOBALS) throws SQLException
    {
        String $db = this.getSchema();
        String $catalog = this.getCatalog();
        
        if (!$singleItem) {
        	$searchClause = "%" + $searchClause + "%";
        }
        Connection connection = GLOBALS.getDbi().getLink();
        ResultSet rs = connection.getMetaData()
        		.getFunctions($catalog, $db, $searchClause);
        
        int $retval = 0;
        while(rs.next()) {
        	++$retval;
        }
        
        return $retval;
    }

    /**
     * Returns the number of events present inside this database
     *
     * @param string  $searchClause A string used to filter the results of
     *                              the query
     * @param boolean $singleItem   Whether to get presence of a single known
     *                              item or false in none
     *
     * @return int
     */
    private int getEventCount(String $searchClause, boolean $singleItem, Globals GLOBALS)
    {
        return 0; //Unsupported
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
     */
    public List<String> getData(String $type, int $pos, String $searchClause /*= ""*/, Globals GLOBALS) throws SQLException
    {
    	List<String> $retval = new ArrayList<>();
        switch ($type) {
            case "tables":
                $retval = this.getTables($pos, $searchClause, GLOBALS);
                break;
            case "views":
                $retval = this.getViews($pos, $searchClause, GLOBALS);
                break;
            case "procedures":
                $retval = this.getProcedures($pos, $searchClause, GLOBALS);
                break;
            case "functions":
                $retval = this.getFunctions($pos, $searchClause, GLOBALS);
                break;
            case "events":
                $retval = this.getEvents($pos, $searchClause, GLOBALS);
                break;
            default:
                break;
        }

        // Remove hidden items so that they are not displayed in navigation tree
        // TODO $cfgRelation = this.relation.getRelationsParam();
        // if ($cfgRelation["navwork"]) {
            List<String> $hiddenItems = this.getHiddenItems($type.substring(0, -1));
            List<String> itemsToRemove = new ArrayList<>();
            for (String $item : $retval) {
                if ($hiddenItems.contains($item)) {
                	itemsToRemove.add($item);
                }
            }
            $retval.removeAll(itemsToRemove);
        // }

        return $retval;
    }

    /**
     * Return list of hidden items of given type
     *
     * @param string $type The type of items we are looking for
     *                     ("table", "function", "group", etc.)
     *
     * @return array Array containing hidden items of given type
     */
    public List<String> getHiddenItems(String $type)
    {
    	return new ArrayList<>(); /* TODO ?
        String $db = this.getSchema();
        $cfgRelation = this.relation.getRelationsParam();
        if (! $cfgRelation["navwork"]) {
            return [];
        }
        $navTable = util.backquote($cfgRelation["db"])
            + '.' + util.backquote($cfgRelation["navigationhiding"]);
        $sqlQuery = 'SELECT `item_name` FROM ' + $navTable
            + ' WHERE `username`="' + $cfgRelation["user"] + '"'
            + ' AND `item_type`="' + $type
            + '" AND `db_name`="' + $GLOBALS["dbi"].escapeString($db)
            + '"';
        $result = this.relation.queryAsControlUser($sqlQuery, false);
        $hiddenItems = [];
        if ($result) {
            while ($row = $GLOBALS["dbi"].fetchArray($result)) {
                $hiddenItems[] = $row[0];
            }
        }
        $GLOBALS["dbi"].freeResult($result);

        return $hiddenItems;*/
    }

    /**
     * Returns the list of tables or views inside this database
     *
     * @param string $which        tables|views
     * @param int    $pos          The offset of the list within the results
     * @param string $searchClause A string used to filter the results of the query
     *
     * @return array
     * @throws SQLException 
     */
    private List<String> getTablesOrViews(String $which, int $pos, String $searchClause, Globals GLOBALS) throws SQLException
    {
        String $db = this.getSchema();
        String $catalog = this.getCatalog();
        
        String[] $types;
        if ($which == "tables") {
            $types = new String[] {"TABLE"};
        } else if ($which == "views") {
            $types = new String[] {"VIEW"};
        } else {
        	throw new IllegalArgumentException("Invalid table type requested: " + $which);
        }
        
        Connection connection = GLOBALS.getDbi().getLink();
        ResultSet rs = connection.getMetaData()
        		.getTables($catalog, $db, $searchClause, $types);
        
        List<String> $retval = new ArrayList<>();
        while(rs.next()) {
        	$retval.add(rs.getString("TABLE_NAME"));
        }
        
        return $retval;
    }

    /**
     * Returns the list of tables inside this database
     *
     * @param int    $pos          The offset of the list within the results
     * @param string $searchClause A string used to filter the results of the query
     *
     * @return array
     * @throws SQLException 
     */
    private List<String> getTables(int $pos, String $searchClause, Globals GLOBALS) throws SQLException
    {
        return this.getTablesOrViews("tables", $pos, $searchClause, GLOBALS);
    }

    /**
     * Returns the list of views inside this database
     *
     * @param int    $pos          The offset of the list within the results
     * @param string $searchClause A string used to filter the results of the query
     *
     * @return array
     * @throws SQLException 
     */
    private List<String> getViews(int $pos, String $searchClause, Globals GLOBALS) throws SQLException
    {
        return this.getTablesOrViews("views", $pos, $searchClause, GLOBALS);
    }

    /**
     * Returns the list of procedures or functions inside this database
     *
     * @param string $routineType  PROCEDURE|FUNCTION
     * @param int    $pos          The offset of the list within the results
     * @param string $searchClause A string used to filter the results of the query
     *
     * @return array
     * @throws SQLException 
     */
    private List<String> getRoutines(String $routineType, int $pos, String $searchClause, Globals GLOBALS) throws SQLException
    {

        String $db = this.getSchema();
        String $catalog = this.getCatalog();
        
        Connection connection = GLOBALS.getDbi().getLink();
        List<String> $retval = new ArrayList<>();
        if ("PROCEDURE".equals($routineType)) {
        	ResultSet rs = connection.getMetaData().getProcedures($catalog, $db, $searchClause);
            while(rs.next()) {
            	$retval.add(rs.getString("PROCEDURE_NAME"));
            }
            rs.close();
        } else if("FUNCTION".equals($routineType)) {
        	ResultSet rs = connection.getMetaData().getFunctions($catalog, $db, $searchClause);
            while(rs.next()) {
            	$retval.add(rs.getString("FUNCTION_NAME"));
            }
            rs.close();
        } else {
        	throw new IllegalArgumentException("Invalid routine type requested: " + $routineType);
        }
        
        return $retval;
    }

    /**
     * Returns the list of procedures inside this database
     *
     * @param int    $pos          The offset of the list within the results
     * @param string $searchClause A string used to filter the results of the query
     *
     * @return array
     */
    private List<String> getProcedures(int $pos, String $searchClause, Globals GLOBALS) throws SQLException
    {
        return this.getRoutines("PROCEDURE", $pos, $searchClause, GLOBALS);
    }

    /**
     * Returns the list of functions inside this database
     *
     * @param int    $pos          The offset of the list within the results
     * @param string $searchClause A string used to filter the results of the query
     *
     * @return array
     */
    private List<String> getFunctions(int $pos, String $searchClause, Globals GLOBALS) throws SQLException
    {
        return this.getRoutines("FUNCTION", $pos, $searchClause, GLOBALS);
    }

    /**
     * Returns the list of events inside this database
     * @param gLOBALS 
     *
     * @param int    $pos          The offset of the list within the results
     * @param string $searchClause A string used to filter the results of the query
     *
     * @return array
     */
    private List<String> getEvents(int $pos, String $searchClause, Globals GLOBALS) throws SQLException
    {
        return new ArrayList<>(); //Unsupported
    }

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
        // $cfgRelation = this.relation.getRelationsParam();
        // if ($cfgRelation["navwork"]) {
            if (this.hiddenCount > 0) {
                Map<String, String> $params = new HashMap<>();
                $params.put("showUnhideDialog", "true");
                $params.put("dbName", this.realName);
                $ret = "<span class='dbItemControls'>"
                    + "<a href='" + Url.getFromRoute("/navigation") + "' data-post='"
                    + Url.getCommon($params, "") + "'"
                    + " class='showUnhide ajax'>"
                    + util.getImage(
                        "show",
                        __("Show hidden items")
                    )
                    + "</a></span>";
            }
        //}

        return $ret;
    }

    /**
     * Sets the number of hidden items in this database
     *
     * @param int $count hidden item count
     *
     * @return void
     */
    public void setHiddenCount(int $count)
    {
        this.hiddenCount = $count;
    }

    /**
     * Returns the number of hidden items in this database
     *
     * @return int hidden item count
     */
    public int getHiddenCount()
    {
        return this.hiddenCount;
    }

}
