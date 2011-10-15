/*
 * @(#)PositionList.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;
import java.util.Vector;

/**
 * The <code>PositionList</code> represents a list of
 * <code>Position</code>.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2000 August 27
 */

public class PositionList extends Vector {
	/**
	 * Constructs an empty <code>PositionList</code>.
	 */
	public PositionList ( ) {
		super();
	}

	/**
	 * Copies the specified <code>PositionList</code> and constructs a
	 * new <code>PositionList</code>.
	 * @param original_list the original_list to copy.
	 */
	public PositionList ( Vector original_list ) {
		super();

		for (int i = 0 ; i < original_list.size() ; i++)
			addElement(original_list.elementAt(i));
	}

	/**
	 * Shifts the (x,y) of all elements by adding the specified 
	 * <code>Position</code>.
	 * @param position the value to shift.
	 */
	public void shift ( Position position ) {
		for (int i = 0 ; i < size() ; i++)
			((Position)elementAt(i)).add(position);
	}

	/**
	 * Maps the (x,y) of all elements based on the specified map
	 * function.
	 * @param mf the map function.
	 */
	public void map ( MapFunction mf ) {
		for (int i = 0 ; i < size() ; i++) {
			Position position = (Position)elementAt(i);
			Position position2 = mf.map(position);
			position.setX(position2.getX());
			position.setY(position2.getY());
		}
	}

	/**
	 * Creates the minimum global tree from the list of
	 * <code>Position</code>.
	 * @return the minimum global tree.
	 */
	public Graph createMinimumGlobalTree ( ) {
		Graph tree = new Graph(size());

		double[][] distance = new double[size()][size()];
		for (int i = 0 ; i < size() ; i++) {
			for (int j = i + 1 ; j < size() ; j++) {
				Position p1 = (Position)elementAt(i);
				Position p2 = (Position)elementAt(j);
				distance[i][j] = Math.sqrt((p1.getX() - p2.getX()) * (p1.getX() - p2.getX()) + (p1.getY() - p2.getY()) * (p1.getY() - p2.getY()));
			}
		}
		for (int i = 0 ; i < size() ; i++) {
			distance[i][i] = -1.0;
		}

		for (int k = 0 ; k < size() - 1 ; k++) {
			double min = -1.0;
			int min_i = -1;
			int min_j = -1;
			for (int i = 0 ; i < size() ; i++) {
				for (int j = i + 1 ; j < size() ; j++) {
					if (distance[i][j] > 0.0  &&  (distance[i][j] < min  ||  min < 0.0)) {
						min = distance[i][j];
						min_i = i;
						min_j = j;
					}
				}
			}

			tree.connect(min_i, min_j);

			int[] reachable_list = tree.getReachableIndexes(min_i);
			for (int i = 0 ; i < reachable_list.length ; i++) {
				for (int j = 0 ; j < reachable_list.length ; j++) {
					distance[reachable_list[i]][reachable_list[j]] = -1.0;
					distance[reachable_list[j]][reachable_list[i]] = -1.0;
				}
			}
		}

		return tree;
	}
}
