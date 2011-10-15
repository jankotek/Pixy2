/*
 * @(#)ImageIOFileFilter.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.io.filechooser;

/**
 * The <code>ImageIOFileFilter</code> is an abstract base classs of
 * <code>FileFilter</code> of image files supported by ImageIO.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public abstract class ImageIOFileFilter extends ImageFileFilter {
	/**
	 * Gets the list of filters of supported image file formats.
	 * @return the list of filters of supported image file formats.
	 */
	public static ImageIOFileFilter[] getSupportedJimiFilters ( ) {


		ImageIOFileFilter[] filters = new ImageIOFileFilter[4];

		int count = 0;
		filters[count++] = new JpegFilter();
		filters[count++] = new GifFilter();
		filters[count++] = new PngFilter();
		filters[count++] = new BmpFilter();

		return filters;
	}

	/**
	 * Gets the list of filters of bitmap image file formats.
	 * @return the list of filters of bitmap image file formats.
	 */
	public static ImageIOFileFilter[] getBitmapJimiFilters ( ) {


		ImageIOFileFilter[] filters = new ImageIOFileFilter[3];

		int count = 0;
		filters[count++] = new JpegFilter();
		filters[count++] = new PngFilter();
		filters[count++] = new BmpFilter();

		return filters;
	}
}
