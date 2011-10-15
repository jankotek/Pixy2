/*
 * @(#)RecordSearchOperation.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.RecordSearch;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.CatalogStar;
import net.aerith.misao.gui.BaseDesktop;
import net.aerith.misao.xml.*;
import net.aerith.misao.catalog.io.StarListReader;
import net.aerith.misao.io.FileManager;
import net.aerith.misao.pixy.identification.DefaultIdentifier;

/**
 * The <code>RecordSearchOperation</code> represents an operation to 
 * search a record identified with the specified star from each XML 
 * report document.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class RecordSearchOperation extends MultiTaskOperation {
	/**
	 * The R.A and Decl. and radius to search.
	 */
	protected CatalogStar target_star;

	/**
	 * The file manager;
	 */
	protected FileManager file_manager;

	/**
	 * The list of records.
	 */
	protected ArrayList list;

	/**
	 * Constructs a <code>RecordSearchOperation</code>.
	 * @param conductor    the conductor of multi task operation.
	 * @param target_star  the R.A. and Decl. and radius to search.
	 * @param file_manager the file manager.
	 */
	public RecordSearchOperation ( MultiTaskConductor conductor, CatalogStar target_star, FileManager file_manager ) {
		this.conductor = conductor;

		this.target_star = target_star;
		this.file_manager = file_manager;
	}

	/**
	 * Shows the dialog to set parameters.
	 * @return 0 if <tt>OK</tt> button is pushed, or 2 if <tt>Cancel</tt>
	 * button is pushed.
	 */
	public int showSettingDialog ( ) {
		return 0;
	}

	/**
	 * Notifies when the operation starts.
	 */
	protected void notifyStart ( ) {
		list = new ArrayList();

		super.notifyStart();
	}

	/**
	 * Gets the records searched in this operation.
	 * @return the records searched in this operation.
	 */
	public ObservationRecord[] getRecords ( ) {
		ObservationRecord[] records = new ObservationRecord[list.size()];
		return (ObservationRecord[])list.toArray(records);
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
			StarListReader reader = new StarListReader();
			reader.addStar(target_star);

			// Reads only the star data around the target star.
			ChartMapFunction cmf = info.getChartMapFunction();
			double radius = target_star.getMaximumPositionErrorInArcsec() / 3600.0 + 5.0 / cmf.getScaleUnitPerDegree();
			XmlReport report = file_manager.readReport(info, target_star.getCoor(), radius);

			DefaultIdentifier identifier = new DefaultIdentifier(report, reader);

			DefaultOperationObserver observer = new DefaultOperationObserver();
			identifier.addObserver(observer);

			identifier.perform();

			Vector identified_list = observer.getSucceededList();
			if (identified_list.size() > 0) {
				XmlStar xml_star = (XmlStar)identified_list.elementAt(0);
				XmlMagRecord mag_record = new XmlMagRecord(info, xml_star);
				XmlPositionRecord position_record = null;
				try {
					position_record = new XmlPositionRecord(info, xml_star);
				} catch ( NoDataException exception ) {
				}
				list.add(new ObservationRecord(mag_record, position_record));
			} else {
				Position position = info.mapCoordinatesToXY(target_star.getCoor());
				if (0 <= position.getX()  &&  position.getX() <= info.getSize().getWidth()  &&
					0 <= position.getY()  &&  position.getY() <= info.getSize().getHeight()) {
					target_star.setPosition(position);

					XmlStar xml_star = new XmlStar();
					xml_star.setName("NEG", 0);
					xml_star.addStar(target_star);

					XmlMagRecord mag_record = new XmlMagRecord(info, xml_star);
					list.add(new ObservationRecord(mag_record, null));
				}
			}

			notifySucceeded(info);
		} catch ( Exception exception ) {
			notifyFailed(info);

			throw exception;
		}
	}
}
