package EDU.auburn.VGJ.util;

/*
 * File: DDimension3.java
 *
 * 7/7/96   Larry Barowski
 *
*/



   /**
 *	A class for holding a real 3D dimension.
 *	</p>Here is the <a href="../util/DDimension3.java">source</a>.
 *
 *@author	Larry Barowski
**/




   public class DDimension3
	  {
	  public double	width, height, depth;
   
   
	  
	  public DDimension3(double width_in, double height_in, double depth_in)
		 {
		 width = width_in;
		 height = height_in;
		 depth = depth_in;
		 } 
	  public DDimension3(DDimension3 init)
		 {
		 width = init.width;
		 height = init.height;
		 depth = init.depth;
		 } 
	  public boolean equals(DDimension3 other)
		 {
		 if(other.width != width || other.height != height ||
		 other.depth != depth)
			return false;
		 return true;
		 } 
}  
