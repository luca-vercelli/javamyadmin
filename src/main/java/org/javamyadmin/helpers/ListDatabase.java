package org.javamyadmin.helpers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.javamyadmin.php.Array;
import org.javamyadmin.php.Globals;

import static org.javamyadmin.php.Php.*;

public class ListDatabase extends ListAbstract {

	private static final long serialVersionUID = 3916639182918450080L;

	/**
	 * Constructor. Call this.build().
	 * 
	 * @param GLOBALS
	 * @throws SQLException
	 */
	public ListDatabase(Globals GLOBALS) throws SQLException {

		super(GLOBALS);

		// TODO $checkUserPrivileges = new CheckUserPrivileges($GLOBALS['dbi']);
		// $checkUserPrivileges.getPrivileges();

		this.build();
	}

	/**
	 * checks if the configuration wants to hide some databases
	 *
	 * @return void
	 */
	protected void checkHideDatabase() {
		if (empty(((Map) GLOBALS.getConfig().get("Server")).get("hide_db"))) {
			return;
		}

		Pattern pattern = Pattern.compile((String) ((Map) GLOBALS.getConfig().get("Server")).get("hide_db"));

		// cannot remove items from list while iterating over itself
		List<String> copy = new ArrayList<>();
		copy.addAll(this);

		for (String $db : copy) {
			Matcher matcher = pattern.matcher($db);
			if (matcher.matches()) {
				this.remove($db);
			}
		}
	}

	/**
	 * retrieves database list from server
	 *
	 * @param string
	 *            $like_db_name usually a db_name containing wildcards
	 *
	 * @return array
	 * @throws SQLException
	 */
	protected List<String> retrieve(String $like_catalog_name /* = null */, String $like_db_name /* = null */)
			throws SQLException {
		// TODO support fog cfg.Server.DisableIS
		// and cfg.NaturalOrder

		List<String> $database_list = new ArrayList<>();

		if (GLOBALS.getDbToTest() == null) {
			Connection connection = GLOBALS.getDbi().getLink(DatabaseInterface.CONNECT_USER);
			ResultSet rs = connection.getMetaData().getSchemas($like_catalog_name, $like_db_name);
			while (rs.next()) {
				$database_list.add(rs.getString(1));
			}
		} else {
			for (String $db : GLOBALS.getDbToTest()) {
				$database_list.addAll(retrieve($like_catalog_name, $db));
			}
		}

		Collections.sort($database_list);

		return $database_list;
	}

	protected List<String> retrieve(String $like_db_name /* = null */) throws SQLException {
		return retrieve(null, $like_db_name);
	}

	protected List<String> retrieve() throws SQLException {
		return retrieve(null, null);
	}

	/**
	 * builds up the list
	 *
	 * @return void
	 * @throws SQLException
	 */
	@Override
	public void build() throws SQLException {
		if (!this.checkOnlyDatabase()) {
			List<String> $items = this.retrieve();
			this.clear();
			this.addAll($items);
		}

		this.checkHideDatabase();
	}

	/**
     * checks the only_db configuration
     *
     * @return boolean false if there is no only_db, otherwise true
	 * @throws SQLException 
     */
    protected boolean checkOnlyDatabase() throws SQLException
    {
    	Object only_db = ((Map) GLOBALS.getConfig().get("Server")).get("only_db");
        if (only_db instanceof String && !empty(only_db)
        ) {
        	Array arr = new Array();
        	arr.add(only_db);
        	multiput(GLOBALS.getConfig().settings, "Server", "only_db", arr);
        	only_db = arr;
        } else if (! is_array(only_db)) {
            return false;
        }

        List<String> $items = new ArrayList<>();
        Collection<String> entries = ((Map)only_db).values();
        for (String $each_only_db : entries) {
            // check if the db name contains wildcard,
            // thus containing not escaped _ or %
            /* TODO if (! preg_match("/(^|[^\\\\])(_|%)/", $each_only_db)) {
                // ... not contains wildcard
                $items[] = Util.unescapeMysqlWildcards($each_only_db);
                continue;
            }*/

            $items.addAll(this.retrieve($each_only_db));
        }

        this.clear();
        this.addAll($items);

        return true;
    }

	/**
	 * returns default item
	 *
	 * @return string default item
	 */
	public String getDefault() {
		if (!empty(GLOBALS.getDb())) {
			return GLOBALS.getDb();
		}

		return this.getEmpty();
	}
	
	/**
	 * This is for compatibility reasons...
	 * @return
	 */
	public ListDatabase getDatabases() {
		return this;
	}
}
