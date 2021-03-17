package EDU.auburn.VGJ.util;

/*
 * File: Matrix44.java
 *
 * 7/13/96   Larry Barowski
 *
*/

/**
 *	A 4x4 Matrix class.
 *	</p>Here is the <a href="../util/Matrix44.java">source</a>.
 *
 * @author     Larry Barowski
 * @created    July 3, 2003
 */
public class Matrix44
{  //implements Cloneable

	public double[][] matrix;
	public double scale;



	public Matrix44()
	{
		matrix = new double[4][4];

		for (int i = 0; i < 4; i++)
		{
			for (int j = 0; j < 4; j++)
			{
				matrix[i][j] = 0.0;
			}
		}
	}


	public Matrix44(Matrix44 init)
	{
		matrix = new double[4][4];

		for (int i = 0; i < 4; i++)
		{
			for (int j = 0; j < 4; j++)
			{
				matrix[i][j] = init.matrix[i][j];
			}
		}
	}


	public void mult(Matrix44 mat2)
	{
		double[][] result  = new double[4][4];
		double[][] a       = matrix;
		double[][] b       = mat2.matrix;

		for (int i = 0; i < 4; i++)
		{
			for (int j = 0; j < 4; j++)
			{
				result[i][j] = a[i][0] * b[0][j] + a[i][1] * b[1][j] + a[i][2] * b[2][j] + a[i][3] * b[3][j];
			}
		}

		for (int i = 0; i < 4; i++)
		{
			for (int j = 0; j < 4; j++)
			{
				a[i][j] = result[i][j];
			}
		}
	}


	public void setTo(Matrix44 mat2)
	{
		for (int i = 0; i < 4; i++)
		{
			for (int j = 0; j < 4; j++)
			{
				matrix[i][j] = mat2.matrix[i][j];
			}
		}
	}
}
