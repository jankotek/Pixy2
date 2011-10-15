/*
 * @(#)XmlBaseCatalog.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.xml;

/**
 * The <code>XmlBaseCatalog</code> is an application side implementation
 * of the class that the relaxer generated automatically.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2004 November 24
 */

public class XmlBaseCatalog extends net.aerith.misao.xml.relaxer.XmlBaseCatalog {
	/**
	 * Returns true when the limiting magnitude to read stars from the 
	 * catalog is specified.
	 * @return true when the limiting magnitude to read stars from the 
	 * catalog is specified.
	 */
	public boolean isLimitingMagnitudeSpecified ( ) {
		if (getLimitingMag() != null)
			return true;

		return false;
	}

	/**
	 * Gets the proper limiting magnitude to read stars from the
	 * catalog.
	 * @return the proper limiting magnitude.
	 */
	public double getProperLimitingMag ( ) {
		if (getLimitingMag() != null)
			return (double)getLimitingMag().floatValue();

		return 0.0;
	}
}
