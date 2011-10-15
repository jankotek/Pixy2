/*
 * @(#)VariabilityChecker.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.pixy;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.database.*;
import net.aerith.misao.xml.*;

/**
 * The <code>VariabilityChecker</code> is a class to check if the
 * magnitude data of a star in the specified database folder shows
 * the variability or not.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2004 January 4
 */

public class VariabilityChecker {
	/**
	 * The number of policy which indicates not to consider the element
	 * value.
	 */
	public final static int POLICY_NOT_CONSIDERED = 1;

	/**
	 * The number of policy which indicates to consider the element 
	 * value, while the records with the empty value are regarded as
	 * to match any value.
	 */
	public final static int POLICY_CONSIDERED_INCLUDING_EMPTY = 2;

	/**
	 * The number of policy which indicates to consider the element 
	 * value, while the records with the empty value are ignored.
	 */
	public final static int POLICY_CONSIDERED_EXCLUDING_EMPTY = 3;

	/**
	 * The number of policy on blending which indicates to search
	 * variability based on measured magnitude.
	 */
	public final static int BLENDING_NOT_CONSIDERED = 1;

	/**
	 * The number of policy on blending which indicates to search 
	 * variability based on blended magnitude.
	 */
	public final static int BLENDING_BLENDED = 2;

	/**
	 * The number of policy on blending which indicates to reject
	 * blending stars.
	 */
	public final static int BLENDING_REJECTED = 3;

	/**
	 * The magnitude threshold.
	 */
	protected double magnitude_threshold = 0.5;

	/**
	 * The brighter limiting magnitude. Only stars whose brightest 
	 * magnitude is brighter than the specified magnitude are selected.
	 */
	protected double brighter_limiting_mag = 99.9;

	/**
	 * The period window size in days.
	 */
	protected double period_window_size = 1.0;

	/**
	 * The policy on the filter element.
	 */
	protected int policy_filter = POLICY_NOT_CONSIDERED;

	/**
	 * The policy on the chip element.
	 */
	protected int policy_chip = POLICY_NOT_CONSIDERED;

	/**
	 * The policy on the catalog element.
	 */
	protected int policy_catalog = POLICY_NOT_CONSIDERED;

	/**
	 * The policy on blending.
	 */
	protected int policy_blending = BLENDING_NOT_CONSIDERED;

	/**
	 * True if the discarded data are also considered.
	 */
	protected boolean include_discarded = false;

	/**
	 * True if the reported data are also considered.
	 */
	protected boolean include_reported = false;

	/**
	 * The pixels from edge to be ignored.
	 */
	protected int ignore_pixels_from_edge = 0;

	/**
	 * Constructs a <code>VariabilityChecker</code>.
	 */
	public VariabilityChecker ( ) {
	}

	/**
	 * Sets the magnitude threshold.
	 * @param threshold the magnitude threshold.
	 */
	public void setMagnitudeThreshold ( double threshold ) {
		magnitude_threshold = threshold;
	}

	/**
	 * Sets the brighter limiting magnitude. Only stars whose 
	 * brightest magnitude is brighter than the specified magnitude
	 * are selected.
	 * @param mag the brighter limiting magnitude.
	 */
	public void setBrighterLimitingMagnitude ( double mag ) {
		brighter_limiting_mag = mag;
	}

	/**
	 * Gets the brighter limiting magnitude.
	 * @return the brighter limiting magnitude.
	 */
	public double getBrighterLimitingMagnitude ( ) {
		return brighter_limiting_mag;
	}

	/**
	 * Sets the period window size in days.
	 * @param period the period window size in days.
	 */
	public void setPeriodWindowSize ( double period ) {
		period_window_size = period;
	}

	/**
	 * Sets the policy on the filter element.
	 * @param policy the number of policy.
	 */
	public void setFilterPolicy ( int policy ) {
		policy_filter = policy;
	}

