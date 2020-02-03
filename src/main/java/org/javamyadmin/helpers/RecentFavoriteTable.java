package org.javamyadmin.helpers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.javamyadmin.php.Globals;
import org.javamyadmin.php.Php.SessionMap;
import static org.javamyadmin.php.Php.*;

/**
 * Handles the recently used and favorite tables.
 *
 * @TODO Change the release version in table pma_recent
 * (#recent in documentation)
 */
public class RecentFavoriteTable
{
    /**
     * Reference to session variable containing recently used or favorite tables.
     *
     * @access private
     * @var array
     */
    private List<Map<String, String>> _tables;

    /**
     * Defines type of action, Favorite or Recent table.
     *
     * @access private
     * @var String
     */
    private String _tableType;

    /**
     * RecentFavoriteTable instances.
     *
     * @access private
     * @var array
     */
    private static Map<String, RecentFavoriteTable> _instances = new HashMap<>();

    /** @var Relation */
    // TODO private relation;

    /**
     * Creates a new instance of RecentFavoriteTable
     *
     * @param String $type the table type
     *
     * @access private
     */
    private RecentFavoriteTable(String $type, SessionMap $_SESSION, Globals GLOBALS)
    {
        //this.relation = new Relation(GLOBALS.getDbi());
        this._tableType = $type;
        String $server_id = Integer.toString(GLOBALS.getServer());
        if (empty(multiget($_SESSION, "tmpval", this._tableType + "Tables", $server_id))
        ) {
        	Map newval = !empty(this._getPmaTable()) ? this.getFromDb(GLOBALS) : new HashMap();
        	multiput($_SESSION, "tmpval", this._tableType + "Tables", $server_id, 
        			newval);
        }
        this._tables = new ArrayList<>();
        Map tables = (Map) multiget($_SESSION, "tmpval", this._tableType + "Tables", $server_id);
        this._tables.addAll(tables.values());
    }

    /**
     * Returns class instance.
     *
     * @param String $type the table type
     *
     * @return RecentFavoriteTable
     */
    public static RecentFavoriteTable getInstance(String $type, SessionMap $_SESSION, Globals GLOBALS)
    {
        if (! _instances.containsKey($type)) {
        	_instances.put($type, new RecentFavoriteTable($type, $_SESSION, GLOBALS));
        }
        return _instances.get($type);
    }

    /**
     * Returns the recent/favorite tables array
     *
     * @return array
     */
    public List<Map<String, String>> getTables()
    {
        return this._tables;
    }

    /**
     * Returns recently used tables or favorite from phpMyAdmin database.
     *
     * @return array
     */
    public Map getFromDb(Globals GLOBALS)
    {
    	/* TODO
        // Read from phpMyAdmin database, if recent tables is not in session
        String $sql_query
            = " SELECT tables FROM " + this._getPmaTable() +
            " WHERE username = '" + GLOBALS.getDbi().escapeString(multiget(GLOBALS.getConfig().settings, "Server", "user")) + "'";

        Map $return = new HashMap();
        ResultSet $result = this.relation.queryAsControlUser($sql_query, false);
        if ($result.next()) {
            Map $row = GLOBALS.getDbi().fetchArray($result);
            if ($row.containsKey(0)) {
                $return = (Map) json_decode((String)$row.get(0));
            }
        }
        return $return;*/ return new HashMap();
    }

    /**
     * Save recent/favorite tables into phpMyAdmin database.
     *
     * @return true|Message
     * @throws SQLException 
     */
    public Message saveToDb(Globals GLOBALS) throws SQLException
    {
        String $username = (String)multiget(GLOBALS.getConfig().settings, "Server", "user");
        String $sql_query
            = " REPLACE INTO " + this._getPmaTable() + " (`username`, `tables`)" +
                " VALUES ('" + GLOBALS.getDbi().escapeString($username) + "', '"
                + GLOBALS.getDbi().escapeString(
                    json_encode(this._tables)
                ) + "')";

        ResultSet $success = GLOBALS.getDbi().tryQuery($sql_query, DatabaseInterface.CONNECT_CONTROL);

        if (!$success.next()) {
            String $error_msg = "";
            switch (this._tableType) {
                case "recent":
                    $error_msg = __("Could not save recent table!");
                    break;

                case "favorite":
                    $error_msg = __("Could not save favorite table!");
                    break;
            }
            Message $message = Message.error($error_msg);
            $message.addMessage(
                Message.rawError(
                    GLOBALS.getDbi().getError(DatabaseInterface.CONNECT_CONTROL)
                ),
                "<br><br>"
            );
            return $message;
        }
        return null;
    }

