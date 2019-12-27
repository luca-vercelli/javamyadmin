package org.javamyadmin.helpers;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.naming.Context;

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
    private Map<Integer, Connection> _links = new HashMap<>();
    
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
     */
    public Object tryQuery(
        String $query,
        int $link /*= DatabaseInterface::CONNECT_USER*/,
        int $options /*= 0*/,
        boolean $cache_affected_rows /*= true*/
    ) {
		return null;
    	/* TODO
        $debug = isset($GLOBALS['cfg']['DBG']) ? $GLOBALS['cfg']['DBG']['sql'] : false;
        if (! isset($this->_links[$link])) {
            return false;
        }
        $time = 0;
        if ($debug) {
            $time = microtime(true);
        }
        $result = $this->_extension->realQuery($query, $this->_links[$link], $options);
        if ($cache_affected_rows) {
            $GLOBALS['cached_affected_rows'] = $this->affectedRows($link, false);
        }
        if ($debug) {
            $time = microtime(true) - $time;
            $this->_dbgQuery($query, $link, $result, $time);
            if ($GLOBALS['cfg']['DBG']['sqllog']) {
                $warningsCount = '';
                if (($options & DatabaseInterface::QUERY_STORE) == DatabaseInterface::QUERY_STORE) {
                    if (isset($this->_links[$link]->warning_count)) {
                        $warningsCount = $this->_links[$link]->warning_count;
                    }
                }
                openlog('phpMyAdmin', LOG_NDELAY | LOG_PID, LOG_USER);
                syslog(
                    LOG_INFO,
                    'SQL[' . basename($_SERVER['SCRIPT_NAME']) . ']: '
                    . sprintf('%0.3f', $time) . '(W:' . $warningsCount . ') > ' . $query
                );
                closelog();
            }
        }
        if ($result !== false && Tracker::isActive()) {
            Tracker::handleQuery($query);
        }
        return $result;*/
    }
    
    /**
     * Run multi query statement and return results
     *
     * @param string $multiQuery multi query statement to execute
     * @param int    $linkIndex  index of the opened database link
     *
     * @return mysqli_result[]|boolean (false)
     */
    public Object tryMultiQuery(
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
    
    /**
     * returns array with table names for given db
     *
     * @param string $database name of database
     * @param mixed  $link     mysql link resource|object
     *
     * @return array   tables names
     */
    public List<Object> getTables(String $database, int $link /*= DatabaseInterface::CONNECT_USER*/)
    {
		return null;
    	/* TODO
        $tables = $this->fetchResult(
            'SHOW TABLES FROM ' . Util::backquote($database) . ';',
            null,
            0,
            $link,
            self::QUERY_STORE
        );
        if ($GLOBALS['cfg']['NaturalOrder']) {
            usort($tables, 'strnatcasecmp');
        }
        return $tables;*/
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
     *
     * @todo    move into Table
     */
    public List getTablesFull(
        String $database,
        String $table /*= ''*/,
        boolean $tbl_is_group /*= false*/,
        int $limit_offset /*= 0*/,
        Integer $limit_count /*= false*/,
        String $sort_by /*= 'Name'*/,
        String $sort_order /*= 'ASC'*/,
        String $table_type /*= null*/,
        int $link /*= DatabaseInterface::CONNECT_USER*/
    ) {
		return null;
        	/* TODO */
    }
    
    public boolean isUserType(String string) {
		// TODO Auto-generated method stub
		return false;
	}

	public Table getTable(String _db, String _table) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isSystemSchema(String _db) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSuperuser() {
		// TODO Auto-generated method stub
		return false;
	}

	public Hashtable<Object, Object> getTables(String _db) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map fetchResult(String string, String string2, Object object, int connectUser, int queryStore) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean fetchValue(String string) {
		// TODO Auto-generated method stub
		return false;
	}

	// TODO


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
}
