/*
 * @(#)VsoljDate.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;
import java.util.StringTokenizer;

/**
 * The <code>VsoljDate</code> represents a date and time of 
 * observation to report to VSOLJ (Variable Star Observers League in
 * Japan). It outputs the time in JST. In addition, when the time is 
 * before 9 o'clock, it outputs the hour from 0 o'clock of the 
 * previous day. For example, 2000 Jan. 23 04:56 JST will be output 
 * as <tt>2000 Jan. 22 28:56</tt>.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 March 10
 */

public class VsoljDate {
	/**
	 * The Julian Day.
	 */
	protected JulianDay julian_day;

	/**
	 * Constructs a <code>VsoljDate</code> of the specified date and
	 * time.
	 * @param jd the date and time.
	 */
	public VsoljDate ( JulianDay jd ) {
		julian_day = jd;
	}

	/**
	 * Returns a string representation of the state of this object
	 * in the specified format.
	 * @param month_format  the number of format to output month.
	 * @param date_accuracy the number of accuracy.
	 * @return a string representation of the state of this object.
	 */
	public String getOutputString ( int month_format, int date_accuracy ) {
		// JST, but before 9 o'clock, it must be output as in the previous day.
		JulianDay jd = new JulianDay(julian_day.getJD() + 9.0 / 24.0 - 9.0 / 24.0);

		String s = jd.getOutputString(month_format, date_accuracy);

		if (month_format == JulianDay.FORMAT_MONTH_IN_NUMBER) {
			// Such as "2000 01 22 28 56"
			StringTokenizer st = new StringTokenizer(s);
			s = st.nextToken() + " " + st.nextToken() + " " + st.nextToken() + " ";
			int hour = Integer.parseInt(st.nextToken()) + 9;
			s += Format.formatIntZeroPadding(hour, 2);
			while (st.hasMoreTokens())
				s += " " + st.nextToken();
		} else if (month_format == JulianDay.FORMAT_MONTH_IN_NUMBER_WITHOUT_SPACE) {
			// Such as "200001222856"
			String s1 = s.substring(0, 8);
			String s2 = s.substring(10);
			int hour = Integer.parseInt(s.substring(8, 10)) + 9;
			s = s1 + Format.formatIntZeroPadding(hour, 2) + s2;
		} else {
			// Such as "2000 Jan. 22 28:56", or "2000 Jan. 22.45678"
			int p = s.indexOf(':');
			if (p > 0) {
				String s1 = s.substring(0, p-2);
				String s2 = s.substring(p);
				int hour = Integer.parseInt(s.substring(p-2, p)) + 9;
				s = s1 + Format.formatIntZeroPadding(hour, 2) + s2;
			}
		}

		return s;
	}
}
