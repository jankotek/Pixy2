/*
 * @(#)BaseDesktop.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;
import net.aerith.misao.image.*;
import net.aerith.misao.image.io.*;
import net.aerith.misao.image.filter.*;
import net.aerith.misao.gui.event.*;
import net.aerith.misao.gui.dialog.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.*;
import net.aerith.misao.catalog.io.*;
import net.aerith.misao.io.*;
import net.aerith.misao.io.filechooser.*;
import net.aerith.misao.database.*;
import net.aerith.misao.xml.*;
import net.aerith.misao.pixy.*;
import net.aerith.misao.toolkit.MultipleCatalogChart.MultipleCatalogChartPane;
import net.aerith.misao.toolkit.MagnitudeRecord.MagnitudeRecordInternalFrame;
import net.aerith.misao.toolkit.PositionRecord.PositionRecordInternalFrame;
import net.aerith.misao.toolkit.ImageGallery.ImageGalleryInternalFrame;
import net.aerith.misao.toolkit.ImageGallery.ImageGalleryPanel;
import net.aerith.misao.toolkit.VariabilityRecord.VariabilityRecordInternalFrame;
import net.aerith.misao.toolkit.HtmlImageGallery.HtmlImageGalleryInternalFrame;

/**
 * The <code>BaseDesktop</code> represents a desktop which has 
 * functions to show a chart, observations, gallery, etc.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2007 May 20
 */

public class BaseDesktop extends Desktop {
	/**
	 * Constructs a <code>BaseDesktop</code>.
	 */
	public BaseDesktop ( ) {
	}

	/**
	 * Returns true when the magnitude database is read only. This 
	 * method must be overrided in the subclasses.
	 * @return true when the magnitude database is read only.
	 */
	public boolean isMagnitudeDatabaseReadOnly ( ) {
		return false;
	}

	/**
	 * Shows the multiple catalog chart around the specified star.
	 * @param star the star.
	 * @return the chart window, or null if failed.
	 */
	public MultipleCatalogChartPane showChart ( Star star ) {
		OpenChartCatalogDialog dialog = new OpenChartCatalogDialog();
		dialog.setCoor(star.getCoor());

		int answer = dialog.show(pane);
		if (answer == 0) {
			StarList list = null;

			try {
				CatalogReader reader = dialog.getSelectedCatalogReader();
				reader.setLimitingMagnitude(dialog.getLimitingMag());

				String[] paths = net.aerith.misao.util.Format.separatePath(dialog.getCatalogPath());
				for (int i = 0 ; i < paths.length ; i++) {
					try {
						reader.addURL(new File(paths[i]).toURL());
					} catch ( MalformedURLException exception ) {
					}
				}

				InteractiveCatalogReader interactive_reader = new InteractiveCatalogReader(reader);
				list = interactive_reader.read(pane, dialog.getCoor(), dialog.getFieldOfView());
			} catch ( Exception exception ) {
				String message = "Failed to open the catalog.";
				JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
				return null;
			}

			ChartMapFunction cmf = new ChartMapFunction(dialog.getCoor(), 400.0 / dialog.getFieldOfView(), 0.0);
			list.mapCoordinatesToXY(cmf);

			MultipleCatalogChartPane chart = new MultipleCatalogChartPane(list, new Size(400, 400), dialog.getCoor(), dialog.getFieldOfView(), dialog.getFieldOfView(), dialog.getLimitingMag());
			try {
				chart.setCatalogDBManager(getDBManager().getCatalogDBManager());
			} catch ( IOException exception ) {
				// Makes no problem.
			}

			list = new StarList();
			try {
				CatalogReader reader = new CatalogDBReader(getDBManager().getCatalogDBManager());
				list = reader.read(dialog.getCoor(), dialog.getFieldOfView());
			} catch ( Exception exception ) {
				String message = "Failed to read database.";
				JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
			}

			if (star instanceof CatalogStar) {
				boolean found = false;
				for (int i = 0 ; i < list.size() ; i++) {
					CatalogStar s = (CatalogStar)list.elementAt(i);
					if (s.getCatalogName().equals(((CatalogStar)star).getCatalogName())  &&  s.getName().equals(star.getName())) {
						found = true;
						break;
					}
				}
				if (found == false)
					list.addElement(star);
			}

			list.mapCoordinatesToXY(cmf);

			StarList l = new StarList();
			for (int i = 0 ; i < list.size() ; i++) {
				CatalogStar s = (CatalogStar)list.elementAt(i);
				if (Math.abs(s.getX()) <= 400 / 2 + 1  &&  Math.abs(s.getY()) <= 400 / 2 + 1) {
					l.addElement(s);
				}
			}
			list = l;

			chart.addStars(list);

			JInternalFrame frame = new JInternalFrame();
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			frame.setSize(400,600);
			frame.setTitle("Chart of " + star.getName());
			frame.setVisible(true);
			frame.setMaximizable(true);
			frame.setIconifiable(true);
			frame.setResizable(true);
			frame.setClosable(true);
			frame.setBackground(Color.white);

			frame.getContentPane().add(chart);
			addFrame(frame);

			return chart;
		}

		return null;
	}

