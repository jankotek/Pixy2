/*
 * @(#)Format.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;
import java.io.File;
import java.util.*;

/**
 * The <code>Format</code> is a static class with static methods to
 * convert number into string in proper format, and methods to 
 * convert string into number.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 January 28
 */

public class Format {
	/**
	 * Formats the integer number in specified figures and creates a 
	 * <code>String</code>.
	 * @param value   the number to format.
	 * @param figures the number of figures of a new string.
	 * @return the string of formatted number.
	 */
	public static String formatInt ( int value, int figures ) {
		return formatInt(value, figures, ' ');
	}

	/**
	 * Formats the integer number in specified figures and creates a 
	 * <code>String</code> with 0 padding.
	 * @param value   the number to format.
	 * @param figures the number of figures of a new string.
	 * @return the string of formatted number.
	 */
	public static String formatIntZeroPadding ( int value, int figures ) {
		return formatInt(value, figures, '0');
	}

	/**
	 * Formats the integer number in specified figures and creates a 
	 * <code>String</code> padding with a specified character.
	 * @param value   the number to format.
	 * @param figures the number of figures of a new string.
	 * @param padding the character for padding.
	 * @return the string of formatted number.
	 */
	private static String formatInt ( int value, int figures, char padding ) {
		int i;
		String s = "";

		for (i = 0 ; i < figures ; i++)
			s += padding;
		s += String.valueOf(value);
		if (String.valueOf(value).length() > figures)
			return String.valueOf(value);

		s = s.substring(s.length() - figures, s.length());
		if (padding == '0'  &&  s.indexOf('-') > 0)
			s = "-" + s.substring(1).replace('-', '0');
		return s;
	}

	/**
	 * Formats the double number in specified figures and creates a 
	 * <code>String</code>.
	 * @param value   the number to format.
	 * @param figures the number of figures of a new string.
	 * @param period  the position of period.
	 * @return the string of formatted number.
	 */
	public static String formatDouble ( double value, int figures, int period ) {
		return formatDouble(value, figures, period, ' ');
	}

	/**
	 * Formats the double number in specified figures and creates a 
	 * <code>String</code> with 0 padding.
	 * @param value   the number to format.
	 * @param figures the number of figures of a new string.
	 * @param period  the position of period.
	 * @return the string of formatted number.
	 */
	public static String formatDoubleZeroPadding ( double value, int figures, int period ) {
		return formatDouble(value, figures, period, '0');
	}

	/**
	 * Formats the double number in specified figures and creates a 
	 * <code>String</code> padding with a specified character.
	 * @param value   the number to format.
	 * @param figures the number of figures of a new string.
	 * @param period  the position of period.
	 * @param padding the character for padding.
	 * @return the string of formatted number.
	 */
	private static String formatDouble ( double value, int figures, int period, char padding ) {
		int after_period = figures - period - 1;

		double geta = 0.5;
		for (int i = 0 ; i < after_period ; i++)
			geta /= 10.0;

		if (value < 0.0)
			value -= geta;
		else
			value += geta;

		String valueStr = String.valueOf(value);
		if (Math.abs(value) < 1.0) {
			double f = Math.abs(value);
			int zero_count = (int)(- Math.log(f) / Math.log(10.0));
			f *= Math.pow(10.0, zero_count);

			// Now f satisfies 0.1 <= f < 1.0.

			valueStr = String.valueOf(f).substring(2);

			for (int i = 0 ; i < zero_count ; i++)
				valueStr = "0" + valueStr;

			valueStr = "0." + valueStr;

			if (value < 0.0)
				valueStr = "-" + valueStr;
		}

		String s = "";

		for (int i = 0 ; i < figures ; i++)
			s += padding;
		s += valueStr;
		if (s.indexOf('.') < 0)
			s += '.';
		for (int i = 0 ; i < figures ; i++)
			s += '0';

		if (valueStr.indexOf('.') > period) {
			figures += valueStr.indexOf('.') - period;
			period = valueStr.indexOf('.');
		}

		int p = s.indexOf('.');
		if (after_period < 0)
			s = s.substring(p - figures, p);
		else
			s = s.substring(p - period, p - period + figures);

		if (padding == '0'  &&  s.indexOf('-') > 0)
			s = "-" + s.substring(1).replace('-', '0');

		return s;
	}

