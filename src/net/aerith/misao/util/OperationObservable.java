/*
 * @(#)OperationObservable.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;
import java.util.*;

/**
 * The <code>OperationObservable</code> represents an object with an 
 * operation whose progress is observable.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public abstract class OperationObservable {
	/**
	 * The list of observers.
	 */
	protected Vector observer_list = new Vector();

	/**
	 * Adds an observer.
	 * @param observer an observer
	 */
	public void addObserver ( OperationObserver observer ) {
		observer_list.addElement(observer);
	}

	/**
	 * Removes an observer.
	 * @param observer an observer
	 */
	public void deleteObserver ( OperationObserver observer ) {
		observer_list.removeElement(observer);
	}

	/**
	 * Notifies when the operation starts.
	 */
	protected void notifyStart ( ) {
		for (int i = 0 ; i < observer_list.size() ; i++)
			((OperationObserver)observer_list.elementAt(i)).notifyStart();
	}

	/**
	 * Notifies when the operation ends.
	 * @param exception the exception if an error occurs, or null if
	 * succeeded.
	 */
	protected void notifyEnd ( Exception exception ) {
		for (int i = 0 ; i < observer_list.size() ; i++)
			((OperationObserver)observer_list.elementAt(i)).notifyEnd(exception);
	}

	/**
	 * Notifies when a task is succeeded.
	 * @param arg the argument.
	 */
	protected void notifySucceeded ( Object arg ) {
		for (int i = 0 ; i < observer_list.size() ; i++)
			((OperationObserver)observer_list.elementAt(i)).notifySucceeded(arg);
	}

	/**
	 * Notifies when a task is failed.
	 * @param arg the argument.
	 */
	protected void notifyFailed ( Object arg ) {
		for (int i = 0 ; i < observer_list.size() ; i++)
			((OperationObserver)observer_list.elementAt(i)).notifyFailed(arg);
	}

	/**
	 * Notifies when a task is warned.
	 * @param arg the argument.
	 */
	protected void notifyWarned ( Object arg ) {
		for (int i = 0 ; i < observer_list.size() ; i++)
			((OperationObserver)observer_list.elementAt(i)).notifyWarned(arg);
	}
}
