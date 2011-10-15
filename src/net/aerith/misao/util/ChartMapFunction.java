/*
 * @(#)ChartMapFunction.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;

/**
 * The <code>ChartMapFunction</code> represents functions to convert
 * (x,y) position to R.A. and Decl. coordinates, and to convert R.A.
 * and Decl. to (x,y) position. It consists of the R.A. and Decl. of
 * the (0,0) position, the scale unit per 1 degree, and the position
 * angle of the up direction.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2002 November 22
 */

public class ChartMapFunction {
	/**
	 * The R.A. and Decl. where (0,0) will be mapped.
	 */
	protected Coor center_coor;

	/**
	 * The scale unit per 1 degree.
	 */
	protected double scale_per_degree;

	/**
	 * The position angle of up direction.
	 */
	protected double position_angle;

	/**
	 * Constructs a <code>ChartMapFunction</code> with the specified 
	 * R.A. and Decl. of the (0,0) position, the scale unit per 1 
	 * degree, and the position angle of up direction.
	 * @param initial_coor  the initial R.A. and Decl. of (0,0) 
	 * position.
	 * @param initial_scale the initial scale unit per 1 degree.
	 * @param initial_pa    the initial position angle of up direction.
	 */
	public ChartMapFunction ( Coor initial_coor, double initial_scale, double initial_pa ) {
		center_coor = initial_coor;
		scale_per_degree = initial_scale;
		position_angle = initial_pa;
	}

	/**
	 * Gets the R.A. and Decl. of the center.
	 * @return the R.A. and Decl. of the center.
	 */
	public Coor getCenterCoor ( ) {
		return center_coor;
	}

	/**
	 * Gets the scale unit per 1 degree.
	 * @return the scale unit per 1 degree.
	 */
	public double getScaleUnitPerDegree ( ) {
		return scale_per_degree;
	}

	/**
	 * Gets the position angle of up direction.
	 * @return the position angle of up direction.
	 */
	public double getPositionAngle ( ) {
		return position_angle;
	}

	/**
	 * Maps the virtual chart of (x,y) positions represented by the 
	 * R.A. and Decl. of the center, the scale unit per 1 degree, and
	 * the position angle of up direction of this 
	 * <code>ChartMapFunction</code>, to a new virtual chart of (x,y)
	 * positions by the specified <code>MapFunction</code>, and 
	 * creates a new <code>ChartMapFunction</code> which represents
	 * the mapped chart.
	 * @param map_function the map function.
	 * @return the chart map function which consists of the R.A. and
	 * Decl. of the center, the scale unit per 1 degree, and the 
	 * position angle of up direction on the mapped virtual chart.
	 */
	public ChartMapFunction map ( MapFunction map_function ) {
		MapFunction inv_mf = map_function.inverse();

		Position center_position = inv_mf.map(new Position(0.0, 0.0));
		Coor new_center_coor = mapXYToCoordinates(center_position);

		double new_scale_per_degree = scale_per_degree * map_function.getRatio();

		Position upper_position = inv_mf.map(new Position(0.0, -1.0));
		Coor upper_coor = mapXYToCoordinates(upper_position);
		double new_position_angle = new_center_coor.getPositionAngleTo(upper_coor);

		return new ChartMapFunction(new_center_coor, new_scale_per_degree, new_position_angle);
	}

	/**
	 * Maps the specified R.A. and Decl. to the (x,y) position. The 
	 * positive directions of (x,y) is to the right and down.
	 * @param coor the R.A. and Decl. to map.
	 * @return the mapped (x,y) position.
	 */
	public Position mapCoordinatesToXY ( Coordinates coor ) {
		double radius = center_coor.getAngularDistanceTo(coor);
		double theta = center_coor.getPositionAngleTo(coor);

		theta -= position_angle;

		Position position = new Position(- radius * Math.sin(theta * Astro.RAD), - radius * Math.cos(theta * Astro.RAD));
		position.rescale(scale_per_degree);

		return position;
	}

	/**
	 * Maps the specified (x,y) position to the R.A. and Decl. The 
	 * positive directions of (x,y) must be to the right and down.
	 * @param position the (x,y) position to map.
	 * @return the mapped R.A. and Decl.
	 */
	public Coor mapXYToCoordinates ( Position position ) {
		double x = position.getX() / scale_per_degree;
		double y = position.getY() / scale_per_degree;

		double radius = Math.sqrt(x * x + y * y);
		double theta = Math.atan2(y, x) / Astro.RAD - position_angle + 90.0;
		theta = Astro.normalize(theta, 360.0);

		double sin_t = Math.sin(center_coor.getDecl() * Astro.RAD) * Math.cos(radius * Astro.RAD) + Math.cos(center_coor.getDecl() * Astro.RAD) * Math.sin(radius * Astro.RAD) * Math.cos(theta * Astro.RAD);
		double decl = Math.atan2(sin_t, Math.sqrt(1 - sin_t * sin_t)) / Astro.RAD;
		double ra = 0.0;
		if (Math.cos(decl * Astro.RAD) != 0.0) {
			if (center_coor.getDecl() >= 90) {
				theta = Math.atan2(- x, - y) / Astro.RAD;
				ra = 180 + center_coor.getRA() - position_angle - theta;
			} else if (center_coor.getDecl() <= -90) {
				theta = Math.atan2(- x, - y) / Astro.RAD;
				ra = center_coor.getRA() + position_angle + theta;
			} else {
				sin_t = Math.sin(theta * Astro.RAD) * Math.sin(radius * Astro.RAD) / Math.cos(decl * Astro.RAD);
				double cos_t = (Math.cos(radius * Astro.RAD) - Math.sin(decl * Astro.RAD) * Math.sin(center_coor.getDecl() * Astro.RAD)) / (Math.cos(decl * Astro.RAD) * Math.cos(center_coor.getDecl() * Astro.RAD));
				ra = center_coor.getRA() - Math.atan2(sin_t, cos_t) / Astro.RAD;
			}
			ra = Astro.normalize(ra, 360.0);
		}

		return new Coor(ra, decl);
	}
}
