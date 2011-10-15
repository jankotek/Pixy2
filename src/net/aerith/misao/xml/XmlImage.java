/*
 * @(#)XmlImage.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.xml;
import java.io.*;
import java.net.*;
import net.aerith.misao.image.*;
import net.aerith.misao.image.io.*;

/**
 * The <code>XmlImage</code> is an application side implementation
 * of the class that the relaxer generated automatically.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 November 26
 */

public class XmlImage extends net.aerith.misao.xml.relaxer.XmlImage {
	/**
	 * Sets the image file format.
	 * @param the image file format object.
	 */
	public void setFileFormat ( Format format ) {
		setFormat(null);

		if (format == null)
			return;

		if (format instanceof Fits)
			setFormat("fits");
		if (format instanceof UnsignedFits)
			setFormat("unsigned-fits");
		if (format instanceof Sbig)
			setFormat("sbig");
		if (format instanceof Ppm)
			setFormat("ppm");
	}
}
