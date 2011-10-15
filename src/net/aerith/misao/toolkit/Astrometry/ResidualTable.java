/*
 * @(#)ResidualTable.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.Astrometry;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.io.*;
import java.net.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.*;
import net.aerith.misao.gui.event.*;
import net.aerith.misao.gui.table.*;
import net.aerith.misao.xml.*;

/**
 * The <code>ResidualTable</code> represents a table which shows the 
 * data and residuals of astrometry. It calculates the chart map 
 * function and distortion field using the data checked by the user.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 September 17
 */

public class ResidualTable extends SortableCheckTable {
	/**
	 * The XML report document.
	 */
	protected XmlReport report;

	/**
	 * The setting of astrometry.
	 */
	protected AstrometrySetting setting;

	/**
	 * The panel to show the astrometry result summary.
	 */
	protected SummaryPanel summary_panel;

	/**
	 * The chart map function.
	 */
	protected ChartMapFunction cmf;

	/**
	 * The distortion field.
	 */
	protected DistortionField df;

	/**
	 * The astrometric error.
	 */
	protected AstrometricError astrometric_error = null;

	/**
	 * The list of pairs.
	 */
	protected Vector pair_list = null;

	/**
	 * The original list of pairs.
	 */
	protected Vector orig_pair_list = null;

	/**
	 * The table model.
	 */
	protected DefaultTableModel model;

	/**
	 * True when to calculate the distortion field.
	 */
	protected boolean calculate_df = false;

	/**
	 * Constructs an <code>ResidualTable</code>.
	 * @param report        the XML report document.
	 * @param setting       the setting of Astrometry.
	 * @param summary_panel the panel to show the astrometry result 
	 * summary.
	 */
	public ResidualTable ( XmlReport report, AstrometrySetting setting, SummaryPanel summary_panel ) {
		this.report = report;
		this.setting = setting;
		this.summary_panel = summary_panel;

		updateContents();

		initPopupMenu();
	}

	/**
	 * Updates all contents.
	 */
	public void updateContents ( ) {
		orig_pair_list = ((XmlData)report.getData()).extractPairs(setting);

		// Clones the detected stars because the R.A. and Decl. 
		// or (x,y) will be dynamically changed.
		pair_list = new Vector();
		for (int i = 0 ; i < orig_pair_list.size() ; i++) {
			StarPair pair = (StarPair)orig_pair_list.elementAt(i);
			StarImage star_image = (StarImage)pair.getFirstStar();
			CatalogStar catalog_star = (CatalogStar)pair.getSecondStar();

			Star new_catalog_star = new Star();
			new_catalog_star.setCoor(new Coor(catalog_star.getCoor()));

			StarPair pair2 = new StarPair(new StarImage(star_image), new_catalog_star);
			pair_list.addElement(pair2);
		}

		index = new ArrayIndex(pair_list.size());

		int columns = 12;
		String[] column_names = new String[columns];
		column_names[0] = "";
		column_names[1] = "Observed position";
		column_names[2] = "Catalog position";
		column_names[3] = "Observed R.A.";
		column_names[4] = "Observed Decl.";
		column_names[5] = "Catalog R.A.";
		column_names[6] = "Catalog Decl.";
		column_names[7] = "Residual (R.A.)";
		column_names[8] = "Residual (Decl.)";
		column_names[9] = "Mag";
		column_names[10] = "Name";
		column_names[11] = "Data";

		model = new DefaultTableModel(column_names, 0);

		Object[] objects = new Object[columns];
		objects[0] = new Boolean(true);
		for (int i = 1 ; i < columns ; i++)
			objects[i] = "";
		for (int i = 0 ; i < pair_list.size() ; i++)
			model.addRow(objects);

		setModel(model);

		DefaultTableColumnModel column_model = (DefaultTableColumnModel)getColumnModel();
		for (int i = 1 ; i < column_names.length ; i++)
			column_model.getColumn(i).setCellRenderer(new StringRenderer(column_names[i], LabelTableCellRenderer.MODE_MULTIPLE_SELECTION));

		initializeCheckColumn();

		setTableHeader(new TableHeader(column_model));

		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		initializeColumnWidth();

		calculate();
	}

	/**
	 * Initializes the column width.
	 */
	protected void initializeColumnWidth ( ) {
		DefaultTableColumnModel column_model = (DefaultTableColumnModel)getColumnModel();
		int columns = column_model.getColumnCount();

		column_model.getColumn(0).setPreferredWidth(20);
		column_model.getColumn(1).setPreferredWidth(120);
		column_model.getColumn(2).setPreferredWidth(120);
		column_model.getColumn(3).setPreferredWidth(100);
		column_model.getColumn(4).setPreferredWidth(100);
		column_model.getColumn(5).setPreferredWidth(100);
		column_model.getColumn(6).setPreferredWidth(100);
		column_model.getColumn(7).setPreferredWidth(40);
		column_model.getColumn(8).setPreferredWidth(40);
		column_model.getColumn(9).setPreferredWidth(60);
		column_model.getColumn(10).setPreferredWidth(180);
		column_model.getColumn(11).setPreferredWidth(1000);
	}

