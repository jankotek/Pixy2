/*
 * @(#)InverseWhiteAndBlackFilter.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.image.filter;
import net.aerith.misao.util.*;
import net.aerith.misao.image.*;

/**
 * The <code>InverseWhiteAndBlackFilter</code> is an image processing 
 * filter to inverse white and black. The result is stored in the 
 * original image.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 13
 */

public class InverseWhiteAndBlackFilter extends Filter {
	/**
	 * Constructs a filter.
	 */
	public InverseWhiteAndBlackFilter ( ) {
	}

	/**
	 * Operates the image processing filter and stores the result into
	 * the original image buffer.
	 * @param image the original image to process.
	 * @return the original image buffer.
	 */
	public MonoImage operate ( MonoImage image ) {
		image.inverse();
		return image;
	}
}
