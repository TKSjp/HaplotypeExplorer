package EDU.auburn.VGJ.graph;

/*
 * File: Node.java
 *
 * 5/25/96   Larry Barowski
 *
*/
//version pass jake pict 02/07/01

import java.awt.Graphics;
import java.awt.Frame;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;
import java.awt.MediaTracker;
import java.awt.Component;
import java.lang.Math;
import java.net.URL;
import java.util.Hashtable;
import java.util.Enumeration;

import EDU.auburn.VGJ.util.DPoint;
import EDU.auburn.VGJ.util.DPoint3;
import EDU.auburn.VGJ.util.DDimension;
import EDU.auburn.VGJ.util.DDimension3;
import EDU.auburn.VGJ.util.Matrix44;

import java.io.FileOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import java.lang.System;


/**
 *	A Node class for use in a Graph, and for display.
 *	</p>Here is the <a href="../graph/Node.java">source</a>.
 *
 * @author     Larry Barowski
 * @created    July 2, 2003
 */
public class Node implements Cloneable, ImageObserver
{

	public final static int OVAL            = 0;
	public final static int RECTANGLE       = 1;

	public final static int BELOW           = 0;
	public final static int IN              = 1;
	public final static int CENTER          = 2;

	public final static String shapeNames[]   = {"Oval", "Rectangle", null};

	private static boolean defaultLabel_    = true;

	private static Toolkit toolkit_         = null;
	private static URL context_             = null;

	private static char hexchars_[]           = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

	/**
	 * Just change this list to change the data types.
	 */
	public static String defaultDataTypes_[]  = {"Frequency", "Weight", "Sequence"};

	/**
	 * The default node. Created nodes are initialized with the fields
	 * of this node. Default attributes can be changed by modifying it,
	 * in the same way those attributes are changed for other nodes.</p>
	 *
	 * For Example: If  <b>Node.defaults.setPosition(1.0, 1.0)</b>  is called,
	 * (1.0, 1.0) becomes the default position for all Nodes created
	 * thereafter.</p>
	 *
	 * Do not assign defaults to null, but it can be assigned to another Node.</p>
	 */
	public static Node defaults             = new Node(1);
	private Set adjacencies_;

	protected double x_, y_, z_;
	protected DPoint3 oldpos_               = null, grouppos_               = null;
	protected DDimension3 oldbox_           = null, groupbox_           = null;
	private double movingZ_;

	protected double width_, height_, depth_;

	private int shape_;

	private String[] label_;
	private String[] label2_;
	private int labelPosition_;

	private String imageLocation_, imageType_;
	private Image image_;

	private double temp_;

	protected boolean isDummy_;

	protected int index_;
	private boolean selected_;

	protected int id_;
	protected boolean haveId_;
	private boolean imageChange_;

	protected int groupNodeId_;
	protected Node groupNode_;
	protected boolean isGroup_              = false;
	protected boolean groupActive_          = false;
	protected boolean inActiveGroup_        = false;

	/**
	 * A general purpose data field.
	 * Algorithms that operate on Nodes can store any necessary data here.
	 */
	public Object data;
	public Hashtable data_;



	/**
	 *	Create a Node with default attributes.
	 */
	public Node()
	{
		index_ = -1;
		copyAttributes(defaults);
		adjacencies_ = new Set();
	}
	// Create the default node.

	private Node(int unused)
	{
		x_ = 0.0;
		y_ = 0.0;
		z_ = 0.0;
		temp_ = 0.0;
		width_ = 1.0;
		height_ = 1.0;
		depth_ = 0.0;
		shape_ = OVAL;
		label_ = new String[0];
		labelPosition_ = BELOW;
		imageLocation_ = new String("");
		imageType_ = new String("");
		image_ = null;
		isDummy_ = false;
		selected_ = false;
		haveId_ = false;
		isGroup_ = false;
		inActiveGroup_ = false;
		data_ = new Hashtable((int) ((defaultDataTypes_.length + 1) * 1.5));
		for (int i = 0; i < defaultDataTypes_.length; i++)
		{
			data_.put(defaultDataTypes_[i], "");
		}
	}
	
