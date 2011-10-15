/*
 * @(#)MpcFormatRecord.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;
import java.util.*;
import net.aerith.misao.xml.*;

/**
 * The <code>MpcFormatRecord</code> represents an astrometric data 
 * record in the MPC format.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2002 October 5
 */

public class MpcFormatRecord {
	/**
	 * The designation.
	 */
	protected Designation designation;

	/**
	 * The XML element of the position data.
	 */
	protected XmlPositionRecord record;

	/**
	 * The observation type.
	 */
	protected int type = TYPE_PHOTOGRAPHIC;

	/**
	 * The magnitude accuracy.
	 */
	protected int accuracy = ACCURACY_EMPTY;

	/**
	 * The magnitude band.
	 */
	protected int band = BAND_EMPTY;

	/**
	 * The observatory code.
	 */
	protected String code = "XXX";

	/**
	 * The observation type number which indicates a photographic 
	 * observation.
	 */
	public final static int TYPE_PHOTOGRAPHIC = 0;

	/**
	 * The observation type number which indicates a CCD observation.
	 */
	public final static int TYPE_CCD = 1;

	/**
	 * The magnitude accuracy number which indicates not to output the
	 * magnitude.
	 */
	public final static int ACCURACY_EMPTY = 0;

	/**
	 * The magnitude accuracy number which indicates to output in 0.1 
	 * mag order.
	 */
	public final static int ACCURACY_10TH = 1;

	/**
	 * The magnitude accuracy number which indicates to output in 0.01 
	 * mag order.
	 */
	public final static int ACCURACY_100TH = 2;

	/**
	 * The magnitude band number which indicates not to output the 
	 * band.
	 */
	public final static int BAND_EMPTY = 0;

	/**
	 * The magnitude band number which indicates a B-band observation.
	 */
	public final static int BAND_B = 1;

	/**
	 * The magnitude band number which indicates a V-band observation.
	 */
	public final static int BAND_V = 2;

	/**
	 * The magnitude band number which indicates a R-band observation.
	 */
	public final static int BAND_R = 3;

	/**
	 * The magnitude band number which indicates a I-band observation.
	 */
	public final static int BAND_I = 4;

	/**
	 * The magnitude band number which indicates a total magnitude
	 */
	public final static int BAND_TOTAL = 10;

	/**
	 * The magnitude band number which indicates a nuclear magnitude.
	 */
	public final static int BAND_NUCLEAR = 11;

	/**
	 * Constructs a <code>MpcFormatRecord</code> of the specified 
	 * position data.
	 * @param record the XML element of the position data.
	 */
	public MpcFormatRecord ( XmlPositionRecord record ) {
		this.record = record;
	}

	/**
	 * Sets the designation.
	 * @param designation the designation.
	 */
	public void setDesignation ( Designation designation ) {
		this.designation = designation;
	}

	/**
	 * Sets the observation type.
	 * @param type the observation type number.
	 */
	public void setObservationType ( int type ) {
		this.type = type;
	}

	/**
	 * Sets the magnitude accuracy.
	 * @param accuracy the magnitude accuracy number.
	 */
	public void setMagnitudeAccuracy ( int accuracy ) {
		this.accuracy = accuracy;
	}

	/**
	 * Sets the magnitude band.
	 * @param band the magnitude band number.
	 */
	public void setMagnitudeBand ( int band ) {
		this.band = band;
	}

	/**
	 * Sets the observatory code.
	 * @param code the observatory code.
	 */
	public void setObservatoryCode ( String code ) {
		this.code = code;
	}

