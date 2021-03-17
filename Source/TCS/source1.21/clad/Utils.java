package clad;

import java.util.ArrayList;
import java.util.Enumeration;

/**
 *  Description of the Class
 *
 * @author     wooo
 * @created    May 18, 2004
 */
public class Utils {
	final static char A = 'A';// = adenine
	final static char C = 'C';// = cytosine
	final static char G = 'G';// = guanine
	final static char T = 'T';// = thymine
	final static char U = 'U';// = uracil
	final static char R = 'R';// = G A (purine)
	final static char Y = 'Y';// = T C (pyrimidine)
	final static char K = 'K';// = G T (keto)
	final static char M = 'M';// = A C (amino)
	final static char S = 'S';// = G C
	final static char W = 'W';// = A T
	final static char B = 'B';// = G T C
	final static char D = 'D';// = G A T
	final static char H = 'H';// = A C T
	final static char V = 'V';// = G C A
	final static char N = 'N';// = A G C T (any)
	final static char Z = '?';// unknown character
	final static char[] matrix = {
	/*  A, B, C, D, e, f, G, H, i, j, K, l, M, N, o, p, q, R, S, T, U, V, W, x, Y */
	/*A*/
			A, N, M, D, Z, Z, R, H, Z, Z, D, Z, M, N, Z, Z, Z, R, V, W, Z, V, W, Z, H,
	/*B*/
			N, B, B, N, Z, Z, B, N, Z, Z, B, Z, N, N, Z, Z, Z, N, B, B, Z, N, N, Z, B,
	/*C*/
			M, B, C, N, Z, Z, S, H, Z, Z, B, Z, M, N, Z, Z, Z, V, S, Y, Z, V, H, Z, Y,
	/*D*/
			D, N, N, D, Z, Z, D, N, Z, Z, D, Z, N, N, Z, Z, Z, D, N, D, Z, N, D, Z, N,
	/*e*/
			Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z,
	/*f*/
			Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z,
	/*G*/
			R, B, S, D, Z, Z, G, N, Z, Z, K, Z, V, N, Z, Z, Z, R, S, K, Z, V, D, Z, B,
	/*H*/
			H, N, H, N, Z, Z, N, H, Z, Z, N, Z, H, N, Z, Z, Z, N, N, H, Z, N, H, Z, H,
	/*i*/
			Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z,
	/*j*/
			Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z,
	/*K*/
			D, B, B, D, Z, Z, K, N, Z, Z, K, Z, N, N, Z, Z, Z, D, B, K, Z, N, D, Z, B,
	/*l*/
			Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z,
	/*M*/
			M, N, M, N, Z, Z, V, H, Z, Z, N, Z, M, N, Z, Z, Z, V, V, H, Z, V, H, Z, H,
	/*N*/
			N, N, N, N, Z, Z, N, N, Z, Z, N, Z, N, N, Z, Z, Z, N, N, N, Z, N, N, Z, N,
	/*o*/
			Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z,
	/*p*/
			Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z,
	/*q*/
			Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z,
	/*R*/
			R, N, V, D, Z, Z, R, N, Z, Z, D, Z, V, N, Z, Z, Z, R, V, D, Z, V, D, Z, N,
	/*S*/
			V, B, S, N, Z, Z, S, N, Z, Z, B, Z, V, N, Z, Z, Z, V, S, B, Z, V, N, Z, B,
	/*T*/
			W, B, Y, D, Z, Z, K, H, Z, Z, K, Z, H, N, Z, Z, Z, D, B, T, Z, N, W, Z, Y,
	/*U*/
			Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z,
	/*V*/
			V, N, V, N, Z, Z, V, N, Z, Z, N, Z, V, N, Z, Z, Z, V, V, N, Z, V, N, Z, N,
	/*W*/
			W, N, H, D, Z, Z, D, H, Z, Z, D, Z, H, N, Z, Z, Z, D, N, W, Z, N, W, Z, H,
	/*x*/
			Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z,
	/*Y*/
			H, B, Y, N, Z, Z, B, H, Z, Z, B, Z, H, N, Z, Z, Z, N, B, Y, Z, N, H, Z, Y
			};



	/**
	 *  DP wrote it. The calcPars function is called by the buildParseTable function. It integrates according to some
	 *  algorithm that I am not aware of and returns a result that enables the b.P.T to determine the max distance.
	 *
	 * @param  j   int
	 * @param  m   Description of the Parameter
	 * @param  it  Description of the Parameter
	 * @return     double
	 * @created    (1/12/00 10:35:02 PM)
	 */

	protected static double calcPars(int j, int m, int it) {

		double i;
		double product;
		double dq;
		double pq;
		double num1;
		double num2;
		double q;
		double int1;
		double int2;
		double t1;
		double tr;
		double tp;
		int b = 3;
		int r = 1;
		double u = 1.0;
		product = 1.0;
		dq = u / it;

		/*
		Product
		*/
		tr = (double)1.0 / (b * r);
		tp = (double)2.0 * m + 1.0;

		for (i = 0.00000001; i < j; i += 1.0) {

			/*
			Integrate
			*/
			int1 = 0.0;
			int2 = 0.0;

			for (q = 0.0000001; q < u; q += dq) {
				t1 = (double)1.0 - q * tr;
				num2 = Math.pow(q, i) * Math.pow(((double)1.0 - q), tp) * t1 * Math.pow(t1 + 1.0 - q, i) * ((double)1.0 - 2 * q * t1) * dq;
				num1 = num2 * q;
				int1 += num1;
				int2 += num2;
			}

			pq = (double)1.0 - int1 / int2;
			product *= pq;
		}

		return (product);
	}

