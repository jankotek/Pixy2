/*
 * @(#)DefaultOperationObserver.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;
import java.util.Vector;

/**
 * The <code>DefaultOperationObserver</code> represents an observer
 * of several tasks in one operation, which remembers the succeeded 
 * and failed tasks.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class DefaultOperationObserver implements OperationObserver {
	/**
	 * The list of succeeded tasks.
	 */
	protected Vector succeeded_list = new Vector();

	/**
	 * The list of failed tasks.
	 */
	protected Vector failed_list = new Vector();

	/**
	 * The list of warned tasks.
	 */
	protected Vector warned_list = new Vector();

	/**
	 * Invoked when the operation starts.
	 */
	public void notifyStart ( ) {
		succeeded_list = new Vector();
		failed_list = new Vector();
		warned_list = new Vector();
	}

	/**
	 * Invoked when the operation ends.
	 * @param exception the exception if an error occurs, or null if
	 * succeeded.
	 */
	public void notifyEnd ( Exception exception ) {
	}

	/**
	 * Invoked when a task is succeeded.
	 * @param arg the argument.
	 */
	public void notifySucceeded ( Object arg ) {
		succeeded_list.addElement(arg);
	}

	/**
	 * Invoked when a task is failed.
	 * @param arg the argument.
	 */
	public void notifyFailed ( Object arg ) {
		failed_list.addElement(arg);
	}

	/**
	 * Invoked when a task is warned.
	 * @param arg the argument.
	 */
	public void notifyWarned ( Object arg ) {
		warned_list.addElement(arg);
	}

	/**
	 * Gets the list of succeeded tasks.
	 * @return the list of succeeded tasks.
	 */
	public Vector getSucceededList ( ) {
		if (succeeded_list != null)
			return succeeded_list;
		return new Vector();
	}

	/**
	 * Gets the list of failed tasks.
	 * @return the list of failed tasks.
	 */
	public Vector getFailedList ( ) {
		if (failed_list != null)
			return failed_list;
		return new Vector();
	}

	/**
	 * Gets the list of warned tasks.
	 * @return the list of warned tasks.
	 */
	public Vector getWarnedList ( ) {
		if (warned_list != null)
			return warned_list;
		return new Vector();
	}
}
