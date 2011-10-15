/*
 * @(#)ImageConversionTable.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.ImageConversion;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.io.*;
import java.net.*;
import java.util.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.gui.event.*;
import net.aerith.misao.gui.table.*;
import net.aerith.misao.gui.dialog.*;
import net.aerith.misao.image.*;
import net.aerith.misao.image.io.*;
import net.aerith.misao.image.filter.FilterSet;
import net.aerith.misao.io.*;
import net.aerith.misao.io.filechooser.ImageFileFilter;
import net.aerith.misao.util.Astro;
import net.aerith.misao.util.ArrayIndex;
import net.aerith.misao.util.Size;
import net.aerith.misao.toolkit.FilterSelection.FilterSelectionDialog;

/**
 * The <code>ImageConversionTable</code> represents a table where the 
 * images to be converted are added. It shows the status of the images 
 * and the progress of the operation.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 17
 */

public class ImageConversionTable extends FileOperationTable {
	/**
	 * The container pane.
	 */
	protected Container pane;

	/**
	 * Constructs an <code>ImageConversionTable</code>.
	 */
	public ImageConversionTable ( ) {
		pane = this;
	}

	/**
	 * Gets the column names. This method must be overrided in the 
	 * subclasses.
	 * @return the column names.
	 */
	protected String[] getColumnNames ( ) {
		String[] column_names = { "Status", "Input image", "Input image format", "Output image", "Output image format", "Transformation", "Filter" };
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

		String[] column_names = getColumnNames();
		for (int i = 1 ; i < column_names.length ; i++)
			column_model.getColumn(i).setCellRenderer(new StringRenderer(column_names[i], LabelTableCellRenderer.MODE_MULTIPLE_SELECTION));

		File dummy_file = new File("dummy");

		JComboBox combo_input_format = new JComboBox();
		ImageFileFilter[] filters = ImageFileFilter.getSupportedFilters();
		for (int i = 0 ; i < filters.length ; i++) {
			try {
				Format format = filters[i].getFormat(dummy_file);
				combo_input_format.addItem(format.getName());
			} catch ( MalformedURLException exception ) {
			} catch ( UnsupportedFileTypeException exception ) {
			}
		}

		JComboBox combo_output_format = new JComboBox();
		filters = ImageFileFilter.getBitmapFilters();
		for (int i = 0 ; i < filters.length ; i++) {
			try {
				Format format = filters[i].getFormat(dummy_file);
				combo_output_format.addItem(format.getName());
			} catch ( MalformedURLException exception ) {
			} catch ( UnsupportedFileTypeException exception ) {
			}
		}

		column_model.getColumn(1).setCellEditor(new InputImageEditor());
		column_model.getColumn(2).setCellEditor(new InputImageFormatEditor(combo_input_format));
		column_model.getColumn(3).setCellEditor(new OutputImageEditor());
		column_model.getColumn(4).setCellEditor(new OutputImageFormatEditor(combo_output_format));
		column_model.getColumn(5).setCellEditor(new TransformationEditor());
		column_model.getColumn(6).setCellEditor(new ImageProcessingFilterEditor());

		return column_model;
	}

	/**
	 * Gets the file drop target listener. This method must be 
	 * overrided in the subclasses.
	 * @return the file drop target listener.
	 */
	protected FileDropTargetAdapter getFileDropTargetListener ( ) {
		return new ImageFileDropTargetListener();
	}

	/**
	 * Initializes the column width.
	 */
	protected void initializeColumnWidth ( ) {
		int columns = column_model.getColumnCount();

		column_model.getColumn(0).setPreferredWidth(70);
		column_model.getColumn(1).setPreferredWidth(180);
		column_model.getColumn(2).setPreferredWidth(40);
		column_model.getColumn(3).setPreferredWidth(140);
		column_model.getColumn(4).setPreferredWidth(40);
		column_model.getColumn(5).setPreferredWidth(60);
		column_model.getColumn(6).setPreferredWidth(40);
	}

	/**
	 * Adds an image.
	 * @param file the image file.
	 * @exception FileNotFoundException if the file does not exist.
	 * @exception MalformedURLException if an unknown protocol is 
	 * specified.
	 * @exception UnsupportedBufferTypeException if the type of the
	 * FITS file is unsupported.
	 */
	public void addImage ( File file )
		throws FileNotFoundException, MalformedURLException, UnsupportedFileTypeException
	{
		addImage(file, Format.create(file));
	}

	/**
	 * Adds an image of the specified format.
	 * @param file   the image file.
	 * @param format the image file format.
	 */
	public void addImage ( File file, Format format ) {
		ImageRecord record = new ImageRecord(file, format);
		record_list.addElement(record);

		setRows();
	}

