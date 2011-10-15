/*
 * @(#)HtmlImageGalleryOperation.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.HtmlImageGallery;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.CatalogStar;
import net.aerith.misao.gui.*;
import net.aerith.misao.gui.dialog.*;
import net.aerith.misao.image.*;
import net.aerith.misao.image.io.*;
import net.aerith.misao.image.filter.ResizeFilter;
import net.aerith.misao.xml.*;
import net.aerith.misao.io.*;
import net.aerith.misao.database.*;
import net.aerith.misao.pixy.ThumbnailImageCreater;

/**
 * The <code>HtmlImageGalleryOperation</code> represents an operation 
 * to create HTML image gallery.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2005 May 22
 */

public class HtmlImageGalleryOperation extends MultiTaskOperation {
	/**
	 * The dialog to set the image size, scale, and rotation.
	 */
	protected ImageGallerySettingDialog dialog;

	/**
	 * The directory to create HTML image gallery files.
	 */
	protected File directory;

	/**
	 * The thumbnail image creater.
	 */
	protected ThumbnailImageCreater creater;

	/**
	 * The HTML image gallery.
	 */
	protected HtmlImageGallery gallery;

	/**
	 * The list of image files failed to read.
	 */
	protected Hashtable hash_failed_images = new Hashtable();

	/**
	 * The current variability.
	 */
	protected Variability current_variability = null;

	/**
	 * The current list of gallery elements.
	 */
	protected Vector current_element_list = new Vector();

	/**
	 * The current list of messages.
	 */
	protected Vector current_message_list = new Vector();

	/**
	 * The current file number.
	 */
	protected int current_index = 0;

	/**
	 * The file manager.
	 */
	protected FileManager file_manager;

	/**
	 * The database manager.
	 */
	protected GlobalDBManager db_manager = null;

	/**
	 * True when to create FITS thumbnail image.
	 */
	protected boolean create_fits = false;

	/**
	 * The mode to add past images from the database.
	 */
	protected int past_mode = HtmlImageGallerySettingDialog.PAST_MODE_NONE;

	/**
	 * True when to add a DSS image.
	 */
	protected boolean dss_image = false;

	/**
	 * True when to show the warning message.
	 */
	protected boolean warning_message_flag = true;

	/**
	 * Constructs a <code>HtmlImageGalleryOperation</code>.
	 * @param conductor    the conductor of multi task operation.
	 * @param file_manager the file manager.
	 * @param db_manager   the database manager.
	 * @param fits         true when to create FITS thumbnail images.
	 * @param past_mode    the mode to add past images from the 
	 * database.
	 * @param dss          true when to add a DSS image.
	 */
	public HtmlImageGalleryOperation ( MultiTaskConductor conductor, FileManager file_manager, GlobalDBManager db_manager, boolean fits, int past_mode, boolean dss ) {
		this.conductor = conductor;

		this.file_manager = file_manager;
		this.db_manager = db_manager;

		create_fits = fits;
		this.past_mode = past_mode;
		dss_image = dss;
	}

	/**
	 * Enables/disables to show the warning message.
	 * @param flag true when to show the message.
	 */
	public void setWarningMessageEnabled ( boolean flag ) {
		warning_message_flag = flag;
	}

	/**
	 * Shows the dialog to set parameters.
	 * @return 0 if <tt>OK</tt> button is pushed, or 2 if <tt>Cancel</tt>
	 * button is pushed.
	 */
	public int showSettingDialog ( ) {
		dialog = new ImageGallerySettingDialog();
		int answer = dialog.show(conductor.getPane());

		if (answer == 0) {
			CommonFileChooser file_chooser = new CommonFileChooser();
			file_chooser.setDialogTitle("Choose a directory.");
			file_chooser.setMultiSelectionEnabled(false);
			file_chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (file_chooser.showSaveDialog(conductor.getPane()) != JFileChooser.APPROVE_OPTION)
				return 2;

			directory = file_chooser.getSelectedFile();
			if (directory.exists() == false  ||  directory.isDirectory() == false) {
				try {
					if (directory.mkdirs() == false) {
						throw new IOException();
					}
				} catch ( IOException exception ) {
					String message = "Failed to create the directory.";
					JOptionPane.showMessageDialog(conductor.getPane(), message, "Error", JOptionPane.ERROR_MESSAGE);
					return 2;
				}
			}
		}

		return answer;
	}

