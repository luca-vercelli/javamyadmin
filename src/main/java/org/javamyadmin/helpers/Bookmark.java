package org.javamyadmin.helpers;

import static org.javamyadmin.php.Php.empty;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Handles bookmarking SQL queries
 *
 * @package PhpMyAdmin
 */
public class Bookmark {
	
    /**
     * ID of the bookmark
     *
     * @var int
     */
    int _id;
    /**
     * Database the bookmark belongs to
     *
     * @var string
     */
    String _database;
    /**
     * The user to whom the bookmark belongs, empty for public bookmarks
     *
     * @var string
     */
    String _user;
    /**
     * Label of the bookmark
     *
     * @var string
     */
    String _label;
    /**
     * SQL query that is bookmarked
     *
     * @var string
     */
    String _query;

    /**
     * Current user
     *
     * @var string
     */
    String user;

    /**
     * Bookmark constructor.
     *
     * @param DatabaseInterface $dbi  DatabaseInterface object
     * @param string            $user Current user
     */
    public Bookmark(String $user)
    {
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
     * Returns the number of variables in a bookmark
     *
     * @return int number of variables
     */
    public int getVariableCount()
    {
    	Pattern pattern = Pattern.compile("\\[VARIABLE[0-9]*\\]");
    	return pattern.matcher(this._query).groupCount();
    }
}