	/**
	 * Sets the image size of all output image files.
	 * @param size         the image size.
	 * @param rescale_sbig true when to rescale ST-4/6 Image.
	 */
	public void setOutputImageSize ( Size size, boolean rescale_sbig ) {
		for (int i = 0 ; i < record_list.size() ; i++) {
			ImageRecord record = (ImageRecord)record_list.elementAt(i);
			record.output_transformation = ImageRecord.TRANSFORMATION_SIZE;
			record.output_size = size;
			record.rescale_sbig = rescale_sbig;
		}

		repaint();
	}

	/**
	 * Sets the image scale of all output image files.
	 * @param scale the image scale.
	 * @param rescale_sbig true when to rescale ST-4/6 Image.
	 */
	public void setOutputImageScale ( int scale, boolean rescale_sbig ) {
		for (int i = 0 ; i < record_list.size() ; i++) {
			ImageRecord record = (ImageRecord)record_list.elementAt(i);
			record.output_transformation = ImageRecord.TRANSFORMATION_SCALE;
			record.output_scale = scale;
			record.rescale_sbig = rescale_sbig;
		}

		repaint();
	}

	/**
	 * Sets the image processing filter of all image files.
	 * @param filter_set the filter set.
	 */
	public void setImageProcessingFilter ( FilterSet filter_set ) {
		for (int i = 0 ; i < record_list.size() ; i++) {
			ImageRecord record = (ImageRecord)record_list.elementAt(i);
			record.filter_set = new FilterSet(filter_set);
		}

		repaint();
	}

	/**
	 * Sets the format of all output image files.
	 * @param filter          the <code>FileFilter</code> for the 
	 * format.
	 * @param change_filename true when to change output file names by
	 * default.
	 */
	public void setOutputImageFileFilter ( ImageFileFilter filter, boolean change_filename ) {
		for (int i = 0 ; i < record_list.size() ; i++) {
			try {
				ImageRecord record = (ImageRecord)record_list.elementAt(i);

				File file = new File(record.output_image);
				if (change_filename) {
					record.output_image = Format.getTruncatedFilename(file) + "." + filter.getTypicalExtension();
					file = new File(record.output_image);
				}

				record.output_format = filter.getFormat(file);
			} catch ( MalformedURLException exception ) {
			} catch ( UnsupportedFileTypeException exception ) {
			}
		}

		repaint();
	}

	/**
	 * Sets the format of all input image files.
	 * @param filter the <code>FileFilter</code> for the format.
	 */
	public void setInputImageFileFilter ( ImageFileFilter filter ) {
		for (int i = 0 ; i < record_list.size() ; i++) {
			try {
				ImageRecord record = (ImageRecord)record_list.elementAt(i);

				record.input_format = filter.getFormat(record.getFile());
			} catch ( MalformedURLException exception ) {
			} catch ( UnsupportedFileTypeException exception ) {
			}
		}

		repaint();
	}

	/**
	 * Gets the record at the specified row.
	 * @param row the row.
	 * @return the record.
	 */
	protected TableRecord getRecordAt ( int row ) {
		return super.getRecordAt(row);
	}

	/**
	 * Gets the output string of the cell.
	 * @param header_value the header value of the column.
	 * @param row          the index of row in original order.
	 * @return the output string of the cell.
	 */
	protected String getCellString ( String header_value, int row ) {
		String s = super.getCellString(header_value, row);
		if (s.length() > 0)
			return s;

		ImageRecord record = null;
		try {
			record = (ImageRecord)record_list.elementAt(row);
		} catch ( ArrayIndexOutOfBoundsException exception ) {
			return "";
		}

		if (header_value.equals("Input image")) {
			return record.getFile().getPath();
		}
		if (header_value.equals("Input image format")) {
			return record.input_format.getName();
		}
		if (header_value.equals("Output image")) {
			return record.output_image;
		}
		if (header_value.equals("Output image format")) {
			return record.output_format.getName();
		}
		if (header_value.equals("Transformation")) {
			s = "";
			if (record.output_transformation == ImageRecord.TRANSFORMATION_SIZE) {
				s = "" + record.output_size.getWidth() + " x " + record.output_size.getHeight();
			} else {
				s = "" + record.output_scale + " %";
			}
			if (record.rescale_sbig)
				s += " , SBIG ST-4/6";
			return s;
		}
		if (header_value.equals("Filter")) {
			if (record.filter_set.getFilters().length > 0) {
				return "Yes";
			}
		}

		return "";
	}