	/**
	 * Sets the flag to calculate the distortion field.
	 * @param flag true when to calculate the distortion field.
	 */
	public void setCalculateDistortionField ( boolean flag ) {
		calculate_df = flag;

		calculate();
	}

	/**
	 * Calculates the chart map function, distortion field, etc. and 
	 * updates the result summary.
	 */
	private void calculate ( ) {
		int width = report.getInformation().getSize().getWidth();
		int height = report.getInformation().getSize().getHeight();
		double fwidth = (double)width;
		double fheight = (double)height;

		DefaultTableColumnModel column_model = (DefaultTableColumnModel)getColumnModel();
		int columns = column_model.getColumnCount();

		int check_column = getCheckColumn();

		Vector tmp_pair_list = new Vector();
		StarImageList list_detected = new StarImageList();
		StarList list_catalog = new StarList();
		for (int i = 0 ; i < pair_list.size() ; i++) {
			StarPair pair = (StarPair)pair_list.elementAt(index.get(i));
			StarImage star_image = (StarImage)pair.getFirstStar();
			Star catalog_star = (Star)pair.getSecondStar();

			list_detected.addElement(star_image);
			list_catalog.addElement(catalog_star);

			boolean checked = ((Boolean)getValueAt(i, check_column)).booleanValue();
			if (checked)
				tmp_pair_list.addElement(pair);
		}

		// Shifts (x,y) position of detected stars
		// so that (0,0) is at the center of the image.
		list_detected.shift(new Position(- fwidth / 2.0, - fheight / 2.0));

		cmf = new ChartMapFunction(new Coor(), 3600.0, 0.0);
		df = new DistortionField();

		// Calculates the initial chart map function.
		if (tmp_pair_list.size() > 0) {
			StarPair pair = (StarPair)pair_list.elementAt(index.get(0));
			Star catalog_star = (Star)pair.getSecondStar();
			cmf = new ChartMapFunction(catalog_star.getCoor(), 3600.0, 0.0);
		}

		// Sets the initial (x,y) position of the catalog data.
		list_catalog.mapCoordinatesToXY(cmf);

		if (tmp_pair_list.size() > 0) {
			for (int loop = 0 ; loop < 2 ; loop++) {
				// Calculates accurate image mapping without distortion field.
				MapFunction map_function = new MapFunction(tmp_pair_list);
				cmf = cmf.map(map_function.inverse());

				// Resets the (x,y) position of the catalog data without adjusting the distortion field.
				list_catalog.mapCoordinatesToXY(cmf);
			}

			if (calculate_df  &&  tmp_pair_list.size() > 2) {
				df = new DistortionField(pair_list);

				// Sets the (x,y) position of the catalog data.
				list_catalog.mapCoordinatesToXY(cmf, df);
			}
		}

		// Sets the R.A. and Decl. of the detected stars.
		list_detected.mapXYToCoordinates(cmf, df);
		list_detected.enableOutputCoordinates();

		astrometric_error = new AstrometricError(tmp_pair_list);

		summary_panel.setNumberOfStars(tmp_pair_list.size());
		summary_panel.setAstrometricError(astrometric_error);

		Position[] d_pos = new Position[5];
		d_pos[1] = df.getValue(new Position(- fwidth / 2.0, - fheight / 2.0));
		d_pos[2] = df.getValue(new Position(fwidth / 2.0, - fheight / 2.0));
		d_pos[0] = df.getValue(new Position());
		d_pos[3] = df.getValue(new Position(- fwidth / 2.0, fheight / 2.0));
		d_pos[4] = df.getValue(new Position(fwidth / 2.0, fheight / 2.0));
		summary_panel.setDistortionFieldResults(d_pos);

		summary_panel.updateLabels();

		// Shifts (x,y) position of detected stars and catalog data
		// so that (0,0) is at the top-left corner.
		list_detected.shift(new Position(fwidth / 2.0, fheight / 2.0));
		list_catalog.shift(new Position(fwidth / 2.0, fheight / 2.0));

		// Updates the table.
		repaint();
	}