	/**
	 * Shows the observation table of the specified star.
	 * @param star the star.
	 * @return the frame window, or null if failed.
	 */
	public MagnitudeRecordInternalFrame showObservationTable ( Star star ) {
		if (star instanceof CatalogStar) {
			try {
				MagnitudeDBManager manager = getDBManager().getMagnitudeDBManager();
				XmlMagRecord[] records = manager.getElements((CatalogStar)star);

				return showObservationTable(star, records);
			} catch ( IOException exception ) {
				String message = "Failed to read the database.";
				JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
				return null;
			}
		} else {
			String message = "No observations.";
			JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
		}

		return null;
	}

	/**
	 * Shows the observation table of the specified star based on the 
	 * specified records.
	 * @param star    the star.
	 * @param records the magnitude records
	 * @return the frame window, or null if failed.
	 */
	public MagnitudeRecordInternalFrame showObservationTable ( Star star, XmlMagRecord[] records ) {
		if (records.length > 0) {
			Vector record_list = new Vector();
			for (int i = 0 ; i < records.length ; i++)
				record_list.addElement(records[i]);

			MagnitudeRecordInternalFrame frame = new MagnitudeRecordInternalFrame(star, record_list, this);
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			frame.setSize(600,450);
			frame.setTitle("Observations of " + star.getName());
			frame.setVisible(true);
			frame.setMaximizable(true);
			frame.setIconifiable(true);
			frame.setResizable(true);
			frame.setClosable(true);

			addFrame(frame);

			return frame;
		} else {
			String message = "No observations.";
			JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
		}

		return null;
	}

	/**
	 * Shows the observation table of the specified star based on the 
	 * specified records.
	 * @param star    the star.
	 * @param records the observation records
	 * @return the frame window, or null if failed.
	 */
	public MagnitudeRecordInternalFrame showObservationTable ( Star star, ObservationRecord[] records ) {
		if (records.length > 0) {
			Vector record_list = new Vector();
			for (int i = 0 ; i < records.length ; i++)
				record_list.addElement(records[i].getMagRecord());

			MagnitudeRecordInternalFrame frame = new MagnitudeRecordInternalFrame(star, record_list, this);

			// Sets the position data record, too, in order not to read
			// the XML report document files again.
			for (int i = 0 ; i < records.length ; i++) {
				if (records[i].getPositionRecord() != null)
					frame.addPositionRecord(records[i].getPositionRecord());
			}

			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			frame.setSize(600,450);
			frame.setTitle("Observations of " + star.getName());
			frame.setVisible(true);
			frame.setMaximizable(true);
			frame.setIconifiable(true);
			frame.setResizable(true);
			frame.setClosable(true);

			addFrame(frame);

			return frame;
		} else {
			String message = "No observations.";
			JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
		}

		return null;
	}

	/**
	 * Shows the position record table of the specified records.
	 * @param records the list of position records
	 * @return the frame window, or null if failed.
	 */
	public PositionRecordInternalFrame showPositionTable ( XmlPositionRecord[] records ) {
		if (records.length > 0) {
			Vector record_list = new Vector();
			for (int i = 0 ; i < records.length ; i++)
				record_list.addElement(records[i]);

			PositionRecordInternalFrame frame = new PositionRecordInternalFrame(record_list);
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			frame.setSize(600,450);
			frame.setTitle("Mean R.A. and Decl.");
			frame.setVisible(true);
			frame.setMaximizable(true);
			frame.setIconifiable(true);
			frame.setResizable(true);
			frame.setClosable(true);

			addFrame(frame);

			return frame;
		} else {
			String message = "No observations.";
			JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
		}

		return null;
	}

