/*
 * @(#)ThumbnailImageCreater.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.pixy;
import java.io.*;
import java.util.*;
import java.awt.Point;
import net.aerith.misao.util.*;
import net.aerith.misao.image.*;
import net.aerith.misao.image.io.*;
import net.aerith.misao.image.filter.*;
import net.aerith.misao.database.*;
import net.aerith.misao.xml.*;
import net.aerith.misao.io.FileManager;
import net.aerith.misao.pixy.image_loading.XmlImageLoader;

/**
 * The <code>ThumbnailImageCreater</code> is a class to create the
 * thumbnail image objects or image files based on the specified list
 * of XML magnitude records.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2007 May 20
 */

public class ThumbnailImageCreater extends OperationObservable {
	/**
	 * The list of the folder and the magnitude record.
	 */
	protected Vector thumbnail_list = new Vector();

	/**
	 * The gallery type.
	 */
	protected int gallery_type = TYPE_IMAGE;

	/**
	 * The size of the thumbnail image.
	 */
	protected Size thumbnail_size = new Size(100, 100);

	/**
	 * The policy on the position.
	 */
	protected int policy_position = POSITION_TARGET_AT_CENTER;

	/**
	 * The policy on the magnification.
	 */
	protected int policy_magnification = MAGNIFICATION_KEEP_ORIGINAL;

	/**
	 * The policy on the rotation.
	 */
	protected int policy_rotation = ROTATION_KEEP_ORIGINAL;

	/**
	 * The resolution, the pixel size in arcsec.
	 */
	protected double resolution = 0.0;

	/**
	 * The magnification.
	 */
	protected double magnification = 1.0;

	/**
	 * The information database manager.
	 */
	protected InformationDBManager info_manager;

	/**
	 * The file manager.
	 */
	protected FileManager file_manager;

	/**
	 * The number of policy on the position which indicates to create
	 * the thumbnail image as the target star locates at the center, 
	 * even if it is at the edge of the original image.
	 */
	public final static int POSITION_TARGET_AT_CENTER = 101;

	/**
	 * The number of policy on the magnification which indicates to
	 * create the thumbnail image without magnification.
	 */
	public final static int MAGNIFICATION_KEEP_ORIGINAL = 201;

	/**
	 * The number of policy on the magnification which indicates to
	 * create the thumbnail image with the specified resolution.
	 */
	public final static int MAGNIFICATION_SPECIFIED_RESOLUTION = 202;

	/**
	 * The number of policy on the magnification which indicates to
	 * create the thumbnail image with the specified magnification.
	 */
	public final static int MAGNIFICATION_SPECIFIED_MAGNIFICATION = 203;

	/**
	 * The number of policy on the rotation which indicates to
	 * create the thumbnail image without rotation.
	 */
	public final static int ROTATION_KEEP_ORIGINAL = 301;

	/**
	 * The number of policy on the rotation which indicates to
	 * create the thumbnail image as north is up by rotating at
	 * right angles.
	 */
	public final static int ROTATION_NORTH_UP_AT_RIGHT_ANGLES = 302;

	/**
	 * The number of gallery type to create an image gallery.
	 */
	public final static int TYPE_IMAGE = 401;

	/**
	 * The number of gallery type to create a differential image 
	 * gallery.
	 */
	public final static int TYPE_DIFFERENTIAL = 402;

	/**
	 * Constructs a <code>ThumbnailImageCreater</code>.
	 * @param file_manager the file manager.
	 */
	public ThumbnailImageCreater ( FileManager file_manager ) {
		thumbnail_list = new Vector();
		info_manager = null;

		this.file_manager = file_manager;
	}

	/**
	 * Sets the gallery type.
	 * @param type the number of gallery type.
	 */
	public void setGalleryType ( int type ) {
		gallery_type = type;
	}

	/**
	 * Sets the size of the thumbnail image.
	 * @param size the size of the thumbnail image.
	 */
	public void setSize ( Size size ) {
		thumbnail_size = size;
	}

	/**
	 * Sets the policy on the position.
	 * @param policy the number of policy on the position.
	 */
	public void setPositionPolicy ( int policy ) {
		policy_position = policy;
	}

	/**
	 * Sets the policy on the magnification.
	 * @param policy the number of policy on the magnification.
	 */
	public void setMagnificationPolicy ( int policy ) {
		policy_magnification = policy;
	}

	/**
	 * Sets the resolution, the pixel size in arcsec.
	 * @param pixel_size the pixel size in arcsec.
	 */
	public void setResolution ( double pixel_size ) {
		resolution = pixel_size;
	}

	/**
	 * Sets the magnification.
	 * @param magnification the magnification.
	 */
	public void setMagnification ( double magnification ) {
		this.magnification = magnification;
	}

	/**
	 * Sets the policy on the rotation.
	 * @param policy the number of policy on the rotation.
	 */
	public void setRotationPolicy ( int policy ) {
		policy_rotation = policy;
	}

	/**
	 * Adds the magnitude record. It is to create the thumbnail image
	 * objects.
	 * @param record the magnitude record.
	 */
	public void addRecord ( XmlMagRecord record ) {
		thumbnail_list.addElement(new FolderAndRecord(null, record));
	}

