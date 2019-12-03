package org.javamyadmin.helpers;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.javamyadmin.php.GLOBALS;

import static org.javamyadmin.php.Php.*;

/**
 * Singleton class used to manage the rendering of pages in PMA
 *
 * @package PhpMyAdmin
 */
public class Response {
    /**
     * Header instance
     *
     * @access private
     * @var Header
     */
    private Header _header;
    /**
     * HTML data to be used in the response
     *
     * @access private
     * @var String
     */
    private String _HTML;
    /**
     * An array of JSON key-value pairs
     * to be sent back for ajax requests
     *
     * @access private
     * @var array
     */
    private Map<String, Object> _JSON;
    /**
     * PhpMyAdmin\Footer instance
     *
     * @access private
     * @var Footer
     */
    private Footer _footer;
    /**
     * Whether we are servicing an ajax request.
     *
     * @access private
     * @var boolean
     */
    private boolean _isAjax;
    /**
     * Whether response object is disabled
     *
     * @access private
     * @var boolean
     */
    private boolean _isDisabled;
    /**
     * Whether there were any errors during the processing of the request
     * Only used for ajax responses
     *
     * @access private
     * @var boolean
     */
    private boolean _isSuccess;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private GLOBALS GLOBALS;
    
    /**
     * Creates a new class instance
     */
    public Response(HttpServletRequest request, HttpServletResponse response, GLOBALS GLOBALS, Map<String, Object> session)
    {
        /*if (! defined("TESTSUITE")) {
            $buffer = OutputBuffering.getInstance();
            $buffer.start();
            register_shutdown_function([this, "response"]);
        }*/
        this._header = new Header(request, response, GLOBALS, session);
        this._HTML   = "";
        this._JSON   = new HashMap<>();
        this._footer = new Footer(GLOBALS, this);

        this._isSuccess  = true;
        this._isDisabled = false;
        this.setAjax(! empty(request.getParameter("ajax_request")));
        
        this.request = request;
        this.response = response;
        this.GLOBALS = GLOBALS;
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
        this._header.setAjax(this._isAjax);
        this._footer.setAjax(this._isAjax);
    }

    /**
     * Set the status of an ajax response,
     * whether it is a success or an error
     *
     * @param boolean $state Whether the request was successfully processed
     *
     * @return void
     */
    public void setRequestStatus(boolean $state)
    {
        this._isSuccess = $state;
    }

    /**
     * Returns true or false depending on whether
     * we are servicing an ajax request
     *
     * @return boolean
     */
    public boolean isAjax()
    {
        return this._isAjax;
    }

    /**
     * Disables the rendering of the header
     * and the footer in responses
     *
     * @return void
     */
    public void disable()
    {
        this._header.disable();
        this._footer.disable();
        this._isDisabled = true;
    }

    /**
     * Returns a PhpMyAdmin\Header object
     *
     * @return Header
     */
    public Header getHeader()
    {
        return this._header;
    }

    /**
     * Returns a PhpMyAdmin\Footer object
     *
     * @return Footer
     */
    public Footer getFooter()
    {
        return this._footer;
    }

    /**
     * Add HTML code to the response
     *
     * @param String $content A String to be appended to
     *                        the current output buffer
     *
     * @return void
     */
    public void addHTML(Object $content)
    {
        if ($content instanceof List) {
            for (Object $msg: ((List<?>)$content)) {
                this.addHTML($msg);
            }
        } else if ($content instanceof Message) {
            this._HTML += ((Message) $content).getDisplay();
        } else {
            this._HTML += $content;
        }
    }

    /**
     * Add JSON code to the response
     *
     * @param mixed $json  Either a key (String) or an
     *                     array or key-value pairs
     * @param mixed $value Null, if passing an array in $json otherwise
     *                     it"s a String value to the key
     *
     * @return void
     */
    public void addJSON(Object $json, Object $value /*= null*/)
    {
        if ($json instanceof Map) {
        	Map<?,?> map = ((Map<?,?>)$json);
            for (Entry<?,?> entry: map.entrySet()) {
                this.addJSON(entry.getKey(), entry.getValue());
            }
        } else { // $json must be a String
        	if ($value instanceof Message) {
                this._JSON.put((String)$json, ((Message)$value).getDisplay());
            } else {
                this._JSON.put((String)$json, $value);
            }
        }
    }

    /**
     * Renders the HTML response text
     *
     * @return string
     */
    private String _getDisplay()
    {
        // The header may contain nothing at all,
        // if its content was already rendered
        // and, in this case, the header will be
        // in the content part of the request
        String $retval  = this._header.getDisplay();
        $retval += this._HTML;
        $retval += this._footer.getDisplay();
        return $retval;
    }

    /**
     * Sends an HTML response to the browser
     *
     * @return void
     * @throws IOException 
     */
    private void _htmlResponse() throws IOException
    {
    	response.getWriter().write(this._getDisplay());
    }

