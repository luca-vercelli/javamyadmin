package org.javamyadmin.helpers.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.javamyadmin.helpers.Core;
import org.javamyadmin.php.Callable;
import org.javamyadmin.php.Globals;
import org.javamyadmin.php.Php.SessionMap;
import org.springframework.beans.factory.annotation.Autowired;

import static org.javamyadmin.php.Php.*;

/**
 * Config file management class.
 * Stores its data in $_SESSION
 *
 * @package PhpMyAdmin
 */
public class ConfigFile {
    /**
     * Stores default PMA config from config.default.php
     * @var array
     */
    private Map<String, Object> _defaultCfg;

    /**
     * Stores allowed values for non-standard fields
     * @var array
     */
    private Map<String, Object> _cfgDb;

    /**
     * Stores original PMA config, not modified by user preferences
     * @var array|null
     */
    private Map<String, Object> _baseCfg;

    /**
     * Whether we are currently working in PMA Setup context
     * @var bool
     */
    private boolean _isInSetup;

    /**
     * Keys which will be always written to config file
     * @var array
     */
    private Set<String> _persistKeys = new HashSet<>();

    /**
     * Changes keys while updating config in {@link updateWithGlobalConfig()}
     * or reading by {@link getConfig()} or {@link getConfigArray()}
     * @var array
     */
    private Map<String, String> _cfgUpdateReadMapping = new HashMap<>();

    /**
     * Key filter for {@link set()}
     * @var array|null
     */
    private Set<String> _setFilter;

    /**
     * Instance id (key in $_SESSION array, separate for each server -
     * ConfigFile{server id})
     * @var string
     */
    private String _id;

    /**
     * Result for {@link _flattenArray()}
     * @var array|null
     */
    private Map<String, Object> _flattenArrayResult;

    @Autowired
    private SessionMap $_SESSION;
    @Autowired
    private Globals $GLOBALS;
    
    /**
     * Constructor
     *
     * @param array|null $baseConfig base configuration read from
     *                               {@link PhpMyAdmin\Config.$base_config},
     *                               use only when not in PMA Setup
     */
    public ConfigFile(Map<String, Object> $baseConfig /*= null*/)
    {
        // load default config values
        Map<String, Object> $cfg = this._defaultCfg;
        // FIXME must load config include ROOT_PATH + "libraries/config.default.php";

        // load additional config information
        // TODO this._cfgDb = include ROOT_PATH + "libraries/config.values.php";

        // apply default values overrides
        Map<String, Object> overrides = (Map) this._cfgDb.get("_overrides");
        if (overrides != null && !overrides.isEmpty()) {
        	Set<Entry<String, Object>> entries = ((Map)this._cfgDb.get("_overrides")).entrySet(); 
            for (Entry<String, Object> entry : entries) {
                Core.arrayWrite(entry.getKey(), $cfg, entry.getValue());
            }
        }

        this._baseCfg = $baseConfig;
        this._isInSetup = $baseConfig == null;
        this._id = "ConfigFile" + $GLOBALS.getServer();
        if (!($_SESSION.containsKey(this._id))) {
            $_SESSION.put(this._id, new HashMap<>());
        }
    }

    /**
     * Sets names of config options which will be placed in config file even if
     * they are set to their default values (use only full paths)
     *
     * @param array $keys the names of the config options
     *
     * @return void
     */
    public void setPersistKeys(Collection<String> $keys)
    {
        // checking key presence is much faster than searching so move values
        // to keys
        this._persistKeys = new HashSet<>($keys);
    }

    /**
     * Returns flipped array set by {@link setPersistKeys()}
     *
     * @return array
     */
    public Set<String> getPersistKeysMap()
    {
        return this._persistKeys;
    }

    /**
     * By default ConfigFile allows setting of all configuration keys, use
     * this method to set up a filter on {@link set()} method
     *
     * @param array|null $keys array of allowed keys or null to remove filter
     *
     * @return void
     */
    public void setAllowedKeys(Collection<String> $keys)
    {
        if ($keys == null) {
            this._setFilter = null;
            return;
        }
        // checking key presence is much faster than searching so move values
        // to keys
        this._setFilter = new HashSet<>($keys);
    }

    /**
     * Sets path mapping for updating config in
     * {@link updateWithGlobalConfig()} or reading
     * by {@link getConfig()} or {@link getConfigArray()}
     *
     * @param array $mapping Contains the mapping of "Server/config options"
     *                       to "Server/1/config options"
     *
     * @return void
     */
    public void setCfgUpdateReadMapping(Map<String, String> $mapping)
    {
        this._cfgUpdateReadMapping = $mapping;
    }

    /**
     * Resets configuration data
     *
     * @return void
     */
    public void resetConfigData()
    {
        $_SESSION.put(this._id, new HashMap<>());
    }

    /**
     * Sets configuration data (overrides old data)
     *
     * @param array $cfg Configuration options
     *
     * @return void
     */
    public void setConfigData(Map<String, Object> $cfg)
    {
        $_SESSION.put(this._id, $cfg);
    }

