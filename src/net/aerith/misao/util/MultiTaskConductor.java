/*
 * @(#)MultiTaskConductor.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;
import java.awt.Container;

/**
 * The <code>MultiTaskConductor</code> represents a conductor of multi 
 * task operation on several objects.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public interface MultiTaskConductor {
	/**
	 * Gets the content pane.
	 * @return the pane.
	 */
	public abstract Container getPane ( );

	/**
	 * Returns true if the objects are ready to be operated.
	 * @return true if the objects are ready to be operated.
	 */
	public abstract boolean ready ( );

	/**
	 * Operates the specified operation on several objects.
	 * @param operation the operation.
	 * @exception InterruptedException if the operatoin is stopped.
	 * @exception Exception if an error occurs.
	 */
	public abstract void operate ( MultiTaskOperation operation )
		throws InterruptedException, Exception;
}
