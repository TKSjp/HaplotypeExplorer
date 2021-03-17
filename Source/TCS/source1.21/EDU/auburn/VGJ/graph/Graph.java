package EDU.auburn.VGJ.graph;

/*
 *File: Graph.java
 *
 * Date      Authors
 * 4/29/96   Larry Barowski, David E. Daniels
 *
 */
import java.io.IOException;
import java.util.Hashtable;
import java.util.Enumeration;
import java.awt.Point;

import EDU.auburn.VGJ.util.DPoint3;
import EDU.auburn.VGJ.util.DDimension3;

import java.lang.Math;
import java.lang.System;

/**
 *	A class for representing a graph abstractly.
 *	</p>Here is the <a href="../graph/Graph.java">source</a>.
 *
 * @author     Larry Barowski
 * @created    July 2, 2003
 */

public class Graph implements Cloneable
{
	/**
	 *nodeList_ holds the graph
	 */
	private NodeList nodeList_;
	private Hashtable idHash_;
	private boolean directed_   = false;
	private int lastTopId_;
	private Hashtable edges_;  // For edges with intermediate points.



	/**
	 * construct empty graph
	 */
	public Graph()
	{
		nodeList_ = new NodeList();
		idHash_ = new Hashtable();
		edges_ = new Hashtable();
		lastTopId_ = 0;
	}


	public Graph(GMLobject gml)
	{
		nodeList_ = new NodeList();
		idHash_ = new Hashtable();
		edges_ = new Hashtable();
		lastTopId_ = 0;

		directed_ = false;
		Integer tmp;
		if ((tmp = (Integer) gml.getValue("directed", GMLobject.GMLinteger)) != null)
		{
			directed_ = (tmp.intValue() != 0);
		}

		Node node;
		GMLobject nodegml;
		for (nodegml = gml.getGMLSubObject("node", GMLobject.GMLlist, false);
				nodegml != null; nodegml = gml.getNextGMLSubObject())
		{
			node = new Node(nodegml);

			Integer id;
			if ((id = node.getIdObject()) != null)
			{
				if (!idHash_.containsKey(id))
				{  // Discard nodes with duplicate id's.

					idHash_.put(id, node);
					nodeList_.addNode(node);
				}
			}
			else
			{
				nodeList_.addNode(node);
			}
		}

		GMLobject edgegml;
		for (edgegml = gml.getGMLSubObject("edge", GMLobject.GMLlist, false);
				edgegml != null; edgegml = gml.getNextGMLSubObject())
		{
			Integer source;
			Integer target;

			source = (Integer) edgegml.getValue("source", GMLobject.GMLinteger);
			target = (Integer) edgegml.getValue("target", GMLobject.GMLinteger);
      
			if (source != null && target != null)
			{
				Node sourcenode;
				Node targetnode;
				sourcenode = (Node) idHash_.get(source);
				targetnode = (Node) idHash_.get(target);
				if (sourcenode != null && targetnode != null)
				{
					insertEdge(new Edge(sourcenode, targetnode, edgegml));
				}
			}
		}

		validateIds();

		// Initialize the groups.
		Node tmpnode;
		for (tmpnode = firstNode(); tmpnode != null; tmpnode = nextNode(tmpnode))
		{
			if (tmpnode.inGroup())
			{
				tmpnode.groupNode_ = (Node) idHash_.get(new Integer(tmpnode.groupNodeId_));
				if (tmpnode.groupNode_ != null)
				{
					tmpnode.groupNode_.isGroup_ = true;
					tmpnode.groupNode_.setChild(tmpnode.getIndex());
				}
			}
		}
	}


	/**
	 * construct empty graph with direction
	 *
	 * @param  yesorno  Description of the Parameter
	 */
	public Graph(boolean yesorno)
	{
		nodeList_ = new NodeList();
		idHash_ = new Hashtable();
		edges_ = new Hashtable();
		lastTopId_ = 0;
		directed_ = yesorno;
	}


