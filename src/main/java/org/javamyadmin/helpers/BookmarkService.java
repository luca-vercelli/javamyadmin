package org.javamyadmin.helpers;

import static org.javamyadmin.php.Php.empty;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
public class BookmarkService {
    
	/*
	 * FIXME. This is something like a DAO. Should use some persistence framework.
	 */
	
    @Autowired
    private Util util;

    @Autowired
    private DatabaseInterface dbi;
    
    private static Map<String, Object> $cfgBookmark = null;

    /**
     * Adds a bookmark
     *
     * @return boolean whether the INSERT succeeds or not
     * @throws SQLException 
     *
     * @access public
     */
    public boolean save(Bookmark bookmark) throws SQLException
    {
        Map<String, Object> $cfgBookmark = getParams(bookmark.user);
        if (empty($cfgBookmark)) {
            return false;
        }

        String $query = "INSERT INTO " + util.backquote((String) $cfgBookmark.get("db"))
            + '.' + util.backquote((String) $cfgBookmark.get("table"))
            + " (id, dbase, user, query, label) VALUES (NULL, "
            + "'" + this.dbi.escapeString(bookmark._database) + "', "
            + "'" + this.dbi.escapeString(bookmark._user) + "', "
            + "'" + this.dbi.escapeString(bookmark._query) + "', "
            + "'" + this.dbi.escapeString(bookmark._label) + "')";
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
    public boolean delete(Bookmark bookmark)
    {
        Map<String, Object> $cfgBookmark = getParams(bookmark.user);
        if (empty($cfgBookmark)) {
            return false;
        }

        String $query  = "DELETE FROM " + util.backquote((String) $cfgBookmark.get("db"))
            + "." + util.backquote((String) $cfgBookmark.get("table"))
            + " WHERE id = " + bookmark._id;
        this.dbi.tryQuery($query, DatabaseInterface.CONNECT_CONTROL);
        return true;
    }
    
    /**
     * Defines the bookmark parameters for the current user
     *
     * @param string $user Current user
     *
     * @return array|bool the bookmark parameters for the current user
     * @access  public
     */
    public Map<String, Object> getParams(String $user)
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
    @Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Bookmark createBookmark(
        DatabaseInterface $dbi,
        String $user,
        Map<String, String> $bkm_fields,
        boolean $all_users /*= false*/
    ) {
        if (empty($bkm_fields.get("bkm_sql_query")) || empty($bkm_fields.get("bkm_sql_query"))
        ) {
            return null;
        }

        Bookmark $bookmark = new Bookmark($user);
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
    protected Bookmark createFromRow(
        DatabaseInterface $dbi,
        String $user,
        Map<String, Object> $row
    ) {
    	Bookmark $bookmark = new Bookmark($user);
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
    public List<Bookmark> getList(
        DatabaseInterface $dbi,
        String $user,
        String $db /*= false*/
    ) throws SQLException {
    	List<Bookmark> $bookmarks = new ArrayList<>();
    	
        $cfgBookmark = getParams($user);
        if (empty($cfgBookmark)) {
            return $bookmarks;
        }

        String $query = "SELECT * FROM " + util.backquote((String) $cfgBookmark.get("db"))
            + "." + util.backquote((String) $cfgBookmark.get("table"))
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
    
    public List<Bookmark> getList(
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
    @Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Bookmark get(
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

        String $query = "SELECT * FROM " + util.backquote((String) $cfgBookmark.get("db"))
        + "." + util.backquote((String) $cfgBookmark.get("table"))
            + " WHERE dbase = '" + $dbi.escapeString($db) + "'";
        if (! $action_bookmark_all) {
            $query += " AND (user = '"
                + $dbi.escapeString((String) $cfgBookmark.get("user")) + "'";
            if (! $exact_user_match) {
                $query += " OR user = ''";
            }
            $query += ')';
        }
        $query += " AND " + util.backquote($id_field)
            + " = '" + $dbi.escapeString(Integer.toString($id)) + "' LIMIT 1"; //FIXME mysql-specific 

        Map<String, Object> $result = (Map)$dbi.fetchSingleRow($query, "ASSOC", DatabaseInterface.CONNECT_CONTROL);
        if (! empty($result)) {
            return createFromRow($dbi, $user, $result);
        }

        return null;
    }

    /**
     * Replace the placeholders in the bookmark query with variables
     *
     * @param array $variables array of variables
     *
     * @return string query with variables applied
     */
    public String applyVariables(Bookmark bookmark, List<String> $variables)
    {
        // remove comments that encloses a variable placeholder
        /* TODO String $query = preg_replace(
            "|/\\*(.*\\[VARIABLE[0-9]*\\].*)\\* /|imsU",
            "${1}",
            this._query
        );*/
    	String $query = bookmark._query;
        // replace variable placeholders with values
        int $number_of_variables = bookmark.getVariableCount();
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

}
