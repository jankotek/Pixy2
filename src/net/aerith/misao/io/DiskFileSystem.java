/*
 * @(#)DiskFileSystem.java
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
 * The <code>DiskFileSystem</code> represents a directory on the real
 * file system on the disk drive.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 November 26
 */

public class DiskFileSystem implements FileSystem {
	/**
	 * The directory.
	 */
	protected File db_folder;

	/**
	 * Constructs a <code>DiskFileSystem</code>.
	 * @param db_folder the directory.
	 * @exception IOException if I/O error occurs.
	 */
	public DiskFileSystem ( File db_folder )
		throws IOException
	{
		this.db_folder = db_folder;

		if (db_folder.exists()) {
			if (db_folder.isDirectory() == false)
				throw new IOException();
		} else {
			if (db_folder.mkdirs() == false)
				throw new IOException();
		}
	}

	/**
	 * Gets the top folder.
	 * @param name the folder name.
	 * @return the folder.
	 */
	public Folder getFolder ( String name ) {
		String path = FileManager.unitePath(db_folder.getPath(), name);

		Vector folder_list = new Vector();
		folder_list.addElement(name);

		return new FileFolder(new File(path), folder_list);
	}

	/**
	 * Gets the folder represented by the specified hierarchy.
	 * @param folder_list the list of hierarchical folders.
	 * @return the terminal folder.
	 */
	public Folder getHierarchicalFolder ( Vector folder_list ) {
		String path = db_folder.getPath();
		for (int i = 0 ; i < folder_list.size() ; i++)
			path = FileManager.unitePath(path, (String)folder_list.elementAt(i));

		return new FileFolder(new File(path), folder_list);
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
		String path = db_folder.getPath();
		for (int i = 0 ; i < folder_list.size() ; i++)
			path = FileManager.unitePath(path, (String)folder_list.elementAt(i));

		File file = new File(path);

		if (file.exists()) {
			if (file.isDirectory() == false)
				throw new IOException();
		} else {
			if (file.mkdirs() == false)
				throw new IOException();
		}

		return new FileFolder(file, folder_list);
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
		return new PrimitiveFileManager(((FileFolder)folder).getFile(), holder_class, record_class);
	}

	/**
	 * Discards the file system. All files and directories in the file
	 * system removed.
	 * @exception IOException if I/O error occurs.
	 */
	public void discard ( )
		throws IOException
	{
		if (discard(db_folder) == false)
			throw new IOException();
	}

	/**
	 * Discards the specified folder. All files and directories in the
	 * specified folder are removed.
	 * @param file the file or directory.
	 * @return true if all files and directories are successfully 
	 * deleted.
	 */
	private boolean discard ( File file ) {
		boolean flag = true;

		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (int i = 0 ; i < files.length ; i++) {
				if (discard(files[i]) == false)
					flag = false;
			}
		}

		if (file.delete() == false)
			flag = false;

		return flag;
	}
}
