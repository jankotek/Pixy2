/*
 * @(#)ObservationRecord.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.xml;

/**
 * The <code>ObservationRecord</code> is a set of <code>XmlMagRecord</code>
 * and <code>XmlPositionRecord</code>.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 November 26
 */

public class ObservationRecord {
	/**
	 * The magnitude record.
	 */
	protected XmlMagRecord mag_record;

	/**
	 * The position record.
	 */
	protected XmlPositionRecord position_record;

	/**
	 * Construct an <code>ObservationRecord</code>.
	 * @param mag_record      the magnitude record.
	 * @param position_record the position record.
	 */
	public ObservationRecord ( XmlMagRecord mag_record, XmlPositionRecord position_record ) {
		this.mag_record = mag_record;
		this.position_record = position_record;
	}

	/**
	 * Gets the magnitude record.
	 * @return the magnitude record.
	 */
	public XmlMagRecord getMagRecord ( ) {
		return mag_record;
	}

	/**
	 * Gets the position record.
	 * @return the position record.
	 */
	public XmlPositionRecord getPositionRecord ( ) {
		return position_record;
	}
}
