/*
 * @(#)XmlFilter.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.io.filechooser;
import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * The <code>XmlFilter</code> represents a <code>FileFilter</code>
 * to select XML files.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class XmlFilter extends ExtensionBasedFileFilter {
	/**
	 * Constructs a <code>XmlFilter</code>.
	 */
	public XmlFilter ( ) {
	}

	/**
	 * Returns the acceptable extensions.
	 * @return the acceptable extensions.
	 */
	public String[] getAcceptableExtensions ( ) {
		String[] exts = new String[1];
		exts[0] = "xml";
		return exts;
	}

	/**
	 * Returns the description of this filter.
	 * @return the description of this filter.
	 */
	public String getDescription ( ) {
		return "XML files";
	}
}
