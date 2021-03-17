package EDU.auburn.VGJ.gui;

/*
    File: pictdialog.java
    2/16/97   Larry Barowski
    6/06/00   Jacob Derington
  */
//version pass jake pict 02/07/01

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
import EDU.auburn.VGJ.graph.Graph;
import java.io.RandomAccessFile;


/**
 * A dialog class that allows the user to specify parameters for
 * PostScript output.
 * </p>Here is the <a href="../gui/PSdialog.java">source</a>.
 *
 * @author     Larry Barowski
 * @created    June 3, 2003
 */

public class pictdialog extends Dialog
{
	private Graph graph_;
	private GraphCanvas graphCanvas_;
	private Frame frame_;

	private TextField width_, height_, pointSize_, margin_, pWidth_, pHeight_, overlap_, printCmd_;
	private Checkbox landscape_;



	public pictdialog(Frame frame, GraphCanvas graph_canvas)
	{
		super(frame, "PICT Format Output", false);
		graphCanvas_ = graph_canvas;
		frame_ = frame;
		LPanel p  = new LPanel();
		//p.addLineLabel("Printed Size (inches):", 0);
		//p.addLabel("Width", 1, 1, 0.0, 1.0, 0, 2);
		//width_ = p.addTextField("8.5", 8, 1, -1, 1.0, 0.0, 1, 1);
		//p.addLabel("Height", 1, 1, 0.0, 1.0, 0, 2);
		//height_ = p.addTextField("11", 8, 0, -1, 1.0, 0.0, 1, 1);
		p.addLineLabel("Change no fields.  Press Save.  Works for PCs, and MACs!!!", 0);

		p.addLabel("Width", 1, 1, 0.0, 1.0, 0, 2);
		pWidth_ = p.addTextField("8.5", 8, 1, -1, 1.0, 0.0, 1, 1);
		p.addLabel("Height", 1, 1, 0.0, 1.0, 0, 2);
		pHeight_ = p.addTextField("11", 8, 0, -1, 1.0, 0.0, 1, 1);

		//p.addLabel("Font Size (points)", 3, 1, 0.0, 1.0, 0, 2);
		//pointSize_ = p.addTextField("10", 8, 0, -1, 1.0, 0.0, 1, 1);
		//p.addLabel("Outside Margin (inches)", 3, 1, 0.0, 1.0, 0, 2);
		//margin_ = p.addTextField(".5", 8, 0, -1, 1.0, 0.0, 1, 1);
		//p.addLabel("Multi-Page Overlap (inches)", 3, 1, 0.0, 1.0, 0, 2);
		//overlap_ = p.addTextField(".5", 8, 0, -1, 1.0, 0.0, 1, 1);
		landscape_ = new Checkbox("Landscape", null, false);
		p.addComponent(landscape_, 0, -1, 0.0, 1.0, 0, 0);
		p.addLineLabel("System Print Command:", 0);
		printCmd_ = p.addTextField("/usr/ucb/lpr", 8, 0, -1, 1.0, 1.0, 1, 0);
		//p.addButtonPanel("Save Print Close", 0); //removed view
		p.addButtonPanel("Save Close", 0);
		//removed view
		p.finish();
		add("Center", p);
		// pack();
		// show();

	}


	public boolean Save(String filename)
	{
		if (filename == null)
		{
			// String filename;
			FileDialog fd;
			try
			{
				fd = new FileDialog(frame_, "Save PICT File", FileDialog.SAVE);
				fd.setVisible(true);

			}
			catch (Throwable e)
			{
				MessageDialog dg  = new MessageDialog(frame_, "Error", "It appears your VM does not allow file saving.", true);
				return true;
			}
			filename = fd.getFile();
			if (filename == null)
			{
				return true;
			}

			filename = fd.getDirectory() + filename;
		}
		FileOutputStream ostream;
		RandomAccessFile out_;
		try
		{
			//ostream = new FileOutputStream(filename);
			out_ = new RandomAccessFile(filename, "rw");
			getPict_(out_);
		}
		catch (IOException f)
		{
			f.printStackTrace();
			return false;
		}
		return true;
	}


