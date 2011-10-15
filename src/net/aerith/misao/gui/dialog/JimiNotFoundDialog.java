/*
 * @(#)JimiNotFoundDialog.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui.dialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * The <code>JimiNotFoundDialog</code> represents a dialog to show the
 * error message which implies the <tt>JIMI</tt> is not installed.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 July 31
 */

public class JimiNotFoundDialog {
	/**
	 * Constructs a <code>JimiNotFoundDialog</code>.
	 */
	public JimiNotFoundDialog ( ) {
	}

	/**
	 * Shows this dialog.
	 * @param pane the parent pane of this dialog.
	 * @return 0 if <tt>OK</tt> button is pushed, or 2 if <tt>Cancel</tt>
	 * button is pushed.
	 */
	public int show ( Container pane ) {
		String message = "<html><body>";
		message += "<p>";
		message += "The JIMI, Java Image Management Interface, is not installed. ";
		message += "<p>";
		message += "Although it is not necessary, you will be able to open/save<br>";
		message += "JPEG/PNG/BMP files and open GIF/TIFF files with JIMI.";
		message += "</p><p>";
		message += "Please download ";
		message += "<blockquote>";
		message += "<b><font color=\"#00aa33\">JIMI Software Development Kit</font></b>";
		message += "</blockquote>";
		message += "from";
		message += "<blockquote>";
		message += "<b><font color=\"#0000ff\">http://java.sun.com/products/jimi/</font></b>";
		message += "</blockquote>";
		message += "then unpack the package, and add the path of";
		message += "<blockquote>";
		message += "<b><font color=\"#ff00aa\">JimiProClasses.zip</font></b>";
		message += "</blockquote>";
		message += "in the classpath.";
		message += "</p><p>";
		message += "When running on the Windows, run the <b><font color=\"#ff00aa\">pixy2.exe</font></b>,<br>";
		message += "push the <b><font color=\"#0000ff\">Setup</font></b> button and set the path.";
		message += "</p>";
		message += "</body></html>";

		JLabel label = new JLabel(message);
		label.setSize(400,300);
		JOptionPane.showMessageDialog(pane, label);

		return 0;
	}

	/**
	 * Gets the dialog message.
	 * @return the dialog message in string array.
	 */
	public final static String[] getMessages ( ) {
		String[] message = new String[10];
		message[0] = "The JIMI, Java Image Management Interface, is not installed. ";
		message[1] = "Although it is not necessary, you will be able to open/save JPEG/PNG/BMP files and open GIF/TIFF files with JIMI.";
		message[2] = "Please download ";
		message[3] = "    JIMI Software Development Kit";
		message[4] = "from";
		message[5] = "    http://java.sun.com/products/jimi/";
		message[6] = "then unpack the package, and add the path of";
		message[7] = "    JimiProClasses.zip";
		message[8] = "in the classpath.";
		message[9] = "When running on the Windows, run the pixy2.exe, push the Setup button and set the path.";

		return message;
	}
}
