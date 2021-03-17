package EDU.auburn.VGJ.gui;

/*
	File: OffsetCanvas.java
	5/29/96   Larry Barowski
*/



	  import java.awt.Dimension;
   import java.awt.Point;
   import java.awt.Canvas;

   import EDU.auburn.VGJ.util.DPoint;
   import EDU.auburn.VGJ.util.DDimension;

/**
 *	This abstract class is used inside a ScrolledPanel, and
 *	implements the required functionality to communicate with it.
 *	</p>Here is the <a href="../gui/OffsetCanvas.java">source</a>.
 *
 *@see		ScrolledPanel
 *@author	Larry Barowski
**/
   public abstract class OffsetCanvas extends Canvas
   {
   
   
   
   
   
   /**
	*	Event id for size change. Subclasses must post an event with this id
	*	when their contents or windows are resized.
	**/
	  public static int	RESIZE = 32450;
   
   /**
	*	Event id for changing the label above the OffsetCanvas.
	*	The arg field of the event must contain the string.
	**/
	  public static int	LABEL = 32451;
   
   
   
   
   
   /**
	*	Return the size of the contents.
	**/
	  abstract public DDimension contentsSize();  
	  abstract public DPoint getOffset()
	  ;  
   /**
	*	Adjust the offset of the contents of the canvas. These must have the
	*	following meaning: screen position + offset = contents position.
	*	e.g. if xoffset is 10, contents starts 10 pixels to the left of the
	*	screen.
	*
	*@param xoffset horizontal offset
	*@param yoffset vertical offset
	**/
	  abstract public void setOffsets(double xoffset, double yoffset, boolean redraw);  
}      
