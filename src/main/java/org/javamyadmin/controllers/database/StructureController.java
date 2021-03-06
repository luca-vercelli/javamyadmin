package org.javamyadmin.controllers.database;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;
import javax.servlet.ServletException;

import org.javamyadmin.helpers.Table;
import org.javamyadmin.helpers.config.PageSettings;
import org.javamyadmin.jtwig.JtwigFactory;
import org.javamyadmin.php.Array;
import org.javamyadmin.php.Globals;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.javamyadmin.php.Php.*;

/**
 * Handles database structure logic
 *
 * @package PhpMyAdmin\Controllers
 */
@RestController
public class StructureController extends AbstractController {

    /**
     * @var int Number of tables
     */
    protected int numTables;

    /**
     * @var int Current position in the list
     */
    protected int position;

    /**
     * @var bool DB is information_schema
     */
    protected boolean dbIsSystemSchema;

    /**
     * @var int Number of tables
     */
    protected int totalNumTables;

    /**
     * @var array Tables in the database
     */
    protected Map<Integer, Map<String, String>> tables; //TODO was array<Table>

    /**
     * @var bool whether stats show or not
     */
    protected boolean isShowStats;

    /**
     * @var Relation
     */
    //private Relation $relation; //TODO ?

    /**
     * @var Replication
     */
    //private Replication $replication; //TODO ?

	@Autowired
	private BeanFactory beanFactory;
	
	/**
     * Retrieves database information for further use
     *
     * @param string $subPart Page part name
     *
     * @return void
	 * @throws SQLException 
     */
    private void getDatabaseInfo(String $subPart) throws SQLException
    {
        // [$tables, $numTables, $totalNumTables,, $isShowStats, $dbIsSystemSchema,,, $position]
        Object[] ret = util.getDbInfo(this.db, $subPart, httpRequest, GLOBALS, $_SESSION);
        this.tables = (Map<Integer, Map<String, String>>) ret[0];
        this.numTables = (int) ret[1];
        this.position = (Integer) ret[8];
        this.dbIsSystemSchema = (boolean) ret[5];
        this.totalNumTables = (Integer) ret[2];
        this.isShowStats = (boolean) ret[4];
    }

    /**
     * Index action
     *
     * @param array $parameters Request parameters
     *
     * @return string HTML
     * @throws IOException 
     * @throws ServletException 
     * @throws SQLException 
     * @throws NamingException 
     */
    @RequestMapping(value = "/structure")
	public String index(Map $parameters) throws ServletException, IOException, SQLException, NamingException
	{
    	commons.execute();
    	
        // TODO require_once ROOT_PATH + "libraries/db_common.inc.php";
        this.response.getHeader().getScripts().addFiles(new String[] {
            "database/structure.js",
            "table/change.js",
        });
        // Drops/deletes/etc. multiple tables if required
        if ((!empty($parameters.get("submit_mult")) && !empty($parameters.get("selected_tbl")))
            || !empty($parameters.get("mult_btn"))
        ) {
            this.multiSubmitAction();
        }
        // Gets the database structure
        this.getDatabaseInfo("_structure");
        // Checks if there are any tables to be shown on current page.
        // If there are no tables, the user is redirected to the last page
        // having any.
        if (this.totalNumTables > 0 && this.position > this.totalNumTables) {
        	Map<String, String> map = new HashMap();
        	map.put("db", this.db);
        	map.put("pos", Integer.toString(Math.max(0, this.totalNumTables - new Integer((String)config.get("MaxTableList")))));
        	map.put("reload", "1");
            String $uri = "database/structure" + url.getCommonRaw(map, "&");
            core.sendHeaderLocation($uri, false, httpRequest, httpResponse);
        }
        // TODO include_once ROOT_PATH + "libraries/replication.inc.php";
        PageSettings.showGroup("DbStructure", beanFactory);
        String $tableList = null;
        String $listNavigator = null;
        if (this.numTables > 0) {
            Map<String, Object> $urlParams = new HashMap<>();
            $urlParams.put("db", this.db);
            $urlParams.put("pos", this.position);
            if (!empty($parameters.get("sort"))) {
                $urlParams.put("sort", $parameters.get("sort"));
            }
            if (!empty($parameters.get("sort_order"))) {
                $urlParams.put("sort_order", $parameters.get("sort_order"));
            }
            $listNavigator = util.getListNavigator(
                this.totalNumTables,
                this.position,
                $urlParams,
                url.getFromRoute("/database/structure"),
                "frame_content",
                config.get("MaxTableList")
            );
            $tableList = this.displayTableList();
        }
        String $createTable = "";
        if (empty(this.dbIsSystemSchema)) {
            // TODO $createTable = CreateTable.getHtml(this.db);
        }
        Map<String, Object> model = new HashMap<>();
        model.put("database", this.db);
        model.put("has_tables", this.numTables > 0);
        model.put("list_navigator_html", $listNavigator);
        model.put("table_list_html", $tableList);
        model.put("is_system_schema", ! empty(this.dbIsSystemSchema));
        model.put("create_table_html", $createTable);
        return template.render("database/structure/index", model);
    }
    
