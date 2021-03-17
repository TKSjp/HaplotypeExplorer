package EDU.auburn.VGJ.gui;

/*
 * File: LPanel.java
 *
 * 5/31/97   Larry Barowski
 *
*/

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Frame;
import java.awt.TextField;
import java.awt.Color;
import java.awt.Label;
import java.awt.Container;
import java.awt.Panel;
import java.awt.Button;
import java.awt.Component;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;


/**
 * A panel class with convenience functions.
 *	</p>Here is the <a href="../gui/LPanel.java">source</a>.
 *
 * @author     Larry Barowski
 * @created    July 2, 2003
 */

public class LPanel extends JPanel
{
	public Color textColor                 = Color.white;
	public int spacing                     = 8;

	public GridBagLayout layout;
	public GridBagConstraints constraints;


	public LPanel()
	{
		super();

		layout = new GridBagLayout();
		constraints = new GridBagConstraints();
		setLayout(layout);

		constraints.insets = new Insets(spacing, spacing, spacing, spacing);
		constraints.fill = GridBagConstraints.NONE;
		constraints.ipadx = constraints.ipady = 0;
		constraints.weightx = constraints.weighty = 1.0;
	}
	

	public Button addButton(String string, int width, int anchor, double weightx, double weighty, int fill, int shift)
	{
		return (Button) (addComponent(new Button(string), width, anchor, weightx, weighty, fill, shift));
	}



	// added by DP 19 March 2004
	public Button addButton(String string, java.awt.Font font, int width, int anchor, double weightx, double weighty, int fill, int shift)
	{
		Button button = new Button (string);
		button.setFont(font);
		return (Button) (addComponent(button, width, anchor, weightx, weighty, fill, shift));
	}


	/**
	 * Add a panel of evenly-spaced buttons.
	 *
	 * @param  labels  The feature to be added to the ButtonPanel attribute
	 * @param  width   The feature to be added to the ButtonPanel attribute
	 * @return         Description of the Return Value
	 */
	public Panel addButtonPanel(String labels, int width)
	{
		Panel panel                           = new Panel();
		GridBagConstraints panel_constraints  = new GridBagConstraints();
		panel_constraints.insets = new Insets(0, 0, 0, 0);
		panel_constraints.fill = GridBagConstraints.NONE;
		panel_constraints.ipadx = panel_constraints.ipady = 0;
		panel_constraints.weightx = panel_constraints.weighty = 1.0;
		GridBagLayout panel_layout            = new GridBagLayout();
		panel.setLayout(panel_layout);

		Button button;
		labels.trim();
		while (labels.length() > 0)
		{
			int end       = labels.indexOf(" ");
			if (end == -1)
			{
				end = labels.length();
			}

			String label  = labels.substring(0, end);
			button = new Button(label);
			panel_layout.setConstraints(button, panel_constraints);
			panel.add(button);

			if (end != labels.length())
			{
				labels = labels.substring(end + 1);
				labels.trim();
			}
			else
			{
				break;
			}
		}

		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.insets.top = spacing * 2;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		if (width == 0)
		{
			constraints.gridwidth = GridBagConstraints.REMAINDER;
		}
		else
		{
			constraints.gridwidth = width;
		}

		layout.setConstraints(panel, constraints);
		add(panel);

		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.insets.top = spacing;

		return panel;
	}


	public Checkbox addCheckbox(String string, CheckboxGroup group, boolean state, int width, int anchor, double weightx, double weighty, int fill, int shift)
	{
		return (Checkbox) (addComponent(new Checkbox(string, group, state), width, anchor, weightx, weighty, fill, shift));
	}


