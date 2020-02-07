package org.javamyadmin.helpers;

import static org.javamyadmin.php.Php.*;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.javamyadmin.java.SmartMap;
import org.javamyadmin.php.Globals;
import org.javamyadmin.php.Php.SessionMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Generates and renders the top menu
 *
 * Notice: in PhpMyAdmin, menus are rendered programmatically.
 * We don't like this approach, we store most data in a properties file.
 * 
 * @package PhpMyAdmin
 */
@Service
public class Menu {

    /**
     * Database name
     *
     * @access private
     * @var string
     */
    private String _db;
    /**
     * Table name
     *
     * @access private
     * @var string
     */
    private String _table;
    
    @Autowired
	private SessionMap session;
	private SmartMap cfg;
    @Autowired
	private HttpServletRequest request;
    @Autowired
	private Globals GLOBALS;
    @Autowired
	private Config config;
    @Autowired
	private Util util;

    private static Map<String, MenuStruct> serverTabs;
    private static Map<String, MenuStruct> dbTabs;
    private static Map<String, MenuStruct> tableTabs;
	static {
		loadTabs();
	}
	
	private static void loadTabs() {
		
		//TODO define tabs as Spring beans
		
		SmartMap menuProperties = new SmartMap();
        try {
			InputStream is = Globals.class.getClassLoader().getResourceAsStream("/menu.properties");
			menuProperties.load(is);
		} catch (NullPointerException e) {
			throw new IllegalStateException("File menu.properties not found!");
		} catch (IOException e) {
			throw new IllegalStateException("Error reading menu.properties!");
		}
        serverTabs = loadTabsFor(menuProperties, "server");
        dbTabs = loadTabsFor(menuProperties, "server");
        tableTabs = loadTabsFor(menuProperties, "table");
	}
	
	private static Map<String, MenuStruct> loadTabsFor(SmartMap menuProperties, String whatfor) {
		Map<String, MenuStruct> retmap = new HashMap<>();
        @SuppressWarnings("unchecked")
		Map<String, Map<String, String>> m = (Map<String, Map<String, String>>) menuProperties.get(whatfor);
        for (Entry<String, Map<String, String>> entry: m.entrySet()) {
        	String tabname = entry.getKey();
        	Map<String, String> tabattrs = entry.getValue();
        	retmap.put(tabname, new MenuStruct(tabattrs.get("icon"), //
        			tabattrs.get("link"), //
        			tabattrs.get("text"), //
        			"true".equals(tabattrs.get("active") //
        					)));
        }
        return retmap;
	}
	
	/**
	 * Bean containing Menu properties
	 *
	 */
    public static class MenuStruct {
    	private String icon = "";
    	private String text = "";
    	private String clazz = ""; // FIXME! 'class' is not allowed here
    	private String fragment = "";
    	private String id = "";
    	private String attr = "";
    	private String sep = "?";
    	private String link = "";
    	private String warning;
    	private Map<String, String> args;
    	private Boolean active = null;

    	public MenuStruct(String icon, String link, String text, boolean active) {
    		this(icon, link, text, active, null);
    	}
    	public MenuStruct(String icon, String link, String text, boolean active, Map<String, String> args) {
    		this.icon = icon;
    		this.text = text;
    		this.link = link;
    		this.args = (args != null) ? args : new HashMap<>();
    		this.active = active;
    	}
		public String getIcon() {
			return icon;
		}
		public void setIcon(String icon) {
			this.icon = icon;
		}
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
		public String getLink() {
			return link;
		}
		public void setLink(String link) {
			this.link = link;
		}
		public String getWarning() {
			return warning;
		}
		public void setWarning(String warnings) {
			this.warning = warnings;
		}
		public Map<String, String> getArgs() {
			return args;
		}
		public void setArgs(Map<String, String> args) {
			this.args = args;
		}
		public Boolean getActive() {
			return active;
		}
		public void setActive(Boolean active) {
			this.active = active;
		}
		public String getClazz() {
			return clazz;
		}
		public void setClazz(String clazz) {
			this.clazz = clazz;
		}
		public String getFragment() {
			return fragment;
		}
		public void setFragment(String fragment) {
			this.fragment = fragment;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getAttr() {
			return attr;
		}
		public void setAttr(String attr) {
			this.attr = attr;
		}
		public String getSep() {
			return sep;
		}
		public void setSep(String sep) {
			this.sep = sep;
		}
    }
    
