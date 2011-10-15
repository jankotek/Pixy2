/*
 * @(#)PositionRecordInternalFrame.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.PositionRecord;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.io.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.gui.event.*;
import net.aerith.misao.xml.*;
import net.aerith.misao.database.*;

/**
 * The <code>PositionRecordInternalFrame</code> represents a frame to 
 * show the table of position data records and the mean position.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class PositionRecordInternalFrame extends BaseInternalFrame {
	/**
	 * The label to show the mean position.
	 */
	protected JLabel label_mean_position = new JLabel();

	/**
	 * The container pane.
	 */
	protected Container pane;

	/**
	 * Constructs a <code>PositionRecordInternalFrame</code>.
	 * @param record_list the list of position data records.
	 */
	public PositionRecordInternalFrame ( Vector record_list ) {
		pane = getContentPane();

		PositionRecordTable table = new PositionRecordTable(record_list, this);

		pane.setLayout(new BorderLayout());

		JPanel panel = new JPanel();
		panel.add(label_mean_position);

		pane.add(panel, BorderLayout.NORTH);
		pane.add(new JScrollPane(table), BorderLayout.CENTER);
	}

	/**
	 * Sets the mean R.A. and Decl.
	 * @param coor the mean R.A. and Decl.
	 */
	public void setMeanCoor ( Coor coor ) {
		label_mean_position.setText(coor.getOutputString());
	}
}
