package org.javamyadmin.helpers.navigation;

import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.javamyadmin.helpers.DatabaseInterface;
import org.javamyadmin.helpers.RecentFavoriteTable;
import org.javamyadmin.helpers.Response;
import org.javamyadmin.helpers.Template;
import org.javamyadmin.helpers.Url;
import org.javamyadmin.helpers.Util;
import org.javamyadmin.helpers.navigation.nodes.Node;
import org.javamyadmin.helpers.navigation.nodes.Node.PathStructure;
import org.javamyadmin.helpers.navigation.nodes.NodeDatabase;
import org.javamyadmin.helpers.navigation.nodes.NodeTable;
import org.javamyadmin.php.Globals;

import static org.javamyadmin.php.Php.*;

/**
 * Displays a collapsible of database objects in the navigation frame
 *
 * @package PhpMyAdmin-Navigation
 */
public class NavigationTree {
    /**
     * @var Node Reference to the root node of the tree
     */
    private Node tree;
    /**
     * @var array The actual paths to all expanded nodes in the tree
     *            This does not include nodes created after the grouping
     *            of nodes has been performed
     */
    private List<String[]> aPath = new ArrayList<>();
    /**
     * @var array The virtual paths to all expanded nodes in the tree
     *            This includes nodes created after the grouping of
     *            nodes has been performed
     */
    private List<String[]> vPath = new ArrayList<>();
    /**
     * @var int Position in the list of databases,
     *          used for pagination
     */
    private Integer pos = null;
    /**
     * @var array The names of the type of items that are being paginated on
     *            the second level of the navigation tree. These may be
     *            tables, views, functions, procedures or events.
     */
    private List<String> pos2Name = new ArrayList<>();
    /**
     * @var array The positions of nodes in the lists of tables, views,
     *            routines or events used for pagination
     */
    private List<Integer> pos2Value = new ArrayList<>();
    /**
     * @var array The names of the type of items that are being paginated
     *            on the second level of the navigation tree.
     *            These may be columns or indexes
     */
    private List<String> pos3Name = new ArrayList<>();
    /**
     * @var array The positions of nodes in the lists of columns or indexes
     *            used for pagination
     */
    private List<Integer> pos3Value = new ArrayList<>();
    /**
     * @var String The search clause to use in SQL queries for
     *             fetching databases
     *             Used by the asynchronous fast filter
     */
    private String searchClause = "";
    /**
     * @var String The search clause to use in SQL queries for
     *             fetching nodes
     *             Used by the asynchronous fast filter
     */
    private String searchClause2 = "";
    /**
     * @var bool Whether a warning was raised for large item groups
     *           which can affect performance.
     */
    private boolean largeGroupWarning = false;

    /**
     * @var Template
     */
    @Autowired
    private Template template;

    /**
     * @var DatabaseInterface
     */
    @Autowired
    private DatabaseInterface dbi;

    @Autowired
    private Globals GLOBALS;
    @Autowired
    private HttpServletRequest httpRequest;
    @Autowired
    private SessionMap sessionMap;
    @Autowired
    private Response response;

    /**
     * NavigationTree constructor.
     * @param Template          $template Template instance
     * @param DatabaseInterface $dbi      DatabaseInterface instance
     */
    public NavigationTree(HttpServletRequest httpRequest)
    {

        /* TODO $checkUserPrivileges = new CheckUserPrivileges(this.dbi);
        $checkUserPrivileges.getPrivileges();*/

        // Save the position at which we are in the database list
        if (!empty(httpRequest.getParameter("pos"))) {
            this.pos = new Integer(httpRequest.getParameter("pos"));
        }
        if (! !empty(this.pos)) {
            this.pos = this.getNavigationDbPos();
        }
        // Get the active node
        if (!empty(httpRequest.getParameter("aPath"))) {
            this.aPath.set(0, this.parsePath(httpRequest.getParameter("aPath")));
            this.pos2Name.set(0, httpRequest.getParameter("pos2_name"));
            this.pos2Value.set(0, new Integer(httpRequest.getParameter("pos2_value")));
            if (!empty(httpRequest.getParameter("pos3_name"))) {
                this.pos3Name.set(0, httpRequest.getParameter("pos3_name"));
                this.pos3Value.set(0, new Integer(httpRequest.getParameter("pos3_value")));
            }
        } else {
            if (!empty(httpRequest.getParameter("n0_aPath"))) {
                int $count = 0;
                while (!empty(httpRequest.getParameter("n" + $count + "_aPath"))) {
                    this.aPath.set($count, this.parsePath(
                        httpRequest.getParameter("n" + $count + "_aPath"))
                    );
                    String $index = "n" + $count + "_pos2_";
                    this.pos2Name.set($count, httpRequest.getParameter($index + "name"));
                    this.pos2Value.set($count, new Integer(httpRequest.getParameter($index + "value")));
                    $index = "n" + $count + "_pos3_";
                    if (!empty(httpRequest.getParameter($index))) {
                        this.pos3Name.set($count, httpRequest.getParameter($index + "name"));
                        this.pos3Value.set($count, new Integer(httpRequest.getParameter($index + "value")));
                    }
                    $count++;
                }
            }
        }
        if (!empty(httpRequest.getParameter("vPath"))) {
            this.vPath.set(0, this.parsePath(httpRequest.getParameter("vPath")));
        } else {
            if (!empty(httpRequest.getParameter("n0_vPath"))) {
                int $count = 0;
                while (!empty(httpRequest.getParameter("n" + $count + "_vPath"))) {
                    this.vPath.set($count, this.parsePath(
                        httpRequest.getParameter("n" + $count + "_vPath"))
                    );
                    $count++;
                }
            }
        }
        if (!empty(httpRequest.getParameter("searchClause"))) {
            this.searchClause = httpRequest.getParameter("searchClause");
        }
        if (!empty(httpRequest.getParameter("searchClause2"))) {
            this.searchClause2 = httpRequest.getParameter("searchClause2");
        }
        // Initialise the tree by creating a root node
        Node $node = NodeFactory.getInstance("NodeDatabaseContainer", "root", httpRequest, GLOBALS);
        this.tree = $node;
        if ("true".equals(Globals.getConfig().get("NavigationTreeEnableGrouping"))
            && "true".equals(Globals.getConfig().get("ShowDatabasesNavigationAsTree"))
        ) {
            this.tree.separator = Globals.getConfig().get("NavigationTreeDbSeparator");
            this.tree.separatorDepth = 10000;
        }
    }