	/**
	 * Gets the packed designation.
	 * @return the packed designation.
	 */
	public String getPackedDesignation ( ) {
		String object = "             ";

		if (designation == null)
			return object;

		String orbit_code = "";
		switch (designation.getOrbit()) {
			case Designation.ORBIT_ASTEROID:
				orbit_code = "A";
				break;
			case Designation.ORBIT_COMET:
				orbit_code = "C";
				break;
			case Designation.ORBIT_PERIODIC:
				orbit_code = "P";
				break;
			case Designation.ORBIT_LOST:
				orbit_code = "D";
				break;
			case Designation.ORBIT_UNCERTAIN:
				orbit_code = "X";
				break;
		}

		String planet_name = "";
		switch (designation.getPlanet()) {
			case Designation.PLANET_MERCURY:
				planet_name = "Mercury";
				break;
			case Designation.PLANET_VENUS:
				planet_name = "Venus";
				break;
			case Designation.PLANET_EARTH:
				planet_name = "Earth";
				break;
			case Designation.PLANET_MARS:
				planet_name = "Mars";
				break;
			case Designation.PLANET_JUPITER:
				planet_name = "Jupiter";
				break;
			case Designation.PLANET_SATURN:
				planet_name = "Saturn";
				break;
			case Designation.PLANET_URANUS:
				planet_name = "Uranus";
				break;
			case Designation.PLANET_NEPTUNE:
				planet_name = "Neptune";
				break;
			case Designation.PLANET_PLUTO:
				planet_name = "Pluto";
				break;
		}

		char packed_code = (char)('A' + designation.getYear() / 100 - 10);
		String packed_year = String.valueOf(packed_code);
		packed_year += Format.formatIntZeroPadding(designation.getYear() % 100, 2);

		String packed_sequential = Format.formatIntZeroPadding(designation.getSequentialNumber(), 2);
		if (designation.getSequentialNumber() >= 100) {
			packed_code = (char)('A' + designation.getSequentialNumber() / 10 - 10);
			if (designation.getSequentialNumber() >= 360)
				packed_code = (char)('a' + designation.getSequentialNumber() / 10 - 36);
			packed_sequential = String.valueOf(packed_code);
			packed_sequential += String.valueOf(designation.getSequentialNumber() % 10);
		}

		if (designation.getType() == Designation.TYPE_ASTEROID_NUMBERED) {
			object = Format.formatIntZeroPadding(designation.getNumber(), 5);
			object += "        ";
		} else if (designation.getType() == Designation.TYPE_ASTEROID_PROVISIONAL) {
			if (designation.getOrbit() == Designation.ORBIT_ASTEROID)
				object = "     ";
			else
				object = "    " + orbit_code;

			object += packed_year;
			object += designation.getMonthCode();
			object += packed_sequential;
			object += designation.getSubCode();
			object += " ";
		} else if (designation.getType() == Designation.TYPE_ASTEROID_SURVEY) {
			String survey_id = "";
			switch (designation.getSurvey()) {
				case Designation.SURVEY_PL:
					survey_id = "PL";
					break;
				case Designation.SURVEY_T1:
					survey_id = "T1";
					break;
				case Designation.SURVEY_T2:
					survey_id = "T2";
					break;
				case Designation.SURVEY_T3:
					survey_id = "T3";
					break;
			}

			object = "     ";
			object += survey_id + "S";
			object += Format.formatIntZeroPadding(designation.getNumber(), 4);
			object += " ";
		} else if (designation.getType() == Designation.TYPE_COMET_NUMBERED) {
			object = Format.formatIntZeroPadding(designation.getNumber(), 4);
			object += orbit_code;
			object += "      ";

			if (designation.getFragment().length() == 0)
				object += " ";
			else
				object += designation.getFragment().toLowerCase();

			object += " ";
		} else if (designation.getType() == Designation.TYPE_COMET_PROVISIONAL) {
			object = "    " + orbit_code;

			object += packed_year;
			object += designation.getMonthCode();
			object += packed_sequential;

			if (designation.getFragment().length() == 0)
				object += "0";
			else
				object += designation.getFragment().toLowerCase();

			object += " ";
		} else if (designation.getType() == Designation.TYPE_SATELLITE_NUMBERED) {
			object = planet_name.substring(0, 1);
			object += Format.formatIntZeroPadding(designation.getNumber(), 3);
			object += "S";
			object += "        ";
		} else if (designation.getType() == Designation.TYPE_SATELLITE_PROVISIONAL) {
			object = "    S";
			object += packed_year;
			object += planet_name.substring(0, 1);
			object += packed_sequential;
			object += "0";
			object += " ";
		}

		return object;
	}

	/**
	 * Returns a string representation of the state of this object
	 * in the proper format.
	 */
	public String getOutputString ( ) {
		String object = "             ";

		if (designation != null)
			object = getPackedDesignation();

		String data = object;

		data += " ";

		if (type == TYPE_CCD)
			data += "C";
		else
			data += " ";

		JulianDay jd = JulianDay.create(record.getDate());
		int date_accuracy = JulianDay.getAccuracy(record.getDate());
		if (JulianDay.isDecimalDay(date_accuracy) == false)
			date_accuracy = JulianDay.getEquivalentAccuracy(date_accuracy);
		String date = jd.getOutputString(JulianDay.FORMAT_MONTH_IN_NUMBER, date_accuracy);
		while (date.length() < 17)
			date += " ";
		date = date.substring(0, 17);
		data += date;

		Coor coor = ((XmlCoor)record.getCoor()).getCoor();
		data += coor.getOutputStringTo100mArcsecWithoutUnit() + " ";

		data += "         ";

		String mag = "";
		XmlMag xml_mag = (XmlMag)record.getMag();
		if (accuracy == ACCURACY_10TH) {
			mag = Format.formatDouble(xml_mag.getContent(), 4, 2) + " ";
		} else if (accuracy == ACCURACY_100TH) {
			mag = Format.formatDouble(xml_mag.getContent(), 5, 2);
		} else {
			mag = "     ";
		}
		data += mag;

		switch (band) {
			case BAND_B:
				data += "B";
				break;
			case BAND_V:
				data += "V";
				break;
			case BAND_R:
				data += "R";
				break;
			case BAND_I:
				data += "I";
				break;
			case BAND_TOTAL:
				data += "T";
				break;
			case BAND_NUCLEAR:
				data += "N";
				break;
			default:
				data += " ";
				break;
		}

		data += "      ";

		while (code.length() < 3)
			code += " ";
		data += code.substring(0, 3);

		return data;
	}
}