	public String getSequence(){
			String result = new String(((String)data_.get("Sequence")).substring(11));
			return result;
	}
	// Create the Node from a GML object.
	// Fields of type GMLinteger that are expected to be GMLobject.GMLreal will be altered so that
	// they are GMLobject.GMLreal (and the value retained).
	public Node(GMLobject gml)
	{
		boolean gotw           = false;
		boolean goth           = false;

		// Set default attributes.
		index_ = -1;
		copyAttributes(defaults);
		adjacencies_ = new Set();

		// Read attributes from GML
		GMLobject graphics;
		if ((graphics = gml.getGMLSubObject("graphics", GMLobject.GMLlist, false)) != null)
		{
			Double tmp;
			GMLobject center;
			if ((center = graphics.getGMLSubObject("center", GMLobject.GMLlist, false)) != null)
			{
				if ((tmp = (Double) center.getValue("x", GMLobject.GMLreal)) != null)
				{
					x_ = tmp.doubleValue();
				}
				if ((tmp = (Double) center.getValue("y", GMLobject.GMLreal)) != null)
				{
					y_ = tmp.doubleValue();
				}
				if ((tmp = (Double) center.getValue("z", GMLobject.GMLreal)) != null)
				{
					z_ = tmp.doubleValue();
				}
			}

			if ((tmp = (Double) graphics.getValue("width", GMLobject.GMLreal)) != null)
			{
				width_ = tmp.doubleValue();
				gotw = true;
			}
			if ((tmp = (Double) graphics.getValue("height", GMLobject.GMLreal)) != null)
			{
				height_ = tmp.doubleValue();
				goth = true;
			}
			if ((tmp = (Double) graphics.getValue("depth", GMLobject.GMLreal)) != null)
			{
				depth_ = tmp.doubleValue();
			}

			String image_loc;
			if ((image_loc = (String) graphics.getValue("Image.Location", GMLobject.GMLstring)) != null)
			{
				imageLocation_ = image_loc;
				String image_type;
				if ((image_type = (String) graphics.getValue("Image.Type", GMLobject.GMLstring)) != null)
				{
					imageType_ = image_type;
				}
				else
				{
					imageType_ = "URL";
				}
			}

		}

		String label;
		if ((label = (String) gml.getValue("label", GMLobject.GMLstring)) != null)
		{
			setLabel(label);
		}

		Integer id;
		if ((id = (Integer) gml.getValue("id", GMLobject.GMLinteger)) != null)
		{
			haveId_ = true;
			id_ = id.intValue();
		}

		String shape;
		if ((shape = (String) gml.getValue("vgj.shape", GMLobject.GMLstring)) != null)
		{
			int i;
			for (i = 0; shapeNames[i] != null; i++)
			{
				if (shape.equalsIgnoreCase(shapeNames[i]))
				{
					shape_ = i;
				}
			}
		}

		Integer tmpint;
		if ((tmpint = (Integer) gml.getValue("vgj.group", GMLobject.GMLinteger)) != null)
		{
			groupNodeId_ = tmpint.intValue();
			groupNode_ = this;
		}

		Double temp;
		if ((temp = (Double) gml.getValue("vgj.Temperature", GMLobject.GMLreal)) != null)
		{
			temp_ = temp.doubleValue();
		}

		String label_position;
		if ((label_position = (String) gml.getValue("vgj.labelPosition", GMLobject.GMLstring)) != null)
		{
			if (label_position.length() > 0)
			{
				if (label_position.charAt(0) == 'i' ||
						label_position.charAt(0) == 'I')
				{
					labelPosition_ = IN;
				}
				else if (label_position.charAt(0) == 'c' ||
						label_position.charAt(0) == 'C')
				{
					labelPosition_ = CENTER;
				}
			}
			else
			{
				labelPosition_ = BELOW;
			}
		}

		gml.setHashFromGML("data", GMLobject.GMLstring, data_);

		setImage(null, !gotw, !goth);
	}


	/**
	 *	Create a Node with default attributes, that may
	 *    be a dummy node. Graph.dummysToEdgePaths() will
	 *    convert the dummy nodes to edge paths.
	 *
	 * @param  dummy  Description of the Parameter
	 */
	public Node(boolean dummy)
	{
		index_ = -1;
		copyAttributes(defaults);
		adjacencies_ = new Set();
		isDummy_ = dummy;
		if (dummy)
		{
			width_ = height_ = 0.0;
		}
	}


	public static boolean getDefaultLabel()
	{
		return defaultLabel_;
	}


	public static int[] getImagePixels(Image image, int wd, int ht)
	{
		if (image == null)
		{
			return null;
		}

		int[] pixels          = new int[ht * wd];
		PixelGrabber grabber  = new PixelGrabber(image, 0, 0, wd, ht, pixels, 0, wd);
		boolean stat;
		try
		{
			stat = grabber.grabPixels(2000);
		}
		catch (InterruptedException e)
		{
			stat = false;
		}

		if (!stat)
		{
			return null;
		}
		return pixels;
	}

//////pretty sure that I wont need it to convert topict
	public static String imagePS(Image image)
	{
		StringBuffer result  = new StringBuffer(30);

		result.append("   < ");

		int ht               = image.getHeight(null);
		int wd               = image.getWidth(null);
		int[] pixels         = getImagePixels(image, wd, ht);
		if (pixels != null)
		{
			result.ensureCapacity(ht * wd * 4 + 30);  //string of pixel format
			int count  = 0;
			int chr    = 0;
			for (int row = 0; row < ht; row++)
			{
				for (int col = 0; col < wd; col++)
				{
					if ((count % 20) == 0 && count != 0)
					{
						result.append("\n     ");
					}
					int pixel  = pixels[count++];
					if ((pixel & 0xFF000000) == 0)
					{  // Clear
						pixel = 0xFF;
					}  // White
					else
					{
						pixel = (int) (.299 * ((pixel >> 16) & 0xFF) + .587 * ((pixel >> 8) & 0xFF) + .114 * (pixel & 0xFF));
					}  //int of the pixel format

					int cbyte  = (pixel >> 4) & 0xF;
					result.append(hexchars_[cbyte]);  //hexof the string
					cbyte = pixel & 0xF;
					result.append(hexchars_[cbyte]).append(' ');  //hex of the string
				}
			}
		}
		else
		{  // Error getting pixels.
			result.append("ff");  // All white.
		}

		result.append(">\n");
		result.append("   ").append(wd).append(" ").append(ht).append("\n");

		return result.toString();
	}


	public static void setContext(URL context)
	{
		context_ = context;
	}


	public static void setDefaultLabel(boolean use_default)
	{
		defaultLabel_ = use_default;
	}


	public static void setToolkit(Toolkit toolkit)
	{
		toolkit_ = toolkit;
	}


	public void clearChild(int child)
	{
		adjacencies_.removeElement(child);
	}


	public Object clone()
		throws java.lang.CloneNotSupportedException
	{
		Node copy;

		copy = (Node) super.clone();
		copy.adjacencies_ = (Set) adjacencies_.clone();

		copy.data_ = (Hashtable) data_.clone();

		return copy;
	}


