/*
 * @(#)ImageConversionControlPanel.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.ImageConversion;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.Vector;
import javax.swing.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.gui.dialog.*;
import net.aerith.misao.io.filechooser.*;
import net.aerith.misao.image.UnsupportedFileTypeException;
import net.aerith.misao.image.io.Fits;

/**
 * The <code>ImageConversionControlPanel</code> represents a control
 * panel to convert the format of and transform the selected images.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class ImageConversionControlPanel extends ControlPanel {
	/**
	 * The table.
	 */
	protected ImageConversionTable table;

	/**
	 * Creates an <code>ImageConversionControlPanel</code>.
	 * @param operation the operation.
	 * @param table     the table.
	 */
	public ImageConversionControlPanel ( ImageConversionOperation operation, ImageConversionTable table ) {
		super(operation);

		this.table = table;
	}

	/**
	 * Gets the button title to set parameters. This must be overrided
	 * in the subclasses.
	 * @return the button title to set parameters.
	 */
	public String getSetButtonTitle ( ) {
		return "Add Image";
	}

	/**
	 * Gets the button title to start the operation. This must be 
	 * overrided in the subclasses.
	 * @return the button title to start the operation.
	 */
	public String getStartButtonTitle ( ) {
		return "Convert";
	}

	/**
	 * Gets the button title to reset. This must be overrided in the
	 * subclasses.
	 * @return the button title to reset.
	 */
	public String getResetButtonTitle ( ) {
		return "Set Ready";
	}

	/**
	 * Invoked when the set button is pushed. This must be overrided 
	 * in the subclasses.
	 * @param e contains the selected menu item.
	 */
	public void actionPerformedSet ( ActionEvent e ) {
		ImageFileOpenChooser file_chooser = new ImageFileOpenChooser();
		file_chooser.setDialogTitle("Open an image file.");
		file_chooser.setMultiSelectionEnabled(false);

		if (file_chooser.showOpenDialog(pane) == JFileChooser.APPROVE_OPTION) {
			try {
				File file = file_chooser.getSelectedFile();
				net.aerith.misao.image.io.Format format = null;
				try {
					format = file_chooser.getSelectedFileFormat();
				} catch ( UnsupportedFileTypeException exception ) {
					format = new Fits(file.toURL());
				}

				table.addImage(file, format);
			} catch ( MalformedURLException exception ) {
			} catch ( IOException exception ) {
			}
		}
	}

	/**
	 * Invoked when the reset button is pushed. This must be overrided 
	 * in the subclasses.
	 * @param e contains the selected menu item.
	 */
	public void actionPerformedReset ( ActionEvent e ) {
		table.setReady();
	}
}