	public Component addComponent(Component component, int width, int anchor, double weightx, double weighty, int fill, int shift)
	{
		constraints.insets.left = spacing;
		constraints.insets.right = spacing;
		if (shift == 1 || shift == 3)
		{
			constraints.insets.left = 0;
		}
		if (shift == 2 || shift == 3)
		{
			constraints.insets.right = 0;
		}

		if (anchor < 0)
		{
			constraints.anchor = GridBagConstraints.WEST;
		}
		else if (anchor == 0)
		{
			constraints.anchor = GridBagConstraints.CENTER;
		}
		else
		{
			constraints.anchor = GridBagConstraints.EAST;
		}

		if (width == 0)
		{
			constraints.gridwidth = GridBagConstraints.REMAINDER;
		}
		else
		{
			constraints.gridwidth = width;
		}

		constraints.weightx = weightx;
		constraints.weighty = weighty;

		if (fill == 0)
		{
			constraints.fill = GridBagConstraints.NONE;
		}
		else if (fill == 1)
		{
			constraints.fill = GridBagConstraints.HORIZONTAL;
		}
		else if (fill == 2)
		{
			constraints.fill = GridBagConstraints.VERTICAL;
		}
		else
		{
			constraints.fill = GridBagConstraints.BOTH;
		}

		layout.setConstraints(component, constraints);
		add(component);

		if (width == 0)
		{
			constraints.insets.top = constraints.insets.bottom = spacing;
		}

		return component;
	}


	public Label addLabel(String string, int width, int anchor, double weightx, double weighty, int fill, int shift)
	{
		return (Label) (addComponent(new Label(string), width, anchor, weightx, weighty, fill, shift));
	}


	/**
	 * Add a left aligned label at the start of a line.
	 *
	 * @param  string  The feature to be added to the LineLabel attribute
	 * @param  width   The feature to be added to the LineLabel attribute
	 * @return         Description of the Return Value
	 */
	public Label addLineLabel(String string, int width)
	{
		Label label;

		if (width == 0)
		{
			constraints.gridwidth = GridBagConstraints.REMAINDER;
			constraints.insets.bottom = 0;
		}
		else
		{
			constraints.gridwidth = width;
			constraints.weightx = 0.0;
		}

		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets.top = spacing;
		constraints.insets.left = spacing;
		constraints.insets.right = spacing;
		label = new Label(string);
		layout.setConstraints(label, constraints);
		add(label);

		if (width == 0)
		{
			constraints.insets.top = 0;
		}
		constraints.gridwidth = 1;
		constraints.insets.bottom = spacing;
		constraints.weightx = 1.0;

		return label;
	}


	/**
	 * Add a left-aligned, full-width text field.
	 *
	 * @param  len      The feature to be added to the TextField attribute
	 * @param  width    The feature to be added to the TextField attribute
	 * @param  anchor   The feature to be added to the TextField attribute
	 * @param  weightx  The feature to be added to the TextField attribute
	 * @param  weighty  The feature to be added to the TextField attribute
	 * @param  fill     The feature to be added to the TextField attribute
	 * @param  shift    The feature to be added to the TextField attribute
	 * @return          Description of the Return Value
	 */
	public TextField addTextField(int len, int width, int anchor, double weightx, double weighty, int fill, int shift)
	{
		TextField textfield  = new TextField("", len);
		textfield.setBackground(textColor);
		return (TextField) (addComponent(textfield, width, anchor, weightx, weighty, fill, shift));
	}


	/**
	 * Add a left-aligned, full-width text field.
	 *
	 * @param  text     The feature to be added to the TextField attribute
	 * @param  len      The feature to be added to the TextField attribute
	 * @param  width    The feature to be added to the TextField attribute
	 * @param  anchor   The feature to be added to the TextField attribute
	 * @param  weightx  The feature to be added to the TextField attribute
	 * @param  weighty  The feature to be added to the TextField attribute
	 * @param  fill     The feature to be added to the TextField attribute
	 * @param  shift    The feature to be added to the TextField attribute
	 * @return          Description of the Return Value
	 */
	public TextField addTextField(String text, int len, int width, int anchor, double weightx, double weighty, int fill, int shift)
	{
		TextField textfield  = new TextField(text, len);
		textfield.setBackground(textColor);
		return (TextField) (addComponent(textfield, width, anchor, weightx, weighty, fill, shift));
	}


	/**
	 * Finish initialization.
	 */
	public void finish()
	{
		constraints = null;
		layout = null;
	}
}
