/*
 * @(#)XmlInstruction.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.xml;
import java.io.*;
import net.aerith.misao.io.FileManager;

/**
 * The <code>XmlInstruction</code> is an application side implementation
 * of the class that the relaxer generated automatically.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 6
 */

public class XmlInstruction extends net.aerith.misao.xml.relaxer.XmlInstruction {
	/**
	 * Gets the output file name of the specified type.
	 * @param type the type of the file.
	 * @return the output file name of the specified type, or null
	 * if no file name of the type is recorded in the XML document.
	 */
	public String getOutput ( String type ) {
		XmlOutput[] outputs = (XmlOutput[])getOutput();
		for (int i = 0 ; i < outputs.length ; i++) {
			if (outputs[i].getType().equals(type))
				return outputs[i].getContent();
		}
		return null;
	}

	/**
	 * Gets the image file. In general, it returns a file at the 
	 * specified relative path of the specified file manager. If it 
	 * does not exist, it returns a file at the specified relative 
	 * path from the directory where the XML report document is 
	 * stored. In the latter case, the XML file path must be recorded
	 * in the <output> element with a type attribute of "xml".
	 * @param file_manager the file manager.
	 * @return the image file.
	 */
	public File getImageFile ( FileManager file_manager ) {
		File file = file_manager.newFile(getImage().getContent());
		if (file.canRead() == false) {
			// In the case the image path is recorded as a relative path
			// from the directory where the XML path is stored.
			if (getOutput("xml") != null) {
				File xml_file = new File(getOutput("xml"));
				File file2 = new File(FileManager.unitePath(new File(xml_file.getAbsolutePath()).getParent(), getImage().getContent()));

				if (file2.canRead())
					return file2;
			}
		}

		return file;
	}

	/**
	 * Returns true when not to calculate the limiting magnitude 
	 * automatically.
	 * @return true when not to calculate the limiting magnitude 
	 * automatically.
	 */
	public boolean fixesLimitingMagnitude ( ) {
		if (getLimitingMag() != null  ||  getUpperLimitMag() != null)
			return true;

		return false;
	}

	/**
	 * Gets the proper limiting magnitude. In the case the limiting 
	 * magnitude is not recorded, it returns the upper-limit magnitude.
	 * @return the proper limiting magnitude.
	 */
	public double getProperLimitingMag ( ) {
		if (getLimitingMag() != null)
			return (double)getLimitingMag().floatValue();
		if (getUpperLimitMag() != null)
			return (double)getUpperLimitMag().floatValue();

		return 0.0;
	}

	/**
	 * Gets the proper upper-limit magnitude. In the case the 
	 * upper-limit magnitude is not recorded, it returns the limiting 
	 * magnitude.
	 * @return the proper upper-limit magnitude.
	 */
	public double getProperUpperLimitMag ( ) {
		if (getUpperLimitMag() != null)
			return (double)getUpperLimitMag().floatValue();
		if (getLimitingMag() != null)
			return (double)getLimitingMag().floatValue();

		return 0.0;
	}
}
