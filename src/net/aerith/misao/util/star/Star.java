/*
 * @(#)Star.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util.star;
import java.awt.Color;
import net.aerith.misao.util.*;

/**
 * The <code>Star</code> represents a star which consists of (x,y) 
 * position, R.A. and Decl., and magnitude. They are expressed in 
 * float data. It also has a color to plot on a chart.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 December 27
 */

public class Star extends StarPosition implements Coordinates {
	/**
	 * The R.A. and Decl.
	 */
	protected Coor coor;

	/**
	 * The pair which contains this object.
	 */
	protected StarPair container_pair = null;

	/**
	 * The colot to plot on a chart.
	 */
	protected Color color;

	/**
	 * Constructs an empty <code>Star</code>. All member fields are
	 * set as 0.
	 */
	public Star ( ) {
		super();
		coor = new Coor();
	}

	/**
	 * Gets R.A. 
	 * @return the R.A.
	 */
	public double getRA ( ) {
		return coor.getRA();
	}

	/**
	 * Gets Decl.
	 * @return the Decl.
	 */
	public double getDecl ( ) {
		return coor.getDecl();
	}

	/**
	 * Gets R.A. and Decl.
	 * @return the R.A. and Decl.
	 */
	public Coor getCoor ( ) {
		return coor;
	}

	/**
	 * Sets R.A. and Decl.
	 * @param new_coor the new R.A. and Decl.
	 */
	public void setCoor ( Coor new_coor ) {
		coor = new_coor;
	}

	/**
	 * Gets the colot to plot on a chart.
	 */
	public Color getColor ( ) {
		return color;
	}

	/**
	 * Sets the colot to plot on a chart.
	 */
	public void setColor ( Color new_color ) {
		color = new_color;
	}

