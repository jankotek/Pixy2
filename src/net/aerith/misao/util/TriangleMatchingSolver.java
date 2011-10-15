/*
 * @(#)TriangleMatchingSolver.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;
import java.util.*;

/**
 * The <code>TriangleMatchingSolver</code> is a class to solve
 * matching between two lists of (x,y) positions, based on the
 * triangles pattern matching.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 May 18
 */

public class TriangleMatchingSolver {
	/**
	 * The first list of (x,y) positions.
	 */
	protected PositionList list1;

	/**
	 * The second list of (x,y) positions.
	 */
	protected PositionList list2;

	/**
	 * The first list of (x,y) positions to check if the map function 
	 * is proper.
	 */
	protected PositionList checklist1;

	/**
	 * The second list of (x,y) positions to check if the map function 
	 * is proper.
	 */
	protected PositionList checklist2;

	/**
	 * The set of monitors.
	 */
	protected MonitorSet monitor_set;

	/**
	 * The number of steps to create triangles from the graph of first
	 * (x,y) list.
	 */
	protected int trigraph_search_step1 = 1;

	/**
	 * The number of steps to create triangles from the graph of 
	 * second (x,y) list.
	 */
	protected int trigraph_search_step2 = 1;

	/**
	 * The number to divide the map of triangles, which is divided
	 * into <tt>triangle_map_division_count</tt> x 
	 * <tt>triangle_map_division_count</tt> parts.
	 */
	protected int triangle_map_division_count = 10;

	/**
	 * The unit of ratio of the map of triangles, which must be 
	 * greater than 1.0.
	 */
	protected double triangle_map_unit_ratio = 1.1;

	/**
	 * The score to pass. When the score of the map function is 
	 * greater than this value, it is judged as correct and returned
	 * immediately.
	 */
	protected double score_to_pass = 0.5;

	/**
	 * The score to fail. When the score of the map function is 
	 * less than this value after all trials finished, the matching 
	 * process is judged as failed and <code>MatchingFailedException</code>
	 * is thrown.
	 */
	protected double score_to_fail = 0.1;

	/**
	 * The upper limit of ratio of the map function to judge proper.
	 * If the ratio of the map function is between 
	 * 1.0 / <tt>acceptable_ratio</tt> and <tt>acceptable_ratio</tt>,
	 * it is proper.
	 */
	protected double acceptable_ratio = 2.0;

	/**
	 * The accuracy to judge a mapped position coincides with a
	 * position in the other list when to check if the calculated map
	 * function is proper. It will be the mesh size to divide the 
	 * check list map.
	 */
	protected double check_accuracy = 1.0;

	/**
	 * The best score of the matching.
	 */
	protected double best_score = 0.0;

	/**
	 * Constructs a <code>TriangleMatchingSolver</code> with two lists
	 * of (x,y) positions. 
	 * @param list1 the first list of (x,y) positions.
	 * @param list2 the second list of (x,y) positions.
	 */
	public TriangleMatchingSolver ( PositionList list1, PositionList list2 ) {
		this.list1 = list1;
		this.list2 = list2;
		checklist1 = list1;
		checklist2 = list2;

		monitor_set = new MonitorSet();
	}

	/**
	 * Adds a monitor.
	 * @param monitor the monitor.
	 */
	public void addMonitor ( Monitor monitor ) {
		monitor_set.addMonitor(monitor);
	}

	/**
	 * Sets the first list of (x,y) positions to check if the map 
	 * function is proper.
	 * @param list1 the first list of (x,y).
	 * @param list2 the second list of (x,y).
	 */
	public void setCheckList ( PositionList list1, PositionList list2 ) {
		checklist1 = list1;
		checklist2 = list2;
	}

	/**
	 * Sets the number of steps to create triangles from the two 
	 * graphs of (x,y) lists.
	 */
	public void setTriGraphSearchSteps ( int step1, int step2 ) {
		trigraph_search_step1 = step1;
		trigraph_search_step2 = step2;
	}

