/*
 * @(#)ChartComponent.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.gui.dialog.*;
import net.aerith.misao.gui.event.*;
import net.aerith.misao.catalog.CatalogManager;
import net.aerith.misao.catalog.io.*;

/**
 * The <code>ChartComponent</code> represents a GUI component to show
 * a star chart.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2004 November 24
 */

public class ChartComponent extends JLabel implements PropertyChangedListener {
	/**
	 * The pane of this component.
	 */
	protected Container pane;

	/**
	 * The off screen image.
	 */
	protected Image offscreen;

	/**
	 * The list of stars to plot.
	 */
	protected StarPositionList list;

	/**
	 * The size of chart.
	 */
	protected Size size;

	/**
	 * The properties to plot stars.
	 */
	protected Hashtable plot_properties;

	/**
	 * The default properties to plot stars.
	 */
	protected PlotProperty default_property;

	/**
	 * The popup menu.
	 */
	protected JPopupMenu popup;

	/**
	 * True if the (0,0) represents the center of the chart. Otherwise,
	 * the (0,0) represents the top left corner.
	 */
	protected boolean basepoint_at_center = false;

	/**
	 * The number of characters to fold the star data in the dialog.
	 */
	protected int folding_characters = 120;

	/**
	 * The listener of star click event. If null, this chart itself 
	 * handles the event and shows the star information message box.
	 */
	protected StarClickListener star_click_listener = null;

	/**
	 * Constructs an empty <code>ChartComponent</code>.
	 * @param size the size of chart.
	 */
	public ChartComponent ( Size initial_size ) {
		size = initial_size;
		offscreen = null;

		plot_properties = new Hashtable();
		default_property = new PlotProperty();

		pane = this;

		initPopupMenu();
	}

	/**
	 * Initializes a popup menu. A <tt>popup</tt> must be created 
	 * previously.
	 */
	protected void initPopupMenu ( ) {
		popup = new JPopupMenu();

		JMenuItem item = new JMenuItem("Save");
		item.addActionListener(new SaveListener());
		popup.add(item);

		Vector writer_list = CatalogManager.getCatalogWriterList();
		for (int i = 0 ; i < writer_list.size() ; i++) {
			CatalogWriter writer = (CatalogWriter)writer_list.elementAt(i);
			item = new JMenuItem("Save As " + writer.getName());
			item.addActionListener(new SaveAsListener(writer));
			popup.add(item);
		}

		enableEvents(AWTEvent.MOUSE_EVENT_MASK);
	}

	/**
	 * Sets a flag so that (0,0) represents the center of the chart.
	 */
	public void setBasepointAtCenter ( ) {
		basepoint_at_center = true;
	}

	/**
	 * Sets a flag so that (0,0) represents the top left corner.
	 */
	public void setBasepointAtTopLeft ( ) {
		basepoint_at_center = false;
	}

	/**
	 * Sets the default property.
	 * @param property the default property.
	 */
	public void setDefaultProperty ( PlotProperty property ) {
		default_property = property;
		offscreen = null;
	}

	/**
	 * Sets the property to plot stars of the specified catalog. In
	 * order to redraw the off screen, here <tt>offscreen</tt> is set
	 * as null. It will be drawn again in <tt>update</tt> method.
	 * @param catalog_name the name of catalog.
	 * @param property     the property.
	 */
	public void setProperty ( String catalog_name, PlotProperty property ) {
		plot_properties.put(catalog_name, property);
		offscreen = null;
	}

	/**
	 * Sets the new size. In order to redraw the off screen, here 
	 * <tt>offscreen</tt> is set as null. It will be drawn again in
	 * <tt>update</tt> method.
	 * @param new_size the new size of the chart.
	 */
	public void setSize ( Size new_size ) {
		size = new_size;
		offscreen = null;
	}

	/**
	 * Sets the new list of stars. In order to redraw the off screen,
	 * here <tt>offscreen</tt> is set as null. It will be drawn again
	 * in <tt>update</tt> method.
	 * @param new_list the new list of stars.
	 */
	public void setStarPositionList ( StarPositionList new_list ) {
		list = new_list;
		offscreen = null;
	}