	private void adjustGroupChildren_(Node node, double dx, double dy, double dz, double rw, double rh, double rd)
	{
		int child_index;
		Node child;

		for (child_index = node.firstChild(); child_index != -1;
				child_index = node.nextChild())
		{
			child = getNodeFromIndex(child_index);
			if (child.isGroup() && !child.groupActive())
			{
				adjustGroupChildren_(child, dx, dy, dz, rw, rh, rd);
			}
			else
			{
				child.x_ += dx;
				child.y_ += dy;
				child.z_ += dz;
				child.width_ *= rw;
				child.height_ *= rh;
				child.depth_ *= rd;
			}
		}

	}
	// children(int n)===========================================================

	/**
	 * @param  n   Description of the Parameter
	 * @return     Description of the Return Value
	 * @returns    a set of all the nodes n leads to
	 */
	public Set children(int n)
	{
		return nodeList_.nodeFromIndex(n).getChildren();
	}
	// clone()===================================================================

	/**
	 * makes a copy of the current graph
	 *
	 * @return    copy of current graph
	 */
	public Object clone()
	{
		Graph copy;

		try
		{
			copy = (Graph) super.clone();
			copy.nodeList_ = (NodeList) nodeList_.clone();
			copy.idHash_ = new Hashtable();
			copy.edges_ = new Hashtable();
			copy.directed_ = directed_;
			copy.lastTopId_ = lastTopId_;

			Enumeration edges  = edges_.elements();
			while (edges.hasMoreElements())
			{
				Edge edge     = (Edge) (edges.nextElement());
				Node head;
				Node tail;
				tail = copy.nodeList_.nodeFromIndex(edge.tail_.getIndex());
				head = copy.nodeList_.nodeFromIndex(edge.head_.getIndex());

				Edge newedge  = new Edge(tail, head, edge);
				copy.edges_.put(new Point(tail.index_, head.index_), newedge);
			}

			Node tmpnode;
			for (tmpnode = copy.firstNode(); tmpnode != null; tmpnode = copy.nextNode(tmpnode))
			{
				copy.idHash_.put(new Integer(tmpnode.getId()), tmpnode);
			}

			return copy;
		}
		catch (CloneNotSupportedException e)
		{
		}
		return null;
	}


	/**
	 * Copy the properties of another graph. This assumes the other graph
	 * will be subsequently deleted - otherwise use copy(clone(graph_to_copy)).
	 *
	 * @param  newgraph  Description of the Parameter
	 */
	public void copy(Graph newgraph)
	{
		directed_ = newgraph.directed_;
		nodeList_ = newgraph.nodeList_;
		idHash_ = newgraph.idHash_;
		edges_ = newgraph.edges_;
		lastTopId_ = newgraph.lastTopId_;
	}


	/**
	 * Convert dummy nodes to edge paths.
	 */
	public void dummysToEdgePaths()
	{
		Node tmpnode;
		for (tmpnode = firstNode(); tmpnode != null; tmpnode = nextNode(tmpnode))
		{
			if (!tmpnode.isDummy_)
			{
				for (int child = tmpnode.firstChild(); child != -1;
						child = tmpnode.nextChild())
				{
					Node childnode  = getNodeFromIndex(child);
					Node tmpchild   = childnode;
					int numdummies  = 0;

					String label    = getEdge(tmpnode.index_, child).getLabel();

					// Only the first child is used.
					while (tmpchild != null && tmpchild.isDummy_)
					{
						numdummies++;
						tmpchild = getNodeFromIndex(tmpchild.firstChild());
					}
					if (numdummies > 0 && tmpchild != null)
					{
						DPoint3[] edge_points  = new DPoint3[numdummies];
						tmpchild = childnode;
						int dummy              = 0;
						while (tmpchild.isDummy_)
						{
							edge_points[dummy++] = new
									DPoint3(tmpchild.getPosition3());
							tmpchild = getNodeFromIndex(tmpchild.firstChild());
						}
						insertEdge(tmpnode.index_, tmpchild.index_, edge_points, label);
					}
				}
			}
		}

		// Remove all dummy nodes.
		for (tmpnode = firstNode(); tmpnode != null;
				tmpnode = nextNode(tmpnode))
		{
			if (tmpnode.isDummy_)
			{
				removeNode(tmpnode);
			}
		}
	}


