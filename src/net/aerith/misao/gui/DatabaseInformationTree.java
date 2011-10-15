/*
 * @(#)DatabaseInformationTree.java
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
import net.aerith.misao.catalog.*;
import net.aerith.misao.gui.event.*;
import net.aerith.misao.database.*;
import net.aerith.misao.xml.*;

/**
 * The <code>DatabaseInformationTree</code> represents a tree of image
 * information in the image database.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class DatabaseInformationTree extends JTree {
	/**
	 * The list of listeners of image information selection.
	 */
	protected Vector listener_list;

	/**
	 * The root node.
	 */
	protected DefaultMutableTreeNode root;

	/**
	 * The mode.
	 */
	protected int mode = PATH_ORIENTED;

	/**
	 * The number of mode to display date oriented tree.
	 */
	public final static int DATE_ORIENTED = 1;

	/**
	 * The number of mode to display path oriented tree.
	 */
	public final static int PATH_ORIENTED = 2;

	/**
	 * The hash table which contains the tree path and the image
	 * information.
	 */
	protected Hashtable hash_info = new Hashtable();

	/**
	 * The information database manager.
	 */
	protected InformationDBManager db_manager;

	/**
	 * The pane of this component.
	 */
	protected Container pane;

	/**
	 * Constructs a <code>DatabaseInformationTree</code>.
	 * @param db_manager the information database manager.
	 * @param mode       the mode.
	 */
	public DatabaseInformationTree ( InformationDBManager db_manager, int mode ) {
		pane = this;

		this.db_manager = db_manager;
		this.mode = mode;

		root = new DefaultMutableTreeNode("Image");

		expandNode(root);

		DefaultTreeModel model = new DefaultTreeModel(root);
		setModel(model);

		addTreeSelectionListener(new NodeSelectionListener());

		listener_list = new Vector();
	}

	/**
	 * Adds nodes to the specified parent node recurrsively.
	 * @param parent_node the parent node.
	 * @param list        the list of child node names.
	 */
	protected void addNode ( DefaultMutableTreeNode parent_node, Vector list ) {
		SortableArray array = null;

		if (mode == DATE_ORIENTED  &&  parent_node.getLevel() <= 2) {
			array = new Array(list.size());

			for (int i = 0 ; i < list.size() ; i++)
				((Array)array).set(i, Integer.parseInt((String)list.elementAt(i)));
		} else {
			array = new StringArray(list.size());

			for (int i = 0 ; i < list.size() ; i++)
				((StringArray)array).set(i, (String)list.elementAt(i));
		}

		ArrayIndex index = array.sortAscendant();

		for (int i = 0 ; i < array.getArraySize() ; i++) {
			String name = (String)list.elementAt(index.get(i));

			// Converts 1...12 to January...December.
			if (mode == DATE_ORIENTED  &&  parent_node.getLevel() == 1) {
				int month = Integer.parseInt(name);
				name = JulianDay.getFullSpellMonthString(month);
			}

			DefaultMutableTreeNode node = new DefaultMutableTreeNode(name);
			parent_node.add(node);
		}
	}

	/**
	 * Adds the listener of image information selection.
	 * @param listener the listener of image information selection.
	 */
	public void addInformationTreeSelectionListener ( InformationTreeSelectionListener listener ) {
		listener_list.addElement(listener);
	}

	/**
	 * Returns the image information of the specified path, if it 
	 * represents an image information. Otherwise returns null.
	 * @param path the tree path.
	 * @return the image information.
	 */
	protected XmlInformation getInformation ( TreePath path ) {
		String path_str = "";
		Object[] paths = path.getPath();
		for (int i = 1 ; i < paths.length ; i++) {
			String name = (String)((DefaultMutableTreeNode)paths[i]).getUserObject();
			path_str += "/" + name;
		}

		XmlInformation info = (XmlInformation)hash_info.get(path_str);
		return info;
	}

	/**
	 * Expands the sub folders and image informations under the 
	 * specified node.
	 * @param node the node.
	 */
	protected void expandNode ( DefaultMutableTreeNode node ) {
		try {
			Object[] paths = node.getPath();

			String path_str = "";
			Vector folder_list = new Vector();
			for (int i = 1 ; i < paths.length ; i++) {
				String name = (String)((DefaultMutableTreeNode)paths[i]).getUserObject();
				path_str += "/" + name;

				// Converts January...December to 1...12.
				if (mode == DATE_ORIENTED  &&  i == 2) {
					for (int m = 1 ; m <= 12 ; m++) {
						if (JulianDay.getFullSpellMonthString(m).equals(name))
							name = String.valueOf(m);
					}
				}

				folder_list.addElement(name);
			}

			// When to expand sub folders.
			Vector folders = new Vector();
			if (mode == DATE_ORIENTED)
				folders = db_manager.getDateOrientedFolders(folder_list);
			if (mode == PATH_ORIENTED)
				folders = db_manager.getPathOrientedFolders(folder_list);
			addNode(node, folders);

			// When to expand image informations.
			XmlDBAccessor accessor = null;
			if (mode == DATE_ORIENTED)
				accessor = db_manager.getDateOrientedAccessor(folder_list);
			if (mode == PATH_ORIENTED)
				accessor = db_manager.getPathOrientedAccessor(folder_list);
			if (accessor != null) {
				Vector name_list = new Vector();
				XmlInformation info = (XmlInformation)accessor.getFirstElement();
				while (info != null) {
					name_list.addElement(info.getPath());
					hash_info.put(path_str + "/" + info.getPath(), info);
					info = (XmlInformation)accessor.getNextElement();
				}
				addNode(node, name_list);
			}

			revalidate();
			repaint();
		} catch ( IOException exception ) {
			String message = "Failed to read the database.";
			JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * The <code>NodeSelectionListener</code> is a listener class of
	 * leaf selection to expand the image informations, or show the
	 * image information.
	 */
	protected class NodeSelectionListener implements TreeSelectionListener {
		/** 
		 * Called whenever the value of the selection changes.
		 * @param e the event that characterizes the change.
		 */
		public void valueChanged ( TreeSelectionEvent e ) {
			TreePath path = e.getPath();
			if (path != null) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
				if (node.isLeaf()) {
					if (getInformation(path) != null) {
						// When selecting an image information.
						XmlInformation info = getInformation(path);
						for (int i = 0 ; i < listener_list.size() ; i++) {
							InformationTreeSelectionListener listener = (InformationTreeSelectionListener)listener_list.elementAt(i);
							listener.select(info);
						}
					} else {
						// When expanding a folder.
						expandNode(node);
					}
				}
			}
		}
	}
}
