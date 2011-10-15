/*
 * @(#)ImageFileChooser.java
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
 * The <code>ImageFileChooser</code> represents a file chooser dialog
 * to open/save an image file.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public abstract class ImageFileChooser extends CommonFileChooser {
	/**
	 * Constructs an <code>ImageFileChooser</code>.
	 */
	protected ImageFileChooser ( ) {
		super();
	}

	/**
	 * Gets the image file format of the selected file.
	 * @return the image file format of the selected file.
	 * @exception FileNotFoundException if the file does not exist.
	 * @exception MalformedURLException if an unknown protocol is 
	 * specified.
	 * @exception UnsupportedBufferTypeException if the data type is 
	 * unsupported.
	 */
	public Format getSelectedFileFormat ( )
		throws FileNotFoundException, MalformedURLException, UnsupportedFileTypeException
	{
		Format format = null;

		ImageFileFilter filter = null;
		try {
			filter = (ImageFileFilter)getFileFilter();
		} catch ( ClassCastException exception ) {
		}
		File file = getSelectedFile();

		if (file == null)
			throw new FileNotFoundException();

		if (file != null) {
			if (filter != null)
				format = filter.getFormat(file);
			else
				format = Format.create(file);
		}

		return format;
	}
}
