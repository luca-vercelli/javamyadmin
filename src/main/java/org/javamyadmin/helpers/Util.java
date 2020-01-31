package org.javamyadmin.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

import org.javamyadmin.helpers.Menu.MenuStruct;
import org.javamyadmin.jtwig.JtwigFactory;
import org.javamyadmin.php.Array;
import org.javamyadmin.php.Globals;
import org.javamyadmin.php.Php.SessionMap;

import static org.javamyadmin.php.Php.*;

/**
 * Misc functions used all over the scripts.
 *
 * @package PhpMyAdmin
 */
public class Util {
    /**
     * Checks whether configuration value tells to show icons.
     *
     * @param String $value Configuration option name
     *
     * @return boolean Whether to show icons.
     */
    public static boolean showIcons(String $value, Globals GLOBALS)
    {
    	String type = (String) Globals.getConfig().get($value);
    	return "icons".equals(type) || "both".equals(type);
    }

    /**
     * Checks whether configuration value tells to show text.
     *
     * @param String $value Configuration option name
     *
     * @return boolean Whether to show text.
     */
    public static boolean showText(String $value, Globals GLOBALS)
    {
    	String type = (String) Globals.getConfig().get($value);
    	return "text".equals(type) || "both".equals(type);
    }

    /**
     * Returns an HTML IMG tag for a particular icon from a theme,
     * which may be an actual file or an icon from a sprite.
     * This function takes into account the ActionLinksMode
     * configuration setting and wraps the image tag in a span tag.
     *
     * @param String  $icon          name of icon file
     * @param String  $alternate     alternate text
     * @param boolean $force_text    whether to force alternate text to be displayed
     * @param boolean $menu_icon     whether this icon is for the menu bar or not
     * @param String  $control_param which directive controls the display
     *
     * @return String an html snippet
     */
    public static String getIcon(
        String $icon,
        String $alternate /*= ""*/,
        boolean $force_text /*= false*/,
        boolean $menu_icon /*= false*/,
        String $control_param /*= "ActionLinksMode"*/,
        Globals GLOBALS,
        SessionMap session
    ) {
        boolean $include_icon = false;
        boolean $include_text = false;
        if (showIcons($control_param, GLOBALS)) {
            $include_icon = true;
        }
        if ($force_text
            || showText($control_param, GLOBALS)
        ) {
            $include_text = true;
        }
        // Sometimes use a span (we rely on this in js/sql.js). But for menu bar
        // we don"t need a span
        String $button = $menu_icon ? "" : "<span class='nowrap'>";
        if ($include_icon) {
            $button += getImage($icon, $alternate, session);
        }
        if ($include_icon && $include_text) {
            $button += "&nbsp;";
        }
        if ($include_text) {
            $button += $alternate;
        }
        $button += $menu_icon ? "" : "</span>";

        return $button;
    }

    public static String getIcon(
            String $icon,
            String $alternate /*= ""*/,
            Globals GLOBALS,
            SessionMap session
        ) {
    	return getIcon(
    	         $icon,
    	         $alternate,
    	        false,false,"ActionLinksMode",
    	         GLOBALS,
    	         session
    	    ) ;
    }
    
    /**
     * Returns an HTML IMG tag for a particular image from a theme
     *
     * The image name should match CSS class defined in icons.css.php
     *
     * @param String $image      The name of the file to get
     * @param String $alternate  Used to set "alt" and "title" attributes
     *                           of the image
     * @param array  $attributes An associative array of other attributes
     *
     * @return String an html IMG tag
     */
    /**
     * Returns an HTML IMG tag for a particular image from a theme
     *
     * The image name should match CSS class defined in icons.css.php
     *
     * @param string image      The name of the file to get
     * @param string alternate  Used to set "alt" and "title" attributes
     *                           of the image
     * @param array  attributes An associative array of other attributes
     *
     * @return string an html IMG tag
     */
    public static String getImage(String $image, String $alternate /*= ""*/, Map<String, Object> $attributes /*= []*/)
    {
    	if ($alternate == null) $alternate = "";
    	if ($attributes == null) $attributes = new HashMap<>();
    			
        $alternate = htmlspecialchars($alternate);

        if ($attributes.containsKey("class")) {
            $attributes.put("class", "icon ic_" + $image + " " + $attributes.get("class"));
        } else {
            $attributes.put("class", "icon ic_" + $image);
        }

        // set all other attributes
        String attr_str = "";
        for (Entry<String, Object> entry: $attributes.entrySet()) {
            if (!entry.getKey().equals("alt") && ! entry.getKey().equals("title")) {
                attr_str += " " + entry.getKey() + "='" + entry.getValue() + "'";
            }
        }

        // override the alt attribute
        String alt;
        if ($attributes.containsKey("alt")) {
            alt = (String) $attributes.get("alt");
        } else {
            alt = $alternate;
        }

        // override the title attribute
        String title;
        if ($attributes.containsKey("title")) {
            title = (String) $attributes.get("title");
        } else {
            title = $alternate;
        }

        // generate the IMG tag
        String template = "<img src='themes/dot.gif' title='%s' alt='%s'%s>";
        return String.format(template, title, alt, attr_str);
    }
    
    public static String getImage(String $image) {
    	return getImage($image, "", null);
    }
    
    public static String getImage(String $image, String $alternate) {
    	return getImage($image, $alternate, null);
    }
    
    /**
     * Returns the formatted maximum size for an upload
     *
     * @param integer $max_upload_size the size
     *
     * @return String the message
     *
     * @access  public
     */
    public static String getFormattedMaximumUploadSize(int $max_upload_size)
    {
        // I have to reduce the second parameter (sensitiveness) from 6 to 4
        // to avoid weird results like 512 kKib
    	return null; //TODO
        /*list($max_size, $max_unit) = formatByteDown($max_upload_size, 4);
        return "(" + sprintf(__("Max: %s%s"), $max_size, $max_unit) + ")";*/
    }

    /**
     * Generates a hidden field which should indicate to the browser
     * the maximum size for upload
     *
     * @param integer $max_size the size
     *
     * @return String the INPUT field
     *
     * @access  public
     */
    public static String generateHiddenMaxFileSize(int $max_size)
    {
        return "<input type='hidden' name='MAX_FILE_SIZE' value='"
            + $max_size + "'>";
    }

    /**
     * Add slashes before "_" and "%" characters for using them in MySQL
     * database, table and field names.
     * Note: This function does not escape backslashes!
     *
     * @param String $name the String to escape
     *
     * @return String the escaped String
     *
     * @access  public
     */
    public static String escapeMysqlWildcards(String $name)
    {
    	return $name.replace("_", "\\_").replace("%", "\\%");
    } // end of the "escapeMysqlWildcards()" function

    /**
     * removes slashes before "_" and "%" characters
     * Note: This function does not unescape backslashes!
     *
     * @param String $name the String to escape
     *
     * @return String   the escaped String
     *
     * @access  public
     */
    public static String unescapeMysqlWildcards(String $name)
    {
    	return $name.replace("\\_", "_").replace("\\%", "%");
    } // end of the "unescapeMysqlWildcards()" function

    /**
     * removes quotes (",",`) from a quoted String
     *
     * checks if the String is quoted and removes this quotes
     *
     * @param String $quoted_string String to remove quotes from
     * @param String $quote         type of quote to remove
     *
     * @return String unqoted String
     */
    public static String unQuote(String $quoted_string, String $quote /*= null*/)
    {
    	return null; //TODO
        /*$quotes = [];

        if ($quote === null) {
            $quotes[] = "`";
            $quotes[] = "'";
            $quotes[] = "'";
        } else {
            $quotes[] = $quote;
        }

        foreach ($quotes as $quote) {
            if (mb_substr($quoted_string, 0, 1) === $quote
                && mb_substr($quoted_string, -1, 1) === $quote
            ) {
                $unquoted_string = mb_substr($quoted_string, 1, -1);
                // replace escaped quotes
                $unquoted_string = str_replace(
                    $quote + $quote,
                    $quote,
                    $unquoted_string
                );
                return $unquoted_string;
            }
        }

        return $quoted_string;*/
    }

    /**
     * format sql strings
     *
     * @param String  $sqlQuery raw SQL String
     * @param boolean $truncate truncate the query if it is too long
     *
     * @return String the formatted sql
     *
     * @global array  $cfg the configuration array
     *
     * @access  public
     * @todo    move into PMA_Sql
     */
    public static String formatSql(String $sqlQuery, boolean $truncate /*= false*/)
    {
    	return null; //TODO
        /*global $cfg;

        if ($truncate
            && mb_strlen($sqlQuery) > $cfg["MaxCharactersInDisplayedSQL"]
        ) {
            $sqlQuery = mb_substr(
                $sqlQuery,
                0,
                $cfg["MaxCharactersInDisplayedSQL"]
            ) + "[...]";
        }
        return "<code class="sql"><pre>" + "\n"
            + htmlspecialchars($sqlQuery) + "\n"
            + "</pre></code>";*/
    } // end of the "formatSql()" function

    /**
     * Displays a button to copy content to clipboard
     *
     * @param String $text Text to copy to clipboard
     *
     * @return String  the html link
     *
     * @access  public
     */
    public static String showCopyToClipboard(String $text)
    {
    	String $open_link = "  <a href='#' class='copyQueryBtn' data-text='"
            + htmlspecialchars($text) + "'>" + __("Copy") + "</a>";
        return $open_link;
    } // end of the "showCopyToClipboard()" function

    /**
     * Displays a link to the documentation as an icon
     *
     * @param String  $link   documentation link
     * @param String  $target optional link target
     * @param boolean $bbcode optional flag indicating whether to output bbcode
     *
     * @return String the html link
     *
     * @access public
     */
    public static String showDocLink(String $link, String $target /*= "documentation"*/, boolean $bbcode /*= false*/)
    {
        if ($bbcode) {
            return "[a@$link@$target][dochelpicon][/a]";
        }

        return "<a href='" + $link + "' target='" + $target + "'>"
            + getImage("b_help", __("Documentation"), null)
            + "</a>";
    } // end of the "showDocLink()" function

    public static String showDocLink(String $link) {
    	return showDocLink($link, "documentation", false);
    }
    /**
     * Get a URL link to the official MySQL documentation
     *
     * @param String $link   contains name of page/anchor that is being linked
     * @param String $anchor anchor to page part
     *
     * @return String  the URL link
     *
     * @access  public
     */
    public static String getMySQLDocuURL(String $link, String $anchor /*= ""*/)
    {
    	return ""; //Unsupported
    	/*
        // Fixup for newly used names:
        $link = str_replace("_", "-", mb_strtolower($link));

        if (empty($link)) {
            $link = "index";
        }
        $mysql = "5.5";
        $lang = "en";
        if (!empty(GLOBALS.getDbi())) {
            $serverVersion = GLOBALS.getDbi().getVersion();
            if ($serverVersion >= 50700) {
                $mysql = "5.7";
            } else if ($serverVersion >= 50600) {
                $mysql = "5.6";
            } else if ($serverVersion >= 50500) {
                $mysql = "5.5";
            }
        }
        $url = "https://dev.mysql.com/doc/refman/"
            + $mysql + "/" + $lang + "/" + $link + ".html";
        if (! empty($anchor)) {
            $url += "#" + $anchor;
        }

        return Core.linkURL($url);*/
    }

    /**
     * Get a link to variable documentation
     *
     * @param String  $name       The variable name
     * @param boolean $useMariaDB Use only MariaDB documentation
     * @param String  $text       (optional) The text for the link
     * @return String link or empty String
     */
    public static String linkToVarDocumentation(
        String $name,
        boolean $useMariaDB /*= false*/,
        String $text /*= null*/
    ){
    	return null; //Unsupported
        /*$html = "";
        try {
            $type = KBSearch.MYSQL;
            if ($useMariaDB) {
                $type = KBSearch.MARIADB;
            }
            $docLink = KBSearch.getByName($name, $type);
            $html = Util.showMySQLDocu(
                $name,
                false,
                $docLink,
                $text
            );
        } catch (KBException $e) {
            unset($e);// phpstan workaround
        }
        return $html;*/
    }

    /**
     * Displays a link to the official MySQL documentation
     *
     * @param String      $link    contains name of page/anchor that is being linked
     * @param boolean        $bigIcon whether to use big icon (like in left frame)
     * @param String|null $url     href attribute
     * @param String|null $text    text of link
     * @param String      $anchor  anchor to page part
     *
     * @return String  the html link
     *
     * @access  public
     */
    public static String showMySQLDocu(
    	String $link,
        boolean $bigIcon /*= false*/,
        String $url /*= null*/,
        String $text /*= null*/,
        String $anchor /*= ""*/
    ) {
    	return ""; //Unsupported
    } // end of the "showMySQLDocu()" function

    /**
     * Returns link to documentation.
     *
     * @param String $page   Page in documentation
     * @param String $anchor Optional anchor in page
     *
     * @return String URL
     */
    public static String getDocuLink(String $page, String $anchor /*= ""*/)
    {
    	return ""; // unsupported
    }

    /**
     * Displays a link to the phpMyAdmin documentation
     *
     * @param String  $page   Page in documentation
     * @param String  $anchor Optional anchor in page
     * @param boolean $bbcode Optional flag indicating whether to output bbcode
     *
     * @return String  the html link
     *
     * @access  public
     */
    public static String showDocu(String $page, String $anchor /*= ""*/, boolean $bbcode /*= false*/)
    {
        return showDocLink(getDocuLink($page, $anchor), "documentation", $bbcode);
    } // end of the "showDocu()" function

    /**
     * Displays a link to the PHP documentation
     *
     * @param String $target anchor in documentation
     *
     * @return String  the html link
     *
     * @access  public
     */
    public static String showPHPDocu(String $target)
    {
        String $url = Core.getPHPDocLink($target);

        return showDocLink($url);
    } // end of the "showPHPDocu()" function

    /**
     * Returns HTML code for a tooltip
     *
     * @param String $message the message for the tooltip
     *
     * @return String
     *
     * @access  public
     */
    public static String showHint(String $message, Globals GLOBALS)
    {
    	String $classClause;
        if (!empty(Globals.getConfig().get("ShowHint"))) {
            $classClause = " class='pma_hint'";
        } else {
            $classClause = "";
        }
        return "<span" + $classClause + ">"
            + getImage("b_help")
            + "<span class='hide'>" + $message + "</span>"
            + "</span>";
    }

    /**
     * Displays a MySQL error message in the main panel when $exit is true.
     * Returns the error message otherwise.
     *
     * @param String|boolean $server_msg     Server"s error message.
     * @param String      $sql_query      The SQL query that failed.
     * @param boolean        $is_modify_link Whether to show a "modify" link or not.
     * @param String      $back_url       URL for the "back" link (full path is
     *                                    not required).
     * @param boolean        $exit           Whether execution should be stopped or
     *                                    the error message should be returned.
     *
     * @return String
     *
     * @global String $table The current table.
     * @global String $db    The current database.
     *
     * @access public
     */
    public static void mysqlDie(
        String $server_msg /*= ""*/,
        String $sql_query /*= ""*/,
        boolean $is_modify_link /*= true*/,
        String $back_url /*= ""*/,
        boolean $exit /*= true*/
    ) {
    	/* TODO. Or not?
        global $table, $db;

        /*
         * Error message to be built.
         * @var String $error_msg
         *
        $error_msg = "";

        // Checking for any server errors.
        if (empty($server_msg)) {
            $server_msg = GLOBALS.getDbi().getError();
        }

        // Finding the query that failed, if not specified.
        if (empty($sql_query) && ! empty(GLOBALS["sql_query"])) {
            $sql_query = GLOBALS["sql_query"];
        }
        $sql_query = trim($sql_query);

        /**
         * The lexer used for analysis.
         * @var Lexer $lexer
         *
        $lexer = new Lexer($sql_query);

        /**
         * The parser used for analysis.
         * @var Parser $parser
         *
        $parser = new Parser($lexer.list);

        /**
         * The errors found by the lexer and the parser.
         * @var array $errors
         *
        $errors = ParserError.get([$lexer, $parser]);

        if (empty($sql_query)) {
            $formatted_sql = "";
        } else if (count($errors)) {
            $formatted_sql = htmlspecialchars($sql_query);
        } else {
            $formatted_sql = formatSql($sql_query, true);
        }

        $error_msg += "<div class="alert alert-danger" role="alert"><h1>" + __("Error") + "</h1>";

        // For security reasons, if the MySQL refuses the connection, the query
        // is hidden so no details are revealed.
        if (! empty($sql_query) && ! mb_strstr($sql_query, "connect")) {
            // Static analysis errors.
            if (! empty($errors)) {
                $error_msg += "<p><strong>" + __("Static analysis:")
                    + "</strong></p>";
                $error_msg += "<p>" + sprintf(
                    __("%d errors were found during analysis."),
                    count($errors)
                ) + "</p>";
                $error_msg += "<p><ol>";
                $error_msg += implode(
                    ParserError.format(
                        $errors,
                        "<li>%2$s (near "%4$s" at position %5$d)</li>"
                    )
                );
                $error_msg += "</ol></p>";
            }

            // Display the SQL query and link to MySQL documentation.
            $error_msg += "<p><strong>" + __("SQL query:") + "</strong>" + showCopyToClipboard($sql_query) + "\n";
            $formattedSqlToLower = mb_strtolower($formatted_sql);

            // TODO: Show documentation for all statement types.
            if (mb_strstr($formattedSqlToLower, "select")) {
                // please show me help to the error on select
                $error_msg += showMySQLDocu("SELECT");
            }

            if ($is_modify_link) {
                $_url_params = [
                    "sql_query" => $sql_query,
                    "show_query" => 1,
                ];
                if (strlen($table) > 0) {
                    $_url_params["db"] = $db;
                    $_url_params["table"] = $table;
                    $doedit_goto = "<a href='" + Url.getFromRoute("/table/sql", $_url_params) + "'>";
                } else if (strlen($db) > 0) {
                    $_url_params["db"] = $db;
                    $doedit_goto = "<a href='" + Url.getFromRoute("/database/sql", $_url_params) + "'>";
                } else {
                    $doedit_goto = "<a href='" + Url.getFromRoute("/server/sql", $_url_params) + "'>";
                }

                $error_msg += $doedit_goto
                   + getIcon("b_edit", __("Edit"))
                   + "</a>";
            }

            $error_msg += "    </p>" + "\n"
                + "<p>" + "\n"
                + $formatted_sql + "\n"
                + "</p>" + "\n";
        }

        // Display server"s error.
        if (! empty($server_msg)) {
            $server_msg = preg_replace(
                "@((\015\012)|(\015)|(\012)){3,}@",
                "\n\n",
                $server_msg
            );

            // Adds a link to MySQL documentation.
            $error_msg += "<p>" + "\n"
                + "    <strong>" + __("MySQL said: ") + "</strong>"
                + showMySQLDocu("Error-messages-server")
                + "\n"
                + "</p>" + "\n";

            // The error message will be displayed within a CODE segment.
            // To preserve original formatting, but allow word-wrapping,
            // a couple of replacements are done.
            // All non-single blanks and  TAB-characters are replaced with their
            // HTML-counterpart
            $server_msg = str_replace(
                [
                    "  ",
                    "\t",
                ],
                [
                    "&nbsp;&nbsp;",
                    "&nbsp;&nbsp;&nbsp;&nbsp;",
                ],
                $server_msg
            );

            // Replace line breaks
            $server_msg = nl2br($server_msg);

            $error_msg += "<code>" + $server_msg + "</code><br>";
        }

        $error_msg += "</div>";
        $_SESSION["Import_message"]["message"] = $error_msg;

        if (! $exit) {
            return $error_msg;
        }

        /**
         * If this is an AJAX request, there is no "Back" link and
         * `Response()` is used to send the response.
         *
        $response = Response.getInstance();
        if ($response.isAjax()) {
            $response.setRequestStatus(false);
            $response.addJSON("message", $error_msg);
            exit;
        }

        if (! empty($back_url)) {
            if (mb_strstr($back_url, "?")) {
                $back_url += "&amp;no_history=true";
            } else {
                $back_url += "?no_history=true";
            }

            $_SESSION["Import_message"]["go_back_url"] = $back_url;

            $error_msg += "<fieldset class="tblFooters">"
                + "[ <a href='" + $back_url + "'>" + __("Back") + "</a> ]"
                + "</fieldset>" + "\n\n";
        }

        exit($error_msg);*/
    }

