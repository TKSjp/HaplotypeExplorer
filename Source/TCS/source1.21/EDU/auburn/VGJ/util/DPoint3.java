package EDU.auburn.VGJ.util;

/*
 * File: DPoint3.java
 *
 * 7/7/96   Larry Barowski
 *
*/


   /**
 *	A class for holding a real 3D position.
 *	</p>Here is the <a href="../util/DPoint3.java">source</a>.
 *
 *@author	Larry Barowski
**/
   public class DPoint3
   {
	  public double	x, y, z;
   
   
   
	  public DPoint3()
	  {
	  }  
	  public DPoint3(double x_in, double y_in, double z_in)
	  {
		 x = x_in;
		 y = y_in;
		 z = z_in;
	  }  
	  public DPoint3(DPoint3 init)
	  {
		 x = init.x;
		 y = init.y;
		 z = init.z;
	  }  
	  public boolean equals(DPoint3 other)
	  {
		 if(other.x != x || other.y != y || other.z != z)
			return false;
		 return true;
	  }  
	  public void move(double new_x, double new_y, double new_z)
	  {
		 x = new_x;
		 y = new_y;
		 z = new_z;
	  }  
	  public void move(DPoint3 newpt)
	  {
		 x = newpt.x;
		 y = newpt.y;
		 z = newpt.z;
	  }  
	  public void transform(Matrix44 matrix)
	  {
		 double x2, y2, z2, w2;
		 double	mat[][] = matrix.matrix;
	  
		 x2 = mat[0][0] * x + mat[0][1] * y + mat[0][2] * z + mat[0][3];
		 y2 = mat[1][0] * x + mat[1][1] * y + mat[1][2] * z + mat[1][3];
		 z2 = mat[2][0] * x + mat[2][1] * y + mat[2][2] * z + mat[2][3];
		 w2 = mat[3][0] * x + mat[3][1] * y + mat[3][2] * z + mat[3][3];
	  
		 x = x2 / w2;
		 y = y2 / w2;
		 z = z2 / w2;
	  }  
	  public void translate(double x_shift, double y_shift, double z_shift)
	  {
		 x += x_shift;
		 y += y_shift;
		 z += z_shift;
	  }  
}      
