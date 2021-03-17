package clad;

/*
// Quinn Snell
// A class for easier reading of input from command line
//
*/

import java.io.*;

public class TextInputStream
{
	private BufferedReader stdin;
	char missed, currchar;
	boolean EOF;


	public TextInputStream()
	{
		stdin = new BufferedReader(new InputStreamReader(System.in));
		missed = '\0';
		currchar = '\0';
		EOF = false;
	}


	/**
	 * Constructs a TextInputStream for input from a text file
	 *
	 * @param  s  the text filename
	 */

	public TextInputStream(String s)
	{
		try
		{
			stdin = new BufferedReader(new InputStreamReader(new FileInputStream(s)));
		}
		catch (FileNotFoundException e)
		{
			stdin = null;
		}
		missed = '\0';
		currchar = '\0';
		EOF = false;
	}


	/**
	 * Closes the input stream
	 */
	public void close()
	{
		try
		{
			stdin.close();
		}
		catch (IOException e)
		{
			;
		}
	}


	/**
	 * Returns true if the end of the file has been reached, false otherwise.
	 *
	 * @return    Description of the Return Value
	 */
	public boolean EOF()
	{
		return (EOF);
	}


	/**
	 * Reads and returns the next double in the stream
	 *
	 * @return    the double
	 */
	public char readChar()
	{
		char[] s  = new char[2];
		if (missed == '\0')
		{
			try
			{
				if (stdin.read(s, 0, 1) == -1)
				{
					EOF = true;
				}
			}
			catch (IOException e)
			{
				;
			}
		}
		else
		{
			s[0] = missed;
			missed = '\0';
		}
		currchar = s[0];
		return s[0];
	}


	/**
	 * Reads and returns the next double in the stream
	 *
	 * @return    the double
	 */
	public double readDouble()
	{
		char[] s    = new char[255];
		int length;

		try
		{
			length = readFloatString(s);
			return Double.valueOf(String.valueOf(s, 0, length)).doubleValue();
		}
		catch (IOException e)
		{
			;
		}
		return Double.NaN;
	}


	/**
	 * Reads and returns the next float in the stream
	 *
	 * @return    the float
	 */
	public float readFloat()
	{
		char[] s    = new char[255];
		int length;

		try
		{
			length = readFloatString(s);
			return Float.valueOf(String.valueOf(s, 0, length)).floatValue();
		}
		catch (IOException e)
		{
			;
		}
		return Float.NaN;
	}


	int readFloatString(char[] s)
		throws IOException
	{
		int i;
		int state  = 0;

		s[0] = missed;

		// Skip Whitespace
		while (Character.isWhitespace(s[0]) || s[0] == '\0')
		{
			s[0] = readChar();
			if (EOF)
			{
				throw new EOFException();
			}
		}

		// Use a finite automaton to check the floating point number
		for (i = 1; !Character.isWhitespace(s[i - 1]); i++)
		{
			switch (s[i - 1])
			{
							case '0':
							case '1':
							case '2':
							case '3':
							case '4':
							case '5':
							case '6':
							case '7':
							case '8':
							case '9':
								switch (state)
								{
												case 0:
												case 1:
												case 2:
												case 4:
													state = 4;
													break;
												case 3:
												case 5:
													state = 5;
													break;
												case 6:
												case 7:
												case 8:
												case 9:
													state = 9;
													break;
												default:
													throw new IOException("Floating point format error1");
								}
								break;
							case '+':
								switch (state)
								{
												case 0:
													i--;  // Throw away the initial + sign
													state = 1;
													break;
												case 6:
													state = 8;
													break;
												default:
													throw new IOException("Floating point format error2");
								}
								break;
							case '-':
								switch (state)
								{
												case 0:
													state = 2;
													break;
												case 6:
													state = 7;
													break;
												default:
													throw new IOException("Floating point format error3");
								}
								break;
							case '.':
								switch (state)
								{
												case 0:
												case 1:
												case 2:
												case 4:
													state = 3;
													break;
												default:
													throw new IOException("Floating point format error4");
								}
								break;
							case 'e':
								switch (state)
								{
												case 4:
												case 5:
													state = 6;
													break;
												default:
													throw new IOException("Floating point format error5");
								}
								break;
							default:
								throw new IOException("Floating point format error6");
			}
			s[i] = readChar();
		}
		missed = s[i - 1];
		return i - 1;
	}


	/**
	 * Reads and returns the next integer in the stream
	 *
	 * @return    the integer
	 */
	public int readInt()
	{
		char[] s    = new char[255];
		int length;

		try
		{
			length = readIntString(s);
			return Integer.valueOf(String.valueOf(s, 0, length)).intValue();
		}
		catch (IOException e)
		{
			/*System.err.println("Caught " + e);*/
		}
		return Integer.MIN_VALUE;
	}


	int readIntString(char[] s)
		throws IOException
	{
		int i;
		int state  = 0;

		s[0] = missed;

		// Skip Whitespace
		while (Character.isWhitespace(s[0]) || s[0] == '\0')
		{
			s[0] = readChar();
			if (EOF)
			{
				throw new EOFException();
			}
		}

		// Use a finite automaton to check the integer number
		for (i = 1; !Character.isWhitespace(s[i - 1]); i++)
		{
			switch (s[i - 1])
			{
							case '0':
							case '1':
							case '2':
							case '3':
							case '4':
							case '5':
							case '6':
							case '7':
							case '8':
							case '9':
								switch (state)
								{
												case 0:
												case 1:
												case 2:
												case 3:
													state = 3;
													break;
												default:
													throw new IOException("Integer format:Bad Start");
								}
								break;
							case '+':
								switch (state)
								{
												case 0:
													i--;  // Throw away the + sign
													state = 1;
													break;
												default:
													throw new IOException("Integer format:+ sign wrong");
								}
								break;
							case '-':
								switch (state)
								{
												case 0:
													state = 2;
													break;
												default:
													throw new IOException("Integer format:- sign wrong");
								}
								break;
							default:
								throw new IOException("Integer format error");
			}
			s[i] = readChar();
		}
		missed = s[i - 1];
		return i - 1;
	}


	/**
	 * Reads and returns the next line in the stream
	 *
	 * @return    the line as a String without the return character
	 */
	public String readLine()
	{
		try
		{
			String s  = stdin.readLine();
			missed = '\0';
			if (s == null)
			{
				EOF = true;
				//throw new EOFException();
			}
			return s;
		}
		catch (IOException e)
		{
			return null;
		}
	}


	/**
	 * Reads and returns the next word in the stream
	 *
	 * @return    the word as a String
	 */
	public String readWord()
	{
		char[] s  = new char[255];
		int i;

		s[0] = missed;

		// Skip Whitespace
		while (Character.isWhitespace(s[0]) || s[0] == '\0')
		{
			s[0] = readChar();
			if (EOF)
			{
				return null;
			}
		}

		for (i = 1; !Character.isWhitespace(s[i - 1]); i++)
		{
			s[i] = readChar();
		}

		missed = s[i - 1];
		return String.valueOf(s, 0, i - 1);
	}
}