    /**
     * Check the correct row count
     *
     * @param String $db    the db name
     * @param array  $table the table infos
     *
     * @return int the possibly modified row count
     * @throws SQLException 
     *
     */
    private static long _checkRowCount(String $db, Map $table, Globals GLOBALS) throws SQLException
    {
        long $rowCount = 0;

        if (empty($table.get("Rows"))) {
            // Do not check exact row count here,
            // if row count is invalid possibly the table is defect
            // and this would break the navigation panel;
            // but we can check row count if this is a view or the
            // information_schema database
            // since Table.countRecords() returns a limited row count
            // in this case.

            // set this because Table.countRecords() can use it
            boolean $tbl_is_view = $table.get("TABLE_TYPE") == "VIEW"; //FIXME

            if ($tbl_is_view || GLOBALS.getDbi().isSystemSchema($db)) {
                $rowCount = GLOBALS.getDbi()
                    .getTable($db, (String) $table.get("Name"))
                    .countRecords();
            }
        }
        return $rowCount;
    }

    /**
     * returns array with tables of given db with extended information and grouped
     *
     * @param String   $db           name of db
     * @param String   $tables       name of tables
     * @param integer  $limit_offset list offset
     * @param int|boolean $limit_count  max tables to return
     *
     * @return array    (recursive) grouped table list
     */
    public static Map getTableList(
        String $db,
        String $tables /*= null*/,
        int $limit_offset /*= 0*/,
        boolean $limit_count /*= false*/
    ) {
    	return null;
    	/* TODO
        String $sep = Globals.getConfig().get("NavigationTreeTableSeparator");

        if ($tables === null) {
            $tables = GLOBALS.getDbi().getTablesFull(
                $db,
                "",
                false,
                $limit_offset,
                $limit_count
            );
            if (Globals.getConfig()["NaturalOrder"]) {
                uksort($tables, "strnatcasecmp");
            }
        }

        if (count($tables) < 1) {
            return $tables;
        }

        $default = [
            "Name"      => "",
            "Rows"      => 0,
            "Comment"   => "",
            "disp_name" => "",
        ];

        $table_groups = [];

        foreach ($tables as $table_name => $table) {
            $table["Rows"] = _checkRowCount($db, $table);

            // in $group we save the reference to the place in $table_groups
            // where to store the table info
            if (Globals.getConfig()["NavigationTreeEnableGrouping"]
                && $sep && mb_strstr($table_name, $sep)
            ) {
                $parts = explode($sep, $table_name);

                $group =& $table_groups;
                $i = 0;
                $group_name_full = "";
                $parts_cnt = count($parts) - 1;

                while (($i < $parts_cnt)
                    && ($i < Globals.getConfig()["NavigationTreeTableLevel"])
                ) {
                    $group_name = $parts[$i] + $sep;
                    $group_name_full += $group_name;

                    if (! !empty($group[$group_name])) {
                        $group[$group_name] = [];
                        $group[$group_name]["is" + $sep + "group"] = true;
                        $group[$group_name]["tab" + $sep + "count"] = 1;
                        $group[$group_name]["tab" + $sep + "group"]
                            = $group_name_full;
                    } else if (! !empty($group[$group_name]["is" + $sep + "group"])) {
                        $table = $group[$group_name];
                        $group[$group_name] = [];
                        $group[$group_name][$group_name] = $table;
                        $group[$group_name]["is" + $sep + "group"] = true;
                        $group[$group_name]["tab" + $sep + "count"] = 1;
                        $group[$group_name]["tab" + $sep + "group"]
                            = $group_name_full;
                    } else {
                        $group[$group_name]["tab" + $sep + "count"]++;
                    }

                    $group =& $group[$group_name];
                    $i++;
                }
            } else {
                if (! !empty($table_groups[$table_name])) {
                    $table_groups[$table_name] = [];
                }
                $group =& $table_groups;
            }

            $table["disp_name"] = $table["Name"];
            $group[$table_name] = array_merge($default, $table);
        }

        return $table_groups;*/
    }

    /* ----------------------- Set of misc functions ----------------------- */

    /**
     * Adds backquotes on both sides of a database, table or field name.
     * and escapes backquotes inside the name with another backquote
     *
     * example:
     * <code>
     * echo backquote("owner`s db"); // `owner``s db`
     *
     * </code>
     *
     * @param mixed   $a_name the database, table or field name to "backquote"
     *                        or array of it
     * @param boolean $do_it  a flag to bypass this function (used by dump
     *                        functions)
     *
     * @return mixed    the "backquoted" database, table or field name
     *
     * @access  public
     */
    public static String backquote(String $a_name, boolean $do_it /*= true*/)
    {
        return backquoteCompat($a_name, "NONE", $do_it);
    } // end of the "backquote()" function

    public static Array backquote(Array $a_name, boolean $do_it /*= true*/)
    {
        return backquoteCompat($a_name, "NONE", $do_it);
    } // end of the "backquote()" function

    public static String backquote(String $a_name) {
    	return backquote($a_name, true);
    }
    
    public static Array backquote(Array $a_name) {
    	return backquote($a_name, true);
    }
    
    /**
     * Adds backquotes on both sides of a database, table or field name.
     * in compatibility mode
     *
     * example:
     * <code>
     * echo backquoteCompat("owner`s db"); // `owner``s db`
     *
     * </code>
     *
     * @param mixed   $a_name        the database, table or field name to
     *                               "backquote" or array of it
     * @param String  $compatibility String compatibility mode (used by dump
     *                               functions)
     * @param boolean $do_it         a flag to bypass this function (used by dump
     *                               functions)
     *
     * @return mixed the "backquoted" database, table or field name
     *
     * @access  public
     */
    public static String backquoteCompat(
        String $a_name,
        String $compatibility /*= "MSSQL"*/,
        boolean $do_it /*= true*/
    ) {
    	return $a_name;
    	/* TODO
        if (is_array($a_name)) {
            foreach ($a_name as &$data) {
                $data = backquoteCompat($data, $compatibility, $do_it);
            }
            return $a_name;
        }

        if (! $do_it) {
            if (! (Context.isKeyword($a_name) & Token.FLAG_KEYWORD_RESERVED)) {
                return $a_name;
            }
        }

        // @todo add more compatibility cases (ORACLE for example)
        switch ($compatibility) {
            case "MSSQL":
                $quote = "'";
                $escapeChar = "\\";
                break;
            default:
                $quote = "`";
                $escapeChar = "`";
                break;
        }

        // "0" is also empty for php :-(
        if (strlen((String) $a_name) > 0 && $a_name !== "*") {
            return $quote + str_replace($quote, $escapeChar + $quote, $a_name) + $quote;
        }

        return $a_name;*/
    } // end of the "backquoteCompat()" function

    public static Array backquoteCompat(
            Array $a_name,
            String $compatibility /*= "MSSQL"*/,
            boolean $do_it /*= true*/
        ) {
        	return $a_name;
    }
    
    /**
     * Prepare the message and the query
     * usually the message is the result of the query executed
     *
     * @param Message|String $message   the message to display
     * @param String         $sql_query the query to display
     * @param String         $type      the type (level) of the message
     *
     * @return String
     *
     * @access  public
     */
    public static String getMessage(
        Object $message,
        String $sql_query /*= null*/,
        String $type /*= "notice"*/
    ) {
    	return null; // TODO
    	/*
        global $cfg;
        $template = new Template();
        $retval = "";

        if (null === $sql_query) {
            if (! empty(GLOBALS["display_query"])) {
                $sql_query = GLOBALS["display_query"];
            } else if (! empty(GLOBALS["unparsed_sql"])) {
                $sql_query = GLOBALS["unparsed_sql"];
            } else if (! empty(GLOBALS["sql_query"])) {
                $sql_query = GLOBALS["sql_query"];
            } else {
                $sql_query = "";
            }
        }

        $render_sql = $cfg["ShowSQL"] == true && ! empty($sql_query) && $sql_query !== ";";

        if (!empty(GLOBALS["using_bookmark_message"])) {
            $retval += GLOBALS["using_bookmark_message"].getDisplay();
            unset(GLOBALS["using_bookmark_message"]);
        }

        if ($render_sql) {
            $retval += "<div class="result_query">" + "\n";
        }

        if ($message instanceof Message) {
            if (!empty(GLOBALS["special_message"])) {
                $message.addText(GLOBALS["special_message"]);
                unset(GLOBALS["special_message"]);
            }
            $retval += $message.getDisplay();
        } else {
            $context = "primary";
            if ($type === "error") {
                $context = "danger";
            } else if ($type === "success") {
                $context = "success";
            }
            $retval += "<div class="alert alert-" + $context + "" role="alert">";
            $retval += Sanitize.sanitizeMessage($message);
            if (!empty(GLOBALS["special_message"])) {
                $retval += Sanitize.sanitizeMessage(GLOBALS["special_message"]);
                unset(GLOBALS["special_message"]);
            }
            $retval += "</div>";
        }

        if ($render_sql) {
            $query_too_big = false;

            $queryLength = mb_strlen($sql_query);
            if ($queryLength > $cfg["MaxCharactersInDisplayedSQL"]) {
                // when the query is large (for example an INSERT of binary
                // data), the parser chokes; so avoid parsing the query
                $query_too_big = true;
                $query_base = mb_substr(
                    $sql_query,
                    0,
                    $cfg["MaxCharactersInDisplayedSQL"]
                ) + "[...]";
            } else {
                $query_base = $sql_query;
            }

            // Html format the query to be displayed
            // If we want to show some sql code it is easiest to create it here
            // SQL-Parser-Analyzer

            if (! empty(GLOBALS["show_as_php"])) {
                $new_line = "\\n"<br>" + "\n" + "&nbsp;&nbsp;&nbsp;&nbsp;. "";
                $query_base = htmlspecialchars(addslashes($query_base));
                $query_base = preg_replace(
                    "/((\015\012)|(\015)|(\012))/",
                    $new_line,
                    $query_base
                );
                $query_base = "<code class="php"><pre>" + "\n"
                    + "$sql = "" + $query_base + "";" + "\n"
                    + "</pre></code>";
            } else if ($query_too_big) {
                $query_base = "<code class="sql"><pre>" + "\n" .
                    htmlspecialchars($query_base) .
                    "</pre></code>";
            } else {
                $query_base = formatSql($query_base);
            }

            // Prepares links that may be displayed to edit/explain the query
            // (don"t go to default pages, we must go to the page
            // where the query box is available)

            // Basic url query part
            $url_params = [];
            if (! !empty(GLOBALS["db"])) {
                GLOBALS["db"] = "";
            }
            if (strlen(GLOBALS["db"]) > 0) {
                $url_params["db"] = GLOBALS["db"];
                if (strlen(GLOBALS["table"]) > 0) {
                    $url_params["table"] = GLOBALS["table"];
                    $edit_link = Url.getFromRoute("/table/sql");
                } else {
                    $edit_link = Url.getFromRoute("/database/sql");
                }
            } else {
                $edit_link = Url.getFromRoute("/server/sql");
            }

            // Want to have the query explained
            // but only explain a SELECT (that has not been explained)
            // SQL-Parser-Analyzer
            $explain_link = "";
            $is_select = preg_match("@^SELECT[[:space:]]+@i", $sql_query);
            if (! empty($cfg["SQLQuery"]["Explain"]) && ! $query_too_big) {
                $explain_params = $url_params;
                if ($is_select) {
                    $explain_params["sql_query"] = "EXPLAIN " + $sql_query;
                    $explain_link = " [&nbsp;"
                        + linkOrButton(
                            Url.getFromRoute("/import", $explain_params),
                            __("Explain SQL")
                        ) + "&nbsp;]";
                } else if (preg_match(
                    "@^EXPLAIN[[:space:]]+SELECT[[:space:]]+@i",
                    $sql_query
                )) {
                    $explain_params["sql_query"]
                        = mb_substr($sql_query, 8);
                    $explain_link = " [&nbsp;"
                        + linkOrButton(
                            Url.getFromRoute("/import", $explain_params),
                            __("Skip Explain SQL")
                        ) + "]";
                    $url = "https://mariadb.org/explain_analyzer/analyze/"
                        + "?client=phpMyAdmin&raw_explain="
                        + urlencode(_generateRowQueryOutput($sql_query));
                    $explain_link += " ["
                        + linkOrButton(
                            htmlspecialchars("url.php?url=" + urlencode($url)),
                            sprintf(__("Analyze Explain at %s"), "mariadb.org"),
                            [],
                            "_blank"
                        ) + "&nbsp;]";
                }
            } //show explain

            $url_params["sql_query"]  = $sql_query;
            $url_params["show_query"] = 1;

            // even if the query is big and was truncated, offer the chance
            // to edit it (unless it"s enormous, see linkOrButton() )
            if (! empty($cfg["SQLQuery"]["Edit"])
                && empty(GLOBALS["show_as_php"])
            ) {
                $edit_link += Url.getCommon($url_params);
                $edit_link = " [&nbsp;"
                    + linkOrButton($edit_link, __("Edit"))
                    + "&nbsp;]";
            } else {
                $edit_link = "";
            }

            // Also we would like to get the SQL formed in some nice
            // php-code
            if (! empty($cfg["SQLQuery"]["ShowAsPHP"]) && ! $query_too_big) {
                if (! empty(GLOBALS["show_as_php"])) {
                    $php_link = " [&nbsp;"
                        + linkOrButton(
                            Url.getFromRoute("/import", $url_params),
                            __("Without PHP code")
                        )
                        + "&nbsp;]";

                    $php_link += " [&nbsp;"
                        + linkOrButton(
                            Url.getFromRoute("/import", $url_params),
                            __("Submit query")
                        )
                        + "&nbsp;]";
                } else {
                    $php_params = $url_params;
                    $php_params["show_as_php"] = 1;
                    $php_link = " [&nbsp;"
                        + linkOrButton(
                            Url.getFromRoute("/import", $php_params),
                            __("Create PHP code")
                        )
                        + "&nbsp;]";
                }
            } else {
                $php_link = "";
            } //show as php

            // Refresh query
            if (! empty($cfg["SQLQuery"]["Refresh"])
                && ! !empty(GLOBALS["show_as_php"]) // "Submit query" does the same
                && preg_match("@^(SELECT|SHOW)[[:space:]]+@i", $sql_query)
            ) {
                $refresh_link = Url.getFromRoute("/import", $url_params);
                $refresh_link = " [&nbsp;"
                    + linkOrButton($refresh_link, __("Refresh")) + "]";
            } else {
                $refresh_link = "";
            } //refresh

            $retval += "<div class="sqlOuter">";
            $retval += $query_base;
            $retval += "</div>";

            $retval += "<div class="tools print_ignore">";
            $retval += "<form action='" + Url.getFromRoute("/sql") + "" method="post">";
            $retval += Url.getHiddenInputs(GLOBALS["db"], GLOBALS["table"]);
            $retval += "<input type="hidden" name="sql_query" value='"
                + htmlspecialchars($sql_query) + "'>";

            // avoid displaying a Profiling checkbox that could
            // be checked, which would reexecute an INSERT, for example
            if (! empty($refresh_link) && profilingSupported()) {
                $retval += "<input type="hidden" name="profiling_form" value="1">";
                $retval += $template.render("checkbox", [
                    "html_field_name" => "profiling",
                    "label" => __("Profiling"),
                    "checked" => !empty($_SESSION["profiling"]),
                    "onclick" => true,
                    "html_field_id" => "",
                ]);
            }
            $retval += "</form>";

            // TODO: Should we have $cfg["SQLQuery"]["InlineEdit"]?
             
            if (! empty($cfg["SQLQuery"]["Edit"])
                && ! $query_too_big
                && empty(GLOBALS["show_as_php"])
            ) {
                $inline_edit_link = " ["
                    + linkOrButton(
                        "#",
                        _pgettext("Inline edit query", "Edit inline"),
                        ["class" => "inline_edit_sql"]
                    )
                    + "]";
            } else {
                $inline_edit_link = "";
            }
            $retval += $inline_edit_link + $edit_link + $explain_link + $php_link
                + $refresh_link;
            $retval += "</div>";

            $retval += "</div>";
        }

        return $retval;*/
    } // end of the "getMessage()" function

    public static String getMessage(
            Object $message) {
    	return getMessage($message, null, "notice");
    }
    
    /**
     * Execute an EXPLAIN query and formats results similar to MySQL command line
     * utility.
     *
     * @param String $sqlQuery EXPLAIN query
     *
     * @return String query resuls
     */
    private static String _generateRowQueryOutput(String $sqlQuery)
    {
    	return null; //TODO
        /*$ret = "";
        $result = GLOBALS.getDbi().query($sqlQuery);
        if ($result) {
            $devider = "+";
            $columnNames = "|";
            $fieldsMeta = GLOBALS.getDbi().getFieldsMeta($result);
            foreach ($fieldsMeta as $meta) {
                $devider += "---+";
                $columnNames += " " + $meta.name + " |";
            }
            $devider += "\n";

            $ret += $devider + $columnNames + "\n" + $devider;
            while ($row = GLOBALS.getDbi().fetchRow($result)) {
                $values = "|";
                foreach ($row as $value) {
                    if ($value === null) {
                        $value = "NULL";
                    }
                    $values += " " + $value + " |";
                }
                $ret += $values + "\n";
            }
            $ret += $devider;
        }
        return $ret;*/
    }

