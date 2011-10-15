/*
 * @(#)FileOperationTable.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui;
import java.awt.*;
import java.awt.event.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.io.*;
import java.net.*;
import java.util.*;
import net.aerith.misao.gui.event.*;
import net.aerith.misao.gui.table.*;
import net.aerith.misao.pixy.Resource;
import net.aerith.misao.io.*;
import net.aerith.misao.util.*;

/**
 * The <code>FileOperationTable</code> represents a table where the 
 * files are added to operate an operation. It shows the status of the 
 * files and the progress of the operation.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2007 May 4
 */

public class FileOperationTable extends SortableTable implements MultiTaskConductor {
	/**
	 * The list of file records.
	 */
	protected Vector record_list = new Vector();

	/**
	 * The table model.
	 */
	protected DefaultTableModel model;

	/**
	 * The table column model.
	 */
	protected DefaultTableColumnModel column_model;

	/**
	 * The drop target.
	 */
	protected DropTarget dt = null;

	/**
	 * The mode.
	 */
	protected int mode = MODE_SETTING;

	/**
	 * The mode number which indicates in setting phase.
	 */
	protected final static int MODE_SETTING = 0;

	/**
	 * The mode number which indicates in operating phase.
	 */
	protected final static int MODE_OPERATING = 1;

	/**
	 * The minimum number of rows.
	 */
	protected final static int minimum_rows = 30;

	/**
	 * The popup menu.
	 */
	protected JPopupMenu popup;

	/**
	 * The content pane of this frame.
	 */
	protected Container pane;

	/**
	 * Constructs a <code>FileOperationTable</code>.
	 */
	public FileOperationTable ( ) {
		pane = this;

		initPopupMenu();

		index = new ArrayIndex(record_list.size());

		String[] column_names = getColumnNames();

		model = new DefaultTableModel(column_names, 0);
		Object[] objects = new Object[column_names.length];
		for (int i = 0 ; i < column_names.length ; i++)
			objects[i] = "";
		for (int row = 0 ; row < minimum_rows ; row++)
			model.addRow(objects);

		setModel(model);

		column_model = createColumnModel();
		setTableHeader(new TableHeader(column_model));

		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		initializeColumnWidth();

		FileDropTargetAdapter drop_listener = getFileDropTargetListener();
		if (drop_listener != null) {
			dt = new DropTarget();
			try {
				dt.addDropTargetListener(drop_listener);
				setDropTarget(dt);
			} catch ( TooManyListenersException exception ) {
				System.err.println(exception);
			}
		}

		// Starts the polling thread.
		new PollingThread().start();
	}

	/**
	 * Initializes a popup menu. A <tt>popup</tt> must be created 
	 * previously.
	 */
	protected void initPopupMenu ( ) {
		popup = new JPopupMenu();

		JMenuItem item = new JMenuItem("Delete");
		item.addActionListener(new DeleteListener());
		popup.add(item);

		enableEvents(AWTEvent.MOUSE_EVENT_MASK);
	}

	/**
	 * Invoked when mouse event occurs. It is to show a click data
	 * dialog or popup menu.
	 * @param e contains the click position.
	 */
	protected void processMouseEvent ( MouseEvent e ) {
		if (e.isPopupTrigger()) {
			popup.show(e.getComponent(), e.getX(), e.getY());
		}
		super.processMouseEvent(e);
	}

	/**
	 * Gets the column names. This method must be overrided in the 
	 * subclasses.
	 * @return the column names.
	 */
	protected String[] getColumnNames ( ) {
		String[] column_names = { "Status" };
		return column_names;
	}

	/**
	 * Creates the column model. This method must be overrided in the 
	 * subclasses.
	 * @return the column model.
	 */
	protected DefaultTableColumnModel createColumnModel ( ) {
		column_model = (DefaultTableColumnModel)getColumnModel();
		column_model.getColumn(0).setCellRenderer(new StatusRenderer());
		return column_model;
	}

