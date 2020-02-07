package org.javamyadmin.helpers;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javamyadmin.php.Globals;
import org.springframework.beans.factory.annotation.Autowired;

import static org.javamyadmin.php.Php.*;

/**
 * Class used to output the console
 *
 * @package PhpMyAdmin
 */
public class Console {


    /**
     * Whether to display anything
     *
     * @access private
     * @var bool
     */
    private boolean _isEnabled;

    /**
     * Whether we are servicing an ajax request.
     *
     * @access private
     * @var bool
     */
    private boolean _isAjax;

    /**
     * @var Relation
     */
    // TODO ? private Relation relation;

    /**
     * @var Template
     */
    @Autowired
    public Template template;

    @Autowired
    private Globals GLOBALS;
    @Autowired
    private Config config;
	@Autowired
	private Util util;
    
    /**
     * Creates a new class instance
     */
    public Console()
    {
        this._isEnabled = true;
        //this.relation = new Relation($GLOBALS['dbi']);
    }

    /**
     * Set the ajax flag to indicate whether
     * we are servicing an ajax request
     *
     * @param bool $isAjax Whether we are servicing an ajax request
     *
     * @return void
     */
    public void setAjax(boolean $isAjax)
    {
        this._isAjax = $isAjax;
    }

    /**
     * Disables the rendering of the footer
     *
     * @return void
     */
    public void disable()
    {
        this._isEnabled = false;
    }

    /**
     * Renders the bookmark content
     *
     * @access public
     * @return string
     * @throws SQLException 
     */
    public String getBookmarkContent(Globals GLOBALS) throws SQLException
    {
        Template $template = new Template();
        Map<String, Object> $cfgBookmark = Bookmark.getParams((String) multiget(config.settings, "Server", "user"));
        if (!empty($cfgBookmark)) {
            List<Bookmark> $bookmarks = Bookmark.getList(
                GLOBALS.getDbi(),
                (String)multiget(config.settings, "Server", "user")
            );
            int $count_bookmarks = $bookmarks.size();
            String $welcomeMessage;
            if ($count_bookmarks > 0) {
                $welcomeMessage = String.format(
                    _ngettext(
                        "Showing %1$d bookmark (both private and shared)",
                        "Showing %1$d bookmarks (both private and shared)",
                        $count_bookmarks
                    ),
                    $count_bookmarks
                );
            } else {
                $welcomeMessage = __("No bookmarks");
            }
            // TODO? unset($private_message, $shared_message);
            
            Map<String, Object> model = new HashMap<>();
            model.put("welcome_message", $welcomeMessage);
            model.put("bookmarks", $bookmarks);
            return $template.render("console/bookmark_content", model);
        }
        return "";
    }

    private String[] scripts = new String[]{
    	"console.js"
    };
    
    /**
     * Returns the list of JS scripts required by console
     *
     * @return array list of scripts
     */
    public List<String> getScripts()
    {
        return Arrays.asList(scripts);
    }

    /**
     * Renders the console
     *
     * @access public
     * @return string
     * @throws SQLException 
     */
    public String getDisplay() throws SQLException
    {
        if ((! this._isAjax) && this._isEnabled) {
            Map<String, Object> $cfgBookmark = Bookmark.getParams(
            	(String) multiget(config.settings, "Server", "user")
            );

            String $image = util.getImage("console", __("SQL Query Console"));
            String $_sql_history = ""; /* TODO = this.relation.getHistory(
            	multiget(Globals.getConfig().settings, "Server", "user")
            );*/
            String $bookmarkContent = getBookmarkContent(GLOBALS);

            Map<String, Object> model = new HashMap<>();
            model.put("cfg_bookmark", $cfgBookmark);
            model.put("image", $image);
            model.put("sql_history", $_sql_history);
            model.put("bookmark_content", $bookmarkContent);
            return this.template.render("console/display", model);
        }
        return "";
    }
}
