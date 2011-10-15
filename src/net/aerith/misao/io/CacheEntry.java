/*
 * @(#)CacheEntry.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.io;
import java.io.*;

/**
 * The <code>CacheEntry</code> represents an entry of a file in the 
 * disk cache.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 February 23
 */

public class CacheEntry {
	/**
	 * The file path.
	 */
	protected String path;

	/**
	 * The file content.
	 */
	protected Object content;

	/**
	 * The update count.
	 */
	protected int update_count;

	/**
	 * The last used time.
	 */
	protected long last_used;

	/**
	 * Constructs a <code>CacheEntry</code>.
	 * @param path    the file path.
	 * @param content the file content.
	 */
	public CacheEntry ( String path, Object content ) {
		this.path = path;
		this.content = content;

		update_count = 0;
		last_used = System.currentTimeMillis();
	}

	/**
	 * Gets the file path.
	 * @return the file path.
	 */
	public String getPath ( ) {
		return path;
	}

	/**
	 * Gets the content.
	 * @return the content.
	 */
	public Object getContent ( ) {
		last_used = System.currentTimeMillis();
		return content;
	}

	/**
	 * Gets the last used time in milli seconds.
	 * @return the last used time in milli seconds.
	 */
	public long getLastUsedTimeMillis ( ) {
		return last_used;
	}

	/**
	 * Returns true when the entry is updated.
	 * @return true when the entry is updated.
	 */
	public boolean isUpdated ( ) {
		return (update_count > 0);
	}

	/**
	 * Gets the update count.
	 * @return the update count.
	 */
	public int getUpdateCount ( ) {
		return update_count;
	}

	/**
	 * Resets the update count.
	 */
	public void resetUpdateCount ( ) {
		update_count = 0;
	}

	/**
	 * Updates the file content.
	 * @param new_content the new file content.
	 */
	public void update ( Object new_content ) {
		content = new_content;

		update_count++;
		last_used = System.currentTimeMillis();
	}
}