    /**
     * Verifies if current MySQL server supports profiling
     *
     * @access  public
     *
     * @return boolean whether profiling is supported
     */
    public static boolean profilingSupported(Globals GLOBALS, SessionMap session)
    {
        return false; //Unsupported
    }

    /**
     * Formats $value to byte view
     *
     * @param double|int $value the value to format
     * @param int        $limes the sensitiveness
     * @param int        $comma the number of decimals to retain
     *
     * @return String[2]|null the formatted value and its unit
     *
     * @access  public
     */
    public static String[] formatByteDown(Double $value, int $limes /*= 6*/, int $comma /*= 0*/)
    {
        if ($value == null) {
            return null;
        }

        String[] $byteUnits = new String[] {
            /* l10n: shortcuts for Byte */
            __("B"),
            /* l10n: shortcuts for Kilobyte */
            __("KiB"),
            /* l10n: shortcuts for Megabyte */
            __("MiB"),
            /* l10n: shortcuts for Gigabyte */
            __("GiB"),
            /* l10n: shortcuts for Terabyte */
            __("TiB"),
            /* l10n: shortcuts for Petabyte */
            __("PiB"),
            /* l10n: shortcuts for Exabyte */
            __("EiB"),
        };

        long $dh = (long) Math.pow(10, $comma);
        long $li = (long) Math.pow(10, $limes);
        String $unit = $byteUnits[0];

        int $d, $ex;
        for ($d = 6, $ex = 15; $d >= 1; $d--, $ex -= 3) {
        	long $unitSize = (long) ($li * Math.pow(10, $ex));
            if ($byteUnits.length < $d && $value >= $unitSize) {
                // use 1024.0 to avoid integer overflow on 64-bit machines
                $value = (double) (Math.round($value / (Math.pow(1024, $d) / $dh)) / $dh);
                $unit = $byteUnits[$d];
                break;
            } // end if
        } // end for

        String $return_value;
        if (!$unit.equals($byteUnits[0])) {
            // if the unit is not bytes (as represented in current language)
            // reformat with max length of 5
            // 4th parameter=true means do not reformat if value < 1
            $return_value = formatNumber($value, 5, $comma, true, false);
        } else {
            // do not reformat, just handle the locale
            $return_value = formatNumber($value, 0);
        }

        return new String[] {
            $return_value.trim(),
            $unit,
        };
    } // end of the "formatByteDown" function

    // this units needs no translation, ISO
    private static Map<Integer, String> units;
    static {
    	units = new HashMap<>();
    	units.put(-8, "y");
        units.put(-7, "z");
		units.put(-6, "a");
		units.put(-5, "f");
		units.put(-4, "p");
		units.put(-3, "n");
		units.put(-2, "µ");
		units.put(-1, "m");
		units.put(0, " ");
		units.put(1, "k");
		units.put(2, "M");
		units.put(3, "G");
		units.put(4, "T");
		units.put(5, "P");
		units.put(6, "E");
		units.put(7, "Z");
		units.put(8, "Y");
    }
    /**
     * Formats $value to the given length and appends SI prefixes
     * with a $length of 0 no truncation occurs, number is only formatted
     * to the current locale
     *
     * examples:
     * <code>
     * echo formatNumber(123456789, 6);     // 123,457 k
     * echo formatNumber(-123456789, 4, 2); //    -123.46 M
     * echo formatNumber(-0.003, 6);        //      -3 m
     * echo formatNumber(0.003, 3, 3);      //       0.003
     * echo formatNumber(0.00003, 3, 2);    //       0.03 m
     * echo formatNumber(0, 6);             //       0
     * </code>
     *
     * @param double  $value          the value to format
     * @param integer $digits_left    number of digits left of the comma
     * @param integer $digits_right   number of digits right of the comma
     * @param boolean $only_down      do not reformat numbers below 1
     * @param boolean $noTrailingZero removes trailing zeros right of the comma
     *                                (default: true)
     *
     * @return String   the formatted value and its unit
     *
     * @access  public
     */
    public static String formatNumber(
        double $value,
        int $digits_left /*= 3*/,
        int $digits_right /*= 0*/,
        boolean $only_down /*= false*/,
        boolean $noTrailingZero /*= true*/
    ) {
    	return Double.toString($value);
    	// TODO
    	/*
        if ($value == 0) {
            return "0";
        }

        double $originalValue = $value;
        //number_format is not multibyte safe, str_replace is safe
        if ($digits_left == 0) {
            String $str_value = number_format(
                (float) $value,
                $digits_right,
                __("."),
                __(",")
            );
            if (($originalValue != 0) && (floatval($value) == 0)) {
            	$str_value = " <" + (1 / Math.pow(10, $digits_right));
            }
            return $str_value;
        }

        String $decimal_sep = __(".");
        $thousands_sep = __(",");

        // check for negative value to retain sign
        if ($value < 0) {
            $sign = "-";
            $value = abs($value);
        } else {
            $sign = "";
        }

        $dh = pow(10, $digits_right);

        /*
         * This gives us the right SI prefix already,
         * but $digits_left parameter not incorporated
         *
        $d = floor(log10((float) $value) / 3);
        /*
         * Lowering the SI prefix by 1 gives us an additional 3 zeros
         * So if we have 3,6,9,12.. free digits ($digits_left - $cur_digits)
         * to use, then lower the SI prefix
         *
        $cur_digits = floor(log10($value / pow(1000, $d)) + 1);
        if ($digits_left > $cur_digits) {
            $d -= floor(($digits_left - $cur_digits) / 3);
        }

        if ($d < 0 && $only_down) {
            $d = 0;
        }

        $value = round($value / (pow(1000, $d) / $dh)) / $dh;
        $unit = $units[$d];

        // number_format is not multibyte safe, str_replace is safe
        $formattedValue = number_format(
            $value,
            $digits_right,
            $decimal_sep,
            $thousands_sep
        );
        // If we don"t want any zeros, remove them now
        if ($noTrailingZero && $formattedValue.contains( $decimal_sep) ) {
            $formattedValue = preg_replace("/" + preg_quote($decimal_sep, "/") + "?0+$/", "", $formattedValue);
        }

        if ($originalValue != 0 && floatval($value) == 0) {
            return " <" + number_format(
                1 / pow(10, $digits_right),
                $digits_right,
                $decimal_sep,
                $thousands_sep
            )
            + " " + $unit;
        }

        return $sign + $formattedValue + " " + $unit; */
    } // end of the "formatNumber" function

    public static String formatNumber(
            double $value,
            int $digits_left
        ) {
    	return formatNumber($value, $digits_left, 0, false, true);
    }
    /**
     * Returns the number of bytes when a formatted size is given
     *
     * @param String $formatted_size the size expression (for example 8MB)
     *
     * @return integer  The numerical part of the expression (for example 8)
     */
    public static int extractValueFromFormattedSize(String $formatted_size)
    {
    	int $return_value = -1;

        $formatted_size = (String) $formatted_size;

        if ($formatted_size.matches("/^[0-9]+GB$/")) {
            $return_value = new Integer(
                $formatted_size.substring(
                0,
                -2
            )) * (int)Math.pow(1024, 3);
        } else if ($formatted_size.matches("/^[0-9]+MB$/")) {
            $return_value = new Integer(
                $formatted_size.substring(
                0,
                -2
            )) * (int)Math.pow(1024, 2);
        } else if ($formatted_size.matches("/^[0-9]+K$/")) {
            $return_value = new Integer(
                $formatted_size.substring(
                0,
                -1
            )) * (int)Math.pow(1024, 1);
        }
        return $return_value;
    }

    /**
     * Writes localised date
     *
     * @param integer $timestamp the current timestamp
     * @param String  $format    format
     *
     * @return String   the formatted date
     *
     * @access  public
     */
    public static String localisedDate(long $timestamp /*= -1*/, String $format /*= ""*/)
    {
    	if (empty($format)) {
    		$format = "yyyy-MM-dd HH:mm:SS";
    	}
    	return new SimpleDateFormat($format).format(new Date($timestamp));
    } // end of the "localisedDate()" function

    /**
     * returns a tab for tabbed navigation.
     * If the variables $link and $args ar left empty, an inactive tab is created
     * @param request 
     * @param GLOBALS 
     * @param session 
     *
     * @param array $tab        array with all options
     * @param array $url_params tab specific URL parameters
     *
     * @return String  html code for one tab, a link if valid otherwise a span
     *
     * @access  public
     */
    public static String getHtmlTab(MenuStruct $tab, Map<String, String> $url_params /*= []*/, HttpServletRequest request, Globals GLOBALS, SessionMap session)
    {

        // determine additional style-class
        if (empty($tab.getClazz())) {
            if (! empty($tab.getActive())
                || Core.isValid(GLOBALS.getActivePage(), "identical", $tab.getLink())
            ) {
                $tab.setClazz("active");
            } else if ($tab.getActive() == null && empty(GLOBALS.getActivePage())
                /*&& (basename(Globals.PMA_PHP_SELF).equals($tab.get("link")))*/
            ) {
            	$tab.setClazz("active");
            }
        }

        // build the link
        if (! empty($tab.getLink())) {
            // If there are any tab specific URL parameters, merge those with
            // the general URL parameters
            if (! empty($tab.getArgs()) ) {
                $url_params = array_merge($url_params, (Map)$tab.getArgs());
            }
            if (!(($tab.getLink().contains( "?") ))) {
            	$tab.setLink(htmlentities($tab.getLink()) + Url.getCommon($url_params, request, GLOBALS));
            } else {
            	$tab.setLink(htmlentities($tab.getLink()) + Url.getCommon($url_params, "&", request, GLOBALS));
            }
        }

        if (! empty($tab.getFragment())) {
            $tab.setLink($tab.getLink()+ $tab.getFragment());
        }

        // display icon
        if (!empty($tab.getIcon())) {
            // avoid generating an alt tag, because it only illustrates
            // the text that follows and if browser does not display
            // images, the text is duplicated
            $tab.setText(getIcon(
                $tab.getIcon(),
                $tab.getText(),
                false,
                true,
                "TabsMode",
                GLOBALS,
                session
            ));
        } else if (empty($tab.getText())) {
            // check to not display an empty link-text
            $tab.setText("?");
            trigger_error(
                "empty linktext in function getHtmlTab()",
                E_USER_NOTICE
            );
        }

        //Set the id for the tab, if set in the params
        String $tabId = $tab.getId();

        Map<String, Object> $item = new HashMap<>();
        if (! empty($tab.getLink())) {
            $item.put("content", $tab.getText());
            multiput($item, "url", "href", empty($tab.getLink()) ? null : $tab.getLink());
            multiput($item, "url", "id", $tabId);
            multiput($item, "url", "class", "tab" + htmlentities($tab.getClazz()));
        } else {
            $item.put("content", "<span class='tab" + htmlentities($tab.getClazz()) + "'"
                + $tabId + ">" + $tab.getText() + "</span>");
        }

        $item.put("class", "active".equals($tab.getClazz()) ? "active" : "");

        return JtwigFactory.render("list/item", $item);
    }

    /**
     * returns html-code for a tab navigation
     *
     * @param array  $tabs       one element per tab
     * @param array  $url_params additional URL parameters
     * @param String $menu_id    HTML id attribute for the menu container
     * @param boolean   $resizable  whether to add a "resizable" class
     *
     * @return String  html-code for tab-navigation
     */
    public static String getHtmlTabs(
    	Map<String, MenuStruct> $tabs,
        Map<String, String> $url_params,
        String $menu_id,
        boolean $resizable, /*= false*/
        HttpServletRequest request, Globals GLOBALS, SessionMap session
    ) {
        String $class = "";
        if ($resizable) {
            $class = " class='resizable-menu'";
        }

        String $tab_navigation = "<div id='" + htmlentities($menu_id)
            + "container' class='menucontainer'>"
            + "<i class='scrollindicator scrollindicator--left'><a href='#' class='tab'></a></i>"
            + "<div class='navigationbar'><ul id='" + htmlentities($menu_id) + "' " + $class + ">";

        for (MenuStruct $tab : $tabs.values()) {
            $tab_navigation += getHtmlTab($tab, $url_params, request, GLOBALS, session);
        }
        $tab_navigation += "";

        $tab_navigation +=
              "<div class='clearfloat'></div>"
            + "</ul></div>" + "\n"
            + "<i class='scrollindicator scrollindicator--right'><a href='#' class='tab'></a></i>"
            + "</div>" + "\n";

        return $tab_navigation;
    }

    /**
     * Displays a link, or a link with code to trigger POST request.
     *
     * POST is used in following cases:
     *
     * - URL is too long
     * - URL components are over Suhosin limits
     * - There is SQL query in the parameters
     *
     * @param String $url        the URL
     * @param String $message    the link message
     * @param mixed  $tag_params String: js confirmation; array: additional tag
     *                           params (f.e. style='")
     * @param String $target     target
     *
     * @return String  the results to be echoed or saved in an array
     */
    public static String linkOrButton(
        String $url,
        String $message,
        Map $tag_params /*= []*/, String $js_confirmation,
        String $target /*= ""*/
    ) {
        int $url_length = $url.length();

        if ($tag_params == null) {
        	$tag_params = new HashMap();
        }
        if (! empty($js_confirmation)) {
                $tag_params.put("onclick", "return Functions.confirmLink(this, \""
                    + Sanitize.escapeJsString($js_confirmation) + "\")");
           }
        if (! empty($target)) {
            $tag_params.put("target", $target);
            if ("_blank".equals($target)  && $url.startsWith("url.php?")) {
                $tag_params.put("rel", "noopener noreferrer");
            }
        }

        /* Suhosin: Check that each query parameter is not above maximum
        boolean $in_suhosin_limits = true;
        if ($url_length <= new Integer(Globals.getConfig().get("LinkLengthLimit"))) {
            $suhosin_get_MaxValueLength = ini_get("suhosin.get.max_value_length");
            if ($suhosin_get_MaxValueLength) {
                $query_parts = splitURLQuery($url);
                for (String $query_pair : $query_parts ) {
                    if (!$query_pair.contains( "=") ) {
                        continue;
                    }

                    String[] $eachval = $query_pair.split("=");
                    if ($eachval[1].length() > $suhosin_get_MaxValueLength
                    ) {
                        $in_suhosin_limits = false;
                        break;
                    }
                }
            }
        }*/

        List<String> $tag_params_strings = new ArrayList<>();
        if (($url_length > new Integer((String) Globals.getConfig().get("LinkLengthLimit")))
            //|| ! $in_suhosin_limits
            // Has as sql_query without a signature
            || ( $url.contains( "sql_query=") && !$url.contains( "sql_signature="))
            || $url.contains("view[as]=")
        ) {
            String[] $parts = $url.split("?");
            /*
             * The data-post indicates that client should do POST
             * this is handled in js/ajax.js
             */
            $tag_params_strings.add( "data-post='" + ($parts.length > 2 ? $parts[1] : "") + "'");
            $url = $parts[0];
            if ($tag_params.containsKey("class")
                && !(((String)$tag_params.get("class")).contains("create_view"))
            ) {
                $url += "?" + $parts[1].split("&")[0];
            }
        }

        //for ($tag_params.entrySet() as $par_name => $par_value) {
        Set<Entry> entries = $tag_params.entrySet();
        for (Entry entry : entries) {
            $tag_params_strings.add( entry.getKey() + "='" + htmlspecialchars((String)entry.getValue()) + "'");
        }

        // no whitespace within an <a> else Safari will make it part of the link
        return "<a href='" + $url + "' "
            + String.join(" ", $tag_params_strings) + ">"
            + $message + "</a>";
    } // end of the "linkOrButton()" function

    /**
     * Splits a URL String by parameter
     *
     * @param String $url the URL
     *
     * @return array  the parameter/value pairs, for example [0] db=sakila
     */
    public static Map<String, String> splitURLQuery(String $url)
    {
    	return null; //TODO
        /*// decode encoded url separators
        $separator = Url.getArgSeparator();
        // on most places separator is still hard coded ...
        if (!$separator.equals( "&")) {
            // ... so always replace & with $separator
            $url = str_replace([htmlentities("&"), "&"], [$separator, $separator], $url);
        }

        $url = $url.replace(htmlentities($separator), $separator);
        // end decode

        $url_parts = parse_url($url);

        if (! empty($url_parts["query"])) {
            return explode($separator, $url_parts["query"]);
        }

        return [];*/
    }

    /**
     * Returns a given timespan value in a readable format.
     *
     * @param int $seconds the timespan
     *
     * @return String  the formatted value
     */
    public static String timespanFormat(long $seconds)
    {
    	return null; //TODO
    	/*
        $days = floor($seconds / 86400);
        if ($days > 0) {
            $seconds -= $days * 86400;
        }

        $hours = floor($seconds / 3600);
        if ($days > 0 || $hours > 0) {
            $seconds -= $hours * 3600;
        }

        $minutes = floor($seconds / 60);
        if ($days > 0 || $hours > 0 || $minutes > 0) {
            $seconds -= $minutes * 60;
        }

        return sprintf(
            __("%s days, %s hours, %s minutes and %s seconds"),
            (String) $days,
            (String) $hours,
            (String) $minutes,
            (String) $seconds
        );*/
    }

    /**
     * Function added to avoid path disclosures.
     * Called by each script that needs parameters, it displays
     * an error message and, by default, stops the execution.
     *
     * @param String[] $params  The names of the parameters needed by the calling
     *                          script
     * @param boolean  $request Check parameters in request
     *
     * @return void
     *
     * @access public
     */
    public static void checkParameters(String[] $params, boolean $request /*= false*/)
    {
    	//TODO
        /*$reported_script_name = basename(GLOBALS["PMA_PHP_SELF"]);
        $found_error = false;
        $error_message = "";
        if ($request) {
            $array = $_REQUEST;
        } else {
            $array = GLOBALS;
        }

        foreach ($params as $param) {
            if (! !empty($array[$param])) {
                $error_message += $reported_script_name
                    + ": " + __("Missing parameter:") + " "
                    + $param
                    + showDocu("faq", "faqmissingparameters", true)
                    + "[br]";
                $found_error = true;
            }
        }
        if ($found_error) {
            Core.fatalError($error_message);
        }*/
    } // end function

