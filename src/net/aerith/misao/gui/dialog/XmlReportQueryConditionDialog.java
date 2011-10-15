/*
 * @(#)XmlReportQueryConditionDialog.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui.dialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import java.net.*;
import java.util.Vector;
import net.aerith.misao.util.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.database.XmlReportQueryCondition;

/**
 * The <code>XmlReportQueryConditionDialog</code> represents a dialog 
 * to set up a query conditions to select XML report documents.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class XmlReportQueryConditionDialog extends Dialog {
	/**
	 * The panel to specify the range of the limiting magnitude.
	 */
	protected LimitingMagPanel limiting_mag_panel;

	/**
	 * Constructs a <code>XmlReportQueryConditionDialog</code>.
	 */
	public XmlReportQueryConditionDialog ( ) {
		components = new Object[1];

		limiting_mag_panel = new LimitingMagPanel();
		components[0] = limiting_mag_panel;

		setDefaultValues();
	}

	/**
	 * Gets the title of the dialog.
	 * @return the title of the dialog.
	 */
	protected String getTitle ( ) {
		return "XML Report Document Query Condition";
	}

	/**
	 * Sets the default values.
	 */
	protected void setDefaultValues ( ) {
	}

	/**
	 * Saves the default values.
	 */
	protected void saveDefaultValues ( ) {
		limiting_mag_panel.saveDefaultValues();
	}

	/**
	 * Gets the query condition.
	 * @return the query condition.
	 */
	public XmlReportQueryCondition getQueryCondition ( ) {
		XmlReportQueryCondition query_condition = new XmlReportQueryCondition();
		query_condition.setLimitingMagnitude(limiting_mag_panel.getBrighterLimit(), limiting_mag_panel.getFainterLimit());
		return query_condition;
	}
}