	/*
	 * Shows the image information table.
	 * @param infos the list of image information elements.
	 * @return the table window, or null if failed.
	 */
	public InformationTable showInformationTable ( XmlInformation[] infos ) {
		try {
			InformationTable table = new InformationTable();
			table.addInformations(infos, getFileManager());

			BaseInternalFrame frame = new BaseInternalFrame();
			frame.getContentPane().add(new JScrollPane(table));
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			frame.setSize(600,450);
			frame.setTitle("XML Files");
			frame.setVisible(true);
			frame.setMaximizable(true);
			frame.setIconifiable(true);
			frame.setResizable(true);
			frame.setClosable(true);

			addFrame(frame);

			return table;
		} catch ( Exception exception ) {
		}

		return null;
	}

	/**
	 * Shows the image gallery.
	 * @param star    the star.
	 * @param records the list of magnitude records.
	 * @return the frame window, or null if failed.
	 */
	public ImageGalleryInternalFrame showImageGallery ( Star star, XmlMagRecord[] records ) {
		ImageGallerySettingDialog dialog = new ImageGallerySettingDialog();
		int answer = dialog.show(pane);
		if (answer != 0)
			return null;

		ThumbnailImageCreater creater = new ThumbnailImageCreater(file_manager);
		try {
			creater.setDBManager(getDBManager().getInformationDBManager());
		} catch ( IOException exception ) {
			// Makes no problem.
		}
		if (dialog.createsDifferentialImageGallery()) {
			creater.setGalleryType(ThumbnailImageCreater.TYPE_DIFFERENTIAL);
		} else {
			creater.setGalleryType(ThumbnailImageCreater.TYPE_IMAGE);
		}
		creater.setSize(dialog.getSize());
		creater.setPositionPolicy(ThumbnailImageCreater.POSITION_TARGET_AT_CENTER);
		if (dialog.unifiesResolution()) {
			double resolution = dialog.getResolution();
			creater.setMagnificationPolicy(ThumbnailImageCreater.MAGNIFICATION_SPECIFIED_RESOLUTION);
			creater.setResolution(resolution);
		} else if (dialog.unifiesMagnification()) {
			double magnification = dialog.getMagnification();
			creater.setMagnificationPolicy(ThumbnailImageCreater.MAGNIFICATION_SPECIFIED_MAGNIFICATION);
			creater.setMagnification(magnification);
		} else {
			creater.setMagnificationPolicy(ThumbnailImageCreater.MAGNIFICATION_KEEP_ORIGINAL);
		}
		if (dialog.rotatesNorthUpAtRightAngles()) {
			creater.setRotationPolicy(ThumbnailImageCreater.ROTATION_NORTH_UP_AT_RIGHT_ANGLES);
		} else {
			creater.setRotationPolicy(ThumbnailImageCreater.ROTATION_KEEP_ORIGINAL);
		}

		DefaultOperationObserver observer = new DefaultOperationObserver();
		creater.addObserver(observer);

		MonoImage[] images = creater.create(records);

		creater.deleteObserver(observer);

		Vector failed_list = observer.getFailedList();
		if (failed_list.size() > 0) {
			String header = "Failed to read images of the following records:";
			MessagesDialog messsages_dialog = new MessagesDialog(header, failed_list);
			messsages_dialog.show(pane, "Warning", JOptionPane.WARNING_MESSAGE);
		}

		if (images == null  ||  images.length == 0)
			return null;

		InformationDBManager manager = null;
		try {
			manager = getDBManager().getInformationDBManager();
		} catch ( IOException exception ) {
			// Makes no problem.
		}

		ImageGalleryInternalFrame frame = new ImageGalleryInternalFrame();

		Vector succeeded_list = observer.getSucceededList();
		for (int i = 0 ; i < images.length ; i++) {
			try {
				XmlMagRecord record = (XmlMagRecord)succeeded_list.elementAt(i);
				XmlInformation info = getFileManager().readInformation(record, manager);

				ImageGalleryPanel panel = new ImageGalleryPanel(images[i], info, record, this);
				frame.addImagePanel(panel);
			} catch ( Exception exception ) {
			}
		}

		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setSize(600,380);
		frame.setTitle("Gallery of " + star.getName());
		frame.setVisible(true);
		frame.setMaximizable(true);
		frame.setIconifiable(true);
		frame.setResizable(true);
		frame.setClosable(true);

		addFrame(frame);

		return frame;
	}

