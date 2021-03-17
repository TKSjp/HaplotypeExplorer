package EDU.auburn.VGJ.graph;


/*
 * File: NodeList.java
 *
 * 5/25/96   Larry Barowski
 *
*/


	  import java.lang.System;


/**
 *	A NodeList class for use in a Graph.
 *	</p>Here is the <a href="../graph/NodeList.java">source</a>.
 *
 *@author	Larry Barowski
**/


//==============================================================================
   public class NodeList implements Cloneable
	  {
	  private Node[]	nodes_;
   
	  private int	lastSlot_;
	  private int	firstAvailable_ = 0;
	  private int       numNodes_ = 0;
   
   
   
   
	  
	  public NodeList()
		 {
		 nodes_ = new Node[1];
		 lastSlot_ = -1;
		 } 
   /*
   * Add a node at the lowest available position.
   *
   */
	  public void addNode(Node new_node)
		 {
		 numNodes_ ++;
	  
		 if(firstAvailable_ < lastSlot_)
			{
			nodes_[firstAvailable_] = new_node;
			new_node.index_ = firstAvailable_;
			updateFirstAvailable(firstAvailable_);
			}
		 else
			{
			addNodeAt(new_node, firstAvailable_);
			}
		 } 
	  public void addNodeAt(Node new_node, int index)
		 {
		 if(nodes_[index] != null)
			numNodes_++;
	  
		 if(index >= lastSlot_)  // Increase the storage size.
			{
			Node[] new_nodes = new Node[index + 30];
			System.arraycopy(nodes_, 0, new_nodes, 0, lastSlot_ + 1);
		 
			for(int i = lastSlot_ + 1; i <= index + 29; i++)
			   new_nodes[i] = null;
		 
			lastSlot_ = index + 29;
			nodes_ = new_nodes;
			}
	  
	  
		 nodes_[index] = new_node;
	  
		 new_node.index_ = index;
	  
		 if(index == firstAvailable_)
			updateFirstAvailable(index);
		 } 
	  public Object clone() throws java.lang.CloneNotSupportedException
		 {
		 NodeList copy = new NodeList();
	  
		 copy = (NodeList)super.clone();
	  
		 copy.nodes_ = new Node[lastSlot_ + 1];
	  
		 for(int i = 0; i <= lastSlot_; i++)
			{
		 
			if (nodeFromIndex(i) != null)
			   copy.nodes_[i] = (Node)nodes_[i].clone();
			}
	  
		 return copy;
		 } 
	  public int count()
		 {
		 return numNodes_;
		 } 
   /*
   * Return the Node in the list with the lowest index.
   * If none are found, null is returned.
   */
	  public Node firstNode()
		 {
		 for(int i = 0; i <= lastSlot_; i++)
			if(nodes_[i] != null)
			   return nodes_[i];
		 return null;
		 } 
   /*
   * Return the index of the Node in the list with the lowest index.
   * If none are found, -1 is returned.
   */
	  public int firstNodeIndex()
		 {
		 for(int i = 0; i <= lastSlot_; i++)
			if(nodes_[i] != null)
			   return i;
		 return -1;
		 } 
	  public int getFirstAvailable()
		 {
		 return firstAvailable_;
		 } 
	/** Return the largest node index.
	 */
	  public int highestIndex()
		 {
		 int count = 0;
	  
		 for(int i = 0; i < lastSlot_; i++)
			if(nodes_[i] != null)
			   count = i;
	  
		 return count;
		 } 
   /*
   * Return the Node in the list with the next lowest index after prev_node.
   * If none are found, null is returned.
   */
	  public Node nextNode(Node prev_node)
		 {
		 for(int i = prev_node.index_ + 1; i <= lastSlot_; i++)
			if(nodes_[i] != null)
			   return nodes_[i];
		 return null;
		 } 
   /*
   * Return the index of the Node in the list with the next lowest index after
   * prev_index.
   * If none are found, -1 is returned.
   */
	  public int nextNodeIndex(int prev_index)
		 {
		 for(int i = prev_index + 1; i <= lastSlot_; i++)
			if(nodes_[i] != null)
			   return i;
		 return -1;
		 } 
   /*
   * Return the Node at index.
   * If there is none, null is returned.
   */
	  public Node nodeFromIndex(int index)
		 {
		 if(index > lastSlot_ || index < 0)
			return null;
		 else
			return nodes_[index];
		 } 
	  public void removeNode(Node node)
		 {
		 if(nodes_[node.index_] != null)
			numNodes_ --;
	  
		 if(firstAvailable_ > node.index_)
			firstAvailable_ = node.index_;
	  
		 nodes_[node.index_] = null;
	  
		 } 
	  public void removeNodeAt(int index)
		 {
		 if(nodes_[index] != null)
			numNodes_ --;
	  
		 if(index <= lastSlot_)
			{
			if(firstAvailable_ > index)
			   firstAvailable_ = index;
			nodes_[index] = null;
			}
		 } 
	  public void updateFirstAvailable(int start)
		 {
		 for(int i = start; i <= lastSlot_; i++)
			{
			if(nodes_[i] == null)
			   {
			   firstAvailable_ = i;
			   break;
			   }
			}
		 } 
}  
