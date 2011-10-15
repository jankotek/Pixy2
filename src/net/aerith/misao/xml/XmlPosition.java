/*
 * @(#)XmlPosition.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.xml;
import net.aerith.misao.util.*;

/**
 * The <code>XmlPosition</code> is an application side implementation
 * of the class that the relaxer generated automatically.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 September 18
 */

public class XmlPosition extends net.aerith.misao.xml.relaxer.XmlPosition {
	/**
	 * Construct an <code>XmlPosition</code>.
	 */
	public XmlPosition ( ) {
	}

	/**
	 * Construct an <code>XmlPosition</code> of the specified (x,y).
	 * @param position the (x,y) position.
	 */
	public XmlPosition ( Position position ) {
		String s = Format.formatDouble(position.getX(), 10, 7).trim();
		setX(Float.parseFloat(s));
		s = Format.formatDouble(position.getY(), 10, 7).trim();
		setY(Float.parseFloat(s));
	}

	/**
	 * Gets a string representing the (x,y) position.
	 * @return the string representing (x,y) position.
	 */
	public String getOutputString ( ) {
		return "(" + Format.formatDouble((double)getX(), 7, 4) + "," + Format.formatDouble((double)getY(), 7, 4) + ")";
	}
}
