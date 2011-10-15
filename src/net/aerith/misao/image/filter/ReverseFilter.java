/*
 * @(#)ReverseFilter.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.image.filter;
import net.aerith.misao.util.*;
import net.aerith.misao.image.*;

/**
 * The <code>ReverseFilter</code> is an image processing filter to
 * reverse the image vertically or horizontally. The result is stored 
 * in the original image.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 13
 */

public class ReverseFilter extends Filter {
	/**
	 * The direction.
	 */
	protected int direction = VERTICALLY;

	/**
	 * The number which indicates to reverse vertically.
	 */
	public final static int VERTICALLY = 1;

	/**
	 * The number which indicates to reverse horizontally.
	 */
	public final static int HORIZONTALLY = 2;

	/**
	 * Constructs a filter.
	 * @param direction the direction.
	 */
	public ReverseFilter ( int direction ) {
		this.direction = direction;
	}

	/**
	 * Operates the image processing filter and stores the result into
	 * the original image buffer.
	 * @param image the original image to process.
	 * @return the original image buffer.
	 */
	public MonoImage operate ( MonoImage image ) {
		if (direction == VERTICALLY)
			image.reverseVertically();
		if (direction == HORIZONTALLY)
			image.reverseHorizontally();
		return image;
	}
}
