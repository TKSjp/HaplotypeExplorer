package EDU.auburn.VGJ.graph;

/*
 * File: Set.java
 *
 * Date		Author		Changes
 * ------------ --------------- ----------------------------------------------
 * 04/15/95     Fwu-Shan Shieh  created
 * 11/21/96     Larry Barowski  Got rid of BitSet and replicated its
 *                              functionality. Rewrote other functions
 *                              that could benefit speedwise from this.
 *
 */


	  import java.util.NoSuchElementException;
   import java.io.IOException;



/**
 * This class is similar to Java's BitSet class (only it is not full of
 * bugs). It is used to represent an ordered set of non-negative integers.
 * The set automatically grows as more spaces are needed.
 * </p>Here is the <a href="../graph/Set.java">source</a>.
 */
   public class Set implements Cloneable
   {
	  final static int BITS = 6;
	  final static int MASK = (1<<BITS)-1;
	  private long bits_[];
   
	  private   int  	nextPosition_ = 0;
   
   
   
	// Set() -----------------------------------------------------------------
	/**
	 * Construct an empty Set.
	 */
	  public Set()
	  {
		 bits_ = new long[1];
	  }  
	/**
	 * Construct a Set with one element.
	 */
	  public Set(int element)
	  {
		 bits_ = new long[1];
		 includeElement(element);
	  }  
	// clone() ---------------------------------------------------------------
	/**
	 * Make a copy of current set.
	 * @return copy of current set.
	 */
	  public Object clone()
	  {
		 Set set = new Set();
		 set.bits_ = new long[bits_.length];
		 System.arraycopy(bits_, 0, set.bits_, 0, bits_.length);
	  
		 return set;
	  }  
	// difference(Set s) -----------------------------------------------------
	/**
	 * Modify the current set to the difference of current set 
	 * and s (current - s).
	 * @param s set to be checked
	 * @return the difference of current set - set s.
	 */
	  public void difference(Set s)
	  {
		 if(s.bits_.length > bits_.length)
			grow_(s.bits_.length);
	  
		 for(int i = 0; i < s.bits_.length; i++)
			bits_[i] = (bits_[i] | s.bits_[i]) ^ s.bits_[i];
	  }  
	// equals(Set s) ---------------------------------------------------------
	/**
	 * Check if set s equals to current set.
	 * @param s set to be check
	 * @return true  -- set s is equal to current set, or
	 *         false -- set s is not equal to current set.
	 */
	  public boolean equals (Set s)
	  {
		 int minsize = Math.min(bits_.length, s.bits_.length);
		 int i;
		 if(minsize < s.bits_.length)
			for(i = minsize; i < s.bits_.length; i++)
			   if(s.bits_[i] != 0)
				  return false;
		 if(minsize < bits_.length)
			for(i = minsize; i < s.bits_.length; i++)
			   if(bits_[i] != 0)
				  return false;
		 for(i = 0; i < minsize; i++)
			if(s.bits_[i] != bits_[i])
			   return false;
	  
		 return true;
	  }  
	// fill(int n) -------------------------------------------------
	/**
	 * Put elements 0 to n - 1 into the current set.
	 * @param  n number of elements to be included
	 */
	  public void fill(int n)
	  {
		 n--;
		 int bytepos = n >> BITS;
		 if(bytepos >= bits_.length)
			grow_(bytepos + 1);
		 int i;
		 for(i = 0; i < bits_.length - 1; i++)
			bits_[i] = ~0L;
	  
		 long bit;
		 for(bit = 1; bit <= (1L << (n & MASK)); bit = bit << 1)
			bits_[i] |= bit;
	  }  
	// first() ---------------------------------------------------------------
	/**
	 * Find the first element of current set.
	 * @return element -- the first element of the set, or
	 *         -1      -- if set is empty.
	 */
	  public int first()
	  {
		 nextPosition_ = searchNext(0) + 1;
		 return nextPosition_ - 1;
	  }  
	  private void grow_(int len)
	  {
		 long newbits[] = new long[len];
		 System.arraycopy(bits_, 0, newbits, 0, bits_.length);
		 bits_ = newbits;
	  }  
	// includeElement(int n) -------------------------------------------------
	/**
	 * Include element n into the current set. This routine doesn't check
	 * if element n exists.
	 * @param  n element to be included
	 */
	  public void includeElement(int n)
	  {
		 int bytepos = n >> BITS;
		 if(bytepos >= bits_.length)
			grow_(bytepos + 1);
		 bits_[bytepos] |= (1L << (n & MASK));
	  }  
   /* Union set with set_list[j] for all elements, j, of index. */
	  public void indexedUnion(Set set_list[], Set index)
	  {
		 int i;
		 for(i = index.first(); i != -1; i = index.next())
			union(set_list[i]);
	  }  
	// intersect(Set s) ------------------------------------------------------
	/**
	 * Modify the current set to the intersection of current set and set s.
	 * @param s set to be checked
	 * @return the intersection of current set and set s.
	 */
	  public void intersect(Set s)
	  {
		 int minsize = Math.min(bits_.length, s.bits_.length);
		 for(int i = 0; i < minsize; i++)
			bits_[i] &= s.bits_[i];
	 if (bits_.length > s.bits_.length)  {
		for (int i = s.bits_.length; i < bits_.length; i++)
			bits_[i] = 0;
		}
	  }  
   /* Determine if sets intersect. */
	  public boolean intersects(Set set)
	  {
		 int len = Math.min(bits_.length, set.bits_.length);
	  
		 int i;
		 for(i = 0; i < len; i++)
			if((bits_[i] & set.bits_[i]) != 0)
			   return true;
		 return false;
	  }  
	// isElement(int n) ------------------------------------------------------
	/**
	 * Check if n is an element of currernt set.
	 * @param n element to be checked
	 * @return true  -- n is an element of current set, or
	 *         flase -- n is not an element of current set.
	 */
	  public boolean isElement(int n)
	  {
		 int bytepos = n >> BITS;
	  
		 if(bytepos >= bits_.length)
			return false;
		 return (bits_[bytepos] & (1L << (n & MASK))) != 0;
	  }  
	// isEmpty() -------------------------------------------------------------
	/**
	 * Check if current set is empty.
	 * @return true  -- if current set is empty, or
	 *         false -- if current set is not empty.
	 */
	  public boolean isEmpty()
	  {
		 int i;
		 for(i = 0; i < bits_.length; i++)
			if(bits_[i] != 0)
			   return false;
		 return true;
	  }  
	// isSubset(Set s) -------------------------------------------------------
	/**
	 * Check if set s is a subset of current set.
	 * @param s subset to be checked
	 * @return true  -- s is a subset of current set, or
	 *         false -- s is not a subset of current set.
	 */
	  public boolean isSubset(Set s)
	  {
		 int minsize = Math.min(bits_.length, s.bits_.length);
		 int i;
		 if(minsize < s.bits_.length)
			for(i = minsize; i < s.bits_.length; i++)
			   if(s.bits_[i] != 0)
				  return false;
		 for(i = 0; i < minsize; i++)
			if((s.bits_[i] & bits_[i]) != s.bits_[i])
			   return false;
	  
		 return true;
	  }  
	// next() ----------------------------------------------------------------
	/**
	 * Find the next element of current set.
	 * @return element -- the next element of the set, or
	 *         -1      -- if there is no next element.
	 */
	  public int next()
	  {
		 nextPosition_ = searchNext(nextPosition_) + 1;
		 return nextPosition_ - 1;
	  }  
	// numberOfElements() ---------------------------------------------------
	/**
	 * Find the number of elements of current set.
	 * @return number of elements.
	 */
	  public int numberOfElements()
	  {
		 int i, num = 0;
	  
		 for (i = first(); i != -1; i = next(), num++)
			;
	  
		 return num;
	  }  
	// removeElement(int n) --------------------------------------------------
	/**
	 * Remove element n from the current set. This routine doesn't check
	 * if element n exists.
	 * @param n element to removed
	 */
	  public void removeElement(int n) throws NoSuchElementException
	  {
		 int bytepos = n >> BITS;
		 if(bytepos >= bits_.length)
			return;
		 bits_[bytepos] &= ~(1L << (n & MASK));
	  }  
	// searchNext(int n) -----------------------------------------------------
	/**
	 * Start at position n to search next element.
	 * @param n start position of searching
	 * @return element -- if an element found, or
	 *         -1      -- if no element found.
	 */
	  public int searchNext(int n)
	  {
		 int bytepos = n >> BITS;
	  
		 int i;
		 for(i = bytepos; i < bits_.length && bits_[i] == 0; i++)
			;
		 if(i == bits_.length)
			return -1;
	  
		 int val;
		 long bit;
		 long byteval = bits_[i];
		 if(i == bytepos)
		 {
			val = n;
			for(bit = 1L << (val & MASK); (bit & byteval) == 0 && bit != (1L << MASK);
			bit = bit << 1, val++)
			   ;
			if((bit & byteval) != 0)
			   return val;
		 
			for(i++; i < bits_.length && bits_[i] == 0; i++)
			   ;
			if(i == bits_.length)
			   return -1;
		 
			byteval = bits_[i];
		 }
	  
		 val = i << BITS;
		 for(bit = 1; (bit & byteval) == 0 && bit != (1L << MASK); bit = bit << 1, val++)
			;
	  
		 if((bit & byteval) == 0)
			return -1;
	  
		 return val;
	  }  
	// toShortString() ------------------------------------------------------------
	/**
	 * Convert set to "(0-2, 5, 9 ..., n)" String format. 
	 * @return current set in String format.
	 */
	  public String toShortString()
	  {
		 String  to_string = "(";
		 boolean first_element = true;
	  
		 for (int i = first(); i != -1; i = next())
		 {
			if (first_element)
			   first_element = false;
			else
			   to_string = to_string + ", ";
			to_string = to_string + i;
		 
			if(isElement(i + 1) && isElement(i + 2))
			{
			   to_string += "-";
			   while(isElement(i + 1))
				  i = next();
			   to_string += i;
			}
		 }
		 to_string = to_string + ")";
	  
		 return to_string;
	  }  
	// toString() ------------------------------------------------------------
	/**
	 * Convert set to "(0, 1, 2, ..., n)" String format. 
	 * @return current set in String format.
	 */
	  public String toString()
	  {
		 String  to_string = "(";
		 boolean first_element = true;
	  
		 for (int i = first(); i != -1; i = next())
		 {
			if (first_element)
			   first_element = false;
			else
			   to_string = to_string + ", ";
			to_string = to_string + i;
		 }
		 to_string = to_string + ")";
	  
		 return to_string;
	  }  
	// union(Set s) ----------------------------------------------------------
	/**
	 * Modify the current set to the union of current set and set s.
	 * @param s set to be united
	 * @return the union of current set and set s.
	 */
	  public void union(Set s)
	  {
		 if(s.bits_.length > bits_.length)
			grow_(s.bits_.length);
	  
		 for(int i = 0; i < s.bits_.length; i++)
			bits_[i] |= s.bits_[i];
	  }  
}      
