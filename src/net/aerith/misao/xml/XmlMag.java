/*
 * @(#)XmlMag.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.xml;
import net.aerith.misao.util.Astro;
import net.aerith.misao.util.Format;

/**
 * The <code>XmlMag</code> is an application side implementation
 * of the class that the relaxer generated automatically.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 January 28
 */

public class XmlMag extends net.aerith.misao.xml.relaxer.XmlMag {
	/**
	 * Gets the order of accuracy, as number of digits under decimal 
	 * points.
	 * @return the order of accuracy.
	 */
	public int getAccuracyOrder ( ) {
		if (getOrder() == null)
			return 2;

		return getOrder().intValue();
	}

	/**
	 * Sets the order of accuracy, as number of digits under decimal 
	 * points.
	 * @param order the order of accuracy.
	 */
	public void setAccuracyOrder ( int order ) {
		if (order == 2)
			setOrder(null);
		else
			setOrder(java.math.BigInteger.valueOf((long)order));
	}

	/**
	 * Blends the specified magnitude into this magnitude.
	 * @param mag the magnitude to be blended.
	 */
	public void blend ( XmlMag mag ) {
		if (mag.getUpperLimit() == null) {
			if (getUpperLimit() == null) {
				double value = Math.pow(Astro.MAG_STEP, - (double)getContent());
				value += Math.pow(Astro.MAG_STEP, - (double)mag.getContent());
				setContent((float)(- Math.log(value) / Math.log(Astro.MAG_STEP)));
			} else {
				setUpperLimit(null);
				setContent(mag.getContent());
			}

			if (mag.getInaccurate() != null)
				setInaccurate("true");

			if (getAccuracyOrder() > mag.getAccuracyOrder())
				setAccuracyOrder(mag.getAccuracyOrder());
		}
	}

	/**
	 * Returns a string representation of the state of this object.
	 * @return a string representation of the state of this object.
	 */
	public String getOutputString ( ) {
		String s = "";

		if (getUpperLimit() != null)
			s += "[";

		int order = getAccuracyOrder();
		if (order == 0)
			s += Format.formatDouble(getContent(), 2, 2).trim();
		else
			s += Format.formatDouble(getContent(), order + 3, 2).trim();

		if (getInaccurate() != null)
			s += ":";

		return s;
	}
}
