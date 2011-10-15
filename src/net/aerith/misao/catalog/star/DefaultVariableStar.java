/*
 * @(#)DefaultVariableStar.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.catalog.star;
import java.io.*;
import java.util.*;
import java.awt.Color;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.*;
import net.aerith.misao.gui.PlotProperty;

/**
 * The <code>DefaultVariableStar</code> represents a base star data 
 * class of a default variable star. It has the magnitude range, 
 * magnitude system, type, spectrum, period and epoch.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2005 June 25
 */

public abstract class DefaultVariableStar extends CatalogStar {
	/**
	 * The type.
	 */
	protected byte[] type = null;

	/**
	 * The spectrum.
	 */
	protected byte[] spectrum = null;

	/**
	 * The epoch.
	 */
	protected byte[] epoch = null;

	/**
	 * The period.
	 */
	protected byte[] period = null;

	/**
	 * The maximum magnitude.
	 */
	protected byte[] mag_max = null;

	/**
	 * The minimum magnitude or magnitude range.
	 */
	protected byte[] mag_min = null;

	/**
	 * True if the maximum and minimum magnitude are recorded. False
	 * if the magnitude and range are recorded.
	 */
	protected boolean mag_max_and_min = false;

	/**
	 * The magnitude system code.
	 */
	protected byte[] mag_system = null;

	/**
	 * Sets the type.
	 * @param type the type.
	 */
	public void setType ( String type ) {
		this.type = null;
		if (type != null  &&  type.length() > 0) {
			try {
				this.type = type.getBytes("ASCII");
			} catch ( UnsupportedEncodingException exception ) {
				this.type = type.getBytes();
			}
		}
	}

	/**
	 * Sets the spectrum.
	 * @param spectrum the spectrum.
	 */
	public void setSpectrum ( String spectrum ) {
		this.spectrum = null;
		if (spectrum != null  &&  spectrum.length() > 0) {
			try {
				this.spectrum = spectrum.getBytes("ASCII");
			} catch ( UnsupportedEncodingException exception ) {
				this.spectrum = spectrum.getBytes();
			}
		}
	}

	/**
	 * Sets the epoch.
	 * @param epoch the epoch.
	 */
	public void setEpoch ( String epoch ) {
		this.epoch = null;
		if (epoch != null  &&  epoch.length() > 0) {
			try {
				this.epoch = epoch.getBytes("ASCII");
			} catch ( UnsupportedEncodingException exception ) {
				this.epoch = epoch.getBytes();
			}
		}
	}

	/**
	 * Sets the period.
	 * @param period the period.
	 */
	public void setPeriod ( String period ) {
		this.period = null;
		if (period != null  &&  period.length() > 0) {
			try {
				this.period = period.getBytes("ASCII");
			} catch ( UnsupportedEncodingException exception ) {
				this.period = period.getBytes();
			}
		}
	}

	/**
	 * Sets the maximum and minimum magnitude.
	 * @param mag_max the maximum magnitude.
	 * @param mag_min the minimum magnitude.
	 */
	public void setMagnitudeByMaxAndMin ( String mag_max, String mag_min ) {
		mag_max_and_min = true;

		if (mag_max != null  &&  mag_max.length() > 0) {
			try {
				this.mag_max = mag_max.getBytes("ASCII");
			} catch ( UnsupportedEncodingException exception ) {
				this.mag_max = mag_max.getBytes();
			} 
		}
		if (mag_min != null  &&  mag_min.length() > 0) {
			try {
				this.mag_min = mag_min.getBytes("ASCII");
			} catch ( UnsupportedEncodingException exception ) {
				this.mag_min = mag_min.getBytes();
			} 
		}
	}

	/**
	 * Sets the magnitude and the range.
	 * @param mag   the magnitude.
	 * @param range the range.
	 */
	public void setMagnitudeByRange ( String mag, String range ) {
		mag_max_and_min = false;

		if (mag != null  &&  mag.length() > 0) {
			try {
				this.mag_max = mag.getBytes("ASCII");
			} catch ( UnsupportedEncodingException exception ) {
				this.mag_max = mag.getBytes();
			}
		}
		if (range != null  &&  range.length() > 0) {
			try {
				this.mag_min = range.getBytes("ASCII");
			} catch ( UnsupportedEncodingException exception ) {
				this.mag_min = range.getBytes();
			}
		} 
	}

	/**
	 * Sets the magnitude system.
	 * @param mag_system the magnitude system.
	 */
	public void setMagSystem ( String mag_system ) {
		if (mag_system != null  &&  mag_system.length() > 0) {
			try {
				this.mag_system = mag_system.getBytes("ASCII");
			} catch ( UnsupportedEncodingException exception ) {
				this.mag_system = mag_system.getBytes();
			}
		}
	}

	/**
	 * Gets an array of keys and values to output.
	 * @return an array of keys and values to output.
	 */
	public KeyAndValue[] getKeyAndValues ( ) {
		Vector l = new Vector();

		if (mag_max_and_min) {
			if (mag_max != null)
				l.addElement(new KeyAndValue("Mag(max)", new String(mag_max)));
			if (mag_min != null)
				l.addElement(new KeyAndValue("Mag(min)", new String(mag_min)));
		} else {
			if (mag_max != null)
				l.addElement(new KeyAndValue("Mag", new String(mag_max)));
			if (mag_min != null)
				l.addElement(new KeyAndValue("Range", new String(mag_min)));
		}
		if (mag_system != null)
			l.addElement(new KeyAndValue("MagSystem", new String(mag_system)));

		if (type != null)
			l.addElement(new KeyAndValue("Type", new String(type)));
		if (spectrum != null)
			l.addElement(new KeyAndValue("Spectrum", new String(spectrum)));
		if (epoch != null)
			l.addElement(new KeyAndValue("Epoch", new String(epoch)));
		if (period != null)
			l.addElement(new KeyAndValue("Period", new String(period)));

		KeyAndValue[] key_and_values = new KeyAndValue[l.size()];
		for (int i = 0 ; i < l.size() ; i++)
			key_and_values[i] = (KeyAndValue)l.elementAt(i);

		return key_and_values;
	}

	/**
	 * Sets the value of the specified key.
	 * @param key_and_value the key and value to set.
	 */
	public void setKeyAndValue ( KeyAndValue key_and_value ) {
		byte[] b = null;
		try {
			b = key_and_value.getValue().getBytes("ASCII");
		} catch ( UnsupportedEncodingException exception ) {
			b = key_and_value.getValue().getBytes();
		} 

		if (key_and_value.getKey().equals("Mag(max)")) {
			mag_max = b;
			mag_max_and_min = true;
		} else if (key_and_value.getKey().equals("Mag(min)")) {
			mag_min = b;
			mag_max_and_min = true;
		} else if (key_and_value.getKey().equals("Mag")) {
			mag_max = b;
			mag_max_and_min = false;
		} else if (key_and_value.getKey().equals("Range")  ||  key_and_value.getKey().equals("MagRange")) {
			mag_min = b;
			mag_max_and_min = false;
		} else if (key_and_value.getKey().equals("MagSystem")) {
			mag_system = b;
		} else if (key_and_value.getKey().equals("Type")) {
			type = b;
		} else if (key_and_value.getKey().equals("Spectrum")) {
			spectrum = b;
		} else if (key_and_value.getKey().equals("Epoch")) {
			epoch = b;
		} else if (key_and_value.getKey().equals("Period")) {
			period = b;
		}
	}
}
