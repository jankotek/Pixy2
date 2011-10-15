/*
 * @(#)MagnitudeSystemCodeDialog.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui.dialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import java.net.*;
import java.util.Vector;
import net.aerith.misao.util.*;
import net.aerith.misao.catalog.*;
import net.aerith.misao.gui.*;

/**
 * The <code>MagnitudeSystemCodeDialog</code> represents a dialog to 
 * intput the magnitude system code.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class MagnitudeSystemCodeDialog extends Dialog {
	/**
	 * The panel to input the magnitude system.
	 */
	protected MagnitudeSystemCodePanel mag_system_panel;

	/**
	 * Constructs a <code>MagnitudeSystemCodeDialog</code>.
	 */
	public MagnitudeSystemCodeDialog ( ) {
		components = new Object[1];

		mag_system_panel = new MagnitudeSystemCodePanel();
		mag_system_panel.setBorder(new TitledBorder("Magnitude system"));
		components[0] = mag_system_panel;
	}

	/**
	 * Gets the title of the dialog.
	 * @return the title of the dialog.
	 */
	protected String getTitle ( ) {
		return "Magnitude System Code";
	}

	/**
	 * Gets the code of the magnitude system.
	 * @return the code of the magnitude system.
	 */
	public String getMagSystemCode ( ) {
		return mag_system_panel.getMagSystemCode();
	}

	/**
	 * Sets the code of the magnitude system.
	 * @param code the code of the magnitude system.
	 */
	public void setMagSystemCode ( String code ) {
		mag_system_panel.setMagSystemCode(code);
	}
}
