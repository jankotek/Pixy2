/*
 * @(#)Graph.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;

/**
 * The <code>Graph</code> represents a graph which consists of some
 * nodes and branches connecting the nodes.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 January 28
 */

public class Graph {
	/**
	 * The number of nodes.
	 */
	protected int number_of_nodes;

	/**
	 * The branch[i][j] is true if node i and j is directly connected.
	 */
	protected boolean[][] branch;

	/**
	 * The maximum steps to search, used in method 
	 * <code>checkReachableIndexes</code>.
	 */
	private int max_steps;

	/**
	 * The reached_flag[i] is true if node i is reached already,
	 * used in method <code>checkReachableIndexes</code>.
	 */
	private boolean[] reached_flag;

	/**
	 * Constructs a <code>Graph</code> of the specified number of 
	 * nodes. No nodes are connected with the other nodes at first.
	 * @param nodes the number of nodes.
	 */
	public Graph ( int nodes ) {
		branch = new boolean[nodes][nodes];
		for (int i = 0 ; i < nodes ; i++) {
			for (int j = 0 ; j < nodes ; j++)
				branch[i][j] = false;
		}

		number_of_nodes = nodes;
	}

	/**
	 * Gets the number of nodes.
	 * @return the number of nodes.
	 */
	public int getNodeCount ( ) {
		return number_of_nodes;
	}

	/**
	 * Connects the node i and j.
	 * @param i the index of node to be connected.
	 * @param j the index of node to be connected.
	 */
	public void connect ( int i, int j ) {
		branch[i][j] = true;
		branch[j][i] = true;
	}

	/**
	 * Disconnects the node i and j.
	 * @param i the index of node to be disconnected.
	 * @param j the index of node to be disconnected.
	 */
	public void disconnect ( int i, int j ) {
		branch[i][j] = false;
		branch[j][i] = false;
	}

	/**
	 * Returns true if the node i and j are connected.
	 * @param i the index of node to check.
	 * @param j the index of node to check.
	 * @return true if the node i and j are connected.
	 */
	public boolean isConnected ( int i, int j ) {
		return branch[i][j];
	}

	/**
	 * Returns true if the node j is reachable from the node i.
	 * @param i the index of node to check.
	 * @param j the index of node to check.
	 * @return true if the node j is reachable from the node i.
	 */
	public boolean isReachable ( int i, int j ) {
		int[] reachable_index = getReachableIndexes(i);
		for (int k = 0 ; k < reachable_index.length ; k++) {
			if (j == reachable_index[k])
				return true;
		}
		return false;
	}

	/**
	 * Returns true if the node j is reachable from the node i within
	 * the specified steps.
	 * @param i     the index of node to check.
	 * @param j     the index of node to check.
	 * @param steps the maximum steps to reach to node j from i.
	 * @return true if the node j is reachable from the node i within
	 * the specified steps.
	 */
	public boolean isReachableInStep ( int i, int j, int steps ) {
		int[] reachable_index = getReachableIndexesInStep(i, steps);
		for (int k = 0 ; k < reachable_index.length ; k++) {
			if (j == reachable_index[k])
				return true;
		}
		return false;
	}

	/**
	 * Gets the list of indexes of nodes reachable from the
	 * specified node. The indexes to be returned are sorted.
	 * @param index the index of start node.
	 * @return the list of indexes of nodes reachable from the
	 * specified node.
	 */
	public int[] getReachableIndexes ( int index ) {
		return getReachableIndexesInStep(index, -1);
	}

	/**
	 * Gets the list of indexes of nodes reachable from the
	 * specified node within the specified steps. The indexes to be 
	 * returned are sorted.
	 * @param index the index of start node.
	 * @param steps the maximum steps to reach. In the case of -1, it 
	 * means there is no limit of steps to reach.
	 * @return the list of indexes of nodes reachable from the
	 * specified node within the specified steps.
	 */
	public int[] getReachableIndexesInStep ( int index, int steps ) {
		max_steps = steps;

		reached_flag = new boolean[getNodeCount()];
		for (int k = 0 ; k < getNodeCount() ; k++)
			reached_flag[k] = false;

		// Recurrsive checks.
		checkReachableIndexes(index, 0);

		int reached_count = 0;
		for (int k = 0 ; k < getNodeCount() ; k++) {
			if (reached_flag[k])
				reached_count++;
		}

		int[] list = new int[reached_count];
		reached_count = 0;
		for (int k = 0 ; k < getNodeCount() ; k++) {
			if (reached_flag[k]) {
				list[reached_count] = k;
				reached_count++;
			}
		}

		return list;
	}

	/**
	 * Checkes the reachable nodes. It is recurrsively invoked in
	 * method <code>getReachableIndexesInStep</code>. The 
	 * <tt>max_steps</tt> and <tt>reached_flag</tt> must be set
	 * properly before invoking this method.
	 * @param index        the index of start node.
	 * @param current_step the current step.
	 */
	private void checkReachableIndexes ( int index, int current_step ) {
		reached_flag[index] = true;

		if (current_step == max_steps)
			return;

		for (int j = 0 ; j < getNodeCount() ; j++) {
			if (index != j  &&  isConnected(index, j)  &&  reached_flag[j] == false)
				checkReachableIndexes(j, current_step + 1);
		}
	}
}
