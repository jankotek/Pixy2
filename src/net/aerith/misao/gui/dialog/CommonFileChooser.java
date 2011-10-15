/*
 * @(#)CommonFileChooser.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui.dialog;
import java.io.*;
import java.net.*;
import java.awt.*;
import javax.swing.*;

/**
 * The <code>CommonFileChooser</code> represents a file chooser dialog
 * which keeps the current directory.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 November 26
 */

public class CommonFileChooser extends JFileChooser {
	/**
	 * The current directory of file chooser dialog.
	 */
	private static File current_directory = net.aerith.misao.pixy.Properties.getHome();

	/**
	 * Constructs a <code>CommonFileChooser</code>.
	 */
	public CommonFileChooser ( ) {
		super(current_directory.getPath());
	}

	/**
	 * Shows the dialog to open or save. When selecting the file, the
	 * current directory is changed.
	 * @param  parent  the parent component of the dialog.
	 * @param  approveButtonText the text of the <code>ApproveButton</code>
	 * @return the return state of the file chooser on popdown:
     */
	public int showDialog (Component parent, String approveButtonText ) {
		int answer = super.showDialog(parent, approveButtonText);

		if (answer == JFileChooser.APPROVE_OPTION) {
			File file = getSelectedFile();
			file = new File(file.getAbsolutePath());
			current_directory = file.getParentFile();

			if (current_directory == null)
				current_directory = net.aerith.misao.pixy.Properties.getHome();
		}

		return answer;
	}
}
