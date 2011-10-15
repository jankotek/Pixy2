/*
 * @(#)DefaultMatchingSolver.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.pixy.matching;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;

/**
 * The <code>DefaultMatchingSolver</code> is a class to solve matching
 * between two lists of stars. Only some bright stars are used in
 * matching. In general, the first list must contain the detected 
 * stars, and the second list must contain the catalog data. And the
 * map area of the second list is 4 times of that of the first list.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 September 17
 */

public class DefaultMatchingSolver extends Operation {
	/**
	 * The first list of stars and the area.
	 */
	protected PositionMap original_map1;

	/**
	 * The second list of stars and the area.
	 */
	protected PositionMap original_map2;

	/**
	 * The map function to convert (x,y) position in the first list to
	 * (x,y) position in the second list.
	 */
	protected MapFunction map_function;

	/**
	 * The mode.
	 */
	protected int mode = MODE_IMAGE_TO_CATALOG;

	/**
	 * The mode number which represents the matching between the 
	 * detected stars and the catalog data.
	 */
	public final static int MODE_IMAGE_TO_CATALOG = 1;

	/**
	 * The mode number which represents the matching between the 
	 * detected stars and the detected stars.
	 */
	public final static int MODE_IMAGE_TO_IMAGE = 2;

	/**
	 * The accuracy to judge a mapped position of a detected star 
	 * coincides with a position of a catalog data.
	 */
	protected double check_accuracy = 2.0;

	/**
	 * The matching score.
	 */
	protected double score = 0.0;

	/**
	 * The exception thrown when matching is failed.
	 */
	protected MatchingFailedException matching_failed_exception = null;

	/**
	 * Constructs a <code>DefaultMatchingSolver</code> with two lists
	 * of stars and the area. In general, the first list must contain
	 * the detected stars, and the second list must contain the 
	 * catalog data.
	 * @param map1 the first list of stars and the area.
	 * @param map2 the second list of stars and the area.
	 */
	public DefaultMatchingSolver ( PositionMap map1, PositionMap map2 ) {
		original_map1 = map1;
		original_map2 = map2;

		mode = MODE_IMAGE_TO_CATALOG;
	}

	/**
	 * Gets the map function to convert (x,y) position in the first
	 * list to (x,y) position in the second list.
	 * @return the map function to convert (x,y) position in the first
	 * list to (x,y) position in the second list.
	 * @exception MatchingFailedException if the best score after
	 * all searches is less than the specified score to fail.
	 */
	public MapFunction getMapFunction ( )
		throws MatchingFailedException
	{
		if (matching_failed_exception != null)
			throw matching_failed_exception;

		return map_function;
	}

	/**
	 * Sets the mode.
	 * @param m the mode.
	 */
	public void setMode ( int m ) {
		mode = m;
	}

	/**
	 * Sets the accuracy to judge a mapped position of a detected star 
	 * coincides with a position of a catalog data.
	 * @param accuracy the accuracy.
	 */
	public void setCheckAccuracy ( double accuracy ) {
		check_accuracy = accuracy;
	}

