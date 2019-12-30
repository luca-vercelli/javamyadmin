package org.javamyadmin.helpers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.javamyadmin.php.Globals;

/**
 * Table metadata
 *
 */
public class Table {

    /**
     * @var string  table name
     */
    protected String _name = null;
    /**
     * @var string  database name, aka schema
     */
    protected String _db_name = null;
    /**
     * @var string  catalog name (in MySQL always null)
     */
    protected String _catalog_name = null;
    /**
     * @var DatabaseInterface
     */
    protected DatabaseInterface _dbi;
    /**
     * @var Relation
     */
    //private Relation $relation; TODO?

	public Table(String $table_name, String $db_name, DatabaseInterface $databaseInterface) {
		this._name = $table_name;
		this._db_name = $db_name;
		this._dbi = $databaseInterface;
	}

	public Table(String $table_name, String $db_name, String $catalog_name, DatabaseInterface $databaseInterface) {
		this._name = $table_name;
		this._db_name = $db_name;
		this._catalog_name = $catalog_name;
		this._dbi = $databaseInterface;
	}

    /**
     * returns whether the table is actually a view
     *
     * @return boolean whether the given is a view
     * @throws SQLException 
     */
	public boolean isView() throws SQLException {
		ResultSet metadata = this._dbi._links.get(DatabaseInterface.CONNECT_USER).getMetaData()
				.getTables(_catalog_name, _db_name, _name, new String[] {"VIEW"});
		return metadata.next();
	}

    /**
     * Returns whether the table is actually an updatable view
     *
     * @return boolean whether the given is an updatable view
     */
	public boolean isUpdatableView() {
		return false; //Unsupported
	}

    /**
     * Checks if this is a merge table
     *
     * If the ENGINE of the table is MERGE or MRG_MYISAM (alias),
     * this is a merge table.
     *
     * @return boolean  true if it is a merge table
     */
    public boolean isMerge()
    {
		return false; //Unsupported
    }
    
    /**
     * Counts and returns (or displays) the number of records in a table
     *
     * @param bool $force_exact whether to force an exact count
     *
     * @return mixed the number of records if "retain" param is true,
     *               otherwise true
     * @throws SQLException 
     */
    public Long countRecords(boolean $force_exact /*= false*/) throws SQLException
    {
        // TODO boolean $is_view = this.isView();
        String $db = this._db_name;
        String $table = this._name;
        /* TODO
        if (this._dbi.getCachedTableContent([$db, $table, "ExactRows"]) != null) {
            $row_count = this._dbi.getCachedTableContent(
                [
                    $db,
                    $table,
                    "ExactRows",
                ]
            );
            return $row_count;
        }*/
        Long $row_count = null;
        /* TODO if (! $force_exact) {
            if ((this._dbi.getCachedTableContent([$db, $table, "Rows"]) == null)
                && ! $is_view
            ) {
                $tmp_tables = this._dbi.getTablesFull($db, $table);
                if (isset($tmp_tables[$table])) {
                    this._dbi.cacheTableContent(
                        [
                            $db,
                            $table,
                        ],
                        $tmp_tables[$table]
                    );
                }
            }
            if (this._dbi.getCachedTableContent([$db, $table, "Rows"]) != null) {
                $row_count = this._dbi.getCachedTableContent(
                    [
                        $db,
                        $table,
                        "Rows",
                    ]
                );
            } else {
                $row_count = false;
            }
        }*/
        // for a VIEW, $row_count is always false at this point
        if ($row_count != null
            && $row_count >= new Long((String)Globals.getConfig().get("MaxExactCount"))
        ) {
            return $row_count;
        }
        //if (! $is_view) {
            $row_count = (Long) this._dbi.fetchValue(
                "SELECT COUNT(*) FROM " + Util.backquote($db) + "."
                + Util.backquote($table)
            );
        /* TODO } else {
            // For complex views, even trying to get a partial record
            // count could bring down a server, so we offer an
            // alternative: setting MaxExactCountViews to 0 will bypass
            // completely the record counting for views
            if ("0".equals((String)Globals.getConfig().get("MaxExactCountViews"))) {
                $row_count = null;
            } else {
                // Counting all rows of a VIEW could be too long,
                // so use a LIMIT clause.
                // Use try_query because it can fail (when a VIEW is
                // based on a table that no longer exists)
                ResultSet $result = this._dbi.tryQuery(
                    "SELECT 1 FROM " + Util.backquote($db) + "."
                    + Util.backquote($table) 
                    //+ " LIMIT " + $GLOBALS["cfg"]["MaxExactCountViews"]
                    ,
                    DatabaseInterface.CONNECT_USER,
                    DatabaseInterface.QUERY_STORE
                );
                if (! this._dbi.getError()) {
                    $row_count = this._dbi.numRows($result);
                }
            }
        }
        if ($row_count) {
            this._dbi.cacheTableContent([$db, $table, "ExactRows"], $row_count);
        }*/
        return $row_count;
    } // end of the "Table.countRecords()" function

    public long countRecords() throws SQLException {
    	return countRecords(false);
    }

    /**
     * Returns the comments for current table.
     *
     * @return string Return comment info if it is set for the selected table or return blank.
     */
    public String getComment()
    {
    	return ""; /* TODO
        $table_comment = $this->getStatusInfo('TABLE_COMMENT', false, true);
        if ($table_comment === false) {
            return '';
        }
        return $table_comment;*/
    }

	public Integer getRealRowCountTable() {
		// TODO Auto-generated method stub
		return null;
	}
}
