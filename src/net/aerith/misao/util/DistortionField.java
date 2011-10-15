/*
 * @(#)DistortionField.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;
import java.util.Vector;

/**
 * The <code>DistortionField</code> represents distortion of (x,y)
 * positions by cubic function.
 * <p>
 * The constructor requires the list of pair of <code>Position</code>
 * data. The distortion field represents the difference of (x,y) 
 * positions. The method <code>getValue</code> requires the (x,y) 
 * position of the first element of pair and returns the difference
 * between the (x,y) position of the second element of pair.
 * <p>
 * The relations among these values are as follows.
 * <pre>
 *     (dx, dy) = getValue (x1, y1)
 *     (x2, y2) = (x1, y1) + (dx, dy)
 * </pre>
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2000 October 20
 */

public class DistortionField {
	/**
	 * The cubic function to calculate distortion of x.
	 */
	public Cubics cubics_dx;

	/**
	 * The cubic function to calculate distortion of y.
	 */
	public Cubics cubics_dy;

	/**
	 * Constructs a flat <code>DistortionField</code>.
	 */
	public DistortionField ( ) {
		cubics_dx = new Cubics();
		cubics_dy = new Cubics();
	}

	/**
	 * Constructs a <code>DistortionField</code> based on the 
	 * specified list of pairs of <code>Position</code> data.
	 * @param pair_list the list of pairs of <code>Position</code> 
	 * data.
	 */
	public DistortionField ( Vector pair_list ) {
		this();

		for (int i = 0 ; i < pair_list.size() ; i++) {
			Pair pair = (Pair)pair_list.elementAt(i);
			Position position1 = pair.getFirstPosition();
			Position position2 = pair.getSecondPosition();

			if (position1 != null  &&  position2 != null) {
				cubics_dx.setData(position1.getX(), position1.getY(), position2.getX() - position1.getX());
				cubics_dy.setData(position1.getX(), position1.getY(), position2.getY() - position1.getY());
			}
		}

		cubics_dx.solve();
		cubics_dy.solve();
	}

	/**
	 * Gets the inverse function of this distortion field.
	 * @return the inverse function of this distortion field.
	 */
	public DistortionField inverse ( ) {
		DistortionField df = new DistortionField();
		df.cubics_dx = cubics_dx.negative();
		df.cubics_dy = cubics_dy.negative();
		return df;
	}

	/**
	 * Gets the distortion value at the specified position.
	 * @return the distortion value at the specified position.
	 */
	public Position getValue ( Position pos ) {
		return new Position(cubics_dx.getValue(pos.getX(), pos.getY()), cubics_dy.getValue(pos.getX(), pos.getY()));
	}
}