	/**
	 * Adds the set of the folder and magnitude record. It is to 
	 * create the thumbnail image files.
	 * @param record the magnitude record.
	 * @param folder the folder to create the thumbnail image.
	 */
	public void addRecord ( XmlMagRecord record, File folder ) {
		thumbnail_list.addElement(new FolderAndRecord(folder, record));
	}

	/**
	 * Cleans up the sets of the folder and magnitude record.
	 */
	public void clean ( ) {
		thumbnail_list = new Vector();
	}

	/**
	 * Sets the information database manager.
	 * @param info_manager the information database manager.
	 */
	public void setDBManager ( InformationDBManager info_manager ) {
		this.info_manager = info_manager;
	}

	/**
	 * Sets the file manager.
	 * @param file_manager the file manager.
	 */
	public void setFileManager ( FileManager file_manager ) {
		this.file_manager = file_manager;
	}

	/**
	 * Creates the thumbnail image objects.
	 * @param records the array of XML magnitude records.
	 * @return the array of the thumbnail image objects.
	 */
	public MonoImage[] create ( XmlMagRecord[] records ) {
		clean();

		for (int i = 0 ; i < records.length ; i++)
			addRecord(records[i]);

		MonoImage[] images = create();

		clean();

		return images;
	}

	/**
	 * Creates the thumbnail image objects of the previously added
	 * records.
	 * @return the array of the thumbnail image objects in the same 
	 * order as magnitude records added to this creater.
	 */
	public MonoImage[] create ( ) {
		notifyStart();

		Vector image_list = new Vector();

		MonoImage base_image = null;
		XmlInformation base_info = null;
		Coor base_coor = null;

		for (int i = 0 ; i < thumbnail_list.size() ; i++) {
			FolderAndRecord set = (FolderAndRecord)thumbnail_list.elementAt(i);

			try {
				XmlInformation info = file_manager.readInformation(set.record, info_manager);

				XmlImageLoader loader = new XmlImageLoader(info, file_manager);
				loader.operate();
				MonoImage image = loader.getMonoImage();

				Position position = new Position(set.record.getPosition().getX(), set.record.getPosition().getY());
				if (gallery_type == TYPE_DIFFERENTIAL  &&  i > 0)
					position = info.mapCoordinatesToXY(base_coor);

				MonoImage thumbnail_image = createThumbnailImage(image, position, info);

				if (gallery_type == TYPE_DIFFERENTIAL) {
					if (i == 0) {
						base_image = thumbnail_image;
						base_info = info;
						base_coor = info.mapXYToCoordinates(position);
					} else {
						thumbnail_image = createDifferentialImage(thumbnail_image, info, base_image, base_info);
					}
				}

				image_list.addElement(thumbnail_image);

				notifySucceeded(set.record);
			} catch ( Exception exception ) {
				notifyFailed(set.record);
			}
		}

		MonoImage[] images = new MonoImage[image_list.size()];
		for (int i = 0 ; i < image_list.size() ; i++)
			images[i] = (MonoImage)image_list.elementAt(i);

		notifyEnd(null);

		return images;
	}

	/**
	 * Creates the thumbnail image files.
	 * @param records the array of XML magnitude records.
	 * @param folder  the folder to create the files.
	 * @return the array of the thumbnail image files.
	 */
	public File[] createFile ( XmlMagRecord[] records, File folder ) {
		clean();

		for (int i = 0 ; i < records.length ; i++)
			addRecord(records[i], folder);

		File[] files = createFile();

		clean();

		return files;
	}

	/**
	 * Creates the thumbnail image files of the previously added
	 * records.
	 * @return the array of the thumbnail image files.
	 */
	public File[] createFile ( ) {
		notifyStart();

		Vector file_list = new Vector();

		MonoImage base_image = null;
		XmlInformation base_info = null;
		Coor base_coor = null;

		for (int i = 0 ; i < thumbnail_list.size() ; i++) {
			FolderAndRecord set = (FolderAndRecord)thumbnail_list.elementAt(i);

			try {
				set.folder.mkdirs();

				XmlInformation info = file_manager.readInformation(set.record, info_manager);

				XmlImageLoader loader = new XmlImageLoader(info, file_manager);
				loader.operate();
				MonoImage image = loader.getMonoImage();

				Position position = new Position(set.record.getPosition().getX(), set.record.getPosition().getY());
				if (gallery_type == TYPE_DIFFERENTIAL  &&  i > 0)
					position = info.mapCoordinatesToXY(base_coor);

				MonoImage thumbnail_image = createThumbnailImage(image, position, info);

				if (gallery_type == TYPE_DIFFERENTIAL) {
					if (i == 0) {
						base_image = thumbnail_image;
						base_info = info;
						base_coor = info.mapXYToCoordinates(position);
					} else {
						thumbnail_image = createDifferentialImage(image, info, base_image, base_info);
					}
				}

				JulianDay jd = JulianDay.create(set.record.getDate());

				String filename = jd.getOutputString(JulianDay.FORMAT_MONTH_IN_NUMBER_WITHOUT_SPACE, JulianDay.FORMAT_DECIMALDAY_100000TH) + ".fts";
				filename = FileManager.unitePath(set.folder.getPath(), filename);

				File file = new File(filename);
				Fits fits = new Fits(file.toURI().toURL());
				fits.save(thumbnail_image);

				file_list.addElement(file);

				notifySucceeded(set.record);
			} catch ( Exception exception ) {
				notifyFailed(set.record);
			}
		}

		File[] files = new File[file_list.size()];
		for (int i = 0 ; i < file_list.size() ; i++)
			files[i] = (File)file_list.elementAt(i);

		notifyEnd(null);

		return files;
	}