	/**
	 * Returns true if the objects are ready to be operated.
	 * @return true if the objects are ready to be operated.
	 */
	public boolean ready ( ) {
		if (super.ready() == false)
			return false;

		Vector filelist = new Vector();

		for (int i = 0 ; i < record_list.size() ; i++) {
			ImageRecord record = (ImageRecord)record_list.elementAt(i);

			File thisfile = new File(record.output_image);

			if (thisfile.getName().equals(record.output_image) == false) {
				// Output filename is illegal.
				String message = "Illegal filename: " + record.output_image;
				JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}

			for (int j = 0 ; j < filelist.size() ; j++) {
				File file = (File)filelist.elementAt(j);
				if (thisfile.compareTo(file) == 0) {
					// Output filename is duplicated.
					String message = "Duplicated filename: " + record.output_image;
					JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
					return false;
				}
			}

			filelist.addElement(thisfile);
		}

		return true;
	}

	/**
	 * The <code>InputImageEditor</code> represents a table cell 
	 * editor to select an image.
	 */
	protected class InputImageEditor extends ButtonCellEditor {
		/**
		 * Gets the cell editor listener. It must be overrided in the
		 * subclasses.
		 * @param object the object to edit.
		 * @param editor the cell editor.
		 * @return the cell editor listener.
		 */
		protected ButtonCellEditorListener getListener ( ) {
			return new AddImageListener();
		}

		/**
		 * Gets the object to edit.
		 * @param row    the row to edit.
		 * @param column the column to edit.
		 * @return the object to edit.
		 */
		protected Object getEditingObject ( int row, int column ) {
			return getRecordAt(row);
		}
	}

	/**
	 * The <code>AddImageListener</code> is a listener class of menu 
	 * selection to add an image.
	 */
	protected class AddImageListener extends ButtonCellEditorListener {
		/**
		 * Invoked when one of the menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			ImageFileOpenChooser file_chooser = new ImageFileOpenChooser();
			file_chooser.setDialogTitle("Open an image file.");
			file_chooser.setMultiSelectionEnabled(false);

			if (file_chooser.showOpenDialog(pane) == JFileChooser.APPROVE_OPTION) {
				try {
					File file = file_chooser.getSelectedFile();
					net.aerith.misao.image.io.Format format = null;
					try {
						format = file_chooser.getSelectedFileFormat();
					} catch ( UnsupportedFileTypeException exception ) {
						format = new Fits(file.toURI().toURL());
					}

					if (object != null) {
						ImageRecord record = (ImageRecord)object;
						record.setFile(file);
						record.input_format = format;
					}
				} catch ( MalformedURLException exception ) {
				} catch ( IOException exception ) {
				}
			}

			editor.stopCellEditing();
			repaint();
		}
	}

	/**
	 * The <code>InputImageFormatEditor</code> represents a table cell 
	 * editor to select an input image file format.
	 */
	protected class InputImageFormatEditor extends CommonCellEditor {
		/**
		 * Constructs an <code>InputImageFormatEditor</code>.
		 * @param combo the combo box to select a file format.
		 */
		public InputImageFormatEditor ( JComboBox combo ) {
			super(combo);
		}

		/**
		 * Gets the cell editor listener. It must be overrided in the
		 * subclasses.
		 * @return the cell editor listener.
		 */
		protected CommonCellEditorListener getListener ( ) {
			return new InputImageFormatEditorListener();
		}

		/**
		 * Gets the object to edit.
		 * @param row    the row to edit.
		 * @param column the column to edit.
		 * @return the object to edit.
		 */
		protected Object getEditingObject ( int row, int column ) {
			return getRecordAt(row);
		}
	}

	/**
	 * The <code>InputImageFormatEditorListener</code> is a listener
	 * class of input image file format selection.
	 */
	protected class InputImageFormatEditorListener extends CommonCellEditorListener {
	    /**
		 * This tells the listeners the editor has ended editing.
		 * @param e the event.
		 */
		public void editingStopped ( ChangeEvent e ) {
			DefaultCellEditor editor = (DefaultCellEditor)e.getSource();
			String value = (String)editor.getCellEditorValue();

			try {
				if (object != null) {
					ImageRecord record = (ImageRecord)object;
					ImageFileFilter[] filters = ImageFileFilter.getSupportedFilters();
					for (int i = 0 ; i < filters.length ; i++) {
						Format format = filters[i].getFormat(record.getFile());
						if (format.getName().equals(value))
							record.input_format = format;
					}
				}
			} catch ( MalformedURLException exception ) {
			} catch ( UnsupportedFileTypeException exception ) {
			}
		}
	}

