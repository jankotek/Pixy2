/*
 * @(#)Xyz.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;

/**
 * The <code>Xyz</code> represents a set of (x,y,z).
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 October 3
 */

public class Xyz {
	/**
	 * The x value.
	 */
	protected double x = 0.0;

	/**
	 * The y value.
	 */
	protected double y = 0.0;

	/**
	 * The z value.
	 */
	protected double z = 0.0;

	/**
	 * Constructs an empty <code>Xyz</code>. The (x,y,z) are set as 
	 * (0,0,0).
	 */
	public Xyz ( ) {
	}

	/**
	 * Constructs an <code>Xyz</code> with specified (x,y,z).
	 * @param initial_x the x position.
	 * @param initial_y the y position.
	 * @param initial_z the z position.
	 */
	public Xyz ( double initial_x, double initial_y, double initial_z ) {
		setX(initial_x);
		setY(initial_y);
		setZ(initial_z);
	}

	/**
	 * Gets the x value.
	 * @return the x value.
	 */
	public double getX ( ) {
		return x;
	}

	/**
	 * Sets the x value.
	 * @param new_x the new x value.
	 */
	public void setX ( double new_x ) {
		x = new_x;
	}

	/**
	 * Gets the y value.
	 * @return the y value.
	 */
	public double getY ( ) {
		return y;
	}

	/**
	 * Sets the y value.
	 * @param new_y the new y value.
	 */
	public void setY ( double new_y ) {
		y = new_y;
	}

	/**
	 * Gets the z value.
	 * @return the z value.
	 */
	public double getZ ( ) {
		return z;
	}

	/**
	 * Sets the z value.
	 * @param new_z the new z value.
	 */
	public void setZ ( double new_z ) {
		z = new_z;
	}

	/**
	 * Gets the radius.
	 * @return the radius.
	 */
	public double getRadius ( ) {
		return Math.sqrt(getX() * getX() + getY() * getY() + getZ() * getZ());
	}

	/**
	 * Converts this (x,y,z) position to the R.A. and Decl.
	 * @return the R.A. and Decl.
	 */
	public Coor convertToCoor ( ) {
		double ra = Math.atan2(getY(), getX()) / Astro.RAD;
		if (ra < 0.0)
			ra += 360.0;
		double decl = Math.asin(getZ() / getRadius()) / Astro.RAD;

		return new Coor(ra, decl);
	}

	/**
	 * Changes the precession. The result is stored in this object.
	 * @param src_jd the Julian Day of the source precession.
	 * @param dst_jd the Julian Day of the destination precession.
	 */
	public void precession ( JulianDay src_jd, JulianDay dst_jd ) {
		double zeta,z,theta,sin_zt,cos_zt,sin_z,cos_z,sin_t,cos_t;
		double xx,xy,xz,yx,yy,yz,zx,zy,zz,x2,y2,z2;
		double ti,tf;

		ti = Astro.get_t2000(src_jd.getJD());
		tf = Astro.get_t2000(dst_jd.getJD());
		if (src_jd.getJD() > dst_jd.getJD()) {
			double t_tmp = ti;
			ti = tf;
			tf = t_tmp;
		}
		tf -= ti;

		zeta = ((2306.2181+1.39656*ti-0.000139*ti*ti)*tf+ (0.30188-0.000344*ti)*tf*tf+0.017998*tf*tf*tf)*Astro.RAD/3600.0;
		z = zeta+((0.79280+0.000410*ti)*tf*tf+0.000205*tf*tf*tf)*Astro.RAD/3600.0;
		theta = ((2004.3109-0.85330*ti-0.000217*ti*ti)*tf- (0.42665+0.000217*ti)*tf*tf-0.041833*tf*tf*tf)*Astro.RAD/3600.0;
		if (src_jd.getJD() > dst_jd.getJD()){
			zeta = - zeta;
			z = - z;
			theta = - theta;
		}

		sin_zt = Math.sin(zeta);
		cos_zt = Math.cos(zeta);
		sin_z = Math.sin(z);
		cos_z = Math.cos(z);
		sin_t = Math.sin(theta);
		cos_t = Math.cos(theta);

		xx = cos_zt * cos_t * cos_z - sin_zt * sin_z;
		yx = - sin_zt * cos_t * cos_z - cos_zt * sin_z;
		zx = - sin_t * cos_z;
		xy = cos_zt * cos_t * sin_z + sin_zt * cos_z;
		yy = - sin_zt * cos_t * sin_z + cos_zt * cos_z;
		zy = - sin_t * sin_z;
		xz = cos_zt * sin_t;
		yz = - sin_zt * sin_t;
		zz = cos_t;

		x2 = xx * getX() + yx * getY() + zx * getZ();
		y2 = xy * getX() + yy * getY() + zy * getZ();
		z2 = xz * getX() + yz * getY() + zz * getZ();

		setX(x2);
		setY(y2);
		setZ(z2);
	}

	/**
	 * Returns a raw string representation of the state of this object,
	 * for debugging use. It should be invoked from <code>toString</code>
	 * method of the subclasses.
	 * @return a string representation of the state of this object.
	 */
	protected String paramString ( ) {
		return "x=" + x + ",y=" + y + ",z=" + z;
	}

	/**
	 * Returns a string representation of the state of this object,
	 * for debugging use.
	 * @return a string representation of the state of this object.
	 */
	public String toString ( ) {
		return getClass().getName() + "[" + paramString() + "]";
	}
}
