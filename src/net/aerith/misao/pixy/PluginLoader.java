/*
 * @(#)PluginLoader.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.pixy;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import net.aerith.misao.io.FileManager;
import net.aerith.misao.io.filechooser.ImageFileFilter;

/**
 * The <code>PluginLoader</code> is a plug-in class loader.
 * <p>
 * The directory of the plug-in class files can be specified by the 
 * system property <b>misao.plugin</b>. By default, it reads class 
 * files from the current directory.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 September 17
 */

public class PluginLoader {
	/**
	 * The plug-in class instances.
	 */
	private static Object[] plugins = new Object[0];

	/**
	 * Loads the plug-in image file filters.
	 * @return the list of plug-in image file filters.
	 */
	public static ImageFileFilter[] loadImageFileFilters ( ) {
		ArrayList list = new ArrayList();

		for (int i = 0 ; i < plugins.length ; i++) {
			if (plugins[i] instanceof ImageFileFilter)
				list.add(plugins[i]);
		}

		ImageFileFilter[] filters = new ImageFileFilter[list.size()];
		return (ImageFileFilter[])list.toArray(filters);
	}

	/**
	 * Loads the plug-in agents.
	 * @return the list of plug-in agents.
	 */
	public static Agent[] loadAgents ( ) {
		ArrayList list = new ArrayList();

		for (int i = 0 ; i < plugins.length ; i++) {
			if (plugins[i] instanceof Agent)
				list.add(plugins[i]);
		}

		Agent[] agents = new Agent[list.size()];
		return (Agent[])list.toArray(agents);
	}

	/**
	 * Loads the plug-in kernels.
	 * @return the list of plug-in kernels.
	 */
	public static Kernel[] loadKernels ( ) {
		ArrayList list = new ArrayList();

		for (int i = 0 ; i < plugins.length ; i++) {
			if (plugins[i] instanceof Kernel)
				list.add(plugins[i]);
		}

		Kernel[] kernels = new Kernel[list.size()];
		return (Kernel[])list.toArray(kernels);
	}

	/**
	 * Loads the plug-in class files and create their instances.
	 */
	static {
		try {
			File directory = Properties.getPluginDirectory();
			if (directory == null)
				directory = new File(".");

			URL[] urls = new URL[1];
			urls[0] = directory.toURI().toURL();
			URLClassLoader class_loader = new URLClassLoader(urls);

			if (directory.isDirectory()) {
				ArrayList list = new ArrayList();

				File[] files = directory.listFiles();
				for (int i = 0 ; i < files.length ; i++) {
					String name = files[i].getName();
					if (FileManager.endsWith(name, ".class")) {
						try {
							String class_name = name.substring(0, name.indexOf(".class"));
							Object clazz = class_loader.loadClass(class_name).newInstance();
							list.add(clazz);
						} catch ( ClassNotFoundException exception ) {
						} catch ( InstantiationException exception ) {
						} catch ( IllegalAccessException exception ) {
						}
					}
				}

				plugins = list.toArray();
			}
		} catch ( MalformedURLException exception ) {
		}
	}
}
