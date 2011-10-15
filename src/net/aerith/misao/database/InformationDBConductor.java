/*
 * @(#)InformationDBConductor.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.database;
import java.awt.*;
import javax.swing.*;
import net.aerith.misao.util.*;
import net.aerith.misao.xml.*;

/**
 * The <code>InformationDBConductor</code> represents a conductor of 
 * multi task operation on XML report documents in the database.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class InformationDBConductor implements MultiTaskConductor {
	/**
	 * The database manager.
	 */
	protected GlobalDBManager db_manager;

	/**
	 * The content pane.
	 */
	protected Container pane;

	/**
	 * Constructs an <code>InformationDBConductor</code>.
	 * @param db_manager the database manager.
	 * @param pane       the content pane.
	 */
	public InformationDBConductor ( GlobalDBManager db_manager, Container pane ) {
		this.db_manager = db_manager;
		this.pane = pane;
	}

	/**
	 * Gets the content pane.
	 * @return the pane.
	 */
	public Container getPane ( ) {
		return pane;
	}

	/**
	 * Returns true if the objects are ready to be operated.
	 * @return true if the objects are ready to be operated.
	 */
	public boolean ready ( ) {
		return true;
	}

	/**
	 * Operates the specified operation on several objects.
	 * @param operation the operation.
	 * @exception InterruptedException if the operatoin is stopped.
	 * @exception Exception if an error occurs.
	 */
	public void operate ( MultiTaskOperation operation )
		throws InterruptedException, Exception
	{
		InformationDBAccessor accessor = db_manager.getInformationDBManager().getAccessor();

		Exception operating_exception = null;

		XmlInformation info = accessor.getFirstElement();
		while (info != null) {
			if (operation != null  &&  operation.isStopped()) {
				throw new InterruptedException();
			}

			System.gc();
			Thread.sleep(1000);

			try {
				operation.operate(info);
			} catch ( Exception exception ) {
				operating_exception = exception;
			}

			info = accessor.getNextElement();
		}

		if (operating_exception != null)
			throw operating_exception;
	}
}
