/*
 * @(#)MultiTaskOperation.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;
import java.awt.*;
import java.io.*;
import java.util.*;

/**
 * The <code>MultiTaskOperation</code> represents a multi task 
 * operation on several objects.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public abstract class MultiTaskOperation extends Operation {
	/**
	 * The conductor of multi task operation.
	 */
	protected MultiTaskConductor conductor;

	/**
	 * Shows the dialog to set parameters.
	 * @return 0 if <tt>OK</tt> button is pushed, or 2 if <tt>Cancel</tt>
	 * button is pushed.
	 */
	public abstract int showSettingDialog ( );

	/**
	 * Returns true if the operation is ready to start.
	 * @return true if the operation is ready to start.
	 */
	public boolean ready ( ) {
		if (conductor.ready()) {
			int answer = showSettingDialog();
			if (answer == 0)
				return true;
		}

		return false;
	}

	/**
	 * Operates.
	 * @exception Exception if an error occurs.
	 */
	protected void operate ( )
		throws Exception
	{
		conductor.operate(this);
	}

	/**
	 * Operates on one item. This is invoked from the conductor of 
	 * multi task operation.
	 * @param object the target object to operate.
	 * @exception Exception if an error occurs.
	 */
	public abstract void operate ( Object object )
		throws Exception;
}
