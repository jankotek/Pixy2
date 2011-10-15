/*
 * @(#)MemoryFolder.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.io;
import java.io.*;
import java.util.*;

/**
 * The <code>MemoryFolder</code> represents a folder on the virtual
 * file system on memory.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2000 November 7
 */

public class MemoryFolder implements Folder {
	/**
	 * The <code>Hashtable</code> object.
	 */
	protected Hashtable hash_folder = null;

	/**
	 * The folder hierarchy.
	 */
	protected Vector folder_list;

	/**
	 * Constructs a <code>MemoryFolder</code>.
	 * @param hash_folder the <code>Hashtable</code> object of the 
	 * folder.
	 * @param folder_list the leading folder hierarchy.
	 */
	public MemoryFolder ( Hashtable hash_folder, Vector folder_list ) {
		this.hash_folder = hash_folder;

		this.folder_list = new Vector();
		for (int i = 0 ; i < folder_list.size() ; i++)
			this.folder_list.addElement(folder_list.elementAt(i));
	}

	/**
	 * Gets the <code>Hashtable</code> object.
	 * @return the <code>Hashtable</code> object.
	 */
	public Hashtable getHash ( ) {
		return hash_folder;
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
		String s = "";
		for (int i = 0 ; i < folder_list.size() ; i++)
			s += File.separator + (String)folder_list.elementAt(i);
		return s;
	}

	/**
	 * Gets the name of this folder.
	 * @return the name of this folder.
	 */
	public String getName ( ) {
		return (String)folder_list.elementAt(folder_list.size() - 1);
	}

	/**
	 * Gets the sub folder.
	 * @param name the name of the sub folder.
	 * @return the sub folder.
	 */
	public Folder getFolder ( String name ) {
		Hashtable hash = null;

		if (hash_folder != null) {
			try {
				hash = (Hashtable)hash_folder.get(name);
			} catch ( ClassCastException exception ) {
				hash = null;
			}
		}

		int index = folder_list.size();

		folder_list.addElement(name);
		Folder folder = new MemoryFolder(hash, folder_list);
		folder_list.removeElementAt(index);

		return folder;
	}

	/**
	 * Returns true if this folder is a real folder.
	 * @return true if this folder is a real folder.
	 */
	public boolean isFolder ( ) {
		return exists();
	}

	/**
	 * Returns true if this folder really exists.
	 * @return true if this folder really exists.
	 */
	public boolean exists ( ) {
		return (hash_folder != null);
	}

	/**
	 * Gets the list of sub folders.
	 * @return the list of sub folders.
	 */
	public Folder[] list ( ) {
		if (hash_folder == null)
			return new Folder[0];

		Enumeration enum_keys = hash_folder.keys();
		Vector list_keys = new Vector();
		while (enum_keys.hasMoreElements())
			list_keys.addElement(enum_keys.nextElement());
		String[] keys = new String[list_keys.size()];
		for (int i = 0 ; i < keys.length ; i++)
			keys[i] = (String)list_keys.elementAt(i);

		Folder[] folders = new Folder[keys.length];

		int index = folder_list.size();

		for (int i = 0 ; i < keys.length ; i++) {
			Hashtable hash = null;
			try {
				hash = (Hashtable)hash_folder.get(keys[i]);
			} catch ( ClassCastException exception ) {
				hash = null;
			}

			folder_list.addElement(keys[i]);
			Folder folder = new MemoryFolder(hash, folder_list);
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
		if (hash_folder != null)
			return getID();
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