    /**
     * Returns the database position for the page selector
     *
     * @return int
     */
    private int getNavigationDbPos()
    {
    	return 0; /* TODO ?
        $retval = 0;

        if (strlen(GLOBALS["db"]) == 0) {
            return $retval;
        }

        // @todo describe a scenario where this code is executed
        if (! Globals.getConfig().get("Server"]["DisableIS"]) {
            $dbSeparator = this.dbi.escapeString(
                Globals.getConfig().get("NavigationTreeDbSeparator"]
            );
            $query = 'SELECT (COUNT(DB_first_level) DIV %d) * %d ';
            $query += 'from ( ';
            $query += ' SELECT distinct SUBSTRING_INDEX(SCHEMA_NAME, ';
            $query += ' "%s", 1) ';
            $query += ' DB_first_level ';
            $query += ' FROM INFORMATION_SCHEMA.SCHEMATA ';
            $query += ' WHERE `SCHEMA_NAME` < "%s" ';
            $query += ') t ';

            $retval = this.dbi.fetchValue(
                sprintf(
                    $query,
                    (int) Globals.getConfig().get("FirstLevelNavigationItems"],
                    (int) Globals.getConfig().get("FirstLevelNavigationItems"],
                    $dbSeparator,
                    this.dbi.escapeString(GLOBALS["db"])
                )
            );

            return $retval;
        }

        $prefixMap = [];
        if (GLOBALS["dbs_to_test"] === false) {
            $handle = this.dbi.tryQuery('SHOW DATABASES');
            if ($handle !== false) {
                while ($arr = this.dbi.fetchArray($handle)) {
                    if (strcasecmp($arr[0], GLOBALS["db"]) >= 0) {
                        break;
                    }

                    $prefix = strstr(
                        $arr[0],
                        Globals.getConfig().get("NavigationTreeDbSeparator"],
                        true
                    );
                    if ($prefix === false) {
                        $prefix = $arr[0];
                    }
                    $prefixMap[$prefix] = 1;
                }
            }
        } else {
            $databases = [];
            foreach (GLOBALS["dbs_to_test"] as $db) {
                $query = 'SHOW DATABASES LIKE "' + $db + '"';
                $handle = this.dbi.tryQuery($query);
                if ($handle === false) {
                    continue;
                }
                while ($arr = this.dbi.fetchArray($handle)) {
                    $databases[] = $arr[0];
                }
            }
            sort($databases);
            foreach ($databases as $database) {
                if (strcasecmp($database, GLOBALS["db"]) >= 0) {
                    break;
                }

                $prefix = strstr(
                    $database,
                    Globals.getConfig().get("NavigationTreeDbSeparator"],
                    true
                );
                if ($prefix === false) {
                    $prefix = $database;
                }
                $prefixMap[$prefix] = 1;
            }
        }

        $navItems = (int) Globals.getConfig().get("FirstLevelNavigationItems"];
        $retval = (int) floor(count($prefixMap) / $navItems) * $navItems;

        return $retval;*/
    }

    /**
     * Converts an encoded path to a node in String format to an array
     *
     * @param String $string The path to parse
     *
     * @return array
     */
    private String[] parsePath(String $string)
    {
        String[] $path = $string.split(".");
        for (int i = 0; i < $path.length; ++i) {
            $path[i] = base64_decode($path[i]);
        }

        return $path;
    }

    /**
     * Generates the tree structure so that it can be rendered later
     *
     * @return Node|false The active node or false in case of failure
     * @throws SQLException 
     */
    private Node buildPath() throws SQLException
    {
        Node $retval = this.tree;

        // Add all databases unconditionally
        List<String> $data = this.tree.getData(
            "databases",
            this.pos,
            this.searchClause
        );
        Map<String, Integer> $hiddenCounts = this.tree.getNavigationHidingData();
        for (String $db : $data) {
        	NodeDatabase $node = (NodeDatabase) NodeFactory.getInstance("NodeDatabase", $db, httpRequest, GLOBALS);
            if (!empty($hiddenCounts.get($db))) {
                $node.setHiddenCount($hiddenCounts.get($db));
            }
            this.tree.addChild($node);
        }

        // Whether build other parts of the tree depends
        // on whether we have any paths in this._aPath
        for (int $key = 0; $key < this.aPath.size(); ++$key) {
        	List<String> aPath = Arrays.asList(this.aPath.get($key));
            $retval = this.buildPathPart(
            	aPath,
                this.pos2Name.get($key),
                this.pos2Value.get($key),
                !empty(this.pos3Name.get($key)) ? this.pos3Name.get($key) : "",
                !empty(this.pos3Value.get($key)) ? this.pos3Value.get($key) : null
            );
        }

        return $retval;
    }

