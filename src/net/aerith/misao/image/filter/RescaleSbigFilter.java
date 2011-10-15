/*
 * @(#)RescaleSbigFilter.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.image.filter;
import net.aerith.misao.util.*;
import net.aerith.misao.image.*;

/**
 * The <code>RescaleSbigFilter</code> is an image processing filter to 
 * rescale the SBIG ST-4/6 image. The result is stored in the new 
 * image.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 13
 */

public class RescaleSbigFilter extends Filter {
	/**
	 * Constructs a filter.
	 */
	public RescaleSbigFilter ( ) {
	}

	/**
	 * Operates the image processing filter and stores the result into
	 * the new image buffer.
	 * @param image the original image to process.
	 * @return the new image buffer.
	 */
	public MonoImage operate ( MonoImage image ) {
		int height = (int)((double)image.getSize().getHeight() * Astro.SBIG_RATIO + 0.5);
		MonoImage new_image = new RescaleFilter(new Size(image.getSize().getWidth(), height)).operate(image);
		return new_image;
	}
}
