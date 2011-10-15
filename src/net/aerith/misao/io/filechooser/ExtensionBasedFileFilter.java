/*
 * @(#)ExtensionBasedFileFilter.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.io.filechooser;
import java.io.File;
import java.util.ArrayList;
import javax.swing.filechooser.FileFilter;
import net.aerith.misao.io.Decoder;
import net.aerith.misao.io.FileManager;

/**
 * The <code>ExtensionBasedFileFilter</code> represents a 
 * <code>FileFilter</code> which selects files based on the file
 * extension.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public abstract class ExtensionBasedFileFilter extends FileFilter {
	/**
	 * True when to accept the compressed files.
	 */
	protected boolean accept_compressed = true;

	/**
	 * Sets the flag to accept compressed files when checks the 
	 * existence of file.
	 * @param flag true when to accept compressed files.
	 */
	public void acceptCompressed ( boolean flag ) {
		accept_compressed = flag;
	}

	/**
	 * Gets the file name without extension.
	 * @param file the image file.
	 * @return the file name without extension.
	 */
	public String getTruncatedFilename ( File file ) {
		String filename = file.getName();

		if (file.isFile()) {
			ArrayList list = new ArrayList();

			String[] exts = getAcceptableExtensions();
			for (int i = 0 ; i < exts.length ; i++)
				list.add("." + exts[i]);

			if (accept_compressed) {
				String[] compressed_exts = Decoder.getCompressedExtensions();
				for (int j = 0 ; j < compressed_exts.length ; j++) {
					for (int i = 0 ; i < exts.length ; i++)
						list.add("." + exts[i] + "." + compressed_exts[j]);
				}
			}

			exts = new String[list.size()];
			exts = (String[])list.toArray(exts);

			for (int i = 0 ; i < exts.length ; i++) {
				if (FileManager.endsWith(filename, exts[i]))
					return filename.substring(0, filename.length() - exts[i].length());
			}
		}

		return filename;
	}

	/**
	 * Returns true if the specified file is an XML file.
	 * @param file the file to judge.
	 * @return true if the file is an XML file.
	 */
	public boolean accept ( File file ) {
		if (file.isFile()) {
			String filename = file.getName();

			if (filename.equals(getTruncatedFilename(file)) == false)
				return true;
		}
		if (file.isDirectory()) {
			return true;
		}
		return false;
	}

	/**
	 * Returns the acceptable extensions.
	 * @return the acceptable extensions.
	 */
	public abstract String[] getAcceptableExtensions();
}
