/*
 * @(#)Designation.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;

/**
 * The <code>Designation</code> represents a designation of an 
 * asteroid, comet or satellite.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2002 October 5
 */

public class Designation {
	/**
	 * The designation type.
	 */
	protected int type = TYPE_ASTEROID_NUMBERED;

	/**
	 * The orbit type.
	 */
	protected int orbit = ORBIT_ASTEROID;

	/**
	 * The permanent number.
	 */
	protected int number = 0;

	/**
	 * The year of provisional designation.
	 */
	protected int year = 2000;

	/**
	 * The month code.
	 */
	protected String month_code = "A";

	/**
	 * The sub code in month.
	 */
	protected String sub_code = "A";

	/**
	 * The sequential number in month.
	 */
	protected int sequential = 0;

	/**
	 * The survey type.
	 */
	protected int survey = SURVEY_PL;

	/**
	 * The fragment.
	 */
	protected String fragment = "";

	/**
	 * The planet.
	 */
	protected int planet = PLANET_JUPITER;

	/**
	 * The designation type number which indicates a numbered asteroid.
	 */
	public final static int TYPE_ASTEROID_NUMBERED = 0;

	/**
	 * The designation type number which indicates a provisional 
	 * asteroid.
	 */
	public final static int TYPE_ASTEROID_PROVISIONAL = 1;

	/**
	 * The designation type number which indicates an asteroid of a
	 * special survey.
	 */
	public final static int TYPE_ASTEROID_SURVEY = 2;

	/**
	 * The designation type number which indicates a numbered comet.
	 */
	public final static int TYPE_COMET_NUMBERED = 10;

	/**
	 * The designation type number which indicates a provisional comet.
	 */
	public final static int TYPE_COMET_PROVISIONAL = 11;

	/**
	 * The designation type number which indicates a numbered 
	 * satellite.
	 */
	public final static int TYPE_SATELLITE_NUMBERED = 20;

	/**
	 * The designation type number which indicates a provisional 
	 * satellite.
	 */
	public final static int TYPE_SATELLITE_PROVISIONAL = 21;

	/**
	 * The orbit type number which indicates an asteroid.
	 */
	public final static int ORBIT_ASTEROID = 0;

	/**
	 * The orbit type number which indicates a comet.
	 */
	public final static int ORBIT_COMET = 1;

	/**
	 * The orbit type number which indicates a periodic comet.
	 */
	public final static int ORBIT_PERIODIC = 2;

	/**
	 * The orbit type number which indicates a lost comet.
	 */
	public final static int ORBIT_LOST = 3;

	/**
	 * The orbit type number which indicates a uncertain comet.
	 */
	public final static int ORBIT_UNCERTAIN = 4;

	/**
	 * The survey type number which indicates the Palomar-Leiden 
	 * Survey (1960).
	 */
	public final static int SURVEY_PL = 0;

	/**
	 * The survey type number which indicates the First Trojan Survey
	 * (1971).
	 */
	public final static int SURVEY_T1 = 1;

	/**
	 * The survey type number which indicates the Second Trojan Survey
	 * (1973).
	 */
	public final static int SURVEY_T2 = 2;

	/**
	 * The survey type number which indicates the Third Trojan Survey
	 * (1977).
	 */
	public final static int SURVEY_T3 = 3;

	/**
	 * The planet number which indicates Mercury.
	 */
	public final static int PLANET_MERCURY = 0;

	/**
	 * The planet number which indicates Venus.
	 */
	public final static int PLANET_VENUS = 1;

	/**
	 * The planet number which indicates Earth.
	 */
	public final static int PLANET_EARTH = 2;

	/**
	 * The planet number which indicates Mars.
	 */
	public final static int PLANET_MARS = 3;

	/**
	 * The planet number which indicates Jupiter.
	 */
	public final static int PLANET_JUPITER = 4;

	/**
	 * The planet number which indicates Saturn.
	 */
	public final static int PLANET_SATURN = 5;

	/**
	 * The planet number which indicates Uranus.
	 */
	public final static int PLANET_URANUS = 6;

	/**
	 * The planet number which indicates Neptune.
	 */
	public final static int PLANET_NEPTUNE = 7;

	/**
	 * The planet number which indicates Pluto.
	 */
	public final static int PLANET_PLUTO = 8;

	/**
	 * Constructs a <code>Designation</code> of the specified 
	 * type.
	 * @param type the designation type.
	 */
	public Designation ( int type ) {
		this.type = type;
	}

	/**
	 * Gets the designation type.
	 * @return the designation type.
	 */
	public int getType ( ) {
		return type;
	}

	/**
	 * Sets the orbit type.
	 * @param orbit the orbit type number.
	 */
	public void setOrbit ( int orbit ) {
		this.orbit = orbit;
	}

	/**
	 * Gets the orbit type.
	 * @return the orbit type number.
	 */
	public int getOrbit ( ) {
		return orbit;
	}

	/**
	 * Sets the permanent number.
	 * @param number the permanent number.
	 */
	public void setNumber ( int number ) {
		this.number = number;
	}

	/**
	 * Gets the permanent number.
	 * @return the permanent number.
	 */
	public int getNumber ( ) {
		return number;
	}

	/**
	 * Sets the year of provisional designation.
	 * @param year the year.
	 */
	public void setYear ( int year ) {
		this.year = year;
	}

	/**
	 * Gets the year of provisional designation.
	 * @return the year.
	 */
	public int getYear ( ) {
		return year;
	}

