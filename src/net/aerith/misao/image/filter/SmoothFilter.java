/*
 * @(#)SmoothFilter.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.image.filter;
import net.aerith.misao.util.*;
import net.aerith.misao.image.*;

/**
 * The <code>SmoothFilter</code> is an image processing filter of 
 * smoothing. The result is stored in the original image.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class SmoothFilter extends Filter {
	/**
	 * The area size to calculate median value.
	 */
	protected int filter_size;

	/**
	 * Constructs a filter.
	 */
	public SmoothFilter ( ) {
		filter_size = 1;
	}

	/**
	 * Constructs a filter with initial filter size.
	 * @param initial_filter_size the initial value of filter size.
	 */
	public SmoothFilter ( int initial_filter_size ) {
		filter_size = initial_filter_size;
		if (filter_size <= 0)
			filter_size = 1;
	}

	/**
	 * Sets the filter size.
	 * @param new_filter_size the new value of filter size.
	 */
	public void setFilterSize ( int new_filter_size ) {
		filter_size = new_filter_size;
		if (filter_size <= 0)
			filter_size = 1;
	}

	/**
	 * Operates the image processing filter and stores the result into
	 * the original image buffer.
	 * @param image the original image to process.
	 * @return the original image buffer.
	 */
	public MonoImage operate ( MonoImage image ) {
		double[] buffer = new double[image.getSize().getWidth()];
		for (int y = 0 ; y < image.getSize().getHeight() ; y++) {
			double val = 0.0;
			for (int x = - (filter_size - 1) / 2 - 1 ; x < filter_size / 2 ; x++)
				val += image.getValueOnFlatExtension(x, y);

			for (int x = 0 ; x < image.getSize().getWidth() ; x++) {
				val -= image.getValueOnFlatExtension(x - (filter_size - 1) / 2 - 1, y);
				val += image.getValueOnFlatExtension(x + filter_size / 2, y);

				buffer[x] = val / (double)filter_size;
			}

			for (int x = 0 ; x < image.getSize().getWidth() ; x++)
				image.setValue(x, y, buffer[x]);
		}

		buffer = new double[image.getSize().getHeight()];
		for (int x = 0 ; x < image.getSize().getWidth() ; x++) {
			double val = 0.0;
			for (int y = - (filter_size - 1) / 2 - 1 ; y < filter_size / 2 ; y++)
				val += image.getValueOnFlatExtension(x, y);

			for (int y = 0 ; y < image.getSize().getHeight() ; y++) {
				val -= image.getValueOnFlatExtension(x, y - (filter_size - 1) / 2 - 1);
				val += image.getValueOnFlatExtension(x, y + filter_size / 2);

				buffer[y] = val / (double)filter_size;
			}

			for (int y = 0 ; y < image.getSize().getHeight() ; y++)
				image.setValue(x, y, buffer[y]);
		}

		return image;
	}
}
