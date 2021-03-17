package EDU.auburn.VGJ.util;

/*
 * File: DDimension.java
 *
 * 5/10/96   Larry Barowski
 *
*/



   /**
 *	A class for holding a real dimension.
 *	</p>Here is the <a href="../util/DDimension.java">source</a>.
 *
 *@author	Larry Barowski
**/




   public class DDimension
   {
	  public double	width, height;
   
   
   
	  public DDimension(double width_in, double height_in)
	  {
		 width = width_in;
		 height = height_in;
	  }  
	  public DDimension(DDimension init)
	  {
		 width = init.width;
		 height = init.height;
	  }  
	  public boolean equals(DDimension other)
	  {
		 if(other.width != width || other.height != height)
			return false;
		 return true;
	  }  
	  public void setTo(double w, double h)
	  {
		 width = w;
		 height = h;
	  }  
	  public void setTo(DDimension from)
	  {
		 width = from.width;
		 height = from.height;
	  }  
}      
