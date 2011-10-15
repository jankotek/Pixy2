/*
 * @(#)Main.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.pixy;
import java.io.*;
import java.util.*;
import net.aerith.misao.gui.dialog.*;

/**
 * The <code>Main</code> is a class to run the PIXY System in the 
 * command line mode.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2005 January 2
 */

public class Main {
	/**
	 * Starts the application.
	 * @param args the options.
	 * @exception Exception if an exception occurs.
	 */
	public static void main ( String[] args )
		throws Exception
	{
		System.setErr(System.out);


		String[] messages = Resource.getVersionAndCopyright();
		for (int i = 0 ; i < messages.length ; i++)
			System.out.println(messages[i]);
		System.out.println("");

		// Loads the plug-in kernels.

		File directory = Properties.getPluginDirectory();
		if (directory == null)
			directory = new File(".");

		Kernel[] kernels = PluginLoader.loadKernels();

		if (kernels.length == 0) {
			System.err.println("No plug-in kernels are found in " + directory.getAbsolutePath() + " directory.");
		} else if (kernels.length == 1) {
			kernels[0].run(args);
		} else {
			boolean found = false;

			if (args.length == 0) {
				System.err.println("Please select one of the available kernels:");

				for (int i = 0 ; i < kernels.length ; i++)
					System.err.println("  " + kernels[i].getName());
			} else {
				for (int i = 0 ; i < kernels.length ; i++) {
					if (kernels[i].getName().equals(args[0])) {
						kernels[i].run(args);

						found = true;
						break;
					}
				}

				if (found == false)
					System.err.println("No plug-in kernel \"" + args[0] + "\" is found in " + directory.getAbsolutePath() + " directory.");
			}
		}

		System.exit(0);
	}
}
