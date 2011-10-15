/*
 * @(#)CdromNotFoundException.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.io;
import java.io.IOException;

/**
 * The <code>CdromNotFoundException</code> is an exception thrown if a
 * file or directory in CD-ROMs is not found. It contains the disk 
 * name to be set in the drive.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 July 31
 */

public class CdromNotFoundException extends IOException {
	/**
	 * The disk name to be set.
	 */
	private String disk_name;

	/**
	 * Constructs a <code>CdromNotFoundException</code> with a disk 
	 * name
	 * @param disk the string indicating disk name to be set.
	 */
	public CdromNotFoundException ( String disk ) {
		disk_name = disk;
	}

	/**
	 * Gets the disk name.
	 * @return the disk name.
	 */
	public String getDiskName ( ) {
		return disk_name;
	}
}
