/*
 * @(#)IONode.java
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
import net.aerith.misao.io.Decoder;
import net.aerith.misao.io.Encoder;

/**
 * The <code>IONode</code> is an abstract class of a node in the XML
 * document with I/O functions.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2005 January 2
 */

public abstract class IONode {
	/**
	 * Reads this XML document from the specified reader.
	 * @param in the reader.
	 * @exception IOException if I/O error occurs.
	 */
	public void read ( Reader in )
		throws IOException
	{
		try {
			DocumentBuilderFactory builder_factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = builder_factory.newDocumentBuilder();
			Document document = builder.parse(new InputSource(in));
			setup(document);
		} catch ( SAXException exception ) {
			System.err.println(exception);
			throw new IOException();
		} catch ( ParserConfigurationException exception ) {
			System.err.println(exception);
			throw new IOException();
		}
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
		Reader in = Decoder.newReader(file);
		read(in);
		in.close();
	}

	/**
	 * Writes this XML document to the specified writer.
	 * @param out the writer.
	 * @exception IOException if I/O error occurs.
	 */
	public void write ( Writer out )
		throws IOException
	{
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
			args[0] = out;

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
		Writer out = Encoder.newWriter(file);
		write(out);
		out.close();
	}

	/**
	 * Initializes this by the Document <code>doc</code>.
	 * @param doc the document.
	 */
	public abstract void setup ( Document doc );

	/**
	 * Creates a DOM representation of the object.
	 * Result is appended to the Node <code>parent</code>.
	 * @param parent the parent node.
	 */
	public abstract void makeElement ( Node parent );

	/**
	 * Translates some special characters from XML style. This is for
	 * the fast reader, but not perfect.
	 * @param string the string.
	 * @return the translated string.
	 */
	protected static String translateFrom ( String string ) {
		while (string.indexOf("&amp;") >= 0) {
			int p = string.indexOf("&amp;");
			string = string.substring(0, p) + "&" + string.substring(p+5);
		}
		while (string.indexOf("&gt;") >= 0) {
			int p = string.indexOf("&gt;");
			string = string.substring(0, p) + ">" + string.substring(p+4);
		}
		while (string.indexOf("&lt;") >= 0) {
			int p = string.indexOf("&lt;");
			string = string.substring(0, p) + "<" + string.substring(p+4);
		}

		return string;
	}

	/**
	 * Translates some special characters into XML style. This is for
	 * the fast writer, but not perfect.
	 * @param string the string.
	 * @return the translated string.
	 */
	protected static String translateTo ( String string ) {
		char[] src = string.toCharArray();
		int dst_length = src.length;
		for (int i = 0 ; i < src.length ; i++) {
			if (src[i] == '&') {
				dst_length += 4;
			} else if (src[i] == '<'  ||  src[i] == '>') {
				dst_length += 3;
			}
		}

		char[] dst = new char[dst_length];
		int j = 0;
		for (int i = 0 ; i < src.length ; i++) {
			if (src[i] == '&') {
				dst[j++] = '&';
				dst[j++] = 'a';
				dst[j++] = 'm';
				dst[j++] = 'p';
				dst[j++] = ';';
			} else if (src[i] == '<') {
				dst[j++] = '&';
				dst[j++] = 'l';
				dst[j++] = 't';
				dst[j++] = ';';
			} else if (src[i] == '>') {
				dst[j++] = '&';
				dst[j++] = 'g';
				dst[j++] = 't';
				dst[j++] = ';';
			} else {
				dst[j++] = src[i];
			}
		}

		return String.valueOf(dst);
	}
}
