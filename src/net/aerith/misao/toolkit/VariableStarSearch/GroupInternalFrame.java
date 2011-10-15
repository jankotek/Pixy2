/*
 * @(#)GroupInternalFrame.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.VariableStarSearch;
import javax.swing.*;
import java.io.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.xml.*;

/**
 * The <code>GroupInternalFrame</code> represents a frame to show XML
 * report documents classified into some groups to search variable 
 * stars.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2007 May 20
 */

public class GroupInternalFrame extends VariableStarSearchInternalFrame {
	/**
	 * The list of variability records of variable stars.
	 */
	protected Vector variability_list;

	/**
	 * The exception thrown while constructing the raw database.
	 */
	protected Exception search_exception;

	/**
	 * Constructs a <code>GroupInternalFrame</code>.
	 * @param desktop the parent desktop.
	 */
	public GroupInternalFrame ( VariableStarSearchDesktop desktop ) {
		super(desktop);
	}

	/**
	 * Creates a new group from the specified XML image information 
	 * elements, and shows a frame of XML report documents classified
	 * into some groups.
	 * @param infos the XML image information elements.
	 */
	public void createGroup ( XmlInformation[] infos ) {
		((GroupTable)table).addGroup();

		try {
			((GroupTable)table).addInformations(infos, desktop.getFileManager());
		} catch ( IOException exception ) {
		}
	}

	/**
	 * Creates the table. This is invoked at construction.
	 * @param desktop the desktop.
	 * @return the table.
	 */
	protected InformationTable createTable ( BaseDesktop desktop ) {
		table = new GroupTable((VariableStarSearchDesktop)desktop);
		return table;
	}

	/**
	 * Shows the dialog to set parameters.
	 * @return 0 if <tt>OK</tt> button is pushed, or 2 if <tt>Cancel</tt>
	 * button is pushed.
	 */
	public int showSettingDialog ( ) {
		int group = ((GroupTable)table).getCurrentGroupNumber();

		if (group == 0) {
			variability_list = new Vector();
			search_exception = null;

			return super.showSettingDialog();
		}

		return 0;
	}

	/**
	 * Invoked when the raw database construction is succeeded.
	 */
	protected void operationSucceeded ( ) {
		try {
			Vector list = searchVariableStars();

			for (int i = 0 ; i < list.size() ; i++) {
				Variability r = (Variability)list.elementAt(i);
				CatalogStar s = r.getStar();

				// Identifies with a variable star detected in the previous groups.
				boolean merged = false;
				for (int j = 0 ; j < variability_list.size() ; j++) {
					Variability record = (Variability)variability_list.elementAt(j);
					CatalogStar star = record.getStar();

					double radius = star.getMaximumPositionErrorInArcsec() + s.getMaximumPositionErrorInArcsec();
					double distance = star.getCoor().getAngularDistanceTo(s.getCoor());
					if (distance < radius / 3600.0) {
						// Merges the same record.
						record.mergeMagnitudeRecords(r.getMagnitudeRecords());

						merged = true;
						break;
					}
				}

				if (! merged)
					variability_list.addElement(r);
			}
		} catch ( IOException exception ) {
			search_exception = exception;
		}

		try {
			((GroupTable)table).proceedGroup();

			initializeDatabase();

			control_panel.proceedOperation();
		} catch ( MaximumRepetitionCountException exception ) {
			((GroupTable)table).resetCurrentGroupNumber();

			if (search_exception == null) {
				String message = "Succeeded.";
				JOptionPane.showMessageDialog(pane, message);
			} else {
				String message = "Failed to search variable stars.";
				JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
			}

			Variability[] records = new Variability[variability_list.size()];
			for (int i = 0 ; i < variability_list.size() ; i++)
				records[i] = (Variability)variability_list.elementAt(i);

			desktop.showVariabilityTable(records);
		}
	}

	/**
	 * Invoked when the raw database construction is failed.
	 */
	protected void operationFailed ( ) {
		((GroupTable)table).resetCurrentGroupNumber();
	}
}
