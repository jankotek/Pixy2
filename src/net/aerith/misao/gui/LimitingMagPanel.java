/*
 * @(#)LimitingMagPanel.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Hashtable;
import net.aerith.misao.util.*;

/**
 * The <code>LimitingMagPanel</code> represents a panel to specify the
 * range of the limiting magnitude.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2002 January 18
 */

public class LimitingMagPanel extends JPanel {
	/**
	 * The default values.
	 */
	protected static Hashtable default_values = new Hashtable();

	/**
	 * The text field to input the brighter limit.
	 */
	protected JTextField text_brighter;

	/**
	 * The text field to input the fainter limit.
	 */
	protected JTextField text_fainter;

	/**
	 * Constructs a <code>LimitingMagPanel</code>.
	 */
	public LimitingMagPanel ( ) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		text_brighter = new JTextField("10.0");
		text_fainter = new JTextField("20.0");

		text_brighter.setColumns(8);
		text_fainter.setColumns(8);

		JPanel panel = new JPanel();
		panel.add(new JLabel("Limiting mag between"));
		panel.add(text_brighter);
		panel.add(new JLabel("and"));
		panel.add(text_fainter);
		panel.add(new JLabel("mag."));
		add(panel);

		setDefaultValues();
	}

	/**
	 * Sets the default values.
	 */
	protected void setDefaultValues ( ) {
		if (default_values.get("brighter") != null)
			text_brighter.setText((String)default_values.get("brighter"));
		if (default_values.get("fainter") != null)
			text_fainter.setText((String)default_values.get("fainter"));
	}

	/**
	 * Saves the default values.
	 */
	public void saveDefaultValues ( ) {
		default_values.put("brighter", text_brighter.getText());
		default_values.put("fainter", text_fainter.getText());
	}

	/**
	 * Gets the brighter limit.
	 * @return the brighter limit.
	 */
	public double getBrighterLimit ( ) {
		return Format.doubleValueOf(text_brighter.getText());
	}

	/**
	 * Gets the fainter limit.
	 * @return the fainter limit.
	 */
	public double getFainterLimit ( ) {
		return Format.doubleValueOf(text_fainter.getText());
	}
}
