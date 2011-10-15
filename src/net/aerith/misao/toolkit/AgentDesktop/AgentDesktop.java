/*
 * @(#)AgentDesktop.java
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
import net.aerith.misao.gui.*;
import net.aerith.misao.util.*;
import net.aerith.misao.xml.*;
import net.aerith.misao.pixy.Agent;

/**
 * The <code>AgentDesktop</code> represents a desktop for operation 
 * using an agent.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 17
 */

public class AgentDesktop extends BaseDesktop {
	/**
	 * The agent.
	 */
	protected Agent agent;

	/**
	 * The common setting dialog.
	 */
	protected AgentCommonSettingDialog dialog;

	/**
	 * The exception thrown in the operation.
	 */
	protected Exception operating_exception = null;

	/**
	 * This desktop.
	 */
	protected AgentDesktop desktop;

	/**
	 * The content pane of this frame.
	 */
	protected Container pane;

	/**
	 * Constructs an <code>AgentDesktop</code>.
	 * @param agent the agent.
	 */
	public AgentDesktop ( Agent agent ) {
		this.agent = agent;

		desktop = this;

		pane = getContentPane();

		addWindowListener(new OpenWindowListener());
	}

	/*
	 * Gets the monitor set.
	 * @return the monitor set.
	 */
	protected MonitorSet getMonitorSet ( ) {
		return monitor_set;
	}

	/**
	 * The <code>OpenWindowListener</code> is a listener class of
	 * opening this window.
	 */
	protected class OpenWindowListener extends WindowAdapter {
		/**
		 * Invoked when this window is opened.
		 * @param e contains the event status.
		 */
		public void windowOpened ( WindowEvent e ) {
			// Shows the dialog to configure agent-dependent setting.
			if (agent.initialize(desktop) == false)
				return;

			// Shows the common setting dialog.
			dialog = new AgentCommonSettingDialog();
			int answer = dialog.show(desktop);
			if (answer != 0)
				return;

			// Gets the instructions.
			XmlInstruction[] instructions = agent.getInstructions();

			// Saves the batch XML file.
			if (dialog.savesBatchXmlFile()) {
				try {
					XmlBatch batch = new XmlBatch();
					batch.setInstruction(instructions);
					batch.write(dialog.getBatchXmlFile());
				} catch ( IOException exception ) {
					operating_exception = exception;
				}
			}

			// Shows the instruction table.
			AgentBatchExaminationInternalFrame frame = new AgentBatchExaminationInternalFrame(agent, desktop);
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			frame.setSize(700,500);
			frame.setTitle("Image Instructions");
			frame.setVisible(true);
			frame.setMaximizable(true);
			frame.setIconifiable(true);
			frame.setResizable(true);
			addFrame(frame);

			frame.addMonitor(getMonitorSet());
			frame.setAgentOperationObserver(new BatchExaminationObserver());
			frame.addInstructions(instructions);

			// Starts the batch examination.
			frame.startOperation();
		}
	}

	/**
	 * The <code>BatchExaminationObserver</code> is an observer of
	 * batch examination.
	 */
	protected class BatchExaminationObserver implements OperationObserver {
		/*
		 * The list of image information elements.
		 */
		protected ArrayList list_info;

		/**
		 * Invoked when the operation starts.
		 */
		public void notifyStart ( ) {
			list_info = new ArrayList();
		}

		/**
		 * Invoked when the operation ends.
		 * @param exception the exception if an error occurs, or null if
		 * succeeded.
		 */
		public void notifyEnd ( Exception exception ) {
			if (exception != null) {
				if (exception instanceof InterruptedException) {
					String message = "Interrupted.";
					JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
					return;
				} else {
					operating_exception = exception;
				}
			}

			if (dialog.searchesNewStars() == false) {
				if (operating_exception == null) {
					String message = "Succeeded.";
					JOptionPane.showMessageDialog(pane, message);
				} else {
					String message = "Failed.";
					JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
				}

				return;
			}

			// Shows the information table to search new stars.
			AgentNewStarSearchInternalFrame frame = new AgentNewStarSearchInternalFrame(agent, desktop);
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			frame.setSize(700,500);
			frame.setTitle("XML Files");
			frame.setVisible(true);
			frame.setMaximizable(true);
			frame.setIconifiable(true);
			frame.setResizable(true);
			addFrame(frame);

			frame.addMonitor(getMonitorSet());
			frame.setAgentOperationObserver(new NewStarSearchObserver());
			if (dialog.savesPackageFile())
				frame.setPackageFile(dialog.getPackageFile());
			if (dialog.createsHtmlImageGallery())
				frame.setHtmlImageGalleryDirectory(dialog.getHtmlImageGalleryDirectory());
			frame.setSingleDetectionRejected(dialog.rejectsSingleDetections());
			frame.setIdentifiedStarsRejected(dialog.rejectsIdentifiedStars());
			try {
				XmlInformation[] infos = new XmlInformation[list_info.size()];
				infos = (XmlInformation[])list_info.toArray(infos);
				frame.addInformations(infos);
			} catch ( Exception exception2 ) {
				operating_exception = exception2;
			}

			// Starts the new star search.
			frame.startOperation();
		}

		/**
		 * Invoked when a task is succeeded.
		 * @param arg the argument.
		 */
		public void notifySucceeded ( Object arg ) {
			XmlReport report = (XmlReport)arg;
			XmlInformation info = new XmlInformation((XmlInformation)report.getInformation());
			list_info.add(info);
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

	/**
	 * The <code>NewStarSearchObserver</code> is an observer of new
	 * star search.
	 */
	protected class NewStarSearchObserver implements OperationObserver {
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
			if (exception != null) {
				if (exception instanceof InterruptedException) {
					String message = "Interrupted.";
					JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
					return;
				} else {
					operating_exception = exception;
				}
			}

			if (operating_exception == null) {
				String message = "Succeeded.";
				JOptionPane.showMessageDialog(pane, message);
			} else {
				String message = "Failed.";
				JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
			}
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
