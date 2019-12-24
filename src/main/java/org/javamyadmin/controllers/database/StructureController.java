package org.javamyadmin.controllers.database;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.javamyadmin.helpers.Response;
import org.javamyadmin.php.Globals;
import org.javamyadmin.php.Php.SessionMap;

/**
 * Handles database structure logic
 *
 * @package PhpMyAdmin\Controllers
 */
public class StructureController extends AbstractController {

    /**
     * @var int Number of tables
     */
    protected int $numTables;

    /**
     * @var int Current position in the list
     */
    protected int $position;

    /**
     * @var bool DB is information_schema
     */
    protected boolean $dbIsSystemSchema;

    /**
     * @var int Number of tables
     */
    protected int $totalNumTables;

    /**
     * @var array Tables in the database
     */
    protected List<?> $tables; //TODO

    /**
     * @var bool whether stats show or not
     */
    protected boolean $isShowStats;

    /**
     * @var Relation
     */
    //private Relation $relation; //TODO ?

    /**
     * @var Replication
     */
    //private Replication $replication; //TODO ?
    
	/*@Override
	protected void service(HttpServletRequest request, HttpServletResponse response, Response pmaResponse,
			SessionMap $_SESSION, Globals GLOBALS, Connection $db) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
	}*/

}