	/**
	 * Sets the number to divide and the unit of ratio of the map of 
	 * triangles.
	 * <p>
	 * The map of triangles is divided into <tt>division_count</tt> x 
	 * <tt>division_count</tt> parts. The unit of ratio must be
	 * greater than 1.0.
	 * @param division_count the number to divide this map.
	 * @param unit_ratio     the unit of ratio to divide.
	 * @exception IllegalArgumentException if the specified 
	 * division count is not greater than 1.0.
	 */
	public void setTriangleMapDivisionParameters ( int division_count, double unit_ratio )
		throws IllegalArgumentException
	{
		if (unit_ratio <= 1.0)
			throw new IllegalArgumentException();

		triangle_map_division_count = division_count;
		triangle_map_unit_ratio = unit_ratio;
	}

	/**
	 * Sets the score to pass. When the score of the map function is 
	 * greater than this value, it is judged as correct and returned
	 * immediately.
	 * @param score the score to pass.
	 */
	public void setScoreToPass ( double score ) {
		score_to_pass = score;
	}

	/**
	 * Sets the score to fail. When the score of the map function is 
	 * less than this value after all trials finished, the matching 
	 * process is judged as failed and <code>MatchingFailedException</code>
	 * is thrown.
	 * @param score the score to fail.
	 */
	public void setScoreToFail ( double score ) {
		score_to_fail = score;
	}

	/**
	 * Sets the upper limit of ratio of the map function to judge 
	 * proper.
	 * @param ratio the acceptable ratio.
	 */
	public void setAcceptableRatio ( double ratio ) {
		acceptable_ratio = ratio;
	}

	/**
	 * Sets the accuracy to judge a mapped position coincides with a
	 * position in the other list when to check if the calculated map
	 * function is proper. It will be the mesh size to divide the 
	 * check list map.
	 * @param accuracy the accuracy.
	 */
	public void setCheckAccuracy ( double accuracy ) {
		check_accuracy = accuracy;
	}

	/**
	 * Gets the matching score. It must be invoked after the 
	 * <tt>run</tt> method was invoked.
	 * @return the matching score.
	 */
	public double getScore ( ) {
		return best_score;
	}

	/**
	 * Solves matching between the specified lists of (x,y) positions
	 * and returns a <code>MapFunction</code> which converts (x,y)
	 * position in the first list to (x,y) position in the second list.
	 * @return the map function to convert (x,y) position.
	 * @exception MatchingFailedException if the best score after
	 * all searches is less than the specified score to fail.
	 */
	public MapFunction solve ( )
		throws MatchingFailedException
	{
		Graph tree1 = list1.createMinimumGlobalTree();
		Graph tree2 = list2.createMinimumGlobalTree();

		TriIndex[] tri_indexes1 = new TriGraph(tree1).createTriangles(trigraph_search_step1);
		TriIndex[] tri_indexes2 = new TriGraph(tree2).createTriangles(trigraph_search_step2);

		Triangle[] triangles1 = new Triangle[tri_indexes1.length];
		for (int i = 0 ; i < tri_indexes1.length ; i++)
			triangles1[i] = new Triangle((Position)list1.elementAt(tri_indexes1[i].element(0)),
										 (Position)list1.elementAt(tri_indexes1[i].element(1)),
										 (Position)list1.elementAt(tri_indexes1[i].element(2)));
		Triangle[] triangles2 = new Triangle[tri_indexes2.length];
		for (int i = 0 ; i < tri_indexes2.length ; i++)
			triangles2[i] = new Triangle((Position)list2.elementAt(tri_indexes2[i].element(0)),
										 (Position)list2.elementAt(tri_indexes2[i].element(1)),
										 (Position)list2.elementAt(tri_indexes2[i].element(2)));

		monitor_set.addMessage("Triangles of list 1: " + triangles1.length);
		monitor_set.addMessage("Triangles of list 2: " + triangles2.length);

		TriangleMap map1 = new TriangleMap(triangles1, triangle_map_division_count, triangle_map_unit_ratio);
		TriangleMap map2 = new TriangleMap(triangles2, triangle_map_division_count, triangle_map_unit_ratio);

		// The map of check list for list2.
		PositionMap checklist2_map = new PositionMap(checklist2);
		checklist2_map.divide(100, 100);

		// Calculates candidates of map function repeatedly
		// while searching similar triangles.
		monitor_set.addMessage("Matching progress:");

		MapFunction map_function = new MapFunction();
		best_score = 0.0;

		for (int j = 0 ; j < triangles1.length ; j++) {
			Triangle tri1 = triangles1[j];

			Vector tri2_list = null;
			try {
				tri2_list = map2.getPartialListWithinSteps(new TrianglePosition(tri1), 1, 1);
			} catch ( OutOfBoundsException exception ) {
				// Never happens. Out-of-bounds data are accepted.
			}

			monitor_set.addMessage("  " + j + " / " + triangles1.length + " : " + tri2_list.size());

			for (int k = 0 ; k < tri2_list.size() ; k++) {
				Triangle tri2 = ((TrianglePosition)tri2_list.elementAt(k)).getTriangle();

				try {
					MapFunction mf = new MapFunction(tri1, tri2);

					if (1.0 / acceptable_ratio < mf.getRatio()  &&  mf.getRatio() < acceptable_ratio) {
						// Calculates the score of the map function, 
						// and checks if it is proper.
						int matched_count = 0;
						for (int i = 0 ; i < checklist1.size() ; i++) {
							Position position1 = (Position)checklist1.elementAt(i);
							position1 = mf.map(position1);

							try {
								Vector matched_list = checklist2_map.getPartialListWithinRadius(position1, check_accuracy);
								if (matched_list.size() > 0)
									matched_count++;
							} catch ( OutOfBoundsException exception ) {
							}
						}

						double score = (double)matched_count / (double)checklist1.size();

						if (score > best_score) {
							best_score = score;
							map_function = mf;

							monitor_set.addMessage(map_function.getOutputString());
							monitor_set.addMessage("Score: " + Format.formatDouble(best_score, 4, 1));

							if (best_score >= score_to_pass)
								return map_function;
						}
					}
				} catch ( ArithmeticException exception ) {
					// The two triangles tri1 and tri2 are not similar.
				}
			}
		}

		if (best_score < score_to_fail)
			throw new MatchingFailedException(map_function, best_score);

		return map_function;
	}

