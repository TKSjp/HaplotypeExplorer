package EDU.auburn.VGJ.graph;

import java.io.IOException;

import java.lang.Double;

/*
	 File: GMLobject.java
	 10/25/96    Larry Barowski
 */
import java.lang.String;
import java.lang.System;

import java.util.Hashtable;


/**
 *    A GML object that does nothing but create itself and write itself.
 *    </p>Here is the <a href="../graph/GMLobject.java">source</a>.
 *
 * @author     Larry Barowski
 * @created    July 3, 2003
 */
public class GMLobject
{

	public final static int GMLfile       = 5;
	public final static int GMLinteger    = 0;
	public final static int GMLlist       = 3;
	public final static int GMLreal       = 1;
	public final static int GMLstring     = 2;
	public final static int GMLundefined  = 4;
	protected String key_;
	protected GMLobject next_;
	protected int type_;
	protected Object value_;
	private String searchkey_             = null;

	// State of iterator.
	private GMLobject searchobj_          = null;
	private int searchtype_;


	public GMLobject()
	{
		key_ = "undefined";
		value_ = null;
		next_ = null;
		type_ = GMLundefined;
	}


	public GMLobject(String key, int type)
	{
		key_ = key;
		type_ = type;
		value_ = null;
		next_ = null;
	}


	/**
	 * Create the object from a GMLlexer and key. A null key will cause
	 *  a GMLfile object to be created.
	 *
	 * @param  lexer                    Description of the Parameter
	 * @param  key                      Description of the Parameter
	 * @exception  java.io.IOException  Description of the Exception
	 * @exception  ParseError           Description of the Exception
	 */
	public GMLobject(GMLlexer lexer, String key)
		throws java.io.IOException, ParseError
	{
		next_ = null;
		key_ = key;

		int token;

		// Parse the value.
		if (key != null)
		{
			token = lexer.nextToken();

			if (token == GMLlexer.GMLinteger)
			{
				type_ = GMLinteger;
				value_ = new Integer((int) (lexer.getDoubleval()));
			}
			else if (token == GMLlexer.GMLreal)
			{
				type_ = GMLreal;
				value_ = new Double(lexer.getDoubleval());
			}
			else if (token == GMLlexer.GMLstring)
			{
				type_ = GMLstring;
				value_ = lexer.getStringval();
			}
			else if (token == '[')
			{
				type_ = GMLlist;
				value_ = parseGMLlist_(lexer);

				if (lexer.getTokenType() != ']')
				{
					throw new ParseError("Expecting GML key or \']\'");
				}
			}
			else
			{
				throw new ParseError("Expecting GML value (number, string, or list)");
			}
		}
		else
		{
			type_ = GMLfile;
			value_ = parseGMLlist_(lexer);

			if (lexer.getTokenType() != GMLlexer.GMLeof)
			{
				throw new ParseError("Expecting GML key or end-of-file");
			}
		}
	}


	/**
	 * Get the first sub-object with path-key "path" and type "type",
	 *  or null if no such object exists.
	 *
	 *  If "create" is true, create the sub-object if it doesn't exist.
	 *
	 *  If type is GMLreal and a matching GMLinteger is found, the integer
	 *  object will be changed to a real.
	 *
	 *  This function begins an iteration sequence that can continue
	 *  with calls to getNextGMLSubObject().
	 *
	 * @param  path    Description of the Parameter
	 * @param  type    Description of the Parameter
	 * @param  create  Description of the Parameter
	 * @return         The gMLSubObject value
	 */
	public GMLobject getGMLSubObject(String path, int type, boolean create)
	{

		int start;
		int end;
		GMLobject currentobj;
		GMLobject obj;
		searchobj_ = null;
		currentobj = this;
		start = 0;

		String key;

		// Should not call this function if object type is not list.
		if (currentobj.type_ != GMLlist && currentobj.type_ != GMLfile)
		{

			return null;
		}

		do
		{
			end = path.indexOf('.', start);

			if (end >= 0)
			{
				key = path.substring(start, end);
			}
			else
			{
				key = path.substring(start);
			}

			for (obj = (GMLobject) (currentobj.value_); obj != null; obj = obj.next_)
			{

				if (obj.key_.equals(key) &&
						((end >= 0 &&
						(obj.type_ == GMLlist || obj.type_ == GMLfile)) ||
						(end == -1 &&
						(obj.type_ == type ||
						(obj.type_ == GMLinteger && type == GMLreal)))))
				{

					break;
				}
			}

			if (obj == null)
			{

				if (!create)
				{

					return null;
				}
				else
				{  // Create key.

					obj = new GMLobject(key, GMLlist);

					if (end == -1)
					{  // Creating end object.

						obj.type_ = type;
					}

					// Add to front of list;
					obj.next_ = (GMLobject) currentobj.value_;
					currentobj.value_ = obj;
				}
			}
			else if (end == -1 && type != obj.type_)
			{

				// Promote the integer to a real.
				obj.type_ = GMLreal;
				obj.value_ = new Double((double) ((Integer) obj.value_).intValue());
			}

			currentobj = obj;
			start = end + 1;
		} while (start != 0);

		searchobj_ = currentobj.next_;
		searchkey_ = key;
		searchtype_ = type;

		return currentobj;
	}


