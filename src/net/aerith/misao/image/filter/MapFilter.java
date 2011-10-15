/*
 * @(#)MapFilter.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.image.filter;
import net.aerith.misao.util.*;
import net.aerith.misao.image.*;

/**
 * The <code>MapFilter</code> is an image processing filter to map the
 * image based on the specified map function. If the buffer of the new
 * image is set, the result will be stored in that buffer. Otherwise, 
 * it creates a new buffer with the same size as the original.
 * <p>
 * The map function must convert (x,y) on the original image to (x,y)
 * on the new image. The (0,0) represents the center of the image.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class MapFilter extends Filter {
	/**
	 * The map function which converts (x,y) on the original image 
	 * to (x,y) on the new image.
	 */
	protected MapFunction map_function = null;

	/**
	 * The new buffer.
	 */
	protected MonoImage new_image = null;

	/**
	 * Constructs a filter.
	 */
	public MapFilter ( ) {
		map_function = new MapFunction();
		new_image = null;
	}

	/**
	 * Constructs a filter with a map function.
	 * @param initial_mf the initial map function.
	 */
	public MapFilter ( MapFunction initial_mf ) {
		map_function = initial_mf;
	}

	/**
	 * Sets the new map function.
	 * @param new_mf the new map function.
	 */
	public void setMapFunction ( MapFunction new_mf ) {
		map_function = new_mf;
	}

	/**
	 * Sets the buffer of the new image.
	 * @param new_buffer the buffer of the new image.
	 */
	public void setBuffer ( MonoImage new_buffer ) {
		new_image = new_buffer;
	}

	/**
	 * Operates the image processing filter. If the buffer of the new
	 * image is set, the result will be stored in that buffer. 
	 * Otherwise, it creates a new buffer with the same size as the 
	 * original.
	 * @param image the original image to process.
	 * @return the new image buffer.
	 */
	public MonoImage operate ( MonoImage image ) {
		if (new_image == null)
			new_image = image.cloneImage();

		double orig_width = (double)image.getSize().getWidth();
		double orig_height = (double)image.getSize().getHeight();
		double new_width = (double)new_image.getSize().getWidth();
		double new_height = (double)new_image.getSize().getHeight();

		MapFunction inv_mf = map_function.inverse();

		for (int h = 0 ; h < new_image.getSize().getHeight() ; h++) {
			for (int w = 0 ; w < new_image.getSize().getWidth() ; w++) {
				double value = 0.0;
				int count = 0;

				for (int dh = -1 ; dh <= 1 ; dh += 2 ) {
					for (int dw = -1 ; dw <= 1 ; dw += 2 ) {
						Position position = new Position((double)w + (double)dw / 3.0 + 0.5 - new_width / 2.0, (double)h + (double)dh / 3.0 + 0.5 - new_height / 2.0);
						position = inv_mf.map(position);

						try {
							value += image.getValue((int)(position.getX() + orig_width / 2.0), (int)(position.getY() + orig_height / 2.0));
							count++;
						} catch ( IndexOutOfBoundsException exception ) {
						}
					}
				}

				if (count > 0)
					value /= (double)count;

				new_image.setValue(w, h, value);
			}
		}

		return new_image;
	}
}
