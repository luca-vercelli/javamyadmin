package org.javamyadmin.helpers;

import static org.javamyadmin.php.Php.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.javamyadmin.java.SmartMap;
import org.javamyadmin.php.Globals;
import org.javamyadmin.php.Php.SessionMap;

/**
 * Generates and renders the top menu
 *
 * Notice: in PhpMyAdmin, menus are rendered programmatically.
 * We don't like this approach, we store most data in a properties file.
 * 
 * @package PhpMyAdmin
 */
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
    
	private SessionMap session;
	private SmartMap cfg;
	private HttpServletRequest request;
	private Globals GLOBALS;

	private static SmartMap menuProperties;
	static {
		menuProperties = new SmartMap();
        try {
			InputStream is = Globals.class.getClassLoader().getResourceAsStream("/menu.properties");
			menuProperties.load(is);
		} catch (NullPointerException e) {
			throw new IllegalStateException("File menu.properties not found!");
		} catch (IOException e) {
			throw new IllegalStateException("Error reading menu.properties!");
		}
	}
	
	/**
	 * Bean containing Menu properties
	 *
	 */
    public static class MenuStruct {
    	private String icon;
    	private String text;
    	private String link;
    	private String warnings;
    	private Map args;
    	private boolean active;

    	public MenuStruct(String icon, String link, String text, boolean active) {
    		this(icon, text, link, active, null);
    	}
    	public MenuStruct(String icon, String link, String text, boolean active, Map args) {
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
		public String getWarnings() {
			return warnings;
		}
		public void setWarnings(String warnings) {
			this.warnings = warnings;
		}
		public Map getArgs() {
			return args;
		}
		public void setArgs(Map args) {
			this.args = args;
		}
		public boolean isActive() {
			return active;
		}
		public void setActive(boolean active) {
			this.active = active;
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
    public Menu(String $db, String $table, HttpServletRequest request, Globals GLOBALS, SessionMap session)
    {
        this._db = $db;
        this._table = $table;
        //this.relation = new Relation(GLOBALS.dbi);
        this.session = session;
        this.cfg = GLOBALS.PMA_Config.settings;
        this.request = request;
        this.GLOBALS = GLOBALS;
    }

    /**
     * Prints the menu and the breadcrumbs
     *
     * @return void
     * @throws IOException 
     */
    public void display(HttpServletRequest request, HttpServletResponse response, Globals GLOBALS) throws IOException
    {
        response.getWriter().write(this.getDisplay(request, GLOBALS));
    }

    /**
     * Returns the menu and the breadcrumbs as a string
     *
     * @return string
     */
    public String getDisplay(HttpServletRequest request, Globals GLOBALS)
    {
        String  $retval  = this._getBreadcrumbs(request, GLOBALS);
        $retval += this._getMenu();
        return $retval;
    }

    /**
     * Returns hash for the menu and the breadcrumbs
     *
     * @return string
     */
    public String getHash()
    {
        return 
            md5(this._getMenu() + this._getBreadcrumbs(request, GLOBALS)).substring(0,8);
    }

    /**
     * Returns the menu as HTML
     *
     * @return string HTML formatted menubar
     */
    private String _getMenu()
    {
        Map<String, String> $url_params = new HashMap<>();
        String $level;
        Map $tabs;
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

        Map<String, Map<String, String>> $allowedTabs = this._getAllowedTabs($level);
        Set<Entry> entries = $tabs.entrySet();
        for (Entry $entry : entries) {
            if (! $allowedTabs.containsKey($entry.getKey())) {
                $tabs.remove($entry.getKey());
            }
        }
        return Util.getHtmlTabs($tabs, $url_params, "topmenu", true, request, GLOBALS, session);
    }

    /**
     * Returns a list of allowed tabs for the current user for the given level
     *
     * @param string $level "server", "db" or "table" level
     *
     * @return array list of allowed tabs
     */
    private Map<String, Map<String, String>> _getAllowedTabs(String $level)
    {
        String $cache_key = "menu-levels-" + $level;
        if (Util.cacheExists($cache_key, GLOBALS, session)) {
            return (Map<String, Map<String, String>>) Util.cacheGet($cache_key, null, GLOBALS, session);
        }
        Map<String, Map<String, String>> $allowedTabs = Util.getMenuTabList($level);
        /*
        TODO
        $cfgRelation = this.relation.getRelationsParam();
        if ($cfgRelation["menuswork"]) {
            $groupTable = Util.backquote($cfgRelation["db"])
                + "."
                + Util.backquote($cfgRelation["usergroups"]);
            $userTable = Util.backquote($cfgRelation["db"])
                + "." + Util.backquote($cfgRelation["users"]);

            String $sql_query = "SELECT `tab` FROM " + $groupTable
                + " WHERE `allowed` = 'N'"
                + " AND `tab` LIKE '" + $level + "%'"
                + " AND `usergroup` = (SELECT usergroup FROM "
                + $userTable + " WHERE `username` = '"
                + GLOBALS.dbi.escapeString(cfg.get("Server")["user"]) + "')";

            $result = this.relation.queryAsControlUser($sql_query, false);
            if ($result) {
                while ($row = GLOBALS.dbi.fetchAssoc($result)) {
                    $tabName = mb_substr(
                        $row["tab"],
                        mb_strpos($row["tab"], "_") + 1
                    );
                    unset($allowedTabs[$tabName]);
                }
            }
        }*/
        Util.cacheSet($cache_key, $allowedTabs, GLOBALS, session);
        return $allowedTabs;
    }

    /**
     * Returns the breadcrumbs as HTML
     *
     * @return string HTML formatted breadcrumbs
     */
    private String  _getBreadcrumbs(HttpServletRequest request, Globals GLOBALS)
    {
        String $retval = "";
        boolean $tbl_is_view = GLOBALS.dbi.getTable(this._db, this._table)
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

        if (Util.showText("TabsMode", GLOBALS)) {
            $item += "%4$s: ";
        }
        $item += "%3$s</a>";
        $retval += "<div id='floating_menubar'></div>";
        $retval += "<div id='serverinfo'>";
        Map<String, Object> params = new HashMap<>();
        params.put("class", "item");
        if (Util.showIcons("TabsMode", GLOBALS)) {
            $retval += Util.getImage(
                "s_host",
                "",
                params
            );
        }
        String $scriptName = Util.getScriptNameForOption(
            (String) cfg.get("DefaultTabServer"),
            "server", request, GLOBALS
        );
        $retval += String.format(
            $item,
            $scriptName,
            Url.getCommon(null, $scriptName.contains( "?") ? "&" : "?", request, GLOBALS),
            htmlspecialchars($server_info),
            __("Server")
        );

        /* TODO
        if (!empty(this._db)) {
            $retval += $separator;
            if (Util.showIcons("TabsMode", GLOBALS)) {
                $retval += Util.getImage(
                    "s_db",
                    "",
                    params
                );
            }
            $scriptName = Util.getScriptNameForOption(
                (String) cfg.get("DefaultTabDatabase"),
                "database"
            );
            Map<String, Object> paramsDb = new HashMap<>();
            paramsDb.put("db", this._db);
            $retval += String.format(
                $item,
                $scriptName,
                Url.getCommon(paramsDb, $scriptName.contains( "?") ? "&" : "?", request, GLOBALS),
                htmlspecialchars(this._db),
                __("Database")
            );
            // if the table is being dropped, $_REQUEST["purge"] is set to "1"
            // so do not display the table name in upper div
            if (! empty(this._table))
                && ! ("1".equals(request.getParameter("purge")))
            ) {
                Object $table_class_object = GLOBALS.dbi.getTable(
                    GLOBALS.db,
                    GLOBALS.table
                );
                if ($table_class_object.isView()) {
                    $tbl_is_view = true;
                    $show_comment = null;
                } else {
                    $tbl_is_view = false;
                    $show_comment = $table_class_object.getComment();
                }
                $retval += $separator;
                if (Util.showIcons("TabsMode", GLOBALS)) {
                    String $icon = $tbl_is_view ? "b_views" : "s_tbl";
                    $retval += Util.getImage(
                        $icon,
                        "",
                        params
                    );
                }
                $scriptName = Util.getScriptNameForOption(
                    (String) cfg.get("DefaultTabTable"),
                    "table"
                );
                paramsDb.put("table", this._table);
                $retval += String.format(
                    $item,
                    $scriptName,
                    Url.getCommon(paramsDb, $scriptName.contains( "?") ? "&" : "?", request, GLOBALS),
                    htmlspecialchars(this._table).replace(" ", "&nbsp;"),
                    $tbl_is_view ? __("View") : __("Table")
                );

                // Displays table comment
                 
                if (! empty($show_comment)
                    && ! isset(GLOBALS.avoid_show_comment)
                ) {
                    if (mb_strstr($show_comment, "; InnoDB free")) {
                        $show_comment = preg_replace(
                            "@; InnoDB free:.*?$@",
                            "",
                            $show_comment
                        );
                    }
                    $retval += "<span class='table_comment'";
                    $retval += " id='span_table_comment'>";
                    $retval += String.format(
                        __("â€œ%sâ€�"),
                        htmlspecialchars($show_comment)
                    );
                    $retval += "</span>";
                } // end if
            } else {
                // no table selected, display database comment if present
                $cfgRelation = this.relation.getRelationsParam();

                // Get additional information about tables for tooltip is done
                // in Util.getDbInfo() only once
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
                }
            }
        }*/
        $retval += "<div class='clearfloat'></div>";
        $retval += "</div>";
        return $retval;
    }
    
    /**
     * Returns the table tabs as an array
     *
     * @return array Data for generating table tabs
     */
    private Map _getTableTabs()
    {

        boolean $db_is_system_schema = GLOBALS.dbi.isSystemSchema(this._db);
        boolean $tbl_is_view = GLOBALS.dbi.getTable(this._db, this._table)
            .isView();
        boolean $updatable_view = false;
        if ($tbl_is_view) {
            $updatable_view = GLOBALS.dbi.getTable(this._db, this._table)
                .isUpdatableView();
        }
        boolean $is_superuser = GLOBALS.dbi.isSuperuser();
        boolean $isCreateOrGrantUser = GLOBALS.dbi.isUserType("grant")
            || GLOBALS.dbi.isUserType("create");

        Map $tabs = new HashMap<>();

/*        $tabs.put(key, value)
        Map params = new HashMap();
        params.put("pos", 0);
        
        $tabs.put("browse", new MenuStruct("b_browse",
        		Url.getFromRoute("/sql", session, request, GLOBALS),
        		__("Browse"),
        		GLOBALS.route.equals("/sql"),
        		params
        		));

        $tabs.put("structure", new MenuStruct("b_props",
        		Url.getFromRoute("/table/structure", session, request, GLOBALS),
        		__("Structure"),
        		GLOBALS.route.equals("/sql"),
        		params
        		));

        $tabs["structure"]["icon"] = "b_props";
        $tabs["structure"]["link"] = Url.getFromRoute("/table/structure");
        $tabs["structure"]["text"] = __("Structure");
        $tabs["structure"]["active"] = in_array(GLOBALS.route, [
            "/table/relation",
            "/table/structure",
        ]);

        $tabs["sql"]["icon"] = "b_sql";
        $tabs["sql"]["link"] = Url.getFromRoute("/table/sql");
        $tabs["sql"]["text"] = __("SQL");
        $tabs["sql"]["active"] = GLOBALS.route.equals("/table/sql";

        $tabs["search"]["icon"] = "b_search";
        $tabs["search"]["text"] = __("Search");
        $tabs["search"]["link"] = Url.getFromRoute("/table/search");
        $tabs["search"]["active"] = in_array(GLOBALS.route, [
            "/table/find_replace",
            "/table/search",
            "/table/zoom_select",
        ]);

        if (! $db_is_system_schema && (! $tbl_is_view || $updatable_view)) {
            $tabs["insert"]["icon"] = "b_insrow";
            $tabs["insert"]["link"] = Url.getFromRoute("/table/change");
            $tabs["insert"]["text"] = __("Insert");
            $tabs["insert"]["active"] = GLOBALS.route.equals("/table/change";
        }

        $tabs["export"]["icon"] = "b_tblexport";
        $tabs["export"]["link"] = Url.getFromRoute("/table/export");
        $tabs["export"]["args"]["single_table"] = "true";
        $tabs["export"]["text"] = __("Export");
        $tabs["export"]["active"] = GLOBALS.route.equals("/table/export";

        // Don"t display "Import" for views and information_schema
        
        if (! $tbl_is_view && ! $db_is_system_schema) {
            $tabs["import"]["icon"] = "b_tblimport";
            $tabs["import"]["link"] = Url.getFromRoute("/table/import");
            $tabs["import"]["text"] = __("Import");
            $tabs["import"]["active"] = GLOBALS.route.equals("/table/import";
        }
        if (($is_superuser || $isCreateOrGrantUser)
            && ! $db_is_system_schema
        ) {
            $tabs["privileges"]["link"] = Url.getFromRoute("/server/privileges");
            $tabs["privileges"]["args"]["checkprivsdb"] = this._db;
            $tabs["privileges"]["args"]["checkprivstable"] = this._table;
            // stay on table view
            $tabs["privileges"]["args"]["viewing_mode"] = "table";
            $tabs["privileges"]["text"] = __("Privileges");
            $tabs["privileges"]["icon"] = "s_rights";
            $tabs["privileges"]["active"] = GLOBALS.route.equals("/server/privileges";
        }
        // Don"t display "Operations" for views and information_schema
       
        if (! $tbl_is_view && ! $db_is_system_schema) {
            $tabs["operation"]["icon"] = "b_tblops";
            $tabs["operation"]["link"] = Url.getFromRoute("/table/operations");
            $tabs["operation"]["text"] = __("Operations");
            $tabs["operation"]["active"] = GLOBALS.route.equals("/table/operations";
        }
        // Views support a limited number of operations
        if ($tbl_is_view && ! $db_is_system_schema) {
            $tabs["operation"]["icon"] = "b_tblops";
            $tabs["operation"]["link"] = Url.getFromRoute("/view/operations");
            $tabs["operation"]["text"] = __("Operations");
            $tabs["operation"]["active"] = GLOBALS.route.equals("/view/operations";
        }

        if (Tracker.isActive() && ! $db_is_system_schema) {
            $tabs["tracking"]["icon"] = "eye";
            $tabs["tracking"]["text"] = __("Tracking");
            $tabs["tracking"]["link"] = Url.getFromRoute("/table/tracking");
            $tabs["tracking"]["active"] = GLOBALS.route.equals("/table/tracking";
        }
        if (! $db_is_system_schema
            && Util.currentUserHasPrivilege(
                "TRIGGER",
                this._db,
                this._table
            )
            && ! $tbl_is_view
        ) {
            $tabs["triggers"]["link"] = Url.getFromRoute("/table/triggers");
            $tabs["triggers"]["text"] = __("Triggers");
            $tabs["triggers"]["icon"] = "b_triggers";
            $tabs["triggers"]["active"] = GLOBALS.route.equals("/table/triggers";
        }*/

        return $tabs;
    }

    /**
     * Returns the db tabs as an array
     *
     * @return array Data for generating db tabs
     */
    private Map<String, MenuStruct> _getDbTabs()
    {
        boolean $db_is_system_schema = GLOBALS.dbi.isSystemSchema(this._db);
        int $num_tables = GLOBALS.dbi.getTables(this._db).size();
        boolean $is_superuser = GLOBALS.dbi.isSuperuser();
        boolean $isCreateOrGrantUser = GLOBALS.dbi.isUserType("grant")
            || GLOBALS.dbi.isUserType("create");

        /**
         * Gets the relation settings
         */
        //TODO $cfgRelation = this.relation.getRelationsParam();

        Map $tabs = new HashMap();

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

            if (Util.currentUserHasPrivilege("EVENT", this._db)) {
                $tabs["events"]["link"] = Url.getFromRoute("/database/events");
                $tabs["events"]["text"] = __("Events");
                $tabs["events"]["icon"] = "b_events";
                $tabs["events"]["active"] = GLOBALS.route.equals("/database/events";
            }

            if (Util.currentUserHasPrivilege("TRIGGER", this._db)) {
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
    	boolean $is_superuser = GLOBALS.dbi.isSuperuser();
    	boolean $isCreateOrGrantUser = GLOBALS.dbi.isUserType("grant")
            || GLOBALS.dbi.isUserType("create");
        
    	/* MYSQL Specific!!!
    	Map $binary_logs;
    	if (Util.cacheExists("binary_logs", session)) {
            $binary_logs = (Map) Util.cacheGet("binary_logs", null, session);
        } else {
            $binary_logs = GLOBALS.dbi.fetchResult(
                "SHOW MASTER LOGS",
                "Log_name",
                null,
                DatabaseInterface.CONNECT_USER,
                DatabaseInterface.QUERY_STORE
            );
            Util.cacheSet("binary_logs", $binary_logs, session);
        }*/

        Map $tabs = new HashMap();
        $tabs.putAll((Map)menuProperties.get("server"));
        
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
}
