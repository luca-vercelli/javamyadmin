package org.javamyadmin.helpers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.naming.Context;

import org.javamyadmin.php.Array;
import org.javamyadmin.php.Globals;
import static org.javamyadmin.php.Php.*;

/**
 * Main interface for database interactions
 * 
 */
public class DatabaseInterface {

	/**
	 * Force STORE_RESULT method, ignored by classic MySQL.
	 */
	public final static int QUERY_STORE = 1;
	/**
	 * Do not read whole query.
	 */
	public final static int QUERY_UNBUFFERED = 2;
	/**
	 * Get session variable.
	 */
	public final static int GETVAR_SESSION = 1;
	/**
	 * Get global variable.
	 */
	public final static int GETVAR_GLOBAL = 2;
	/**
	 * User connection.
	 */
	public final static int CONNECT_USER = 0x100;
	/**
	 * Control user connection.
	 */
	public final static int CONNECT_CONTROL = 0x101;
	/**
	 * Auxiliary connection.
	 *
	 * Used for example for replication setup.
	 */
	public final static int CONNECT_AUXILIARY = 0x102;

    /**
     * Opened database links
     *
     * @var array
     */
    public Map<Integer, Connection> _links = new HashMap<>();
    
    /**
     * runs a query
     *
     * @param string $query               SQL query to execute
     * @param mixed  $link                optional database link to use
     * @param int    $options             optional query options
     * @param bool   $cache_affected_rows whether to cache affected rows
     *
     * @return mixed
     */
    public Object query(
        String $query,
        int $link /*= DatabaseInterface.CONNECT_USER*/,
        int $options /*= 0*/,
        boolean $cache_affected_rows /*= true*/
    ) {
    	/* TODO
        $res = $this->tryQuery($query, $link, $options, $cache_affected_rows)
           or Generator::mysqlDie($this->getError($link), $query);
        return $res;*/
    	return null;
    }
    
    /**
     * runs a query and returns the result
     *
     * @param string  $query               query to run
     * @param mixed   $link                link type
     * @param integer $options             query options
     * @param bool    $cache_affected_rows whether to cache affected row
     *
     * @return mixed
     * @throws SQLException 
     */
    public ResultSet tryQuery(
        String $query,
        int $link /*= DatabaseInterface::CONNECT_USER*/,
        int $options /*= 0*/,
        boolean $cache_affected_rows /*= true*/
    ) throws SQLException {
    	boolean $debug = "true".equals(((Map) Globals.getConfig().get("DBG")).get("sql"));
        if (! _links.containsKey($link)) {
            return null;
        }
        long $time = 0;
        if ($debug) {
            $time = new Date().getTime();
        }
        // FIXME $options currently ignored
        ResultSet $result = _links.get($link).createStatement().executeQuery($query);
        
        /* TODO if ($cache_affected_rows) {
            $GLOBALS["cached_affected_rows"] = $this.affectedRows($link, false);
        }*/
        if ($debug) {
            $time = new Date().getTime() - $time;
            System.out.println("DEBUG - " + $query + " executed in " + $time + "ms"); // FIXME use some kind of logger
        }
        /* TODO if ($result !== false && Tracker.isActive()) {
            Tracker.handleQuery($query);
        }*/
        return $result;
    }
    
    public ResultSet tryQuery(String $query) throws SQLException {
    	return tryQuery($query, DatabaseInterface.CONNECT_USER, 0, true);
    }
    
    public ResultSet tryQuery(String $query, int $link) throws SQLException {
    	return tryQuery($query, $link, 0, true);
    }
    
    public ResultSet tryQuery(String $query, int $link, int $options) throws SQLException {
    	return tryQuery($query, $link, $options, true);
    }
    
    /**
     * Run multi query statement and return results
     *
     * @param string $multiQuery multi query statement to execute
     * @param int    $linkIndex  index of the opened database link
     *
     * @return mysqli_result[]|boolean (false)
     */
    public ResultSet[] tryMultiQuery(
        String $multiQuery /*= ''*/,
        int $linkIndex /*= DatabaseInterface::CONNECT_USER*/
    ) {
		return null;
    	/* TODO
        if (! isset($this->_links[$linkIndex])) {
            return false;
        }
        return $this->_extension->realMultiQuery($this->_links[$linkIndex], $multiQuery);*/
    }
    
    public Array getTables(String $catalogName, String $database,
    		int $link /*= DatabaseInterface::CONNECT_USER*/
    ) throws SQLException
    {
		ResultSet metadata = this._links.get($link).getMetaData()
				.getTables($catalogName, $database, null, new String[] {"TABLE"});
		return fetchResult(metadata);
		
    	/* TODO see $GLOBALS['cfg']['NaturalOrder'] */
    }

    /**
     * returns array with table names for given db
     *
     * @param string $database name of database
     * @param mixed  $link     mysql link resource|object
     *
     * @return array   tables names
     * @throws SQLException 
     */
    public Array getTables(String $database, int $link /*= DatabaseInterface::CONNECT_USER*/) throws SQLException
    {
    	return getTables(null, $database, $link);
    	/* TODO see $GLOBALS['cfg']['NaturalOrder'] */
    }

	public Array getTables(String $database) throws SQLException {
		return getTables($database, DatabaseInterface.CONNECT_USER);
	}