    /**
     * Builds a branch of the tree
     *
     * @param array  $path  A paths pointing to the branch
     *                      of the tree that needs to be built
     * @param String $type2 The type of item being paginated on
     *                      the second level of the tree
     * @param int    $pos2  The position for the pagination of
     *                      the branch at the second level of the tree
     * @param String $type3 The type of item being paginated on
     *                      the third level of the tree
     * @param int    $pos3  The position for the pagination of
     *                      the branch at the third level of the tree
     *
     * @return Node|bool    The active node or false in case of failure, true if the path contains <= 1 items
     * @throws SQLException 
     */
    private Node buildPathPart(List<String> $path, String $type2, Integer $pos2, String $type3, Integer $pos3) throws SQLException
    {
        if (empty($pos2)) {
            $pos2 = 0;
        }
        if (empty($pos3)) {
            $pos3 = 0;
        }

        Node $retval = null;
        if ($path.size() <= 1) {
            return null; // FIXME... in PMA was return true...
        }

        array_shift($path); // remove "root"
        /** @var NodeDatabase $db */
        NodeDatabase $db = (NodeDatabase) this.tree.getChild($path.get(0));
        $retval = $db;

        if ($db == null) {
            return null;
        }

        LinkedHashMap<String, Node> $containers = this.addDbContainers($db, $type2, $pos2);

        array_shift($path); // remove db

        if (($path.size() <= 0 || ! $containers.containsKey($path.get(0)))
            && $containers.size() != 1
        ) {
            return $retval;
        }
        
        Node $container;
        if ($containers.size() == 1) {
            $container = array_shift($containers);
        } else {
            $container = $db.getChild($path.get(0), true);
            if ($container == null) {
                return null;
            }
        }
        $retval = $container;

        Node $node = null;
        if ($container.children.size() <= 1) {
            List<String> $dbData = $db.getData(
                $container.realName,
                $pos2,
                this.searchClause2
            );
            for (String $item : $dbData) {
                switch ($container.realName) {
                    case "events":
                        $node = NodeFactory.getInstance(
                            "NodeEvent",
                            $item,
                            httpRequest,
                            GLOBALS
                        );
                        break;
                    case "functions":
                        $node = NodeFactory.getInstance(
                            "NodeFunction",
                            $item,
                            httpRequest,
                            GLOBALS
                        );
                        break;
                    case "procedures":
                        $node = NodeFactory.getInstance(
                            "NodeProcedure",
                            $item,
                            httpRequest,
                            GLOBALS
                        );
                        break;
                    case "tables":
                        $node = NodeFactory.getInstance(
                            "NodeTable",
                            $item,
                            httpRequest,
                            GLOBALS
                        );
                        break;
                    case "views":
                        $node = NodeFactory.getInstance(
                            "NodeView",
                            $item,
                            httpRequest,
                            GLOBALS
                        );
                        break;
                    default:
                        break;
                }
                if (!empty($node)) {
                    if ($type2 == $container.realName) {
                        $node.pos2 = $pos2;
                    }
                    $container.addChild($node);
                }
            }
        }
        if ($path.size() > 1 && !$path.get(0).equals("tables")) {
            $retval = null;
            return $retval;
        }

        array_shift($path); // remove container
        if ($path.size() <= 0) {
            return $retval;
        }

        /** @var NodeTable $table */
        Node $table = $container.getChild($path.get(0), true);
        if ($table == null) {
            if ($db.getPresence("tables", $path.get(0), GLOBALS) == 0) {
                return null;
            }

            $node = NodeFactory.getInstance(
                "NodeTable",
                $path.get(0), httpRequest, GLOBALS
            );
            if ($type2 == $container.realName) {
                $node.pos2 = $pos2;
            }
            $container.addChild($node);
            $table = $container.getChild($path.get(0), true);
        }
        $retval = $table;
        $containers = this.addTableContainers(
            (NodeTable) $table,
            $pos2,
            $type3,
            $pos3
        );
        array_shift($path); // remove table
        if ($path.size() <= 0
            || ! $containers.containsKey($path.get(0))
        ) {
            return $retval;
        }

        $container = $table.getChild($path.get(0), true);
        $retval = $container;
        List<String> $tableData = $table.getData(
            $container.realName,
            $pos3
        );
        for (String $item : $tableData) {
            switch ($container.realName) {
                case "indexes":
                    $node = NodeFactory.getInstance(
                        "NodeIndex",
                        $item,
                        httpRequest,
                        GLOBALS
                    );
                    break;
                case "columns":
                    $node = NodeFactory.getInstance(
                        "NodeColumn",
                        $item,
                        httpRequest,
                        GLOBALS
                    );
                    break;
                case "triggers":
                    $node = NodeFactory.getInstance(
                        "NodeTrigger",
                        $item,
                        httpRequest,
                        GLOBALS
                    );
                    break;
                default:
                    break;
            }
            if (!empty($node)) {
                $node.pos2 = $container.parent.pos2;
                if ($type3 == $container.realName) {
                    $node.pos3 = $pos3;
                }
                $container.addChild($node);
            }
        }

        return $retval;
    }

    /**
     * Adds containers to a node that is a table
     *
     * References to existing children are returned
     * if this function is called twice on the same node
     *
     * @param NodeTable $table The table node, new containers will be
     *                         attached to this node
     * @param int       $pos2  The position for the pagination of
     *                         the branch at the second level of the tree
     * @param String    $type3 The type of item being paginated on
     *                         the third level of the tree
     * @param int       $pos3  The position for the pagination of
     *                         the branch at the third level of the tree
     *
     * @return array An array of new nodes
     * @throws SQLException 
     */
    private LinkedHashMap<String, Node> addTableContainers(NodeTable $table, int $pos2, String $type3, int $pos3) throws SQLException
    {
    	LinkedHashMap<String, Node> $retval = new LinkedHashMap<>();
        if (!$table.hasChildren(true)) {
            if ($table.getPresence("columns", GLOBALS) > 0) {
                $retval.put("columns", NodeFactory.getInstance(
                    "NodeColumnContainer", httpRequest, GLOBALS
                ));
            }
            if ($table.getPresence("indexes", GLOBALS) > 0) {
                $retval.put("indexes", NodeFactory.getInstance(
                    "NodeIndexContainer", httpRequest, GLOBALS
                ));
            }
            if ($table.getPresence("triggers", GLOBALS) > 0) {
                $retval.put("triggers", NodeFactory.getInstance(
                    "NodeTriggerContainer", httpRequest, GLOBALS
                ));
            }
            // Add all new Nodes to the tree
            for (Node $node : $retval.values()) {
                $node.pos2 = $pos2;
                if ($type3 == $node.realName) {
                    $node.pos3 = $pos3;
                }
                $table.addChild($node);
            }
        } else {
            for (Node $node : $table.children) {
                if ($type3 == $node.realName) {
                    $node.pos3 = $pos3;
                }
                $retval.put($node.realName, $node);
            }
        }

        return $retval;
    }

