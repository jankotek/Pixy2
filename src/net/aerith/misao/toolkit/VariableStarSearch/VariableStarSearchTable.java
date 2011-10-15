/*
 * @(#)VariableStarSearchTable.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.VariableStarSearch;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.xml.*;

/**
 * The <code>VariableStarSearchTable</code> represents a table where 
 * the XML report documents are added to search variable stars. It 
 * shows the status of the XML files and the progress of the operation.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2004 January 3
 */

public class VariableStarSearchTable extends InformationTable {
	/**
	 * The desktop.
	 */
	protected VariableStarSearchDesktop desktop;

	/**
	 * Constructs a <code>VariableStarSearchTable</code>.
	 * @param desktop the desktop.
	 */
	public VariableStarSearchTable ( VariableStarSearchDesktop desktop ) {
		this.desktop = desktop;
	}

	/**
	 * Initializes a popup menu. A <tt>popup</tt> must be created 
	 * previously.
	 */
	protected void initPopupMenu ( ) {
		super.initPopupMenu();

		popup.addSeparator();

		JMenuItem item = new JMenuItem("Create Group");
		item.addActionListener(new CreateGroupListener());
		popup.add(item);

		item = new JMenuItem("Create Group with Overlapping Images");
		item.addActionListener(new CreateGroupWithOverlappingImagesListener());
		popup.add(item);
	}

	/**
	 * Gets the number of records.
	 * @return the number of records.
	 */
	public int getRecordCount ( ) {
		return record_list.size();
	}

	/**
	 * Gets the XML image information element at the specified index.
	 * @param index the index.
	 * @return the XML image information element.
	 */
	public XmlInformation getInformationAt ( int index ) {
		InformationRecord record = (InformationRecord)record_list.elementAt(index);
		return record.getInformation();
	}

	/**
	 * The <code>CreateGroupListener</code> is a listener class of 
	 * menu selection to create a group of XML documents.
	 */
	protected class CreateGroupListener implements ActionListener {
		/**
		 * Invoked when the menu is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			ArrayIndex index = getSortingIndex();
			if (index == null)
				return;

			int[] rows = getSelectedRows();

			XmlInformation[] infos = new XmlInformation[rows.length];

			for (int i = 0 ; i < rows.length ; i++)
				infos[i] = getInformationAt(index.get(rows[i]));

			desktop.createGroup(infos);
		}
	}

	/**
	 * The <code>CreateGroupWithOverlappingImagesListener</code> is a 
	 * listener class of menu selection to create a group of XML 
	 * documents with overlapping XML documents.
	 */
	protected class CreateGroupWithOverlappingImagesListener implements ActionListener {
		/**
		 * Invoked when the menu is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			ArrayIndex index = getSortingIndex();
			if (index == null)
				return;

			Hashtable hash = new Hashtable();

			int[] rows = getSelectedRows();
			for (int i = 0 ; i < rows.length ; i++) {
				XmlInformation info1 = getInformationAt(index.get(rows[i]));

				// Searches overlapping XML documents.
				for (int j = 0 ; j < getRecordCount() ; j++) {
					XmlInformation info2 = getInformationAt(j);

					if (info1.overlaps(info2))
						hash.put(info2.getPath(), info2);
				}
			}

			XmlInformation[] infos = new XmlInformation[hash.size()];

			Enumeration keys = hash.keys();
			int i = 0;
			while (keys.hasMoreElements()) {
				String path = (String)keys.nextElement();
				infos[i] = (XmlInformation)hash.get(path);
				i++;
			}

			desktop.createGroup(infos);
		}
	}
}
