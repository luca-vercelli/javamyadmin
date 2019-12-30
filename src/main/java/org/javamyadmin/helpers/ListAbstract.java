package org.javamyadmin.helpers;

import java.sql.SQLException;
import java.util.ArrayList;

import org.javamyadmin.php.Globals;

import static org.javamyadmin.php.Php.*;

/**
 * Generic list class
 *
 * @todo add caching
 * @abstract
 * @package PhpMyAdmin
 * @since phpMyAdmin 2.9.10
 */
public abstract class ListAbstract extends ArrayList<String> {

	// JMA comment: please notice this is *not* a generic list,
	// this is a generic list of databases :( FIXME

	protected Globals GLOBALS;
	
	private static final long serialVersionUID = -4068643271489886965L;


	public ListAbstract(Globals GLOBALS) throws SQLException {

		this.GLOBALS = GLOBALS;
	}
	
	/*
	 * exists -> containsAll
	 */

	/**
	 * @var mixed empty item
	 */
	protected final String item_empty = "";

	/**
	 * defines what is an empty item (0, "", false or null)
	 *
	 * @return mixed an empty item
	 */
	public String getEmpty() {
		return this.item_empty;
	}

	/**
     * returns HTML <option>-tags to be used inside <select></select>
     *
     * @param mixed   $selected                   the selected db or true for
     *                                            selecting current db
     * @param boolean $include_information_schema whether include information schema
     *
     * @return string  HTML option tags
     */
    public String getHtmlOptions(
        String $selected /*= ""*/,
        boolean $include_information_schema /*= true*/,
        Globals GLOBALS
    ) {
        if ($selected == null) {
            $selected = this.getDefault();
        }

        String $options = "";
        for (String $each_item : this) {
            if (!$include_information_schema
                && GLOBALS.getDbi().isSystemSchema($each_item)
            ) {
                continue;
            }
            $options += "<option value='" + htmlspecialchars($each_item) + "'";
            if ($selected.equals($each_item)) {
                $options += " selected='selected'";
            }
            $options += ">" + htmlspecialchars($each_item) + "</option>" + '\n';
        }

        return $options;
    }

	public String getHtmlOptions(Globals GLOBALS) {
		return getHtmlOptions("", true, GLOBALS);
	}

	/**
	 * returns default item
	 *
	 * @return string default item
	 */
	public String getDefault() {
		return this.getEmpty();
	}

    /**
     * builds up the list
     *
     * @return void
     * @throws SQLException 
     */
    abstract public void build() throws SQLException;
}
