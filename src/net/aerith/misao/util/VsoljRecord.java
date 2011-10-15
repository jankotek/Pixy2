/*
 * @(#)VsoljRecord.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;
import java.util.*;
import net.aerith.misao.xml.*;

/**
 * The <code>VsnetRecord</code> represents a magnitude data record in
 * the VSOLJ format.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 January 28
 */

public class VsoljRecord extends VsnetRecord {
	/**
	 * Constructs a <code>VsoljRecord</code> of the specified star
	 * and the magnitude data.
	 * @param name   the name of the star.
	 * @param record the XML element of the magnitude data.
	 */
	public VsoljRecord ( String name, XmlMagRecord record ) {
		super(name, record);
	}

	/**
	 * Gets the date string.
	 * @return the date string.
	 */
	public String getDate ( ) {
		// When to report to VSOLJ, the date and time must be converted into JST.
		JulianDay date = JulianDay.create(record.getDate());
		int accuracy = JulianDay.getAccuracy(record.getDate());

		if (JulianDay.isDecimalDay(accuracy))
			accuracy = JulianDay.getEquivalentAccuracy(accuracy);

		VsoljDate vsolj_date = new VsoljDate(date);
		return vsolj_date.getOutputString(JulianDay.FORMAT_MONTH_IN_NUMBER_WITHOUT_SPACE, accuracy);
	}
}
