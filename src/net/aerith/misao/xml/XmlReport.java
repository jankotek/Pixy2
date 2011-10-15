/*
 * @(#)XmlReport.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.xml;
import java.io.*;
import java.lang.reflect.*;
import javax.xml.parsers.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.io.Decoder;

/**
 * The <code>XmlReport</code> is an application side implementation
 * of the class that the relaxer generated automatically.
 * <p>
 * In order to reduce the memory assumption, all the star data are
 * converted into the internal data by <tt>masticateXml</tt> method 
 * after reading the XML file, and restored into the XML data by
 * <tt>composeXml</tt> method.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2005 January 2
 */

public class XmlReport extends net.aerith.misao.xml.relaxer.XmlReport {
	/**
	 * Reads this XML document from the specified reader.
	 * <p>
	 * This method does not use the standard DOM parser, but runs 
	 * faster and does not consume too much memory.
	 * @param in the reader.
	 * @exception IOException if I/O error occurs.
	 */
	public void read ( Reader in )
		throws IOException
	{
		/*
		try {
			DocumentBuilderFactory builder_factory = new DocumentBuilderFactoryImpl();
			DocumentBuilder builder = builder_factory.newDocumentBuilder();

			Document document = null;
			try {
				document = builder.parse(new InputSource(in));
			} catch ( OutOfMemoryError error ) {
				document = null;

				System.gc();

				try {
					Thread.sleep(60000);
				} catch ( InterruptedException exception ) {
					System.err.println(exception);
				}

				document = builder.parse(new InputSource(in));
			}

			setup(document);

			((Data)getData()).masticateXml();

			((Data)getData()).deleteXml();

			Size sz = new Size(getInformation().getSize().getWidth(), getInformation().getSize().getHeight());
			((Data)getData()).createStarMap(sz);
		} catch ( SAXException exception ) {
			System.err.println(exception);
			throw new IOException();
		} catch ( ParserConfigurationException exception ) {
			System.err.println(exception);
			throw new IOException();
		}
		*/

		BufferedReader buffered_in = null;
		if (in instanceof BufferedReader)
			buffered_in = (BufferedReader)in;
		else
			buffered_in = new BufferedReader(in);

		try {
			String xml_version = buffered_in.readLine();
			buffered_in.readLine();

			buffered_in.readLine();	// <report>
			buffered_in.readLine();	// <system>

			XmlSystem system = new XmlSystem();
			system.setVersion(getContent(buffered_in.readLine()));
			system.setExaminedDate(getContent(buffered_in.readLine()));
			system.setModifiedDate(getContent(buffered_in.readLine()));
			setSystem(system);

			buffered_in.readLine();	// </system>

			String information_string = xml_version + "\n";
			while (true) {
				String s = buffered_in.readLine();
				information_string += s;

				if (s.indexOf("</information>") >= 0)
					break;
			}
			XmlInformation info = new XmlInformation();
			Reader string_in = new StringReader(information_string);
			info.read(string_in);
			string_in.close();
			setInformation(info);

			buffered_in.readLine();	// <data>

			XmlData data = new XmlData();

			XmlStar xml_star = null;
			XmlRecord xml_record = null;
			XmlCoor coor = new XmlCoor();
			XmlPosition position = new XmlPosition();

			while (true) {
				String s = buffered_in.readLine();
				if (s.indexOf("</data>") >= 0)
					break;

				if (s.indexOf("<star ") >= 0) {
					xml_star = new XmlStar();
					xml_star.setName(getAttribute(s));
				} else if (s.indexOf("</star>") >= 0) {
					data.addStar(xml_star);
				} else if (s.indexOf("<record ") >= 0) {
					xml_record = new XmlRecord();
					xml_record.setClassValue(getAttribute(s));
				} else if (s.indexOf("</record>") >= 0) {
					Star star = xml_record.createStar();
					xml_star.addStar(star);
				} else if (s.indexOf("<name>") >= 0) {
					xml_record.setName(getContent(s));
				} else if (s.indexOf("<coor>") >= 0) {
					coor.setRa(getContent(buffered_in.readLine()));
					coor.setDecl(getContent(buffered_in.readLine()));
					xml_record.setCoor(coor);
				} else if (s.indexOf("<position>") >= 0) {
					position.setX(Float.parseFloat(getContent(buffered_in.readLine())));
					position.setY(Float.parseFloat(getContent(buffered_in.readLine())));
					xml_record.setPosition(position);
				} else if (s.indexOf("<item ") >= 0) {
					XmlItem item = new XmlItem();
					item.setKey(getAttribute(s));
					item.setContent(getContent(s));
					xml_record.addItem(item);
				}
			}

			setData(data);

			buffered_in.close();

			Size size = new Size(info.getSize().getWidth(), info.getSize().getHeight());
			data.createStarMap(size);

			return;
		} catch ( ClassNotFoundException exception ) {
			System.err.println(exception);
		} catch ( IllegalAccessException exception ) {
			System.err.println(exception);
		} catch ( InstantiationException exception ) {
			System.err.println(exception);
		}

		throw new IOException();
	}

