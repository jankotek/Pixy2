/*
 * @(#)ImageConversionFrame.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.ImageConversion;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.io.filechooser.*;

/**
 * The <code>ImageConversionFrame</code> represents a frame to convert
 * the format of and transform the selected images.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 17
 */

public class ImageConversionFrame extends BaseFrame {
	/*
	 * The table.
	 */
	protected ImageConversionTable table;

	/*
	 * The operation.
	 */
	 protected ImageConversionOperation operation;

	/*
	 * The control panel.
	 */
	protected ImageConversionControlPanel control_panel;

	/**
	 * The content pane of this frame.
	 */
	protected Container pane;

	/**
	 * Constructs an <code>ImageConversionFrame</code>.
	 */
	public ImageConversionFrame ( ) {
		pane = getContentPane();

		table = new ImageConversionTable();
		operation = new ImageConversionOperation(table);
		control_panel = new ImageConversionControlPanel(operation, table);

		pane.setLayout(new BorderLayout());

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(2, 1));
		panel.add(control_panel);
		panel.add(new JLabel("Drag image files and drop on the table."));

		pane.add(panel, BorderLayout.NORTH);
		pane.add(new JScrollPane(table), BorderLayout.CENTER);

		initMenu();
	}

	/**
	 * Initializes this window. This is invoked at construction.
	 */
	public void initialize ( ) {
		// Never invoke initMenu() here. It must be invoked after the 
		// control panel is created in the constructor of this class.
//		initMenu();
	}

	/**
	 * Initializes menu bar. A <code>JMenuBar</code> must be set to 
	 * this <code>JFrame</code> previously.
	 */
	public void initMenu ( ) {
		addFileMenu();

		addOperationMenu();

		super.initMenu();
	}

	/**
	 * Adds the <tt></tt> menus to the menu bar.
	 */
	public void addFileMenu ( ) {
		JMenu menu = addMenu("File");

		JMenuItem menu_config = new JMenuItem("Configuration");
		menu_config.addActionListener(new ConfigurationListener());
		menu.add(menu_config);

		menu.addSeparator();

		JMenuItem item = new JMenuItem("Close");
		item.addActionListener(new CloseListener());
		menu.add(item);
	}

	/**
	 * Adds the <tt>Operaton</tt> menus to the menu bar.
	 */
	public void addOperationMenu ( ) {
		JMenu menu = addMenu("Operation");

		JMenuItem[] menu_items = control_panel.getMenuItems();
		for (int i = 0 ; i < menu_items.length ; i++)
			menu.add(menu_items[i]);
	}

	/**
	 * Sets the operation.
	 * @param operation the operation.
	 */
	protected void setOperation ( ImageConversionOperation operation ) {
		this.operation = operation;

		control_panel.setOperation(operation);
	}

	/**
	 * Starts the operation.
	 */
	public void startOperation ( ) {
		control_panel.start();
	}

	/**
	 * The <code>ConfigurationListener</code> is a listener class of 
	 * menu selection to configure.
	 */
	protected class ConfigurationListener implements ActionListener {
		/**
		 * Invoked when one of the menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			if (control_panel.getCurrentMode() == ControlPanel.MODE_SETTING) {
				ConversionDialog dialog = new ConversionDialog();

				int answer = dialog.show(pane);

				if (answer == 0) {
					if (dialog.specifiesInputFormat()) {
						table.setInputImageFileFilter(dialog.getInputFileFilter());
					}

					if (dialog.specifiesOutputFormat()) {
						table.setOutputImageFileFilter(dialog.getOutputFileFilter(), dialog.changesFilenames());
					}

					if (dialog.transformsAll()) {
						if (dialog.isSize()) {
							table.setOutputImageSize(dialog.getImageSize(), dialog.rescalesSbig());
						} else {
							table.setOutputImageScale(dialog.getScale(), dialog.rescalesSbig());
						}
					}

					if (dialog.appliesImageProcessingFilters()) {
						table.setImageProcessingFilter(dialog.getFilterSet());
					}
				}
			}
		}
	}

	/**
	 * The <code>CloseListener</code> is a listener class of menu 
	 * selection to close.
	 */
	protected class CloseListener implements ActionListener {
		/**
		 * Invoked when one of the menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			dispose();
		}
	}
}