    /**
     * Sets config value
     *
     * @param string $path          Path
     * @param mixed  $value         Value
     * @param string $canonicalPath Canonical path
     *
     * @return void
     */
    public void set(String $path, Object $value, String $canonicalPath /*= null*/)
    {
        if ($canonicalPath == null) {
            $canonicalPath = this.getCanonicalPath($path);
        }
        // apply key whitelist
        if (this._setFilter != null
            && ! (this._setFilter.contains($canonicalPath))
        ) {
            return;
        }
        // if the path isn"t protected it may be removed
        if ((this._persistKeys.contains($canonicalPath))) {
            Core.arrayWrite($path, (Map) $_SESSION.get(this._id), $value);
            return;
        }

        Object $defaultValue = this.getDefault($canonicalPath);
        boolean $removePath = $value == $defaultValue;
        if (this._isInSetup) {
            // remove if it has a default value or is empty
            $removePath = $removePath
                || (empty($value) && empty($defaultValue));
        } else {
            // get original config values not overwritten by user
            // preferences to allow for overwriting options set in
            // config.inc.php with default values
            Object $instanceDefaultValue = Core.arrayRead(
                $canonicalPath,
                this._baseCfg
            );
            // remove if it has a default value and base config (config.inc.php)
            // uses default value
            $removePath = $removePath
                && ($instanceDefaultValue.equals($defaultValue));
        }
        if ($removePath) {
            Core.arrayRemove($path, (Map) $_SESSION.get(this._id));
            return;
        }

        Core.arrayWrite($path, (Map) $_SESSION.get(this._id), $value);
    }

    public void set(String $path, Object $value) {
    	set($path, $value, null);
    }
    
    /**
     * Flattens multidimensional array, changes indices to paths
     * (eg. "key/subkey").
     * Used as array_walk() callback.
     *
     * @param mixed $value  Value
     * @param mixed $key    Key
     * @param mixed $prefix Prefix
     *
     * @return void
     */
    private void _flattenArray(Object $value, String $key, String $prefix)
    {
        // no recursion for numeric arrays
        if ($value instanceof Map) {
            $prefix += $key + "/";
            ConfigFile that = this;
            Callable callback = new Callable() {
				@Override
				public void apply(Object... args) {
					that._flattenArray(args[0], (String)args[1], (String)args[2]);
				}};
            array_walk((Map)$value, callback, $prefix);
        } else {
            this._flattenArrayResult.put($prefix + $key, $value);
        }
    }

    /**
     * Returns default config in a flattened array
     *
     * @return array
     */
    public Map<String, Object> getFlatDefaultConfig()
    {
        this._flattenArrayResult = new HashMap<>();
        ConfigFile that = this;
        Callable callback = new Callable() {
			@Override
			public void apply(Object... args) {
				that._flattenArray(args[0], (String)args[1], (String)args[2]);
			}};
        array_walk(this._defaultCfg, callback, "");
        Map<String, Object> $flatConfig = this._flattenArrayResult;
        this._flattenArrayResult = null;
        return $flatConfig;
    }

    /**
     * Updates config with values read from given array
     * (config will contain differences to defaults from config.defaults.php).
     *
     * @param array $cfg Configuration
     *
     * @return void
     */
    public void updateWithGlobalConfig(Map<String, Object> $cfg)
    {
        // load config array and flatten it
        this._flattenArrayResult = new HashMap<>();
        ConfigFile that = this;
        Callable callback = new Callable() {
			@Override
			public void apply(Object... args) {
				that._flattenArray(args[0], (String)args[1], (String)args[2]);
			}};
        array_walk($cfg, callback, "");
        Map<String, Object> $flatConfig = this._flattenArrayResult;
        this._flattenArrayResult = null;

        // save values map for translating a few user preferences paths,
        // should be complemented by code reading from generated config
        // to perform inverse mapping
        for (Object $path : $flatConfig.keySet()) {
        	Object $value = $flatConfig.get($path);
            if (this._cfgUpdateReadMapping.containsKey($path)) {
                $path = this._cfgUpdateReadMapping.get($path);
            }
            this.set((String)$path, $value, (String)$path);
        }
    }

    /**
     * Returns config value or $default if it"s not set
     *
     * @param string $path    Path of config file
     * @param mixed  $default Default values
     *
     * @return mixed
     */
    public Object get(String $path, Object $default /*= null*/)
    {
        return Core.arrayRead($path, (Map) $_SESSION.get(this._id), $default);
    }

    public Object get(String $path) {
    	return get($path, null);
    }
    
    /**
     * Returns default config value or $default it it"s not set ie. it doesn"t
     * exist in config.default.php ($cfg) and config.values.php
     * ($_cfg_db["_overrides"])
     *
     * @param string $canonicalPath Canonical path
     * @param mixed  $default       Default value
     *
     * @return mixed
     */
    public Object getDefault(String $canonicalPath, Object $default /*= null*/)
    {
        return Core.arrayRead($canonicalPath, this._defaultCfg, $default);
    }