	/**
	 * The <code>OutputImageEditor</code> represents a table cell 
	 * editor to input the image file name.
	 */
	protected class OutputImageEditor extends CommonCellEditor {
		/**
		 * Gets the cell editor listener. It must be overrided in the
		 * subclasses.
		 * @return the cell editor listener.
		 */
		protected CommonCellEditorListener getListener ( ) {
			return new OutputImageEditorListener();
		}

		/**
		 * Gets the object to edit.
		 * @param row    the row to edit.
		 * @param column the column to edit.
		 * @return the object to edit.
		 */
		protected Object getEditingObject ( int row, int column ) {
			return getRecordAt(row);
		}
	}

	/**
	 * The <code>OutputImageEditorListener</code> is a listenerclass
	 * of editing output image file name.
	 */
	protected class OutputImageEditorListener extends CommonCellEditorListener {
	    /**
		 * This tells the listeners the editor has ended editing.
		 * @param e the event.
		 */
		public void editingStopped ( ChangeEvent e ) {
			DefaultCellEditor editor = (DefaultCellEditor)e.getSource();
			String value = (String)editor.getCellEditorValue();

			try {
				if (value.length() > 0) {
					if (object != null) {
						ImageRecord record = (ImageRecord)object;
						record.output_image = value;
						record.output_format.setURL(new File(record.output_image).toURI().toURL());
					}
				}
			} catch ( MalformedURLException exception ) {
			}
		}
	}

	/**
	 * The <code>OutputImageFormatEditor</code> represents a table cell 
	 * editor to select an output image file format.
	 */
	protected class OutputImageFormatEditor extends CommonCellEditor {
		/**
		 * Constructs an <code>OutputImageFormatEditor</code>.
		 * @param combo the combo box to select a file format.
		 */
		public OutputImageFormatEditor ( JComboBox combo ) {
			super(combo);
		}

		/**
		 * Gets the cell editor listener. It must be overrided in the
		 * subclasses.
		 * @return the cell editor listener.
		 */
		protected CommonCellEditorListener getListener ( ) {
			return new OutputImageFormatEditorListener();
		}

		/**
		 * Gets the object to edit.
		 * @param row    the row to edit.
		 * @param column the column to edit.
		 * @return the object to edit.
		 */
		protected Object getEditingObject ( int row, int column ) {
			return getRecordAt(row);
		}
	}

	/**
	 * The <code>OutputImageFormatEditorListener</code> is a listener
	 * class of output image file format selection.
	 */
	protected class OutputImageFormatEditorListener extends CommonCellEditorListener {
	    /**
		 * This tells the listeners the editor has ended editing.
		 * @param e the event.
		 */
		public void editingStopped ( ChangeEvent e ) {
			DefaultCellEditor editor = (DefaultCellEditor)e.getSource();
			String value = (String)editor.getCellEditorValue();

			try {
				if (object != null) {
					ImageRecord record = (ImageRecord)object;
					File file = new File(record.output_image);
					ImageFileFilter[] filters = ImageFileFilter.getBitmapFilters();
					for (int i = 0 ; i < filters.length ; i++) {
						Format format = filters[i].getFormat(file);
						if (format.getName().equals(value))
							record.output_format = format;
					}
				}
			} catch ( MalformedURLException exception ) {
			} catch ( UnsupportedFileTypeException exception ) {
			}
		}
	}

	/**
	 * The <code>TransformationEditor</code> represents a table cell 
	 * editor to set the image size or scale.
	 */
	protected class TransformationEditor extends ButtonCellEditor {
		/**
		 * Gets the cell editor listener. It must be overrided in the
		 * subclasses.
		 * @param object the object to edit.
		 * @param editor the cell editor.
		 * @return the cell editor listener.
		 */
		protected ButtonCellEditorListener getListener ( ) {
			return new TransformationListener();
		}

		/**
		 * Gets the object to edit.
		 * @param row    the row to edit.
		 * @param column the column to edit.
		 * @return the object to edit.
		 */
		protected Object getEditingObject ( int row, int column ) {
			return getRecordAt(row);
		}
	}

