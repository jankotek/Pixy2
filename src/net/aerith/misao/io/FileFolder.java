/*
 * @(#)FileFolder.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.io;
import java.io.*;
import java.util.*;

/**
 * The <code>FileFolder</code> represents a directory on the real file
 * system.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2002 September 23
 */

public class FileFolder implements Folder {
	/**
	 * The <code>File</code> object.
	 */
	protected File file;

	/**
	 * The folder hierarchy.
	 */
	protected Vector folder_list;

	/**
	 * Constructs a <code>FileFolder</code>.
	 * @param file        the <code>File</code> object of the folder.
	 * @param folder_list the leading folder hierarchy.
	 */
	public FileFolder ( File file, Vector folder_list ) {
		this.file = file;

		this.folder_list = new Vector();
		for (int i = 0 ; i < folder_list.size() ; i++)
			this.folder_list.addElement(folder_list.elementAt(i));
	}

	/**
	 * Gets the <code>File</code> object.
	 * @return the <code>File</code> object.
	 */
	public File getFile ( ) {
		return file;
	}

	/**
	 * Gets the folder hierarchy.
	 * @return the folder hierarchy.
	 */
	public Vector getHierarchy ( ) {
		Vector list = new Vector();
		for (int i = 0 ; i < folder_list.size() ; i++)
			list.addElement(folder_list.elementAt(i));
		return list;
	}

	/**
	 * Gets the ID of this folder.
	 * @return the ID of this folder.
	 */
	public String getID ( ) {
		return file.getAbsolutePath();
	}

	/**
	 * Gets the name of this folder.
	 * @return the name of this folder.
	 */
	public String getName ( ) {
		return file.getName();
	}

	/**
	 * Gets the sub folder.
	 * @param name the name of the sub folder.
	 * @return the sub folder.
	 */
	public Folder getFolder ( String name ) {
		String path = FileManager.unitePath(file.getPath(), name);

		int index = folder_list.size();

		folder_list.addElement(name);
		Folder folder = new FileFolder(new File(path), folder_list);
		folder_list.removeElementAt(index);

		return folder;
	}

	/**
	 * Returns true if this folder is a real folder.
	 * @return true if this folder is a real folder.
	 */
	public boolean isFolder ( ) {
		return file.isDirectory();
	}

	/**
	 * Returns true if this folder really exists.
	 * @return true if this folder really exists.
	 */
	public boolean exists ( ) {
		return file.exists();
	}

	/**
	 * Gets the list of sub folders.
	 * @return the list of sub folders.
	 */
	public Folder[] list ( ) {
		File[] files = file.listFiles();
		if (files == null)
			return new Folder[0];

		Folder[] folders = new Folder[files.length];

		int index = folder_list.size();

		for (int i = 0 ; i < files.length ; i++) {
			String name = files[i].getName();

			String path = FileManager.unitePath(file.getPath(), name);

			folder_list.addElement(name);
			Folder folder = new FileFolder(new File(path), folder_list);
			folder_list.removeElementAt(index);

			folders[i] = folder;
		}

		return folders;
	}

	/**
	 * Returns a raw string representation of the state of this object,
	 * for debugging use. It should be invoked from <code>toString</code>
	 * method of the subclasses.
	 * @return a string representation of the state of this object.
	 */
	protected String paramString ( ) {
		if (file != null)
			return file.getPath();
		return "null";
	}

	/**
	 * Returns a string representation of the state of this object,
	 * for debugging use.
	 * @return a string representation of the state of this object.
	 */
	public String toString ( ) {
		return getClass().getName() + "[" + paramString() + "]";
	}
}
