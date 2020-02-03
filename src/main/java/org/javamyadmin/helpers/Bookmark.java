package org.javamyadmin.helpers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.javamyadmin.php.Php.*;

/**
 * Handles bookmarking SQL queries
 *
 * @package PhpMyAdmin
 */
public class Bookmark {
	
	/*
	 * FIXME. This is something like a DAO. Should use some persistence framework.
	 */
	
    /**
     * ID of the bookmark
     *
     * @var int
     */
    private int _id;
    /**
     * Database the bookmark belongs to
     *
     * @var string
     */
    private String _database;
    /**
     * The user to whom the bookmark belongs, empty for public bookmarks
     *
     * @var string
     */
    private String _user;
    /**
     * Label of the bookmark
     *
     * @var string
     */
    private String _label;
    /**
     * SQL query that is bookmarked
     *
     * @var string
     */
    private String _query;

    /**
     * @var DatabaseInterface
     */
    private DatabaseInterface dbi;

    /**
     * Current user
     *
     * @var string
     */
    private String user;

    /**
     * Bookmark constructor.
     *
     * @param DatabaseInterface $dbi  DatabaseInterface object
     * @param string            $user Current user
     */
    public Bookmark(DatabaseInterface $dbi, String $user)
    {
        this.dbi = $dbi;
        this.user = $user;
    }

    /**
     * Returns the ID of the bookmark
     *
     * @return int
     */
    public int getId()
    {
        return (int) this._id;
    }

    /**
     * Returns the database of the bookmark
     *
     * @return string
     */
    public String getDatabase()
    {
        return this._database;
    }

    /**
     * Returns the user whom the bookmark belongs to
     *
     * @return string
     */
    public String getUser()
    {
        return this._user;
    }

    /**
     * Returns the label of the bookmark
     *
     * @return string
     */
    public String getLabel()
    {
        return this._label;
    }

    /**
     * Returns the query
     *
     * @return string
     */
    public String getQuery()
    {
        return this._query;
    }

    /**
     * Adds a bookmark
     *
     * @return boolean whether the INSERT succeeds or not
     * @throws SQLException 
     *
     * @access public
     */
    public boolean save() throws SQLException
    {
        Map<String, Object> $cfgBookmark = getParams(this.user);
        if (empty($cfgBookmark)) {
            return false;
        }

        String $query = "INSERT INTO " + Util.backquote((String) $cfgBookmark.get("db"))
            + '.' + Util.backquote((String) $cfgBookmark.get("table"))
            + " (id, dbase, user, query, label) VALUES (NULL, "
            + "'" + this.dbi.escapeString(this._database) + "', "
            + "'" + this.dbi.escapeString(this._user) + "', "
            + "'" + this.dbi.escapeString(this._query) + "', "
            + "'" + this.dbi.escapeString(this._label) + "')";
        this.dbi.query($query, DatabaseInterface.CONNECT_CONTROL);
        return true;
    }

    /**
     * Deletes a bookmark
     *
     * @return bool true if successful
     *
     * @access public
     */
    public boolean delete()
    {
        Map<String, Object> $cfgBookmark = getParams(this.user);
        if (empty($cfgBookmark)) {
            return false;
        }

        String $query  = "DELETE FROM " + Util.backquote((String) $cfgBookmark.get("db"))
            + "." + Util.backquote((String) $cfgBookmark.get("table"))
            + " WHERE id = " + this._id;
        this.dbi.tryQuery($query, DatabaseInterface.CONNECT_CONTROL);
        return true;
    }

    /**
     * Returns the number of variables in a bookmark
     *
     * @return int number of variables
     */
    public int getVariableCount()
    {
    	Pattern pattern = Pattern.compile("\\[VARIABLE[0-9]*\\]");
    	return pattern.matcher(this._query).groupCount();
    }

    /**
     * Replace the placeholders in the bookmark query with variables
     *
     * @param array $variables array of variables
     *
     * @return string query with variables applied
     */
    public String applyVariables(List<String> $variables)
    {
        // remove comments that encloses a variable placeholder
        /* TODO String $query = preg_replace(
            "|/\\*(.*\\[VARIABLE[0-9]*\\].*)\\* /|imsU",
            "${1}",
            this._query
        );*/
    	String $query = this._query;
        // replace variable placeholders with values
        int $number_of_variables = this.getVariableCount();
        for (int $i = 1; $i <= $number_of_variables; $i++) {
            String $var = "";
            if (! empty($variables.get($i))) {
                $var = this.dbi.escapeString($variables.get($i));
            }
            $query = $query.replace("[VARIABLE" + $i + "]", $var);
            // backward compatibility
            if ($i == 1) {
                $query = $query.replace("[VARIABLE]", $var);
            }
        }
        return $query;
    }
    
    private static Map<String, Object> $cfgBookmark = null;

    /**
     * Defines the bookmark parameters for the current user
     *
     * @param string $user Current user
     *
     * @return array|bool the bookmark parameters for the current user
     * @access  public
     */
    public static Map<String, Object> getParams(String $user)
    {
        if ($cfgBookmark != null) {
            return $cfgBookmark;
        }

        //$relation = new Relation($GLOBALS["dbi"]);
        //$cfgRelation = $relation.getRelationsParam();
        //if ($cfgRelation["bookmarkwork"]) {
            $cfgBookmark = new HashMap<>();
            $cfgBookmark.put("user", $user);
            $cfgBookmark.put("db", null); // FIXME $cfgRelation["db"]
            $cfgBookmark.put("table", null); // FIXME $cfgRelation["bookmark"]
        //} else {
        //    $cfgBookmark = false;
        //}

        return $cfgBookmark;
    }