	/**
	 * Creates the thumbnail image.
	 * @param image           the original image object.
	 * @param target_position the (x,y) position of the target.
	 * @param info            the XML information document.
	 * @return the image object.
	 */
	protected MonoImage createThumbnailImage ( MonoImage image, Position target_position, XmlInformation info ) {
		// Magnifies the thumbnail image.
		Size size = thumbnail_size;
		if (policy_magnification == MAGNIFICATION_SPECIFIED_RESOLUTION) {
			ChartMapFunction cmf = info.getChartMapFunction();
			double pixel_size = 3600.0 / cmf.getScaleUnitPerDegree();
			double ratio = pixel_size / resolution;

			size = new Size((int)((double)thumbnail_size.getWidth() / ratio + 0.5), (int)((double)thumbnail_size.getHeight() / ratio + 0.5));
		} else if (policy_magnification == MAGNIFICATION_SPECIFIED_MAGNIFICATION) {
			size = new Size((int)((double)thumbnail_size.getWidth() / magnification + 0.5), (int)((double)thumbnail_size.getHeight() / magnification + 0.5));
		}

		int base_x = (int)(target_position.getX() - (double)size.getWidth() / 2.0 + 0.5);
		int base_y = (int)(target_position.getY() - (double)size.getHeight() / 2.0 + 0.5);

		ResizeFilter filter = new ResizeFilter(size);
		filter.setBasePosition(base_x, base_y);

		MonoImage thumbnail_image = filter.operate(image);

		// Rotates the thumbnail image.
		if (policy_rotation == ROTATION_NORTH_UP_AT_RIGHT_ANGLES)
			thumbnail_image = new RotateAtRightAnglesFilter(- info.getRotation().getContent()).operate(thumbnail_image);

		// Magnifies the thumbnail image.
		if (policy_magnification == MAGNIFICATION_SPECIFIED_RESOLUTION  ||  policy_magnification == MAGNIFICATION_SPECIFIED_MAGNIFICATION) {
			thumbnail_image = new RescaleFilter(thumbnail_size).operate(thumbnail_image);
		}

		return thumbnail_image;
	}

	/**
	 * Creates the differential image.
	 * @param image      the target image object.
	 * @param info       the XML information document of the target image.
	 * @param base_image the base image object.
	 * @param base_info  the XML information document of the base image.
	 * @return the image object.
	 */
	protected MonoImage createDifferentialImage ( MonoImage image, XmlInformation info, MonoImage base_image, XmlInformation base_info ) {
		// Restores rotation of the thumbnail image.
		if (policy_rotation == ROTATION_NORTH_UP_AT_RIGHT_ANGLES)
			image = new RotateAtRightAnglesFilter(info.getRotation().getContent()).operate(image);

		// Magnification.
		double magnification = 1.0;
		if (policy_magnification == MAGNIFICATION_KEEP_ORIGINAL  ||  policy_magnification == MAGNIFICATION_SPECIFIED_MAGNIFICATION) {
			ChartMapFunction cmf = info.getChartMapFunction();
			ChartMapFunction base_cmf = base_info.getChartMapFunction();
			magnification = base_cmf.getScaleUnitPerDegree() / cmf.getScaleUnitPerDegree();
		}

		// Rotation.
		double pa = info.getRotation().getContent();
		double base_pa = base_info.getRotation().getContent();
		double rotation = base_pa - pa;

		// Maps the image to the base image.
		MapFunction map_function = new MapFunction(new Position(0, 0), magnification, rotation);
		image = new MapFilter(map_function).operate(image);

		// Rotates the thumbnail image.
		if (policy_rotation == ROTATION_NORTH_UP_AT_RIGHT_ANGLES)
			image = new RotateAtRightAnglesFilter(- base_info.getRotation().getContent()).operate(image);

		// Subtract the base image.
		image = new SubtractionFilter(image).operate(base_image);

		return image;
	}

	/**
	 * The <code>FolderAndRecord</code> is a set of the folder and the
	 * magnitude record to create a thumbnail image.
	 */
	protected class FolderAndRecord {
		/**
		 * The folder to create the thumbnail image.
		 */
		public File folder;

		/**
		 * The magnitude record.
		 */
		public XmlMagRecord record;

		/**
		 * Constructs a <code>FolderAndRecord</code>.
		 * @param folder the folder to create the thumbnail image.
		 * @param record the magnitude record.
		 */
		public FolderAndRecord ( File folder, XmlMagRecord record ) {
			this.folder = folder;
			this.record = record;
		}
	}
}
