/*
 * @(#)StarImageRadiusBasedPairMaker.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.pixy.pairing;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;

/**
 * The <code>StarImageRadiusBasedPairMaker</code> is a class to make
 * pairs among two lists of stars. From the brightest star to the 
 * faintest star, a star in the first list is getting paired with a 
 * star in the second list. If some stars are within the radius of the
 * star image, the brightest one is selected as a counterpart. If all
 * stars within the radius are too faint over the specified difference,
 * it will not be paired with any one.
 * <p>
 * The star elements in the first list must be the <code>StarImage</code>
 * objects.
 * <p>
 * The (x,y) position and magnitude of stars in the both lists must be
 * set properly. The two lists are sorted in the constructor.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class StarImageRadiusBasedPairMaker extends Operation {
	/**
	 * The first list of stars.
	 */
	protected StarList first_list;

	/**
	 * The second list of stars.
	 */
	protected StarList second_list;

	/**
	 * The map of the first list of stars.
	 */
	protected PositionMap first_map = null;

	/**
	 * The map of the second list of stars.
	 */
	protected PositionMap second_map = null;

	/**
	 * The coefficient of radius to search a counterpart.
	 */
	protected double search_radius_coefficient = 1.0;

	/**
	 * The minimum radius to search.
	 */
	protected double minimum_radius = 0.0;

	/**
	 * The difference of magnitude to be accepted to paired with.
	 */
	protected double acceptable_mag_difference = 1.0;

	/**
	 * The list of pairs.
	 */
	protected Vector pair_list = null;

	/**
	 * Constructs a <code>StarImageRadiusBasedPairMaker</code> with 
	 * two lists of stars. 
	 * @param first_list  the first list of stars.
	 * @param second_list the second list of stars.
	 */
	public StarImageRadiusBasedPairMaker ( StarList first_list, StarList second_list ) {
		this.first_list = first_list;
		this.second_list = second_list;

		this.first_list.sort();
		this.second_list.sort();
	}

	/**
	 * Sets the coefficient of radius to search a counterpart.
	 * @param coefficient the coefficient of radius to search a 
	 * counterpart.
	 */
	public void setSearchRadiusCoefficient ( double coefficient ) {
		search_radius_coefficient = coefficient;
	}

	/**
	 * Sets the minimum radius to search.
	 * @param radius the minimum radius to search.
	 */
	public void setMinimumRadius ( double radius ) {
		minimum_radius = radius;
	}

	/**
	 * Sets the differenct of magnitude to be accepted to paired with.
	 * @param delta_mag the differenct of magnitude.
	 */
	public void setAcceptableMagnitudeDifference ( double delta_mag ) {
		acceptable_mag_difference = delta_mag;
	}

	/**
	 * Sets the map areas to check if a star is out of the area on 
	 * the other map. If the specified map is null, stars will not
	 * been checked if out of area or not.
	 * @param map1 the map area of the first list of stars.
	 * @param map2 the map area of the second list of stars.
	 */
	public void setMapAreas ( PositionMap map1, PositionMap map2 ) {
		first_map = map1;
		second_map = map2;
	}

	/**
	 * Gets the list of pairs.
	 * @return the list of pairs.
	 */
	public Vector getPairList ( ) {
		return pair_list;
	}

	/**
	 * Returns true if the operation is ready to start.
	 * @return true if the operation is ready to start.
	 */
	public boolean ready ( ) {
		return true;
	}

	/**
	 * Operates.
	 * @exception Exception if an error occurs.
	 */
	public void operate ( )
		throws Exception
	{
		pair_list = new Vector();

		monitor_set.addMessage("[Making pairs based on star image radius]");
		monitor_set.addMessage(new Date().toString());

		if (first_list != null  &&  second_list != null) {
			PositionMap map2 = new PositionMap(second_list);
			map2.divide(100, 100);
			map2.acceptOutOfBounds();

			for (int i = 0 ; i < first_list.size() ; i++) {
				StarImage star1 = (StarImage)first_list.elementAt(i);

				Star star2 = null;

				double radius = star1.getRadius() * search_radius_coefficient;
				if (radius >= minimum_radius) {
					// Finds the brightest star around the (x,y) of star1.
					try {
						Vector l = map2.getPartialListWithinRadius(new Position(star1.getX(), star1.getY()), radius);
						for (int j = 0 ; j < l.size() ; j++) {
							Star s = (Star)l.elementAt(j);
							if (s.getMag() <= star1.getMag() + acceptable_mag_difference) {
								if (star2 == null  ||  star2.getMag() > s.getMag())
									star2 = s;
							}
						}
					} catch ( OutOfBoundsException exception ) {
						// Never happens.
					}
				}

				// Creates a pair.
				StarPair pair = new StarPair(star1, star2);
				pair_list.addElement(pair);

				// Removes the paired star from the map of second list.
				if (star2 != null)
					map2.removePosition(star2);

				// Checks if the single star in the first list is out of the area on the second map.
				if (star2 == null  &&  second_map != null) {
					if (second_map.isOutOfBounds(star1))
						pair.setOutOfArea();
				}
			}

			// Adds the single stars in the second list to the list of pairs.
			Vector l = map2.getAllPositions();
			for (int j = 0 ; j < l.size() ; j++) {
				Star star2 = (Star)l.elementAt(j);
				StarPair pair = new StarPair(null, star2);
				pair_list.addElement(pair);

				// Checks if the single star in the second list is out of the area on the first map.
				if (first_map != null) {
					if (first_map.isOutOfBounds(star2))
						pair.setOutOfArea();
				}
			}
		}

		monitor_set.addMessage(new Date().toString());
		monitor_set.addSeparator();
	}
}
