/*
 * @(#)Variability.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.xml;
import java.util.ArrayList;
import net.aerith.misao.util.star.CatalogStar;
import net.aerith.misao.util.*;

/**
 * The <code>Variability</code> represents a set of a catalog star
 * object, and the XML magnitude elements of the maximum and minimum.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2007 May 20
 */

public class Variability {
	/**
	 * The star.
	 */
	protected CatalogStar star;

	/**
	 * The typical identified star.
	 */
	protected CatalogStar identified_star;

	/**
	 * The brightest magnitude.
	 */
	protected XmlMagRecord brightest_mag;

	/**
	 * The faintest magnitude.
	 */
	protected XmlMagRecord faintest_mag;

	/**
	 * The all magnitude records.
	 */
	protected XmlMagRecord[] records;

	/**
	 * Constructs a <code>Variability</code>.
	 * @param star    the star object.
	 * @param records the magnitude records.
	 */
	public Variability ( CatalogStar star, XmlMagRecord[] records ) {
		this.star = star;
		this.records = records;

		identified_star = null;

		brightest_mag = null;
		faintest_mag = null;

		for (int j = 0 ; j < records.length ; j++) {
			if (records[j].getMag().getUpperLimit() == null) {
				if (brightest_mag == null  ||  brightest_mag.getMag().getContent() > records[j].getMag().getContent())
					brightest_mag = records[j];
			}
			if (faintest_mag == null  ||  faintest_mag.getMag().getContent() < records[j].getMag().getContent())
				faintest_mag = records[j];
		}

		if (brightest_mag == null)
			brightest_mag = faintest_mag;
	}

	/**
	 * Constructs a <code>Variability</code>.
	 * @param star          the star object.
	 * @param brightest_mag the brightest magnitude.
	 * @param faintest_mag  the faintest magnitude.
	 * @param records       the all magnitude records.
	 */
	public Variability ( CatalogStar star, XmlMagRecord brightest_mag, XmlMagRecord faintest_mag, XmlMagRecord[] records ) {
		this.star = star;
		this.brightest_mag = brightest_mag;
		this.faintest_mag = faintest_mag;
		this.records = records;

		identified_star = null;
	}

	/**
	 * Gets the star object.
	 * @return the star object.
	 */
	public CatalogStar getStar ( ) {
		return star;
	}

	/**
	 * Sets the star object.
	 * @param star the star object.
	 */
	public void setStar ( CatalogStar star ) {
		this.star = star;
	}

	/**
	 * Gets the typical identified star object.
	 * @return the typical identified star object.
	 */
	public CatalogStar getIdentifiedStar ( ) {
		return identified_star;
	}

	/**
	 * Sets the typical identified star object.
	 * @param star the typical identified star object.
	 */
	public void setIdentifiedStar ( CatalogStar star ) {
		identified_star = star;
	}

	/**
	 * Gets the brightest magnitude.
	 * @return the brightest magnitude.
	 */
	public XmlMagRecord getBrightestMagnitude ( ) {
		return brightest_mag;
	}

	/**
	 * Gets the faintest magnitude.
	 * @return the faintest magnitude.
	 */
	public XmlMagRecord getFaintestMagnitude ( ) {
		return faintest_mag;
	}

	/**
	 * Gets the magnitude range.
	 * @return the magnitude range.
	 */
	public double getMagnitudeRange ( ) {
		return (double)(faintest_mag.getMag().getContent() - brightest_mag.getMag().getContent());
	}

	/**
	 * Adds a magnitude record.
	 * @param record a magnitude record.
	 */
	public void addMagnitudeRecord ( XmlMagRecord record ) {
		ArrayList list = new ArrayList();
		for (int i = 0 ; i < records.length ; i++)
			list.add(records[i]);

		list.add(record);

		records = new XmlMagRecord[list.size()];
        records = (XmlMagRecord[])list.toArray(records);

		brightest_mag = null;
		faintest_mag = null;

		for (int j = 0 ; j < records.length ; j++) {
			if (records[j].getMag().getUpperLimit() == null) {
				if (brightest_mag == null  ||  brightest_mag.getMag().getContent() > records[j].getMag().getContent())
					brightest_mag = records[j];
			}
			if (faintest_mag == null  ||  faintest_mag.getMag().getContent() < records[j].getMag().getContent())
				faintest_mag = records[j];
		}

		if (brightest_mag == null)
			brightest_mag = faintest_mag;
	}

