/*
 * @(#)OperationObserver.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;

/**
 * The <code>OperationObserver</code> is an interface which represents 
 * an observer of progress of an operation.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public interface OperationObserver {
	/**
	 * Invoked when the operation starts.
	 */
	public abstract void notifyStart ( );

	/**
	 * Invoked when the operation ends.
	 * @param exception the exception if an error occurs, or null if
	 * succeeded.
	 */
	public abstract void notifyEnd ( Exception exception );

	/**
	 * Invoked when a task is succeeded.
	 * @param arg the argument.
	 */
	public abstract void notifySucceeded ( Object arg );

	/**
	 * Invoked when a task is failed.
	 * @param arg the argument.
	 */
	public abstract void notifyFailed ( Object arg );

	/**
	 * Invoked when a task is warned.
	 * @param arg the argument.
	 */
	public abstract void notifyWarned ( Object arg );
}
