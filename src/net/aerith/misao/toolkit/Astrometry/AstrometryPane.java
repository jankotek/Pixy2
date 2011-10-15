/*
 * @(#)AstrometryPane.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.Astrometry;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.util.*;
import net.aerith.misao.xml.*;
import net.aerith.misao.util.*;

/**
 * The <code>AstrometryPane</code> represents a base pane which shows 
 * the pairs of the detected star and the reference catalog data for 
 * astrometry. It calculates the chart map function and distortion 
 * field using the data checked by the user and applies the result to
 * the XML report document.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class AstrometryPane extends JSplitPane {
	/**
	 * The XML report document.
	 */
	protected XmlReport report;

	/**
	 * The setting of astrometry.
	 */
	protected AstrometrySetting setting;

	/**
	 * The panel to show the astrometry result summary.
	 */
	protected SummaryPanel summary_panel;

	/**
	 * The table to show the data and residual of astrometry.
	 */
	protected ResidualTable residual_table;

	/*
	 * The list of listeners of the XML report document update.
	 */
	protected Vector listeners;

	/**
	 * Constructs an <code>AstrometryPane</code>.
	 * @param report  the XML report document.
	 * @param setting the setting of astrometry.
	 */
	public AstrometryPane ( XmlReport report, AstrometrySetting setting ) {
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
	 * Sets the flag to calculate the distortion field.
	 * @param flag true when to calculate the distortion field.
	 */
	public void setCalculateDistortionField ( boolean flag ) {
		residual_table.setCalculateDistortionField(flag);
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
	 * Applies the result of astrometry to the XML report document.
	 */
	public void applyAstrometry ( ) {
		residual_table.applyAstrometry();

		for (int i = 0 ; i < listeners.size() ; i++) {
			ReportDocumentUpdatedListener listener = (ReportDocumentUpdatedListener)listeners.elementAt(i);
			listener.astrometryUpdated(report);
		}
	}
}