	/**
	 * Insert the keys and values of the first sub-object with path-key
	 *  "path" and type "type", into the hash table "hash".
	 *
	 *  If type is GMLreal and a matching GMLinteger is found, the integer
	 *  object will be changed to a real.
	 *
	 * @param  path  The new hashFromGML value
	 * @param  type  The new hashFromGML value
	 * @param  hash  The new hashFromGML value
	 */
	public void setHashFromGML(String path, int type, Hashtable hash)
	{

		// Should not call this function if object type is not list.
		if (type_ != GMLlist && type_ != GMLfile)
		{

			return;
		}

		GMLobject list  = getGMLSubObject(path, GMLlist, false);

		if (list == null)
		{

			return;
		}

		GMLobject tmp;

		for (tmp = (GMLobject) list.value_; tmp != null; tmp = tmp.next_)
		{

			if (type == tmp.type_ ||
					(type == GMLreal && tmp.type_ == GMLinteger))
			{
				hash.put(tmp.key_, tmp.value_);
			}
		}
	}


	/**
	 * Iterator initialized by getGMLSubObject().
	 *
	 *  Returns the next sub-object matching the path-key and type specified
	 *  in a previous call to getGMLSubObject(), or null if no such object
	 *  exists.
	 *
	 * @return    The nextGMLSubObject value
	 */
	public GMLobject getNextGMLSubObject()
	{

		while (searchobj_ != null && searchobj_.key_ != null &&
				!(searchobj_.key_.equals(searchkey_) &&
				(searchobj_.type_ == searchtype_ ||
				(searchobj_.type_ == GMLinteger && searchtype_ == GMLreal))))
		{
			searchobj_ = searchobj_.next_;
		}

		if (searchobj_ != null && searchobj_.type_ != searchtype_)
		{

			// Promote the integer to a real.
			searchobj_.type_ = GMLreal;
			searchobj_.value_ = new Double((double) ((Integer) searchobj_.value_).intValue());
		}

		GMLobject retval  = searchobj_;

		if (searchobj_ != null)
		{
			searchobj_ = searchobj_.next_;
		}

		return retval;
	}

	// Do not call from a non-list object, or null pointer exception will result.
	public void setValue(String path, int type, Object value)
	{

		GMLobject obj  = getGMLSubObject(path, type, true);
		obj.value_ = value;
	}


	/**
	 * Get the value of the first sub object matching the path-key "path"
	 *  and type "type", or null if there is no sub object.
	 *
	 * @param  path  Description of the Parameter
	 * @param  type  Description of the Parameter
	 * @return       The value value
	 */
	public Object getValue(String path, int type)
	{

		GMLobject obj  = getGMLSubObject(path, type, false);

		if (obj == null)
		{

			return null;
		}

		return obj.value_;
	}

	// Do not call from a non-list object.
	public void addObject(GMLobject object)
	{

		if (type_ != GMLlist)
		{

			return;
		}

		object.next_ = (GMLobject) value_;
		value_ = object;
	}

	// Do not call from a non-list object.
	public void addObjectToEnd(GMLobject object)
	{

		if (type_ != GMLlist && type_ != GMLfile)
		{

			return;
		}

		GMLobject obj;

		for (obj = (GMLobject) value_;
				obj != null && obj.next_ != null;
				obj = obj.next_)
		{
			;
		}

		if (obj == null)
		{
			value_ = object;
		}
		else
		{
			obj.next_ = object;
		}
	}


	public void deleteAll(String key, int type)
	{

		GMLobject object      = (GMLobject) value_;
		GMLobject prevobject  = null;

		while (object != null)
		{

			if (object.key_.equals(key) && object.type_ == type)
			{
				object = object.next_;

				if (prevobject != null)
				{
					prevobject.next_ = object;
				}
				else
				{
					value_ = object;
				}
			}
			else
			{
				prevobject = object;
				object = object.next_;
			}
		}
	}

