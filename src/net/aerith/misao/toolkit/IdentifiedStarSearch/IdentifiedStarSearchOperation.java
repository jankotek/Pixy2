/*
 * @(#)IdentifiedStarSearchOperation.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.IdentifiedStarSearch;
import java.io.*;
import java.net.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.gui.dialog.*;
import net.aerith.misao.catalog.CatalogManager;
import net.aerith.misao.catalog.io.CatalogReader;
import net.aerith.misao.xml.*;
import net.aerith.misao.pixy.identification.DefaultIdentifier;
import net.aerith.misao.toolkit.ReportBatch.ReportBatchOperation;

/**
 * The <code>IdentifiedStarSearchOperation</code> represents a batch 
 * operation to search stars identified with a specified catalog from 
 * XML report documents.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2007 March 13
 */

public class IdentifiedStarSearchOperation extends ReportBatchOperation {
	/**
	 * The dialog to open the catalog.
	 */
	protected OpenIdentificationCatalogDialog dialog;

	/**
	 * The list of variability records of identified stars.
	 */
	protected Vector list;

	/**
	 * Constructs a <code>IdentifiedStarSearchOperation</code>.
	 * @param conductor the conductor of multi task operation.
	 */
	public IdentifiedStarSearchOperation ( MultiTaskConductor conductor ) {
		this.conductor = conductor;
	}

	/**
	 * Shows the dialog to set parameters.
	 * @return 0 if <tt>OK</tt> button is pushed, or 2 if <tt>Cancel</tt>
	 * button is pushed.
	 */
	public int showSettingDialog ( ) {
		Vector catalog_list = CatalogManager.getIdentificationCatalogReaderList();
		dialog = new OpenIdentificationCatalogDialog(catalog_list);
		return dialog.show(conductor.getPane());
	}

	/**
	 * Notifies when the operation starts.
	 */
	protected void notifyStart ( ) {
		list = new Vector();

		super.notifyStart();
	}

	/**
	 * Gets the variability records of new stars.
	 * @return the variability records of new stars.
	 */
	public Variability[] getVariabilityRecords ( ) {
		Variability[] records = new Variability[list.size()];
		for (int i = 0 ; i < list.size() ; i++)
			records[i] = (Variability)list.elementAt(i);
		return records;
	}

	/**
	 * Operates on one item. This is invoked from the conductor of 
	 * multi task operation.
	 * @param object the target object to operate.
	 * @exception Exception if an error occurs.
	 */
	public void operate ( Object object )
		throws Exception
	{
		XmlInformation info = (XmlInformation)object;

		try {
			CatalogReader reader = dialog.getSelectedCatalogReader();

			String[] paths = net.aerith.misao.util.Format.separatePath(dialog.getCatalogPath());
			for (int i = 0 ; i < paths.length ; i++) {
				try {
					reader.addURL(new File(paths[i]).toURI().toURL());
				} catch ( MalformedURLException exception ) {
					System.err.println(exception);
				}
			}

			// Checks if any stars can be identified.
			// Note that acceptNegative() is required because the document is empty.
			XmlReport report = info.cloneEmptyReport();

			DefaultIdentifier identifier = new DefaultIdentifier(report, reader);
			identifier.acceptNegative();
			IdentifiedStarCounter counter = new IdentifiedStarCounter();
			identifier.addObserver(counter);
			identifier.operate();

			// Reads the XML report document if needed, and identifies.
			if (counter.getIdentifiedStarCount() > 0) {
				File xml_file = file_manager.newFile(info.getPath());
				report = file_manager.readReport(info);

				identifier = new DefaultIdentifier(report, reader);
				identifier.addMonitor(monitor_set);
				if (dialog.isNegativeDataIgnored())
					identifier.exceptNegative();

				IdentifiedStarObserver observer = new IdentifiedStarObserver(info);
				identifier.addObserver(observer);

				monitor_set.addMessage(info.getPath());
				identifier.operate();
			}
		} catch ( Exception exception ) {
			monitor_set.addMessage(info.getPath() + ": error");
			throw exception;
		}
	}

	/**
	 * The <code>IdentifiedStarObserver</code> is an observer of star
	 * identification.
	 */
	protected class IdentifiedStarObserver implements OperationObserver {
		/**
		 * The image information.
		 */
		private XmlInformation info;

		/**
		 * Constructs an <code>IdentifiedStarObserver</code>.
		 * @param info the image information.
		 */
		public IdentifiedStarObserver ( XmlInformation info ) {
			this.info = info;
		}

		/**
		 * Invoked when the operation starts.
		 */
		public void notifyStart ( ) {
		}

		/**
		 * Invoked when the operation ends.
		 * @param exception the exception if an error occurs, or null if
		 * succeeded.
		 */
		public void notifyEnd ( Exception exception ) {
		}

		/**
		 * Invoked when a task is succeeded.
		 * @param arg the argument.
		 */
		public void notifySucceeded ( Object arg ) {
			XmlStar star = (XmlStar)arg;

			// Note that the last star in the record list is a newly identified star.
			CatalogStar identified_star = (CatalogStar)star.getAllRecords().lastElement();

			// Identifies with identified stars from other images.
			Variability variability = null;
			for (int i = 0 ; i < list.size() ; i++) {
				Variability variability2 = (Variability)list.elementAt(i);
				CatalogStar catalog_star = variability2.getStar();

				if (catalog_star.equals(identified_star)) {
					variability = variability2;
					break;
				}
			}

			try {
				XmlMagRecord record = new XmlMagRecord(info, star);

				if (variability == null) {
					XmlMagRecord[] records = new XmlMagRecord[1];
					records[0] = record;
					variability = new Variability(identified_star, records);
					list.addElement(variability);

					variability.setIdentifiedStar(identified_star);
				} else {
					variability.addMagnitudeRecord(record);
				}
			} catch ( DocumentIncompleteException exception ) {
				monitor_set.addMessage(info.getPath() + ": error");
			}
		}

		/**
		 * Invoked when a task is failed.
		 * @param arg the argument.
		 */
		public void notifyFailed ( Object arg ) {
		}

		/**
		 * Invoked when a task is warned.
		 * @param arg the argument.
		 */
		public void notifyWarned ( Object arg ) {
		}
	}

	/**
	 * The <code>IdentifiedStarCounter</code> is an observer to count
	 * the number of identified stars.
	 */
	protected class IdentifiedStarCounter implements OperationObserver {
		/**
		 * The number of identified stars.
		 */
		protected int identified_count = 0;

		/**
		 * Constructs an <code>IdentifiedStarCounter</code>.
		 */
		public IdentifiedStarCounter ( ) {
			identified_count = 0;
		}

		/**
		 * Gets the number of identified stars.
		 * @return the number of identified stars.
		 */
		public int getIdentifiedStarCount ( ) {
			return identified_count;
		}

		/**
		 * Invoked when the operation starts.
		 */
		public void notifyStart ( ) {
		}

		/**
		 * Invoked when the operation ends.
		 * @param exception the exception if an error occurs, or null if
		 * succeeded.
		 */
		public void notifyEnd ( Exception exception ) {
		}

		/**
		 * Invoked when a task is succeeded.
		 * @param arg the argument.
		 */
		public void notifySucceeded ( Object arg ) {
			identified_count++;
		}

		/**
		 * Invoked when a task is failed.
		 * @param arg the argument.
		 */
		public void notifyFailed ( Object arg ) {
		}

		/**
		 * Invoked when a task is warned.
		 * @param arg the argument.
		 */
		public void notifyWarned ( Object arg ) {
		}
	}
}
