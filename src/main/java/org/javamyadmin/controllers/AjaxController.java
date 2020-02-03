package org.javamyadmin.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;
import javax.servlet.ServletException;

import org.javamyadmin.helpers.Config;
import org.javamyadmin.helpers.ListDatabase;
import org.javamyadmin.helpers.Message;
import org.javamyadmin.php.Globals;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.javamyadmin.php.Php.*;

@RestController
public class AjaxController extends AbstractController {

    /**
     * @var Config
     */
    private Config config;

    /**
     * AjaxController constructor.
     *
     * @param Response          $response Response instance
     * @param DatabaseInterface $dbi      DatabaseInterface instance
     * @param Template          $template Template object
     * @param Config            $config   Config instance
     */
    public AjaxController()
    {
        this.config = Globals.getConfig();
    }

    /*
     *  TODO use plain Spring methods that return JSON
     *  instead of response.response()
     */
    
    /**
     * @return array JSON
     */
    @PostMapping("/ajax/list-databases")
    public void databases() throws IOException, SQLException, ServletException, NamingException
    {
    	super.prepareResponse();
    	
        Map<String, ListDatabase> retval = new HashMap<>();
        retval.put("databases", GLOBALS.getDblist().getDatabases());
        
        response.addJSON(retval);
		response.response();
    }

    /**
     * @param array $params Request parameters
     * @return array JSON
     */
    @PostMapping("/ajax/list-tables/{database}")
    public void tables(@PathVariable String database)
    	throws IOException, SQLException, ServletException, NamingException
    {
    	super.prepareResponse();
    	
        Map<String, Map<Integer, Map<String, String>>> retval = new HashMap<>();
        retval.put("tables", GLOBALS.getDbi().getTables(database));
        
        response.addJSON(retval);
		response.response();
    }

    /**
     * @param array $params Request parameters
     * @return array JSON
     */
    @PostMapping("/list-columns/{database}/{table}")
    public void columns(@PathVariable String database, @PathVariable String table)
    	throws IOException, SQLException, ServletException, NamingException
    {
    	super.prepareResponse();
    	
        Map<String, List<String>> retval = new HashMap<>();
        retval.put("columns", GLOBALS.getDbi().getColumnNames(database, table));

        response.addJSON(retval);
		response.response();
    }

    /**
     * @param array $params Request parameters
     * @return array JSON
     * @throws SQLException 
     * @throws IOException 
     * @throws NamingException 
     * @throws ServletException 
     */
    @PostMapping("/config-get")
    public void getConfig(@RequestParam String key)
    	throws IOException, SQLException, ServletException, NamingException
    {
    	super.prepareResponse();
    	
        if (empty(key)) {
            this.response.setRequestStatus(false);
            Map<String, Message> retval = new HashMap<>();
            retval.put("message", Message.error(null));
            response.addJSON(retval);
            
        } else {

            Map<String, Object> retval = new HashMap<>();
            retval.put("value", this.config.get(key));
            response.addJSON(retval);
        }

		response.response();
    }

    /**
     * @param array $params Request parameters
     * @return array
     */
    @PostMapping("/config-set")
    public void setConfig(@RequestParam String key, @RequestParam String value)
    	throws IOException, SQLException, ServletException, NamingException
    {
    	super.prepareResponse();
    	
        if (empty(key) || empty(value)) {
            this.response.setRequestStatus(false);
            Map<String, Message> retval = new HashMap<>();
            retval.put("message", Message.error(null));
            response.addJSON(retval);
        } else {

	        Message $result = this.config.setUserValue(
	            null,
	            key,
	            (String)json_decode(value)
	        );
	        Map<String, Object> $json = new HashMap<>();
	        if ($result != null) {
	            this.response.setRequestStatus(false);
	            $json.put("message", $result);
	        }
            response.addJSON($json);
        }

		response.response();
    }
    
}