    /**
     * Get a table with database name and table name
     *
     * @param string $db_name    DB name
     * @param string $table_name Table name
     *
     * @return Table
     */
    public Table getTable(String $db_name, String $table_name)
    {
        return new Table($table_name, $db_name, this);
    }
    
    /**
     * returns
     *
     * @param string $database name of database
     * @param array  $tables   list of tables to search for for relations
     * @param int    $link     mysql link resource|object
     *
     * @return array           array of found foreign keys
     */
    public List<Object> getForeignKeyConstrains(String $database, List $tables, int $link /*= DatabaseInterface::CONNECT_USER*/)
    {
		return $tables;
    	/* TODO
        $tablesListForQuery = '';
        foreach ($tables as $table) {
            $tablesListForQuery .= "'" . $this->escapeString($table) . "',";
        }
        $tablesListForQuery = rtrim($tablesListForQuery, ',');
        $foreignKeyConstrains = $this->fetchResult(
            'SELECT'
                    . ' TABLE_NAME,'
                    . ' COLUMN_NAME,'
                    . ' REFERENCED_TABLE_NAME,'
                    . ' REFERENCED_COLUMN_NAME'
                . ' FROM information_schema.key_column_usage'
                . ' WHERE referenced_table_name IS NOT NULL'
                    . " AND TABLE_SCHEMA = '" . $this->escapeString($database) . "'"
                    . ' AND TABLE_NAME IN (' . $tablesListForQuery . ')'
                    . ' AND REFERENCED_TABLE_NAME IN (' . $tablesListForQuery . ');',
            null,
            null,
            $link,
            self::QUERY_STORE
        );
        return $foreignKeyConstrains;*/
    }
    
    /**
     * returns array of all tables in given db or dbs
     * this function expects unquoted names:
     * RIGHT: my_database
     * WRONG: `my_database`
     * WRONG: my\_database
     * if $tbl_is_group is true, $table is used as filter for table names
     *
     * <code>
     * $dbi->getTablesFull('my_database');
     * $dbi->getTablesFull('my_database', 'my_table'));
     * $dbi->getTablesFull('my_database', 'my_tables_', true));
     * </code>
     *
     * @param string          $database     database
     * @param string|array    $table        table name(s)
     * @param boolean         $tbl_is_group $table is a table group
     * @param integer         $limit_offset zero-based offset for the count
     * @param boolean|integer $limit_count  number of tables to return
     * @param string          $sort_by      table attribute to sort by
     * @param string          $sort_order   direction to sort (ASC or DESC)
     * @param string          $table_type   whether table or view
     * @param mixed           $link         link type
     *
     * @return array           list of tables in given db(s)
     * @throws SQLException 
     *
     * @todo    move into Table
     */
    public Array getTablesFull(
    	String $catalogName,
        String $database,
        String $table /*= null*/,
        boolean $tbl_is_group /*= false*/,
        int $limit_offset /*= 0*/,
        Integer $limit_count /*= false*/,
        String $sort_by /*= 'Name'*/,
        String $sort_order /*= 'ASC'*/,
        String $table_type /*= null*/,
        int $link /*= DatabaseInterface::CONNECT_USER*/
    ) throws SQLException {
    	
    	ResultSet metadata = this._links.get($link).getMetaData()
				.getTables($catalogName, $database, $table, new String[] {"TABLE"});
		return fetchResult(metadata);
		
		// TODO respect parameters
    }
    
    public Array getTablesFull(
        	String $database,
            String $table /*= null*/,
            boolean $tbl_is_group /*= false*/,
            int $limit_offset /*= 0*/,
            Integer $limit_count /*= false*/,
            String $sort_by /*= 'Name'*/,
            String $sort_order /*= 'ASC'*/,
            String $table_type /*= null*/,
            int $link /*= DatabaseInterface::CONNECT_USER*/
        ) throws SQLException {
        	
        	return getTablesFull(null, $database, $table, $tbl_is_group, $limit_offset,
        			$limit_count, $sort_by, $sort_order, $table_type, $link);
        }

	public boolean isSystemSchema(String _db) {
		// TODO Auto-generated method stub
		return false;
	}

    /**
     * gets the current user with host
     *
     * @return string the current user i.e. user@host
     */
    public String getCurrentUser()
    {
    	return "";
    	/* TODO
        if (Util::cacheExists('mysql_cur_user')) {
            return Util::cacheGet('mysql_cur_user');
        }
        $user = $this->fetchValue('SELECT CURRENT_USER();');
        if ($user !== false) {
            Util::cacheSet('mysql_cur_user', $user);
            return $user;
        }
        return '@';*/
    }
    
    /**
     * Checks if current user is superuser
     *
     * @return bool Whether user is a superuser
     */
	public boolean isSuperuser() {
		return isUserType("super");
	}

