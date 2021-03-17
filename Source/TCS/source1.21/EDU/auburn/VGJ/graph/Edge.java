package EDU.auburn.VGJ.graph;

/*
 *File: Edge.java
 *
 * Date      Author
 * 2/20/97   Larry Barowski
 *
 */
//version pass jake pict 02/07/01

import java.awt.Graphics;
import java.awt.FontMetrics;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.lang.System;
import java.util.Hashtable;
import java.util.Enumeration;

import EDU.auburn.VGJ.util.DPoint3;
import EDU.auburn.VGJ.util.Matrix44;
import EDU.auburn.VGJ.util.DPoint;
import EDU.auburn.VGJ.gui.GraphCanvas;

import java.io.FileOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 *	A class for representing a graph edge.
 *	</p>Here is the <a href="../graph/Edge.java">source</a>.
 *
 * @author     Larry Barowski
 * @created    July 2, 2003
 */

public class Edge
{

	/* Change the following four lists to change the line styles. */
	public static String styleNames[]         = {"solid", "dashed", "dotted", "dashdot"};
	public static Color styleColors[]         = {Color.black, Color.blue, Color.green, Color.orange};
	public static String styleLabels[]        = {"solid (black)", "dashed (blue)", "dotted (green)", "dashdot (orange)"};
	public static String stylePatterns[]      = {"[1 0 0 0] 0", "[2 1 2 1] 0", "[1 1 1 1] 0", "[2 1 1 1] 0"};

	/**
	 * Just change this list to change the data types.
	 */
//	public static String defaultDataTypes_[]  = {"Frequency", "Weight"};
	public static String defaultDataTypes_[]  = {"Changes", "Confidence"};
	protected Node head_, tail_;
	protected DPoint3[] points_;
	private DPoint3[] oldpoints_            = null;
	private boolean dummy_                  = false;
	private static boolean displayEdgeLabels = false;
	public boolean selected                 = false;
	private String label_;
	private int lineStyle_;
	public Hashtable data_;

	/**
	 * A general purpose data field.
	 * Algorithms that operate on Edges can store any necessary data here.
	 */
	public Object data;


/**
 *Duplicate edge, used to keep data from getting removed from the graph on save, etc.
 */
	public Edge(Node tail, Node head, DPoint3[] points, boolean dummy, Edge edge)
	{
		head_ = head;
		tail_ = tail;
		points_ = points;
		if (points == null)
		{
			points = new DPoint3[0];
		}
		dummy_ = dummy;
		label_ = edge.label_;
		lineStyle_ = edge.lineStyle_;
    
    data_ = edge.data_;

	}

  public Edge(Node tail, Node head, DPoint3[] points, boolean dummy)
	{
		head_ = head;
		tail_ = tail;
		points_ = points;
		if (points == null)
		{
			points = new DPoint3[0];
		}
		dummy_ = dummy;
		label_ = new String("");
		lineStyle_ = 0;
    
		data_ = new Hashtable((int) ((defaultDataTypes_.length + 1) * 1.5));
		for (int i = 0; i < defaultDataTypes_.length; i++)
		{
			data_.put(defaultDataTypes_[i], "");
		}
	}

	public Edge(Node tail, Node head, Edge from)
	{
		head_ = head;
		tail_ = tail;
		dummy_ = from.dummy_;
		label_ = from.label_;
		lineStyle_ = from.lineStyle_;
		DPoint3[] frompoints  = from.points();
		points_ = new DPoint3[frompoints.length];
		System.arraycopy(frompoints, 0, points_, 0, frompoints.length);
		data_ = new Hashtable((int) ((defaultDataTypes_.length + 1) * 1.5));
		for (int i = 0; i < defaultDataTypes_.length; i++)
		{
			data_.put(defaultDataTypes_[i], "");
		}
	}


