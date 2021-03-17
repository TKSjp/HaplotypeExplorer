/*
 * Created on May 20, 2005
 * Created by wooo as part of TCS
 */
package clad;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author wooo
 *
 * A Path object used for mapping characters to Networks
 */
public class Path {
    /**
     * @param myDestt
     * @param mySourcet
     */
    
    public Path(TaxaItem mySourcet, TaxaItem myDestt, int myType) {
        source = mySourcet;
        dest = myDestt;
        type = myType;
        resolved = false;
        edges = new ArrayList();
        differences = new ArrayList();
		isAmbiguous = false; 
//        state = 0;
    }
    final public static int INTINT = 0;
    final public static int HAPINT = 1;
    final public static int HAPHAP = 2;
    public ArrayList edges;
	public boolean isAmbiguous;
    public int type;
    public TaxaItem source;
    public TaxaItem dest;
    public String label;
    public String sites;
    public boolean resolved;
    public ArrayList differences;
    public String toString() {
//        return "type = " + type + " source = " + source.toString() + " dest = " + dest.toString();
        return "type=" +type+" source="+source+" dest=" + dest + " resolved=" +resolved+" length="+edges.size() + "(" +getLabel()+ ")";
    }
    
    
        
    
    /**
     * @return
     */
    public String getLabel() {

        StringBuffer s = new StringBuffer();
        Iterator it = differences.iterator();
        while (it.hasNext()) {
            s.append(it.next());
            if (it.hasNext())
                s.append(",");
        }
        return s.toString();
    }
}
