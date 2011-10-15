/*
 * @(#)TransformationPanel.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.ImageConversion;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import net.aerith.misao.util.*;

/**
 * The <code>TransformationPanel</code> represents a panel to set the 
 * image size or the rescale rate.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class TransformationPanel extends JPanel {
	/**
	 * The text field to input image width.
	 */
	protected JTextField text_width;

	/**
	 * The text field to input image height;
	 */
	protected JTextField text_height;

	/**
	 * The text field to input rescale rate.
	 */
	protected JTextField text_scale;

	/**
	 * The radio button for size.
	 */
	protected JRadioButton radio_size;

	/**
	 * The radio button for scale.
	 */
	protected JRadioButton radio_scale;

	/**
	 * The check box to rescale ST-4/6 Image.
	 */
	protected JCheckBox check_rescale_sbig;

	/**
	 * Constructs a <code>TransformationPanel</code>.
	 */
	public TransformationPanel ( ) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		text_width = new JTextField("100");
		text_height = new JTextField("100");
		text_scale = new JTextField("100");

		text_width.setColumns(6);
		text_height.setColumns(6);
		text_scale.setColumns(6);

		ButtonGroup bg = new ButtonGroup();
		radio_size = new JRadioButton("");
		radio_scale = new JRadioButton("", true);
		bg.add(radio_size);
		bg.add(radio_scale);

		JPanel panel_width = new JPanel();
		panel_width.add(new JLabel("Width"));
		panel_width.add(text_width);

		JPanel panel_height = new JPanel();
		panel_height.add(new JLabel("Height"));
		panel_height.add(text_height);

		JPanel panel_wh = new JPanel();
		panel_wh.setLayout(new BoxLayout(panel_wh, BoxLayout.Y_AXIS));
		panel_wh.add(panel_width);
		panel_wh.add(panel_height);

		JPanel panel_wh2 = new JPanel();
		panel_wh2.add(new JLabel("        "));
		panel_wh2.add(panel_wh);

		JPanel panel_radio_size = new JPanel();
		panel_radio_size.add(radio_size);
		panel_radio_size.add(new JLabel("Size"));

		JPanel panel_size = new JPanel();
		panel_size.setLayout(new BorderLayout());
		panel_size.add(panel_radio_size, BorderLayout.WEST);
		panel_size.add(panel_wh2, BorderLayout.SOUTH);

		JPanel panel_input_scale = new JPanel();
		panel_input_scale.add(new JLabel("        "));
		panel_input_scale.add(text_scale);
		panel_input_scale.add(new JLabel("%"));

		JPanel panel_radio_scale = new JPanel();
		panel_radio_scale.add(radio_scale);
		panel_radio_scale.add(new JLabel("Scale"));

		JPanel panel_scale = new JPanel();
		panel_scale.setLayout(new BorderLayout());
		panel_scale.add(panel_radio_scale, BorderLayout.WEST);
		panel_scale.add(panel_input_scale, BorderLayout.SOUTH);

		check_rescale_sbig = new JCheckBox("Rescale ST-4/6 Image");
		check_rescale_sbig.setSelected(false);

		JPanel panel_sbig = new JPanel();
		panel_sbig.setLayout(new BorderLayout());
		panel_sbig.add(check_rescale_sbig, BorderLayout.WEST);

		add(panel_size);
		add(panel_scale);
		add(panel_sbig);
	}

	/**
	 * Enables/Disables the components.
	 * @param flag true when to enable the components.
	 */
	public void setEnabled ( boolean flag ) {
		radio_size.setEnabled(flag);
		radio_scale.setEnabled(flag);
		text_width.setEnabled(flag);
		text_height.setEnabled(flag);
		text_scale.setEnabled(flag);
		check_rescale_sbig.setEnabled(flag);
	}

	/**
	 * Returns true if the size is selected.
	 * @return true if the size is selected.
	 */
	public boolean isSize ( ) {
		return radio_size.isSelected();
	}

	/**
	 * Returns true if the scale is selected.
	 * @return true if the scale is selected.
	 */
	public boolean isScale ( ) {
		return radio_scale.isSelected();
	}

	/**
	 * Selects the size.
	 */
	public void selectSize ( ) {
		radio_size.setSelected(true);
	}

	/**
	 * Selects the scale.
	 */
	public void selectScale ( ) {
		radio_scale.setSelected(true);
	}

	/**
	 * Gets the image size.
	 * @return the image size.
	 */
	public Size getImageSize ( ) {
		int width = Integer.parseInt(text_width.getText());
		int height = Integer.parseInt(text_height.getText());
		return new Size(width, height);
	}

	/**
	 * Gets the image scale.
	 * @return the image scale.
	 */
	public int getScale ( ) {
		return Integer.parseInt(text_scale.getText());
	}

	/**
	 * Sets the image size.
	 * @param size the image size.
	 */
	public void setImageSize ( Size size ) {
		text_width.setText("" + size.getWidth());
		text_height.setText("" + size.getHeight());
	}

	/**
	 * Sets the image scale.
	 * @param scale the image scale.
	 */
	public void setScale ( int scale ) {
		text_scale.setText("" + scale);
	}

	/**
	 * Returns true when to rescale ST-4/6 Image.
	 * @return true when to rescale ST-4/6 Image.
	 */
	public boolean rescalesSbig ( ) {
		return check_rescale_sbig.isSelected();
	}

	/**
	 * Sets the flag to rescale ST-4/6 Image.
	 * @param flag true when to rescale ST-4/6 Image.
	 */
	public void setRescaleSbig ( boolean flag ) {
		check_rescale_sbig.setSelected(flag);
	}
}