    /**
     * Checks if current user has global create user/grant privilege
     * or is a superuser (i.e. SELECT on mysql.users)
     * while caching the result in session.
     *
     * @param string $type type of user to check for
     *                     i.e. 'create', 'grant', 'super'
     *
     * @return bool Whether user is a given type of user
     */
    public boolean isUserType(String type) {

    	return false;
		/* TODO
		 if (Util::cacheExists('is_' . $type . 'user')) {
            return Util::cacheGet('is_' . $type . 'user');
        }

        // when connection failed we don't have a $userlink
        if (! isset($this->_links[DatabaseInterface::CONNECT_USER])) {
            return false;
        }

        // checking if user is logged in
        if ($type === 'logged') {
            return true;
        }

        if (! $GLOBALS['cfg']['Server']['DisableIS'] || $type === 'super') {
            // Prepare query for each user type check
            $query = '';
            if ($type === 'super') {
                $query = 'SELECT 1 FROM mysql.user LIMIT 1';
            } elseif ($type === 'create') {
                list($user, $host) = $this->getCurrentUserAndHost();
                $query = "SELECT 1 FROM `INFORMATION_SCHEMA`.`USER_PRIVILEGES` "
                    . "WHERE `PRIVILEGE_TYPE` = 'CREATE USER' AND "
                    . "'''" . $user . "''@''" . $host . "''' LIKE `GRANTEE` LIMIT 1";
            } elseif ($type === 'grant') {
                list($user, $host) = $this->getCurrentUserAndHost();
                $query = "SELECT 1 FROM ("
                    . "SELECT `GRANTEE`, `IS_GRANTABLE` FROM "
                    . "`INFORMATION_SCHEMA`.`COLUMN_PRIVILEGES` UNION "
                    . "SELECT `GRANTEE`, `IS_GRANTABLE` FROM "
                    . "`INFORMATION_SCHEMA`.`TABLE_PRIVILEGES` UNION "
                    . "SELECT `GRANTEE`, `IS_GRANTABLE` FROM "
                    . "`INFORMATION_SCHEMA`.`SCHEMA_PRIVILEGES` UNION "
                    . "SELECT `GRANTEE`, `IS_GRANTABLE` FROM "
                    . "`INFORMATION_SCHEMA`.`USER_PRIVILEGES`) t "
                    . "WHERE `IS_GRANTABLE` = 'YES' AND "
                    . "'''" . $user . "''@''" . $host . "''' LIKE `GRANTEE` LIMIT 1";
            }

            $is = false;
            $result = $this->tryQuery(
                $query,
                self::CONNECT_USER,
                self::QUERY_STORE
            );
            if ($result) {
                $is = (bool) $this->numRows($result);
            }
            $this->freeResult($result);
        } else {
            $is = false;
            $grants = $this->fetchResult(
                "SHOW GRANTS FOR CURRENT_USER();",
                null,
                null,
                self::CONNECT_USER,
                self::QUERY_STORE
            );
            if ($grants) {
                foreach ($grants as $grant) {
                    if ($type === 'create') {
                        if (strpos($grant, "ALL PRIVILEGES ON *.*") !== false
                            || strpos($grant, "CREATE USER") !== false
                        ) {
                            $is = true;
                            break;
                        }
                    } elseif ($type === 'grant') {
                        if (strpos($grant, "WITH GRANT OPTION") !== false) {
                            $is = true;
                            break;
                        }
                    }
                }
            }
        }

        Util::cacheSet('is_' . $type . 'user', $is);
        return $is;
        
		 */
	}

	public Array fetchResult(
        ResultSet $result,
        Object $key /*= null*/,
        Object $value /*= null*/
    ) throws SQLException {
    	
    	//FIXME why query whole table and then filter on $value only ?!?

    	Array $resultrows = new Array();

        // return empty array if result is empty or false
        if ($result == null) {
            return $resultrows;
        }

        if (null == $key) {
        	// Will return an Array with Integer keys
        	Array $row;
            while (($row = this.fetchAssoc($result)) != null) {
                $resultrows.add(_fetchValue($row, $value));
            }
            return $resultrows;
        } else {
            if ($key instanceof Object[]) {
            	Array $row;
                while (($row = this.fetchAssoc($result)) != null) {
                	// Es. $row = (group => admin, name => john)
                    Array $result_target = $resultrows;
                    for (Object $key_index : (Object[])$key) {
                        if (null == $key_index) {
                            continue;
                        }

                        if (! ($result_target.containsKey($row.get($key_index)))) {
                            $result_target.put($row.get($key_index), new Array());
                        }
                        $result_target = (Array) $result_target.get($row.get($key_index));
                    }
                    $result_target.add(_fetchValue($row, $value));
                }
            } else {
            	Array $row;
                while (($row = this.fetchAssoc($result)) != null) {
                    $resultrows.put($row.get($key), _fetchValue($row, $value));
                }
            }
            return $resultrows;
        }
    }

	public Array fetchResult(ResultSet $result) throws SQLException {
    	return fetchResult($result, null, null);
    }
	
    public Array fetchResult(String $query) throws SQLException {
    	return fetchResult($query, null, null, DatabaseInterface.CONNECT_USER, 0);
    }

