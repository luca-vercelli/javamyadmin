package org.javamyadmin.php;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Hash table and linked list implementation of the Map interface, with
 * predictable iteration order.
 * 
 * Just a shortcut
 *
 */
public class Array extends LinkedHashMap<Object, Object> implements Iterable<Entry<Object, Object>> {

	private static final long serialVersionUID = 6442347190410894897L;
	int lastInsert;

	public Array() {
	}

	public Array(int initialCapacity) {
		super(initialCapacity);
	}

	public Array(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public Array(Map<? extends Object, ? extends Object> items) {
		super(items);
	}

	public Array(int initialCapacity, float loadFactor, boolean accessOrder) {
		super(initialCapacity, loadFactor, accessOrder);
	}

	/**
	 * Add an element at the end, with an Integer key
	 * 
	 * @param value
	 */
	public void add(Object value) {
		this.put(lastInsert++, value);
	}

	/**
	 * Iterate over entries in iteration order
	 */
	@Override
	public Iterator<Entry<Object, Object>> iterator() {
		return entrySet().iterator();
	}

}