	/**
	 * Gets the image size.
	 * @return the image size.
	 */
	protected Size getImageSize ( ) {
		return dialog.getSize();
	}

	/**
	 * Returns true when to unify the resolution.
	 * @return true when to unify the resolution.
	 */
	protected boolean unifiesResolution ( ) {
		return dialog.unifiesResolution();
	}

	/**
	 * Gets the resolution in arcsec/pixel.
	 * @return the resolution in arcsec/pixel.
	 */
	protected double getResolution ( ) {
		return dialog.getResolution();
	}

	/**
	 * Returns true when to unify the magnification.
	 * @return true when to unify the magnification.
	 */
	protected boolean unifiesMagnification ( ) {
		return dialog.unifiesMagnification();
	}

	/**
	 * Gets the magnification.
	 * @return the magnification.
	 */
	protected double getMagnification ( ) {
		return dialog.getMagnification();
	}

	/**
	 * Returns true when to rotate north up at right angles.
	 * @return true when to rotate north up at right angles.
	 */
	protected boolean rotatesNorthUpAtRightAngles ( ) {
		return dialog.rotatesNorthUpAtRightAngles();
	}

	/**
	 * Notifies when the operation starts.
	 */
	protected void notifyStart ( ) {
		creater = new ThumbnailImageCreater(file_manager);
		if (db_manager != null)
			creater.setDBManager(db_manager.getInformationDBManager());

		creater.setSize(getImageSize());
		creater.setPositionPolicy(ThumbnailImageCreater.POSITION_TARGET_AT_CENTER);

		if (unifiesResolution()) {
			double resolution = getResolution();
			creater.setMagnificationPolicy(ThumbnailImageCreater.MAGNIFICATION_SPECIFIED_RESOLUTION);
			creater.setResolution(resolution);
		} else if (unifiesMagnification()) {
			double magnification = getMagnification();
			creater.setMagnificationPolicy(ThumbnailImageCreater.MAGNIFICATION_SPECIFIED_MAGNIFICATION);
			creater.setMagnification(magnification);
		} else {
			creater.setMagnificationPolicy(ThumbnailImageCreater.MAGNIFICATION_KEEP_ORIGINAL);
		}

		if (rotatesNorthUpAtRightAngles()) {
			creater.setRotationPolicy(ThumbnailImageCreater.ROTATION_NORTH_UP_AT_RIGHT_ANGLES);
		} else {
			creater.setRotationPolicy(ThumbnailImageCreater.ROTATION_KEEP_ORIGINAL);
		}

		gallery = new HtmlImageGallery(directory);
		gallery.setInsertNextAnchor(true);

		try {
			gallery.open();
		} catch ( IOException exception ) {
		}

		hash_failed_images = new Hashtable();

		current_variability = null;
		current_element_list = new Vector();
		current_message_list = new Vector();
		current_index = 0;

		super.notifyStart();
	}

	/**
	 * Notifies when the operation ends.
	 * @param exception the exception if an error occurs, or null if
	 * succeeded.
	 */
	protected void notifyEnd ( Exception exception ) {
		try {
			addCurrentElement();

			gallery.close();
		} catch ( Exception exception2 ) {
			if (exception == null)
				exception = exception2;
		}

		// Images failed to read.
		if (hash_failed_images.size() > 0) {
			Vector l = new Vector();
			Enumeration keys = hash_failed_images.keys();
			while (keys.hasMoreElements()) 
				l.addElement(keys.nextElement());

			if (warning_message_flag) {
				String header = "Failed to read the following images:";
				MessagesDialog dialog = new MessagesDialog(header, l);
				dialog.show(conductor.getPane(), "Warning", JOptionPane.WARNING_MESSAGE);
			}
		}

		super.notifyEnd(exception);
	}

