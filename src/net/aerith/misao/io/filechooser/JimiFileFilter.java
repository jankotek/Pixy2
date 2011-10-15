/*
 * @(#)JimiFileFilter.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.io.filechooser;
import java.io.*;
import java.net.*;
import javax.swing.filechooser.FileFilter;
import net.aerith.misao.image.io.Format;
import net.aerith.misao.image.UnsupportedFileTypeException;

/**
 * The <code>JimiFileFilter</code> is an abstract base classs of 
 * <code>FileFilter</code> of image files supported by JIMI.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public abstract class JimiFileFilter extends ImageFileFilter {
	/**
	 * Gets the list of filters of supported image file formats.
	 * @return the list of filters of supported image file formats.
	 */
	public static JimiFileFilter[] getSupportedJimiFilters ( ) {
		try {
			Class.forName("com.sun.jimi.core.Jimi");
		} catch ( ClassNotFoundException exception ) {
			return new JimiFileFilter[0];
		}

		JimiFileFilter[] filters = new JimiFileFilter[5];

		int count = 0;
		filters[count++] = new JpegFilter();
		filters[count++] = new GifFilter();
		filters[count++] = new PngFilter();
		filters[count++] = new TiffFilter();
		filters[count++] = new BmpFilter();

		return filters;
	}

	/**
	 * Gets the list of filters of bitmap image file formats.
	 * @return the list of filters of bitmap image file formats.
	 */
	public static JimiFileFilter[] getBitmapJimiFilters ( ) {
		try {
			Class.forName("com.sun.jimi.core.Jimi");
		} catch ( ClassNotFoundException exception ) {
			return new JimiFileFilter[0];
		}

		JimiFileFilter[] filters = new JimiFileFilter[3];

		int count = 0;
		filters[count++] = new JpegFilter();
		filters[count++] = new PngFilter();
		filters[count++] = new BmpFilter();

		return filters;
	}
}