	/**
	 * Formats the specified angular values representing the width and
	 * height and creates a new <code>String</code> with a proper unit.
	 * @param width  the anglular value of width.
	 * @param height the anglular value of height.
	 * @return the formatted string with a proper unit.
	 */
	public static String formatAngularSize ( double width, double height ) {
		double fov = (width > height ? width : height);

		String s = "";
		if (fov < 1.0 / 60.0) {
			s = formatDouble(width * 3600.0, 6, 3).trim() + " x " + formatDouble(height * 3600.0, 6, 3).trim() + " arcsec";
		} else if (fov < 10.0 / 60.0) {
			s = formatDouble(width * 60.0, 6, 2).trim() + " x " + formatDouble(height * 60.0, 6, 2).trim() + " arcmin";
		} else if (fov < 1.0) {
			s = formatDouble(width * 60.0, 6, 3).trim() + " x " + formatDouble(height * 60.0, 6, 3).trim() + " arcmin";
		} else if (fov < 10.0) {
			s = formatDouble(width, 6, 2).trim() + " x " + formatDouble(height, 6, 2).trim() + " degree";
		} else {
			s = formatDouble(width, 6, 3).trim() + " x " + formatDouble(height, 6, 3).trim() + " degree";
		}
		return s;
	}

	/**
	 * Removes space characters in the specified <code>String</code>.
	 * @param string the string where to remove space characters.
	 * @return the new string.
	 */
	public static String removeSpace ( String string ) {
		String s = "";
		for (int i = 0 ; i < string.length() ; i++) {
			if (Character.isSpaceChar(string.charAt(i)) == false)
				s += string.charAt(i);
		}
		return s;
	}

	/**
	 * Converts a byte array into integer value.
	 * @param b      the byte array.
	 * @param offset the offset index to start the conversion.
	 * @param bytes  the number of bytes to convert.
	 * @return the integer value.
	 */
	public static int intValueOf ( byte[] b, int offset, int bytes ) {
		int value = 0;
		for (int i = 0 ; i < bytes ; i++) {
			int v = b[offset + i];
			if (v < 0)
				v += 256;
			value = (value << 8) | v;
		}
		return value;
	}

	/**
	 * Converts <code>String</code> into integer value. If some white 
	 * spaces or other letters exist, if positive sign '+' is attached,
	 * if being padded by 0, it does not throw exception and converts
	 * a proper value.
	 * @param string the string to convert into integer value.
	 * @return the integer value.
	 */
	public static int intValueOf ( String string ) {
		String s = string.trim();
		int p = s.indexOf(' ');
		if (p >= 0)
			s = s.substring(0, p);

		if (s.length() == 0)
			return 0;

		if (s.charAt(0) == '+')
			s = s.substring(1);

		p = s.substring(1).indexOf('-');
		if (p >= 0)
			s = s.substring(0,p);

		String s2 = "";
		for (int i = 0 ; i < s.length() ; i++) {
			if (('0' <= s.charAt(i)  &&  s.charAt(i) <= '9')  ||  s.charAt(i) == '-')
				s2 += s.charAt(i);
			else
				break;
		}

		if (s2.length() == 0)
			return 0;

		return Integer.parseInt(s2);
	}

	/**
	 * Converts <code>String</code> into double value. This method
	 * can convert exponential style. If some white spaces or other
	 * letters exist, if positive sign '+' is attached, if being 
	 * padded by 0, it does not throw exception and converts a
	 * proper value.
	 * @param string the string to convert into double value.
	 * @return the double value.
	 */
	public static double doubleValueOf ( String string ) {
		String s = string.trim();
		int p = s.indexOf(' ');
		if (p >= 0)
			s = s.substring(0, p);

		if (s.length() == 0)
			return 0.0;

		// in the case of exponential style
		p = s.indexOf('E');
		if (p >= 0  &&  p+1 < s.length()  &&  (s.charAt(p+1) == '+'  ||  s.charAt(p+1) == '-')) {
			double f = Double.valueOf(s.substring(0,p)).doubleValue();
			int q = Integer.parseInt(s.substring(p+2));
			if (s.charAt(p+1) == '-')
				q = - q;

			double a = 10.0;
			if (q < 0) {
				a = 0.1;
				q = - q;
			}
			for (int i = 0 ; i < q ; i++)
				f *= a;

			return f;
		}

		if (s.charAt(0) == '+')
			s = s.substring(1);

		p = s.indexOf('.');
		int q = s.substring(p+1).indexOf('.');
		if (q >= 0)
			s = s.substring(0,q);

		p = s.substring(1).indexOf('-');
		if (p >= 0)
			s = s.substring(0,p);

		String s2 = "";
		for (int i = 0 ; i < s.length() ; i++) {
			if (('0' <= s.charAt(i)  &&  s.charAt(i) <= '9')  ||  s.charAt(i) == '.'  ||  s.charAt(i) == '-')
				s2 += s.charAt(i);
			else
				break;
		}

		if (s2.length() == 0)
			return 0.0;

		return Double.valueOf(s2).doubleValue();
	}

	/**
	 * Separates the path string by the system dependent path 
	 * separator
	 * @param path the path string.
	 * @return the array of separated paths.
	 */
	public static String[] separatePath ( String path ) {
		if (path == null)
			return new String[0];

		StringTokenizer st = new StringTokenizer(path, File.pathSeparator);
		String[] paths = new String[st.countTokens()];
		for (int i = 0 ; st.hasMoreTokens() ; i++)
			paths[i] = st.nextToken();
		return paths;
	}
}
