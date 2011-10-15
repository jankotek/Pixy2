/*
 * @(#)CoorPanel.java
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
 * The <code>CoorPanel</code> represents a panel which consists of 
 * components to input R.A. and Decl.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 October 3
 */

public class CoorPanel extends JPanel {
	/**
	 * The default values.
	 */
	protected static Hashtable default_values = new Hashtable();

	/**
	 * The text field to input hour of R.A.
	 */
	protected JTextField text_RA_h;

	/**
	 * The text field to input minute of R.A.
	 */
	protected JTextField text_RA_m;

	/**
	 * The text field to input second of R.A.
	 */
	protected JTextField text_RA_s;

	/**
	 * The combo box to select sign of Decl.
	 */
	protected JComboBox combo_sign;

	/**
	 * The text field to input degree of Decl.
	 */
	protected JTextField text_Decl_d;

	/**
	 * The text field to input arcmin of Decl.
	 */
	protected JTextField text_Decl_m;

	/**
	 * The text field to input arcsec of Decl.
	 */
	protected JTextField text_Decl_s;

	/**
	 * Constructs a <code>CoorPanel</code>.
	 */
	public CoorPanel ( ) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		text_RA_h = new JTextField("00");
		text_RA_m = new JTextField("00");
		text_RA_s = new JTextField("00.00");
		text_Decl_d = new JTextField("00");
		text_Decl_m = new JTextField("00");
		text_Decl_s = new JTextField("00.0");

		text_RA_h.setColumns(4);
		text_RA_m.setColumns(4);
		text_RA_s.setColumns(8);
		text_Decl_d.setColumns(4);
		text_Decl_m.setColumns(4);
		text_Decl_s.setColumns(8);

		JPanel panel = new JPanel();
		panel.add(new JLabel("R.A."));
		panel.add(text_RA_h);
		panel.add(new JLabel("h"));
		panel.add(text_RA_m);
		panel.add(new JLabel("m"));
		panel.add(text_RA_s);
		panel.add(new JLabel("s"));
		add(panel);

		String[] signs = { "+", "-" };
		combo_sign = new JComboBox(signs);

		panel = new JPanel();
		panel.add(new JLabel("Decl."));
		panel.add(combo_sign);
		panel.add(text_Decl_d);
		panel.add(new JLabel("o"));
		panel.add(text_Decl_m);
		panel.add(new JLabel("'"));
		panel.add(text_Decl_s);
		panel.add(new JLabel("\""));
		add(panel);

		setDefaultValues();
	}

	/**
	 * Sets the default values.
	 */
	protected void setDefaultValues ( ) {
		if (default_values.get("coor") != null)
			setCoor((Coor)default_values.get("coor"));
	}

	/**
	 * Saves the default values.
	 */
	public void saveDefaultValues ( ) {
		default_values.put("coor", getCoor());
	}

	/**
	 * Sets whether or not this component is enabled.
	 * @param enabled true when to enable this panel.
	 */
	public void setEnabled ( boolean enabled ) {
		super.setEnabled(enabled);

		text_RA_h.setEnabled(enabled);
		text_RA_m.setEnabled(enabled);
		text_RA_s.setEnabled(enabled);
		combo_sign.setEnabled(enabled);
		text_Decl_d.setEnabled(enabled);
		text_Decl_m.setEnabled(enabled);
		text_Decl_s.setEnabled(enabled);
	}

	/**
	 * Gets the center R.A. and Decl.
	 * @return the center R.A. and Decl.
	 */
	public Coor getCoor ( ) {
		return new Coor(Integer.parseInt(text_RA_h.getText()),
						Integer.parseInt(text_RA_m.getText()),
						Format.doubleValueOf(text_RA_s.getText()),
						combo_sign.getSelectedIndex() == 1,
						Integer.parseInt(text_Decl_d.getText()),
						Integer.parseInt(text_Decl_m.getText()),
						Format.doubleValueOf(text_Decl_s.getText()));
	}

	/**
	 * Sets the center R.A. and Decl.
	 * @param coor the center R.A. and Decl.
	 */
	public void setCoor ( Coor coor ) {
		text_RA_h.setText(String.valueOf(coor.getRA_h()));
		text_RA_m.setText(String.valueOf(coor.getRA_m()));
		text_RA_s.setText(Format.formatDouble(coor.getRA_s(), 5, 2));

		if (coor.getDecl() < 0)
			combo_sign.setSelectedIndex(1);
		else
			combo_sign.setSelectedIndex(0);
		text_Decl_d.setText(String.valueOf(coor.getAbsDecl_d()));
		text_Decl_m.setText(String.valueOf(coor.getAbsDecl_m()));
		text_Decl_s.setText(Format.formatDouble(coor.getAbsDecl_s(), 4, 2));
	}
}
