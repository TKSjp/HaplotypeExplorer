package clad;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

/**
*  Class that handles most of the IO related to the TCS.java class
 *
 * @author     Steven Woolley, using methods created by DP, JD, MC
 * @created    May 17, 2004
 */
public class TCSIO {
	/**
	*  Description of the Method
	 *
	 * @param  components  Description of Parameter
	 * @param  Log         Description of Parameter
	 * @created            May 17, 2004
	 */
	protected static void printTaxa(Vector components, Logger Log) {
		Enumeration enumc = components.elements();
		
		while (enumc.hasMoreElements()) {
			Component tempc = (Component)enumc.nextElement();
			Log.dprintln("tempc id: " + tempc.id);
			Enumeration enumt = tempc.taxa.elements();
			
			while (enumt.hasMoreElements()) {
				TaxaItem tempt = (TaxaItem)enumt.nextElement();
				Log.dprintln("tempt id: " + tempt.id);
				Log.dprintln("Name: " + tempt.name);
				Log.dprintln("Num Duplicates: " + tempt.numduplicates);
				Log.dprintln("Data" + tempt.characters);
			}
		}
	}
	
	
	/**
	*  Description of the Method
	 *
	 * @param  filename    Description of the Parameter
	 * @param  components  Description of Parameter
	 * @param  distances   Description of Parameter
	 * @param  Log         Description of Parameter
	 * @param  maxWtaxa    Description of Parameter
	 * @param  maxNameLen  Description of Parameter
	 * @created            7-15-03
	 */
	protected static void printGraph(String filename, Vector components, boolean distances, Logger Log, String[] maxWtaxa, int maxNameLen, ArrayList edges) {
		// added SW 7-15-03 to output in a standard format the network
		Enumeration enumc = components.elements();
		int numNetworks = components.size() + 1;
		/*if (numNetworks > 2) {
			System.out.println("There are multiple networks (" + components.size() + ")!");
		}*/
		TaxaItem[] roots = new TaxaItem[numNetworks];
		
		int networkNum = 0;
		
		TextOutputStream out = new TextOutputStream(filename);
		
		out.println("graph [");
		out.println("directed 0");
		
		// Print out all the nodes and their neighbors
		while (enumc.hasMoreElements()) {
			Component tempc = (Component)enumc.nextElement();
			// int numberOfNodes = tempc.taxa.size();
			// outString += numberOfNodes + "\n";
			Enumeration enumt = tempc.taxa.elements();
			networkNum++;
			
			while (enumt.hasMoreElements()) {
				TaxaItem tempt = (TaxaItem)enumt.nextElement();
				
				if (distances) {
					tempt.numduplicates = 0;
				}
				
				//
				// Print out the node
				//Log.dprint(tempt.name + "[" + tempt.id + "]: ");
				out.println("node [");
				out.println("   id " + tempt.id);
				
				if (tempt.isIntermediate) {
					
					if (!TCS.printIntermediateLabels) {
						out.println("   label \" \"");
						out.println("   graphics [");
						out.println("   width 6.0");
						//these give the old format
						out.println("   height 6.0");
					} else {
						out.println("   label \"" + tempt.name + "\"");
						out.println("   graphics [");
						out.println("   width 28.0");
						out.println("   height 18.0");
					}
					
					/////////////////////////////////////////////////jake 10-27-00
					out.println("   depth 1.0");
					out.println("]");
					/*out.println("   data [");
					out.println("   ]");
					out.println("   vgj [");
					out.println("      labelPosition \"center\"");
					out.println("   ]");*/
				} else {
					out.println("   label \"" + tempt.name + "\"");
					out.println("   vgj [");
					
					if (tempt.name.equalsIgnoreCase(maxWtaxa[networkNum])) {
						roots[networkNum] = tempt;
						
						// added for finding direction
						out.println("   shape  \"rectangle\"");
					}
					
					out.println("      labelPosition \"center\"");
					out.println("   ]");
					out.println("   graphics [");
					int namesize = 9 * maxNameLen + tempt.numduplicates * 5;
					out.println("   width " + namesize);
					namesize = 20 + tempt.numduplicates * 5;
					out.println("   height " + namesize);
					out.println("   depth 20.0");
					out.println("]");
					
					/*out.println("   data [");
					out.println("    Frequency \"frequency=" + (tempt.numduplicates + 1));
					out.println(" " + tempt.name);
					// Print out this taxa duplicates
					Enumeration enumd = tempt.dupnames.elements();
					while (enumd.hasMoreElements()) {
						String tempd = (String)enumd.nextElement();
						out.println(" " + tempd);
					}
					out.println("\"");
					out.printf("    Weight \"outgroup weight= %6.2f", tempt.oweight);
					out.println("\"");
					
					
					out.println("    Sequence \"Sequence =");
					// String seq = new String(tempt.characters);
					out.println(new String(tempt.characters));
					out.println("\"");
					
					out.println("   ]");*/
					/*out.println("   vgj [");
					out.println("      labelPosition \"center\"");
					out.println("   ]");*/
				}
				
				
				out.println("   data [");
				out.println("    Frequency \"frequency=" + (tempt.numduplicates + 1));
				out.println(" " + tempt.name);
				
				// Print out this taxa duplicates
				Enumeration enumd = tempt.dupnames.elements();
				
				while (enumd.hasMoreElements()) {
					
					String tempd = (String)enumd.nextElement();
					out.println(" " + tempd);
				}
				
				out.println("\"");
				out.printf("    Weight \"outgroup weight= %6.2f", tempt.oweight);
				out.println("\"");
				
				out.println("    Sequence \"Sequence =");
				// String seq = new String(tempt.characters);
				if (tempt.characters != null)
				    out.println(new String(tempt.characters));
				out.println("\"");
				
				out.println("   ]");
				out.println("   vgj [");
				out.println("      labelPosition \"center\"");
				out.println("   ]");
				
				out.println("]");
				
			}
			
			// for this each element of this specific connected network
			// this particular connected part (Component)
			//			boolean hasAmbiguity = directAndPrintEdges(out, roots[networkNum], Log);
			
			//			Log.dprintln("Ambiguous = " + hasAmbiguity);
			//		directAndPrintEdges(out, roots[networkNum], Log);
			if(networkNum == 1) {
			    printEdges(out,edges,Log);
			}
			//Log.dprintln("Ambiguous = " + "?");
		}
		
		// end of for each connected element of the overall graph
		out.println("]");
		out.close();
	}
	
	
	
