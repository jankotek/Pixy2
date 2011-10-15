/*
 * @(#)DefaultImageLoader.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.pixy.image_loading;
import java.io.*;
import java.net.*;
import java.util.*;
import net.aerith.misao.image.*;
import net.aerith.misao.image.io.*;
import net.aerith.misao.util.*;
import net.aerith.misao.pixy.Resource;

/**
 * The <code>DefaultImageLoader</code> is a class to load an image file.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class DefaultImageLoader extends Operation {
	/**
	 * The image file to load.
	 */
	protected File file;

	/**
	 * The format of the image file.
	 */
	protected net.aerith.misao.image.io.Format format = null;

	/**
	 * The loaded image.
	 */
	protected MonoImage image = null;

	/**
	 * Constructs a <code>DefaultImageLoader</code> with a 
	 * <code>File</code> of the image file.
	 * @param file   the image file to load.
	 * @param format the image format. In the case of null, the system 
	 * automatically selects a proper format based on the file name.
	 */
	public DefaultImageLoader ( File file, net.aerith.misao.image.io.Format format ) {
		this.file = file;
		this.format = format;
	}

	/**
	 * Gets the loaded image.
	 * @return the loaded image.
	 */
	public MonoImage getMonoImage ( ) {
		return image;
	}

	/**
	 * Returns true if the operation is ready to start.
	 * @return true if the operation is ready to start.
	 */
	public boolean ready ( ) {
		return true;
	}

	/**
	 * Operates.
	 * @exception Exception if an error occurs.
	 */
	public void operate ( )
		throws Exception
	{
		monitor_set.addMessage("[Opening the image]");
		monitor_set.addMessage(new Date().toString());

		if (file == null)
			throw new FileNotFoundException();

		monitor_set.addMessage("Image: " + file.getPath());

		if (format == null)
			format = net.aerith.misao.image.io.Format.create(file);

		monitor_set.addMessage("Image type: " + format.getName());
		if (format.isFits())
			monitor_set.addMessage("Image data order: " + Resource.getDefaultFitsOrder());

		image = format.read();

		if (image != null) {
			Statistics stat = new Statistics(image);
			stat.calculate();

			monitor_set.addMessage("Size: " + image.getSize().getWidth() + " x " + image.getSize().getHeight());
			monitor_set.addMessage("Statistics: " + stat.getOutputString());
		}

		monitor_set.addMessage(new Date().toString());
		monitor_set.addSeparator();
	}
}