	public Edge(Node tail, Node head, GMLobject gml)
	{
		head_ = head;
		tail_ = tail;

		GMLobject edgegraphics  = gml.getGMLSubObject("graphics", GMLobject.GMLlist, false);
		if (edgegraphics != null)
		{
			GMLobject point;
			int points_size      = 10;  // Arbitrary value.
			DPoint3[] points     = new DPoint3[points_size];
			DPoint3[] tmppoints;
			int pointnum         = 0;
			for (point = edgegraphics.getGMLSubObject("Point", GMLobject.GMLlist, false); point != null; point = edgegraphics.getNextGMLSubObject())
			{
				Double x;
				Double y;
				Double z;
				x = (Double) point.getValue("x", GMLobject.GMLreal);
				y = (Double) point.getValue("y", GMLobject.GMLreal);
				z = (Double) point.getValue("z", GMLobject.GMLreal);

				if (x != null && y != null)
				{
					if (pointnum >= points_size)
					{
						points_size = pointnum * 2;
						tmppoints = new DPoint3[points_size];
						System.arraycopy(points, 0, tmppoints, 0, pointnum);
						points = tmppoints;
					}
					points[pointnum] = new DPoint3(x.doubleValue(), y.doubleValue(), 0.0);
					if (z != null)
					{
						points[pointnum].z = z.doubleValue();
					}
					else
					{
						points[pointnum].z = 0.0;
					}
					pointnum++;
				}
			}
			points_ = new DPoint3[pointnum];
			System.arraycopy(points, 0, points_, 0, pointnum);
		}
		else
		{
			points_ = new DPoint3[0];
		}

		String label;
		if ((label = (String) gml.getValue("label", GMLobject.GMLstring)) != null)
		{
			label_ = label;
		}
		else
		{
			label = new String();
		}

		String style;
		lineStyle_ = 0;
		if ((style = (String) gml.getValue("linestyle", GMLobject.GMLstring)) != null)
		{
			for (int i = 0; i < styleNames.length; i++)
			{
				if (styleNames[i].equals(style))
				{
					lineStyle_ = i;
					break;
				}
			}
		}

		data_ = new Hashtable((int) ((defaultDataTypes_.length + 1) * 1.5));
		for (int i = 0; i < defaultDataTypes_.length; i++)
		{
			data_.put(defaultDataTypes_[i], "");
		}
		gml.setHashFromGML("data", GMLobject.GMLstring, data_);

	}


	private String arrowPS_(DPoint3 p1, DPoint3 p2)
	{
		double dx      = p1.x - p2.x;
		double dy      = p1.y - p2.y;
		double length  = Math.sqrt(dx * dx + dy * dy);
		dx /= length;
		dy /= length;

		return PSnum_(p2.x) + PSnum_(p2.y) + PSnum_(dx) + PSnum_(dy) + "arrow\n";
	}