	/**
	 * Gets the property of the specified star.
	 * @param star the star position object.
	 * @return the property of the specified star.
	 */
	protected PlotProperty getProperty ( StarPosition star ) {
		PlotProperty property = null;
		if (star instanceof CatalogStar)
			property = (PlotProperty)plot_properties.get(((CatalogStar)star).getCatalogName());
		if (property == null)
			property = default_property;
		return property;
	}

	/**
	 * Gets the preferred size, considering the zoom level.
	 * @return the preferred size.
	 */
    public Dimension getPreferredSize ( ) {
		return new Dimension(size.getWidth(), size.getHeight());
	}

	/**
	 * Sets the listener of star click event. If null, this chart 
	 * itself becomes to handle the event and shows the star 
	 * information message box.
	 * @param listener the listener.
	 */
	public void setStarClickListener ( StarClickListener listener ) {
		star_click_listener = listener;
	}

	/**
	 * Invoked when mouse event occurs. It is to show a click data
	 * dialog or popup menu.
	 * @param e contains the click position.
	 */
	protected void processMouseEvent ( MouseEvent e ) {
		if (e.isPopupTrigger()) {
			popup.show(e.getComponent(), e.getX(), e.getY());
		} else if (e.getID() == MouseEvent.MOUSE_CLICKED) {
			// Outputs data of a star at the mouse clicked point.
			StarList star_list = new StarList();
			Vector string_list = new Vector();
			for (int i = 0 ; i < list.size() ; i++) {
				StarPosition star = (StarPosition)list.elementAt(i);
				double x = star.getX();
				double y = star.getY();
				if (basepoint_at_center) {
					x += size.getWidth() / 2;
					y += size.getHeight() / 2;
				}

				PlotProperty property = getProperty(star);

				if (property.isEnabled()) {
					int radius = 0;
					if (star instanceof Star) {
						radius = property.getRadius(property.getMagnitude((Star)star));
					} else {
						radius = property.getFixedRadius();
					}

					if (Math.abs(e.getX() - x) <= radius  &&  Math.abs(e.getY() - y) <= radius) {
						if (star instanceof Star)
							star_list.addElement(star);

						String[] strings = null;
						if (star instanceof Star)
							strings = ((Star)star).getOutputStringsWithXY();
						else
							strings = star.getOutputStrings();
						for (int j = 0 ; j < strings.length ; j++)
							string_list.addElement(strings[j]);
					}
				}
			}

			if (star_click_listener != null) {
				// Transfers to the event handler.
				if (star_list.size() > 0)
					star_click_listener.starsClicked(star_list);
			} else {
				if (string_list.size() > 0) {
					// Folds the long messages.
					Vector l = new Vector();
					for (int i = 0 ; i < string_list.size() ; i++) {
						String s = (String)string_list.elementAt(i);
						while (s.length() > folding_characters) {
							l.addElement(s.substring(0, folding_characters));
							s = "    " + s.substring(folding_characters);
						}
						if (s.length() > 0)
							l.addElement(s);
					}

					MessagesDialog dialog = new MessagesDialog(null, l);
					dialog.setContentsToSave(string_list);
					dialog.show(this);
				}
			}
		}
		super.processMouseEvent(e);
	}

	/**
	 * Applies the property change. In order to redraw the off screen,
	 * here <tt>offscreen</tt> is set as null. It will be drawn again
	 * in <tt>update</tt> method.
	 */
	public void propertyChanged ( ) {
		offscreen = null;
		repaint();
	}

	/**
	 * Updates the view.
	 * @param g the <code>Graphics</code> to update.
	 */
	public void paint ( Graphics g ) {
		update(g);
	}

	/**
	 * Updates the view.
	 * @param g the <code>Graphics</code> to update.
	 */
	public void update ( Graphics g ) {
		if (size != null) {
			if (offscreen == null) {
				offscreen = createImage(size.getWidth(), size.getHeight());
				draw(offscreen.getGraphics());
			}

			g.drawImage(offscreen, 0, 0, this);
		}
	}