    /**
     * @var Relation
     */
    //private Relation $relation;

    /**
     * Creates a new instance of Menu
     *
     * @param string $db    Database name
     * @param string $table Table name
     */
    public Menu()
    {
        //this.relation = new Relation(GLOBALS.getDbi());
        this.cfg = config.settings;
    }
    
    /**
     * Prints the menu and the breadcrumbs
     *
     * @return void
     * @throws IOException 
     * @throws SQLException 
     */
    public void display(HttpServletRequest request, HttpServletResponse response, Globals GLOBALS) throws IOException, SQLException
    {
        response.getWriter().write(this.getDisplay(request, GLOBALS));
    }

    /**
     * Returns the menu and the breadcrumbs as a string
     *
     * @return string
     * @throws SQLException 
     */
    public String getDisplay(HttpServletRequest request, Globals GLOBALS) throws SQLException
    {
        String  $retval  = this._getBreadcrumbs(request, GLOBALS);
        $retval += this._getMenu();
        return $retval;
    }

    /**
     * Returns hash for the menu and the breadcrumbs
     *
     * @return string
     * @throws SQLException 
     */
    public String getHash() throws SQLException
    {
        return 
            md5(this._getMenu() + this._getBreadcrumbs(request, GLOBALS)).substring(0,8);
    }

    /**
     * Returns the menu as HTML
     *
     * @return string HTML formatted menubar
     * @throws SQLException 
     */
    private String _getMenu() throws SQLException
    {
        Map<String, String> $url_params = new HashMap<>();
        String $level;
        Map<String, MenuStruct> $tabs;
        if (!empty(this._table)) {
            $tabs = this._getTableTabs();
            $url_params.put("db", this._db);
            $url_params.put("table", this._table);
            $level = "table";
        } else if (!empty(this._db)) {
            $tabs = this._getDbTabs();
            $url_params.put("db", this._db);
            $level = "db";
        } else {
            $tabs = this._getServerTabs();
            $level = "server";
        }

        Map<String, String> $allowedTabs = this._getAllowedTabs($level);
        Map<String, MenuStruct> $tabsCopy = new HashMap<>($tabs);
        for (Entry<String, MenuStruct> $entry : $tabsCopy.entrySet()) {
            if (! $allowedTabs.containsKey($entry.getKey())) {
                $tabs.remove($entry.getKey());
            }
        }
        return util.getHtmlTabs($tabs, $url_params, "topmenu", true, request, GLOBALS, session);
    }

    /**
     * Returns a list of allowed tabs for the current user for the given level
     *
     * @param string $level "server", "db" or "table" level
     *
     * @return array list of allowed ta0bs
     */
    private Map<String, String> _getAllowedTabs(String $level)
    {
        String $cache_key = "menu-levels-" + $level;
        if (util.cacheExists($cache_key, GLOBALS, session)) {
            return (Map<String, String>) util.cacheGet($cache_key, null, GLOBALS, session);
        }
        Map<String, String> $allowedTabs = util.getMenuTabList($level);
        /*
        TODO
        $cfgRelation = this.relation.getRelationsParam();
        if ($cfgRelation["menuswork"]) {
            $groupTable = util.backquote($cfgRelation["db"])
                + "."
                + util.backquote($cfgRelation["usergroups"]);
            $userTable = util.backquote($cfgRelation["db"])
                + "." + util.backquote($cfgRelation["users"]);

            String $sql_query = "SELECT `tab` FROM " + $groupTable
                + " WHERE `allowed` = 'N'"
                + " AND `tab` LIKE '" + $level + "%'"
                + " AND `usergroup` = (SELECT usergroup FROM "
                + $userTable + " WHERE `username` = '"
                + GLOBALS.getDbi().escapeString(cfg.get("Server")["user"]) + "')";

            $result = this.relation.queryAsControlUser($sql_query, false);
            if ($result) {
                while ($row = GLOBALS.getDbi().fetchAssoc($result)) {
                    $tabName = mb_substr(
                        $row["tab"],
                        mb_strpos($row["tab"], "_") + 1
                    );
                    unset($allowedTabs[$tabName]);
                }
            }
        }*/
        util.cacheSet($cache_key, $allowedTabs, GLOBALS, session);
        return $allowedTabs;
    }

