package clad;


/**
 *  Description of the Class
 *
 * @author
 * @created
 */
public class Distance {
	/**  source id (most likely place in vector) */
	public int source;
	/**  ptr to the source component */
	public Component sc;
	/**  destination id (likely spot in vector) */
	public int destination;
	/**  ptr to the destination component */
	public Component dc;
	/**  distance between the two node */
	public int distance;
	/**  Description of the Field */
	public boolean marked;


	/**
	 *  Constructor for the Distance object
	 *
	 * @created
	 */
	Distance() {
		source = 0;
		destination = 0;
		distance = 0;
		marked = false;
	}


	/**
	 *  Constructor for the Distance object
	 *
	 * @param  src   Description of Parameter
	 * @param  dest  Description of Parameter
	 * @param  dist  Description of Parameter
	 * @created
	 */
	Distance(int src, int dest, int dist) {
		source = src;
		destination = dest;
		distance = dist;
		marked = false;
	}


	/**
	 *  Insert the method's description here. Creation date: (11/8/99 7:12:54 AM)
	 *
	 * @param  src   Description of the Parameter
	 * @param  dest  Description of the Parameter
	 * @param  dist  Description of the Parameter
	 * @param  srcc  Description of the Parameter
	 * @param  dstc  Description of the Parameter
	 * @created
	 */
	Distance(int src, int dest, int dist, Component srcc, Component dstc) {
		source = src;
		destination = dest;
		distance = dist;
		marked = false;
		sc = srcc;
		dc = dstc;
	}


	/**
	 *  Insert the method's description here. Creation date: (11/5/99 9:40:45 AM)
	 *
	 * @param  sourced  Description of the Parameter
	 * @created
	 */
	public void clone(Distance sourced) {
		this.source = sourced.source;
		this.destination = sourced.destination;
		this.marked = sourced.marked;
		this.sc = sourced.sc;
		this.distance = sourced.distance;
		this.dc = sourced.dc;
	}
}