    /**
     * Creates a Bookmark object from the parameters
     *
     * @param DatabaseInterface $dbi        DatabaseInterface object
     * @param string            $user       Current user
     * @param array             $bkm_fields the properties of the bookmark to add; here,
     *                                      $bkm_fields["bkm_sql_query"] is urlencoded
     * @param boolean           $all_users  whether to make the bookmark
     *                                      available for all users
     *
     * @return Bookmark|false
     */
    public static Bookmark createBookmark(
        DatabaseInterface $dbi,
        String $user,
        Map<String, String> $bkm_fields,
        boolean $all_users /*= false*/
    ) {
        if (empty($bkm_fields.get("bkm_sql_query")) || empty($bkm_fields.get("bkm_sql_query"))
        ) {
            return null;
        }

        Bookmark $bookmark = new Bookmark($dbi, $user);
        $bookmark._database = $bkm_fields.get("bkm_database");
        $bookmark._label = $bkm_fields.get("bkm_label");
        $bookmark._query = $bkm_fields.get("bkm_sql_query");
        $bookmark._user = $all_users ? "" : $bkm_fields.get("bkm_user");

        return $bookmark;
    }

    /**
     * @param DatabaseInterface $dbi  DatabaseInterface object
     * @param string            $user Current user
     * @param array             $row  Resource used to build the bookmark
     *
     * @return Bookmark
     */
    protected static Bookmark createFromRow(
        DatabaseInterface $dbi,
        String $user,
        Map<String, Object> $row
    ) {
    	Bookmark $bookmark = new Bookmark($dbi, $user);
        $bookmark._id = (Integer)$row.get("id");
        $bookmark._database = (String) $row.get("dbase");
        $bookmark._user = (String) $row.get("user");
        $bookmark._label = (String) $row.get("label");
        $bookmark._query = (String) $row.get("query");
        return $bookmark;
    }

    /**
     * Gets the list of bookmarks defined for the current database
     *
     * @param DatabaseInterface $dbi  DatabaseInterface object
     * @param string            $user Current user
     * @param string|bool       $db   the current database name or false
     *
     * @return Bookmark[] the bookmarks list
     * @throws SQLException 
     *
     * @access public
     */
    public static List<Bookmark> getList(
        DatabaseInterface $dbi,
        String $user,
        String $db /*= false*/
    ) throws SQLException {
    	List<Bookmark> $bookmarks = new ArrayList<>();
    	
        $cfgBookmark = getParams($user);
        if (empty($cfgBookmark)) {
            return $bookmarks;
        }

        String $query = "SELECT * FROM " + Util.backquote((String) $cfgBookmark.get("db"))
            + "." + Util.backquote((String) $cfgBookmark.get("table"))
            + " WHERE ( `user` = ''"
            + " OR `user` = '" + $dbi.escapeString((String) $cfgBookmark.get("user")) + "' )";
        if ($db != null) {
            $query += " AND dbase = '" + $dbi.escapeString($db) + "'";
        }
        $query += " ORDER BY label ASC";

        Map<String, Map<Object, Object>> $result = (Map)$dbi.fetchResult(
            $query,
            null,
            null,
            DatabaseInterface.CONNECT_CONTROL,
            DatabaseInterface.QUERY_STORE
        );

        if (! empty($result)) {
            
            for (Map $row : $result.values()) {
                $bookmarks.add(createFromRow($dbi, $user, $row));
            }

            return $bookmarks;
        }

        return $bookmarks;
    }
    
    public static List<Bookmark> getList(
            DatabaseInterface $dbi,
            String $user) throws SQLException {
    	return getList($dbi, $user, null);
    }

    /**
     * Retrieve a specific bookmark
     *
     * @param DatabaseInterface $dbi                 DatabaseInterface object
     * @param string            $user                Current user
     * @param string            $db                  the current database name
     * @param mixed             $id                  an identifier of the bookmark to get
     * @param string            $id_field            which field to look up the identifier
     * @param boolean           $action_bookmark_all true: get all bookmarks regardless
     *                                               of the owning user
     * @param boolean           $exact_user_match    whether to ignore bookmarks with no user
     *
     * @return Bookmark the bookmark
     * @throws SQLException 
     *
     * @access  public
     *
     */
    public static Bookmark get(
        DatabaseInterface $dbi,
        String $user,
        String $db,
        int $id,
        String $id_field /*= "id"*/,
        boolean $action_bookmark_all /*= false*/,
        boolean $exact_user_match /*= false*/
    ) throws SQLException {
        $cfgBookmark = getParams($user);
        if (empty($cfgBookmark)) {
            return null;
        }

        String $query = "SELECT * FROM " + Util.backquote((String) $cfgBookmark.get("db"))
        + "." + Util.backquote((String) $cfgBookmark.get("table"))
            + " WHERE dbase = '" + $dbi.escapeString($db) + "'";
        if (! $action_bookmark_all) {
            $query += " AND (user = '"
                + $dbi.escapeString((String) $cfgBookmark.get("user")) + "'";
            if (! $exact_user_match) {
                $query += " OR user = ''";
            }
            $query += ')';
        }
        $query += " AND " + Util.backquote($id_field)
            + " = '" + $dbi.escapeString(Integer.toString($id)) + "' LIMIT 1"; //FIXME mysql-specific 

        Map<String, Object> $result = (Map)$dbi.fetchSingleRow($query, "ASSOC", DatabaseInterface.CONNECT_CONTROL);
        if (! empty($result)) {
            return createFromRow($dbi, $user, $result);
        }

        return null;
    }
}
