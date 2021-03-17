package EDU.auburn.VGJ.calc;
/*created sept 11, 2000
 *by Jake Derington
 *
*/
import java.lang.Object;

import java.awt.Dialog;
import java.awt.Button;
import java.awt.Frame;
import java.awt.Event;
import java.awt.TextField;
import java.awt.FileDialog;
import java.awt.Checkbox;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.lang.Process;
import java.lang.Runtime;
import java.lang.Exception;
import EDU.auburn.VGJ.graph.Graph;
import java.io.DataOutputStream;
import EDU.auburn.VGJ.gui.*;
import clad.*;

public class CalcDialog extends Dialog
{

	private Graph graph_;
	private GraphCanvas graphCanvas_;
	private Frame frame_;

	private TextField width_, height_, pointSize_, margin_, pWidth_, pHeight_, overlap_, printCmd_;
	private Checkbox landscape_;

	public TCS tcs;


	public CalcDialog(Frame frame, GraphCanvas graph_canvas, TCS tdna)
	{
		super(frame, "Clads Base pair changes", false);
		graphCanvas_ = graph_canvas;
		frame_ = frame;
		LPanel p  = new LPanel();
		p.addLineLabel("This will give one implementation of base pair changes that occur on each node", 0);
		p.addButtonPanel("Execute Close", 0);
		p.finish();
		add("Center", p);
		pack();
		show();
		tcs = tdna;
	}


	public boolean action(Event event, Object object)
	{
		if (event.target instanceof Button)
		{

			if ("Close".equals(object))
			{
				hide();
			}
			else if ("Execute".equals(object))
			{
				Calc calc  = new Calc(tcs);
				calc.run();
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
}  //end class

