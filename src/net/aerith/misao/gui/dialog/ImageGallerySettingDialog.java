/*
 * @(#)ImageGallerySettingDialog.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui.dialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import java.net.*;
import net.aerith.misao.util.Size;
import net.aerith.misao.gui.*;
import net.aerith.misao.io.filechooser.ImageFileFilter;
import net.aerith.misao.image.*;
import net.aerith.misao.image.io.*;

/**
 * The <code>ImageGallerySettingDialog</code> represents a dialog to 
 * set the image size, scale, and rotation.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2007 May 20
 */

public class ImageGallerySettingDialog extends Dialog {
	/**
	 * The radio button to create an image gallery.
	 */
	protected JRadioButton radio_type_image_gallery;

	/**
	 * The radio button to create a differential image gallery.
	 */
	protected JRadioButton radio_type_diff_gallery;

	/**
	 * The text field to input the image width.
	 */
	protected JTextField text_width;

	/**
	 * The text field to input the image height.
	 */
	protected JTextField text_height;

	/**
	 * The radio button to keep the original scale.
	 */
	protected JRadioButton radio_scale_keep_original;

	/**
	 * The radio button to unify the resulution.
	 */
	protected JRadioButton radio_scale_unify_resolution;

	/**
	 * The radio button to unify the field of view.
	 */
	protected JRadioButton radio_scale_unify_fov;

	/**
	 * The radio button to unify the magnification.
	 */
	protected JRadioButton radio_scale_unify_magnification;

	/**
	 * The text field to input the resolution.
	 */
	protected JTextField text_resolution;

	/**
	 * The text field to input the field width in arcmin.
	 */
	protected JTextField text_fov_width;

	/**
	 * The text field to input the field height in arcmin.
	 */
	protected JTextField text_fov_height;

	/**
	 * The text field to input the magnification.
	 */
	protected JTextField text_magnification;

	/**
	 * The radio button to keep the original rotation.
	 */
	protected JRadioButton radio_rotation_keep_original;

	/**
	 * The radio button to rotate north up at right angles.
	 */
	protected JRadioButton radio_rotation_north_up_at_right_angles;