	/**
	 * Copy attributes from another Node. This will not change the children or
	 * the data.
	 *
	 * @param  node_to_copy  Description of the Parameter
	 */
	public void copyAttributes(Node node_to_copy)
	{
		x_ = node_to_copy.x_;
		y_ = node_to_copy.y_;
		z_ = node_to_copy.z_;

		width_ = node_to_copy.width_;
		height_ = node_to_copy.height_;
		depth_ = node_to_copy.depth_;

		shape_ = node_to_copy.shape_;

		temp_ = node_to_copy.temp_;

		isDummy_ = node_to_copy.isDummy_;

		int rows  = node_to_copy.label_.length;
		label_ = new String[rows];
		for (int i = 0; i < rows; i++)
		{
			//label_[i] = new String(node_to_copy.label_[i]);
			//label2_[i] = new String(node_to_copy.label_[i]);
		}
		labelPosition_ = node_to_copy.labelPosition_;

		selected_ = node_to_copy.selected_;

		imageType_ = new String(node_to_copy.imageType_);
		imageLocation_ = new String(node_to_copy.imageLocation_);
		setImage(null, false, false);

		data_ = (Hashtable) node_to_copy.data_.clone();
	}


	public void draw(Component comp, Graphics graphics, Matrix44 transform, int quality)
	{

		if ((isGroup_ && !groupActive_) || inActiveGroup_)
		{
			return;
		}

		double scale      = transform.scale;

		// imageChange_ is used to make sure height and width don't change
		// after this assignment.
		imageChange_ = false;
		boolean errflag   = false;

		Image image       = image_;
		if (quality == 0)
		{
			image = null;
		}

		// We scale the bounds here.
		scaleBounds_(graphics, scale);

		double w          = width_ * scale;
		double h          = height_ * scale;

		if (w < 1)
		{
			w = 1;
		}
		if (h < 1)
		{
			h = 1;
		}

		DPoint3 position  = new DPoint3(x_, y_, z_);
		position.transform(transform);

		int x             = (int) position.x;
		int y             = (int) position.y;

		// float temp = (float)(.6 * (1.0 - temp_));

		//	graphics.setColor(new Color(Color.HSBtoRGB((float)temp, (float)1.0, (float)1.0)));

		graphics.setColor(Color.black);

		if (image != null)
		{
			MediaTracker mt  = new MediaTracker(comp);
			mt.addImage(image_, 0, (int) w, (int) h);
			boolean stat     = false;
			try
			{
				stat = mt.waitForID(0, 1000);
			}
			catch (Exception e)
			{
			}
			if (stat && !mt.isErrorID(0))
			{
				if (imageChange_)
				{
					w = width_ * scale;
					h = height_ * scale;

					if (w < 1)
					{
						w = 1;
					}
					if (h < 1)
					{
						h = 1;
					}

					mt.addImage(image_, 1, (int) w, (int) h);
					try
					{
						stat = mt.waitForID(1, 1000);
					}
					catch (Exception e)
					{
					}
				}
				if (stat)
				{
					graphics.drawImage(image_, (int) (x - w / 2), (int) (y - h / 2), (int) w, (int) h, null);
				}
				else
				{
					errflag = true;
				}
			}
			else
			{
				errflag = true;
			}

			if (errflag)
			{
				String error;
				String type     = "URL";
				if (imageType_.equalsIgnoreCase("file"))
				{
					type = "file";
				}
				if (mt.isErrorID(0))
				{
					error = "Error loading or scaling " + type + " \"" + imageLocation_ + "\".";
				}
				else
				{
					error = "Timeout loading image - try a refresh.";
				}
				FontMetrics fm  = graphics.getFontMetrics();
				double lx       = x - fm.stringWidth(error) / 2.0;
				graphics.drawString(error, (int) lx, (int) (y + h / 2));
			}
		}
		else if (shape_ == OVAL)
		{
			graphics.drawOval((int) (x - w / 2), (int) (y - h / 2), (int) w, (int) h);
			if (isGroup_ && w > 4 && h > 4)
			{
				graphics.drawOval((int) (x - w / 2 + 2), (int) (y - h / 2 + 2), (int) w - 4, (int) h - 4);
			}
		}
		else if (shape_ == RECTANGLE)
		{
			graphics.drawRect((int) (x - w / 2), (int) (y - h / 2), (int) w, (int) h);
			if (isGroup_ && w > 4 && h > 4)
			{
				graphics.drawRect((int) (x - w / 2 + 2), (int) (y - h / 2 + 2), (int) w - 4, (int) h - 4);
			}
		}

		if ((defaultLabel_ || label_.length > 0) && quality > 0)
		{
			FontMetrics fm  = graphics.getFontMetrics();

			double widest   = 0.0;
			if (label_.length > 0)
			{
				for (int i = 0; i < label_.length; i++)
				{
					if (fm.stringWidth(label_[i]) > widest)
					{
						widest = fm.stringWidth(label_[i]);
					}
				}
			}
			else
			{
				widest = fm.stringWidth(new String("Node " + id_));
			}

			double lx;

			double ly;

			lx = x - widest / 2.0;

			if (labelPosition_ != BELOW)
			{
				if (label_.length > 0)
				{
					ly = y - fm.getHeight() * (label_.length - 2) / 2.0;
				}
				else
				{
					ly = y + fm.getHeight() / 2.0;
				}
				ly -= fm.getDescent();
			}
			else
			{
				ly = y + h / 2.0 + fm.getAscent() + 1.0;
			}

			if (label_.length == 0)
			{
				graphics.drawString(new String("Node " + id_), (int) lx, (int) ly);
			}
			else
			{
				for (int i = 0; i < label_.length; i++)
				{
//					graphics.drawString(label_[i], (int) lx, (int) ly);
				    java.awt.Graphics2D g = (java.awt.Graphics2D)graphics;
				    g.drawString(label_[i], (int) lx, (int) ly);
					ly += fm.getHeight();
				}
			}
		}

		if (!selected_)
		{
			return;
		}

		graphics.setColor(Color.red);

		// Draw selection components.
		DPoint tr         = new DPoint(position.x + width_ * scale / 2.0 + 1, position.y - height_ * scale / 2.0 - 1);
		DPoint bl         = new DPoint(position.x - width_ * scale / 2.0 - 1, position.y + height_ * scale / 2.0 + 1);

		graphics.drawRect((int) position.x - 4, (int) position.y - 1, 8, 2);
		graphics.drawRect((int) position.x - 1, (int) position.y - 4, 2, 8);

		graphics.drawRect((int) tr.x - 2, (int) tr.y - 2, 4, 4);

		graphics.drawRect((int) position.x - 3, (int) bl.y - 1, 6, 2);
		graphics.drawRect((int) bl.x - 1, (int) position.y - 3, 2, 6);

		graphics.setColor(Color.white);
		graphics.drawLine((int) position.x - 3, (int) position.y, (int) position.x + 3, (int) position.y);
		graphics.drawLine((int) position.x, (int) position.y - 3, (int) position.x, (int) position.y + 3);
		graphics.drawRect((int) tr.x - 1, (int) tr.y - 1, 2, 2);
		graphics.drawLine((int) position.x - 2, (int) bl.y, (int) position.x + 2, (int) bl.y);
		graphics.drawLine((int) bl.x, (int) position.y - 2, (int) bl.x, (int) position.y + 2);
	}


