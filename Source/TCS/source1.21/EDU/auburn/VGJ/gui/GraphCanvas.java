package EDU.auburn.VGJ.gui;

/*
    File: GraphCanvas.java
    5/29/96   Larry Barowski
  */
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Event;
import java.awt.Point;
import java.awt.Dimension;
import java.awt.Color;
import java.util.Enumeration;
import java.awt.FontMetrics;
import java.awt.Font;
import java.awt.Frame;

import EDU.auburn.VGJ.algorithm.GraphUpdate;
import EDU.auburn.VGJ.util.DDimension;
import EDU.auburn.VGJ.util.DDimension3;
import EDU.auburn.VGJ.util.DPoint;
import EDU.auburn.VGJ.util.DPoint3;
import EDU.auburn.VGJ.util.DRect;
import EDU.auburn.VGJ.util.Matrix44;
import EDU.auburn.VGJ.graph.Graph;
import EDU.auburn.VGJ.graph.Node;
import EDU.auburn.VGJ.graph.Edge;
import EDU.auburn.VGJ.graph.NodePropertiesDialog;
import EDU.auburn.VGJ.graph.EdgePropertiesDialog;

import java.io.IOException;
import java.io.RandomAccessFile;

import java.awt.geom.AffineTransform;
import java.awt.image.*;


/**
 *	A window class for editing and displaying Graphs.
 *	</p>Here is the <a href="../gui/GraphCanvas.java">source</a>.
 *
 * @author     Larry Barowski
 * @created    June 4, 2003
 */

