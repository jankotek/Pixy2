/*
 * @(#)JaxpNotFoundDialog.java
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
 * The <code>JaxpNotFoundDialog</code> represents a dialog to show the
 * error message which implies the <tt>jaxp.jar</tt> or <tt>parser.jar</tt>
 * are not found.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2005 January 2
 */

public class JaxpNotFoundDialog {
	/**
	 * Constructs a <code>JaxpNotFoundDialog</code>.
	 */
	public JaxpNotFoundDialog ( ) {
	}

	/**
	 * Shows this dialog.
	 * @param pane the parent pane of this dialog.
	 * @return 0 if <tt>OK</tt> button is pushed, or 2 if <tt>Cancel</tt>
	 * button is pushed.
	 */
	public int show ( Container pane ) {
		String ext_path = System.getProperty("java.ext.dirs");

		String message = "<html><body>";
		message += "<p>";
		message += "The JAXP, Java API for XML Parsing, is not installed. ";
		message += "<p>";
		message += "Please install the Java 2 Platform, Standard Edition (J2SE) Version 1.3 or later from ";
		message += "<blockquote>";
		message += "<b><font color=\"#0000ff\">http://java.sun.com/j2se/index.jsp</font></b>";
		message += "</blockquote>";
		message += "</p><p>";
		message += "If you keep using J2SE 1.2.2, please download ";
		message += "<blockquote>";
		message += "<b><font color=\"#00aa33\">Java API for XML Parsing Reference Implementation Version 1.0.1</font></b>";
		message += "</blockquote>";
		message += "from";
		message += "<blockquote>";
		message += "<b><font color=\"#0000ff\">http://java.sun.com/xml/download.html</font></b>";
		message += "</blockquote>";
		message += "then unpack the package, and copy ";
		message += "<blockquote>";
		message += "<b><font color=\"#ff00aa\">jaxp.jar</font></b> and ";
		message += "<b><font color=\"#ff00aa\">parser.jar</font></b>";
		message += "</blockquote>";
		message += "in the ";
		message += "<blockquote>";
		message += "<b><font color=\"#0000ff\">" + ext_path + "</font></b>";
		message += "</blockquote>";
		message += "directory. ";
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
		String ext_path = System.getProperty("java.ext.dirs");

		String[] message = new String[12];
		message[0] = "The JAXP, Java API for XML Parsing, is not installed. ";
		message[1] = "Please install the Java 2 Platform, Standard Edition (J2SE) Version 1.3 or later from ";
		message[2] = "    http://java.sun.com/j2se/index.jsp";
		message[3] = "If you keep using J2SE 1.2.2, please download ";
		message[4] = "    Java API for XML Parsing Reference Implementation Version 1.0.1";
		message[5] = "from";
		message[6] = "    http://java.sun.com/xml/download.html";
		message[7] = "then unpack the package, and copy ";
		message[8] = "    jaxp.jar and parser.jar";
		message[9] = "in the ";
		message[10] = "    " + ext_path;
		message[11] = "directory. ";

		return message;
	}
}