    /**
     * returns all rows in the resultset in one array
     *
     * <code>
     * $sql = 'SELECT * FROM `user`';
     * $users = $dbi->fetchResult($sql);
     * // produces
     * // $users[] = array('id' => 123, 'name' => 'John Doe')
     *
     * $sql = 'SELECT `id`, `name` FROM `user`';
     * $users = $dbi->fetchResult($sql, 'id');
     * // produces
     * // $users['123'] = array('id' => 123, 'name' => 'John Doe')
     *
     * $sql = 'SELECT `id`, `name` FROM `user`';
     * $users = $dbi->fetchResult($sql, 0);
     * // produces
     * // $users['123'] = array(0 => 123, 1 => 'John Doe')
     *
     * $sql = 'SELECT `id`, `name` FROM `user`';
     * $users = $dbi->fetchResult($sql, 'id', 'name');
     * // or
     * $users = $dbi->fetchResult($sql, 0, 1);
     * // produces
     * // $users['123'] = 'John Doe'
     *
     * $sql = 'SELECT `name` FROM `user`';
     * $users = $dbi->fetchResult($sql);
     * // produces
     * // $users[] = 'John Doe'
     *
     * $sql = 'SELECT `group`, `name` FROM `user`'
     * $users = $dbi->fetchResult($sql, array('group', null), 'name');
     * // produces
     * // $users['admin'][] = 'John Doe'
     *
     * $sql = 'SELECT `group`, `name` FROM `user`'
     * $users = $dbi->fetchResult($sql, array('group', 'name'), 'id');
     * // produces
     * // $users['admin']['John Doe'] = '123'
     * </code>
     *
     * @param string               $query   query to execute
     * @param string|integer|array $key     field-name or offset
     *                                      used as key for array
     *                                      or array of those
     * @param string|integer       $value   value-name or offset
     *                                      used as value for array
     * @param integer              $link    link type
     * @param integer              $options query options
     *
     * @return array resultrows or values indexed by $key
	 * @throws SQLException 
     */
    public Array fetchResult(
    		String $query,
            Object $key /*= null*/,
            Object $value /*= null*/,
            int $link /*= DatabaseInterface.CONNECT_USER*/,
            int $options /*= 0*/
        ) throws SQLException {
        ResultSet $result = this.tryQuery($query, $link, $options, false);
        return fetchResult($result, $key, $value);
    }
    
    /**
     * Returns row or element of a row
     *
     * @param array           $row   Row to process
     * @param string|null|int $key Which column to return
     *
     * @return mixed
     */
    private Object _fetchValue(Array $row, Object $value)
    {
        return $value == null ? $row : $row.get($value);
    }

    /**
     * returns a single value from the given result or query,
     * if the query or the result has more than one row or field
     * the first field of the first row is returned
     *
     * <code>
     * $sql = "SELECT `name` FROM `user` WHERE `id` = 123";
     * $user_name = $dbi.fetchValue($sql);
     * // produces
     * // $user_name = "John Doe"
     * </code>
     *
     * @param string         $query      The query to execute
     * @param integer        $row_number row to fetch the value from,
     *                                   starting at 0, with 0 being default
     * @param integer|string $field      field to fetch the value from,
     *                                   starting at 0, with 0 being default
     * @param integer        $link       link type
     *
     * @return mixed value of first field in first row from result
     *               or false if not found
     * @throws SQLException 
     */
    public Object fetchValue(
        String $query,
        int $row_number /*= 0*/,
        Object $field /*= 0*/,
        int $link /*= DatabaseInterface.CONNECT_USER*/
    ) throws SQLException {
        Object $value = false;

        ResultSet $result = this.tryQuery(
            $query,
            $link,
            QUERY_STORE,
            false
        );
        if ($result == null) {
            return false;
        }

        // get requested row
        for (int $i = 0; $i < $row_number; $i++) {
        	if (!$result.next()) {
        		return null;
        	}
        }

        if ($field instanceof Integer) {
        	$value = $result.getObject((Integer)$field);
        } else if ($field instanceof String) {
        	$value = $result.getObject((String)$field);
        } else {
        	System.err.println("Unexpected $field type: " + $field.getClass().getName());
        	return null;
        }
        
        return $value;
    }
    
    public Object fetchValue(String $query) throws SQLException {
    	return fetchValue($query, 0, 0, DatabaseInterface.CONNECT_USER);
    }

    /**
     * returns only the first row from the result
     *
     * <code>
     * $sql = "SELECT * FROM `user` WHERE `id` = 123";
     * $user = $dbi.fetchSingleRow($sql);
     * // produces
     * // $user = array("id" => 123, "name" => "John Doe")
     * </code>
     *
     * @param string  $query The query to execute
     * @param string  $type  NUM|ASSOC|BOTH returned array should either numeric
     *                       associative or both
     * @param integer $link  link type
     *
     * @return array first row from result
     *                       or null if result is empty
     * @throws SQLException 
     */
    public Array fetchSingleRow(
        String $query,
        String $type /*= "ASSOC"*/,
        int $link /*= DatabaseInterface.CONNECT_USER*/
    ) throws SQLException {
        ResultSet $result = this.tryQuery(
            $query,
            $link,
            QUERY_STORE,
            false
        );
        if ($result == null) {
            return null;
        }

        // return false if result is empty or false
    	if (!$result.next()) {
    		return null;
    	}

    	Array $row = null;
        switch ($type) {
            case "NUM":
                $row = fetchRow($result);
                break;
            case "ASSOC":
                $row = fetchAssoc($result);
                break;
            case "BOTH":
            default:
                $row = fetchArray($result);
                break;
        }
        
        return $row;
    }

