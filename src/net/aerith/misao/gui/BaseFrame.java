/*
 * @(#)BaseFrame.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import javax.swing.*;
import net.aerith.misao.pixy.Resource;

/**
 * The <code>BaseFrame</code> represents a base frame. It is a 
 * <code>JFrame</code> with a function to change the look and feel.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2007 July 15
 */

public class BaseFrame extends JFrame {
	/**
	 * The content pane of this frame.
	 */
	protected Container pane;

	/**
	 * The array of look and feel information.
	 */
	protected UIManager.LookAndFeelInfo[] ui_info;

	/**
	 * Constructs a <code>BaseFrame</code>.
	 */
	public BaseFrame ( ) {
		pane = getContentPane();

		ui_info = UIManager.getInstalledLookAndFeels();

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
		JMenu menu = addMenu("Option");
		JMenuItem[] items = createLookAndFeelMenus();
		for (int i = 0 ; i < items.length ; i++)
			menu.add(items[i]);
	}

	/**
	 * Adds a menu to show the copyright message. This method will be
	 * invoked in <tt>initMenu</tt> method in the subclasses.
	 */
	public void addCopyrightMenu ( ) {
		JMenu menu = addMenu("Help");
		JMenuItem mi = new JMenuItem("Copyright");
		mi.addActionListener(new CopyrightListener());
		menu.add(mi);
	}

	/**
	 * Returns an array of <code>JMenuItem</code> which consists of
	 * look and feels menus. Items are newly created when invoked.
	 * @return an array of menu items.
	 */
	public JMenuItem[] createLookAndFeelMenus ( ) {
		Vector list = new Vector();
		ButtonGroup ui_group = new ButtonGroup();
		for (int i = 0 ; i < ui_info.length ; i++) {
			JMenuItem item = new JRadioButtonMenuItem(ui_info[i].getName() + "Look and Feel");
			item.addItemListener(new UIListener());
			list.addElement(item);
			ui_group.add(item);
			if (i == 0) {
				item.setSelected(true);
				updateUI(0);
			}
		}

		JMenuItem[] item_list = new JMenuItem[list.size()];
		for (int i = 0 ; i < list.size() ; i++)
			item_list[i] = (JMenuItem)list.elementAt(i);
		return item_list;
	}

	/**
	 * Changes the look and feel.
	 * @param index the index number of new look and feel.
	 */
	private void updateUI ( int index ) {
		try {
			UIManager.setLookAndFeel(ui_info[index].getClassName());
			SwingUtilities.updateComponentTreeUI(this);
		} catch ( UnsupportedLookAndFeelException exception ) {
			System.err.println(exception);
		} catch ( ClassNotFoundException exception ) {
			System.err.println(exception);
		} catch ( InstantiationException exception ) {
			System.err.println(exception);
		} catch ( IllegalAccessException exception ) {
			System.err.println(exception);
		}
	}

	/**
	 * The <code>UIListener</code> is a listener class of menu 
	 * selection to change the look and feel.
	 */
	protected class UIListener implements ItemListener {
		/**
		 * Invoked when one of the radio button menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void itemStateChanged ( ItemEvent e ) {
			JRadioButtonMenuItem button = (JRadioButtonMenuItem)e.getSource();
			for (int i = 0 ; i < ui_info.length ; i++) {
				if (button.getText().equals(ui_info[i].getName() + "Look and Feel")) {
					if (button.isSelected()) {
						updateUI(i);
					}
				}
			}
		}
	}

	/**
	 * The <code>CopyrightListener</code> is a listener class of menu 
	 * selection to show the copyright message.
	 */
	protected class CopyrightListener implements ActionListener {
		/**
		 * Invoked when one of the menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			JLabel[] labels = new JLabel[2];

			ImageIcon icon = Resource.getSystemIcon();
			labels[0] = new ImageLabel(icon);

			labels[1] = new JLabel(Resource.getSpecialThanksHtmlMessage());
			JOptionPane.showMessageDialog(pane, labels, Resource.getVersion(), JOptionPane.PLAIN_MESSAGE);
		}
	}
}
