/*
 * @(#)ControlPanel.java
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
import net.aerith.misao.pixy.Resource;
import net.aerith.misao.util.*;

/**
 * The <code>ControlPanel</code> represents a control panel with four 
 * buttons: one is to set parameters, one is to start the operation, 
 * one is to stop the operation and one is to reset.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 17
 */

public class ControlPanel extends JPanel implements OperationObserver {
	/*
	 * The thread of the current operation.
	 */
	protected Thread thread = null;

	/*
	 * The operation.
	 */
	protected Operation operation;

	/**
	 * The mode.
	 */
	protected int mode = MODE_SETTING;

	/**
	 * The mode number which indicates in setting phase.
	 */
	public final static int MODE_SETTING = 0;

	/**
	 * The mode number which indicates in operating phase.
	 */
	public final static int MODE_OPERATING = 1;

	/**
	 * The mode number which indicates in stopped phase.
	 */
	public final static int MODE_STOPPED = 2;

	/**
	 * The content pane of this panel.
	 */
	protected Container pane;

	/*
	 * The button to set parameters.
	 */
	protected JButton button_set;

	/*
	 * The button to start the operation.
	 */
	protected JButton button_start;

	/*
	 * The button to stop the operation.
	 */
	protected JButton button_stop;

	/*
	 * The button to set ready.
	 */
	protected JButton button_reset;

	/*
	 * The menu items.
	 */
	protected JMenuItem[] menu_items;

	/**
	 * The listener to set parameters.
	 */
	protected SetListener listener_set;

	/**
	 * The listener to start the operation.
	 */
	protected StartListener listener_start;

	/**
	 * The listener to stop parameters.
	 */
	protected StopListener listener_stop;

	/**
	 * The listener to reset.
	 */
	protected ResetListener listener_reset;

	/**
	 * True when to show the succeeded message.
	 */
	protected boolean succeeded_message_flag = true;

	/**
	 * True when to show the interrupted message.
	 */
	protected boolean interrupted_message_flag = true;

	/**
	 * True when to show the failed message.
	 */
	protected boolean failed_message_flag = true;

	/**
	 * Constructs an <code>ControlPanel</code>.
	 */
	public ControlPanel ( ) {
		this(null);
	}

	/**
	 * Constructs a <code>ControlPanel</code>.
	 * @param operation the operation.
	 */
	public ControlPanel ( Operation operation ) {
		super();

		pane = this;

		if (operation != null)
			setOperation(operation);

		setLayout(new GridLayout(1, 4));

		button_set = new JButton(getSetButtonTitle());
		listener_set = new SetListener();
		button_set.addActionListener(listener_set);
		add(button_set);

		button_start = new JButton(getStartButtonTitle());
		listener_start = new StartListener();
		button_start.addActionListener(listener_start);
		add(button_start);

		button_stop = new JButton(getStopButtonTitle());
		listener_stop = new StopListener();
		button_stop.addActionListener(listener_stop);
		add(button_stop);

		button_reset = new JButton(getResetButtonTitle());
		listener_reset = new ResetListener();
		button_reset.addActionListener(listener_reset);
		add(button_reset);

		menu_items = new JMenuItem[4];
		menu_items[0] = new JMenuItem(getSetButtonTitle());
		menu_items[0].addActionListener(getSetListener());
		menu_items[1] = new JMenuItem(getStartButtonTitle());
		menu_items[1].addActionListener(getStartListener());
		menu_items[2] = new JMenuItem(getStopButtonTitle());
		menu_items[2].addActionListener(getStopListener());
		menu_items[3] = new JMenuItem(getResetButtonTitle());
		menu_items[3].addActionListener(getResetListener());

		updateButtons();
	}

	/**
	 * Sets the operation.
	 * @param operation the operation.
	 */
	public void setOperation ( Operation operation ) {
		this.operation = operation;
		if (this.operation != null)
			this.operation.addObserver(this);
	}