	/**
	 * Constructs an <code>ImageGallerySettingDialog</code>.
	 */
	public ImageGallerySettingDialog ( ) {
		components = new Object[1];

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		ButtonGroup bg_type = new ButtonGroup();
		radio_type_image_gallery = new JRadioButton("", true);
		radio_type_diff_gallery = new JRadioButton("");
		bg_type.add(radio_type_image_gallery);
		bg_type.add(radio_type_diff_gallery);

		JPanel panel_type_image_gallery = new JPanel();
		panel_type_image_gallery.add(radio_type_image_gallery);
		panel_type_image_gallery.add(new JLabel("Image gallery."));
		JPanel panel_type_image_gallery2 = new JPanel();
		panel_type_image_gallery2.setLayout(new BorderLayout());
		panel_type_image_gallery2.add(panel_type_image_gallery, BorderLayout.WEST);

		JPanel panel_type_diff_gallery = new JPanel();
		panel_type_diff_gallery.add(radio_type_diff_gallery);
		panel_type_diff_gallery.add(new JLabel("Differential image gallery."));
		JPanel panel_type_diff_gallery2 = new JPanel();
		panel_type_diff_gallery2.setLayout(new BorderLayout());
		panel_type_diff_gallery2.add(panel_type_diff_gallery, BorderLayout.WEST);

		JPanel panel_type = new JPanel();
		panel_type.setLayout(new BoxLayout(panel_type, BoxLayout.Y_AXIS));
		panel_type.add(panel_type_image_gallery2);
		panel_type.add(panel_type_diff_gallery2);
		panel_type.setBorder(new TitledBorder("Type"));

		text_width = new JTextField("200");
		text_height = new JTextField("200");
		text_width.setColumns(5);
		text_height.setColumns(5);

		JPanel panel_size = new JPanel();
		panel_size.add(text_width);
		panel_size.add(new JLabel(" x "));
		panel_size.add(text_height);
		panel_size.setBorder(new TitledBorder("Size"));

		ButtonGroup bg_scale = new ButtonGroup();
		radio_scale_keep_original = new JRadioButton("", true);
		radio_scale_unify_resolution = new JRadioButton("");
		radio_scale_unify_fov = new JRadioButton("");
		radio_scale_unify_magnification = new JRadioButton("");
		bg_scale.add(radio_scale_keep_original);
		bg_scale.add(radio_scale_unify_resolution);
		bg_scale.add(radio_scale_unify_fov);
		bg_scale.add(radio_scale_unify_magnification);

		JPanel panel_scale_keep_original = new JPanel();
		panel_scale_keep_original.add(radio_scale_keep_original);
		panel_scale_keep_original.add(new JLabel("Keep original."));
		JPanel panel_scale_keep_original2 = new JPanel();
		panel_scale_keep_original2.setLayout(new BorderLayout());
		panel_scale_keep_original2.add(panel_scale_keep_original, BorderLayout.WEST);

		text_resolution = new JTextField("3.0");
		text_resolution.setColumns(6);

		JPanel panel_scale_unify_resolution = new JPanel();
		panel_scale_unify_resolution.add(radio_scale_unify_resolution);
		panel_scale_unify_resolution.add(new JLabel("Unify resolution to "));
		panel_scale_unify_resolution.add(text_resolution);
		panel_scale_unify_resolution.add(new JLabel(" arcsec/pixel."));
		JPanel panel_scale_unify_resolution2 = new JPanel();
		panel_scale_unify_resolution2.setLayout(new BorderLayout());
		panel_scale_unify_resolution2.add(panel_scale_unify_resolution, BorderLayout.WEST);

		text_fov_width = new JTextField("15.0");
		text_fov_width.setColumns(6);
		text_fov_height = new JTextField("15.0");
		text_fov_height.setColumns(6);

		JPanel panel_scale_unify_fov = new JPanel();
		panel_scale_unify_fov.add(radio_scale_unify_fov);
		panel_scale_unify_fov.add(new JLabel("Unify field of view to "));
		panel_scale_unify_fov.add(text_fov_width);
		panel_scale_unify_fov.add(new JLabel(" x "));
		panel_scale_unify_fov.add(text_fov_height);
		panel_scale_unify_fov.add(new JLabel(" arcmin."));
		JPanel panel_scale_unify_fov2 = new JPanel();
		panel_scale_unify_fov2.setLayout(new BorderLayout());
		panel_scale_unify_fov2.add(panel_scale_unify_fov, BorderLayout.WEST);

		text_magnification = new JTextField("1.0");
		text_magnification.setColumns(6);

		JPanel panel_scale_unify_magnification = new JPanel();
		panel_scale_unify_magnification.add(radio_scale_unify_magnification);
		panel_scale_unify_magnification.add(new JLabel("Unify magnification to "));
		panel_scale_unify_magnification.add(text_magnification);
		JPanel panel_scale_unify_magnification2 = new JPanel();
		panel_scale_unify_magnification2.setLayout(new BorderLayout());
		panel_scale_unify_magnification2.add(panel_scale_unify_magnification, BorderLayout.WEST);

		JPanel panel_scale = new JPanel();
		panel_scale.setLayout(new BoxLayout(panel_scale, BoxLayout.Y_AXIS));
		panel_scale.add(panel_scale_keep_original2);
		panel_scale.add(panel_scale_unify_resolution2);
		panel_scale.add(panel_scale_unify_fov2);
		panel_scale.add(panel_scale_unify_magnification2);
		panel_scale.setBorder(new TitledBorder("Scale"));

		ButtonGroup bg_rotation = new ButtonGroup();
		radio_rotation_keep_original = new JRadioButton("", true);
		radio_rotation_north_up_at_right_angles = new JRadioButton("");
		bg_rotation.add(radio_rotation_keep_original);
		bg_rotation.add(radio_rotation_north_up_at_right_angles);

		JPanel panel_rotation_keep_original = new JPanel();
		panel_rotation_keep_original.add(radio_rotation_keep_original);
		panel_rotation_keep_original.add(new JLabel("Keep original."));
		JPanel panel_rotation_keep_original2 = new JPanel();
		panel_rotation_keep_original2.setLayout(new BorderLayout());
		panel_rotation_keep_original2.add(panel_rotation_keep_original, BorderLayout.WEST);

		JPanel panel_rotation_north_up_at_right_angles = new JPanel();
		panel_rotation_north_up_at_right_angles.add(radio_rotation_north_up_at_right_angles);
		panel_rotation_north_up_at_right_angles.add(new JLabel("North up at right angles."));
		JPanel panel_rotation_north_up_at_right_angles2 = new JPanel();
		panel_rotation_north_up_at_right_angles2.setLayout(new BorderLayout());
		panel_rotation_north_up_at_right_angles2.add(panel_rotation_north_up_at_right_angles, BorderLayout.WEST);

		JPanel panel_rotation = new JPanel();
		panel_rotation.setLayout(new BoxLayout(panel_rotation, BoxLayout.Y_AXIS));
		panel_rotation.add(panel_rotation_keep_original2);
		panel_rotation.add(panel_rotation_north_up_at_right_angles2);
		panel_rotation.setBorder(new TitledBorder("Rotation"));

		panel.add(panel_type);
		panel.add(panel_size);
		panel.add(panel_scale);
		panel.add(panel_rotation);

		components[0] = panel;

		setDefaultValues();
	}

	/**
	 * Gets the title of the dialog.
	 * @return the title of the dialog.
	 */
	protected String getTitle ( ) {
		return "Image Gallery Setting";
	}

