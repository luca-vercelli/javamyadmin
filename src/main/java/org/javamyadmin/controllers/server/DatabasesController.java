package org.javamyadmin.controllers.server;

import static org.javamyadmin.php.Php.*;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.naming.NamingException;
import javax.servlet.ServletException;

import org.javamyadmin.controllers.AbstractController;
import org.javamyadmin.helpers.Config;
import org.javamyadmin.helpers.DatabaseInterface;
import org.javamyadmin.helpers.Header;
import org.javamyadmin.helpers.Message;
import org.javamyadmin.helpers.Scripts;
import org.javamyadmin.helpers.Url;
import org.javamyadmin.helpers.Util;
import org.javamyadmin.php.Array;
import org.javamyadmin.php.Globals;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DatabasesController extends AbstractController {

    /**
     * @var array array of database details
     */
    private Array databases = new Array();
    /**
     * @var int number of databases
     */
    private int databaseCount = 0;
    /**
     * @var string sort by column
     */
    private String sortBy;
    /**
     * @var string sort order of databases
     */
    private String sortOrder;
    /**
     * @var boolean whether to show database statistics
     */
    private boolean hasStatistics;
    /**
     * @var int position in list navigation
     */
    private int position;
    
	@RequestMapping(value = "/databases")
	public void index(@RequestParam(required=false) String sort_by,
			@RequestParam(required=false) String sort_order,
			@RequestParam(required=false) String statistics,
			@RequestParam(defaultValue="0") Integer pos)
	throws ServletException, IOException, SQLException, NamingException {

		commons.execute();
        //global $cfg, $server, $dblist, $is_create_db_priv;
        //global $replication_info, $db_to_create, $pmaThemeImage, $text_dir;
        
		boolean $is_create_db_priv = GLOBALS.getIsCreateDbPriv();
		String $db_to_create = GLOBALS.getDbToCreate();
		
		Header $header = this.response.getHeader();
        Scripts $scripts = $header.getScripts();
        $scripts.addFile("server/databases.js");
        /* TODO include_once ROOT_PATH . "libraries/replication.inc.php";
        include_once ROOT_PATH . "libraries/server_common.inc.php";
        */
        this.setSortDetails(sort_by, sort_order);
        this.hasStatistics = ! empty(statistics);
        this.position = pos;
        /**
         * Gets the databases list
         */
        if (GLOBALS.getServer() > 0) {
            this.databases = GLOBALS.getDbi().getDatabasesFull(
                null,
                this.hasStatistics,
                DatabaseInterface.CONNECT_USER,
                this.sortBy,
                this.sortOrder,
                this.position,
                null
            );
            this.databaseCount = GLOBALS.getDblist().size();
        }
        Array $urlParams = new Array();
        $urlParams.put("statistics", this.hasStatistics);
        $urlParams.put("pos", this.position);
        $urlParams.put("sort_by", this.sortBy);
        $urlParams.put("sort_order", this.sortOrder);
        Array $databases = this.getDatabases(/*$replication_types ?? []*/);
        List<String> $charsetsList = new ArrayList<>();
        //$charsetsList = [];
        /*if ($cfg.get("ShowCreateDb") && $is_create_db_priv) {
            $charsets = Charsets.getCharsets(this.dbi, $cfg["Server"]["DisableIS"]);
            $collations = Charsets.getCollations(this.dbi, $cfg["Server"]["DisableIS"]);
            $serverCollation = this.dbi.getServerCollation();
            // @var Charset $charset
            foreach ($charsets as $charset) {
                $collationsList = [];
                // @var Collation $collation
                foreach ($collations[$charset.getName()] as $collation) {
                    $collationsList[] = [
                        "name" => $collation.getName(),
                        "description" => $collation.getDescription(),
                        "is_selected" => $serverCollation === $collation.getName(),
                    ];
                }
                $charsetsList[] = [
                    "name" => $charset.getName(),
                    "description" => $charset.getDescription(),
                    "collations" => $collationsList,
                ];
            }
        }*/
        String $headerStatistics = ""; //this.getStatisticsColumns();
        
		Map<String, Object> model = new HashMap<>();
	    model.put("is_create_database_shown", config.get("ShowCreateDb"));
	    model.put("has_create_database_privileges", $is_create_db_priv);
	    model.put("has_statistics", this.hasStatistics);
	    model.put("database_to_create", $db_to_create);
	    model.put("databases", $databases.get("databases"));
	    model.put("total_statistics", $databases.get("total_statistics"));
	    model.put("header_statistics", $headerStatistics);
	    model.put("charsets", $charsetsList);
	    model.put("database_count", this.databaseCount);
	    model.put("pos", this.position);
	    model.put("url_params", $urlParams);
	    model.put("max_db_list", config.get("MaxDbList"));
	    //model.put("has_master_replication", $replication_info["master"]["status"]);
	    //model.put("has_slave_replication", $replication_info["slave"]["status"]);
	    model.put("is_drop_allowed", this.getDbi().isSuperuser() || "true".equals(config.get("AllowUserDropDatabase")));
	    model.put("pma_theme_image", GLOBALS.getPmaThemeImage());
	    model.put("text_dir", GLOBALS.getTextDir());

		String html = template.render("server/databases/index", model);
		response.addHTML(html);
	}

    /**
     * Handles creating a new database
     *
     * @param array $params Request parameters
     *
     * @return array JSON
     * @throws NamingException 
     * @throws SQLException 
     * @throws IOException 
     * @throws ServletException 
     */
	@RequestMapping(value = "/create")
    public Array create(@RequestParam String new_db) throws ServletException, IOException, SQLException, NamingException
    {
		commons.execute();
    	
		Config $cfg = config;
		
		Array $json = new Array();
		
        if (empty(new_db) || ! this.response.isAjax()) {
        	$json.put("message", Message.error(null));
        	return $json;
        }

        /**
         * Builds and executes the db creation sql query
         */
        String $sqlQuery = "CREATE DATABASE " + Util.backquote(new_db);
        ResultSet $result = this.getDbi().tryQuery($sqlQuery);

        if ($result == null) {
        	// avoid displaying the not-created db name in header or navi panel
            GLOBALS.setDb(null);

            Message $message = Message.rawError(this.getDbi().getError());
            $json.put("message", $message);

            this.response.setRequestStatus(false);
        } else {
            GLOBALS.setDb(new_db);

            Message $message = Message.success(__("Database %1$s has been created."));
            $message.addParam(new_db);

            String $scriptName = Util.getScriptNameForOption(
                (String)$cfg.get("DefaultTabDatabase"),
                "database"
            );

            $json.put("message", $message);
            $json.put("sql_query", Util.getMessage(null, $sqlQuery, "success"));
            
            Map<String, String> $queryParam = new HashMap<>();
            $queryParam.put("db", new_db);
            $json.put("url_query", $scriptName + Url.getCommon($queryParam, $scriptName.contains("?") ? "&" : "?"));
            
        }

        return $json;
    }

    /**
     * Handles dropping multiple databases
     *
     * @param array $params Request parameters
     *
     * @return array JSON
     * @throws NamingException 
     * @throws SQLException 
     * @throws IOException 
     * @throws ServletException 
     */
	@RequestMapping(value = "/destroy")
    public Array destroy(@RequestParam String drop_selected_dbs, @RequestParam String selected_dbs) throws ServletException, IOException, SQLException, NamingException
    {
		commons.execute();

		Config $cfg = config;
		
        // global $submit_mult, $mult_btn, $selected, $err_url, $cfg;
		Message $message = null;
		
        if (empty(drop_selected_dbs)
            || ! this.response.isAjax()
            || (! this.getDbi().isSuperuser() && "false".equals($cfg.get("AllowUserDropDatabase")))
        ) {
            $message = Message.error(null);
        } else if (empty(selected_dbs)) {
            $message = Message.error(__("No databases selected."));
        } else {
            // for mult_submits.inc.php
            String $action = Url.getFromRoute("/server/databases");
            GLOBALS.setErrUrl($action);

            GLOBALS.setSubmitMult("drop_db");
            GLOBALS.setMultBtn(__("Yes"));

            //TODO include ROOT_PATH . "libraries/mult_submits.inc.php";

            if (empty($message)) { // no error message
                int $numberOfDatabases = GLOBALS.getSelected().size();
                $message = Message.success(
                    _ngettext(
                        "%1$d database has been dropped successfully.",
                        "%1$d databases have been dropped successfully.",
                        $numberOfDatabases
                    )
                );
                $message.addParam($numberOfDatabases);
            }
        }

        Array $json = new Array();
        if ($message instanceof Message) {
            $json.put("message", $message);
            this.response.setRequestStatus($message.isSuccess());
        }

        return $json;
    }

    /**
     * Extracts parameters sort order and sort by
     *
     * @param string|null $sortBy    sort by
     * @param string|null $sortOrder sort order
     *
     * @return void
     */
    private void setSortDetails(String $sortBy, String $sortOrder)
    {
    	// JMA: $sortBy will be ignored

		this.sortBy = "SCHEMA_NAME";
		this.sortOrder = "asc";
		if ($sortOrder != null && $sortOrder.toLowerCase().equals("desc")) {
			this.sortOrder = "desc";
		}
    }
	

    /**
     * Returns database list
     *
     * @param array $replicationTypes replication types
     *
     * @return array
     */
    private Array getDatabases(/*array $replicationTypes*/)
    {
    	// JMA: This method is far less powerful than the original one
        Array $databases = new Array();
        for (Entry<Object, Object> entry: this.databases) {
        	String db_name = (String) ((Map) entry.getValue()).get("SCHEMA_NAME");
        	Array $database_refactored = new Array();
        	$database_refactored.put("name", db_name);
        	$databases.put(db_name, $database_refactored);
        }
        Array array = new Array();
        Array $totalStatistics = new Array(); //Unsupported
        array.put("databases", $databases);
        array.put("total_statistics", $totalStatistics);
        return array;
    }
}
