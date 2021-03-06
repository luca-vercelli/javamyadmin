package org.javamyadmin.helpers;

import static org.javamyadmin.php.Php.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.javamyadmin.php.Globals;
import org.javamyadmin.php.Php.SessionMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Core {

    @Autowired
    private Sanitize sanitize;
    @Autowired
    private Config config;
	@Autowired
	private Util util;
    
    /**
     * the whitelist for goto parameter
     * @static array $goto_whitelist
     */
    public String[] $goto_whitelist = new String[] {
        "index.php",
    };
    /**
     * checks given $var and returns it if valid, or $default of not valid
     * given $var is also checked for type being "similar" as $default
     * or against any other type if $type is provided
     *
     * <code>
     * // $_REQUEST["db"] not set
     * echo Core.ifSetOr($_REQUEST["db"], ""); // ""
     * // $_POST["sql_query"] not set
     * echo Core.ifSetOr($_POST["sql_query"]); // null
     * // config["EnableFoo"] not set
     * echo Core.ifSetOr(config["EnableFoo"], false, "boolean"); // false
     * echo Core.ifSetOr(config["EnableFoo"]); // null
     * // config["EnableFoo"] set to 1
     * echo Core.ifSetOr(config["EnableFoo"], false, "boolean"); // false
     * echo Core.ifSetOr(config["EnableFoo"], false, "similar"); // 1
     * echo Core.ifSetOr(config["EnableFoo"], false); // 1
     * // config["EnableFoo"] set to true
     * echo Core.ifSetOr(config["EnableFoo"], false, "boolean"); // true
     * </code>
     *
     * @param mixed $var     param to check
     * @param mixed $default default value
     * @param mixed $type    var type or array of values to check against $var
     *
     * @return mixed $var or $default
     *
     * @see isValid()
     */
    public Object ifSetOr(Object $var, Object $default /*= null*/, String $type /*= "similar"*/)
    {
        if (! isValid($var, $type, $default)) {
            return $default;
        }
        return $var;
    }
    public Object ifSetOr(Object $var, Object $default) {
    	return ifSetOr($var, $default, "similar");
    }
    public Object ifSetOr(Object $var) {
    	return ifSetOr($var, null, "similar");
    }
    
    /**
     * checks given $var against $type or $compare
     *
     * $type can be:
     * - false       : no type checking
     * - "scalar"    : whether type of $var is integer, float, String or boolean
     * - "numeric"   : whether type of $var is any number representation
     * - "length"    : whether type of $var is scalar with a String length > 0
     * - "similar"   : whether type of $var is similar to type of $compare
     * - "equal"     : whether type of $var is identical to type of $compare
     * - "identical" : whether $var is identical to $compare, not only the type!
     * - or any other valid PHP variable type
     *
     * <code>
     * // $_REQUEST["doit"] = true;
     * Core.isValid($_REQUEST["doit"], "identical", "true"); // false
     * // $_REQUEST["doit"] = "true";
     * Core.isValid($_REQUEST["doit"], "identical", "true"); // true
     * </code>
     *
     * NOTE: call-by-reference is used to not get NOTICE on undefined vars,
     * but the var is not altered inside this function, also after checking a var
     * this var exists nut is not set, example:
     * <code>
     * // $var is not set
     * isset($var); // false
     * functionCallByReference($var); // false
     * isset($var); // true
     * functionCallByReference($var); // true
     * </code>
     *
     * to avoid this we set this var to null if not isset
     *
     * @param mixed $var     variable to check
     * @param mixed $type    var type or array of valid values to check against $var
     * @param mixed $compare var to compare with $var
     *
     * @return boolean whether valid or not
     *
     * @todo add some more var types like hex, bin, ...?
     * @see https://secure.php.net/gettype
     */
    public boolean isValid(Object $var, Object $type /*= "length"*/, Object $compare /*= null*/)
    {
        if ($var == null) {
            // var is not even set
            return false;
        }
        if ($type == null) {
            // no vartype requested
            return true;
        }
        if ($type instanceof List) {
            return ((List)$type).contains($var);
        }
        if ($type instanceof Map) {
        	return ((Map)$type).containsValue($var);
        }
        // allow some aliases of var types
        $type = ((String)$type).toLowerCase();
        switch ((String)$type) {
            case "identic":
                $type = "identical";
                break;
            case "len":
                $type = "length";
                break;
            case "bool":
                $type = "boolean";
                break;
            case "float":
                $type = "double";
                break;
            case "int":
                $type = "integer";
                break;
            case "null":
                $type = "NULL";
                break;
        }
        if ($type.equals("identical")) {
            return $var == $compare;
        }
        // whether we should check against given $compare
        if ($type.equals("similar")) {
            switch (gettype($compare)) {
                case "string":
                case "boolean":
                    $type = "scalar";
                    break;
                case "integer":
                case "double":
                    $type = "numeric";
                    break;
                default:
                    $type = gettype($compare);
            }
        } else if ($type.equals("equal")) {
            $type = gettype($compare);
        }
        // do the check
        if ($type.equals("length") || $type.equals("scalar")) {
            boolean $is_scalar = is_scalar($var);
            if ($is_scalar && $type.equals("length")) {
                return ((String) $var).length() > 0;
            }
            return $is_scalar;
        }
        if ($type.equals("numeric")) {
            return is_numeric($var);
        }
        return gettype($var).equals($type);
    }
    public boolean isValid(Object $var) {
    	return isValid($var, "length", null);
    }
    /**
     * Removes insecure parts in a path; used before include() or
     * require() when a part of the path comes from an insecure source
     * like a cookie or form.
     *
     * @param String $path The path to check
     *
     * @return String  The secured path
     *
     * @access  public
     */
    public String securePath(String $path)
    {
        // change .. to .
        return $path.replace("..", ".");
    } // end function
    
    public class ErrorBean {
    	boolean success;
    	String message;
    	public ErrorBean(boolean success, String message) {
    		this.success = success;
    		this.message = message;
    	}
    }
    /**
     * displays the given error message on phpMyAdmin error page in foreign language,
     * ends script execution and closes session
     *
     * loads language file if not loaded already
     *
     * @param String       $error_message the error message or named error message
     * @param String|array $message_args  arguments applied to $error_message
     *
     * @return void
     */
    public void fatalError(
        HttpServletRequest req, HttpServletResponse resp, Globals GLOBALS, Response pmaResponse,
        String $error_message,
        Object $message_args /*= null*/
    ) {
        /* Use format String if applicable */
        if ($message_args instanceof String) {
            $error_message = String.format($error_message, $message_args);
        } else if ($message_args instanceof List) {
            $error_message = String.format($error_message, $message_args);
        }
        /*
         * Avoid using Response class as config does not have to be loaded yet
         * (this can happen on early fatal error)
         */
        if (GLOBALS.getDbi() != null && config != null
            && config.get("is_setup").equals("false")
            && pmaResponse.isAjax()) {
        	pmaResponse.setRequestStatus(false);
        	pmaResponse.addJSON("message", Message.error($error_message));
        } else if (! empty(req.getParameter("ajax_request"))) {
            // Generate JSON manually
            headerJSON(resp);
            try {
				resp.getWriter().write(json_encode(new ErrorBean(false, Message.error($error_message).getDisplay()))
				);
			} catch (IOException e) {
				e.printStackTrace();
			}
        } else {
            $error_message = $error_message.replace("<br>", "[br]");
            String $error_header = __("Error");
            String $lang = GLOBALS.getLang() != null ? GLOBALS.getLang() : "en";
            String $dir = GLOBALS.getTextDir() != null ? GLOBALS.getTextDir() : "ltr";
            /* TODO echo(DisplayError.display(new Template(), $lang, $dir, $error_header, $error_message));*/
        }
    }
    
    public void fatalError(
            HttpServletRequest req, HttpServletResponse resp, Globals GLOBALS, Response pmaResponse,
            String $error_message)
    {
    	fatalError(req, resp, GLOBALS, pmaResponse, $error_message, null);
    }
    
    /**
     * Returns a link to the PHP documentation
     *
     * @param String $target anchor in documentation
     *
     * @return String  the URL
     *
     * @access  public
     */
    public String getPHPDocLink(String $target)
    {
    	return ""; // Unsupported
        /* List of PHP documentation translations */
        /*$php_doc_languages = [
            "pt_BR",
            "zh",
            "fr",
            "de",
            "it",
            "ja",
            "pl",
            "ro",
            "ru",
            "fa",
            "es",
            "tr",
        ];
        $lang = "en";
        if (in_array(GLOBALS["lang"], $php_doc_languages)) {
            $lang = GLOBALS["lang"];
        }
        return linkURL("https://secure.php.net/manual/" + $lang + "/" + $target);*/
    }
    /**
     * Warn or fail on missing extension.
     *
     * @param String $extension Extension name
     * @param boolean   $fatal     Whether the error is fatal.
     * @param String $extra     Extra String to append to message.
     *
     * @return void
     */
    public void warnMissingExtension(
        String $extension,
        boolean $fatal /*= false*/,
        String $extra /*= ""*/
    ) {
    	// Unsupported
    }
    /**
     * returns count of tables in given db
     *
     * @param String $db database to count tables for
     *
     * @return integer count of tables in $db
     */
    public int getTableCount(String $db)
    {
    	// FIXME mysql specific
    	return -1;
    	// see https://stackoverflow.com/questions/2780284/how-to-get-all-table-names-from-a-database
        /*$tables = GLOBALS["dbi"].tryQuery(
            "SHOW TABLES FROM " + util.backquote($db) + ";",
            DatabaseInterface.CONNECT_USER,
            DatabaseInterface.QUERY_STORE
        );
        if ($tables) {
            $num_tables = GLOBALS["dbi"].numRows($tables);
            GLOBALS["dbi"].freeResult($tables);
        } else {
            $num_tables = 0;
        }
        return $num_tables;*/
    }
    /**
     * Converts numbers like 10M into bytes
     * Used with permission from Moodle (https://moodle.org) by Martin Dougiamas
     * (renamed with PMA prefix to avoid double definition when embedded
     * in Moodle)
     *
     * @param String|int $size size (Default = 0)
     *
     * @return integer
     */
    public Long getRealSize(String $size /*= 0*/)
    {
        if (empty($size)) {
            return 0L;
        }
        Map<String, Long> $binaryprefixes = new HashMap<>();
        $binaryprefixes.put("T" , 1099511627776L);
        $binaryprefixes.put("G" ,    1073741824L);
        $binaryprefixes.put("M" ,       1048576L);
        $binaryprefixes.put("K" ,          1024L);

        $size=$size.toUpperCase().trim();
        String lastLetter = $size.substring($size.length()-1);
        if ($binaryprefixes.containsKey(lastLetter)) {
        	return new Long($size.substring(0, -1)) * $binaryprefixes.get(lastLetter);
        }
        return new Long($size);
    } // end getRealSize()
    /**
     * Checks given $page against given $whitelist and returns true if valid
     * it optionally ignores query parameters in $page (script.php?ignored)
     *
     * @param String  $page      page to check
     * @param array   $whitelist whitelist to check page against
     * @param boolean $include   whether the page is going to be included
     *
     * @return boolean whether $page is valid or not (in $whitelist or not)
     */
    public boolean checkPageValidity(String $page, List $whitelist /*= []*/, boolean $include /*= false*/)
    {
    	if (empty($whitelist)) {
            $whitelist = Arrays.asList($goto_whitelist);
        }
        if (empty($page)) {
            return false;
        }
        if ($whitelist.contains($page)) {
            return true;
        }
        if ($include) {
            return false;
        }
        String $_page = $page + "?"; 
        $_page = $_page.substring(
            0,
            $_page.indexOf( "?")
        );
        if ($whitelist.contains($_page)) {
            return true;
        }
        //$_page = urldecode($page);
        //... TODO
        return false;
    }
    
    public boolean checkPageValidity(String $page) {
    	return checkPageValidity($page, new ArrayList<>(), false);
    }
    
    /**
     * tries to find the value for the given environment variable name
     *
     * searches in $_SERVER, $_ENV then tries getenv() and apache_getenv()
     * in this order
     *
     * @param String $var_name variable name
     *
     * @return String  value of $var or empty String
     */
    public String getenv(String $var_name)
    {
    	if (! empty(System.getenv($var_name))) {
    		return System.getenv($var_name);
    	}
    	if (! empty(System.getProperty($var_name))) {
    		return System.getProperty($var_name);
    	}
        return "";
    }
    /**
     * Send HTTP header, taking IIS limits into account (600 seems ok)
     * @param request 
     *
     * @param String $uri         the header to send
     * @param boolean   $use_refresh whether to use Refresh: header when running on IIS
     *
     * @return void
     */
    public void sendHeaderLocation(String $uri, boolean $use_refresh /*= false*/, HttpServletRequest request, HttpServletResponse response)
    {
        /*
         * Avoid relative path redirect problems in case user entered URL
         * like /phpmyadmin/index.php/ which some web servers happily accept.
         */
        if ($uri.charAt(0) == '.') {
            $uri = config.getRootPath(request) + $uri.substring(2);
        }
        response.addHeader("Location: ", $uri);
    }
    
    public void sendHeaderLocation(String $uri, HttpServletRequest request, HttpServletResponse response) {
    	sendHeaderLocation($uri, false, request, response);
    }
    
    /**
     * Outputs application/json headers. This includes no caching.
     *
     * @return void
     */
    public void headerJSON(HttpServletResponse response)
    {
        /*if (defined("TESTSUITE")) {
            return;
        }*/
        // No caching
        noCacheHeader(response);
        // MIME type
        response.addHeader("Content-Type", "application/json; charset=UTF-8");
        // Disable content sniffing in browser
        // This is needed in case we include HTML in JSON, browser might assume it"s
        // html to display
        response.addHeader("X-Content-Type-Options", "nosniff");
    }
    /**
     * Outputs headers to prevent caching in browser (and on the way).
     *
     * @return void
     */
    public void noCacheHeader(HttpServletResponse response)
    {
        // rfc2616 - Section 14.21
    	response.addHeader("Expires", new Date().toString()); //FIXME check format
        // HTTP/1.1
    	response.addHeader(
            "Cache-Control", "no-store, no-cache, must-revalidate,"
            + "  pre-check=0, post-check=0, max-age=0"
        );
    	response.addHeader("Pragma", "no-cache"); // HTTP/1.0
        // test case: exporting a database into a .gz file with Safari
        // would produce files not having the current time
        // (added this header for Safari but should not harm other browsers)
    	response.addHeader("Last-Modified", new Date().toString());
    }
    /**
     * Sends header indicating file download.
     *
     * @param String $filename Filename to include in headers if empty,
     *                         none Content-Disposition header will be sent.
     * @param String $mimetype MIME type to include in headers.
     * @param int    $length   Length of content (optional)
     * @param boolean   $no_cache Whether to include no-caching headers.
     *
     * @return void
     */
    public void downloadHeader(
        String $filename,
        String $mimetype,
        int $length /*= 0*/,
        boolean $no_cache, /*= true*/
        HttpServletResponse response,
        Globals GLOBALS
    ) {
        if ($no_cache) {
            noCacheHeader(response);
        }
        /* Replace all possibly dangerous chars in filename */
        $filename = sanitize.sanitizeFilename($filename);
        if (! empty($filename)) {
        	response.addHeader("Content-Description", "File Transfer");
        	response.addHeader("Content-Disposition", "attachment; filename=\"" + $filename + "\"");
        }
        response.addHeader("Content-Type", $mimetype);
        // inform the server that compression has been done,
        // to avoid a double compression (for example with Apache + mod_deflate)
        boolean $notChromeOrLessThan43 = !"CHROME".equals(GLOBALS.getPMA_USR_BROWSER_AGENT())  // see bug #4942
            || ("CHROME".equals(GLOBALS.getPMA_USR_BROWSER_AGENT()) && GLOBALS.getPMA_USR_BROWSER_VER() < 43);
        if ($mimetype.contains("gzip") && $notChromeOrLessThan43) {
        	response.addHeader("Content-Encoding", "gzip");
        }
        response.addHeader("Content-Transfer-Encoding", "binary");
        if ($length > 0) {
        	response.addHeader("Content-Length", Integer.toString($length));
        }
    }
    /**
     * Returns value of an element in $array given by $path.
     * $path is a String describing position of an element in an associative array,
     * eg. Servers/1/host refers to $array[Servers][1][host]
     *
     * @param String $path    path in the array
     * @param array  $array   the array
     * @param mixed  $default default value
     *
     * @return mixed    array element or $default
     */
    public Object arrayRead(String $path, Map $array, Object $default /*= null*/)
    {
        String[] $keys = $path.split("/");
        for (String $key : $keys ) {
            if (! $array.containsKey($key)) {
                return $default;
            }
            $array = (Map) $array.get($key);
        }
        return $array;
    }
    
    public Object arrayRead(String $path, Map $array) {
    	return arrayRead($path, $array, null);
    }
    
    /**
     * Stores value in an array
     *
     * @param String $path  path in the array
     * @param array  $array the array
     * @param mixed  $value value to store
     *
     * @return void
     */
    public void arrayWrite(String $path, Map $array, Object $value)
    {
        String[] $keys = $path.split("/");
        for (int i = 0 ; i < $keys.length-1; ++i) {
        	String $key = $keys[i];
            if (! $array.containsKey($key)) {
            	$array.put($key, new HashMap());
            }
            $array = (Map) $array.get($key);
        }
        $array.put($keys[$keys.length-1], $value);
    }
    /**
     * Removes value from an array
     *
     * @param String $path  path in the array
     * @param array  $array the array
     *
     * @return void
     */
    public void arrayRemove(String $path, Map $array)
    {
    	throw new IllegalStateException("Not implemented");
        /*String[] $keys = $path.split("/");
        $keys_last = array_pop($keys);
        $path = new HashMap();
        $depth = 0;
        $path[0] =& $array;
        $found = true;
        // go as deep as required or possible
        foreach ($keys as $key) {
            if (! isset($path[$depth][$key])) {
                $found = false;
                break;
            }
            $depth++;
            $path[$depth] =& $path[$depth - 1][$key];
        }
        // if element found, remove it
        if ($found) {
            unset($path[$depth][$keys_last]);
            $depth--;
        }
        // remove empty nested arrays
        for (; $depth >= 0; $depth--) {
            if (! isset($path[$depth + 1]) || count($path[$depth + 1]) === 0) {
                unset($path[$depth][$keys[$depth]]);
            } else {
                break;
            }
        }*/
    }
    /**
     * Returns link to (possibly) external site using defined redirector.
     * @param req 
     * @param Globals 
     *
     * @param String $url URL where to go.
     *
     * @return String URL for a link.
     */
    public String linkURL(String $url)
    {
        if (! $url.matches("^https?://")) {
            return $url;
        }
        
        //FIXME not sure of what the original function did
        $url = urlencode($url);
        		
        if (config != null && "true".equals(config.get("is_setup"))) {
            $url = "../url.php?url=" + $url;
        } else {
            $url = "./url.php?url=" + $url;
        }
        return $url;
    }
    /**
     * Checks whether domain of URL is whitelisted domain or not.
     * Use only for URLs of external sites.
     *
     * @param String $url URL of external site.
     *
     * @return boolean True: if domain of $url is allowed domain,
     *                 False: otherwise.
     */
    public boolean isAllowedDomain(String $url)
    {
    	return true; // TODO
        /* $arr = parse_url($url);
        // We need host to be set
        if (! isset($arr["host"]) || strlen($arr["host"]) == 0) {
            return false;
        }
        // We do not want these to be present
        $blocked = new String[] {
                "user",
                "pass",
                "port"
        }
        foreach ($blocked as $part) {
            if (isset($arr[$part]) && strlen((String) $arr[$part]) != 0) {
                return false;
            }
        }
        $domain = $arr["host"];
        $domainWhiteList = [
            $_SERVER["SERVER_NAME"],
            "wiki.phpmyadmin.net",
            "www.phpmyadmin.net",
            "phpmyadmin.net",
            "demo.phpmyadmin.net",
            "docs.phpmyadmin.net",
            "dev.mysql.com",
            "bugs.mysql.com",
            "mariadb.org",
            "mariadb.com",
            "php.net",
            "secure.php.net",
            "github.com",
            "www.github.com",
            "www.percona.com",
            "mysqldatabaseadministration.blogspot.com",
        ];
        return in_array($domain, $domainWhiteList);*/
    }
    /**
     * Replace some html-unfriendly stuff
     *
     * @param String $buffer String to process
     *
     * @return String Escaped and cleaned up text suitable for html
     */
    public String mimeDefaultFunction(String $buffer)
    {
        $buffer = htmlspecialchars($buffer);
        $buffer = $buffer.replace("  ", " &nbsp;");
        return $buffer.replaceAll("@((\015\012)|(\015)|(\012))@", "<br>" + "\n");
    }
    /**
     * Displays SQL query before executing.
     * @param pmaResponse 
     *
     * @param array|String $query_data Array containing queries or query itself
     *
     * @return void
     */
    public void previewSQL(String $query_data, Response pmaResponse)
    {
        String $retval = "<div class='preview_sql'>";
        if (empty($query_data)) {
            $retval += __("No change");
        } else {
            $retval += util.formatSql($query_data, false);
        }
        $retval += "</div>";
        pmaResponse.addJSON("sql_data", $retval);
        //exit();  //FIXME
    }

    public void previewSQL(List<String> $query_data, Response pmaResponse)
    {
        String $retval = "<div class='preview_sql'>";
        if (empty($query_data)) {
            $retval += __("No change");
        } else {
            for (String $query : $query_data ) {
                $retval += util.formatSql($query, false);
            }
        }
        $retval += "</div>";
        pmaResponse.addJSON("sql_data", $retval);
        //exit();  //FIXME
    }
    /**
     * recursively check if variable is empty
     *
     * @param mixed $value the variable
     *
     * @return boolean true if empty
     */
    public boolean emptyRecursive(Object $value)
    {
        boolean $empty = true;
        if ($value instanceof Map) {
        	Map map = (Map)$value;
        	for (Object value: map.values()) {
        		if (!emptyRecursive(value)) {
        			$empty = false;
        			break;
        		}
        	}
        } else {
            $empty = empty($value);
        }
        return $empty;
    }
    /**
     * Creates some globals from $_POST variables matching a pattern
     *
     * @param array $post_patterns The patterns to search for
     *
     * @return void
     */
    public void setPostAsGlobal(Map $post_patterns)
    {
    	/* TODO
        foreach (array_keys($_POST) as $post_key) {
            foreach ($post_patterns as $one_post_pattern) {
                if (preg_match($one_post_pattern, $post_key)) {
                    Migration.getInstance().setGlobal($post_key, $_POST[$post_key]);
                }
            }
        }*/
    }
    /**
     * Creates some globals from $_REQUEST
     *
     * @param String $param db|table
     *
     * @return void
     */
    public void setGlobalDbOrTable(String $param)
    {
    	/* TODO
        $value = "";
        if (isValid($_REQUEST[$param])) {
            $value = $_REQUEST[$param];
        }
        Migration.getInstance().setGlobal($param, $value);
        Migration.getInstance().setGlobal("url_params", [$param => $value] + GLOBALS["url_params"]);
        */
    }
    /**
     * PATH_INFO could be compromised if set, so remove it from PHP_SELF
     * and provide a clean PHP_SELF here
     *
     * @return void
     */
    public void cleanupPathInfo()
    {
    	/* TODO
        global $PMA_PHP_SELF;
        $PMA_PHP_SELF = getenv("PHP_SELF");
        if (empty($PMA_PHP_SELF)) {
            $PMA_PHP_SELF = urldecode(getenv("REQUEST_URI"));
        }
        $_PATH_INFO = getenv("PATH_INFO");
        if (! empty($_PATH_INFO) && ! empty($PMA_PHP_SELF)) {
            $question_pos = mb_strpos($PMA_PHP_SELF, "?");
            if ($question_pos != false) {
                $PMA_PHP_SELF = mb_substr($PMA_PHP_SELF, 0, $question_pos);
            }
            $path_info_pos = mb_strrpos($PMA_PHP_SELF, $_PATH_INFO);
            if ($path_info_pos !== false) {
                $path_info_part = mb_substr($PMA_PHP_SELF, $path_info_pos, mb_strlen($_PATH_INFO));
                if ($path_info_part == $_PATH_INFO) {
                    $PMA_PHP_SELF = mb_substr($PMA_PHP_SELF, 0, $path_info_pos);
                }
            }
        }
        $path = [];
        foreach (explode("/", $PMA_PHP_SELF) as $part) {
            // ignore parts that have no value
            if (empty($part) || $part === ".") {
                continue;
            }
            if ($part !== "..") {
                // cool, we found a new part
                $path[] = $part;
            } else if (count($path) > 0) {
                // going back up? sure
                array_pop($path);
            }
            // Here we intentionall ignore case where we go too up
            // as there is nothing sane to do
        }
        $PMA_PHP_SELF = htmlspecialchars("/" + implode("/", $path));*/
    }
    /**
     * Checks that required PHP extensions are there.
     * @return void
     */
    public void checkExtensions()
    {
    	// Unsupported
    }
    /**
     * Gets the "true" IP address of the current user
     *
     * @return String|boolean the ip of the user
     *
     * @access  private
     */
    public String getIp(SessionMap session)
    {
        /* Get the address of user */
        if (empty(session.get("REMOTE_ADDR"))) {
            /* We do not know remote IP */
            return null;
        }
        String $direct_ip = (String) session.get("REMOTE_ADDR");
        /* Do we trust this IP as a proxy? If yes we will use it"s header. */
        if (! empty(multiget(config.settings, "TrustedProxies", $direct_ip))) {
            /* Return true IP */
            return $direct_ip;
        }
        /**
         * Parse header in form:
         * X-Forwarded-For: client, proxy1, proxy2
         */
        // Get header content
        String $value = getenv((String) multiget(config.settings,"TrustedProxies", $direct_ip));
        // Grab first element what is client adddress
        $value = $value.split(",")[0];
        // checks that the header contains only one IP address,
        return $value;
        /* TODO
        String $is_ip = filter_var($value, FILTER_VALIDATE_IP);
        if ($is_ip !== false) {
            // True IP behind a proxy
            return $value;
        }
        // We could not parse header
        return null;*/
    } // end of the "getIp()" function
    /**
     * Sanitizes MySQL hostname
     *
     * * strips p: prefix(es)
     *
     * @param String $name User given hostname
     *
     * @return String
     */
    public String sanitizeMySQLHost(String $name)
    {
    	return $name; //Unsupported
        /*while (strtolower(substr($name, 0, 2)) == "p:") {
            $name = substr($name, 2);
        }
        return $name;*/
    }
    /**
     * Sanitizes MySQL username
     *
     * * strips part behind null byte
     *
     * @param String $name User given username
     *
     * @return String
     */
    public String sanitizeMySQLUser(String $name) 
    {
    	return $name; //Unsupported
        /*$position = strpos($name, chr(0));
        if ($position !== false) {
            return substr($name, 0, $position);
        }
        return $name;*/
    }
    /**
     * Safe unserializer wrapper
     *
     * It does not unserialize data containing objects
     *
     * @param String $data Data to unserialize
     *
     * @return mixed
     */
    public Object safeUnserialize(String $data)
    {
    	return null; //TODO
    	/*
        if (! is_string($data)) {
            return null;
        }
        // validate serialized data
        $length = strlen($data);
        $depth = 0;
        for ($i = 0; $i < $length; $i++) {
            $value = $data[$i];
            switch ($value) {
                case "}":
                    /* end of array
                    if ($depth <= 0) {
                        return null;
                    }
                    $depth--;
                    break;
                case "s":
                    // String
                    // parse sting length
                    $strlen = intval(substr($data, $i + 2));
                    // String start
                    $i = strpos($data, ":", $i + 2);
                    if ($i === false) {
                        return null;
                    }
                    // skip String, quotes and ;
                    $i += 2 + $strlen + 1;
                    if ($data[$i] != ";") {
                        return null;
                    }
                    break;
                case "b":
                case "i":
                case "d":
                    // boolean, integer or double *
                    // skip value to sepearator
                    $i = strpos($data, ";", $i);
                    if ($i === false) {
                        return null;
                    }
                    break;
                case "a":
                    /* array *
                    // find array start
                    $i = strpos($data, "{", $i);
                    if ($i === false) {
                        return null;
                    }
                    // remember nesting
                    $depth++;
                    break;
                case "N":
                    /* null *
                    // skip to end
                    $i = strpos($data, ";", $i);
                    if ($i === false) {
                        return null;
                    }
                    break;
                default:
                    /* any other elements are not wanted *
                    return null;
            }
        }
        // check unterminated arrays
        if ($depth > 0) {
            return null;
        }
        return unserialize($data);*/
    }
    /**
     * Applies changes to PHP configuration.
     *
     * @return void
     */
    public void configure()
    {
    	//Unsupported
    }
    /**
     * Check whether PHP configuration matches our needs.
     *
     * @return void
     */
    public void checkConfiguration()
    {
    	//Unsupported
    }
    /**
     * Checks request and fails with fatal error if something problematic is found
     * @param response 
     * @param GLOBALS 
     * @param pmaResponse 
     *
     * @return void
     */
    public void checkRequest(HttpServletRequest request, HttpServletResponse response, Globals GLOBALS, Response pmaResponse)
    {
        if (!empty(request.getParameter("GLOBALS")) ) {
            fatalError(request, response, GLOBALS, pmaResponse, __("GLOBALS overwrite attempt"));
        }
        /**
         * protect against possible exploits - there is no need to have so much variables
         */
        if (request.getParameterMap().size() > 1000) {
            fatalError(request, response, GLOBALS, pmaResponse, __("possible exploit"));
        }
    }
    /**
     * Sign the sql query using hmac using the session token
     *
     * @param String $sqlQuery The sql query
     * @return String
     */
    public String signSqlQuery(String $sqlQuery, SessionMap session)
    {
    	return null; // TODO
        //return hash_hmac("sha256", $sqlQuery, (String)session.get(" HMAC_secret ") + (String)config.get("blowfish_secret"));
    }
    /**
     * Check that the sql query has a valid hmac signature
     *
     * @param String $sqlQuery  The sql query
     * @param String $signature The Signature to check
     * @return boolean
     */
    public boolean checkSqlQuerySignature(String $sqlQuery, String $signature, SessionMap session)
    {
    	return true; // TODO
        //String $hmac = hash_hmac("sha256", $sqlQuery, (String)session.get(" HMAC_secret ") + (String)config.get("blowfish_secret"));
        //return hash_equals($hmac, $signature);
    }
}
