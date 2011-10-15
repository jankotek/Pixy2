/*
 * @(#)VariabilityPanel.java
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
import net.aerith.misao.xml.*;
import net.aerith.misao.pixy.Resource;

/**
 * The <code>VariabilityPanel</code> represents a panel which consists 
 * of the buttons to show the observation date and chart, etc., and 
 * the table to show the variability information.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class VariabilityPanel extends JPanel {
	/**
	 * The variability.
	 */
	protected Variability variability;

	/**
	 * The parent desktop.
	 */
	protected BaseDesktop desktop;

	/**
	 * The button to show the observation data.
	 */
	protected JButton button_observation;

	/**
	 * The button to show the chart.
	 */
	protected JButton button_chart;

	/**
	 * Constructs a <code>VariabilityPanel</code>.
	 * @param variability the variability.
	 * @param desktop     the parent desktop.
	 */
	public VariabilityPanel ( Variability variability, BaseDesktop desktop ) {
		this.variability = variability;
		this.desktop = desktop;

		setLayout(new BorderLayout());

		button_chart = new JButton("Chart");
		button_observation = new JButton("Observations");
		button_chart.addActionListener(new ChartListener());
		button_observation.addActionListener(new ObservationListener());

		JPanel panel_button = new JPanel();
		panel_button.setLayout(new GridLayout(1, 2));
		panel_button.add(button_chart);
		panel_button.add(button_observation);

		JTable table = new JTable();

		String[] column_names = { "Key", "Value" };
		DefaultTableModel model = new DefaultTableModel(column_names, 0);

		String ra_decl = variability.getStar().getCoor().getOutputString();
		StringTokenizer st = new StringTokenizer(ra_decl);

		Object[] objects = new Object[2];
		objects[0] = "R.A.";
		objects[1] = st.nextToken();
		model.addRow(objects);

		objects = new Object[2];
		objects[0] = "Decl.";
		objects[1] = st.nextToken();
		model.addRow(objects);

		objects = new Object[2];
		objects[0] = "Max Mag";
		objects[1] = ((XmlMag)variability.getBrightestMagnitude().getMag()).getOutputString();
		model.addRow(objects);

		objects = new Object[2];
		objects[0] = "Min Mag";
		objects[1] = ((XmlMag)variability.getFaintestMagnitude().getMag()).getOutputString();
		model.addRow(objects);

		objects = new Object[2];
		objects[0] = "Mag Range";
		objects[1] = Format.formatDouble(variability.getMagnitudeRange(), 5, 2);
		model.addRow(objects);

		objects = new Object[2];
		objects[0] = "Observations";
		objects[1] = String.valueOf(variability.getObservations());
		model.addRow(objects);

		objects = new Object[2];
		objects[0] = "Arc";
		objects[1] = String.valueOf(variability.getArcInDays());
		model.addRow(objects);

		objects = new Object[2];
		objects[0] = "First Date";
		objects[1] = "";
		String s = variability.getFirstDate();
		if (s != null)
			objects[1] = s;
		model.addRow(objects);

		objects = new Object[2];
		objects[0] = "Last Date";
		objects[1] = "";
		s = variability.getLastDate();
		if (s != null)
			objects[1] = s;
		model.addRow(objects);

		table.setModel(model);

		add(panel_button, BorderLayout.NORTH);
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
			desktop.showChart(variability.getStar());
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
			desktop.showObservationTable(variability.getStar(), variability.getMagnitudeRecords());
		}
	}
}