	/**
	 *  Returns the index of the lowest numbered child, -1 if there are no
	 * children.</p>
	 * This resets the iterator nextChild()</a>.
	 *
	 * @return    Description of the Return Value
	 */
	public int firstChild()
	{
		return adjacencies_.first();
	}


	public DDimension getBoundingBox()
	{
		DDimension bbox  = new DDimension(width_, height_);
		return bbox;
	}


	public DDimension3 getBoundingBox3()
	{
		DDimension3 bbox  = new DDimension3(width_, height_, depth_);
		return bbox;
	}


	public Set getChildren()
	{
		return (Set) adjacencies_.clone();
	}
	// Get the drawn bounds of the Node.

	public void getDrawBounds_(double scale, Matrix44 transform, DPoint width, DPoint height)
	{
		double w          = width_ * scale;
		double h          = height_ * scale;

		if (w < 1)
		{
			w = 1;
		}
		if (h < 1)
		{
			h = 1;
		}

		DPoint3 position  = new DPoint3(x_, y_, z_);
		position.transform(transform);

		width.x = position.x - w / 2.0;
		width.y = position.x + w / 2.0;
		height.x = position.y - h / 2.0;
		height.y = position.y + h / 2.0;
	}


	public int getId()
	{
		return id_;
	}


	public Integer getIdObject()
	{
		if (!haveId_)
		{
			return null;
		}
		return new Integer(id_);
	}


	public Image getImage()
	{
		return image_;
	}


	public String getImageSource()
	{
		return imageLocation_;
	}


	public String getImageType()
	{
		return imageType_;
	}


	public int getIndex()
	{
		return index_;
	}


	public String getLabel()
	{
		if (label_.length > 0)
		{
			int len              = 0;
			int i;
			for (i = 0; i < label_.length; i++)
			{
				len += label_[i].length() + 2;
			}
			StringBuffer result  = new StringBuffer(len);
			for (i = 0; i < label_.length; i++)
			{
				result.append(label_[i]);
				if (i < label_.length - 1)
				{
					result.append("\\n");
				}
			}

			return new String(result);
		}
		else
		{
			return new String("");
		}
	}


	public int getLabelPosition()
	{
		return labelPosition_;
	}


	public DPoint getPosition()
	{
		DPoint position  = new DPoint(x_, y_);
		return position;
	}


	public DPoint3 getPosition3()
	{
		DPoint3 position;
		if (inActiveGroup_)
		{
			return groupNode_.getPosition3();
		}

		position = new DPoint3(x_, y_, z_);
		return position;
	}


	public boolean getSelected()
	{
		return selected_;
	}


	public int getShape()
	{
		return shape_;
	}


	public double getTemp()
	{
		return temp_;
	}


	public Node getVisibleGroupRoot()
	{
		Node node;
		for (node = this; node.inActiveGroup_; node = node.groupNode_)
		{
			;
		}
		return node;
	}


	public boolean groupActive()
	{
		return groupActive_;
	}


	public boolean hasChild(int child)
	{
		return adjacencies_.isElement(child);
	}


	public boolean hasChild(Node child)
	{
		return adjacencies_.isElement(child.index_);
	}


	public boolean imageUpdate(Image image, int info, int x, int y, int w, int h)
	{
		if ((info & ImageObserver.HEIGHT) != 0)
		{
			height_ = h;
			imageChange_ = true;
		}
		if ((info & ImageObserver.WIDTH) != 0)
		{
			width_ = w;
			imageChange_ = true;
		}
		return true;
	}


	public boolean inGroup()
	{
		return (groupNode_ != null);
	}


