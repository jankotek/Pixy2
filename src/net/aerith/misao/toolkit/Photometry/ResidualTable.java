/*
 * @(#)ResidualTable.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.Photometry;
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
 * data and residuals of photometry. It calculates the magnitude 
 * translation formula using the data checked by the user.
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
	 * The setting of photometry.
	 */
	protected PhotometrySetting setting;

	/**
	 * The panel to show the photometry result summary.
	 */
	protected SummaryPanel summary_panel;

	/**
	 * The magnitude translation formula.
	 */
	protected MagnitudeTranslationFormula magnitude_translation_formula = new MagnitudeTranslationFormula();

	/**
	 * The photometric error.
	 */
	protected PhotometricError photometric_error = null;

	/**
	 * The list of pairs.
	 */
	protected Vector pair_list = null;

	/**
	 * The table model.
	 */
	protected DefaultTableModel model;

	/**
	 * Constructs an <code>ResidualTable</code>.
	 * @param report        the XML doreport cument.
	 * @param setting       the setting of photometry.
	 * @param summary_panel the panel to show the photometry result 
	 * summary.
	 */
	public ResidualTable ( XmlReport report, PhotometrySetting setting, SummaryPanel summary_panel ) {
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
		Vector orig_pair_list = ((XmlData)report.getData()).extractPairs(setting);

		// Clones the detected stars because the magnitude will be 
		// dynamically changed.
		pair_list = new Vector();
		for (int i = 0 ; i < orig_pair_list.size() ; i++) {
			StarPair pair = (StarPair)orig_pair_list.elementAt(i);
			StarImage star_image = (StarImage)pair.getFirstStar();
			CatalogStar catalog_star = (CatalogStar)pair.getSecondStar();

			StarPair pair2 = new StarPair(new StarImage(star_image), catalog_star);
			pair_list.addElement(pair2);
		}

		index = new ArrayIndex(pair_list.size());

		KeyAndValue[] key_and_values = new KeyAndValue[0];
		try {
			String class_name = CatalogManager.getCatalogStarClassName(setting.getCatalogName());
			if (class_name == null  ||  class_name.length() == 0) {
			} else {
				Class t = Class.forName(class_name);
				CatalogStar star = (CatalogStar)t.newInstance();

				key_and_values = star.getKeyAndValuesForPhotometry();
			}
		} catch ( ClassNotFoundException exception ) {
			System.err.println(exception);
		} catch ( IllegalAccessException exception ) {
			System.err.println(exception);
		} catch ( InstantiationException exception ) {
			System.err.println(exception);
		}

		int columns = 7 + key_and_values.length;
		String[] column_names = new String[columns];
		column_names[0] = "";
		column_names[1] = "Position";
		column_names[2] = "Observed mag";
		column_names[3] = "Catalog mag";
		column_names[4] = "Residual";
		column_names[5] = "Name";
		column_names[columns - 1] = "Data";
		for (int i = 6 ; i < columns - 1 ; i++)
			column_names[i] = key_and_values[i-6].getKey();

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
		column_model.getColumn(2).setPreferredWidth(60);
		column_model.getColumn(3).setPreferredWidth(60);
		column_model.getColumn(4).setPreferredWidth(60);
		column_model.getColumn(5).setPreferredWidth(180);
		for (int i = 6 ; i < columns - 1 ; i++)
			column_model.getColumn(i).setPreferredWidth(60);
		column_model.getColumn(columns - 1).setPreferredWidth(1000);
	}

	/**
	 * Calculates the magnitude translation formula, residuals, etc.
	 * and updates the result summary.
	 */
	private void calculate ( ) {
		DefaultTableColumnModel column_model = (DefaultTableColumnModel)getColumnModel();
		int columns = column_model.getColumnCount();

		int check_column = getCheckColumn();

		// Calculates the gradient of the magnitude system formula.
		if (setting.getMethod() == PhotometrySetting.METHOD_FREE_PHOTOMETRY) {
			Vector tmp_pair_list = new Vector();
			for (int i = 0 ; i < pair_list.size() ; i++) {
				boolean checked = ((Boolean)getValueAt(i, check_column)).booleanValue();
				if (checked) {
					StarPair pair = (StarPair)pair_list.elementAt(index.get(i));
					tmp_pair_list.addElement(pair);
				}
			}

			double gradient = MagnitudeSystem.calculateGradient(tmp_pair_list);
			setting.setGradientBV(gradient);
		}

		XmlInformation info = (XmlInformation)report.getInformation();
		Cubics magnitude_correction = null;
		if (info.getMagnitudeCorrection() != null)
			magnitude_correction = Cubics.create(info.getMagnitudeCorrection());

		Vector tmp_pair_list = new Vector();
		StarImageList list_detected = new StarImageList();
		for (int i = 0 ; i < pair_list.size() ; i++) {
			StarPair pair = (StarPair)pair_list.elementAt(index.get(i));
			StarImage star_image = (StarImage)pair.getFirstStar();
			CatalogStar catalog_star = (CatalogStar)pair.getSecondStar();
				
			list_detected.addElement(star_image);

			boolean checked = ((Boolean)getValueAt(i, check_column)).booleanValue();
			if (checked) {
				try {
					double catalog_mag = catalog_star.getMagnitude(setting.getMagnitudeSystem());

					// Clones the detected stars because the value is adjusted
					// based on the magnitude correction formula.
					if (magnitude_correction != null) {
						double x = star_image.getX() - (double)info.getSize().getWidth() / 2.0;
						double y = star_image.getY() - (double)info.getSize().getHeight() / 2.0;
						double delta_mag = magnitude_correction.getValue(x, y);
						double delta_value = Math.pow(Astro.MAG_STEP, delta_mag);

						star_image = new StarImage(star_image);
						star_image.setValue(star_image.getValue() / delta_value);
					}

					// Clones the catalog stars because the magnitude of the
					// specified system is recorded.
					Star star = new Star();
					star.setMag(catalog_mag);

					StarPair pair2 = new StarPair(star_image, star);
					tmp_pair_list.addElement(pair2);
				} catch ( UnsupportedMagnitudeSystemException exception ) {
				}
			}
		}
		summary_panel.setNumberOfStars(tmp_pair_list.size());

		if (tmp_pair_list.size() > 0) {
			if (setting.getMethod() == PhotometrySetting.METHOD_COMPARISON  &&  setting.gradientFixed() == false)
				magnitude_translation_formula = new MagnitudeTranslationFormula(tmp_pair_list, MagnitudeTranslationFormula.MODE_CALCULATE_GRADIENT);
			else
				magnitude_translation_formula = new MagnitudeTranslationFormula(tmp_pair_list, MagnitudeTranslationFormula.MODE_FIX_GRADIENT);
		} else {
			magnitude_translation_formula = new MagnitudeTranslationFormula();
		}
		summary_panel.setMagnitudeTranslationFormula(magnitude_translation_formula);

		list_detected.setMagnitude(magnitude_translation_formula);

		if (magnitude_correction != null) {
			for (int i = 0 ; i < list_detected.size() ; i++) {
				StarImage star_image = (StarImage)list_detected.elementAt(i);
				double x = star_image.getX() - (double)info.getSize().getWidth() / 2.0;
				double y = star_image.getY() - (double)info.getSize().getHeight() / 2.0;
				star_image.setMag(star_image.getMag() + magnitude_correction.getValue(x, y));
			}
		}

		photometric_error = new PhotometricError(tmp_pair_list);
		summary_panel.setPhotometricError(photometric_error.getError());

		summary_panel.setGradientBV(setting.getGradientBV());

		summary_panel.updateLabels();

		// Updates the observed magnitude.
		repaint();
	}

	/**
	 * Applies the result of photometry to the XML report document.
	 */
	public void applyPhotometry ( ) {
		((XmlSystem)report.getSystem()).setModifiedJD(JulianDay.create(new Date()));

		XmlInformation info = (XmlInformation)report.getInformation();

		MagnitudeTranslationFormula old_formula = MagnitudeTranslationFormula.create(info.getMagnitudeTranslationFormula());

		double value = old_formula.convertToPixelValue(info.getLimitingMag());
		double mag = magnitude_translation_formula.convertToMagnitude(value);
		info.setFormattedLimitingMag(mag);

		value = old_formula.convertToPixelValue(info.getProperUpperLimitMag());
		mag = magnitude_translation_formula.convertToMagnitude(value);
		info.setFormattedUpperLimitMag(mag);

		if (photometric_error != null)
			info.setPhotometricError(photometric_error);

		info.setMagnitudeTranslationFormula(magnitude_translation_formula);

		XmlPhotometry photometry = new XmlPhotometry(setting);
		info.setPhotometry(photometry);

		Cubics magnitude_correction = null;
		if (info.getMagnitudeCorrection() != null)
			magnitude_correction = Cubics.create(info.getMagnitudeCorrection());

		XmlStar[] xml_stars = (XmlStar[])report.getData().getStar();
		for (int i = 0 ; i < xml_stars.length ; i++) {
			StarImage star_image = xml_stars[i].getStarImage();
			if (star_image != null) {
				mag = magnitude_translation_formula.convertToMagnitude(star_image.getValue());
				if (magnitude_correction != null) {
					double x = star_image.getX() - (double)info.getSize().getWidth() / 2.0;
					double y = star_image.getY() - (double)info.getSize().getHeight() / 2.0;
					mag += magnitude_correction.getValue(x, y);
				}
				star_image.setMag(mag);
			}
		}
	}

	/**
	 * Gets the output string of the cell.
	 * @param header_value the header value of the column.
	 * @param row          the index of row in original order.
	 * @return the output string of the cell.
	 */
	protected String getCellString ( String header_value, int row ) {
		if (pair_list == null)
			return "";

		StarPair pair = (StarPair)pair_list.elementAt(row);
		StarImage star_image = (StarImage)pair.getFirstStar();
		CatalogStar catalog_star = (CatalogStar)pair.getSecondStar();

		if (header_value.equals("Position")) {
			return star_image.getPositionString();
		}
		if (header_value.equals("Observed mag")) {
			return Format.formatDouble(star_image.getMag(), 5, 2).trim();
		}
		if (header_value.equals("Catalog mag")) {
			try {
				if (setting.getMethod() == PhotometrySetting.METHOD_INSTRUMENTAL_PHOTOMETRY  ||
					setting.getMethod() == PhotometrySetting.METHOD_FREE_PHOTOMETRY) {
					double catalog_mag = catalog_star.getMagnitude(setting.getMagnitudeSystem());
					return Format.formatDouble(catalog_mag, 5, 2).trim();
				} else {
					return catalog_star.getMagnitudeString(setting.getMagnitudeSystem().getSystemCode());
				}
			} catch ( UnsupportedMagnitudeSystemException exception ) {
				return "";
			}
		}
		if (header_value.equals("Residual")) {
			try {
				double catalog_mag = catalog_star.getMagnitude(setting.getMagnitudeSystem());

				double residual = star_image.getMag() - catalog_mag;
				return Format.formatDouble(residual, 6, 3).trim();
			} catch ( UnsupportedMagnitudeSystemException exception ) {
				return "";
			}
		}
		if (header_value.equals("Name")) {
			return catalog_star.getName();
		}
		if (header_value.equals("Data")) {
			return catalog_star.getOutputStringWithoutName();
		}

		if (header_value.length() > 0) {
			KeyAndValue[] key_and_values = catalog_star.getKeyAndValuesForPhotometry();
			for (int i = 0 ; i < key_and_values.length ; i++) {
				if (key_and_values[i].getKey().equals(header_value))
					return key_and_values[i].getValue();
			}
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
		if (pair_list == null)
			return null;

		SortableArray array = null;
		if (header_value.equals("")  ||
			header_value.equals("Position")  ||
			header_value.equals("Name")  ||
			header_value.equals("Data")) {
			array = new StringArray(pair_list.size());
		} else {
			array = new Array(pair_list.size());
		}

		for (int i = 0 ; i < pair_list.size() ; i++) {
			String s = getCellString(header_value, i);
			if (array instanceof Array) {
				if (s.equals(""))
					((Array)array).set(i, -9999.9);
				else
					((Array)array).set(i, Format.doubleValueOf(s));
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
