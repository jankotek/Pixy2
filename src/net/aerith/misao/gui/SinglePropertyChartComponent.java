/*
 * @(#)SinglePropertyChartComponent.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.gui.dialog.*;
import net.aerith.misao.gui.event.*;
import net.aerith.misao.catalog.CatalogManager;

/**
 * The <code>SinglePropertyChartComponent</code> represents a GUI 
 * component to show a star chart of one sort of catalog star.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 May 10
 */

public class SinglePropertyChartComponent extends ChartComponent {
	/**
	 * The typical catalog star.
	 */
	protected CatalogStar catalog_star;

	/**
	 * Constructs an empty <code>SinglePropertyChartComponent</code>.
	 * @param size         the size of chart.
	 * @param catalog_star the typical catalog star.
	 */
	public SinglePropertyChartComponent ( Size initial_size, CatalogStar catalog_star ) {
		super(initial_size);

		this.catalog_star = catalog_star;

		popup.addSeparator();

		JMenuItem item = new JMenuItem("Property");
		item.addActionListener(new PropertyListener());
		popup.add(item);
	}

	/**
	 * The <code>PropertyListener</code> is a listener class of menu
	 * selection to set the property to plot stars.
	 */
	protected class PropertyListener implements ActionListener {
		/**
		 * Invoked when the menu is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			if (catalog_star == null)
				return;

			try {
				MagnitudeSystemSettingDialog dialog = new MagnitudeSystemSettingDialog(catalog_star);

				int answer = dialog.show(pane);
				if (answer == 0) {
					if (default_property == null)
						default_property = new PlotProperty();

					default_property.setMagnitudeSystem(dialog.getMagnitudeSystem());

					propertyChanged();
				}
			} catch ( UnsupportedMagnitudeSystemException exception ) {
				System.err.println(exception);
			}
		}
	}
}
