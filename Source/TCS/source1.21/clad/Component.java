package clad;

import java.util.Vector;

/**
 *  Description of the Class
 *
 * @author    [MC]
 */
public class Component {
	/**  vector of distance items */
	public Vector compdist;
	/**  Description of the Field */
	public int id;
	/**  the minimum distance between this and all other taxa items */
	public Distance mindist;
	/**  has only one until it becomes a cluster of taxa doesn't need to be a vector? */
	public Vector taxa;



	/**
	 *  Constructor for the Component object
	 *
	 * @param  ident  Description of Parameter
	 */
	protected Component(int ident) {
		taxa = new Vector();
		compdist = new Vector();
		id = ident;
		mindist = new Distance();
	}
}
