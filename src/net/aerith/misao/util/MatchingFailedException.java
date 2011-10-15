/*
 * @(#)MatchingFailedException.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;

/**
 * The <code>MatchingFailedException</code> is an exception thrown if
 * the matching process is failed.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 January 9
 */

public class MatchingFailedException extends Exception {
	/**
	 * The map function.
	 */
	protected MapFunction map_function = null;

	/**
	 * The matching score.
	 */
	protected double score = 0.0;

	/**
	 * Constructs an <code>MatchingFailedException</code>.
	 */
	public MatchingFailedException ( ) {
	}

	/**
	 * Constructs an <code>MatchingFailedException</code> with the
	 * calculated map function.
	 * @param failed_mf the calculated map function.
	 * @param score     the matching score.
	 */
	public MatchingFailedException ( MapFunction failed_mf, double score ) {
		map_function = failed_mf;
		this.score = score;
	}

	/**
	 * Gets the calculated map function.
	 * @return the calculated map function.
	 */
	public MapFunction getMapFunction ( ) {
		return map_function;
	}

	/**
	 * Gets the matching score. It must be invoked after the 
	 * <tt>run</tt> method was invoked.
	 * @return the matching score.
	 */
	public double getScore ( ) {
		return score;
	}
}
