/*
 * @(#)ReadingOperation.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.PhotometryCalibration;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.xml.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.gui.dialog.*;
import net.aerith.misao.toolkit.ReportBatch.ReportBatchOperation;

/**
 * The <code>ReadingOperation</code> represents an operation to select
 * the method for photometry and read data of the selected catalog 
 * from the XML files.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class ReadingOperation extends ReportBatchOperation {
	/**
	 * The dialog to select the catalog and method for photometry.
	 */
	protected PhotometryCatalogSettingDialog dialog;

	/**
	 * The frame.
	 */
	protected PhotometryCalibrationInternalFrame frame;

	/**
	 * The dummy XML report document which contains the pairs of 
	 * detected stars and catalog data used in photometry read from 
	 * all XML report document files.
	 */
	protected XmlReport global_report = null;

	/**
	 * Constructs a <code>ReadingOperation</code>.
	 * @param conductor the conductor of multi task operation.
	 * @param frame     the frame.
	 */
	public ReadingOperation ( MultiTaskConductor conductor, PhotometryCalibrationInternalFrame frame ) {
		this.conductor = conductor;

		this.frame = frame;
	}

	/**
	 * Shows the dialog to set parameters.
	 * @return 0 if <tt>OK</tt> button is pushed, or 2 if <tt>Cancel</tt>
	 * button is pushed.
	 */
	public int showSettingDialog ( ) {
		dialog = new PhotometryCatalogSettingDialog();
		return dialog.show(conductor.getPane());
	}

	/**
	 * Notifies when the operation starts.
	 */
	protected void notifyStart ( ) {
		global_report = null;

		super.notifyStart();
	}

	/**
	 * Notifies when the operation ends.
	 * @param exception the exception if an error occurs, or null if
	 * succeeded.
	 */
	protected void notifyEnd ( Exception exception ) {
		PhotometrySetting setting = dialog.getPhotometrySetting();
		frame.readingOperationCompleted(setting);

		super.notifyEnd(exception);
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

		File xml_file = file_manager.newFile(info.getPath());
		XmlReport report = file_manager.readReport(info);

		info = (XmlInformation)report.getInformation();

		PhotometrySetting setting = dialog.getPhotometrySetting();

		if (setting.getCatalogName() == null  ||  setting.getCatalogName().length() == 0)
			throw new Exception();

		// If the global_report is null, this XML report document is the
		// first file. So here craetes the empty XML report document.
		if (global_report == null) {
			global_report = info.cloneEmptyReport();
			frame.setGlobalReport(global_report);
		}

		XmlData global_data = (XmlData)global_report.getData();

		// Selects only data used in photometry and 
		// adds to the dummy XML report document.
		Vector pair_list = ((XmlData)report.getData()).extractPairs(setting);
		for (int i = 0 ; i < pair_list.size() ; i++) {
			StarPair pair = (StarPair)pair_list.elementAt(i);
			Star star1 = pair.getFirstStar();
			Star star2 = pair.getSecondStar();

			if (star1 != null  &&  star2 != null) {
				XmlStar s = new XmlStar();
				s.setName("STR", 0);
				s.addStar(star1);
				s.addStar(star2);
				global_data.addStar(s);
			}
		}
	}
}
