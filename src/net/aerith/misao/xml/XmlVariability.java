/*
 * @(#)XmlVariability.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.xml;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;

/**
 * The <code>XmlVariability</code> is an application side implementation
 * of the class that the relaxer generated automatically.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 November 26
 */

public class XmlVariability extends net.aerith.misao.xml.relaxer.XmlVariability {
	/**
	 * Construct an <code>XmlVariability</code>.
	 */
	public XmlVariability ( ) {
	}

	/**
	 * Construct an <code>XmlVariability</code>.
	 * @param variability the variability.
	 */
	public XmlVariability ( Variability variability ) {
		XmlRecord record = new XmlRecord(variability.getStar());
		addRecord(record);

		if (variability.getIdentifiedStar() != null) {
			record = new XmlRecord(variability.getIdentifiedStar());
			addRecord(record);
		}

		XmlMagRecord[] mag_records = variability.getMagnitudeRecords();
		setMagRecord(mag_records);
	}

	/**
	 * Gets the variability.
	 * @return the variability.
	 */
	public Variability getVariability ( ) {
		CatalogStar star = null;
		CatalogStar identified_star = null;

		XmlRecord[] records = (XmlRecord[])getRecord();
		for (int i = 0 ; i < records.length ; i++) {
			try {
				CatalogStar s = (CatalogStar)records[i].createStar();
				if (i == 0)
					star = s;
				else
					identified_star = s;
			} catch ( ClassNotFoundException exception ) {
				System.err.println(exception);
			} catch ( IllegalAccessException exception ) {
				System.err.println(exception);
			} catch ( InstantiationException exception ) {
				System.err.println(exception);
			}
		}

		Variability variability = new Variability(star, (XmlMagRecord[])getMagRecord());
		variability.setIdentifiedStar(identified_star);

		return variability;
	}
}