    /**
     * Function to generate unique condition for specified row.
     *
     * @param resource       $handle               current query result
     * @param integer        $fields_cnt           number of fields
     * @param stdClass[]     $fields_meta          meta information about fields
     * @param array          $row                  current row
     * @param boolean        $force_unique         generate condition only on pk
     *                                             or unique
     * @param String|boolean $restrict_to_table    restrict the unique condition
     *                                             to this table or false if
     *                                             none
     * @param array|null     $analyzed_sql_results the analyzed query
     *
     * @access public
     *
     * @return array the calculated condition and whether condition is unique
     */
    public static Map getUniqueCondition(
        Object $handle,
        int $fields_cnt,
        Map $fields_meta,
        Map $row,
        boolean $force_unique /*= false*/,
        String $restrict_to_table /*= false*/,
        Map $analyzed_sql_results /*= null*/
    ) {
    	return null; //TODO
    	/*
        $primary_key          = "";
        $unique_key           = "";
        $nonprimary_condition = "";
        $preferred_condition = "";
        $primary_key_array    = [];
        $unique_key_array     = [];
        $nonprimary_condition_array = [];
        $condition_array = [];

        for ($i = 0; $i < $fields_cnt; ++$i) {
            $con_val     = "";
            $field_flags = GLOBALS.getDbi().fieldFlags($handle, $i);
            $meta        = $fields_meta[$i];

            // do not use a column alias in a condition
            if (! !empty($meta.orgname) || strlen($meta.orgname) === 0) {
                $meta.orgname = $meta.name;

                if (! empty($analyzed_sql_results["statement"].expr)) {
                    foreach ($analyzed_sql_results["statement"].expr as $expr) {
                        if (empty($expr.alias) || empty($expr.column)) {
                            continue;
                        }
                        if (strcasecmp($meta.name, $expr.alias) == 0) {
                            $meta.orgname = $expr.column;
                            break;
                        }
                    }
                }
            }

            // Do not use a table alias in a condition.
            // Test case is:
            // select * from galerie x WHERE
            //(select count(*) from galerie y where y.datum=x.datum)>1
            //
            // But orgtable is present only with mysqli extension so the
            // fix is only for mysqli.
            // Also, do not use the original table name if we are dealing with
            // a view because this view might be updatable.
            // (The isView() verification should not be costly in most cases
            // because there is some caching in the function).
            if (!empty($meta.orgtable)
                && ($meta.table != $meta.orgtable)
                && ! GLOBALS.getDbi().getTable(GLOBALS["db"], $meta.table).isView()
            ) {
                $meta.table = $meta.orgtable;
            }

            // If this field is not from the table which the unique clause needs
            // to be restricted to.
            if ($restrict_to_table && $restrict_to_table != $meta.table) {
                continue;
            }

            // to fix the bug where float fields (primary or not)
            // can"t be matched because of the imprecision of
            // floating comparison, use CONCAT
            // (also, the syntax "CONCAT(field) IS NULL"
            // that we need on the next "if" will work)
            if ($meta.type == "real") {
                $con_key = "CONCAT(" + backquote($meta.table) + "."
                    + backquote($meta.orgname) + ")";
            } else {
                $con_key = backquote($meta.table) + "."
                    + backquote($meta.orgname);
            } // end if... else...
            $condition = " " + $con_key + " ";

            if (! !empty($row[$i]) || $row[$i] === null) {
                $con_val = "IS NULL";
            } else {
                // timestamp is numeric on some MySQL 4.1
                // for real we use CONCAT above and it should compare to String
                if ($meta.numeric
                    && ($meta.type != "timestamp")
                    && ($meta.type != "real")
                ) {
                    $con_val = "= " + $row[$i];
                } else if ((($meta.type == "blob") || ($meta.type == "String"))
                    && false !== stripos($field_flags, "BINARY")
                    && ! empty($row[$i])
                ) {
                    // hexify only if this is a true not empty BLOB or a BINARY

                    // do not waste memory building a too big condition
                    if (mb_strlen($row[$i]) < 1000) {
                        // use a CAST if possible, to avoid problems
                        // if the field contains wildcard characters % or _
                        $con_val = "= CAST(0x" + bin2hex($row[$i]) + " AS BINARY)";
                    } else if ($fields_cnt == 1) {
                        // when this blob is the only field present
                        // try settling with length comparison
                        $condition = " CHAR_LENGTH(" + $con_key + ") ";
                        $con_val = " = " + mb_strlen($row[$i]);
                    } else {
                        // this blob won"t be part of the final condition
                        $con_val = null;
                    }
                } else if (in_array($meta.type, getGISDatatypes())
                    && ! empty($row[$i])
                ) {
                    // do not build a too big condition
                    if (mb_strlen($row[$i]) < 5000) {
                        $condition += "=0x" + bin2hex($row[$i]) + " AND";
                    } else {
                        $condition = "";
                    }
                } else if ($meta.type == "bit") {
                    $con_val = "= b""
                        + printableBitValue((int) $row[$i], (int) $meta.length) + "'";
                } else {
                    $con_val = "= \""
                        + GLOBALS.getDbi().escapeString($row[$i]) + "\"";
                }
            }

            if ($con_val != null) {
                $condition += $con_val + " AND";

                if ($meta.primary_key > 0) {
                    $primary_key += $condition;
                    $primary_key_array[$con_key] = $con_val;
                } else if ($meta.unique_key > 0) {
                    $unique_key  += $condition;
                    $unique_key_array[$con_key] = $con_val;
                }

                $nonprimary_condition += $condition;
                $nonprimary_condition_array[$con_key] = $con_val;
            }
        } // end for

        // Correction University of Virginia 19991216:
        // prefer primary or unique keys for condition,
        // but use conjunction of all values if no primary key
        $clause_is_unique = true;

        if ($primary_key) {
            $preferred_condition = $primary_key;
            $condition_array = $primary_key_array;
        } else if ($unique_key) {
            $preferred_condition = $unique_key;
            $condition_array = $unique_key_array;
        } else if (! $force_unique) {
            $preferred_condition = $nonprimary_condition;
            $condition_array = $nonprimary_condition_array;
            $clause_is_unique = false;
        }

        $where_clause = trim(preg_replace("|\\s?AND$|", "", $preferred_condition));
        return [
            $where_clause,
            $clause_is_unique,
            $condition_array,
        ];*/
    } // end function

    /**
     * Generate the charset query part
     *
     * @param String  $collation Collation
     * @param boolean $override  (optional) force "CHARACTER SET" keyword
     *
     * @return String
     */
    public static String getCharsetQueryPart(String $collation, boolean $override /*= false*/)
    {
    	return null; //TODO
        /*list($charset) = explode("_", $collation);
        $keyword = " CHARSET=";

        if ($override) {
            $keyword = " CHARACTER SET ";
        }
        return $keyword + $charset
            + ($charset == $collation ? "" : " COLLATE " + $collation);*/
    }

    /**
     * Generate a button or image tag
     *
     * @param String $button_name  name of button element
     * @param String $button_class class of button or image element
     * @param String $text         text to display
     * @param String $image        image to display
     * @param String $value        value
     *
     * @return String              html content
     *
     * @access  public
     */
    public static String getButtonOrImage(
    	String $button_name,
    	String $button_class,
    	String $text,
    	String $image,
    	String $value, /*= ""*/
    	Globals GLOBALS,
        SessionMap session
    ) {
        if ($value == "") {
            $value = $text;
        }
        if (Globals.getConfig().get("ActionLinksMode") == "text") {
            return " <input class='btn btn-link' type='submit' name='" + $button_name + "'"
                + " value='" + htmlspecialchars($value) + "'"
                + " title='" + htmlspecialchars($text) + "'>" + "\n";
        }
        return "<button class='btn btn-link " + $button_class + "' type='submit'"
            + " name='" + $button_name + "' value='" + htmlspecialchars($value)
            + "' title='" + htmlspecialchars($text) + "'>" + "\n"
            + getIcon($image, $text, false, false, $value, GLOBALS, session)
            + "</button>" + "\n";
    } // end function

    /**
     * Generate a pagination selector for browsing resultsets
     *
     * @param String $name        The name for the request parameter
     * @param int    $rows        Number of rows in the pagination set
     * @param int    $pageNow     current page number
     * @param int    $nbTotalPage number of total pages
     * @param int    $showAll     If the number of pages is lower than this
     *                            variable, no pages will be omitted in pagination
     * @param int    $sliceStart  How many rows at the beginning should always
     *                            be shown?
     * @param int    $sliceEnd    How many rows at the end should always be shown?
     * @param int    $percent     Percentage of calculation page offsets to hop to a
     *                            next page
     * @param int    $range       Near the current page, how many pages should
     *                            be considered "nearby" and displayed as well?
     * @param String $prompt      The prompt to display (sometimes empty)
     *
     * @return String
     *
     * @access  public
     */
    public static String pageselector(
    	String $name,
        int $rows,
        int $pageNow /*= 1*/,
        int $nbTotalPage /*= 1*/,
        int $showAll /*= 200*/,
        int $sliceStart /*= 5*/,
        int $sliceEnd /*= 5*/,
        int $percent /*= 20*/,
        int $range /*= 10*/,
        String $prompt /*= ""*/
    ) {
        int $increment = (int) Math.floor($nbTotalPage / $percent);
        int $pageNowMinusRange = ($pageNow - $range);
        int $pageNowPlusRange = ($pageNow + $range);

        String $gotopage = $prompt + " <select class='pageselector ajax'";

        $gotopage += " name='" + $name + "' >";
        SortedSet<Integer> $pages = new TreeSet<>();
        if ($nbTotalPage < $showAll) {
            $pages.addAll(range(1, $nbTotalPage));
        } else {

            // Always show first X pages
            for (int $i = 1; $i <= $sliceStart; $i++) {
                $pages.add( $i);
            }

            // Always show last X pages
            for (int $i = $nbTotalPage - $sliceEnd; $i <= $nbTotalPage; $i++) {
            	$pages.add( $i);
            }

            // Based on the number of results we add the specified
            // $percent percentage to each page number,
            // so that we have a representing page number every now and then to
            // immediately jump to specific pages.
            // As soon as we get near our currently chosen page ($pageNow -
            // $range), every page number will be shown.
            int $i = $sliceStart;
            int $x = $nbTotalPage - $sliceEnd;
            boolean $met_boundary = false;

            while ($i <= $x) {
                if ($i >= $pageNowMinusRange && $i <= $pageNowPlusRange) {
                    // If our pageselector comes near the current page, we use 1
                    // counter increments
                    $i++;
                    $met_boundary = true;
                } else {
                    // We add the percentage increment to our current page to
                    // hop to the next one in range
                    $i += $increment;

                    // Make sure that we do not cross our boundaries.
                    if ($i > $pageNowMinusRange && ! $met_boundary) {
                        $i = $pageNowMinusRange;
                    }
                }

                if ($i > 0 && $i <= $x) {
                	$pages.add( $i);
                }
            }

            /*
            Add page numbers with "geometrically increasing" distances.

            This helps me a lot when navigating through giant tables.

            Test case: table with 2.28 million sets, 76190 pages. Page of interest
            is between 72376 and 76190.
            Selecting page 72376.
            Now, old version enumerated only +/- 10 pages around 72376 and the
            percentage increment produced steps of about 3000.

            The following code adds page numbers +/- 2,4,8,16,32,64,128,256 etc.
            around the current page.
            */
            $i = $pageNow;
            int $dist = 1;
            while ($i < $x) {
                $dist = 2 * $dist;
                $i = $pageNow + $dist;
                if ($i > 0 && $i <= $x) {
                	$pages.add( $i);
                }
            }

            $i = $pageNow;
            $dist = 1;
            while ($i > 0) {
                $dist = 2 * $dist;
                $i = $pageNow - $dist;
                if ($i > 0 && $i <= $x) {
                	$pages.add( $i);
                }
            }
        }

        String $selected;
        for (int $i : $pages) {
            if ($i == $pageNow) {
                $selected = "selected='selected' style='font-weight: bold'";
            } else {
                $selected = "";
            }
            $gotopage += "                <option " + $selected
                + " value='" + (($i - 1) * $rows) + "'>" + $i + "</option>" + "\n";
        }

        $gotopage += " </select>";

        return $gotopage;
    } // end function


    /**
     * Calculate page number through position
     * @param int $pos       position of first item
     * @param int $max_count number of items per page
     * @return int $page_num
     * @access public
     */
    public static int getPageFromPosition(int $pos, int $max_count)
    {
        return (int) Math.floor($pos / $max_count) + 1;
    }

    /**
     * Prepare navigation for a list
     *
     * @param int      $count       number of elements in the list
     * @param int      $pos         current position in the list
     * @param array    $_url_params url parameters
     * @param String   $script      script name for form target
     * @param String   $frame       target frame
     * @param int      $max_count   maximum number of elements to display from
     *                              the list
     * @param String   $name        the name for the request parameter
     * @param String[] $classes     additional classes for the container
     *
     * @return String the  html content
     *
     * @access  public
     *
     * @todo    use $pos from $_url_params
     */
    public static String getListNavigator(
        int $count,
        int $pos,
        Map<String, String> $_url_params,
        String $script,
        String $frame,
        int $max_count,
        String $name /*= "pos"*/,
        String[] $classes /*= []*/
    ) {
    	return ""; //TODO
    	/*
        // This is often coming from $cfg["MaxTableList"] and
        // people sometimes set it to empty String
        $max_count = intval($max_count);
        if ($max_count <= 0) {
            $max_count = 250;
        }

        $class = $frame == "frame_navigation" ? " class='ajax'" : "";

        $list_navigator_html = "";

        if ($max_count < $count) {
            $classes[] = "pageselector";
            $list_navigator_html += "<div class='" + implode(" ", $classes) + "'>";

            if ($frame != "frame_navigation") {
                $list_navigator_html += __("Page number:");
            }

            // Move to the beginning or to the previous page
            if ($pos > 0) {
                $caption1 = "";
                $caption2 = "";
                if (showIcons("TableNavigationLinksMode")) {
                    $caption1 += "&lt;&lt; ";
                    $caption2 += "&lt; ";
                }
                if (showText("TableNavigationLinksMode")) {
                    $caption1 += _pgettext("First page", "Begin");
                    $caption2 += _pgettext("Previous page", "Previous");
                }
                $title1 = " title='" + _pgettext("First page", "Begin") + "'";
                $title2 = " title='" + _pgettext("Previous page", "Previous") + "'";

                $_url_params[$name] = 0;
                $list_navigator_html += "<a" + $class + $title1 + " href='" + $script
                    + Url.getCommon($_url_params, "&") + "'>" + $caption1
                    + "</a>";

                $_url_params[$name] = $pos - $max_count;
                $list_navigator_html += " <a" + $class + $title2
                    + " href='" + $script + Url.getCommon($_url_params, "&") + "'>"
                    + $caption2 + "</a>";
            }

            $list_navigator_html += "<form action='" + $script
                + "" method="post">";

            $list_navigator_html += Url.getHiddenInputs($_url_params);
            $list_navigator_html += pageselector(
                $name,
                $max_count,
                getPageFromPosition($pos, $max_count),
                ceil($count / $max_count)
            );
            $list_navigator_html += "</form>";

            if ($pos + $max_count < $count) {
                $caption3 = "";
                $caption4 = "";
                if (showText("TableNavigationLinksMode")) {
                    $caption3 += _pgettext("Next page", "Next");
                    $caption4 += _pgettext("Last page", "End");
                }
                if (showIcons("TableNavigationLinksMode")) {
                    $caption3 += " &gt;";
                    $caption4 += " &gt;&gt;";
                }
                $title3 = " title='" + _pgettext("Next page", "Next") + "'";
                $title4 = " title='" + _pgettext("Last page", "End") + "'";

                $_url_params[$name] = $pos + $max_count;
                $list_navigator_html += "<a" + $class + $title3 + " href='" + $script
                    + Url.getCommon($_url_params, "&") + "" >" + $caption3
                    + "</a>";

                $_url_params[$name] = floor($count / $max_count) * $max_count;
                if ($_url_params[$name] == $count) {
                    $_url_params[$name] = $count - $max_count;
                }

                $list_navigator_html += " <a" + $class + $title4
                    + " href='" + $script + Url.getCommon($_url_params, "&") + "" >"
                    + $caption4 + "</a>";
            }
            $list_navigator_html += "</div>" + "\n";
        }

        return $list_navigator_html;*/
    }

    public static String getListNavigator(
            int $count,
            int $pos,
            Map<String, String> $_url_params,
            String $script,
            String $frame,
            int $max_count,
            String $name) {
    	return getListNavigator(
                $count,
                $pos,
                $_url_params,
                $script,
                $frame,
                $max_count,
                $name,
                new String [] {});
    }

    public static String getListNavigator(
            int $count,
            int $pos,
            Map<String, String> $_url_params,
            String $script,
            String $frame,
            int $max_count) {
    	return getListNavigator(
                $count,
                $pos,
                $_url_params,
                $script,
                $frame,
                $max_count,
                "pos",
                new String [] {});
    }
    
    /**
     * replaces %u in given path with current user name
     *
     * example:
     * <code>
     * $user_dir = userDir("/var/pma_tmp/%u/"); // "/var/pma_tmp/root/"
     *
     * </code>
     *
     * @param String $dir with wildcard for user
     *
     * @return String  per user directory
     */
    public static String  userDir(String $dir)
    {
        // add trailing slash
        if (!$dir.endsWith("/")) {
            $dir += "/";
        }

        return $dir.replace("%u", Core.securePath((String) multiget(Globals.getConfig().settings, "Server", "user")));
    }

    /**
     * returns html code for db link to default db page
     * @param request 
     *
     * @param String $database database
     *
     * @return String  html link to default db page
     */
    public static String getDbLink(String $database /*= ""*/, HttpServletRequest request, Globals GLOBALS)
    {
        if (empty( $database) ) {
            if (empty(GLOBALS.getDb())) {
                return "";
            }
            $database = GLOBALS.getDb();
        } else {
            $database = unescapeMysqlWildcards($database);
        }

        String $scriptName = getScriptNameForOption(
            (String)Globals.getConfig().get("DefaultTabDatabase"),
            "database", request, GLOBALS
        );
        Map params = new HashMap();
        params.put("db", $database);
        return "<a href='"
            + $scriptName
            + Url.getCommon(params, ($scriptName.contains( "?") ? "&" : "?"), request, GLOBALS)
            + "' title='"
            + htmlspecialchars(
                String.format(
                    __("Jump to database â€œ%sâ€�."),
                    $database
                )
            )
            + "'>" + htmlspecialchars($database) + "</a>";
    }

    /**
     * Prepare a lightbulb hint explaining a known external bug
     * that affects a functionality
     *
     * @param String $functionality   localized message explaining the func.
     * @param String $component       "mysql" (eventually, "php")
     * @param String $minimum_version of this component
     * @param String $bugref          bug reference for this component
     *
     * @return String
     */
    public static String getExternalBug(
    		String $functionality,
    		String $component,
    		String $minimum_version,
    		String $bugref
    ) {
    	return ""; // Unsupported
    	/* 
        $ext_but_html = "";
        if (($component == "mysql") && (GLOBALS.getDbi().getVersion() < $minimum_version)) {
            $ext_but_html += showHint(
                sprintf(
                    __("The %s functionality is affected by a known bug, see %s"),
                    $functionality,
                    Core.linkURL("https://bugs.mysql.com/") + $bugref
                )
            );
        }
        return $ext_but_html;*/
    }

