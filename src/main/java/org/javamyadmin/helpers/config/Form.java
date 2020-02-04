package org.javamyadmin.helpers.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.javamyadmin.php.Callable;

import static org.javamyadmin.php.Php.*;

/**
 * Base class for forms, loads default configuration options, checks allowed
 * values etc.
 *
 * @package PhpMyAdmin
 */
public class Form {
    /**
     * Form name
     * @var string
     */
    public String name;

    /**
     * Arbitrary index, doesn"t affect class" behavior
     * @var int
     */
    public int index;

    /**
     * Form fields (paths), filled by {@link readFormPaths()}, indexed by field name
     * @var array
     */
    public Map<String, String> fields;

    /**
     * Stores default values for some fields (eg. pmadb tables)
     * @var array
     */
    public Map<String, Object> vDefault;

    /**
     * Caches field types, indexed by field names
     * @var array
     */
    private Map<String, String> _fieldsTypes;

    /**
     * ConfigFile instance
     * @var ConfigFile
     */
    private ConfigFile _configFile;

    /**
     * Constructor, reads default config values
     *
     * @param string     $formName Form name
     * @param array      $form     Form data
     * @param ConfigFile $cf       Config file instance
     * @param int        $index    arbitrary index, stored in Form.$index
     */
    public Form(
        String $formName,
        Map $form,
        ConfigFile $cf,
        Integer $index /*= null*/
    ) {
        this.index = $index;
        this._configFile = $cf;
        this.loadForm($formName, $form);
    }

    /**
     * Returns type of given option
     *
     * @param string $optionName path or field name
     *
     * @return string|null one of: boolean, integer, double, string, select, array
     */
    public String getOptionType(String $optionName)
    {
        String $key = ltrim($optionName.substring($optionName.indexOf("/")), "/"); 
        return this._fieldsTypes.get($key);
    }

    /**
     * Returns allowed values for select fields
     *
     * @param string $optionPath Option path
     *
     * @return array
     */
    public Map getOptionValueList(String $optionPath)
    {
        Object $value = this._configFile.getDbEntry($optionPath);
        if ($value == null) {
            trigger_error("$optionPath - select options not defined", E_USER_ERROR);
            return new HashMap<>();
        }
        if (! is_array($value)) {
            trigger_error("$optionPath - not a static value list", E_USER_ERROR);
            return new HashMap<>();
        }
        /* TODO
        // convert array("#", "a", "b") to array("a", "b")
        if (isset($value[0]) && $value[0] == "#") {
            // remove first element ("#")
            array_shift($value);
            // $value has keys and value names, return it
            return $value;
        }

        // convert value list array("a", "b") to array("a" => "a", "b" => "b")
        boolean $hasStringKeys = false;
        $keys = [];
        for ($i = 0, $nb = count($value); $i < $nb; $i++) {
            if (! isset($value[$i])) {
                $hasStringKeys = true;
                break;
            }
            $keys[] = is_bool($value[$i]) ? (int) $value[$i] : $value[$i];
        }
        if (! $hasStringKeys) {
            $value = array_combine($keys, $value);
        }

        // $value has keys and value names, return it
        
         */
        return (Map) $value;
    }

    private static int $groupCounter = 0;
    
    /**
     * array_walk callback function, reads path of form fields from
     * array (see docs for \PhpMyAdmin\Config\Forms\BaseForm.getForms)
     *
     * @param mixed $value  Value
     * @param mixed $key    Key
     * @param mixed $prefix Prefix
     *
     * @return void
     */
    private void _readFormPathsCallback(Object $value, Object $key, String $prefix)
    {
        if (is_array($value)) {
            $prefix += $key + "/";
            
            Form that = this;
            Callable callback = new Callable() {
    			@Override
    			public void apply(Object... args) {
    				that._readFormPathsCallback(args[0], (String)args[1], (String)args[2]);
    			}};
        
    		array_walk((Map)$value, callback, $prefix);
            
            return;
        }

        if (!($key instanceof Integer)) {
            this.vDefault.put($prefix + $key, $value);
            $value = $key;
        }
        // add unique id to group ends
        if ($value == ":group:end") {
            $value += ":" + $groupCounter++;
        }
        this.fields.put(Double.toString(Math.random()), $prefix + $value );
    }

    /**
     * Reads form paths to {@link $fields}
     *
     * @param array $form Form
     *
     * @return void
     */
    protected void readFormPaths(Map $form)
    {
        // flatten form fields" paths and save them to $fields
        this.fields = new LinkedHashMap<>();
        
        Form that = this;
        Callable callback = new Callable() {
			@Override
			public void apply(Object... args) {
				that._readFormPathsCallback(args[0], (String)args[1], (String)args[2]);
			}};
        
        array_walk($form, callback, "");

        // this.fields is an array of the form: [0..n] => "field path"
        // change numeric indexes to contain field names (last part of the path)
        Map<String, String> $paths = this.fields;
        this.fields = new LinkedHashMap<>();
        for (String $path : $paths.values()) {
            String $key = ltrim(
                $path.substring($path.indexOf("/")),
                "/"
            );
            this.fields.put($key, $path);
        }
        // now this.fields is an array of the form: "field name" => "field path"
    }

    /**
     * Reads fields" types to this._fieldsTypes
     *
     * @return void
     */
    protected void readTypes()
    {
        ConfigFile $cf = this._configFile;
        for (String $name : this.fields.keySet()) {
        	String $path = this.fields.get($name);
            if ($name.startsWith(":group:")) {
                this._fieldsTypes.put($name, "group");
                continue;
            }
            Object $v = $cf.getDbEntry($path);
            String $type;
            if ($v != null) {
                $type = is_array($v) ? "select" : (String)$v;
            } else {
                $type = gettype($cf.getDefault($path));
            }
            this._fieldsTypes.put($name, $type);
        }
    }

    /**
     * Reads form settings and prepares class to work with given subset of
     * config file
     *
     * @param string $formName Form name
     * @param array  $form     Form
     *
     * @return void
     */
    public void loadForm(String $formName, Map $form)
    {
        this.name = $formName;
        this.readFormPaths($form);
        this.readTypes();
    }
}