    /**
     * Adds containers to a node that is a database
     *
     * References to existing children are returned
     * if this function is called twice on the same node
     *
     * @param NodeDatabase $db   The database node, new containers will be
     *                           attached to this node
     * @param String       $type The type of item being paginated on
     *                           the second level of the tree
     * @param int          $pos2 The position for the pagination of
     *                           the branch at the second level of the tree
     *
     * @return array An array of new nodes
     * @throws SQLException 
     */
    private LinkedHashMap<String, Node> addDbContainers(NodeDatabase $db, String $type, int $pos2) throws SQLException
    {
        // Get items to hide
    	List<String> $hidden = $db.getHiddenItems("group");
        if ("false".equals(Globals.getConfig().get("NavigationTreeShowTables"))
            && ! $hidden.contains("tables")
        ) {
            $hidden.add("tables");
        }
        if ("false".equals(Globals.getConfig().get("NavigationTreeShowViews"))
            && ! $hidden.contains("views")
        ) {
            $hidden.add("views");
        }
        if ("false".equals(Globals.getConfig().get("NavigationTreeShowFunctions"))
            && ! $hidden.contains("functions")
        ) {
            $hidden.add("functions");
        }
        if ("false".equals(Globals.getConfig().get("NavigationTreeShowProcedures"))
            && ! $hidden.contains("procedures")
        ) {
            $hidden.add("procedures");
        }
        if ("false".equals(Globals.getConfig().get("NavigationTreeShowEvents"))
            && ! $hidden.contains("events")
        ) {
            $hidden.add("events");
        }

        LinkedHashMap<String, Node> $retval = new LinkedHashMap<>();
        if (!$db.hasChildren(true)) {
            if (! $hidden.contains("tables") && $db.getPresence("tables", GLOBALS) > 0) {
                $retval.put("tables", NodeFactory.getInstance(
                    "NodeTableContainer", httpRequest, GLOBALS
                ));
            }
            if (! $hidden.contains("views") && $db.getPresence("views", GLOBALS) > 0) {
                $retval.put("views", NodeFactory.getInstance(
                    "NodeViewContainer", httpRequest, GLOBALS
                ));
            }
            if (! $hidden.contains("functions") && $db.getPresence("functions", GLOBALS) > 0) {
                $retval.put("functions", NodeFactory.getInstance(
                    "NodeFunctionContainer", httpRequest, GLOBALS
                ));
            }
            if (! $hidden.contains("procedures") && $db.getPresence("procedures", GLOBALS) > 0) {
                $retval.put("procedures", NodeFactory.getInstance(
                    "NodeProcedureContainer", httpRequest, GLOBALS
                ));
            }
            if (! $hidden.contains("events") && $db.getPresence("events", GLOBALS) > 0) {
                $retval.put("events", NodeFactory.getInstance(
                    "NodeEventContainer", httpRequest, GLOBALS
                ));
            }
            // Add all new Nodes to the tree
            for (Node $node : $retval.values()) {
                if ($type == $node.realName) {
                    $node.pos2 = $pos2;
                }
                $db.addChild($node);
            }
        } else {
            for (Node $node : $db.children) {
                if ($type == $node.realName) {
                    $node.pos2 = $pos2;
                }
                $retval.put($node.realName, $node);
            }
        }

        return $retval;
    }

    /**
     * Recursively groups tree nodes given a separator
     *
     * @param mixed $node The node to group or null
     *                    to group the whole tree. If
     *                    passed as an argument, $node
     *                    must be of type CONTAINER
     *
     * @return void
     */
    public void groupTree(Node $node /*= null*/)
    {
        if ($node == null) {
            $node = this.tree;
        }
        this.groupNode($node);
        for (Node $child : $node.children) {
            this.groupTree($child);
        }
    }
    
    public void groupTree() {
    	this.groupTree(null);
    }

    /**
     * Recursively groups tree nodes given a separator
     *
     * @param Node $node The node to group
     *
     * @return void
     */
    public void groupNode(Node $node)
    {
    	/* TODO
        if ($node.type != Node.CONTAINER
            || ! Globals.getConfig().get("NavigationTreeEnableExpansion"]
        ) {
            return;
        }

        $separators = [];
        if (is_array($node.separator)) {
            $separators = $node.separator;
        } else {
            if (strlen($node.separator)) {
                $separators[] = $node.separator;
            }
        }
        $prefixes = [];
        if ($node.separatorDepth > 0) {
            foreach ($node.children as $child) {
                $prefixPos = false;
                foreach ($separators as $separator) {
                    $sepPos = mb_strpos((String) $child.name, $separator);
                    if ($sepPos != false
                        && $sepPos != mb_strlen($child.name)
                        && $sepPos != 0
                        && ($prefixPos === false || $sepPos < $prefixPos)
                    ) {
                        $prefixPos = $sepPos;
                    }
                }
                if ($prefixPos !== false) {
                    $prefix = mb_substr($child.name, 0, $prefixPos);
                    if (! !empty($prefixes[$prefix])) {
                        $prefixes[$prefix] = 1;
                    } else {
                        $prefixes[$prefix]++;
                    }
                }
                //Bug #4375: Check if prefix is the name of a DB, to create a group.
                foreach ($node.children as $otherChild) {
                    if (array_key_exists($otherChild.name, $prefixes)) {
                        $prefixes[$otherChild.name]++;
                    }
                }
            }
            //Check if prefix is the name of a DB, to create a group.
            foreach ($node.children as $child) {
                if (array_key_exists($child.name, $prefixes)) {
                    $prefixes[$child.name]++;
                }
            }
        }
        // It is not a group if it has only one item
        foreach ($prefixes as $key => $value) {
            if ($value == 1) {
                unset($prefixes[$key]);
            }
        }
        // rfe #1634 Don"t group if there"s only one group and no other items
        if (count($prefixes) === 1) {
            $keys = array_keys($prefixes);
            $key = $keys[0];
            if ($prefixes[$key] == count($node.children) - 1) {
                unset($prefixes[$key]);
            }
        }
        if (count($prefixes)) {
            // @var Node[] $groups
            $groups = [];
            foreach ($prefixes as $key => $value) {
                // warn about large groups
                if ($value > 500 && ! this.largeGroupWarning) {
                    trigger_error(
                        __(
                            "There are large item groups in navigation panel which "
                            + "may affect the performance. Consider disabling item "
                            + "grouping in the navigation panel."
                        ),
                        E_USER_WARNING
                    );
                    this.largeGroupWarning = true;
                }

                $groups[$key] = new Node(
                    htmlspecialchars((String) $key),
                    Node.CONTAINER,
                    true
                );
                $groups[$key].separator = $node.separator;
                $groups[$key].separatorDepth = $node.separatorDepth - 1;
                $groups[$key].icon = Util.getImage(
                    "b_group",
                    __("Groups")
                );
                $groups[$key].pos2 = $node.pos2;
                $groups[$key].pos3 = $node.pos3;
                if ($node instanceof NodeTableContainer
                    || $node instanceof NodeViewContainer
                ) {
                    $tblGroup = "&amp;tbl_group=" + urlencode($key);
                    $groups[$key].links = [
                        "text" => $node.links["text"] + $tblGroup,
                        "icon" => $node.links["icon"] + $tblGroup,
                    ];
                }
                $node.addChild($groups[$key]);
                foreach ($separators as $separator) {
                    $separatorLength = strlen($separator);
                    // FIXME: this could be more efficient
                    foreach ($node.children as $child) {
                        $keySeparatorLength = mb_strlen((String) $key) + $separatorLength;
                        $nameSubstring = mb_substr(
                            (String) $child.name,
                            0,
                            $keySeparatorLength
                        );
                        if (($nameSubstring != $key + $separator
                            && $child.name != $key)
                            || $child.type != Node.OBJECT
                        ) {
                            continue;
                        }
                        $class = get_class($child);
                        $className = substr($class, strrpos($class, "\\") + 1);
                        unset($class);
                        $newChild = NodeFactory.getInstance(
                            $className,
                            mb_substr(
                                $child.name,
                                $keySeparatorLength
                            )
                        );

                        if ($newChild instanceof NodeDatabase
                            && $child.getHiddenCount() > 0
                        ) {
                            $newChild.setHiddenCount($child.getHiddenCount());
                        }

                        $newChild.realName = $child.realName;
                        $newChild.icon = $child.icon;
                        $newChild.links = $child.links;
                        $newChild.pos2 = $child.pos2;
                        $newChild.pos3 = $child.pos3;
                        $groups[$key].addChild($newChild);
                        foreach ($child.children as $elm) {
                            $newChild.addChild($elm);
                        }
                        $node.removeChild($child.name);
                    }
                }
            }
            foreach ($prefixes as $key => $value) {
                this.groupNode($groups[$key]);
                $groups[$key].classes = 'navGroup';
            }
        }*/
    }

