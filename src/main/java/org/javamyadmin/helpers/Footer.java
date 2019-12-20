package org.javamyadmin.helpers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javamyadmin.helpers.Scripts.FStruct2;
import org.javamyadmin.helpers.html.Generator;
import org.javamyadmin.jtwig.JtwigFactory;
import org.javamyadmin.php.Globals;
import static org.javamyadmin.php.Php.*;

/**
 * Class used to output the footer
 *
 * @package PhpMyAdmin
 */
public class Footer {
	   /**
     * Scripts instance
     *
     * @access private
     * @var Scripts
     */
    private Scripts _scripts;
    /**
     * Whether we are servicing an ajax request.
     *
     * @access private
     * @var boolean
     */
    private boolean _isAjax;
    /**
     * Whether to only close the BODY and HTML tags
     * or also include scripts, errors and links
     *
     * @access private
     * @var boolean
     */
    private boolean _isMinimal;
    /**
     * Whether to display anything
     *
     * @access private
     * @var boolean
     */
    private boolean _isEnabled;

    /**
     * @var Relation
     */
    // TODO private Relation relation;

    /**
     * @var Template
     */
    //private Template template;

    private Globals GLOBALS;
	private Response pmaResponse;
    
    /**
     * Creates a new class instance
     */
    public Footer(Globals GLOBALS, Response pmaResponse)
    {
        this._isEnabled = true;
        this._scripts = new Scripts(GLOBALS);
        this._isMinimal = false;
        // TODO this.relation = new Relation(GLOBALS.dbi);
        
        this.GLOBALS = GLOBALS;
        this.pmaResponse = pmaResponse;
    }

    /**
     * Returns the message for demo server to error messages
     *
     * @return String
     */
    private String _getDemoMessage()
    {
    	return ""; // TODO
        /*String $message = "<a href="/">" + __("phpMyAdmin Demo Server") + "</a>: ";
        if (@file_exists(ROOT_PATH + "revision-info.php")) {
            $revision = "";
            $fullrevision = "";
            $repobase = "";
            $repobranchbase = "";
            $branch = "";
            include ROOT_PATH . "revision-info.php";
            $message += sprintf(
                __("Currently running Git revision %1$s from the %2$s branch."),
                "<a target="_blank" rel="noopener noreferrer" href="" + $repobase + $fullrevision + "">"
                . $revision . "</a>",
                "<a target="_blank" rel="noopener noreferrer" href="" + $repobranchbase + $branch + "">"
                . $branch . "</a>"
            );
        } else {
            $message += __("Git information missing!");
        }

        return Message.notice($message).getDisplay();*/
    }

    /**
     * Remove recursions and iterator objects from an object
     *
     * @param object|array $object Object to clean
     * @param array        $stack  Stack used to keep track of recursion,
     *                             need not be passed for the first time
     *
     * @return object Reference passed object
     */
    private static Object _removeRecursion(Object $object, List<Object> $stack /*= []*/)
    {
        // TODO (original impl was by reference)
        return $object;
    }

    /**
     * Renders the debug messages
     *
     * @return String
     */
    public String getDebugMessage()
    {
    	return ""; /*
        String $retval = "\"null\"";
        if ($GLOBALS.cfg.get("DBG.sql")
            && empty($_REQUEST["no_debug"])
            && ! empty($_SESSION["debug"])
        ) {
            // Remove recursions and iterators from $_SESSION["debug"]
            self._removeRecursion($_SESSION["debug"]);

            $retval = json_encode($_SESSION["debug"]);
            $_SESSION["debug"] = [];
            return json_last_error() ? "\"false\"" : $retval;
        }
        $_SESSION["debug"] = [];
        return $retval;*/
    }

    /**
     * Returns the url of the current page
     *
     * @return String
     */
    public String getSelfUrl()
    {
    	return ""; /* TODO
        global $route, $db, $table, $server;

        $params = [];
        if (isset($route)) {
            $params["route"] = $route;
        }
        if (isset($db) && strlen($db) > 0) {
            $params["db"] = $db;
        }
        if (isset($table) && strlen($table) > 0) {
            $params["table"] = $table;
        }
        $params["server"] = $server;

        // needed for server privileges tabs
        if (isset($_GET["viewing_mode"])
            && in_array($_GET["viewing_mode"], ["server", "db", "table"])
        ) {
            $params["viewing_mode"] = $_GET["viewing_mode"];
        }
        /*
         * @todo    coming from /server/privileges, here $db is not set,
         *          add the following condition below when that is fixed
         *          && $_GET["checkprivsdb"] == $db
         
        if (isset($_GET["checkprivsdb"])
        ) {
            $params["checkprivsdb"] = $_GET["checkprivsdb"];
        }
        /*
         * @todo    coming from /server/privileges, here $table is not set,
         *          add the following condition below when that is fixed
         *          && $_REQUEST["checkprivstable"] == $table
         *
        if (isset($_GET["checkprivstable"])
        ) {
            $params["checkprivstable"] = $_GET["checkprivstable"];
        }
        if (isset($_REQUEST["single_table"])
            && in_array($_REQUEST["single_table"], [true, false])
        ) {
            $params["single_table"] = $_REQUEST["single_table"];
        }
        return basename(Core.getenv("SCRIPT_NAME")) . Url.getCommonRaw($params);*/
    }

