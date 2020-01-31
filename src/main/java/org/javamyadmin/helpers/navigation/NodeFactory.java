package org.javamyadmin.helpers.navigation;


import static org.javamyadmin.php.Php.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.servlet.http.HttpServletRequest;

import org.javamyadmin.helpers.navigation.nodes.Node;
import org.javamyadmin.php.Globals;

/**
 * Node factory - instantiates Node objects or objects derived from the Node class
 *
 * @package PhpMyAdmin-Navigation
 */
public class NodeFactory {
	
	
	// TODO replace this class with Spring BeanFactory
	
	private final static String $namespace = "org.javamyadmin.helpers.navigation.nodes.";
	
    /**
     * Sanitizes the name of a Node class
     *
     * @param string $class The class name to be sanitized
     *
     * @return string
     */
    private static String sanitizeClass(String $class)
    {
        if (! $class.matches("^Node\\w*$")) {
            $class = "Node";
            trigger_error(
                String.format(
                    /* l10n: The word 'Node' must not be translated here */
                    __("Invalid class name '%1$s', using default of 'Node'"),
                    $class
                ),
                E_USER_ERROR
            );
        }

        return checkClass($class);
    }

    /**
     * Checks if a class exists and try to load it.
     * Will return the default class name back if the
     * file for some subclass is not available
     *
     * @param string $class The class name to check
     *
     * @return string
     */
    private static String checkClass(String $class)
    {
        $class = $namespace + $class;

        try {
        	Class.forName($class);
        } catch(ClassNotFoundException exc) {
        	trigger_error(
                    String.format(
                        __("Could not load class '%1$s'"),
                        $class
                    ),
                    E_USER_ERROR
                );
        }

        return $class;
    }

    /**
     * Instantiates a Node object
     *
     * @param string $class   The name of the class to instantiate
     * @param string $name    An identifier for the new node
     * @param int    $type    Type of node, may be one of CONTAINER or OBJECT
     * @param bool   $isGroup Whether this object has been created
     *                        while grouping nodes
     *
     * @return mixed
     */
    @SuppressWarnings("unchecked")
	public static Node getInstance(
        String $class /*= "Node"*/,
        String $name /*= "default"*/,
        int $type /*= Node.OBJECT*/,
        boolean $isGroup /*= false*/,
        HttpServletRequest httpRequest, Globals GLOBALS
    ) {
        $class = sanitizeClass($class);
        Class<Node> clazz;
        Constructor<Node> constructor = null;
    	try {
			clazz = (Class<Node>) Class.forName($class);
		} catch (ClassNotFoundException e1) {
			throw new IllegalStateException(e1);
		}
    	
    	try {
                
        	
        	try {
        		// This is for NodeContainer's
        		constructor = clazz.getConstructor(String.class, HttpServletRequest.class, Globals.class);
        		
        	} catch (NoSuchMethodException e) {
				// DO NOTHING
				e.printStackTrace();
			} catch (SecurityException e) {
				// DO NOTHING
			} catch (IllegalArgumentException e) {
				// DO NOTHING
			}
        	if (constructor != null) {
        		return constructor.newInstance($name, httpRequest, GLOBALS);
        	}
        	
        	constructor = clazz.getConstructor(String.class, int.class, boolean.class, HttpServletRequest.class, Globals.class); 
			return constructor.newInstance($name, $type, $isGroup, httpRequest, GLOBALS);
		} catch (InstantiationException e) {
			throw new IllegalStateException(e);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e);
		} catch (IllegalArgumentException e) {
			throw new IllegalStateException(e);
		} catch (InvocationTargetException e) {
			throw new IllegalStateException(e);
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException(e);
		} catch (SecurityException e) {
			throw new IllegalStateException(e);
		}
    }
    
    public static Node getInstance(String $class, String $name, HttpServletRequest httpRequest, Globals GLOBALS) {
    	return getInstance($class, $name, Node.OBJECT, false, httpRequest, GLOBALS);
    }
    
    public static Node getInstance(String $class, HttpServletRequest httpRequest, Globals GLOBALS) {
    	return getInstance($class, "default", Node.OBJECT, false, httpRequest, GLOBALS);
    }
    
    public static Node getInstance(HttpServletRequest httpRequest, Globals GLOBALS) {
    	return getInstance("Node", "default", Node.OBJECT, false, httpRequest, GLOBALS);
    }
}
