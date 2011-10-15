/*
 * @(#)SubtractionFilter.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.image.filter;
import net.aerith.misao.util.*;
import net.aerith.misao.image.*;

/**
 * The <code>SubtractionFilter</code> is an image processing filter to
 * subtract an image from the base image. The result is stored in the
 * new image.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class SubtractionFilter extends Filter {
	/**
	 * The base image.
	 */
	protected MonoImage base_image;

	/**
	 * The map function to convert (x,y) on the specified image to 
	 * (x,y) on the base image.
	 */
	protected MapFunction map_function = null;

	/**
	 * Constructs a filter.
	 * @param base_image the base image.
	 */
	public SubtractionFilter ( MonoImage base_image ) {
		this.base_image = base_image;
	}

	/**
	 * Sets the map function.
	 * @param map_function the map function to convert (x,y) on the 
	 * specified image to (x,y) on the base image.
	 */
	public void setMapFunction ( MapFunction map_function ) {
		this.map_function = map_function;
	}

	/**
	 * Operates the image processing filter and creates the new image
	 * buffer.
	 * @param image the original image to process.
	 * @return the new image buffer.
	 */
	public MonoImage operate ( MonoImage image ) {
		// Maps the specified image.
		MonoImage mapped_image = image;
		if (map_function != null) {
			MapFilter filter = new MapFilter(map_function);
			filter.setBuffer(base_image.cloneImage());
			mapped_image = filter.operate(image);
		}

		double base_width = (double)base_image.getSize().getWidth();
		double base_height = (double)base_image.getSize().getHeight();
		Position base_shift = new Position(- base_width / 2.0, - base_height / 2.0);
		double orig_width = (double)image.getSize().getWidth();
		double orig_height = (double)image.getSize().getHeight();
		Position orig_shift = new Position(orig_width / 2.0, orig_height / 2.0);

		MapFunction inv_mf = null;
		if (map_function != null)
			inv_mf = map_function.inverse();

		// Calculates the pixel value conversion formula.
		RegressionEquation re = new RegressionEquation();
		for (int y = 0 ; y < base_image.getSize().getHeight() ; y++) {
			for (int x = 0 ; x < base_image.getSize().getWidth() ; x++) {
				try {
					if (inv_mf != null) {
						Position position = new Position(x, y);
						position.add(base_shift);
						position = inv_mf.map(position);
						position.add(orig_shift);
						double value = image.getValue((int)position.getX(), (int)position.getY());
					}

					double value1 = base_image.getValueOnFlatExtension(x, y);
					double value2 = mapped_image.getValueOnFlatExtension(x, y);

					re.setData(value2, value1);
				} catch ( IndexOutOfBoundsException exception ) {
				}
			}
		}
		re.solve();

		// Subtracts the specified image.
		MonoImage new_image = new MonoImage(new FloatBuffer(base_image.getSize()));
		for (int y = 0 ; y < base_image.getSize().getHeight() ; y++) {
			for (int x = 0 ; x < base_image.getSize().getWidth() ; x++) {
				double value1 = base_image.getValueOnFlatExtension(x, y);
				double value2 = mapped_image.getValueOnFlatExtension(x, y);

				new_image.setValue(x, y, value1 - re.getValue(value2));
			}
		}

		return new_image;
	}
}
