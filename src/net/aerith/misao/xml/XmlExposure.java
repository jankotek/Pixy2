/*
 * @(#)XmlExposure.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.xml;

/**
 * The <code>XmlExposure</code> is an application side implementation
 * of the class that the relaxer generated automatically.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2004 January 1
 */

public class XmlExposure extends net.aerith.misao.xml.relaxer.XmlExposure {
	/**
	 * Gets the exposure in second.
	 * @return the exposure in second.
	 */
	public double getValueInSecond ( ) {
		double seconds = (double)getContent();
		String unit = getUnit();
		if (unit.equals("minute"))
			seconds *= 60.0;
		if (unit.equals("hour"))
			seconds *= 3600.0;
		return seconds;
	}
}