    /**
     * Generates a set of radio HTML fields
     *
     * @param String  $html_field_name the radio HTML field
     * @param array   $choices         the choices values and labels
     * @param String  $checked_choice  the choice to check by default
     * @param boolean $line_break      whether to add HTML line break after a choice
     * @param boolean $escape_label    whether to use htmlspecialchars() on label
     * @param String  $class           enclose each choice with a div of this class
     * @param String  $id_prefix       prefix for the id attribute, name will be
     *                                 used if this is not supplied
     *
     * @return String                  set of html radio fiels
     */
    public static String getRadioFields(
    		String $html_field_name,
        	Map $choices,
        	String $checked_choice /*= ""*/,
        	boolean $line_break /*= true*/,
        	boolean $escape_label /*= true*/,
        	String $class /*= ""*/,
        	String $id_prefix /*= ""*/
    ) {
        String $radio_html = "";

        //for ($choices as $choice_value => $choice_label) {
        Set<Entry> entries = $choices.entrySet();
        for (Entry entry : entries) {
        	String $choice_value = (String) entry.getKey();
        	String $choice_label = (String) entry.getValue(); 
            if (empty($id_prefix)) {
                $id_prefix = $html_field_name;
            }
            String $html_field_id = $id_prefix + "_" + $choice_value;
            int $cheched;
            
            int $checked;
            if ($choice_value .equals( $checked_choice)) {
                $checked = 1;
            } else {
                $checked = 0;
            }
            
            Map<String, Object> model = new HashMap<>();
            model.put("class" , $class);
            model.put("html_field_name" , $html_field_name);
            model.put("html_field_id" , $html_field_id);
            model.put("choice_value" , $choice_value);
            model.put("is_line_break" , $line_break);
            model.put("choice_label" , $choice_label);
            model.put("escape_label" , $escape_label);
            model.put("checked" , $checked);
                             
            $radio_html += JtwigFactory.render("radio_fields", model);
        }

        return $radio_html;
    }

    /**
     * Generates and returns an HTML dropdown
     *
     * @param String $select_name   name for the select element
     * @param array  $choices       choices values
     * @param String $active_choice the choice to select by default
     * @param String $id            id of the select element; can be different in
     *                              case the dropdown is present more than once
     *                              on the page
     * @param String $class         class for the select element
     * @param String $placeholder   Placeholder for dropdown if nothing else
     *                              is selected
     *
     * @return String               html content
     *
     * @todo    support titles
     */
    public static String getDropdown(
    	String $select_name,
        Map $choices,
        String $active_choice,
        String $id,
        String $class /*= ""*/,
        String $placeholder /*= null*/
    ) {
        Map $resultOptions = new HashMap();
        boolean $selected = false;

        //foreach ($choices as $one_choice_value => $one_choice_label) {
        Set<Entry> entries = $choices.entrySet();
        for (Entry entry : entries) {
        	String $one_choice_value = (String) entry.getKey();
        	String $one_choice_label = (String) entry.getValue();
        	multiput($resultOptions, $one_choice_value, "value", $one_choice_value);
        	multiput($resultOptions, $one_choice_value, "selected", false);

            if ($one_choice_value.equals($active_choice)) {
            	multiput($resultOptions, $one_choice_value, "selected", true);
                $selected = true;
            }
            multiput($resultOptions, $one_choice_value, "label", $one_choice_label);
        }
        
        Map<String, Object> model = new HashMap<>();
        model.put("select_name" , $select_name);
        model.put("id" , $id);
        model.put("class" , $class);
        model.put("placeholder" , $placeholder);
        model.put("selected" , $selected);
        model.put("result_options" , $resultOptions);
        
        return JtwigFactory.render("dropdown", model);
    }

    /**
     * Generates a slider effect (jQjuery)
     * Takes care of generating the initial <div> and the link
     * controlling the slider; you have to generate the </div> yourself
     * after the sliding section.
     *
     * @param String      $id              the id of the <div> on which to apply the effect
     * @param String      $message         the message to show as a link
     * @param String|null $overrideDefault override InitialSlidersState config
     *
     * @return String         html div element
     *
     */
    public static String getDivForSliderEffect(String $id /*= ""*/,String $message /*= ""*/, String $overrideDefault /*= null*/)
    {
        Map<String, Object> model = new HashMap<>();
        model.put("id" , $id);
        model.put("initial_sliders_state" , ($overrideDefault != null) ? $overrideDefault : Globals.getConfig().get("InitialSlidersState"));
        model.put("message" , $message);
        
        return JtwigFactory.render("div_for_slider_effect", model);
    }

    /**
     * Creates an AJAX sliding toggle button
     * (or and equivalent form when AJAX is disabled)
     *
     * @param String $action      The URL for the request to be executed
     * @param String $select_name The name for the dropdown box
     * @param array  $options     An array of options (see PhpMyAdmin\Rte\Footer)
     * @param String $callback    A JS snippet to execute when the request is
     *                            successfully processed
     *
     * @return String   HTML code for the toggle button
     */
    public static String toggleButton(String $action, String $select_name, List<Map> $options, String $callback, Globals GLOBALS)
    {
        
        // Do the logic first
        String $link = "$action&amp;" + urlencode($select_name) + "=";
        String $link_on = $link + urlencode((String)$options.get(1).get("value"));
        String $link_off = $link + urlencode((String)$options.get(0).get("value"));

        String $state;
        if ($options.get(1).get("selected").equals(true)) {
            $state = "on";
        } else if ($options.get(0).get("selected").equals(true)) {
            $state = "off";
        } else {
            $state = "on";
        }

        Map<String, Object> model = new HashMap<>();
        model.put("pma_theme_image" , GLOBALS.getPmaThemeImage());
        model.put("text_dir" , GLOBALS.getTextDir());
        model.put("link_on" , $link_on);
        model.put("link_off" , $link_off);
        model.put("toggle_on" , $options.get(1).get("label"));
        model.put("link_on" , $options.get(0).get("label"));
        model.put("callback" , $callback);
        model.put("state" , $state);
        
        return JtwigFactory.render("toggle_button", model);
    }

    /**
     * Clears cache content which needs to be refreshed on user change.
     *
     * @return void
     */
    public static void clearUserCache(Globals GLOBALS, SessionMap session)
    {
        cacheUnset("is_superuser", GLOBALS, session);
        cacheUnset("is_createuser", GLOBALS, session);
        cacheUnset("is_grantuser", GLOBALS, session);
    }

    /**
     * Calculates session cache key
     *
     * @return String
     */
    public static String cacheKey(Globals GLOBALS)
    {
        if (!empty(multiget(Globals.getConfig().settings, "Server", "user"))) {
            return "server_" + GLOBALS.getServer() + "_" + multiget(Globals.getConfig().settings, "Server", "user");
        }

        return "server_" + GLOBALS.getServer();
    }

    /**
     * Verifies if something is cached in the session
     *
     * @param String $var variable name
     *
     * @return boolean
     */
    public static boolean cacheExists(String $var, Globals GLOBALS, SessionMap session)
    {
        return !empty(multiget(session, "cache", cacheKey(GLOBALS), $var));
    }

    /**
     * Gets cached information from the session
     *
     * @param String  $var      variable name
     * @param Closure $callback callback to fetch the value
     *
     * @return mixed
     */
    public static Object cacheGet(String $var, Function $callback /*= null*/, Globals GLOBALS, SessionMap session)
    {
        if (cacheExists($var, GLOBALS, session)) {
        	//System.out.println("DEBUG Loading: " + $var + " that contains:" + multiget(session, "cache", cacheKey(GLOBALS), $var));
            return multiget(session, "cache", cacheKey(GLOBALS), $var);
        }

        if ($callback != null) {
			Object $val = $callback.apply(null);	//FIXME 0-ary function in Java ?!?
            cacheSet($var, $val, GLOBALS, session);
            return $val;
        }
        return null;
    }

    /**
     * Caches information in the session
     *
     * @param String $var variable name
     * @param mixed  $val value
     *
     * @return void
     */
    public static void cacheSet(String $var, Object $val /*= null*/, Globals GLOBALS, SessionMap session)
    {
    	multiput(session, "cache", cacheKey(GLOBALS), $var, $val);
    }

    /**
     * Removes cached information from the session
     *
     * @param String $var variable name
     *
     * @return void
     */
    public static void cacheUnset(String $var, Globals GLOBALS, SessionMap session)
    {
    	multiremove(session, "cache", cacheKey(GLOBALS), $var);
    }

    /**
     * Converts a bit value to printable format;
     * in MySQL a BIT field can be from 1 to 64 bits so we need this
     * function because in PHP, decbin() supports only 32 bits
     * on 32-bit servers
     *
     * @param int $value  coming from a BIT field
     * @param int $length length
     *
     * @return String the printable value
     */
    public static String printableBitValue(int $value, int $length)
    {
    	return null; // TODO
    	/*
        // if running on a 64-bit server or the length is safe for decbin()
        if (PHP_INT_SIZE == 8 || $length < 33) {
            $printable = decbin($value);
        } else {
            // FIXME: does not work for the leftmost bit of a 64-bit value
            $i = 0;
            $printable = "";
            while ($value >= pow(2, $i)) {
                ++$i;
            }
            if ($i != 0) {
                --$i;
            }

            while ($i >= 0) {
                if ($value - pow(2, $i) < 0) {
                    $printable = "0" + $printable;
                } else {
                    $printable = "1" + $printable;
                    $value -= pow(2, $i);
                }
                --$i;
            }
            $printable = strrev($printable);
        }
        $printable = str_pad($printable, $length, "0", STR_PAD_LEFT);
        return $printable;*/
    }

    /**
     * Converts a BIT type default value
     * for example, b"010" becomes 010
     *
     * @param String $bit_default_value value
     *
     * @return String the converted value
     */
    public static String convertBitDefaultValue(String $bit_default_value)
    {
    	return null; //TODO
        //return rtrim(ltrim(htmlspecialchars_decode($bit_default_value, ENT_QUOTES), "b'"), "'");
    }

    /**
     * Extracts the various parts from a column spec
     *
     * @param String $columnspec Column specification
     *
     * @return array associative array containing type, spec_in_brackets
     *          and possibly enum_set_values (another array)
     */
    public static String extractColumnSpec(Map $columnspec)
    {
    	return ""; //TODO
    	/*
        $first_bracket_pos = mb_strpos($columnspec, "(");
        if ($first_bracket_pos) {
            $spec_in_brackets = rtrim(
                mb_substr(
                    $columnspec,
                    $first_bracket_pos + 1,
                    mb_strrpos($columnspec, ")") - $first_bracket_pos - 1
                )
            );
            // convert to lowercase just to be sure
            $type = mb_strtolower(
                rtrim(mb_substr($columnspec, 0, $first_bracket_pos))
            );
        } else {
            // Split trailing attributes such as unsigned,
            // binary, zerofill and get data type name
            $type_parts = explode(" ", $columnspec);
            $type = mb_strtolower($type_parts[0]);
            $spec_in_brackets = "";
        }

        if ("enum" == $type || "set" == $type) {
            // Define our working vars
            $enum_set_values = parseEnumSetValues($columnspec, false);
            $printtype = $type
                + "(" + str_replace("","", "", "", $spec_in_brackets) + ")";
            $binary = false;
            $unsigned = false;
            $zerofill = false;
        } else {
            $enum_set_values = [];

            // Create printable type name
            $printtype = mb_strtolower($columnspec);

            // Strip the "BINARY" attribute, except if we find "BINARY(" because
            // this would be a BINARY or VARBINARY column type;
            // by the way, a BLOB should not show the BINARY attribute
            // because this is not accepted in MySQL syntax.
            if (false !== strpos($printtype, "binary")
                && ! preg_match("@binary[\\(]@", $printtype)
            ) {
                $printtype = str_replace("binary", "", $printtype);
                $binary = true;
            } else {
                $binary = false;
            }

            $printtype = preg_replace(
                "@zerofill@",
                "",
                $printtype,
                -1,
                $zerofill_cnt
            );
            $zerofill = ($zerofill_cnt > 0);
            $printtype = preg_replace(
                "@unsigned@",
                "",
                $printtype,
                -1,
                $unsigned_cnt
            );
            $unsigned = ($unsigned_cnt > 0);
            $printtype = trim($printtype);
        }

        $attribute     = " ";
        if ($binary) {
            $attribute = "BINARY";
        }
        if ($unsigned) {
            $attribute = "UNSIGNED";
        }
        if ($zerofill) {
            $attribute = "UNSIGNED ZEROFILL";
        }

        $can_contain_collation = false;
        if (! $binary
            && preg_match(
                "@^(char|varchar|text|tinytext|mediumtext|longtext|set|enum)@",
                $type
            )
        ) {
            $can_contain_collation = true;
        }

        // for the case ENUM("&#8211;","&ldquo;")
        $displayed_type = htmlspecialchars($printtype);
        if (mb_strlen($printtype) > Globals.getConfig()["LimitChars"]) {
            $displayed_type  = "<abbr title='" + htmlspecialchars($printtype) + "'>";
            $displayed_type += htmlspecialchars(
                mb_substr(
                    $printtype,
                    0,
                    Globals.getConfig()["LimitChars"]
                ) + "..."
            );
            $displayed_type += "</abbr>";
        }

        return [
            "type" => $type,
            "spec_in_brackets" => $spec_in_brackets,
            "enum_set_values"  => $enum_set_values,
            "print_type" => $printtype,
            "binary" => $binary,
            "unsigned" => $unsigned,
            "zerofill" => $zerofill,
            "attribute" => $attribute,
            "can_contain_collation" => $can_contain_collation,
            "displayed_type" => $displayed_type,
        ];*/
    }

    /**
     * Verifies if this table"s engine supports foreign keys
     *
     * @param String $engine engine
     *
     * @return boolean
     */
    public static boolean isForeignKeySupported(String $engine)
    {
    	return false; //TODO
    	/*
        $engine = strtoupper((String) $engine);
        if (($engine == "INNODB") || ($engine == "PBXT")) {
            return true;
        } else if ($engine == "NDBCLUSTER" || $engine == "NDB") {
            $ndbver = strtolower(
                GLOBALS.getDbi().fetchValue("SELECT @@ndb_version_string")
            );
            if (substr($ndbver, 0, 4) == "ndb-") {
                $ndbver = substr($ndbver, 4);
            }
            return version_compare($ndbver, "7.3", ">=");
        }

        return false;*/
    }

    /**
     * Is Foreign key check enabled?
     *
     * @return boolean
     */
    public static boolean isForeignKeyCheck()
    {
    	return false; //TODO
    	/*
        if (Globals.getConfig()["DefaultForeignKeyChecks"] === "enable") {
            return true;
        } else if (Globals.getConfig()["DefaultForeignKeyChecks"] === "disable") {
            return false;
        }
        return (GLOBALS.getDbi().getVariable("FOREIGN_KEY_CHECKS") == "ON");*/
    }

    /**
     * Get HTML for Foreign key check checkbox
     *
     * @return String HTML for checkbox
     */
    public static String getFKCheckbox()
    {
        Map<String, Object> model = new HashMap<>();
        model.put("checked", isForeignKeyCheck());
        return JtwigFactory.render("fk_checkbox", model);
    }

    /**
     * Handle foreign key check request
     *
     * @return boolean Default foreign key checks value
     */
    public static boolean handleDisableFKCheckInit()
    {
    	return false; //TODO
    	/*
        boolean $default_fk_check_value
            = GLOBALS.getDbi().getVariable("FOREIGN_KEY_CHECKS") == "ON";
        if (!empty($_REQUEST["fk_checks"])) {
            if (empty($_REQUEST["fk_checks"])) {
                // Disable foreign key checks
                GLOBALS.getDbi().setVariable("FOREIGN_KEY_CHECKS", "OFF");
            } else {
                // Enable foreign key checks
                GLOBALS.getDbi().setVariable("FOREIGN_KEY_CHECKS", "ON");
            }
        } // else do nothing, go with default
        return $default_fk_check_value;*/
    }

    /**
     * Cleanup changes done for foreign key check
     *
     * @param boolean $default_fk_check_value original value for "FOREIGN_KEY_CHECKS"
     *
     * @return void
     */
    public static void handleDisableFKCheckCleanup(boolean $default_fk_check_value)
    {
    	//TODO
    	/*GLOBALS.getDbi().setVariable(
            "FOREIGN_KEY_CHECKS",
            $default_fk_check_value ? "ON" : "OFF"
        );*/
    }

    /**
     * Converts GIS data to Well Known Text format
     *
     * @param String $data        GIS data
     * @param boolean   $includeSRID Add SRID to the WKT
     *
     * @return String GIS data in Well Know Text format
     */
    public static String asWKT(String $data, boolean $includeSRID /*= false*/)
    {
    	return null; //TODO
    	/*
        // Convert to WKT format
        $hex = bin2hex($data);
        $spatialAsText = "ASTEXT";
        $spatialSrid = "SRID";
        if (GLOBALS.getDbi().getVersion() >= 50600) {
            $spatialAsText = "ST_ASTEXT";
            $spatialSrid = "ST_SRID";
        }
        $wktsql     = "SELECT $spatialAsText(x'" + $hex + "')";
        if ($includeSRID) {
            $wktsql += ", $spatialSrid(x'" + $hex + "')";
        }

        $wktresult  = GLOBALS.getDbi().tryQuery(
            $wktsql
        );
        $wktarr     = GLOBALS.getDbi().fetchRow($wktresult, 0);
        $wktval     = $wktarr[0] ?? null;

        if ($includeSRID) {
            $srid = $wktarr[1] ?? null;
            $wktval = "'" + $wktval + ""," + $srid;
        }
        @GLOBALS.getDbi().freeResult($wktresult);

        return $wktval;*/
    }

    /**
     * If the String starts with a \r\n pair (0x0d0a) add an extra \n
     *
     * @param String $String String
     *
     * @return String with the chars replaced
     */
    public static String duplicateFirstNewline(String $String)
    {
        int $first_occurence = $String.indexOf("\r\n");
        if ($first_occurence == 0) {
            $String = "\n" + $String;
        }
        return $String;
    }

    /**
     * Get the action word corresponding to a script name
     * in order to display it as a title in navigation panel
     *
     * @param String $target a valid value for $cfg["NavigationTreeDefaultTabTable"],
     *                       $cfg["NavigationTreeDefaultTabTable2"],
     *                       $cfg["DefaultTabTable"] or $cfg["DefaultTabDatabase"]
     *
     * @return String|boolean Title for the $cfg value
     */
    public static String getTitleForTarget(String $target)
    {
    	Map<String, String> $mapping = new HashMap<>();
    	$mapping.put("structure", __("Structure"));
    	$mapping.put("sql", __("SQL"));
    	$mapping.put("search", __("Search"));
    	$mapping.put("insert", __("Insert"));
    	$mapping.put("browse", __("Browse"));
    	$mapping.put("operations", __("Operations"));
    	
        return $mapping.get($target);
    }

