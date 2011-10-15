/*
 * @(#)ResizeFilter.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.image.filter;
import net.aerith.misao.util.*;
import net.aerith.misao.image.*;

/**
 * The <code>ResizeFilter</code> is an image processing filter to
 * resize the image to the specified size. It copies the part of 
 * the buffer based on the specified base position and the new size,
 * creates the new image buffer, and pastes on the new buffer. If the
 * size is not specified, the new image is the same as the original 
 * size. The result is stored in the new image.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class ResizeFilter extends Filter {
	/**
	 * The new size to rescale.
	 */
	protected Size size;

	/**
	 * The base x position.
	 */
	protected int base_x = 0;

	/**
	 * The base y position.
	 */
	protected int base_y = 0;

	/**
	 * Constructs a filter.
	 */
	public ResizeFilter ( ) {
		size = null;
	}

	/**
	 * Constructs a filter with new size.
	 * @param new_size the new size to rescale.
	 */
	public ResizeFilter ( Size new_size ) {
		size = new_size;
	}

	/**
	 * Sets the new size to rescale.
	 * @param new_size the new size to rescale.
	 */
	public void setSize ( Size new_size ) {
		size = new_size;
	}

	/**
	 * Sets the base position.
	 * @param x the base x position.
	 * @param y the base y position.
	 */
	public void setBasePosition ( int x, int y ) {
		base_x = x;
		base_y = y;
	}

	/**
	 * Operates the image processing filter and creates the new image
	 * buffer.
	 * @param image the original image to process.
	 * @return the new image buffer.
	 */
	public MonoImage operate ( MonoImage image ) {
		Size new_size = size;
		if (size == null)
			new_size = image.getSize();

		MonoImage new_image = image.cloneImage(new_size);

		for (int y = 0 ; y < new_size.getHeight() ; y++) {
			for (int x = 0 ; x < new_size.getWidth() ; x++) {
				double value = 0.0;

				try {
					value = image.getValue(x + base_x, y + base_y);
				} catch ( IndexOutOfBoundsException exception ) {
				}

				new_image.setValue(x, y, value);
			}
		}

		return new_image;
	}
}