	/**
	 * Adds the current element to the gallery.
	 * @exception IOException if I/O error occurs.
	 * @exception FileNotFoundException if the file does not exist.
	 * @exception UnsupportedBufferTypeException if the data type is 
	 * unsupported.
	 * @exception UnsupportedFileTypeException if the file type is 
	 * unsupported.
	 */
	protected void addCurrentElement ( )
		throws IOException, FileNotFoundException, UnsupportedBufferTypeException, UnsupportedFileTypeException
	{
		if (current_variability != null) {
			// Adds a past image from database.

			if (past_mode != HtmlImageGallerySettingDialog.PAST_MODE_NONE) {
				try {
					// Searches images from the database.
					Vector record_list = new Vector();
					Vector info_list = new Vector();

					CatalogStar star = current_variability.getStar();

					XmlReportQueryCondition query_condition = new XmlReportQueryCondition();

					InformationDBAccessor accessor = db_manager.getInformationDBManager().getAccessor(star.getCoor(), query_condition.getBrighterLimit(), query_condition.getFainterLimit());

					XmlInformation info = accessor.getFirstElement();
					while (info != null) {
						if (query_condition.accept(info)) {
							Position position = info.mapCoordinatesToXY(star.getCoor());
							star.setPosition(position);

							if (0 <= position.getX()  &&  position.getX() <= info.getSize().getWidth()  &&
								0 <= position.getY()  &&  position.getY() <= info.getSize().getHeight()) {
								XmlStar xml_star = new XmlStar();
								xml_star.setName("NEG", 0);
								xml_star.addStar(star);

								XmlMagRecord mag_record = new XmlMagRecord(info, xml_star);
								record_list.add(mag_record);
								info_list.add(info);
							}
						}
						info = accessor.getNextElement();
					}

					// Sorts in order of date.
					Array array = new Array(record_list.size());
					for (int i = 0 ; i < record_list.size() ; i++) {
						XmlMagRecord mag_record = (XmlMagRecord)record_list.elementAt(i);
						JulianDay jd = JulianDay.create(mag_record.getDate());
						array.set(i, jd.getJD());
					}
					ArrayIndex index = array.sortAscendant();

					// Adds to the gallery.
					int selected_index = -1;
					double pixels_from_edge = 0.0;
					for (int i = 0 ; i < record_list.size() ; i++) {
						XmlMagRecord mag_record = (XmlMagRecord)record_list.elementAt(index.get(i));
						info = (XmlInformation)info_list.elementAt(index.get(i));

						if (pixels_from_edge < mag_record.getPixelsFromEdge().doubleValue()) {
							selected_index = index.get(i);
							pixels_from_edge = mag_record.getPixelsFromEdge().doubleValue();
						}

						if (past_mode == HtmlImageGallerySettingDialog.PAST_MODE_ALL) {
							current_index++;

							ImageGalleryElement element = createImageGalleryElement(mag_record, info);

							current_element_list.addElement(element);
							current_message_list.addElement(mag_record.getOutputString());
						}
					}

					if (past_mode == HtmlImageGallerySettingDialog.PAST_MODE_CENTER) {
						if (selected_index >= 0) {
							XmlMagRecord mag_record = (XmlMagRecord)record_list.elementAt(selected_index);
							info = (XmlInformation)info_list.elementAt(selected_index);

							current_index++;

							ImageGalleryElement element = createImageGalleryElement(mag_record, info);

							current_element_list.addElement(element);
							current_message_list.addElement(mag_record.getOutputString());
						}
					}
				} catch ( Exception exception ) {
					// Ignores.
				}
			}

			// Adds a DSS image.

			if (dss_image) {
				try {
					CatalogStar star = current_variability.getStar();

					String coor_str = star.getCoor().getOutputStringTo100mArcsecWithoutUnit();
					String ra = coor_str.substring(0, 11).replace(' ', '+');
					String decl = coor_str.substring(13).replace(' ', '+');
					if (coor_str.charAt(12) == '+')
						decl = "%2B" + decl;
					else
						decl = "-" + decl;

					File file_dss_gif = new File("__pixy2_dss_tmp.gif");

					URL url = new URL("http://stdatu.stsci.edu/cgi-bin/dss_search?v=1&r=" + ra + "&d=" + decl + "&e=J2000&h=15.0&w=15.0&f=gif&c=gz&fov=NONE&v3=");
					URLConnection uc = url.openConnection();
					DataInputStream dis = new DataInputStream(uc.getInputStream());
					DataOutputStream dos = new DataOutputStream(new FileOutputStream(file_dss_gif));

					byte[] b = new byte[1024];
					int bytes = dis.read(b);
					while (bytes >= 0) {
						if (bytes > 0)
							dos.write(b, 0, bytes);
						bytes = dis.read(b);
					}
					dos.flush();
					dis.close();
					dos.close();

					Gif gif = new Gif(file_dss_gif.toURL());
					MonoImage image = gif.read();

					file_dss_gif.delete();

					ResizeFilter filter = new ResizeFilter(getImageSize());
					filter.setBasePosition((530 - getImageSize().getWidth()) / 2, (530 - getImageSize().getHeight()) / 2);
					image = filter.operate(image);

					current_index++;

					File file = new File(String.valueOf(current_index) + ".png");
					net.aerith.misao.image.io.Format format = new Png(file.toURL());
					ImageGalleryElement element = new ImageGalleryElement(image, file.toURL(), format);

					String fov_width = net.aerith.misao.util.Format.formatDouble(15.0 / 530.0 * (double)getImageSize().getWidth(), 7, 5).trim();
					String fov_height = net.aerith.misao.util.Format.formatDouble(15.0 / 530.0 * (double)getImageSize().getHeight(), 7, 5).trim();

					Vector data = new Vector();
					data.addElement("DSS image");
					data.addElement(fov_width + " x " + fov_height + " arcmin");
					element.setData(data);

					current_element_list.addElement(element);
				} catch ( Exception exception ) {
					// Ignores.
				}
			}

			// Outputs the data.

			CatalogStar star = current_variability.getStar();
			ImageGalleryElement element = new ImageGalleryElement(star.getStarFolder(), current_element_list);
			element.setTitle(star.getName());

			Vector data = new Vector();

			String[] dss_anchors = createDssAnchors(star.getCoor());
			data.addElement(dss_anchors[0]);
//			data.addElement(dss_anchors[1]);
			data.addElement(create2massAnchor(star.getCoor()));
			data.addElement(createSimbadAnchor(star.getCoor()));
			data.addElement(createAsasAnchor(star.getCoor()));
			data.addElement("");

			data.addElement(star.getOutputString());
			if (current_variability.getIdentifiedStar() != null) {
				data.addElement("");
				data.addElement(current_variability.getIdentifiedStar().getOutputString());
			}
			data.addElement("");

			for (int i = 0 ; i < current_message_list.size() ; i++)
				data.addElement(current_message_list.elementAt(i));

			element.setData(data);

			gallery.addElement(element);
		}
	}