	/**
	 * The <code>TriIndex</code> represents a set of three indexes in 
	 * the original list which expresses a triangle.
	 */
	protected class TriIndex {
		/**
		 * The three indexes.
		 */
		protected int[] indexes;

		/**
		 * Constructs a <code>TriIndex</code>.
		 * @param index1 the first index.
		 * @param index2 the second index.
		 * @param index3 the third index.
		 */
		public TriIndex ( int index1, int index2, int index3 ) {
			indexes = new int[3];
			indexes[0] = index1;
			indexes[1] = index2;
			indexes[2] = index3;
		}

		/**
		 * Gets an index.
		 * @param n the n-th index to be returned.
		 */
		public int element ( int n ) {
			return indexes[n];
		}
	}

	/**
	 * The <code>TriGraph</code> represents a graph which has function
	 * to create triangles from itself.
	 */
	protected class TriGraph {
		/**
		 * The graph to create triangles from.
		 */
		protected Graph graph;

		/**
		 * Constructs a <code>TriGraph</code> with a <code>Graph</code>.
		 * @param graph the graph object.
		 */
		public TriGraph ( Graph graph ) {
			this.graph = graph;
		}

		/**
		 * Creates triangles from the graph, only based on the nodes
		 * connected within the specified steps.
		 * @param steps the upper limit of steps to search nodes to 
		 * create a triangle.
		 * @return the list of triangles, a triangle consists of three
		 * indexes of nodes in the original list of the specified 
		 * graph.
		 */
		public TriIndex[] createTriangles ( int steps ) {
			boolean[][] flag = new boolean[graph.getNodeCount()][graph.getNodeCount()];
			for (int i = 0 ; i < graph.getNodeCount() ; i++) {
				for (int j = 0 ; j < graph.getNodeCount() ; j++)
					flag[i][j] = false;
			}
			for (int i = 0 ; i < graph.getNodeCount() ; i++) {
				int[] index = graph.getReachableIndexesInStep(i, steps);
				for (int j = 0 ; j < index.length ; j++)
					flag[i][index[j]] = true;
			}

			Vector l = new Vector();
			for (int i = 0 ; i < graph.getNodeCount() - 2 ; i++) {
				for (int j = i +1 ; j < graph.getNodeCount() - 1 ; j++) {
					if (flag[i][j]) {
						for (int k = j + 1 ; k < graph.getNodeCount() ; k++) {
							if (flag[i][k]) {
								TriIndex tri = new TriIndex(i, j, k);
								l.addElement(tri);
							}
						}
					}
				}
			}

			TriIndex[] indexes = new TriIndex[l.size()];
			for (int i = 0 ; i < l.size() ; i++)
				indexes[i] = (TriIndex)l.elementAt(i);

			return indexes;
		}
	}