    /**
     * Get the script name corresponding to a plain English config word
     * in order to append in links on navigation and main panel
     *
     * @param String $target   a valid value for
     *                         $cfg["NavigationTreeDefaultTabTable"],
     *                         $cfg["NavigationTreeDefaultTabTable2"],
     *                         $cfg["DefaultTabTable"], $cfg["DefaultTabDatabase"] or
     *                         $cfg["DefaultTabServer"]
     * @param String $location one out of "server", "table", "database"
     *
     * @return String script name corresponding to the config word
     */
    public static String getScriptNameForOption(String $target, String $location, HttpServletRequest req, Globals GLOBALS)
    {
    	if ($target == null) {
    		return "";
    	}
        if ("server".equals($location)) {
            // Values for $cfg["DefaultTabServer"]
            switch ($target) {
                case "welcome":
                    return Url.getFromRoute("/", req, GLOBALS);
                case "databases":
                    return Url.getFromRoute("/server/databases", req, GLOBALS);
                case "status":
                    return Url.getFromRoute("/server/status", req, GLOBALS);
                case "variables":
                    return Url.getFromRoute("/server/variables", req, GLOBALS);
                case "privileges":
                    return Url.getFromRoute("/server/privileges", req, GLOBALS);
            }
        } else if ("database".equals($location)) {
            // Values for $cfg["DefaultTabDatabase"]
            switch ($target) {
                case "structure":
                    return Url.getFromRoute("/database/structure", req, GLOBALS);
                case "sql":
                    return Url.getFromRoute("/database/sql", req, GLOBALS);
                case "search":
                    return Url.getFromRoute("/database/search", req, GLOBALS);
                case "operations":
                    return Url.getFromRoute("/database/operations", req, GLOBALS);
            }
        } else if ("table".equals($location)) {
            // Values for $cfg["DefaultTabTable"],
            // $cfg["NavigationTreeDefaultTabTable"] and
            // $cfg["NavigationTreeDefaultTabTable2"]
            switch ($target) {
                case "structure":
                    return Url.getFromRoute("/table/structure", req, GLOBALS);
                case "sql":
                    return Url.getFromRoute("/table/sql", req, GLOBALS);
                case "search":
                    return Url.getFromRoute("/table/search", req, GLOBALS);
                case "insert":
                    return Url.getFromRoute("/table/change", req, GLOBALS);
                case "browse":
                    return Url.getFromRoute("/sql", req, GLOBALS);
            }
        }

        return $target;
    }

    /**
     * Formats user String, expanding @VARIABLES@, accepting strftime format
     * String.
     *
     * @param String       $String  Text where to do expansion.
     * @param array|String $escape  Function to call for escaping variable values.
     *                              Can also be an array of:
     *                              - the escape method name
     *                              - the class that contains the method
     *                              - location of the class (for inclusion)
     * @param array        $updates Array with overrides for default parameters
     *                              (obtained from GLOBALS).
     *
     * @return String
     */
    public static String expandUserString(
        String $String,
        Object $escape /*= null*/,
        Map $updates /*= []*/
    ) {
    	return null; //TODO
    	/*
        // Content
        $vars = [];
        $vars["http_host"] = Core.getenv("HTTP_HOST");
        $vars["server_name"] = Globals.getConfig()["Server"]["host"];
        $vars["server_verbose"] = Globals.getConfig()["Server"]["verbose"];

        if (empty(Globals.getConfig()["Server"]["verbose"])) {
            $vars["server_verbose_or_name"] = Globals.getConfig()["Server"]["host"];
        } else {
            $vars["server_verbose_or_name"] = Globals.getConfig()["Server"]["verbose"];
        }

        $vars["database"] = GLOBALS["db"];
        $vars["table"] = GLOBALS["table"];
        $vars["phpmyadmin_version"] = "phpMyAdmin " + PMA_VERSION;

        // Update forced variables
        foreach ($updates as $key => $val) {
            $vars[$key] = $val;
        }

        // Replacement mapping
        // The __VAR__ ones are for backward compatibility, because user
        // might still have it in cookies.
        $replace = [
            "@HTTP_HOST@" => $vars["http_host"],
            "@SERVER@" => $vars["server_name"],
            "__SERVER__" => $vars["server_name"],
            "@VERBOSE@" => $vars["server_verbose"],
            "@VSERVER@" => $vars["server_verbose_or_name"],
            "@DATABASE@" => $vars["database"],
            "__DB__" => $vars["database"],
            "@TABLE@" => $vars["table"],
            "__TABLE__" => $vars["table"],
            "@PHPMYADMIN@" => $vars["phpmyadmin_version"],
        ];

        // Optional escaping
        if ($escape !== null) {
            if (is_array($escape)) {
                $escape_class = new $escape[1];
                $escape_method = $escape[0];
            }
            foreach ($replace as $key => $val) {
                if (!empty($escape_class, $escape_method)) {
                    $replace[$key] = $escape_class.$escape_method($val);
                } else {
                    $replace[$key] = ($escape == "backquote")
                        ? $escape($val)
                        : $escape($val);
                }
            }
        }

        // Backward compatibility in 3.5.x 
        if (mb_strpos($String, "@FIELDS@") !== false) {
            $String = strtr($String, ["@FIELDS@" => "@COLUMNS@"]);
        }

        // Fetch columns list if required 
        if (mb_strpos($String, "@COLUMNS@") !== false) {
            $columns_list = GLOBALS.getDbi().getColumns(
                GLOBALS["db"],
                GLOBALS["table"]
            );

            // sometimes the table no longer exists at this point
            if ($columns_list !== null) {
                $column_names = [];
                foreach ($columns_list as $column) {
                    if ($escape !== null) {
                        $column_names[] = $escape($column["Field"]);
                    } else {
                        $column_names[] = $column["Field"];
                    }
                }
                $replace["@COLUMNS@"] = implode(",", $column_names);
            } else {
                $replace["@COLUMNS@"] = "*";
            }
        }

        // Do the replacement 
        return strtr((String) strftime($String), $replace);
        */
    }

    public static String expandUserString(
            String $String) {
    	return expandUserString($String, null, new HashMap<>());
    }
            
    /**
     * Prepare the form used to browse anywhere on the local server for a file to
     * import
     *
     * @param String $max_upload_size maximum upload size
     *
     * @return String
     */
    public static String getBrowseUploadFileBlock(int $max_upload_size, Globals GLOBALS)
    {
        String $block_html = "";

        if (GLOBALS.isUpload() && ! empty(Globals.getConfig().get("UploadDir"))) {
            $block_html += "<label for='radio_import_file'>";
        } else {
            $block_html += "<label for='input_import_file'>";
        }

        $block_html += __("Browse your computer:") + "</label>"
            + "<div id='upload_form_status' class='hide'></div>"
            + "<div id='upload_form_status_info' class=''hide'></div>"
            + "<input type='file' name='import_file' id='input_import_file'>"
            + getFormattedMaximumUploadSize($max_upload_size) + "\n"
            // some browsers should respect this :)
            + generateHiddenMaxFileSize($max_upload_size) + "\n";

        return $block_html;
    }

    /**
     * Prepare the form used to select a file to import from the server upload
     * directory
     *
     * @param ImportPlugin[] $import_list array of import plugins
     * @param String         $uploaddir   upload directory
     *
     * @return String
     */
    public static String getSelectUploadFileBlock(List $import_list, String $uploaddir)
    {
    	return null; //TODO
    	/*
        $fileListing = new FileListing();

        $block_html = "";
        $block_html += "<label for='radio_local_import_file'>"
            + sprintf(
                __("Select from the web server upload directory <b>%s</b>:"),
                htmlspecialchars(userDir($uploaddir))
            )
            + "</label>";

        $extensions = "";
        foreach ($import_list as $import_plugin) {
            if (! empty($extensions)) {
                $extensions += "|";
            }
            $extensions += $import_plugin.getProperties().getExtension();
        }

        $matcher = "@\.(" + $extensions + ")(\.("
            + $fileListing.supportedDecompressions() + "))?$@";

        $active = (!empty(GLOBALS["timeout_passed"], GLOBALS["local_import_file"]) && GLOBALS["timeout_passed"])
            ? GLOBALS["local_import_file"]
            : "";

        $files = $fileListing.getFileSelectOptions(
            userDir($uploaddir),
            $matcher,
            $active
        );

        if ($files === false) {
            Message.error(
                __("The directory you set for upload work cannot be reached.")
            ).display();
        } else if (! empty($files)) {
            $block_html += "\n"
                + "    <select style='margin: 5px' size='1' "
                + "name='local_import_file' "
                + "id='select_local_import_file'>" + "\n"
                + "        <option value=''>&nbsp;</option>" + "\n"
                + $files
                + "    </select>" + "\n";
        } else if (empty($files)) {
            $block_html += "<i>" + __("There are no files to upload!") + "</i>";
        }

        return $block_html;*/
    }

    /**
     * Build titles and icons for action links
     * @param GLOBALS 
     * @param session 
     *
     * @return array   the action titles
     */
    public static Map<String,String> buildActionTitles(Globals GLOBALS, SessionMap session)
    {
    	Map<String,String> $titles = new HashMap<String,String>();

        $titles.put("Browse"     , getIcon("b_browse", __("Browse"), GLOBALS, session));
        $titles.put("NoBrowse"   , getIcon("bd_browse", __("Browse"), GLOBALS, session));
        $titles.put("Search"     , getIcon("b_select", __("Search"), GLOBALS, session));
        $titles.put("NoSearch"   , getIcon("bd_select", __("Search"), GLOBALS, session));
        $titles.put("Insert"     , getIcon("b_insrow", __("Insert"), GLOBALS, session));
        $titles.put("NoInsert"   , getIcon("bd_insrow", __("Insert"), GLOBALS, session));
        $titles.put("Structure"  , getIcon("b_props", __("Structure"), GLOBALS, session));
        $titles.put("Drop"       , getIcon("b_drop", __("Drop"), GLOBALS, session));
        $titles.put("NoDrop"     , getIcon("bd_drop", __("Drop"), GLOBALS, session));
        $titles.put("Empty"      , getIcon("b_empty", __("Empty"), GLOBALS, session));
        $titles.put("NoEmpty"    , getIcon("bd_empty", __("Empty"), GLOBALS, session));
        $titles.put("Edit"       , getIcon("b_edit", __("Edit"), GLOBALS, session));
        $titles.put("NoEdit"     , getIcon("bd_edit", __("Edit"), GLOBALS, session));
        $titles.put("Export"     , getIcon("b_export", __("Export"), GLOBALS, session));
        $titles.put("NoExport"   , getIcon("bd_export", __("Export"), GLOBALS, session));
        $titles.put("Execute"    , getIcon("b_nextpage", __("Execute"), GLOBALS, session));
        $titles.put("NoExecute"  , getIcon("bd_nextpage", __("Execute"), GLOBALS, session));
        // For Favorite/NoFavorite, we need icon only.
        $titles.put("Favorite"  , getIcon("b_favorite", "", GLOBALS, session));
        $titles.put("NoFavorite" , getIcon("b_no_favorite", "", GLOBALS, session));

        return $titles;
    }

    /**
     * This function processes the datatypes supported by the DB,
     * as specified in Types.getColumns() and either returns an array
     * (useful for quickly checking if a datatype is supported)
     * or an HTML snippet that creates a drop-down list.
     *
     * @param boolean   $html     Whether to generate an html snippet or an array
     * @param String $selected The value to mark as selected in HTML mode
     *
     * @return mixed   An HTML snippet or an array of datatypes.
     *
     */
    public static Object getSupportedDatatypes(boolean $html /*= false*/, String $selected /*= ""*/)
    {
    	return null; //TODO
    	/*
        if ($html) {
            // NOTE: the SELECT tag in not included in this snippet.
            $retval = "";

            foreach (GLOBALS.getDbi().types.getColumns() as $key => $value) {
                if (is_array($value)) {
                    $retval += "<optgroup label='" + htmlspecialchars($key) + "'>";
                    foreach ($value as $subvalue) {
                        if ($subvalue == $selected) {
                            $retval += sprintf(
                                "<option selected="selected" title="%s">%s</option>",
                                GLOBALS.getDbi().types.getTypeDescription($subvalue),
                                $subvalue
                            );
                        } else if ($subvalue === "-") {
                            $retval += "<option disabled="disabled">";
                            $retval += $subvalue;
                            $retval += "</option>";
                        } else {
                            $retval += sprintf(
                                "<option title="%s">%s</option>",
                                GLOBALS.getDbi().types.getTypeDescription($subvalue),
                                $subvalue
                            );
                        }
                    }
                    $retval += "</optgroup>";
                } else {
                    if ($selected == $value) {
                        $retval += sprintf(
                            "<option selected="selected" title="%s">%s</option>",
                            GLOBALS.getDbi().types.getTypeDescription($value),
                            $value
                        );
                    } else {
                        $retval += sprintf(
                            "<option title="%s">%s</option>",
                            GLOBALS.getDbi().types.getTypeDescription($value),
                            $value
                        );
                    }
                }
            }
        } else {
            $retval = [];
            foreach (GLOBALS.getDbi().types.getColumns() as $value) {
                if (is_array($value)) {
                    foreach ($value as $subvalue) {
                        if ($subvalue !== "-") {
                            $retval[] = $subvalue;
                        }
                    }
                } else {
                    if ($value !== "-") {
                        $retval[] = $value;
                    }
                }
            }
        }

        return $retval;*/
    } // end getSupportedDatatypes()

    /**
     * Returns a list of datatypes that are not (yet) handled by PMA.
     * Used by: /table/change and libraries/db_routines.inc.php
     *
     * @return array   list of datatypes
     */
    public static Map unsupportedDatatypes()
    {
        return new HashMap();
    }

    /**
     * Return GIS data types
     *
     * @param boolean $upper_case whether to return values in upper case
     *
     * @return String[] GIS data types
     */
    public static String[] getGISDatatypes(boolean $upper_case /*= false*/)
    {
        String [] $gis_data_types = new String[] {
            "geometry",
            "point",
            "linestring",
            "polygon",
            "multipoint",
            "multilinestring",
            "multipolygon",
            "geometrycollection",
        };
        if ($upper_case) {
            for (int i = 0; i < $gis_data_types.length; ++i) {
            	$gis_data_types[i] = $gis_data_types[i].toUpperCase();
            }
        }
        return $gis_data_types;
    }

    /**
     * Generates GIS data based on the String passed.
     *
     * @param String $gis_string   GIS String
     * @param int    $mysqlVersion The mysql version as int
     *
     * @return String GIS data enclosed in "ST_GeomFromText" or "GeomFromText" function
     */
    public static String createGISData(String $gis_string, int $mysqlVersion)
    {
    	return null; //TODO
    	/*
        $geomFromText = ($mysqlVersion >= 50600) ? "ST_GeomFromText" : "GeomFromText";
        $gis_string = trim($gis_string);
        $geom_types = "(POINT|MULTIPOINT|LINESTRING|MULTILINESTRING|"
            + "POLYGON|MULTIPOLYGON|GEOMETRYCOLLECTION)";
        if (preg_match("/^"" + $geom_types + "\(.*\)",[0-9]*$/i", $gis_string)) {
            return $geomFromText + "(" + $gis_string + ")";
        } else if (preg_match("/^" + $geom_types + "\(.*\)$/i", $gis_string)) {
            return $geomFromText + "("" + $gis_string + "")";
        }

        return $gis_string;*/
    }

    /**
     * Returns the names and details of the functions
     * that can be applied on geometry data types.
     *
     * @param String $geom_type if provided the output is limited to the functions
     *                          that are applicable to the provided geometry type.
     * @param boolean   $binary    if set to false functions that take two geometries
     *                          as arguments will not be included.
     * @param boolean   $display   if set to true separators will be added to the
     *                          output array.
     *
     * @return array names and details of the functions that can be applied on
     *               geometry data types.
     */
    public static Map getGISFunctions(
    	String $geom_type /*= null*/,
        boolean $binary /*= true*/,
        boolean $display /*= false*/
    ) {
    	return null; // TODO
    	/*
        $funcs = [];
        if ($display) {
            $funcs[] = ["display" => " "];
        }

        // Unary functions common to all geometry types
        $funcs["Dimension"]    = [
            "params" => 1,
            "type" => "int",
        ];
        $funcs["Envelope"]     = [
            "params" => 1,
            "type" => "Polygon",
        ];
        $funcs["GeometryType"] = [
            "params" => 1,
            "type" => "text",
        ];
        $funcs["SRID"]         = [
            "params" => 1,
            "type" => "int",
        ];
        $funcs["IsEmpty"]      = [
            "params" => 1,
            "type" => "int",
        ];
        $funcs["IsSimple"]     = [
            "params" => 1,
            "type" => "int",
        ];

        $geom_type = mb_strtolower(trim((String) $geom_type));
        if ($display && $geom_type != "geometry" && $geom_type != "multipoint") {
            $funcs[] = ["display" => "--------"];
        }

        // Unary functions that are specific to each geometry type
        if ($geom_type == "point") {
            $funcs["X"] = [
                "params" => 1,
                "type" => "float",
            ];
            $funcs["Y"] = [
                "params" => 1,
                "type" => "float",
            ];
        } else if ($geom_type == "linestring") {
            $funcs["EndPoint"]   = [
                "params" => 1,
                "type" => "point",
            ];
            $funcs["GLength"]    = [
                "params" => 1,
                "type" => "float",
            ];
            $funcs["NumPoints"]  = [
                "params" => 1,
                "type" => "int",
            ];
            $funcs["StartPoint"] = [
                "params" => 1,
                "type" => "point",
            ];
            $funcs["IsRing"]     = [
                "params" => 1,
                "type" => "int",
            ];
        } else if ($geom_type == "multilinestring") {
            $funcs["GLength"]  = [
                "params" => 1,
                "type" => "float",
            ];
            $funcs["IsClosed"] = [
                "params" => 1,
                "type" => "int",
            ];
        } else if ($geom_type == "polygon") {
            $funcs["Area"]         = [
                "params" => 1,
                "type" => "float",
            ];
            $funcs["ExteriorRing"] = [
                "params" => 1,
                "type" => "linestring",
            ];
            $funcs["NumInteriorRings"] = [
                "params" => 1,
                "type" => "int",
            ];
        } else if ($geom_type == "multipolygon") {
            $funcs["Area"]     = [
                "params" => 1,
                "type" => "float",
            ];
            $funcs["Centroid"] = [
                "params" => 1,
                "type" => "point",
            ];
            // Not yet implemented in MySQL
            //$funcs["PointOnSurface"] = array("params" => 1, "type" => "point");
        } else if ($geom_type == "geometrycollection") {
            $funcs["NumGeometries"] = [
                "params" => 1,
                "type" => "int",
            ];
        }

        // If we are asked for binary functions as well
        if ($binary) {
            // section separator
            if ($display) {
                $funcs[] = ["display" => "--------"];
            }

            if (GLOBALS.getDbi().getVersion() < 50601) {
                $funcs["Crosses"]    = [
                    "params" => 2,
                    "type" => "int",
                ];
                $funcs["Contains"]   = [
                    "params" => 2,
                    "type" => "int",
                ];
                $funcs["Disjoint"]   = [
                    "params" => 2,
                    "type" => "int",
                ];
                $funcs["Equals"]     = [
                    "params" => 2,
                    "type" => "int",
                ];
                $funcs["Intersects"] = [
                    "params" => 2,
                    "type" => "int",
                ];
                $funcs["Overlaps"]   = [
                    "params" => 2,
                    "type" => "int",
                ];
                $funcs["Touches"]    = [
                    "params" => 2,
                    "type" => "int",
                ];
                $funcs["Within"]     = [
                    "params" => 2,
                    "type" => "int",
                ];
            } else {
                // If MySQl version is greater than or equal 5.6.1,
                // use the ST_ prefix.
                $funcs["ST_Crosses"]    = [
                    "params" => 2,
                    "type" => "int",
                ];
                $funcs["ST_Contains"]   = [
                    "params" => 2,
                    "type" => "int",
                ];
                $funcs["ST_Disjoint"]   = [
                    "params" => 2,
                    "type" => "int",
                ];
                $funcs["ST_Equals"]     = [
                    "params" => 2,
                    "type" => "int",
                ];
                $funcs["ST_Intersects"] = [
                    "params" => 2,
                    "type" => "int",
                ];
                $funcs["ST_Overlaps"]   = [
                    "params" => 2,
                    "type" => "int",
                ];
                $funcs["ST_Touches"]    = [
                    "params" => 2,
                    "type" => "int",
                ];
                $funcs["ST_Within"]     = [
                    "params" => 2,
                    "type" => "int",
                ];
            }

            if ($display) {
                $funcs[] = ["display" => "--------"];
            }
            // Minimum bounding rectangle functions
            $funcs["MBRContains"]   = [
                "params" => 2,
                "type" => "int",
            ];
            $funcs["MBRDisjoint"]   = [
                "params" => 2,
                "type" => "int",
            ];
            $funcs["MBREquals"]     = [
                "params" => 2,
                "type" => "int",
            ];
            $funcs["MBRIntersects"] = [
                "params" => 2,
                "type" => "int",
            ];
            $funcs["MBROverlaps"]   = [
                "params" => 2,
                "type" => "int",
            ];
            $funcs["MBRTouches"]    = [
                "params" => 2,
                "type" => "int",
            ];
            $funcs["MBRWithin"]     = [
                "params" => 2,
                "type" => "int",
            ];
        }
        return $funcs;*/
    }