	/**
	 * Creates the gallery element of one image.
	 * @param mag_record the magnitude record.
	 * @param info       the image information.
	 * @return the image gallery element.
	 * @exception IOException if I/O error occurs.
	 * @exception FileNotFoundException if the file does not exist.
	 * @exception UnsupportedBufferTypeException if the data type is 
	 * unsupported.
	 * @exception UnsupportedFileTypeException if the file type is 
	 * unsupported.
	 */
	private ImageGalleryElement createImageGalleryElement ( XmlMagRecord mag_record, XmlInformation info )
		throws IOException, FileNotFoundException, UnsupportedBufferTypeException, UnsupportedFileTypeException
	{
		XmlMagRecord[] mag_records = new XmlMagRecord[1];
		mag_records[0] = mag_record;

		MonoImage[] images = creater.create(mag_records);

		File file = new File(String.valueOf(current_index) + ".png");
		net.aerith.misao.image.io.Format format = new Png(file.toURL());
		ImageGalleryElement element = new ImageGalleryElement(images[0], file.toURL(), format);

		// Creates the thumbnail FITS image, too.
		if (create_fits) {
			// Note that the image must be with original resolution.
			creater.setMagnificationPolicy(ThumbnailImageCreater.MAGNIFICATION_KEEP_ORIGINAL);
			mag_records[0] = mag_record;
			images = creater.create(mag_records);

			file = new File(String.valueOf(current_index) + ".fts");
			format = new Fits(file.toURL());

			HtmlImageAnchor anchor = new HtmlImageAnchor(images[0], file.toURL(), format, "FITS image");
			element.addImageAnchor(anchor);

			// Restores the resulution/magnification.
			if (unifiesResolution())
				creater.setMagnificationPolicy(ThumbnailImageCreater.MAGNIFICATION_SPECIFIED_RESOLUTION);
			else if (unifiesMagnification())
				creater.setMagnificationPolicy(ThumbnailImageCreater.MAGNIFICATION_SPECIFIED_MAGNIFICATION);
		}

		// Calculates the field of view.
		double pixel_size = 1.0;
		if (unifiesResolution()) {
			pixel_size = getResolution() / 60.0;
		} else {
			ChartMapFunction cmf = info.getChartMapFunction();
			pixel_size = 60.0 / cmf.getScaleUnitPerDegree();

			if (unifiesMagnification())
				pixel_size /= getMagnification();
		}
		String fov_width = net.aerith.misao.util.Format.formatDouble((double)getImageSize().getWidth() * pixel_size, 7, 5).trim();
		String fov_height = net.aerith.misao.util.Format.formatDouble((double)getImageSize().getHeight() * pixel_size, 7, 5).trim();

		Vector data = new Vector();
		data.addElement(mag_record.getDate());
		data.addElement(((XmlMag)mag_record.getMag()).getOutputString());
		data.addElement(fov_width + " x " + fov_height + " arcmin");
		element.setData(data);

		return element;
	}

