/*
 * @(#)FlatteningFilter.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.image.filter;
import net.aerith.misao.util.*;
import net.aerith.misao.image.*;

/**
 * The <code>FlatteningFilter</code> is an image processing filter to
 * flattern the image dividing by the flat image. The result is stored
 * in the original image.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class FlatteningFilter extends Filter {
	/**
	 * The flat image.
	 */
	protected MonoImage flat_image;

	/**
	 * Constructs a filter.
	 * @param flat_image the flat image to subtract.
	 */
	public FlatteningFilter ( MonoImage flat_image ) {
		this.flat_image = flat_image;
	}

	/**
	 * Operates the image processing filter and stores the result into
	 * the original image buffer.
	 * @param image the original image to process.
	 * @return the original image buffer.
	 */
	public MonoImage operate ( MonoImage image ) {
		for (int y = 0 ; y < image.getSize().getHeight() ; y++) {
			for (int x = 0 ; x < image.getSize().getWidth() ; x++) {
				try {
					image.setValue(x, y, image.getValue(x, y) / flat_image.getValue(x, y));
				} catch ( IndexOutOfBoundsException exception ) {
				}
			}
		}

		return image;
	}
}