	public void draw(Graphics graphics, Matrix44 transform, boolean inplane, boolean directed, boolean arrow_only, int quality, GraphCanvas canvas, int which_gr)
	{
		double scale  = transform.scale;

		int npoints   = points_.length;

		graphics.setColor(styleColors[lineStyle_]);

		DPoint3 p1to;

		DPoint3 p2to;
		if (npoints == 0)
		{
			p1to = head_.getPosition3();
			p2to = tail_.getPosition3();
		}
		else
		{
			p1to = points_[0];
			p2to = points_[npoints - 1];
		}

		DPoint3 p1    = tail_.intersectWithLineTo(p1to, inplane, quality);
		DPoint3 p2    = head_.intersectWithLineTo(p2to, inplane, quality);

		p1.transform(transform);
		p2.transform(transform);

		// Self edge with no intermediate points.
		if (head_ == tail_ && npoints == 0)
		{
			p2.x = p1.x + 1;
			p2.y = p1.y;
		}

		if (!arrow_only)
		{
			if (npoints == 0)
			{
				graphics.drawLine((int) p1.x, (int) p1.y, (int) p2.x, (int) p2.y);
			}
			else
			{
				DPoint3 point     = new DPoint3(points_[0]);
				point.transform(transform);
				graphics.drawLine((int) p1.x, (int) p1.y, (int) point.x, (int) point.y);
				DPoint3 oldpoint  = new DPoint3();
				for (int i = 1; i < npoints; i++)
				{
					oldpoint.move(point);
					point.move(points_[i]);
					point.transform(transform);
					graphics.drawLine((int) oldpoint.x, (int) oldpoint.y, (int) point.x, (int) point.y);
				}
				graphics.drawLine((int) point.x, (int) point.y, (int) p2.x, (int) p2.y);
			}
		}

		// Draw arrow.
		if (directed)
		{
			DPoint3 from  = new DPoint3(p2to);
			from.transform(transform);
			if ((int) from.x == (int) p2.x && (int) from.y == (int) p2.y)
			{
				from.x -= 10.0;
			}

			drawArrow_(graphics, from, p2);
		}

		// Draw selection handles.
		if (!arrow_only && selected)
		{
			graphics.setColor(Color.red);

			graphics.drawRect((int) p1.x - 2, (int) p1.y - 2, 4, 4);

			if (head_ != tail_)
			{  // Not a self-edge.
				graphics.drawRect((int) p2.x - 2, (int) p2.y - 2, 4, 4);
			}

			for (int pointindex = 0; pointindex < npoints; pointindex++)
			{
				p2.move(points_[pointindex]);
				p2.transform(transform);
				graphics.drawRect((int) p2.x - 2, (int) p2.y - 2, 4, 4);
			}

			graphics.setColor(Color.white);

			graphics.drawRect((int) p1.x - 1, (int) p1.y - 1, 2, 2);

			if (head_ != tail_)
			{  // Not a self-edge.
				graphics.drawRect((int) p2.x - 1, (int) p2.y - 1, 2, 2);
			}

			for (int pointindex = 0; pointindex < npoints; pointindex++)
			{
				p2.move(points_[pointindex]);
				p2.transform(transform);
				graphics.drawRect((int) p2.x - 1, (int) p2.y - 1, 2, 2);
			}

			graphics.setColor(Color.black);
		}

		// Draw label.
		if (quality > 0 && label_ != null && label_.length() > 0)
		{
//			char[] label     = label_.toCharArray();
			
			
			
			
			DPoint3 to;
			if (npoints == 0)
			{
				to = new DPoint3(p2);
			}
			else
			{
				to = new DPoint3(p1to);
				to.transform(transform);
			}
			if (p1.x == to.x && p1.y == to.y)
			{
				to.x++;
			}

			double center_x  = (p1.x + to.x) / 2.0;
			double center_y  = (p1.y + to.y) / 2.0;
			
			double theta     = Math.atan2(-(to.y - p1.y), to.x - p1.x);
			
			int x             = (int) center_x;
			int y             = (int) center_y;
			// change x and y to center the text...
			
//			FontMetrics fm  = graphics.getFontMetrics();

			//the lower id first higher is second, in the data.Changes
			// that is, if the x coord of the lower node.id, is less than for the higher node.id:
			//   put the first site in front of the string, and 2nd after
			//FIXME the PostScript and PICT output does not yet print the site info
			// ALSO, we might want to turn this on/off as an option?
			int tid = tail_.getId();
			int hid = head_.getId();
			double tx = p1.x;
			double hx = p2.x;
			String changes = (String)this.data_.get("Changes");
			char lowerChar = ' ';
			char upperChar = ' ';
			String myLabel = "";
			if (displayEdgeLabels) {
				myLabel = label_;
			}
			if (changes.length() != 0 && displayEdgeLabels){
				
				lowerChar = changes.charAt(0);
				upperChar = changes.charAt(2);
			
//				String myLabel = label_;
				if (tid < hid) { //tailid < headid
					myLabel = ((tx<=hx)?lowerChar:upperChar) + "-" + label_ + "-" + ((tx<=hx)?upperChar:lowerChar);
				} else {
					myLabel = ((tx<=hx)?upperChar:lowerChar)  + "-" + label_ + "-" + ((tx<=hx)?lowerChar:upperChar);
				}
			}
			
			
			graphics.setFont(canvas.getFont(false));
			int label_w        = graphics.getFontMetrics().stringWidth(myLabel);
//			int label_h        = fm.getHeight();

			double cos_theta   = Math.cos(theta);
			double sin_theta   = Math.sin(theta);
			
//			double cx = x + (-sin_theta * label_w / 2.0);
//			double cy = y + (-cos_theta * label_w / 2.0);
			double cy = y;
	    double cx = x - label_w/2.0;
	    
			if (cos_theta < 0.0)
			{
				theta += Math.PI;
//				cos_theta = -cos_theta;
//				label_w = -label_w;
			}
			
			

	    java.awt.Graphics2D g = (java.awt.Graphics2D)graphics;
			
//			AffineTransform at = new AffineTransform();
//	    at.setToRotation(-theta,x,y);
//	    
//	    System.out.print("printing \"" + label_ + "\" at " + x + "," +y);
//	    System.out.println(" theta = " + theta +"centered at " + cx + "," +cy + " with labelwidth = " + label_w);
//	    
//	    
//	    g.setTransform(at);
	    g.rotate(-theta,x,y);
			
			
			// otherwise, do it the other way...
	    g.drawString(myLabel, (int)cx, (int)cy);
			
//			canvas.drawRotatedText(label_, theta, (int) center_x, (int) center_y, graphics, which_gr);
	    
	    
		}

	}