	/**
	 * Sets the policy on the chip element.
	 * @param policy the number of policy.
	 */
	public void setChipPolicy ( int policy ) {
		policy_chip = policy;
	}

	/**
	 * Sets the policy on the catalog element.
	 * @param policy the number of policy.
	 */
	public void setCatalogPolicy ( int policy ) {
		policy_catalog = policy;
	}

	/**
	 * Gets the policy on blending.
	 * @return the number of policy on blending.
	 */
	public int getBlendingPolicy ( ) {
		return policy_blending;
	}

	/**
	 * Sets the policy on blending.
	 * @param policy the number of policy on blending.
	 */
	public void setBlendingPolicy ( int policy ) {
		policy_blending = policy;
	}

	/**
	 * Considers the discarded data.
	 */
	public void includeDiscarded ( ) {
		include_discarded = true;
	}

	/**
	 * Considers the reported data.
	 */
	public void includeReported ( ) {
		include_reported = true;
	}

	/**
	 * Sets the pixels from edge to be ignored.
	 * @param pixels the pixels from edge to be ignored.
	 */
	public void setIgnoredPixelsFromEdge ( int pixels ) {
		ignore_pixels_from_edge = pixels;
	}

	/**
	 * Checks if the specified list of magnitude records shows the 
	 * variability or not.
	 * @param records the list of magnitude records.
	 * @return the set of brightest and faintest magnitude element if 
	 * the records show the variability, otherwise null.
	 */
	public Variability check ( XmlMagRecord[] records ) {
		Hashtable hash_filter = new Hashtable();
		Hashtable hash_chip = new Hashtable();
		Hashtable hash_catalog = new Hashtable();

		int count_filter = 0;
		int count_chip = 0;
		int count_catalog = 0;

		for (int i = 0 ; i < records.length ; i++) {
			if (policy_filter != POLICY_NOT_CONSIDERED) {
				String filter = records[i].getFilter();
				if (filter != null) {
					hash_filter.put(filter, this);
					count_filter++;
				}
			}

			if (policy_chip != POLICY_NOT_CONSIDERED) {
				String chip = records[i].getChip();
				if (chip != null) {
					hash_chip.put(chip, this);
					count_chip++;
				}
			}

			if (policy_catalog != POLICY_NOT_CONSIDERED) {
				String catalog = records[i].getCatalog();
				if (catalog != null) {
					hash_catalog.put(catalog, this);
					count_catalog++;
				}
			}
		}

		if (count_filter == 0)
			hash_filter.put("(null)", this);
		if (count_chip == 0)
			hash_chip.put("(null)", this);
		if (count_catalog == 0)
			hash_catalog.put("(null)", this);

		Enumeration enum_filter = hash_filter.keys();
		while (enum_filter.hasMoreElements()) {
			String filter = (String)enum_filter.nextElement();
			if (filter.equals("(null)"))
				filter = null;

			Enumeration enum_chip = hash_chip.keys();
			while (enum_chip.hasMoreElements()) {
				String chip = (String)enum_chip.nextElement();
				if (chip.equals("(null)"))
					chip = null;

				Enumeration enum_catalog = hash_catalog.keys();
				while (enum_catalog.hasMoreElements()) {
					String catalog = (String)enum_catalog.nextElement();
					if (catalog.equals("(null)"))
						catalog = null;

					XmlMagRecord[] sub_records = createSubRecordArray(records, filter, chip, catalog);
					if (sub_records != null  &&  sub_records.length > 1) {
						if (isVariable(sub_records)) {
							XmlMagRecord brightest_mag = null;
							XmlMagRecord faintest_mag = null;
							for (int j = 0 ; j < sub_records.length ; j++) {
								if (sub_records[j].getMag().getUpperLimit() == null) {
									if (brightest_mag == null  ||  brightest_mag.getMag().getContent() > sub_records[j].getMag().getContent())
										brightest_mag = sub_records[j];
								}
								if (faintest_mag == null  ||  faintest_mag.getMag().getContent() < sub_records[j].getMag().getContent())
									faintest_mag = sub_records[j];
							}
							Variability variability = new Variability(null, brightest_mag, faintest_mag, sub_records);
							return variability;
						}
					}
				}
			}
		}

		return null;
	}

