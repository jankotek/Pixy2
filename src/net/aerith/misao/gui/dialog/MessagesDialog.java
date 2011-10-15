/*
 * @(#)MessagesDialog.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui.dialog;
import java.io.*;
import java.util.Vector;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import net.aerith.misao.util.StringOutputtable;

/**
 * The <code>MessagesDialog</code> represents a dialog with string 
 * messages and a button to save the messages.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class MessagesDialog {
	/**
	 * The header meassage. It can be null.
	 */
	protected String header;

	/**
	 * The content meassages.
	 */
	protected Vector contents;

	/**
	 * The content meassages to save.
	 */
	protected Vector contents_saved;

	/**
	 * The button to save.
	 */
	protected JButton button;

	/**
	 * The maximum number of contents.
	 */
	private final static int max_contents = 10;

	/**
	 * Constructs a <code>MessagesDialog</code>.
	 * @param header   the header message. It can be null.
	 * @param contents the content messages.
	 */
	public MessagesDialog ( String header, Vector contents ) {
		this.header = header;
		this.contents = contents;
		this.contents_saved = contents;
	}

	/**
	 * Sets the content messages to save.
	 * @param contents the content messages to save.
	 */
	public void setContentsToSave ( Vector contents ) {
		contents_saved = contents;
	}

	/**
	 * Shows a dialog.
	 * @param parent   the parent component.
	 */
	public void show ( Component parent ) {
		show(parent, "Message", JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Shows a dialog.
	 * @param parent   the parent component.
	 * @param title    the title.
	 * @param type     the type.
	 */
	public void show ( Component parent, String title, int type ) {
		int contents_size = contents.size();

		// When the number of contents is too big,
		// only some of them are output to the display.
		boolean truncated = false;
		if (contents_size > max_contents) {
			contents_size = max_contents;
			truncated = true;
		}

		int header_size = 1;
		if (header == null)
			header_size = 0;

		Object[] objects = new Object[header_size + contents_size + 1];

		if (header != null)
			objects[0] = header;

		for (int i = 0 ; i < contents_size ; i++) {
			if (truncated  &&  i == contents_size - 1) {
				objects[i + header_size] = new JLabel("    .... truncated.");
			} else {
				Object o = contents.elementAt(i);
				if (o instanceof StringOutputtable)
					objects[i + header_size] = ((StringOutputtable)o).getOutputString();
				else
					objects[i + header_size] = o;
			}
		}

		button = new JButton("Save");
		button.addActionListener(new SaveListener());
		objects[header_size + contents_size] = button;

		JOptionPane.showMessageDialog(parent, objects, title, type);
	}

	/**
	 * The <code>SaveListener</code> is a listener class of menu 
	 * selection to save the content messages.
	 */
	protected class SaveListener implements ActionListener {
		/**
		 * Invoked when the menu is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			try {
				CommonFileChooser file_chooser = new CommonFileChooser();
				file_chooser.setDialogTitle("Save content messages.");
				file_chooser.setMultiSelectionEnabled(false);

				if (file_chooser.showSaveDialog(button) == JFileChooser.APPROVE_OPTION) {
					File file = file_chooser.getSelectedFile();
					if (file.exists()) {
						String message = "Overwrite to " + file.getPath() + " ?";
						if (0 != JOptionPane.showConfirmDialog(button, message, "Confirmation", JOptionPane.YES_NO_OPTION)) {
							return;
						}
					}

					PrintStream stream = new PrintStream(new BufferedOutputStream(new FileOutputStream(file)));

					// Outputs all content messages.
					for (int i = 0 ; i < contents_saved.size() ; i++) {
						Object o = contents_saved.elementAt(i);
						if (o instanceof StringOutputtable)
							stream.println(((StringOutputtable)o).getOutputString());
						else
							stream.println((String)o);
					}

					stream.close();

					String message = "Succeeded to save " + file.getPath();
					JOptionPane.showMessageDialog(button, message);
				}
			} catch ( IOException exception ) {
				String message = "Failed to save file.";
				JOptionPane.showMessageDialog(button, message, "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
