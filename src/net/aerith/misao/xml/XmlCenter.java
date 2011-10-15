/*
 * @(#)XmlCenter.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.xml;
import net.aerith.misao.util.Coor;

/**
 * The <code>XmlCenter</code> is an application side implementation
 * of the class that the relaxer generated automatically.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 November 26
 */

public class XmlCenter extends net.aerith.misao.xml.relaxer.XmlCenter {
	/**
	 * Gets the R.A. and Decl.
	 * @return the R.A. and Decl.
	 */
	public Coor getCoor ( ) {
		return Coor.create(getRa() + " " + getDecl());
	}
}