	public boolean action(Event event, Object object)
	{
		if (event.target instanceof Button)
		{
			//if ("View".equals(object)) {
			//String PS = getPS_();
			//if (PS != null)
			//	new TextOutDialog(frame_, "PostScript", PS, false);
			//}
			//else
			if ("Close".equals(object))
			{
				hide();
			}
			else if ("Save".equals(object))
			{
				return Save(null);
				/*
				    commented out SW 6-2003
				    if (PS == null)
				    return false;
				    try {
				    String filename;
				    FileDialog fd;
				    try
				    {
				    fd = new FileDialog(frame_, "Save PICT File", FileDialog.SAVE);
				    fd.setVisible(true);

				    }
				    catch (Throwable e)
				    {
				    MessageDialog dg  = new MessageDialog(frame_, "Error", "It appears your VM does not allow file saving.", true);
				    return true;
				    }
				    filename = fd.getFile();
				    if (filename == null)
				    {
				    return true;
				    }
				  */
				/*
				    Work around JDK Windows bug.
				    if (filename.endsWith(".*.*")) {
				    String tmpstr = filename.substring(0, filename.length() - 4);
				    filename = tmpstr;
				    }
				  */
				/*
				    commented out by SW 6-2003
				    filename = fd.getDirectory() + filename;
				    PrintStream ps = new PrintStream(new FileOutputStream(filename));//what is this
				    FileOutputStream ostream;
				    RandomAccessFile out_;
				    try
				    {
				    ostream = new FileOutputStream(filename);
				    out_ = new RandomAccessFile(filename, "rw");
				    getPict_(out_);
				    }
				    catch (IOException f)
				    {
				    f.printStackTrace();
				    }
				  */
				//ps.println(PS);
				//ps.close();//I don't know if I need this
			}
			//catch (IOException e) {
			//MessageDialog dg = new MessageDialog(frame_, "Error", e.getMessage(), true);
			//}
			//}
			else if ("Print".equals(object))
			{
				//String PS = getPS_();
				//if (PS == null)
				//return false;
				File file  = new File("vgj00.ps");
				try
				{
					PrintStream ps  = new PrintStream(new FileOutputStream(file));
					//ps.println(PS);
					ps.close();
					String cmd      = new String(printCmd_.getText() + " " + "vgj00.ps");
					Runtime.getRuntime().exec(cmd).waitFor();
				}
				catch (Exception e)
				{
					MessageDialog dg  = new MessageDialog(frame_, "Error", "Printing error: " + e.getMessage(), true);
				}
				try
				{
					file.delete();
				}
				catch (Exception e)
				{
					// Delete fails from appletviewer (although
					// writing over is allowed).

				}
			}
		}
		else if (event.target instanceof Checkbox)
		{
			String tmp   = pWidth_.getText();
			String tmp2  = pHeight_.getText();
			pWidth_.setText(tmp2);
			pHeight_.setText(tmp);
		}
		return false;
	}


//this is the end of action

	private void getPict_(RandomAccessFile out_)
	{
		double width       = 7.5;
		double height      = 10;
		double p_width     = 7.5;
		double p_height    = 7.5;
		double
				fontsize    = 10;
		double margin      = .5;
		double overlap     = .5;
		boolean landscape  = false;

		try
		{
			//width = getVal_(width_, "Width", 1.0, 1000000.0);
			//height = getVal_(height_, "Height", 1.0, 1000000.0);
			p_width = getVal_(pWidth_, "Paper Width", 1.0, 1000000.0);
			p_height = getVal_(pHeight_, "Paper Height", 1.0, 1000000.0);
			//fontsize = getVal_(pointSize_ , "Font Size", .5, 100.0);
			//margin = getVal_(margin_, "Margin", 0.0, 4.0);
			//overlap = getVal_(overlap_, "Overlap", 0.0, 2.0);
		}
		catch (NumberFormatException e)
		{
			return;
		}

		if (width - margin * 2.0 < 1.0)
		{
			new MessageDialog(frame_, "Error", "Width minus twice margin must be at least one inch.", true);
			return;
		}
		if (height - margin * 2.0 < 1.0)
		{
			new MessageDialog(frame_, "Error", "Height minus twice margin must be at least one inch.", true);
			return;
		}

		landscape = landscape_.getState();
		graphCanvas_.toPict(width, height, p_width, p_height, fontsize, margin, overlap, landscape, out_);
		return;
	}


	private double getVal_(TextField text, String name, double min, double max)
	{
		double result  = 1.0;

		try
		{
			result = Double.valueOf(text.getText()).doubleValue();
		}
		catch (NumberFormatException e)
		{
			result = 1.0;
			String msg  = "Bad format for " + name + ".";
			new MessageDialog(frame_, "Error", msg, true);
			throw e;
		}

		if (result < min)
		{
			String msg  = name + " must be at least " + min + ".";
			new MessageDialog(frame_, "Error", msg, true);

			throw new NumberFormatException();
		}

		if (result > max)
		{
			String msg  = name + " must be no more than " + max + ".";
			new MessageDialog(frame_, "Error", msg, true);

			throw new NumberFormatException();
		}

		return result;
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

}

