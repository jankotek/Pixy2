/*
 * @(#)OutOfBoundsException.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;

/**
 * The <code>OutOfBoundsException</code> is an exception thrown if the
 * specified index is out of the proper range. Although the 
 * <code>IndexOutOfBoundsException</code> is a subclass of 
 * <code>RuntimeException</code>, this is not a runtime exception.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2000 June 24
 */

public class OutOfBoundsException extends Exception {
	/**
	 * Constructs an <code>OutOfBoundsException</code>.
	 */
	public OutOfBoundsException ( ) {
	}
}
