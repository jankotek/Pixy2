/*
 * @(#)FileSystem.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.io;
import java.io.IOException;
import java.util.*;
import net.aerith.misao.database.*;

/**
 * The <code>FileSystem</code> is an interface of a file system. It is
 * a directory on the real file system on the disk drive, or a virtual
 * file system on memory.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2000 December 30
 */

public interface FileSystem {
	/**
	 * Gets the top folder.
	 * @param name the folder name.
	 * @return the folder.
	 */
	public abstract Folder getFolder ( String name );

	/**
	 * Gets the folder represented by the specified hierarchy.
	 * @param folder_list the list of hierarchical folders.
	 * @return the terminal folder.
	 */
	public abstract Folder getHierarchicalFolder ( Vector folder_list );

	/**
	 * Creates the folder hierarchy and returns the folder.
	 * @param folder_list the list of hierarchical folders.
	 * @return the terminal folder.
	 * @exception IOException if I/O error occurs.
	 */
	public abstract Folder createHierarchicalFolder ( Vector folder_list )
		throws IOException;

	/**
	 * Gets the new <code>PrimitiveManager</code> of the specified
	 * folder.
	 * @param folder       the folder.
	 * @param holder_class the holder class object of the XML records.
	 * @param record_class the class object of the XML records.
	 * @exception IOException if I/O error occurs.
	 */
	public abstract PrimitiveManager getPrimitiveManager ( Folder folder, XmlDBHolder holder_class, XmlDBRecord record_class )
		throws IOException;

	/**
	 * Discards the file system. All files and directories in the file
	 * system removed.
	 * @exception IOException if I/O error occurs.
	 */
	public abstract void discard ( )
		throws IOException;
}