	/**
	 * Creates the sub array of the magnitude records whose filter, 
	 * chip and catalog element correspond to the specified strings.
	 * @param records the list of magnitude records.
	 * @param filter  the filter.
	 * @param chip    the chip.
	 * @param catalog the catalog.
	 * @return the sub array of the magnitude records.
	 */
	protected XmlMagRecord[] createSubRecordArray ( XmlMagRecord[] records, String filter, String chip, String catalog ) {
		Vector list = new Vector();
		for (int i = 0 ; i < records.length ; i++) {
			if (policy_filter == POLICY_NOT_CONSIDERED  ||
				(policy_filter == POLICY_CONSIDERED_INCLUDING_EMPTY  &&  filter == null)  ||
				(policy_filter == POLICY_CONSIDERED_INCLUDING_EMPTY  &&  records[i].getFilter() == null)  ||
				(policy_filter == POLICY_CONSIDERED_INCLUDING_EMPTY  &&  records[i].getFilter().equals(filter))  ||
				(policy_filter == POLICY_CONSIDERED_EXCLUDING_EMPTY  &&  filter != null  &&  records[i].getFilter() != null  &&  records[i].getFilter().equals(filter)))
			{
				if (policy_chip == POLICY_NOT_CONSIDERED  ||
					(policy_chip == POLICY_CONSIDERED_INCLUDING_EMPTY  &&  chip == null)  ||
					(policy_chip == POLICY_CONSIDERED_INCLUDING_EMPTY  &&  records[i].getChip() == null)  ||
					(policy_chip == POLICY_CONSIDERED_INCLUDING_EMPTY  &&  records[i].getChip().equals(chip))  ||
					(policy_chip == POLICY_CONSIDERED_EXCLUDING_EMPTY  &&  chip != null  &&  records[i].getChip() != null  &&  records[i].getChip().equals(chip)))
				{
					if (policy_catalog == POLICY_NOT_CONSIDERED  ||
						(policy_catalog == POLICY_CONSIDERED_INCLUDING_EMPTY  &&  catalog == null)  ||
						(policy_catalog == POLICY_CONSIDERED_INCLUDING_EMPTY  &&  records[i].getCatalog() == null)  ||
						(policy_catalog == POLICY_CONSIDERED_INCLUDING_EMPTY  &&  records[i].getCatalog().equals(catalog))  ||
						(policy_catalog == POLICY_CONSIDERED_EXCLUDING_EMPTY  &&  catalog != null  &&  records[i].getCatalog() != null  &&  records[i].getCatalog().equals(catalog)))
					{
						if (records[i].getDiscarded() == null  ||  include_discarded) {
							if (records[i].getReported() == null  ||  records[i].getReported().length == 0  ||  include_reported) {
								if (records[i].getPixelsFromEdge() == null  ||  records[i].getPixelsFromEdge().intValue() >= ignore_pixels_from_edge) {
									list.addElement(records[i]);
								}
							}
						}
					}
				}
			}
		}

		if (list.size() == 0)
			return null;

		XmlMagRecord[] sub_records = new XmlMagRecord[list.size()];
		for (int i = 0 ; i < list.size() ; i++)
			sub_records[i] = (XmlMagRecord)list.elementAt(i);

		return sub_records;
	}

