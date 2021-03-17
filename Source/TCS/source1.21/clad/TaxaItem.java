package clad;

import java.util.ArrayList;
import java.util.Vector;


/**
 *  Description of the Class
 *
 * @author     [JD]
 * @created    May 18, 2004
 */
public class TaxaItem {

	/**  Description of the Field */
	public final int BIGNUM = 32767;

	/**  Description of the Field */
	public boolean visited;
	/**  Description of the Field */
	public int levelNumber;

	/**  Description of the Field */
	public char[] characters;
	/**  This is our computed distance and there is one for everyone -distance obj */
	public Vector compdist;
	/**  Keeps track of the names of the duplicate guys */
	public Vector dupnames;
	/**  Internal identifier for this TaxaItem */
	public int id;
	/**  Description of the Field */
	public boolean isIntermediate;
	/**  Description of the Field */
	public boolean isTip;
	/**  This is a temporary distance array with one for everyone */
	public Vector metricdist;
	/**  The minimum distance (we dont allow any wrong connections less than this */
	public int minRealDist;
	/**  Description of the Field */
	public String name;
	/**  Description of the Field */
	public Vector nbor;
	/**  Description of the Field */
	public Vector newconnections;
	/**  the number of individual sequences identical to this one */
	public int numduplicates;
	/**  Description of the Field */
	public double oweight;
	/**  Keep track of the component to which it belongs */
	public Component parentComponent;
	/**  This only has entries for real taxa (not intermediates)-distance obj */
	public Vector realdist;
	/**  outgroup weights */
	public double weight;
	/**  haplotype inferral confidence */
	public double confidence;
	/**  if this taxa's sequence is resolved */
	public boolean resolved;
	public ArrayList paths;
	public boolean isAmbiguous;
	/**
	 *  Constructor for the TaxaItem object (used for internal Nodes only)
	 *
	 * @param  n      The String name of this node
	 * @param  ident  the id of this node
	 * @param  chars  The iupac code sequence for this intermediate node
	 * @created       May 18, 2004
	 */
	protected TaxaItem(String n, int ident, char[] chars) {
	   paths = new ArrayList();
		name = n;
		characters = chars;
		realdist = new Vector();
		compdist = new Vector();
		nbor = new Vector();
		newconnections = new Vector();
		id = ident;
		numduplicates = 0;
		dupnames = new Vector();
		metricdist = new Vector();
		weight = 0;
		oweight = 0;
		visited = false;
		levelNumber = -1;
		resolved = false;
		isAmbiguous = false;
	}


	/**
	 *Constructor for the TaxaItem object
	 *
	 * @param  n       The String name of this node
	 * @param  length  The length of the sequence
	 * @param  ident   the id of this node
	 * @created
	 */
	protected TaxaItem(String n, int length, int ident) {
	    paths = new ArrayList();
		name = n;
		characters = new char[length];
		realdist = new Vector();
		compdist = new Vector();
		nbor = new Vector();
		newconnections = new Vector();
		id = ident;
		numduplicates = 0;
		dupnames = new Vector();
		metricdist = new Vector();
		weight = 0;
		oweight = 0;
		visited = false;
		levelNumber = -1;
		resolved = false;
		isAmbiguous = false;
	}


	/**
	 *  Converts to a String representation of the object.
	 *
	 * @return     A string representation of the object.
	 * @created    May 18, 2004
	 */
	public String toString() {
		/*String result = "***************************************\n";
		result += "id = " + id;
		result += "\nname = " + name;
		result += "\ncharacter = " + characters;
		result += "\nnumdup = " + numduplicates;
		result += "\nisintermediate = " + isIntermediate;
		result += "\nisresolved = " + resolved;
		result += "\n" + nbor.size() + " nbors = {";
		for (int i = 0; i < nbor.size(); i++) {
			result += "\n\t" + ((TaxaItem)nbor.get(i)).id;
		}
		result += "\n}";
		return result;*/
	    return ""+name + "(resolved=" +resolved +")";
	}
}