	/**
	 * Plots stars in the list on the off screen.
	 * @param g the <code>Graphics</code> to draw.
	 */
	public void draw ( Graphics g ) {
		Color color = g.getColor();
		g.setColor(getBackground());
		g.fillRect(0, 0, size.getWidth(), size.getHeight());
		g.setColor(color);

		for (int i = 0 ; i < list.size() ; i++) {
			StarPosition star = (StarPosition)list.elementAt(i);

			int x = (int)star.getX();
			int y = (int)star.getY();
			if (basepoint_at_center) {
				x += size.getWidth() / 2;
				y += size.getHeight() / 2;
			}

			PlotProperty property = getProperty(star);

			if (property.isEnabled())
				property.plot(g, x, y, star);
		}
	}

	/**
	 * The <code>SaveListener</code> is a listener class of menu 
	 * selection to save the report in a file.
	 */
	protected class SaveListener implements ActionListener {
		/**
		 * Invoked when the menu is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			try {
				CommonFileChooser file_chooser = new CommonFileChooser();
				file_chooser.setDialogTitle("Save star data.");
				file_chooser.setMultiSelectionEnabled(false);

				if (file_chooser.showSaveDialog(pane) == JFileChooser.APPROVE_OPTION) {
					File file = file_chooser.getSelectedFile();
					if (file.exists()) {
						String message = "Overwrite to " + file.getPath() + " ?";
						if (0 != JOptionPane.showConfirmDialog(pane, message, "Confirmation", JOptionPane.YES_NO_OPTION)) {
							return;
						}
					}

					PrintStream stream = new PrintStream(new BufferedOutputStream(new FileOutputStream(file)));

					// Outputs data of all stars.
					for (int i = 0 ; i < list.size() ; i++) {
						StarPosition star = (StarPosition)list.elementAt(i);

						PlotProperty property = getProperty(star);
						if (property.isEnabled()) {
							String[] strings = null;
							if (star instanceof Star)
								strings = ((Star)star).getOutputStringsWithXY();
							else
								strings = star.getOutputStrings();
							for (int j = 0 ; j < strings.length ; j++)
								stream.println(strings[j]);
						}
					}

					stream.close();

					String message = "Succeeded to save " + file.getPath();
					JOptionPane.showMessageDialog(pane, message);
				}
			} catch ( IOException exception ) {
				System.err.println(exception);

				String message = "Failed to save file.";
				JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * The <code>SaveAsListener</code> is a listener class of menu 
	 * selection to save the report in the specified catalog.
	 */
	protected class SaveAsListener implements ActionListener {
		/**
		 * The catalog writer.
		 */
		protected CatalogWriter writer = null;

		/**
		 * Construct a <code>SaveAsListener</code>.
		 * @param writer the catalog writer.
		 */
		public SaveAsListener ( CatalogWriter writer ) {
			this.writer = writer;
		}

		/**
		 * Invoked when the menu is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			try {
				CommonFileChooser file_chooser = new CommonFileChooser();
				file_chooser.setDialogTitle("Save star data.");
				file_chooser.setMultiSelectionEnabled(false);

				if (file_chooser.showSaveDialog(pane) == JFileChooser.APPROVE_OPTION) {
					File file = file_chooser.getSelectedFile();
					if (file.exists()) {
						String message = "Overwrite to " + file.getPath() + " ?";
						if (0 != JOptionPane.showConfirmDialog(pane, message, "Confirmation", JOptionPane.YES_NO_OPTION)) {
							return;
						}
					}

					StarList star_list = new StarList();
					for (int i = 0 ; i < list.size() ; i++) {
						StarPosition star = (StarPosition)list.elementAt(i);
						if (star instanceof Star)
							star_list.addElement(star);
					}

					// Outputs data of all stars.
					writer.setFile(file);
					writer.writeAll(star_list);

					String message = "Succeeded to save " + file.getPath();
					JOptionPane.showMessageDialog(pane, message);
				}
			} catch ( IOException exception ) {
				String message = "Failed to save file.";
				JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
			} catch ( UnsupportedStarClassException exception ) {
				String message = "Failed to save some star data.";
				JOptionPane.showMessageDialog(pane, message, "Warning", JOptionPane.WARNING_MESSAGE);
			}
		}
	}
}
