/*
 * @(#)PositionMap.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;
import java.util.Vector;

/**
 * The <code>PositionMap</code> represents a rectangular map of
 * <code>Position</code>s which consists of a list of (x,y) data and 
 * the two <code>Position</code>s at corners of the map area.
 * <p>
 * This class has functions to search data around the specified 
 * position. This <code>PositionMap</code> can be divided into some
 * parts. After division properly, the search will be much faster.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 January 9
 */

public class PositionMap {
	/**
	 * The list of (x,y) positions.
	 */
	protected Vector list;

	/**
	 * The position at the top-left corner.
	 */
	protected Position top_left;

	/**
	 * The position at the bottom-right corner.
	 */
	protected Position bottom_right;

	/**
	 * The table of divided parts.
	 */
	protected Vector[][] table;

	/**
	 * The number of rows of divided table.
	 */
	protected int table_rows;

	/**
	 * The number of columns of divided table.
	 */
	protected int table_columns;

	/**
	 * True if <code>OutOfBoundsException</code> must not be thrown.
	 */
	protected boolean accept_out_of_bounds = false;

	/**
	 * Constructs an empty <code>PositionMap</code> with the positions
	 * at corners.
	 * @param top_left     the position at top-left corner.
	 * @param bottom_right the position at bottom-right corner.
	 */
	public PositionMap ( Position top_left, Position bottom_right ) {
		list = new Vector();
		table = null;

		this.top_left = top_left;
		this.bottom_right = bottom_right;
	}

	/**
	 * Copies the specified <code>PositionMap</code> and constructs a
	 * new <code>PositionMap</code>.
	 * @param original_map the original map to copy.
	 */
	public PositionMap ( PositionMap original_map ) {
		this(original_map.top_left, original_map.bottom_right);

		accept_out_of_bounds = original_map.accept_out_of_bounds;

		list = new Vector();
		try {
			addPosition(original_map.list);
		} catch ( OutOfBoundsException exception ) {
			// Never happens.
		}

		if (original_map.table != null)
			divide(original_map.table_rows, original_map.table_columns);
	}

	/**
	 * Constructs a <code>PositionMap</code> with the list of 
	 * positions. The top-left and bottom-right corner positions are
	 * automatically calculated.
	 * @param list the list of (x,y) positions.
	 */
	public PositionMap ( PositionList list ) {
		this.list = new Vector();
		table = null;

		top_left = new Position();
		bottom_right = new Position();

		for (int i = 0 ; i < list.size() ; i++) {
			Position pos = (Position)list.elementAt(i);
			if (i == 0  ||  top_left.getX() > pos.getX())
				top_left.setX(pos.getX());
			if (i == 0  ||  top_left.getY() > pos.getY())
				top_left.setY(pos.getY());
			if (i == 0  ||  bottom_right.getX() < pos.getX())
				bottom_right.setX(pos.getX());
			if (i == 0  ||  bottom_right.getY() < pos.getY())
				bottom_right.setY(pos.getY());
		}

		// Just for safety.
		bottom_right.setX((bottom_right.getX() - top_left.getX()) * 0.00001 + bottom_right.getX());
		bottom_right.setY((bottom_right.getY() - top_left.getY()) * 0.00001 + bottom_right.getY());
		if (bottom_right.getX() <= top_left.getX())
			bottom_right.setX(top_left.getX() + 1.0);
		if (bottom_right.getY() <= top_left.getY())
			bottom_right.setY(top_left.getY() + 1.0);

		try {
			addPosition(list);
		} catch ( OutOfBoundsException exception ) {
			// Never happens.
		}
	}

	/**
	 * Accepts the data out of the area, which will be shifted onto 
	 * this map area. The <code>OutOfBoundsException</code> must not 
	 * be thrown.
	 */
	public void acceptOutOfBounds ( ) {
		accept_out_of_bounds = true;
	}

	/**
	 * Excepts the data out of the area and the
	 * <code>OutOfBoundsException</code> must be thrown.
	 */
	public void exceptOutOfBounds ( ) {
		accept_out_of_bounds = false;
	}

	/**
	 * Checks if the specified position is out of this map area.
	 * @param position the position to check.
	 * @return true if the specified position is out of this map area.
	 */
	public boolean isOutOfBounds ( Position position ) {
		Position pos = convertPosition(position);

		if (pos.getX() < top_left.getX()  ||
			pos.getX() > bottom_right.getX()  ||
			pos.getY() < top_left.getY()  ||
			pos.getY() > bottom_right.getY()) {
			return true;
		}
		return false;
	}