	/**
	 * Gets the file drop target listener. This method must be 
	 * overrided in the subclasses.
	 * @return the file drop target listener.
	 */
	protected FileDropTargetAdapter getFileDropTargetListener ( ) {
		return new DefaultFileDropTargetListener();
	}

	/**
	 * Initializes the column width.
	 */
	protected void initializeColumnWidth ( ) {
		column_model.getColumn(0).setPreferredWidth(100);
	}

	/**
	 * Adds a file.
	 * @param file the file.
	 */
	protected void addFile ( File file ) {
		TableRecord record = new TableRecord(file);
		record_list.addElement(record);

		setRows();
	}

	/**
	 * Adds files.
	 * @param files the files.
	 */
	protected void addFiles ( File[] files ) {
		for (int i = 0 ; i < files.length ; i++) {
			TableRecord record = new TableRecord(files[i]);
			record_list.addElement(record);
		}

		setRows();
	}

	/**
	 * Deletes a file record at the specified row.
	 * @param row the row.
	 */
	protected void deleteFileAt ( int row ) {
		ArrayIndex index = getSortingIndex();
		if (index == null)
			return;

		try {
			record_list.removeElementAt(index.get(row));
			model.removeRow(index.get(row));
		} catch ( ArrayIndexOutOfBoundsException exception ) {
			return;
		}

		setRows();
	}

	/**
	 * Deletes file recoreds at the specified rows.
	 * @param rows the rows.
	 */
	protected void deleteFilesAt ( int[] rows ) {
		ArrayIndex index = getSortingIndex();
		if (index == null)
			return;

		boolean[] delete_flag = new boolean[record_list.size()];
		for (int i = 0 ; i < record_list.size() ; i++)
			delete_flag[i] = false;

		for (int i = 0 ; i < rows.length ; i++) {
			try {
				delete_flag[index.get(rows[i])] = true;
			} catch ( ArrayIndexOutOfBoundsException exception ) {
			}
		}

		deleteFlaggedFiles(delete_flag);
	}

	/**
	 * Deletes flagged file recoreds.
	 * @param flags the array of flags in original order.
	 */
	protected void deleteFlaggedFiles ( boolean[] flags ) {
		for (int i = flags.length - 1 ; i >= 0 ; i--) {
			if (flags[i]) {
				record_list.removeElementAt(i);
				model.removeRow(i);
			}
		}

		setRows();
	}

	/**
	 * Sets the rows.
	 */
	protected void setRows ( ) {
		index = new ArrayIndex(record_list.size());

		String[] column_names = getColumnNames();

		int max_rows = record_list.size();
		if (max_rows < minimum_rows)
			max_rows = minimum_rows;
		for (int i = model.getRowCount() ; i < max_rows ; i++) {
			Object[] objects = new Object[column_names.length];
			for (int j = 0 ; j < column_names.length ; j++)
				objects[j] = "";
			model.addRow(objects);
		}

		repaint();
	}

	/**
	 * Returns true when sorting is acceptable.
	 * @return true when sorting is acceptable.
	 */
	protected boolean acceptsSorting ( ) {
		return (mode == MODE_SETTING);
	}

	/**
	 * Gets the record at the specified row.
	 * @param row the row.
	 * @return the record.
	 */
	protected TableRecord getRecordAt ( int row ) {
		ArrayIndex index = getSortingIndex();
		if (index == null)
			return null;

		try {
			return (TableRecord)record_list.elementAt(index.get(row));
		} catch ( ArrayIndexOutOfBoundsException exception ) {
			return null;
		}
	}