	/**
	* @param myOut
     * @param myEdges
     * @param myLog
     */
    private static void printEdges(TextOutputStream out, ArrayList myEdges, Logger myLog) {
        Iterator it = myEdges.iterator();
        while (it.hasNext()) {
			LWEdge edge = (LWEdge)it.next();
			String label = edge.path.getLabel();
			String changes = "";
			String ambig = "";
			if (label.length() != 0) {
				// we have data for this edge
				if (edge.path.edges.size() != edge.path.differences.size() || edge.path.source.isAmbiguous || edge.path.dest.isAmbiguous) {
					ambig = "*";
				}
				int edgeIndex = edge.path.edges.indexOf(edge);
				if (edgeIndex >= edge.path.differences.size()){ // if there weren't as many chars as branches...
					changes = "";
					label = "";
				} else {
					int pos = ((Integer)edge.path.differences.get(edgeIndex)).intValue();
					char s = edge.path.source.characters[pos]; 
					char d = edge.path.dest.characters[pos];
					// need to know the orientation of this relative to source/dest's id!
					// go from edge.source (by source) until we reach path.source or path.dest
					// if we reached path.source s=s
					// if we reached path.dest, then s=d and d = s;
					TaxaItem temp = edge.source;
					TaxaItem prev = edge.dest;
					while (temp != edge.path.source && temp != edge.path.dest) {
						if (temp.nbor.get(0) != prev) {
							// then get this as the next temp
							prev = temp;
							temp = (TaxaItem)temp.nbor.get(0);
						} else {
							prev = temp;
							temp = (TaxaItem)temp.nbor.get(1);
						}
					}
					if (temp == edge.path.source) {
						
					} else {
						char t = d;
						d = s;
						s = t;
					}
					
					int sid = edge.source.id; 
					int did = edge.dest.id;
					
					// do the lower id first
					changes += ((did > sid)?s:d) + "\t" + ((did > sid)?d:s);
					label = "" + (pos + 1);
				}
			}
			//FIXME put in code to save the character states for the two ends, and then display it in the label?
			out.println("edge [");
			out.println("   linestyle \"solid\"");
			out.println("   label \"" +ambig + label + "\"");
			out.println("   source " + edge.source.id);
			out.println("   target " + edge.dest.id);
			out.println("data [");
			out.println("Changes \"" + changes +"\"");
			out.println("]");
			out.println("]");
			
		}
		
        out.flush();
		
        
    }
	
	
    /**
	*  this prints out the names of taxa from the component list and all dups names [DP]
	 *
	 * @param  components  Description of Parameter
	 * @param  Log         Description of Parameter
	 * @created            May 17, 2004
	 */
	protected static void printHaplotypeList(Vector components, Logger Log) {
		
		Log.dprintln("\nHaplotype list:\n\n");
		
		Enumeration enumc = components.elements();
		while (enumc.hasMoreElements()) {
			
			Component tempc = (Component)enumc.nextElement();
			Enumeration enumt = tempc.taxa.elements();
			
			while (enumt.hasMoreElements()) {
				TaxaItem tempt = (TaxaItem)enumt.nextElement();
				Log.dprint(" - " + tempt.name + " : ");
				Enumeration enumd = tempt.dupnames.elements();
				
				while (enumd.hasMoreElements()) {
					String tempd = (String)enumd.nextElement();
					Log.dprint(" " + tempd);
				}
				
				Log.dprintln();
			}
		}
		
		Log.dprintln();
	}
	
	
	
