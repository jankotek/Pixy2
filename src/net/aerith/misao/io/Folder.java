/*
 * @(#)Folder.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.io;
import java.io.*;
import java.util.*;

/**
 * The <code>Folder</code> is an interface of a folder on the virtual 
 * file system.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2000 December 30
 */

public interface Folder {
	/**
	 * Gets the folder hierarchy.
	 * @return the folder hierarchy.
	 */
	public abstract Vector getHierarchy ( );

	/**
	 * Gets the ID of this folder.
	 * @return the ID of this folder.
	 */
	public abstract String getID ( );

	/**
	 * Gets the name of this folder.
	 * @return the name of this folder.
	 */
	public abstract String getName ( );

	/**
	 * Gets the sub folder.
	 * @param name the name of the sub folder.
	 * @return the sub folder.
	 */
	public abstract Folder getFolder ( String name );

	/**
	 * Returns true if this folder is a real folder.
	 * @return true if this folder is a real folder.
	 */
	public abstract boolean isFolder ( );

	/**
	 * Returns true if this folder really exists.
	 * @return true if this folder really exists.
	 */
	public abstract boolean exists ( );

	/**
	 * Gets the list of sub folders.
	 * @return the list of sub folders.
	 */
	public abstract Folder[] list ( );

	/**
	 * Returns a string representation of the state of this object,
	 * for debugging use.
	 * @return a string representation of the state of this object.
	 */
	public String toString ( );
}