    /**
     * Returns the breadcrumbs as HTML
     *
     * @return string HTML formatted breadcrumbs
     * @throws SQLException 
     */
    private String  _getBreadcrumbs(HttpServletRequest request, Globals GLOBALS) throws SQLException
    {
        String $retval = "";
        boolean $tbl_is_view = GLOBALS.getDbi().getTable(this._db, this._table)
            .isView();
        if (empty(multiget(cfg, "Server", "host"))) {
            multiput(cfg, "Server", "host", "");
        }
        String $server_info = ! empty(multiget(cfg, "Server", "verbose"))
            ? (String) multiget(cfg, "Server", "verbose")
            : (String) multiget(cfg, "Server", "host");
        $server_info += empty(multiget(cfg, "Server", "port"))
            ? ""
            : ":" + multiget(cfg, "Server", "port");

        String $separator = "<span class='separator item'>&nbsp;Â»</span>";
        String $item = "<a href='%1$s%2$s' class='item'>";

        if (util.showText("TabsMode", GLOBALS)) {
            $item += "%4$s: ";
        }
        $item += "%3$s</a>";
        $retval += "<div id='floating_menubar'></div>";
        $retval += "<div id='serverinfo'>";
        Map<String, Object> params = new HashMap<>();
        params.put("class", "item");
        if (util.showIcons("TabsMode", GLOBALS)) {
            $retval += util.getImage(
                "s_host",
                "",
                params
            );
        }
        String $scriptName = util.getScriptNameForOption(
            (String) cfg.get("DefaultTabServer"),
            "server"
        );
        $retval += String.format(
            $item,
            $scriptName,
            Url.getCommon(null, $scriptName.contains( "?") ? "&" : "?"),
            htmlspecialchars($server_info),
            __("Server")
        );

        if (!empty(this._db)) {
            $retval += $separator;
            if (util.showIcons("TabsMode", GLOBALS)) {
                $retval += util.getImage(
                    "s_db",
                    "",
                    params
                );
            }
            $scriptName = util.getScriptNameForOption(
                (String) cfg.get("DefaultTabDatabase"),
                "database"
            );
            Map<String, String> paramsDb = new HashMap<>();
            paramsDb.put("db", this._db);
            $retval += String.format(
                $item,
                $scriptName,
                Url.getCommon(paramsDb, $scriptName.contains( "?") ? "&" : "?"),
                htmlspecialchars(this._db),
                __("Database")
            );
            // if the table is being dropped, $_REQUEST["purge"] is set to "1"
            // so do not display the table name in upper div
            String $show_comment;
            if (! empty(this._table)
                && ! ("1".equals(request.getParameter("purge")))
            ) {
                Table $table_class_object = GLOBALS.getDbi().getTable(
                    GLOBALS.getDb(),
                    GLOBALS.getTable()
                );
                if ($table_class_object.isView()) {
                    $tbl_is_view = true;
                    $show_comment = null;
                } else {
                    $tbl_is_view = false;
                    $show_comment = $table_class_object.getComment();
                }
                $retval += $separator;
                if (util.showIcons("TabsMode", GLOBALS)) {
                    String $icon = $tbl_is_view ? "b_views" : "s_tbl";
                    $retval += util.getImage(
                        $icon,
                        "",
                        params
                    );
                }
                $scriptName = util.getScriptNameForOption(
                    (String) cfg.get("DefaultTabTable"),
                    "table"
                );
                paramsDb.put("table", this._table);
                $retval += String.format(
                    $item,
                    $scriptName,
                    Url.getCommon(paramsDb, $scriptName.contains( "?") ? "&" : "?"),
                    htmlspecialchars(this._table).replace(" ", "&nbsp;"),
                    $tbl_is_view ? __("View") : __("Table")
                );

                // Displays table comment
                 
                if (! empty($show_comment)
                    && ! (GLOBALS.getAvoidShowComment())
                ) {
                    /*if (mb_strstr($show_comment, "; InnoDB free")) {
                        $show_comment = preg_replace(
                            "@; InnoDB free:.*?$@",
                            "",
                            $show_comment
                        );
                    }*/
                    $retval += "<span class='table_comment'";
                    $retval += " id='span_table_comment'>";
                    $retval += String.format(
                        __("â€œ%sâ€�"),
                        htmlspecialchars($show_comment)
                    );
                    $retval += "</span>";
                } // end if
            } else {
            	/* TODO
                // no table selected, display database comment if present
                $cfgRelation = this.relation.getRelationsParam();

                // Get additional information about tables for tooltip is done
                // in util.getDbInfo() only once
                if ($cfgRelation["commwork"]) {
                    String $comment = this.relation.getDbComment(this._db);
                    // Displays table comment
                    if (! empty($comment)) {
                        $retval += "<span class='table_comment'"
                            + " id='span_table_comment'>"
                            + String.format(
                                __("â€œ%sâ€�"),
                                htmlspecialchars($comment)
                            )
                            + "</span>";
                    } // end if
                }*/
            }
        }
        $retval += "<div class='clearfloat'></div>";
        $retval += "</div>";
        return $retval;
    }
    