	/**
	 * Gets the name of this star. In principle, this method must be
	 * overrided in subclasses. This method returns such a string as
	 * <tt>J123456.78+012345.6</tt>. The name can have some single
	 * space characters, but cannot have double space characters.
	 * @return the name of this star.
	 */
	public String getName ( ) {
		String name = coor.getOutputStringTo100mArcsecWithoutUnit();
		name = Format.removeSpace(name);
		return "J" + name;
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). In principle, this method must be overrided in
	 * subclasses. This method returns the name itself. The name 
	 * cannot have any space characters.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		return getName().replace(' ', '_');
	}

	/**
	 * Maps the R.A. and Decl. to the (x,y) position based on the
	 * specified <code>ChartMapFunction</code>.
	 * @param cmf the map function.
	 */
	public void mapCoordinatesToXY ( ChartMapFunction cmf ) {
		mapCoordinatesToXY(cmf, null);
	}

	/**
	 * Maps the R.A. and Decl. to the (x,y) position based on the
	 * specified <code>ChartMapFunction</code>.
	 * @param cmf the map function.
	 * @param df  the distortion field.
	 */
	public void mapCoordinatesToXY ( ChartMapFunction cmf, DistortionField df ) {
		setPosition(cmf.mapCoordinatesToXY(getCoor()));

		if (df != null) {
			Position df_pos = df.inverse().getValue(this);
			x += df_pos.getX();
			y += df_pos.getY();
		}
	}

	/**
	 * Maps the (x,y) position to the R.A. and Decl. based on the
	 * specified <code>ChartMapFunction</code>.
	 * @param cmf the map function.
	 */
	public void mapXYToCoordinates ( ChartMapFunction cmf ) {
		mapXYToCoordinates(cmf, null);
	}

	/**
	 * Maps the (x,y) position to the R.A. and Decl. based on the
	 * specified <code>ChartMapFunction</code>.
	 * @param cmf the map function.
	 * @param df  the distortion field.
	 */
	public void mapXYToCoordinates ( ChartMapFunction cmf, DistortionField df ) {
		Position position = new Position(getX(), getY());

		if (df != null) {
			Position df_pos = df.getValue(this);
			position.setX(position.getX() + df_pos.getX());
			position.setY(position.getY() + df_pos.getY());
		}

		setCoor(cmf.mapXYToCoordinates(position));
	}

	/**
	 * Gets the pair which contains this object.
	 * @return the pair which contains this object.
	 */
	public StarPair getPair ( ) {
		return container_pair;
	}

	/**
	 * Sets the pair which contains this object.
	 * @param pair the pair which contains this object.
	 */
	public void setPair ( StarPair pair ) {
		container_pair = pair;
	}

	/**
	 * Returns true when the specified star equals to this object.
	 * @return true when the specified star equals to this object.
	 */
	public boolean equals ( Star star ) {
		return getOutputString().equals(star.getOutputString());
	}

	/**
	 * Gets a string representing the delimiter between each items,
	 * that is two space characters.
	 * @return the string representing the delimiter between each
	 * items.
	 */
	public final static String getItemDelimiter ( ) {
		return "  ";
	}

	/**
	 * Gets a string representing the delimiter between the key and
	 * the value, that is a colon.
	 * @return the string representing the delimiter between the key
	 * and the value.
	 */
	public final static String getKeyAndValueDelimiter ( ) {
		return ":";
	}

	/**
	 * Gets a string representing the R.A. and Decl. in a proper 
	 * format and accuracy. This method can be overrided in subclasses.
	 * This method returns such a string as <tt>12h34m56s.78 
	 * +01o23'45".6</tt>.
	 * @return the string representing R.A. and Decl.
	 */
	public String getCoorString ( ) {
		return coor.getOutputStringTo100mArcsecWithUnit();
	}

	/**
	 * Gets a string representing the R.A. and Decl. in a proper 
	 * format and accuracy without unit.
	 * @return the string representing R.A. and Decl.
	 */
	public String getCoorStringWithoutUnit ( ) {
		return coor.getOutputStringTo100mArcsecWithoutUnit();
	}

	/**
	 * Gets a string representing the (x,y) position. This method 
	 * returns such a string as <tt>(1234.56, 1234.56)</tt>.
	 * @return the string representing (x,y) position.
	 */
	public String getPositionString ( ) {
		return "(" + Format.formatDouble(getX(), 7, 4) + "," + Format.formatDouble(getY(), 7, 4) + ")";
	}

	/**
	 * Gets an array of keys and values to output. In principle, this
	 * method must be overrided in subclasses. This method returns an
	 * empty array.
	 * @return an array of keys and values to output.
	 */
	public KeyAndValue[] getKeyAndValues ( ) {
		return new KeyAndValue[0];
	}

	/**
	 * Sets the value of the specified key. In principle, this method
	 * must be overrided in subclasses.
	 * @param key_and_value the key and value to set.
	 */
	public void setKeyAndValue ( KeyAndValue key_and_value ) {
	}

	/**
	 * Returns a string representation of the state of this object.
	 * @return a string representation of the state of this object.
	 */
	public String getOutputString ( ) {
		String s = getName() + getItemDelimiter() + getCoorString();

		KeyAndValue[] key_and_values = getKeyAndValues();
		for (int i = 0 ; i < key_and_values.length ; i++)
			s += getItemDelimiter() + key_and_values[i].getKey() + getKeyAndValueDelimiter() + key_and_values[i].getValue();

		return s;
	}

	/**
	 * Returns a string representation of the state of this object.
	 * @return a string representation of the state of this object.
	 */
	public String getOutputStringWithoutName ( ) {
		String string = getOutputString();
		int p = string.indexOf(getItemDelimiter());
		return string.substring(p + getItemDelimiter().length());
	}

	/**
	 * Returns a string representation of the state of this object.
	 * @return a string representation of the state of this object.
	 */
	public String getOutputStringWithXY ( ) {
		return getPositionString() + "  " + getOutputString();
	}

	/**
	 * Returns an array of string representations of the state of 
	 * stars contained in this object. In general, it only returns a
	 * string representation of the state of only this object itself, 
	 * except for <code>UnifiedStar</code> and <code>BlendingStar</code>,
	 * the subclasses of <code>Star</code>.
	 * @return an array of string representations.
	 */
	public String[] getOutputStringsWithXY ( ) {
		String[] s = new String[1];
		s[0] = getOutputStringWithXY();
		return s;
	}

	/**
	 * Returns a string representation of the state of this object
	 * for PXF output.
	 * @return a string representation of the state of this object.
	 */
	public String getPxfString ( ) {
		String s = getName() + getItemDelimiter();
		s += getCoorStringWithoutUnit();

		KeyAndValue[] key_and_values = getKeyAndValues();
		for (int i = 0 ; i < key_and_values.length ; i++)
			s += getItemDelimiter() + key_and_values[i].getKey() + getKeyAndValueDelimiter() + key_and_values[i].getValue();

		return s;
	}

	/**
	 * Returns a string representation of the state of this object
	 * for PXF output.
	 * @return a string representation of the state of this object.
	 */
	public String getPxfStringWithXY ( ) {
		return getPositionString() + "  " + getPxfString();
	}

	/**
	 * Returns an array of string representations of the state of 
	 * stars contained in this object. In general, it only returns a
	 * string representation of the state of only this object itself, 
	 * except for <code>UnifiedStar</code> and <code>BlendingStar</code>,
	 * the subclasses of <code>Star</code>.
	 * @return an array of string representations.
	 */
	public String[] getPxfStringsWithXY ( ) {
		String[] s = new String[1];
		s[0] = getPxfStringWithXY();
		return s;
	}
}