	public DPoint3 intersectWithLineTo(DPoint3 to, boolean inplane, int quality)
	{
		if (inActiveGroup_)
		{
			return groupNode_.intersectWithLineTo(to, inplane, quality);
		}

		if (isDummy_)
		{
			return new DPoint3(x_, y_, z_);
		}

		if (width_ == 0.0 || height_ == 0.0)
		{
			return new DPoint3(x_, y_, z_);
		}

		if (!inplane || z_ != to.z)
		{  // For now, assume spherical.

			double mindim  = Math.min(width_, height_) / 2.0;
			double dist    = Math.sqrt((to.x - x_) * (to.x - x_) + (to.y - y_) * (to.y - y_) + (to.z - z_) * (to.z - z_));
			if (dist <= mindim)
			{
				return new DPoint3(x_, y_, z_);
			}
			double ratio   = mindim / dist;
			return new DPoint3(x_ + (to.x - x_) * ratio, y_ + (to.y - y_) * ratio, z_ + (to.z - z_) * ratio);
		}

		double dy     = to.y - y_;
		double dx     = to.x - x_;

		// If point is inside the node, return the centerpoint.
		if (dx * dx / (width_ * width_) + dy * dy / (height_ * height_)
				 <= .25)
		{
			return new DPoint3(x_, y_, z_);
		}

		if (image_ != null && quality > 1)
		{
			int ht        = image_.getHeight(null);
			int wd        = image_.getWidth(null);
			int[] pixels  = getImagePixels(image_, wd, ht);
			if (pixels != null)
			{
				double t       = 0.0;
				double wmul    = (double) (to.x < x_ ? 1 : -1);
				double hmul    = (double) (to.y < y_ ? 1 : -1);
				double xint;
				double yint;
				double pixelw  = width_ / (double) wd;
				double pixelh  = height_ / (double) ht;
				double xorg    = x_ - width_ / 2.0;
				double yorg    = y_ - height_ / 2.0;
				double tx      = 0;
				double ty      = 0;
				dx = -dx;
				dy = -dy;

				boolean first  = true;
				int xpix;
				int ypix;
				int xind;
				int yind;
				tx = 0.0;
				for (xpix = 0; xpix < wd; xpix++)
				{
					xint = xorg + ((wmul < 0 ? wd : 0) + xpix * wmul) * pixelw;
					tx = (xint - to.x) / dx;
					yint = to.y + dy * tx;
					ypix = -(int) Math.floor((yint - yorg) / pixelh - ht + 1);
					if (ypix >= 0 && ypix < ht)
					{
						xind = (wmul < 0 ? wd - xpix - 1 : xpix);
						if ((pixels[ypix * wd + xind] & 0xFF000000) != 0)
						{
							break;
						}
					}
				}
				ty = 0.0;
				for (ypix = 0; ypix < ht; ypix++)
				{
					yint = yorg + ((hmul > 0 ? ht : 0) - (ht - ypix) * hmul) * pixelh;
					ty = (yint - to.y) / dy;
					xint = to.x + dx * ty;
					xpix = (int) Math.floor((xint - xorg) / pixelw);
					if (xpix >= 0 && xpix < wd)
					{
						yind = (hmul > 0 ? ht - ypix - 1 : ypix);
						if ((pixels[yind * wd + xpix] & 0xFF000000) != 0)
						{
							break;
						}
					}
				}
				t = -1.0;
				if (ty >= 0.0 && ty < 1.0 && (ty < tx || tx < 0.0))
				{
					t = ty;
				}
				else if (tx >= 0.0 && tx < 1.0)
				{
					t = tx;
				}
				if (t >= 1.0 || t < 0.0)
				{
					return new DPoint3(x_, y_, z_);
				}
				else
				{
					xint = to.x + dx * t;
					yint = to.y + dy * t;
					return new DPoint3(xint, yint, z_);
				}
			}
			else
			{
				return new DPoint3(x_, y_, z_);
			}
		}

		if (dx == 0)
		{
			if (dy > 0)
			{
				return new DPoint3(x_, y_ + height_ / 2, z_);
			}
			else
			{
				return new DPoint3(x_, y_ - height_ / 2, z_);
			}
		}
		double slope  = dy / dx;

		double x;

		double y;

		if (shape_ == OVAL)
		{
			x = width_ * height_ / (2 * Math.sqrt(height_ * height_ + slope * slope * width_ * width_));
			if (to.x < x_)
			{
				x = -x;
			}
			y = slope * x;

			return new DPoint3(x_ + x, y_ + y, z_);
		}
		else if (shape_ == RECTANGLE)
		{
			if (dx > 0)
			{  // point is to the right of node
				x = x_ + width_ / 2.0;
			}
			else
			{
				x = x_ - width_ / 2.0;
			}
			y = to.y - (to.x - x) * slope;

			if (Math.abs(y - y_) <= height_ / 2.0)
			{
				return new DPoint3(x, y, z_);
			}

			if (dy > 0)
			{  // point is below node
				y = y_ + height_ / 2.0;
			}
			else
			{
				y = y_ - height_ / 2.0;
			}

			x = to.x - (to.y - y) / slope;

			return new DPoint3(x, y, z_);
		}

		return new DPoint3(x_, y_, z_);
	}


	public boolean isGroup()
	{
		return isGroup_;
	}


	public boolean isVisible()
	{
		if (inActiveGroup_)
		{
			return false;
		}
		return !(isGroup_ && !groupActive_);
	}


	/**
	 *  Returns the index of the next child, -1 if there is none.
	 *
	 * @return    Description of the Return Value
	 */
	public int nextChild()
	{
		return adjacencies_.next();
	}


	public int numberOfChildren()
	{
		return adjacencies_.numberOfElements();
	}