    /**
     * Trim recent.favorite table according to the
     * NumRecentTables/NumFavoriteTables configuration.
     *
     * @return boolean True if trimming occurred
     */
    public boolean trim()
    {
    	String num = (String) Globals.getConfig().get("Num" + ucfirst(this._tableType) + "Tables");
    	if (empty(num)) {
    		num = "0";
    	}
        int $max = new Integer(num);
        boolean $trimming_occurred = this._tables.size() > $max;
        if ($trimming_occurred) {
        	this._tables = this._tables.subList(0, $max);
        }
        return $trimming_occurred;
    }

    /**
     * Return HTML ul.
     *
     * @return String
     */
    public String getHtmlList(HttpServletRequest req, SessionMap $_SESSION, Globals GLOBALS)
    {
        String $html = "";
        if (this._tables.size() > 0) {
            if ("recent".equals(this._tableType)) {
                for (Map<String,String> $table : this._tables) {
                    $html += "<li class='warp_link'>";
                    Map<String, String> map = new HashMap<>();
                    map.put("db", $table.get("db"));
                    map.put("table", $table.get("table"));
                    String $recent_url = Url.getFromRoute("/table/recent-favorite", map, req, GLOBALS);
                    $html += "<a href='" + $recent_url + "'>`"
                          + htmlspecialchars($table.get("db")) + "`.`"
                          + htmlspecialchars($table.get("table")) + "`</a>";
                    $html += "</li>";
                }
            } else {
                for (Map<String, String> $table : this._tables) {
                    $html += "<li class='warp_link'>";

                    $html += "<a class='ajax favorite_table_anchor' ";
                    Map<String, String> map = new HashMap<>();
                    map.put("db", $table.get("db"));
                    map.put("ajax_request", "true");
                    map.put("favorite_table", $table.get("table"));
                    map.put("remove_favorite", "true");
                    String $fav_rm_url = Url.getFromRoute("/database/structure/favorite-table", map, req, GLOBALS);
                    $html += "href='" + $fav_rm_url
                        + "' title='" + __("Remove from Favorites")
                        + "' data-favtargetn='"
                        + md5($table.get("db") + "." + $table.get("table"))
                        + "' >"
                        + Util.getIcon("b_favorite", null, GLOBALS, $_SESSION)
                        + "</a>";

                    Map<String, String> map2 = new HashMap<>();
                    map2.put("db", $table.get("db"));
                    map2.put("table", $table.get("table"));
                    String $table_url = Url.getFromRoute("/table/recent-favorite", map2, req, GLOBALS);
                    $html += "<a href='" + $table_url + "'>"
                        + htmlspecialchars($table.get("db")) + "."
                        + htmlspecialchars($table.get("table")) + "</a>";
                    $html += "</li>";
                }
            }
        } else {
            $html += "<li class='warp_link'>"
                  + (this._tableType == "recent"
                    ? __("There are no recent tables.")
                    : __("There are no favorite tables."))
                  + "</li>";
        }
        return $html;
    }

    /**
     * Return HTML.
     *
     * @return String
     */
    public String getHtml(HttpServletRequest req, SessionMap $_SESSION, Globals GLOBALS)
    {
        String $html  = "<div class='drop_list'>";
        if (this._tableType == "recent") {
            $html += "<button title='" + __("Recent tables")
                + "' class='drop_button btn'>"
                + __("Recent") + "</button><ul id='pma_recent_list'>";
        } else {
            $html += "<button title='" + __("Favorite tables")
                + "' class='drop_button btn'>"
                + __("Favorites") + "</button><ul id='pma_favorite_list'>";
        }
        $html += this.getHtmlList(req, $_SESSION, GLOBALS);
        $html += "</ul></div>";
        return $html;
    }