    /**
     * Returns the table tabs as an array
     *
     * @return array Data for generating table tabs
     * @throws SQLException 
     */
    private Map<String, MenuStruct> _getTableTabs() throws SQLException
    {
        /*boolean $db_is_system_schema = GLOBALS.getDbi().isSystemSchema(this._db);
        boolean $tbl_is_view = GLOBALS.getDbi().getTable(this._db, this._table)
            .isView();
        boolean $updatable_view = false;
        if ($tbl_is_view) {
            $updatable_view = GLOBALS.getDbi().getTable(this._db, this._table)
                .isUpdatableView();
        }
        boolean $is_superuser = GLOBALS.getDbi().isSuperuser();
        boolean $isCreateOrGrantUser = GLOBALS.getDbi().isUserType("grant")
            || GLOBALS.getDbi().isUserType("create");*/

        Map<String, MenuStruct> $tabs = new HashMap<>();
        $tabs.putAll(tableTabs);
        

        /*Map<String, String> params = new HashMap<>();
        params.put("pos", "0");
        
        $tabs.put("browse", new MenuStruct("b_browse",
        		Url.getFromRoute("/sql", (Map)session, request, GLOBALS),
        		__("Browse"),
        		GLOBALS.getRoute().equals("/sql"),
        		params
        		));

        $tabs.put("structure", new MenuStruct("b_props",
        		Url.getFromRoute("/table/structure", (Map)session, request, GLOBALS),
        		__("Structure"),
        		GLOBALS.getRoute().equals("/sql"),
        		params
        		));

        multiput($tabs,"structure", "icon",  "b_props");
        multiput($tabs,"structure", "link",  Url.getFromRoute("/table/structure", request, GLOBALS));
        multiput($tabs,"structure", "text",  __("Structure"));
        multiput($tabs,"structure", "active",  in_array(GLOBALS.getRoute(), new String[] {
            "/table/relation",
            "/table/structure",
        }));

        multiput($tabs,"sql", "icon",  "b_sql");
        multiput($tabs,"sql", "link",  Url.getFromRoute("/table/sql", request, GLOBALS));
        multiput($tabs,"sql", "text",  __("SQL"));
        multiput($tabs,"sql", "active",  GLOBALS.getRoute().equals("/table/sql"));

        multiput($tabs,"search", "icon",  "b_search");
        multiput($tabs,"search", "text",  __("Search"));
        multiput($tabs,"search", "link",  Url.getFromRoute("/table/search", request, GLOBALS));
        multiput($tabs,"search", "active",  in_array(GLOBALS.getRoute(), new String[] {
            "/table/find_replace",
            "/table/search",
            "/table/zoom_select",
        }));

        if (! $db_is_system_schema && (! $tbl_is_view || $updatable_view)) {
            multiput($tabs,"insert", "icon",  "b_insrow");
            multiput($tabs,"insert", "link",  Url.getFromRoute("/table/change", request, GLOBALS));
            multiput($tabs,"insert", "text",  __("Insert"));
            multiput($tabs,"insert", "active",  GLOBALS.getRoute().equals("/table/change"));
        }

        multiput($tabs,"export", "icon",  "b_tblexport");
        multiput($tabs,"export", "link",  Url.getFromRoute("/table/export", request, GLOBALS));
        multiput($tabs,"export", "args", "single_table",  "true");
        multiput($tabs,"export", "text",  __("Export"));
        multiput($tabs,"export", "active",  GLOBALS.getRoute().equals("/table/export"));

        // Don"t display "Import" for views and information_schema
        
        if (! $tbl_is_view && ! $db_is_system_schema) {
            multiput($tabs,"import", "icon",  "b_tblimport");
            multiput($tabs,"import", "link",  Url.getFromRoute("/table/import", request, GLOBALS));
            multiput($tabs,"import", "text",  __("Import"));
            multiput($tabs,"import", "active",  GLOBALS.getRoute().equals("/table/import"));
        }
        if (($is_superuser || $isCreateOrGrantUser)
            && ! $db_is_system_schema
        ) {
            multiput($tabs,"privileges", "link",  Url.getFromRoute("/server/privileges", request, GLOBALS));
            multiput($tabs,"privileges", "args", "checkprivsdb",  this._db);
            multiput($tabs,"privileges", "args", "checkprivstable",  this._table);
            // stay on table view
            multiput($tabs,"privileges", "args", "viewing_mode",  "table");
            multiput($tabs,"privileges", "text",  __("Privileges"));
            multiput($tabs,"privileges", "icon",  "s_rights");
            multiput($tabs,"privileges", "active",  GLOBALS.getRoute().equals("/server/privileges"));
        }
        // Don"t display "Operations" for views and information_schema
       
        if (! $tbl_is_view && ! $db_is_system_schema) {
            multiput($tabs,"operation", "icon",  "b_tblops");
            multiput($tabs,"operation", "link",  Url.getFromRoute("/table/operations", request, GLOBALS));
            multiput($tabs,"operation", "text",  __("Operations"));
            multiput($tabs,"operation", "active",  GLOBALS.getRoute().equals("/table/operations"));
        }
        // Views support a limited number of operations
        if ($tbl_is_view && ! $db_is_system_schema) {
            multiput($tabs,"operation", "icon",  "b_tblops");
            multiput($tabs,"operation", "link",  Url.getFromRoute("/view/operations", request, GLOBALS));
            multiput($tabs,"operation", "text",  __("Operations"));
            multiput($tabs,"operation", "active",  GLOBALS.getRoute().equals("/view/operations"));
        }

        if (Tracker.isActive() && ! $db_is_system_schema) {
            multiput($tabs,"tracking", "icon",  "eye");
            multiput($tabs,"tracking", "text",  __("Tracking"));
            multiput($tabs,"tracking", "link",  Url.getFromRoute("/table/tracking", request, GLOBALS));
            multiput($tabs,"tracking", "active",  GLOBALS.getRoute().equals("/table/tracking");
        }
        if (! $db_is_system_schema
            && util.currentUserHasPrivilege(
                "TRIGGER",
                this._db,
                this._table
            )
            && ! $tbl_is_view
        ) {
            multiput($tabs,"triggers", "link",  Url.getFromRoute("/table/triggers", request, GLOBALS));
            multiput($tabs,"triggers", "text",  __("Triggers"));
            multiput($tabs,"triggers", "icon",  "b_triggers");
            multiput($tabs,"triggers", "active",  GLOBALS.getRoute().equals("/table/triggers"));
        }*/

        return $tabs;
    }

