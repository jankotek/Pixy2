/*
 * @(#)AgentNewStarSearchInternalFrame.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.AgentDesktop;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.gui.dialog.*;
import net.aerith.misao.xml.*;
import net.aerith.misao.pixy.Agent;
import net.aerith.misao.toolkit.NewStarSearch.NewStarSearchInternalFrame;

/**
 * The <code>AgentNewStarSearchInternalFrame</code> represents a frame 
 * to select XML report documents to search new stars using the agent.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2005 May 22
 */

public class AgentNewStarSearchInternalFrame extends NewStarSearchInternalFrame {
	/**
	 * The agent.
	 */
	protected Agent agent;

	/**
	 * The observer of new star search operation using the agent.
	 */
	protected OperationObserver agent_observer = null;

	/**
	 * The package file to be created, or null when not to save the
	 * package file.
	 */
	protected File package_file = null;

	/**
	 * The directory to create HTML image gallery, or null when not to
	 * create it.
	 */
	protected File gallery_directory = null;

	/**
	 * True when to reject single detections.
	 */
	protected boolean reject_single = false;

	/**
	 * True when to reject identified stars.
	 */
	protected boolean reject_identified = false;

	/**
	 * Constructs an <code>AgentNewStarSearchInternalFrame</code>.
	 * @param agent   the agent.
	 * @param desktop the parent desktop.
	 */
	public AgentNewStarSearchInternalFrame ( Agent agent, BaseDesktop desktop ) {
		super(desktop);

		this.agent = agent;

		try {
			setOperation(new AgentNewStarSearchOperation(table, desktop.getDBManager().getCatalogDBManager(), agent));
		} catch ( IOException exception ) {
		}

		// Disables due to proceeded to the next operation
		// except for interruption.
		control_panel.setSucceededMessageEnabled(false);
		control_panel.setInterruptedMessageEnabled(true);
		control_panel.setFailedMessageEnabled(false);
	}

	/**
	 * Sets an observer.
	 * @param observer an observer
	 */
	public void setAgentOperationObserver ( OperationObserver observer ) {
		agent_observer = observer;
	}

	/**
	 * Adds XML information documents. The XML file path must be 
	 * recorded in the information documents.
	 * @param infos the XML information documents.
	 * @exception IOException if I/O error occurs.
	 * @exception FileNotFoundException if a file does not exists.
	 */
	public void addInformations ( XmlInformation[] infos )
		throws IOException, FileNotFoundException
	{
		table.addInformations(infos, desktop.getFileManager());
	}

	/**
	 * Sets the package file, if to save the package file.
	 * @param file the package file.
	 */
	public void setPackageFile ( File file ) {
		package_file = file;
	}

	/**
	 * Sets the directory to create HTML image gallery, if to create.
	 * @param directory the directory.
	 */
	public void setHtmlImageGalleryDirectory ( File directory ) {
		gallery_directory = directory;
	}

	/**
	 * Sets the flag to reject single detections.
	 * @param flag true when to reject single detections.
	 */
	public void setSingleDetectionRejected ( boolean flag ) {
		reject_single = flag;
	}

	/**
	 * Sets the flag to reject identified stars.
	 * @param flag true when to reject identified stars.
	 */
	public void setIdentifiedStarsRejected ( boolean flag ) {
		reject_identified = flag;
	}

	/**
	 * Invoked when the operation ends.
	 * @param exception the exception if an error occurs, or null if
	 * succeeded.
	 */
	public void notifyEnd ( Exception exception ) {
		Variability[] records = operation.getVariabilityRecords();

		if (reject_single  ||  reject_identified) {
			ArrayList list = new ArrayList();
			for (int i = 0 ; i < records.length ; i++) {
				boolean valid = true;

				if (reject_single) {
					if (records[i].getObservations() <= 1)
						valid = false;
				}

				if (reject_identified) {
					if (records[i].getIdentifiedStar() != null)
						valid = false;
				}

				if (valid)
					list.add(records[i]);
			}

			records = new Variability[list.size()];
			records = (Variability[])list.toArray(records);
		}

		desktop.showVariabilityTable(records);

		if (exception instanceof InterruptedException) {
			if (agent_observer != null)
				agent_observer.notifyEnd(exception);
		} else {
			// Saves the package file.
			if (package_file != null) {
				try {
					XmlVariabilityHolder holder = new XmlVariabilityHolder();
					for (int i = 0 ; i < records.length ; i++) {
						XmlVariability variability = new XmlVariability(records[i]);
						holder.addVariability(variability);
					}

					holder.write(package_file);
				} catch ( IOException exception2 ) {
					if (exception == null)
						exception = exception2;
				}
			}

			// Creates HTML image gallery.
			if (gallery_directory != null) {
				Vector list = new Vector();
				for (int i = 0 ; i < records.length ; i++)
					list.addElement(records[i]);

				AgentHtmlImageGalleryInternalFrame frame = new AgentHtmlImageGalleryInternalFrame(list, agent.getNewStarSearchHtmlImageGalleryMode(), true, agent.getNewStarSearchHtmlImageGalleryPastImageMode(), agent.addsDssImageInHtmlImageGallery(), desktop);
				frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				frame.setSize(400,300);
				frame.setTitle("HTML Image Gallery");
				frame.setVisible(true);
				frame.setMaximizable(true);
				frame.setIconifiable(true);
				frame.setResizable(true);
				frame.setClosable(true);
				desktop.addFrame(frame);

				frame.setAgentOperationObserver(new HtmlImageGalleryObserver(agent_observer, exception));
				frame.setDirectory(gallery_directory);
				frame.setImageSize(agent.getHtmlGalleryImageSize());
				frame.setResolutionUnified(agent.unifiesHtmlGalleryImageResolution());
				if (agent.unifiesHtmlGalleryImageResolution())
					frame.setResolution(agent.getHtmlGalleryImageResolution());
				frame.setRotatedNorthUpAtRightAngles(agent.rotatesHtmlGalleryImageNorthUpAtRightAngles());

				// Starts creating HTML image gallery.
				frame.startOperation();
			}
		}
	}

	/**
	 * The <code>HtmlImageGalleryObserver</code> is an observer to 
	 * create HTML image gallery.
	 */
	protected class HtmlImageGalleryObserver implements OperationObserver {
		/**
		 * The parent observer.
		 */
		protected OperationObserver parent_observer = null;

		/**
		 * The exception thrown in the previous operation.
		 */
		protected Exception previous_exception = null;

		/**
		 * Constructs a <code>HtmlImageGalleryObserver</code>.
		 */
		public HtmlImageGalleryObserver ( OperationObserver parent_observer, Exception previous_exception ) {
			this.parent_observer = parent_observer;
			this.previous_exception = previous_exception;
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
			if (exception == null)
				exception = previous_exception;

			if (parent_observer != null)
				parent_observer.notifyEnd(exception);
		}

		/**
		 * Invoked when a task is succeeded.
		 * @param arg the argument.
		 */
		public void notifySucceeded ( Object arg ) {
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
