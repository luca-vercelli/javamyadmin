package org.javamyadmin.helpers.navigation.nodes;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.javamyadmin.helpers.Url;
import org.javamyadmin.helpers.Util;
import org.javamyadmin.php.Globals;
import org.javamyadmin.springmvc.ApplicationContextProvider;

import static org.javamyadmin.php.Php.*;

/**
 * The Node is the building block for the collapsible navigation tree
 *
 * @package PhpMyAdmin-Navigation
 */
public class Node
{
    /**
     * @var int Defines a possible node type
     */
    public final static int CONTAINER = 0;
    /**
     * @var int Defines a possible node type
     */
    public final static int OBJECT = 1;
    /**
     * @var string A non-unique identifier for the node
     *             This may be trimmed when grouping nodes
     */
    public String name = "";
    /**
     * @var string A non-unique identifier for the node
     *             This will never change after being assigned
     */
    public String realName = "";
    /**
     * @var int May be one of CONTAINER or OBJECT
     */
    public int type = Node.OBJECT;
    /**
     * @var bool Whether this object has been created while grouping nodes
     *           Only relevant if the node is of type CONTAINER
     */
    public boolean isGroup;
    /**
     * @var bool Whether to add a 'display: none;' CSS
     *           rule to the node when rendering it
     */
    public boolean visible = false;
    /**
     * @var Node A reference to the parent object of
     *           this node, NULL for the root node.
     */
    public Node parent;
    /**
     * @var Node[] An array of Node objects that are
     *             direct children of this node
     */
    public List<Node> children = new ArrayList<>();
    /**
     * @var Mixed A string used to group nodes, or an array of strings
     *            Only relevant if the node is of type CONTAINER
     */
    public Object separator = "";
    /**
     * @var int How many time to recursively apply the grouping function
     *          Only relevant if the node is of type CONTAINER
     */
    public int separatorDepth = 1;
    /**
     * @var string|array An IMG tag, used when rendering the node, an array for NodeTabl
     */
    public List<String> icon = new ArrayList<>();
    /**
     * @var array An array of A tags, used when rendering the node
     *            The indexes in the array may be "icon" and "text"
     */
    public Map<String, Object> links;
    /**
     * @var string HTML title
     */
    public String title;
    /**
     * @var string Extra CSS classes for the node
     */
    public String classes = "";
    /**
     * @var bool Whether this node is a link for creating new objects
     */
    public boolean isNew = false;
    /**
     * @var int The position for the pagination of
     *          the branch at the second level of the tree
     */
    public int pos2 = 0;
    /**
     * @var int The position for the pagination of
     *          the branch at the third level of the tree
     */
    public int pos3 = 0;

    /**
     * @var Relation
     */
    // FIXME protected Relation relation;

    /**
     * @var string $displayName  display name for the navigation tree
     */
    public String displayName;

    protected Util util; //not really @Autowired
    protected Url url; //not really @Autowired
    
    /**
     * Initialises the class by setting the mandatory variables
     *
     * @param string $name    An identifier for the new node
     * @param int    $type    Type of node, may be one of CONTAINER or OBJECT
     * @param bool   $isGroup Whether this object has been created
     *                        while grouping nodes
     */
    public Node(String $name, int $type /*= Node.OBJECT*/, boolean $isGroup /*= false*/, HttpServletRequest req, Globals GLOBALS)
    {
        if ($name != null && !$name.isEmpty()) {
            this.name = $name;
            this.realName = $name;
        }
        if ($type == Node.CONTAINER) {
            this.type = Node.CONTAINER;
        }
        this.isGroup = $isGroup;
        //this.relation = new Relation(GLOBALS.getDbi());

    	this.util = ApplicationContextProvider.getApplicationContext().getBean(Util.class);
    	this.url = ApplicationContextProvider.getApplicationContext().getBean(Url.class);
    	
    }

    public Node(String $name, HttpServletRequest req, Globals GLOBALS) {
    	this($name, Node.OBJECT, false, req, GLOBALS);
    }
    