	private void fillBackEdges_()
	{
		Enumeration edges  = edges_.elements();
		while (edges.hasMoreElements())
		{
			Edge edge  = (Edge) (edges.nextElement());
			if (edges_.get(new Point(edge.head().getIndex(), edge.tail().getIndex())) == null)
			{
				int n1                    = edge.tail().getIndex();
				int n2                    = edge.head().getIndex();
				edge.head().setChild(n1);
				DPoint3[] reverse_points;
				if (n1 > n2)
				{
					DPoint3[] points  = edge.points();
					int length        = points.length;
					reverse_points = new DPoint3[length];
					for (int i = 0; i < length; i++)
					{
						reverse_points[i] = points[length - 1 - i];
					}
				}
				else
				{
					reverse_points = new DPoint3[0];
				}
				edges_.put(new Point(n2, n1), new Edge(edge.head(), edge.tail(), reverse_points, true));
			}
		}
	}
	//firstAvailable()===========================================================

	public int firstAvailable()
	{
		return nodeList_.getFirstAvailable();
	}
	// firstNode()===============================================================

	/**
	 * @return     Description of the Return Value
	 * @returns    the first node of the graph
	 */
	public Node firstNode()
	{
		return nodeList_.firstNode();
	}
	//firstNodeIndex()===========================================================

	/**
	 * @return     Description of the Return Value
	 * @returns    the  index of the first node in graph.nodeList
	 */
	public int firstNodeIndex()
	{
		return nodeList_.firstNodeIndex();
	}


	public Edge getEdge(int n1, int n2)
	{
		return (Edge) (edges_.get(new Point(n1, n2)));
	}


	/**
	 * Get the path points for an edge.
	 *
	 * @param  n1  Description of the Parameter
	 * @param  n2  Description of the Parameter
	 * @return     The edgePathPoints value
	 */
	public DPoint3[] getEdgePathPoints(int n1, int n2)
	{
		Edge edge  = (Edge) (edges_.get(new Point(n1, n2)));
		if (edge == null)
		{
			return null;
		}

		return edge.points();
	}


	public Enumeration getEdges()
	{
		return edges_.elements();
	}


	private int getGroupCoordinates_(Node node, DPoint3 pos, DDimension3 size)
	{
		int child_index;
		Node child;
		int count        = 0;

		for (child_index = node.firstChild(); child_index != -1;
				child_index = node.nextChild())
		{
			child = getNodeFromIndex(child_index);
			if (child.isGroup() && !child.groupActive())
			{
				count += getGroupCoordinates_(child, pos, size);
			}
			else
			{
				count++;
				size.width += child.width_ * child.width_;
				size.height += child.height_ * child.height_;
				size.depth += child.depth_ * child.depth_;
				pos.x += child.x_;
				pos.y += child.y_;
				pos.z += child.z_;
			}
		}

		return count;
	}
	//getIndexFromNode (Node node)===============================================

	/**
	 * @param  node  Description of the Parameter
	 * @return       The indexFromNode value
	 * @returns      the integer value of the index for the node
	 */
	public int getIndexFromNode(Node node)
	{
		return node.index_;
	}


	public Node getNodeFromId(int id)
	{
		return (Node) (idHash_.get(new Integer(id)));
	}
	//getNodeFromIndex(int index)================================================

	/**
	 * @param  index  Description of the Parameter
	 * @return        The nodeFromIndex value
	 * @returns       the node at  the 'index'
	 */
	public Node getNodeFromIndex(int index)
	{
		return nodeList_.nodeFromIndex(index);
	}