    /**
     * Returns default function for a particular column.
     *
     * @param array $field       Data about the column for which
     *                           to generate the dropdown
     * @param boolean  $insert_mode Whether the operation is "insert"
     *
     * @global   array    $cfg            PMA configuration
     * @global   mixed    $data           data of currently edited row
     *                                    (used to detect whether to choose defaults)
     *
     * @return String   An HTML snippet of a dropdown list with function
     *                    names appropriate for the requested column.
     */
    public static String getDefaultFunctionForField(Map $field, boolean $insert_mode)
    {
    	return null;         //TODO
    	/*
        global $cfg, $data;

        $default_function   = "";

        // Can we get field class based values?
        $current_class = GLOBALS.getDbi().types.getTypeClass($field["True_Type"]);
        if (! empty($current_class)) {
            if (!empty($cfg["DefaultFunctions"]["FUNC_" + $current_class])) {
                $default_function
                    = $cfg["DefaultFunctions"]["FUNC_" + $current_class];
            }
        }

        // what function defined as default?
        // for the first timestamp we don"t set the default function
        // if there is a default value for the timestamp
        // (not including CURRENT_TIMESTAMP)
        // and the column does not have the
        // ON UPDATE DEFAULT TIMESTAMP attribute.
        if (($field["True_Type"] == "timestamp")
            && $field["first_timestamp"]
            && empty($field["Default"])
            && empty($data)
            && $field["Extra"] != "on update CURRENT_TIMESTAMP"
            && $field["Null"] == "NO"
        ) {
            $default_function = $cfg["DefaultFunctions"]["first_timestamp"];
        }

        // For primary keys of type char(36) or varchar(36) UUID if the default
        // function
        // Only applies to insert mode, as it would silently trash data on updates.
        if ($insert_mode
            && $field["Key"] == "PRI"
            && ($field["Type"] == "char(36)" || $field["Type"] == "varchar(36)")
        ) {
             $default_function = $cfg["DefaultFunctions"]["FUNC_UUID"];
        }

        return $default_function;*/
    }

    /**
     * Creates a dropdown box with MySQL functions for a particular column.
     *
     * @param array $field       Data about the column for which
     *                           to generate the dropdown
     * @param boolean  $insert_mode Whether the operation is "insert"
     * @param array $foreignData Foreign data
     *
     * @return String   An HTML snippet of a dropdown list with function
     *                    names appropriate for the requested column.
     */
    public static String getFunctionsForField(Map $field, boolean $insert_mode, Map $foreignData)
    {
    	return ""; //TODO
    	/*
        $default_function = getDefaultFunctionForField($field, $insert_mode);
        $dropdown_built = [];

        // Create the output
        $retval = "<option></option>" + "\n";
        // loop on the dropdown array and print all available options for that
        // field.
        $functions = GLOBALS.getDbi().types.getFunctions($field["True_Type"]);
        foreach ($functions as $function) {
            $retval += "<option";
            if (!empty($foreignData["foreign_link"]) && $foreignData["foreign_link"] !== false && $default_function === $function) {
                $retval += " selected="selected"";
            }
            $retval += ">" + $function + "</option>" + "\n";
            $dropdown_built[$function] = true;
        }

        // Create separator before all functions list
        if (count($functions) > 0) {
            $retval += "<option value='" disabled="disabled">--------</option>"
                + "\n";
        }

        // For compatibility"s sake, do not let out all other functions. Instead
        // print a separator (blank) and then show ALL functions which weren"t
        // shown yet.
        $functions = GLOBALS.getDbi().types.getAllFunctions();
        foreach ($functions as $function) {
            // Skip already included functions
            if (!empty($dropdown_built[$function])) {
                continue;
            }
            $retval += "<option";
            if ($default_function === $function) {
                $retval += " selected="selected"";
            }
            $retval += ">" + $function + "</option>" + "\n";
        } // end for

        return $retval;*/
    } // end getFunctionsForField()

    /**
     * Checks if the current user has a specific privilege and returns true if the
     * user indeed has that privilege or false if they don"t. This function must
     * only be used for features that are available since MySQL 5, because it
     * relies on the INFORMATION_SCHEMA database to be present.
     *
     * Example:   currentUserHasPrivilege("CREATE ROUTINE", "mydb");
     *            // Checks if the currently logged in user has the global
     *            // "CREATE ROUTINE" privilege or, if not, checks if the
     *            // user has this privilege on database "mydb".
     *
     * @param String $priv The privilege to check
     * @param mixed  $db   null, to only check global privileges
     *                     String, db name where to also check for privileges
     * @param mixed  $tbl  null, to only check global/db privileges
     *                     String, table name where to also check for privileges
     *
     * @return boolean
     */
    public static boolean currentUserHasPrivilege(String $priv, String $db /*= null*/, String $tbl /*= null*/)
    {
    	return true; //TODO
    	/*
        // Get the username for the current user in the format
        // required to use in the information schema database.
        list($user, $host) = GLOBALS.getDbi().getCurrentUserAndHost();

        if ($user === "") { // MySQL is started with --skip-grant-tables
            return true;
        }

        $username  = "'"";
        $username += str_replace("'", "'"", $user);
        $username += "'"@"'";
        $username += str_replace("'", "'"", $host);
        $username += "'"";

        // Prepare the query
        $query = "SELECT `PRIVILEGE_TYPE` FROM `INFORMATION_SCHEMA`.`%s` "
               + "WHERE GRANTEE="%s" AND PRIVILEGE_TYPE="%s"";

        // Check global privileges first.
        $user_privileges = GLOBALS.getDbi().fetchValue(
            sprintf(
                $query,
                "USER_PRIVILEGES",
                $username,
                $priv
            )
        );
        if ($user_privileges) {
            return true;
        }
        // If a database name was provided and user does not have the
        // required global privilege, try database-wise permissions.
        if ($db !== null) {
            $query += " AND "%s" LIKE `TABLE_SCHEMA`";
            $schema_privileges = GLOBALS.getDbi().fetchValue(
                sprintf(
                    $query,
                    "SCHEMA_PRIVILEGES",
                    $username,
                    $priv,
                    GLOBALS.getDbi().escapeString($db)
                )
            );
            if ($schema_privileges) {
                return true;
            }
        } else {
            // There was no database name provided and the user
            // does not have the correct global privilege.
            return false;
        }
        // If a table name was also provided and we still didn"t
        // find any valid privileges, try table-wise privileges.
        if ($tbl !== null) {
            // need to escape wildcards in db and table names, see bug #3518484
            $tbl = str_replace(["%", "_"], ["\%", "\_"], $tbl);
            $query += " AND TABLE_NAME="%s"";
            $table_privileges = GLOBALS.getDbi().fetchValue(
                sprintf(
                    $query,
                    "TABLE_PRIVILEGES",
                    $username,
                    $priv,
                    GLOBALS.getDbi().escapeString($db),
                    GLOBALS.getDbi().escapeString($tbl)
                )
            );
            if ($table_privileges) {
                return true;
            }
        }
        // If we reached this point, the user does not
        // have even valid table-wise privileges.
        return false;*/
    }

    /**
     * Returns server type for current connection
     *
     * Known types are: MariaDB, Percona and MySQL (default)
     *
     * @return String
     */
    public static String getServerType()
    {
    	return null; //Unsupported
    	/*
        if (GLOBALS.getDbi().isMariaDB()) {
            return "MariaDB";
        }

        if (GLOBALS.getDbi().isPercona()) {
            return "Percona Server";
        }

        return "MySQL";*/
    }

    /**
     * Returns information about SSL status for current connection
     *
     * @return String
     */
    public static String getServerSSL()
    {
    	return null; // Unsupported
    	/*
        $server = Globals.getConfig()["Server"];
        $class = "caution";
        if (! $server["ssl"]) {
            $message = __("SSL is not being used");
            if (! empty($server["socket"]) || $server["host"] == "127.0.0.1" || $server["host"] == "localhost") {
                $class = "";
            }
        } else if (! $server["ssl_verify"]) {
            $message = __("SSL is used with disabled verification");
        } else if (empty($server["ssl_ca"])) {
            $message = __("SSL is used without certification authority");
        } else {
            $class = "";
            $message = __("SSL is used");
        }
        return "<span class='" + $class + "'>" + $message + "</span> " + showDocu("setup", "ssl");*/
    }

    /**
     * Parses ENUM/SET values
     *
     * @param String $definition The definition of the column
     *                           for which to parse the values
     * @param boolean   $escapeHtml Whether to escape html entities
     *
     * @return array
     */
    public static Map parseEnumSetValues(String $definition, boolean $escapeHtml /*= true*/)
    {
    	return null; //TODO
    	/*
        $values_string = htmlentities($definition, ENT_COMPAT, "UTF-8");
        // There is a JS port of the below parser in functions.js
        // If you are fixing something here,
        // you need to also update the JS port.
        $values = [];
        $in_string = false;
        $buffer = "";

        for ($i = 0, $length = mb_strlen($values_string); $i < $length; $i++) {
            $curr = mb_substr($values_string, $i, 1);
            $next = ($i == mb_strlen($values_string) - 1)
                ? ""
                : mb_substr($values_string, $i + 1, 1);

            if (! $in_string && $curr == "'") {
                $in_string = true;
            } else if (($in_string && $curr == "\\") && $next == "\\") {
                $buffer += "&#92;";
                $i++;
            } else if (($in_string && $next == "'")
                && ($curr == "'" || $curr == "\\")
            ) {
                $buffer += "&#39;";
                $i++;
            } else if ($in_string && $curr == "'") {
                $in_string = false;
                $values[] = $buffer;
                $buffer = "";
            } else if ($in_string) {
                 $buffer += $curr;
            }
        }

        if (strlen($buffer) > 0) {
            // The leftovers in the buffer are the last value (if any)
            $values[] = $buffer;
        }

        if (! $escapeHtml) {
            foreach ($values as $key => $value) {
                $values[$key] = html_entity_decode($value, ENT_QUOTES, "UTF-8");
            }
        }

        return $values;*/
    }

    /**
     * Get regular expression which occur first inside the given sql query.
     *
     * @param array  $regex_array Comparing regular expressions.
     * @param String $query       SQL query to be checked.
     *
     * @return String Matching regular expression.
     */
    public static String getFirstOccurringRegularExpression(List<String> $regex_array, String $query)
    {
    	return null; //TODO
    	/*
        $minimum_first_occurence_index = null;
        $regex = null;

        for (String $test_regex : $regex_array ) {
            if (preg_match($test_regex, $query, $matches, PREG_OFFSET_CAPTURE)) {
                if ($minimum_first_occurence_index === null
                    || ($matches[0][1] < $minimum_first_occurence_index)
                ) {
                    $regex = $test_regex;
                    $minimum_first_occurence_index = $matches[0][1];
                }
            }
        }
        return $regex;*/
    }

    /**
     * Return the list of tabs for the menu with corresponding names
     *
     * @param String $level "server", "db" or "table" level
     *
     * @return array|null list of tabs for the menu
     */
    public static Map getMenuTabList(String $level /*= null*/)
    {
        Map<String, Map<String,String>> $tabList = new HashMap<>(); 
        
        Map<String,String> server = new HashMap<>();
        server.put("databases", __("Databases"));
        server.put("sql", __("SQL"));
        server.put("status", __("Status"));
        server.put("rights", __("Users"));
        server.put("export", __("Export"));
        server.put("export", __("Export"));
        server.put("settings", __("Settings"));
        server.put("binlog", __("Binary log"));
        server.put("replication", __("Replication"));
        server.put("vars", __("Variables"));
        server.put("charset", __("Charsets"));
        server.put("plugins", __("Plugins"));
        server.put("engine", __("Engines"));
        $tabList.put("server", server);
        
        Map<String,String> db = new HashMap<>();
        db.put("structure", __("Structure"));
        db.put("sql", __("SQL"));
        db.put("search", __("Search"));
        db.put("query", __("Query"));
        db.put("structure", __("Structure"));
        db.put("export", __("Export"));
        db.put("import", __("Import"));
        db.put("operation", __("Operations"));
        db.put("privileges", __("Privileges"));
        db.put("routines", __("Routines"));
        db.put("events", __("Events"));
        db.put("triggers", __("Triggers"));
        db.put("tracking", __("Tracking"));
        db.put("designer", __("Designer"));
        db.put("central_columns", __("Central columns"));
        $tabList.put("db", db);
        
        Map<String,String> table = new HashMap<>();
        table.put("browse", __("Browse"));
        table.put("structure", __("Structure"));
        table.put("sql", __("SQL"));
        table.put("search", __("Search"));
        table.put("insert", __("Insert"));
        db.put("export", __("Export"));
        db.put("import", __("Import"));
        db.put("privileges", __("Privileges"));
        db.put("operation", __("Operations"));
        db.put("tracking", __("Tracking"));
        db.put("triggers", __("Triggers"));
        $tabList.put("table", table);
        
        if ($level == null) {
            return $tabList;
        } else if ($tabList.containsKey($level)) {
            return $tabList.get($level);
        }

        return null;
    }

    /**
     * Add fractional seconds to time, datetime and timestamp strings.
     * If the String contains fractional seconds,
     * pads it with 0s up to 6 decimal places.
     *
     * @param String $value time, datetime or timestamp strings
     *
     * @return String time, datetime or timestamp strings with fractional seconds
     */
    public static String addMicroseconds(String $value)
    {
        if (empty($value) || $value == "CURRENT_TIMESTAMP"
            || $value == "current_timestamp()") {
            return $value;
        }

        if (!$value.contains( ".") ) {
            return $value + ".000000";
        }

        $value += "000000";
        return $value.substring(
            0,
            $value.indexOf( ".") + 7
        );
    }

    /**
     * Reads the file, detects the compression MIME type, closes the file
     * and returns the MIME type
     *
     * @param resource $file the file handle
     *
     * @return String the MIME type for compression, or "none"
     * @throws IOException 
     */
    public static String getCompressionMimeType(File $file) throws IOException
    {
    	FileReader fw = new FileReader($file);
    	char[] $buffer = new char[4];
    	fw.read($buffer, 0, 4);
        String $test = new String($buffer);
        int $len = $test.length();
        fw.close();
        if ($len >= 2 && $buffer[0] == (char)31 && $buffer[1] == (char)139) {
            return "application/gzip";
        }
        if ($len >= 3 && $test.substring(0, 3) == "BZh") {
            return "application/bzip2";
        }
        if ($len >= 4 && $test == "PK\003\004") {
            return "application/zip";
        }
        return "none";
    }

    /**
     * Renders a single link for the top of the navigation panel
     *
     * @param String  $link        The url for the link
     * @param boolean    $showText    Whether to show the text or to
     *                             only use it for title attributes
     * @param String  $text        The text to display and use for title attributes
     * @param boolean    $showIcon    Whether to show the icon
     * @param String  $icon        The filename of the icon to show
     * @param String  $linkId      Value to use for the ID attribute
     * @param boolean $disableAjax Whether to disable ajax page loading for this link
     * @param String  $linkTarget  The name of the target frame for the link
     * @param array   $classes     HTML classes to apply
     *
     * @return String HTML code for one link
     */
    public static String getNavigationLink(
    	String $link,
        boolean $showText,
        String $text,
        boolean $showIcon,
        String $icon,
        String $linkId /*= ""*/,
        boolean $disableAjax /*= false*/,
        String $linkTarget /*= ""*/,
        List<String> $classes /*= []*/
    ) {
        String $retval = "<a href='" + $link + "'";
        if (! empty($linkId)) {
            $retval += " id='" + $linkId + "'";
        }
        if (! empty($linkTarget)) {
            $retval += " target='" + $linkTarget + "'";
        }
        if ($classes == null) {
            $classes = new ArrayList<>();
        }
        if ($disableAjax) {
            $classes.add( "disableAjax" );
        }
        if (! empty($classes)) {
            $retval += " class='" + String.join(" ", $classes) + "'";
        }
        $retval += " title='" + $text + "'>";
        if ($showIcon) {
            $retval += getImage(
                $icon,
                $text,
                null
            );
        }
        if ($showText) {
            $retval += $text;
        }
        $retval += "</a>";
        if ($showText) {
            $retval += "<br>";
        }
        return $retval;
    }

    public static String getNavigationLink(
        	String $link,
            boolean $showText,
            String $text,
            boolean $showIcon,
            String $icon,
            String $linkId) {
    	return getNavigationLink($link,
                $showText,
                $text,
                $showIcon,
                $icon,
                $linkId, false, "", new ArrayList<>());
    }
    