	private void drawArrow_(Graphics graphics, DPoint3 p1, DPoint3 p2)
	{
		double dx          = p1.x - p2.x;
		double dy          = p1.y - p2.y;

		double length      = Math.sqrt(dx * dx + dy * dy);

		DPoint p3          = new DPoint(p2.x, p2.y);

		double arrow_size  = 6;

		// Move 5 pixels back.
		p3.x += arrow_size / length * dx;
		p3.y += arrow_size / length * dy;

		DPoint p4          = new DPoint(p3.x, p3.y);

		// Out 4 pixels.
		p4.x += arrow_size * .7 / length * dy;
		p4.y -= arrow_size * .7 / length * dx;

		p3.x -= arrow_size * .7 / length * dy;
		p3.y += arrow_size * .7 / length * dx;

		graphics.drawLine((int) p2.x, (int) p2.y, (int) p4.x, (int) p4.y);
		graphics.drawLine((int) p2.x, (int) p2.y, (int) p3.x, (int) p3.y);
	}


	public String getLabel()
	{
		return label_;
	}


	public int getLineStyle()
	{
		return lineStyle_;
	}


	public Node head()
	{
		return head_;
	}


	public boolean isDummy()
	{
		return dummy_;
	}


	public DPoint3[] points()
	{
		return points_;
	}


	private String PSnum_(double num)
	{
		if (num > 0.0)
		{
			return String.valueOf(num) + " ";
		}
		else if (num < 0.0)
		{
			return String.valueOf(-num) + " neg ";
		}
		else
		{
			return "0 ";
		}
	}
	// Add escape characters for PostScript.

	private StringBuffer psString_(String source)
	{
		int len              = source.length();
		StringBuffer result  = new StringBuffer(len * 2);
		for (int i = 0; i < len; i++)
		{
			char chr  = source.charAt(i);
			if (chr == '(' || chr == ')' || chr == '\\')
			{
				result.append('\\');
			}
			if (chr >= 32 && chr < 128)
			{
				result.append(chr);
			}
			else
			{
				result.append("\\" + ((chr >> 6) & 7) + ((chr >> 3) & 7) + (chr & 7));
			}
		}
		return result;
	}


	public void saveState()
	{
		oldpoints_ = new DPoint3[points_.length];
		for (int i = 0; i < points_.length; i++)
		{
			oldpoints_[i] = new DPoint3(points_[i]);
		}
	}

	public static void setDefaultLabel(boolean state) {
		
		displayEdgeLabels = state;
	}

	public void setGMLvalues(GMLobject gml)
	{
		gml.setValue("target", GMLobject.GMLinteger, new Integer(head_.getIdObject().intValue()));
		gml.setValue("source", GMLobject.GMLinteger, new Integer(tail_.getIdObject().intValue()));

		gml.setValue("data", GMLobject.GMLlist, null);
		Enumeration keys  = data_.keys();
		while (keys.hasMoreElements())
		{
			String key;
			String value;
			key = (String) keys.nextElement();
			value = (String) data_.get(key);
			if (value != null && value.length() != 0)
			{
				String datakey  = "data." + key;
				gml.setValue(datakey, GMLobject.GMLstring, value);
			}
		}

		if (points_.length > 0)
		{
			GMLobject edgegraphics  = new GMLobject("graphics", GMLobject.GMLlist);
			gml.addObjectToEnd(edgegraphics);
			int length              = points_.length;
			for (int pt = 0; pt < length; pt++)
			{
				GMLobject point  = new GMLobject("Point", GMLobject.GMLlist);
				edgegraphics.addObjectToEnd(point);
				point.setValue("z", GMLobject.GMLreal, new Double(points_[pt].z));
				point.setValue("y", GMLobject.GMLreal, new Double(points_[pt].y));
				point.setValue("x", GMLobject.GMLreal, new Double(points_[pt].x));
			}
		}

		gml.setValue("label", GMLobject.GMLstring, label_);

		gml.setValue("linestyle", GMLobject.GMLstring, styleNames[lineStyle_]);
	}


