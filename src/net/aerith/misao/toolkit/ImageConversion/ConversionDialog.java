/*
 * @(#)ConversionDialog.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.ImageConversion;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import java.net.*;
import net.aerith.misao.util.Size;
import net.aerith.misao.io.filechooser.ImageFileFilter;
import net.aerith.misao.image.*;
import net.aerith.misao.image.io.*;
import net.aerith.misao.image.filter.FilterSet;
import net.aerith.misao.gui.dialog.Dialog;
import net.aerith.misao.toolkit.FilterSelection.FilterSelectionDialog;

/**
 * The <code>ConversionDialog</code> represents a dialog to set up
 * parameters to convert the format of and transform all of the 
 * images.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 17
 */

public class ConversionDialog extends Dialog {
	/**
	 * The check box to select the input image format.
	 */
	protected JCheckBox check_input_format;

	/**
	 * The combo box to select the input image format.
	 */
	protected JComboBox combo_input_format;

	/**
	 * The list of input image file filters.
	 */
	protected ImageFileFilter[] input_filters;

	/**
	 * The check box to select the output image format.
	 */
	protected JCheckBox check_output_format;

	/**
	 * The combo box to select the output image format.
	 */
	protected JComboBox combo_output_format;

	/**
	 * The list of output image file filters.
	 */
	protected ImageFileFilter[] output_filters;

	/**
	 * The check box to change the file names by default.
	 */
	protected JCheckBox check_change_filename;

	/**
	 * The check box to set the image size or scale.
	 */
	protected JCheckBox check_transform;

	/**
	 * The panel to set the image size or scale.
	 */
	protected TransformationPanel transform_panel;

	/**
	 * The check box to apply the image processing filters.
	 */
	protected JCheckBox check_filter;

	/**
	 * The button to show the dialog to set the image processing 
	 * filters.
	 */
	protected JButton button_filter;

	/**
	 * The set of image processing filters.
	 */
	protected FilterSet filter_set = new FilterSet();

	/**
	 * Constructs a <code>ConversionDialog</code>.
	 */
	public ConversionDialog ( ) {
		components = new Object[1];

		JTabbedPane tab = new JTabbedPane();

		File dummy_file = new File("dummy");

		combo_input_format = new JComboBox();
		input_filters = ImageFileFilter.getSupportedFilters();
		for (int i = 0 ; i < input_filters.length ; i++) {
			try {
				Format format = input_filters[i].getFormat(dummy_file);
				combo_input_format.addItem(format.getName());
			} catch ( MalformedURLException exception ) {
				System.err.println(exception);
			} catch ( UnsupportedFileTypeException exception ) {
				System.err.println(exception);
			}
		}
		combo_input_format.setSelectedIndex(0);

		check_input_format = new JCheckBox("Read all images in the same format.");
		check_input_format.setSelected(true);
		check_input_format.addActionListener(new InputFormatListener());

		JPanel panel_input_format = new JPanel();
		panel_input_format.setLayout(new BoxLayout(panel_input_format, BoxLayout.Y_AXIS));
		panel_input_format.add(check_input_format);
		panel_input_format.add(combo_input_format);
		panel_input_format.setBorder(new TitledBorder("Input Image format"));

		combo_output_format = new JComboBox();
		output_filters = ImageFileFilter.getBitmapFilters();
		for (int i = 0 ; i < output_filters.length ; i++) {
			try {
				Format format = output_filters[i].getFormat(dummy_file);
				combo_output_format.addItem(format.getName());
			} catch ( MalformedURLException exception ) {
				System.err.println(exception);
			} catch ( UnsupportedFileTypeException exception ) {
				System.err.println(exception);
			}
		}
		combo_output_format.setSelectedIndex(0);

		check_output_format = new JCheckBox("Create all images in the same format.");
		check_output_format.setSelected(true);
		check_output_format.addActionListener(new OutputFormatListener());

		check_change_filename = new JCheckBox("Change output image file names by default.");
		check_change_filename.setSelected(true);

		JPanel panel_output_format = new JPanel();
		panel_output_format.setLayout(new BoxLayout(panel_output_format, BoxLayout.Y_AXIS));
		panel_output_format.add(check_output_format);
		panel_output_format.add(combo_output_format);
		panel_output_format.add(check_change_filename);
		panel_output_format.setBorder(new TitledBorder("Output Image format"));

		JPanel panel_format= new JPanel();
		panel_format.setLayout(new BoxLayout(panel_format, BoxLayout.Y_AXIS));
		panel_format.add(panel_input_format);
		panel_format.add(panel_output_format);

		tab.addTab("Format", panel_format);

		check_transform = new JCheckBox("Transform all images in the same size or scale.");
		check_transform.setSelected(true);
		check_transform.addActionListener(new TransformListener());

		transform_panel = new TransformationPanel();

		JPanel panel_transform = new JPanel();
		panel_transform.setLayout(new BorderLayout());
		panel_transform.add(check_transform, BorderLayout.WEST);
		panel_transform.add(transform_panel, BorderLayout.SOUTH);
		panel_transform.setBorder(new TitledBorder("Image size"));

		tab.addTab("Transformation", panel_transform);

		check_filter = new JCheckBox("Apply image processing filters to all images.");
		check_filter.setSelected(false);
		check_filter.addActionListener(new FilterCheckListener());

		button_filter = new JButton("Select Filters");
		button_filter.setEnabled(false);
		button_filter.addActionListener(new FilterButtonListener());

		JPanel panel_filter = new JPanel();
		panel_filter.setLayout(new BorderLayout());
		panel_filter.add(check_filter, BorderLayout.WEST);
		panel_filter.add(button_filter, BorderLayout.SOUTH);

		tab.addTab("Filter", panel_filter);

		components[0] = tab;
	}