	/**
	 * Merges magnitude records.
	 * @param new_records new magnitude records to merge.
	 */
	public void mergeMagnitudeRecords ( XmlMagRecord[] new_records ) {
		ArrayList list = new ArrayList();
		for (int i = 0 ; i < records.length ; i++)
			list.add(records[i]);

		// Finds the same record.
		for (int j = 0 ; j < new_records.length ; j++) {
			boolean found = false;

			for (int i = 0 ; i < records.length ; i++) {
				if (records[i].getID().equals(new_records[j].getID())  &&
					records[i].getName().equals(new_records[j].getName())) {
					found = true;
					break;
				}
			}

			if (! found)
				list.add(new_records[j]);
		}

		records = new XmlMagRecord[list.size()];
        records = (XmlMagRecord[])list.toArray(records);

		brightest_mag = null;
		faintest_mag = null;

		for (int j = 0 ; j < records.length ; j++) {
			if (records[j].getMag().getUpperLimit() == null) {
				if (brightest_mag == null  ||  brightest_mag.getMag().getContent() > records[j].getMag().getContent())
					brightest_mag = records[j];
			}
			if (faintest_mag == null  ||  faintest_mag.getMag().getContent() < records[j].getMag().getContent())
				faintest_mag = records[j];
		}

		if (brightest_mag == null)
			brightest_mag = faintest_mag;
	}

	/**
	 * Gets the magnitude records.
	 * @return the magnitude records.
	 */
	public XmlMagRecord[] getMagnitudeRecords ( ) {
		return records;
	}

	/**
	 * Gets the number of observations.
	 * @return the number of observations.
	 */
	public int getObservations ( ) {
		return records.length;
	}

	/**
	 * Gets the number of positive observations.
	 * @return the number of positive observations.
	 */
	public int getPositiveObservations ( ) {
		int count = 0;

		for (int i = 0 ; i < records.length ; i++) {
			if (records[i].getMag().getUpperLimit() == null)
				count++;
		}

		return count;
	}

	/**
	 * Gets the first date.
	 * @return the first date.
	 */
	public String getFirstDate ( ) {
		String first_jd_str = null;
		double first_jd = 0.0;
		for (int i = 0 ; i < records.length ; i++) {
			double jd = JulianDay.create(records[i].getDate()).getJD();
			if (first_jd_str == null  ||  jd < first_jd) {
				first_jd_str = records[i].getDate();
				first_jd = jd;
			}
		}
		return first_jd_str;
	}

	/**
	 * Gets the last date.
	 * @return the last date.
	 */
	public String getLastDate ( ) {
		String last_jd_str = null;
		double last_jd = 0.0;
		for (int i = 0 ; i < records.length ; i++) {
			double jd = JulianDay.create(records[i].getDate()).getJD();
			if (last_jd_str == null  ||  jd > last_jd) {
				last_jd_str = records[i].getDate();
				last_jd = jd;
			}
		}
		return last_jd_str;
	}

	/**
	 * Gets the arc in days.
	 * @return the arc in days.
	 */
	public int getArcInDays ( ) {
		if (records.length == 0)
			return 0;

		JulianDay first = JulianDay.create(getFirstDate());
		JulianDay last = JulianDay.create(getLastDate());

		double first_jd = new JulianDay(first.getYear(), first.getMonth(), first.getDay()).getJD();
		double last_jd = new JulianDay(last.getYear(), last.getMonth(), last.getDay()).getJD();

		return (int)(last_jd - first_jd) + 1;
	}
}