	/**
	 * The <code>TrianglePosition</code> represents a triangle with
	 * function to get virtual (x,y) position on a <code>TriangleMap</code>.
	 * <p>
	 * When the edge 1 is the longest, the edge 2 is second, and the 
	 * edge 3 is the shortest, the x value represents the ratio edge 2
	 * to edge 2, the y value represents the ratio edge 3 to edge 2.
	 */
	protected class TrianglePosition extends Position {
		/**
		 * The triangle.
		 */
		protected Triangle triangle;

		/**
		 * Constructs a <code>TrianglePosition</code> with a
		 * <code>Triangle</code>.
		 * @param triangle the triangle.
		 */
		public TrianglePosition ( Triangle triangle ) {
			Position pos1 = triangle.element(0);
			Position pos2 = triangle.element(1);
			Position pos3 = triangle.element(2);

			double r1 = Math.sqrt((pos1.getX() - pos2.getX()) * (pos1.getX() - pos2.getX()) + (pos1.getY() - pos2.getY()) * (pos1.getY() - pos2.getY()));
			double r2 = Math.sqrt((pos2.getX() - pos3.getX()) * (pos2.getX() - pos3.getX()) + (pos2.getY() - pos3.getY()) * (pos2.getY() - pos3.getY()));
			double r3 = Math.sqrt((pos3.getX() - pos1.getX()) * (pos3.getX() - pos1.getX()) + (pos3.getY() - pos1.getY()) * (pos3.getY() - pos1.getY()));

			if (r2 < r1) {
				double swap = r2; r2 = r1; r1 = swap;
			}
			if (r3 < r2) {
				double swap = r3; r3 = r2; r2 = swap;
			}
			if (r2 < r1) {
				double swap = r2; r2 = r1; r1 = swap;
			}

			setX(r2 / r1);
			setY(r3 / r2);

			this.triangle = triangle;
		}

		/**
		 * Gets the triangle.
		 * @return the triangle.
		 */
		public Triangle getTriangle ( ) {
			return triangle;
		}
	}

	/**
	 * The <code>TriangleMap</code> represents a map of triangles 
	 * based on the ratio of edges.
	 */
	protected class TriangleMap extends PositionMap {
		/**
		 * The unit of ratio, which must be greater than 1.0.
		 */
		protected double unit_ratio = 1.1;

		/**
		 * Constructs a <code>TriangleMap</code> with a list of
		 * <code>Triangle</code>s, the number to divide this map and 
		 * the unit of ratio, and divides this map into some parts for
		 * fast search.
		 * <p>
		 * This map is divided into <tt>division_count</tt> x 
		 * <tt>division_count</tt> parts. The unit of ratio must be
		 * greater than 1.0.
		 * @param triangles      the list of triangles.
		 * @param division_count the number to divide this map.
		 * @param unit_ratio     the unit of ratio to divide.
		 * @exception IllegalArgumentException if the specified 
		 * division count is not greater than 1.0.
		 */
		public TriangleMap ( Triangle[] triangles, int division_count, double unit_ratio )
			throws IllegalArgumentException
		{
			super(new Position(0.0, 0.0), new Position((double)division_count, (double)division_count));

			if (unit_ratio <= 1.0)
				throw new IllegalArgumentException();

			this.unit_ratio = unit_ratio;

			acceptOutOfBounds();

			try {
				for (int i = 0 ; i < triangles.length ; i++)
					addPosition(new TrianglePosition(triangles[i]));

				divide(division_count, division_count);
			} catch ( OutOfBoundsException exception ) {
				// Never happens.
			}
		}

		/**
		 * Converts the (x,y) position into the proper system for this 
		 * map.
		 * @param position the original (x,y).
		 * @return the converted position in the system of this map.
		 */
		protected Position convertPosition ( Position position ) {
			double x = Math.log(position.getX()) / Math.log(unit_ratio);
			double y = Math.log(position.getY()) / Math.log(unit_ratio);
			return new Position(x, y);
		}
	}
}
