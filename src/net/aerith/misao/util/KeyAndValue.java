/*
 * @(#)KeyAndValue.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;

/**
 * The <code>KeyAndValue</code> represents a set of a key and the 
 * value.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2000 June 24
 */

public class KeyAndValue {
	/**
	 * The key.
	 */
	private String key;

	/**
	 * The value.
	 */
	private String value;

	/**
	 * Constructs a <code>KeyAndValue</code> of the specified key and 
	 * value.
	 * @param initial_key the key.
	 * @param initial_value the value.
	 */
	public KeyAndValue ( String initial_key, String initial_value ) {
		key = initial_key;
		value = initial_value;
	}

	/**
	 * Gets the key.
	 * @return the key.
	 */
	public String getKey ( ) {
		return key;
	}

	/**
	 * Gets the value.
	 * @return the value.
	 */
	public String getValue ( ) {
		return value;
	}

	/**
	 * Returns a raw string representation of the state of this object,
	 * for debugging use. It should be invoked from <code>toString</code>
	 * method of the subclasses.
	 * @return a string representation of the state of this object.
	 */
	protected String paramString ( ) {
		return "key=" + key + ",value=" + value;
	}

	/**
	 * Returns a string representation of the state of this object,
	 * for debugging use.
	 * @return a string representation of the state of this object.
	 */
	public String toString ( ) {
		return getClass().getName() + "[" + paramString() + "]";
	}
}
