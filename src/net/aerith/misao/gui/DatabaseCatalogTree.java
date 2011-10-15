/*
 * @(#)DatabaseCatalogTree.java
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
import java.io.IOException;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.*;
import net.aerith.misao.gui.dialog.*;
import net.aerith.misao.gui.event.*;
import net.aerith.misao.database.*;

/**
 * The <code>DatabaseCatalogTree</code> represents a tree of catalogs 
 * in the catalog database classified into some groups.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class DatabaseCatalogTree extends CatalogTree {
	/**
	 * The hash table which contains the tree path and the star object.
	 */
	protected Hashtable hash_stars = new Hashtable();

	/**
	 * The database manager.
	 */
	protected GlobalDBManager db_manager;

	/**
	 * Constructs a <code>DatabaseCatalogTree</code>.
	 * @param db_manager the database manager.
	 */
	public DatabaseCatalogTree ( GlobalDBManager db_manager ) {
		this.db_manager = db_manager;

		root = new DefaultMutableTreeNode("Catalog");

		Hashtable hash_db = new Hashtable();
		try {
			hash_db = getCatalogTreeManager().getCatalogHierarchy(CatalogTreeManager.EXCLUDE_SUBFOLDERS);
		} catch ( IOException exception ) {
			String message = "Failed to read the database.";
			JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
		}

		String[] categories = CatalogManager.getCatalogCategories();
		for (int i = 0 ; i < categories.length ; i++) {
			DefaultMutableTreeNode category_node = new DefaultMutableTreeNode(categories[i]);
			root.add(category_node);

			Hashtable hash_catalogs = (Hashtable)hash_db.get(categories[i]);

			if (hash_catalogs != null) {
				Vector catalogs = CatalogManager.getCatalogListInCategory(categories[i]);
				for (int j = 0 ; j < catalogs.size() ; j++) {
					String catalog_name = (String)catalogs.elementAt(j);
					Hashtable hash = (Hashtable)hash_catalogs.get(catalog_name);
					if (hash != null) {
						DefaultMutableTreeNode node = new DefaultMutableTreeNode(catalog_name);
						category_node.add(node);

						addNode(node, hash);
					}
				}
			}
		}

		DefaultTreeModel model = new DefaultTreeModel(root);
		setModel(model);

		addTreeSelectionListener(new CatalogSelectionListener());
		addTreeSelectionListener(new StarFolderSelectionListener());

		listener_list = new Vector();
	}

	/**
	 * Adds nodes to the specified parent node recurrsively.
	 * @param parent_node the parent node.
	 * @param hash        the hash table which contains the hierarchy.
	 */
	protected void addNode ( DefaultMutableTreeNode parent_node, Hashtable hash ) {
		StringArray array = new StringArray(hash.size());

		Enumeration keys = hash.keys();
		int i = 0;
		while (keys.hasMoreElements()) {
			array.set(i, (String)keys.nextElement());
			i++;
		}

		ArrayIndex index = array.sortAscendant();

		for (i = 0 ; i < array.getArraySize() ; i++) {
			String key = array.getValueAt(i);

			DefaultMutableTreeNode node = new DefaultMutableTreeNode(key);
			parent_node.add(node);

			Hashtable hash_child = (Hashtable)hash.get(key);
			if (hash_child != null)
				addNode(node, hash_child);
		}
	}

	/**
	 * Gets the catalog tree manager.
	 * @return the catalog tree manager.
	 */
	protected CatalogTreeManager getCatalogTreeManager ( ) {
		return db_manager.getCatalogDBManager();
	}

	/**
	 * Returns the star object of the specified path, if it represents
	 * a star. Otherwise returns null.
	 * @param path the tree path.
	 * @return the star object.
	 */
	protected Star getStar ( TreePath path ) {
		String path_str = "";
		Object[] paths = path.getPath();
		for (int i = 1 ; i < paths.length ; i++) {
			String name = (String)((DefaultMutableTreeNode)paths[i]).getUserObject();
			if (i == 2)
				name = CatalogManager.convertCatalogNameToFolder(name);
			path_str += "/" + name;
		}

		Star star = (Star)hash_stars.get(path_str);
		return star;
	}

	/**
	 * The <code>StarFolderSelectionListener</code> is a listener 
	 * class of leaf selection to expand the stars.
	 */
	protected class StarFolderSelectionListener implements TreeSelectionListener {
		/** 
		 * Called whenever the value of the selection changes.
		 * @param e the event that characterizes the change.
		 */
		public void valueChanged ( TreeSelectionEvent e ) {
			TreePath path = e.getPath();
			if (path != null) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
				if (node.isLeaf()  &&  path.getPathCount() >= 3) {
					// When selecting a star.
					if (getStar(path) != null)
						return;

					try {
						Object[] paths = path.getPath();

						String category_name = (String)((DefaultMutableTreeNode)paths[1]).getUserObject();
						String catalog_name = (String)((DefaultMutableTreeNode)paths[2]).getUserObject();

						String path_str = "";
						Vector folder_list = new Vector();
						for (int i = 1 ; i < paths.length ; i++) {
							String name = (String)((DefaultMutableTreeNode)paths[i]).getUserObject();
							if (i >= 3)
								folder_list.addElement(name);

							if (i == 2)
								name = CatalogManager.convertCatalogNameToFolder(name);
							path_str += "/" + name;
						}

						CatalogTreeManager tree_manager = getCatalogTreeManager();

						// When to expand sub folders of a catalog.
						Vector subfolder_list = tree_manager.getCatalogSubfolders(category_name, catalog_name, folder_list);
						if (subfolder_list.size() > 0) {
							for (int i = 0 ; i < subfolder_list.size() ; i++) {
								DefaultMutableTreeNode star_node = new DefaultMutableTreeNode((String)subfolder_list.elementAt(i));
								node.add(star_node);
							}

							revalidate();
							repaint();

							return;
						}

						// When to expand stars.
						DefaultOperationObserver observer = new DefaultOperationObserver();

						tree_manager.addObserver(observer);

						Vector star_list = tree_manager.getStars(category_name, catalog_name, folder_list);

						tree_manager.deleteObserver(observer);

						Vector failed_list = observer.getFailedList();
						if (failed_list.size() > 0) {
							String header = "Unknown stars:";
							MessagesDialog dialog = new MessagesDialog(header, failed_list);
							dialog.show(pane, "Warning", JOptionPane.WARNING_MESSAGE);
						}

						if (star_list != null  &&  star_list.size() > 0) {
							for (int i = 0 ; i < star_list.size() ; i++) {
								Star star = (Star)star_list.elementAt(i);

								DefaultMutableTreeNode star_node = new DefaultMutableTreeNode(star.getName());
								node.add(star_node);

								hash_stars.put(path_str + "/" + star.getName(), star);
							}

							revalidate();
							repaint();
						}
					} catch ( IOException exception ) {
						String message = "Failed to read the database.";
						JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
					} catch ( ClassNotFoundException exception ) {
						String message = "Failed to create a star data.";
						JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
					} catch ( IllegalAccessException exception ) {
						String message = "Failed to create a star data.";
						JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
					} catch ( InstantiationException exception ) {
						String message = "Failed to create a star data.";
						JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}
	}
}
