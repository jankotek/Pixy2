/*
 * @(#)StarClass.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.xml;
import net.aerith.misao.util.star.*;

/**
 * The <code>StarClass</code> is a class with a funciton to convert 
 * between the Java class and the <tt>class</tt> element in the XML
 * document of a star.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 February 28
 */

public class StarClass {
	/**
	 * Gets the list of class paths to search Java class of a star.
	 * @return the list of class paths to search Java class of a star.
	 */
	private static String[] getClassPath ( ) {
		String[] paths = new String[2];
		paths[0] = "net.aerith.misao.util.star";
		paths[1] = "net.aerith.misao.catalog.star";
		return paths;
	}

	/**
	 * Gets the name for the <tt>class</tt> element in the XML 
	 * document of the specified star object.
	 * @param star the star object.
	 * @return the name for the <tt>class</tt> element.
	 */
	public static String getClassName ( Star star ) {
		String class_name = star.getClass().getName();

		String[] paths = getClassPath();
		for (int i = 0 ; i < paths.length ; i++) {
			int l = paths[i].length();
			if (class_name.substring(0, l).equals(paths[i]))
				return class_name.substring(l+1);
		}

		return "";
	}

	/**
	 * Constructs a new Java star object of the specified name of the 
	 * <tt>class</tt> element in the XML document.
	 * @param name the name of the <tt>class</tt> element.
	 * @return the Java star object.
	 * @exception ClassNotFoundException if the class cannot be 
	 * located.
	 * @exception IllegalAccessException if the class or initializer 
	 * is not accessible.
	 * @exception InstantiationException if the class represents an 
	 * abstract class, an interface, an array class, a primitive type,
	 * or void; or if the instantiation fails for some other reason.
	 */
	public static Star newInstance ( String name )
		throws ClassNotFoundException, IllegalAccessException, InstantiationException
	{
		String[] paths = getClassPath();
		for (int i = 0 ; i < paths.length ; i++) {
			try {
				Class t = Class.forName(paths[i] + "." + name);

				return (Star)t.newInstance();
			} catch ( ClassNotFoundException exception ) {
			}
		}

		throw new ClassNotFoundException(name);
	}
}
