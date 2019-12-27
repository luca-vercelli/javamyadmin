package org.javamyadmin.helpers;

/**
 * Table metadata
 *
 */
public class Table {

	private String table_name;
	String db_name;
	DatabaseInterface databaseInterface;

	public Table(String $table_name, String $db_name, DatabaseInterface $databaseInterface) {
		this.table_name = $table_name;
		this.db_name = $db_name;
		this.databaseInterface = $databaseInterface;
	}

	public boolean isView() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isUpdatableView() {
		// TODO Auto-generated method stub
		return false;
	}

	public int countRecords() {
		// TODO Auto-generated method stub
		return 0;
	}

	// TODO
}
