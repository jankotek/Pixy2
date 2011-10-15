/*
 * @(#)FilterSelectionPanel.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.FilterSelection;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.gui.dialog.*;
import net.aerith.misao.image.filter.*;

/**
 * The <code>FilterSelectionPanel</code> represents a panel to select
 * image processing filters.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 September 17
 */

public class FilterSelectionPanel extends JPanel {
	/**
	 * The set of image processing filters.
	 */
	protected FilterSet filter_set;

	/**
	 * The list of available filters.
	 */
	protected JList list_available;

	/**
	 * The list of selected filters.
	 */
	protected JList list_selected;

	/**
	 * The list model of available filters.
	 */
	protected DefaultListModel model_available;

	/**
	 * The list model of selected filters.
	 */
	protected DefaultListModel model_selected;

	/**
	 * The pane of this component.
	 */
	protected Container pane;

	/**
	 * Constructs a <code>FilterSelectionPanel</code>.
	 * @param filter_set the set of image processing filters.
	 */
	public FilterSelectionPanel ( FilterSet filter_set ) {
		this.filter_set = filter_set;

		pane = this;

		setLayout(new BorderLayout());

		model_available = new DefaultListModel();
		model_available.addElement("Smoothing Filter");
		model_available.addElement("Median Filter");
		model_available.addElement("Cancel Streaks");
		model_available.addElement("Cancel Blooming");
		model_available.addElement("Transform Meridian Image");
		model_available.addElement("Inverse White and Black");
		model_available.addElement("Reverse Upside Down");
		model_available.addElement("Rescale ST-4/6 Image");
		model_available.addElement("Flatten Background");
		model_available.addElement("Remove Lattice Pattern");
		model_available.addElement("Fill Dark Rows and Columns");
		model_available.addElement("Fill Illegal Rows and Columns");
		model_available.addElement("Equalize");
		list_available = new JList(model_available);

		JPanel panel_available = new JPanel();
		panel_available.setLayout(new BoxLayout(panel_available, BoxLayout.Y_AXIS));
		panel_available.add(new JLabel("Available filters:"));
		panel_available.add(new JScrollPane(list_available));
		add(panel_available, BorderLayout.WEST);

		model_selected = new DefaultListModel();
		String[] names = filter_set.getFilterNames();
		for (int i = 0 ; i < names.length ; i++)
			model_selected.addElement(names[i]);
		list_selected = new JList(model_selected);

		JPanel panel_selected = new JPanel();
		panel_selected.setLayout(new BoxLayout(panel_selected, BoxLayout.Y_AXIS));
		panel_selected.add(new JLabel("Selected filters:"));
		panel_selected.add(new JScrollPane(list_selected));
		add(panel_selected, BorderLayout.EAST);

		JButton button_select = new JButton(">>");
		button_select.addActionListener(new SelectListener());
		JButton button_deselect = new JButton("<<");
		button_deselect.addActionListener(new DeselectListener());

		JPanel panel_buttons = new JPanel();
		panel_buttons.setLayout(new GridLayout(7, 1));
		panel_buttons.add(new JLabel("        "));
		panel_buttons.add(new JLabel("        "));
		panel_buttons.add(button_select);
		panel_buttons.add(new JLabel("        "));
		panel_buttons.add(button_deselect);
		panel_buttons.add(new JLabel("        "));
		panel_buttons.add(new JLabel("        "));

		JPanel panel_buttons2 = new JPanel();
		panel_buttons2.setLayout(new BorderLayout());
		panel_buttons2.add(new JLabel("  "), BorderLayout.WEST);
		panel_buttons2.add(panel_buttons, BorderLayout.CENTER);
		panel_buttons2.add(new JLabel("  "), BorderLayout.EAST);
		add(panel_buttons2, BorderLayout.CENTER);
	}

	/**
	 * Gets a set of selected image processing filters.
	 * @return a set of selected image processing filters.
	 */
	public FilterSet getFilterSet ( ) {
		return filter_set;
	}

	/**
	 * The <code>SelectListener</code> is a listener class of menu
	 * selection to select some filters.
	 */
	protected class SelectListener implements ActionListener {
		/**
		 * Invoked when one of the radio button menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			Object[] values = list_available.getSelectedValues();

			for (int i = 0 ; i < values.length ; i++) {
				String name = (String)values[i];

				Filter filter = null;

				if (name.equals("Smoothing Filter")) {
					FilterSizeDialog dialog = new FilterSizeDialog("Smoothing Filter");

					int answer = dialog.show(pane);
					if (answer == 0)
						filter = new SmoothFilter(dialog.getFilterSize());
				} else if (name.equals("Median Filter")) {
					FilterSizeDialog dialog = new FilterSizeDialog("Median Filter");

					int answer = dialog.show(pane);
					if (answer == 0)
						filter = new MedianFilter(dialog.getFilterSize());
				} else if (name.equals("Cancel Streaks")) {
					filter = new StreakCancelFilter();
				} else if (name.equals("Cancel Blooming")) {
					filter = new BloomingCancelFilter();
				} else if (name.equals("Transform Meridian Image")) {
					MeridianImageTransformFilterSettingDialog dialog = new MeridianImageTransformFilterSettingDialog();

					int answer = dialog.show(pane);
					if (answer == 0)
						filter = new MeridianImageTransformFilter(dialog.getDeclAtCenter(), dialog.getPixelSizeInArcsec(), dialog.getRAIntervalInSecond());
				} else if (name.equals("Inverse White and Black")) {
					filter = new InverseWhiteAndBlackFilter();
				} else if (name.equals("Reverse Upside Down")) {
					filter = new ReverseFilter(ReverseFilter.VERTICALLY);
				} else if (name.equals("Rescale ST-4/6 Image")) {
					filter = new RescaleSbigFilter();
				} else if (name.equals("Flatten Background")) {
					filter = new FlattenBackgroundFilter();
				} else if (name.equals("Remove Lattice Pattern")) {
					filter = new RemoveLatticePatternFilter();
				} else if (name.equals("Fill Dark Rows and Columns")) {
					FillIllegalRowAndColumnFilter filter2 = new FillIllegalRowAndColumnFilter();
					filter2.setDecreaseEnabled(false);
					filter = filter2;
				} else if (name.equals("Fill Illegal Rows and Columns")) {
					filter = new FillIllegalRowAndColumnFilter();
				} else if (name.equals("Equalize")) {
					filter = new EqualizeFilter();
				}

				if (filter != null) {
					filter_set.add(filter, name);
					model_selected.addElement(name);
				}
			}
		}
	}

	/**
	 * The <code>DeselectListener</code> is a listener class of menu
	 * selection to deselect some filters.
	 */
	protected class DeselectListener implements ActionListener {
		/**
		 * Invoked when one of the radio button menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			int[] indices = list_selected.getSelectedIndices();

			for (int i = indices.length - 1 ; i >= 0  ; i--) {
				filter_set.removeAt(indices[i]);
				model_selected.remove(indices[i]);
			}
		}
	}
}
