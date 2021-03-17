/*
 * Created on May 16, 2005
 * Created by wooo as part of TCS
 */
package clad;

import java.util.ArrayList;

/**
 * @author wooo
 *
 * A lightweight Edge object for use when mapping characters
 */
public class LWEdge {
    /**
     * @param myDestt
     * @param mySourcet
     */
    
    public LWEdge(TaxaItem myDestt, TaxaItem mySourcet, int myType, int label) {
        source = mySourcet;
        dest = myDestt;
        type = myType;
        this.label = "" + label;
        third = new ArrayList();
        inPath = false;
        path = null;
		
				//        state = 0;
    }
    public Path path;

    final public static int INTINT = 0;
    final public static int HAPINT = 1;
    final public static int HAPHAP = 2;
    public ArrayList third;
    public boolean inPath;
    public int type;
    public TaxaItem source;
    public TaxaItem dest;
    public String label;
    public String sites;
    public double confidence;
    public ArrayList differences = new ArrayList();
//    public int state;
    public String toString() {
//        return "type = " + type + " source = " + source.toString() + " dest = " + dest.toString();
        return label;
    }
}
