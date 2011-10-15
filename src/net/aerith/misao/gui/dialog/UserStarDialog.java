/*
 * @(#)UserStarDialog.java
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
import net.aerith.misao.gui.*;
import net.aerith.misao.catalog.star.UserStar;

/**
 * The <code>UserStarDialog</code> represents a dialog to set the name, 
 * R.A. and Decl., and the magnitude of a user's star.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class UserStarDialog extends Dialog {
	/**
	 * The text field to input the name.
	 */
	protected JTextField text_name;

	/**
	 * The panel to input R.A. and Decl.
	 */
	protected CoorPanel coor_panel;

	/**
	 * The text field to input the magnitude.
	 */
	protected JTextField text_mag;

	/**
	 * Constructs a <code>UserStarDialog</code>.
	 * @param messages the messages to show.
	 */
	public UserStarDialog ( String[] messages ) {
		components = new Object[4];

		JPanel message_panel = new JPanel();
		message_panel.setLayout(new BoxLayout(message_panel, BoxLayout.Y_AXIS));
		for (int i = 0 ; i < messages.length ; i++)
			message_panel.add(new JLabel(messages[i]));
		components[0] = message_panel;

		JPanel name_panel = new JPanel();
		text_name = new JTextField("");
		text_name.setColumns(40);
		name_panel.add(text_name);
		name_panel.setBorder(new TitledBorder("Name"));
		components[1] = name_panel;

		coor_panel = new CoorPanel();
		coor_panel.setBorder(new TitledBorder("R.A. and Decl."));
		components[2] = coor_panel;

		JPanel mag_panel = new JPanel();
		text_mag = new JTextField("0.0");
		text_mag.setColumns(10);
		mag_panel.add(text_mag);
		mag_panel.setBorder(new TitledBorder("Mag"));
		components[3] = mag_panel;

		setDefaultValues();
	}

	/**
	 * Gets the title of the dialog.
	 * @return the title of the dialog.
	 */
	protected String getTitle ( ) {
		return "User's Star Setting";
	}

	/**
	 * Sets the default values.
	 */
	protected void setDefaultValues ( ) {
		if (default_values.get("users-star-name") != null)
			text_name.setText((String)default_values.get("users-star-name"));
		if (default_values.get("users-star-mag") != null)
			text_mag.setText((String)default_values.get("users-star-mag"));
	}

	/**
	 * Saves the default values.
	 */
	protected void saveDefaultValues ( ) {
		coor_panel.saveDefaultValues();

		default_values.put("users-star-name", text_name.getText());
		default_values.put("users-star-mag", text_mag.getText());
	}

	/**
	 * Gets the user's star.
	 * @return the user's star.
	 */
	public UserStar getStar ( ) {
		String name = text_name.getText();
		double mag = Format.doubleValueOf(text_mag.getText());

		return new UserStar(name, coor_panel.getCoor(), mag);
	}
}