	/**
	 * Gets the title of the dialog.
	 * @return the title of the dialog.
	 */
	protected String getTitle ( ) {
		return "Image Conversion Setting";
	}

	/**
	 * Returns true when to read images in the same format.
	 * @return true when to read images in the same format.
	 */
	public boolean specifiesInputFormat ( ) {
		return check_input_format.isSelected();
	}

	/**
	 * Gets the <code>FileFilter</code> for the selected input image
	 *  format.
	 * @return the <code>FileFilter</code> for the selected input 
	 * image format.
	 */
	public ImageFileFilter getInputFileFilter ( ) {
		return input_filters[combo_input_format.getSelectedIndex()];
	}

	/**
	 * Returns true when to create images in the same format.
	 * @return true when to create images in the same format.
	 */
	public boolean specifiesOutputFormat ( ) {
		return check_output_format.isSelected();
	}

	/**
	 * Gets the <code>FileFilter</code> for the selected output image
	 *  format.
	 * @return the <code>FileFilter</code> for the selected output 
	 * image format.
	 */
	public ImageFileFilter getOutputFileFilter ( ) {
		return output_filters[combo_output_format.getSelectedIndex()];
	}

	/**
	 * Returns true when to change all output image file names by
	 * default.
	 * @return true when to change all output image file names by
	 * default.
	 */
	public boolean changesFilenames ( ) {
		return check_change_filename.isSelected();
	}

	/**
	 * Returns true when to create images in the same size or scale.
	 * @return true when to create images in the same size or scale.
	 */
	public boolean transformsAll ( ) {
		return check_transform.isSelected();
	}

	/**
	 * Returns true if the size is selected.
	 * @return true if the size is selected.
	 */
	public boolean isSize ( ) {
		return transform_panel.isSize();
	}

	/**
	 * Returns true if the scale is selected.
	 * @return true if the scale is selected.
	 */
	public boolean isScale ( ) {
		return transform_panel.isScale();
	}

	/**
	 * Gets the image size.
	 * @return the image size.
	 */
	public Size getImageSize ( ) {
		return transform_panel.getImageSize();
	}

	/**
	 * Gets the image scale.
	 * @return the image scale.
	 */
	public int getScale ( ) {
		return transform_panel.getScale();
	}

	/**
	 * Returns true when to rescale ST-4/6 Image.
	 * @return true when to rescale ST-4/6 Image.
	 */
	public boolean rescalesSbig ( ) {
		return transform_panel.rescalesSbig();
	}

	/**
	 * Returns true when to apply image processing filters to all 
	 * images.
	 * @return true when to apply image processing filters to all 
	 * images.
	 */
	public boolean appliesImageProcessingFilters ( ) {
		return check_filter.isSelected();
	}

	/**
	 * Gets the set of image processing filters.
	 * @return the set of image processing filters.
	 */
	public FilterSet getFilterSet ( ) {
		return filter_set;
	}

	/**
	 * The <code>InputFormatListener</code> is a listener class of 
	 * menu selection to specify the input format.
	 */
	protected class InputFormatListener implements ActionListener {
		/**
		 * Invoked when one of the menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			combo_input_format.setEnabled(check_input_format.isSelected());
		}
	}

	/**
	 * The <code>OutputFormatListener</code> is a listener class of 
	 * menu selection to specify the output format.
	 */
	protected class OutputFormatListener implements ActionListener {
		/**
		 * Invoked when one of the menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			combo_output_format.setEnabled(check_output_format.isSelected());
			check_change_filename.setEnabled(check_output_format.isSelected());
		}
	}

	/**
	 * The <code>TransformListener</code> is a listener class of menu
	 * selection to transform the images.
	 */
	protected class TransformListener implements ActionListener {
		/**
		 * Invoked when one of the menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			transform_panel.setEnabled(check_transform.isSelected());
		}
	}

	/**
	 * The <code>FilterCheckListener</code> is a listener class of 
	 * menu selection to apply image processing filters.
	 */
	protected class FilterCheckListener implements ActionListener {
		/**
		 * Invoked when one of the menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			button_filter.setEnabled(check_filter.isSelected());
		}
	}

	/**
	 * The <code>FilterButtonListener</code> is a listener class of 
	 * menu selection to select image processing filters.
	 */
	protected class FilterButtonListener implements ActionListener {
		/**
		 * Invoked when one of the menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			FilterSelectionDialog dialog = new FilterSelectionDialog(filter_set);
			int answer = dialog.show(button_filter);

			if (answer == 0)
				filter_set = dialog.getFilterSet();
		}
	}
}