	/**
	 * Checks if the specified list of magnitude records shows the 
	 * variability or not. 
	 * @param records the list of magnitude records.
	 * @return true if the specified list of magnitude records shows
	 * the variability.
	 */
	protected boolean isVariable ( XmlMagRecord[] records ) {
		// Sorts in order of the date.
		Array date_array = new Array(records.length);
		for (int i = 0 ; i < records.length ; i++)
			date_array.set(i, JulianDay.create(records[i].getDate()).getJD());
		ArrayIndex index = date_array.sortAscendant();

		// Initializes the magnitude range.
		Range[] ranges = new Range[records.length];
		for (int i = 0 ; i < records.length ; i++)
			ranges[i] = new Range((XmlMag)records[index.get(i)].getMag());

		// Calculates the magnitude range during the specified period.
		for (int i = 0 ; i < records.length - 1 ; i++) {
			double jd = JulianDay.create(records[index.get(i)].getDate()).getJD();
			for (int j = i + 1 ; j < records.length ; j++) {
				double days = JulianDay.create(records[index.get(j)].getDate()).getJD() - jd;
				if (days < period_window_size) {
					ranges[i].append((XmlMag)records[index.get(j)].getMag());
					ranges[j].append((XmlMag)records[index.get(i)].getMag());
				}
			}
		}

		boolean result = true;

		// Calculates the distance between any pairs of magnitude ranges,
		// and checks if the distance is larger than the threshold.
		if (result) {
			result = false;
			for (int i = 0 ; i < records.length - 1 ; i++) {
				for (int j = i + 1 ; j < records.length ; j++) {
					if (ranges[i].getDistanceFrom(ranges[j]) >= magnitude_threshold) {
						result = true;
						break;
					}
				}
			}
		}

		// Checks if the brighterst magnitude is brighter than the
		// specified brighter limiting magnitude.
		if (result) {
			result = false;
			for (int i = 0 ; i < records.length ; i++) {
				if (records[i].getMag().getUpperLimit() == null  &&  
					records[i].getMag().getContent() < brighter_limiting_mag) {
					result = true;
					break;
				}
			}
		}

		return result;
	}

	/**
	 * The <code>Range</code> represents the range of the magnitude.
	 */
	protected class Range {
		/** 
		 * The brightest magnitude.
		 */
		protected XmlMag brightest_mag;

		/** 
		 * The faintest magnitude.
		 */
		protected XmlMag faintest_mag;

		/** 
		 * Constructs a <code>Range</code> with the magnitude record.
		 * @param mag the magnitude record.
		 */
		public Range ( XmlMag mag ) {
			brightest_mag = mag;
			faintest_mag = mag;
		}

		/** 
		 * Appends the magnitude record.
		 * @param mag the magnitude record.
		 */
		public void append ( XmlMag mag ) {
			if (mag.getUpperLimit() == null) {
				if (mag.getContent() < brightest_mag.getContent()) {
					brightest_mag = mag;
				} else if (mag.getContent() > faintest_mag.getContent()) {
					faintest_mag = mag;
					if (brightest_mag.getUpperLimit() != null)
						brightest_mag = mag;
				}
			} else {
				if (brightest_mag.getUpperLimit() != null) {
					if (mag.getContent() > brightest_mag.getContent()) {
						brightest_mag = mag;
						faintest_mag = mag;
					}
				} else if (mag.getContent() > faintest_mag.getContent()) {
					faintest_mag = mag;
				}
			}
		}

		/** 
		 * Calculates the distance between the two magnitude ranges.
		 * It returns 0 if the two ranges overlap.
		 * @param range another range.
		 * @return the distance between the two magnitude ranges.
		 */
		public double getDistanceFrom ( Range range ) {
			if (faintest_mag.getUpperLimit() == null  &&
				range.brightest_mag.getContent() > faintest_mag.getContent())
				return range.brightest_mag.getContent() - faintest_mag.getContent();

			if (range.faintest_mag.getUpperLimit() == null  &&
				brightest_mag.getContent() > range.faintest_mag.getContent())
				return brightest_mag.getContent() - range.faintest_mag.getContent();

			// Overlap.
			return 0;
		}
	}
}