	/**
	 * Sets the month code.
	 * @param month_code the month code.
	 */
	public void setMonthCode ( String month_code ) {
		this.month_code = month_code;
	}

	/**
	 * Gets the month code.
	 * @return the month code.
	 */
	public String getMonthCode ( ) {
		return month_code;
	}

	/**
	 * Sets the sub code in month.
	 * @param sub_code the sub code in month.
	 */
	public void setSubCode ( String sub_code ) {
		this.sub_code = sub_code;
	}

	/**
	 * Gets the sub code in month.
	 * @return the sub code in month.
	 */
	public String getSubCode ( ) {
		return sub_code;
	}

	/**
	 * Sets the sequential number in month.
	 * @param sequential the sequential number in month.
	 */
	public void setSequentialNumber ( int sequential ) {
		this.sequential = sequential;
	}

	/**
	 * Gets the sequential number in month.
	 * @return the sequential number in month.
	 */
	public int getSequentialNumber ( ) {
		return sequential;
	}

	/**
	 * Sets the survey type.
	 * @param survey the survey type number.
	 */
	public void setSurvey ( int survey ) {
		this.survey = survey;
	}

	/**
	 * Gets the survey type.
	 * @return the survey type number.
	 */
	public int getSurvey ( ) {
		return survey;
	}

	/**
	 * Sets the fragment.
	 * @param fragment the fragment.
	 */
	public void setFragment ( String fragment ) {
		this.fragment = fragment;
	}

	/**
	 * Gets the fragment.
	 * @return the fragment.
	 */
	public String getFragment ( ) {
		return fragment;
	}

	/**
	 * Sets the planet.
	 * @param planet the planet number.
	 */
	public void setPlanet ( int planet ) {
		this.planet = planet;
	}

	/**
	 * Gets the planet.
	 * @return the planet number.
	 */
	public int getPlanet ( ) {
		return planet;
	}

	/**
	 * Returns a string representation of this designation.
	 */
	public String getOutputString ( ) {
		String designation = "";

		String orbit_code = "";
		switch (orbit) {
			case ORBIT_ASTEROID:
				orbit_code = "A";
				break;
			case ORBIT_COMET:
				orbit_code = "C";
				break;
			case ORBIT_PERIODIC:
				orbit_code = "P";
				break;
			case ORBIT_LOST:
				orbit_code = "D";
				break;
			case ORBIT_UNCERTAIN:
				orbit_code = "X";
				break;
		}

		String planet_name = "";
		switch (planet) {
			case PLANET_MERCURY:
				planet_name = "Mercury";
				break;
			case PLANET_VENUS:
				planet_name = "Venus";
				break;
			case PLANET_EARTH:
				planet_name = "Earth";
				break;
			case PLANET_MARS:
				planet_name = "Mars";
				break;
			case PLANET_JUPITER:
				planet_name = "Jupiter";
				break;
			case PLANET_SATURN:
				planet_name = "Saturn";
				break;
			case PLANET_URANUS:
				planet_name = "Uranus";
				break;
			case PLANET_NEPTUNE:
				planet_name = "Neptune";
				break;
			case PLANET_PLUTO:
				planet_name = "Pluto";
				break;
		}

		if (type == TYPE_ASTEROID_NUMBERED) {
			designation = "(" + String.valueOf(number) + ")";
		} else if (type == TYPE_ASTEROID_PROVISIONAL) {
			if (orbit != ORBIT_ASTEROID)
				designation = orbit_code + "/";

			designation += String.valueOf(year) + " ";
			designation += month_code + sub_code;

			if (sequential > 0)
				designation += String.valueOf(sequential);
		} else if (type == TYPE_ASTEROID_SURVEY) {
			String survey_id = "";
			switch (survey) {
				case SURVEY_PL:
					survey_id = "P-L";
					break;
				case SURVEY_T1:
					survey_id = "T-1";
					break;
				case SURVEY_T2:
					survey_id = "T-2";
					break;
				case SURVEY_T3:
					survey_id = "T-3";
					break;
			}

			designation = String.valueOf(number) + " " + survey_id;
		} else if (type == TYPE_COMET_NUMBERED) {
			designation = String.valueOf(number);
			designation += orbit_code;

			if (fragment.length() > 0)
				designation += "-" + fragment;
		} else if (type == TYPE_COMET_PROVISIONAL) {
			designation = orbit_code + "/";
			designation += String.valueOf(year) + " ";
			designation += month_code;
			designation += String.valueOf(sequential);

			if (fragment.length() > 0)
				designation += "-" + fragment;
		} else if (type == TYPE_SATELLITE_NUMBERED) {
			designation = planet_name + " ";

			for (int i = 0 ; i < number / 10 ; i++)
				designation += "X";

			switch (number % 10) {
				case 0:	break;
				case 1:	designation += "I";		break;
				case 2:	designation += "II";	break;
				case 3:	designation += "III";	break;
				case 4:	designation += "IV";	break;
				case 5:	designation += "V";		break;
				case 6:	designation += "VI";	break;
				case 7:	designation += "VII";	break;
				case 8:	designation += "VIII";	break;
				case 9:	designation += "IX";	break;
			}
		} else if (type == TYPE_SATELLITE_PROVISIONAL) {
			designation = "S/";
			designation += String.valueOf(year) + " ";
			designation += planet_name.substring(0, 1) + " ";
			designation += String.valueOf(sequential);
		}

		return designation;
	}
}
