/*
 * @(#)Bitmap.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.image.io;
import java.io.*;
import java.net.*;
import net.aerith.misao.image.*;

/**
 * The <code>Bitmap</code> is an interface of bitmap. It has a method
 * to save an image using the specified <code>LevelAdjustmentSet</code>.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 July 31
 */

public interface Bitmap {
	/**
	 * Saves an image buffer into an image file, using the specified
	 * <code>LevelAdjustmentSet</code>. The url of the image file 
	 * must be set previously.
	 * @param image the monochrome image buffer to save.
	 * @exception IOException if I/O error occurs.
	 * @exception UnsupportedFileTypeException if the file type is 
	 * unsupported.
	 */
	public abstract void save ( MonoImage image, LevelAdjustmentSet set )
		throws IOException, UnsupportedFileTypeException;
}