	public void group(Node node, boolean state)
	{
		int child_index;
		Node child;
		if (state == true && node.groupNode_ != null)
		{
			node.groupNode_.groupActive_ = true;
			Node groupnode    = node.groupNode_;
			markGroupChildren_(groupnode, true);

			DPoint3 pos       = new DPoint3(0.0, 0.0, 0.0);
			DDimension3 size  = new DDimension3(0.0, 0.0, 0.0);
			int n;
			n = getGroupCoordinates_(groupnode, pos, size);

			pos.x /= n;
			pos.y /= n;
			pos.z /= n;
			size.width = Math.sqrt(size.width);
			size.height = Math.sqrt(size.height);
			size.depth = Math.sqrt(size.depth);

			groupnode.setPosition(pos);
			groupnode.setBoundingBox(size);
			groupnode.grouppos_ = pos;
			groupnode.groupbox_ = size;
		}
		else if (state == false && node.isGroup_)
		{

			node.groupActive_ = false;
			markGroupChildren_(node, false);

			node.setSelected(false);
			double dx;
			double dy;
			double dz;
			double rw;
			double rh;
			double rd;
			dx = node.x_ - node.grouppos_.x;
			dy = node.y_ - node.grouppos_.y;
			dz = node.z_ - node.grouppos_.z;
			rw = node.width_ / node.groupbox_.width;
			rh = node.height_ / node.groupbox_.height;
			rd = node.depth_ / node.groupbox_.depth;

			adjustGroupChildren_(node, dx, dy, dz, rw, rh, rd);
		}
	}
	//highestIndex()=====================================================================

	public int highestIndex()
	{
		return nodeList_.highestIndex();
	}
	// insertEdge(int n1, int n2)

	/**
	 * insert an edge between two nodes
	 *
	 * @param  n1  Description of the Parameter
	 * @param  n2  Description of the Parameter
	 */
	public void insertEdge(int n1, int n2)
	{
		insertEdge(n1, n2, new DPoint3[0]);
	}


	/**
	 * Insert an edge with path points.
	 *
	 * @param  n1      Description of the Parameter
	 * @param  n2      Description of the Parameter
	 * @param  points  Description of the Parameter
	 */
	public void insertEdge(int n1, int n2, DPoint3[] points)
	{
		insertEdge(new Edge(nodeList_.nodeFromIndex(n1), nodeList_.nodeFromIndex(n2), points, false));
	}


	/**
	 * Insert an edge with path points and a label.
	 *
	 * @param  n1      Description of the Parameter
	 * @param  n2      Description of the Parameter
	 * @param  points  Description of the Parameter
	 * @param  label   Description of the Parameter
	 */
	public void insertEdge(int n1, int n2, DPoint3[] points, String label)
	{
		Edge edge  = new Edge(nodeList_.nodeFromIndex(n1), nodeList_.nodeFromIndex(n2), points, false);
		insertEdge(edge);
		edge.setLabel(label);
	}


	/**
	 * Insert an edge.
	 *
	 * @param  edge  Description of the Parameter
	 */
	public void insertEdge(Edge edge)
	{
		Node tempnode;

		int n1            = edge.tail().getIndex();
		int n2            = edge.head().getIndex();

		edges_.remove(new Point(n1, n2));
		if (!directed_)
		{
			edges_.remove(new Point(n2, n1));
		}

		edge.tail().setChild(n2);
		if (!directed_)
		{
			edge.head().setChild(n1);
		}

		edges_.put(new Point(n1, n2), edge);
		DPoint3[] points  = edge.points();
		if (!directed_ && n1 != n2)
		{
			int length                = points.length;
			DPoint3[] reverse_points  = new DPoint3[length];
			for (int i = 0; i < length; i++)
			{
				reverse_points[i] = points[length - 1 - i];
			}
      
			edges_.put(new Point(n2, n1), new Edge(edge.head(), edge.tail(), reverse_points, false,edge));
		}
	}
	// insertNode()==============================================================

	/**
	 * insert new node into graph; no initial connections
	 *
	 * @return    index of the new node
	 */
	public int insertNode()
	{
		return insertNode(false);
	}