	public void setLabel(String label)
	{
		label_ = label;
	}


	public void setLineStyle(int line_style)
	{
		lineStyle_ = line_style;
	}


	public void slide(Matrix44 moveTransform, Matrix44 viewTransform, int xoffs, int yoffs)
	{
		if (oldpoints_ == null)
		{
			return;
		}

		for (int i = 0; i < points_.length; i++)
		{
			points_[i].move(oldpoints_[i]);
			points_[i].transform(viewTransform);  // Transform to screen coordinates.
			points_[i].x += xoffs;
			points_[i].y += yoffs;
			points_[i].transform(moveTransform);  // Transform to graph coordinates.
		}
	}


	public Node tail()
	{
		return tail_;
	}


	public String toPS(Matrix44 transform, boolean inplane, boolean directed)
	{
		double scale   = transform.scale;

		String result  = new String();

		int npoints    = points_.length;

		DPoint3 p1to;

		DPoint3 p2to;
		if (npoints == 0)
		{
			p1to = head_.getPosition3();
			p2to = tail_.getPosition3();
		}
		else
		{
			p1to = points_[0];
			p2to = points_[npoints - 1];
		}

		DPoint3 p1     = tail_.intersectWithLineTo(p1to, inplane, 2);
		DPoint3 p2     = head_.intersectWithLineTo(p2to, inplane, 2);

		p1.transform(transform);
		p2.transform(transform);

		// Self edge with no intermediate points.
		if (head_ == tail_ && npoints == 0)
		{
			p2.x = p1.x + 1;
			p2.y = p1.y;
		}
		//code I won't need

		result += PSnum_(p1.x) + PSnum_(p1.y) + "moveto\n";
		DPoint3 point  = new DPoint3();
		for (int i = 0; i < npoints; i++)
		{
			point.move(points_[i]);
			point.transform(transform);
			result += PSnum_(point.x) + PSnum_(point.y) + "lineto\n";
		}
		result += PSnum_(p2.x) + PSnum_(p2.y) + "lineto\n";
		result += stylePatterns[lineStyle_] + " setdash\n";
		result += "stroke\n";
		result += "[1 0 0 0] 0 setdash\n";

		// Draw label.
		if (label_ != null && label_.length() > 0)
		{
			DPoint3 to;
			if (npoints == 0)
			{
				to = new DPoint3(p2);
			}
			else
			{
				to = new DPoint3(p1to);
				to.transform(transform);
			}
			if (p1.x == to.x && p1.y == to.y)
			{
				to.x++;
			}

			double center_x  = (p1.x + to.x) / 2.0;
			double center_y  = (p1.y + to.y) / 2.0;

			double dx        = to.x - p1.x;
			double dy        = to.y - p1.y;

			double angle     = Math.atan2(dx, dy);

			angle = angle * 180.0 / Math.PI - 90.0;
			if (Math.abs(angle) > 90.0)
			{
				angle += 180;
			}
			//change this
			result += "(" + psString_(label_) + ") " + center_x + " " + center_y + " " + angle + " slantlabel\n";
		}

		// Draw arrow.
		if (directed)
		{
			DPoint3 from  = new DPoint3(p2to);
			from.transform(transform);
			if ((int) from.x == (int) p2.x && (int) from.y == (int) p2.y)
			{
				from.x -= 10.0;
			}
			//make some kind of arrow thing?
			result += arrowPS_(from, p2);
		}
		result += "\n";

		return result;
	}

///////////////////////////this is jakes code beneath this point////
	public void toPict(Matrix44 transform, boolean inplane, boolean directed, RandomAccessFile out_, double minx, double miny)
	{
		double scale  = transform.scale;
		double x      = 0;
		double y      = 0;
		double x2     = 0;
		double y2     = 0;
		//String result = new String();

		int npoints   = points_.length;

		DPoint3 p1to;

		DPoint3 p2to;
		if (npoints == 0)
		{
			p1to = head_.getPosition3();
			p2to = tail_.getPosition3();
		}
		else
		{
			p1to = points_[0];
			p2to = points_[npoints - 1];
		}

		DPoint3 p1    = tail_.intersectWithLineTo(p1to, inplane, 2);
		DPoint3 p2    = head_.intersectWithLineTo(p2to, inplane, 2);

		//p1.transform(transform);
		//p2.transform(transform);

		// Self edge with no intermediate points.
		if (head_ == tail_ && npoints == 0)
		{
			p2.x = p1.x + 1;
			p2.y = p1.y;
		}
		x = (p1.x + minx);
		x2 = (p2.x + minx);
		p2.x += minx;
		y = (p1.y + miny);
		y2 = (p2.y + miny);
		p2.y += miny;
		drawline((short) x, (short) y, (short) x2, (short) y2, out_);

		if (label_ != null && label_.length() > 0)
		{

			double tx    = (x + x2) / 2.0;
			double ty    = (y + y2) / 2.0;

			double len   = label_.length();
			double len2  = (len * 5) / 2;

			tx += len2;
			ty += 2;

			if ((label_.length() > 0) && (label_ != "null"))
			{
				drawtext((int) tx, (int) ty, (int) len, out_);
			}

		}

		// Draw arrow. not done yet need example
		if (directed)
		{
			DPoint3 from  = new DPoint3(p2to);
			//from.transform(transform);
			if ((int) from.x == (int) p2.x && (int) from.y == (int) p2.y)
			{
				from.x -= 10.0;
			}
			from.x += minx;
			from.y += miny;
			short len     = 0;
			int lab;
			lab = label_.length();
			len = (short) lab;
			if (len > 0)
			{
				drawtext((int) from.x, (int) from.y, len, out_);
			}
			arrowPict(from, p2, out_);
			//make some kind of arrow thing?
			//result += arrowPS_(from, p2);
		}
		//result += "\n";

		return;
	}