    /**
     * Adds a child node to this node
     *
     * @param Node $child A child node
     *
     * @return void
     */
    public void addChild(Node $child)
    {
        this.children.add($child);
        $child.parent = this;
    }

    /**
     * Returns a child node given it"s name
     *
     * @param string $name     The name of requested child
     * @param bool   $realName Whether to use the 'realName'
     *                         instead of 'name' in comparisons
     *
     * @return false|Node The requested child node or false,
     *                    if the requested node cannot be found
     */
    public Node getChild(String $name, boolean $realName /*= false*/)
    {
        if ($realName) {
            for (Node $child: this.children) {
                if ($name.equals($child.realName)) {
                    return $child;
                }
            }
        } else {
        	for (Node $child: this.children) {
        		if ($name.equals($child.name)) {
                    return $child;
                }
            }
        }

        return null;
    }
    
    public Node getChild(String $name) {
    	return getChild($name, false);
    }

    /**
     * Removes a child node from this node
     *
     * @param string $name The name of child to be removed
     *
     * @return void
     */
    public void removeChild(String $name)
    {
    	for (int i = 0; i < this.children.size(); ++i) {
    		Node $child =  this.children.get(i);
            if ($name.equals($child.name)) {
                this.children.remove(i);
                break;
            }
    	}
    }

    /**
     * Retrieves the parents for a node
     *
     * @param bool $self       Whether to include the Node itself in the results
     * @param bool $containers Whether to include nodes of type CONTAINER
     * @param bool $groups     Whether to include nodes which have $group == true
     *
     * @return array An array of parent Nodes
     */
    public List<Node> parents(boolean $self /*= false*/, boolean $containers /*= false*/, boolean $groups /*= false*/)
    {
    	List<Node> $parents = new ArrayList<>();
        if ((this.type != Node.CONTAINER || $containers)
            && (! this.isGroup || $groups)
        ) {
            $parents.add(this);
        }
        Node $parent = this.parent;
        while ($parent != null) {
            if (($parent.type != Node.CONTAINER || $containers)
                && (! $parent.isGroup || $groups)
            ) {
                $parents.add($parent);
            }
            $parent = $parent.parent;
        }

        return $parents;
    }
    
    public List<Node> parents(boolean $self /*= false*/, boolean $containers /*= false*/) {
    	return parents($self, $containers, false);
    }
    
    public List<Node> parents(boolean $self) {
    	return parents($self, false, false);
    }
    
    public List<Node> parents() {
    	return parents(false, false, false);
    }

    /**
     * Returns the actual parent of a node. If used twice on an index or columns
     * node, it will return the table and database nodes. The names of the returned
     * nodes can be used in SQL queries, etc...
     *
     * @return Node|false
     */
    public Node realParent()
    {
        List<Node> $retval = this.parents();
        if ($retval.size() == 0) {
            return null;
        }

        return $retval.get(0);
    }

    /**
     * This function checks if the node has children nodes associated with it
     *
     * @param bool $countEmptyContainers Whether to count empty child
     *                                   containers as valid children
     *
     * @return bool Whether the node has child nodes
     */
    public boolean hasChildren(boolean $countEmptyContainers /*= true*/)
    {
        boolean $retval = false;
        if ($countEmptyContainers) {
            if (this.children.size() > 0) {
                $retval = true;
            }
        } else {
            for (Node $child : this.children) {
                if ($child.type == Node.OBJECT || $child.hasChildren(false)) {
                    $retval = true;
                    break;
                }
            }
        }

        return $retval;
    }
    
    public boolean hasChildren() {
    	return hasChildren(true); 
    }