	/**
	 * Creates the anchors to get the DSS GIF and FITS images.
	 * @param coor the R.A. and Decl.
	 * @return the anchors to get the DSS GIF and FITS images.
	 */
	private static String[] createDssAnchors ( Coor coor ) {
		String coor_str = coor.getOutputStringTo100mArcsecWithoutUnit();
		String ra = coor_str.substring(0, 11).replace(' ', '+');
		String decl = coor_str.substring(13).replace(' ', '+');
		if (coor_str.charAt(12) == '+')
			decl = "%2B" + decl;
		else
			decl = "-" + decl;

		String[] dss_anchors = new String[2];
		dss_anchors[0] = "<A HREF=\"http://stdatu.stsci.edu/cgi-bin/dss_search?v=1&r=" + ra + "&d=" + decl + "&e=J2000&h=15.0&w=15.0&f=gif&c=gz&fov=NONE&v3=\" TARGET=\"new\">Digitized Sky Survey GIF image (15 x 15 arcmin)</A>";
		dss_anchors[1] = "<A HREF=\"http://stdatu.stsci.edu/cgi-bin/dss_search?v=1&r=" + ra + "&d=" + decl + "&e=J2000&h=15.0&w=15.0&f=fits&c=gz&fov=NONE&v3=\">Digitized Sky Survey FITS image (15 x 15 arcmin)</A>";

		return dss_anchors;
	}

