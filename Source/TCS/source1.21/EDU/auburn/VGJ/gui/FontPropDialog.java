package EDU.auburn.VGJ.gui;

/*
 * File: FontPropDialog.java
 *
 * 4/10/97   Larry Barowski
 *
*/

import java.awt.Dialog;
import java.awt.TextField;
import java.awt.Button;
import java.awt.Label;
import java.awt.Frame;
import java.awt.Event;
import java.awt.Font;

import java.lang.System;



/**
 * A dialog class that allows the user to specify font
 * properites.
 * </p>Here is the <a href="../gui/FontPropDialog.java">source</a>.
 *
 * @author     Larry Barowski
 * @created    July 2, 2003
 */

public class FontPropDialog extends Dialog
{
	private GraphCanvas graphCanvas_;
	private Frame frame_;

	private TextField name_, size_;
	private boolean fontForNode;


	public FontPropDialog(Frame frame, GraphCanvas graph_canvas, boolean node)
	{
		super(frame, "", true);
		String label;
		fontForNode = node;
		if (node) {
			label = "Font for Node labels";
		} else {
			label = "Font for Edge labels";
		}
		super.setTitle(label);

		graphCanvas_ = graph_canvas;
		frame_ = frame;
		LPanel p  = new LPanel();

		p.addLabel("Font Name", 1, 1, 0.0, 1.0, 0, 2);
		name_ = p.addTextField(15, 0, -1, 1.0, 1.0, 1, 1);
		p.addLabel("Font Size", 1, 1, 0.0, 1.0, 0, 2);
		size_ = p.addTextField(15, 0, -1, 1.0, 1.0, 1, 1);
		p.addButtonPanel("Apply Cancel", 0);

		p.finish();
		add("Center", p);

	}


	public boolean action(Event event, Object object)
	{
		if (event.target instanceof Button)
		{
			if ("Apply".equals(object))
			{
				boolean ok  = true;
				try
				{
					Font font;
					font = new Font(name_.getText(), Font.PLAIN, Integer.valueOf(size_.getText()).intValue());
					if (fontForNode)
						graphCanvas_.setFont(font);
					else
						graphCanvas_.setEdgeFont(font);
				}
				catch (NumberFormatException e)
				{
					new MessageDialog(frame_, "Error", "Bad format for font size.", true);
					ok = false;
				}

				if (ok)
				{
					hide();
				}
			}
			else if ("Cancel".equals(object))
			{
				hide();
			}
		}

		return false;
	}


	public boolean handleEvent(Event event)
	{
		// Avoid having everything destroyed.
		if (event.id == Event.WINDOW_DESTROY)
		{
			hide();
			return true;
		}
		return super.handleEvent(event);
	}


	public void showMe()
	{
		pack();
		Font font  = graphCanvas_.getFont(fontForNode);
		name_.setText(font.getName());
		size_.setText(String.valueOf(font.getSize()));

		show();
	}
}
