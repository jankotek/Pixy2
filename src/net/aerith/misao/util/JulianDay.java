/*
 * @(#)JulianDay.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;
import java.util.*;

/**
 * The <code>JulianDay</code> represents a Julian Day.
 * <p>
 * It keeps the date and time value in two styles. One is the Julian 
 * Day in <tt>double</tt>. Another one is the set of year, month and
 * day in <tt>int</tt>, <tt>int</tt> and <tt>double</tt> repsectively.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2005 November 20
 */

public class JulianDay {
	/**
	 * The value of this date and time in Julian Day.
	 */
	protected double julian_day;

	/**
	 * The year value of this date and time.
	 */
	protected int year;

	/**
	 * The month value of this date and time.
	 */
	protected int month;

	/**
	 * The day value of this date and time.
	 */
	protected double day;

	/**
	 * The full spell output strings of months.
	 */
	protected final static String month_full_name[] = { "", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" };

	/**
	 * The reduced output strings of months.
	 */
	protected final static String month_reduced_name[] = { "", "Jan.", "Feb.", "Mar.", "Apr.", "May", "June", "July", "Aug.", "Sept.", "Oct.", "Nov.", "Dec." };

	/**
	 * The output codes of months.
	 */
	protected final static String month_code[] = { "", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

	/**
	 * The number of output accuracy type, which represents to output
	 * such a string as "2000 Jan 23.45678".
	 */
	public final static int FORMAT_DEFAULT = 0;

	/**
	 * The number of output accuracy type, which represents to output
	 * such a string as "2000 Jan 23.45678".
	 */
	public final static int FORMAT_DECIMALDAY_100000TH = 1;

	/**
	 * The number of output accuracy type, which represents to output
	 * such a string as "2000 Jan 23.4567".
	 */
	public final static int FORMAT_DECIMALDAY_10000TH = 2;

	/**
	 * The number of output accuracy type, which represents to output
	 * such a string as "2000 Jan 23.456".
	 */
	public final static int FORMAT_DECIMALDAY_1000TH = 3;

	/**
	 * The number of output accuracy type, which represents to output
	 * such a string as "2000 Jan 23.45".
	 */
	public final static int FORMAT_DECIMALDAY_100TH = 4;

	/**
	 * The number of output accuracy type, which represents to output
	 * such a string as "2000 Jan 23.4".
	 */
	public final static int FORMAT_DECIMALDAY_10TH = 5;

	/**
	 * The number of output accuracy type, which represents to output
	 * such a string as "2000 Jan 23".
	 */
	public final static int FORMAT_DECIMALDAY_1TH = 6;

	/**
	 * The number of output accuracy type, which represents to output
	 * such a string as "2000 Jan 23 01:23:45".
	 */
	public final static int FORMAT_TO_SECOND = 11;

	/**
	 * The number of output accuracy type, which represents to output
	 * such a string as "2000 Jan 23 01:23".
	 */
	public final static int FORMAT_TO_MINUTE = 12;

	/**
	 * The number of output accuracy type, which represents to output
	 * such a string as "2000 Jan 23 01h".
	 */
	public final static int FORMAT_TO_HOUR = 13;

	/**
	 * The number of output accuracy type, which represents to output
	 * such a string as "2000 Jan 23".
	 */
	public final static int FORMAT_TO_DAY = 14;

	/**
	 * The number of type to output month, which represents to output
	 * the full spell name such as "2000 January 23".
	 */
	public final static int FORMAT_MONTH_IN_FULL_SPELL = 101;

	/**
	 * The number of type to output month, which represents to output
	 * the reduced name such as "2000 Jan. 23".
	 */
	public final static int FORMAT_MONTH_IN_REDUCED = 102;

	/**
	 * The number of type to output month, which represents to output
	 * the code of month such as "2000 Jan 23".
	 */
	public final static int FORMAT_MONTH_IN_CODE = 103;

	/**
	 * The number of type to output month, which represents to output
	 * the month in number such as "2000 01 23".
	 */
	public final static int FORMAT_MONTH_IN_NUMBER = 104;

	/**
	 * The number of type to output month, which represents to output
	 * the month in number without space such as "20000123".
	 */
	public final static int FORMAT_MONTH_IN_NUMBER_WITHOUT_SPACE = 105;

	/**
	 * The number of type to output month, which represents to output
	 * the Julian Day such as "2451234.12345".
	 */
	public final static int FORMAT_JD = 106;

	/**
	 * Constructs a <code>JulianDay</code> at J2000.0.
	 */
	public JulianDay ( ) {
		this(Astro.JULIUS_2000);
	}

	/**
	 * Constructs a <code>JulianDay</code> of the specified Julian 
	 * Day.
	 * @param initial_jd the Julian Day.
	 */
	public JulianDay ( double initial_jd ) {
		julian_day = initial_jd;
		updateYearMonthDay();
	}

	/**
	 * Constructs a <code>JulianDay</code> of the specified year, 
	 * month and day.
	 * @param initial_year  the year of the date and time to construct.
	 * @param initial_month the month of the date and time to construct.
	 * @param initial_day   the day of the date and time to construct.
	 */
	public JulianDay ( int initial_year, int initial_month, double initial_day ) {
		year = initial_year;
		month = initial_month;
		day = initial_day;

		long a;
		if (initial_month < 3) {
			initial_year -= 1;
			initial_month += 12;
		}

		if (initial_year >= 1583  ||  (initial_year == 1582  &&  (initial_month >= 11  ||  (initial_month == 10  &&  initial_day >= 15.0)))) {
			// Gregorian calendar (since 1582 Oct. 15)
			a = (long)(365.25 * (double)initial_year) + (long)(initial_year / 400) - (long)(initial_year / 100) + (long)(30.59 * (double)(initial_month - 2));
			julian_day = (double)a + initial_day + 1721088.5;
		} else {
			// Julian calendar (until 1582 Oct. 4)
			a = (long)(365.25 * (double)initial_year) + (long)(30.59 * (double)(initial_month - 2));
			julian_day = (double)a + initial_day + 1721086.5;
			if (initial_year < 0)
				julian_day -= 1.0;
		}
	}

	/**
	 * Constructs a <code>JulianDay</code> of the specified year, 
	 * month, day, hour, minute, and second.
	 * @param initial_year   the year of the date and time to construct.
	 * @param initial_month  the month of the date and time to construct.
	 * @param initial_day    the day of the date and time to construct.
	 * @param initial_hour   the hour of the date and time to construct.
	 * @param initial_minute the minute of the date and time to construct.
	 * @param initial_second the second of the date and time to construct.
	 */
	public JulianDay ( int initial_year, int initial_month, int initial_day, int initial_hour, int initial_minute, double initial_second ) {
		this(initial_year, initial_month, (double)initial_day + (double)initial_hour / 24.0 + (double)initial_minute / 24.0 / 60.0 + initial_second / 24.0 / 3600.0);
	}

	/**
	 * Creates a <code>JulianDay</code> object from the specified 
	 * local time considering the time zone.
	 * @param date the <code>Date</code> object of the local time.
	 * @return the new <code>JulianDay</code> object.
	 */
	public static JulianDay create ( Date date ) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);

		TimeZone tz = calendar.getTimeZone();
		int milliseconds = calendar.get(Calendar.HOUR_OF_DAY) * 3600 + calendar.get(Calendar.MINUTE) * 60 + calendar.get(Calendar.SECOND);
		int offset = tz.getOffset(calendar.get(Calendar.ERA), calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.DAY_OF_WEEK), milliseconds);

