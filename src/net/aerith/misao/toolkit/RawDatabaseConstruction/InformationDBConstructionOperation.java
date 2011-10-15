/*
 * @(#)InformationDBConstructionOperation.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.RawDatabaseConstruction;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.xml.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.database.*;
import net.aerith.misao.toolkit.ReportBatch.ReportBatchOperation;

/**
 * The <code>InformationDBConstructionOperation</code> represents an 
 * operation to construct an image information database.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2007 July 8
 */

public class InformationDBConstructionOperation extends ReportBatchOperation {
	/**
	 * The frame.
	 */
	protected RawDatabaseConstructionInternalFrame frame;

	/**
	 * Constructs an <code>InformationDBConstructionOperation</code>.
	 * @param conductor the conductor of multi task operation.
	 * @param frame     the frame.
	 */
	public InformationDBConstructionOperation ( MultiTaskConductor conductor, RawDatabaseConstructionInternalFrame frame ) {
		this.conductor = conductor;

		this.frame = frame;
	}

	/**
	 * Shows the dialog to set parameters.
	 * @return 0 if <tt>OK</tt> button is pushed, or 2 if <tt>Cancel</tt>
	 * button is pushed.
	 */
	public int showSettingDialog ( ) {
		return frame.showSettingDialog();
	}

	/**
	 * Operates on one item. This is invoked from the conductor of 
	 * multi task operation.
	 * @param object the target object to operate.
	 * @exception Exception if an error occurs.
	 */
	public void operate ( Object object )
		throws Exception
	{
		XmlInformation info = (XmlInformation)object;

		monitor_set.addMessage(info.getPath());

		File file = file_manager.newFile(info.getPath());
		getDBManager().addInformation(file, info);

		monitor_set.addMessage("...added to the image database.");
	}
}
