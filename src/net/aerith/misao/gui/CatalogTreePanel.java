/*
 * @(#)CatalogTreePanel.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.*;
import net.aerith.misao.gui.event.*;
import net.aerith.misao.database.GlobalDBManager;

/**
 * The <code>CatalogTreePanel</code> represents a panel which consists
 * of a catalog tee and the radio button to change the display mode.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 September 18
 */

public class CatalogTreePanel extends JPanel {
	/**
	 * The catalog tree.
	 */
	protected CatalogTree catalog_tree;

	/**
	 * The panel of radio buttons.
	 */
	protected JPanel radio_panel;

	/**
	 * The radio button to display catalog name in tree.
	 */
	protected JRadioButton radio_name;

	/**
	 * The radio button to display catalog code in tree.
	 */
	protected JRadioButton radio_code;

	/**
	 * Constructs a <code>CatalogTreePanel</code>.
	 * @param catalog_tree the catalog tree component.
	 */
	public CatalogTreePanel ( CatalogTree catalog_tree ) {
		this.catalog_tree = catalog_tree;

		radio_name = new JRadioButton("Full name", true);
		radio_code = new JRadioButton("Short name");
		ButtonGroup bg = new ButtonGroup();
		bg.add(radio_name);
		bg.add(radio_code);
		radio_name.addActionListener(new CatalogTreeDisplayListener());
		radio_code.addActionListener(new CatalogTreeDisplayListener());
		radio_panel = new JPanel();
		radio_panel.add(radio_name);
		radio_panel.add(radio_code);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(radio_panel);
		add(new JScrollPane(catalog_tree));
	}

	/**
	 * Gets the catalog tree.
	 * @return the catalog tree.
	 */
	public CatalogTree getCatalogTree ( ) {
		return catalog_tree;
	}

	/**
	 * Gets the width of the radio button panel.
	 * @return the width of the radio button panel.
	 */
	public int getPanelWidth ( ) {
		return radio_panel.getPreferredSize().width;
	}

	/**
	 * The <code>CatalogTreeDisplayListener</code> is a listener 
	 * class of radio button check to display catalog name or code
	 * in catalog tree nodes.
	 */
	protected class CatalogTreeDisplayListener implements ActionListener {
		/**
		 * Invoked when one of the radio button is checked.
		 * @param e contains the radio button status.
		 */
		public void actionPerformed ( ActionEvent e ) {
			if (radio_name.isSelected())
				catalog_tree.displayCatalogName();
			if (radio_code.isSelected())
				catalog_tree.displayCatalogCode();
		}
	}
}
