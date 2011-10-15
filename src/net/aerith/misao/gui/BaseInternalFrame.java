/*
 * @(#)BaseInternalFrame.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import javax.swing.*;

/**
 * The <code>BaseInternalFrame</code> represents a base internal frame.
 * It is a <code>JInternalFrame</code> with a framework to add menus.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 September 26
 */

public class BaseInternalFrame extends JInternalFrame {
	/**
	 * The content pane of this frame.
	 */
	protected Container pane;

	/**
	 * Constructs a <code>BaseInternalFrame</code>.
	 */
	public BaseInternalFrame ( ) {
		pane = getContentPane();

		setJMenuBar(new JMenuBar());

		initialize();
	}

	/**
	 * Initializes this window. This is invoked at construction.
	 */
	public void initialize ( ) {
		initMenu();
	}

	/**
	 * Addes a menu in the menu bar. If the specified menu already
	 * exists, returns the <code>JMenu</code> of it. Otherwise, new
	 * <code>JMenu</code> is added in the menu bar and returns it.
	 * @param name the name of menu.
	 * @return the menu object.
	 */
	public JMenu addMenu ( String name ) {
		JMenuBar menu_bar = getJMenuBar();
		for (int i = 0 ; i < menu_bar.getMenuCount() ; i++) {
			JMenu menu = menu_bar.getMenu(i);
			if (menu.getText().equals(name))
				return menu;
		}

		JMenu menu = (JMenu)getJMenuBar().add(new JMenu(name));
		return menu;
	}

	/**
	 * Initializes menu bar. A <code>JMenuBar</code> must be set to 
	 * this <code>JFrame</code> previously.
	 */
	public void initMenu ( ) {
		JMenu menu = addMenu("File");
		if (menu.getItemCount() > 0)
			menu.addSeparator();

		JMenuItem[] items = createFileMenus();
		for (int i = 0 ; i < items.length ; i++)
			menu.add(items[i]);
	}

	/**
	 * Returns an array of <code>JMenuItem</code> which consists of
	 * file menus. Items are newly created when invoked.
	 * @return an array of menu items.
	 */
	public JMenuItem[] createFileMenus ( ) {
		JMenuItem[] items = new JMenuItem[1];
		items[0] = new JMenuItem("Close");
		items[0].addActionListener(new CloseListener());
		return items;
	}

	/**
	 * The <code>CloseListener</code> is a listener class of menu 
	 * selection to close.
	 */
	protected class CloseListener implements ActionListener {
		/**
		 * Invoked when one of the menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			if (isClosable())
				dispose();
		}
	}
}