    /**
     * Renders a state of the tree, used in light mode when
     * either JavaScript and/or Ajax are disabled
     *
     * @return String HTML code for the navigation tree
     * @throws SQLException 
     */
    public String renderState() throws SQLException
    {
        this.buildPath();

        String $quickWarp = this.quickWarp();
        String $fastFilter = this.fastFilterHtml(this.tree);
        String $controls = "";
        if ("true".equals(Globals.getConfig().get("NavigationTreeEnableExpansion"))) {
            $controls = this.controls();
        }
        String $pageSelector = this.getPageSelector(this.tree);

        this.groupTree();
        List<Node> $children = this.tree.children;
        /* TODO usort($children, [
            NavigationTree.class,
            "sortNode",
        ]);*/
        this.setVisibility();

        String $nodes = "";
        for (int $i = 0, $nbChildren = $children.size(); $i < $nbChildren; $i++) {
            if ($i == 0) {
                $nodes += this.renderNode($children.get(0), true, "first");
            } else {
                if ($i + 1 != $nbChildren) {
                    $nodes += this.renderNode($children.get($i), true);
                } else {
                    $nodes += this.renderNode($children.get($i), true, "last");
                }
            }
        }

        Map<String, Object> model = new HashMap<>();
        model.put("quick_warp", $quickWarp);
        model.put("fast_filter", $fastFilter);
        model.put("controls", $controls);
        model.put("page_selector", $pageSelector);
        model.put("nodes", $nodes);
        return this.template.render("navigation/tree/state", model);
    }

    /**
     * Renders a part of the tree, used for Ajax requests in light mode
     *
     * @return String|false HTML code for the navigation tree
     * @throws SQLException 
     */
    public String renderPath() throws SQLException
    {
        Node $node = this.buildPath();
        String $parentName = null;
        String $listContent = null;
        if ($node != null) {
            this.groupTree();
            $listContent = this.fastFilterHtml($node);
            $listContent += this.getPageSelector($node);
            List<Node> $children = $node.children;
            /* TODO usort($children, [
                NavigationTree.class,
                "sortNode",
            ]);*/

            for (int $i = 0, $nbChildren = $children.size(); $i < $nbChildren; $i++) {
                if ($i + 1 != $nbChildren) {
                    $listContent += this.renderNode($children.get($i), true);
                } else {
                    $listContent += this.renderNode($children.get($i), true, "last");
                }
            }

            if ("false".equals(Globals.getConfig().get("ShowDatabasesNavigationAsTree"))) {
                List<Node> $parents = $node.parents(true);
                $parentName = $parents.get(0).realName;
            }
        }

        if (! empty(this.searchClause) || ! empty(this.searchClause2)) {
            int $results = 0;
            if (! empty(this.searchClause2)) {
                // FIXME if (is_object($node.realParent())) {
                    $results = $node.realParent()
                        .getPresence(
                            $node.realName,
                            this.searchClause2,
                            GLOBALS
                        );
                //}
            } else {
                $results = this.tree.getPresence(
                    "databases",
                    this.searchClause,
                    GLOBALS
                );
            }
            String $results_str = String.format(
                _ngettext(
                    "%s result found",
                    "%s results found",
                    $results
                ),
                $results
            );
            response
                .addJSON(
                    "results",
                    $results_str
                );
        }

        if ($node != null) {
        	Map<String, Object> model = new HashMap<>();
            model.put("has_search_results", ! empty(this.searchClause) || ! empty(this.searchClause2));
            model.put("list_content", $listContent);
            model.put("is_tree", Globals.getConfig().get("ShowDatabasesNavigationAsTree"));
            model.put("parent_name", $parentName);
        	
            return this.template.render("navigation/tree/path", model);
        }
        return null;
    }