    /**
     * Sends a JSON response to the browser
     * @param response2 
     *
     * @return void
     * @throws IOException 
     */
    private void _ajaxResponse() throws IOException
    {
        /* Avoid wrapping in case we"re disabled */
    	
    	if (this._isDisabled) {
    		response.getWriter().write(this._getDisplay());
            return;
        }

        if (empty(this._JSON.get("message"))) {
            this._JSON.put("message", this._getDisplay());
        } else if (this._JSON.get("message") instanceof Message) {
            this._JSON.put("message", ((Message) this._JSON.get("message")).getDisplay());
        }

        if (this._isSuccess) {
            this._JSON.put("success", true);
        } else {
            this._JSON.put("success", false);
            this._JSON.put("error", this._JSON.get("message"));
            this._JSON.remove("message");
        }

        if (this._isSuccess) {
            this.addJSON("title", "<title>" + this.getHeader().getPageTitle() + "</title>");

            if (GLOBALS.dbi != null) {
                String $menuHash = this.getHeader().getMenu().getHash();
                this.addJSON("menuHash", $menuHash);
                List<String> $hashes = Collections.emptyList();
                if (!empty(request.getParameter("menuHashes"))) {
                    $hashes = Arrays.asList(request.getParameter("menuHashes").split("-"));
                }
                if (! $hashes.contains($menuHash)) {
                    this.addJSON(
                        "menu",
                        this.getHeader()
                            .getMenu()
                            .getDisplay(request, GLOBALS)
                    );
                }
            }

            this.addJSON("scripts", this.getHeader().getScripts().getFiles());
            this.addJSON("selflink", this.getFooter().getSelfUrl());
            this.addJSON("displayMessage", this.getHeader().getMessage());

            String $debug = this._footer.getDebugMessage();
            if (empty(request.getParameter("no_debug"))
                && ! empty($debug)
            ) {
                this.addJSON("debug", $debug);
            }

            String $errors = this._footer.getErrorMessages();
            if (!empty($errors) ) {
                this.addJSON("errors", $errors);
            }
            // Unsupported String $promptPhpErrors = GLOBALS.error_handler.hasErrorsForPrompt();
            // this.addJSON("promptPhpErrors", $promptPhpErrors);

            if (empty(GLOBALS.error_message)) {
                // set current db, table and sql query in the querywindow
                // (this is for the bottom console)
                String $query = "";
                Integer $maxChars = new Integer((String) GLOBALS.PMA_Config.get("MaxCharactersInDisplayedSQL"));
                if (!empty(GLOBALS.sql_query)
                    && GLOBALS.sql_query.length() < $maxChars
                ) {
                    $query = GLOBALS.sql_query;
                }
                Map<String, Object> params = new HashMap<>();
                params.put("db", Core.ifSetOr(GLOBALS.db, ""));
                params.put("table", Core.ifSetOr(GLOBALS.table, ""));
                params.put("sql_query", $query);
                this.addJSON(
                    "reloadQuerywindow",
                    params
                );
                if (! empty(GLOBALS.focus_querywindow)) {
                    this.addJSON("_focusQuerywindow", $query);
                }
                if (! empty(GLOBALS.reload)) {
                    this.addJSON("reloadNavigation", 1);
                }
                this.addJSON("params", this.getHeader().getJsParams());
            }
        }

        // Set the Content-Type header to JSON so that jQuery parses the
        // response correctly.
        Core.headerJSON(response);

        String $result = json_encode(this._JSON);
        /* TDO if ($result === false) {
            switch (json_last_error()) {
                case JSON_ERROR_NONE:
                    $error = "No errors";
                    break;
                case JSON_ERROR_DEPTH:
                    $error = "Maximum stack depth exceeded";
                    break;
                case JSON_ERROR_STATE_MISMATCH:
                    $error = "Underflow or the modes mismatch";
                    break;
                case JSON_ERROR_CTRL_CHAR:
                    $error = "Unexpected control character found";
                    break;
                case JSON_ERROR_SYNTAX:
                    $error = "Syntax error, malformed JSON";
                    break;
                case JSON_ERROR_UTF8:
                    $error = "Malformed UTF-8 characters, possibly incorrectly encoded";
                    break;
                case JSON_ERROR_RECURSION:
                    $error = "One or more recursive references in the value to be encoded";
                    break;
                case JSON_ERROR_INF_OR_NAN:
                    $error = "One or more NAN or INF values in the value to be encoded";
                    break;
                case JSON_ERROR_UNSUPPORTED_TYPE:
                    $error = "A value of a type that cannot be encoded was given";
                    break;
                default:
                    $error = "Unknown error";
                    break;
            }
            echo json_encode([
                "success" => false,
                "error" => "JSON encoding failed: " . $error,
            ]);
        } else */ {
            response.getWriter().write($result);
        }
    }

    /**
     * Sends an HTML response to the browser
     *
     * @return void
     * @throws IOException 
     */
    public void response() throws IOException
    {
        /*
        TODO
        $buffer = OutputBuffering::getInstance();
        if (empty(this._HTML)) {
            this._HTML = $buffer->getContents();
        }*/
        if (this.isAjax()) {
            this._ajaxResponse();
        } else {
            this._htmlResponse();
        }
    }

    /**
     * Generate header for 303
     *
     * @param String $location will set location to redirect.
     *
     * @return void
     */
    public void generateHeader303(String $location)
    {
        response.setStatus(303);
        response.addHeader("Location", $location);
    }

    /**
     * Configures response for the login page
     *
     * @return boolean Whether caller should exit
     */
    public boolean loginPage()
    {
        /* Handle AJAX redirection */
        if (this.isAjax()) {
            this.setRequestStatus(false);
            // redirect_flag redirects to the login page
            this.addJSON("redirect_flag", "1");
            return true;
        }

        this.getFooter().setMinimal();
        Header $header = this.getHeader();
        $header.setBodyId("loginform");
        $header.setTitle("javaMyAdmin");
        $header.disableMenuAndConsole();
        $header.disableWarnings();
        return false;
    }
}