	/**
	 * Applies the result of astrometry to the XML report document.
	 */
	public void applyAstrometry ( ) {
		((XmlSystem)report.getSystem()).setModifiedJD(JulianDay.create(new Date()));

		XmlInformation info = (XmlInformation)report.getInformation();

		int width = info.getSize().getWidth();
		int height = info.getSize().getHeight();
		double fwidth = (double)width;
		double fheight = (double)height;
		Size image_size = new Size(width, height);

		info.setInformation(image_size, cmf);
		info.setAstrometricError(astrometric_error);
		info.setDistortionField(df);

		XmlAstrometry Astrometry = new XmlAstrometry(setting);
		info.setAstrometry(Astrometry);

		XmlStar[] xml_stars = (XmlStar[])report.getData().getStar();
		for (int i = 0 ; i < xml_stars.length ; i++) {
			Vector records = xml_stars[i].getAllRecords();

			for (int j = 0 ; j < records.size() ; j++) {
				Star star = (Star)records.elementAt(j);

				// Shifts (x,y) position so that (0,0) is at the 
				// center of the image.
				star.add(new Position(- fwidth / 2.0, - fheight / 2.0));

				if (star instanceof StarImage)
					star.mapXYToCoordinates(cmf, df);
				else
					star.mapCoordinatesToXY(cmf, df);

				// Shifts (x,y) position so that (0,0) is at the 
				// top-left corner.
				star.add(new Position(fwidth / 2.0, fheight / 2.0));
			}
		}

		((XmlData)report.getData()).createStarMap(image_size);
	}

	/**
	 * Gets the output string of the cell.
	 * @param header_value the header value of the column.
	 * @param row          the index of row in original order.
	 * @return the output string of the cell.
	 */
	protected String getCellString ( String header_value, int row ) {
		if (orig_pair_list == null  ||  pair_list == null)
			return "";

		StarPair pair = (StarPair)orig_pair_list.elementAt(row);
		CatalogStar orig_catalog_star = (CatalogStar)pair.getSecondStar();
		pair = (StarPair)pair_list.elementAt(row);
		StarImage star_image = (StarImage)pair.getFirstStar();
		Star catalog_star = (Star)pair.getSecondStar();

		Coor err = star_image.getCoor().residual(catalog_star.getCoor());
		double delta_ra = err.getRA() * 3600.0;
		double delta_decl = err.getDecl() * 3600.0;

		if (header_value.equals("Observed position")) {
			return star_image.getPositionString();
		}
		if (header_value.equals("Catalog position")) {
			return catalog_star.getPositionString();
		}
		if (header_value.equals("Observed R.A.")) {
			StringTokenizer st = new StringTokenizer(star_image.getCoor().getOutputString());
			return st.nextToken();
		}
		if (header_value.equals("Observed Decl.")) {
			StringTokenizer st = new StringTokenizer(star_image.getCoor().getOutputString());
			st.nextToken();
			return st.nextToken();
		}
		if (header_value.equals("Catalog R.A.")) {
			StringTokenizer st = new StringTokenizer(catalog_star.getCoor().getOutputString());
			return st.nextToken();
		}
		if (header_value.equals("Catalog Decl.")) {
			StringTokenizer st = new StringTokenizer(catalog_star.getCoor().getOutputString());
			st.nextToken();
			return st.nextToken();
		}
		if (header_value.equals("Residual (R.A.)")) {
			String s = Format.formatDouble(Math.abs(delta_ra), 10, 8).trim();
			if (delta_ra < 0)
				return "-" + s;
			else
				return "+" + s;
		}
		if (header_value.equals("Residual (Decl.)")) {
			String s = Format.formatDouble(Math.abs(delta_decl), 10, 8).trim();
			if (delta_decl < 0)
				return "-" + s;
			else
				return "+" + s;
		}
		if (header_value.equals("Mag")) {
			return Format.formatDouble(star_image.getMag(), 5, 2).trim();
		}
		if (header_value.equals("Name")) {
			return orig_catalog_star.getName();
		}
		if (header_value.equals("Data")) {
			return orig_catalog_star.getOutputStringWithoutName();
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
		if (orig_pair_list == null  ||  pair_list == null)
			return null;

		SortableArray array = null;
		if (header_value.equals("")  ||
			header_value.equals("Observed position")  ||
			header_value.equals("Catalog position")  ||
			header_value.equals("Name")  ||
			header_value.equals("Data")) {
			array = new StringArray(pair_list.size());
		} else {
			array = new Array(pair_list.size());
		}

		for (int i = 0 ; i < pair_list.size() ; i++) {
			String s = getCellString(header_value, i);
			if (array instanceof Array) {
				if (s.equals("")) {
					((Array)array).set(i, -9999.9);
				} else {
					StarPair pair = (StarPair)pair_list.elementAt(i);
					StarImage star_image = (StarImage)pair.getFirstStar();
					Star catalog_star = (Star)pair.getSecondStar();

					if (header_value.equals("Observed R.A.")) {
						((Array)array).set(i, star_image.getCoor().getRA());
					} else if (header_value.equals("Observed Decl.")) {
						((Array)array).set(i, star_image.getCoor().getDecl());
					} else if (header_value.equals("Catalog R.A.")) {
						((Array)array).set(i, catalog_star.getCoor().getRA());
					} else if (header_value.equals("Catalog Decl.")) {
						((Array)array).set(i, catalog_star.getCoor().getDecl());
					} else {
						((Array)array).set(i, Format.doubleValueOf(s));
					}
				}
			} else {
				((StringArray)array).set(i, s);
			}
		}

		return array;
	}

	/**
	 * Invoked when the check box is edited.
	 */
	protected void checkEdited ( ) {
		calculate();
	}
}