    /**
     * Renders the parameters that are required on the client
     * side to know which page(s) we will be requesting data from
     *
     * @param Node $node The node to create the pagination parameters for
     *
     * @return String
     */
    private String getPaginationParamsHtml(Node $node)
    {
        String $retval = "";
        PathStructure $paths = $node.getPaths();
        if ($paths.aPathClean.size() > 2) {
            $retval += "<span class='hide pos2_name'>";
            $retval += $paths.aPathClean.get(2);
            $retval += "</span>";
            $retval += "<span class='hide pos2_value'>";
            $retval += htmlspecialchars(Integer.toString($node.pos2));
            $retval += "</span>";
        }
        if ($paths.aPathClean.size() > 4) {
            $retval += "<span class='hide pos3_name'>";
            $retval += $paths.aPathClean.get(4);
            $retval += "</span>";
            $retval += "<span class='hide pos3_value'>";
            $retval += htmlspecialchars(Integer.toString($node.pos3));
            $retval += "</span>";
        }

        return $retval;
    }

    /**
     * Finds whether given tree matches this tree.
     *
     * @param array $tree  Tree to check
     * @param array $paths Paths to check
     *
     * @return boolean
     */
    private boolean findTreeMatch(List<String[]> $tree, List<String> $paths)
    {
    	return false; /* TODO
        boolean $match = false;
        for ($path : $tree) {
            $match = true;
            for ($paths as $key => $part) {
                if (! !empty($path[$key]) || $part != $path[$key]) {
                    $match = false;
                    break;
                }
            }
            if ($match) {
                break;
            }
        }

        return $match;*/
    }

    /**
     * Renders a single node or a branch of the tree
     *
     * @param Node   $node      The node to render
     * @param bool   $recursive Bool: Whether to render a single node or a branch
     * @param String $class     An additional class for the list item
     *
     * @return String HTML code for the tree node or branch
     * @throws SQLException 
     */
    private String renderNode(Node $node, boolean $recursive, String $class /*= ""*/) throws SQLException
    {
        String $retval = "";
        boolean $wrap;
        PathStructure $paths = $node.getPaths();
        if ($node.hasSiblings()
            || $node.realParent() == null
        ) {
            if ($node.type == Node.CONTAINER
                && $node.children.isEmpty()
                && ! response.isAjax()
            ) {
                return "";
            }
            $retval += "<li class='" + ($class + " " + $node.classes).trim() + "'>";
            String[] $sterile = new String[] {
                "events",
                "triggers",
                "functions",
                "procedures",
                "views",
                "columns",
                "indexes",
            };
            String $parentName = "";
            List<Node> $parents = $node.parents(false, true);
            if (!$parents.isEmpty()) {
                $parentName = $parents.get(0).realName;
            }
            // if node name itself is in sterile, then allow
            if ($node.isGroup
                || (! in_array($parentName, $sterile) && ! $node.isNew)
                || in_array($node.realName, $sterile)
            ) {
                $retval += "<div class='block'>";
                String $iClass = "";
                if ($class.equals("first")) {
                    $iClass = " class='first'";
                }
                $retval += "<i$iClass></i>";
                if (!($class.contains("last"))) {
                    $retval += "<b></b>";
                }

                boolean $match = this.findTreeMatch(
                    this.vPath,
                    $paths.vPathClean
                );

                $retval += "<a class='" + $node.getCssClasses($match) + "'";
                $retval += " href='#'>";
                $retval += "<span class='hide aPath'>";
                $retval += $paths.aPath;
                $retval += "</span>";
                $retval += "<span class='hide vPath'>";
                $retval += $paths.vPath;
                $retval += "</span>";
                $retval += "<span class='hide pos'>";
                $retval += this.pos;
                $retval += "</span>";
                $retval += this.getPaginationParamsHtml($node);
                if ("true".equals(Globals.getConfig().get("ShowDatabasesNavigationAsTree"))
                    || !"root".equals($parentName) 
                ) {
                    $retval += $node.getIcon($match);
                }

                $retval += "</a>";
                $retval += "</div>";
            } else {
                $retval += "<div class='block'>";
                String $iClass = "";
                if ($class.equals("first")) {
                    $iClass = " class='first'";
                }
                $retval += "<i$iClass></i>";
                $retval += this.getPaginationParamsHtml($node);
                $retval += "</div>";
            }

            String $linkClass = "";
            List<String> $haveAjax = Arrays.asList(new String[] {
                "functions",
                "procedures",
                "events",
                "triggers",
                "indexes",
            });
            List<Node> $parent = $node.parents(false, true);
            boolean $isNewView = $parent.get(0).realName.equals("views") && $node.isNew;
            if ($parent.get(0).type == Node.CONTAINER
                && ($haveAjax.contains($parent.get(0).realName) || $isNewView)
            ) {
                $linkClass = " ajax";
            }

            if ($node.type == Node.CONTAINER) {
                $retval += "<i>";
            }

            String $divClass = "";

            List<String> $iconLinks = new ArrayList<>();
            List<String> $icons = new ArrayList<>();
            if (!empty($node.links.get("icon"))) {
            	$iconLinks = (List<String>) $node.links.get("icon");
                $icons = $node.icon;

                if ($icons.size() > 1) {
                    $divClass = "double";
                }
            }

            $retval += "<div class='block " + $divClass + "'>";

            List<String> $args = new ArrayList<>();
            String $link, $title;
            if (!empty($node.links.get("icon"))) {
                for (Node $parent0 : $node.parents(true)) {
                    $args.add(urlencode($parent0.realName));
                }

                // for ($icons as $key => $icon) {
                for (int $key = 0; $key < $icons.size(); ++$key) {
                	String $icon = $icons.get($key);
                    $link = String.format($iconLinks.get($key), $args);
                    if (!$linkClass .isEmpty()) {
                        $retval += "<a class='$linkClass' href='$link'>";
                        $retval += $icon + "</a>";
                    } else {
                        $retval += "<a href='" + $link + "'>" + $icon + "</a>";
                    }
                }
            } else {
                $retval += "<u>{$node.icon}</u>";
            }
            $retval += "</div>";

            if (!empty($node.links.get("text"))) {
                for (Node $parent0 : $node.parents(true)) {
                    $args.add(urlencode($parent0.realName));
                }
                $link = String.format((String)$node.links.get("text"), $args);
                $title = !empty($node.links.get("text")) ? (String) $node.links.get("text") : "";
                if ($node.type == Node.CONTAINER) {
                    $retval += "&nbsp;<a class='hover_show_full' href='$link'>";
                    $retval += htmlspecialchars($node.name);
                    $retval += "</a>";
                } else {
                    $retval += "<a class='hover_show_full$linkClass' href='$link'";
                    $retval += " title='$title'>";
                    $retval += htmlspecialchars(!empty($node.displayName) ? $node.displayName : $node.realName);
                    $retval += "</a>";
                }
            } else {
                $retval += "&nbsp;{$node.name}";
            }
            $retval += $node.getHtmlForControlButtons();
            if ($node.type == Node.CONTAINER) {
                $retval += "</i>";
            }
            $retval += "<div class='clearfloat'></div>";
            $wrap = true;
        } else {
            $node.visible = true;
            $wrap = false;
            $retval += this.getPaginationParamsHtml($node);
        }

        if ($recursive) {
            String $hide = "";
            if (! $node.visible) {
                $hide = " style='display: none;'";
            }
            List<Node> $children = $node.children;
            /* FIXME usort(
                $children,
                [
                    NavigationTree.class,
                    "sortNode",
                ]
            );*/
            String $buffer = "";
            String $extraClass = "";
            for (int $i = 0, $nbChildren = $children.size(); $i < $nbChildren; $i++) {
                if ($i + 1 == $nbChildren) {
                    $extraClass = " last";
                }
                $buffer += this.renderNode(
                    $children.get($i),
                    true,
                    $children.get($i).classes + $extraClass
                );
            }
            if (! empty($buffer)) {
                if ($wrap) {
                    $retval += "<div$hide class='list_container'><ul>";
                }
                $retval += this.fastFilterHtml($node);
                $retval += this.getPageSelector($node);
                $retval += $buffer;
                if ($wrap) {
                    $retval += "</ul></div>";
                }
            }
        }
        if ($node.hasSiblings()) {
            $retval += "</li>";
        }

        return $retval;
    }
    
