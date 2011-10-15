/*
 * @(#)TargetStar.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util.star;
import java.util.Vector;
import net.aerith.misao.util.*;
import net.aerith.misao.catalog.*;

/**
 * The <code>TargetStar</code> represents the target to search.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 November 26
 */

public class TargetStar extends CatalogStar {
	/**
	 * The astrometric error in arcsec.
	 */
	protected double astrometric_error = 0.0;

	/**
	 * Constructs an empty <code>TargetStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	 public TargetStar ( ) {
		super();
	 }

	/**
	 * Constructs a <code>TargetStar</code>.
	 * @param coor the  the R.A. and Decl.
	 * @param pos_error the astrometric error in arcsec.
	 */
	 public TargetStar ( Coor coor, double pos_error ) {
		super();
		setCoor(coor);

		astrometric_error = pos_error;
	 }

	/**
	 * Gets the mean error of position in arcsec.
	 * @return the mean error of position in arcsec.
	 */
	public double getPositionErrorInArcsec ( ) {
		return astrometric_error;
	}

	/**
	 * Gets the maximum error of position in arcsec. It is the search
	 * area size to identify with other stars.
	 * @return the maximum error of position in arcsec.
	 */
	public double getMaximumPositionErrorInArcsec ( ) {
		double pos_error = astrometric_error * 1.5;
		if (pos_error < 5.0)
			pos_error = 5.0;
		return pos_error;
	}
}