	/**
	 * Converts the (x,y) position into the proper system for this 
	 * map. This method will be overrided in the subclasses. Thanks to
	 * this method, the (x,y) positions in the list do not have to be 
	 * converted into the linear system in fact and remain original.
	 * @param position the original (x,y).
	 * @return the converted position in the system of this map.
	 */
	protected Position convertPosition ( Position position ) {
		return position;
	}

	/**
	 * Adds a (x,y) position on this map.
	 * @param new_position the new (x,y) position.
	 * @exception OutOfBoundsException if the specified position is
	 * out of the map area.
	 */
	public void addPosition ( Position new_position )
		throws OutOfBoundsException
	{
		Position pos = convertPosition(new_position);

		if (pos.getX() < top_left.getX()  ||
			pos.getX() > bottom_right.getX()  ||
			pos.getY() < top_left.getY()  ||
			pos.getY() > bottom_right.getY()) {
			if (accept_out_of_bounds == false) {
				throw new OutOfBoundsException();
			}
		}

		list.addElement(new_position);

		if (table != null)
			getPartialList(new_position).addElement(new_position);
	}

	/**
	 * Adds a list of (x,y) positions on this map.
	 * @param new_list the list of new (x,y) positions.
	 * @exception OutOfBoundsException if one of the position in the
	 * specified list is out of the map area.
	 */
	public void addPosition ( Vector new_list )
		throws OutOfBoundsException
	{
		for (int i = 0 ; i < new_list.size() ; i++) {
			Position position = (Position)new_list.elementAt(i);
			addPosition(position);
		}
	}

	/**
	 * Removes the specified position object from this map.
	 * @param position the position object to be removed.
	 * @return true if the specified position object is in this map.
	 */
	public boolean removePosition ( Position position ) {
		boolean found = list.remove(position);
		if (found == false)
			return false;

		if (table != null) {
			Position pos = convertPosition(position);

			double width = (bottom_right.getX() - top_left.getX()) / (double)table_columns;
			double height = (bottom_right.getY() - top_left.getY()) / (double)table_rows;

			int column = (int)((pos.getX() - top_left.getX()) / width);
			int row = (int)((pos.getY() - top_left.getY()) / height);

			if (row < 0  ||  row >= table_rows  ||  column < 0  ||  column >= table_columns) {
				if (row < 0)
					row = 0;
				if (row >= table_rows)
					row = table_rows - 1;
				if (column < 0)
					column = 0;
				if (column >= table_columns)
					column = table_columns - 1;
			}

			table[row][column].remove(position);
		}

		return true;
	}

	/**
	 * Gets the width of this map.
	 * @return the width of this map.
	 */
	public double getWidth ( ) {
		return bottom_right.getX() - top_left.getX();
	}

	/**
	 * Gets the height of this map.
	 * @return the height of this map.
	 */
	public double getHeight ( ) {
		return bottom_right.getY() - top_left.getY();
	}

	/**
	 * Gets the (x,y) of the top left corner.
	 * @return the (x,y) of the top left corner.
	 */
	public Position getTopLeftCorner ( ) {
		return new Position(top_left.getX(), top_left.getY());
	}

	/**
	 * Gets the (x,y) of the top right corner.
	 * @return the (x,y) of the top right corner.
	 */
	public Position getTopRightCorner ( ) {
		return new Position(bottom_right.getX(), top_left.getY());
	}

	/**
	 * Gets the (x,y) of the bottom left corner.
	 * @return the (x,y) of the bottom left corner.
	 */
	public Position getBottomLeftCorner ( ) {
		return new Position(top_left.getX(), bottom_right.getY());
	}

	/**
	 * Gets the (x,y) of the bottom right corner.
	 * @return the (x,y) of the bottom right corner.
	 */
	public Position getBottomRightCorner ( ) {
		return new Position(bottom_right.getX(), bottom_right.getY());
	}

	/**
	 * Gets the (x,y) of the center.
	 * @return the (x,y) of the center.
	 */
	public Position getCenter ( ) {
		return new Position((top_left.getX() + bottom_right.getX()) / 2.0, (top_left.getY() + bottom_right.getY()) / 2.0);
	}

