/*
 * @(#)CatalogTree.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.*;
import net.aerith.misao.gui.event.*;

/**
 * The <code>CatalogTree</code> represents a tree of catalogs 
 * classified into some groups.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 September 18
 */

public class CatalogTree extends JTree {
	/**
	 * The list of listeners of catalog selection.
	 */
	protected Vector listener_list;

	/**
	 * The root node.
	 */
	protected DefaultMutableTreeNode root;

	/**
	 * The number of mode to display catalog name in nodes.
	 */
	protected final static int DISPLAY_CATALOG_NAME = 0;

	/**
	 * The number of mode to display catalog code in nodes.
	 */
	protected final static int DISPLAY_CATALOG_CODE = 1;

	/**
	 * The pane of this component.
	 */
	protected Container pane;

	/**
	 * Constructs an empty <code>CatalogTree</code>. This is only for
	 * the subclasses.
	 */
	protected CatalogTree ( ) {
		pane = this;
	}

	/**
	 * Constructs a <code>CatalogTree</code>.
	 * @param catalog_name_list the list of catalog names.
	 */
	public CatalogTree ( Vector catalog_name_list ) {
		this();

		root = new DefaultMutableTreeNode("Catalog");

		String[] categories = CatalogManager.getCatalogCategories();
		for (int i = 0 ; i < categories.length ; i++) {
			DefaultMutableTreeNode category_node = new DefaultMutableTreeNode(categories[i]);
			root.add(category_node);

			Vector catalog_list_in_category = CatalogManager.getCatalogListInCategory(categories[i]);
			Hashtable hash = new Hashtable();
			for (int j = 0 ; j < catalog_list_in_category.size() ; j++)
				hash.put((String)catalog_list_in_category.elementAt(j), this);

			for (int j = 0 ; j < catalog_name_list.size() ; j++) {
				String catalog_name = (String)catalog_name_list.elementAt(j);
				if (hash.get(catalog_name) != null) {
					DefaultMutableTreeNode node = new DefaultMutableTreeNode(catalog_name);
					category_node.add(node);
				}
			}
		}

		DefaultTreeModel model = new DefaultTreeModel(root);
		setModel(model);

		addTreeSelectionListener(new CatalogSelectionListener());

		listener_list = new Vector();
	}

	/**
	 * Displays the catalog name in nodes.
	 */
	public void displayCatalogName ( ) {
		displayNode(DISPLAY_CATALOG_NAME);
	}

	/**
	 * Displays the catalog code in nodes.
	 */
	public void displayCatalogCode ( ) {
		displayNode(DISPLAY_CATALOG_CODE);
	}

	/**
	 * Creates the tree model and sets to the tree.
	 * @param display_mode the mode to display node.
	 */
	protected void displayNode ( int display_mode ) {
		Enumeration category_nodes = root.children();
		while (category_nodes.hasMoreElements()) {
			DefaultMutableTreeNode category_node = (DefaultMutableTreeNode)category_nodes.nextElement();

			Enumeration catalog_nodes = category_node.children();
			while (catalog_nodes.hasMoreElements()) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)catalog_nodes.nextElement();
				String name = (String)node.getUserObject();

				if (display_mode == DISPLAY_CATALOG_NAME)
					name = CatalogManager.convertCatalogCodeToName(name);
				if (display_mode == DISPLAY_CATALOG_CODE)
					name = CatalogManager.convertCatalogNameToCode(name);
				node.setUserObject(name);
			}
		}

		revalidate();
		repaint();
	}

	/**
	 * Adds the listener of catalog selection.
	 * @param listener the listener of catalog selection.
	 */
	public void addCatalogTreeSelectionListener ( CatalogTreeSelectionListener listener ) {
		listener_list.addElement(listener);
	}

	/**
	 * Returns the star object of the specified path, if it represents
	 * a star. Otherwise returns null.
	 * @param path the tree path.
	 * @return the star object.
	 */
	protected Star getStar ( TreePath path ) {
		return null;
	}

	/**
	 * The <code>CatalogSelectionListener</code> is a listener class
	 * of catalog selection.
	 */
	protected class CatalogSelectionListener implements TreeSelectionListener {
		/** 
		 * Called whenever the value of the selection changes.
		 * @param e the event that characterizes the change.
		 */
		public void valueChanged ( TreeSelectionEvent e ) {
			TreePath path = e.getPath();
			if (path != null) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
				String node_name = (String)node.getUserObject();

				for (int i = 0 ; i < listener_list.size() ; i++) {
					CatalogTreeSelectionListener listener = (CatalogTreeSelectionListener)listener_list.elementAt(i);

					if (path.getPathCount() == 1) {
						listener.selectAll();
					} else if (path.getPathCount() == 2) {
						listener.selectCategory(node_name);
					} else if (path.getPathCount() == 3) {
						listener.selectCatalog(CatalogManager.convertCatalogCodeToName(node_name));
					} else {
						Star star = getStar(path);
						if (star != null)
							listener.selectStar(star);
					}
				}
			}
		}
	}
}
