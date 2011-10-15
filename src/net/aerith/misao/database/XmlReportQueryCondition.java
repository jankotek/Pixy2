/*
 * @(#)XmlReportQueryCondition.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.database;
import net.aerith.misao.util.*;
import net.aerith.misao.xml.*;

/**
 * The <code>XmlReportQueryCondition</code> represents a query 
 * condition to select XML report documents.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2002 January 18
 */

public class XmlReportQueryCondition {
	/**
	 * The brighter limit of the limiting magnitude.
	 */
	protected double brighter_limit = 0.0;

	/**
	 * The fainter limit of the limiting magnitude.
	 */
	protected double fainter_limit = 99.9;

	/**
	 * Returns true if the specified XML report document is acceptable
	 * by this query condition.
	 * @param info the XML information object.
	 * @return true if the specified XML report document is acceptable
	 */
	public boolean accept ( XmlInformation info ) {
		if (brighter_limit <= info.getLimitingMag()  &&  info.getLimitingMag() <= fainter_limit)
			return true;

		return false;
	}

	/**
	 * Sets the range of the limiting magnitude.
	 * @param brighter_limit the brighter limit of the limiting 
	 * magnitude.
	 * @param fainter_limit  the fainter limit of the limiting 
	 * magnitude.
	 */
	public void setLimitingMagnitude ( double brighter_limit, double fainter_limit ) {
		this.brighter_limit = brighter_limit;
		this.fainter_limit = fainter_limit;
	}

	/**
	 * Gets the brighter limit of the limiting magnitude.
	 * @return the brighter limit of the limiting magnitude.
	 */
	public double getBrighterLimit ( ) {
		return brighter_limit;
	}

	/**
	 * Gets the fainter limit of the limiting magnitude.
	 * @return the fainter limit of the limiting magnitude.
	 */
	public double getFainterLimit ( ) {
		return fainter_limit;
	}
}