    /**
     * Renders the link to open a new page
     *
     * @param String $url The url of the page
     *
     * @return String
     */
    private String _getSelfLink(String $url)
    {
        String $retval  = "";
        $retval += "<div id='selflink' class='print_ignore'>";
        $retval += "<a href='" + htmlspecialchars($url) + "'"
            + " title='" + __("Open new phpMyAdmin window") + "' target='_blank' rel='noopener noreferrer'>";
        if (Util.showIcons("TabsMode", GLOBALS)) {
            $retval += Generator.getImage(
                "window-new",
                __("Open new phpMyAdmin window"),
                null
            );
        } else {
            $retval +=  __("Open new phpMyAdmin window");
        }
        $retval += "</a>";
        $retval += "</div>";
        return $retval;
    }

    /**
     * Renders the link to open a new page
     *
     * @return String
     */
    public String getErrorMessages()
    {
        String $retval = "";
        /* FIXME
        if (GLOBALS.error_handler.hasDisplayErrors()) {
            $retval += GLOBALS.error_handler..getDispErrors();
        }

        /**
         * Report php errors
         *
        GLOBALS.error_handler.reportErrors();
		*/
        
        return $retval;
    }

    /**
     * Saves query in history
     *
     * @return void
     */
    private void _setHistory()
    {
        /* TODO if (! Core.isValid(request.getParameter("no_history"))
            && empty(GLOBALS.error_message)
            && ! empty(GLOBALS.sql_query)
            && GLOBALS.dbi != null
            && GLOBALS.dbi.isUserType("logged")
        ) {
            this.relation.setHistory(
                Core.ifSetOr(GLOBALS.db, ""),
                Core.ifSetOr(GLOBALS.table, ""),
                GLOBALS.cfg.get("Server.user"),
                GLOBALS.sql_query
            );
        }*/
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
     * Set the ajax flag to indicate whether
     * we are servicing an ajax request
     *
     * @param boolean $isAjax Whether we are servicing an ajax request
     *
     * @return void
     */
    public void setAjax(boolean $isAjax)
    {
        this._isAjax = $isAjax;
    }

    /**
     * Turn on minimal display mode
     *
     * @return void
     */
    public void setMinimal()
    {
        this._isMinimal = true;
    }

    /**
     * Returns the Scripts object
     *
     * @return Scripts object
     */
    public Scripts getScripts()
    {
        return this._scripts;
    }

    /**
     * Renders the footer
     *
     * @return String
     */
    public String getDisplay()
    {
    	Map<String, Object> model = new HashMap<String, Object>();
    	
        this._setHistory();
        if (this._isEnabled) {
        	
            String $demoMessage = null;
            String $url = null;
            Header $header = null;
            String $scripts = null;
            String $menuHash = null;
            String $selfLink = null;
            String $errorMessages = null;
            String $footer = null;
        	
            if (! this._isAjax && ! this._isMinimal) {
                if (Core.getenv("SCRIPT_NAME") != null
                    //&& empty($_POST)
                    && ! this._isAjax
                ) {
                    $url = this.getSelfUrl();
                    $header = pmaResponse.getHeader();
                    List<FStruct2> $files = $header.getScripts().getFiles();
                    $menuHash = $header.getMenu().getHash();
                    // prime the client-side cache
                    this._scripts.addCode(
                        String.format(
                            "if (! (history && history.pushState)) "
                            + "MicroHistory.primer = {"
                            + " url: '%s',"
                            + " scripts: %s,"
                            + " menuHash: '%s'"
                            + "};",
                            Sanitize.escapeJsString($url),
                            json_encode($files),
                            Sanitize.escapeJsString($menuHash)
                        )
                    );
                }
                if (Core.getenv("SCRIPT_NAME") != null
                    && ! this._isAjax
                ) {
                    $url = this.getSelfUrl();
                    $selfLink = this._getSelfLink($url);
                }
                this._scripts.addCode(
                    "var debugSQLInfo = " + this.getDebugMessage() + ";"
                );

                $errorMessages = this.getErrorMessages();
                $scripts = this._scripts.getDisplay();

                if ("true".equals(multiget(GLOBALS.PMA_Config.settings, "DBG" , "demo"))) {
                    $demoMessage = this._getDemoMessage();
                }

                $footer = Config.renderFooter();
            }
            
            model.put("is_ajax", this._isAjax);
            model.put("is_minimal", this._isMinimal);
            model.put("self_link", $selfLink);
            model.put("error_messages",  $errorMessages);
            model.put("scripts", $scripts);
            model.put("is_demo", multiget(GLOBALS.PMA_Config.settings, "DBG", "demo"));
            model.put("demo_message", $demoMessage);
            model.put("footer", $footer);
            
        }
        return JtwigFactory.render("footer", model);
    }

}
