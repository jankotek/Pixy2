/*
 * @(#)SupplementaryPairMaker.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.pixy.pairing;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;

/**
 * The <code>SupplementaryPairMaker</code> is a class to make pairs
 * between the single (not paired) stars among two lists of stars. 
 * From the brightest star to the faintest star, a single star in the
 * first list is getting paired with a single star in the second list.
 * But if the second star has a closer candidate in the first list,
 * it is paired with the closer one. Note that only bright stars,
 * brighter than the specified threshold, are considered. 
 * <p>
 * The search radius is determined based on the magnitude as:
 * <pre>
 *      7 mag : 4'<br>
 *      9 mag : 2'<br>
 *     11 mag : 1'<br>
 *     13 mag : 30"<br>
 *     15 mag : 15"<br>
 * </pre>
 * The upper limit is 3'. The lower limit is 5 pixels.
 * <p>
 * The (x,y) position and magnitude of stars in the both lists must be
 * set properly. The two lists are sorted in the constructor.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class SupplementaryPairMaker extends Operation {
	/**
	 * The first list of stars.
	 */
	protected StarList first_list;

	/**
	 * The second list of stars.
	 */
	protected StarList second_list;

	/**
	 * The chart composition of catalog stars.
	 */
	protected ChartMapFunction cmf;

	/**
	 * The threshold.
	 */
	protected double threshold = 0.0;

	/**
	 * The minimum search radius.
	 */
	protected double minimum_radius = 5.0;

	/**
	 * The list of pairs.
	 */
	protected Vector pair_list = null;

	/**
	 * Constructs a <code>SupplementaryPairMaker</code> with two 
	 * lists of stars. 
	 * @param first_list  the first list of stars.
	 * @param second_list the second list of stars.
	 * @param cmf         the chart composition of catalog stars.
	 */
	public SupplementaryPairMaker ( StarList first_list, StarList second_list, ChartMapFunction cmf ) {
		this.first_list = first_list;
		this.second_list = second_list;
		this.cmf = cmf;

		this.first_list.sort();
		this.second_list.sort();
	}

	/**
	 * Sets the threshold.
	 * @param threshold the threshold.
	 */
	public void setThreshold ( double threshold ) {
		this.threshold = threshold;
	}

	/**
	 * Sets the minimum search radius.
	 * @param radius the minimum search radius.
	 */
	public void setMinimumSearchRadius ( double radius ) {
		minimum_radius = radius;
	}

	/**
	 * Gets the list of pairs.
	 * @return the list of pairs.
	 */
	public Vector getPairList ( ) {
		return pair_list;
	}

	/**
	 * Gets the search radius.
	 * @param mag the magnitude.
	 * @return the search radius.
	 */
	private double getSearchRadius ( double mag ) {
		double distance = Math.pow(2.0, (11.0 - mag) / 2.0);
		if (distance > 3.0)
			distance = 3.0;

		double search_radius = distance / 60.0 * cmf.getScaleUnitPerDegree();
		if (search_radius < minimum_radius)
			search_radius = minimum_radius;

		return search_radius;
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

		monitor_set.addMessage("[Making pairs between single data]");
		monitor_set.addMessage(new Date().toString());

		if (first_list != null  &&  second_list != null) {
			StarList first_single_list = new StarList();
			for (int i = 0 ; i < first_list.size() ; i++) {
				Star star = (Star)first_list.elementAt(i);
				if (star.getPair().getSecondStar() == null  &&  star.getMag() < threshold)
					first_single_list.addElement(star);
				else
					pair_list.addElement(star.getPair());
			}

			StarList second_single_list = new StarList();
			for (int i = 0 ; i < second_list.size() ; i++) {
				Star star = (Star)second_list.elementAt(i);
				if (star.getPair().getFirstStar() == null  &&  star.getMag() < threshold)
					second_single_list.addElement(star);
				else if (star.getPair().getFirstStar() == null)
					pair_list.addElement(star.getPair());
			}

			monitor_set.addMessage("Threshold: " + threshold);
			monitor_set.addMessage("Number of single data in the first list: " + first_single_list.size());
			monitor_set.addMessage("Number of single data in the second list: " + second_single_list.size());

			PositionMap map1 = new PositionMap(first_single_list);
			map1.divide(100, 100);
			map1.acceptOutOfBounds();

			PositionMap map2 = new PositionMap(second_single_list);
			map2.divide(100, 100);
			map2.acceptOutOfBounds();

			int paired_count = 0;
			for (int i = 0 ; i < first_single_list.size() ; i++) {
				Star star1 = (Star)first_single_list.elementAt(i);
				double search_radius = getSearchRadius(star1.getMag());

				Star star2 = null;
				try {
					StarList l2 = new StarList(map2.getPartialListWithinRadius(new Position(star1.getX(), star1.getY()), search_radius));
					l2.sort();

					for (int j = 0 ; j < l2.size() ; j++) {
						Star s2 = (Star)l2.elementAt(j);
						double search_radius2 = getSearchRadius(s2.getMag());

						Vector l1 = map1.getPartialListWithinRadius(new Position(s2.getX(), s2.getY()), search_radius2);
						Star s1 = null;
						double min_dist = 0.0;
						for (int k = 0 ; k < l1.size() ; k++) {
							Star s = (Star)l1.elementAt(k);
							double dist = s.getDistanceFrom(s2);
							if (s1 == null  ||  min_dist > dist) {
								s1 = s;
								min_dist = dist;
							}
						}

						// The closest one must be the current star.
						if (s1 == star1) {
							star2 = s2;
							break;
						}
					}
				} catch ( OutOfBoundsException exception ) {
					// Never happens.
				}

				// Creates a pair.
				if (star2 == null) {
					pair_list.addElement(star1.getPair());
				} else {
					StarPair pair = new StarPair(star1, star2);
					pair_list.addElement(pair);

					paired_count++;
				}

				// Removes the paired star from the map.
				map1.removePosition(star1);
				if (star2 != null)
					map2.removePosition(star2);
			}
			monitor_set.addMessage("Number of new pairs: " + paired_count);

			// Adds the single stars in the second list to the list of pairs.
			Vector l = map2.getAllPositions();
			for (int j = 0 ; j < l.size() ; j++) {
				Star star2 = (Star)l.elementAt(j);
				pair_list.addElement(star2.getPair());
			}
		}

		monitor_set.addMessage(new Date().toString());
		monitor_set.addSeparator();
	}
}