    /**
     * Add recently used or favorite tables.
     *
     * @param String $db    database name where the table is located
     * @param String $table table name
     *
     * @return true|Message True if success, Message if not
     * @throws SQLException 
     */
    public Message add(String $db, String $table, Globals GLOBALS) throws SQLException
    {
        // If table does not exist, do not add._getPmaTable()
        if (! GLOBALS.getDbi().getColumns($db, $table).next()) {
            return null;
        }

        Map<String, String> $table_arr = new HashMap<>();
        $table_arr.put("db", $db);
        $table_arr.put("table", $table);

        // add only if this is new table
        if ((this._tables.isEmpty()) || !this._tables.get(0).equals($table_arr)) {
        	this._tables.add(0, $table_arr);
            this.trim();
            if (!empty(this._getPmaTable())) {
                return this.saveToDb(GLOBALS);
            }
        }
        return null;
    }

    /**
     * Removes recent/favorite tables that don"t exist.
     *
     * @param String $db    database
     * @param String $table table
     *
     * @return boolean|Message True if invalid and removed, False if not invalid,
     *                            Message if error while removing
     * @throws SQLException 
     */
    public Message removeIfInvalid(String $db, String $table, Globals GLOBALS) throws SQLException
    {
        for (Map<String, String> $tbl : this._tables) {
            if ($tbl.get("db").equals($db) && $tbl.get("table").equals($table)) {
                // TODO Figure out a better way to find the existence of a table
                if (! GLOBALS.getDbi().getColumns($tbl.get("db"), $tbl.get("table")).next()) {
                    return this.remove($tbl.get("db"), $tbl.get("table"));
                }
            }
        }
        return null;
    }

    /**
     * Remove favorite tables.
     *
     * @param String $db    database name where the table is located
     * @param String $table table name
     *
     * @return true|Message True if success, Message if not
     */
    public Message remove(String $db, String $table)
    {
        /* TODO foreach (this._tables as $key => $value) {
            if ($value.get("db") == $db && $value.get("table") == $table) {
                unset(this._tables[$key]);
            }
        }
        if (this._getPmaTable()) {
            return this.saveToDb();
        }*/
        return null;
    }

    /**
     * Generate Html for sync Favorite tables anchor. (from localStorage to pmadb)
     *
     * @return String
     */
    public String getHtmlSyncFavoriteTables(HttpServletRequest req, SessionMap $_SESSION, Globals GLOBALS)
    {
        String $retval = "";
        int $server_id = GLOBALS.getServer();
        if ($server_id == 0) {
            return "";
        }
        //FIXME $cfgRelation = this.relation.getRelationsParam();
        // Not to show this once list is synchronized.
        if (/*$cfgRelation["favoritework"] &&*/ empty(multiget($_SESSION, "tmpval", "favorites_synced", $server_id))) {

            Map<String, String> map = new HashMap<>();
            map.put("ajax_request", "true");
            map.put("favorite_table", "true");
            map.put("sync_favorite_tables", "true");
            String $url = Url.getFromRoute("/database/structure/favorite-table", map, req, GLOBALS);
            $retval  = "<a class='hide' id='sync_favorite_tables'";
            $retval += " href='" + $url + "'></a>";
        }
        return $retval;
    }

    /**
     * Generate Html to update recent tables.
     *
     * @return String html
     */
    public static String getHtmlUpdateRecentTables(HttpServletRequest httpRequest, Globals GLOBALS)
    {
        String $retval = "<a class='hide' id='update_recent_tables' href='";
        Map<String, String> map = new HashMap<>();
        map.put("ajax_request", "true");
        map.put("recent_table", "true");
        $retval += Url.getFromRoute("/recent-table", map, httpRequest, GLOBALS);
        $retval += "'></a>";
        return $retval;
    }

    /**
     * Return the name of the configuration storage table
     *
     * @return String|null pma table name
     */
    private String _getPmaTable()
    {
        /* TODO $cfgRelation = this.relation.getRelationsParam();
        if (! $cfgRelation["recentwork"]) {
            return null;
        }

        if (! empty($cfgRelation.get("db"))
            && ! empty($cfgRelation[this._tableType])
        ) {
            return Util.backquote($cfgRelation.get("db")) + "."
                + Util.backquote($cfgRelation[this._tableType]);
        }*/
        return null;
    }
}