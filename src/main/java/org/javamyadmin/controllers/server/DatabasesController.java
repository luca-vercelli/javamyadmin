package org.javamyadmin.controllers.server;

import static org.javamyadmin.php.Php.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.naming.NamingException;
import javax.servlet.ServletException;

import org.javamyadmin.controllers.AbstractController;
import org.javamyadmin.helpers.DatabaseInterface;
import org.javamyadmin.helpers.Header;
import org.javamyadmin.helpers.Response;
import org.javamyadmin.helpers.Scripts;
import org.javamyadmin.jtwig.JtwigFactory;
import org.javamyadmin.php.Array;
import org.javamyadmin.php.Globals;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
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
	public void index() throws ServletException, IOException, SQLException, NamingException {

		super.prepareResponse();
        //global $cfg, $server, $dblist, $is_create_db_priv;
        //global $replication_info, $db_to_create, $pmaThemeImage, $text_dir;
        
		Header $header = this.pmaResponse.getHeader();
        Scripts $scripts = $header.getScripts();
        $scripts.addFile("server/databases.js");
        /* TODO include_once ROOT_PATH . "libraries/replication.inc.php";
        include_once ROOT_PATH . "libraries/server_common.inc.php";
        */
        this.setSortDetails($params.get("sort_by"), $params.get("sort_order"));
        this.hasStatistics = ! empty($params.get("statistics"));
        this.position = ! empty($params.get("pos")) ? new Integer($params.get("pos")) : 0;
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
                true
            );
            this.databaseCount = Globals.getDblist().databases.size();
        }
        Array $urlParams = new Array();
        $urlParams.put("statistics", this.hasStatistics);
        $urlParams.put("pos", this.position);
        $urlParams.put("sort_by", this.sortBy);
        $urlParams.put("sort_order", this.sortOrder);
        Array $databases = this.getDatabases(/*$replication_types ?? []*/);
        //$charsetsList = [];
        if ($cfg["ShowCreateDb"] && $is_create_db_priv) {
            $charsets = Charsets.getCharsets(this.dbi, $cfg["Server"]["DisableIS"]);
            $collations = Charsets.getCollations(this.dbi, $cfg["Server"]["DisableIS"]);
            $serverCollation = this.dbi.getServerCollation();
            /** @var Charset $charset */
            foreach ($charsets as $charset) {
                $collationsList = [];
                /** @var Collation $collation */
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
        }
        $headerStatistics = this.getStatisticsColumns();		
		Map<String, Object> model = new HashMap<>();
	    model.put("is_create_database_shown", $cfg["ShowCreateDb"]);
	    model.put("has_create_database_privileges", $is_create_db_priv);
	    model.put("has_statistics", this.hasStatistics);
	    model.put("database_to_create", $db_to_create);
	    model.put("databases", $databases["databases"]);
	    model.put("total_statistics", $databases["total_statistics"]);
	    model.put("header_statistics", $headerStatistics);
	    model.put("charsets", $charsetsList);
	    model.put("database_count", this.databaseCount);
	    model.put("pos", this.position);
	    model.put("url_params", $urlParams);
	    model.put("max_db_list", $cfg["MaxDbList"]);
	    model.put("has_master_replication", $replication_info["master"]["status"]);
	    model.put("has_slave_replication", $replication_info["slave"]["status"]);
	    model.put("is_drop_allowed", this.dbi.isSuperuser() || $cfg["AllowUserDropDatabase"]);
	    model.put("pma_theme_image", $pmaThemeImage);
	    model.put("text_dir", $text_dir);

		String html = JtwigFactory.render("databases/index", model);
		pmaResponse.addHTML(html);
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
