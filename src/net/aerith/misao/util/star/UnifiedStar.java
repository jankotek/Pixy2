/*
 * @(#)UnifiedStar.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util.star;
import java.util.Vector;
import net.aerith.misao.util.*;

/**
 * The <code>UnifiedStar</code> represents a set of some data of one
 * star. The position and magnitude of this object is the simple 
 * average of all data. When a <code>Star</code> is added to this 
 * object, the position and magnitude are recalculated.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2000 July 23
 */

public class UnifiedStar extends MergedStar {
	/**
	 * Constructs an <code>UnifiedStar</code> of only one star, not
	 * unified.
	 * @param star the initial star.
	 */
	public UnifiedStar ( Star star ) {
		super(star);
	}

	/**
	 * Gets the name of this star. Because only stars with the same
	 * name can be unified, the names of all content stars are same.
	 * So it returns the name of a content star.
	 * @return the name of this star.
	 */
	public String getName ( ) {
		return ((Star)content_list.elementAt(0)).getName();
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). Because only stars with the same name can be
	 * unified, the names of all content stars are same. So it returns
	 * the name of a content star.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		return ((Star)content_list.elementAt(0)).getVsnetName();
	}

	/**
	 * Appends a <code>Star</code> to this object. The R.A. and Decl.,
	 * (x,y) position and magnitude are recalculated.
	 * @param star the star to append.
	 */
	public void append ( Star star ) {
		double original_weight = (double)getStarCount();
		double append_weight = 1.0;
		double total_weight = original_weight + append_weight;

		setX((getX() * original_weight + star.getX() * append_weight) / total_weight);
		setY((getY() * original_weight + star.getY() * append_weight) / total_weight);
		setMag((getMag() * original_weight + star.getMag() * append_weight) / total_weight);

		double angular_distance = getCoor().getAngularDistanceTo(star.getCoor());
		double pa = getCoor().getPositionAngleTo(star.getCoor());
		angular_distance *= (append_weight / total_weight);

		Coor coor = new Coor(getCoor());
		coor.move(angular_distance, pa);
		setCoor(coor);

		content_list.addElement(star);
	}
}
