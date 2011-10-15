/*
 * @(#)PhotometryPane.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.Photometry;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.util.*;
import net.aerith.misao.xml.*;
import net.aerith.misao.util.*;

/**
 * The <code>PhotometryPane</code> represents a base pane which shows 
 * the pairs of the detected star and the reference catalog data for 
 * photometry. It calculates the magnitude translation formula using
 * the data checked by the user and applies the result to the XML
 * report document.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class PhotometryPane extends JSplitPane {
	/**
	 * The XML report document.
	 */
	protected XmlReport report;

	/**
	 * The setting of photometry.
	 */
	protected PhotometrySetting setting;

	/**
	 * The panel to show the photometry result summary.
	 */
	protected SummaryPanel summary_panel;

	/**
	 * The table to show the data and residual of photometry.
	 */
	protected ResidualTable residual_table;

	/*
	 * The list of listeners of the XML report document update.
	 */
	protected Vector listeners;

	/**
	 * Constructs a <code>PhotometryPane</code>.
	 * @param report  the XML report document.
	 * @param setting the setting of photometry.
	 */
	public PhotometryPane ( XmlReport report, PhotometrySetting setting ) {
		this.report = report;
		this.setting = setting;

		setOrientation(VERTICAL_SPLIT);
		setDividerSize(1);

		summary_panel = new SummaryPanel(this);
		setTopComponent(summary_panel);

		residual_table = new ResidualTable(report, setting, summary_panel);
		setRightComponent(new JScrollPane(residual_table));

		listeners = new Vector();
	}

	/**
	 * Adds the XML report document updated listener.
	 * @param listener the listener.
	 */
	public void addReportDocumentUpdatedListener ( ReportDocumentUpdatedListener listener ) {
		listeners.addElement(listener);
	}

	/**
	 * Updates all contents.
	 */
	public void updateContents ( ) {
		residual_table.updateContents();
	}

	/**
	 * Applies the result of photometry to the XML report document.
	 */
	public void applyPhotometry ( ) {
		residual_table.applyPhotometry();

		for (int i = 0 ; i < listeners.size() ; i++) {
			ReportDocumentUpdatedListener listener = (ReportDocumentUpdatedListener)listeners.elementAt(i);
			listener.photometryUpdated(report);
		}
	}
}
