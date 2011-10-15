/*
 * @(#)PhotometryCalibrationControlPanel.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.PhotometryCalibration;
import net.aerith.misao.gui.*;
import net.aerith.misao.util.*;

/**
 * The <code>PhotometryCalibrationControlPanel</code> represents a 
 * control panel of reading and writing operations.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2007 March 11
 */

public class PhotometryCalibrationControlPanel extends XmlReportControlPanel {
	/**
	 * Creates a <code>PhotometryCalibrationControlPanel</code>.
	 * @param operation the operation.
	 * @param table     the table.
	 */
	public PhotometryCalibrationControlPanel ( MultiTaskOperation operation, InformationTable table ) {
		super(operation, table);
	}

	/**
	 * Gets the button title to start the operation. This must be 
	 * overrided in the subclasses.
	 * @return the button title to start the operation.
	 */
	public String getStartButtonTitle ( ) {
		return "Photometry";
	}
}
