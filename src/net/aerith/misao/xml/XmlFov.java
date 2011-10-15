/*
 * @(#)XmlFov.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.xml;

/**
 * The <code>XmlFov</code> is an application side implementation
 * of the class that the relaxer generated automatically.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 February 19
 */

public class XmlFov extends net.aerith.misao.xml.relaxer.XmlFov {
	/**
	 * Gets the field of view width in degree.
	 * @return the field of view width in degree.
	 */
	public double getWidthInDegree ( ) {
		double width = (double)getWidth();
		if (getUnit().equals("arcmin"))
			width /= 60.0;
		if (getUnit().equals("arcsec"))
			width /= 3600.0;
		return width;
	}

	/**
	 * Gets the field of view height in degree.
	 * @return the field of view height in degree.
	 */
	public double getHeightInDegree ( ) {
		double height = (double)getHeight();
		if (getUnit().equals("arcmin"))
			height /= 60.0;
		if (getUnit().equals("arcsec"))
			height /= 3600.0;
		return height;
	}
}