    public Object getDefault(String $canonicalPath) {
    	return getDefault($canonicalPath, null);
    }
    
    /**
     * Returns config value, if it"s not set uses the default one; returns
     * $default if the path isn"t set and doesn"t contain a default value
     *
     * @param string $path    Path
     * @param mixed  $default Default value
     *
     * @return mixed
     */
    public Object getValue(String $path, Object $default /*= null*/)
    {
        Object $v = Core.arrayRead($path, (Map) $_SESSION.get(this._id), null);
        if ($v != null) {
            return $v;
        }
        $path = this.getCanonicalPath($path);
        return this.getDefault($path, $default);
    }
    
    public Object getValue(String $path) {
    	return getValue($path, null);
    }

    /**
     * Returns canonical path
     *
     * @param string $path Path
     *
     * @return string
     */
    public String getCanonicalPath(String $path)
    {
        return $path.replaceAll("^Servers/([\\d]+)/", "Servers/1/");
    }

    /**
     * Returns config database entry for $path
     *
     * @param string $path    path of the variable in config db
     * @param mixed  $default default value
     *
     * @return mixed
     */
    public Object getDbEntry(String $path, Object $default /*= null*/)
    {
        return Core.arrayRead($path, this._cfgDb, $default);
    }

    public Object getDbEntry(String $path) {
    	return getDbEntry($path, null);
    }
    
    /**
     * Returns server count
     *
     * @return int
     */
    public int getServerCount()
    {
    	Map servers = (Map) multiget($_SESSION, this._id, "Servers");
        return servers != null
            ? servers.size()
            : 0;
    }

    /**
     * Returns server list
     *
     * @return array|null
     */
    public Map getServers()
    {
    	return (Map) multiget($_SESSION, this._id, "Servers");
    }

    /**
     * Returns DSN of given server
     *
     * @param integer $server server index
     *
     * @return string
     */
    public String getServerDSN(int $server)
    {
    	return null; // Unsupported
    }

    /**
     * Returns server name
     *
     * @param int $id server index
     *
     * @return string
     */
    public String getServerName(int $id)
    {
        if (multiget($_SESSION, this._id, "Servers", $id) == null) {
            return "";
        }
        String $verbose = (String) this.get("Servers/$id/verbose");
        if (! empty($verbose)) {
            return $verbose;
        }
        String $host = (String) this.get("Servers/$id/host");
        return empty($host) ? "localhost" : $host;
    }

    /**
     * Removes server
     *
     * @param int $server server index
     *
     * @return void
     */
    public void removeServer(int $server)
    {
        if (multiget($_SESSION, this._id, "Servers", $server) == null) {
            return;
        }
        int $lastServer = this.getServerCount();

        for (int $i = $server; $i < $lastServer; $i++) {
        	multiput($_SESSION, this._id, "Servers", $i,
        	multiget($_SESSION, this._id, "Servers", $i +1));
        }
        multiremove($_SESSION, this._id, "Servers", $lastServer);

        if (multiget($_SESSION, this._id, "ServerDefault") != null
            && multiget($_SESSION, this._id, "ServerDefault").equals($lastServer)
        ) {
        	multiremove($_SESSION, this._id, "ServerDefault");
        }
    }

    /**
     * Returns configuration array (full, multidimensional format)
     *
     * @return array
     */
    public Map<String, Object> getConfig()
    {
		Map $c = (Map) $_SESSION.get(this._id);
        for (Entry<String, String> $entry : this._cfgUpdateReadMapping.entrySet()) {
        	String $mapTo = $entry.getKey();
        	String $mapFrom = $entry.getValue();
            // if the key $c exists in $map_to
            if (Core.arrayRead($mapTo, $c) != null) {
                Core.arrayWrite($mapTo, $c, Core.arrayRead($mapFrom, $c));
                Core.arrayRemove($mapFrom, $c);
            }
        }
        return $c;
    }

    /**
     * Returns configuration array (flat format)
     *
     * @return array
     */
    public Map<String, Object> getConfigArray()
    {
        this._flattenArrayResult = new HashMap<>();
        ConfigFile that = this;
        Callable callback = new Callable() {
			@Override
			public void apply(Object... args) {
				that._flattenArray(args[0], (String)args[1], (String)args[2]);
			}};
        array_walk((Map)$_SESSION.get(this._id), callback, "");
        Map<String, Object> $c = this._flattenArrayResult;
        this._flattenArrayResult = null;

        Set<String> $persistKeys = new HashSet<>(this._persistKeys);
        $persistKeys.removeAll($c.keySet());
        
        for (String $k : $persistKeys) {
            $c.put($k, this.getDefault(this.getCanonicalPath($k)));
        }

        for (Entry<String, String> $entry : this._cfgUpdateReadMapping.entrySet()) {
        	String $mapTo = $entry.getKey();
        	String $mapFrom = $entry.getValue();
            if (! ($c.containsKey($mapFrom))) {
                continue;
            }
            $c.put($mapTo, $c.get($mapFrom));
            $c.remove($mapFrom);
        }
        return $c;
    }
}
