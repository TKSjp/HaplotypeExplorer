package EDU.auburn.VGJ.graph;

/*
	File: GMLlexer.java
	10/25/96    Larry Barowski
*/

	  import java.io.InputStream;
   import java.lang.String;
   import java.lang.Math;


   import java.lang.System;


/**
 *	A GML lexer. No distinction is made between Integer and Real
 *      (all numbers are real).
 *	</p>Here is the <a href="../graph/GMLlexer.java">source</a>.
 *
 *@author	Larry Barowski
**/
   public class GMLlexer
   {
	  public static int GMLstring = 256;
	  public static int GMLinteger = 257;
	  public static int GMLreal = 258;
	  public static int GMLeof = 259;
	  public static int GMLkey = 260;
   
	  private InputStream stream_;
	  private int linenumber_ = 0;
	  private int nextChar_ = '\n';
   
	  private String stringval_;
	  private double doubleval_;
   
	  private int tokenType_;
   
   
   
	  public GMLlexer(InputStream streamin)
	  {
		 stream_ = streamin;
	  }  
	  public double getDoubleval()
	  { 
		 return doubleval_;
	  }  
	  public int getLineNumber()
	  {
		 return linenumber_;
	  }  
	  public String getStringval()
	  { 
		 return stringval_;
	  }  
	  public int getTokenType()
	  {
		 return tokenType_;
	  }  
	  public int nextToken() throws java.io.IOException
	  {
		 skipWhitespace_();
	  
		 if((nextChar_ >= 'a' && nextChar_ <= 'z') ||
		 (nextChar_ >= 'A' && nextChar_ <= 'Z'))
			// Key
		 {
			stringval_ = "";
			int index = 0;
			while((nextChar_ >= 'a' && nextChar_ <= 'z') ||
			(nextChar_ >= 'A' && nextChar_ <= 'Z') ||
			(nextChar_ >= '0' && nextChar_ <= '9'))
			{
			   stringval_ += String.valueOf((char)nextChar_);
			   nextChar_ = stream_.read();
			}
			return (tokenType_ = GMLkey);
		 }
		 else if(nextChar_ == '-' || (nextChar_ >= '0' && nextChar_ <= '9'))
		 {
			// Number
			double fracval = 0, expval = 0, intval = 0;
			boolean havefrac = false, haveexp = false, isneg = false, expisneg = false;
		 
			if(nextChar_ == '-')
			{
			   isneg = true;
			   nextChar_ = stream_.read();
			   if(nextChar_ < '0' || nextChar_ > '9')
				  ; // throw a parse warning
			}
		 
			while(nextChar_ >= '0' && nextChar_ <= '9')
			{
			   intval = intval * 10 + nextChar_ - '0';
			   // Add check here later for overflow.
			   nextChar_ = stream_.read();
			}
		 
			if(nextChar_ == '.')
			{
			   havefrac = true;
			   nextChar_ = stream_.read();
			   // Accepts no digits after decimal.
			   while(nextChar_ >= '0' && nextChar_ <= '9')
			   {
				  fracval = (fracval + (double)(nextChar_ - '0')) / 10.0;
				  nextChar_ = stream_.read();
			   }
			}
		 
			if(nextChar_ == 'e' || nextChar_ == 'E')
			{
			   stream_.mark(3);
			   nextChar_ = stream_.read();
			   if(nextChar_ < '0' && nextChar_ > '9' && nextChar_ != '-' && nextChar_ != '+')
				  stream_.reset();
			   else
			   {
				  if(nextChar_ == '+' || nextChar_ == '-')
				  {
					 if(nextChar_ == '-')
						expisneg = true;
					 nextChar_ = stream_.read();
				  }
			   
				  if(nextChar_ < '0' && nextChar_ > '9')
					 stream_.reset();
				  else
				  {
					 haveexp = true;
				  
					 while(nextChar_ >= '0' && nextChar_ <= '9')
					 {
						expval = expval * 10.0 + (double)(nextChar_ - '0');
						nextChar_ = stream_.read();
					 }
				  }
			   }
			}
			doubleval_ = intval;
			if(havefrac)
			   doubleval_ += fracval;
			if(isneg)
			   doubleval_ = -doubleval_;
			if(expisneg)
			   expval = -expval;
			if(haveexp)
			   doubleval_ *= Math.pow(10.0, expval);
		 
			if(!havefrac && !haveexp)
			   return (tokenType_ = GMLinteger);
			return GMLreal;
		 }
		 else if(nextChar_ == '\"')
			// String
		 {
			stringval_ = "";
		 
			nextChar_ = stream_.read();
			while(nextChar_ >= 0 && nextChar_ != '\"')
			{
			   stringval_ += String.valueOf((char)nextChar_);
			   nextChar_ = stream_.read();
			}
		 
			if(nextChar_ != '\"')
			   ; // throw a parse error - end of file in string constant
		 
			nextChar_ = stream_.read();
			return (tokenType_ = GMLstring);
		 }
		 else if(nextChar_ != -1)
		 {
			int retval = nextChar_;
			stringval_ = String.valueOf((char)nextChar_);
			nextChar_ = stream_.read();
			return (tokenType_ = retval);
		 }
	  
		 return (tokenType_ = GMLeof);
	  }  
	  private void skipWhitespace_() throws java.io.IOException
	  {
		 while(nextChar_ == ' ' || nextChar_ == '\t' || nextChar_ == '\n'
		 || nextChar_ == '\r')
		 {
			// Increment line number and skip comment lines.
			if(nextChar_ == '\n' || nextChar_ == '\r')
			{
			   int chr = nextChar_;
			   linenumber_++;
			   nextChar_ = stream_.read();
			   if(chr == '\r' && nextChar_ == '\n')
				  nextChar_ = stream_.read();
			   if(nextChar_ == '#')
			   {
				  while(nextChar_ != '\n' && nextChar_ != '\r'
				  && nextChar_ != -1)
				  {
					 chr = nextChar_;
					 nextChar_ = stream_.read();
				  }
				  if(chr == '\r' && nextChar_ == '\n')
					 nextChar_ = stream_.read();
			   }
			}
			else
			   nextChar_ = stream_.read();
		 }
	  }  
}      
