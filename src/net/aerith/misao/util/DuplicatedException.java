/*
 * @(#)DuplicatedException.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;

/**
 * The <code>DuplicatedException</code> is an exception thrown if 
 * someting is duplicated.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 November 26
 */

public class DuplicatedException extends Exception {
	/**
	 * The duplicated object.
	 */
	protected Object duplicated;

	/**
	 * Constructs a <code>DuplicatedException</code>.
	 * @param object the duplicated object.
	 */
	public DuplicatedException ( Object object ) {
		duplicated = object;
	}

	/**
	 * Gets the duplicated object.
	 * @return the duplicated object.
	 */
	public Object getDuplicatedObject ( ) {
		return duplicated;
	}
}
