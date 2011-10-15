/*
 * @(#)ReportBatchOperation.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.ReportBatch;
import java.io.*;
import net.aerith.misao.util.*;
import net.aerith.misao.xml.*;
import net.aerith.misao.database.GlobalDBManager;
import net.aerith.misao.io.FileManager;

/**
 * The <code>ReportBatchOperation</code> represents a batch operation 
 * on XML report documents.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public abstract class ReportBatchOperation extends MultiTaskOperation {
	/**
	 * The database manager.
	 */
	private GlobalDBManager db_manager = null;

	/**
	 * The file manager.
	 */
	protected FileManager file_manager = new FileManager();

	/**
	 * Gets the database manager.
	 * @return the database manager.
	 * @exception IOException if I/O error occurs.
	 */
	protected GlobalDBManager getDBManager ( )
		throws IOException
	{
		if (db_manager == null)
			throw new IOException();

		return db_manager;
	}

	/**
	 * Sets the database manager.
	 * @param db_manager the database manager.
	 */
	public void setDBManager ( GlobalDBManager db_manager ) {
		this.db_manager = db_manager;
	}

	/**
	 * Sets the file manager.
	 * @param file_manager the file manager.
	 */
	public void setFileManager ( FileManager file_manager ) {
		this.file_manager = file_manager;
	}
}