	// Delete all unsafe sub objects (hierarchical) except those with key "vgj".
	public void deleteUnsafe()
	{

		if (type_ != GMLlist && type_ != GMLfile)
		{

			return;
		}

		GMLobject object      = (GMLobject) value_;
		GMLobject prevobject  = null;

		while (object != null)
		{

			int chr;

			if (!object.key_.equals("vgj") &&
					(chr = object.key_.charAt(0)) >= 'A' && chr <= 'Z')
			{
				object = object.next_;

				if (prevobject != null)
				{
					prevobject.next_ = object;
				}
				else
				{
					value_ = object;
				}
			}
			else
			{
				object.deleteUnsafe();
				prevobject = object;
				object = object.next_;
			}
		}
	}


	/**
	 * Remove all null GMLlists from the object tree.
	 */
	public void prune()
	{

		GMLobject obj;
		GMLobject prevobj;

		if (type_ != GMLlist)
		{

			return;
		}

		for (obj = (GMLobject) (value_); obj != null; obj = obj.next_)
		{
			obj.prune();
		}

		prevobj = null;

		for (obj = (GMLobject) (value_); obj != null; obj = obj.next_)
		{

			if (obj.value_ == null)
			{

				if (prevobj != null)
				{
					prevobj.next_ = obj.next_;
				}
				else
				{
					value_ = obj.next_;
				}
			}
			else
			{
				prevobj = obj;
			}
		}
	}


	/**
	 * Print the text representation of the object tree, with "numtabs"
	 *  leading tabs.
	 *
	 * @param  numtabs  Description of the Parameter
	 * @return          Description of the Return Value
	 */
	public String toString(int numtabs)
	{

		if (value_ == null)
		{

			return "";
		}

		String retval  = "";
		int i;

		for (i = 0; i < numtabs; i++)
		{
			retval += "   ";
		}

		if (key_ != null)
		{  // For the top level "GMLfile" object, the key

			// is null.
			retval += key_;
		}

		if (type_ == GMLinteger)
		{
			retval += " " + ((Integer) value_).toString() + "\n";
		}
		else if (type_ == GMLreal)
		{

			// Use GML real format
			double val           = ((Double) value_).doubleValue();

			if (val == Double.POSITIVE_INFINITY || val == Double.NaN)
			{
				val = Double.MAX_VALUE;
			}

			if (val == Double.NEGATIVE_INFINITY)
			{
				val = Double.MIN_VALUE;
			}

			String doublestring  = Double.toString(val);

			if (doublestring.indexOf('.') == -1)
			{

				String expstring  = "";
				int expstart;

				if ((expstart = doublestring.indexOf('e')) != -1)
				{
					expstring = doublestring.substring(expstart);
					doublestring = doublestring.substring(0, expstart);
				}

				doublestring += ".0" + expstring;
			}

			retval += " " + doublestring + "\n";
		}
		else if (type_ == GMLstring)
		{

			if (value_ == null)
			{
				retval += " \"\"\n";
			}
			else
			{
				retval += " \"" + (String) value_ + "\"\n";
			}
		}
		else if (type_ == GMLlist)
		{
			retval += " [\n";

			GMLobject obj;

			for (obj = (GMLobject) value_; obj != null; obj = obj.next_)
			{

				if (obj.value_ != null)
				{
					retval += obj.toString(numtabs + 1);
				}
			}

			for (i = 0; i < numtabs; i++)
			{
				retval += "   ";
			}

			retval += "]\n";
		}
		else
		{  // File

			GMLobject obj;

			for (obj = (GMLobject) value_; obj != null; obj = obj.next_)
			{
				retval += obj.toString(numtabs);
			}
		}

		return retval;
	}


	private GMLobject parseGMLlist_(GMLlexer lexer)
		throws java.io.IOException, ParseError
	{

		GMLobject retval   = null;
		GMLobject prevobj  = null;
		int token;

		while ((token = lexer.nextToken()) == GMLlexer.GMLkey)
		{

			GMLobject newobj  = new GMLobject(lexer, lexer.getStringval());

			if (prevobj == null)
			{
				retval = newobj;
			}
			else
			{
				prevobj.next_ = newobj;
			}

			prevobj = newobj;
		}

		return retval;
	}
}