	/**
	 * Insert new node or dummy node into the graph.
	 *
	 * @param  dummy  Description of the Parameter
	 * @return        index of the new node
	 */
	public int insertNode(boolean dummy)
	{
		Node node;

		node = new Node(dummy);
		nodeList_.addNode(node);

		node.haveId_ = true;
		node.id_ = lastTopId_;
		idHash_.put(new Integer(lastTopId_), node);
		do
		{
			lastTopId_++;
		} while (idHash_.containsKey(new Integer(lastTopId_)));

		return node.index_;
	}
	// insertNodeAt(int index)===================================================

	/**
	 * insert new node into graph; into the nodelist at the index
	 *
	 * @param  index            Description of the Parameter
	 * @exception  IOException  Description of the Exception
	 */
	public void insertNodeAt(int index)
		throws IOException
	{
		Node node;
		IOException e  = new IOException("Node " + index + " already exist.");

		if (nodeList_.nodeFromIndex(index) == null)
		{
			node = new Node();
			nodeList_.addNodeAt(node, index);

			node.haveId_ = true;
			node.id_ = lastTopId_;
			idHash_.put(new Integer(lastTopId_), node);
			do
			{
				lastTopId_++;
			} while (idHash_.containsKey(new Integer(lastTopId_)));
		}
		else
		{
			throw e;
		}
	}
	// boolean isDirected()======================================================

	/**
	 * function to determine the graph type (directed or undirected)
	 *
	 * @return    boolean value
	 */
	public boolean isDirected()
	{
		if (directed_)
		{
			return directed_;
		}
		else
		{
			return directed_;
		}
	}


	public void killGroup(Node node)
	{
		if (node.isGroup())
		{
			int child_index;
			Node child;

			for (child_index = node.firstChild(); child_index != -1;
					child_index = node.nextChild())
			{
				node.clearChild(child_index);
				child = getNodeFromIndex(child_index);
				child.inActiveGroup_ = false;
				child.groupNode_ = null;

				if (child.isGroup() && !child.groupActive_)
				{
					markGroupChildren_(child, false);
				}
			}

			removeNode(node.getIndex());
		}
	}


	private void markGroupChildren_(Node node, boolean state)
	{
		int child_index;
		Node child;

		for (child_index = node.firstChild(); child_index != -1;
				child_index = node.nextChild())
		{
			child = getNodeFromIndex(child_index);
			child.inActiveGroup_ = state;
			if (child.isGroup_ && (state == true || !child.groupActive_))
			{
				markGroupChildren_(child, state);
			}
		}

	}
	//nextNode(Node node)========================================================

	/**
	 * @param  node  Description of the Parameter
	 * @return       Description of the Return Value
	 * @returns      the next node after node
	 */
	public Node nextNode(Node node)
	{
		return nodeList_.nextNode(node);
	}
	//nextNodeIndex(int index)===================================================

	/**
	 * @param  index  Description of the Parameter
	 * @return        Description of the Return Value
	 * @returns       the index of the next node after the current index
	 */
	public int nextNodeIndex(int index)
	{
		return nodeList_.nextNodeIndex(index);
	}


	public Node nodeFromIndex(int index)
	{
		return nodeList_.nodeFromIndex(index);
	}
	// numberOfNodes()===========================================================

	/**
	 * @return     Description of the Return Value
	 * @returns    the number of nodes in the graph
	 */
	public int numberOfNodes()
	{
		return nodeList_.count();
	}