	private void arrowPict(DPoint3 p1, DPoint3 p2, RandomAccessFile out_)
	{
		double dx      = p1.x - p2.x;
		double dy      = p1.y - p2.y;
		double length  = Math.sqrt(dx * dx + dy * dy);
		dx /= length;
		dy /= length;

		drawline((int) p2.x, (int) p2.y, (int) dx, (int) dy, out_);
		//return PSnum_(p2.x) + PSnum_(p2.y) +	PSnum_(dx) + PSnum_(dy) + "arrow\n";
	}


	public void drawline(int xa, int ya, int xb, int yb, RandomAccessFile out_)
	{
		short x1  = (short) xa;
		short x2  = (short) xb;
		short y1  = (short) ya;
		short y2  = (short) yb;
		try
		{
			out_.writeShort(0x0020);  //draw line opcode
			out_.writeShort(y1);
			out_.writeShort(x1);
			out_.writeShort(y2);
			out_.writeShort(x2);
		}
		catch (IOException f)
		{
			f.printStackTrace();
		}

	}


	public void drawtext(int x1, int y1, int len1, RandomAccessFile out_)
	{
		String label  = label_;
		short x       = (short) x1;
		short y       = (short) y1;
		short len     = (short) len1;
		short first   = 0;
		short middle  = 0;
		short letter  = 0;
		char let;
		int place     = 0;
		int temp1     = 0;
		try
		{

			out_.writeShort(0x000d);  //write out text size
			out_.writeShort(0x0008);

			out_.writeShort(0x0028);  //write text opcode
			out_.writeShort(y);
			out_.writeShort(x);  //5+.2

			first = len;
			first = (short) (first << 8);  //length of text

			let = label.charAt(place);  //calc
			place++;
			letter = (short) let;
			first = (short) (first + let);

			out_.writeShort(first);
			while (place < len)
			{
				let = label.charAt(place);
				place++;
				middle = (short) let;
				middle = (short) (middle << 8);
				if (place != len)
				{
					let = label.charAt(place);
					place++;
					middle = (short) (middle + (short) let);
				}
				out_.writeShort(middle);
			}
		}
		catch (IOException f)
		{
			f.printStackTrace();
		}
	}

}