	public Array fetchSingleRow(String $query) throws SQLException {
		return fetchSingleRow($query, "ASSOC", DatabaseInterface.CONNECT_USER);
    }
    
    /**
     * returns array of rows with associative and numeric keys from $result
     *
     * @param object $result result set identifier
     *
     * @return Map of String|Integer to Object
     * @throws SQLException 
     */
    public Array fetchArray(ResultSet $result) throws SQLException
    {
    	//currently: 1 row only, and the resultset must be open
    	Array map = new Array();
    	int $n = $result.getMetaData().getColumnCount();
    	for (int i = 0; i < $n; ++i) {
    		Object value = $result.getObject(i);
    		map.put(new Integer(i), value);
    		map.put($result.getMetaData().getColumnName(i), value);
    	}
        return map;
    }

    /**
     * returns array of rows with associative keys from $result
     *
     * @param object $result result set identifier
     *
     * @return array|null
     * @throws SQLException 
     */
    public Array fetchAssoc(ResultSet $result) throws SQLException
    {
    	//currently: 1 row only, and the resultset must be open
    	Array map = new Array();
    	int $n = $result.getMetaData().getColumnCount();
    	for (int i = 0; i < $n; ++i) {
    		map.put($result.getMetaData().getColumnName(i), $result.getObject(i));
    	}
        return map;
    }

    /**
     * returns array of rows with numeric keys from $result
     *
     * @param object $result result set identifier
     *
     * @return array|null
     * @throws SQLException 
     */
    public Array fetchRow(ResultSet $result) throws SQLException
    {
    	//currently: 1 row only, and the resultset must be open
    	Array map = new Array();
    	int $n = $result.getMetaData().getColumnCount();
    	for (int i = 0; i < $n; ++i) {
    		map.add($result.getObject(i));
    	}
    	return map;
    }


    /**
     * Return connection parameters for the database server
     *
     * @param integer    $mode   Connection mode one of CONNECT_USER, CONNECT_CONTROL
     *                           or CONNECT_AUXILIARY.
     * @param array|null $server Server information like host/port/socket/persistent
     *
     * @return array user, host and server settings array
     */
    public ConnectionParams getConnectionParams(int $mode, Map<String, Object> $serverMap /*= null*/)
    {
        Config $cfg = Globals.getConfig();
        ConnectionParams connectionParams = new ConnectionParams();
        String $user = null;
        String $password = null;
        ServerParams $server = connectionParams.$server = new ServerParams();
        
        if ($mode == DatabaseInterface.CONNECT_USER) {
            $user = (String) multiget($cfg.settings, "Server", "user");
            $password = (String) multiget($cfg.settings, "Server", "password");
            $server.host = (String) multiget($cfg.settings, "Server", "host");
            $server.jndiName = (String) multiget($cfg.settings, "Server", "jndiName");
            $server.jdbcUrl = (String) multiget($cfg.settings, "Server", "jdbcUrl");
            $server.jdbcDriver = (String) multiget($cfg.settings, "Server", "jdbcDriver");
            if (!empty(multiget($cfg.settings, "Server", "port"))) {
                $server.port = new Integer((String)multiget($cfg.settings, "Server", "port"));
            }
            $server.socket = (String)multiget($cfg.settings, "Server", "socket");
            $server.compress = "true".equals(multiget($cfg.settings, "Server", "compress"));
            $server.ssl = "true".equals(multiget($cfg.settings, "Server", "ssl"));
            $server.ssl_key = (String)multiget($cfg.settings, "Server", "ssl_key");
            $server.ssl_cert = (String)multiget($cfg.settings, "Server", "ssl_cert");
            $server.ssl_ca = (String)multiget($cfg.settings, "Server", "ssl_ca");
            $server.ssl_ca_path = (String)multiget($cfg.settings, "Server", "ssl_ca_path");
            $server.ssl_ciphers = (String)multiget($cfg.settings, "Server", "ssl_ciphers");
            $server.ssl_verify = (String)multiget($cfg.settings, "Server", "ssl_verify");
            
        } else if ($mode == DatabaseInterface.CONNECT_CONTROL) {
            $user = (String) multiget($cfg.settings, "Server", "controluser");
            $password = (String) multiget($cfg.settings, "Server", "controlpass");
            if (! empty(multiget($cfg.settings, "Server", "controlhost"))) {
                $server.host = (String) multiget($cfg.settings, "Server", "controlhost");
            } else {
                $server.host = (String) multiget($cfg.settings, "Server", "host");
            }
            if (! empty(multiget($cfg.settings, "Server", "controlJndiName"))) {
                $server.jndiName = (String) multiget($cfg.settings, "Server", "controlJndiName");
            } else {
                $server.jndiName = (String) multiget($cfg.settings, "Server", "jndiName");
            }
            if (! empty(multiget($cfg.settings, "Server", "controlJdbcUrl"))) {
                $server.jdbcUrl = (String) multiget($cfg.settings, "Server", "controlJdbcUrl");
            } else {
                $server.jdbcUrl = (String) multiget($cfg.settings, "Server", "jdbcUrl");
            }
            if (! empty(multiget($cfg.settings, "Server", "controlJdbcDriver"))) {
                $server.jdbcDriver = (String) multiget($cfg.settings, "Server", "controlJdbcDriver");
            } else {
                $server.jdbcDriver = (String) multiget($cfg.settings, "Server", "jdbcDriver");
            }
            // Share the settings if the host is same
            if ($server.host.equals(multiget($cfg.settings, "Server", "host"))) {
            	
            	if (!empty(multiget($cfg.settings, "Server", "port"))) {
                    $server.port = new Integer((String)multiget($cfg.settings, "Server", "port"));
                }
                $server.socket = (String)multiget($cfg.settings, "Server", "socket");
                $server.compress = "true".equals(multiget($cfg.settings, "Server", "compress"));
                $server.ssl = "true".equals(multiget($cfg.settings, "Server", "ssl"));
                $server.ssl_key = (String)multiget($cfg.settings, "Server", "ssl_key");
                $server.ssl_cert = (String)multiget($cfg.settings, "Server", "ssl_cert");
                $server.ssl_ca = (String)multiget($cfg.settings, "Server", "ssl_ca");
                $server.ssl_ca_path = (String)multiget($cfg.settings, "Server", "ssl_ca_path");
                $server.ssl_ciphers = (String)multiget($cfg.settings, "Server", "ssl_ciphers");
                $server.ssl_verify = (String)multiget($cfg.settings, "Server", "ssl_verify");
            }
            // Set configured port
            if (! empty(multiget($cfg.settings, "Server", "controlport"))) {
                $server.port = new Integer((String)multiget($cfg.settings, "Server", "controlport"));
            }
            // Set any configuration with control_ prefix
            /* TODO or not?
             for (String $key : ((Map<String, Object>) $cfg.get("Server")).keySet()) {
            	Object $val = ((Map) $cfg.get("Server")).get($key);
                if ($key.startsWith("control_")) {
                    $server.put($key.substring(8), $val);
                }
            }*/
        } else {
            if ($serverMap == null) {
                return connectionParams;
            }
            if (!empty($serverMap.get("user"))) {
                $user = (String) $serverMap.get("user");
            }
            if (!empty($serverMap.get("password"))) {
            	$password = (String) $serverMap.get("password");
            }
        }
        
        // Perform sanity checks on some variables
        if ($server.port == null) {
            $server.port = 0;
        }
        if (empty($server.host)) {
            $server.host = "localhost";
        }
        
        connectionParams.$user = $user;
        connectionParams.$password = $password;
        
        return connectionParams;
    }
    