	/**
	 * Creates the anchor to get the 2MASS images.
	 * @param coor the R.A. and Decl.
	 * @return the anchor to get the 2MASS images.
	 */
	private static String create2massAnchor ( Coor coor ) {
		String ra = String.valueOf(coor.getRA());
		String decl = "";
		if (coor.getDecl() < 0.0)
			decl = String.valueOf(coor.getDecl());
		else
			decl = "%2B" + String.valueOf(coor.getDecl());

		String anchor = "<A HREF=\"http://irsa.ipac.caltech.edu/cgi-bin/2MASS/Visualizer/nph-surveyvis?locstr=" + ra + "+" + decl + "+eq&mapsize=2.0&show=coadd&date=&hemisphere=&scan=&coadd=&band=All&coaddsize=300&submit=Submit\" TARGET=\"new\">2MASS Quicklook Image (5 x 5 arcmin)</A>";

		return anchor;
	}

	/**
	 * Creates the anchor to search data in SIMBAD.
	 * @param coor the R.A. and Decl.
	 * @return the anchor to search data in SIMBAD.
	 */
	private static String createSimbadAnchor ( Coor coor ) {
		String coor_str = coor.getOutputStringTo100mArcsecWithoutUnit();
		String ra = coor_str.substring(0, 11).replace(' ', '+');
		String decl = coor_str.substring(13).replace(' ', '+');
		if (coor_str.charAt(12) == '+')
			decl = "%2B" + decl;
		else
			decl = "-" + decl;

		String anchor = "<A HREF=\"http://simbad.u-strasbg.fr/sim-id.pl?protocol=html&Ident=" + ra + "+" + decl + "&NbIdent=1&Radius=3&Radius.unit=arcmin&CooFrame=FK5&CooEpoch=2000&CooEqui=2000&output.max=all&o.catall=on&output.mesdisp=N&Bibyear1=1983&Bibyear2=2004&Frame1=FK5&Frame2=FK4&Frame3=G&Equi1=2000.0&Equi2=1950.0&Equi3=2000.0&Epoch1=2000.0&Epoch2=1950.0&Epoch3=2000.0\" TARGET=\"new\">SIMBAD Query</A>";

		return anchor;
	}

	/**
	 * Creates the anchor to get the ASAS-3 light curve.
	 * @param coor the R.A. and Decl.
	 * @return the anchor to get the ASAS-3 light curve.
	 */
	private static String createAsasAnchor ( Coor coor ) {
		String ra_decl = coor.getOutputStringTo100mArcminHoursecWithoutSpace();
		StringTokenizer st = new StringTokenizer(ra_decl);
		String ra = st.nextToken();
		String decl = st.nextToken();

		String anchor = "<A HREF=\"http://www.astrouw.edu.pl/cgi-asas/asas_variable/" + ra + decl + ",asas3,0,0,500,320\" TARGET=\"new\">ASAS-3 Light Curve</A>";

		return anchor;
	}

	/**
	 * Operates on one item. This is invoked from the conductor of 
	 * multi task operation.
	 * @param object the target object to operate.
	 * @exception Exception if an error occurs.
	 */
	public void operate ( Object object )
		throws Exception
	{
		HtmlImageGalleryTable.ImageRecord record = (HtmlImageGalleryTable.ImageRecord)object;

		try {
			Variability variability = record.getVariability();

			if (variability != current_variability) {
				if (current_variability != null)
					addCurrentElement();

				current_variability = variability;
				current_element_list = new Vector();
				current_message_list = new Vector();
				current_index = 0;
			} else {
				current_index++;
			}

			ImageGalleryElement element = createImageGalleryElement(record.getMagRecord(), record.getInformation());

			current_element_list.addElement(element);
			current_message_list.addElement(record.getMagRecord().getOutputString());
		} catch ( Exception exception ) {
			String image_file = record.getInformation().getImage().getContent();
			hash_failed_images.put(image_file, image_file);

			throw exception;
		}
	}
}
