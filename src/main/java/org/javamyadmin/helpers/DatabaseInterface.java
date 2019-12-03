package org.javamyadmin.helpers;

import java.util.Hashtable;
import java.util.Map;

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

}
