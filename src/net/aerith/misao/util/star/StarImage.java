/*
 * @(#)StarImage.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util.star;
import net.aerith.misao.util.*;

/**
 * The <code>StarImage/code> represents a detected star which consists
 * of (x,y) position, R.A. and Decl., magnitude, and some values
 * obtained at star detection. They are expressed in float data.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 January 28
 */

public class StarImage extends Star {
	/**
	 * The amount of pixel values.
	 */
	protected float value;

	/**
	 * The peak value.
	 */
	protected float peak;

	/**
	 * The area_size;
	 */
	protected int area;

	/**
	 * True if the R.A. and Decl. are set properly.
	 */
	protected boolean output_coordinates = false;

	/**
	 * Constructs an empty <code>StarImage</code>. All member fields
	 * are set as 0.
	 */
	public StarImage ( ) {
		super();
	}

	/**
	 * Constructs a copy <code>StarImage</code>.
	 */
	public StarImage ( StarImage star_image ) {
		setPosition(star_image);
		setCoor(new Coor(star_image.getCoor()));
		setMag(star_image.getMag());

		value = star_image.value;
		peak = star_image.peak;
		area = star_image.area;
		output_coordinates = star_image.output_coordinates;
	}

	/**
	 * Sets the flag so that R.A. and Decl. are also output.
	 */
	public void enableOutputCoordinates ( ) {
		output_coordinates = true;
	}

	/**
	 * Gets the amount of pixel values.
	 * @return the amount of pixel values.
	 */
	public double getValue ( ) {
		return (double)value;
	}

	/**
	 * Sets the amount of pixel values.
	 * @param new_value the new amount of pixel values.
	 */
	public void setValue ( double new_value ) {
		value = (float)new_value;
	}

	/**
	 * Gets the peak value.
	 * @return the peak value.
	 */
	public double getPeak ( ) {
		return (double)peak;
	}

	/**
	 * Sets the peak value.
	 * @param new_peak the new peak value.
	 */
	public void setPeak ( double new_peak ) {
		peak = (float)new_peak;
	}

	/**
	 * Gets the area size.
	 * @return the area size.
	 */
	public int getArea ( ) {
		return area;
	}

	/**
	 * Sets the area size.
	 * @param new_area the new area size.
	 */
	public void setArea ( int new_area ) {
		area = new_area;
	}

	/**
	 * Gets the radius.
	 * @return the radius.
	 */
	public double getRadius ( ) {
		return Math.sqrt((double)area / Math.PI);
	}

	/**
	 * Gets an array of keys and values to output. When the R.A. and
	 * Decl. are not set properly, the magnitude will not be output.
	 * @return an array of keys and values to output.
	 */
	public KeyAndValue[] getKeyAndValues ( ) {
		int key_count = 3;
		if (output_coordinates == true)
			key_count++;

		KeyAndValue[] key_and_values = new KeyAndValue[key_count];
		key_count = 0;

		if (output_coordinates == true)
			key_and_values[key_count++] = new KeyAndValue("Mag", Format.formatDouble(getMag(), 5, 2).trim());

		key_and_values[key_count++] = new KeyAndValue("Value", Format.formatDouble(value, 20, 18).trim());
		key_and_values[key_count++] = new KeyAndValue("Peak", Format.formatDouble(peak, 20, 18).trim());
		key_and_values[key_count++] = new KeyAndValue("Area", String.valueOf(area));

		return key_and_values;
	}

	/**
	 * Sets the value of the specified key.
	 * @param key_and_value the key and value to set.
	 */
	public void setKeyAndValue ( KeyAndValue key_and_value ) {
		if (key_and_value.getKey().equals("Mag")) {
			setMag(Double.parseDouble(key_and_value.getValue()));
		} else if (key_and_value.getKey().equals("Value")) {
			setValue(Double.parseDouble(key_and_value.getValue()));
		} else if (key_and_value.getKey().equals("Peak")) {
			setPeak(Double.parseDouble(key_and_value.getValue()));
		} else if (key_and_value.getKey().equals("Area")) {
			setArea(Integer.parseInt(key_and_value.getValue()));
		}
	}

	/**
	 * Returns a string representation of the state of this object.
	 * When the R.A. and Decl. are not set properly, the name, R.A. 
	 * and Decl. and the magnitude will not be output.
	 * @return a string representation of the state of this object.
	 */
	public String getOutputString ( ) {
		if (output_coordinates == true)
			return super.getOutputString();

		String s = "";

		KeyAndValue[] key_and_values = getKeyAndValues();
		for (int i = 0 ; i < key_and_values.length ; i++) {
			if (i > 0)
				s += getItemDelimiter();
			s += key_and_values[i].getKey() + getKeyAndValueDelimiter() + key_and_values[i].getValue();
		}

		return s;

	}

	/**
	 * Returns a string representation of the state of this object
	 * for PXF output. In the case of <code>StarImage</code>, the name
	 * of this star will not be output.
	 * @return a string representation of the state of this object.
	 */
	public String getPxfString ( ) {
		String s = super.getPxfString();
		int p = s.indexOf(getItemDelimiter());
		// Cuts the name.
		if (p > 0)
			s = s.substring(p + getItemDelimiter().length());
		return s;
	}

	/**
	 * Returns a raw string representation of the state of this object,
	 * for debugging use. It should be invoked from <code>toString</code>
	 * method of the subclasses.
	 * @return a string representation of the state of this object.
	 */
	protected String paramString ( ) {
		return super.paramString() + ",value=" + value + ",peak=" + peak + ",area=" + area;
	}

	/**
	 * Returns a string representation of the state of this object,
	 * for debugging use.
	 * @return a string representation of the state of this object.
	 */
	public String toString ( ) {
		return getClass().getName() + "[" + paramString() + "]";
	}
}
