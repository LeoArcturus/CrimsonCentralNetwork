package com.crimsoncentral.util;

import java.util.HashMap;

public class CrimsonMap extends HashMap<Object, Object> {

	public HashMap<Integer, Object> order_values = new HashMap<Integer, Object>();
	public HashMap<Integer, Object> order_keys = new HashMap<Integer, Object>();
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void add(Object one, Object two) {

		this.put(one, two);
		order_keys.put(order_keys.size() + 1, one);
		order_values.put(order_values.size() + 1, two);

	}

}
