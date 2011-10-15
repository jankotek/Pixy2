/*
 * @(#)UnsupportedFileTypeException.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.image;
import java.net.URL;

/**
 * The <code>UnsupportedFileTypeException</code> is an exception
 * thrown if the type of image file is unsupported.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 July 31
 */

public class UnsupportedFileTypeException extends Exception {
	/**
	 * The image URL.
	 */
	protected URL url;

	/**
	 * The image format name.
	 */
	protected String format;

	/**
	 * The operation not supported.
	 */
	protected String operation;

	/**
	 * Constructs an <code>UnsupportedFileTypeException</code>.
	 * @param url       the image URL.
	 * @param format    the image format name.
	 * @param operation the operation not supported.
	 */
	public UnsupportedFileTypeException ( URL url, String format, String operation ) {
		this.url = url;
		this.format = format;
		this.operation = operation;
	}

	/**
	 * Gets the error message.
	 * @return the error message.
	 */
	public String getMessage ( ) {
		String message = "";

		if (operation.length() == 0)
			message += "Operation";
		else
			message += "Operation " + operation;

		message += " is not supported";

		if (format.length() > 0)
			message += " in " + format + " format";

		message += ": " + url.toString();

		return message;
	}
}
