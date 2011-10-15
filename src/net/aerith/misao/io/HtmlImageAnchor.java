/*
 * @(#)HtmlImageAnchor.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.io;
import java.io.*;
import java.net.*;
import java.util.*;
import net.aerith.misao.image.MonoImage;
import net.aerith.misao.image.io.Format;

/**
 * The <code>HtmlImageAnchor</code> represents an anchor to the image
 * file.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 17
 */

public class HtmlImageAnchor {
	/**
	 * The image.
	 */
	protected MonoImage image = null;

	/**
	 * The URL.
	 */
	protected URL url = null;

	/**
	 * The format.
	 */
	protected Format format = null;

	/**
	 * The text.
	 */
	protected String text = null;

	/**
	 * Constructs a <code>HtmlImageAnchor</code>.
	 * @param image  the image.
	 * @param url    the URL.
	 * @param format the format.
	 * @param text   the text.
	 */
	public HtmlImageAnchor ( MonoImage image, URL url, Format format, String text ) {
		this.image = image;
		this.url = url;
		this.format = format;
		this.text = text;
	}

	/**
	 * Gets the image.
	 * @return the image.
	 */
	public MonoImage getImage ( ) {
		return image;
	}

	/**
	 * Gets the URL.
	 * @return the URL.
	 */
	public URL getURL ( ) {
		return url;
	}

	/**
	 * Gets the format.
	 * @return the format.
	 */
	public Format getFormat ( ) {
		return format;
	}

	/**
	 * Gets the text.
	 * @return the text.
	 */
	public String getText ( ) {
		return text;
	}
}