	/**
	 * Gets the output string of the cell.
	 * @param header_value the header value of the column.
	 * @param row          the index of row in original order.
	 * @return the output string of the cell.
	 */
	protected String getCellString ( String header_value, int row ) {
		TableRecord record = null;
		try {
			record = (TableRecord)record_list.elementAt(row);
		} catch ( ArrayIndexOutOfBoundsException exception ) {
			return "";
		}

		if (header_value.equals("Status")) {
			if (mode == MODE_OPERATING) {
				if (record.getStatus() == record.STATUS_DONE)
					return "Done";
				if (record.getStatus() == record.STATUS_FAILED)
					return "Failed";
				return "Ready";
			} else {
				if (record.getFile().exists())
					return "Exists";
				return "Not Found";
			}
		}

		return "";
	}

	/**
	 * Gets the sortable array of the specified column.
	 * @param header_value the header value of the column to sort.
	 */
	protected SortableArray getSortableArray ( String header_value ) {
		SortableArray array = new StringArray(record_list.size());

		for (int i = 0 ; i < record_list.size() ; i++) {
			String value = getCellString(header_value, i);
			((StringArray)array).set(i, value);
		}

		return array;
	}

	/**
	 * Returns true when operating.
	 * @return true when operating.
	 */
	public boolean isOperating ( ) {
		return (mode == MODE_OPERATING);
	}

	/**
	 * Resets to the setting mode.
	 */
	public void setReady ( ) {
		mode = MODE_SETTING;

		if (dt != null)
			dt.setActive(true);
	}

	/**
	 * Gets the content pane.
	 * @return the pane.
	 */
	public Container getPane ( ) {
		return pane;
	}