	/**
	 * Gets the area of this map.
	 * @return the area of this map.
	 */
	public double getArea ( ) {
		return getWidth() * getHeight();
	}

	/**
	 * Gets the list of all data in this map.
	 * @return the new list which contains all data in this map.
	 */
	public Vector getAllPositions ( ) {
		Vector l = new Vector();
		for (int i = 0 ; i < list.size() ; i++)
			l.addElement(list.elementAt(i));
		return l;
	}

	/**
	 * Gets the list of divided position maps.
	 * @return the list of divided position maps.
	 */
	public Vector getDividedPositionMapList ( ) {
		Vector l = new Vector();

		if (table == null) {
			l.addElement(this);
		} else {
			double width = (bottom_right.getX() - top_left.getX()) / (double)table_columns;
			double height = (bottom_right.getY() - top_left.getY()) / (double)table_rows;

			for (int row = 0 ; row < table_rows ; row++) {
				for (int column = 0 ; column < table_columns ; column++) {
					try {
						Position p1 = new Position(top_left.getX() + width * (double)column, top_left.getY() + height * (double)row);
						Position p2 = new Position(top_left.getX() + width * (double)(column + 1), top_left.getY() + height * (double)(row + 1));
						PositionMap map = new PositionMap(p1, p2);
						map.addPosition(table[row][column]);
						l.addElement(map);
					} catch ( OutOfBoundsException exception ) {
					}
				}
			}
		}

		return l;
	}

	/**
	 * Gets the partial list on the divided map where the specified 
	 * position locates in. If this map is not yet divided, it returns
	 * null.
	 * @param position the position.
	 * @return the list where the position locates in.
	 * @exception OutOfBoundsException if the specified position is
	 * out of the map area.
	 */
	public Vector getPartialList ( Position position )
		throws OutOfBoundsException
	{
		if (table == null)
			return null;

		Position pos = convertPosition(position);

		double width = (bottom_right.getX() - top_left.getX()) / (double)table_columns;
		double height = (bottom_right.getY() - top_left.getY()) / (double)table_rows;

		int column = (int)((pos.getX() - top_left.getX()) / width);
		int row = (int)((pos.getY() - top_left.getY()) / height);

		if (row < 0  ||  row >= table_rows  ||  column < 0  ||  column >= table_columns) {
			if (accept_out_of_bounds == false) {
				throw new OutOfBoundsException();
			}

			if (row < 0)
				row = 0;
			if (row >= table_rows)
				row = table_rows - 1;
			if (column < 0)
				column = 0;
			if (column >= table_columns)
				column = table_columns - 1;
		}

		return table[row][column];
	}

	/**
	 * Creates a list of elements in the partial lists on the divided
	 * map where the specified position locates in and around within
	 * the specified division steps. If this map is not yet divided, 
	 * it returns null.
	 * @param position     the position.
	 * @param row_steps    the number of division steps to search
	 * vertically.
	 * @param column_steps the number of division steps to search
	 * horizontally.
	 * @return the list where the position locates in and around.
	 * @exception OutOfBoundsException if the specified position is
	 * out of the map area.
	 */
	public Vector getPartialListWithinSteps ( Position position, int row_steps, int column_steps )
		throws OutOfBoundsException
	{
		if (table == null)
			return null;

		double width = (bottom_right.getX() - top_left.getX()) / (double)table_columns;
		double height = (bottom_right.getY() - top_left.getY()) / (double)table_rows;

		Position pos = convertPosition(position);

		int base_column = (int)((pos.getX() - top_left.getX()) / width);
		int base_row = (int)((pos.getY() - top_left.getY()) / height);

		if (base_row < 0  ||  base_row >= table_rows  ||  base_column < 0  ||  base_column >= table_columns) {
			if (accept_out_of_bounds == false) {
				throw new OutOfBoundsException();
			}

			if (base_row < 0)
				base_row = 0;
			if (base_row >= table_rows)
				base_row = table_rows - 1;
			if (base_column < 0)
				base_column = 0;
			if (base_column >= table_columns)
				base_column = table_columns - 1;
		}

		Vector l = new Vector();
		for (int row = base_row - row_steps ; row <= base_row + row_steps ; row++) {
			if (0 <= row  &&  row < table_rows) {
				for (int column = base_column - column_steps ; column <= base_column + column_steps ; column++) {
					if (0 <= column  &&  column < table_columns) {
						for (int i = 0 ; i < table[row][column].size() ; i++)
							l.addElement(table[row][column].elementAt(i));
					}
				}
			}
		}

		return l;
	}

