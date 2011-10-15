/*
 * @(#)BlendingStar.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util.star;
import java.util.Vector;
import net.aerith.misao.util.*;

/**
 * The <code>BlendingStar</code> represents a set of some blending 
 * stars. The position of this object is the barycenter of all 
 * blending stars. The magnitude of this object is the amount of all
 * blending stars. When a <code>Star</code> is added to this object,
 * the position and magnitude are recalculated.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2000 July 23
 */

public class BlendingStar extends MergedStar {
	/**
	 * Constructs a <code>BlendingStar</code> of only one star, not
	 * blending.
	 * @param star the initial star.
	 */
	public BlendingStar ( Star star ) {
		super(star);
	}

	/**
	 * Gets the brightest star.
	 * @return the brightest star.
	 */
	public Star getBrightestStar ( ) {
		Star star = getStarAt(0);
		for (int i = 1 ; i < content_list.size() ; i++) {
			Star s = getStarAt(i);
			if (star.getMag() > s.getMag())
				star = s;
		}
		return star;
	}

	/**
	 * Gets the name of this star. Because each content star has a
	 * different name, it returns the name of the brightest star.
	 * @return the name of this star.
	 */
	public String getName ( ) {
		return getBrightestStar().getName();
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). Because each content star has a different name,
	 * it returns the name of the brightest star.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		return getBrightestStar().getVsnetName();
	}

	/**
	 * Appends a <code>Star</code> to this object. The R.A. and Decl.,
	 * (x,y) position and magnitude are recalculated.
	 * @param star the star to append.
	 */
	public void append ( Star star ) {
		double original_weight = 0.0;
		for (int i = 0 ; i < content_list.size() ; i++) {
			Star s = (Star)content_list.elementAt(i);
			original_weight += Math.pow(Astro.MAG_STEP, - s.getMag());
		}
		double append_weight = Math.pow(Astro.MAG_STEP, - star.getMag());
		double total_weight = original_weight + append_weight;

		setX((getX() * original_weight + star.getX() * append_weight) / total_weight);
		setY((getY() * original_weight + star.getY() * append_weight) / total_weight);
		setMag(- Math.log(total_weight) / Math.log(Astro.MAG_STEP));

		double angular_distance = getCoor().getAngularDistanceTo(star.getCoor());
		double pa = getCoor().getPositionAngleTo(star.getCoor());
		angular_distance *= (append_weight / total_weight);

		Coor coor = new Coor(getCoor());
		coor.move(angular_distance, pa);
		setCoor(coor);

		content_list.addElement(star);
	}
}
