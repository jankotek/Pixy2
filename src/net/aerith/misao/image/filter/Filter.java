/*
 * @(#)Filter.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.image.filter;
import net.aerith.misao.image.MonoImage;
import net.aerith.misao.util.Monitor;
import net.aerith.misao.util.MonitorSet;

/**
 * The <code>Filter</code> is an interface of image processing filter.
 * <p>
 * There are two types of filters. 
 * <p>
 * One is to store the result into the original image buffer. In this 
 * case, the <code>operate</code> method returns the original image.
 * <p>
 * Another one is to create a new image buffer. In this case, the 
 * original image remains unchanged.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public abstract class Filter {
	/**
	 * The set of monitors.
	 */
	protected MonitorSet monitor_set = new MonitorSet();

	/**
	 * Sets a monitor.
	 * @param monitor the monitor.
	 */
	public void setMonitor ( Monitor monitor ) {
		monitor_set.setMonitor(monitor);
	}

	/**
	 * Adds a monitor.
	 * @param monitor the monitor.
	 */
	public void addMonitor ( Monitor monitor ) {
		monitor_set.addMonitor(monitor);
	}

	/**
	 * Operates the image processing filter.
	 * @return the filtered image.
	 */
	public abstract MonoImage operate ( MonoImage image );
}
