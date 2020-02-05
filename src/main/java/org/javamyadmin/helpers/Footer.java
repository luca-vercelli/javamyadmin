package org.javamyadmin.helpers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.javamyadmin.helpers.Scripts.FStruct2;
import org.javamyadmin.php.Globals;
import org.springframework.beans.factory.annotation.Autowired;

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
	@Autowired
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
    @Autowired
    private Template template;

    @Autowired
    private Globals GLOBALS;
    @Autowired
    private Response response;
    @Autowired
    private HttpServletRequest httpRequest;
    @Autowired
    private SessionMap $_SESSION;
    
    /**
     * Creates a new class instance
     */
    public Footer()
    {
        this._isEnabled = true;
        this._isMinimal = false;
        // TODO this.relation = new Relation(GLOBALS.getDbi());
        
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
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private static Object _removeRecursion(Object $object, Stack<Object> $stack /*= []*/)
    {
    	// Original PMA implementation was by reference
    	if ($object != null) {
    		if ($object instanceof Collection) {
    			$stack.push($object);
    			Collection $copy = new ArrayList((List)$object);
    			for (Object $subobject : (List)$copy) {
    				if ($stack.contains($subobject)) {
    					try {
    						((Collection) $object).remove($subobject);
    					} catch (UnsupportedOperationException e) {
    						System.out.println("Cannot remove recursive items! UnsupportedOperationException.");
    						e.printStackTrace();
    					}
    				}
    				_removeRecursion($subobject, $stack);
    			}
    			$stack.pop();
    		} else if ($object instanceof Map) {
    			$stack.push($object);
    			Map $copy = new HashMap((Map)$object);
    			Set<Entry> $entries = ((Map)$copy).entrySet(); 
    			for (Entry $entry : $entries) {
    				Object $subobject =$entry.getValue();  
    				if ($stack.contains($subobject)) {
    					try {
    						((Map) $object).remove($entry.getKey());
    					} catch (UnsupportedOperationException e) {
    						System.out.println("Cannot remove recursive items! UnsupportedOperationException.");
    						e.printStackTrace();
    					}
    				}
    				_removeRecursion($subobject, $stack);
    			}
    			$stack.pop();
    		}
    	}
        return $object;
    }
    
    private static Object _removeRecursion(Object $object) {
    	return _removeRecursion($object, new Stack<>());
    }

    /**
     * Renders the debug messages
     *
     * @return String
     */
    public String getDebugMessage()
    {
        String $retval = "\"null\"";
        if (!empty(Globals.getConfig().get("DBG.sql"))
            && empty(httpRequest.getParameter("no_debug"))
            && ! empty($_SESSION.get("debug"))
        ) {
            // Remove recursions and iterators from $_SESSION["debug"]
            _removeRecursion($_SESSION.get("debug"));

            $retval = json_encode($_SESSION.get("debug"));
            $_SESSION.put("debug", new HashMap<>());
            return $retval;
        }
        $_SESSION.put("debug", new HashMap<>());
        return $retval;
    }

    private List<String> viewing_modes = Arrays.asList(
    		new String[] {"server", "db", "table" }
    		);
    
    /**
     * Returns the url of the current page
     *
     * @return String
     */
    public String getSelfUrl()
    {
        //global $route, $db, $table, $server;

        Map<String, String> $params = new HashMap<>();
        if (!empty(GLOBALS.getRoute())) {
            $params.put("route", GLOBALS.getRoute());
        }
        if (!empty(GLOBALS.getDb())) {
            $params.put("db", GLOBALS.getDb());
        }
        if (!empty(GLOBALS.getTable())) {
            $params.put("table", GLOBALS.getTable());
        }
        $params.put("server", Integer.toString(GLOBALS.getServer()));

        // needed for server privileges tabs
        if (!empty(httpRequest.getParameter("viewing_mode"))
            && viewing_modes.contains(httpRequest.getParameter("viewing_mode"))
        ) {
            $params.put("viewing_mode", httpRequest.getParameter("viewing_mode"));
        }
         
        if (!empty(httpRequest.getParameter("checkprivsdb"))
        ) {
            $params.put("checkprivsdb", httpRequest.getParameter("checkprivsdb"));
        }

        if (!empty(httpRequest.getParameter("checkprivstable"))
        ) {
            $params.put("checkprivstable", httpRequest.getParameter("checkprivstable"));
        }
        if (!empty(httpRequest.getParameter("single_table"))
            && (httpRequest.getParameter("single_table").equals("true") || httpRequest.getParameter("single_table").equals("false"))
        ) {
            $params.put("single_table", httpRequest.getParameter("single_table"));
        }
        return /* TODO basename(Core.getenv("SCRIPT_NAME")) + */ Url.getCommonRaw($params);
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
            $retval += Util.getImage(
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
        /* TODO
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
        if (! Core.isValid(httpRequest.getParameter("no_history"))
            && empty(GLOBALS.getMessage())
            && ! empty(GLOBALS.getSqlQuery())
            && GLOBALS.getDbi() != null
            && GLOBALS.getDbi().isUserType("logged")
        ) {
        	/* TODO
            this.relation.setHistory(
                Core.ifSetOr(GLOBALS.getDb(), ""),
                Core.ifSetOr(GLOBALS.getTable(), ""),
                Globals.getConfig().get("Server.user"),
                GLOBALS.getSqlQuery()
            ); */
        }
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
     * @throws SQLException 
     */
    public String getDisplay() throws SQLException
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
                    $header = response.getHeader();
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

                if ("true".equals(multiget(Globals.getConfig().settings, "DBG" , "demo"))) {
                    $demoMessage = this._getDemoMessage();
                }

                $footer = Config.renderFooter();
            }
            
            model.put("is_ajax", this._isAjax);
            model.put("is_minimal", this._isMinimal);
            model.put("self_link", $selfLink);
            model.put("error_messages",  $errorMessages);
            model.put("scripts", $scripts);
            model.put("is_demo", multiget(Globals.getConfig().settings, "DBG", "demo"));
            model.put("demo_message", $demoMessage);
            model.put("footer", $footer);
            
        }
        return template.render("footer", model);
    }

}