	/**
	 * Re-index so the indexes go from 0 to number of
	 *  nodes - 1.
	 */
	public void pack()
	{
		int map[];
		int n               = numberOfNodes();

		if (n == nodeList_.highestIndex() + 1)
		{
			return;
		}

		int highest_index   = nodeList_.highestIndex();
		map = new int[highest_index + 1];

		// Re-map the node indexes.
		int node_index;

		// Re-map the node indexes.
		int new_node_index;
		Node tmpnode;
		for (node_index = nextNodeIndex(n - 1); node_index != -1;
				node_index = nextNodeIndex(node_index))
		{
			tmpnode = getNodeFromIndex(node_index);
			nodeList_.addNode(tmpnode);  // This will re-map the index.
			map[node_index] = tmpnode.index_;
			nodeList_.removeNodeAt(node_index);
		}

		// Correct child lists.
		for (tmpnode = firstNode(); tmpnode != null; tmpnode = nextNode(tmpnode))
		{
			for (int child = tmpnode.searchNextChild(n - 1); child != -1;
					child = tmpnode.searchNextChild(child + 1))
			{
				if (child >= n)
				{
					tmpnode.setChild(map[child]);
					tmpnode.clearChild(child);
				}
			}
		}

		// Correct the edge hash table.
		Enumeration keys    = edges_.keys();
		while (keys.hasMoreElements())
		{
			Point key  = (Point) (keys.nextElement());
			if (key.x >= n || key.y >= n)
			{
				if (key.x > highest_index || key.y > highest_index)
				{  // Must be a remnant.
					edges_.remove(key);
				}
				else
				{
					Edge element  = (Edge) edges_.get(key);
					edges_.remove(key);
					if (key.x >= n)
					{
						key.x = map[key.x];
					}
					if (key.y >= n)
					{
						key.y = map[key.y];
					}
					edges_.put(key, element);
				}
			}
		}
	}
	// parents(int n)============================================================

	/**
	 * @param  n   Description of the Parameter
	 * @return     Description of the Return Value
	 * @returns    a set of all the nodes leading to n
	 */
	public Set parents(int n)
	{
		Set parents  = new Set();
		Node node;

		for (node = nodeList_.firstNode(); node != null; node = nodeList_.nextNode(node))
		{
			if (node.hasChild(n))
			{
				parents.includeElement(node.index_);
			}
		}
		return parents;
	}
	// removeEdge(int n1, int n2)================================================

	/**
	 * remove the connection from n1 to n2 but leave the nodes in place
	 *
	 * @param  n1  Description of the Parameter
	 * @param  n2  Description of the Parameter
	 */
	public void removeEdge(int n1, int n2)
	{
		Node tempnode  = nodeList_.nodeFromIndex(n1);
		tempnode.clearChild(n2);
		edges_.remove(new Point(n1, n2));
		if (!directed_ && (n1 != n2))
		{
			tempnode = nodeList_.nodeFromIndex(n2);
			tempnode.clearChild(n1);
			edges_.remove(new Point(n2, n1));
		}

	}


	public void removeEdge(Edge edge)
	{
		removeEdge(edge.tail().getIndex(), edge.head().getIndex());
	}


	/**
	 * Eliminate edge paths.
	 */
	public void removeEdgePaths()
	{
		Enumeration elements  = edges_.elements();
		while (elements.hasMoreElements())
		{
			Edge edge  = (Edge) (elements.nextElement());
			edge.points_ = new DPoint3[0];
		}
	}


	private void removeFalseEdges_()
	{
		Enumeration edges  = edges_.elements();
		while (edges.hasMoreElements())
		{
			Edge edge  = (Edge) (edges.nextElement());
			if (edge.isDummy())
			{
				edge.tail().clearChild(edge.head().getIndex());
				edges_.remove(new Point(edge.tail().getIndex(), edge.head().getIndex()));
			}
		}
	}


	public void removeGroups()
	{
		int node_index;

		for (node_index = nodeList_.firstNodeIndex(); node_index != -1;
				node_index = nodeList_.nextNodeIndex(node_index))
		{
			killGroup(getNodeFromIndex(node_index));
		}
	}


