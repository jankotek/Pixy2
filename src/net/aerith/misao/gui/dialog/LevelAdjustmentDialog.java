/*
 * @(#)LevelAdjustmentDialog.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui.dialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.image.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.pixy.Resource;

/**
 * The <code>LevelAdjustmentDialog</code> represents a dialog to 
 * ajudst image level to view.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class LevelAdjustmentDialog {
	/**
	 * The target image.
	 */
	private Statisticable image;

	/**
	 * The target image component.
	 */
	private LevelAdjustable target;

	/**
	 * The thread to control the adjust command to the target.
	 */
	private AdjustCommanderThread thread;

	/**
	 * The image level and statistics.
	 */
	private LevelAdjustmentSet stat;

	/**
	 * The minimum value of the slider range.
	 */
	private double range_minimum;

	/**
	 * The maximum value of the slider range.
	 */
	private double range_maximum;

	/**
	 * The slider for minimum value.
	 */
	private JSlider slider_minimum;

	/**
	 * The slider for maximum value.
	 */
	private JSlider slider_maximum;

	/**
	 * The check box to adjust all images.
	 */
	private JCheckBox checkbox_adjust_all;

	/**
	 * Array of objects to show in the dialog.
	 */
	private Object[] objects;

	/**
	 * Text field to input minimum value.
	 */
	private JTextField min_field;

	/**
	 * Text field to input maximum value.
	 */
	private JTextField max_field;

	/**
	 * The histogram.
	 */
	private HistogramComponent histogram;

	/**
	 * True if the it is disabled to update text fields and repaint
	 * the image.
	 */
	private boolean disable_update = false;

	/**
	 * True if the minimum and maximum value should be printed as
	 * integer value in the text fields.
	 */
	private boolean textfield_integer = true;

	/**
	 * True if the adjust command controller thead is running.
	 */
	protected boolean running = false;

	/**
	 * Constructs a <code>LevelAdjustmentDialog</code>.
	 * @param image  the target image.
	 * @param target the target image component.
	 * @param stat   the image level and statistics.
	 */
	public LevelAdjustmentDialog ( Statisticable image, LevelAdjustable target, LevelAdjustmentSet stat ) {
		this.image = image;
		this.target = target;
		this.stat = new LevelAdjustmentSet(stat);

		double level_range = stat.original_statistics.getMax() - stat.original_statistics.getMin();
		String s = "Image level: [ ";
		if (level_range < 10)
			s += stat.original_statistics.getMin();
		else
			s += (int)stat.original_statistics.getMin();
		s += ", ";
		if (level_range < 10)
			s += stat.original_statistics.getMax();
		else
			s += (int)stat.original_statistics.getMax();
		s += "]  Average = " + stat.original_statistics.getAverage();
		JLabel image_stat = new JLabel(s);

		level_range = Math.abs(stat.current_maximum - stat.current_minimum);
		JPanel input_panel = new JPanel();
		textfield_integer = false;
		if (level_range < 10) {
			min_field = new JTextField("" + stat.current_minimum, 10);
			max_field = new JTextField("" + stat.current_maximum, 10);
		} else {
			textfield_integer = true;
			min_field = new JTextField("" + (int)stat.current_minimum, 10);
			max_field = new JTextField("" + (int)stat.current_maximum, 10);
		}
		input_panel.add(new JLabel("Min: "));
		input_panel.add(min_field);
		input_panel.add(new JLabel("Max: "));
		input_panel.add(max_field);
		JButton button = new JButton("Apply");
		button.addActionListener(new ApplyListener());
		input_panel.add(button);
		button = new JButton("Auto");
		button.addActionListener(new AutoListener());
		input_panel.add(button);

		disable_update = true;

		slider_minimum = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
		slider_minimum.addChangeListener(new SliderChangeListener());
		Hashtable hash = slider_minimum.createStandardLabels(10);
		slider_minimum.setLabelTable(hash);
		slider_minimum.setPaintLabels(true);
		slider_minimum.setMajorTickSpacing(10);
		slider_minimum.setMinorTickSpacing(2);
		slider_minimum.setPaintTicks(true);
		slider_minimum.setBorder(new TitledBorder("Minimum (%)"));

		slider_maximum = new JSlider(JSlider.HORIZONTAL, 0, 100, 100);
		slider_maximum.addChangeListener(new SliderChangeListener());
		hash = slider_maximum.createStandardLabels(10);
		slider_maximum.setLabelTable(hash);
		slider_maximum.setPaintLabels(true);
		slider_maximum.setMajorTickSpacing(10);
		slider_maximum.setMinorTickSpacing(2);
		slider_maximum.setPaintTicks(true);
		slider_maximum.setBorder(new TitledBorder("Maximum (%)"));

		disable_update = false;

		histogram = new HistogramComponent(image);

		resetSlider();

		checkbox_adjust_all = null;

		objects = new Object[5];
		objects[0] = image_stat;
		objects[1] = input_panel;
		objects[2] = slider_minimum;
		objects[3] = slider_maximum;
		objects[4] = histogram;
	}

	/**
	 * Adds a check box to adjust all images at one time.
	 */
	public void addAllImagesAdjustmentCheckbox ( ) {
		if (checkbox_adjust_all == null) {
			Object[] new_objects = new Object[objects.length + 1];
			for (int i = 0 ; i < objects.length ; i++)
				new_objects[i] = objects[i];

			checkbox_adjust_all = new JCheckBox("Adjust all images");
			new_objects[objects.length] = checkbox_adjust_all;

			objects = new_objects;
		}
	}

	/**
	 * Shows the dialog. When closed, the current minimum and maximum
	 * values are updated.
	 * @param pane the parent window.
	 * @return the code of selected button.
	 */
	public int show ( Component pane ) {
		running = true;
		thread = new AdjustCommanderThread();
		thread.setPriority(Resource.getThreadPriority());
		thread.start();

		int answer = JOptionPane.showConfirmDialog(pane, objects, "Level Adjustment", JOptionPane.DEFAULT_OPTION);

		running = false;

		return answer;
	}

	/**
	 * Resets the slider position so that the current minimum value 
	 * will be 0.25 and the current maximum value will be 0.75.
	 */
	private void resetSlider ( ) {
		disable_update = true;

		double average = (stat.current_maximum + stat.current_minimum) / 2.0;
		double level_range = Math.abs(stat.current_maximum - stat.current_minimum);
		if (level_range == 0)
			level_range = 1;

		range_minimum = average - level_range;
		range_maximum = average + level_range;

		slider_minimum.setValue(convertValueToLevel(stat.current_minimum));
		slider_maximum.setValue(convertValueToLevel(stat.current_maximum));

		histogram.updateHistogram(range_minimum, range_maximum);
		histogram.updatePointer(stat.current_minimum, stat.current_maximum);

		disable_update = false;
	}

	/**
	 * Converts the pixel value to the level based on the specified
	 * minimum and maximum value.
	 * @param value the pixel value.
	 * @return the level.
	 */
	protected int convertValueToLevel ( double value ) {
		int level = (int)((value - range_minimum) / (range_maximum - range_minimum) * 100);
		if (level <= 0)
			return 0;
		if (level >= 100)
			return 100;

		return level;
	}

	/**
	 * Converts the level to the pixel value based on the specified
	 * minimum and maximum value.
	 * @param level the level.
	 * @return the pixel value.
	 */
	protected double convertLevelToValue ( int level ) {
		return (double)level / 100.0 * (range_maximum - range_minimum) + range_minimum;
	}

	/**
	 * Adjusts the level of the target.
	 * @param new_minimum the minimum level.
	 * @param new_maximum the maximum level.
	 */
	public void adjustLevel ( double new_minimum, double new_maximum ) {
		if (thread != null)
			thread.reset();

		if (checkbox_adjust_all != null) {
			if (checkbox_adjust_all.isSelected())
				target.adjustLevelAll(new_minimum, new_maximum);
			else
				target.adjustLevel(new_minimum, new_maximum);
		} else {
			target.adjustLevel(new_minimum, new_maximum);
		}
	}

	/**
	 * The <code>ApplyListener</code> is a listener class of button 
	 * push to apply the level to the image component.
	 */
	protected class ApplyListener implements ActionListener {
		/**
		 * Invoked when the button is pushed.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			stat.current_minimum = Format.doubleValueOf(min_field.getText());
			stat.current_maximum = Format.doubleValueOf(max_field.getText());

			resetSlider();

			adjustLevel(stat.current_minimum, stat.current_maximum);
		}
	}

	/**
	 * The <code>AutoListener</code> is a listener class of button 
	 * push to set the proper level automatically.
	 */
	protected class AutoListener implements ActionListener {
		/**
		 * Invoked when the button is pushed.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			stat = new DefaultLevelAdjustmentSet((MonoImage)image);

			double level_range = Math.abs(stat.current_maximum - stat.current_minimum);
			textfield_integer = false;
			if (level_range < 10) {
				min_field.setText("" + stat.current_minimum);
				max_field.setText("" + stat.current_maximum);
			} else {
				textfield_integer = true;
				min_field.setText("" + (int)stat.current_minimum);
				max_field.setText("" + (int)stat.current_maximum);
			}

			stat.current_minimum = Format.doubleValueOf(min_field.getText());
			stat.current_maximum = Format.doubleValueOf(max_field.getText());

			resetSlider();

			adjustLevel(stat.current_minimum, stat.current_maximum);
		}
	}

	/**
	 * The <code>SliderChangeListener</code> is a listener class of 
	 * slider motion.
	 */
	protected class SliderChangeListener implements ChangeListener {
		/**
		 * Invoked when the slider is moved.
		 * @param e contains the slider.
		 */
		public void stateChanged ( ChangeEvent e ) {
			if (disable_update == false) {
				JSlider slider = (JSlider)e.getSource();
				double value = convertLevelToValue(slider.getValue());

				if (slider == slider_minimum) {
					stat.current_minimum = value;
					if (textfield_integer)
						min_field.setText("" + (int)value);
					else
						min_field.setText("" + value);
				}
				if (slider == slider_maximum) {
					stat.current_maximum = value;
					if (textfield_integer)
						max_field.setText("" + (int)value);
					else
						max_field.setText("" + value);
				}

//				adjustLevel(stat.current_minimum, stat.current_maximum);

				histogram.updatePointer(stat.current_minimum, stat.current_maximum);
			}
		}
	}


	/**
	 * The <code>AdjustCommanderThread</code> is a thread to control 
	 * the adjust command to the target.
	 * <p>
	 * Because creating <code>java.awt.Image</code> is heavy, this
	 * thread restricts the adjust command twice in one second at most.
	 * As a result, the slider control becomes light weight.
	 */
	protected class AdjustCommanderThread extends Thread {
		/**
		 * The last minimum pixel value.
		 */
		private double last_minimum;

		/**
		 * The last maximum pixel value.
		 */
		private double last_maximum;

		/**
		 * Sets the last minimum and maximum pixel value as same as 
		 * the dialog member fields.
		 */
		public void reset ( ) {
			last_minimum = stat.current_minimum;
			last_maximum = stat.current_maximum;
		}

		/**
		 * Runs this thread.
		 */
		public void run ( ) {
			reset();

			while (running) {
				if (last_minimum != stat.current_minimum  ||  last_maximum != stat.current_maximum) {
					adjustLevel(stat.current_minimum, stat.current_maximum);
				}

				try {
					sleep(500);
				} catch ( InterruptedException exception ) {
				}
			}
		}
	}
}
