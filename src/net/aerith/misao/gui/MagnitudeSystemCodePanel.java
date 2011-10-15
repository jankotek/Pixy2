/*
 * @(#)MagnitudeSystemCodePanel.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import net.aerith.misao.util.*;

/**
 * The <code>MagnitudeSystemCodePanel</code> represents a panel which
 * consists of components to input magnitude system code and the 
 * button to show the help message.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2002 August 4
 */

public class MagnitudeSystemCodePanel extends JPanel {
	/**
	 * The text field to input the magnitude system code.
	 */
	protected JTextField text;

	/**
	 * The button to show the help message.
	 */
	protected JButton button_help;

	/**
	 * The pane of this component.
	 */
	protected Container pane;

	/**
	 * Constructs a <code>DateAndTimePanel</code>.
	 */
	public MagnitudeSystemCodePanel ( ) {
		pane = this;

		JPanel panel = new JPanel();

		text = new JTextField();
		text.setColumns(10);
		panel.add(text);
		button_help = new JButton("Help");
		button_help.addActionListener(new HelpListener());
		panel.add(button_help);

		add(panel);
	}

	/**
	 * Gets the code of the magnitude system.
	 * @return the code of the magnitude system.
	 */
	public String getMagSystemCode ( ) {
		return text.getText();
	}

	/**
	 * Sets the code of the magnitude system.
	 * @param code the code of the magnitude system.
	 */
	public void setMagSystemCode ( String code ) {
		text.setText(code);
	}

	/**
	 * The <code>HelpListener</code> is a listener class of button 
	 * push to show the help message.
	 */
	protected class HelpListener implements ActionListener {
		/**
		 * Invoked when the button is pushed.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			String message = "<html><body>";
			message += "<p>";
			message += "Magnitude system codes:";
			message += "<blockquote><table>";
			message += "<tr><td align=center><b><font color=\"#ff00aa\">C</font></b></td><td>Unfiltered CCD</td></tr>";
			message += "<tr><td align=center><b><font color=\"#ff00aa\">Ic</font></b></td><td>Ic-band filter</td></tr>";
			message += "<tr><td align=center><b><font color=\"#ff00aa\">Rc</font></b></td><td>Rc-band filter</td></tr>";
			message += "<tr><td align=center><b><font color=\"#ff00aa\">R</font></b></td><td>R-band filter</td></tr>";
			message += "<tr><td align=center><b><font color=\"#ff00aa\">V</font></b></td><td>V-band filter</td></tr>";
			message += "<tr><td align=center><b><font color=\"#ff00aa\">B</font></b></td><td>B-band filter</td></tr>";
			message += "<tr><td align=center><b><font color=\"#ff00aa\">U</font></b></td><td>U-band filter</td></tr>";
			message += "<tr><td align=center><b><font color=\"#ff00aa\">CIR</font></b></td><td>IR-cutting filter</td></tr>";
			message += "<tr><td align=center><b><font color=\"#ff00aa\">p</font></b></td><td>Photograph</td></tr>";
			message += "</table></blockquote>";
			message += "</body></html>";

			JLabel label = new JLabel(message);
			label.setSize(400,300);
			JOptionPane.showMessageDialog(pane, label);
		}
	}
}