	private String PSnum_(double num)
	{
		if (num > 0.0)
		{
			return String.valueOf(num) + " ";
		}
		return String.valueOf(-num) + " neg ";
	}
	// Add escape characters for PostScript. Make this part of a
	// static utility later.
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
		oldpos_ = new DPoint3(x_, y_, z_);
		oldbox_ = new DDimension3(width_, height_, depth_);
	}


	public void scale(double scalex, double scaley, double scalez)
	{
		if (oldbox_ == null)
		{
			return;
		}

		width_ = scalex * oldbox_.width;
		height_ = scaley * oldbox_.height;
		depth_ = scalez * oldbox_.depth;
	}


	private void scaleBounds_(Graphics graphics, double scale)
	{
		if (labelPosition_ == IN)
		{
			FontMetrics fm  = graphics.getFontMetrics();

			double w;

			double h;
			int i;

			if (label_.length > 0)
			{
				h = fm.getHeight() * label_.length;
				for (w = 0.0, i = 0; i < label_.length; i++)
				{
					if (fm.stringWidth(label_[i]) > w)
					{
						w = fm.stringWidth(label_[i]);
					}
				}
			}

			else
			{
				h = fm.getHeight();
				w = fm.stringWidth(new String("Node " + id_));
			}

			if (shape_ == OVAL)
			{
				w *= 1.42;
				h *= 1.42;
			}

			w += 6;
			h += 6;

			width_ = w / scale;
			height_ = h / scale;
		}
	}


	/**
	 * Return the next child with index >= n, or -1 if n
	 * is the last.
	 *
	 * @param  n  Description of the Parameter
	 * @return    Description of the Return Value
	 */
	public int searchNextChild(int n)
	{
		return adjacencies_.searchNext(n);
	}


	public void setBoundingBox(double new_width, double new_height)
	{
		width_ = new_width;
		height_ = new_height;
	}


	public void setBoundingBox(double new_width, double new_height, double new_depth)
	{
		width_ = new_width;
		height_ = new_height;
		depth_ = new_depth;
	}


	public void setBoundingBox(DDimension new_bbox)
	{
		width_ = new_bbox.width;
		height_ = new_bbox.height;
	}


	public void setBoundingBox(DDimension3 new_bbox)
	{
		width_ = new_bbox.width;
		height_ = new_bbox.height;
		depth_ = new_bbox.depth;
	}


	public void setChild(int child)
	{
		adjacencies_.includeElement(child);
	}
	// Set the GML values of a GML object to those of this Node (create new
	// fields where necessary).
	public void setGMLvalues(GMLobject gml)
	{
		if (temp_ == 0.0)
		{
			gml.setValue("vgj.Temperature", GMLobject.GMLreal, null);
		}
		else
		{
			gml.setValue("vgj.Temperature", GMLobject.GMLreal, new Double(temp_));
		}
		gml.setValue("vgj.shape", GMLobject.GMLstring, shapeNames[shape_]);
		if (inGroup())
		{
			gml.setValue("vgj.group", GMLobject.GMLinteger, new Integer(groupNode_.getId()));
		}

		if (labelPosition_ == IN)
		{
			gml.setValue("vgj.labelPosition", GMLobject.GMLstring, "in");
		}
		else if (labelPosition_ == CENTER)
		{
			gml.setValue("vgj.labelPosition", GMLobject.GMLstring, "center");
		}
		else
		{
			gml.setValue("vgj.labelPosition", GMLobject.GMLstring, "below");
		}

		gml.setValue("graphics.depth", GMLobject.GMLreal, new Double(depth_));
		gml.setValue("graphics.height", GMLobject.GMLreal, new Double(height_));
		gml.setValue("graphics.width", GMLobject.GMLreal, new Double(width_));

		gml.setValue("graphics.center.z", GMLobject.GMLreal, new Double(z_));
		gml.setValue("graphics.center.y", GMLobject.GMLreal, new Double(y_));
		gml.setValue("graphics.center.x", GMLobject.GMLreal, new Double(x_));

		gml.setValue("graphics.Image.Location", GMLobject.GMLstring, imageLocation_);
		gml.setValue("graphics.Image.Type", GMLobject.GMLstring, imageType_);

		gml.setValue("label", GMLobject.GMLstring, getLabel());
		gml.setValue("id", GMLobject.GMLinteger, new Integer(id_));

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
	}


	public void setGroup()
	{
		isGroup_ = true;
	}


	public void setId(int id)
	{
		haveId_ = true;
		id_ = id;
	}


	public void setImage(Component comp, boolean set_w, boolean set_h)
	{
		image_ = null;

		if (imageLocation_.length() <= 0)
		{
			return;
		}

		if (toolkit_ != null)
		{
			if (imageType_.equalsIgnoreCase("file"))
			{
				image_ = toolkit_.getImage(imageLocation_);
			}
			else
			{
				URL src  = null;
				try
				{
					src = new URL(context_, imageLocation_);
				}
				catch (java.net.MalformedURLException e)
				{
				}
				;
				if (src != null)
				{
					image_ = toolkit_.getImage(src);
				}
			}

			if (comp != null)
			{
				MediaTracker mt  = new MediaTracker(comp);
				mt.addImage(image_, 0);
				boolean stat     = false;
				try
				{
					stat = mt.waitForID(0, 2000);
				}
				catch (Exception e)
				{
				}
			}

			int ht;

			int wd;
			if (set_h)
			{
				height_ = -1.0;
				ht = image_.getHeight(this);
				if (ht != -1)
				{
					height_ = ht;
				}
			}
			if (set_w)
			{
				width_ = -1.0;
				wd = image_.getWidth(this);
				if (wd != -1)
				{
					width_ = wd;
				}
			}
		}

		if (image_ == null)
		{
			imageType_ = new String("");
		}
	}


	public void setImageSource(String image_source)
	{
		imageLocation_ = image_source;
	}


	public void setImageType(String image_type)
	{
		imageType_ = image_type;
	}


	public void setLabel(String label)
	{
		int rows   = 0;
		int i;
		int j;
		int k;
		int llen   = label.length();
		for (i = 0; i < llen; i++)
		{
			if (label.charAt(i) == '\\' && i < llen - 1)
			{
				i++;
				if (label.charAt(i) == 'n')
				{
					rows++;
				}
			}
		}
		if (llen > 0)
		{
			rows++;
		}

		label_ = new String[rows];

		if (llen == 0)
		{
			return;
		}

		int row    = 0;
		int start  = 0;

		for (i = 0; i <= llen; i++)
		{
			if (i == llen || (label.charAt(i) == '\\' && i < llen - 1))
			{
				if (i == llen || label.charAt(i + 1) == 'n')
				{
					char[] chars  = new char[i - start];
					for (j = start, k = 0; j < i; j++)
					{
						if (label.charAt(j) != '\\' || j == i - 1)
						{
							chars[k++] = label.charAt(j);
						}
						else
						{
							chars[k++] = label.charAt(++j);
						}
					}
					label_[row++] = new String(chars, 0, k);
					if (i < llen)
					{
						i++;
					}
					start = i + 1;
				}
				i++;
			}
		}
	}


	public void setLabelPosition(String label_position)
	{
		if (label_position.length() <= 0)
		{
			return;
		}

		if (label_position.charAt(0) == 'I')
		{
			labelPosition_ = IN;
		}
		else if (label_position.charAt(0) == 'C')
		{
			labelPosition_ = CENTER;
		}
		else
		{
			labelPosition_ = BELOW;
		}
	}


	public void setPosition(double new_x, double new_y)
	{
		x_ = new_x;
		y_ = new_y;
	}


	public void setPosition(double new_x, double new_y, double new_z)
	{
		x_ = new_x;
		y_ = new_y;
		z_ = new_z;
	}


	public void setPosition(DPoint new_position)
	{
		x_ = new_position.x;
		y_ = new_position.y;
	}


	public void setPosition(DPoint3 new_position)
	{
		x_ = new_position.x;
		y_ = new_position.y;
		z_ = new_position.z;
	}


	public void setSelected(boolean selected)
	{
		selected_ = selected;
	}


	public void setShape(int shape)
	{
		shape_ = shape;
	}


	public void setTemp(double temp)
	{
		temp_ = temp;
	}


	public void slide(Matrix44 moveTransform, Matrix44 viewTransform, int xoffs, int yoffs)
	{
		if (oldpos_ == null)
		{
			return;
		}

		DPoint3 tmppos  = new DPoint3(oldpos_);
		tmppos.transform(viewTransform);  // Transform to screen coordinates.
		tmppos.x += xoffs;
		tmppos.y += yoffs;
		tmppos.transform(moveTransform);  // Transform to graph coordinates.

		x_ = tmppos.x;
		y_ = tmppos.y;
		z_ = tmppos.z;
	}


	/**
	 * Generate PostScript code for the node.
	 *
	 * @param  transform  Description of the Parameter
	 * @return            Description of the Return Value
	 */

	//do I need to create a pict format for this?
	public String toPS(Matrix44 transform)
	{
		if (!isVisible())
		{
			return new String("");
		}

		double scale   = transform.scale;

		String result  = new String();

		double w       = width_ * scale;
		double h       = height_ * scale;

		if (w < 1)
		{
			w = 1;
		}
		if (h < 1)
		{
			h = 1;
		}

		DPoint3 pos    = new DPoint3(x_, y_, z_);
		pos.transform(transform);

		if (image_ != null)
		{
		}
		else if (shape_ == OVAL)
		{
			result += PSnum_(pos.x) + PSnum_(pos.y) + PSnum_(w) + PSnum_(h) + "ellipse\n";
			if (isGroup_ && w > 4 && h > 4)
			{
				result += PSnum_(pos.x) + PSnum_(pos.y) + PSnum_(w - 4) + PSnum_(h - 4) + "ellipse\n";
			}
		}
		else if (shape_ == RECTANGLE)
		{
			result += PSnum_(pos.x) + PSnum_(pos.y) + PSnum_(w) + PSnum_(h) + "rectangle\n";
			if (isGroup_ && w > 4 && h > 4)
			{
				result += PSnum_(pos.x) + PSnum_(pos.y) + PSnum_(w - 4) + PSnum_(h - 4) + "rectangle\n";
			}
		}

		if (defaultLabel_ || label_.length > 0)
		{
			int i;

			result += "[\n";
			if (label_.length > 0)
			{
				for (i = 0; i < label_.length; i++)
				{
					result += "  (" + psString_(label_[i]) + ")\n";
				}
			}
			else
			{
				result += ("  (Node " + id_ + ")\n");
			}
			result += "]\n";

			if (labelPosition_ == BELOW)
			{
				result += PSnum_(pos.x) + PSnum_(pos.y + h / 2.0) + "0 label\n";
			}
			else if (labelPosition_ == CENTER)
			{
				result += PSnum_(pos.x) + PSnum_(pos.y) + "1 label\n";
			}
			else
			{
				if (shape_ == OVAL)
				{
					w /= 1.42;
					h /= 1.42;
				}
				result += PSnum_(pos.x) + PSnum_(pos.y) + PSnum_(w) + PSnum_(h) + "2 label\n";
			}
		}
		result += "\n";

		return result;
	}


	//do I need to create a pict format of this?
	// Given the image number, create the PostScript for the image.
	public String toPSimage(int number, Matrix44 transform)
	{
		if (!isVisible())
		{
			return new String("");
		}

		double scale   = transform.scale;

		double w       = width_ * scale;
		double h       = height_ * scale;

		if (w < 1)
		{
			w = 1;
		}
		if (h < 1)
		{
			h = 1;
		}

		DPoint3 pos    = new DPoint3(x_, y_, z_);
		pos.transform(transform);

		String result  = PSnum_(pos.x) + PSnum_(pos.y) + PSnum_(w) + PSnum_(h) + "image" + number + " vgjimage\n\n";

		return result;
	}


	public double getX()
	{
		return x_;
	}


	public double getY()
	{
		return y_;
	}


	public double getHeight(Matrix44 transform)
	{
		double scale   = transform.scale;
		double height  = (short) (height_ * scale);
		return height;
	}


	public double getWidth(Matrix44 transform)
	{
		double scale  = transform.scale;
		double width  = (short) (width_ * scale);
		return width;
	}

	//////////////////draw oval///////////////////////
	public void drawOval(RandomAccessFile out_, Matrix44 transform, short minx, short miny)
	{  //int x1,int y1,int width1,int height1,String label)

		//short x = (short)(x_ - minx);
		//short y = (short)(y_ - miny);
		short x       = 0;
		short y       = 0;
		//short width = (short)width_;
		//short height = (short)height_;
		short len     = 0;
		//short width = (short)width_;
		//short height = (short)height_;
		short len2    = 0;
		short x2      = 0;
		short y2      = 0;
		short tx      = 0;
		short ty      = 0;
		short let     = 0x0005;
		int lab;
		String label  = null;
		////////////

		if ((label_.length == 0))
		{  //(defaultLabel_ == false) ||

//			 System.out.println("this is a dead node");
//                          System.out.println(label_);
			return;
		}

		double scale  = transform.scale;

		short width   = (short) (width_ * scale);
		short height  = (short) (height_ * scale);

		if (width < 1)
		{
			width = 1;
		}
		if (height < 1)
		{
			height = 1;
		}

		//	 DPoint3 pos = new DPoint3(x_, y_, z_);
		// pos.transform(transform);
		x = (short) (x_ - (.5 * width) + minx);
		y = (short) (y_ - (.5 * height) + miny);
		boolean test;

		//		 System.out.println(label_);
		///////////////
		if (label_.length > 0)
		{  //defaultLabel_ ||

			int i;

			if (label_.length > 0)
			{
				for (i = 0; i < label_.length; i++)
				{
					label += label_[i];
				}

				//				System.out.println("label " + label_);

				lab = label.length();
				//				System.out.println("length " + lab);
				test = label.startsWith("null");
				if (test == true)
				{
					len2 = (short) (lab - 4);
				}
				else
				{
					len2 = (short) lab;
				}

				len = (short) lab;
			}
		}

		x2 = (short) (x + width);
		y2 = (short) (y + height);

		test = label.startsWith("nullIn");

		/*
			if (test == true )
			{
			   System.out.println("this is intermediate change x2 and y2");
				 x2 = (short)(x + 15);
				 y2 = (short)(y + 12);
			}
			*/
		//this is just a try at optimizing the pict code

//			System.out.println("x1 " + x);
//			System.out.println("y1 " + y);
//			System.out.println("x2 " + x2);
//			System.out.println("y2 " + y2);

		try
		{
			if (shape_ == OVAL)
			{
				out_.writeShort(0x0050);  //frame oval	opcode
			}
			else if (shape_ == RECTANGLE)
			{
				out_.writeShort(0x0030);  //frame rectangle	opcode
			}
			else
			{
				out_.writeShort(0x0050);  //frame oval	opcode
				/*dprintln("this is strange, It is not a rect or oval");*/
			}

			out_.writeShort(y);  //center oval
			out_.writeShort(x);
			out_.writeShort(y2);
			out_.writeShort(x2);

			if ((defaultLabel_ || (label_.length > 0)) && (label != "null") && (len2 > 0))
			{
				out_.writeShort(0x000d);
				ty = (short) (y + (height / 2));  //calc the y pos of text label
				tx = (short) (x + (width / 2));
				test = label.startsWith("nullIn");
				if (test == true)
				{
					ty = (short) (ty + 0x0002);
					tx = (short) (tx - (0x0002 + (0x0008 * (len2 / 3))));  //calc the x pos of text label//jake2/20/01 key to xpos
					out_.writeShort(0x0006);
					tx = (short) (tx + 0x0006);
					/*System.out.println("Should be intermediate node: " + label);*/
				}
				else if ((len2 * 6 < width) && (height > 20))
				{
					ty = (short) (ty + 0x0004);
					tx = (short) (tx - (let * (len2 / 2)));  //calc the x pos of text label
					tx = (short) (tx - 0x0002);
					out_.writeShort(0x000c);
				}
				else if ((len2 * 5 < width) && (height > 14))
				{
					ty = (short) (ty + 0x0003);
					tx = (short) (tx - (let * (len2 / 2)));  //calc the x pos of text label
					out_.writeShort(0x000a);
				}
				else if ((len2 * 4 < width) && (height > 10))
				{
					ty = (short) (ty + 0x0002);
					tx = (short) (tx - (4 * (len2 / 2)));  //calc the x pos of text label
					out_.writeShort(0x0008);
				}
				else
				{
					ty = (short) (ty + 0x0001);
					tx = (short) (tx - (3 * (len2 / 2)));  //calc the x pos of text label
					out_.writeShort(0x0006);
					tx = (short) (tx + 0x0006);
				}
				if (len2 > 0)
				{
					drawtext((int) tx, (int) ty, (int) len, label, out_);
				}
			}
		}
		catch (IOException f)
		{
			f.printStackTrace();
		}
	}


	public void drawtext(int x1, int y1, int len1, String label, RandomAccessFile out_)
	{
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
			out_.writeShort(0x0028);  //write text opcode
			out_.writeShort(y);
			out_.writeShort(x);  //5+.2
			boolean test  = label.startsWith("null");
			if (test == true)
			{
				/*System.out.println("we got a null at the start");*/
				first = (short) (len - 4);
				first = (short) (first << 8);  //length of text
				place = place + 4;
				let = label.charAt(place);  //calc
				place++;
				letter = (short) let;
				first = (short) (first + let);
			}
			else
			{
				first = len;
				first = (short) (first << 8);  //length of text

				let = label.charAt(place);  //calc
				place++;
				letter = (short) let;
				first = (short) (first + let);
			}
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