	protected static TaxaItem [] getAllResolved(TaxaItem source, TaxaItem dest) {
	    
	    int numNbors = source.nbor.size();
	    TaxaItem resolvedNbors [] = new TaxaItem[numNbors+1]; 
	    resolvedNbors [numNbors] = dest; // do I need to include this one?  any difference between only it and the source, should already be apparent, if we are resolved at that spot in the source,. right?
	    for (int x = 0; x < numNbors; x++) {
	        resolvedNbors[x] = getResolved((TaxaItem)source.nbor.get(x),source);
	    }
	    return resolvedNbors;
	}
	
	/**
	 * Returns a list of all path edges, and the source (real taxa if one is) and dest
	 * @param source
	 * @param prev
	 * @return
	 */
	/*protected static Object[] getPath(LWEdge edge, LWEdge next) {
	    Object [] result = new Object[3]; // first element is source, second is list of edges, third is dest
	    ArrayList edges = new ArrayList();
	    TaxaItem source = edge.source;
	    TaxaItem dest = edge.dest;
	    int numNbors = source.nbor.size();
	    
	    if (numNbors > 2) {
	        if (source.isIntermediate) {
	            result[2] = source;
	        } else {
	            result[0] = source;
	        }
	    }
	    if (source.nbor.get(0) == prev) {
	        return getResolved((TaxaItem)source.nbor.get(1),source);	        
	    } else {
	        return getResolved((TaxaItem)source.nbor.get(0),source);
	    }
	}*/
	
	protected static TaxaItem getResolved(TaxaItem source,TaxaItem prev) {
	    
	    if (source.resolved) {
	        return source;
	    }
	    int numNbors = source.nbor.size();
	    if (numNbors > 2) {
	        System.out.println("woops, we are at an unresolved fork!");
	        // just return this source?  
	        // return source;
	        
	    }
	    if (source.nbor.get(0) == prev) {
	        return getResolved((TaxaItem)source.nbor.get(1),source);	        
	    } else {
	        return getResolved((TaxaItem)source.nbor.get(0),source);
	    }
	}
	
	
	/**
	 *  Updates the compdist between all srcComponent taxa and all destComponent taxa i.e. if the new dist is less than the
	 *  previous one
	 *
	 * @param  sourcec  The source taxon's component
	 * @param  destc    The destination taxon's component
	 * @param  newdist  The new distance upon adding this connection
	 * @created
	 */
	protected static void updateDistance(Component sourcec, Component destc, Distance newdist) {

		Enumeration srcCompsTaxaEnum = sourcec.taxa.elements();

		while (srcCompsTaxaEnum.hasMoreElements()) {
			TaxaItem curSrcTaxa = (TaxaItem)srcCompsTaxaEnum.nextElement();
			Distance curSrcDist = (Distance)curSrcTaxa.compdist.get(newdist.source);

			Enumeration dstCompsTaxaEnum = destc.taxa.elements();

			while (dstCompsTaxaEnum.hasMoreElements()) {

				TaxaItem curDestTaxa = (TaxaItem)dstCompsTaxaEnum.nextElement();

				Distance curDestDist = (Distance)curDestTaxa.compdist.get(newdist.destination);

				if ((curSrcDist.distance == TCS.INFINITY) || (curDestDist.distance == TCS.INFINITY)) {
					continue;
				} else {
					// If there was some kind of a connection to the border taxa before
					// Update the distance between this node and all others in the opposite cluster

					Distance curDist = (Distance)curSrcTaxa.compdist.get(curDestTaxa.id);
					int newDistance = curDestDist.distance + curSrcDist.distance + newdist.distance;

					if (newDistance < curDist.distance) {
						curDist.distance = newDistance;
						// Reflect these so that both clusters see them. (aren't all distances symmetric?)
						curDist = (Distance)curDestTaxa.compdist.get(curSrcTaxa.id);
						curDist.distance = newDistance;
					}
				}
			}
		}
	}


	/**
	 *  returns the consensus sequence using iupac ambiguity characters if needed, from destt and sourcet
	 *
	 * @param  sourcet  source taxa
	 * @param  destt    destination taxa
	 * @return          The consensus value
	 * @created         May 18, 2004
	 */
	static char[] getConsensus(TaxaItem sourcet, TaxaItem destt) {
		int length = sourcet.characters.length;
		char[] consensus = new char[length];

		for (int i = 0; i < length; i++) {
			char dest = destt.characters[i];
			char src = sourcet.characters[i];// 65 is offset of ascii for'A'

			if (dest < 65 || dest > 90 || src < 65 || src > 90) {
				consensus[i] = Z;
			} else {
				consensus[i] = matrix[(dest - 65) * 25 + (src - 65)];
			}
		}
		return consensus;
	}

}
