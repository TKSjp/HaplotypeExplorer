package EDU.auburn.VGJ.graph;

/*
 * File: ParseError.java
 *
 * 5/3/96   Larry Barowski
 *
*/


import java.lang.Exception;




/**
 *	A parse error Exception.
 * </p>Here is the <a href="../graph/ParseError.java">source</a>.
 *
 *@author	Larry Barowski
**/
public class ParseError extends Exception
{
	public ParseError(String string)
		{ super(string); }
}
