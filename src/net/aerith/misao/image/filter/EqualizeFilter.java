/*
 * @(#)EqualizeFilter.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.image.filter;
import net.aerith.misao.util.*;
import net.aerith.misao.image.*;

/**
 * The <code>EqualizeFilter</code> is an image processing filter to
 * equalize the image. The result is stored in the original image.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class EqualizeFilter extends Filter {
	/**
	 * The area size to calculate median value.
	 */
	protected int filter_size;

	/**
	 * Constructs a filter.
	 */
	public EqualizeFilter ( ) {
		filter_size = 1;
	}

	/**
	 * Constructs a filter with initial filter size.
	 * @param initial_filter_size the initial value of filter size.
	 */
	public EqualizeFilter ( int initial_filter_size ) {
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
		Array array = new Array(image.getSize().getWidth());
		for (int y = 0 ; y < image.getSize().getHeight() ; y++) {
			for (int x = 0 ; x < image.getSize().getWidth() ; x++) {
				array.set(x, image.getValue(x, y));
			}
			array.sortAscendant();
			double median = array.getValueAt(image.getSize().getWidth() / 2);
			for (int x = 0 ; x < image.getSize().getWidth() ; x++) {
				image.setValue(x, y, image.getValue(x, y) - median);
			}
		}

		array = new Array(image.getSize().getHeight());
		for (int x = 0 ; x < image.getSize().getWidth() ; x++) {
			for (int y = 0 ; y < image.getSize().getHeight() ; y++) {
				array.set(y, image.getValue(x, y));
			}
			array.sortAscendant();
			double median = array.getValueAt(image.getSize().getHeight() / 2);
			for (int y = 0 ; y < image.getSize().getHeight() ; y++) {
				image.setValue(x, y, image.getValue(x, y) - median);
			}
		}

		return image;
	}
}
