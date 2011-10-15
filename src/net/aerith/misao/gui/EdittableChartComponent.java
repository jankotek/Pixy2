/*
 * @(#)EdittableChartComponent.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui;
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
import net.aerith.misao.gui.dialog.*;
import net.aerith.misao.gui.event.ChartEditionListener;
import net.aerith.misao.catalog.*;
import net.aerith.misao.catalog.io.*;
import net.aerith.misao.database.*;

/**
 * The <code>EdittableChartComponent</code> represents a GUI component 
 * to show the chart where stars can be added.
 * <p>
 * Note that the (x,y) position of specified stars must be set 
 * properly.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class EdittableChartComponent extends ChartComponent {
	/**
	 * The R.A. and Decl. of the center.
	 */
	protected Coor center_coor;

	/**
	 * The horizontal field of view in degree.
	 */
	protected double fov_width;

	/**
	 * The vertical field of view in degree.
	 */
	protected double fov_height;

	/**
	 * The list of chart edition listeners.
	 */
	protected Vector chart_edition_listeners = new Vector();

	/**
	 * The catalog database manager.
	 */
	protected CatalogDBManager db_manager;

	/**
	 * Constructs an empty <code>EdittableChartComponent</code>.
	 * @param size       the size of chart.
	 * @param coor       the R.A. and Decl. of the center.
	 * @param fov_width  the horizontal field of view in degree.
	 * @param fov_height the vertical field of view in degree.
	 */
	public EdittableChartComponent ( Size initial_size, Coor coor, double fov_width, double fov_height ) {
		super(initial_size);

		this.center_coor = coor;
		this.fov_width = fov_width;
		this.fov_height = fov_height;

		popup.addSeparator();

		JMenuItem item = new JMenuItem("Add Catalog");
		item.addActionListener(new AddCatalogListener());
		popup.add(item);

		item = new JMenuItem("Add Stars in Database");
		item.addActionListener(new AddDatabaseListener());
		popup.add(item);
	}

	/**
	 * Adds a chart edition listeners.
	 * @param listener the listener.
	 */
	public void addChartEditionListener ( ChartEditionListener listener ) {
		chart_edition_listeners.addElement(listener);
	}

	/**
	 * Sets the catalog database.
	 * @param manager the catalog database manager.
	 */
	public void setCatalogDBManager ( CatalogDBManager manager ) {
		this.db_manager = manager;
	}

	/**
	 * The <code>AddCatalogListener</code> is a listener class of menu
	 * selection to add another catalog to plot.
	 */
	protected class AddCatalogListener implements ActionListener {
		/**
		 * Invoked when the menu is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			Vector catalog_list = CatalogManager.getIdentificationCatalogReaderList();
			OpenCatalogDialog dialog = new OpenCatalogDialog(catalog_list);

			int answer = dialog.show(pane);
			if (answer == 0) {
				CatalogReader reader = dialog.getSelectedCatalogReader();

				double fov = (fov_width > fov_height ? fov_width : fov_height);
				if (reader.hasFovLimit()  &&  fov >= reader.getFovLimit()) {
					String message = "The field of view must be less than " + reader.getFovLimitMessage() + ".";
					JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}

				if (reader.isDateDependent()) {
					String message = "The catalog is date dependent.";
					JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}

				if (reader.hasDateLimit()) {
					String message = "The date must be within " + reader.getDateLimitMessage() + ".";
					JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}

				String[] paths = net.aerith.misao.util.Format.separatePath(dialog.getCatalogPath());
				for (int i = 0 ; i < paths.length ; i++) {
					try {
						reader.addURL(new File(paths[i]).toURL());
					} catch ( MalformedURLException exception ) {
					}
				}

				try {
					ChartMapFunction cmf = new ChartMapFunction(center_coor, (double)size.getWidth() / fov_width, 0.0);
					Position center_position = new Position((double)size.getWidth() / 2.0, (double)size.getHeight() / 2.0);

					StarList l = new StarList();

					reader.open(center_coor, fov);
					CatalogStar star = reader.readNext();
					while (star != null) {
						star.mapCoordinatesToXY(cmf);

						if (Math.abs(star.getX()) <= size.getWidth() / 2 + 1  &&  Math.abs(star.getY()) <= size.getHeight() / 2 + 1) {
							if (! basepoint_at_center)
								star.add(center_position);

							list.addElement(star);
							l.addElement(star);
						}

						star = reader.readNext();
					}
					reader.close();

					repaint();

					for (int i = 0 ; i < chart_edition_listeners.size() ; i++) {
						ChartEditionListener listener = (ChartEditionListener)chart_edition_listeners.elementAt(i);
						listener.starsAdded(l);
					}

					String message = "Completed.";
					JOptionPane.showMessageDialog(pane, message);
				} catch ( Exception exception ) {
					String message = "Failed to read catalog.";
					JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	/**
	 * The <code>AddDatabaseListener</code> is a listener class of menu
	 * selection to add stars in the database.
	 */
	protected class AddDatabaseListener implements ActionListener {
		/**
		 * Invoked when the menu is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			try {
				CatalogReader reader = null;
				if (db_manager == null)
					reader = new CatalogDBReader(new GlobalDBManager().getCatalogDBManager());
				else
					reader = new CatalogDBReader(db_manager);

				ChartMapFunction cmf = new ChartMapFunction(center_coor, (double)size.getWidth() / fov_width, 0.0);
				Position center_position = new Position((double)size.getWidth() / 2.0, (double)size.getHeight() / 2.0);

				StarList l = new StarList();

				double fov = (fov_width > fov_height ? fov_width : fov_height);
				reader.open(center_coor, fov);
				CatalogStar star = reader.readNext();
				while (star != null) {
					star.mapCoordinatesToXY(cmf);

					if (Math.abs(star.getX()) <= size.getWidth() / 2 + 1  &&  Math.abs(star.getY()) <= size.getHeight() / 2 + 1) {
						if (! basepoint_at_center)
							star.add(center_position);

						list.addElement(star);
						l.addElement(star);
					}

					star = reader.readNext();
				}
				reader.close();

				repaint();

				for (int i = 0 ; i < chart_edition_listeners.size() ; i++) {
					ChartEditionListener listener = (ChartEditionListener)chart_edition_listeners.elementAt(i);
					listener.starsAdded(l);
				}

				String message = "Completed.";
				JOptionPane.showMessageDialog(pane, message);
			} catch ( Exception exception ) {
				String message = "Failed to read database.";
				JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
