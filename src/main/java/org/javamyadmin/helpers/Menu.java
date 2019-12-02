package org.javamyadmin.helpers;

import static org.javamyadmin.php.Php.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.javamyadmin.php.GLOBALS;

/**
 * Generates and renders the top menu
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
	private Map<String, Object> session;

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
    public Menu(String $db, String $table, Map<String, Object> session)
    {
        this._db = $db;
        this._table = $table;
        //this.relation = new Relation(GLOBALS.dbi);
        this.session = session;
    }

    /**
     * Prints the menu and the breadcrumbs
     *
     * @return void
     * @throws IOException 
     */
    public void display(HttpServletRequest request, HttpServletResponse response, GLOBALS GLOBALS) throws IOException
    {
        response.getWriter().write(this.getDisplay(request, GLOBALS));
    }

    /**
     * Returns the menu and the breadcrumbs as a string
     *
     * @return string
     */
    public String getDisplay(HttpServletRequest request, GLOBALS GLOBALS)
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
    public String getHash(HttpServletRequest request, GLOBALS GLOBALS)
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

        Map<String, Map<String, String>> $allowedTabs = this._getAllowedTabs($level);
        for (Entry<String, MenuStruct> $entry : $tabs.entrySet()) {
            if (! $allowedTabs.containsKey($entry.getKey())) {
                $tabs.remove($entry.getKey());
            }
        }
        return Util.getHtmlTabs($tabs, $url_params, "topmenu", true);
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
        if (Util.cacheExists($cache_key, session)) {
            return (Map<String, Map<String, String>>) Util.cacheGet($cache_key, null, session);
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
                + GLOBALS["dbi"].escapeString(GLOBALS.cfg.get("Server")["user"]) + "')";

            $result = this.relation.queryAsControlUser($sql_query, false);
            if ($result) {
                while ($row = GLOBALS["dbi"].fetchAssoc($result)) {
                    $tabName = mb_substr(
                        $row["tab"],
                        mb_strpos($row["tab"], "_") + 1
                    );
                    unset($allowedTabs[$tabName]);
                }
            }
        }*/
        Util.cacheSet($cache_key, $allowedTabs, session);
        return $allowedTabs;
    }

    /**
     * Returns the breadcrumbs as HTML
     *
     * @return string HTML formatted breadcrumbs
     */
    private String  _getBreadcrumbs(HttpServletRequest request, GLOBALS GLOBALS)
    {
        String $retval = "";
        boolean $tbl_is_view = GLOBALS.dbi.getTable(this._db, this._table)
            .isView();
        if (empty(multiget(GLOBALS.cfg, "Server", "host"))) {
            multiput(GLOBALS.cfg, "", "Server", "host");
        }
        String $server_info = ! empty(multiget(GLOBALS.cfg, "Server", "verbose"))
            ? (String) multiget(GLOBALS.cfg, "Server", "verbose")
            : (String) multiget(GLOBALS.cfg, "Server", "host");
        $server_info += empty(multiget(GLOBALS.cfg, "Server", "port"))
            ? ""
            : ":" + multiget(GLOBALS.cfg, "Server", "port");

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
            (String) GLOBALS.cfg.get("DefaultTabServer"),
            "server"
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
                (String) GLOBALS.cfg.get("DefaultTabDatabase"),
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
                    (String) GLOBALS.cfg.get("DefaultTabTable"),
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

    public static class MenuStruct {
    	public String icon;
    	public String text;
    	public String link;
    	public Map args;
    	public boolean active;
    }
    
    /**
     * Returns the table tabs as an array
     *
     * @return array Data for generating table tabs
     */
    private Map<String, MenuStruct> _getTableTabs()
    {
    	
        global $route;

        $db_is_system_schema = GLOBALS["dbi"].isSystemSchema(this._db);
        $tbl_is_view = GLOBALS["dbi"].getTable(this._db, this._table)
            .isView();
        $updatable_view = false;
        if ($tbl_is_view) {
            $updatable_view = GLOBALS["dbi"].getTable(this._db, this._table)
                .isUpdatableView();
        }
        $is_superuser = GLOBALS["dbi"].isSuperuser();
        $isCreateOrGrantUser = GLOBALS["dbi"].isUserType("grant")
            || GLOBALS["dbi"].isUserType("create");

        Map<String, MenuStruct> $tabs = new HashMap<>();

        $tabs["browse"]["icon"] = "b_browse";
        $tabs["browse"]["text"] = __("Browse");
        $tabs["browse"]["link"] = Url.getFromRoute("/sql");
        $tabs["browse"]["args"]["pos"] = 0;
        $tabs["browse"]["active"] = $route === "/sql";

        $tabs["structure"]["icon"] = "b_props";
        $tabs["structure"]["link"] = Url.getFromRoute("/table/structure");
        $tabs["structure"]["text"] = __("Structure");
        $tabs["structure"]["active"] = in_array($route, [
            "/table/relation",
            "/table/structure",
        ]);

        $tabs["sql"]["icon"] = "b_sql";
        $tabs["sql"]["link"] = Url.getFromRoute("/table/sql");
        $tabs["sql"]["text"] = __("SQL");
        $tabs["sql"]["active"] = $route === "/table/sql";

        $tabs["search"]["icon"] = "b_search";
        $tabs["search"]["text"] = __("Search");
        $tabs["search"]["link"] = Url.getFromRoute("/table/search");
        $tabs["search"]["active"] = in_array($route, [
            "/table/find_replace",
            "/table/search",
            "/table/zoom_select",
        ]);

        if (! $db_is_system_schema && (! $tbl_is_view || $updatable_view)) {
            $tabs["insert"]["icon"] = "b_insrow";
            $tabs["insert"]["link"] = Url.getFromRoute("/table/change");
            $tabs["insert"]["text"] = __("Insert");
            $tabs["insert"]["active"] = $route === "/table/change";
        }

        $tabs["export"]["icon"] = "b_tblexport";
        $tabs["export"]["link"] = Url.getFromRoute("/table/export");
        $tabs["export"]["args"]["single_table"] = "true";
        $tabs["export"]["text"] = __("Export");
        $tabs["export"]["active"] = $route === "/table/export";

        /**
         * Don"t display "Import" for views and information_schema
         */
        if (! $tbl_is_view && ! $db_is_system_schema) {
            $tabs["import"]["icon"] = "b_tblimport";
            $tabs["import"]["link"] = Url.getFromRoute("/table/import");
            $tabs["import"]["text"] = __("Import");
            $tabs["import"]["active"] = $route === "/table/import";
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
            $tabs["privileges"]["active"] = $route === "/server/privileges";
        }
        /**
         * Don"t display "Operations" for views and information_schema
         */
        if (! $tbl_is_view && ! $db_is_system_schema) {
            $tabs["operation"]["icon"] = "b_tblops";
            $tabs["operation"]["link"] = Url.getFromRoute("/table/operations");
            $tabs["operation"]["text"] = __("Operations");
            $tabs["operation"]["active"] = $route === "/table/operations";
        }
        /**
         * Views support a limited number of operations
         */
        if ($tbl_is_view && ! $db_is_system_schema) {
            $tabs["operation"]["icon"] = "b_tblops";
            $tabs["operation"]["link"] = Url.getFromRoute("/view/operations");
            $tabs["operation"]["text"] = __("Operations");
            $tabs["operation"]["active"] = $route === "/view/operations";
        }

        if (Tracker.isActive() && ! $db_is_system_schema) {
            $tabs["tracking"]["icon"] = "eye";
            $tabs["tracking"]["text"] = __("Tracking");
            $tabs["tracking"]["link"] = Url.getFromRoute("/table/tracking");
            $tabs["tracking"]["active"] = $route === "/table/tracking";
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
            $tabs["triggers"]["active"] = $route === "/table/triggers";
        }

        return $tabs;
    }

    /**
     * Returns the db tabs as an array
     *
     * @return array Data for generating db tabs
     */
    private Map<String, MenuStruct> _getDbTabs()
    {
        global $route;

        $db_is_system_schema = GLOBALS["dbi"].isSystemSchema(this._db);
        $num_tables = count(GLOBALS["dbi"].getTables(this._db));
        $is_superuser = GLOBALS["dbi"].isSuperuser();
        $isCreateOrGrantUser = GLOBALS["dbi"].isUserType("grant")
            || GLOBALS["dbi"].isUserType("create");

        /**
         * Gets the relation settings
         */
        $cfgRelation = this.relation.getRelationsParam();

        Map<String, MenuStruct> $tabs = new HashMap<>();

        $tabs["structure"]["link"] = Url.getFromRoute("/database/structure");
        $tabs["structure"]["text"] = __("Structure");
        $tabs["structure"]["icon"] = "b_props";
        $tabs["structure"]["active"] = $route === "/database/structure";

        $tabs["sql"]["link"] = Url.getFromRoute("/database/sql");
        $tabs["sql"]["text"] = __("SQL");
        $tabs["sql"]["icon"] = "b_sql";
        $tabs["sql"]["active"] = $route === "/database/sql";

        $tabs["search"]["text"] = __("Search");
        $tabs["search"]["icon"] = "b_search";
        $tabs["search"]["link"] = Url.getFromRoute("/database/search");
        $tabs["search"]["active"] = $route === "/database/search";
        if ($num_tables == 0) {
            $tabs["search"]["warning"] = __("Database seems to be empty!");
        }

        $tabs["query"]["text"] = __("Query");
        $tabs["query"]["icon"] = "s_db";
        $tabs["query"]["link"] = Url.getFromRoute("/database/multi_table_query");
        $tabs["query"]["active"] = $route === "/database/multi_table_query" || $route === "/database/qbe";
        if ($num_tables == 0) {
            $tabs["query"]["warning"] = __("Database seems to be empty!");
        }

        $tabs["export"]["text"] = __("Export");
        $tabs["export"]["icon"] = "b_export";
        $tabs["export"]["link"] = Url.getFromRoute("/database/export");
        $tabs["export"]["active"] = $route === "/database/export";
        if ($num_tables == 0) {
            $tabs["export"]["warning"] = __("Database seems to be empty!");
        }

        if (! $db_is_system_schema) {
            $tabs["import"]["link"] = Url.getFromRoute("/database/import");
            $tabs["import"]["text"] = __("Import");
            $tabs["import"]["icon"] = "b_import";
            $tabs["import"]["active"] = $route === "/database/import";

            $tabs["operation"]["link"] = Url.getFromRoute("/database/operations");
            $tabs["operation"]["text"] = __("Operations");
            $tabs["operation"]["icon"] = "b_tblops";
            $tabs["operation"]["active"] = $route === "/database/operations";

            if ($is_superuser || $isCreateOrGrantUser) {
                $tabs["privileges"]["link"] = Url.getFromRoute("/server/privileges");
                $tabs["privileges"]["args"]["checkprivsdb"] = this._db;
                // stay on database view
                $tabs["privileges"]["args"]["viewing_mode"] = "db";
                $tabs["privileges"]["text"] = __("Privileges");
                $tabs["privileges"]["icon"] = "s_rights";
                $tabs["privileges"]["active"] = $route === "/server/privileges";
            }

            $tabs["routines"]["link"] = Url.getFromRoute("/database/routines");
            $tabs["routines"]["text"] = __("Routines");
            $tabs["routines"]["icon"] = "b_routines";
            $tabs["routines"]["active"] = $route === "/database/routines";

            if (Util.currentUserHasPrivilege("EVENT", this._db)) {
                $tabs["events"]["link"] = Url.getFromRoute("/database/events");
                $tabs["events"]["text"] = __("Events");
                $tabs["events"]["icon"] = "b_events";
                $tabs["events"]["active"] = $route === "/database/events";
            }

            if (Util.currentUserHasPrivilege("TRIGGER", this._db)) {
                $tabs["triggers"]["link"] = Url.getFromRoute("/database/triggers");
                $tabs["triggers"]["text"] = __("Triggers");
                $tabs["triggers"]["icon"] = "b_triggers";
                $tabs["triggers"]["active"] = $route === "/database/triggers";
            }
        }

        if (Tracker.isActive() && ! $db_is_system_schema) {
            $tabs["tracking"]["text"] = __("Tracking");
            $tabs["tracking"]["icon"] = "eye";
            $tabs["tracking"]["link"] = Url.getFromRoute("/database/tracking");
            $tabs["tracking"]["active"] = $route === "/database/tracking";
        }

        if (! $db_is_system_schema) {
            $tabs["designer"]["text"] = __("Designer");
            $tabs["designer"]["icon"] = "b_relations";
            $tabs["designer"]["link"] = Url.getFromRoute("/database/designer");
            $tabs["designer"]["id"] = "designer_tab";
            $tabs["designer"]["active"] = $route === "/database/designer";
        }

        if (! $db_is_system_schema
            && $cfgRelation["centralcolumnswork"]
        ) {
            $tabs["central_columns"]["text"] = __("Central columns");
            $tabs["central_columns"]["icon"] = "centralColumns";
            $tabs["central_columns"]["link"] = Url.getFromRoute("/database/central_columns");
            $tabs["central_columns"]["active"] = $route === "/database/central_columns";
        }
        return $tabs;
    }

    /**
     * Returns the server tabs as an array
     *
     * @return array Data for generating server tabs
     */
    private Map<String, MenuStruct> _getServerTabs()
    {
        global $route;

        $is_superuser = GLOBALS["dbi"].isSuperuser();
        $isCreateOrGrantUser = GLOBALS["dbi"].isUserType("grant")
            || GLOBALS["dbi"].isUserType("create");
        if (Util.cacheExists("binary_logs")) {
            $binary_logs = Util.cacheGet("binary_logs");
        } else {
            $binary_logs = GLOBALS["dbi"].fetchResult(
                "SHOW MASTER LOGS",
                "Log_name",
                null,
                DatabaseInterface.CONNECT_USER,
                DatabaseInterface.QUERY_STORE
            );
            Util.cacheSet("binary_logs", $binary_logs);
        }

        $tabs = [];

        $tabs["databases"]["icon"] = "s_db";
        $tabs["databases"]["link"] = Url.getFromRoute("/server/databases");
        $tabs["databases"]["text"] = __("Databases");
        $tabs["databases"]["active"] = $route === "/server/databases";

        $tabs["sql"]["icon"] = "b_sql";
        $tabs["sql"]["link"] = Url.getFromRoute("/server/sql");
        $tabs["sql"]["text"] = __("SQL");
        $tabs["sql"]["active"] = $route === "/server/sql";

        $tabs["status"]["icon"] = "s_status";
        $tabs["status"]["link"] = Url.getFromRoute("/server/status");
        $tabs["status"]["text"] = __("Status");
        $tabs["status"]["active"] = in_array($route, [
            "/server/status",
            "/server/status/advisor",
            "/server/status/monitor",
            "/server/status/processes",
            "/server/status/queries",
            "/server/status/variables",
        ]);

        if ($is_superuser || $isCreateOrGrantUser) {
            $tabs["rights"]["icon"] = "s_rights";
            $tabs["rights"]["link"] = Url.getFromRoute("/server/privileges");
            $tabs["rights"]["text"] = __("User accounts");
            $tabs["rights"]["active"] = in_array($route, [
                "/server/privileges",
                "/server/user_groups",
            ]);
            $tabs["rights"]["args"]["viewing_mode"] = "server";
        }

        $tabs["export"]["icon"] = "b_export";
        $tabs["export"]["link"] = Url.getFromRoute("/server/export");
        $tabs["export"]["text"] = __("Export");
        $tabs["export"]["active"] = $route === "/server/export";

        $tabs["import"]["icon"] = "b_import";
        $tabs["import"]["link"] = Url.getFromRoute("/server/import");
        $tabs["import"]["text"] = __("Import");
        $tabs["import"]["active"] = $route === "/server/import";

        $tabs["settings"]["icon"] = "b_tblops";
        $tabs["settings"]["link"] = Url.getFromRoute("/preferences/manage");
        $tabs["settings"]["text"] = __("Settings");
        $tabs["settings"]["active"] = in_array($route, [
            "/preferences/forms",
            "/preferences/manage",
            "/preferences/twofactor",
        ]);

        if (! empty($binary_logs)) {
            $tabs["binlog"]["icon"] = "s_tbl";
            $tabs["binlog"]["link"] = Url.getFromRoute("/server/binlog");
            $tabs["binlog"]["text"] = __("Binary log");
            $tabs["binlog"]["active"] = $route === "/server/binlog";
        }

        if ($is_superuser) {
            $tabs["replication"]["icon"] = "s_replication";
            $tabs["replication"]["link"] = Url.getFromRoute("/server/replication");
            $tabs["replication"]["text"] = __("Replication");
            $tabs["replication"]["active"] = $route === "/server/replication";
        }

        $tabs["vars"]["icon"] = "s_vars";
        $tabs["vars"]["link"] = Url.getFromRoute("/server/variables");
        $tabs["vars"]["text"] = __("Variables");
        $tabs["vars"]["active"] = $route === "/server/variables";

        $tabs["charset"]["icon"] = "s_asci";
        $tabs["charset"]["link"] = Url.getFromRoute("/server/collations");
        $tabs["charset"]["text"] = __("Charsets");
        $tabs["charset"]["active"] = $route === "/server/collations";

        $tabs["engine"]["icon"] = "b_engine";
        $tabs["engine"]["link"] = Url.getFromRoute("/server/engines");
        $tabs["engine"]["text"] = __("Engines");
        $tabs["engine"]["active"] = $route === "/server/engines";

        $tabs["plugins"]["icon"] = "b_plugin";
        $tabs["plugins"]["link"] = Url.getFromRoute("/server/plugins");
        $tabs["plugins"]["text"] = __("Plugins");
        $tabs["plugins"]["active"] = $route === "/server/plugins";

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
