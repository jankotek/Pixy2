/*
 * @(#)StarPanel.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.gui.event.*;
import net.aerith.misao.pixy.Resource;

/**
 * The <code>StarPanel</code> represents a panel which consists of 
 * the star name, buttons to show the chart and magnitude data, and 
 * the table of star data.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class StarPanel extends JPanel {
	/**
	 * The star.
	 */
	protected Star star;

	/**
	 * The parent desktop.
	 */
	protected BaseDesktop desktop;

	/**
	 * The button to show the chart.
	 */
	protected JButton button_chart;

	/**
	 * The button to show the observation table.
	 */
	protected JButton button_observation;

	/**
	 * Constructs a <code>StarPanel</code>.
	 * @param star    the star.
	 * @param desktop the parent desktop.
	 */
	public StarPanel ( Star star, BaseDesktop desktop ) {
		this.star = star;
		this.desktop = desktop;

		setLayout(new BorderLayout());

		JLabel label_title = new JLabel(star.getName());

		button_chart = new JButton("Chart");
		button_observation = new JButton("Observations");
		button_chart.addActionListener(new ChartListener());
		button_observation.addActionListener(new ObservationListener());

		JPanel panel_button = new JPanel();
		panel_button.setLayout(new GridLayout(2, 1));
		panel_button.add(button_chart);
		panel_button.add(button_observation);

		JPanel panel_title = new JPanel();
		panel_title.setLayout(new BorderLayout());
		panel_title.add(label_title, BorderLayout.CENTER);
		panel_title.add(panel_button, BorderLayout.EAST);

		JTable table = new JTable();

		String[] column_names = { "Key", "Value" };
		DefaultTableModel model = new DefaultTableModel(column_names, 0);

		Object[] objects = new Object[2];
		objects[0] = "Star";
		objects[1] = star.getName();
		model.addRow(objects);

		String ra_decl = star.getCoorString();
		StringTokenizer st = new StringTokenizer(ra_decl);

		objects = new Object[2];
		objects[0] = "R.A.";
		objects[1] = st.nextToken();
		model.addRow(objects);

		objects = new Object[2];
		objects[0] = "Decl.";
		objects[1] = st.nextToken();
		model.addRow(objects);

		if (star instanceof CatalogStar) {
			objects = new Object[2];
			objects[0] = "Catalog";
			objects[1] = ((CatalogStar)star).getCatalogName();
			model.addRow(objects);
		}

		KeyAndValue[] key_and_values = star.getKeyAndValues();
		for (int i = 0 ; i < key_and_values.length ; i++) {
			objects = new Object[2];
			objects[0] = key_and_values[i].getKey();
			objects[1] = key_and_values[i].getValue();
			model.addRow(objects);
		}

		table.setModel(model);

		add(panel_title, BorderLayout.NORTH);
		add(new JScrollPane(table), BorderLayout.CENTER);
	}

	/**
	 * The <code>ChartListener</code> is a listener class of menu 
	 * selection to show the chart.
	 */
	protected class ChartListener implements ActionListener, Runnable {
		/**
		 * Invoked when one of the menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			Thread thread = new Thread(this);
			thread.setPriority(Resource.getThreadPriority());
			thread.start();
		}

		/**
		 * Runs this thread.
		 */
		public void run ( ) {
			desktop.showChart((CatalogStar)star);
		}
	}

	/**
	 * The <code>ObservationListener</code> is a listener class of
	 * menu selection to show the observation table.
	 */
	protected class ObservationListener implements ActionListener, Runnable {
		/**
		 * Invoked when one of the menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			Thread thread = new Thread(this);
			thread.setPriority(Resource.getThreadPriority());
			thread.start();
		}

		/**
		 * Runs this thread.
		 */
		public void run ( ) {
			desktop.showObservationTable((CatalogStar)star);
		}
	}
}
