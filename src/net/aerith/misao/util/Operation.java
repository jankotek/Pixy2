/*
 * @(#)Operation.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;
import java.util.*;

/**
 * The <code>Operation</code> represents an operation to be run as a
 * thread.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public abstract class Operation extends OperationObservable implements Runnable {
	/**
	 * The set of monitors.
	 */
	protected MonitorSet monitor_set = new MonitorSet();

	/**
	 * True when this operation must be stopped.
	 */
	protected boolean stopped = false;

	/**
	 * Adds a monitor.
	 * @param monitor the monitor.
	 */
	public void addMonitor ( Monitor monitor ) {
		monitor_set.addMonitor(monitor);
	}

	/**
	 * Returns true if the operation is ready to start.
	 * @return true if the operation is ready to start.
	 */
	public abstract boolean ready ( );

	/**
	 * Performs this operation.
	 * @exception Exception if an error occurs.
	 */
	public void perform ( )
		throws Exception
	{
		notifyStart();

		Exception operating_exception = null;

		try {
			operate();
		} catch ( Exception exception ) {
			operating_exception = exception;
		}

		stopped = false;

		notifyEnd(operating_exception);
		
		if (operating_exception != null)
			throw operating_exception;
	}

	/**
	 * Operates.
	 * @exception Exception if an error occurs.
	 */
	protected abstract void operate ( )
		throws Exception;

	/**
	 * Sets the flag to be stopped.
	 */
	public void stop ( ) {
		stopped = true;
	}

	/**
	 * Returns true if the operation is to be stopped.
	 */
	public boolean isStopped ( ) {
		return stopped;
	}

	/**
	 * Run this thread.
	 */
	public void run ( ) {
		try {
			perform();
		} catch ( Exception exception ) {
		}
	}
}
