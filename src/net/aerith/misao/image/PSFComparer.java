/*
 * @(#)PSFComparer.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.image;
import java.util.Vector;
import net.aerith.misao.util.*;

/**
 * The <code>PSFComparer</code> is a class to compare two point spread
 * functions.
 * <p>
 * The method <tt>calculateProportionTo</tt> calculates the proportion
 * <i>k</i> which minimizes the amount of square of residuals:
 * <pre>
 *     \sigma_{x} { f(x,y) - k * p(x,y) } ^ 2
 * </pre>
 * where <i>f(x,y)</i> means the specified PSF, and <i>p(x,y)</i>
 * means the base PSF.
 * <p>
 * The <i>k</i> is calculated as:
 * <pre>
 *          \sigma_{x} { f(x,y) * p(x,y) }
 *     k = --------------------------------
 *              \sigma_{x} p(x,y) ^ 2
 * </pre>
 * in the least square method.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 February 26
 */

public class PSFComparer {
	/**
	 * The base PSF.
	 */
	protected PSF base_psf = null;

	/**
	 * The ratio of pixels to be rejected to calculate the proportion.
	 */
	protected double rejected_ratio = 0.0;

	/**
	 * The radius from the center not to reject. When negative, every
	 * pixel can be rejected.
	 */
	protected double essential_radius = 1.0;

	/**
	 * The list of sight scopes to be rejected.
	 */
	protected Vector rejected_scopes = new Vector();

	/**
	 * Constructs a <code>PSFComparer</code>.
	 * @param base_psf the base PSF.
	 */
	public PSFComparer ( PSF base_psf ) {
		this.base_psf = base_psf;
	}

	/**
	 * Sets the ratio of pixels to be rejected to calculate the 
	 * proportion.
	 * @param ratio the ratio of pixels to be rejected to calculate 
	 * the proportion.
	 */
	public void setRejectedRatio ( double ratio ) {
		rejected_ratio = ratio;
	}

	/**
	 * Sets the radius from the center not to reject. When negative, 
	 * every pixel can be rejected.
	 * @param radius the radius from the center not to reject.
	 */
	public void setEssentialRadius ( double radius ) {
		essential_radius = radius;
	}

	/**
	 * Adds a sight scope to reject pixels within. Note that the sight 
	 * scope must be expressed as 0 degree is in the direction of x 
	 * axis, 90 degree is in the direction of y axis.
	 * @param scope a sight scope to reject pixels within.
	 */
	public void addRejectedSightScope ( SightScope scope ) {
		rejected_scopes.addElement(scope);
	}

	/**
	 * Calculates the proportion to the specified PSF from the base
	 * PSF.
	 * @param psf the PSF.
	 * @return the proportion to the specified PSF from the base PSF.
	 */
	public double calculateProportionTo ( PSF psf ) {
		MonoImage p_image = base_psf.getMonoImage();
		MonoImage f_image = psf.getMonoImage();

		// Note that the width must be odd.
		int p_width = p_image.getSize().getWidth();
		int f_width = f_image.getSize().getWidth();
		int p_center = p_width / 2;
		int f_center = f_width / 2;

		int width = (p_width < f_width ? p_width : f_width);

		// Rejects pixels in the range of specified sight scopes.
		double[] p_value = new double[width * width];
		double[] f_value = new double[width * width];
		int[] radius2 = new int[width * width];
		Position base_position = new Position(0, 0);
		int i = 0;
		for (int dy = - width / 2 ; dy <= width / 2 ; dy++) {
			int p_y = p_center + dy;
			int f_y = f_center + dy;
			for (int dx = - width / 2 ; dx <= width / 2 ; dx++) {
				int p_x = p_center + dx;
				int f_x = f_center + dx;

				Position position = new Position(dx, dy);
				double angle = base_position.getPositionAngleTo(position);

				// Rejected when within a range of a rejected sight scope.
				boolean rejected = false;
				for (int j = 0 ; j < rejected_scopes.size() ; j++) {
					SightScope scope = (SightScope)rejected_scopes.elementAt(j);
					if (scope.inRange(angle)) {
						rejected = true;
						break;
					}
				}

				// Square of radius from the center.
				int r2 = dx * dx + dy * dy;

				// Used when within the specified radius from the center.
				if (essential_radius >= 0.0) {
					if ((double)r2 <= essential_radius * essential_radius)
						rejected = false;
				}

				if (rejected == false) {
					p_value[i] = p_image.getValue(p_x, p_y);
					f_value[i] = f_image.getValue(f_x, f_y);

					radius2[i] = r2;

					i++;
				}
			}
		}
		int size = i;

		// Calculates the amount of pixel value.
		double p_amount = 0.0;
		double f_amount = 0.0;
		for (i = 0 ; i < size ; i++) {
			p_amount += p_value[i];
			f_amount += f_value[i];
		}

		// Preliminary proportion baesd on the amount of pixel value.
		double pre_k = f_amount / p_amount;

		// Creates the array of residuals.
		Array residual2_array = new Array(size);
		for (i = 0 ; i < size ; i++) {
			double residual = f_value[i] - pre_k * p_value[i];
			residual2_array.set(i, residual * residual);
		}

		// Sorts in order to determine the pixels to reject.
		ArrayIndex index = residual2_array.sortAscendant();

		// Number of pixels to reject and use.
		int rejected_pixels = (int)(rejected_ratio * (double)size);
		int used_pixels = size - rejected_pixels;

		// Calculates the proportion.
		double amount_fp = 0.0;
		double amount_p2 = 0.0;
		for (i = 0 ; i < size ; i++) {
			boolean flag = false;

			if (i < used_pixels) {
				flag = true;
			} else {
				if (essential_radius >= 0.0) {
					if ((double)radius2[index.get(i)] <= essential_radius * essential_radius)
						flag = true;
				}
			}

			amount_fp += f_value[index.get(i)] * p_value[index.get(i)];
			amount_p2 += p_value[index.get(i)] * p_value[index.get(i)];
		}
		double k = amount_fp / amount_p2;

		return k;
	}
}
