package com.tn.hiringprocess.common.logging;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Class ElapsedTimeMap - Implementation of Map that times how long values have
 * been idle in the Map. Every call to get(key) causes the timer to reset().
 * 
 
 */
public class ElapsedTimeMap implements Map {
	private Map map = Collections.synchronizedMap(new HashMap());

	/**
	 * Constructor for TimedMap
	 */
	public ElapsedTimeMap() {
		super();
	}

	/**
	 * Method containsValue.
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	@Override
	public boolean containsValue(Object value) {
		return this.map.containsValue(new TimedValue(value));
	}

	/**
	 * Method get. Causes a reset() of the timer.
	 * @see java.util.Map#get(java.lang.Object)
	 */
	@Override
	public Object get(Object key) {
		TimedValue tv = (TimedValue) this.map.get(key);
		if (tv != null) {
			tv.reset();
			return tv.value;
		}
		return null;
	}

	/**
	 * Method put.
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Object put(Object key, Object value) {
		return this.map.put(key, new TimedValue(value));
	}

	/**
	 * Method remove.
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	@Override
	public Object remove(Object key) {
		TimedValue tv = (TimedValue) this.map.remove(key);
		return (tv == null) ? null : tv.value;
	}

	/**
	 * Method values.
	 * @see java.util.Map#values()
	 */
	@Override
	public Collection values() {
		Set values = new HashSet();
		Collection rawValues = this.map.values();
		synchronized (this.map) {
			Iterator valueIt = rawValues.iterator();
			while (valueIt.hasNext()) {
				TimedValue tv = (TimedValue) valueIt.next();
				values.add(tv.value);
			}
		}
		return values;
	}

	/**
	 * Method size.
	 * @see java.util.Map#size()
	 */
	@Override
	public int size() {
		return this.map.size();
	}

	/**
	 * Method clear.
	 * @see java.util.Map#clear()
	 */
	@Override
	public void clear() {
		this.map.clear();
	}

	/**
	 * Method isEmpty.
	 * @see java.util.Map#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return this.map.isEmpty();
	}

	/**
	 * Method containsKey.
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	@Override
	public boolean containsKey(Object key) {
		return this.map.containsKey(key);
	}

	/**
	 * Method putAll.
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	@Override
	public void putAll(Map m) {
		throw new UnsupportedOperationException(
		"putAll(Map) not allowed on ElapsedTimeMap.");
	}

	/**
	 * Method entrySet - Only method that allows access to raw values. Values
	 * Objects returned are actually instances of TimedValue (wrapper around
	 * original value and timestamp).
	 * 
	 * @see java.util.Map#entrySet()
	 */
	@Override
	public Set entrySet() {
		Set entryCopies = new HashSet();
		Set entries = this.map.entrySet();
		synchronized (this.map) {
			Iterator entryIt = entries.iterator();
			while (entryIt.hasNext()) {
				entryCopies.add(entryIt.next());
			}
		}
		return entryCopies;
	}

	/**
	 * Method keySet.
	 * 
	 * @see java.util.Map#keySet()
	 */
	@Override
	public Set keySet() {
		Set keyCopies = new HashSet();
		Set keys = this.map.keySet();
		synchronized (this.map) {
			Iterator keyIt = keys.iterator();
			while (keyIt.hasNext()) {
				keyCopies.add(keyIt.next());
			}
		}
		return keyCopies;
	}

	/**
	 * Method removeExpired - Removes and returns all values that have been idle
	 * for as long or longer than the input timeout.
	 * 
	 * @param timeout
	 * @return Collection
	 */
	public Collection removeExpired(long timeout) {
		Set expiredValues = new HashSet();
		Collection allValues = this.map.values();
		synchronized (this.map) {
			Iterator valueIt = allValues.iterator();
			while (valueIt.hasNext()) {
				TimedValue tv = (TimedValue) valueIt.next();
				if (tv.getElapsedTime() >= timeout) {
					expiredValues.add(tv.value);
					valueIt.remove();
				}
			}
		}
		return expiredValues;
	}

	private class TimedValue {
		private Object value;

		private long startTime;

		private TimedValue(Object value) {
			this.value = value;
			this.startTime = System.currentTimeMillis();
		}

		/**
		 * Method equals.
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object thatObj) {
			if (thatObj instanceof TimedValue) {
				TimedValue that = (TimedValue) thatObj;
				return that.value.equals(this.value);
			}
			return false;
		}

		public long getElapsedTime() {
			return System.currentTimeMillis() - this.startTime;
		}

		public void reset() {
			this.startTime = System.currentTimeMillis();
		}
	}

	@Override
	public Object getOrDefault(Object key, Object defaultValue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void forEach(BiConsumer action) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void replaceAll(BiFunction function) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object putIfAbsent(Object key, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean remove(Object key, Object value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean replace(Object key, Object oldValue, Object newValue) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object replace(Object key, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object computeIfAbsent(Object key, Function mappingFunction) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object computeIfPresent(Object key, BiFunction remappingFunction) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object compute(Object key, BiFunction remappingFunction) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object merge(Object key, Object value, BiFunction remappingFunction) {
		// TODO Auto-generated method stub
		return null;
	}
}