    /**
     * Returns true if the node has some siblings (other nodes on the same tree
     * level, in the same branch), false otherwise.
     * The only exception is for nodes on
     * the third level of the tree (columns and indexes), for which the function
     * always returns true. This is because we want to render the containers
     * for these nodes
     *
     * @return bool
     */
    public boolean hasSiblings()
    {
    	boolean $retval = false;
    	PathStructure $paths = this.getPaths();
        if ($paths.aPathClean.size() > 3) {
            return true;
        }

        for (Node $child : this.parent.children) {
            if ($child != this
                && ($child.type == Node.OBJECT || $child.hasChildren(false))
            ) {
                $retval = true;
                break;
            }
        }

        return $retval;
    }

    /**
     * Returns the number of child nodes that a node has associated with it
     *
     * @return int The number of children nodes
     */
    public int numChildren()
    {
        int $retval = 0;
        for (Node $child : this.children) {
            if ($child.type == Node.OBJECT) {
                $retval++;
            } else {
                $retval += $child.numChildren();
            }
        }

        return $retval;
    }

    public static class PathStructure {
    	public String aPath;
    	public List<String> aPathClean;
    	public String vPath;
    	public List<String> vPathClean;
    }
    
    /**
     * Returns the actual path and the virtual paths for a node
     * both as clean arrays and base64 encoded strings
     *
     * @return array
     */
    public PathStructure getPaths()
    {
    	PathStructure $retval = new PathStructure();
    	
    	List<String> $aPath = new ArrayList<>();
    	List<String> $aPathClean = new ArrayList<>();
        for (Node $parent : this.parents(true, true, false)) {
        	$aPath.add(base64_encode($parent.realName));
        	$aPathClean.add($parent.realName);
        }
        Collections.reverse($aPath);
        $retval.aPath = String.join(".", $aPath);
        Collections.reverse($aPathClean);
        $retval.aPathClean = $aPathClean;

        List<String> $vPath = new ArrayList<>();
        List<String> $vPathClean = new ArrayList<>();
        for (Node $parent : this.parents(true, true, true)) {
        	$vPath.add(base64_encode($parent.name));
        	$vPathClean.add($parent.name);
        }
        Collections.reverse($vPath);
        $retval.vPath = String.join(".", $vPath);
        Collections.reverse($vPathClean);
        $retval.vPathClean = $vPathClean;

        return $retval;
    }