	/**
	 * Gets the matching score.
	 * @return the matching score.
	 */
	public double getScore ( ) {
		return score;
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
		monitor_set.addMessage("[Matching]");
		monitor_set.addMessage(new Date().toString());

		matching_failed_exception = null;

		if (original_map1 != null  &&  original_map2 != null) {
			MatchingPositionMap map1 = new MatchingPositionMap(original_map1);
			MatchingPositionMap map2 = new MatchingPositionMap(original_map2);

			// Divides the two maps.
			if (mode == MODE_IMAGE_TO_IMAGE) {
				map1.divide(4, 4);
				map2.divide(4, 4);
			} else {
				map1.divide(3, 3);
				map2.divide(8, 8);
			}

			// Selects data used in matching.
			PositionList list1 = map1.selectDataByPart(3);
			PositionList list2 = map2.selectDataByPart(3);
			PositionList checklist1 = map1.selectDataByPart(9);
			PositionList checklist2 = null;
			if (mode == MODE_IMAGE_TO_IMAGE) {
				checklist2 = map2.selectDataByPart(9);
			} else {
				checklist2 = map2.selectAllData();
			}

			// In general, the first list must contain the detected 
			// stars, and the second list must contain the catalog data.
			monitor_set.addMessage("Detected stars to use: " + list1.size());
			monitor_set.addMessage("Catalog data to use: " + list2.size());
			monitor_set.addMessage("Detected stars to check: " + checklist1.size());
			monitor_set.addMessage("Catalog data to check: " + checklist2.size());

			// Matching based on the triangles pattern matching.
			TriangleMatchingSolver solver = new TriangleMatchingSolver(list1, list2);
			solver.setCheckList(checklist1, checklist2);
			solver.setTriGraphSearchSteps(3, 6);
			solver.setTriangleMapDivisionParameters(40, 1.05);
			solver.setScoreToPass(0.35);
			solver.setScoreToFail(0.2);
			solver.setAcceptableRatio(2.0);
			solver.setCheckAccuracy(check_accuracy);
			solver.addMonitor(monitor_set);

			try {
				map_function = solver.solve();
				score = solver.getScore();
			} catch ( MatchingFailedException exception ) {
				monitor_set.addMessage("Matching failed.");
				monitor_set.addMessage(new Date().toString());
				monitor_set.addSeparator();

				map_function = exception.getMapFunction();
				score = exception.getScore();

				matching_failed_exception = exception;
				return;
			}

			monitor_set.addMessage("Matching succeeded.");
			monitor_set.addMessage("Map function: " + map_function.getOutputString());
			monitor_set.addMessage(new Date().toString());
			monitor_set.addSeparator();
		}
	}

	/**
	 * The <code>MatchingPositionMap</code> represents a rectangular
	 * map of <code>StarPosition</code>s.
	 * <p>
	 * It has functions to use or select only some bright stars, so 
	 * the list will be sorted in order of magnitude. However, the 
	 * specified list is copied in the constructor, so the original
	 * list will not change.
	 */
	protected class MatchingPositionMap extends PositionMap {
		/**
		 * Constructs a <code>MatchingPositionMap</code> from a 
		 * <code>PositionMap</code>. The list will be copied, but the
		 * elements are not copied.
		 * @param original_map the original map of positions.
		 */
		public MatchingPositionMap ( PositionMap original_map ) {
			super(original_map);

			StarPositionList l = new StarPositionList(list);
			l.sort();
			list = l;
		}

		/**
		 * Reduces the number of elements in the list. The list must
		 * be sorted previously, which is in general sorted in the 
		 * constructor.
		 * @param count the number of elements in the list after
		 * reduction.
		 */
		public void reduceDataTo ( int count ) {
			if (list.size() <= count)
				return;

			list.setSize(count);
		}

		/**
		 * Selects data up to the specified number by part and returns
		 * a new sorted list. The map must be divided previously. If
		 * not divided, it returns null.
		 * @param count the number of elements to select by part.
		 * @return the new list.
		 */
		public StarPositionList selectDataByPart ( int count ) {
			if (table == null)
				return null;

			StarPositionList l = new StarPositionList();
			for (int row = 0 ; row < table_rows ; row++) {
				for (int column = 0 ; column < table_columns ; column++) {
					for (int i = 0 ; i < table[row][column].size()  &&  i < count ; i++)
						l.addElement((StarPosition)table[row][column].elementAt(i));
				}
			}

			l.sort();
			return l;
		}

		/**
		 * Selects all data on this map and returns a new sorted list.
		 * @return the new list.
		 */
		public StarPositionList selectAllData ( ) {
			StarPositionList l = new StarPositionList(list);
			l.sort();
			return l;
		}
	}
}
