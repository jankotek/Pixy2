/*
 * @(#)DocumentIncompleteException.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.xml;

/**
 * The <code>DocumentIncompleteException</code> is an exception thrown
 * if some of the required elements in the XML document are not 
 * recorded.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 February 19
 */

public class DocumentIncompleteException extends Exception {
	/**
	 * Constructs a <code>DocumentIncompleteException</code>.
	 * @param tag the empty tag.
	 */
	public DocumentIncompleteException ( String tag ) {
		super("<" + tag + ">");
	}
}
