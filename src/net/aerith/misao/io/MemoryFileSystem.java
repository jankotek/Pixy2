/*
 * @(#)MemoryFileSystem.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.io;
import java.io.File;
import java.io.IOException;
import java.util.*;
import net.aerith.misao.database.*;

/**
 * The <code>MemoryFileSystem</code> represents a virtual file system
 * on memory.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2000 December 30
 */

public class MemoryFileSystem implements FileSystem {
	/**
	 * The root hash table.
	 */
	protected Hashtable hash_root;

	/**
	 * The hash table for objects.
	 */
	protected Hashtable hash_objects;

	/**
	 * Constructs a <code>MemoryFileSystem</code>.
	 */
	public MemoryFileSystem ( ) {
		hash_root = new Hashtable();
		hash_objects = new Hashtable();
	}

	/**
	 * Gets the top folder.
	 * @param name the folder name.
	 * @return the folder.
	 */
	public Folder getFolder ( String name ) {
		Hashtable hash = null;
		try {
			hash = (Hashtable)hash_root.get(name);
		} catch ( ClassCastException exception ) {
			hash = null;
		}

		Vector folder_list = new Vector();
		folder_list.addElement(name);

		return new MemoryFolder(hash, folder_list);
	}

	/**
	 * Gets the folder represented by the specified hierarchy.
	 * @param folder_list the list of hierarchical folders.
	 * @return the terminal folder.
	 */
	public Folder getHierarchicalFolder ( Vector folder_list ) {
		Hashtable hash = hash_root;
		for (int i = 0 ; i < folder_list.size() ; i++) {
			String name = (String)folder_list.elementAt(i);
			if (hash != null) {
				try {
					hash = (Hashtable)hash.get(name);
				} catch ( ClassCastException exception ) {
					hash = null;
				}
			}
		}

		return new MemoryFolder(hash, folder_list);
	}

	/**
	 * Creates the folder hierarchy and returns the folder.
	 * @param folder_list the list of hierarchical folders.
	 * @return the terminal folder.
	 * @exception IOException if I/O error occurs.
	 */
	public Folder createHierarchicalFolder ( Vector folder_list )
		throws IOException
	{
		Hashtable hash = hash_root;
		for (int i = 0 ; i < folder_list.size() ; i++) {
			String name = (String)folder_list.elementAt(i);
			try {
				Hashtable hash_child = (Hashtable)hash.get(name);

				if (hash_child == null) {
					hash_child = new Hashtable();
					hash.put(name, hash_child);
				}

				hash = hash_child;
			} catch ( ClassCastException exception ) {
				throw new IOException();
			}
		}

		return new MemoryFolder(hash, folder_list);
	}

	/**
	 * Gets the new <code>PrimitiveManager</code> of the specified
	 * folder.
	 * @param folder       the folder.
	 * @param holder_class the holder class object of the XML records.
	 * @param record_class the class object of the XML records.
	 * @exception IOException if I/O error occurs.
	 */
	public PrimitiveManager getPrimitiveManager ( Folder folder, XmlDBHolder holder_class, XmlDBRecord record_class )
		throws IOException
	{
		return new PrimitiveMemoryManager(((MemoryFolder)folder).getHash(), holder_class, record_class);
	}

	/**
	 * Discards the file system. All files and directories in the file
	 * system removed.
	 * @exception IOException if I/O error occurs.
	 */
	public void discard ( )
		throws IOException
	{
		hash_root = new Hashtable();
	}
}