    /**
     * Returns the db tabs as an array
     *
     * @return array Data for generating db tabs
     * @throws SQLException 
     */
    private Map<String, MenuStruct> _getDbTabs() throws SQLException
    {
        /*boolean $db_is_system_schema = GLOBALS.getDbi().isSystemSchema(this._db);
        int $num_tables = GLOBALS.getDbi().getTables(this._db).size();
        boolean $is_superuser = GLOBALS.getDbi().isSuperuser();
        boolean $isCreateOrGrantUser = GLOBALS.getDbi().isUserType("grant")
            || GLOBALS.getDbi().isUserType("create");*/

        /**
         * Gets the relation settings
         */
        //TODO $cfgRelation = this.relation.getRelationsParam();

        Map<String, MenuStruct> $tabs = new HashMap<>();
        $tabs.putAll(dbTabs);

        /*
        $tabs["structure"]["link"] = Url.getFromRoute("/database/structure");
        $tabs["structure"]["text"] = __("Structure");
        $tabs["structure"]["icon"] = "b_props";
        $tabs["structure"]["active"] = GLOBALS.route == "/database/structure";

        $tabs["sql"]["link"] = Url.getFromRoute("/database/sql");
        $tabs["sql"]["text"] = __("SQL");
        $tabs["sql"]["icon"] = "b_sql";
        $tabs["sql"]["active"] = GLOBALS.route == "/database/sql";

        $tabs["search"]["text"] = __("Search");
        $tabs["search"]["icon"] = "b_search";
        $tabs["search"]["link"] = Url.getFromRoute("/database/search");
        $tabs["search"]["active"] = GLOBALS.route.equals("/database/search";
        if ($num_tables == 0) {
            $tabs["search"]["warning"] = __("Database seems to be empty!");
        }

        $tabs["query"]["text"] = __("Query");
        $tabs["query"]["icon"] = "s_db";
        $tabs["query"]["link"] = Url.getFromRoute("/database/multi_table_query");
        $tabs["query"]["active"] = GLOBALS.route.equals("/database/multi_table_query" || GLOBALS.route.equals("/database/qbe";
        if ($num_tables == 0) {
            $tabs["query"]["warning"] = __("Database seems to be empty!");
        }

        $tabs["export"]["text"] = __("Export");
        $tabs["export"]["icon"] = "b_export";
        $tabs["export"]["link"] = Url.getFromRoute("/database/export");
        $tabs["export"]["active"] = GLOBALS.route.equals("/database/export";
        if ($num_tables == 0) {
            $tabs["export"]["warning"] = __("Database seems to be empty!");
        }

        if (! $db_is_system_schema) {
            $tabs["import"]["link"] = Url.getFromRoute("/database/import");
            $tabs["import"]["text"] = __("Import");
            $tabs["import"]["icon"] = "b_import";
            $tabs["import"]["active"] = GLOBALS.route.equals("/database/import";

            $tabs["operation"]["link"] = Url.getFromRoute("/database/operations");
            $tabs["operation"]["text"] = __("Operations");
            $tabs["operation"]["icon"] = "b_tblops";
            $tabs["operation"]["active"] = GLOBALS.route.equals("/database/operations";

            if ($is_superuser || $isCreateOrGrantUser) {
                $tabs["privileges"]["link"] = Url.getFromRoute("/server/privileges");
                $tabs["privileges"]["args"]["checkprivsdb"] = this._db;
                // stay on database view
                $tabs["privileges"]["args"]["viewing_mode"] = "db";
                $tabs["privileges"]["text"] = __("Privileges");
                $tabs["privileges"]["icon"] = "s_rights";
                $tabs["privileges"]["active"] = GLOBALS.route.equals("/server/privileges";
            }

            $tabs["routines"]["link"] = Url.getFromRoute("/database/routines");
            $tabs["routines"]["text"] = __("Routines");
            $tabs["routines"]["icon"] = "b_routines";
            $tabs["routines"]["active"] = GLOBALS.route.equals("/database/routines";

            if (util.currentUserHasPrivilege("EVENT", this._db)) {
                $tabs["events"]["link"] = Url.getFromRoute("/database/events");
                $tabs["events"]["text"] = __("Events");
                $tabs["events"]["icon"] = "b_events";
                $tabs["events"]["active"] = GLOBALS.route.equals("/database/events";
            }

            if (util.currentUserHasPrivilege("TRIGGER", this._db)) {
                $tabs["triggers"]["link"] = Url.getFromRoute("/database/triggers");
                $tabs["triggers"]["text"] = __("Triggers");
                $tabs["triggers"]["icon"] = "b_triggers";
                $tabs["triggers"]["active"] = GLOBALS.route.equals("/database/triggers";
            }
        }

        if (Tracker.isActive() && ! $db_is_system_schema) {
            $tabs["tracking"]["text"] = __("Tracking");
            $tabs["tracking"]["icon"] = "eye";
            $tabs["tracking"]["link"] = Url.getFromRoute("/database/tracking");
            $tabs["tracking"]["active"] = GLOBALS.route.equals("/database/tracking";
        }

        if (! $db_is_system_schema) {
            $tabs["designer"]["text"] = __("Designer");
            $tabs["designer"]["icon"] = "b_relations";
            $tabs["designer"]["link"] = Url.getFromRoute("/database/designer");
            $tabs["designer"]["id"] = "designer_tab";
            $tabs["designer"]["active"] = GLOBALS.route.equals("/database/designer";
        }

        if (! $db_is_system_schema
            && $cfgRelation["centralcolumnswork"]
        ) {
            $tabs["central_columns"]["text"] = __("Central columns");
            $tabs["central_columns"]["icon"] = "centralColumns";
            $tabs["central_columns"]["link"] = Url.getFromRoute("/database/central_columns");
            $tabs["central_columns"]["active"] = GLOBALS.route.equals("/database/central_columns";
        }*/
        return $tabs;
    }
    
