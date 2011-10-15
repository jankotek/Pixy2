/*
 * @(#)BackgroundEstimationFilter.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.image.filter;
import net.aerith.misao.util.*;
import net.aerith.misao.image.*;

/**
 * The <code>BackgroundEstimationFilter</code> is an image processing
 * filter to create the background image. The result is stored in the
 * new image.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class BackgroundEstimationFilter extends Filter {
	/**
	 * Constructs a filter.
	 */
	public BackgroundEstimationFilter ( ) {
	}

	/**
	 * Operates the image processing filter and creates the new image
	 * buffer.
	 * @param image the original image to process.
	 * @return the new image buffer.
	 */
	public MonoImage operate ( MonoImage image ) {
		// Creates sky field as a result of median filter.
		MonoImage sky_image = new RescaleFilter(new Size(image.getSize().getWidth() / 8, image.getSize().getHeight() / 8)).operate(image);
		new MedianFilter(5).operate(sky_image);
		sky_image = new RescaleFilter(image.getSize()).operate(sky_image);
		new SmoothFilter(16).operate(sky_image);

		// Because of the following two reasons, many noises can be easily
		// detected at the edge of images.
		//   1. The image size reduction, median filter and smoothing filter
		//      algorithms tend to estimate the sky image around the edge
		//      much closer to the value at the inner pixles.
		//   2. Some CCD images tend to be brighter within a several pixels 
		//      from the edge.
		// In order to avoid the noises, the sky images around the edge
		// are estimated by 8x8 median filter again.
		MedianFilter filter = new MedianFilter(8);
		for (int y = 0 ; y < image.getSize().getHeight() ; y++) {
			for (int x = 0 ; x < image.getSize().getWidth() ; x++) {
				if (y < 8  ||  y >= image.getSize().getHeight() - 8  ||  
					x < 8  ||  x >= image.getSize().getWidth() - 8) {
					int pixels_from_edge = 99;
					if (x < 8) {
						if (pixels_from_edge > x)
							pixels_from_edge = x;
					}
					if (x >= image.getSize().getWidth() - 8) {
						if (pixels_from_edge > image.getSize().getWidth() - x - 1)
							pixels_from_edge = image.getSize().getWidth() - x - 1;
					}
					if (y < 8) {
						if (pixels_from_edge > y)
							pixels_from_edge = y;
					}
					if (y >= image.getSize().getHeight() - 8) {
						if (pixels_from_edge > image.getSize().getHeight() - y - 1)
							pixels_from_edge = image.getSize().getHeight() - y - 1;
					}
					double value1 = filter.getFilteredValue(image, x, y);
					double value2 = sky_image.getValue(x, y);
					sky_image.setValue(x, y, value1 * (8.0 - (double)pixels_from_edge) / 8.0 + value2 * (double)pixels_from_edge / 8.0);
				}
			}
		}

		return sky_image;
	}
}