    public static class ServerParams {
		public String jndiName;
    	public String jdbcUrl;
    	public String host;
		public Integer port;
    	public String jdbcDriver;
		
		// MySQL specific: !?!
    	public boolean ssl = false;
    	public String ssl_verify;
		public String ssl_ciphers;
		public String ssl_ca_path;
		public String ssl_ca;
		public String ssl_cert;
		public String ssl_key;
    	boolean compress = false;
    	public String socket;
    }
    
    public static class ConnectionParams {
    	public String $user;
    	public String $password;
    	public ServerParams $server;
    }
    
    /**
     * connects to the database server
     *
     * @param integer    $mode   Connection mode on of CONNECT_USER, CONNECT_CONTROL
     *                           or CONNECT_AUXILIARY.
     * @param array|null $server Server information like host/port/socket/persistent
     * @param integer    $target How to store connection link, defaults to $mode
     *
     * @return mixed false on error or a connection object on success
     * @throws SQLException 
     * @throws NamingException 
     */
    public Connection connect(int $mode, Map<String, Object> $serverMap /*= null*/, Integer $target /*= null*/) throws SQLException, NamingException
    {
    	ConnectionParams connectionParams = this.getConnectionParams($mode, $serverMap); 
    	String $user = connectionParams.$user;
    	String $password = connectionParams.$password;
    	ServerParams $server = connectionParams.$server;
    	
        if ($target == null) {
            $target = $mode;
        }
        if ($user == null || $password == null) {
            trigger_error(
                __("Missing connection parameters!"),
                E_USER_WARNING
            );
            return null;
        }
        
        Connection $result = null;
        
        if ($server.jndiName != null) {
        	
        	// Datasource
        	
        	Context ctx = new InitialContext();
        	DataSource ds = (DataSource)ctx.lookup($server.jndiName);
        	$result = ds.getConnection();
        	
        } else if ($server.jdbcUrl != null) {
        	
        	// Direct JDBC url
        	
        	if (!empty($user) || !empty($password)) {
        		$result = DriverManager.getConnection($server.jdbcUrl, $user, $password);
        	} else {
        		$result = DriverManager.getConnection($server.jdbcUrl);
        	}
        } else {
        	trigger_error(
                    __(
                        "Currently, connection by hostname is not supported."
                    ),
                    E_USER_WARNING
                );
        	return null;
        }
        
        if ($result != null) {
            this._links.put($target, $result);
            /* Run post connect for user connections */
            if ($target == DatabaseInterface.CONNECT_USER) {
                this.postConnect();
            }
            return $result;
        }
        if ($mode == DatabaseInterface.CONNECT_CONTROL) {
            trigger_error(
                __(
                    "Connection for controluser as defined in your "
                    + "configuration failed."
                ),
                E_USER_WARNING
            );
            return null;
        } else if ($mode == DatabaseInterface.CONNECT_AUXILIARY) {
            // Do not go back to main login if connection failed
            // (currently used only in unit testing)
            return null;
        }
        return $result;
    }
    
