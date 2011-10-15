/*
 * @(#)IdentifiedStarChartPane.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.MultipleCatalogChart;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.gui.event.*;
import net.aerith.misao.xml.*;

/**
 * The <code>IdentifiedStarChartPane</code> represents a base pane 
 * which shows the identified star chart and the property table.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class IdentifiedStarChartPane extends JSplitPane implements ReportDocumentUpdatedListener {
	/**
	 * The XML document.
	 */
	protected XmlReport report;

	/**
	 * The chart.
	 */
	protected ChartComponent chart;

	/**
	 * The property table.
	 */
	protected PlotPropertyTable table;

	/**
	 * Constructs an <code>IdentifiedStarChartPane</code>.
	 * @param report the XML document.
	 */
	public IdentifiedStarChartPane ( XmlReport report ) {
		setOrientation(VERTICAL_SPLIT);
		setDividerSize(1);

		this.report = report;

		Size size = new Size(report.getInformation().getSize().getWidth(), report.getInformation().getSize().getHeight());

		chart = new ChartComponent(size);
		table = new PlotPropertyTable();

		photometryUpdated(report);

		table.addPropertyChangedListener(chart);

		setTopComponent(new JScrollPane(chart));
		setBottomComponent(new JScrollPane(table));
	}

	/**
	 * Invoked when the measured magnitude of the detected stars are
	 * updated.
	 * @param updated_report the XML report document.
	 */
	public void photometryUpdated ( XmlReport updated_report ) {
		XmlData data = (XmlData)report.getData();
		int star_count = data.getStarCount();
		StarList list = new StarList();
		XmlStar[] stars = (XmlStar[])data.getStar();
		for (int i = 0 ; i < star_count ; i++) {
			Vector l = (stars[i]).getAllRecords();
			for (int j = 0 ; j < l.size() ; j++)
				list.addElement((Star)l.elementAt(j));
		}

		chart.setBasepointAtTopLeft();
		chart.setStarPositionList(list);
		chart.setBackground(Color.white);

		PlotProperty default_property = new PlotProperty();
		default_property.setColor(Color.gray);
		default_property.setFilled(true);
		default_property.setDependentSizeParameters(1.0, report.getInformation().getLimitingMag(), 1);
		default_property.setMark(PlotProperty.PLOT_CIRCLE);

		chart.setDefaultProperty(default_property);

		Vector catalog_list = ((XmlData)report.getData()).getIdentifiedCatalogList();
		for (int i = 0 ; i < catalog_list.size() ; i++) {
			String catalog_name = (String)catalog_list.elementAt(i);
			PlotProperty property = CatalogManager.getDefaultProperty(catalog_name);
			if (property.isSizeFixed() == false)
				property.setLimitingMag(report.getInformation().getLimitingMag());

			if (catalog_name.equals(new DetectedStar().getCatalogName()))
				property = default_property;

			chart.setProperty(catalog_name, property);
			table.setProperty(catalog_name, property);
		}
	}

	/**
	 * Invoked when the measured position of the detected stars are
	 * updated.
	 * @param updated_report the XML report document.
	 */
	public void astrometryUpdated ( XmlReport updated_report ) {
	}

	/**
	 * Invoked when some stars are added, removed or replaced.
	 * @param updated_report the XML report document.
	 */
	public void starsUpdated ( XmlReport updated_report ) {
	}

	/**
	 * Invoked when the image date is updated.
	 * @param updated_report the XML report document.
	 */
	public void dateUpdated ( XmlReport updated_report ) {
	}

	/**
	 * Invoked when a secondary record, like instruments, is updated.
	 * @param updated_report the XML report document.
	 */
	public void recordUpdated ( XmlReport updated_report ) {
	}
}