	/**
	 * Creates a list of elements in the partial lists on the divided
	 * map where the specified position locates in and around within
	 * the specified radius. If this map is not yet divided, it 
	 * returns null.
	 * @param position the position.
	 * @param radius   the radius to search.
	 * @return the list where the position locates in and around.
	 * @exception OutOfBoundsException if the specified position is
	 * out of the map area.
	 */
	public Vector getPartialListWithinRadius ( Position position, double radius )
		throws OutOfBoundsException
	{
		if (table == null)
			return null;

		double width = (bottom_right.getX() - top_left.getX()) / (double)table_columns;
		double height = (bottom_right.getY() - top_left.getY()) / (double)table_rows;

		int row_steps = (int)(radius / height) + 1;
		int column_steps = (int)(radius / width) + 1;

		Vector tmp_list = getPartialListWithinSteps(position, row_steps, column_steps);

		// Removes elements which is really out of the radius.
		Vector l = new Vector();
		Position pos1 = convertPosition(position);
		for (int i = 0 ; i < tmp_list.size() ; i++) {
			Position pos2 = convertPosition((Position)tmp_list.elementAt(i));
			double distance = Math.sqrt((pos1.getX() - pos2.getX()) * (pos1.getX() - pos2.getX()) + 
										(pos1.getY() - pos2.getY()) * (pos1.getY() - pos2.getY()));
			if (distance <= radius)
				l.addElement(tmp_list.elementAt(i));
		}

		return l;
	}

	/**
	 * Divides this map into some parts for fast search, based on the
	 * specified unit.
	 * @param unit_row    the unit to divide vertically.
	 * @param unit_column the unit to divide horizontally.
	 */
	public void divideByUnit ( double unit_row, double unit_column ) {
		int rows = (int)((bottom_right.getX() - top_left.getX()) / unit_row);
		if (rows <= 0)
			rows = 1;
		int columns = (int)((bottom_right.getY() - top_left.getY()) / unit_column);
		if (columns <= 0)
			columns = 1;

		divide(rows, columns);
	}

	/**
	 * Divides this map into some parts for fast search.
	 * @param new_rows    the number to divide vertically.
	 * @param new_columns the number to divide horizontally.
	 */
	public void divide ( int new_rows, int new_columns ) {
		table_rows = new_rows;
		table_columns = new_columns;
		table = new Vector[table_rows][table_columns];

		for (int row = 0 ; row < table_rows ; row++) {
			for (int column = 0 ; column < table_columns ; column++) {
				table[row][column] = new Vector();
			}
		}

		try {
			for (int i = 0 ; i < list.size() ; i++) {
				Position position = (Position)list.elementAt(i);
				getPartialList(position).addElement(position);
			}
		} catch ( OutOfBoundsException exception ) {
		}
	}

	/**
	 * Divides this map into some parts so that a circle with the 
	 * specified radius covers each divided part.
	 * @param radius the radius of a circle.
	 */
	public void divideByCircleCoverage ( double radius ) {
		int column_division = 1;
		int row_division = 1;

		double width = bottom_right.getX() - top_left.getX();
		double height = bottom_right.getY() - top_left.getY();

		while (Math.sqrt(width * width + height * height) > radius * 2.0) {
			if (width > height) {
				column_division++;
				width = (bottom_right.getX() - top_left.getX()) / (double)column_division;
			} else {
				row_division++;
				height = (bottom_right.getY() - top_left.getY()) / (double)row_division;
			}
		}

		divide(row_division, column_division);
	}

	/**
	 * Returns a raw string representation of the state of this object,
	 * for debugging use. It should be invoked from <code>toString</code>
	 * method of the subclasses.
	 * @return a string representation of the state of this object.
	 */
	protected String paramString ( ) {
		return "top_left=(" + top_left.getX() + "," + top_left.getY() + "),bottom_right=(" + bottom_right.getX() + "," + bottom_right.getY() + "),table_columns=" + table_columns + ",table_rows=" + table_rows + ",list=" + list.size();
	}

	/**
	 * Returns a string representation of the state of this object,
	 * for debugging use.
	 * @return a string representation of the state of this object.
	 */
	public String toString ( ) {
		return getClass().getName() + "[" + paramString() + "]";
	}
}
