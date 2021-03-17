package EDU.auburn.VGJ.gui;

/*
 * File: AlgPropDialog.java
 *
 * 2/25/97   Larry Barowski
 *
*/

import java.awt.Dialog;
import java.awt.TextField;
import java.awt.Button;
import java.awt.Label;
import java.awt.Frame;
import java.awt.Event;

import java.lang.System;



/**
 * A dialog class that allows the user to specify parameters
 * used by algorithms.
 * </p>Here is the <a href="../gui/AlgPropDialog.java">source</a>.
 *
 * @author     Larry Barowski
 * @created    July 2, 2003
 */

public class AlgPropDialog extends Dialog
{
	private GraphCanvas graphCanvas_;
	private Frame frame_;

	private TextField vertical_, horizontal_;



	public AlgPropDialog(Frame frame, GraphCanvas graph_canvas)
	{
		super(frame, "Spacing (for some algorithms)", true);

		graphCanvas_ = graph_canvas;
		frame_ = frame;
		LPanel p  = new LPanel();

		p.addLabel("Vertical Spacing (pixels)", 1, 1, 0.0, 1.0, 0, 2);
		vertical_ = p.addTextField(8, 0, -1, 1.0, 1.0, 1, 1);
		p.addLabel("Horizontal Spacing (pixels)", 1, 1, 0.0, 1.0, 0, 2);
		horizontal_ = p.addTextField(8, 0, -1, 1.0, 1.0, 1, 1);
		p.addButtonPanel("Apply Cancel", 0);

		p.finish();
		add("Center", p);
		showMe();
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
					graphCanvas_.hSpacing = Double.valueOf
							(horizontal_.getText()).doubleValue();
				}
				catch (NumberFormatException e)
				{
					new MessageDialog(frame_, "Error", "Bad format for horizontal spacing.", true);
					ok = false;
				}
				try
				{
					graphCanvas_.vSpacing = Double.valueOf
							(vertical_.getText()).doubleValue();
				}
				catch (NumberFormatException e)
				{
					new MessageDialog(frame_, "Error", "Bad format for vertical spacing.", true);
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

		vertical_.setText(String.valueOf(graphCanvas_.vSpacing));
		horizontal_.setText(String.valueOf(graphCanvas_.hSpacing));

		show();
	}
}
