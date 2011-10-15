/*
 * @(#)IdentificationReportPane.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.IdentificationReport;
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
 * The <code>IdentificationReportPane</code> represents a base pane 
 * which shows all the identified stars data and the distance between 
 * them. It consists of three splitted panes, a tree of identified 
 * catalogs, a table of stars of the selected catalog, and a table of 
 * distance to the other records from the specified star in one XML 
 * star.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class IdentificationReportPane extends JSplitPane implements ReportDocumentUpdatedListener {
	/**
	 * The XML document.
	 */
	protected XmlReport report;

	/**
	 * The split pane in the right area.
	 */
	protected JSplitPane right_pane;

	/**
	 * The catalog tree panel.
	 */
	protected CatalogTreePanel catalog_tree_panel;

	/**
	 * The catalog tree.
	 */
	protected CatalogTree catalog_tree;

	/**
	 * The table of stars of the selected catalog.
	 */
	protected IdentifiedStarTable star_table;

	/**
	 * The panel of table of distance to the other records.
	 */
	protected RecordDistancePanel record_table_panel;

	/**
	 * Constructs an <code>IdentificationReportPane</code>.
	 * @param report the XML document.
	 */
	public IdentificationReportPane ( XmlReport report ) {
		setOrientation(HORIZONTAL_SPLIT);
		setDividerSize(1);

		this.report = report;

		Vector catalog_list = ((XmlData)report.getData()).getIdentifiedCatalogList();
		catalog_list.removeElement(new DetectedStar().getCatalogName());

		catalog_tree = new CatalogTree(catalog_list);
		catalog_tree_panel = new CatalogTreePanel(catalog_tree);
		catalog_tree.addCatalogTreeSelectionListener(new CatalogSelectionListener());
		star_table = new IdentifiedStarTable(report);
		star_table.addStarSelectionListener(new IdentifiedStarSelectionListener());
		record_table_panel = new RecordDistancePanel();

		setLeftComponent(catalog_tree_panel);

		right_pane = new JSplitPane();
		right_pane.setOrientation(VERTICAL_SPLIT);
		right_pane.setDividerSize(1);
		right_pane.setTopComponent(new JScrollPane(star_table));
		right_pane.setBottomComponent(record_table_panel);
		setRightComponent(right_pane);
	}

	/**
	 * Selects the first catalog and the first data.
	 */
	public void initialize ( ) {
//		catalog_tree.setSelectionRow(0);
//		star_table.setRowSelectionInterval(0, 0);

		setDividerLocation(catalog_tree_panel.getPanelWidth());
		right_pane.setDividerLocation(300);
	}

	/**
	 * Invoked when the measured magnitude of the detected stars are
	 * updated.
	 * @param updated_report the XML report document.
	 */
	public void photometryUpdated ( XmlReport updated_report ) {
		star_table.photometryUpdated(updated_report);
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

	/**
	 * The <code>CatalogSelectionListener</code> is a listener class
	 * of catalog selection.
	 */
	protected class CatalogSelectionListener implements CatalogTreeSelectionListener {
		/** 
		 * Invoked when the root node is selected.
		 */
		public void selectAll ( ) {
			Vector catalog_list = ((XmlData)report.getData()).getIdentifiedCatalogList();
			catalog_list.removeElement(new DetectedStar().getCatalogName());
			star_table.setCatalogList(catalog_list);
		}

		/** 
		 * Invoked when the specified catalog category is selected.
		 * @param category_name the category name.
		 */
		public void selectCategory ( String category_name ) {
			Vector catalog_list = ((XmlData)report.getData()).getIdentifiedCatalogList();
			catalog_list.removeElement(new DetectedStar().getCatalogName());

			Vector catalog_list_in_category = CatalogManager.getCatalogListInCategory(category_name);
			Hashtable hash = new Hashtable();
			for (int i = 0 ; i < catalog_list_in_category.size() ; i++)
				hash.put((String)catalog_list_in_category.elementAt(i), this);

			Vector list = new Vector();
			for (int i = 0 ; i < catalog_list.size() ; i++) {
				String catalog_name = (String)catalog_list.elementAt(i);
				if (hash.get(catalog_name) != null)
					list.addElement(catalog_name);
			}

			star_table.setCatalogList(list);
		}

		/** 
		 * Invoked when the specified catalog is selected.
		 * @param catalog_name the catalog name.
		 */
		public void selectCatalog ( String catalog_name ) {
			Vector list = new Vector();
			list.addElement(catalog_name);
			star_table.setCatalogList(list);

			star_table.setRowSelectionInterval(0, 0);
			star_table.revalidate();
		}

		/** 
		 * Invoked when the specified star is selected.
		 * @param star the star.
		 */
		public void selectStar ( Star star ) {
		}
	}

	/**
	 * The <code>IdentifiedStarSelectionListener</code> is a listener
	 * class of identified star selection.
	 */
	protected class IdentifiedStarSelectionListener implements StarSelectionListener {
		/** 
		 * Invoked when the specified star is selected.
		 * @param xml_star    the XML star object.
		 * @param record_star the record in the XML star object.
		 */
		public void select ( XmlStar xml_star, Star record_star ) {
			record_table_panel.setStar(xml_star, record_star);
		}
	}
}
