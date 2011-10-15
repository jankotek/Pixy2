/*
 * @(#)MedianFilter.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.image.filter;
import net.aerith.misao.util.*;
import net.aerith.misao.image.*;

/**
 * The <code>MedianFilter</code> is an image processing filter of 
 * median filter. The result is stored in the original image.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class MedianFilter extends Filter {
	/**
	 * The area size to calculate median value.
	 */
	protected int filter_size;

	/**
	 * Constructs a filter.
	 */
	public MedianFilter ( ) {
		filter_size = 1;
	}

	/**
	 * Constructs a filter with initial filter size.
	 * @param initial_filter_size the initial value of filter size.
	 */
	public MedianFilter ( int initial_filter_size ) {
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
	 * Gets the filtered value at the specified position.
	 * @param image the original image to process.
	 * @param x     the x.
	 * @param y     the y.
	 * @return the filtered value.
	 */
	public double getFilteredValue ( MonoImage image, int x, int y ) {
		int area_size = filter_size / 2;
		area_size = (area_size * 2 + 1) * (area_size * 2 + 1);

		Array array = new Array(area_size);

		int count = 0;
		for (int dy = - filter_size / 2 ; dy <= filter_size / 2 ; dy++) {
			for (int dx = - filter_size / 2 ; dx <= filter_size / 2 ; dx++) {
				array.set(count++, image.getValueOnFlatExtension(x + dx, y + dy));
			}
		}

		array.sortAscendant();

		return array.getValueAt(area_size / 2);
	}

	/**
	 * Operates the image processing filter and stores the result into
	 * the original image buffer.
	 * @param image the original image to process.
	 * @return the original image buffer.
	 */
	public MonoImage operate ( MonoImage image ) {
		MonoImage original_image = (MonoImage)image.cloneImage();

		for (int y = 0 ; y < image.getSize().getHeight() ; y++) {
			for (int x = 0 ; x < image.getSize().getWidth() ; x++) {
				image.setValue(x, y, getFilteredValue(original_image, x, y));
			}
		}

		return image;
	}
}