    /**
     * Returns the names of children of type $type present inside this container
     * This method is overridden by the PhpMyAdmin\Navigation\Nodes\NodeDatabase and PhpMyAdmin\Navigation\Nodes\NodeTable classes
     *
     * @param string $type         The type of item we are looking for
     *                             ("tables", "views", etc)
     * @param int    $pos          The offset of the list within the results
     * @param string $searchClause A string used to filter the results of the query
     *
     * @return array
     */
    public List<String> getData(String $type, int $pos, String $searchClause /*= ""*/)
    {
    	return new ArrayList<>(); /* TODO ? 
        $maxItems = $GLOBALS["cfg"]["FirstLevelNavigationItems"];
        if (! $GLOBALS["cfg"]["NavigationTreeEnableGrouping"]
            || ! $GLOBALS["cfg"]["ShowDatabasesNavigationAsTree"]
        ) {
            if (isset($GLOBALS["cfg"]["Server"]["DisableIS"])
                && ! $GLOBALS["cfg"]["Server"]["DisableIS"]
            ) {
                $query = 'SELECT `SCHEMA_NAME` ';
                $query .= 'FROM `INFORMATION_SCHEMA`.`SCHEMATA` ';
                $query .= this.getWhereClause("SCHEMA_NAME", $searchClause);
                $query .= 'ORDER BY `SCHEMA_NAME` ';
                $query .= 'LIMIT $pos, $maxItems';
                $retval = $GLOBALS["dbi"].fetchResult($query);

                return $retval;
            }

            if ($GLOBALS["dbs_to_test"] === false) {
                $retval = [];
                $query = 'SHOW DATABASES ';
                $query .= this.getWhereClause("Database", $searchClause);
                $handle = $GLOBALS["dbi"].tryQuery($query);
                if ($handle === false) {
                    return $retval;
                }

                $count = 0;
                if (! $GLOBALS["dbi"].dataSeek($handle, $pos)) {
                    return $retval;
                }

                while ($arr = $GLOBALS["dbi"].fetchArray($handle)) {
                    if ($count < $maxItems) {
                        $retval[] = $arr[0];
                        $count++;
                    } else {
                        break;
                    }
                }

                return $retval;
            }

            $retval = [];
            $count = 0;
            foreach (this.getDatabasesToSearch($searchClause) as $db) {
                $query = 'SHOW DATABASES LIKE "' + $db + '"';
                $handle = $GLOBALS["dbi"].tryQuery($query);
                if ($handle === false) {
                    continue;
                }

                while ($arr = $GLOBALS["dbi"].fetchArray($handle)) {
                    if (this.isHideDb($arr[0])) {
                        continue;
                    }
                    if (in_array($arr[0], $retval)) {
                        continue;
                    }

                    if ($pos <= 0 && $count < $maxItems) {
                        $retval[] = $arr[0];
                        $count++;
                    }
                    $pos--;
                }
            }
            sort($retval);

            return $retval;
        }

        $dbSeparator = $GLOBALS["cfg"]["NavigationTreeDbSeparator"];
        if (isset($GLOBALS["cfg"]["Server"]["DisableIS"])
            && ! $GLOBALS["cfg"]["Server"]["DisableIS"]
        ) {
            $query = 'SELECT `SCHEMA_NAME` ';
            $query .= 'FROM `INFORMATION_SCHEMA`.`SCHEMATA`, ';
            $query .= '(';
            $query .= 'SELECT DB_first_level ';
            $query .= 'FROM ( ';
            $query .= 'SELECT DISTINCT SUBSTRING_INDEX(SCHEMA_NAME, ';
            $query .= '"' + $GLOBALS["dbi"].escapeString($dbSeparator) + '", 1) ';
            $query .= 'DB_first_level ';
            $query .= 'FROM INFORMATION_SCHEMA.SCHEMATA ';
            $query .= this.getWhereClause("SCHEMA_NAME", $searchClause);
            $query .= ') t ';
            $query .= 'ORDER BY DB_first_level ASC ';
            $query .= 'LIMIT $pos, $maxItems';
            $query .= ') t2 ';
            $query .= this.getWhereClause("SCHEMA_NAME", $searchClause);
            $query .= 'AND 1 = LOCATE(CONCAT(DB_first_level, ';
            $query .= '"' + $GLOBALS["dbi"].escapeString($dbSeparator) + '"), ';
            $query .= 'CONCAT(SCHEMA_NAME, ';
            $query .= '"' + $GLOBALS["dbi"].escapeString($dbSeparator) + '")) ';
            $query .= 'ORDER BY SCHEMA_NAME ASC';
            $retval = $GLOBALS["dbi"].fetchResult($query);

            return $retval;
        }

        if ($GLOBALS["dbs_to_test"] === false) {
            $query = 'SHOW DATABASES ';
            $query .= this.getWhereClause("Database", $searchClause);
            $handle = $GLOBALS["dbi"].tryQuery($query);
            $prefixes = [];
            if ($handle !== false) {
                $prefixMap = [];
                $total = $pos + $maxItems;
                while ($arr = $GLOBALS["dbi"].fetchArray($handle)) {
                    $prefix = strstr($arr[0], $dbSeparator, true);
                    if ($prefix === false) {
                        $prefix = $arr[0];
                    }
                    $prefixMap[$prefix] = 1;
                    if (count($prefixMap) == $total) {
                        break;
                    }
                }
                $prefixes = array_slice(array_keys($prefixMap), (int) $pos);
            }

            $query = 'SHOW DATABASES ';
            $query .= this.getWhereClause("Database", $searchClause);
            $query .= 'AND (';
            $subClauses = [];
            foreach ($prefixes as $prefix) {
                $subClauses[] = ' LOCATE("'
                    + $GLOBALS["dbi"].escapeString((string) $prefix) + $dbSeparator
                    + '", '
                    + 'CONCAT(`Database`, "' + $dbSeparator + '")) = 1 ';
            }
            $query .= implode('OR', $subClauses) + ')';
            $retval = $GLOBALS["dbi"].fetchResult($query);

            return $retval;
        }

        $retval = [];
        $prefixMap = [];
        $total = $pos + $maxItems;
        foreach (this.getDatabasesToSearch($searchClause) as $db) {
            $query = 'SHOW DATABASES LIKE "' + $db + '"';
            $handle = $GLOBALS["dbi"].tryQuery($query);
            if ($handle === false) {
                continue;
            }

            while ($arr = $GLOBALS["dbi"].fetchArray($handle)) {
                if (this.isHideDb($arr[0])) {
                    continue;
                }
                $prefix = strstr($arr[0], $dbSeparator, true);
                if ($prefix === false) {
                    $prefix = $arr[0];
                }
                $prefixMap[$prefix] = 1;
                if (count($prefixMap) == $total) {
                    break 2;
                }
            }
        }
        $prefixes = array_slice(array_keys($prefixMap), $pos);

        foreach (this.getDatabasesToSearch($searchClause) as $db) {
            $query = 'SHOW DATABASES LIKE "' + $db + '"';
            $handle = $GLOBALS["dbi"].tryQuery($query);
            if ($handle === false) {
                continue;
            }

            while ($arr = $GLOBALS["dbi"].fetchArray($handle)) {
                if (this.isHideDb($arr[0])) {
                    continue;
                }
                if (in_array($arr[0], $retval)) {
                    continue;
                }

                foreach ($prefixes as $prefix) {
                    $startsWith = strpos(
                        $arr[0] + $dbSeparator,
                        $prefix + $dbSeparator
                    ) === 0;
                    if ($startsWith) {
                        $retval[] = $arr[0];
                        break;
                    }
                }
            }
        }
        sort($retval);

        return $retval;*/
    }
    