	/**
	 * Sets the default values.
	 */
	protected void setDefaultValues ( ) {
		if (default_values.get("diff-gallery") != null) {
			radio_type_image_gallery.setSelected(! ((Boolean)default_values.get("diff-gallery")).booleanValue());
			radio_type_diff_gallery.setSelected(((Boolean)default_values.get("diff-gallery")).booleanValue());
		}
		if (default_values.get("width") != null)
			text_width.setText((String)default_values.get("width"));
		if (default_values.get("height") != null)
			text_height.setText((String)default_values.get("height"));
		if (default_values.get("unify-resolution") != null) {
			radio_scale_keep_original.setSelected(((Integer)default_values.get("unify-resolution")).intValue() == 0);
			radio_scale_unify_resolution.setSelected(((Integer)default_values.get("unify-resolution")).intValue() == 1);
			radio_scale_unify_fov.setSelected(((Integer)default_values.get("unify-resolution")).intValue() == 2);
			radio_scale_unify_magnification.setSelected(((Integer)default_values.get("unify-resolution")).intValue() == 3);
		}
		if (default_values.get("resolution") != null)
			text_resolution.setText((String)default_values.get("resolution"));
		if (default_values.get("fov-width") != null)
			text_fov_width.setText((String)default_values.get("fov-width"));
		if (default_values.get("fov-height") != null)
			text_fov_height.setText((String)default_values.get("fov-height"));
		if (default_values.get("magnification") != null)
			text_magnification.setText((String)default_values.get("magnification"));
		if (default_values.get("north-up") != null) {
			radio_rotation_keep_original.setSelected(! ((Boolean)default_values.get("north-up")).booleanValue());
			radio_rotation_north_up_at_right_angles.setSelected(((Boolean)default_values.get("north-up")).booleanValue());
		}
	}

	/**
	 * Saves the default values.
	 */
	protected void saveDefaultValues ( ) {
		default_values.put("diff-gallery", new Boolean(radio_type_diff_gallery.isSelected()));

		default_values.put("width", text_width.getText());
		default_values.put("height", text_height.getText());

		if (radio_scale_keep_original.isSelected())
			default_values.put("unify-resolution", new Integer(0));
		else if (radio_scale_unify_resolution.isSelected())
			default_values.put("unify-resolution", new Integer(1));
		else if (radio_scale_unify_fov.isSelected())
			default_values.put("unify-resolution", new Integer(2));
		else if (radio_scale_unify_magnification.isSelected())
			default_values.put("unify-resolution", new Integer(3));

		default_values.put("resolution", text_resolution.getText());
		default_values.put("fov-width", text_fov_width.getText());
		default_values.put("fov-height", text_fov_height.getText());
		default_values.put("magnification", text_magnification.getText());
		default_values.put("north-up", new Boolean(radio_rotation_north_up_at_right_angles.isSelected()));
	}

	/**
	 * Returns true when to create a differential image gallery.
	 * @return true when to create a differential image gallery.
	 */
	public boolean createsDifferentialImageGallery ( ) {
		return radio_type_diff_gallery.isSelected();
	}

	/**
	 * Gets the size.
	 * @return the size.
	 */
	public Size getSize ( ) {
		int width = Integer.parseInt(text_width.getText());
		int height = Integer.parseInt(text_height.getText());
		return new Size(width, height);
	}

	/**
	 * Returns true when to unify the resolution.
	 * @return true when to unify the resolution.
	 */
	public boolean unifiesResolution ( ) {
		if (radio_scale_unify_resolution.isSelected()  ||  radio_scale_unify_fov.isSelected())
			return true;

		return false;
	}

	/**
	 * Gets the resolution in arcsec/pixel.
	 * @return the resolution in arcsec/pixel.
	 */
	public double getResolution ( ) {
		if (radio_scale_unify_fov.isSelected()) {
			double resolution_width = Double.parseDouble(text_fov_width.getText()) * 60.0 / Double.parseDouble(text_width.getText());
			double resolution_height = Double.parseDouble(text_fov_height.getText()) * 60.0 / Double.parseDouble(text_height.getText());

			return (resolution_width + resolution_height) / 2.0;
		}

		return Double.parseDouble(text_resolution.getText());
	}

	/**
	 * Returns true when to unify the magnification.
	 * @return true when to unify the magnification.
	 */
	public boolean unifiesMagnification ( ) {
		if (radio_scale_unify_magnification.isSelected())
			return true;

		return false;
	}

	/**
	 * Gets the magnification.
	 * @return the magnification.
	 */
	public double getMagnification ( ) {
		return Double.parseDouble(text_magnification.getText());
	}

	/**
	 * Returns true when to rotate north up at right angles.
	 * @return true when to rotate north up at right angles.
	 */
	public boolean rotatesNorthUpAtRightAngles ( ) {
		return radio_rotation_north_up_at_right_angles.isSelected();
	}
}