	/**
	 * Shows the variability table.
	 * @param records the variability records.
	 * @return the frame window, or null if failed.
	 */
	public VariabilityRecordInternalFrame showVariabilityTable ( Variability[] records ) {
		Vector record_list = new Vector();
		for (int i = 0 ; i < records.length ; i++)
			record_list.addElement(records[i]);

		VariabilityRecordInternalFrame frame = new VariabilityRecordInternalFrame(record_list, this);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setSize(500, 300);
		frame.setTitle("Variable Stars");
		frame.setVisible(true);
		frame.setMaximizable(true);
		frame.setIconifiable(true);
		frame.setResizable(true);
		frame.setClosable(true);

		addFrame(frame);

		return frame;
	}

	/**
	 * Shows the panel of the specified variability.
	 * @param variability the variability.
	 * @return the panel window, or null if failed.
	 */
	public VariabilityPanel showVariabilityPanel ( Variability variability ) {
		VariabilityPanel panel = new VariabilityPanel(variability, this);

		JInternalFrame frame = new JInternalFrame();
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setSize(300, 250);
		frame.setTitle("Variability of " + variability.getStar().getName());
		frame.setVisible(true);
		frame.setMaximizable(true);
		frame.setIconifiable(true);
		frame.setResizable(true);
		frame.setClosable(true);

		frame.getContentPane().add(panel);
		addFrame(frame);

		return panel;
	}

	/**
	 * Shows the table to create the HTML image gallery.
	 * @param records   the variability records.
	 * @param mode      the mode.
	 * @param fits      true when to create FITS thumbnail images.
	 * @param past_mode the mode to add past images from the database.
	 * @param dss       true when to add a DSS image.
	 * @return the frame window, or null if failed.
	 */
	public HtmlImageGalleryInternalFrame showHtmlImageGalleryTable ( Variability[] records, int mode, boolean fits, int past_mode, boolean dss ) {
		Vector list = new Vector();
		for (int i = 0 ; i < records.length ; i++)
			list.addElement(records[i]);

		HtmlImageGalleryInternalFrame frame = new HtmlImageGalleryInternalFrame(list, mode, fits, past_mode, dss, this);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setSize(400,300);
		frame.setTitle("HTML Image Gallery");
		frame.setVisible(true);
		frame.setMaximizable(true);
		frame.setIconifiable(true);
		frame.setResizable(true);
		frame.setClosable(true);

		addFrame(frame);

		return frame;
	}

	/**
	 * Shows the VSNET report table.
	 * @param star    the star.
	 * @param records the list of magnitude records.
	 * @param vsnet   true when to report to VSNET, false to VSOLJ.
	 * @return the table window, or null if failed.
	 */
	public VsnetReportTable showVsnetReportTable ( Star star, XmlMagRecord[] records, boolean vsnet ) {
		VsnetReportSettingDialog dialog = new VsnetReportSettingDialog(star.getVsnetName());
		if (vsnet)
			dialog.fixToVsnet();
		else
			dialog.fixToVsolj();

		int answer = dialog.show(pane);
		if (answer == 0) {
			if (dialog.getObserverCode().length() == 0) {
				String message = "Please set observer's code.";
				JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
				return null;
			}

			Vector record_list = new Vector();
			for (int i = 0 ; i < records.length ; i++) {
				VsnetRecord vsnet_record = null;
				if (vsnet)
					vsnet_record = new VsnetRecord(dialog.getStarName(), records[i]);
				else
					vsnet_record = new VsoljRecord(dialog.getStarName(), records[i]);

				vsnet_record.setFormat(dialog.getFormat());
				vsnet_record.setAccuracy(dialog.getAccuracy());
				vsnet_record.setObserverCode(dialog.getObserverCode());
				record_list.addElement(vsnet_record);
			}

			VsnetReportTable table = new VsnetReportTable(record_list);

			BaseInternalFrame frame = new BaseInternalFrame();
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			frame.setSize(600,450);
			frame.setTitle("Report " + star.getName() + " to VSNET");
			frame.setVisible(true);
			frame.setMaximizable(true);
			frame.setIconifiable(true);
			frame.setResizable(true);
			frame.setClosable(true);

			frame.getContentPane().add(new JScrollPane(table));
			addFrame(frame);

			return table;
		}

		return null;
	}
}