	/**
	 * Reads this XML document from the specified file. When the 
	 * filename ends with ".gz" suffix, it opens the file through
	 * <code>GZIPInputStream</code>.
	 * @param file the file.
	 * @exception IOException if I/O error occurs.
	 */
	public void read ( File file )
		throws IOException
	{
		super.read(file);

		getInformation().setPath(file.getPath());
	}

	/**
	 * Gets the content string in one line of the XML document. This 
	 * is for the fast reader, but not perfect.
	 * @param s the one line string.
	 * @return the content.
	 */
	private static String getContent ( String s ) {
		int p = s.indexOf(">");
		int q = s.lastIndexOf("<");
		s = s.substring(p+1, q);

		return translateFrom(s);
	}

	/**
	 * Gets the attribute string in one line of the XML document. This 
	 * is for the fast reader, but not perfect.
	 * @param s the one line string.
	 * @return the attiribute.
	 */
	private static String getAttribute ( String s ) {
		int p = s.indexOf("\"");
		int q = s.indexOf("\"", p+1);
		return translateFrom(s.substring(p+1, q));
	}

	/**
	 * Reads only the information element in the XML document from the
	 * specified file. When the filename ends with ".gz" suffix, it
	 * opens the file through <code>GZIPInputStream</code>.
	 * @param file the XML document file.
	 * @return the information element.
	 * @exception IOException if I/O error occurs.
	 * @exception FileNotFoundException if a file does not exists.
	 */
	public static XmlInformation readInformation ( File file )
		throws IOException, FileNotFoundException
	{
		URLSet url_set = new URLSet();
		url_set.addURL(file.toURL());

		Reader in = Decoder.newReader(url_set.exists());

		XmlInformation info = readInformation(in);

		in.close();

		info.setPath(file.getPath());

		return info;
	}

	/**
	 * Reads only the information element in the XML document from the
	 * specified reader.
	 * @param in the reader.
	 * @return the information element.
	 * @exception IOException if I/O error occurs.
	 */
	public static XmlInformation readInformation ( Reader in )
		throws IOException
	{
		char[] buffer = new char[1024];

		String info_string = "";

		boolean loop = true;
		while (loop) {
			int bytes = in.read(buffer);

			if (bytes >= 0) {
				info_string += String.valueOf(buffer, 0, bytes);
				if (info_string.indexOf("</information>") >= 0)
					loop = false;
			} else {
				throw new IOException();
			}
		}

		int ptr_start = info_string.indexOf("<information>");
		int ptr_end = info_string.indexOf("</information>");

		info_string = info_string.substring(ptr_start, ptr_end + 14);

		StringReader reader = new StringReader(info_string);
		XmlInformation info = new XmlInformation();
		info.read(reader);
		reader.close();

		return info;
	}

