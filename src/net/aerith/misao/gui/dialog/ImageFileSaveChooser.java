/*
 * @(#)ImageFileSaveChooser.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui.dialog;
import java.io.*;
import java.net.*;
import javax.swing.*;
import net.aerith.misao.io.filechooser.*;
import net.aerith.misao.image.io.Format;
import net.aerith.misao.image.UnsupportedFileTypeException;

/**
 * The <code>ImageFileSaveChooser</code> represents a file chooser 
 * dialog to save an image file.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class ImageFileSaveChooser extends ImageFileChooser {
	/**
	 * Constructs an <code>ImageFileSaveChooser</code>.
	 */
	public ImageFileSaveChooser ( ) {
		super();

		addChoosableFileFilter(new FitsFilter());

		ImageFileFilter[] filters = ImageFileFilter.getBitmapFilters();
		for (int i = 0 ; i < filters.length ; i++)
			addChoosableFileFilter(filters[i]);
	}
}
