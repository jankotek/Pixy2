/*
 * @(#)RecordDistancePanel.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.IdentificationReport;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.*;
import net.aerith.misao.gui.event.*;
import net.aerith.misao.xml.*;

/**
 * The <code>RecordDistancePanel</code> represents a panel which
 * consists of the record distance table and the labels on top of the
 * table.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class RecordDistancePanel extends JPanel {
	/**
	 * The table of distance to the other records.
	 */
	protected RecordDistanceTable record_table;

	/**
	 * The label to show the XML star name.
	 */
	protected JLabel xml_star_label;

	/**
	 * The label to show the base record.
	 */
	protected JLabel base_record_label;

	/**
	 * Constructs a <code>RecordDistancePanel</code>.
	 */
	public RecordDistancePanel ( ) {
		record_table = new RecordDistanceTable();

		xml_star_label = new JLabel();
		base_record_label = new JLabel();

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(xml_star_label);
		add(base_record_label);
		add(new JScrollPane(record_table));
	}

	/**
	 * Creates the table of the specified star. The old contents are
	 * removed and the table is renewed.
	 * @param star        the XML star object.
	 * @param base_record the record in the star to calculate distance
	 * to the other records from.
	 */
	public void setStar ( XmlStar star, Star base_record ) {
		record_table.setStar(star, base_record);

		xml_star_label.setText("Identification Data of " + star.getName());

		String name = base_record.getName();
		if (base_record instanceof StarImage)
			name = "the Detected Star";
		base_record_label.setText("Distance from " + name);

		repaint();
	}
}
