package EDU.auburn.VGJ.util;

/*
 * File: DPoint.java
 *
 * 5/10/96   Larry Barowski
 *
*/


   /**
 *	A class for holding a real position.
 *	</p>Here is the <a href="../util/DPoint.java">source</a>.
 *
 *@author	Larry Barowski
**/
   public class DPoint
   {
	  public double	x, y;
   
   
   
	  public DPoint()
	  {
	  }  
	  public DPoint(double x_in, double y_in)
	  {
		 x = x_in;
		 y = y_in;
	  }  
	  public DPoint(DPoint init)
	  {
		 x = init.x;
		 y = init.y;
	  }  
	  public boolean equals(DPoint other)
	  {
		 if(other.x != x || other.y != y)
			return false;
		 return true;
	  }  
	  public void move(double new_x, double new_y)
	  {
		 x = new_x;
		 y = new_y;
	  }  
	  public void move(DPoint to)
	  {
		 x = to.x;
		 y = to.y;
	  }  
	  public void translate(double x_shift, double y_shift)
	  {
		 x += x_shift;
		 y += y_shift;
	  }  
}      