    public List<String> getData(String $type, int $pos) {
    	return getData($type, $pos, "");
    }

    /**
     * Returns the number of children of type $type present inside this container
     * This method is overridden by the PhpMyAdmin\Navigation\Nodes\NodeDatabase and PhpMyAdmin\Navigation\Nodes\NodeTable classes
     *
     * @param string $type         The type of item we are looking for
     *                             ("tables", "views", etc)
     * @param string $searchClause A string used to filter the results of the query
     *
     * @return int
     * @throws SQLException 
     */
    public int getPresence(String $type /*= ""*/, String $searchClause /*= ""*/, Globals GLOBALS) throws SQLException
    {
    	return 0; /* TODO ?
        if (! $GLOBALS["cfg"]["NavigationTreeEnableGrouping"]
            || ! $GLOBALS["cfg"]["ShowDatabasesNavigationAsTree"]
        ) {
            if (isset($GLOBALS["cfg"]["Server"]["DisableIS"])
                && ! $GLOBALS["cfg"]["Server"]["DisableIS"]
            ) {
                $query = 'SELECT COUNT(*) ';
                $query .= 'FROM INFORMATION_SCHEMA.SCHEMATA ';
                $query .= this.getWhereClause("SCHEMA_NAME", $searchClause);
                $retval = (int) $GLOBALS["dbi"].fetchValue($query);

                return $retval;
            }

            if ($GLOBALS["dbs_to_test"] === false) {
                $query = 'SHOW DATABASES ';
                $query .= this.getWhereClause("Database", $searchClause);
                $retval = $GLOBALS["dbi"].numRows(
                    $GLOBALS["dbi"].tryQuery($query)
                );

                return $retval;
            }

            $retval = 0;
            foreach (this.getDatabasesToSearch($searchClause) as $db) {
                $query = 'SHOW DATABASES LIKE "' + $db + '"';
                $retval += $GLOBALS["dbi"].numRows(
                    $GLOBALS["dbi"].tryQuery($query)
                );
            }

            return $retval;
        }

        $dbSeparator = $GLOBALS["cfg"]["NavigationTreeDbSeparator"];
        if (! $GLOBALS["cfg"]["Server"]["DisableIS"]) {
            $query = 'SELECT COUNT(*) ';
            $query .= 'FROM ( ';
            $query .= 'SELECT DISTINCT SUBSTRING_INDEX(SCHEMA_NAME, ';
            $query .= '"$dbSeparator", 1) ';
            $query .= 'DB_first_level ';
            $query .= 'FROM INFORMATION_SCHEMA.SCHEMATA ';
            $query .= this.getWhereClause("SCHEMA_NAME", $searchClause);
            $query .= ') t ';
            $retval = (int) $GLOBALS["dbi"].fetchValue($query);

            return $retval;
        }

        if ($GLOBALS["dbs_to_test"] !== false) {
            $prefixMap = [];
            foreach (this.getDatabasesToSearch($searchClause) as $db) {
                $query = 'SHOW DATABASES LIKE "' + $db + '"';
                $handle = $GLOBALS["dbi"].tryQuery($query);
                if ($handle === false) {
                    continue;
                }

                while ($arr = $GLOBALS["dbi"].fetchArray($handle)) {
                    if (this.isHideDb($arr[0])) {
                        continue;
                    }
                    $prefix = strstr($arr[0], $dbSeparator, true);
                    if ($prefix === false) {
                        $prefix = $arr[0];
                    }
                    $prefixMap[$prefix] = 1;
                }
            }
            $retval = count($prefixMap);

            return $retval;
        }

        $prefixMap = [];
        $query = 'SHOW DATABASES ';
        $query .= this.getWhereClause("Database", $searchClause);
        $handle = $GLOBALS["dbi"].tryQuery($query);
        if ($handle !== false) {
            while ($arr = $GLOBALS["dbi"].fetchArray($handle)) {
                $prefix = strstr($arr[0], $dbSeparator, true);
                if ($prefix === false) {
                    $prefix = $arr[0];
                }
                $prefixMap[$prefix] = 1;
            }
        }
        $retval = count($prefixMap);

        return $retval;*/
    }


