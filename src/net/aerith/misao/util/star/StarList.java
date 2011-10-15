/*
 * @(#)StarList.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util.star;
import java.util.Vector;
import net.aerith.misao.util.*;

/**
 * The <code>StarList</code> represents a list of <code>Star</code>.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 January 28
 */

public class StarList extends StarPositionList {
	/**
	 * The mode of <tt>merge</tt> method which represents to unify.
	 */
	private final static int MERGE_UNIFY = 0;

	/**
	 * The mode of <tt>merge</tt> method which represents to blend.
	 */
	private final static int MERGE_BLEND = 1;

	/**
	 * Constructs an empty <code>StarList</code>.
	 */
	public StarList ( ) {
		super();
	}

	/**
	 * Copies the specified <code>StarList</code> and constructs a new
	 * <code>StarList</code>.
	 * @param original_list the original_list to copy.
	 */
	public StarList ( Vector original_list ) {
		super(original_list);
	}

	/**
	 * Maps the R.A. and Decl. of all elements to the (x,y) position
	 * based on the specified <code>ChartMapFunction</code>.
	 * @param cmf the map function.
	 */
	public void mapCoordinatesToXY ( ChartMapFunction cmf ) {
		mapCoordinatesToXY(cmf, null);
	}

	/**
	 * Maps the R.A. and Decl. of all elements to the (x,y) position
	 * based on the specified <code>ChartMapFunction</code>.
	 * @param cmf the map function.
	 * @param df  the distortion field.
	 */
	public void mapCoordinatesToXY ( ChartMapFunction cmf, DistortionField df ) {
		for (int i = 0 ; i < size() ; i++) {
			Star star = (Star)elementAt(i);
			star.mapCoordinatesToXY(cmf, df);
		}
	}

	/**
	 * Maps the (x,y) position of all elements to the R.A. and Decl.
	 * based on the specified <code>ChartMapFunction</code>.
	 * @param cmf the map function.
	 */
	public void mapXYToCoordinates ( ChartMapFunction cmf ) {
		mapXYToCoordinates(cmf, null);
	}

	/**
	 * Maps the (x,y) position of all elements to the R.A. and Decl.
	 * based on the specified <code>ChartMapFunction</code>.
	 * @param cmf the map function.
	 * @param df  the distortion field.
	 */
	public void mapXYToCoordinates ( ChartMapFunction cmf, DistortionField df ) {
		for (int i = 0 ; i < size() ; i++) {
			Star star = (Star)elementAt(i);
			star.mapXYToCoordinates(cmf, df);
		}
	}

	/**
	 * Unifies some elements with the same name and creates a new list
	 * which consists of the unified star data. It only searches stars
	 * to unify within the specified radius. The R.A. and Decl., and
	 * (x,y) position must be set properly. 
	 * @param search_radius the radius to search the counterparts to
	 * unify in degree.
	 * @return the new list.
	 */
	public StarList unify ( double search_radius ) {
		return merge(search_radius, MERGE_UNIFY);
	}

	/**
	 * Blends some elements within the specified radius and creates a
	 * new list which consists of the blended stars. The R.A. and Decl.,
	 * and (x,y) position must be set properly. 
	 * @param search_radius the radius to search the counterparts to
	 * blend in degree.
	 * @return the new list.
	 */
	public StarList blend ( double search_radius ) {
		return merge(search_radius, MERGE_BLEND);
	}