	/**
	 * remove the node from the graph(also removes the edges connected to it)
	 *
	 * @param  n  Description of the Parameter
	 */
	public void removeNode(int n)
	{
		Set pSet      = new Set();
		Set cSet      = new Set();
		Node node;

		Node remnode  = (Node) nodeList_.nodeFromIndex(n);

		if (remnode.isGroup())
		{
			int child_index;
			Node child;

			for (child_index = remnode.firstChild(); child_index != -1;
					child_index = remnode.nextChild())
			{
				removeNode(child_index);
			}
		}
		else
		{

			for (int child = remnode.firstChild(); child != -1;
					child = remnode.nextChild())
			{
				removeEdge(n, child);
			}

			for (node = nodeList_.firstNode(); node != null;
					node = nodeList_.nextNode(node))
			{
				if (node.hasChild(n))
				{
					removeEdge(node.getIndex(), n);
				}
			}
		}

		nodeList_.removeNodeAt(n);
		Integer id    = remnode.getIdObject();
		idHash_.remove(id);
		if (id.intValue() < lastTopId_)
		{
			lastTopId_ = id.intValue();
		}

		// If group parent is an empty group, remove it.
		if (remnode.inGroup())
		{
			remnode.groupNode_.clearChild(remnode.getIndex());
			if (remnode.groupNode_.firstChild() == -1)
			{
				removeNode(remnode.groupNode_.getIndex());
			}
		}
	}


	/**
	 * remove the node from the graph
	 *
	 * @param  nin  Description of the Parameter
	 */
	public void removeNode(Node nin)
	{
		removeNode(nin.getIndex());
	}


	public void setDirected(boolean directed)
	{
		if (directed == directed_)
		{
			return;
		}

		if (directed)
		{  // From undirected to directed.

			directed_ = true;
			removeFalseEdges_();
		}
		else
		{
			fillBackEdges_();
			directed_ = false;
		}
	}


	/**
	 * Set the GML values of a GML object to those of this Graph.
	 *
	 * @param  gml  The new gMLvalues value
	 */

	/* change later so GML is attached to Nodes (and edges) ****/
	public void setGMLvalues(GMLobject gml)
	{
		Node node;
		// Nodes
		gml.deleteAll("node", GMLobject.GMLlist);
		for (node = nodeList_.firstNode(); node != null; node = nodeList_.nextNode(node))
		{
			GMLobject nodegml  = new GMLobject("node", GMLobject.GMLlist);
			gml.addObjectToEnd(nodegml);
			node.setGMLvalues(nodegml);
		}

		gml.setValue("directed", GMLobject.GMLinteger, new Integer(directed_ ? 1 : 0));

		// Edges
		gml.deleteAll("edge", GMLobject.GMLlist);

		Enumeration edges  = edges_.elements();
		while (edges.hasMoreElements())
		{
			Edge edge  = (Edge) (edges.nextElement());
			if (directed_ || edge.tail().getIndex() <= edge.head().getIndex())
			{
				GMLobject edgegml  = new GMLobject("edge", GMLobject.GMLlist);
				gml.addObjectToEnd(edgegml);
				edge.setGMLvalues(edgegml);
			}
		}

	}


	public void setNodeGroup(Node node, Node groupnode)
	{

		// Remove from current group.
		if (node.inGroup())
		{
			Node current_group  = node.groupNode_;
			current_group.clearChild(node.getIndex());

			// If group parent is an empty group, remove it.
			if (current_group.firstChild() == -1)
			{
				removeNode(current_group.getIndex());
			}
		}

		// Add to new group.
		node.groupNode_ = groupnode;
		groupnode.setChild(node.getIndex());
	}
	// Set id's for non-id'd nodes, so all nodes have id's.

	private void validateIds()
	{
		// Initialize lastTopId_.
		lastTopId_ = 0;
		while (idHash_.containsKey(new Integer(lastTopId_)))
		{
			lastTopId_++;
		}

		// Fill in empty ids.
		Node tmpnode;
		for (tmpnode = firstNode(); tmpnode != null; tmpnode = nextNode(tmpnode))
		{
			if (tmpnode.getIdObject() == null)
			{
				tmpnode.id_ = lastTopId_;
				tmpnode.haveId_ = true;
				idHash_.put(new Integer(lastTopId_), tmpnode);

				do
				{
					lastTopId_++;
				} while (idHash_.containsKey(new Integer(lastTopId_)));
			}
		}
	}
}
