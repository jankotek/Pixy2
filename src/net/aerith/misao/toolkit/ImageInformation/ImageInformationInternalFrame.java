/*
 * @(#)ImageInformationInternalFrame.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.ImageInformation;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.File;
import net.aerith.misao.util.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.xml.XmlInformation;
import net.aerith.misao.pixy.Resource;
import net.aerith.misao.toolkit.PixyDesktop.PixyReviewDesktop;

/**
 * The <code>ImageInformationInternalFrame</code> represents a frame
 * which consists of the XML file name, a button to open the PIXY 
 * System desktop for review, and the table of image information.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class ImageInformationInternalFrame extends BaseInternalFrame {
	/**
	 * The image information.
	 */
	protected XmlInformation info;

	/**
	 * The parent desktop.
	 */
	protected BaseDesktop desktop;

	/**
	 * The table.
	 */
	protected ImageInformationTable table;

	/**
	 * The content pane of this frame.
	 */
	protected Container pane;

	/**
	 * Constructs an <code>ImageInformationInternalFrame</code>.
	 * @param info    the image information.
	 * @param desktop the parent desktop.
	 */
	public ImageInformationInternalFrame ( XmlInformation info, BaseDesktop desktop ) {
		this.info = info;
		this.desktop = desktop;

		pane = getContentPane();

		JPanel panel = new JPanel();

		panel.setLayout(new BorderLayout());

		JLabel label_title = new JLabel(info.getPath());

		JButton button_review = new JButton("Review Examination");
		button_review.addActionListener(new ReviewListener());

		JPanel panel_title = new JPanel();
		panel_title.setLayout(new BorderLayout());
		panel_title.add(label_title, BorderLayout.CENTER);
		panel_title.add(button_review, BorderLayout.EAST);

		table = new ImageInformationTable(info);

		panel.add(panel_title, BorderLayout.NORTH);
		panel.add(new JScrollPane(table), BorderLayout.CENTER);

		pane.add(panel);
	}

	/**
	 * Updates the contents.
	 * @param info the image information.
	 */
	public void updateContents ( XmlInformation info ) {
		table.updateContents(info);
	}

	/**
	 * The <code>ReviewListener</code> is a listener class of menu 
	 * selection to open the PIXY review desktops.
	 */
	protected class ReviewListener implements ActionListener, Runnable {
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
			if (info.getPath() != null) {
				PixyReviewDesktop new_desktop = new PixyReviewDesktop();
				new_desktop.setHelpMessageEnabled(false);
				new_desktop.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				new_desktop.setSize(800,600);
				new_desktop.setTitle("PIXY System Desktop");
				new_desktop.setVisible(true);

				File file = desktop.getFileManager().newFile(info.getPath());
				new_desktop.operate(file);
			}
		}
	}
}
