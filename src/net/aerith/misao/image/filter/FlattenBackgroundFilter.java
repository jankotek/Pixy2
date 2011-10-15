/*
 * @(#)FlattenBackgroundFilter.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.image.filter;
import net.aerith.misao.util.*;
import net.aerith.misao.image.*;

/**
 * The <code>InverseWhiteAndBlackFilter</code> is an image processing 
 * filter to flatten the background. The result is stored in the new 
 * image.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 13
 */

public class FlattenBackgroundFilter extends Filter {
	/**
	 * Constructs a filter.
	 */
	public FlattenBackgroundFilter ( ) {
	}

	/**
	 * Operates the image processing filter and stores the result into
	 * the new image buffer.
	 * @param image the original image to process.
	 * @return the new image buffer.
	 */
	public MonoImage operate ( MonoImage image ) {
		MonoImage sky_image = new BackgroundEstimationFilter().operate(image);
		sky_image.subtract(image);
		sky_image.inverse();
		return sky_image;
	}
}