	/**
	 * The <code>TransformationListener</code> is a listener class of 
	 * menu selection to set the image size or scale.
	 */
	protected class TransformationListener extends ButtonCellEditorListener {
		/**
		 * Invoked when one of the menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			if (object != null) {
				ImageRecord record = (ImageRecord)object;

				TransformationDialog dialog = new TransformationDialog();

				if (record.output_transformation == ImageRecord.TRANSFORMATION_SIZE) {
					dialog.selectSize();
					dialog.setImageSize(record.output_size);
				} else {
					dialog.selectScale();
					dialog.setScale(record.output_scale);
				}

				dialog.setRescaleSbig(record.rescale_sbig);

				int answer = dialog.show(pane);

				if (answer == 0) {
					if (dialog.isSize()) {
						record.output_transformation = ImageRecord.TRANSFORMATION_SIZE;
						record.output_size = dialog.getImageSize();
					} else {
						record.output_transformation = ImageRecord.TRANSFORMATION_SCALE;
						record.output_scale = dialog.getScale();
					}

					record.rescale_sbig = dialog.rescalesSbig();
				}
			}

			editor.stopCellEditing();
			repaint();
		}
	}

	/**
	 * The <code>ImageProcessingFilterEditor</code> represents a table 
	 * cell editor to set the image processing filter.
	 */
	protected class ImageProcessingFilterEditor extends ButtonCellEditor {
		/**
		 * Gets the cell editor listener. It must be overrided in the
		 * subclasses.
		 * @param object the object to edit.
		 * @param editor the cell editor.
		 * @return the cell editor listener.
		 */
		protected ButtonCellEditorListener getListener ( ) {
			return new ImageProcessingFilterListener();
		}

		/**
		 * Gets the object to edit.
		 * @param row    the row to edit.
		 * @param column the column to edit.
		 * @return the object to edit.
		 */
		protected Object getEditingObject ( int row, int column ) {
			return getRecordAt(row);
		}
	}

	/**
	 * The <code>ImageProcessingFilterListener</code> is a listener 
	 * class of menu selection to set the image processing filter.
	 */
	protected class ImageProcessingFilterListener extends ButtonCellEditorListener {
		/**
		 * Invoked when one of the menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			if (object != null) {
				ImageRecord record = (ImageRecord)object;

				FilterSelectionDialog dialog = new FilterSelectionDialog(record.filter_set);
				int answer = dialog.show(pane);

				if (answer == 0)
					record.filter_set = dialog.getFilterSet();
			}

			editor.stopCellEditing();
			repaint();
		}
	}

	/**
	 * The <code>ImageFileDropTargetListener</code> is a listener 
	 * class of drop event from native filer application.
	 */
	protected class ImageFileDropTargetListener extends FileDropTargetAdapter {
		/**
		 * Invoked when files are dropped.
		 * @param files the dropped files.
		 */
		public void dropFiles ( File[] files ) {
			if (isOperating())
				return;

			for (int i = 0 ; i < files.length ; i++) {
				try {
					Format format = null;
					try {
						format = Format.create(files[i]);
					} catch ( UnsupportedFileTypeException exception ) {
						format = new Fits(files[i].toURI().toURL());
					}
					addImage(files[i], format);
				} catch ( FileNotFoundException exception ) {
				} catch ( MalformedURLException exception ) {
				}
			}
		}
	}

	/**
	 * The <code>ImageRecord</code> is a set of input image file and 
	 * the output image file, and the configuration.
	 */
	protected class ImageRecord extends TableRecord {
		/**
		 * The input image file format;
		 */
		public Format input_format;

		/**
		 * The output image file name.
		 */
		protected String output_image;

		/**
		 * The output image file format.
		 */
		protected Format output_format;

		/**
		 * The output image size.
		 */
		protected Size output_size;

		/**
		 * The output image scale.
		 */
		protected int output_scale;

		/**
		 * True when to rescale SBIG ST-4/6 ratio.
		 */
		protected boolean rescale_sbig = false;

		/**
		 * The output image transformation, size or scale.
		 */
		protected int output_transformation = TRANSFORMATION_SIZE;

		/**
		 * The set of image processing filters.
		 */
		protected FilterSet filter_set = new FilterSet();

		/**
		 * The output image transformation number indicating size.
		 */
		protected final static int TRANSFORMATION_SIZE = 0;

		/**
		 * The output image transformation number indicating scale.
		 */
		protected final static int TRANSFORMATION_SCALE = 1;

		/**
		 * Construct an <code>ImageRecord</code>.
		 * @param file   the input image file. 
		 * @param format the input image file format.
		 */
		public ImageRecord ( File file, Format format ) {
			super(file);

			input_format = format;

			output_image = Format.getTruncatedFilename(file) + ".ppm";
			output_format = new Ppm(null);

			output_transformation = TRANSFORMATION_SCALE;
			output_scale = 100;
		}

		/**
		 * Gets the target of operation.
		 * @return the target of operation.
		 */
		public Object getOperationTarget ( ) {
			return this;
		}
	}
}
