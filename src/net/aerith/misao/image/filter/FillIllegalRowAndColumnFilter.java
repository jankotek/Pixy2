/*
 * @(#)FillIllegalRowAndColumnFilter.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.image.filter;
import net.aerith.misao.util.*;
import net.aerith.misao.image.*;

/**
 * The <code>FillIllegalRowAndColumnFilter</code> is an image
 * processing filter to fill the illegal rows and columns with the
 * median pixel value. In the case of some astronomical images, some
 * pixels from the edges have illegal value, which are too white or
 * too black. They will cause threshold estimation failure or too many
 * noises detection, which leads to fail the star detection or 
 * matching process. So in general, every image must be corrected 
 * previously by this filter before star detection. The result is 
 * stored in the original image.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class FillIllegalRowAndColumnFilter extends Filter {
	/**
	 * True when possible to decrease pixel value.
	 */
	protected boolean decrease_enabled = true;

	/**
	 * True when the image is modified, pixel value is increased.
	 */
	protected boolean modified_increased = false;

	/**
	 * True when the image is modified, pixel value is decreased.
	 */
	protected boolean modified_decreased = false;

	/**
	 * True when some rows are modified.
	 */
	protected boolean modified_horizontal = false;

	/**
	 * True when some columns are modified.
	 */
	protected boolean modified_vertical = false;

	/**
	 * Constructs a filter.
	 */
	public FillIllegalRowAndColumnFilter ( ) {
	}

	/**
	 * Sets the flag to decrease pixel value.
	 * @param flag true when possible to decrease pixel value.
	 */
	public void setDecreaseEnabled ( boolean flag ) {
		decrease_enabled = flag;
	}

	/**
	 * Returns true when the image is modified.
	 * @return true when the image is modified.
	 */
	public boolean isModified ( ) {
		return (modified_increased || modified_decreased);
	}

	/**
	 * Returns true when the image is modified, pixel value is 
	 * increased.
	 * @return true when the image is modified, pixel value is 
	 * increased.
	 */
	public boolean isModifiedIncreased ( ) {
		return modified_increased;
	}

	/**
	 * Returns true when the image is modified, pixel value is 
	 * decreased.
	 * @return true when the image is modified, pixel value is 
	 * decreased.
	 */
	public boolean isModifiedDecreased ( ) {
		return modified_decreased;
	}

	/**
	 * Returns true when some rows are modified.
	 * @return true when some rows are modified.
	 */
	public boolean isModifiedHorizontal ( ) {
		return modified_horizontal;
	}

	/**
	 * Returns true when some columns are modified.
	 * @return true when some columns are modified.
	 */
	public boolean isModifiedVertical ( ) {
		return modified_vertical;
	}

	/**
	 * Operates the image processing filter and stores the result into
	 * the original image buffer.
	 * @param image the original image to process.
	 * @return the original image buffer.
	 */
	public MonoImage operate ( MonoImage image ) {
		modified_increased = false;
		modified_decreased = false;
		modified_horizontal = false;
		modified_vertical = false;

		Size size = image.getSize();

		// for each row

		// Sorts rows in order of the median of pixel values.
		Array array = new Array(size.getHeight());
		for (int y = 0 ; y < size.getHeight() ; y++) {
			Array array_in_row = new Array(size.getWidth());
			for (int x = 0 ; x < size.getWidth() ; x++)
				array_in_row.set(x, image.getValue(x, y));
			array_in_row.sortAscendant();
			array.set(y, array_in_row.getValueAt(size.getWidth() / 2));
		}
		ArrayIndex index = array.sortAscendant();

		HighOrderStatistics stat = new HighOrderStatistics(array);
		stat.calculate();

		// The row is judged illegal if the difference of the median 
		// value from the next in the sorted list is greater than 3
		// sigma. Rows whose median values are further than that row
		// from the median of median values are also judged illegal.
		boolean illegal_flag = false;
		if (decrease_enabled) {
			for (int y = size.getHeight() / 2 + 1 ; y < size.getHeight() ; y++) {
				if (illegal_flag  ||  array.getValueAt(y) - array.getValueAt(y - 1) > stat.getDeviation() * 3.0) {
					illegal_flag = true;
					for (int x = 0 ; x < size.getWidth() ; x++)
						image.setValue(x, index.get(y), stat.getMedian());
					modified_decreased = true;
					modified_horizontal = true;
				}
			}
		}
		illegal_flag = false;
		for (int y = size.getHeight() / 2 - 1 ; y >= 0 ; y--) {
			if (illegal_flag  ||  array.getValueAt(y + 1) - array.getValueAt(y) > stat.getDeviation() * 3.0) {
				illegal_flag = true;
				for (int x = 0 ; x < size.getWidth() ; x++)
					image.setValue(x, index.get(y), stat.getMedian());
				modified_increased = true;
				modified_horizontal = true;
			}
		}

		// for each column

		// Sorts columns in order of the median of pixel values.
		array = new Array(size.getWidth());
		for (int x = 0 ; x < size.getWidth() ; x++) {
			Array array_in_column = new Array(size.getHeight());
			for (int y = 0 ; y < size.getHeight() ; y++)
				array_in_column.set(y, image.getValue(x, y));
			array_in_column.sortAscendant();
			array.set(x, array_in_column.getValueAt(size.getHeight() / 2));
		}
		index = array.sortAscendant();

		stat = new HighOrderStatistics(array);
		stat.calculate();

		// The column is judged illegal if the difference of the median 
		// value from the next in the sorted list is greater than 3
		// sigma. Columns whose median values are further than that column
		// from the median of median values are also judged illegal.
		illegal_flag = false;
		if (decrease_enabled) {
			for (int x = size.getWidth() / 2 + 1 ; x < size.getWidth() ; x++) {
				if (illegal_flag  ||  array.getValueAt(x) - array.getValueAt(x - 1) > stat.getDeviation() * 3.0) {
					illegal_flag = true;
					for (int y = 0 ; y < size.getHeight() ; y++)
						image.setValue(index.get(x), y, stat.getMedian());
					modified_decreased = true;
					modified_vertical = true;
				}
			}
		}
		illegal_flag = false;
		for (int x = size.getWidth() / 2 - 1 ; x >= 0 ; x--) {
			if (illegal_flag  ||  array.getValueAt(x + 1) - array.getValueAt(x) > stat.getDeviation() * 3.0) {
				illegal_flag = true;
				for (int y = 0 ; y < size.getHeight() ; y++)
					image.setValue(index.get(x), y, stat.getMedian());
				modified_increased = true;
				modified_vertical = true;
			}
		}

		return image;
	}
}
