/*
 * @(#)PositionRecordTable.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.PositionRecord;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Vector;
import java.util.StringTokenizer;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.gui.table.*;
import net.aerith.misao.gui.dialog.*;
import net.aerith.misao.xml.*;
import net.aerith.misao.database.*;
import net.aerith.misao.pixy.Resource;

/**
 * The <code>PositionRecordTable</code> represents a table which 
 * contains position data in the database.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class PositionRecordTable extends SortableCheckTable {
	/**
	 * The list of position data records.
	 */
	protected Vector record_list;

	/**
	 * The frame.
	 */
	protected PositionRecordInternalFrame frame;

	/**
	 * The mean R.A. and Decl.
	 */
	protected Coor mean_coor = new Coor();

	/**
	 * The columns.
	 */
	protected final static String[] column_names = { "", "R.A.", "Decl.", "O-C(R.A.)", "O-C(Decl.)", "Mag", "Area", "Pixel Size", "Astrometric Error", "Date", "Catalog", "Equinox", "Observer", "Instruments", "XML File", "ID", "Position", "Pixels from Edge" };

	/**
	 * The table model.
	 */
	protected DefaultTableModel model;

	/**
	 * The table column model.
	 */
	protected DefaultTableColumnModel column_model;

	/**
	 * The container pane.
	 */
	protected Container pane;

	/**
	 * Constructs a <code>PositionRecordTable</code> with a list of 
	 * position data records.
	 * @param record_list the list of position data records.
	 * @param frame       the frame.
	 */
	public PositionRecordTable ( Vector record_list, PositionRecordInternalFrame frame ) {
		this.record_list = record_list;
		this.frame = frame;

		index = new ArrayIndex(record_list.size());

		model = new DefaultTableModel(column_names, 0);
		Object[] objects = new Object[column_names.length];
		objects[0] = new Boolean(true);
		for (int i = 1 ; i < column_names.length ; i++)
			objects[i] = "";
		for (int i = 0 ; i < record_list.size() ; i++)
			model.addRow(objects);
		setModel(model);

		column_model = (DefaultTableColumnModel)getColumnModel();
		for (int i = 1 ; i < column_names.length ; i++)
			column_model.getColumn(i).setCellRenderer(new StringRenderer(column_names[i], LabelTableCellRenderer.MODE_MULTIPLE_SELECTION));

		initializeCheckColumn();

		setTableHeader(new TableHeader(column_model));

		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		initializeColumnWidth();

		pane = this;

		initPopupMenu();

		calculateMeanCoor();
	}

	/**
	 * Initializes the column width.
	 */
	protected void initializeColumnWidth ( ) {
		column_model.getColumn(0).setPreferredWidth(20);
		column_model.getColumn(1).setPreferredWidth(100);
		column_model.getColumn(2).setPreferredWidth(100);
		column_model.getColumn(3).setPreferredWidth(40);
		column_model.getColumn(4).setPreferredWidth(40);
		column_model.getColumn(5).setPreferredWidth(60);
		column_model.getColumn(6).setPreferredWidth(40);
		column_model.getColumn(7).setPreferredWidth(60);
		column_model.getColumn(8).setPreferredWidth(60);
		column_model.getColumn(9).setPreferredWidth(150);
		column_model.getColumn(10).setPreferredWidth(60);
		column_model.getColumn(11).setPreferredWidth(60);
		column_model.getColumn(12).setPreferredWidth(120);
		column_model.getColumn(13).setPreferredWidth(80);
		column_model.getColumn(14).setPreferredWidth(180);
		column_model.getColumn(15).setPreferredWidth(80);
		column_model.getColumn(16).setPreferredWidth(120);
		column_model.getColumn(17).setPreferredWidth(40);
	}

	/**
	 * Initializes a popup menu. A <tt>popup</tt> must be created 
	 * previously.
	 */
	protected void initPopupMenu ( ) {
		super.initPopupMenu();

		popup.addSeparator();

		JMenuItem item = new JMenuItem("Save");
		item.addActionListener(new SaveListener());
		popup.add(item);

		item = new JMenuItem("Save As MPC Format");
		item.addActionListener(new SaveAsMpcFormatListener());
		popup.add(item);
	}

	/**
	 * Gets the list of selected records.
	 * @return the list of selected records.
	 */
	public XmlPositionRecord[] getSelectedRecords ( ) {
		ArrayList list = new ArrayList();

		int check_column = getCheckColumn();
		for (int i = 0 ; i < model.getRowCount() ; i++) {
			if (((Boolean)getValueAt(i, check_column)).booleanValue()) {
				XmlPositionRecord record = (XmlPositionRecord)record_list.elementAt(index.get(i));
				list.add(record);
			}
		}

		XmlPositionRecord[] records = new XmlPositionRecord[list.size()];
        return (XmlPositionRecord[])list.toArray(records);
	}

	/**
	 * Calculates the mean position from the selected data.
	 */
	private void calculateMeanCoor ( ) {
		XmlPositionRecord[] records = getSelectedRecords();
		if (records.length == 0) {
			mean_coor = new Coor();
		} else {
			Coor center_coor = ((XmlCoor)records[0].getCoor()).getCoor();
			ChartMapFunction cmf = new ChartMapFunction(center_coor, 1.0, 0.0);

			double x = 0.0;
			double y = 0.0;
			for (int i = 0 ; i < records.length ; i++) {
				Coor coor = ((XmlCoor)records[i].getCoor()).getCoor();
				Position position = cmf.mapCoordinatesToXY(coor);
				x += position.getX();
				y += position.getY();
			}
			x /= (double)records.length;
			y /= (double)records.length;

			mean_coor = cmf.mapXYToCoordinates(new Position(x, y));
		}

		// Shows the mean R.A. and Decl.
		frame.setMeanCoor(mean_coor);

		// Updates the O-C.
		repaint();
	}

	/**
	 * Gets the output string of the cell.
	 * @param header_value the header value of the column.
	 * @param row          the index of row in original order.
	 * @return the output string of the cell.
	 */
	protected String getCellString ( String header_value, int row ) {
		XmlPositionRecord record = (XmlPositionRecord)record_list.elementAt(row);

		Coor coor = ((XmlCoor)record.getCoor()).getCoor();
		double delta_ra = coor.getRA() - mean_coor.getRA();
		if (delta_ra > 180.0)
			delta_ra -= 360.0;
		if (delta_ra < -180.0)
			delta_ra += 360.0;
		delta_ra *= 3600.0;
		double delta_decl = (coor.getDecl() - mean_coor.getDecl()) * 3600.0;

		if (header_value.equals("R.A.")) {
			StringTokenizer st = new StringTokenizer(coor.getOutputString());
			return st.nextToken();
		}
		if (header_value.equals("Decl.")) {
			StringTokenizer st = new StringTokenizer(coor.getOutputString());
			st.nextToken();
			return st.nextToken();
		}
		if (header_value.equals("O-C(R.A.)")) {
			String s = Format.formatDouble(Math.abs(delta_ra), 10, 8).trim();
			if (delta_ra < 0)
				return "-" + s;
			else
				return "+" + s;
		}
		if (header_value.equals("O-C(Decl.)")) {
			String s = Format.formatDouble(Math.abs(delta_decl), 10, 8).trim();
			if (delta_decl < 0)
				return "-" + s;
			else
				return "+" + s;
		}
		if (header_value.equals("Mag")) {
			return ((XmlMag)record.getMag()).getOutputString();
		}
		if (header_value.equals("Area")) {
			if (record.getArea() != null)
				return String.valueOf(record.getArea().intValue());
		}
		if (header_value.equals("Pixel Size")) {
			return record.getPixelSize().getWidth() + " x " + record.getPixelSize().getHeight() + " " + record.getPixelSize().getUnit();
		}
		if (header_value.equals("Astrometric Error")) {
			XmlAstrometricError astrometric_error = (XmlAstrometricError)record.getAstrometricError();
			return astrometric_error.getRa() + " x " + astrometric_error.getDecl() + " " + astrometric_error.getUnit();
		}
		if (header_value.equals("Date")) {
			return record.getDate();
		}
		if (header_value.equals("Catalog")) {
			if (record.getCatalog() != null)
				return record.getCatalog();
		}
		if (header_value.equals("Equinox")) {
			if (record.getEquinox() != null)
				return record.getEquinox();
		}
		if (header_value.equals("Observer")) {
			return record.getObserver();
		}
		if (header_value.equals("Instruments")) {
			if (record.getInstruments() != null)
				return record.getInstruments();
		}
		if (header_value.equals("XML File")) {
			return record.getImageXmlPath();
		}
		if (header_value.equals("ID")) {
			return record.getName();
		}
		if (header_value.equals("Position")) {
			XmlPosition position = (XmlPosition)record.getPosition();
			return position.getOutputString();
		}
		if (header_value.equals("Pixels from Edge")) {
			if (record.getPixelsFromEdge() != null)
				return String.valueOf(record.getPixelsFromEdge().intValue());
		}
		return "";
	}

	/**
	 * Gets the sortable array of the specified column.
	 * @param header_value the header value of the column to sort.
	 */
	protected SortableArray getSortableArray ( String header_value ) {
		if (header_value.length() == 0)
			return null;

		SortableArray array = null;
		if (header_value.equals("R.A.")  ||  header_value.equals("Decl.")  ||  header_value.equals("O-C(R.A.)")  ||  header_value.equals("O-C(Decl.)")  ||  header_value.equals("Mag")  ||  header_value.equals("Area")  ||  header_value.equals("Date")  ||  header_value.equals("Pixels from Edge"))
			array = new Array(record_list.size());
		else 
			array = new StringArray(record_list.size());

		for (int i = 0 ; i < record_list.size() ; i++) {
			String value = getCellString(header_value, i);

			XmlPositionRecord record = (XmlPositionRecord)record_list.elementAt(i);

			Coor coor = ((XmlCoor)record.getCoor()).getCoor();
			double delta_ra = coor.getRA() - mean_coor.getRA();
			if (delta_ra > 180.0)
				delta_ra -= 360.0;
			if (delta_ra < -180.0)
				delta_ra += 360.0;
			delta_ra *= 3600.0;
			double delta_decl = (coor.getDecl() - mean_coor.getDecl()) * 3600.0;

			if (header_value.equals("R.A.")) {
				((Array)array).set(i, coor.getRA());
			} else if (header_value.equals("Decl.")) {
				((Array)array).set(i, coor.getDecl());
			} else if (header_value.equals("O-C(R.A.)")) {
				((Array)array).set(i, delta_ra);
			} else if (header_value.equals("O-C(Decl.)")) {
				((Array)array).set(i, delta_decl);
			} else if (header_value.equals("Mag")) {
				double mag_value = 0.0;
				if ('0' <= value.charAt(0)  &&  value.charAt(0) <= '9'  ||
					value.charAt(0) == '-'  ||  value.charAt(0) == '+') {
					mag_value = Format.doubleValueOf(value);
				} else {
					mag_value = 100 + Format.doubleValueOf(value.substring(1));
				}
				((Array)array).set(i, mag_value);
			} else if (header_value.equals("Area")) {
				if (record.getArea() != null)
					((Array)array).set(i, record.getArea().intValue());
				else
					((Array)array).set(i, 0);
			} else if (header_value.equals("Date")) {
				double jd = JulianDay.create(value).getJD();
				((Array)array).set(i, jd);
			} else if (header_value.equals("Pixels from Edge")) {
				int pixels = -1;
				if (value.length() > 0)
					pixels = Integer.parseInt(value);
				((Array)array).set(i, pixels);
			} else {
				((StringArray)array).set(i, value);
			}
		}

		return array;
	}

	/**
	 * Invoked when the check box is edited.
	 */
	protected void checkEdited ( ) {
		calculateMeanCoor();
	}

	/**
	 * The <code>SaveListener</code> is a listener class of menu 
	 * selection to save the report in a file.
	 */
	protected class SaveListener implements ActionListener {
		/**
		 * Invoked when the menu is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			ArrayIndex index = getSortingIndex();
			if (index == null)
				return;

			try {
				CommonFileChooser file_chooser = new CommonFileChooser();
				file_chooser.setDialogTitle("Save data.");
				file_chooser.setMultiSelectionEnabled(false);

				if (file_chooser.showSaveDialog(pane) == JFileChooser.APPROVE_OPTION) {
					File file = file_chooser.getSelectedFile();
					if (file.exists()) {
						String message = "Overwrite to " + file.getPath() + " ?";
						if (0 != JOptionPane.showConfirmDialog(pane, message, "Confirmation", JOptionPane.YES_NO_OPTION)) {
							return;
						}
					}

					PrintStream stream = new PrintStream(new BufferedOutputStream(new FileOutputStream(file)));

					stream.println(mean_coor.getOutputString());
					stream.println("");

					int check_column = getCheckColumn();

					int[] length = new int[column_names.length];
					for (int i = 0 ; i < model.getRowCount() ; i++) {
						if (((Boolean)getValueAt(i, check_column)).booleanValue()) {
							for (int j = 0 ; j < column_names.length ; j++) {
								String value = getCellString(column_names[j], index.get(i));
								if (length[j] < value.length())
									length[j] = value.length();
							}
						}
					}

					for (int i = 0 ; i < model.getRowCount() ; i++) {
						boolean selected = ((Boolean)getValueAt(i, check_column)).booleanValue();

						String[] values = new String[5];
						for (int j = 0 ; j < 5 ; j++) {
							int column = j;
							switch (j) {
								case 0:	column = 9;	break;
								case 1:	column = 1;	break;
								case 2:	column = 2;	break;
								case 3:	column = 3;	break;
								case 4:	column = 4;	break;
							}

							values[j] = getCellString(column_names[column], index.get(i));
							if (j < 4) {
								while (values[j].length() < length[column]) {
									if (column == 3  ||  column == 4)	// O-C
										values[j] = " " + values[j];
									else
										values[j] += " ";
								}
							}
						}

						String data = values[0] + "  " + values[1] + " " + values[2] + "  ";
						if (selected)
							data += " ";
						else
							data += "(";
						data += values[3] + "  " + values[4];
						if (selected == false)
							data += ")";

						stream.println(data);
					}

					stream.println("");

					for (int i = 0 ; i < model.getRowCount() ; i++) {
						boolean selected = ((Boolean)getValueAt(i, check_column)).booleanValue();

						String[] values = new String[5];
						for (int j = 0 ; j < 5 ; j++) {
							int column = j;
							switch (j) {
								case 0:	column = 9;	break;
								case 1:	column = 5;	break;
								case 2:	column = 6;	break;
								case 3:	column = 16;	break;
								case 4:	column = 14;	break;
							}

							values[j] = getCellString(column_names[column], index.get(i));
							if (j < 5) {
								while (values[j].length() < length[column]) {
									if (column == 5  ||  column == 6)	// Mag, Area
										values[j] = " " + values[j];
									else
										values[j] += " ";
								}
							}
						}

						String data = values[0] + "  " + values[1] + "  " + values[2] + "  " + values[3] + "  " + values[4];
						stream.println(data);
					}

					stream.close();

					String message = "Succeeded to save " + file.getPath();
					JOptionPane.showMessageDialog(pane, message);
				}
			} catch ( IOException exception ) {
				String message = "Failed to save file.";
				JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * The <code>SaveAsMpcFormatListener</code> is a listener class of
	 * menu selection to save the positions in MPC format.
	 */
	protected class SaveAsMpcFormatListener implements ActionListener {
		/**
		 * Invoked when the menu is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			ArrayIndex index = getSortingIndex();
			if (index == null)
				return;

			MpcFormatSettingDialog dialog = new MpcFormatSettingDialog();
			int answer = dialog.show(pane);

			if (answer == 0) {
				try {
					CommonFileChooser file_chooser = new CommonFileChooser();
					file_chooser.setDialogTitle("Save selected data.");
					file_chooser.setMultiSelectionEnabled(false);

					if (file_chooser.showSaveDialog(pane) == JFileChooser.APPROVE_OPTION) {
						File file = file_chooser.getSelectedFile();
						if (file.exists()) {
							String message = "Overwrite to " + file.getPath() + " ?";
							if (0 != JOptionPane.showConfirmDialog(pane, message, "Confirmation", JOptionPane.YES_NO_OPTION)) {
								return;
							}
						}

						PrintStream stream = new PrintStream(new BufferedOutputStream(new FileOutputStream(file)));

						int check_column = getCheckColumn();
						for (int i = 0 ; i < model.getRowCount() ; i++) {
							if (((Boolean)getValueAt(i, check_column)).booleanValue()) {
								XmlPositionRecord record = (XmlPositionRecord)record_list.elementAt(index.get(i));

								MpcFormatRecord mpc_record = new MpcFormatRecord(record);
								mpc_record.setDesignation(dialog.getDesignation());
								mpc_record.setObservationType(dialog.getObservationType());
								mpc_record.setMagnitudeAccuracy(dialog.getMagnitudeAccuracy());
								mpc_record.setMagnitudeBand(dialog.getMagnitudeBand());
								mpc_record.setObservatoryCode(dialog.getObservatoryCode());

								stream.println(mpc_record.getOutputString());
							}
						}

						stream.close();

						String message = "Succeeded to save " + file.getPath();
						JOptionPane.showMessageDialog(pane, message);
					}
				} catch ( IOException exception ) {
					String message = "Failed to save file.";
					JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
}