	/**
	 * Returns true if the objects are ready to be operated.
	 * @return true if the objects are ready to be operated.
	 */
	public boolean ready ( ) {
		for (int i = 0 ; i < record_list.size() ; i++) {
			TableRecord record = (TableRecord)record_list.elementAt(i);
			if (record.getFile().exists() == false) {
				String message = "File not found: " + record.getFile().getPath();
				JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}

		return true;
	}

	/**
	 * Operates the specified operation on several objects.
	 * @param operation the operation.
	 * @exception InterruptedException if the operatoin is stopped.
	 * @exception Exception if an error occurs.
	 */
	public void operate ( MultiTaskOperation operation )
		throws InterruptedException, Exception
	{
		mode = MODE_OPERATING;
		if (dt != null)
			dt.setActive(false);

		Vector target_list = getTargetList();

		for (int i = 0 ; i < target_list.size() ; i++) {
			TableRecord record = (TableRecord)target_list.elementAt(i);
			record.setStatus(TableRecord.STATUS_READY);
		}

		Exception operating_exception = null;

		for (int i = 0 ; i < target_list.size() ; i++) {
			if (operation != null  &&  operation.isStopped()) {
				throw new InterruptedException();
			}

			System.gc();
			Thread.sleep(1000);

			TableRecord record = (TableRecord)target_list.elementAt(i);

			try {
				operation.operate(record.getOperationTarget());

				record.setStatus(TableRecord.STATUS_DONE);
			} catch ( Exception exception ) {
				record.setStatus(TableRecord.STATUS_FAILED);
				operating_exception = exception;
			}
		}

		if (operating_exception != null)
			throw operating_exception;
	}

	/**
	 * Returns the list of target records for the operation. 
	 * @return the list of target records for the operation. 
	 */
	protected Vector getTargetList ( ) {
		Vector target_list = new Vector();

		for (int i = 0 ; i < record_list.size() ; i++)
			target_list.addElement(record_list.elementAt(index.get(i)));

		return target_list;
	}

	/**
	 * The <code>StatusRenderer</code> is a renderer for the status
	 * column.
	 */
	protected class StatusRenderer extends LabelTableCellRenderer {
		/**
		 * The header value.
		 */
		protected String header_value = "Status";

		/**
		 * Constructs a <code>StatusRenderer</code>.
		 */
		public StatusRenderer ( ) {
			super(LabelTableCellRenderer.MODE_MULTIPLE_SELECTION);
		}

		/**
		 * Constructs a <code>StatusRenderer</code>.
		 * @param header_value the header value.
		 */
		public StatusRenderer ( String header_value ) {
			super(LabelTableCellRenderer.MODE_MULTIPLE_SELECTION);

			this.header_value = header_value;
		}

		/**
		 * Gets the string of the specified row. It must be overrided in
		 * the subclasses.
		 * @param row the row.
		 * @return the string to show.
		 */
		public String getStringAt ( int row ) {
			ArrayIndex index = getSortingIndex();
			if (index == null)
				return null;

			try {
				return getCellString(header_value, index.get(row));
			} catch ( ArrayIndexOutOfBoundsException exception ) {
				return "";
			}
		}

		/**
		 * Gets the icon of the specified row. It must be overrided in the
		 * subclasses.
		 * @param row the row.
		 * @return the icon to show.
		 */
		public Icon getIconAt ( int row ) {
			String status = getStringAt(row);

			if (status.equals("Done"))
				return Resource.getStatusSuccessIcon();
			if (status.equals("Failed"))
				return Resource.getStatusErrorIcon();
			if (status.equals("Ready"))
				return Resource.getStatusMidwayIcon();
			if (status.equals("Exists"))
				return Resource.getStatusSuccessIcon();
			if (status.equals("Not Found"))
				return Resource.getStatusErrorIcon();

			return null;
		}
	}

	/**
	 * The <code>DefaultFileDropTargetListener</code> is a listener 
	 * class of drop event from native filer application.
	 */
	protected class DefaultFileDropTargetListener extends FileDropTargetAdapter {
		/**
		 * Invoked when files are dropped.
		 * @param files the dropped files.
		 */
		public void dropFiles ( File[] files ) {
			if (mode == MODE_OPERATING)
				return;

			addFiles(files);
		}
	}

	/**
	 * The <code>DeleteListener</code> is a listener class of menu 
	 * selection to delete selected rows.
	 */
	protected class DeleteListener implements ActionListener {
		/**
		 * Invoked when the menu is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			int[] rows = getSelectedRows();
			deleteFilesAt(rows);
		}
	}

	/**
	 * The <code>TableRecord</code> is a record of a file and the 
	 * status.
	 */
	protected class TableRecord {
		/**
		 * The file.
		 */
		protected File file;

		/**
		 * The status.
		 */
		protected int status = STATUS_READY;

		/**
		 * The status number which indicates the file is ready for
		 * the operation.
		 */
		protected final static int STATUS_READY = 0;

		/**
		 * The status number which indicates the file is operated.
		 */
		protected final static int STATUS_DONE = 1;

		/**
		 * The status number which indicates the file is failed to 
		 * be operated.
		 */
		protected final static int STATUS_FAILED = 2;

		/**
		 * Construct a <code>TableRecord</code>.
		 * @param file   the file. 
		 */
		public TableRecord ( File file ) {
			this.file = file;
		}

		/**
		 * Gets the file.
		 * @return the file.
		 */
		public File getFile ( ) {
			return file;
		}

		/**
		 * Sets the file.
		 * @param file the file.
		 */
		public void setFile ( File file ) {
			this.file = file;
		}

		/**
		 * Gets the status.
		 * @return the status.
		 */
		public int getStatus ( ) {
			return status;
		}

		/**
		 * Sets the status.
		 * @param status the status.
		 */
		public void setStatus ( int status ) {
			this.status = status;
		}

		/**
		 * Gets the target of operation.
		 * @return the target of operation.
		 */
		public Object getOperationTarget ( ) {
			return file;
		}
	}

	/**
	 * The <code>PollingThread</code> is a thread to poll files and
	 * update the status column.
	 */
	protected class PollingThread extends Thread {
		/**
		 * Runs this thread and update the status column.
		 */
		public void run ( ) {
			while (true) {
				repaint();

				try {
					Thread.sleep(1000);
				} catch ( InterruptedException exception ) {
					break;
				}
			}
		}
	}
}
