/*
 * @(#)VsnetRecord.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;
import java.util.*;
import net.aerith.misao.xml.*;

/**
 * The <code>VsnetRecord</code> represents a magnitude data record in
 * the VSNET format.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2002 September 21
 */

public class VsnetRecord {
	/**
	 * The name of the star.
	 */
	protected String name;

	/**
	 * The XML element of the magnitude data.
	 */
	protected XmlMagRecord record;

	/**
	 * The format number.
	 */
	protected int format = FORMAT_ORIGINAL;

	/**
	 * The magnitude accuracy.
	 */
	protected int accuracy = ACCURACY_100TH;

	/**
	 * The observer's code.
	 */
	protected String observer_code = "Mis";

	/**
	 * The magnitude accuracy number which indicates to output in 0.1 
	 * mag order.
	 */
	public final static int ACCURACY_10TH = 0;

	/**
	 * The magnitude accuracy number which indicates to output in 0.01 
	 * mag order.
	 */
	public final static int ACCURACY_100TH = 1;

	/**
	 * The format number which indicates the original format.
	 */
	public final static int FORMAT_ORIGINAL = 0;

	/**
	 * The format number which indicates the extended format.
	 */
	public final static int FORMAT_EXTENDED = 1;

	/**
	 * The format number which indicates the extended format, with the
	 * instruments.
	 */
	public final static int FORMAT_EXTENDED_INSTRUMENTS = 2;

	/**
	 * Constructs a <code>VsnetRecord</code> of the specified star
	 * and the magnitude data.
	 * @param name   the name of the star.
	 * @param record the XML element of the magnitude data.
	 */
	public VsnetRecord ( String name, XmlMagRecord record ) {
		this.name = name.replace(' ', '_');
		this.record = record;
	}

	/**
	 * Sets the format.
	 * @param format the format number.
	 */
	public void setFormat ( int format ) {
		this.format = format;
	}

	/**
	 * Sets the magnitude accuracy.
	 * @param accuracy the magnitude accuracy number.
	 */
	public void setAccuracy ( int accuracy ) {
		this.accuracy = accuracy;
	}

	/**
	 * Gets the star name.
	 * @return the star name.
	 */
	public String getName ( ) {
		return name;
	}

	/**
	 * Gets the date string.
	 * @return the date string.
	 */
	public String getDate ( ) {
		JulianDay date = JulianDay.create(record.getDate());
		int accuracy = JulianDay.getAccuracy(record.getDate());

		if (JulianDay.isDecimalDay(accuracy) == false)
			accuracy = JulianDay.getEquivalentAccuracy(accuracy);

		return date.getOutputString(JulianDay.FORMAT_MONTH_IN_NUMBER_WITHOUT_SPACE, accuracy);
	}

	/**
	 * Gets the magnitude.
	 * @return the magnitude.
	 */
	public String getMag ( ) {
		String mag = "";
		XmlMag xml_mag = (XmlMag)record.getMag();
		if (xml_mag.getUpperLimit() != null)
			mag += "<";
		if (accuracy == ACCURACY_10TH) {
			mag += Format.formatDouble(xml_mag.getContent() * 10.0, 3, 3).trim();
		} else {
			mag += Format.formatDouble(xml_mag.getContent(), 5, 2).trim();
		}
		if (xml_mag.getInaccurate() != null)
			mag += ":";

		if (record.getFilter() != null)
			mag += record.getFilter();

		return mag;
	}

	/**
	 * Sets the observer's code.
	 * @param observer the observer's code.
	 */
	public void setObserverCode ( String observer ) {
		observer_code = observer.replace(' ', '_');
	}

	/**
	 * Gets the observer's code.
	 * @return the observer's code.
	 */
	public String getObserverCode ( ) {
		return observer_code;
	}

	/**
	 * Gets the chip.
	 * @return the chip.
	 */
	public String getChip ( ) {
		String chip = "";
		if (record.getChip() != null)
			chip = record.getChip().replace(' ', '_');
		if (chip.length() == 0)
			chip = "-";
		return chip;
	}

	/**
	 * Gets the catalog.
	 * @return the catalog.
	 */
	public String getCatalog ( ) {
		String catalog = "";
		if (record.getCatalog() != null)
			catalog = record.getCatalog().replace(' ', '_');
		if (catalog.length() == 0)
			catalog = "-";
		return catalog;
	}

	/**
	 * Gets the image observer.
	 * @return the image observer.
	 */
	public String getImageObserver ( ) {
		String image_observer = "";
		if (record.getObserver() != null)
			image_observer = record.getObserver().replace(' ', '_');
		if (image_observer.length() == 0)
			image_observer = "-";
		return image_observer;
	}

	/**
	 * Gets the instruments.
	 * @return the instruments.
	 */
	public String getInstruments ( ) {
		String instruments = "";
		if (record.getInstruments() != null)
			instruments = record.getInstruments().replace(' ', '_');
		if (instruments.length() == 0)
			instruments = "-";
		return instruments;
	}

	/**
	 * Returns a string representation of the state of this object
	 * in the specified format.
	 */
	public String getOutputString ( ) {
		String date = getDate();

		String mag = getMag();

		if (format == FORMAT_ORIGINAL)
			return name + " " + date + " " + mag + " " + observer_code;

		String chip = getChip();
		String catalog = getCatalog();
		String image_observer = getImageObserver();

		if (format == FORMAT_EXTENDED)
			return name + " " + date + " " + mag + " " + observer_code + " " + chip + " " + catalog + " " + image_observer;

		String instruments = getInstruments();

		return name + " " + date + " " + mag + " " + observer_code + " " + chip + " " + catalog + " " + image_observer + " " + instruments;
	}
}