    public int getPresence(String $type /*= ""*/, Globals GLOBALS) throws SQLException  {
    	return getPresence($type, "", GLOBALS);
    }
    
    public int getPresence(Globals GLOBALS) throws SQLException {
    	return getPresence("", "", GLOBALS);
    }
    
    /**
     * Detemines whether a given database should be hidden according to "hide_db"
     *
     * @param string $db database name
     *
     * @return boolean whether to hide
     */
    private boolean isHideDb(String $db, Globals GLOBALS)
    {
    	String hideDb = (String) multiget(GLOBALS.getConfig().settings, "Server", "hide_db");
        return ! empty(hideDb)
            && $db.matches(hideDb);
    }

    /**
     * Get the list of databases for "SHOW DATABASES LIKE" queries.
     * If a search clause is set it gets the highest priority while only_db gets
     * the next priority. In case both are empty list of databases determined by
     * GRANTs are used
     *
     * @param string $searchClause search clause
     *
     * @return array array of databases
     */
    private List getDatabasesToSearch(String $searchClause)
    {
    	return null; /* TODO ?
        $databases = [];
        if (! empty($searchClause)) {
            $databases = [
                '%' + $GLOBALS["dbi"].escapeString($searchClause) + '%',
            ];
        } elseif (! empty($GLOBALS["cfg"]["Server"]["only_db"])) {
            $databases = $GLOBALS["cfg"]["Server"]["only_db"];
        } elseif (! empty($GLOBALS["dbs_to_test"])) {
            $databases = $GLOBALS["dbs_to_test"];
        }
        sort($databases);

        return $databases;*/
    }