    public Connection connect(int $mode) throws SQLException, NamingException {
    	return connect($mode, null, null);
    }

    /**
     * Function called just after a connection to the MySQL database server has
     * been established. It sets the connection collation, and determines the
     * version of MySQL which is running.
     *
     * @return void
     */
    public void postConnect()
    {
    	/*
        $version = this.fetchSingleRow(
            "SELECT @@version, @@version_comment",
            "ASSOC",
            DatabaseInterface.CONNECT_USER
        );

        if ($version) {
            this._version_int = self.versionToInt($version["@@version"]);
            this._version_str = $version["@@version"];
            this._version_comment = $version["@@version_comment"];
            if (stripos($version["@@version"], "mariadb") !== false) {
                this._is_mariadb = true;
            }
            if (stripos($version["@@version_comment"], "percona") !== false) {
                this._is_percona = true;
            }
        }

        if (this._version_int > 50503) {
            $default_charset = "utf8mb4";
            $default_collation = "utf8mb4_general_ci";
        } else {
            $default_charset = "utf8";
            $default_collation = "utf8_general_ci";
        }
        $GLOBALS["collation_connection"] = $default_collation;
        $GLOBALS["charset_connection"] = $default_charset;
        this.query(
            "SET NAMES "$default_charset" COLLATE "$default_collation";",
            DatabaseInterface.CONNECT_USER,
            self.QUERY_STORE
        );

        // Locale for messages
        $locale = LanguageManager.getInstance().getCurrentLanguage().getMySQLLocale();
        if (! empty($locale)) {
            this.query(
                "SET lc_messages = "" . $locale . "";",
                DatabaseInterface.CONNECT_USER,
                self.QUERY_STORE
            );
        }

        // Set timezone for the session, if required.
        if ($GLOBALS["cfg"]["Server"]["SessionTimeZone"] != "") {
            $sql_query_tz = "SET " . Util.backquote("time_zone") . " = "
                . "\""
                . this.escapeString($GLOBALS["cfg"]["Server"]["SessionTimeZone"])
                . "\"";

            if (! this.tryQuery($sql_query_tz)) {
                $error_message_tz = sprintf(
                    __(
                        "Unable to use timezone "%1$s" for server %2$d. "
                        . "Please check your configuration setting for "
                        . "[em]$cfg[\"Servers\"][%3$d][\"SessionTimeZone\"][/em]. "
                        . "phpMyAdmin is currently using the default time zone "
                        . "of the database server."
                    ),
                    $GLOBALS["cfg"]["Server"]["SessionTimeZone"],
                    $GLOBALS["server"],
                    $GLOBALS["server"]
                );

                trigger_error($error_message_tz, E_USER_WARNING);
            }
        }

        // Loads closest context to this version.
        Context.loadClosest(
            (this._is_mariadb ? "MariaDb" : "MySql") . this._version_int
        );

        // the DatabaseList class as a stub for the ListDatabase class
        $GLOBALS["dblist"] = new DatabaseList();
        */
    }
    

