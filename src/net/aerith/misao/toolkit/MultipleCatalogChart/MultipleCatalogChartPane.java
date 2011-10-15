/*
 * @(#)MultipleCatalogChartPane.java
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
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.gui.event.*;
import net.aerith.misao.catalog.CatalogManager;
import net.aerith.misao.database.CatalogDBManager;

/**
 * The <code>MultipleCatalogChartPane</code> represents a base pane 
 * which shows the chart where several catalogs are plotted, and the 
 * property table.
 * <p>
 * Note that the (x,y) position of specified stars must be set 
 * properly so that (0,0) represents the center.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2007 March 11
 */

public class MultipleCatalogChartPane extends JSplitPane implements ChartEditionListener {
	/**
	 * The catalog database manager.
	 */
	protected CatalogDBManager db_manager;

	/**
	 * The list of stars.
	 */
	protected StarList list;

	/**
	 * The name of the base catalog.
	 */
	protected String base_catalog;

	/**
	 * The limiting magnitude.
	 */
	protected double limit_mag;

	/**
	 * The chart.
	 */
	protected EdittableChartComponent chart;

	/**
	 * The property table.
	 */
	protected PlotPropertyTable table;

	/**
	 * The content pane of this panel.
	 */
	protected Container pane;

	/**
	 * Constructs a <code>MultipleCatalogChartPane</code>.
	 * @param base_list  the list of stars of base star catalog.
	 * @param size       the chart window size.
	 * @param coor       the R.A. and Decl. of the center.
	 * @param fov_width  the horizontal field of view in degree.
	 * @param fov_height the vertical field of view in degree.
	 * @param limit_mag  the limiting magnitude.
	 */
	public MultipleCatalogChartPane ( StarList base_list, Size size, Coor coor, double fov_width, double fov_height, double limit_mag ) {
		this.limit_mag = limit_mag;

		pane = this;

		// Copies the list, because the list can be editted
		// in EdittableChartComponent.
		list = new StarList();
		for (int i = 0 ; i < base_list.size() ; i++)
			list.addElement((Star)base_list.elementAt(i));
		
		base_catalog = "";
		if (list.size() > 0) {
			CatalogStar star = (CatalogStar)list.elementAt(0);
			base_catalog = star.getCatalogName();
		}

		setOrientation(VERTICAL_SPLIT);
		setDividerSize(1);

		chart = new EdittableChartComponent(size, coor, fov_width, fov_height);
		chart.setBasepointAtCenter();
		chart.setStarPositionList(list);
		chart.setBackground(Color.white);
		chart.addChartEditionListener(this);

		table = new PlotPropertyTable();

		update();

		table.addPropertyChangedListener(chart);

		setTopComponent(new JScrollPane(chart));
		setBottomComponent(new JScrollPane(table));

		setDividerLocation(size.getHeight());
	}

	/**
	 * Sets the catalog database.
	 * @param manager the catalog database manager.
	 */
	public void setCatalogDBManager ( CatalogDBManager manager ) {
		chart.setCatalogDBManager(manager);
	}

	/**
	 * Adds stars.
	 * @param list the list of stars.
	 */
	public void addStars ( StarList list ) {
		for (int i = 0 ; i < list.size() ; i++)
			this.list.addElement((Star)list.elementAt(i));

		update();
	}

	/**
	 * Invoked when stars are added to the chart.
	 * @param list the list of stars added to the chart.
	 */
	public void starsAdded ( StarList list ) {
		update();
	}

	/**
	 * Updates the chart and property table.
	 */
	protected void update ( ) {
		PlotProperty default_property = new PlotProperty();
		default_property.setColor(Color.gray);
		default_property.setFilled(true);
		default_property.setDependentSizeParameters(1.0, limit_mag, 1);
		default_property.setMark(PlotProperty.PLOT_CIRCLE);

		chart.setDefaultProperty(default_property);

		Hashtable hash_catalogs = new Hashtable();
		for (int i = 0 ; i < list.size() ; i++) {
			CatalogStar star = (CatalogStar)list.elementAt(i);
			hash_catalogs.put(star.getCatalogName(), star);
		}

		Enumeration catalogs = hash_catalogs.keys();
		while (catalogs.hasMoreElements()) {
			String catalog_name = (String)catalogs.nextElement();

			PlotProperty property = CatalogManager.getDefaultProperty(catalog_name);
			if (property.isSizeFixed() == false)
				property.setLimitingMag(limit_mag);

			if (catalog_name.equals(base_catalog))
				property = default_property;

			chart.setProperty(catalog_name, property);
			table.setProperty(catalog_name, property);
		}

		table.initializeColumnWidth();
	}
}
