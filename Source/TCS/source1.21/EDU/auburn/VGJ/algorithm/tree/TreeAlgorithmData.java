package EDU.auburn.VGJ.algorithm.tree;

/*
*
* Date		Author
* 11/12/96	Larry Barowski
*
*/

	  import EDU.auburn.VGJ.graph.Node;


/**
 * Data container for the TreeAlgorithm class.
 * </p>Here is the <a href="../algorithm/tree/TreeAlgorithmData.java">source</a>.
 */
   public class TreeAlgorithmData
   {
	  public int level;
	  public Node parent;
	  public Node leftChild;
	  public Node rightChild;
	  public Node leftSibling;
	  public Node rightSibling;
	  public Node leftNeighbor;
	  public Node rightNeighbor;
	  public boolean isLeaf;
	  public double modifier;
	  public double prelim;
	  public Node group;
   
   
	  public TreeAlgorithmData()
	  {
		 level = -1;
		 parent = leftChild = rightChild = leftSibling = rightSibling =
			leftNeighbor = rightNeighbor = null;
		 isLeaf = false;
		 modifier = prelim = 0;
		 group = null;
	  }  
}      
