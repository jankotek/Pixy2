/*
 * @(#)ImageInformationTable.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.ImageInformation;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import java.io.*;
import net.aerith.misao.util.*;
import net.aerith.misao.io.FileManager;
import net.aerith.misao.io.filechooser.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.gui.dialog.*;
import net.aerith.misao.xml.*;

/**
 * The <code>ImageInformationTable</code> represents a table which 
 * shows the image information.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class ImageInformationTable extends JTable {
	/**
	 * The popup menu.
	 */
	protected JPopupMenu popup;

	/**
	 * The container pane.
	 */
	protected Container pane;

	/**
	 * Constructs an <code>ImageInformationTable</code> of the 
	 * specified image information.
	 * @param info the image information.
	 */
	public ImageInformationTable ( XmlInformation info ) {
		updateContents(info);

		pane = this;

		initPopupMenu();
	}

	/**
	 * Updates the contents.
	 * @param info the image information.
	 */
	public void updateContents ( XmlInformation info ) {
		String[] column_names = { "Key", "Value" };
		DefaultTableModel model = new DefaultTableModel(column_names, 0);

		KeyAndValue[] key_and_values = info.getKeyAndValues();
		for (int i = 0 ; i < key_and_values.length ; i++) {
			Object[] objects = new Object[2];
			objects[0] = key_and_values[i].getKey();
			objects[1] = key_and_values[i].getValue();
			model.addRow(objects);
		}

		setModel(model);
		sizeColumnsToFit(-1);

		repaint();
	}

	/**
	 * Initializes a popup menu. A <tt>popup</tt> must be created 
	 * previously.
	 */
	protected void initPopupMenu ( ) {
		popup = new JPopupMenu();

		JMenuItem item = new JMenuItem("Save");
		item.addActionListener(new SaveListener());
		popup.add(item);

		enableEvents(AWTEvent.MOUSE_EVENT_MASK);
	}

	/**
	 * Invoked when mouse event occurs. It is to show a click data
	 * dialog or popup menu.
	 * @param e contains the click position.
	 */
	protected void processMouseEvent ( MouseEvent e ) {
		if (e.isPopupTrigger()) {
			popup.show(e.getComponent(), e.getX(), e.getY());
		}
		super.processMouseEvent(e);
	}

	/**
	 * The <code>SaveListener</code> is a listener class of menu 
	 * selection to save the contents in a file.
	 */
	protected class SaveListener implements ActionListener {
		/**
		 * Invoked when the menu is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			try {
				CommonFileChooser file_chooser = new CommonFileChooser();
				file_chooser.setDialogTitle("Save contents.");
				file_chooser.setMultiSelectionEnabled(false);

				if (file_chooser.showSaveDialog(pane) == JFileChooser.APPROVE_OPTION) {
					File file = file_chooser.getSelectedFile();
					if (file.exists()) {
						String message = "Overwrite to " + file.getPath() + " ?";
						if (0 != JOptionPane.showConfirmDialog(pane, message, "Confirmation", JOptionPane.YES_NO_OPTION)) {
							return;
						}
					}

					PrintStream stream = new PrintStream(new BufferedOutputStream(new FileOutputStream(file)));

					DefaultTableModel model = (DefaultTableModel)getModel();

					int rows = model.getRowCount();

					for (int i = 0 ; i < rows ; i++) {
						String key = (String)model.getValueAt(i, 0);
						String value = (String)model.getValueAt(i, 1);

						if (key.length() >= 16)
							stream.println(key + "\t" + value);
						else if (key.length() >= 8)
							stream.println(key + "\t\t" + value);
						else
							stream.println(key + "\t\t\t" + value);
					}

					stream.close();

					String message = "Succeeded to save " + file.getPath();
					JOptionPane.showMessageDialog(pane, message);
				}
			} catch ( IOException exception ) {
				String message = "Failed to save file.";
				JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