	/**
	*  Description of the Method
	 *
	 * @param  out   Description of the Parameter
	 * @param  root  Description of the Parameter
	 * @param  Log   Description of Parameter
	 * @return       Description of the Return Value
	 * @created      May 17, 2004
	 */
	protected static boolean directAndPrintEdges(TextOutputStream out, TaxaItem root, Logger Log) {
		
		boolean isAmbig = false;
		
		// get a minimum distance from root for each node, and a min dist to a leaf?
		//       use a stack to do a breadth first traversal (highest weight first?), going away from root if there is no abiguity...
		root.levelNumber = 0;
		
		LinkedList queue = new LinkedList();
		queue.addLast(root);
		
		// Number of steps away from the root
		while (queue.size() > 0) {
			
			TaxaItem cur = (TaxaItem)queue.removeFirst();
			cur.visited = true;
			
			TaxaItem source;
			TaxaItem dest;
			Enumeration enumn = cur.nbor.elements();
			TaxaItem tempn;
			int numNeighbors = cur.nbor.size();
			int numEdges = 0;
			
			// need to keep track of whether or not the node has been visited
			while (numEdges < numNeighbors) {
				
				//if enumn != null?
				tempn = (TaxaItem)enumn.nextElement();
				
				if (tempn.levelNumber == -1) {
					tempn.levelNumber = cur.levelNumber + 1;
				}
				
				if (!tempn.visited) {
					source = cur;
					dest = tempn;
					
					// System.out.println("numEdge visiting = " + numEdges + "(" + tempn.name + ")");
					/*					if (tempn.levelNumber < cur.levelNumber) {
						// if cur is further from the root than it's child
						Log.dprintln("Weird!  Parent is farther from root than Child!");
					}*/
					if (tempn.levelNumber == cur.levelNumber) {
						
						// if they are at the same level (Also should mean that there is ambig!)
						
						// add to this all those where tempn is more than one level from cur
						if (tempn.isIntermediate != cur.isIntermediate) {
							
							// make the intermed -> leaf
							if (tempn.isIntermediate) {
								
								//this is not absolutely true... It seemed it had to be that way because otherwise I would get an internal nodes with no outgoing branch
								// at least with my algorithm...  that is probably the most likely...
								// However, if there is at least one outgoing, higher-level branch from the intermediate, it could be the other way...
								if (hasUnvisitedNeighbors(tempn)) {
									
									// perhaps I should do a weight comparison?
									
									if (tempn.oweight > cur.oweight) {
										source = tempn;
										dest = cur;
									}
								} else {
									source = tempn;
									dest = cur;
								}
							} else {
								
								// if cur is intermediate...
								
								//this is not absolutely true... It seemed it had to be that way because otherwise I would get an internal nodes with no outgoing branch
								// at least with my algorithm...  that is probably the most likely...
								// However, if there is at least one outgoing, higher-level branch from the intermediate, it could be the other way...
								if (hasUnvisitedNeighbors(cur)) {
									
									// perhaps I should do a weight comparison?
									
									if (tempn.oweight > cur.oweight) {
										source = tempn;
										dest = cur;
									}
								} else {
									dest = tempn;
									source = cur;
								}
							}
						} else {
							
							// if both are intermediates or leaves
							// find the best weight
							//compare the weights (which one should be the root?)
							// rootest has greatest oweight
							
							if (tempn.oweight > cur.oweight) {
								source = tempn;
								dest = cur;
							}
						}
					}
					// find a path in both source and dest, and use its differences as a label
					HashSet h1 = new HashSet (source.paths);
					HashSet h2 = new HashSet (dest.paths);
					h1.retainAll(h2);
					
					Path[] p = new Path[0];
					p = (Path[])h1.toArray(p);
					String label = "";
					if (p.length != 0) {
					    
					    label = p[0].getLabel();
						//System.out.println("   label \""+ label +"\"");
					} else {
						System.out.println("unknown path");
						//					    System.out.println("h1 had " + source.paths.size());
						//              Iterator i = source.paths.iterator();
						//              while (i.hasNext()){
						//								System.out.println(i);
						//              }
						//					    System.out.println("h2 had " + dest.paths.size());
						//					    i = source.paths.iterator();
						//              while (i.hasNext()){
						//								System.out.println(i);
						//              }
					}
					
					out.println("edge [");
					out.println("   linestyle \"solid\"");
					out.println("   label \"" + label +"\"");
					out.println("   source " + source.id);
					out.println("   target " + dest.id);
					out.println("   data [");
					out.println("      Changes \"" + label +"\"");
					out.println("   ]");
					out.println("]");
					
					//sort the neighbors by the weight
					// then push them on the stack highest weight first
					// add them to the queue in the right order
					// only add if it is not a leaf, and it is not yet visited
					if (!queue.contains(tempn)) {
						queue.addLast(tempn);
					} else {
						isAmbig = true;
					}
				}
				
				numEdges++;
			}
			
		}
		
		return isAmbig;
	}
	
	
	/**
	*  Method to determine if all of a given internal node's neighbors have been visited in the Directing algorithm
	 *
	 * @param  tempn  Description of the Parameter
	 * @return        Description of the Return Value
	 * @created       May 17, 2004
	 */
	protected static boolean hasUnvisitedNeighbors(TaxaItem tempn) {
		
		Vector neig = tempn.nbor;
		int size = neig.size();
		
		while (size > 0) {
			
			TaxaItem temp = (TaxaItem)neig.get(--size);
			
			if (!temp.visited) {
				
				return true;
			}
		}
		
		return false;
	}
	
	
	/**
	*  Outputs the real, computed and difference distance matrices to the Logfile
	 *
	 * @param  realtaxa  Description of Parameter
	 * @param  Log       Description of Parameter
	 * @created          May 17, 2004
	 */
	protected static void printMatrix(Vector realtaxa, Logger Log) {
		// [SW] Rewrote eliminating copied code, no longer used matrixfile but outputs to the log file
		//thecase = 0 realdist, 1 real and calcdist
		/////////print matrix completely rewriten 02/07/01 jake
		TaxaItem tempt;
		TaxaItem tempt2;
		
		// Enumeration enum1;
		
		// Enumeration enum2;
		Distance dist;
		Distance dist2;
		
		int tabs = 0;
		
		int matrixdiff = 0;
		int matrixminus = 0;
		StringBuffer realDistLog = new StringBuffer("\n\nREAL ");
		StringBuffer computedDistLog = new StringBuffer("\n\nCOMPUTED FROM THE NETWORK ");
		StringBuffer diffDistLog = new StringBuffer("\n\nDIFFERENCE MATRIX \n");
		
		realDistLog.append("DISTANCE MATRIX\n");
		computedDistLog.append("DISTANCE MATRIX\n");
		
		//Log.dprintln("components # of elements " + num);
		//Log.dprint("\nthis is a real (observed among sequences) distance matrix \n");
		
		realDistLog.append("pos:   ");
		computedDistLog.append("pos:   ");
		diffDistLog.append("pos:   ");
		
		// enum1 = realtaxa.elements();
		
		int realTaxaIndex = 0;
		int numRealtaxa = realtaxa.size();
		
		// tempt = (TaxaItem) enum1.nextElement();
		tempt = (TaxaItem)realtaxa.get(realTaxaIndex);
		
		tabs = tempt.name.length() / 2;
		
		for (int i = 0; i < tabs; i++) {
			realDistLog.append("\t");
			computedDistLog.append("\t");
			diffDistLog.append("\t");
		}
		
		realDistLog.append(" ");
		computedDistLog.append(" ");
		diffDistLog.append(" ");
		
		while (realTaxaIndex < numRealtaxa /*enum1.hasMoreElements()*/) {
			
			// tempt = (TaxaItem) enum1.nextElement();
			tempt = (TaxaItem)realtaxa.get(realTaxaIndex);
			realTaxaIndex++;
			
			realDistLog.append(" " + (tempt.id + 1) + " ");
			computedDistLog.append(" " + (tempt.id + 1) + " ");
			diffDistLog.append(" " + (tempt.id + 1) + " ");
		}
		
		realDistLog.append("\n");
		computedDistLog.append("\n");
		diffDistLog.append("\n");
		
		// enum1 = realtaxa.elements();
		realTaxaIndex = 0;
		// numRealtaxa = realtaxa.size();
		
		while (realTaxaIndex < numRealtaxa /*enum1.hasMoreElements()*/) {
			
			// enum2 = realtaxa.elements();
			int realTaxaIndex2 = 0;
			
			// tempt = (TaxaItem) enum1.nextElement();
			tempt = (TaxaItem)realtaxa.get(realTaxaIndex);
			realTaxaIndex++;
			
			realDistLog.append("[" + tempt.name + "]");
			computedDistLog.append("[" + tempt.name + "]");
			diffDistLog.append("[" + tempt.name + "]");
			
			//Log.dprint(Integer.toString(tempcl.id+1));
			realDistLog.append(Integer.toString(tempt.id + 1));
			computedDistLog.append(Integer.toString(tempt.id + 1));
			diffDistLog.append(Integer.toString(tempt.id + 1));
			
			if (tempt.id < 9) {
				realDistLog.append(" ");
				computedDistLog.append(" ");
				diffDistLog.append(" ");
			}
			
			realDistLog.append("\t:");
			computedDistLog.append("\t:");
			diffDistLog.append("\t:");
			
			while (realTaxaIndex2 < numRealtaxa /*enum2.hasMoreElements()*/) {
				
				// tempt2 = (TaxaItem) enum2.nextElement();
				tempt2 = (TaxaItem)realtaxa.get(realTaxaIndex2);
				realTaxaIndex2++;
				
				//if (tempc1.id != tempc2.id) {
				if (tempt.id != tempt2.id) {
					
					dist = (Distance)tempt.realdist.get(tempt2.id);
					dist2 = (Distance)tempt.compdist.get(tempt2.id);
					int diff = dist2.distance - dist.distance;
					if (dist2.distance == TCS.INFINITY) {
						// Log.dprint(" ## ");
						realDistLog.append(" ## ");
						computedDistLog.append(" ## ");
						diffDistLog.append(" ## ");
					} else {
						realDistLog.append(" " + dist.distance + " ");
						computedDistLog.append(" " + dist2.distance + " ");
						diffDistLog.append(" " + diff + " ");
						
						if (diff < 0) {
							matrixminus += diff;
						} else {
							matrixdiff += diff;
						}
						
					}
					// end else
					
				} else {
					realDistLog.append(" -- ");
					computedDistLog.append(" -- ");
					diffDistLog.append(" -- ");
				}
				}
			realDistLog.append("\n");
			computedDistLog.append("\n");
			diffDistLog.append("\n");
			}
		
		diffDistLog.append("\nThe total positive difference matrix is " + (matrixdiff / 2.0) + "\n");
		diffDistLog.append("\nThe total negative difference matrix is " + (matrixminus / 2.0) + "\n");
		
		Log.dprintln(realDistLog.toString());
		Log.dprintln(computedDistLog.toString());
		Log.dprintln(diffDistLog.toString());
		
		}
	
	
	/**
	*  Insert the method's description here.
	 *
	 * @param  tempc  Description of the Parameter
	 * @param  Log    Description of Parameter
	 * @created       (11/5/99 4:07:13 PM)
	 */
	protected static void printcluster(Component tempc, Logger Log) {
		
		TaxaItem tempt;
		Enumeration enum1;
		enum1 = tempc.taxa.elements();
		Log.dprintln("cluster # " + tempc.id);
		
		while (enum1.hasMoreElements()) {
			tempt = (TaxaItem)enum1.nextElement();
			Log.dprint("Taxa # " + tempt.id + "[" + tempt.name + "],");
		}
		
		Log.dprintln("");
	}
	
	
	/**
	*  Insert the method's description here.
	 *
	 * @param  components  Description of Parameter
	 * @param  Log         Description of Parameter
	 * @created            (11/5/99 4:07:13 PM)
	 */
	protected static void printclusters(Vector components, Logger Log) {
		
		Enumeration enum1 = components.elements();
		
		while (enum1.hasMoreElements()) {
			Component tempc1 = (Component)enum1.nextElement();
			Log.dprintln();
			Log.dprintln("cluster # " + tempc1.id + " !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			Log.dprintln("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			Enumeration enum2 = tempc1.taxa.elements();
			
			while (enum2.hasMoreElements()) {
				TaxaItem tempt1 = (TaxaItem)enum2.nextElement();
				Log.dprintln("Taxa # " + tempt1.id + "[" + tempt1.name + "] compdist");
				Enumeration enum3 = tempt1.compdist.elements();
				
				while (enum3.hasMoreElements()) {
					Distance dist = (Distance)enum3.nextElement();
					Log.dprint("([" + dist.source + "," + dist.destination + "]=" + dist.distance + ")");
				}
				
				Log.dprintln();
				Log.dprintln("Taxa # " + tempt1.id + " realdist");
				enum3 = tempt1.realdist.elements();
				
				while (enum3.hasMoreElements()) {
					Distance dist = (Distance)enum3.nextElement();
					Log.dprint("([" + dist.source + "," + dist.destination + "]=" + dist.distance + ")");
				}
				
				Log.dprintln();
				Log.dprintln("Taxa # " + tempt1.id + " metricdist");
				enum3 = tempt1.metricdist.elements();
				
				while (enum3.hasMoreElements()) {
					Distance dist = (Distance)enum3.nextElement();
					Log.dprint("([" + dist.source + "," + dist.destination + "]=" + dist.distance + ")");
				}
				
				Log.dprintln();
				Log.dprintln("Taxa # " + tempt1.id + "Neighbors");
				enum3 = tempt1.nbor.elements();
				
				while (enum3.hasMoreElements()) {
					TaxaItem tempt2 = (TaxaItem)enum3.nextElement();
					Log.dprint("(" + tempt2.id + "[" + tempt2.name + "]),");
				}
				
				Log.dprintln();
			}
			
			if (tempc1.mindist.dc != null) {
				Log.dprintln("mindist= " + tempc1.mindist.distance + " between " + tempc1.mindist.source + " in " + tempc1.id + " and " + tempc1.mindist.destination + " in " + tempc1.mindist.dc.id);
			}
		}
	}
	}