    /**
     * returns array with databases containing extended infos about them
     *
     * @param string   $database     database
     * @param boolean  $force_stats  retrieve stats also for MySQL < 5
     * @param integer  $link         link type
     * @param string   $sort_by      column to order by
     * @param string   $sort_order   ASC or DESC
     * @param integer  $limit_offset starting offset for LIMIT
     * @param bool|int $limit_count  row count for LIMIT or true
     *                               for $GLOBALS['cfg']['MaxDbList']
     *
     * @return array
     *
     * @todo    move into ListDatabase?
     */
    public Array getDatabasesFull(
        String $database /*= null*/,
        boolean $force_stats /*= false*/,
        int $link /*= DatabaseInterface::CONNECT_USER*/,
        String $sort_by /*= 'SCHEMA_NAME'*/,
        String $sort_order /*= 'ASC'*/,
        int $limit_offset /*= 0*/,
        Integer $limit_count /*= false*/
    ) {
    	if ($sort_order != null) {
    		$sort_order = $sort_order.toUpperCase();
    	}
        if (true === $limit_count) {
            $limit_count = $GLOBALS['cfg']['MaxDbList'];
        }
        $apply_limit_and_order_manual = true;
        if (! $GLOBALS['cfg']['Server']['DisableIS']) {
            /**
             * if $GLOBALS['cfg']['NaturalOrder'] is enabled, we cannot use LIMIT
             * cause MySQL does not support natural ordering,
             * we have to do it afterward
             */
            $limit = "";
            if (! $GLOBALS["cfg"]["NaturalOrder"]) {
                if ($limit_count) {
                    $limit = " LIMIT " . $limit_count . " OFFSET " . $limit_offset;
                }
                $apply_limit_and_order_manual = false;
            }
            // get table information from information_schema
            if (! empty($database)) {
                $sql_where_schema = "WHERE `SCHEMA_NAME` LIKE \""
                    . $this.escapeString($database, $link) . "\"";
            } else {
                $sql_where_schema = "";
            }
            $sql  = "SELECT *,
                    CAST(BIN_NAME AS CHAR CHARACTER SET utf8) AS SCHEMA_NAME
                FROM (";
            $sql .= "SELECT
                BINARY s.SCHEMA_NAME AS BIN_NAME,
                s.DEFAULT_COLLATION_NAME";
            if ($force_stats) {
                $sql .= ",
                    COUNT(t.TABLE_SCHEMA)  AS SCHEMA_TABLES,
                    SUM(t.TABLE_ROWS)      AS SCHEMA_TABLE_ROWS,
                    SUM(t.DATA_LENGTH)     AS SCHEMA_DATA_LENGTH,
                    SUM(t.MAX_DATA_LENGTH) AS SCHEMA_MAX_DATA_LENGTH,
                    SUM(t.INDEX_LENGTH)    AS SCHEMA_INDEX_LENGTH,
                    SUM(t.DATA_LENGTH + t.INDEX_LENGTH)
                                           AS SCHEMA_LENGTH,
                    SUM(IF(t.ENGINE <> \"InnoDB\", t.DATA_FREE, 0))
                                           AS SCHEMA_DATA_FREE";
            }
            $sql .= "
                   FROM `information_schema`.SCHEMATA s ";
            if ($force_stats) {
                $sql .= "
                    LEFT JOIN `information_schema`.TABLES t
                        ON BINARY t.TABLE_SCHEMA = BINARY s.SCHEMA_NAME";
            }
            $sql .= $sql_where_schema . "
                    GROUP BY BINARY s.SCHEMA_NAME, s.DEFAULT_COLLATION_NAME
                    ORDER BY ";
            if ($sort_by == "SCHEMA_NAME"
                || $sort_by == "DEFAULT_COLLATION_NAME"
            ) {
                $sql .= "BINARY ";
            }
            $sql .= Util.backquote($sort_by)
                . " " . $sort_order
                . $limit;
            $sql .= ") a";
            $databases = $this.fetchResult($sql, "SCHEMA_NAME", null, $link);
            $mysql_error = $this.getError($link);
            if (! count($databases) && $GLOBALS["errno"]) {
                Generator.mysqlDie($mysql_error, $sql);
            }
            // display only databases also in official database list
            // f.e. to apply hide_db and only_db
            $drops = array_diff(
                array_keys($databases),
                (array) $GLOBALS["dblist"].databases
            );
            foreach ($drops as $drop) {
                unset($databases[$drop]);
            }
        } else {
            $databases = [];
            foreach ($GLOBALS["dblist"].databases as $database_name) {
                // Compatibility with INFORMATION_SCHEMA output
                $databases[$database_name]["SCHEMA_NAME"]      = $database_name;
                $databases[$database_name]["DEFAULT_COLLATION_NAME"]
                    = $this.getDbCollation($database_name);
                if (! $force_stats) {
                    continue;
                }
                // get additional info about tables
                $databases[$database_name]["SCHEMA_TABLES"]          = 0;
                $databases[$database_name]["SCHEMA_TABLE_ROWS"]      = 0;
                $databases[$database_name]["SCHEMA_DATA_LENGTH"]     = 0;
                $databases[$database_name]["SCHEMA_MAX_DATA_LENGTH"] = 0;
                $databases[$database_name]["SCHEMA_INDEX_LENGTH"]    = 0;
                $databases[$database_name]["SCHEMA_LENGTH"]          = 0;
                $databases[$database_name]["SCHEMA_DATA_FREE"]       = 0;
                $res = $this.query(
                    "SHOW TABLE STATUS FROM "
                    . Util.backquote($database_name) . ";"
                );
                if ($res === false) {
                    unset($res);
                    continue;
                }
                while ($row = $this.fetchAssoc($res)) {
                    $databases[$database_name]["SCHEMA_TABLES"]++;
                    $databases[$database_name]["SCHEMA_TABLE_ROWS"]
                        += $row["Rows"];
                    $databases[$database_name]["SCHEMA_DATA_LENGTH"]
                        += $row["Data_length"];
                    $databases[$database_name]["SCHEMA_MAX_DATA_LENGTH"]
                        += $row["Max_data_length"];
                    $databases[$database_name]["SCHEMA_INDEX_LENGTH"]
                        += $row["Index_length"];
                    // for InnoDB, this does not contain the number of
                    // overhead bytes but the total free space
                    if ("InnoDB" != $row["Engine"]) {
                        $databases[$database_name]["SCHEMA_DATA_FREE"]
                            += $row["Data_free"];
                    }
                    $databases[$database_name]["SCHEMA_LENGTH"]
                        += $row["Data_length"] + $row["Index_length"];
                }
                $this.freeResult($res);
                unset($res);
            }
        }
        /**
         * apply limit and order manually now
         * (caused by older MySQL < 5 or $GLOBALS["cfg"]["NaturalOrder"])
         */
        if ($apply_limit_and_order_manual) {
            $GLOBALS["callback_sort_order"] = $sort_order;
            $GLOBALS["callback_sort_by"] = $sort_by;
            usort(
                $databases,
                [
                    self.class,
                    "_usortComparisonCallback",
                ]
            );
            unset($GLOBALS["callback_sort_order"], $GLOBALS["callback_sort_by"]);
            /**
             * now apply limit
             */
            if ($limit_count) {
                $databases = array_slice($databases, $limit_offset, $limit_count);
            }
        }
        return $databases;
    }
}