	/**
	 * Reads the XML report document with only star data around the
	 * specified R.A. and Decl. 
	 * @param in     the reader.
	 * @param coor   the R.A. and Decl.
	 * @param radius the radius in degree.
	 * @return the Xml report document.
	 * @exception IOException if I/O error occurs.
	 */
	public static XmlReport read ( BufferedReader in, Coor coor, double radius )
		throws IOException
	{
		String report_string = "";
		String star_string = "";
		boolean data_flag = false;
		boolean coor_flag = false;
		boolean accept_flag = false;
		String coor_string = "";

		String s = in.readLine();
		while (s != null) {
			if (s.indexOf("<data>") >= 0) {
				data_flag = true;

				report_string += s + "\n";
			} else if (s.indexOf("</data>") >= 0) {
				data_flag = false;
			}

			if (data_flag == false) {
				report_string += s + "\n";
			} else {
				if (s.indexOf("<star ") >= 0) {
					star_string = "";
					coor_flag = false;
					accept_flag = true;
				} else if (coor_flag == false) {
					if (s.indexOf("<ra>") >= 0) {
						int p_start = s.indexOf("<ra>") + 4;
						int p_end = s.indexOf("</ra>");

						coor_string = s.substring(p_start, p_end);
					} else if (s.indexOf("<decl>") >= 0) {
						int p_start = s.indexOf("<decl>") + 6;
						int p_end = s.indexOf("</decl>");

						coor_flag = true;

						coor_string += " " + s.substring(p_start, p_end);
						Coor cr = Coor.create(coor_string);

						if (cr.getAngularDistanceTo(coor) > 10.0 / 3600.0)
							accept_flag = false;
					}
				}

				if (accept_flag) {
					star_string += s + "\n";

					if (s.indexOf("</star>") >= 0)
						report_string += star_string;
				}
			}

			s = in.readLine();
		}

		StringReader reader = new StringReader(report_string);
		XmlReport report = new XmlReport();
		report.read(reader);
		reader.close();

		return report;
	}

	/**
	 * Reads the star element in the XML report document with the
	 * specified name.
	 * @param in   the reader.
	 * @param name the name.
	 * @return the star element.
	 * @exception IOException if I/O error occurs.
	 */
	public static XmlStar readStar ( BufferedReader in, String name )
		throws IOException
	{
		String string = "";
		boolean accept_flag = false;

		String s = in.readLine();
		while (s != null) {
			if (s.indexOf("<star name=") >= 0) {
				string = "";
				accept_flag = false;

				int p_start = s.indexOf("<star name=") + 12;
				int p_end = s.indexOf("\">");

				String name2 = s.substring(p_start, p_end);
				if (name2.equals(name))
					accept_flag = true;
			}

			if (accept_flag) {
				string += s + "\n";

				if (s.indexOf("</star>") >= 0) {
					StringReader reader = new StringReader(string);
					XmlStar star = new XmlStar();
					star.read(reader);
					reader.close();

					star.masticateXml();
					star.deleteXml();

					return star;
				}
			}

			s = in.readLine();
		}

		return null;
	}

