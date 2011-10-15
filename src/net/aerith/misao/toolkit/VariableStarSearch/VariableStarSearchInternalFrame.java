/*
 * @(#)VariableStarSearchInternalFrame.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.VariableStarSearch;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.gui.dialog.*;
import net.aerith.misao.catalog.CatalogManager;
import net.aerith.misao.catalog.io.CatalogReader;
import net.aerith.misao.database.*;
import net.aerith.misao.xml.*;
import net.aerith.misao.pixy.Resource;
import net.aerith.misao.pixy.VariabilityChecker;
import net.aerith.misao.toolkit.RawDatabaseConstruction.RawDatabaseConstructionInternalFrame;
import net.aerith.misao.toolkit.RawDatabaseConstruction.RawDatabaseConstructionControlPanel;

/**
 * The <code>VariableStarSearchInternalFrame</code> represents a frame 
 * to select XML report documents to search variable stars.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class VariableStarSearchInternalFrame extends RawDatabaseConstructionInternalFrame {
	/*
	 * The parent desktop.
	 */
	protected VariableStarSearchDesktop desktop;

	/**
	 * The dialog to set up parameters to search variable stars.
	 */
	protected VariableStarSearchSettingDialog dialog;

	/**
	 * The table.
	 */
	protected VariableStarSearchTable table;

	/**
	 * The content pane of this frame.
	 */
	protected Container pane;

	/**
	 * Constructs a <code>VariableStarSearchInternalFrame</code>.
	 * @param desktop the parent desktop.
	 */
	public VariableStarSearchInternalFrame ( VariableStarSearchDesktop desktop ) {
		super(desktop);

		this.desktop = desktop;

		pane = getContentPane();
	}

	/**
	 * Adds the <tt>File</tt> menus to the menu bar.
	 */
	public void addFileMenu ( ) {
		JMenu menu = addMenu("File");

		JMenuItem item = new JMenuItem("Search Overlapping Images");
		item.addActionListener(new SearchOverlappingImagesListener());
		menu.add(item);

		menu.addSeparator();

		super.addFileMenu();
	}

	/**
	 * Creates the table. This is invoked at construction.
	 * @param desktop the desktop.
	 * @return the table.
	 */
	protected InformationTable createTable ( BaseDesktop desktop ) {
		table = new VariableStarSearchTable((VariableStarSearchDesktop)desktop);
		return table;
	}

	/**
	 * Creates the control panel. This is invoked at construction.
	 * @param operation the operation.
	 * @param table     the table.
	 * @return the control panel.
	 */
	protected RawDatabaseConstructionControlPanel createControlPanel ( MultiTaskOperation operation, InformationTable table ) {
		return new VariableStarSearchControlPanel(operation, table, this);
	}

	/**
	 * Shows the dialog to set parameters.
	 * @return 0 if <tt>OK</tt> button is pushed, or 2 if <tt>Cancel</tt>
	 * button is pushed.
	 */
	public int showSettingDialog ( ) {
		dialog = new VariableStarSearchSettingDialog();
		return dialog.show(pane);
	}

	/**
	 * Returns the limiting magnitude of the catalog database.
	 * @return the limiting magnitude of the catalog database.
	 */
	public double getLimitingMagnitude ( ) {
		if (dialog != null)
			return dialog.getLimitingMagnitude();

		return 99.9;
	}

	/**
	 * Gets the mode of operation.
	 * @return the mode of operation.
	 */
	protected int getOperationMode ( ) {
		return control_panel.getCurrentMode();
	}

	/**
	 * Invoked when the raw database construction is started.
	 */
	protected void operationStarted ( ) {
	}

	/**
	 * Invoked when the raw database construction is succeeded.
	 */
	protected void operationSucceeded ( ) {
		try {
			Vector list = searchVariableStars();

			String message = "Succeeded.";
			JOptionPane.showMessageDialog(pane, message);

			Variability[] records = new Variability[list.size()];
			for (int i = 0 ; i < list.size() ; i++)
				records[i] = (Variability)list.elementAt(i);

			desktop.showVariabilityTable(records);
		} catch ( IOException exception ) {
			String message = "Failed to search variable stars.";
			JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Invoked when the raw database construction is failed.
	 */
	protected void operationFailed ( ) {
	}

	/**
	 * Search variable stars from the raw database.
	 * @return the list of variable stars.
	 * @exception IOException if I/O error occurs.
	 */
	protected Vector searchVariableStars ( )
		throws IOException
	{
		// Search variable stars from the magnitude database
		// of detected stars.
		VariabilityChecker checker = new VariabilityChecker();
		checker.setMagnitudeThreshold(dialog.getMagnitudeThreshold());
		checker.setBrighterLimitingMagnitude(dialog.getLimitingMagnitude());
		checker.setPeriodWindowSize(dialog.getPeriodWindowSize());
		checker.setIgnoredPixelsFromEdge(dialog.getIgnoreEdge());
		checker.setBlendingPolicy(dialog.getBlendingPolicy());

		// Seeks the variable stars in the magntiude database.
		Vector list = db_manager.getMagnitudeDBManager().seekVariable(checker);

		// Identifies with catalog database.
		for (int i = 0 ; i < list.size() ; i++) {
			try {
				Variability variability = (Variability)list.elementAt(i);

				Vector identified_list = new Vector();

				CatalogReader reader = new CatalogDBReader(desktop.getDBManager().getCatalogDBManager());

				StarList l = reader.read(variability.getStar().getCoor(), 0.1);
				for (int j = 0 ; j < l.size() ; j++) {
					CatalogStar s = (CatalogStar)l.elementAt(j);

					double radius = variability.getStar().getMaximumPositionErrorInArcsec() + s.getMaximumPositionErrorInArcsec();
					double distance = variability.getStar().getCoor().getAngularDistanceTo(s.getCoor());
					if (distance < radius / 3600.0)
						identified_list.addElement(s);
				}

				CatalogStar id_star = (CatalogStar)CatalogManager.selectTypicalVsnetCatalogStar(identified_list);
				variability.setIdentifiedStar(id_star);
			} catch ( IOException exception2 ) {
			} catch ( QueryFailException exception2 ) {
			}
		}

		return list;
	}

	/**
	 * The <code>SearchOverlappingImagesListener</code> is a listener 
	 * class of menu selection to search overlapping images in the 
	 * database.
	 */
	protected class SearchOverlappingImagesListener implements ActionListener, Runnable {
		/**
		 * Invoked when one of the menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			Thread thread = new Thread(this);
			thread.setPriority(Resource.getThreadPriority());
			thread.start();
		}

		/**
		 * Runs this thread.
		 */
		public void run ( ) {
			if (getOperationMode() == ControlPanel.MODE_SETTING) {
				XmlReportQueryConditionDialog dialog = new XmlReportQueryConditionDialog();
				int answer = dialog.show(pane);

				if (answer == 0) {
					XmlReportQueryCondition query_condition = dialog.getQueryCondition();

					try {
						Vector info_list = new Vector(table.getRecordCount());
						for (int i = 0 ; i < table.getRecordCount() ; i++)
							info_list.addElement(table.getInformationAt(i));

						InformationDBAccessor accessor = desktop.getDBManager().getInformationDBManager().getAccessor(info_list, query_condition.getBrighterLimit(), query_condition.getFainterLimit());

						XmlInformation info = accessor.getFirstElement();
						while (info != null) {
							if (query_condition.accept(info)) {
								for (int i = 0 ; i < info_list.size() ; i++) {
									XmlInformation info2 = (XmlInformation)info_list.elementAt(i);
									if (info.overlaps(info2)) {
										table.addInformation(info, desktop.getFileManager());
										break;
									}
								}
							}

							info = accessor.getNextElement();
						}

						String message = "Completed.";
						JOptionPane.showMessageDialog(pane, message);
					} catch ( IOException exception ) {
						String message = "Failed to read XML files.";
						JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}
	}
}