	/**
	 * Starts the operation.
	 * @return truen when the operation is successfully started.
	 */
	public boolean start ( ) {
		if (mode != MODE_SETTING)
			return false;

		ActionEvent e = null;
		listener_start.actionPerformed(e);

		if (mode == MODE_OPERATING)
			return true;

		return false;
	}

	/**
	 * Starts the operation. When the current operation is still 
	 * running, here waits until it ends.
	 */
	public void proceedOperation ( ) {
		new OperationProceedingThread(null).start();
	}

	/**
	 * Starts the specified operation. When the current operation is
	 * still running, here waits until it ends.
	 * @param new_operation the new operation to start.
	 */
	public void proceedOperation ( Operation new_operation ) {
		new OperationProceedingThread(new_operation).start();
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
		mode = MODE_STOPPED;
		updateButtons();

		if (exception == null) {
			if (succeeded_message_flag) {
				String message = "Succeeded.";
				JOptionPane.showMessageDialog(pane, message);
			}
		} else if (exception instanceof InterruptedException) {
			if (interrupted_message_flag) {
				String message = "Interrupted.";
				JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
			}
		} else {
			if (failed_message_flag) {
				String message = "Failed.";
				JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
			}
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

	/**
	 * Enables/disables to show the succeeded message.
	 * @param flag true when to show the message.
	 */
	public void setSucceededMessageEnabled ( boolean flag ) {
		succeeded_message_flag = flag;
	}

	/**
	 * Enables/disables to show the interrupted message.
	 * @param flag true when to show the message.
	 */
	public void setInterruptedMessageEnabled ( boolean flag ) {
		interrupted_message_flag = flag;
	}

	/**
	 * Enables/disables to show the failed message.
	 * @param flag true when to show the message.
	 */
	public void setFailedMessageEnabled ( boolean flag ) {
		failed_message_flag = flag;
	}

	/**
	 * Updates the enable/disable status of buttons based on the 
	 * current mode.
	 */
	private void updateButtons ( ) {
		button_set.setEnabled(false);
		button_start.setEnabled(false);
		button_stop.setEnabled(false);
		button_reset.setEnabled(false);
		for (int i = 0 ; i < menu_items.length ; i++)
			menu_items[i].setEnabled(false);

		switch (mode) {
			case MODE_SETTING:
				button_set.setEnabled(true);
				button_start.setEnabled(true);
				menu_items[0].setEnabled(true);
				menu_items[1].setEnabled(true);
				break;
			case MODE_OPERATING:
				button_stop.setEnabled(true);
				menu_items[2].setEnabled(true);
				break;
			case MODE_STOPPED:
				button_reset.setEnabled(true);
				menu_items[3].setEnabled(true);
				break;
		}
	}

	/*
	 * Gets the current mode.
	 * @return the current mode.
	 */
	public int getCurrentMode ( ) {
		return mode;
	}

	/*
	 * Gets the menu items.
	 * @return the menu items.
	 */
	public JMenuItem[] getMenuItems ( ) {
		return menu_items;
	}

	/**
	 * Gets the button title to set parameters. This must be overrided
	 * in the subclasses.
	 * @return the button title to set parameters.
	 */
	public String getSetButtonTitle ( ) {
		return "Set";
	}

	/**
	 * Gets the button title to start the operation. This must be 
	 * overrided in the subclasses.
	 * @return the button title to start the operation.
	 */
	public String getStartButtonTitle ( ) {
		return "Start";
	}

	/**
	 * Gets the button title to stop the operation. This must be 
	 * overrided in the subclasses.
	 * @return the button title to stop the operation.
	 */
	public String getStopButtonTitle ( ) {
		return "Stop";
	}

	/**
	 * Gets the button title to reset. This must be overrided in the
	 * subclasses.
	 * @return the button title to reset.
	 */
	public String getResetButtonTitle ( ) {
		return "Reset";
	}

	/**
	 * Gets the listener to set parameters.
	 * @return the listener to set parameters.
	 */
	public ActionListener getSetListener ( ) {
		return listener_set;
	}

	/**
	 * Gets the listener to start the operation.
	 * @return the listener to start the operation.
	 */
	public ActionListener getStartListener ( ) {
		return listener_start;
	}

	/**
	 * Gets the listener to stop the operation.
	 * @return the listener to stop the operation.
	 */
	public ActionListener getStopListener ( ) {
		return listener_stop;
	}

	/**
	 * Gets the listener to reset.
	 * @return the listener to reset.
	 */
	public ActionListener getResetListener ( ) {
		return listener_reset;
	}

	/**
	 * Invoked when the set button is pushed. This must be overrided 
	 * in the subclasses.
	 * @param e contains the selected menu item.
	 */
	public void actionPerformedSet ( ActionEvent e ) {
	}

	/**
	 * Invoked when the start button is pushed. This must be overrided 
	 * in the subclasses.
	 * @param e contains the selected menu item.
	 */
	public void actionPerformedStart ( ActionEvent e ) {
	}

	/**
	 * Invoked when the stop button is pushed. This must be overrided 
	 * in the subclasses.
	 * @param e contains the selected menu item.
	 */
	public void actionPerformedStop ( ActionEvent e ) {
	}

	/**
	 * Invoked when the reset button is pushed. This must be overrided 
	 * in the subclasses.
	 * @param e contains the selected menu item.
	 */
	public void actionPerformedReset ( ActionEvent e ) {
	}

	/**
	 * The <code>SetListener</code> is a listener class of menu 
	 * selection to set parameters.
	 */
	protected class SetListener implements ActionListener {
		/**
		 * Invoked when one of the menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			if (thread != null  &&  thread.isAlive())
				return;

			actionPerformedSet(e);
		}
	}

	/**
	 * The <code>StartListener</code> is a listener class of menu 
	 * selection to start the operation.
	 */
	protected class StartListener implements ActionListener {
		/**
		 * Invoked when one of the menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			if (thread != null  &&  thread.isAlive())
				return;

			if (operation != null) {
				if (operation.ready()) {
					thread = new Thread(operation);
					thread.setPriority(Resource.getThreadPriority());
					thread.start();

					actionPerformedStart(e);

					mode = MODE_OPERATING;
					updateButtons();
				}
			}
		}
	}

	/**
	 * The <code>StopListener</code> is a listener class of menu 
	 * selection to stop the operation.
	 */
	protected class StopListener implements ActionListener {
		/**
		 * Invoked when one of the menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			if (operation != null)
				operation.stop();

			actionPerformedStop(e);

			mode = MODE_STOPPED;
			updateButtons();
		}
	}

	/**
	 * The <code>ResetListener</code> is a listener class of menu 
	 * selection to reset.
	 */
	protected class ResetListener implements ActionListener {
		/**
		 * Invoked when one of the menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			if (thread != null  &&  thread.isAlive())
				return;

			actionPerformedReset(e);

			mode = MODE_SETTING;
			updateButtons();
		}
	}

	/**
	 * The <code>OperationProceedingThread</code> is a thread to 
	 * wait until the current operation ends and start the new
	 * operation.
	 */
	protected class OperationProceedingThread extends Thread {
		/**
		 * The new operation to start.
		 */
		protected Operation new_operation;

		/**
		 * Constructs an <code>OperationProceedingThread</code>.
		 * @param new_operation the new operation to start.
		 */
		public OperationProceedingThread ( Operation new_operation ) {
			this.new_operation = new_operation;
		}

		/**
		 * Run this thread.
		 */
		public void run ( ) {
			while (thread != null  &&  thread.isAlive()) {
				try {
					sleep(500);
				} catch ( InterruptedException exception ) {
					return;
				}
			}

			if (new_operation != null)
				setOperation(new_operation);

			ActionEvent e = null;
			listener_start.actionPerformed(e);
		}
	}
}