    private void multiSubmitAction() {
		// TODO Auto-generated method stub
		
	}

	/**
     * Handles request for real row count on database level view page.
     *
     * @param array $parameters Request parameters
     *
     * @return array JSON
     * @throws IOException 
     * @throws ServletException 
	 * @throws NamingException 
	 * @throws SQLException 
     */
    @RequestMapping(value = "/structure/real-row-count")
    public Map<String, Object> handleRealRowCountRequestAction(@RequestParam String table, @RequestParam String real_row_count_all) throws ServletException, IOException, SQLException, NamingException
    {
    	commons.execute();
    	
        // TODO require_once ROOT_PATH + "libraries/db_common.inc.php";
        if (! this.response.isAjax()) {
            return new HashMap<>();
        }
        // If there is a request to update all table"s row count.
        if (empty(real_row_count_all)) {
            // Get the real row count for the table.
            int $realRowCount_int = this.getDbi()
                .getTable(this.db, table)
                .getRealRowCountTable();
            // Format the number.
            String $realRowCount = util.formatNumber(new Double($realRowCount_int), 0);
            Map<String, Object> map = new HashMap<>();
            map.put("real_row_count", $realRowCount);
            return map;
        }
        // Array to store the results.
        Map<String, Object> $realRowCountAll = new HashMap<>();
        // Iterate over each table and fetch real row count.
        for (Map<String, String> $table : this.tables.values()) {
            int $rowCount = this.getDbi()
                .getTable(this.db, (String) $table.get("TABLE_NAME"))
                .getRealRowCountTable();
            $realRowCountAll.put("table", $table.get("TABLE_NAME"));
            $realRowCountAll.put("row_count", $rowCount);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("real_row_count_all", json_encode($realRowCountAll));
        return map;
    }
    
    /**
     * Displays the list of tables
     *
     * @return string HTML
     */
    protected String displayTableList()
    {
        String $html = "";
        // filtering
        Map<String, Object> model = new HashMap<>();
        model.put("filter_value", "");
        $html += JtwigFactory.render("filter", model);
        int $i = 0;
        int $sum_entries = 0;
        boolean $overhead_check = false;
        //String $create_time_all = "";
        //String $update_time_all = "";
        //String $check_time_all = "";
        int PropertiesNumColumns = new Integer((String) config.get("PropertiesNumColumns"));
        int $num_columns = PropertiesNumColumns > 1
            ? (int)Math.ceil(this.numTables / PropertiesNumColumns) + 1
            : 0;
        int $row_count      = 0;
        int $sum_size       = 0;
        int $overhead_size  = 0;
        List<String> $hidden_fields = new ArrayList<>();
        boolean $overall_approx_rows = false;
        List<Map<String,Object>> $structure_table_rows = new ArrayList<>();
        for (Integer $keyname : this.tables.keySet()) {
        	Map<String, String> $current_table = this.tables.get($keyname);
            // Get valid statistics whatever is the table type
            String $drop_query = "";
            String $drop_message = "";
            String $overhead = "";
            String[] $input_class = new String[] {"checkall"};
            boolean $table_is_view = false;
            // Sets parameters for links
            Map<String, Object> $tableUrlParams = new HashMap<>();
            $tableUrlParams.put("db", this.db);
            $tableUrlParams.put("table", $current_table.get("TABLE_NAME"));
            // do not list the previous table"s size info for a view
            /*[
                $current_table,
                $formatted_size,
                $unit,
                $formatted_overhead,
                $overhead_unit,
                $overhead_size,
                $table_is_view,
                $sum_size,
            ] = this.getStuffForEngineTypeTable(
                    $current_table,
                    $sum_size,
                    $overhead_size
                );*/
            Table $curTable = this.getDbi()
                .getTable(this.db, (String) $current_table.get("TABLE_NAME"));
            /*if (! $curTable.isMerge()) {
                $sum_entries += (Integer)$current_table.get("TABLE_ROWS");
            }*/
            String $collationDefinition = "---";
            /*if (!empty($current_table.get("Collation"))) {
                String $tableCollation = Charsets.findCollationByName(
                    this.dbi,
                    multiget(config, "Server", "DisableIS"),
                    $current_table.get("Collation")
                );
                if ($tableCollation != null) {
                    Map model1 = new HashMap();
                    model1.put("valueTitle", $tableCollation.getDescription());
                    model1.put("value", $tableCollation.getName());
                    $collationDefinition = JTwigFactory.render("database/structure/collation_definition", model1);
                }
            }*/
            /*if (this.isShowStats) {
                $overhead = "-";
                if ($formatted_overhead != "") {
                    Map model = new HashMap();
                    $overhead = JTwigFactory.render("database/structure/overhead", [
                        "table_url_params" => $tableUrlParams,
                        "formatted_overhead" => $formatted_overhead,
                        "overhead_unit" => $overhead_unit,
                    ]);
                    $overhead_check = true;
                    $input_class[] = "tbl-overhead";
                }
            }*/
            /*if (config["ShowDbStructureCharset"]) {
                $charset = "";
                if (isset($tableCollation)) {
                    $charset = $tableCollation.getCharset();
                }
            }*/
            /*if (config["ShowDbStructureCreation"]) {
                $create_time = $current_table["Create_time"] ?? "";
                if ($create_time
                    && (! $create_time_all
                    || $create_time < $create_time_all)
                ) {
                    $create_time_all = $create_time;
                }
            }*/
            /*if (config["ShowDbStructureLastUpdate"]) {
                $update_time = $current_table["Update_time"] ?? "";
                if ($update_time
                    && (! $update_time_all
                    || $update_time < $update_time_all)
                ) {
                    $update_time_all = $update_time;
                }
            }*/
            /*if (config["ShowDbStructureLastCheck"]) {
                $check_time = $current_table["Check_time"] ?? "";
                if ($check_time
                    && (! $check_time_all
                    || $check_time < $check_time_all)
                ) {
                    $check_time_all = $check_time;
                }
            }*/
            String $truename = (String) $current_table.get("TABLE_NAME");
            $i++;
            $row_count++;
            if ($table_is_view) {
                $hidden_fields.add("<input type='hidden' name='views[]' value='"
                    + htmlspecialchars((String)$current_table.get("TABLE_NAME")) + "'>");
            }
            /*
             * Always activate links for Browse, Search and Empty, even if
             * the icons are greyed, because
             * 1. for views, we don"t know the number of rows at this point
             * 2. for tables, another source could have populated them since the
             *    page was generated
             *
             * I could have used the PHP ternary conditional operator but I find
             * the code easier to read without this operator.
             */
            boolean $may_have_rows = true ; // (Integer)$current_table.get("TABLE_ROWS") > 0 || $table_is_view;
            Map<String, String> $titles = util.buildActionTitles(GLOBALS, $_SESSION);
            if (! this.dbIsSystemSchema) {
                $drop_query = String.format(
                    "DROP %s %s",
                    $table_is_view || $current_table.get("ENGINE") == null ? "VIEW"
                    : "TABLE",
                    util.backquote(
                        (String) $current_table.get("TABLE_NAME")
                    )
                );
                $drop_message = String.format(
                    ($table_is_view || $current_table.get("ENGINE") == null
                        ? __("View %s has been dropped.")
                        : __("Table %s has been dropped.")),
                    htmlspecialchars((String)$current_table.get("TABLE_NAME"))
                    	.replace(" ", "&nbsp;")
                );
            }
            if ($num_columns > 0
                && this.numTables > $num_columns
                && ($row_count % $num_columns) == 0
            ) {
                $row_count = 1;
                Map<String, Object> model1 = new HashMap<>();
                model1.put("db", this.db);
                model1.put("db_is_system_schema", this.dbIsSystemSchema);
                model1.put("replication", null);
                model1.put("properties_num_columns", config.get("PropertiesNumColumns"));
                model1.put("is_show_stats", config.get("is_show_stats"));
                model1.put("show_charset", config.get("ShowDbStructureCharset"));
                model1.put("show_comment", config.get("ShowDbStructureComment"));
                model1.put("show_creation", config.get("ShowDbStructureCreation"));
                model1.put("show_last_update", config.get("ShowDbStructureLastUpdate"));
                model1.put("show_last_check", config.get("ShowDbStructureLastCheck"));
                model1.put("num_favorite_tables", config.get("NumFavoriteTables"));
                model1.put("structure_table_rows", $structure_table_rows);
                $html += JtwigFactory.render("database/structure/table_header", model1);
                $structure_table_rows = new ArrayList<>();
            }
            /*[$approx_rows, $show_superscript] = this.isRowCountApproximated(
                $current_table,
                $table_is_view
            );*/
            boolean $approx_rows = false;
            String $show_superscript = "";
            /*[$do, $ignored] = this.getReplicationStatus($truename);*/
            boolean $do = true;
            boolean $ignored = false;
            Map<String, Object> $row = (Map)new Array();
            $structure_table_rows.add($row);
            $row.put("table_name_hash", md5((String)$current_table.get("TABLE_NAME")));
            $row.put("db_table_name_hash", md5(this.db + "." + $current_table.get("TABLE_NAME")));
            $row.put("db", this.db);
            $row.put("curr", $i);
            $row.put("input_class", String.join(" ", $input_class));
            $row.put("table_is_view", $table_is_view);
            $row.put("current_table", $current_table);
            $row.put("browse_table_title", $may_have_rows ? $titles.get("Browse") : $titles.get("NoBrowse"));
            $row.put("search_table_title", $may_have_rows ? $titles.get("Search") : $titles.get("NoSearch"));
            $row.put("browse_table_label_title", htmlspecialchars((String)$current_table.get("TABLE_COMMENT")));
            $row.put("browse_table_label_truename", $truename);
            $row.put("empty_table_sql_query", "TRUNCATE " + util.backquote(
                (String)$current_table.get("TABLE_NAME")
            ));
            $row.put("empty_table_message_to_show", urlencode(
                String.format(
                    __("Table %s has been emptied."),
                    htmlspecialchars(
                        (String)$current_table.get("TABLE_NAME")
                    )
                )
            ));
            $row.put("empty_table_title", $may_have_rows ? $titles.get("Empty") : $titles.get("NoEmpty"));
            $row.put("tracking_icon", this.getTrackingIcon($truename));
            $row.put("server_slave_status", null);
            $row.put("table_url_params", $tableUrlParams);
            $row.put("db_is_system_schema", this.dbIsSystemSchema);
            $row.put("titles", $titles);
            $row.put("drop_query", $drop_query);
            $row.put("drop_message", $drop_message);
            $row.put("collation", $collationDefinition);
//            $row.put("formatted_size", $formatted_size);
//            $row.put("unit", $unit);
            $row.put("overhead", $overhead);
//            $row.put("create_time", isset($create_time) && $create_time
//                    ? util.localisedDate(strtotime($create_time)) : "-");
//            $row.put("update_time", isset($update_time) && $update_time
//                    ? util.localisedDate(strtotime($update_time)) : "-");
//            $row.put("check_time", isset($check_time) && $check_time
//                    ? util.localisedDate(strtotime($check_time)) : "-");
//            $row.put("charset", $charset);
            $row.put("is_show_stats", this.isShowStats);
            $row.put("ignored", $ignored);
            $row.put("doit", $do);
            $row.put("approx_rows", $approx_rows);
            $row.put("show_superscript", $show_superscript);
            $row.put("already_favorite", this.checkFavoriteTable(
                $current_table.get("TABLE_NAME"), $_SESSION, GLOBALS
            ));
            $row.put("num_favorite_tables", config.get("NumFavoriteTables"));
            $row.put("properties_num_columns", config.get("PropertiesNumColumns"));
            $row.put("limit_chars", config.get("LimitChars"));
            $row.put("show_charset", config.get("ShowDbStructureCharset"));
            $row.put("show_comment", config.get("ShowDbStructureComment"));
            $row.put("show_creation", config.get("ShowDbStructureCreation"));
            $row.put("show_last_update", config.get("ShowDbStructureLastUpdate"));
            $row.put("show_last_check", config.get("ShowDbStructureLastCheck"));
//            $overall_approx_rows = $overall_approx_rows || $approx_rows;
        }
        /*$databaseCollation = [];
        $databaseCharset = "";
        $collation = Charsets.findCollationByName(
            this.dbi,
            config["Server"]["DisableIS"],
            this.dbi.getDbCollation(this.db)
        );
        if ($collation !== null) {
            $databaseCollation = [
                "name" => $collation.getName(),
                "description" => $collation.getDescription(),
            ];
            $databaseCharset = $collation.getCharset();
        }*/
        // table form
        Map<String, Object> body_for_table_summary = new HashMap<>();
        body_for_table_summary.put("num_tables", this.numTables);
//        body_for_table_summary.put("server_slave_status", $GLOBALS.get("replication_info").get("slave").get("status"));
        body_for_table_summary.put("db_is_system_schema", this.dbIsSystemSchema);
        body_for_table_summary.put("sum_entries", $sum_entries);
//        body_for_table_summary.put("database_collation", $databaseCollation);
        body_for_table_summary.put("is_show_stats", this.isShowStats);
//        body_for_table_summary.put("database_charset", $databaseCharset);
        body_for_table_summary.put("sum_size", $sum_size);
        body_for_table_summary.put("overhead_size", $overhead_size);
//        body_for_table_summary.put("create_time_all", $create_time_all ? util.localisedDate(strtotime($create_time_all)) : "-");
//        body_for_table_summary.put("update_time_all", $update_time_all ? util.localisedDate(strtotime($update_time_all)) : "-");
//        body_for_table_summary.put("check_time_all", $check_time_all ? util.localisedDate(strtotime($check_time_all)) : "-");
        body_for_table_summary.put("approx_rows", $overall_approx_rows);
        body_for_table_summary.put("num_favorite_tables", config.get("NumFavoriteTables"));
        body_for_table_summary.put("db", GLOBALS.getDb());
        body_for_table_summary.put("properties_num_columns", config.get("PropertiesNumColumns"));
        body_for_table_summary.put("dbi", this.getDbi());
        body_for_table_summary.put("show_charset", config.get("ShowDbStructureCharset"));
        body_for_table_summary.put("show_comment", config.get("ShowDbStructureComment"));
        body_for_table_summary.put("show_creation", config.get("ShowDbStructureCreation"));
        body_for_table_summary.put("show_last_update", config.get("ShowDbStructureLastUpdate"));
        body_for_table_summary.put("show_last_check", config.get("ShowDbStructureLastCheck"));
        Map<String, Object> check_all_tables = new HashMap<>();
        check_all_tables.put("pma_theme_image", GLOBALS.getPmaThemeImage());
        check_all_tables.put("text_dir", GLOBALS.getTextDir());
        check_all_tables.put("overhead_check", $overhead_check);
        check_all_tables.put("db_is_system_schema", this.dbIsSystemSchema);
        check_all_tables.put("hidden_fields", $hidden_fields);
        check_all_tables.put("disable_multi_table", config.get("DisableMultiTableMaintenance"));
//        check_all_tables.put("central_columns_work", GLOBALS.cfgRelation.get("centralcolumnswork"));
        model.clear();;
        model.put("db", this.db);
        model.put("db_is_system_schema", this.dbIsSystemSchema);
//        model.put("replication", $GLOBALS.get("replication_info").get("slave").get("status"));
        model.put("properties_num_columns", config.get("PropertiesNumColumns"));
        model.put("is_show_stats", this.isShowStats);
        model.put("show_charset", config.get("ShowDbStructureCharset"));
        model.put("show_comment", config.get("ShowDbStructureComment"));
        model.put("show_creation", config.get("ShowDbStructureCreation"));
        model.put("show_last_update", config.get("ShowDbStructureLastUpdate"));
        model.put("show_last_check", config.get("ShowDbStructureLastCheck"));
        model.put("num_favorite_tables", config.get("NumFavoriteTables"));
        model.put("structure_table_rows", $structure_table_rows);
        model.put("body_for_table_summary", body_for_table_summary);
        model.put("check_all_tables", check_all_tables);
        
        $html += JtwigFactory.render("database/structure/table_header", model);
        return $html;
    }
    /**
     * Returns the tracking icon if the table is tracked
     *
     * @param string $table table name
     *
     * @return string HTML for tracking icon
     */
    protected String getTrackingIcon(String $table)
    {
        String $tracking_icon = "";
        /* TODO if (Tracker.isActive()) {
            $is_tracked = Tracker.isTracked(this.db, $table);
            if ($is_tracked
                || Tracker.getVersion(this.db, $table) > 0
            ) {
                Map model = new HashMap();
                model.put("db", this.db);
                model.put("table", $table);
                model.put("is_tracked", $is_tracked);
                $tracking_icon = JTwigFactory.render("database/structure/tracking_icon", model);
            }
        }*/
        return $tracking_icon;
    }
    
    /**
     * Find table with truename
     *
     * @param array  $db       DB to look into
     * @param string $truename Table name
     *
     * @return bool
     */
    protected boolean hasTable(List<String> $db, String $truename)
    {
    	/* TODO
        for (String $db_table: $db) {
            if (this.db == this.replication.extractDbOrTable($db_table)
                && preg_match(
                    "@^" .
                    preg_quote(mb_substr(this.replication.extractDbOrTable($db_table, "table"), 0, -1), "@") + "@",
                    $truename
                )
            ) {
                return true;
            }
        }*/
        return false;
    }

    /**
     * Function to check if a table is already in favorite list.
     *
     * @param string $currentTable current table
     *
     * @return bool
     */
    protected boolean checkFavoriteTable(String $currentTable, SessionMap $_SESSION, Globals GLOBALS)
    {
    	/* TODO
        // ensure $_SESSION['tmpval']['favoriteTables'] is initialized
        RecentFavoriteTable.getInstance("favorite");
        Map $favoriteTables = (Map) multiget($_SESSION, "tmpval", "favoriteTables", GLOBALS.getServer());
        for (Object $value : $favoriteTables.values()) {
            if (this.db.equals($value.get("db")) && $currentTable.equals($value.get("table"))) {
                return true;
            }
        }*/
        return false;
    }
}