	/**
	 * Merges some elements within the specified radius and creates a
	 * new list which consists of the merged stars. Depending on the 
	 * specified mode number, the stars within the radius are unified
	 * only if they have the same name, or are blended. The R.A. and
	 * Decl., and (x,y) position must be set properly. 
	 * @param search_radius the radius to search the counterparts to
	 * merge in degree.
	 * @return the new list.
	 */
	private StarList merge ( double search_radius, int mode ) {
		StarList new_list = new StarList();

		if (size() == 0)
			return new_list;

		Star star1 = (Star)elementAt(0);
		Star star2 = null;
		for (int i = 1 ; i < size()  &&  star2 == null ; i++) {
			Star star = (Star)elementAt(i);
			if (star.getX() != star1.getX()  ||  star.getY() != star1.getY())
				star2 = star;
		}

		// In the case all the elements have the same (x,y).
		if (star2 == null) {
			if (mode == MERGE_UNIFY) {
				StarList copy_list = new StarList(this);
				for (int i = 0 ; i < copy_list.size() ; i++) {
					Star star = (Star)copy_list.elementAt(i);
					Vector merged_list = new Vector();
					for (int j = i + 1 ; j < copy_list.size() ; j++) {
						Star s = (Star)copy_list.elementAt(j);
						if (star.getName().equals(s.getName()))
							merged_list.addElement(copy_list.elementAt(j));
					}

					if (merged_list.size() == 0) {
						new_list.addElement(star);
					} else {
						UnifiedStar unified_star = new UnifiedStar(star);
						for (int j = 0 ; j < merged_list.size() ; j++) {
							unified_star.append((Star)merged_list.elementAt(j));
							copy_list.removeElement(merged_list.elementAt(j));
						}
						new_list.addElement(unified_star);
					}
				}
			} else if (mode == MERGE_BLEND) {
				BlendingStar blending_star = new BlendingStar((Star)elementAt(0));
				for (int i = 1 ; i < size() ; i++)
					blending_star.append((Star)elementAt(i));
				new_list.addElement(blending_star);
			}
			return new_list;
		}

		double xy_distance = Math.sqrt((star1.getX() - star2.getX()) * (star1.getX() - star2.getX()) + 
									   (star1.getY() - star2.getY()) * (star1.getY() - star2.getY()));
		double angular_distance = star1.getCoor().getAngularDistanceTo(star2);
		double search_xy_radius = xy_distance / angular_distance * search_radius;

		PositionMap map = new PositionMap(this);
		map.divide(100, 100);
		map.acceptOutOfBounds();

		StarList copy_list = new StarList(this);

		for (int i = 0 ; i < copy_list.size() ; i++) {
			Star star = (Star)copy_list.elementAt(i);

			Vector merged_list = new Vector();

			try {
				Vector l = map.getPartialListWithinRadius(star, search_xy_radius);
				for (int j = 0 ; j < l.size() ; j++) {
					Star s = (Star)l.elementAt(j);
					if (mode == MERGE_UNIFY) {
						if (star != s  &&  star.getName().equals(s.getName())) {
							// To be unified.
							merged_list.addElement(s);
						}
					} else if (mode == MERGE_BLEND) {
						if (star != s) {
							// To be blended.
							merged_list.addElement(s);
						}
					}
				}
			} catch ( OutOfBoundsException exception ) {
				// Never happens.
			}

			if (merged_list.size() == 0) {
				new_list.addElement(star);
			} else {
				if (mode == MERGE_UNIFY) {
					UnifiedStar unified_star = new UnifiedStar(star);
					for (int j = 0 ; j < merged_list.size() ; j++) {
						unified_star.append((Star)merged_list.elementAt(j));
						copy_list.removeElement(merged_list.elementAt(j));
					}
					new_list.addElement(unified_star);
				} else if (mode == MERGE_BLEND) {
					BlendingStar blending_star = new BlendingStar(star);
					for (int j = 0 ; j < merged_list.size() ; j++) {
						blending_star.append((Star)merged_list.elementAt(j));
						copy_list.removeElement(merged_list.elementAt(j));
					}
					new_list.addElement(blending_star);
				}
			}
		}

		return new_list;
	}

	/**
	 * Unpacks all merged stars and creates a new flat list.
	 * @return the new list.
	 */
	public StarList flatten ( ) {
		StarList new_list = new StarList();

		for (int i = 0 ; i < size() ; i++) {
			Star star = (Star)elementAt(i);
			new_list.addUnpacked(star);
		}

		return new_list;
	}

	/**
	 * Unpacks the specified star and adds to the list.
	 * @param star the merged star to add.
	 */
	private void addUnpacked ( Star star ) {
		if (star instanceof MergedStar) {
			for (int i = 0 ; i < ((MergedStar)star).getStarCount() ; i++) {
				addUnpacked(((MergedStar)star).getStarAt(i));
			}
		} else {
			addElement(star);
		}
	}

	/**
	 * Adjusts the magnitude of all elements based on the specified 
	 * function.
	 * @param ma the magnitude adjustment function.
	 */
	public void adjustMagnitude ( MagnitudeAdjustment ma ) {
		for (int i = 0 ; i < size() ; i++) {
			Star star = (Star)elementAt(i);
			star.setMag(ma.adjust(star.getMag()));
		}
	}
}