		calendar.add(Calendar.SECOND, - offset / 1000);

		return new JulianDay(calendar.get(Calendar.YEAR), 
							 calendar.get(Calendar.MONTH) + 1,
							 calendar.get(Calendar.DAY_OF_MONTH),
							 calendar.get(Calendar.HOUR_OF_DAY),
							 calendar.get(Calendar.MINUTE),
							 calendar.get(Calendar.SECOND));
	}

	/**
	 * Creates a <code>JulianDay</code> object from the specified 
	 * string.
	 * @param string the string which represents the date and time.
	 * @return the new <code>JulianDay</code> object.
	 */
	public static JulianDay create ( String string ) {
		string = string.replace(':', ' ');
		StringTokenizer st = new StringTokenizer(string);
		if (st.countTokens() == 1) {
			double value = Double.parseDouble(string);
			if (value < 10000000.0) {
				// JD
				return new JulianDay(value);
			} else {
				int y = Integer.parseInt(string.substring(0, 4));
				int m = Integer.parseInt(string.substring(4, 6));
				if (string.length() == 8) {
					// yyyymmdd
					int d = Integer.parseInt(string.substring(6));
					return new JulianDay(y, m, (double)d);
				} else if (string.charAt(8) == '.') {
					// yyyymmdd.ddddd
					double d = Double.parseDouble(string.substring(6));
					return new JulianDay(y, m, d);
				} else {
					// yyyymmddhhmmss
					int d = Integer.parseInt(string.substring(6, 8));
					int h = Integer.parseInt(string.substring(8, 10));
					int mm = 0;
					double s = 0.0;
					if (string.length() >= 12)
						mm = Integer.parseInt(string.substring(10, 12));
					if (string.length() >= 14) {
						s = Double.parseDouble(string.substring(12, 14));
						if (string.length() >= 15  &&  string.charAt(14) == '.')
							s = Double.parseDouble(string.substring(12));
					}
					return new JulianDay(y, m, d, h, mm, s);
				}
			}
		} else if (st.countTokens() == 3) {
			int y = Integer.parseInt(st.nextToken());
			String m_str = st.nextToken();
			int m = 0;
			try {
				m = getMonth(m_str);
			} catch ( NoSuchElementException exception ) {
				m = Integer.parseInt(m_str);
			}
			double d = Double.parseDouble(st.nextToken());
			return new JulianDay(y, m, d);
		} else {
			int y = Integer.parseInt(st.nextToken());
			String m_str = st.nextToken();
			int m = 0;
			try {
				m = getMonth(m_str);
			} catch ( NoSuchElementException exception ) {
				m = Integer.parseInt(m_str);
			}
			int d = Integer.parseInt(st.nextToken());
			int h = Integer.parseInt(st.nextToken());
			int mm = 0;
			double s = 0.0;
			if (st.hasMoreTokens())
				mm = Integer.parseInt(st.nextToken());
			if (st.hasMoreTokens())
				s = Double.parseDouble(st.nextToken());
			return new JulianDay(y, m, d, h, mm, s);
		}
	}

	/**
	 * Gets a accuracy of the specified date string.
	 * @param string the string which represents the date and time.
	 * @return the format number of the date accuracy.
	 */
	public static int getAccuracy ( String string ) {
		string = string.replace(':', ' ');
		StringTokenizer st = new StringTokenizer(string);
		if (st.countTokens() == 1) {
			double value = Double.parseDouble(string);
			if (value < 10000000.0) {
				// JD
				if (string.indexOf('.') < 0)
					return FORMAT_DECIMALDAY_1TH;
				switch (string.length() - string.indexOf('.') - 1) {
					case 1:
						return FORMAT_DECIMALDAY_10TH;
					case 2:
						return FORMAT_DECIMALDAY_100TH;
					case 3:
						return FORMAT_DECIMALDAY_1000TH;
					case 4:
						return FORMAT_DECIMALDAY_10000TH;
					case 5:
						return FORMAT_DECIMALDAY_100000TH;
				}
			} else {
				int y = Integer.parseInt(string.substring(0, 4));
				int m = Integer.parseInt(string.substring(4, 6));
				if (string.length() == 8) {
					// yyyymmdd
					return FORMAT_DECIMALDAY_1TH;
				} else if (string.charAt(8) == '.') {
					// yyyymmdd.ddddd
					switch (string.length() - string.indexOf('.') - 1) {
						case 1:
							return FORMAT_DECIMALDAY_10TH;
						case 2:
							return FORMAT_DECIMALDAY_100TH;
						case 3:
							return FORMAT_DECIMALDAY_1000TH;
						case 4:
							return FORMAT_DECIMALDAY_10000TH;
						case 5:
							return FORMAT_DECIMALDAY_100000TH;
					}
				} else {
					// yyyymmddhhmmss
					if (string.length() >= 14)
						return FORMAT_TO_SECOND;
					if (string.length() >= 12)
						return FORMAT_TO_MINUTE;
					return FORMAT_TO_HOUR;
				}
			}
		} else if (st.countTokens() == 3) {
			st.nextToken();
			st.nextToken();
			String d_str = st.nextToken();
			if (d_str.indexOf('.') < 0)
				return FORMAT_DECIMALDAY_1TH;
			switch (d_str.length() - d_str.indexOf('.') - 1) {
				case 1:
					return FORMAT_DECIMALDAY_10TH;
				case 2:
					return FORMAT_DECIMALDAY_100TH;
				case 3:
					return FORMAT_DECIMALDAY_1000TH;
				case 4:
					return FORMAT_DECIMALDAY_10000TH;
				case 5:
					return FORMAT_DECIMALDAY_100000TH;
			}
		} else {
			st.nextToken();
			st.nextToken();
			st.nextToken();
			st.nextToken();

			int format = FORMAT_TO_HOUR;
			if (st.hasMoreTokens()) {
				format = FORMAT_TO_MINUTE;
				st.nextToken();
			}
			if (st.hasMoreTokens()) {
				format = FORMAT_TO_SECOND;
				st.nextToken();
			}
			return format;
		}

		return FORMAT_DEFAULT;
	}

	/**
	 * Converts the Julian Day of this object into year, month and day, 
	 * and sets them into the member fields of this object.
	 */
	protected void updateYearMonthDay ( ) {
		year = (int)(julian_day / 365.2421988 - 4712.0);
		JulianDay date = new JulianDay(year, 1, 0.0);
		if (date.getJD() + 1.0 > julian_day) {
			year--;
			date = new JulianDay(year, 1, 0.0);
		}
		double jd0 = julian_day - date.getJD();

		int i, p, q, r, s, t, t_old;
		i = 1;
		t = 0;
		do {
			i++;
			t_old = t;
			p = i - 1;
			q = (int)((i + 7) / 10);
			r = (int)((double)year / 4.0 - (double)((int)(year / 4)) + 0.77);
			s = (int)(0.55 * p - 0.33);
			t = 30 * p + q * (s - r) + p * (1 - q);
		} while (jd0 - (double)t >= 1.0  &&  i < 13) ;

		month = i - 1;
		day = jd0 - (double)t_old;
	}

	/**
	 * Gets date and time value of this object as Julian Day.
	 * @return the Julian Day.
	 */
	public double getJD ( ) {
		return julian_day;
	}

	/**
	 * Gets the year.
	 * @return the year.
	 */
	public int getYear ( ) {
		return year;
	}

	/**
	 * Gets the month.
	 * @return the month.
	 */
	public int getMonth ( ) {
		return month;
	}

	/**
	 * Gets the day.
	 * @return the day.
	 */
	public int getDay ( ) {
		return (int)day;
	}

	/**
	 * Gets the day and time in day.
	 * @return the day and time in day.
	 */
	public double getDecimalDay ( ) {
		return day;
	}

	/**
	 * Gets the hour.
	 * @return the hour.
	 */
	public int getHour ( ) {
		double d = day + 0.0000001;
		return (int)((d - (int)d) * 24.0);
	}

	/**
	 * Gets the minute.
	 * @return the minute.
	 */
	public int getMinute ( ) {
		double d = day + 0.0000001;
		return (int)((d - (int)d - (double)getHour() / 24.0) * 24.0 * 60.0);
	}

	/**
	 * Gets the second.
	 * @return the second.
	 */
	public int getSecond ( ) {
		double d = day + 0.0000001;
		return (int)((d - (int)d - (double)getHour() / 24.0 - (double)getMinute() / 24.0 / 60.0) * 24.0 * 60.0 * 60.0);
	}

	/**
	 * Rounds this day to the accuracy of the specified format and 
	 * creates a new <code>JulianDay</code> object.
	 * @param date_accuracy the number of accuracy.
	 * @return the rounded day.
	 */
	public JulianDay round ( int date_accuracy ) {
		switch (date_accuracy) {
			case FORMAT_DECIMALDAY_100000TH:
			case FORMAT_DECIMALDAY_10000TH:
			case FORMAT_DECIMALDAY_1000TH:
			case FORMAT_DECIMALDAY_100TH:
			case FORMAT_DECIMALDAY_10TH:
			case FORMAT_DECIMALDAY_1TH:
				date_accuracy = getEquivalentAccuracy(date_accuracy);
		}

		JulianDay day = create(getOutputString(FORMAT_MONTH_IN_NUMBER, date_accuracy));
		return day;
	}

	/**
	 * Gets the full spell name of the specified month.
	 * @param m the month between 1 and 12.
	 * @return the full spell name of the specified month.
	 */
	public static String getFullSpellMonthString ( int m ) {
		return month_full_name[m];
	}

	/**
	 * Gets the reduced name of the specified month.
	 * @param m the month between 1 and 12.
	 * @return the reduced name of the specified month.
	 */
	public static String getReducedMonthString ( int m ) {
		return month_reduced_name[m];
	}

	/**
	 * Gets the code of the specified month.
	 * @param m the month between 1 and 12.
	 * @return the code of the specified month.
	 */
	public static String getMonthCode ( int m ) {
		return month_code[m];
	}

	/**
	 * Gets the month of the specified month string.
	 * @param month the month string.
	 * @return the month between 1 and 12.
	 * @exception NoSuchElementException if the specified code does
	 * not much any month.
	 */
	public static int getMonth ( String month )
		throws NoSuchElementException
	{
		for (int i = 1 ; i <= 12 ; i++) {
			if (getFullSpellMonthString(i).equals(month))
				return i;
		}
		for (int i = 1 ; i <= 12 ; i++) {
			if (getReducedMonthString(i).equals(month))
				return i;
		}
		for (int i = 1 ; i <= 12 ; i++) {
			if (getMonthCode(i).equals(month))
				return i;
		}

		throw new NoSuchElementException();
	}

	/**
	 * Returns true when the specified accuracy represents decimal day.
	 * @param date_accuracy the format number of the accuracy.
	 * @return true when the specified accuracy represents decimal day.
	 */
	public static boolean isDecimalDay ( int date_accuracy ) {
		switch (date_accuracy) {
			case FORMAT_DECIMALDAY_100000TH:
			case FORMAT_DECIMALDAY_10000TH:
			case FORMAT_DECIMALDAY_1000TH:
			case FORMAT_DECIMALDAY_100TH:
			case FORMAT_DECIMALDAY_10TH:
			case FORMAT_DECIMALDAY_1TH:
				return true;
		}
		return false;
	}

	/**
	 * Gets the equivalent accuracy of the specified accuracy.
	 * @param date_accuracy the format number of the accuracy.
	 * @return the format number of the equivalent accuracy.
	 */
	public static int getEquivalentAccuracy ( int date_accuracy ) {
		switch (date_accuracy) {
			case FORMAT_DECIMALDAY_100000TH:
			case FORMAT_DECIMALDAY_10000TH:
				return FORMAT_TO_SECOND;
			case FORMAT_DECIMALDAY_1000TH:
			case FORMAT_DECIMALDAY_100TH:
				return FORMAT_TO_MINUTE;
			case FORMAT_DECIMALDAY_10TH:
				return FORMAT_TO_HOUR;
			case FORMAT_DECIMALDAY_1TH:
				return FORMAT_TO_DAY;
			case FORMAT_TO_SECOND:
				return FORMAT_DECIMALDAY_100000TH;
			case FORMAT_TO_MINUTE:
				return FORMAT_DECIMALDAY_1000TH;
			case FORMAT_TO_HOUR:
				return FORMAT_DECIMALDAY_10TH;
			case FORMAT_TO_DAY:
				return FORMAT_DECIMALDAY_1TH;
		}
		return FORMAT_DEFAULT;
	}

	/**
	 * Returns a string representation of the state of this object
	 * in the specified format.
	 * @param month_format  the number of format to output month.
	 * @param date_accuracy the number of accuracy.
	 * @return a string representation of the state of this object.
	 */
	public String getOutputString ( int month_format, int date_accuracy ) {
		if (month_format == FORMAT_JD) {
			switch (date_accuracy) {
				case FORMAT_TO_SECOND:
				case FORMAT_TO_MINUTE:
				case FORMAT_TO_HOUR:
				case FORMAT_TO_DAY:
					date_accuracy = getEquivalentAccuracy(date_accuracy);
			}

			String s = "";
			switch (date_accuracy) {
				case FORMAT_DECIMALDAY_10000TH:
					s = Format.formatDouble(julian_day, 12, 7);
					break;
				case FORMAT_DECIMALDAY_1000TH:
					s = Format.formatDouble(julian_day, 11, 7);
					break;
				case FORMAT_DECIMALDAY_100TH:
					s = Format.formatDouble(julian_day, 10, 7);
					break;
				case FORMAT_DECIMALDAY_10TH:
					s = Format.formatDouble(julian_day, 9, 7);
					break;
				case FORMAT_DECIMALDAY_1TH:
					s = Format.formatDouble(julian_day, 7, 7);
					break;
				case FORMAT_DECIMALDAY_100000TH:
				default:
					s = Format.formatDouble(julian_day, 13, 7);
					break;
			}

			return s;
		}

		double correction = 0.0;
		switch (date_accuracy) {
			case FORMAT_DECIMALDAY_100000TH:
				correction = 0.000005;
				break;
			case FORMAT_DECIMALDAY_10000TH:
				correction = 0.00005;
				break;
			case FORMAT_DECIMALDAY_1000TH:
				correction = 0.0005;
				break;
			case FORMAT_DECIMALDAY_100TH:
				correction = 0.005;
				break;
			case FORMAT_DECIMALDAY_10TH:
				correction = 0.05;
				break;
			case FORMAT_DECIMALDAY_1TH:
				correction = 0.5;
				break;
			case FORMAT_TO_SECOND:
				correction = 0.5 / 60.0 / 60.0 / 24.0;
				break;
			case FORMAT_TO_MINUTE:
				correction = 0.5 / 60.0 / 24.0;
				break;
			case FORMAT_TO_HOUR:
				correction = 0.5 / 24.0;
				break;
			default:
				correction = 0.000005;
				break;
		}
		JulianDay corrected_jd = new JulianDay(getJD() + correction);

		String s = "";
		s += Format.formatIntZeroPadding(corrected_jd.getYear(), 4);

		switch (month_format) {
			case FORMAT_MONTH_IN_FULL_SPELL:
				s += " " + month_full_name[corrected_jd.getMonth()] + " ";
				break;
			case FORMAT_MONTH_IN_REDUCED:
				s += " " + month_reduced_name[corrected_jd.getMonth()] + " ";
				break;
			case FORMAT_MONTH_IN_CODE:
				s += " " + month_code[corrected_jd.getMonth()] + " ";
				break;
			case FORMAT_MONTH_IN_NUMBER:
				s += " " + Format.formatIntZeroPadding(corrected_jd.getMonth(), 2) + " ";
				break;
			case FORMAT_MONTH_IN_NUMBER_WITHOUT_SPACE:
				s += Format.formatIntZeroPadding(corrected_jd.getMonth(), 2);
				break;
			default:
				s += " " + month_code[corrected_jd.getMonth()] + " ";
				break;
		}

		JulianDay jd_for_day = corrected_jd;
		if ((int)getDecimalDay() == (int)corrected_jd.getDecimalDay())
			jd_for_day = this;

		switch (date_accuracy) {
			case FORMAT_DECIMALDAY_100000TH:
				if (month_format == FORMAT_MONTH_IN_NUMBER  ||  month_format == FORMAT_MONTH_IN_NUMBER_WITHOUT_SPACE)
					s += Format.formatDoubleZeroPadding(jd_for_day.getDecimalDay(), 8, 2);
				else if (month_format == FORMAT_MONTH_IN_CODE)
					s += Format.formatDouble(jd_for_day.getDecimalDay(), 8, 2);
				else
					s += Format.formatDouble(jd_for_day.getDecimalDay(), 8, 2).trim();
				break;
			case FORMAT_DECIMALDAY_10000TH:
				if (month_format == FORMAT_MONTH_IN_NUMBER  ||  month_format == FORMAT_MONTH_IN_NUMBER_WITHOUT_SPACE)
					s += Format.formatDoubleZeroPadding(jd_for_day.getDecimalDay(), 7, 2);
				else if (month_format == FORMAT_MONTH_IN_CODE)
					s += Format.formatDouble(jd_for_day.getDecimalDay(), 7, 2);
				else
					s += Format.formatDouble(jd_for_day.getDecimalDay(), 7, 2).trim();
				break;
			case FORMAT_DECIMALDAY_1000TH:
				if (month_format == FORMAT_MONTH_IN_NUMBER  ||  month_format == FORMAT_MONTH_IN_NUMBER_WITHOUT_SPACE)
					s += Format.formatDoubleZeroPadding(jd_for_day.getDecimalDay(), 6, 2);
				else if (month_format == FORMAT_MONTH_IN_CODE)
					s += Format.formatDouble(jd_for_day.getDecimalDay(), 6, 2);
				else
					s += Format.formatDouble(jd_for_day.getDecimalDay(), 6, 2).trim();
				break;
			case FORMAT_DECIMALDAY_100TH:
				if (month_format == FORMAT_MONTH_IN_NUMBER  ||  month_format == FORMAT_MONTH_IN_NUMBER_WITHOUT_SPACE)
					s += Format.formatDoubleZeroPadding(jd_for_day.getDecimalDay(), 5, 2);
				else if (month_format == FORMAT_MONTH_IN_CODE)
					s += Format.formatDouble(jd_for_day.getDecimalDay(), 5, 2);
				else
					s += Format.formatDouble(jd_for_day.getDecimalDay(), 5, 2).trim();
				break;
			case FORMAT_DECIMALDAY_10TH:
				if (month_format == FORMAT_MONTH_IN_NUMBER  ||  month_format == FORMAT_MONTH_IN_NUMBER_WITHOUT_SPACE)
					s += Format.formatDoubleZeroPadding(jd_for_day.getDecimalDay(), 4, 2);
				else if (month_format == FORMAT_MONTH_IN_CODE)
					s += Format.formatDouble(jd_for_day.getDecimalDay(), 4, 2);
				else
					s += Format.formatDouble(jd_for_day.getDecimalDay(), 4, 2).trim();
				break;
			case FORMAT_DECIMALDAY_1TH:
				if (month_format == FORMAT_MONTH_IN_NUMBER  ||  month_format == FORMAT_MONTH_IN_NUMBER_WITHOUT_SPACE)
					s += Format.formatIntZeroPadding(corrected_jd.getDay(), 2);
				else if (month_format == FORMAT_MONTH_IN_CODE)
					s += Format.formatInt(corrected_jd.getDay(), 2);
				else
					s += Format.formatInt(corrected_jd.getDay(), 2).trim();
				break;
			case FORMAT_TO_SECOND: {
				double d = corrected_jd.getDecimalDay();
				double f_hour = (d - (double)((int)d)) * 24.0;
				int hour = (int)f_hour;
				double f_minute = (f_hour - (double)hour) * 60.0;
				int minute = (int)f_minute;
				double f_second = (f_minute - (double)minute) * 60.0;
				int second = (int)f_second;
				
				if (month_format == FORMAT_MONTH_IN_NUMBER_WITHOUT_SPACE) {
					s += Format.formatIntZeroPadding((int)d, 2) + Format.formatIntZeroPadding(hour, 2) + Format.formatIntZeroPadding(minute, 2) + Format.formatIntZeroPadding(second, 2);
				} else if (month_format == FORMAT_MONTH_IN_NUMBER) {
					s += Format.formatIntZeroPadding((int)d, 2) + " " + Format.formatInt(hour, 2).trim() + " " + Format.formatInt(minute, 2).trim() + " " + Format.formatInt(second, 2).trim();
				} else {
					s += Format.formatInt((int)d, 2).trim() + " " + Format.formatIntZeroPadding(hour, 2) + ":" + Format.formatIntZeroPadding(minute, 2) + ":" + Format.formatIntZeroPadding(second, 2);
				}
				break;
			}
			case FORMAT_TO_MINUTE: {
				double d = corrected_jd.getDecimalDay();
				double f_hour = (d - (double)((int)d)) * 24.0;
				int hour = (int)f_hour;
				double f_minute = (f_hour - (double)hour) * 60.0;
				int minute = (int)f_minute;
				
				if (month_format == FORMAT_MONTH_IN_NUMBER_WITHOUT_SPACE) {
					s += Format.formatIntZeroPadding((int)d, 2) + Format.formatIntZeroPadding(hour, 2) + Format.formatIntZeroPadding(minute, 2);
				} else if (month_format == FORMAT_MONTH_IN_NUMBER) {
					s += Format.formatIntZeroPadding((int)d, 2) + " " + Format.formatInt(hour, 2).trim() + " " + Format.formatInt(minute, 2).trim();
				} else {
					s += Format.formatInt((int)d, 2).trim() + " " + Format.formatIntZeroPadding(hour, 2) + ":" + Format.formatIntZeroPadding(minute, 2);
				}
				break;
			}
			case FORMAT_TO_HOUR: {
				double d = corrected_jd.getDecimalDay();
				double f_hour = (d - (double)((int)d)) * 24.0;
				int hour = (int)f_hour;
				
				if (month_format == FORMAT_MONTH_IN_NUMBER_WITHOUT_SPACE) {
					s += Format.formatIntZeroPadding((int)d, 2) + Format.formatIntZeroPadding(hour, 2);
				} else if (month_format == FORMAT_MONTH_IN_NUMBER) {
					s += Format.formatIntZeroPadding((int)d, 2) + " " + Format.formatInt(hour, 2).trim();
				} else {
					s += Format.formatInt((int)d, 2).trim() + " " + Format.formatInt(hour, 2).trim() + "h";
				}
				break;
			}
			default:
				s += Format.formatDouble(jd_for_day.getDecimalDay(), 8, 2).trim();
				break;
		}

		return s;
	}
}