    /**
     * Returns the WHERE clause depending on the $searchClause parameter
     * and the hide_db directive
     *
     * @param string $columnName   Column name of the column having database names
     * @param string $searchClause A string used to filter the results of the query
     *
     * @return string
     */
    private String getWhereClause(String $columnName, String $searchClause /*= ""*/)
    {
    	return ""; /* TODO ?
        $whereClause = 'WHERE TRUE ';
        if (! empty($searchClause)) {
            $whereClause .= 'AND ' + Util.backquote($columnName)
                + ' LIKE "%';
            $whereClause .= $GLOBALS["dbi"].escapeString($searchClause);
            $whereClause .= '%" ';
        }

        if (! empty($GLOBALS["cfg"]["Server"]["hide_db"])) {
            $whereClause .= 'AND ' + Util.backquote($columnName)
                + ' NOT REGEXP "'
                + $GLOBALS["dbi"].escapeString($GLOBALS["cfg"]["Server"]["hide_db"])
                + '" ';
        }

        if (! empty($GLOBALS["cfg"]["Server"]["only_db"])) {
            if (is_string($GLOBALS["cfg"]["Server"]["only_db"])) {
                $GLOBALS["cfg"]["Server"]["only_db"] = [
                    $GLOBALS["cfg"]["Server"]["only_db"],
                ];
            }
            $whereClause .= 'AND (';
            $subClauses = [];
            foreach ($GLOBALS["cfg"]["Server"]["only_db"] as $eachOnlyDb) {
                $subClauses[] = ' ' + Util.backquote($columnName)
                    + ' LIKE "'
                    + $GLOBALS["dbi"].escapeString($eachOnlyDb) + '" ';
            }
            $whereClause .= implode('OR', $subClauses) + ') ';
        }

        return $whereClause;*/
    }

    /**
     * Returns HTML for control buttons displayed infront of a node
     *
     * @return String HTML for control buttons
     */
    public String getHtmlForControlButtons()
    {
        return "";
    }

    /**
     * Returns CSS classes for a node
     *
     * @param boolean $match Whether the node matched loaded tree
     *
     * @return String with html classes.
     */
    public String getCssClasses(boolean $match, Globals GLOBALS)
    {
    	
        if ("false".equals(GLOBALS.getConfig().get("NavigationTreeEnableExpansion"))
        ) {
            return "";
        }

        String $result = "expander";

        if (this.isGroup || $match) {
            $result += " loaded";
        }
        if (this.type == Node.CONTAINER) {
            $result += " container";
        }

        return $result;
    }

    /**
     * Returns icon for the node
     *
     * @param boolean $match Whether the node matched loaded tree
     *
     * @return String with image name
     */
    public String getIcon(boolean $match, Globals GLOBALS)
    {	
        if ("false".equals(GLOBALS.getConfig().get("NavigationTreeEnableExpansion"))
        ) {
            return "";
        } else if ($match) {
            this.visible = true;

            return util.getImage("b_minus");
        }

        return util.getImage("b_plus", __("Expand/Collapse"));
    }

    /**
     * Gets the count of hidden elements for each database
     *
     * @return array|null array containing the count of hidden elements for each database
     */
    public Map<String, Integer> getNavigationHidingData()
    {
    	return null; /* TODO ?
        $cfgRelation = this.relation.getRelationsParam();
        if ($cfgRelation["navwork"]) {
            $navTable = Util.backquote($cfgRelation["db"])
                + '.' + Util.backquote(
                    $cfgRelation["navigationhiding"]
                );
            $sqlQuery = 'SELECT `db_name`, COUNT(*) AS `count` FROM ' + $navTable
                + ' WHERE `username`="'
                + $GLOBALS["dbi"].escapeString(
                    $GLOBALS["cfg"]["Server"]["user"]
                ) + '"'
                + ' GROUP BY `db_name`';
            $counts = $GLOBALS["dbi"].fetchResult(
                $sqlQuery,
                "db_name",
                "count",
                DatabaseInterface.CONNECT_CONTROL
            );

            return $counts;
        }

        return null;*/
    }
}

