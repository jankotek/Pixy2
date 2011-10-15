/*
 * @(#)SplashScreen.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui;
import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.basic.*;

/**
 * The <code>SplashScreen</code> represents the splash screen to be 
 * shown when the application starts. 
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2000 June 24
 */

public class SplashScreen extends JWindow {
	/**
	 * The image to show.
	 */
	protected ImageIcon splash_icon;

	/**
	 * The time to show the screen in second.
	 */
	protected int time_to_show = 10;

	/**
	 * Constructs a <code>SplashScreen</code> with an <code>ImageIcon</code>
	 * to show.
	 * @param icon the image to show.
	 */
	public SplashScreen ( ImageIcon icon ) {
		splash_icon = icon;
	}

	/**
	 * Sets the time to show the screen in second.
	 * @param time the time to show the screen in second.
	 */
	public void setTime ( int time ) {
		time_to_show = time;
	}

	/**
	 * Shows the splash screen while the specified time.
	 */
	public void showSplashScreen ( ) {
		Dimension screen_rect = Toolkit.getDefaultToolkit().getScreenSize();

		int x = screen_rect.width / 2 - splash_icon.getIconWidth() / 2;
		int y = screen_rect.height / 2 - splash_icon.getIconHeight() / 2;

		JPanel splash = (JPanel)getContentPane();
		splash.add(new ImageLabel(splash_icon), BorderLayout.CENTER);

		setBounds(x, y, splash_icon.getIconWidth(), splash_icon.getIconHeight());
		setVisible(true);

		try {
			Thread.sleep(time_to_show * 1000);
		} catch ( Exception exception ) {
		}

		setVisible(false);
	}
}