public class GraphCanvas extends OffsetCanvas
		 implements GraphUpdate
{
	public final static int MOUSEMOVE                    = 32451;
	// event id for mouse movement

	public final static int CREATE_NODES                 = 0;
	public final static int CREATE_EDGES                 = 1;
	public final static int SELECT_NODES                 = 2;
	public final static int SELECT_EDGES                 = 3;
	public final static int SELECT_BOTH                  = 4;

	// Event indicating that the graph has changed.
	public final static int UPDATE                       = 38792;

	private final static int aaDivs_                     = 4;

	private final static int NONE_                       = 0;
	private final static int CENTER_                     = 1;
	private final static int CORNER_                     = 2;
	private final static int BOTTOM_                     = 3;
	private final static int LEFT_                       = 4;
	// Number of pixel divisions for
	// anti-aliasing.
	private static Color[] aaShades_;

	private static NodePropertiesDialog propDialog_      = null;
	private static EdgePropertiesDialog edgePropDialog_  = null;

	private Frame frame_;

	private double width_;
	// drawable width
	private double height_;
	// drawable height

	private Dimension windowSize_                        =
			new Dimension(-1, -1);
	// window viewable size, -1 values
	// will force a change_size at start

	// the scroll offsets - screen position + offset = physical position
	private double offsetx_                              = 0, offsety_                              = 0;

	private double minx_, miny_, maxx_, maxy_;

	private double scale_                                = 1.0;

	private Graph graph_;

	private DPoint offset_                               = new DPoint(0, 0);

	private Node newEdgeNode_;
	private Node movingNode_;
	private Node selectedNode_;
	private double movingZ_, movingX_, movingY_;
	private double selectedRatio_;
	private Point selectedEdge_;

	private int selected_                                = NONE_;

	private int mouseMode_                               = SELECT_BOTH;

	private boolean scaleBounds_                         = true;

	private Matrix44 viewTransform_, moveTransform_;
	private Matrix44 scaleMatrix_, shiftMatrix_;
	private Matrix44 rotxMatrix_, rotzMatrix_;

	private boolean xyPlane_                             = true;

	private boolean _3d_                                 = false;
	private Image backImage_                             = null;
	private Font font_;
	private Font edgeFont_;
	
	private int currentMouseAction_                      = 0;

	private DPoint3 lastEdgePoint_                       = null;
	private int pathLength_, pathArraySize_;
	private DPoint3[] pathArray_;

	private int multiSelectX_, multiSelectY_, multiSelectX2_, multiSelectY2_;

	private double moveX_, moveY_;

	public double hSpacing                               = 30, vSpacing                               = 40;

	private DragFix dragFix_;

	private int qualityCB_                               = 1;
	private int quality_                                 = 1;

/////////////////////////////////////////////////////////////////////////////////
	// Construct a pict file for the graph///////////////////////////
///////////////////////////////////////////////////////////////////////////////
	short height1_;
	short width1_;

	// Create anti-aliasing grey shades.
	static
	{
		aaShades_ = new Color[aaDivs_ * aaDivs_ * 2];

		for (int i = 0; i < aaDivs_ * aaDivs_ * 2; i++)
		{
			int shade  = 255 - i * 512 / (aaDivs_ * aaDivs_);
			if (shade < 0)
			{
				shade = 0;
			}
			aaShades_[i] = new Color(shade, shade, shade);
		}
	}


	public GraphCanvas(Graph graph_in, Frame frame_in)
	{
		graph_ = graph_in;
		frame_ = frame_in;
		setBackground(Color.white);
		font_ = new Font("Helvetica", Font.PLAIN, 12);
		edgeFont_ = new Font("Helvetica", Font.PLAIN, 10);

		computeBounds_();

		scaleMatrix_ = new Matrix44();

		scaleMatrix_.matrix[0][0] = scaleMatrix_.matrix[1][1] =
				scaleMatrix_.matrix[2][2] = scaleMatrix_.matrix[3][3] = 1.0;

		shiftMatrix_ = new Matrix44(scaleMatrix_);
		rotxMatrix_ = new Matrix44(scaleMatrix_);
		rotxMatrix_.matrix[1][1] = rotxMatrix_.matrix[2][2] = -1.0;
		rotxMatrix_.matrix[2][1] = -(rotxMatrix_.matrix[1][2] = 0.0);
		rotzMatrix_ = new Matrix44(scaleMatrix_);

		updateViewTransform_();

		dragFix_ = new DragFix(this);
	}


	public void center()
	{
		computeBounds_();

		setOffsets_(.5 * (windowSize_.width - (minx_ + maxx_) * scale_), .5 * (windowSize_.height - (miny_ + maxy_) * scale_));
		getParent().postEvent(new Event((Object) this, RESIZE, (Object) this));

		paintOver();
	}


	private void computeBounds_()
	{
		double oldminx     = minx_;
		double oldminy     = miny_;

		minx_ = miny_ = maxx_ = maxy_ = 0;

		Node tmpnode       = graph_.firstNode();
		DPoint tmppoint;
		DDimension tmpdim;

		if (tmpnode != null)
		{
			tmppoint = tmpnode.getPosition();
			tmpdim = tmpnode.getBoundingBox();

			minx_ = tmppoint.x - tmpdim.width / 2.0;
			maxx_ = tmppoint.x + tmpdim.width / 2.0;
			miny_ = tmppoint.y - tmpdim.height / 2.0;
			maxy_ = tmppoint.y + tmpdim.height / 2.0;

			tmpnode = graph_.nextNode(tmpnode);
			while (tmpnode != null)
			{
				tmppoint = tmpnode.getPosition();
				tmpdim = tmpnode.getBoundingBox();

				double w  = tmpdim.width / 2.0;
				double h  = tmpdim.height / 2.0;

				if (tmppoint.x - w < minx_)
				{
					minx_ = tmppoint.x - w;
				}
				if (tmppoint.x + w > maxx_)
				{
					maxx_ = tmppoint.x + w;
				}
				if (tmppoint.y - h < miny_)
				{
					miny_ = tmppoint.y - h;
				}
				if (tmppoint.y + h > maxy_)
				{
					maxy_ = tmppoint.y + h;
				}
				tmpnode = graph_.nextNode(tmpnode);
			}
		}

		// 3D approach.
		double maxdim      = Math.abs(maxy_);
		if (Math.abs(miny_) > maxdim)
		{
			maxdim = Math.abs(miny_);
		}
		if (Math.abs(minx_) > maxdim)
		{
			maxdim = Math.abs(minx_);
		}
		if (Math.abs(maxx_) > maxdim)
		{
			maxdim = Math.abs(maxx_);
		}

		maxx_ = maxy_ = maxdim;
		minx_ = miny_ = -maxdim;
		width_ = height_ = 2.0 * maxdim;
	}


	private void computeDrawOffset_()
	{
		setOffsets_((-offsetx_ - minx_) * scale_ + windowSize_.width, (-offsety_ - miny_) * scale_ + windowSize_.height);
	}


	public DDimension contentsSize()
	{
		double w  = width_ * scale_ + (double) windowSize_.width * 2.0;
		double h  = height_ * scale_ + (double) windowSize_.height * 2.0;

		return new DDimension(w, h);
	}


	public void deleteSelected(boolean group_warning)
	{
		if (selectedNode_ != null || selectedEdge_ != null)
		{
			if (selectedNode_ != null)
			{

				if (group_warning)
				{
					boolean group  = false;
					for (Node tmpnode = graph_.firstNode(); tmpnode != null;
							tmpnode = graph_.nextNode(tmpnode))
					{
						if (tmpnode.getSelected() && tmpnode.isGroup())
						{
							group = true;
							break;
						}
					}

					if (group)
					{
						new GroupWarningDialog(frame_, this);
						return;
					}
				}

				for (Node tmpnode = graph_.firstNode(); tmpnode != null;
						tmpnode = graph_.nextNode(tmpnode))
				{
					if (tmpnode.getSelected())
					{
						graph_.removeNode(tmpnode);
					}
				}
				selectedNode_ = null;
				computeBounds_();
				getParent().postEvent(new Event((Object) this, RESIZE, (Object) this));
			}
			if (selectedEdge_ != null)
			{
				Enumeration edges  = graph_.getEdges();
				while (edges.hasMoreElements())
				{
					Edge edge  = (Edge) (edges.nextElement());
					if (edge.selected)
					{
						graph_.removeEdge(edge);
					}
				}
			}
			paintOver();
		}
		else if (currentMouseAction_ == 2)
		{
			// Drawing edge.

			currentMouseAction_ = 0;
			paintOver();
		}
	}


	private synchronized void drawAxes_(Graphics graphics)
	{
		String letter;
		double lx;
		double ly;
		FontMetrics fm  = graphics.getFontMetrics();

		graphics.setColor(Color.black);

		DPoint3 p2      = new DPoint3();
		p2.move(25, 0, 0);
		p2.transform(rotzMatrix_);
		p2.transform(rotxMatrix_);
		graphics.drawLine(40, 40, 40 + (int) p2.x, 40 + (int) p2.y);

		p2.move(32, 0, 0);
		p2.transform(rotzMatrix_);
		p2.transform(rotxMatrix_);
		letter = new String("X");
		lx = p2.x - fm.stringWidth(letter) / 2.0;
		ly = p2.y + fm.getAscent() / 2.0;
		graphics.drawString(letter, 40 + (int) lx, 40 + (int) ly);

		p2.move(0, 25, 0);
		p2.transform(rotzMatrix_);
		p2.transform(rotxMatrix_);
		graphics.drawLine(40, 40, 40 + (int) p2.x, 40 + (int) p2.y);

		p2.move(0, 32, 0);
		p2.transform(rotzMatrix_);
		p2.transform(rotxMatrix_);
		letter = new String("Y");
		lx = p2.x - fm.stringWidth(letter) / 2.0;
		ly = p2.y + fm.getAscent() / 2.0;
		graphics.drawString(letter, 40 + (int) lx, 40 + (int) ly);

		p2.move(0, 0, 25);
		p2.transform(rotzMatrix_);
		p2.transform(rotxMatrix_);
		graphics.drawLine(40, 40, 40 + (int) p2.x, 40 + (int) p2.y);

		p2.move(0, 0, 32);
		p2.transform(rotzMatrix_);
		p2.transform(rotxMatrix_);
		letter = new String("Z");
		lx = p2.x - fm.stringWidth(letter) / 2.0;
		ly = p2.y + fm.getAscent() / 2.0;
		graphics.drawString(letter, 40 + (int) lx, 40 + (int) ly);
	}


	// See if an edge has an identical back edge and head index > tail index.

	private boolean drawBackEdge_(int n1, int n2)
	{
		if (n1 <= n2 || graph_.getEdge(n2, n1) == null)
		{
			return false;
		}

		DPoint3[] path1  = graph_.getEdgePathPoints(n1, n2);
		DPoint3[] path2  = graph_.getEdgePathPoints(n2, n1);
		if (path1.length != path2.length)
		{
			return false;
		}
		for (int pt = 0; pt < path1.length; pt++)
		{
			if (!path1[pt].equals(path2[path1.length - 1 - pt]))
			{
				return false;
			}
		}
		return true;
	}


	// Draw selected or unselected objects.

	public synchronized void drawObjects_(boolean selected, Graphics graphics, int which_gr)
	{
		Node tmpnode;

		// Draw nodes.
		for (tmpnode = graph_.firstNode(); tmpnode != null;
				tmpnode = graph_.nextNode(tmpnode))
		{
			if (tmpnode.getSelected() == selected)
			{
				tmpnode.draw(this, graphics, viewTransform_, quality_);
			}
		}

		// Draw edges.
		Enumeration edges  = graph_.getEdges();
		boolean directed   = graph_.isDirected();
		while (edges.hasMoreElements())
		{
			Edge edge           = (Edge) (edges.nextElement());
			Node head           = edge.head();
			Node tail           = edge.tail();

			if ((head.getVisibleGroupRoot() == tail.getVisibleGroupRoot() &&
					head != tail) || (head == tail && !head.isVisible()))
			{
				// Inter-group edge.
				continue;
			}

			boolean arrow_only  = false;
			if (directed)
			{
				arrow_only = drawBackEdge_(tail.getIndex(), head.getIndex());
			}

			boolean sel         = edge.selected;
			sel |= head.getVisibleGroupRoot().getSelected();
			sel |= tail.getVisibleGroupRoot().getSelected();
			if (sel == selected)
			{
				if (directed || tail.getIndex() <= head.getIndex())
				{
					edge.draw(graphics, viewTransform_, xyPlane_, directed, arrow_only, quality_, this, which_gr);
					graphics.dispose();
					if (which_gr == 0)
					{
						graphics = getGraphicsInternal_();
					}
					else
					{
						graphics = getBackGraphics_();
					}
				}
			}
		}
	}


	public void drawRotatedText(String string, double theta, int cx, int cy, Graphics graphics_in, int which_gr)
	{
		FontMetrics fm     = graphics_in.getFontMetrics();
		int label_w        = fm.stringWidth(string);
		int label_h        = fm.getHeight();

		/*double cos_theta   = Math.cos(theta);
		if (cos_theta < 0.0)
		{
			theta += Math.PI;
			cos_theta = -cos_theta;
		}
		double sin_theta   = Math.sin(theta);
		cx += -sin_theta * label_h / 2.0;
		cy += -cos_theta * label_h / 2.0;*/

		graphics_in.dispose();

//		Image tmp_image    = createImage(label_w, label_h);
//		Graphics graphics  = tmp_image.getGraphics();
//		graphics.setFont(font_);
//		graphics.setColor(new Color(0));
//		graphics.drawString(string, 0, fm.getAscent());
//		graphics.dispose();

/*		int[] pixels       = Node.getImagePixels(tmp_image, label_w, label_h);
		if (pixels == null)
		{
			return;
		}

		int x;

		int y;
		int image_size     = (int) Math.ceil(Math.sqrt((double) (label_w * label_w) + (double) (label_h * label_h))) + 2;

		int[] result       = new int[image_size * image_size];

		rotImage_(theta, pixels, label_w, label_h, result, image_size);

		cx -= image_size / 2;
		cy -= image_size / 2;

*/		if (which_gr == 0)
		{
			graphics_in = getGraphicsInternal_();
		}
		else
		{
			graphics_in = getBackGraphics_();
		}
		
    
/*		for (y = 0; y < image_size; y++)
		{
			for (x = 0; x < image_size; x++)
			{
				if (result[y * image_size + x] > 0)
				{
					graphics_in.setColor(aaShades_[result[y * image_size + x] / 2]);
					graphics_in.drawLine(cx + x, cy + y, cx + x, cy + y);
				}

			}
		}*/
		
		
	AffineTransform at = new AffineTransform();
    at.setToRotation(Math.PI/2.0);
    
    Graphics2D g2 = (Graphics2D) graphics_in;
    g2.setFont(edgeFont_);
	g2.setColor(Color.black);
//    System.out.println("printing \"" + string + "\" at " + cx + "," +cy);
    g2.setTransform(at);
    g2.drawString(string, (int)cx, (int)cy);
    
//		graphics_in.dispose();
	}


	private void drawSelectedNodes_()
	{
		if (selectedNode_ == null)
		{
			return;
		}

		Graphics graphics  = getGraphicsInternal_();

		graphics.setColor(Color.black);
		graphics.setPaintMode();
		graphics.drawImage(backImage_, 0, 0, null);
		setWireframe(true);
		drawObjects_(true, graphics, 0);
		setWireframe(false);
		graphics.dispose();
	}


	private void drawSelectRect_()
	{
		Graphics graphics  = getGraphics();
		graphics.setColor(Color.black);

		int sx             = Math.min(multiSelectX_, multiSelectX2_);
		int sy             = Math.min(multiSelectY_, multiSelectY2_);
		int sw             = Math.abs(multiSelectX_ - multiSelectX2_);
		int sh             = Math.abs(multiSelectY_ - multiSelectY2_);
		if (multiSelectX2_ != -1)
		{
			graphics.drawImage(backImage_, 0, 0, null);
			graphics.drawRect(sx, sy, sw, sh);
		}
		graphics.dispose();
	}


	private Point findNearestEdge_(double x, double y)
	{
		Point edge       = null;

		Node tmpnode;
		DPoint3 pos      = new DPoint3();
		DPoint3 pos2     = new DPoint3();
		DDimension bbox;

		double closest   = width_ + height_;

		double xd;

		double yd;

		double dist;

		for (tmpnode = graph_.firstNode(); tmpnode != null; tmpnode = graph_.nextNode(tmpnode))
		{
			if (tmpnode.isGroup())
			{
				continue;
			}
			for (int child = tmpnode.firstChild(); child != -1; child = tmpnode.nextChild())
			{
				Node childnode  = graph_.getNodeFromIndex(child);
				if (childnode.isGroup())
				{
					continue;
				}

				int npoints     = 0;
				DPoint3[] path  = graph_.getEdgePathPoints(tmpnode.getIndex(), child);
				if (path != null)
				{
					npoints = path.length;
				}

				for (int pointindex = 0; pointindex <= npoints; pointindex++)
				{
					if (pointindex == 0)
					{
						pos = tmpnode.getPosition3();
					}
					else
					{
						pos.move(path[pointindex - 1]);
					}

					if (pointindex == npoints)
					{
						pos2 = childnode.getPosition3();
					}
					else
					{
						pos2.move(path[pointindex]);
					}

					pos.transform(viewTransform_);
					pos2.transform(viewTransform_);

					if ((x >= pos.x - 1 && x <= pos2.x + 1 || x >= pos2.x - 1 &&
							x <= pos.x + 1) &&
							(y >= pos.y - 1 && y <= pos2.y + 1 ||
							y >= pos2.y - 1 && y <= pos.y + 1))
					{
						double dx  = pos2.x - pos.x;
						double dy  = pos2.y - pos.y;

						if (dx == 0.0 || dy == 0.0)
						{
							dist = 0.0;
						}
						else
						{
							// x distance to line
							xd = Math.abs((dx / dy) * (y - pos.y) + pos.x - x);

							// y distance to line
							yd = Math.abs((dy / dx) * (x - pos.x) + pos.y - y);

							dist = Math.min(xd, yd);
						}

						if (dist < 3.0 && dist < closest)
						{
							closest = dist;
							edge = new Point(tmpnode.getIndex(), child);
						}

					}
				}
			}
		}
		return edge;
	}


	private Node findNearestNode_(double x, double y, boolean group_nodes)
	{
		Node node        = null;

		Node tmpnode;
		DPoint3 pos;
		DDimension bbox;

		double closest   = (width_ + height_) * scale_;

		for (tmpnode = graph_.firstNode(); tmpnode != null; tmpnode = graph_.nextNode(tmpnode))
		{
			if (!tmpnode.isVisible() || (!group_nodes && tmpnode.isGroup()))
			{
				continue;
			}
			pos = tmpnode.getPosition3();
			bbox = tmpnode.getBoundingBox();

			pos.transform(viewTransform_);

			pos.x = Math.abs(pos.x - x);
			pos.y = Math.abs(pos.y - y);

			if (pos.x < bbox.width / 2.0 * scale_ + 1 && pos.y < bbox.height / 2.0 * scale_ + 1)
			{
				if (pos.x + pos.y < closest)
				{
					// rough estimate of closeness

					closest = pos.x + pos.y;
					node = tmpnode;
				}
			}
		}
		return node;
	}


	private Graphics getBackGraphics_()
	{
		Graphics graphics  = backImage_.getGraphics();
		graphics.setFont(font_);
		return graphics;
	}


	public DPoint3 getCenter()
	{
		DPoint3 retval  = new DPoint3(windowSize_.width / 2.0, windowSize_.height / 2.0, 0);
		retval.transform(moveTransform_);

		return retval;
	}


	// Get bounds of drawing with current transform.

	public void getDrawBounds_(DPoint width, DPoint height)
	{
		boolean first     = true;
		DPoint tmpwidth   = new DPoint();
		DPoint tmpheight  = new DPoint();
		DPoint3 pos       = new DPoint3();
		Node tmpnode;
		for (tmpnode = graph_.firstNode(); tmpnode != null;
				tmpnode = graph_.nextNode(tmpnode))
		{
			tmpnode.getDrawBounds_(scale_, viewTransform_, tmpwidth, tmpheight);
			if (first)
			{
				first = false;
				width.move(tmpwidth);
				height.move(tmpheight);
			}
			else
			{
				width.x = Math.min(width.x, tmpwidth.x);
				width.y = Math.max(width.y, tmpwidth.y);
				height.x = Math.min(height.x, tmpheight.x);
				height.y = Math.max(height.y, tmpheight.y);
			}

			// Check edge path points.
			int tmpnode_index  = tmpnode.getIndex();
			for (int child = tmpnode.firstChild(); child != -1; child = tmpnode.nextChild())
			{
				if (graph_.isDirected() || tmpnode_index <= child)
				{
					DPoint3[] path  = graph_.getEdgePathPoints(tmpnode_index, child);
					if (path != null && path.length > 0)
					{
						for (int i = 0; i < path.length; i++)
						{
							pos.move(path[i]);
							pos.transform(viewTransform_);
							if (first)
							{
								first = false;
								width.move(pos.x, pos.x);
								height.move(pos.y, pos.y);
							}
							else
							{
								width.x = Math.min(width.x, pos.x);
								width.y = Math.max(width.y, pos.x);
								height.x = Math.min(height.x, pos.y);
								height.y = Math.max(height.y, pos.y);
							}
						}
					}
				}
			}

		}

		if (first)
		{
			width.move(0.0, 1.0);
			height.move(0.0, 1.0);
		}

	}


	public Font getFont(boolean node)
	{
		if (node)
			return font_;
		else 
			return edgeFont_;
	}


	public Frame getFrame()
	{
		return frame_;
	}


	public Graph getGraph()
	{
		return graph_;
	}


	// Get the graphics with the font set.

	private Graphics getGraphicsInternal_()
	{
		Graphics graphics  = getGraphics();
		graphics.setFont(font_);
		return graphics;
	}


	public double getHSpacing()
	{
		return 1 / scale_ * hSpacing;
	}


	private String getLabel_(double x, double y, double z, boolean mousein)
	{
		String string  = new String();

		if (mousein)
		{
			string = string.concat("x: " + x + "  y: " + y + "  z: " + z);
		}

		if (selectedNode_ != null)
		{
			DPoint3 pos       = selectedNode_.getPosition3();
			DDimension3 bbox  = selectedNode_.getBoundingBox3();
			string = string.concat("   Node " + selectedNode_.getIndex() + "   x: " + pos.x + "  y: " + pos.y + "  z: " + pos.z + "   w: " + bbox.width + "  h: " + bbox.height + "  d: " + bbox.depth);
		}

		if (selectedEdge_ != null)
		{
			string = string.concat("   Edge (" + selectedEdge_.x + "," + selectedEdge_.y + ")");
		}

		return string;
	}


	public DPoint getOffset()
	{
		DPoint val  = new DPoint(0, 0);

		val.x = -(offset_.x - (double) windowSize_.width) - minx_ * scale_;
		val.y = -(offset_.y - (double) windowSize_.height) - miny_ * scale_;

		return val;
	}


	public Node getSelectedNode()
	{
		return selectedNode_;
	}


	public double getVSpacing()
	{
		return 1 / scale_ * vSpacing;
	}


	public void groupControl(int key)
	{
		if ((key == 'g' || key == 'u' || key == 'd'))
		{
			Node tmpnode;
			for (tmpnode = graph_.firstNode(); tmpnode != null;
					tmpnode = graph_.nextNode(tmpnode))
			{
				if (tmpnode.getSelected() && tmpnode.isVisible())
				{
					if (key == 'g' || key == 'u')
					{
						// Group or ungroup.
						graph_.group(tmpnode, key == 'g');
					}
					else if (key == 'd')
					{
						// Delete group.
						graph_.killGroup(tmpnode);
					}
				}

			}

		}
		else if (key == 'c')
		{
			// Create group.
			int groupnode_id  = graph_.insertNode();
			Node groupnode    = graph_.nodeFromIndex(groupnode_id);
			groupnode.setGroup();

			Node tmpnode;

			Node one_member   = null;
			for (tmpnode = graph_.firstNode(); tmpnode != null;
					tmpnode = graph_.nextNode(tmpnode))
			{
				if (tmpnode.getSelected() && tmpnode.isVisible())
				{
					graph_.setNodeGroup(tmpnode, groupnode);
					one_member = tmpnode;
				}
			}
			if (one_member != null)
			{
				graph_.group(one_member, true);
			}
		}
		unselectItems();
		paintOver();

	}


	public boolean handleEvent(Event e)
	{
		if (e.id == DragFix.QUEUED)
		{
			super.handleEvent((Event) e.arg);
			getParent().postEvent((Event) e.arg);
			return true;
		}
		dragFix_.queueEvent(e);
		return true;
	}


	public boolean keyDown(Event e, int key)
	{
		if (e.id == Event.KEY_PRESS)
		{
			if (key == 127)
			{
				// Delete key.
				deleteSelected(true);
			}
			else if ((key == 'g' || key == 'u' || key == 'd' || key == 'c'))
			{
				groupControl(key);
			}
		}

		return true;
	}


	public boolean mouseDown(Event e, int x_in, int y_in)
	{
		if (currentMouseAction_ == 2 && newEdgeNode_ != null)
		{
			if ((e.modifiers & Event.SHIFT_MASK) == 0)
			{
				Node tmpnode  = findNearestNode_(x_in, y_in, false);

				if (tmpnode != null)
				{
					// Finish edge.
					if (pathLength_ == 0 && newEdgeNode_ != tmpnode)
					{
						graph_.insertEdge(graph_.getIndexFromNode(newEdgeNode_), graph_.getIndexFromNode(tmpnode));
					}
					else
					{
						DPoint3[] tmp_array;
						if (pathLength_ == 0)
						{
							// Must be self-edge with no intermediate points
							// Add some intermediate points.
							tmp_array = new DPoint3[2];
							DPoint3 pos       = tmpnode.getPosition3();
							pos.transform(viewTransform_);
							DPoint3 pos2      = new DPoint3(pos);
							DDimension3 size  = tmpnode.getBoundingBox3();
							double w          = size.width / 2.0 * scale_;
							if (w < 10)
							{
								w = 10;
							}
							double h          = size.height / 2.0 * scale_ + w * 1.5;
							pos.translate(-w, -h, 0);
							pos2.translate(w, -h, 0);
							pos.transform(moveTransform_);
							pos2.transform(moveTransform_);
							tmp_array[0] = pos;
							tmp_array[1] = pos2;
						}
						else
						{
							tmp_array = new DPoint3[pathLength_];
							System.arraycopy(pathArray_, 0, tmp_array, 0, pathLength_);
						}
						graph_.insertEdge(graph_.getIndexFromNode(newEdgeNode_), graph_.getIndexFromNode(tmpnode), tmp_array);
					}
					currentMouseAction_ = 0;
					paintOver();
				}
			}
			else
			{
				// New edge path point.

				DPoint3 pos         = new DPoint3(x_in, y_in, 0.0);
				pos.transform(moveTransform_);

				DPoint3 p2;
				if (lastEdgePoint_ != null)
				{
					p2 = new DPoint3(lastEdgePoint_);
				}
				else
				{
					p2 = newEdgeNode_.intersectWithLineTo(pos, xyPlane_, quality_);
				}
				p2.transform(viewTransform_);

				Graphics bgraphics  = getBackGraphics_();
				bgraphics.setColor(Color.black);

				bgraphics.drawLine((int) x_in, (int) y_in, (int) p2.x, (int) p2.y);
				bgraphics.dispose();
				Graphics graphics   = getGraphics();
				graphics.drawImage(backImage_, 0, 0, null);
				graphics.dispose();

				lastEdgePoint_ = pos;

				if (pathLength_ >= pathArraySize_)
				{
					pathArraySize_ = pathLength_ * 2;
					DPoint3[] new_array  = new DPoint3[pathArraySize_];
					System.arraycopy(pathArray_, 0, new_array, 0, pathLength_);
					pathArray_ = new_array;
				}
				pathArray_[pathLength_] = lastEdgePoint_;
				pathLength_++;

				return false;
			}

			return false;
		}

		// Avoid overlapping mouse events.
		if (currentMouseAction_ != 0)
		{
			return false;
		}

		DPoint3 pos    = new DPoint3(x_in, y_in, 0.0);
		pos.transform(moveTransform_);

		movingZ_ = 0.0;

		// Determine which action to take.
		if ((e.modifiers & (Event.META_MASK | Event.ALT_MASK | Event.CTRL_MASK)) == 0)
		{
			// button 1

			currentMouseAction_ = 1;
			if (mouseMode_ == CREATE_EDGES)
			{
				currentMouseAction_ = 2;
			}
			else if (mouseMode_ == SELECT_NODES || mouseMode_ == SELECT_EDGES ||
					mouseMode_ == SELECT_BOTH)
			{
				currentMouseAction_ = 3;
			}
		}
		else if ((e.modifiers & (Event.ALT_MASK | Event.CTRL_MASK)) != 0)
		{
			// button 2
			currentMouseAction_ = 2;
		}
		else
		{
			currentMouseAction_ = 3;
		}

		if (currentMouseAction_ == 1)
		{
			if (selectedNode_ != null || selectedEdge_ != null)
			{
				unselectItems();
				paintOver();
			}

			int index          = graph_.insertNode();

			movingNode_ = graph_.getNodeFromIndex(index);

			movingNode_.setPosition(pos);

			DDimension bbox    = movingNode_.getBoundingBox();

			if (scaleBounds_)
			{
				bbox.width /= scale_;
				bbox.height /= scale_;
			}

			movingNode_.setBoundingBox(bbox.width, bbox.height);

			Graphics graphics  = getGraphicsInternal_();
			graphics.setColor(Color.black);

			movingNode_.draw(this, graphics, viewTransform_, quality_);
			graphics.dispose();
		}
		else if (currentMouseAction_ == 2)
		{
			newEdgeNode_ = null;
			lastEdgePoint_ = null;

			if (selectedNode_ != null || selectedEdge_ != null)
			{
				unselectItems();
				paintOver();
			}

			if ((newEdgeNode_ = findNearestNode_(x_in, y_in, false)) != null)
			{
				pathLength_ = 0;
				pathArraySize_ = 10;
				// Arbitrary value.
				pathArray_ = new DPoint3[pathArraySize_];
			}
		}
		else if (currentMouseAction_ == 3)
		{
			// Select object.

			selected_ = NONE_;

			if (selectedEdge_ != null && e.clickCount == 2)
			{
				setEdgeProperties(false);
				return false;
			}

			if (selectedNode_ != null)
			{
				if (e.clickCount == 2)
				{
					setNodeProperties(false);
					return false;
				}

				for (Node tmpnode = graph_.firstNode(); tmpnode != null && selected_ == NONE_;
						tmpnode = graph_.nextNode(tmpnode))
				{
					if (tmpnode.getSelected() && tmpnode.isVisible())
					{
						selectedNode_ = tmpnode;

						DPoint3 posc     = tmpnode.getPosition3();
						DDimension bbox  = tmpnode.getBoundingBox();

						posc.transform(viewTransform_);

						DPoint tr        = new DPoint(posc.x + bbox.width / 2.0 * scale_ + 1, posc.y - bbox.height / 2.0 * scale_ - 1);
						DPoint bl        = new DPoint(posc.x - bbox.width / 2.0 * scale_ - 1, posc.y + bbox.height / 2.0 * scale_ + 1);

						double dist      = 16.0;

						double newdist;
						newdist = (posc.x - x_in) * (posc.x - x_in) + (posc.y - y_in) * (posc.y - y_in);
						if (newdist < dist)
						{
							selected_ = CENTER_;
							dist = newdist;
						}
						newdist = (tr.x - x_in) * (tr.x - x_in) + (tr.y - y_in) * (tr.y - y_in);
						if (newdist < dist)
						{
							selected_ = CORNER_;
							if (bbox.height == 0.0 && bbox.width == 0.0)
							{
								selectedRatio_ = 1;
							}
							else
							{
								selectedRatio_ = bbox.height / bbox.width;
							}
							dist = newdist;
						}
						newdist = (posc.x - x_in) * (posc.x - x_in) + (bl.y - y_in) * (bl.y - y_in);
						if (newdist < dist)
						{
							selected_ = BOTTOM_;
							dist = newdist;
						}
						newdist = (bl.x - x_in) * (bl.x - x_in) + (posc.y - y_in) * (posc.y - y_in);
						if (newdist < dist)
						{
							selected_ = LEFT_;
						}

					}
				}

				if (selected_ != NONE_)
				{
					DPoint3 tmppos     = selectedNode_.getPosition3();
					tmppos.transform(viewTransform_);
					movingZ_ = tmppos.z;
					movingX_ = tmppos.x;
					movingY_ = tmppos.y;

					for (Node tmpnode = graph_.firstNode(); tmpnode != null;
							tmpnode = graph_.nextNode(tmpnode))
					{
						tmpnode.saveState();
					}
					Enumeration edges  = graph_.getEdges();
					while (edges.hasMoreElements())
					{
						((Edge) (edges.nextElement())).saveState();
					}
					moveX_ = (double) x_in;
					moveY_ = (double) y_in;
				}
			}
			if (selected_ == NONE_)
			{
				if ((e.modifiers & Event.SHIFT_MASK) == 0)
				{
					unselectItems();
				}

				Node tmpnode   = findNearestNode_(x_in, y_in, true);
				Point tmpedge;
				if (mouseMode_ != SELECT_EDGES && tmpnode != null)
				{
					selectedNode_ = tmpnode;
					selectedNode_.setSelected(true);
					selected_ = NONE_;
				}
				else if (mouseMode_ != SELECT_NODES &&
						(tmpedge = findNearestEdge_(x_in, y_in)) != null)
				{
					selectedEdge_ = tmpedge;
					setEdgeSelected_(selectedEdge_.x, selectedEdge_.y, true);
				}
				else
				{
					currentMouseAction_ = 4;
					// Multiple select.
					multiSelectX_ = x_in;
					multiSelectY_ = y_in;
					multiSelectX2_ = -1;
					// First time flag.
				}
			}
			//paintOver(currentMouseAction_ == 4);
		}

		String string  = getLabel_(pos.x, pos.y, pos.z, true);

		getParent().postEvent(new Event((Object) this, OffsetCanvas.LABEL, string));

		return false;
	}


	public boolean mouseDrag(Event e, int x_in, int y_in)
	{
		DPoint3 pos    = new DPoint3(x_in, y_in, movingZ_);
		DPoint3 vpos   = new DPoint3(pos);
		pos.transform(moveTransform_);

		if (currentMouseAction_ == 1)
		{
			Graphics graphics  = getGraphicsInternal_();

			graphics.setColor(Color.black);
			movingNode_.setPosition(pos);
			graphics.drawImage(backImage_, 0, 0, null);
			movingNode_.draw(this, graphics, viewTransform_, quality_);
			graphics.dispose();
		}

		else if (currentMouseAction_ == 2 && newEdgeNode_ != null)
		{
			DPoint3 p2;
			if (lastEdgePoint_ != null)
			{
				p2 = new DPoint3(lastEdgePoint_);
			}
			else
			{
				p2 = newEdgeNode_.intersectWithLineTo(pos, xyPlane_, quality_);
			}

			p2.transform(viewTransform_);

			Graphics graphics  = getGraphics();
			graphics.setColor(Color.black);

			graphics.drawImage(backImage_, 0, 0, null);
			graphics.drawLine((int) x_in, (int) y_in, (int) p2.x, (int) p2.y);
			graphics.dispose();
		}

		else if (currentMouseAction_ == 3 && selectedNode_ != null)
		{
			/*
			    If a node is dragged far enough, start moving it.
			  */
			if (selected_ == NONE_)
			{
				DPoint3 tmppos  = selectedNode_.getPosition3();
				tmppos.transform(viewTransform_);

				moveX_ = tmppos.x;
				moveY_ = tmppos.y;
				int xoffs       = x_in - (int) moveX_;
				int yoffs       = y_in - (int) moveY_;
				if (xoffs * xoffs + yoffs * yoffs > 9
				/*
				    Three pixels
				  */
						)
				{
					selected_ = CENTER_;

					movingZ_ = tmppos.z;
					movingX_ = tmppos.x;
					movingY_ = tmppos.y;

					for (Node tmpnode = graph_.firstNode(); tmpnode != null;
							tmpnode = graph_.nextNode(tmpnode))
					{
						tmpnode.saveState();
					}
					Enumeration edges  = graph_.getEdges();
					while (edges.hasMoreElements())
					{
						((Edge) (edges.nextElement())).saveState();
					}
					paintOver();
				}
			}

			if (selected_ == CENTER_)
			{
				int xoffs          = x_in - (int) moveX_;
				int yoffs          = y_in - (int) moveY_;

				for (Node tmpnode = graph_.firstNode(); tmpnode != null;
						tmpnode = graph_.nextNode(tmpnode))
				{
					if (tmpnode.getSelected())
					{
						tmpnode.slide(moveTransform_, viewTransform_, xoffs, yoffs);
					}
				}

				Enumeration edges  = graph_.getEdges();
				while (edges.hasMoreElements())
				{
					Edge edge  = (Edge) (edges.nextElement());
					if (edge.tail().getSelected() && edge.head().getSelected())
					{
						edge.slide(moveTransform_, viewTransform_, xoffs, yoffs);
					}
				}

			}
			else if (selected_ != NONE_)
			{
				double ratiox  = 1.0;
				double ratioy  = 1.0;
				double ratioz  = 1.0;
				if (selected_ == CORNER_)
				{
					ratiox = ratioy = ratioz = Math.max(Math.abs(x_in - movingX_) / Math.abs(moveX_ - movingX_), Math.abs(y_in - movingY_) / Math.abs(moveY_ - movingY_));
				}
				else if (selected_ == BOTTOM_)
				{
					ratioy = Math.abs(y_in - movingY_) / Math.abs(moveY_ - movingY_);
				}
				else if (selected_ == LEFT_)
				{
					ratiox = Math.abs(x_in - movingX_) / Math.abs(moveX_ - movingX_);
				}
				for (Node tmpnode = graph_.firstNode(); tmpnode != null;
						tmpnode = graph_.nextNode(tmpnode))
				{
					if (tmpnode.getSelected())
					{
						tmpnode.scale(ratiox, ratioy, ratioz);
					}
				}
			}

			drawSelectedNodes_();
		}
		else if (currentMouseAction_ == 4)
		{
			multiSelectX2_ = x_in;
			multiSelectY2_ = y_in;
			drawSelectRect_();
		}

		String string  = getLabel_(pos.x, pos.y, pos.z, true);

		getParent().postEvent(new Event((Object) this, OffsetCanvas.LABEL, string));

		return false;
	}


	public boolean mouseExit(Event event, int x_in, int y_in)
	{
		String string  = getLabel_(0, 0, 0, false);

		getParent().postEvent(new Event((Object) this, OffsetCanvas.LABEL, string));

		return false;
	}


	public boolean mouseMove(Event event, int x_in, int y_in)
	{
		mouseDrag(event, x_in, y_in);
		return false;
	}


	public boolean mouseUp(Event e, int x_in, int y_in)
	{
		DPoint3 pos    = new DPoint3(x_in, y_in, movingZ_);
		pos.transform(moveTransform_);

		if (currentMouseAction_ == 1)
		{
			// button 1

			computeBounds_();
			getParent().postEvent(new Event((Object) this, RESIZE, (Object) this));
			paintOver();
		}
		else if (currentMouseAction_ == 2 && newEdgeNode_ != null)
		{
			// Creating an edge, do nothing.

			return false;
		}
		else if (currentMouseAction_ == 3)
		{
			computeBounds_();
			getParent().postEvent(new Event((Object) this, RESIZE, (Object) this));
			paintOver();
		}
		else if (currentMouseAction_ == 4)
		{
			multiSelect_();
		}

		currentMouseAction_ = 0;

		String string  = getLabel_(pos.x, pos.y, pos.z, true);

		getParent().postEvent(new Event((Object) this, OffsetCanvas.LABEL, string));

		return false;
	}


	private void multiSelect_()
	{
		if (multiSelectX2_ == -1)
		{
			return;
		}

		double x1  = (double) Math.min(multiSelectX_, multiSelectX2_);
		double y1  = (double) Math.min(multiSelectY_, multiSelectY2_);
		double x2  = (double) Math.max(multiSelectX_, multiSelectX2_);
		double y2  = (double) Math.max(multiSelectY_, multiSelectY2_);

		if (mouseMode_ != SELECT_EDGES)
		{
			Node tmpnode;
			for (tmpnode = graph_.firstNode(); tmpnode != null; tmpnode = graph_.nextNode(tmpnode))
			{
				DPoint3 pos  = tmpnode.getPosition3();
				pos.transform(viewTransform_);

				if (pos.x >= x1 && pos.x <= x2 && pos.y >= y1 && pos.y <= y2)
				{
					tmpnode.setSelected(true);
					selectedNode_ = tmpnode;
				}
			}
		}

		if (mouseMode_ == SELECT_EDGES)
		{
			Enumeration edges  = graph_.getEdges();
			boolean directed   = graph_.isDirected();
			while (edges.hasMoreElements())
			{
				Edge edge  = (Edge) (edges.nextElement());
				if (directed || edge.tail().getIndex() <= edge.head().getIndex())
				{
					// Check the edge.

					DPoint3 p1  = edge.tail().getPosition3();
					p1.transform(viewTransform_);
					DPoint3 p2  = edge.head().getPosition3();
					p2.transform(viewTransform_);
					if (p1.x >= x1 && p1.x <= x2 && p1.y >= y1 && p1.y <= y2 &&
							p2.x >= x1 && p2.x <= x2 && p2.y >= y1 && p2.y <= y2)
					{
						edge.selected = true;
						selectedEdge_ = new Point(edge.tail().getIndex(), edge.head().getIndex());
					}
				}
			}
		}

		paintOver();
	}


	public synchronized void paint(Graphics graphics)
	{
		graphics.dispose();
		paintOver();
	}


	public synchronized void paintOver()
	{
		Dimension tmpdim        = size();
		if (tmpdim.width != windowSize_.width || tmpdim.height != windowSize_.height)
		{
			// Maintain the center point.
			if (windowSize_.width > 0)
			{
				// not first time

				setOffsets_(offset_.x + (tmpdim.width - windowSize_.width) / 2, offset_.y + (tmpdim.height - windowSize_.height) / 2);
			}
			else
			{
				// initialize to centered

				setOffsets_(.5 * (tmpdim.width - (minx_ + maxx_) * scale_), .5 * (tmpdim.height - (miny_ + maxy_) * scale_));
			}

			windowSize_.width = tmpdim.width;
			windowSize_.height = tmpdim.height;

			// post an event indicating a size change
			getParent().postEvent(new Event((Object) this, RESIZE, (Object) this));

			// Force recreation of back buffer.
			backImage_ = null;
		}

		if (backImage_ == null)
		{
			backImage_ = createImage(windowSize_.width, windowSize_.height);
		}

		Graphics back_graphics  = getBackGraphics_();
		back_graphics.setColor(Color.white);
		back_graphics.setPaintMode();
		back_graphics.clearRect(0, 0, windowSize_.width, windowSize_.height);

		back_graphics.setColor(Color.black);
		//drawAxes_(back_graphics);
		drawObjects_(false, back_graphics, 1);
		back_graphics.dispose();

		Graphics graphics       = getGraphics();
		if (graphics != null)
		{
		// System.out.println("graphics = " + graphics);
		graphics.setPaintMode();
		graphics.setColor(Color.black);
		graphics.drawImage(backImage_, 0, 0, null);

		// Draw selected objects directly to screen.
		graphics.setFont(font_);
		drawObjects_(true, graphics, 0);

		graphics.dispose();
		}
	}


	// This will give the initial window size.

	public Dimension preferredSize()
	{
		return new Dimension(400, 400);
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


	public void removeEdgeBends()
	{
		graph_.removeEdgePaths();
		paintOver();
	}


	public void removeGroups()
	{
		graph_.removeGroups();
		paintOver();
	}


	public synchronized void removeNotify()
	{
		dragFix_.killThread();
		super.removeNotify();
	}


	private void rotImage_(double theta, int[] pixels, int w, int h, int[] result, int image_size)
	{
		double dxX    = Math.cos(theta);
		double dyX    = -Math.sin(theta);
		double dxY    = -dyX;
		double dyY    = dxX;
		double dxIx   = dxX / (double) aaDivs_;
		double dyIx   = dyX / (double) aaDivs_;
		double dxIy   = dxY / (double) aaDivs_;
		double dyIy   = dyY / (double) aaDivs_;
		double hdxIx  = dxIx / 2.0;
		double hdyIx  = dyIx / 2.0;
		double hdxIy  = dxIy / 2.0;
		double hdyIy  = dyIy / 2.0;

		double xX     = image_size / 2.0 - dxX * w / 2.0 - dxY * h / 2.0;
		double yX     = image_size / 2.0 - dyX * w / 2.0 - dyY * h / 2.0;

		double xY;

		double yY;

		double Ix;

		double Iy;

		double xIx;

		double yIx;

		double xIy;

		double yIy;
		int x;
		int y;
		for (x = 0; x < w; x++)
		{
			xY = xX;
			yY = yX;
			for (y = 0; y < h; y++)
			{
				xIx = xY + hdxIx;
				yIx = yY + hdyIx;
				if ((pixels[y * w + x] & 0xFFFFFF) == 0)
				{
					for (Ix = 0; Ix < aaDivs_; Ix++)
					{
						xIy = xIx + hdxIy;
						yIy = yIx + hdyIy;
						for (Iy = 0; Iy < aaDivs_; Iy++)
						{
							if (((int) yIy * image_size + (int) xIy > 0) &&
									((int) yIy * image_size + (int) xIy < image_size * image_size))
							{
								result[(int) yIy * image_size + (int) xIy]++;
							}

							xIy += dxIy;
							yIy += dyIy;
						}
						xIx += dxIx;
						yIx += dyIx;
					}
				}
				xY += dxY;
				yY += dyY;
			}
			xX += dxX;
			yX += dyX;
		}
	}


	public void scale(double scaleval)
	{
		setScale(scaleval);
	}


	public void scaleBounds(boolean sb)
	{
		scaleBounds_ = sb;
	}


	public void selectAll()
	{
		for (Node tmpnode = graph_.firstNode(); tmpnode != null;
				tmpnode = graph_.nextNode(tmpnode))
		{
			tmpnode.setSelected(true);
		}
		selectedNode_ = graph_.firstNode();
		paintOver();
	}


	public void selectRoot()
	{
		for (Node tmpnode = graph_.firstNode(); tmpnode != null;
				tmpnode = graph_.nextNode(tmpnode))
		{
			// if tmp.node == root
			if (tmpnode.getShape() == Node.RECTANGLE)
			{
				tmpnode.setSelected(true);
				selectedNode_ = tmpnode;
				break;
			}
		}
		paintOver();

	}


	public void selectNode(int node_index)
	{
		Node node  = graph_.getNodeFromId(node_index);

		selectedNode_ = node;
		node.setSelected(true);
		paintOver();
	}


	public void setDirected(boolean directed)
	{
		graph_.setDirected(directed);
		paintOver();

		getParent().postEvent(new Event((Object) this, UPDATE, null));
	}


	public void setEdgeProperties(boolean always_default)
	{
		Edge which  = graph_.getEdge(selectedEdge_.x, selectedEdge_.y);
		if (always_default)
		{
			which = null;
		}

		if (edgePropDialog_ == null)
		{
			edgePropDialog_ = new EdgePropertiesDialog(frame_, which, graph_);
		}
		else
		{
			edgePropDialog_.setEdge(which, graph_);
		}

		edgePropDialog_.pack();
		edgePropDialog_.setVisible(true);


		if (!always_default)
		{
			update(true);
		}
	}


	private void setEdgeSelected_(int n1, int n2, boolean state)
	{
		if (!graph_.isDirected())
		{
			Edge edge  = graph_.getEdge(Math.min(n1, n2), Math.max(n1, n2));
			edge.selected = state;
		}
		else
		{
			Edge edge  = graph_.getEdge(n1, n2);
			edge.selected = state;
		}
	}


	public void setFont(Font font)
	{
		font_ = font;
		paintOver();
	}

	public void setEdgeFont(Font font)
	{
		edgeFont_ = font;
		paintOver();
	}
	

	public void setMouseMode(int mode)
	{
		mouseMode_ = mode;
	}


	public void setNodeProperties(boolean always_default)
	{
		Node which  = selectedNode_;
		if (always_default)
		{
			which = null;
		}

		if (propDialog_ == null)
		{
			propDialog_ = new NodePropertiesDialog(frame_, which);
		}
		else
		{
			propDialog_.setNode(which);
		}

		propDialog_.pack();
		propDialog_.setVisible(true);


		if (!always_default)
		{
			update(true);
		}
	}


	public void setOffsets(double xoffset, double yoffset, boolean redraw)
	{
		offsetx_ = xoffset / scale_;
		offsety_ = yoffset / scale_;
		computeDrawOffset_();
		if (redraw)
		{
			paintOver();
		}
	}


	private void setOffsets_(double offx, double offy)
	{
		offset_.x = offx;
		offset_.y = offy;

		shiftMatrix_.matrix[0][3] = offx;
		shiftMatrix_.matrix[1][3] = offy;

		updateViewTransform_();
	}


	public void setQuality(int quality)
	{
		if (qualityCB_ == quality_)
		{
			quality_ = quality;
		}
		qualityCB_ = quality;
	}


	public void setScale(double new_scale)
	{
		// Scale about the center - compute new offset to keep centered
		setOffsets_(windowSize_.width / 2.0 - (new_scale / scale_) * (windowSize_.width / 2.0 - offset_.x), windowSize_.height / 2.0 - (new_scale / scale_) * (windowSize_.height / 2.0 - offset_.y));

		scale_ = new_scale;

		scaleMatrix_.matrix[0][0] = scaleMatrix_.matrix[1][1] =
				scaleMatrix_.matrix[2][2] = scale_;

		updateViewTransform_();

		getParent().postEvent(new Event((Object) this, RESIZE, (Object) this));

		paintOver();
	}


	public void setViewAngles(double theta, double phi)
	{
		rotxMatrix_.matrix[1][1] = rotxMatrix_.matrix[2][2] = -Math.cos(-phi + Math.PI / 2.0);
		rotxMatrix_.matrix[2][1] = -(rotxMatrix_.matrix[1][2] = -Math.sin(-phi + Math.PI / 2.0));

		rotzMatrix_.matrix[0][0] = rotzMatrix_.matrix[1][1] = Math.cos(-theta);
		rotzMatrix_.matrix[1][0] = -(rotzMatrix_.matrix[0][1] = -Math.sin(-theta));

		updateViewTransform_();

		xyPlane_ = (theta == 0.0 && phi == Math.PI / 2.0);

		paintOver();
	}


	public void setWireframe(boolean wireframe)
	{
		if (wireframe)
		{
			quality_ = 0;
		}
		else
		{
			quality_ = qualityCB_;
		}
	}


	// Construct a PostScript file for the graph
	public String toPS(double width, double height, double pagewidth, double pageheight, double fontsize, double margin, double overlap, boolean landscape)
	{
		String result       = new String();

		if (landscape)
		{
			double tmp  = pagewidth;
			pagewidth = pageheight;
			pageheight = tmp;
		}

		// Get graph drawn boundaries.
		DPoint graphwidth   = new DPoint();

		// Get graph drawn boundaries.
		DPoint graphheight  = new DPoint();
		getDrawBounds_(graphwidth, graphheight);

		int pages_wide      = (int) Math.ceil(width / pagewidth);
		int pages_high      = (int) Math.ceil(height / pageheight);

		// Adjust for margins and overlap.
		width -= 2.0 * margin + ((double) pages_wide - 1.0) * overlap;
		height -= 2.0 * margin + ((double) pages_high - 1.0) * overlap;

		// Convert to points.
		width *= 72.0;
		height *= 72.0;
		margin *= 72.0;
		overlap *= 72.0;
		pagewidth *= 72.0;
		pageheight *= 72.0;

		height -= fontsize;
		// Room for a label at the bottom.

		// Adjust width or height according to graph bounds.
		double ratio        = (graphwidth.y - graphwidth.x) /
				(graphheight.y - graphheight.x);
		if (ratio > width / height)
		{
			height = width / ratio;
		}
		else
		{
			width = height * ratio;
		}

		// One of theses may change if the whole bounds were not needed.
		pages_wide = (int) Math.ceil((width - overlap + 2.0 * margin) /
				(pagewidth - overlap));
		pages_high = (int) Math.ceil((height + fontsize - overlap + 2.0
				 * margin) / (pageheight - overlap));

		double scale        = width / (graphwidth.y - graphwidth.x);

		result += "%!PS-Adobe-3.0\n\n%%BoundingBox: 0 0 612 792\n";
		result += "%% Pages: " + (pages_wide * pages_high) + "\n%% EndComments\n\n";
		result += "/ellipse\n  {\n  gsave\n  newpath\n  /h exch def\n";
		result += "  /w exch def\n";
		result += "  translate\n  1 h w div scale\n  0 0 w 2 div 0 360 arc\n";
		result += "  1 w h div scale\n  stroke\n  grestore\n";
		result += "  }\ndef\n\n";

		result += "/rectangle\n  {\n  newpath\n  /h exch def\n  /w exch def\n";
		result += "  moveto\n  w 2 div h 2 div rmoveto\n";
		result += "  w neg 0 rlineto\n  0 h neg rlineto\n  w 0 rlineto\n  closepath\n";
		result += "  stroke\n}\ndef\n\n";

		result += "/label\n  {\n  gsave\n  newpath\n";
		result += "  /type exch def\n  type 2 eq\n";
		result += "  {  /h exch def  /w exch def  } if";
		result += "  translate\n  1 -1 scale\n  dup dup length /rows exch def\n";
		result += "  /sw 0 def\n  {\n    stringwidth pop /csw exch def\n";
		result += "    csw sw gt\n    { /sw csw def }\n    if";
		result += "  } forall\n";
		result += "  type 0 eq\n  { sw 2 div neg fontsize neg moveto } if\n";
		result += "  type 1 eq\n  { sw 2 div neg fontsize 2 div neg moveto } if\n";
		result += "  type 2 eq\n  { w 8 scl div sub sw div h 8 scl div sub fontsize rows mul div scale\n";
		result += "    sw 2 div neg rows 2 sub fontsize mul 2 div fontsize descent mul add moveto } if\n";
		result += "  {\n    currentpoint 3 2 roll\n";
		result += "    show\n    fontsize sub moveto\n  } forall\n";
		result += "  stroke\n  grestore\n}\ndef\n\n";

		result += "/inlabel\n  {\n  gsave\n  newpath\n";
		result += "  /h exch def\n  /w exch def\n";
		result += "  /y exch def\n  /x exch def\n";
		result += "  x y h 2 div sub translate\n  1 -1 scale\n";
		result += "  dup stringwidth pop\n  /sw exch def\n";
		result += "  w 8 scl div sub sw div h 8 scl div sub fontsize div scale\n";
		result += "  sw 2 div neg fontsize 2 div neg moveto\n  show\n";
		result += "  stroke\n  grestore\n}\ndef\n\n";

		result += "/arrow\n  {\n  newpath\n  /dy exch arrowsize mul def\n";
		result += "  /dx exch arrowsize mul def\n";
		result += "  /y exch def\n  /x exch def\n";
		result += "  /dy2 .7 dy mul def\n  /dx2 .7 dx mul def\n  x y moveto\n  dx dy rmoveto\n";
		result += "  dy2 dx2 neg rmoveto\n  x y lineto\n  dx dy rmoveto\n  dy2 neg dx2 rmoveto\n";
		result += "  x y lineto\n  stroke\n}\ndef\n\n";

		result += "/slantlabel\n  {\n  gsave\n  newpath\n  /angle exch def\n";
		result += "  translate\n  1 -1 scale\n  angle rotate\n";
		result += "  dup stringwidth pop 2 div neg fontsize 3 div moveto\n";
		result += "  show\n  stroke\n  grestore\n}\ndef\n\n";

		result += "/vgjimage\n  {\n  gsave\n  /ih exch def\n";
		result += "  /iw exch def\n  /imagedata exch def\n  /h exch def\n";
		result += "  /w exch def\n  translate\n  w h scale\n";
		result += "  iw ih 8 [iw 0 0 ih iw 2 div ih 2 div]";
		result += " { imagedata } image\n  grestore\n}\ndef\n\n";

		// Output the images - with no repeats.
		Node tmpnode;
		Image tmpimage;
		int nnodes          = graph_.numberOfNodes();
		Image[] images      = new Image[nnodes];
		int image_count     = 0;
		for (tmpnode = graph_.firstNode(); tmpnode != null;
				tmpnode = graph_.nextNode(tmpnode))
		{
			if ((tmpimage = tmpnode.getImage()) != null)
			{
				int i;
				for (i = 0; i < image_count; i++)
				{
					if (images[i] == tmpimage)
					{
						break;
					}
				}
				if (i == image_count)
				{
					images[image_count++] = tmpimage;
				}
			}
		}

		int i;
		for (i = 0; i < image_count; i++)
		{
			result += "/image" + i + "  {\n\n" + Node.imagePS(images[i]) + "}\ndef\n\n";
		}

		result += "/graph\n{\n";
		result += "0 396 translate\n1 1 neg scale\n0 neg 396 neg translate\n\n";

		result += "/scl " + scale + " def\nscl scl scale\n.6 scl div setlinewidth\n";
		result += "/fontsize " + fontsize + " scl div def\n";
		result += "/Courier findfont fontsize scalefont setfont\n";
		result += "/Courier findfont\nbegin\nFontType 1 eq\n";
		result += "{ FontBBox }\n{ -2 -2 8 8 } ifelse\nend\n";
		result += "/y2 exch def pop /y1 exch def pop\n";
		result += "y1 neg y2 y1 sub div /descent exch def\n";
		result += "/arrowsize 5 scl div def\n";
		result += PSnum_(-graphwidth.x + margin / scale) + PSnum_(-graphheight.x + margin / scale) + "translate\n\n";

		// First output the node images.
		for (tmpnode = graph_.firstNode(); tmpnode != null;
				tmpnode = graph_.nextNode(tmpnode))
		{
			if ((tmpimage = tmpnode.getImage()) != null)
			{
				for (i = 0; i < image_count; i++)
				{
					if (images[i] == tmpimage)
					{
						result += tmpnode.toPSimage(i, viewTransform_);
					}
				}
			}
		}

		for (tmpnode = graph_.firstNode(); tmpnode != null;
				tmpnode = graph_.nextNode(tmpnode))
		{
			result += tmpnode.toPS(viewTransform_);
		}

		Enumeration edges   = graph_.getEdges();
		boolean directed    = graph_.isDirected();
		while (edges.hasMoreElements())
		{
			Edge edge  = (Edge) (edges.nextElement());
			Node head  = edge.head();
			Node tail  = edge.tail();

			if ((head.getVisibleGroupRoot() == tail.getVisibleGroupRoot() &&
					head != tail) || (head == tail && !head.isVisible()))
			{
				// Inter-group edge.
				continue;
			}

			if (directed || tail.getIndex() <= head.getIndex())
			{
				result += edge.toPS(viewTransform_, xyPlane_, directed);
			}
		}

		//drawAxes_(back_graphics);

		result += "}\ndef\n\n";

		int page            = 1;
		for (int w = 0; w < pages_wide; w++)
		{
			for (int h = 0; h < pages_high; h++)
			{
				result += "%%Page:" + page + " " + page + "\n";
				if (landscape)
				{
					result += "792 0 translate\n90 rotate\n";
				}

				result += PSnum_(((double) w) * (-pagewidth + overlap)) + PSnum_(((double) h) * (pageheight - overlap)) + "translate\n";
				result += "graph\n";
				result += "showpage\n\n";

				page++;
			}
		}

		return result;
	}


	public void toPict(double width, double height, double pagewidth, double pageheight, double fontsize, double margin, double overlap, boolean landscape, RandomAccessFile out_)
	{

		if (landscape)
		{
			double tmp  = pagewidth;
			pagewidth = pageheight;
			pageheight = tmp;
		}

		// Get graph drawn boundaries.
		DPoint graphwidth   = new DPoint();

		// Get graph drawn boundaries.
		DPoint graphheight  = new DPoint();
		getDrawBounds_(graphwidth, graphheight);

		int pages_wide      = (int) Math.ceil(width / pagewidth);
		int pages_high      = (int) Math.ceil(height / pageheight);

		// Adjust for margins and overlap.
		width -= 2.0 * margin + ((double) pages_wide - 1.0) * overlap;
		height -= 2.0 * margin + ((double) pages_high - 1.0) * overlap;

//		System.out.println("this is width " + width);
//		System.out.println("this is height " + height);
//		System.out.println("this is margin " + margin);
//		System.out.println("this is overlap " + overlap);
//		System.out.println("this is pagewidth " + pagewidth);
//		System.out.println("this is pageheight " + pageheight);

		// Convert to points.
		width *= 72.0;
		height *= 72.0;
		margin *= 72.0;
		overlap *= 72.0;
		pagewidth *= 72.0;
		pageheight *= 72.0;

//		System.out.println("this is width " + width);
//		System.out.println("this is height " + height);
//		System.out.println("this is margin " + margin);
//		System.out.println("this is overlap " + overlap);
//		System.out.println("this is pagewidth " + pagewidth);
//		System.out.println("this is pageheight " + pageheight);

		height1_ = (short) pageheight;
		width1_ = (short) pagewidth;

		height -= fontsize;
		// Room for a label at the bottom.

		// Adjust width or height according to graph bounds.
		double ratio        = (graphwidth.y - graphwidth.x) /
				(graphheight.y - graphheight.x);
		if (ratio > width / height)
		{
			height = width / ratio;
		}
		else
		{
			width = height * ratio;
		}

		// One of theses may change if the whole bounds were not needed.
		pages_wide = (int) Math.ceil((width - overlap + 2.0 * margin) /
				(pagewidth - overlap));
		pages_high = (int) Math.ceil((height + fontsize - overlap + 2.0
				 * margin) / (pageheight - overlap));

//		System.out.println("this is page_width " + pages_wide);
//		System.out.println("this is page_height " + pages_high);

		double scale        = width / (graphwidth.y - graphwidth.x);

		// Output the images - with no repeats.
		Node tmpnode;
		Image tmpimage;
		int nnodes          = graph_.numberOfNodes();
		Image[] images      = new Image[nnodes];
		int image_count     = 0;
		for (tmpnode = graph_.firstNode(); tmpnode != null;
				tmpnode = graph_.nextNode(tmpnode))
		{
			if ((tmpimage = tmpnode.getImage()) != null)
			{
				int i;
				for (i = 0; i < image_count; i++)
				{
					if (images[i] == tmpimage)
					{
						break;
					}
				}
				if (i == image_count)
				{
					images[image_count++] = tmpimage;
				}
			}
		}
		//////////This should be right just loading images

		// int i;
		// for (i = 0; i < image_count; i++)
		// {
		//result += "/image" + i + "  {\n\n" + Node.imagePS(images[i]) +		  "}\ndef\n\n";
		// }
		////////this above I don't know if it is needed pretty sure no


		// First output the node images.
		// for (tmpnode = graph_.firstNode(); tmpnode != null;	tmpnode = graph_.nextNode(tmpnode))
		// {
		// if ((tmpimage = tmpnode.getImage()) != null)
		// {
		// for (i = 0; i < image_count; i++)
		// {
		// if (images[i] == tmpimage)
		// {
		// }
		// }
		// }
		// }

		//result += tmpnode.toPSimage(i, viewTransform_);
		double minx         = 10000;
		//result += tmpnode.toPSimage(i, viewTransform_);
		double tempx;
		//result += tmpnode.toPSimage(i, viewTransform_);
		double minwid       = 0;
		//result += tmpnode.toPSimage(i, viewTransform_);
		double maxx         = -10000;
		//result += tmpnode.toPSimage(i, viewTransform_);
		double maxwid       = -10000;
		double miny         = 10000;
		double tempy;
		double minhigh      = 0;
		double maxy         = -10000;
		double maxhigh      = -10000;
		double totwid       = 0;
		double tothigh      = 0;
		//this loop should find the min x and y coordinates
		for (tmpnode = graph_.firstNode(); tmpnode != null; tmpnode = graph_.nextNode(tmpnode))
		{
			tempx = tmpnode.getX();
			tempy = tmpnode.getY();
			minwid = tmpnode.getWidth(viewTransform_);
			minhigh = tmpnode.getHeight(viewTransform_);

			if (tempx < minx)
			{
				minx = tempx;
			}
			if (tempy < miny)
			{
				miny = tempy;
			}
			if (tempx > maxx)
			{
				maxx = tempx;
			}
			if (tempy > maxy)
			{
				maxy = tempy;
			}
			if (minwid > maxwid)
			{
				maxwid = minwid;
			}
			if (minhigh > maxhigh)
			{
				maxhigh = minhigh;
			}
		}

//		System.out.println("this is min x " + minx);
//		System.out.println("this is min y " + miny);
//		System.out.println("this is minwid " + minwid);
//		System.out.println("this is minhigh" + minhigh);
//		System.out.println("this is max x " + maxx);
//		System.out.println("this is max y " + maxy);
//		System.out.println("this is maxwid " + maxwid);
//		System.out.println("this is maxhigh" + maxhigh);

		totwid = maxx - minx;
		//		System.out.println("this is totwid" + totwid);
		totwid = Math.abs(totwid);
		//		System.out.println("this is totwid" + totwid);
		totwid += 4 * maxwid;
		//		System.out.println("this is totwid" + totwid);

		tothigh = maxy - miny;
		tothigh = Math.abs(tothigh);
		tothigh += 4 * maxhigh;
		if (minx < 0)
		{
			minx = minx - 2 * minx;
			//			System.out.println("this is min x " + minx);
			minx = minx + 2 * maxwid;
		}
		if (miny < 0)
		{
			miny = miny - 2 * miny;
			//			System.out.println("this is min y " + miny);
			miny = miny + maxhigh;
		}
		width1_ = (short) totwid;
		height1_ = (short) tothigh;
//		System.out.println("this is min x " + minx);
//		System.out.println("this is min y " + miny);

		//minx = minwid *.25;

//		System.out.println("this is pagewidth " + width1_);
//		System.out.println("this is pageheight " + height1_);

		try
		{
			writeHeader(out_);
		}
		catch (IOException f)
		{
			f.printStackTrace();
		}

		//result += tmpnode.toPS(viewTransform_);
		//this loop should draw all of the ovals
		for (tmpnode = graph_.firstNode(); tmpnode != null; tmpnode = graph_.nextNode(tmpnode))
		{
			tmpnode.drawOval(out_, viewTransform_, (short) minx, (short) miny);
		}

		//this loop should hit all edges
		Enumeration edges   = graph_.getEdges();
		boolean directed    = graph_.isDirected();
		while (edges.hasMoreElements())
		{
			Edge edge  = (Edge) (edges.nextElement());
			Node head  = edge.head();
			Node tail  = edge.tail();

			if ((head.getVisibleGroupRoot() == tail.getVisibleGroupRoot() &&
					head != tail) || (head == tail && !head.isVisible()))
			{
				// Inter-group edge.
				continue;
			}
			//this should draw all of the edges
			if (directed || tail.getIndex() <= head.getIndex())
			{
			}
			edge.toPict(viewTransform_, xyPlane_, directed, out_, minx, miny);
		}

		//drawAxes_(back_graphics);

		// result += "}\ndef\n\n";

		int page            = 1;
		for (int w = 0; w < pages_wide; w++)
		{
			for (int h = 0; h < pages_high; h++)
			{
				//result += "%%Page:" + page + " " + page + "\n";
				if (landscape)
				{
				}
				//result += "792 0 translate\n90 rotate\n";


				//result += PSnum_(((double)w) * (-pagewidth + overlap)) +	 PSnum_(((double)h) * (pageheight - overlap)) + "translate\n";
				//result += "graph\n";
				//result += "showpage\n\n";

				page++;
			}
		}

		long thesize        = 0;
		short size          = 0;
		long offset         = 512;
		try
		{
			writeEnd(out_);
			thesize = out_.length();
			out_.seek(offset);
			size = (short) (thesize - offset);
			//				System.out.println("File size = "+size);
			out_.writeShort(size);
		}
		catch (IOException f)
		{
			f.printStackTrace();
		}
		/*
		    try
		    {
		    here is where we need to add the size of file to the pict file.
		    }
		    catch(IOException f)
		    {
		    f.printStackTrace();
		    }
		  */
		return;
	}


/////////////////////////////////write Header////////////////////////////////
	public void writeHeader(RandomAccessFile out_)
		throws IOException
	{
		byte[] buf  = new byte[64];

		// write out the leading 512 bytes of header
		for (int i = 512 / 64; --i >= 0; )
		{
			out_.write(buf);
		}
		out_.writeShort(0);
		// size field - 0 unused for v2
		// rectangle, top left Y, top left X, bottom right Y, bottom right X
		// Rect 8 bytes (top, left, bottom, right: integer)
		// default of dots per inch of 72 is saved
		// therefore calculate the size of the area
		out_.writeShort(0);
		// top left Y
		out_.writeShort(0);
		// top left X
		out_.writeShort(height1_);
		// bottom right Y
		out_.writeShort(width1_);
		// bottom right X

		out_.writeShort(0x1101);
		// v1 opcode 11 version 01
		out_.writeShort(0x0100);
		//0x0100 version 1	// v2 version 0x2FF
		/*
		    version 2 extended header
		    out_.writeShort(0x0C00);		// v2 header opcode
		    out_.writeShort(0xFFFE);	// -1 for v2
		    out_.writeShort(0);			// reserved
		    out_.writeInt(0x480000);   	// original horizontal pixels/inch
		    out_.writeInt(0x480000);   		// original vertical pixels/inch
		    out_.writeShort(0);				//top left corner X
		    out_.writeShort(0);				//top left corner Y
		    out_.writeShort(height1_);	//bottom Right corner X
		    out_.writeShort(width1_);	//bottom Right corner Y
		    out_.writeShort(0);   		//reserved
		    out_.writeShort(0x0001); //clip region opcode
		  */
		out_.write(0x0a);
		//0A); //set region size opcode one byte
		out_.writeShort(0x0000);
		out_.writeShort(0x0000);
		out_.writeShort(height1_);
		out_.writeShort(width1_);

	}


//////////////////write End////////////////////////////////////
	public void writeEnd(RandomAccessFile out_)
		throws IOException
	{
		out_.writeShort(0x00FF);
		// end
	}


///////////////////////////////////////////////////////////////////////////

//////////////////////////////////////end of my code twiddling/////////////
////////////////////////////////////////////////////////////////////////////
	public void unselectItems()
	{
		if (selectedNode_ == null && selectedEdge_ == null)
		{
			return;
		}
		for (Node tmpnode = graph_.firstNode(); tmpnode != null;
				tmpnode = graph_.nextNode(tmpnode))
		{
			tmpnode.setSelected(false);
		}
		selectedNode_ = null;

		Enumeration edges  = graph_.getEdges();
		while (edges.hasMoreElements())
		{
			((Edge) (edges.nextElement())).selected = false;
		}
		selectedEdge_ = null;

		paintOver();
	}


	public void update(boolean adjust_bounds)
	{
		unselectItems();
		currentMouseAction_ = 0;

		if (adjust_bounds)
		{
			computeBounds_();
			getParent().postEvent(new Event((Object) this, RESIZE, (Object) this));

			String string  = getLabel_(0, 0, 0, false);
			getParent().postEvent(new Event((Object) this, OffsetCanvas.LABEL, string));

		}
		paintOver();

		getParent().postEvent(new Event((Object) this, UPDATE, null));
	}


	private synchronized void updateViewTransform_()
	{
		viewTransform_ = new Matrix44(shiftMatrix_);
		viewTransform_.mult(scaleMatrix_);
		viewTransform_.mult(rotxMatrix_);
		viewTransform_.mult(rotzMatrix_);

		moveTransform_ = new Matrix44(rotzMatrix_);
		moveTransform_.matrix[1][0] = -moveTransform_.matrix[1][0];
		moveTransform_.matrix[0][1] = -moveTransform_.matrix[0][1];

		Matrix44 tmp_matrix  = new Matrix44(rotxMatrix_);
		tmp_matrix.matrix[2][1] = -tmp_matrix.matrix[2][1];
		tmp_matrix.matrix[1][2] = -tmp_matrix.matrix[1][2];
		moveTransform_.mult(tmp_matrix);

		tmp_matrix.setTo(scaleMatrix_);
		tmp_matrix.matrix[0][0] = 1.0 / tmp_matrix.matrix[0][0];
		tmp_matrix.matrix[1][1] = 1.0 / tmp_matrix.matrix[1][1];
		tmp_matrix.matrix[2][2] = 1.0 / tmp_matrix.matrix[2][2];
		moveTransform_.mult(tmp_matrix);

		tmp_matrix.setTo(shiftMatrix_);
		tmp_matrix.matrix[0][3] = -tmp_matrix.matrix[0][3];
		tmp_matrix.matrix[1][3] = -tmp_matrix.matrix[1][3];
		moveTransform_.mult(tmp_matrix);

		viewTransform_.scale = scale_;
	}


	public DRect windowRect()
	{
		return new DRect(-offset_.x / scale_, -offset_.y / scale_, windowSize_.width / scale_, windowSize_.height / scale_);
	}
}