    /**
     * Provide COLLATE clause, if required, to perform case sensitive comparisons
     * for queries on information_schema.
     *
     * @return String COLLATE clause if needed or empty String.
     */
    public static String getCollateForIS()
    {
    	return ""; //TODO
    	/*
        $names = GLOBALS.getDbi().getLowerCaseNames();
        if ($names === "0") {
            return "COLLATE utf8_bin";
        } else if ($names === "2") {
            return "COLLATE utf8_general_ci";
        }
        return "";*/
    }

    /**
     * Process the index data.
     *
     * @param array $indexes index data
     *
     * @return array processes index data
     */
    public static Map processIndexData(Map $indexes)
    {
    	return null; // TODO
    	/*
        $lastIndex    = "";

        $primary      = "";
        $pk_array     = []; // will be use to emphasis prim. keys in the table
        $indexes_info = [];
        $indexes_data = [];

        // view
        foreach ($indexes as $row) {
            // Backups the list of primary keys
            if ($row["Key_name"] == "PRIMARY") {
                $primary   += $row["Column_name"] + ", ";
                $pk_array[$row["Column_name"]] = 1;
            }
            // Retains keys informations
            if ($row["Key_name"] != $lastIndex) {
                $indexes[] = $row["Key_name"];
                $lastIndex = $row["Key_name"];
            }
            $indexes_info[$row["Key_name"]]["Sequences"][] = $row["Seq_in_index"];
            $indexes_info[$row["Key_name"]]["Non_unique"] = $row["Non_unique"];
            if (!empty($row["Cardinality"])) {
                $indexes_info[$row["Key_name"]]["Cardinality"] = $row["Cardinality"];
            }
            // I don"t know what does following column mean....
            // $indexes_info[$row["Key_name"]]["Packed"]          = $row["Packed"];

            $indexes_info[$row["Key_name"]]["Comment"] = $row["Comment"];

            $indexes_data[$row["Key_name"]][$row["Seq_in_index"]]["Column_name"]
                = $row["Column_name"];
            if (!empty($row["Sub_part"])) {
                $indexes_data[$row["Key_name"]][$row["Seq_in_index"]]["Sub_part"]
                    = $row["Sub_part"];
            }
        } // end while

        return [
            $primary,
            $pk_array,
            $indexes_info,
            $indexes_data,
        ];*/
    }

    /**
     * Function to get html for the start row and number of rows panel
     *
     * @param String $sql_query sql query
     *
     * @return String html
     */
    public static String getStartAndNumberOfRowsPanel(String $sql_query, HttpServletRequest request, SessionMap session)
    {
    	Integer $rows;
        if (!empty(request.getParameter("session_max_rows"))) {
            $rows = new Integer( (String) request.getParameter("session_max_rows"));
        } else if (!empty(multiget(session,"tmpval","max_rows"))
                    && multiget(session,"tmpval","max_rows") != "all"
        ) {
            $rows = new Integer( (String) multiget(session,"tmpval","max_rows"));
        } else {
            $rows = new Integer( (String) Globals.getConfig().get("MaxRows"));
            multiput(session,"tmpval","max_rows", $rows.toString());
        }

        Integer $pos;
        if (!empty(request.getParameter("pos"))) {
            $pos = new Integer(request.getParameter("pos"));
        } else if (!empty(multiget(session,"tmpval","pos"))) {
            $pos = new Integer( (String) multiget(session,"tmpval","pos"));
        } else {
            int $number_of_line = new Integer((String)request.getParameter("unlim_num_rows"));
            $pos = (int) ((Math.ceil($number_of_line / $rows) - 1) * $rows);
            multiput(session,"tmpval","pos", $pos.toString());
        }

        Map<String,Object> model = new HashMap<>();
        model.put("pos" , $pos);
        model.put("unlim_num_rows" , request.getParameter("unlim_num_rows"));
        model.put("rows" , $rows);
        model.put("sql_query" , $sql_query);
        
        return JtwigFactory.render("start_and_number_of_rows_panel", model);
    }

    /**
     * Returns whether the database server supports virtual columns
     *
     * @return boolean
     */
    public static boolean isVirtualColumnsSupported()
    {
    	return false; // TODO
    	/*
        $serverType = getServerType();
        $serverVersion = GLOBALS.getDbi().getVersion();
        return in_array($serverType, ["MySQL", "Percona Server"]) && $serverVersion >= 50705
             || ($serverType == "MariaDB" && $serverVersion >= 50200);*/
    }

    private static Map<String, String> $sortable_name_mappings = new HashMap<>();
    static {
		 $sortable_name_mappings.put("table"      , "Name");
		 $sortable_name_mappings.put("records"    , "Rows");
		 $sortable_name_mappings.put("type"       , "Engine");
		 $sortable_name_mappings.put("collation"  , "Collation");
		 $sortable_name_mappings.put("size"       , "Data_length");
		 $sortable_name_mappings.put("overhead"   , "Data_free");
		 $sortable_name_mappings.put("creation"   , "Create_time");
		 $sortable_name_mappings.put("last_update", "Update_time");
		 $sortable_name_mappings.put("last_check" , "Check_time");
		 $sortable_name_mappings.put("comment"    , "Comment");
    }

    /**
     * Gets the list of tables in the current db and information about these
     * tables if possible
     *
     * @param String      $db       database name
     * @param String|null $sub_part part of script name
     *
     * @return array
     * @throws SQLException 
     *
     */
    public static Object[] getDbInfo(String $db, String $sub_part, HttpServletRequest request, Globals GLOBALS, SessionMap $_SESSION) throws SQLException
    {
    	//ResultSet metadata = this._links.get($link).getMetaData()
		//		.getTables($catalogName, $database, null, new String[] {"TABLE"});
    	
        Config $cfg = Globals.getConfig();

        // limits for table list
         
        if (empty(multiget($_SESSION, "tmpval", "table_limit_offset"))
            || ($db!= null && !$db.equals(multiget($_SESSION, "tmpval", "table_limit_offset_db")))
        ) {
            multiput($_SESSION, "tmpval", "table_limit_offset", 0);
            multiput($_SESSION, "tmpval", "table_limit_offset_db", $db);
        }
        if (!empty(request.getParameter("pos"))) {
            multiput($_SESSION, "tmpval", "table_limit_offset", new Integer(request.getParameter("pos")));
        }
        Integer $pos = (Integer) multiget($_SESSION, "tmpval", "table_limit_offset");

        /**
         * whether to display extended stats
         */
        boolean $is_show_stats = "true".equals($cfg.get("ShowStats"));

        /**
         * whether selected db is information_schema
         */
        boolean $db_is_system_schema = false;

        if (GLOBALS.getDbi().isSystemSchema($db)) {
            $is_show_stats = false;
            $db_is_system_schema = true;
        }

        /**
         * information about tables in db
         */
        Map<Integer, Map<String,String>> $tables = new HashMap<>();

        Map $tooltip_truename = new HashMap();
        Map $tooltip_aliasname = new HashMap();

        
        // Set some sorting defaults
        String $sort = "Name";
        String $sort_order = "ASC";

        if (!empty(request.getParameter("sort"))) {

            // Make sure the sort type is implemented
            if (!empty($sortable_name_mappings.get(request.getParameter("sort")))) {
                $sort = $sortable_name_mappings.get(request.getParameter("sort"));
                if ("DESC".equals(request.getParameter("sort_order"))) {
                    $sort_order = "DESC";
                }
            }
        }

        String $groupWithSeparator = null;
        String $tbl_type = null;
        int $limit_offset = 0;
        Integer $limit_count = null;
        Map<Integer, Map<String,String>> $groupTable = new HashMap<>();
        Integer $total_num_tables = null;
        
        if (! empty(request.getParameter("tbl_group")) || ! empty(request.getParameter("tbl_type"))) {
            if (! empty(request.getParameter("tbl_type"))) {
                // only tables for selected type
                $tbl_type = request.getParameter("tbl_type");
            }
            if (! empty(request.getParameter("tbl_group"))) {
                // only tables for selected group
                String $tbl_group = request.getParameter("tbl_group");
                // include the table with the exact name of the group if such
                // exists
                $groupTable = GLOBALS.getDbi().getTablesFull(
                    $db,
                    $tbl_group,
                    false,
                    $limit_offset,
                    $limit_count,
                    $sort,
                    $sort_order,
                    $tbl_type
                );
                $groupWithSeparator = $tbl_group
                    + Globals.getConfig().get("NavigationTreeTableSeparator");
            }
        } else {
            // all tables in db
            // - get the total number of tables
            //  (needed for proper working of the MaxTableList feature)
            $tables = GLOBALS.getDbi().getTables($db);
            $total_num_tables = $tables.size();
            if (! (!empty($sub_part) && $sub_part == "_export")) {
                // fetch the details for a possible limited subset
                $limit_offset = $pos;
                $limit_count = null;
            }
        }
        $tables = array_merge(
            $groupTable,
            GLOBALS.getDbi().getTablesFull(
                $db,
                $groupWithSeparator,
                $groupWithSeparator != null,
                $limit_offset,
                $limit_count,
                $sort,
                $sort_order,
                $tbl_type
            )
        );
    

        int $num_tables = $tables.size();
        //  (needed for proper working of the MaxTableList feature)
        if (! !empty($total_num_tables)) {
            $total_num_tables = $num_tables;
        }

        // If coming from a Show MySQL link on the home page,
        // put something in $sub_part
        if (empty($sub_part)) {
            $sub_part = "_structure";
        }

        return new Object[] {
            $tables,
            $num_tables,
            $total_num_tables,
            $sub_part,
            $is_show_stats,
            $db_is_system_schema,
            $tooltip_truename,
            $tooltip_aliasname,
            $pos,
        };
    }

    /**
     * Gets the list of tables in the current db, taking into account
     * that they might be "in use"
     *
     * @param String $db             database name
     * @param object $db_info_result result set
     *
     * @return array list of tables
     *
     */
    public static List getTablesWhenOpen(String $db, Object $db_info_result)
    {
    	return new ArrayList<>(); //TODO
    	/*
        $sot_cache = [];
        $tables = [];

        while ($tmp = GLOBALS.getDbi().fetchAssoc($db_info_result)) {
            $sot_cache[$tmp["Table"]] = true;
        }
        GLOBALS.getDbi().freeResult($db_info_result);

        // is there at least one "in use" table?
        if (count($sot_cache) > 0) {
            $tblGroupSql = "";
            $whereAdded = false;
            if (Core.isValid($_REQUEST["tbl_group"])) {
                $group = escapeMysqlWildcards($_REQUEST["tbl_group"]);
                $groupWithSeparator = escapeMysqlWildcards(
                    $_REQUEST["tbl_group"]
                    + Globals.getConfig()["NavigationTreeTableSeparator"]
                );
                $tblGroupSql += " WHERE ("
                    + backquote("Tables_in_" + $db)
                    + " LIKE "" + $groupWithSeparator + "%""
                    + " OR "
                    + backquote("Tables_in_" + $db)
                    + " LIKE "" + $group + "")";
                $whereAdded = true;
            }
            if (Core.isValid($_REQUEST["tbl_type"], ["table", "view"])) {
                $tblGroupSql += $whereAdded ? " AND" : " WHERE";
                if ($_REQUEST["tbl_type"] == "view") {
                    $tblGroupSql += " `Table_type` NOT IN ("BASE TABLE", "SYSTEM VERSIONED")";
                } else {
                    $tblGroupSql += " `Table_type` IN ("BASE TABLE", "SYSTEM VERSIONED")";
                }
            }
            $db_info_result = GLOBALS.getDbi().query(
                "SHOW FULL TABLES FROM " + backquote($db) + $tblGroupSql,
                DatabaseInterface.CONNECT_USER,
                DatabaseInterface.QUERY_STORE
            );
            unset($tblGroupSql, $whereAdded);

            if ($db_info_result && GLOBALS.getDbi().numRows($db_info_result) > 0) {
                $names = [];
                while ($tmp = GLOBALS.getDbi().fetchRow($db_info_result)) {
                    if (! !empty($sot_cache[$tmp[0]])) {
                        $names[] = $tmp[0];
                    } else { // table in use
                        $tables[$tmp[0]] = [
                            "TABLE_NAME" => $tmp[0],
                            "ENGINE" => "",
                            "TABLE_TYPE" => "",
                            "TABLE_ROWS" => 0,
                            "TABLE_COMMENT" => "",
                        ];
                    }
                } // end while
                if (count($names) > 0) {
                    $tables = array_merge(
                        $tables,
                        GLOBALS.getDbi().getTablesFull($db, $names)
                    );
                }
                if (Globals.getConfig()["NaturalOrder"]) {
                    uksort($tables, "strnatcasecmp");
                }
            } else if ($db_info_result) {
                GLOBALS.getDbi().freeResult($db_info_result);
            }
            unset($sot_cache);
        }
        return $tables;*/
    }

    /**
     * Returs list of used PHP extensions.
     *
     * @return array of strings
     */
    public static List listPHPExtensions()
    {
    	return new ArrayList(); //Unsupported
    }

    /**
     * Converts given (request) paramter to String
     *
     * @param mixed $value Value to convert
     *
     * @return String
     */
    public static String requestString(Object $value)
    {
    	return $value.toString(); //TODO
    	/*
        while (is_array($value) || is_object($value)) {
            $value = reset($value);
        }
        return trim((String) $value);*/
    }

    /**
     * Generates random String consisting of ASCII chars
     *
     * @param integer $length Length of String
     * @param boolean    $asHex  (optional) Send the result as hex
     *
     * @return String
     * 
     * @see https://www.geeksforgeeks.org/generate-random-string-of-given-size-in-java/
     */
    public static String generateRandom(int $length /*, boolean $asHex = false*/)
    {
        // chose a Character random from this String 
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                                    + "0123456789"
                                    + "abcdefghijklmnopqrstuvxyz"; 
  
        // create StringBuffer size of AlphaNumericString 
        StringBuilder sb = new StringBuilder($length); 
  
        for (int i = 0; i < $length; i++) { 
  
            // generate a random number between 
            // 0 to AlphaNumericString variable length 
            int index 
                = (int)(AlphaNumericString.length() 
                        * Math.random()); 
  
            // add Character one by one in end of sb 
            sb.append(AlphaNumericString 
                          .charAt(index)); 
        } 
  
        return sb.toString(); 
    }

    /**
     * Wrapper around php"s set_time_limit
     *
     * @return void
     */
    public static void setTimeLimit()
    {
    	//Unsupported
        // The function can be disabled in php.ini
        /*if (function_exists("set_time_limit")) {
            @set_time_limit(Globals.getConfig()["ExecTimeLimit"]);
        }*/
    }

    /**
     * Access to a multidimensional array by dot notation
     *
     * @param array        $array   List of values
     * @param String|array $path    Path to searched value
     * @param mixed        $default Default value
     *
     * @return mixed Searched value
     */
    public static Object getValueByKey(Map $array, String[] $path, Object $default /*= null*/)
    {
        Object $value = multiget($array, $path);
        return !empty($value) ? $value : $default;
    }
    
    public static Object getValueByKey(Map $array, String $path, Object $default /*= null*/)  {
    	return getValueByKey($array, $path.split("\\."), $default);
    }

    /**
     * Creates a clickable column header for table information
     *
     * @param String $title            Title to use for the link
     * @param String $sort             Corresponds to sortable data name mapped
     *                                 in Util.getDbInfo
     * @param String $initialSortOrder Initial sort order
     *
     * @return String Link to be displayed in the table header
     */
    public static String sortableTableHeader(String $title, String $sort, String $initialSortOrder /*= "ASC"*/)
    {
    	return null; //TODO
    	/*
        $requestedSort = "table";
        $requestedSortOrder = $futureSortOrder = $initialSortOrder;
        // If the user requested a sort
        if (!empty($_REQUEST["sort"])) {
            $requestedSort = $_REQUEST["sort"];
            if (!empty($_REQUEST["sort_order"])) {
                $requestedSortOrder = $_REQUEST["sort_order"];
            }
        }
        $orderImg = "";
        $orderLinkParams = [];
        $orderLinkParams["title"] = __("Sort");
        // If this column was requested to be sorted.
        if ($requestedSort == $sort) {
            if ($requestedSortOrder == "ASC") {
                $futureSortOrder = "DESC";
                // current sort order is ASC
                $orderImg = " " + getImage(
                    "s_asc",
                    __("Ascending"),
                    [
                        "class" => "sort_arrow",
                        "title" => "",
                    ]
                );
                $orderImg += " " + getImage(
                    "s_desc",
                    __("Descending"),
                    [
                        "class" => "sort_arrow hide",
                        "title" => "",
                    ]
                );
                // but on mouse over, show the reverse order (DESC)
                $orderLinkParams["onmouseover"] = "$(".sort_arrow").toggle();";
                // on mouse out, show current sort order (ASC)
                $orderLinkParams["onmouseout"] = "$(".sort_arrow").toggle();";
            } else {
                $futureSortOrder = "ASC";
                // current sort order is DESC
                $orderImg = " " + getImage(
                    "s_asc",
                    __("Ascending"),
                    [
                        "class" => "sort_arrow hide",
                        "title" => "",
                    ]
                );
                $orderImg += " " + getImage(
                    "s_desc",
                    __("Descending"),
                    [
                        "class" => "sort_arrow",
                        "title" => "",
                    ]
                );
                // but on mouse over, show the reverse order (ASC)
                $orderLinkParams["onmouseover"] = "$(".sort_arrow").toggle();";
                // on mouse out, show current sort order (DESC)
                $orderLinkParams["onmouseout"] = "$(".sort_arrow").toggle();";
            }
        }
        $urlParams = [
            "db" => $_REQUEST["db"],
            "pos" => 0, // We set the position back to 0 every time they sort.
            "sort" => $sort,
            "sort_order" => $futureSortOrder,
        ];

        if (Core.isValid($_REQUEST["tbl_type"], ["view", "table"])) {
            $urlParams["tbl_type"] = $_REQUEST["tbl_type"];
        }
        if (! empty($_REQUEST["tbl_group"])) {
            $urlParams["tbl_group"] = $_REQUEST["tbl_group"];
        }

        $url = Url.getFromRoute("/database/structure", $urlParams);

        return linkOrButton($url, $title + $orderImg, $orderLinkParams);*/
    }

    public static String sortableTableHeader(String $title, String $sort) {
    	return sortableTableHeader($title, $sort, "ASC");
    }
    
    /**
     * Check that input is an int or an int in a String
     *
     * @param mixed $input input to check
     *
     * @return boolean
     */
    public static boolean isInteger(Object $input)
    {
    	if ($input instanceof String) {
    		try {
    			new Long((String)$input);
    			return true;
    		} catch(NumberFormatException exc) {
    			return false;
    		}
    	}
    	return $input instanceof Integer || $input instanceof Long;
    }

	public static String getListNavigator(int totalNumTables, int position, Map<String, Object> $urlParams,
			String fromRoute, String string, Object object) {
		// TODO Auto-generated method stub
		return null;
	}

}
