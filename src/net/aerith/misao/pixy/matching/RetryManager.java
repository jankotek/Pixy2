/*
 * @(#)RetryManager.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.pixy.matching;
import java.util.*;
import net.aerith.misao.util.*;

/**
 * The <code>RetryManager</code> is a class to generate the initial
 * R.A. and Decl. of the center, and the initial field of view for
 * matching process. It generates some sets of those parameters based
 * on the specified policy.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class RetryManager {
	/**
	 * The number of policy which implies to run the matching process
	 * only once with no retry.
	 */
	public final static int POLICY_NO_RETRY = 0;

	/**
	 * The number of policy which implies the R.A. and Decl. is 
	 * uncertain and it retries the matching process 9 times while
	 * shifting the R.A. and Decl. around the specified position.
	 */
	public final static int POLICY_POSITION_UNCERTAIN = 1;

	/**
	 * The number of policy which implies to search the R.A. and Decl.
	 * within the specified radius, while shifting the R.A. and Decl. 
	 * around the specified position.
	 */
	public final static int POLICY_POSITION_SEARCH = 2;

	/**
	 * The policy.
	 */
	protected int policy = POLICY_NO_RETRY;

	/**
	 * The original R.A. and Decl.
	 */
	protected Coor original_coor;

	/**
	 * The original horizontal field of view.
	 */
	protected double original_fov_width;

	/**
	 * The original vertical field of view.
	 */
	protected double original_fov_height;

	/**
	 * The search radius.
	 */
	protected double search_radius = 0.0;

	/**
	 * The retry count.
	 */
	protected int retry_count;

	/**
	 * The list of center R.A. and Decl.
	 */
	private Vector coor_list;

	/**
	 * Constructs a <code>RetryManager</code>.
	 * @param center_coor the R.A. and Decl. of the center.
	 * @param fov_width   the horizontal field of view in degree.
	 * @param fov_height  the vertical field of view in degree.
	 */
	public RetryManager ( Coor center_coor,
						  double fov_width,
						  double fov_height )
	{
		original_coor = center_coor;
		original_fov_width = fov_width;
		original_fov_height = fov_height;

		retry_count = 0;
		coor_list = new Vector();

		coor_list.addElement(original_coor);
	}

	/**
	 * Sets the search radius.
	 * @param radius the search radius.
	 */
	public void setSearchRadius ( double radius ) {
		search_radius = radius;

		setPolicy(policy);
	}

	/**
	 * Sets the policy.
	 * @param policy the number of policy.
	 */
	public void setPolicy ( int policy ) {
		this.policy = policy;

		retry_count = 0;
		coor_list = new Vector();

		ChartMapFunction cmf = new ChartMapFunction(original_coor, 1.0, 0.0);
		double fov = original_fov_width;
		if (fov > original_fov_height)
			fov = original_fov_height;

		if (policy == POLICY_POSITION_UNCERTAIN) {
			double shift = fov / 3.0;

			Position position = new Position(0,0);
			position.rescale(shift);
			coor_list.addElement(cmf.mapXYToCoordinates(position));

			position = new Position(-1, 0);
			position.rescale(shift);
			coor_list.addElement(cmf.mapXYToCoordinates(position));

			position = new Position( 1, 0);
			position.rescale(shift);
			coor_list.addElement(cmf.mapXYToCoordinates(position));

			position = new Position( 0,-1);
			position.rescale(shift);
			coor_list.addElement(cmf.mapXYToCoordinates(position));

			position = new Position( 0, 1);
			position.rescale(shift);
			coor_list.addElement(cmf.mapXYToCoordinates(position));

			position = new Position(-1,-1);
			position.rescale(shift);
			coor_list.addElement(cmf.mapXYToCoordinates(position));

			position = new Position(-1, 1);
			position.rescale(shift);
			coor_list.addElement(cmf.mapXYToCoordinates(position));

			position = new Position( 1,-1);
			position.rescale(shift);
			coor_list.addElement(cmf.mapXYToCoordinates(position));

			position = new Position( 1, 1);
			position.rescale(shift);
			coor_list.addElement(cmf.mapXYToCoordinates(position));
		} else if (policy == POLICY_POSITION_SEARCH) {
			double shift = fov / 2.0;

			int count = (int)(search_radius / shift);

			for (int x = - count ; x <= count ; x++) {
				for (int y = - count ; y <= count ; y++) {
					Position position = new Position(x, y);
					position.rescale(shift);
					if (position.getDistanceFrom(new Position()) <= search_radius)
						coor_list.addElement(cmf.mapXYToCoordinates(position));
				}
			}
		} else {
			coor_list.addElement(original_coor);
		}
	}

	/**
	 * Increments the retry count.
	 * @exception MaximumRepetitionCountException if the retry count
	 * reaches to the maximum count.
	 */
	public void increment ( )
		throws MaximumRepetitionCountException
	{
		retry_count++;

		if (retry_count >= coor_list.size())
			throw new MaximumRepetitionCountException();
	}

	/**
	 * Gets the R.A. and Decl. of the center for the current retry.
	 * @return the R.A. and Decl. of the center for the current retry.
	 */
	public Coor getCenterCoor ( ) {
		return (Coor)coor_list.elementAt(retry_count);
	}

	/**
	 * Gets the horizontal field of view for the current retry.
	 * @return the horizontal field of view for the current retry.
	 */
	public double getHorizontalFov ( ) {
		return original_fov_width;
	}

	/**
	 * Gets the vertical field of view for the current retry.
	 * @return the vertical field of view for the current retry.
	 */
	public double getVerticalFov ( ) {
		return original_fov_height;
	}
}
