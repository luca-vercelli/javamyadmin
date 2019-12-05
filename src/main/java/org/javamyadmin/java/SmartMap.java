package org.javamyadmin.java;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * A better implementation of java.util.Properties
 * 
 * Properties present in Properties file are organized in Maps
 * 
 * @author lucav
 *
 */
public class SmartMap extends HashMap<String, Object> {

	private static final long serialVersionUID = 6814535397378322456L;

	public SmartMap() {
	}

	@Override
	public Object put(String key, Object value) {
		String[] keys = key.split("\\.");
		if (keys.length == 1) {
			return super.put(key, value);
		} else {
			multiputRev(value, keys);
			return value;
		}
	}

	@Override
	public Object get(Object key) {
		String[] keys = ((String) key).split("\\.");
		if (keys.length == 1) {
			return super.get(key);
		} else {
			return multiget(keys);
		}
	}

	@Override
	public Object remove(Object key) {
		String[] keys = ((String) key).split("\\.");
		if (keys.length == 1) {
			return super.remove(key);
		} else {
			return multiremove(keys);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void putAll(Map map) {
		Set<Entry> entries = map.entrySet();
		for (Entry entry : entries) {
			put((String) entry.getKey(), entry.getValue());
		}
	}

	/**
	 * Load properties from a .properties file
	 * 
	 * @param propertiesFilename
	 * @throws IOException
	 */
	public void load(String propertiesFilename) throws IOException {
		load(new FileReader(propertiesFilename));
	}

	/**
	 * Load properties from a .properties file
	 * 
	 * @param propertiesFile
	 * @throws IOException
	 */
	public void load(File propertiesFile) throws IOException {
		load(new FileReader(propertiesFile));
	}

	/**
	 * Load properties from a .properties file
	 * 
	 * @param propertiesReader
	 * @throws IOException
	 */
	public void load(Reader propertiesReader) throws IOException {
		Properties p = new Properties();
		p.load(propertiesReader);
		putAll(p);
	}

	/**
	 * Load properties from a .properties file
	 * 
	 * @param propertiesAsStream
	 * @throws IOException
	 */
	public void load(InputStream propertiesAsStream) throws IOException {
		Properties p = new Properties();
		p.load(propertiesAsStream);
		putAll(p);
	}

	/**
	 * Load properties from a Properties object
	 * 
	 * @param properties
	 * @throws IOException
	 * @throws InvalidPropertiesFormatException
	 */
	public void loadFromXML(InputStream xmlInputStream) throws InvalidPropertiesFormatException, IOException {
		Properties p = new Properties();
		p.loadFromXML(xmlInputStream);
		putAll(p);
	}

	/**
	 * Load from a .properties file in classpath
	 * 
	 * @param resource
	 * @throws IOException
	 */
	public void loadFromResource(String resource) throws IOException {
		try {
			InputStream is = SmartMap.class.getClassLoader().getResourceAsStream(resource);
			load(is);
			is.close();
		} catch (NullPointerException e) {
			throw new FileNotFoundException("File " + resource + " not found");
		} catch (IOException e) {
			throw new IOException("Error reading " + resource, e);
		}
	}

	/**
	 * array[k1][k2][k3]
	 */
	@SuppressWarnings({ "rawtypes" })
	public Object multiget(String... keys) {

		if (keys.length < 1) {
			throw new IllegalArgumentException("At least one key required");
		}

		Map map = this;
		// Hashmaps part
		for (int i = 0; i < keys.length - 1; ++i) {
			Object key = keys[i];
			if (map.containsKey(key)) {
				map = (Map) map.get(key);
			} else {
				return null;
			}
		}

		// value part
		return map.get(keys[keys.length - 1]);
	}

	/**
	 * array[k1][k2].remove([k3])
	 * 
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	public Object multiremove(String... keys) {

		if (keys.length < 1) {
			throw new IllegalArgumentException("At least one key required");
		}

		Map map = this;
		// Hashmaps part
		for (int i = 0; i < keys.length - 1; ++i) {
			Object key = keys[i];
			if (map.containsKey(key)) {
				map = (Map) map.get(key);
			} else {
				return null;
			}
		}

		// value part
		return map.remove(keys[keys.length - 1]);
	}

	/**
	 * array[k1][k2][k3] = val
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void multiput(Object... keysAndValue) {

		if (keysAndValue.length < 2) {
			throw new IllegalArgumentException("At least one key and one value required");
		}

		Map map = createStructure(keysAndValue, keysAndValue.length - 2);

		// value part
		map.put(keysAndValue[keysAndValue.length - 2], keysAndValue[keysAndValue.length - 1]);
	}

	/**
	 * array[k1][k2][k3] = val
	 * 
	 * Same behaviour of multiput(), with different syntax
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void multiputRev(Object value, String... keys) {

		if (keys.length < 1) {
			throw new IllegalArgumentException("At least one key required");
		}

		Map map = createStructure(keys, keys.length - 1);

		// value part
		map.put(keys[keys.length - 1], value);
	}

	/**
	 * Create missing Maps inside tree structure
	 * 
	 * @param keys
	 * @param length
	 * @return last Map in structure
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map createStructure(Object[] keys, int length) {
		Map map = this;
		for (int i = 0; i < length && i < keys.length; ++i) {
			Object key = keys[i];
			if (map.containsKey(key)) {
				if (!(map.get(key) instanceof Map)) {
					System.out.println("Warning! Property " + key + " is going to become a Map, was " + map.get(key).getClass().getName());
					Map newmap = new HashMap();
					map.put(key, newmap);
					map = newmap;
				}
				map = (Map) map.get(key);
			} else {
				Map newmap = new HashMap();
				map.put(key, newmap);
				map = newmap;
			}
		}
		return map;
	}
}