    /**
     * Returns the server tabs as an array
     *
     * @return array Data for generating server tabs
     */
    private Map<String, MenuStruct> _getServerTabs()
    {
    	boolean $is_superuser = GLOBALS.getDbi().isSuperuser();
    	boolean $isCreateOrGrantUser = GLOBALS.getDbi().isUserType("grant")
            || GLOBALS.getDbi().isUserType("create");
        
    	/* MYSQL Specific!!!
    	Map $binary_logs;
    	if (util.cacheExists("binary_logs", session)) {
            $binary_logs = (Map) util.cacheGet("binary_logs", null, session);
        } else {
            $binary_logs = GLOBALS.getDbi().fetchResult(
                "SHOW MASTER LOGS",
                "Log_name",
                null,
                DatabaseInterface.CONNECT_USER,
                DatabaseInterface.QUERY_STORE
            );
            util.cacheSet("binary_logs", $binary_logs, session);
        }*/

        Map<String, MenuStruct> $tabs = new HashMap<>();
        $tabs.putAll(serverTabs);
        
        /*
        $tabs.put("databases", new MenuStruct("s_db",
				Url.getFromRoute("/server/databases",
				__("Databases"),
				GLOBALS.route.equals("/server/databases")
				)));
        $tabs.put("sql", new MenuStruct("b_sql",
				Url.getFromRoute("/server/sql",
				__("SQL"),
				GLOBALS.route.equals("/server/sql")
				)));
        $tabs.put("status", new MenuStruct("s_status",
				Url.getFromRoute("/server/status",
				__("Status"),
				statusRoutes.contains(GLOBALS.route)
				)));
        
        if ($is_superuser || $isCreateOrGrantUser) {
        	Map params = new HashMap();
        	params.put("viewing_mode", "server");
        	$tabs.put("rights", new MenuStruct("s_rights",
    				Url.getFromRoute("/server/privileges",
    				__("User accounts"),
    				privilegesRoutes.contains(GLOBALS.route),
    				params
    				)));
        }

        $tabs.put("status", new MenuStruct("b_export",
				Url.getFromRoute("/server/export"),
				__("Export"),
				GLOBALS.route.equals("/server/export")
				)));
        
        $tabs["export"]["icon"] = "b_export";
        $tabs["export"]["link"] = Url.getFromRoute("/server/export");
        $tabs["export"]["text"] = __("Export");
        $tabs["export"]["active"] = GLOBALS.route.equals("/server/export";

        $tabs["import"]["icon"] = "b_import";
        $tabs["import"]["link"] = Url.getFromRoute("/server/import");
        $tabs["import"]["text"] = __("Import");
        $tabs["import"]["active"] = GLOBALS.route.equals("/server/import";

        $tabs["settings"]["icon"] = "b_tblops";
        $tabs["settings"]["link"] = Url.getFromRoute("/preferences/manage");
        $tabs["settings"]["text"] = __("Settings");
        $tabs["settings"]["active"] = in_array(GLOBALS.route, [
            "/preferences/forms",
            "/preferences/manage",
            "/preferences/twofactor",
        ]);

        if (! empty($binary_logs)) {
            $tabs["binlog"]["icon"] = "s_tbl";
            $tabs["binlog"]["link"] = Url.getFromRoute("/server/binlog");
            $tabs["binlog"]["text"] = __("Binary log");
            $tabs["binlog"]["active"] = GLOBALS.route.equals("/server/binlog");
        }

        if ($is_superuser) {
            $tabs["replication"]["icon"] = "s_replication";
            $tabs["replication"]["link"] = Url.getFromRoute("/server/replication");
            $tabs["replication"]["text"] = __("Replication");
            $tabs["replication"]["active"] = GLOBALS.route.equals("/server/replication");
        }

        $tabs["vars"]["icon"] = "s_vars";
        $tabs["vars"]["link"] = Url.getFromRoute("/server/variables");
        $tabs["vars"]["text"] = __("Variables");
        $tabs["vars"]["active"] = GLOBALS.route.equals("/server/variables";

        $tabs["charset"]["icon"] = "s_asci";
        $tabs["charset"]["link"] = Url.getFromRoute("/server/collations");
        $tabs["charset"]["text"] = __("Charsets");
        $tabs["charset"]["active"] = GLOBALS.route.equals("/server/collations");

        $tabs["engine"]["icon"] = "b_engine";
        $tabs["engine"]["link"] = Url.getFromRoute("/server/engines");
        $tabs["engine"]["text"] = __("Engines");
        $tabs["engine"]["active"] = GLOBALS.route.equals("/server/engines");

        $tabs["plugins"]["icon"] = "b_plugin";
        $tabs["plugins"]["link"] = Url.getFromRoute("/server/plugins");
        $tabs["plugins"]["text"] = __("Plugins");
        $tabs["plugins"]["active"] = GLOBALS.route.equals("/server/plugins");
		*/
        return $tabs;
    }

    /**
     * Set current table
     *
     * @param string $table Current table
     *
     * @return Menu
     */
    public Menu setTable(String $table)
    {
        this._table = $table;
        return this;
    }

    /**
     * Set current database
     *
     * @param string $db Current db
     *
     * @return Menu
     */
    public Menu setDb(String $db)
    {
        this._db = $db;
        return this;
    }
}