    private String renderNode(Node $node, boolean $recursive) throws SQLException {
    	return renderNode($node, $recursive, "");
    }

    /**
     * Renders a database select box like the pre-4.0 navigation panel
     *
     * @return String HTML code
     * @throws SQLException 
     */
    public String renderDbSelect() throws SQLException
    {
        this.buildPath();

        String $quickWarp = this.quickWarp();

        this.tree.isGroup = false;

        // Provide for pagination in database select
        Map<String, String> args0 = new HashMap<>();
        args0.put("server", Integer.toString(GLOBALS.getServer()));
        String $listNavigator = Util.getListNavigator(
            this.tree.getPresence("databases", "", GLOBALS),
            this.pos,
            args0,
            Url.getFromRoute("/navigation", httpRequest, GLOBALS),
            "frame_navigation",
            new Integer((String)Globals.getConfig().get("FirstLevelNavigationItems")),
            "pos",
            new String[] {"dbselector"}
        );

        List<Node> $children = this.tree.children;
        String $selected = GLOBALS.getDb();
        String $options = "";
        for (Node $node : $children) {
            if ($node.isNew) {
                continue;
            }
            PathStructure $paths = $node.getPaths();
            if (!empty($node.links.get("text"))) {
                String $title = !empty($node.links.get("title")) ? "" : (String) $node.links.get("title");
                $options += "<option value='"
                    + htmlspecialchars($node.realName) + "'"
                    + " title='" + htmlspecialchars($title) + "'"
                    + " apath='" + $paths.aPath + "'"
                    + " vpath='" + $paths.vPath + "'"
                    + " pos='" + this.pos + "'";
                if ($node.realName == $selected) {
                    $options += " selected";
                }
                $options += ">" + htmlspecialchars($node.realName);
                $options += "</option>";
            }
        }

        $children = this.tree.children;
        /* TODO usort($children, [
            NavigationTree.class,
            "sortNode",
        ]);*/
        this.setVisibility();

        String $nodes = "";
        for (int $i = 0, $nbChildren = $children.size(); $i < $nbChildren; $i++) {
            if ($i == 0) {
                $nodes += this.renderNode($children.get(0), true, "first");
            } else {
                if ($i + 1 != $nbChildren) {
                    $nodes += this.renderNode($children.get($i), true);
                } else {
                    $nodes += this.renderNode($children.get($i), true, "last");
                }
            }
        }

        Map<String, Object> model = new HashMap<>();
        model.put("quick_warp", $quickWarp);
        model.put("list_navigator", $listNavigator);
		model.put("server", GLOBALS.getServer());
		model.put("options", $options);
		model.put("nodes", $nodes);
        return this.template.render("navigation/tree/database_select", model);
    }

    /**
     * Makes some nodes visible based on the which node is active
     *
     * @return void
     */
    private void setVisibility()
    {
        for(String[] $path : this.vPath) {
            Node $node = this.tree;
            for (String $value : $path) {
                Node $child = $node.getChild($value);
                if ($child != null) {
                    $child.visible = true;
                    $node = $child;
                }
            }
        }
    }