	/**
	 * Writes this XML document to the specified writer.
	 * @param out the writer.
	 * @exception IOException if I/O error occurs.
	 */
	public void write ( Writer out )
		throws IOException
	{
		countStars();

		/*
		Document document = new XmlDocument();

		((XmlData)getData()).composeXml();

		makeElement(document);
		((XmlDocument)document).write(out);

		((XmlData)getData()).deleteXml();
		*/

		// Writes to the string with no data.
		XmlData data = (XmlData)getData();
		setData(new XmlData());

		StringWriter writer = new StringWriter();

		Class clazz = null;
		Document document = null;

		// J2SE 1.4
		try {
			clazz = Class.forName("org.apache.crimson.tree.XmlDocument");
			document = (Document)clazz.newInstance();
		} catch ( ClassNotFoundException exception ) {
		} catch ( IllegalAccessException exception ) {
		} catch ( InstantiationException exception ) {
		} catch ( ClassCastException exception ) {
		}
		if (document == null) {
			// JAXP 1.0.1
			try {
				clazz = Class.forName("com.sun.xml.tree.XmlDocument");
				document = (Document)clazz.newInstance();
			} catch ( ClassNotFoundException exception ) {
				System.err.println(exception);
				throw new IOException();
			} catch ( IllegalAccessException exception ) {
				System.err.println(exception);
				throw new IOException();
			} catch ( InstantiationException exception ) {
				System.err.println(exception);
				throw new IOException();
			} catch ( ClassCastException exception ) {
				System.err.println(exception);
				throw new IOException();
			}
		}

		makeElement(document);

		try {
			Class[] parameterTypes = new Class[1];
			parameterTypes[0] = Class.forName("java.io.Writer");

			Method method = clazz.getMethod("write", parameterTypes);

			Object[] args = new Object[1];
			args[0] = writer;

			method.invoke(document, args);
		} catch ( ClassNotFoundException exception ) {
			System.err.println(exception);
			throw new IOException();
		} catch ( NoSuchMethodException exception ) {
			System.err.println(exception);
			throw new IOException();
		} catch ( IllegalAccessException exception ) {
			System.err.println(exception);
			throw new IOException();
		} catch ( IllegalArgumentException exception ) {
			System.err.println(exception);
			throw new IOException();
		} catch ( InvocationTargetException exception ) {
			System.err.println(exception);
			throw new IOException();
		}

		writer.close();

		// Restores the original data.
		setData(data);

		// Writes the <system> and <information> tags.
		BufferedReader reader = new BufferedReader(new StringReader(writer.toString()));
		String s = reader.readLine();
		while (s != null) {
			if (s.indexOf("<data") >= 0)
				break;

			out.write(s + System.getProperty("line.separator"));

			s = reader.readLine();
		}
		reader.close();

		out.write("  <data>" + System.getProperty("line.separator"));

		// Writes the stars.
		XmlStar[] stars = (XmlStar[])data.getStar();
		for (int i = 0 ; i < stars.length ; i++)
			stars[i].write(out);

		out.write("  </data>" + System.getProperty("line.separator"));
		out.write("</report>" + System.getProperty("line.separator"));
		out.write(System.getProperty("line.separator"));
	}

	/**
	 * Writes this XML document to the specified file. When the 
	 * filename ends with ".gz" suffix, it opens the file through
	 * <code>GZIPOutputStream</code>.
	 * @param out the writer.
	 * @exception IOException if I/O error occurs.
	 */
	public void write ( File file )
		throws IOException
	{
		getInformation().setPath(file.getPath());

		super.write(file);
	}

	/**
	 * Counts the number of stars.
	 */
	public void countStars ( ) {
		XmlData data = (XmlData)getData();
		XmlStar[] stars = (XmlStar[])data.getStar();

		int STR_count = 0;
		int VAR_count = 0;
		int MOV_count = 0;
		int NEW_count = 0;
		int ERR_count = 0;
		int NEG_count = 0;
		for (int i = 0 ; i < stars.length ; i++) {
			String code = stars[i].getName().substring(0,3);
			if (code.equals("STR"))
				STR_count++;
			else if (code.equals("VAR"))
				VAR_count++;
			else if (code.equals("MOV"))
				MOV_count++;
			else if (code.equals("NEW"))
				NEW_count++;
			else if (code.equals("ERR"))
				ERR_count++;
			else if (code.equals("NEG"))
				NEG_count++;
		}

		XmlStarCount sc = (XmlStarCount)getInformation().getStarCount();
		if (sc == null) {
			sc = new XmlStarCount();
			getInformation().setStarCount(sc);
		}

		sc.setStr(STR_count);
		sc.setVar(VAR_count);
		sc.setMov(MOV_count);
		sc.setNew(NEW_count);
		sc.setErr(ERR_count);
		sc.setNeg(NEG_count);
	}
}
