/*
 * @(#)RescaleFilter.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.image.filter;
import net.aerith.misao.util.*;
import net.aerith.misao.image.*;

/**
 * The <code>RescaleFilter</code> is an image processing filter to 
 * rescale the image into the specified size and creates a new image
 * buffer. If the size is not specified, the new image is the same as
 * the original size. The result is stored in the new image.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class RescaleFilter extends Filter {
	/**
	 * The new size to rescale.
	 */
	protected Size size;

	/**
	 * Constructs a filter.
	 */
	public RescaleFilter ( ) {
		size = null;
	}

	/**
	 * Constructs a filter with new size.
	 * @param new_size the new size to rescale.
	 */
	public RescaleFilter ( Size new_size ) {
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
	 * Operates the image processing filter and creates the new image
	 * buffer.
	 * @param image the original image to process.
	 * @return the new image buffer.
	 */
	public MonoImage operate ( MonoImage image ) {
		Size new_size = size;
		if (size == null)
			new_size = image.getSize();

		FloatBuffer work_buffer = new FloatBuffer(new Size(new_size.getWidth(), image.getSize().getHeight()));
		FloatBuffer new_buffer = new FloatBuffer(new_size);

		double rx = (double)new_size.getWidth() / (double)image.getSize().getWidth();
		double ry = (double)new_size.getHeight() / (double)image.getSize().getHeight();

		for (int y = 0 ; y < image.getSize().getHeight() ; y++) {
			for (int x = 0 ; x < new_size.getWidth() ; x++)
				work_buffer.set(x, y, 0);

			int x = 0;
			double phase = 0;
			for (int old_x = 0 ; old_x < image.getSize().getWidth() ; old_x++) {
				if (phase + rx <= 1.00001) {
					work_buffer.set(x, y, work_buffer.get(x, y) + (float)(image.getValue(old_x, y) * rx));
					phase += rx;
					if (phase >= 1.0)
						phase = 1.0;
				} else {
					work_buffer.set(x, y, work_buffer.get(x, y) + (float)(image.getValue(old_x, y) * (1 - phase)));
					double r = rx - (1 - phase);
					x++;
					if (x >= new_size.getWidth())
						x--;
					phase = 0;
					while (r >= 0.99999  &&  x < new_size.getWidth()) {
						work_buffer.set(x, y, (float)image.getValue(old_x, y));
						x++;
						r -= 1;
						if (r < 0)
							r = 0;
					}
					if (r > 0.00001  &&  x < new_size.getWidth()) {
						work_buffer.set(x, y, (float)(image.getValue(old_x, y) * r));
						phase = r;
					}
				}
			}
		}

		for (int x = 0 ; x < new_size.getWidth() ; x++) {
			for (int y = 0 ; y < new_size.getHeight() ; y++)
				new_buffer.setValue(x, y, 0);

			int y = 0;
			double phase = 0;
			for (int old_y = 0 ; old_y < image.getSize().getHeight() ; old_y++) {
				if (phase + ry <= 1.00001) {
					new_buffer.set(x, y, new_buffer.get(x, y) + work_buffer.get(x, old_y) * (float)ry);
					phase += ry;
					if (phase >= 1.0)
						phase = 1.0;
				} else {
					new_buffer.set(x, y, new_buffer.get(x, y) + work_buffer.get(x, old_y) * (float)(1 - phase));
					double r = ry - (1 - phase);
					y++;
					if (y >= new_size.getHeight())
						y--;
					phase = 0;
					while (r >= 0.99999  &&  y < new_size.getHeight()) {
						new_buffer.set(x, y, work_buffer.get(x, old_y));
						y++;
						r -= 1;
						if (r < 0)
							r = 0;
					}
					if (r > 0.00001  &&  y < new_size.getHeight()) {
						new_buffer.set(x, y, work_buffer.get(x, old_y) * (float)r);
						phase = r;
					}
				}
			}
		}

		MonoImage new_image = new MonoImage(new_buffer);

		return new_image;
	}
}