    /**
     * Generates the HTML code for displaying the fast filter for tables
     *
     * @param Node $node The node for which to generate the fast filter html
     *
     * @return String LI element used for the fast filter
     * @throws SQLException 
     */
    private String fastFilterHtml(Node $node) throws SQLException
    {
        String $retval = "";
        int $filterDbMin
            = new Integer((String)Globals.getConfig().get("NavigationTreeDisplayDbFilterMinimum"));
        int $filterItemMin
            = new Integer((String)Globals.getConfig().get("NavigationTreeDisplayItemFilterMinimum"));
        if ($node == this.tree
            && this.tree.getPresence(GLOBALS) >= $filterDbMin
        ) {
            Map $urlParams = new HashMap();
            $urlParams.put("pos", 0);
            $retval += "<li class='fast_filter db_fast_filter'>";
            $retval += "<form class='ajax fast_filter'>";
            $retval += Url.getHiddenInputs(httpRequest, GLOBALS, sessionMap/*$urlParams*/); //FIXME
            $retval += "<input class='searchClause' type='text'";
            $retval += " name='searchClause' accesskey='q'";
            $retval += " placeholder='"
                + __("Type to filter these, Enter to search all");
            $retval += "'>";
            $retval += "<span title='" + __("Clear fast filter") + "'>X</span>";
            $retval += "</form>";
            $retval += "</li>";

            return $retval;
        }

        if (($node.type == Node.CONTAINER
            && ($node.realName == "tables"
            || $node.realName == "views"
            || $node.realName == "functions"
            || $node.realName == "procedures"
            || $node.realName == "events"))
            //&& method_exists($node.realParent(), "getPresence")
            && $node.realParent().getPresence($node.realName, GLOBALS) >= $filterItemMin
        ) {
            PathStructure $paths = $node.getPaths();
            Map<String, Object> $urlParams = new HashMap<>();
            $urlParams.put("pos"       , this.pos);
            $urlParams.put("aPath"     , $paths.aPath);
            $urlParams.put("vPath"     , $paths.vPath);
            $urlParams.put("pos2_name" , $node.realName);
            $urlParams.put("pos2_value", 0);
            $retval += "<li class='fast_filter'>";
            $retval += "<form class='ajax fast_filter'>";
            $retval += Url.getHiddenFields($urlParams, sessionMap);
            $retval += "<input class='searchClause' type='text'";
            $retval += " name='searchClause2'";
            $retval += " placeholder='"
                + __("Type to filter these, Enter to search all") + "'>";
            $retval += "<span title='" + __("Clear fast filter") + "'>X</span>";
            $retval += "</form>";
            $retval += "</li>";
        }

        return $retval;
    }

    /**
     * Creates the code for displaying the controls
     * at the top of the navigation tree
     *
     * @return String HTML code for the controls
     */
    private String controls()
    {
        // always iconic
        boolean $showIcon = true;
        boolean $showText = false;

        String $retval = "<!-- CONTROLS START -.";
        $retval += "<li id='navigation_controls_outer'>";
        $retval += "<div id='navigation_controls'>";
        $retval += Util.getNavigationLink(
            "#",
            $showText,
            __("Collapse all"),
            $showIcon,
            "s_collapseall",
            "pma_navigation_collapse"
        );
        String $syncImage = "s_unlink";
        String $title = __("Link with main panel");
        if ("true".equals(Globals.getConfig().get("NavigationLinkWithMainPanel"))) {
            $syncImage = "s_link";
            $title = __("Unlink from main panel");
        }
        $retval += Util.getNavigationLink(
            "#",
            $showText,
            $title,
            $showIcon,
            $syncImage,
            "pma_navigation_sync"
        );
        $retval += "</div>";
        $retval += "</li>";
        $retval += "<!-- CONTROLS ENDS -.";

        return $retval;
    }

    /**
     * Generates the HTML code for displaying the list pagination
     *
     * @param Node $node The node for whose children the page
     *                   selector will be created
     *
     * @return String
     * @throws SQLException 
     */
    private String getPageSelector(Node $node) throws SQLException
    {
    	String $retval = "";
        if ($node == this.tree) {
        	Map params1 = new HashMap();
        	params1.put("server", GLOBALS.getServer());
        	String[] params2 = new String[] { "dbselector" };
            $retval += Util.getListNavigator(
                this.tree.getPresence("databases", this.searchClause, GLOBALS),
                this.pos,
                params1,
                Url.getFromRoute("/navigation", httpRequest, GLOBALS),
                "frame_navigation",
                new Integer((String)Globals.getConfig().get("FirstLevelNavigationItems")),
                "pos",
                params2
            );
        } else {
            if ($node.type == Node.CONTAINER && ! $node.isGroup) {
                PathStructure $paths = $node.getPaths();

                int $level = !empty($paths.aPathClean.get(4)) ? 3 : 2;
                Map<String, String> $urlParams = new HashMap<>();
                $urlParams.put("aPath"    , $paths.aPath);
                $urlParams.put("vPath"    , $paths.vPath);
                $urlParams.put("pos"      , Integer.toString(this.pos));
                $urlParams.put("server"   , Integer.toString(GLOBALS.getServer()));
                $urlParams.put("pos2_name", $paths.aPathClean.get(2));
                int $pos;
                if ($level == 3) {
                    $pos = $node.pos3;
                    $urlParams.put("pos2_value", Integer.toString($node.pos2));
                    $urlParams.put("pos3_name", $paths.aPathClean.get(4));
                } else {
                    $pos = $node.pos2;
                }
                int $num = $node.realParent()
                    .getPresence(
                        $node.realName,
                        this.searchClause2, GLOBALS
                    );
                $retval += Util.getListNavigator(
                    $num,
                    $pos,
                    $urlParams,
                    Url.getFromRoute("/navigation", httpRequest, GLOBALS),
                    "frame_navigation",
                    new Integer((String)Globals.getConfig().get("MaxNavigationItems")),
                    "pos" + $level + "_value"
                );
            }
        }

        return $retval;
    }

    /*
     * Called by usort() for sorting the nodes in a container
     *
     * @param Node $a The first element used in the comparison
     * @param Node $b The second element used in the comparison
     *
     * @return int See strnatcmp() and strcmp()
     *
    public static int sortNode(Node $a, Node $b)
    {
        if ($a.isNew) {
            return -1;
        }

        if ($b.isNew) {
            return 1;
        }

        if (Globals.getConfig().get("NaturalOrder")) {
            return strnatcasecmp($a.name, $b.name);
        }

        return strcasecmp($a.name, $b.name);
    }*/

    /**
     * Display quick warp links, contain Recents and Favorites
     *
     * @return String HTML code
     */
    private String quickWarp()
    {
        String $retval = "<div class='pma_quick_warp'>";
        
        
        if (!empty(Globals.getConfig().get("NumRecentTables")) && !"0".equals(Globals.getConfig().get("NumRecentTables"))) {
            $retval += RecentFavoriteTable.getInstance("recent", sessionMap, GLOBALS)
                .getHtml(httpRequest, sessionMap, GLOBALS);
        }
        if (!empty(Globals.getConfig().get("NumFavoriteTables")) && !"0".equals(Globals.getConfig().get("NumFavoriteTables"))) {
            $retval += RecentFavoriteTable.getInstance("favorite", sessionMap, GLOBALS)
            		.getHtml(httpRequest, sessionMap, GLOBALS);
        }
        $retval += "<div class='clearfloat'></div>";
        $retval += "</div>";

        return $retval;
    }
}
