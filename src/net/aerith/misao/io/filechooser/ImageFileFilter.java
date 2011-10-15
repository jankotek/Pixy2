/*
 * @(#)ImageFileFilter.java
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
import net.aerith.misao.pixy.PluginLoader;

/**
 * The <code>ImageFileFilter</code> is an abstract base classs of 
 * <code>FileFilter</code> of image files.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public abstract class ImageFileFilter extends ExtensionBasedFileFilter {
	/**
	 * Returns the acceptable extensions.
	 * @return the acceptable extensions.
	 */
	public String[] getAcceptableExtensions ( ) {
		String[] exts = new String[1];
		exts[0] = getTypicalExtension();
		return exts;
	}

	/**
	 * Gets the proper format for the image type.
	 * @return the image file format.
	 * @exception MalformedURLException if an unknown protocol is 
	 * specified.
	 * @exception UnsupportedFileTypeException if the file type is 
	 * unsupported.
	 */
	public abstract Format getFormat ( File file )
		throws MalformedURLException, UnsupportedFileTypeException;

	/**
	 * Gets the format ID recorded in an XML document.
	 * @return the format ID recorded in an XML document.
	 */
	public abstract String getFormatID ( );

	/**
	 * Gets the typical file extension.
	 * @return the typical file extension.
	 */
	public abstract String getTypicalExtension ( );

	/**
	 * Gets the list of filters of supported image file formats.
	 * @return the list of filters of supported image file formats.
	 */
	public static ImageFileFilter[] getSupportedFilters ( ) {
		JimiFileFilter[] jimi_filters = JimiFileFilter.getSupportedJimiFilters();
		ImageFileFilter[] plugin_filters = PluginLoader.loadImageFileFilters();

		ImageFileFilter[] filters = new ImageFileFilter[4 + jimi_filters.length + plugin_filters.length];

		int count = 0;
		for (int i = 0 ; i < plugin_filters.length ; i++)
			filters[count++] = plugin_filters[i];
		for (int i = 0 ; i < jimi_filters.length ; i++)
			filters[count++] = jimi_filters[i];
		filters[count++] = new PpmFilter();
		filters[count++] = new SbigFilter();
		filters[count++] = new UnsignedFitsFilter();
		filters[count++] = new FitsFilter();

		return filters;
	}

	/**
	 * Gets the list of filters of bitmap image file formats.
	 * @return the list of filters of bitmap image file formats.
	 */
	public static ImageFileFilter[] getBitmapFilters ( ) {
		int count = 2;

		JimiFileFilter[] jimi_filters = JimiFileFilter.getBitmapJimiFilters();

		ImageFileFilter[] filters = new ImageFileFilter[count + jimi_filters.length];

		count = 0;
		for (int i = 0 ; i < jimi_filters.length ; i++)
			filters[count++] = jimi_filters[i];
		filters[count++] = new PpmFilter();
		filters[count++] = new RawGrayFilter();

		return filters;
	}
}
