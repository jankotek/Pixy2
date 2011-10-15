/*
 * @(#)MeridianImageTransformFilter.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.image.filter;
import net.aerith.misao.util.*;
import net.aerith.misao.image.*;

/**
 * The <code>MeridianImageTransformFilter</code> is an image 
 * processing filter to transform the meridian image. The result is 
 * stored in the new image.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 September 17
 */

public class MeridianImageTransformFilter extends Filter {
	/**
	 * The declination of the center.
	 */
	protected double decl;

	/**
	 * The pixel size in arcsec.
	 */
	protected double pixel_size;

	/**
	 * The R.A. interval in second.
	 */
	protected double ra_interval;

	/**
	 * Constructs a filter.
	 * @param decl        the declination of the center.
	 * @param pixel_size  the pixel size in arcsec.
	 * @param ra_interval the R.A. interval in second.
	 */
	public MeridianImageTransformFilter ( double decl, double pixel_size, double ra_interval ) {
		this.decl = decl;
		this.pixel_size = pixel_size;
		this.ra_interval = ra_interval;
	}

	/**
	 * Operates the image processing filter and stores the result into
	 * the new image buffer.
	 * @param image the original image to process.
	 * @return the new image buffer.
	 */
	public MonoImage operate ( MonoImage image ) {
		// The covering area of the image.

		double ra_width = (ra_interval * 15.0 / 3600.0) * (double)image.getSize().getWidth();
		double decl_north = decl + pixel_size / 3600.0 * (double)image.getSize().getHeight() / 2.0;
		if (decl_north >= 90.0)
			decl_north = 90.0;
		double decl_south = decl - pixel_size / 3600.0 * (double)image.getSize().getHeight() / 2.0;
		if (decl_south <= -90.0)
			decl_south = -90.0;

		// The size of new image after transformation.

		double new_half_width = 0.0;
		double new_height_up = 0;
		double new_height_down = 0;

		ChartMapFunction cmf = new ChartMapFunction(new Coor(0.0, decl), 1.0 / (pixel_size / 3600.0), 0.0);

		Position position = cmf.mapCoordinatesToXY(new Coor(- ra_width / 2.0, decl_north));
		if (new_half_width < position.getX())
			new_half_width = position.getX();
		if (new_height_up < - position.getY())
			new_height_up = - position.getY();
		position = cmf.mapCoordinatesToXY(new Coor(0.0, decl_north));
		if (new_height_up < - position.getY())
			new_height_up = - position.getY();
		position = cmf.mapCoordinatesToXY(new Coor(- ra_width / 2.0, decl_south));
		if (new_half_width < position.getX())
			new_half_width = position.getX();
		if (new_height_down < position.getY())
			new_height_down = position.getY();
		position = cmf.mapCoordinatesToXY(new Coor(0.0, decl_south));
		if (new_height_down < position.getY())
			new_height_down = position.getY();
		if (decl_north * decl_south < 0.0) {
			position = cmf.mapCoordinatesToXY(new Coor(- ra_width / 2.0, 0.0));
			if (new_half_width < position.getX())
				new_half_width = position.getX();
		}

		Size new_size = new Size((int)(new_half_width * 2.0) + 1, (int)(new_height_up + new_height_down) + 1);

		// The declination of the center on the new image 
		// after transformation..

		double new_decl = decl - pixel_size / 3600.0 * (new_height_down - new_height_up) / 2.0;

		cmf = new ChartMapFunction(new Coor(0.0, new_decl), 1.0 / (pixel_size / 3600.0), 0.0);

		// Rescales the original image so that the R.A. interval
		// becomes equal to the pixel size.

		double ratio = (ra_interval * 15.0 * Math.cos(decl * Astro.RAD)) / pixel_size;
		int tmp_width = (int)((double)image.getSize().getWidth() * ratio);
		MonoImage tmp_image = new RescaleFilter(new Size(tmp_width, image.getSize().getHeight())).operate(image);

		// Transforms the image.

		FloatBuffer new_buffer = new FloatBuffer(new_size);

		for (int y = 0 ; y < new_size.getHeight() ; y++) {
			for (int x = 0 ; x < new_size.getWidth() ; x++) {
				try {
					// Converts the (x,y) to R.A. and Decl.
					position = new Position((double)x - (double)new_size.getWidth() / 2.0, (double)y - (double)new_size.getHeight() / 2.0);
					Coor coor = cmf.mapXYToCoordinates(position);

					// Converts the R.A. and Decl. to (x,y) on the original image.
					double orig_fx = 0.0;
					if (coor.getRA() < 180.0)
						orig_fx = - coor.getRA() / (ra_interval * 15.0 / 3600.0) * ratio;
					else
						orig_fx = (360.0 - coor.getRA()) / (ra_interval * 15.0 / 3600.0) * ratio;
					double orig_fy = (decl - coor.getDecl()) / (pixel_size / 3600.0);
					int orig_x = (int)(orig_fx + (double)tmp_image.getSize().getWidth() / 2.0);
					int orig_y = (int)(orig_fy + (double)tmp_image.getSize().getHeight() / 2.0);

					// Calculates the pixel value.
					double value = tmp_image.getValue(orig_x, orig_y);
					new_buffer.setValue(x, y, value);
				} catch ( IndexOutOfBoundsException exception ) {
				}
			}
		}

		MonoImage new_image = new MonoImage(new_buffer);

		return new_image;
	}
}
