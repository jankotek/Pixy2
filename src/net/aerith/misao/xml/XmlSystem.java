/*
 * @(#)XmlSystem.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.xml;
import net.aerith.misao.util.JulianDay;

/**
 * The <code>XmlSystem</code> is an application side implementation
 * of the class that the relaxer generated automatically.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 February 19
 */

public class XmlSystem extends net.aerith.misao.xml.relaxer.XmlSystem {
	/**
	 * Sets the examined date.
	 * @param jd the examined date.
	 */
	public void setExaminedJD ( JulianDay jd ) {
		setExaminedDate(jd.getOutputString(JulianDay.FORMAT_MONTH_IN_REDUCED, JulianDay.FORMAT_TO_SECOND));
	}

	/**
	 * Sets the modified date.
	 * @param jd the modified date.
	 */
	public void setModifiedJD ( JulianDay jd ) {
		setModifiedDate(jd.getOutputString(JulianDay.FORMAT_MONTH_IN_REDUCED, JulianDay.FORMAT_TO_SECOND));
	}
}
