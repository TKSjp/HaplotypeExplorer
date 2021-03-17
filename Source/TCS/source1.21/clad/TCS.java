/*
Name: TCS
Programmers:	Mark Clement	[MC]
Jacob Derington	[JD]
Steven Woolley  [SW]
David Posada	[DP]
Mark, Jake and Steve are at the Department of Computer Science at Brigham Young University, Provo, UT 84601
David is now at the Department of Biochemistry, Genetics and Imunology at the University of Vigo, 36200 Vigo, Spain
Purpose: TCS estimates a phylogenetic network from DNA sequences following the statistical parsimony algorithm
as described in
Templeton, A. R., K. A. Crandall and C. F. Sing 1992. A cladistic analysis of phenotypic
associations with haplotypes inferred from restriction endonuclease mapping and DNA
sequence data. III. Cladogram estimation. Genetics 132: 619-633.
Contact:		dposada@uvigo.es
VERSION HISTORY
--------------------------------------------------------------------------------------------------
Version 1.04 (Jul 00)		:	Bug when collapsing sequences to haplotypes fixed [DP]
Bug	when calculating outgroup weights for several networks [DP]

Version 1.05 (Jul 00)		:	Bug that violated minimum connections fixed [MC]
Cosmetic changes [DP]

Version 1.06 (Oct 00)		:	Run information and outgroup weights for each haplotype (see below)
is send to the LOGFILE instead of to a window [DP]
A progress bar is included that displays the progress of the program.
It is not linear, but will be a good indication that the program is making
its calculations [DP]
TCS will display in the window the maximum number of steps (substiutions)
to connect parsimoniously two haplotypes with a 95% confidence [DP]
Once in the graph,  if you double click on a haplotype, you will be able
to see which sequences belong to that haplotype and the ougroupt weight for
that haplotype (see Castelloe and Templeton 1994, MPE 3 (2): 102-113) [DP]

Version 1.07 (Nov 00)		:	A histogram class was added. It is there but I do not want to use it yet [DP]

Version 1.08 (Feb 01)		:	Fixed bug that was creating several unconnected haplotyped i.e. when they should be
connected for some big data sets [MC]
The progress of the calculations are showed in the GUI [DP]
Added some new classes for nesting and mutation mapping that we are not
releasing yet [JD]

Version 1.09 (Feb 22)  		:   Removed the intermediate default labeling in display [DP]
New display features [DP]

Version 1.10 (Feb 24)		:	Fixed a bug by which lowecase or uppercase nucleotides were treated as different
(i.e. A != a, when it should be A == a) [DP]
Logfile output format improved [DP]
Version information is only dependent on dna.java (before it was on VGJ.java and
																									 GraphWindow.java) [DP]
Version 1.11 (12 March 01)	:	Set a switch for all output, switch for intermediate, modified print matrix	[MC]
Evaluatemetric too small distances really bad, connectcluster added candidatedest vector
to hold new added intermediates on connections connectTaxa same as connectclusters [MC]
Bestmetric passes candidates through, connectcomponents adds intermediates to candidate vector [MC]

Version 1.12 (16 March 01)  :	Made negative paths very costly. Less care about number of intermediates [MC]

Version 1.13 (4 April 01)	:	Fixed again the unconnectedness bug [MC]
A list of haplotypes is printed to the logfile [DP]
Close file pointer after reading sequences or after errors! [DP]
Display messages in case haplotypes differ only by missing data [DP]

Version 1.14 (5 June 01)	: 	Fixed again the too close bug [JD]
Fixed the output format for .pict (works on Macs and PC) [JD]
Improved the Nesting algorithm, with two separate output formats into a Nesting file [JD]
Fixed some minor bugs through out the dna.java file [JD]
Added additional explaination through out the dna.java file [JD]

Version 1.15 (27 February 03):	Allowed nesting on re-opened graphs and modified graphs [SW]
Also allowed the user to choose the connection limit [SW]
(both by % or actual connections) [SW]
Changed many static values to non-static, so it can be instantiated
accurately multiple times [SW]
- Version 1.15 Additions (June 2003):
Added option for selecting the root (assumes that the root is a rectangular node) [SW]
Automatically selects down when new network is created using this select (changed from selectAll) [SW]
Disabled Nesting from menu [SW]
Added buttons to select whether to calculate connection limit or to input directly  [SW]
Disabled run button until a valid file is opened... Should now be able to open and
run multiple files from one window sequentially [SW]
Moved file reading and Debug output code to a new classes [SW]
Allows command line operation for multiple files: [SW]
usage:     VGJ -toPict directory
Converts any .graph file in the given Directory to Pict file (same filename + .PICT) VGJ directory
Constructs a network for each .phylip file in the given directory
Added private boolean directAndPrintEdges(TextOutputStream out, TaxaItem root)
which changes the Graph to a Directed graph [SW]
- Version 1.15 Additions (July 2003):
Add mode -saveStand which will look at all .graph files in the given directory [SW]
and save them into a standard graph format that consists of the following:
First line = total#nodes total#NodesRepresentingRealSequences
Next       = x and y coordinates for all nodes
Finally    = list of all connections by index
Note:  These indices don't corrospond directly to the taxanumber (or names obviously)
That information is in the logfile under the header "Haplotype list:"
Note:  This will only output the first graph, if there are multiple (due to unconnected parts)
Note:  This file is saved as the original filename + .net at the end, and is saved automatically
when run with just a directory as the first parameter.
Added option for inputing the connection limit [SW]
Usage VGJ directory maxconnectionlimit
- Version 1.15 additions (January 2004)
I commented out several of the debug log statements in the nested inner loops of the
main network construction algorithm, since they were using the majority of the CPU
on complex network reconstructions [SW]
I fixed a bug where the "ambiguous data" variable was not being reset between individual
sequence comparisons, resulting in saying that sequences differed only in ambiguous
or gap characters, 	when really they didn't differ at all  [SW]

Version 1.16 (16 March 04)		
[SW] Reorganized source files: divided dna.java in several source files,
	removed some BYU files (and other sent to CLAD) and eliminated several from the EDU folder.
[DP] Put together new package. 
[DP] Added expiration method (but disabled)
[DP] The program can read IUPAC symbols and will treat them as missing data (i.e, like a ?)
	IUPAC codes -> 	M: A/C		S: G/C		H: A/C/T	B: C/G/T
	R: A/G		K: G/T		V: A/C/G	X: A/C/G/T = ?
	W: A/T		Y: C/T		D: A/G/T	N: A/C/G/T = ?
	Note: we could do better than this (e.g., clearly W != S) but not sure whether it is worthy
[DP] Reads nexus file with names consisting of one character (e.g. 'A')
[DP] Added a function (ReadHaps) to work with inferred haplotype (for example from PHASE) and include frequency
	and confidence:
	ID# haplotype absfreq confidence
	1 1222111111111211111121 1100 0.987
	2 2121112111111121111111 634 0.984
	3 2121111111111211111121 536 0.983
	4 2121112112111111112121 486 0.983
	5 1222111111111211211121 404 0.985
[DP] Removed Histogram.java we are not really using this
[DP] Removed NestDialog.java, we are not ready
[DP] Enabled Spring menu [DP]
[DP] Fixed some (but not all) deprecated methods in Java 1.4.2
[DP] Added option to display the running JVM on the title
    (so added a second addButton method in Lpanel.java)
[DP] Removed XYZ axes from the graph display
[DP] Recognize current OS [DP]

Version 1.17 (13 May 04)	
[DP] Fixed PICT and PS outpout menu
[DP] Remove an extra GraphWindow.java file in the source directory
[DP] Make BrowserLauncher.java class to be part of the clad package

Version 1.18 (01 June 04)
[SW] Gapmode was not working fine
[SW] Optimized the PrintMatrix function 
[SW] Cleaned up VGJ.java file (added commandline only method)
[SW] Refactored GraphWindow.java (moving IO related methods to clad.GraphIO.java)
[SW] Renamed some methods and classes to better describe what they do, and conform to java coding standards (Capitalized classnames, lowercase method names)  Most notably, this class was changed from dna.java to TCS.java
[SW] Refactored TCS.java moving print methods into TCSIO.java
[SW] Removed redundant code, cleaned up code, moved globals to methods (where possible), added meaningful comments in TCS.java
[SW] Converted any .java files from mac hard rights to unix hard rights (allows diff's to be done between versions better)
[SW] Moved static methods from TCS.java to Utils.java
[DP] Make static URLs
[DP] Fixed a bug that prevented opening graph files

Version 1.19 (23 April 2005)
[DP] Fixed printing bug at TextOutputStream.java (this function comes from a package from BYU computer science). 
	 When precision implied a rounding of 1, it would print 0 instad. 
	 For example printf("%10.4f",0.99998) would print 0.0000

Version 1.20 (25 June 2005)
[SW] Fixed "Save Graph" to actually save the graph shown (if changes have been made)
			Fixed Display of Edge labels (no longer a big blurr on Mac's)
			Fixed Scrollbars for Graph Canvas, now scrolls when you move the sliders, without having to click the arrow, etc.
[SW] Added code to map character substitutions to the branches
	Displays ambiguous mappings with an asterisk (*) before the site number
	The exact location of a site along a branch with more than 1 edge is also ambiguous (though this is assumed and not displayed)
[SW] Added option to change node and edge label font separately
[SW] Added option to display the character mapping info on the edges
[DP] Change above option to a button. 
[DP] Enable and Disable some GUI options before and after doing graph, opening graph or doing distance data
[DP] IUPAC warning will appear just once
[DP] GUI will display just the file name, without the path

Version 1.21 (30 June 2005)
[SW]  Fixed ReadFile to actually treat ambiguity characters as missing data (?'s) for the rest of the analyses.   
	Namely, they are stored as ?'s which are later ignored for collapsing haplotypes, distance measures and mappings
	I also added a message to the warning about IUPAC ambiguity codes that states that they are treated as missing data.
[SW]  Fixed the Mapping code to deal with gaps correctly as defined in the gui:  either as 5th state or as missing (?)...

*/

package clad;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.JOptionPane;
import EDU.auburn.VGJ.graph.GMLobject;
import EDU.auburn.VGJ.gui.GraphWindow;


/**
 *  Perform the TCS algorithm on a given set of sequences or distance matrix
 *
 * @author     See above
 * @created
 */
public class TCS {

	private Vector alltaxa;
	private boolean graphExists;
	private GraphWindow frame;
	private int internalNodeNumber;
	private Vector components;
	private Vector realtaxa;
	private Logger Log;
	private FileReader fileReader;
	private String[] maxWtaxa;
	private int maxParsimonyDistance;
	private boolean commandLineMax;
	private ArrayList edges;
	private int numHapHap;
	private int numHapInt;
	
	/**  Description of the Field */
	public static String currentOS;
	/**  turns on and off the intermediate label display */
	public final static boolean printIntermediateLabels = false;
	/**  The Version of TCS */
	public final static String VERSION = "v1.21";
	/**  Used for infinity in distances */
	public final static int INFINITY = 1000000000;
	/**  prints debug to a logfile instead of stdout */
	public final static boolean log = true;
	/**
	 *  Level of debugging: (may have been commented out for speed optimizations) 1=readfile 2=evaluatemetric
	 *  4=printcluster 0x8=connectcomponents 0x10=recalcdist 0x10000=time variable 0x20=buildmatrix 0x40=PrintTaxa
	 *  0x80=print metric 0x100=recalcmindistance 0x200=connecttaxa(bestMetric) 0x400=connectclusters(evaluateMetric) 0x800
	 *  print metrics from each choice 0x1000=print cluster connections 0x2000 minimal cluster connection printing.
	 *  0x4000=write out intermediate graph files 0x8000=added connections(connenctComponents) 0x40000=cluster connection
	 *  debug 0x10000 timing debug
	 */
	public final static int debug = 0x8800;

	// just add this to the metric score nomatter how much too big it is
	private final static int TOO_BIG_SCORE = 10;

	// Add this amount for each intermediate
	private final static int NUM_INTERMEDIATE_SCORE = 1;

	// Add this if the scores match
	private final static int CORRECT_SCORE = 20;

	// urls
	public static String urlTCS = "http://darwin.uvigo.es/software/tcs.html";
	public static String docTCS = "/docs/TCS1.21.html";
	

	/**
	 *  Constructor for the TCS class
	 *
	 * @param  f   The GraphWindow associated with this TCS run
	 * @created
	 */
	public TCS(GraphWindow f) {
		frame = f;
		commandLineMax = false;
	}


	/**
	 *  Constructor for the TCS object
	 *
	 * @created
	 */
	public TCS() {
		commandLineMax = false;
	}



	/**
	 *  Allows maxParsimonyDistance to be set manually (used for commandline version)
	 *
	 * @param  distance  The value for the maxParsimonyDistance
	 * @created
	 */
	public void setMaxDistance(int distance) {
		maxParsimonyDistance = distance;
		commandLineMax = true;
	}


	/**
	 *  Returns a String listing all taxa names for the given haplotype number
	 *
	 * @param  hapNum  The haplotype number whose taxa names we want
	 * @return         A String listing all taxa names for the given haplotype number
	 * @created
	 */
	public String getHaplotypeList(int hapNum) {

		Enumeration enumDuplicateNames;

		TaxaItem taxon;
		String hapList = "";
		// Log.dprintln("\nHaplotype list:\n\n");

		taxon = getTaxaItem(new Integer(hapNum));
		enumDuplicateNames = taxon.dupnames.elements();

		hapList += (taxon.dupnames.size() + 1) + "\n" + taxon.name;

		while (enumDuplicateNames.hasMoreElements()) {
			String str = (String)enumDuplicateNames.nextElement();
			hapList += "\n" + str;
		}
		return hapList;
	}


	/**
	 *  Constructs a network using the TCS algorithm
	 *
	 * @param  infile       Description of the Parameter
	 * @param  outfile      Description of the Parameter
	 * @param  gapmode      Description of Parameter
	 * @param  distances    Description of Parameter
	 * @param  logFileName  Description of the Parameter
	 * @return              Description of the Returned Value
	 * @created
	 */
	public boolean runTCS(String infile, String outfile, boolean gapmode, boolean distances, String logFileName) {
		graphExists = false;
		long startTime = startTime = System.currentTimeMillis();
		internalNodeNumber = 0;
		numHapHap = 0;
		numHapInt = 0;
		edges = new ArrayList();

		if (log) {
			Log = new Logger();
			// logfile = new TextOutputStream("logfile.txt");
			Log.setLogFile(logFileName);
		}

		boolean useGui = false;

		if (frame != null) {
			useGui = true;
			frame.progressBar.setValue(0);
			frame.progressBar.update(frame.progressBar.getGraphics());
			frame.statustextField.setText("STATUS: Reading file and collapsing");
			frame.statustextField.setForeground(Color.red);
			frame.statustextField.update(frame.statustextField.getGraphics());
		}

		Log.dprintln("TCS " + VERSION);

		Date time = new Date();
		Log.dprintln(time.toString());
		Log.dprintln("Datafile = " + infile);

		Log.dprintln("Current OS = " + currentOS);

		// Read the input file
		fileReader = new FileReader(frame, Log);

		// if (frame != null) {
		if (useGui) {
			frame.progressBar.setValue(1);
			frame.progressBar.update(frame.progressBar.getGraphics());
		}

		if (distances) {
			fileReader.ReadDistanceFile(infile);
		} else {
			fileReader.ReadInputFile(infile, gapmode);
		}

		// if there is a gui or there wasn't a commandline maxDistance set
		if (useGui || !commandLineMax) {
			calculateConnectionLimit();
		} else {
			Log.dprintln("\nRUN SETTINGS");
			Log.dprintln("User specified maximum connection steps = " + maxParsimonyDistance);
		}

		if ((debug & 0x64) != 0) {
			TCSIO.printTaxa(components, Log);
		}
		boolean warnMissing = fileReader.warnMissing;
		// missingString = fileReader.missingString;
		int maxNameLen = fileReader.maxNameLen;
		components = fileReader.components;
		// alltaxa and realtaxa are identical vectors at this point
		alltaxa = fileReader.alltaxa;
		realtaxa = fileReader.realtaxa;

		//this will put the char seq into components/alltaxa/realtaxa
		if (gapmode) {
			Log.dprintln("Gaps treated as fifth state");
		} else {
			Log.dprintln("Gaps treated as missing data");
		}

		if (warnMissing) {
			Log.dprintln("\n\nWARNING: POSSIBLE MISSING DATA AMBIGUITIES");
			Log.dprintln(fileReader.missingString);
		}

		Log.dprintln("\n\nHAPLOTYPES");
		Log.dprintln("Number of haplotypes = " + components.size());
		
		if (components.size() == 1) {
			JOptionPane.showMessageDialog(frame, "There is only one haplotype" + "\nA graph cannot be built.", "TCS warning", JOptionPane.ERROR_MESSAGE);
			System.out.println("Only one Haplotype!");

			if (warnMissing) {
				JOptionPane.showMessageDialog(frame, "Warning : potential missing data ambiguities" + "\nwhen collapsing sequences to haplotypes" + "\nCheck the logfile.\n", "TCS warning", JOptionPane.ERROR_MESSAGE);
			}
			return false;
		}

		if (!distances) {

			// Build the distance matrix
			TCSIO.printHaplotypeList(components, Log);

			if (useGui) {
				frame.statustextField.setText("STATUS: Calculating distances");
				frame.statustextField.update(frame.statustextField.getGraphics());
			}
			// initialize the distances
			buildMatrix(gapmode);
		}

		// TCSIO.printMatrix(realtaxa, Log);
		// TCSIO.printMatrix(alltaxa, Log);

		Log.dprintln("\n\nCONNECTIONS");

		float quantum = 0;

		int graphNumber = 0;
		int val = 0;

		if (useGui) {
			quantum = (float)50 / (float)realtaxa.size();
			frame.statustextField.setText("STATUS: Estimating subnetworks");
			frame.statustextField.update(frame.statustextField.getGraphics());
			val = frame.progressBar.getValue();
		}

		// Now connect each haplotype to any other haplotypes within the minimum distance
		Enumeration enumRealTaxa = realtaxa.elements();
		int currentIteration = 0;
		int progress = 0;

		// for each taxaitem in realTaxa
		while (enumRealTaxa.hasMoreElements()) {

			if (useGui) {
				currentIteration++;
				progress = val + (int)(currentIteration * quantum);
				frame.progressBar.setValue(progress);
				frame.progressBar.update(frame.progressBar.getGraphics());
			}

			TaxaItem sourceTaxaItem = (TaxaItem)enumRealTaxa.nextElement();
			if ((debug & 0x4) != 0) {

				TCSIO.printclusters(components, Log);
			}
			boolean addedConnection = connectTaxa(sourceTaxaItem);

			if ((debug & 0x4000) != 0) {
				Log.dprintln("writing graph" + graphNumber + ".out");
				Mapper.map(edges, maxParsimonyDistance, gapmode);
				TCSIO.printGraph("graph" + graphNumber + ".out", components, distances, Log, maxWtaxa, maxNameLen,edges);
			}

			graphNumber++;
		}

		if (useGui) {
			quantum = (float)50 / (float)realtaxa.size();
			currentIteration = 0;
			frame.statustextField.setText("STATUS: Connecting subnetworks");
			frame.statustextField.update(frame.statustextField.getGraphics());
		}

		// Now connect all remaining clusters
		while (components.size() > 1) {

			if (useGui) {
				currentIteration++;
				progress = 50 + (int)(currentIteration * quantum);
				frame.progressBar.setValue(progress);
				frame.progressBar.update(frame.progressBar.getGraphics());
			}
			if ((debug & 0x4) != 0) {
				TCSIO.printclusters(components, Log);
			}

			if ((debug & 0x4000) != 0) {
				Log.dprintln("writing graph" + graphNumber + ".out");
				Mapper.map(edges, maxParsimonyDistance, gapmode);
				TCSIO.printGraph("graph" + graphNumber + ".out", components, distances, Log, maxWtaxa, maxNameLen,edges);
			}

			graphNumber++;

			if (!connectComponents()) {
				break;
			}
		}

		TCSIO.printMatrix(realtaxa, Log);

		// Could we make at least 1 connection ? (i.e., is there a graph?). If not, do not continue
		// lets try if none of the realdistances are < maxParsimonyDistance then cannot create a graph?  perhaps?


		if (graphExists) {

			// Now print out the graph
			calculateOutgroupWeights();
			//Log.dprintln("\n\nGRAPH");
			//Log.dprintln("Here is the graph");
			Mapper.map(edges, maxParsimonyDistance,gapmode);
			
			TCSIO.printGraph(outfile, components, distances, Log, maxWtaxa, maxNameLen,edges);
			Log.dprintln("\n\n\nCalculations are finished.\n");
		} else {
			JOptionPane.showMessageDialog(frame, "No connection below limit."
					+ "\nA graph cannot be built.", "TCS warning", JOptionPane.ERROR_MESSAGE);
			return false;
		}

		Log.dprintln("\n\n_________________________________________________________");
		Log.dprintln("TCS " + VERSION);

		Date date = new Date();
		Log.dprintln(date.toString());
		Log.dprintln("Datafile = " + infile);
		long endTime = System.currentTimeMillis();
		Log.dprintln("\nIt took " + (endTime - startTime) / 60000.0 + " minutes.");

		if (useGui) {
			frame.progressBar.setValue(100);
			frame.progressBar.update(frame.progressBar.getGraphics());
			frame.statustextField.setText("STATUS: program finished");
			frame.statustextField.update(frame.statustextField.getGraphics());
		}

		Log.close();

		if (warnMissing) {
			JOptionPane.showMessageDialog(frame, "Warning : potential missing data ambiguities"
					+ "\nwhen collapsing sequences to haplotypes"
					+ "\nCheck the logfile.\n", "TCS warning", JOptionPane.ERROR_MESSAGE);
		}
		
//		CharacterMapper.fTCS(null, realtaxa, maxParsimonyDistance,  edges, numHapHap,numHapInt);

		
		
		return graphExists;
	}



	/**
	 *  gets haplotype and duplicate names of the data used to create this graph.
	 *
	 * @param  GMLo  Description of the Parameter
	 * @created
	 */
	public void fillTaxaInfo(GMLobject GMLo) {
		alltaxa = new Vector();
		GMLobject nodegml;
		GMLobject gml;
		GMLobject GMLtmp;
		gml = GMLo;

		// If the GML doesn't contain a graph, assume it is a graph.
		GMLtmp = gml.getGMLSubObject("graph", GMLobject.GMLlist, false);

		if (GMLtmp != null) {
			gml = GMLtmp;
		}
		// go through and get all nodes...

		for (nodegml = gml.getGMLSubObject("node", GMLobject.GMLlist, false); nodegml != null; nodegml = gml.getNextGMLSubObject()) {

			boolean intermediate = false;
			String name = null;
			int length = 0;
			int id = -1;
			Integer tmpint = null;
			String tmp = null;

			if ((tmp = (String)nodegml.getValue("label", GMLobject.GMLstring)) != null) {
				name = tmp;
			}

			if ((tmpint = (Integer)nodegml.getValue("id", GMLobject.GMLinteger)) != null) {
				id = tmpint.intValue();
			}

			GMLobject data;
			int freq = 1;

			if ((data = nodegml.getGMLSubObject("data", GMLobject.GMLlist, false)) != null) {

				if ((tmp = (String)data.getValue("Frequency", GMLobject.GMLstring)) != null) {

					int first = tmp.indexOf("=");
					int second = tmp.indexOf("\n");
					if (second < 0) {
					    second = tmp.indexOf("\r");
					}
					//FIXME:: we get a stringIndexoutofbounds exception here opening older tcs files...
//					if (first < 0 || second < 0) {
//					    System.err.println("This graph does not have proper frequency information!");
//					} else {
					    String number = tmp.substring(first + 1, second).trim();
					    freq = Integer.parseInt(number);
//					}
					
					
				}
			} else {
				intermediate = true;
				freq = 1;
			}

			if (name.length() != 0) {

				TaxaItem taxa = new TaxaItem(name, length, id);
				taxa.numduplicates = freq - 1;
				taxa.isIntermediate = intermediate;
				//FIXME we are going to assume that any sequences read in from the graph are resolved...
				taxa.resolved = true;

				alltaxa.add(taxa);
			}
		}

		GMLobject edgegml;

		// now get all edges
		for (edgegml = gml.getGMLSubObject("edge", GMLobject.GMLlist, false); edgegml != null; edgegml = gml.getNextGMLSubObject()) {

			Integer source;
			Integer target;
			source = (Integer)edgegml.getValue("source", GMLobject.GMLinteger);
			target = (Integer)edgegml.getValue("target", GMLobject.GMLinteger);

			if (source != null && target != null) {

				TaxaItem tSource = getTaxaItem(source);
				TaxaItem tTarget = getTaxaItem(target);
				tSource.nbor.add(tTarget);
				tTarget.nbor.add(tSource);
			}
		}
	}



	/**
	 *  Gets the taxaItem with the given Integer id
	 *
	 * @param  id  The Integer ID of the desired TaxaItem
	 * @return     The corrosponding TaxaItem or null if it is not found
	 * @created
	 */
	private TaxaItem getTaxaItem(Integer id) {

		int index = id.intValue();
		int size = alltaxa.size();

		for (int x = 0; x < size; x++) {

			TaxaItem taxon = (TaxaItem)alltaxa.get(x);

			if (taxon.id == index) {

				return taxon;
			}
		}

		return null;
	}



	/**
	 *  add the new intermediate to all of the distance vectors We only have non-infinite distances if the taxa used to
	 *  have a connection to before or after, if connected to before it is the distance to before+1 if connected to after
	 *  it is the distance to after + distance to the other end Otherwise, it is infinite
	 *
	 * @param  newtaxa         the new intermediate
	 * @param  before          the taxa before the new intermediate in the chain
	 * @param  after           the taxa on the other end of this connection
	 * @param  after_distance  the distance from the new intermediate to the taxa on the other end of the chain.
	 * @created                (11/4/99 11:42:13AM)
	 */
	private void addIntermediate(TaxaItem newtaxa, TaxaItem before, TaxaItem after, int after_distance) {

		// As we add new taxa, we need to create their distance vectors
		// We also update everyone elses distance vectors to include this node.
		Enumeration enumAllTaxa = alltaxa.elements();

		while (enumAllTaxa.hasMoreElements()) {

			Distance newdistance;
			Distance fromnewdistance;
			Distance dummy;
			TaxaItem curtaxa = (TaxaItem)enumAllTaxa.nextElement();

			if (curtaxa == newtaxa) {
				// If this is the new intermediate, just add the distance object and leave
				fromnewdistance = new Distance(newtaxa.id, curtaxa.id, 0);
				newtaxa.compdist.add(fromnewdistance);
				dummy = new Distance(newtaxa.id, curtaxa.id, 0);
				newtaxa.metricdist.add(dummy);

				continue;
			}

			// Get the distance between us and the taxa before and the distance between us and the taxa at the other end
			Distance beforetaxa = (Distance)curtaxa.compdist.get(before.id);
			Distance aftertaxa = (Distance)curtaxa.compdist.get(after.id);

			// Add a new Distance to all of the distance vectors
			// If there is a current connection to the before taxa that is less than the connection to the after
			if ((beforetaxa.distance != INFINITY) && (beforetaxa.distance < aftertaxa.distance)) {
				newdistance = new Distance(curtaxa.id, newtaxa.id, beforetaxa.distance + 1);
				fromnewdistance = new Distance(newtaxa.id, curtaxa.id, beforetaxa.distance + 1);
			} else if (aftertaxa.distance != INFINITY) {
				newdistance = new Distance(curtaxa.id, newtaxa.id, aftertaxa.distance + after_distance);
				fromnewdistance = new Distance(newtaxa.id, curtaxa.id, aftertaxa.distance + after_distance);
			} else {
				newdistance = new Distance(curtaxa.id, newtaxa.id, INFINITY);
				fromnewdistance = new Distance(newtaxa.id, curtaxa.id, INFINITY);
			}

			curtaxa.compdist.add(newdistance);
			newtaxa.compdist.add(fromnewdistance);
			dummy = new Distance(curtaxa.id, newtaxa.id, INFINITY);
			curtaxa.metricdist.add(dummy);
			dummy = new Distance(newtaxa.id, curtaxa.id, INFINITY);
			newtaxa.metricdist.add(dummy);

			// We shouldnt need to add one to realdist because it only
			//  deals with real taxa
		}
		if ((debug & 0x4) != 0) {
			TCSIO.printclusters(components, Log);
		}
	}



	/**
	 *  Sets all the components distances from each other and keeps track of the minimum distance from each taxon to all
	 *  others. basically, just initializes a distance matrix NOTE: it is redunant, in that the distances are symmetric,
	 *  but it calculates them twice in both directions...
	 *
	 * @param  gapmode  if true, treat gaps as a 5th state
	 * @created
	 */
	private void buildMatrix(boolean gapmode) {

		Component componentOne;
		Component componentTwo;
		// The two components we are going to compare

		Enumeration enumComponentsOne = components.elements();
		TaxaItem taxaItemOne;
		TaxaItem taxaItemTwo;
		// The two taxa we are going to compare

		Distance minDist = new Distance();

		//this is important for .... entries
		while (enumComponentsOne.hasMoreElements()) {
			//first while
			componentOne = (Component)enumComponentsOne.nextElement();
			taxaItemOne = (TaxaItem)componentOne.taxa.get(0);
			componentOne.mindist.distance = INFINITY;
			// set this component's mindistance to a large number

			Enumeration enumComponentsTwo = components.elements();
			minDist.distance = INFINITY;
			// minDist will hold the minimum distance from this componentOne to any other component

			while (enumComponentsTwo.hasMoreElements()) {
				//second while
				componentTwo = (Component)enumComponentsTwo.nextElement();
				taxaItemTwo = (TaxaItem)componentTwo.taxa.get(0);

				Distance realdist = new Distance(componentOne.id, componentTwo.id, 0, componentOne, componentTwo);
				Distance tmpcdist = new Distance(componentOne.id, componentTwo.id, 0, componentOne, componentOne);
				Distance tmptdist = new Distance(taxaItemOne.id, taxaItemTwo.id, 0, componentOne, componentOne);
				Distance metricdist = new Distance(taxaItemOne.id, taxaItemTwo.id, 0, componentOne, componentOne);
				// initialize the realdistance between compOne and compTwo to 0

				// If it is myself, leave distances as zero, otherwise, calculate the realdist
				if (componentOne.id != componentTwo.id) {
					// increment realdist.distance 1 for each sequence difference.
					for (int k = 0; k < taxaItemOne.characters.length; k++) {

						char char1 = taxaItemOne.characters[k];
						char char2 = taxaItemTwo.characters[k];

						if (!gapmode && (char1 == '-' || char2 == '-')) {
							continue;
						}

						if (char1 == '?' || char2 == '?') {
							continue;
						}
						if (char1 != char2) {
							realdist.distance++;

							//MAPPING ADDITION NEEDED HERE
							//TABLE OF POSITIONS & CHARS
						}
					}

					// put in the real distance as the Component dist, and INFINITY for the distance between the taxaItems
					tmpcdist.distance = realdist.distance;
					tmptdist.distance = INFINITY;
					metricdist.distance = INFINITY;

					// minDist should always be set to the shortest distance between two taxa
					if (realdist.distance < minDist.distance) {
						minDist.clone(realdist);
					}
				}
				componentOne.compdist.add(tmpcdist);
				taxaItemOne.compdist.add(tmptdist);
				taxaItemOne.realdist.add(realdist);
				taxaItemOne.metricdist.add(metricdist);
			}

			componentOne.mindist.clone(minDist);
			taxaItemOne.minRealDist = minDist.distance;
		}
	}



	/**
	 *  Connect the clusters to each other 3) Combine the two clusters and recalculate the minimum distances to other
	 *  clusters
	 *
	 * @param  componentOne  The first component
	 * @param  componentTwo  The second component
	 * @return               Description of the Returned Value
	 * @created              (10/26/99 2:50:04 PM)
	 */
	private boolean combineComponents(Component componentOne, Component componentTwo) {

		// Now join the two components into one.
		if ((debug & 0x4) != 0) {

			TCSIO.printcluster(componentOne, Log);
			TCSIO.printcluster(componentTwo, Log);
		}

		if (componentOne == componentTwo) {

			return false;
		}

		Enumeration enum1 = componentTwo.taxa.elements();

		while (enum1.hasMoreElements()) {

			// take the taxa out of the old cluster and put it in the new one
			TaxaItem tempt1 = (TaxaItem)enum1.nextElement();
			componentOne.taxa.add(tempt1);
			tempt1.parentComponent = componentOne;
		}

		// remove the second component
		components.remove(componentTwo);

		// Recalculate the min connections
		// For clusters not involved in this collapse,
		// they need only point to the remaining cluster if the one
		// they were pointing to was removed.
		// We need to go through all of the taxa in the collapsed cluster to find out what the min connection is
		recalcMinDistance(componentOne, componentTwo.id);
		return true;
	}


	/**
	 *  Connect the components to each other:
	 *  1) find the two clusters that are closest
	 *  2) insert intermediate nodes in such a way as to preserve the maximum number of distances between taxa
	 *  3) Combine the two clusters and recalculate the
	 *  minimum distances to other clusters
	 *
	 * @return     true if a connection was made, false otherwise
	 * @created    (10/26/99 2:50:04 PM)
	 */
	private boolean connectComponents() {

		Distance mindist = new Distance();
		// This vector will hold all of the destination taxa and the intermediates that have allready been added

		// Find the two components that have the minimum distance
		// Look for minimum distance between clusters
		mindist.distance = INFINITY;
		Enumeration enum1 = components.elements();

		while (enum1.hasMoreElements()) {
			Component tempc1 = (Component)enum1.nextElement();

			if (tempc1.mindist.distance < mindist.distance) {
				mindist.clone(tempc1.mindist);
			}
		}

		if (mindist.distance > maxParsimonyDistance) {
			return false;
		}

		// Connect every pair of taxa that are at this min distance.
		// Now connect the two real taxa from these two components that have the min distance
		// Delete the second component and copy the other taxa into the first component

		Component tempc1 = mindist.sc;
		Component tempc2 = mindist.dc;

		if ((debug & 0x4) != 0) {
			TCSIO.printcluster(tempc1, Log);
			TCSIO.printcluster(tempc2, Log);
		}
		// First initialize the candidate vector with all of the valid destination taxa
		// Additional intermediates will be added to this vector each time we call
		//  bestMetric so we need a place to store them between invocations



		Vector destCandidates = new Vector(mindist.dc.taxa);
		enum1 = tempc1.taxa.elements();

		while (enum1.hasMoreElements()) {
			TaxaItem tempt1 = (TaxaItem)enum1.nextElement();

			if (!tempt1.isIntermediate) {
				Enumeration enum2 = tempc2.taxa.elements();

				while (enum2.hasMoreElements()) {
					TaxaItem tempt2 = (TaxaItem)enum2.nextElement();

					if (!tempt2.isIntermediate) {

						// If this distance is the min distance, add the connection to the list
						Distance thisdist = (Distance)tempt1.realdist.get(tempt2.id);

						if (thisdist.distance == mindist.distance) {
							Log.dprintln("adding two min taxa " + thisdist.source + "[" + tempt1.name + "] , " + thisdist.destination + "[" + tempt2.name + "]", 0x8000);
							Log.dprintln("In clusters " + tempc1.id + " , " + tempc2.id + " distance " + mindist.distance, 0x8000);

							Distance curdist = (Distance)tempt1.compdist.get(tempt2.id);

							// if connection already exists
							if (curdist.distance <= mindist.distance) {

								continue;
							}

							bestMetric(tempc1, tempc2, tempt1, tempt2, mindist.distance, destCandidates);
						}
					}
				}

			}
		}

		// Now join the two components into one.
		combineComponents(tempc1, tempc2);

		return true;
	}


	/**
	 *  adds intermediates (if necessary) and adds the sourceTaxa to the destTaxa's neighbor list and vice versa.
	 *
	 * @param  sourcec     The source taxons component
	 * @param  destc       The destination taxons component
	 * @param  newdist     The new distance between the source and dest taxa
	 * @param  candidates  The current vector of candidate taxa
	 * @created
	 */
	private void connectComponents(Component sourcec, Component destc, Distance newdist, Vector candidates) {

		if ((debug & 0x4) != 0) {

			TCSIO.printcluster(sourcec, Log);
			TCSIO.printcluster(destc, Log);
		}

		TaxaItem sourcet = (TaxaItem)alltaxa.get(newdist.source);
		TaxaItem destt = (TaxaItem)alltaxa.get(newdist.destination);

		/*if (sourcet.isIntermediate && destt.isIntermediate) {
		    System.out.println("two intermediates being connected...");
		}*/
		/*
		// NOTE:
		// 	If one of the two taxa to connect is unresolved, resolve it, then continue
		if (sourcet.resolved && destt.resolved) {
		    // FIXME problem!  what if I mark it as resolved, but with this new connection, it's resolution is unsure?
		    // if earlier it was A|C at a site, then resolved with another C connecting to that node, 
		    // 	 	so, what if we now see another A connecting to the same node?  (only a problem with 4 or more branches going to a node!)
		    // just in case maybe we should store a history type thing?
		    // really, what it means is we can't mark something resolved (except known taxa, or single step stuff)...
		    // which means the trick will be resolving when we connect two unresolved branches, if that actually happens? (connecting two internal nodes)
		    
		    // maybe, since we build it up from shortest branches, it is more reliable this way?
		    
		    if ( sourcet.isIntermediate && destt.isIntermediate) {
		        // look at everything again, to see if this new connection changes things...
		        System.out.println("both are resolved and both are internal: we should probably revise to see if we change...");
		        // make a copy of the chars for the one we'll change, try it both ways, if no difference, great, otherwise???
		    } else 
		    if (sourcet.isIntermediate) {
		        resolveSeq(sourcet,destt);
		    } else 
		    if (destt.isIntermediate) {
		        resolveSeq(destt,sourcet);
		    } else {
		        // leave them be, both nodes are real... and totally defined
		    }
		} else {
		    // resolve the unresolved connections (if it is possible that both are unresolved, then the order for resolving them may matter!)
		    if (!(destt.resolved || sourcet.resolved)) { // this should only happen if we don't mark them resolved...maybe?
		        System.out.println("neither end is resolved, order may matter here, but I just did source first, then end!");
		        // we could try it both ways and see if there is a difference?
		        // or, we could assume that the most confident were done first, and just go from there?
		        
		    } else
		    if (!sourcet.resolved) {
		        // resolve the seq. of sourcet
		        resolveSeq(sourcet,destt);
		    } else
		    if (!destt.resolved) {
		        resolveSeq(destt,sourcet);
		    }
		    
		}
		*/
		
		// if we reach here, I think we are positive we made a connection
		graphExists = true;
		
		
//		System.out.println("connecting " + sourcet.name + " with " + destt.name);
		

		// Now insert intermediate nodes and make the connections
		if (newdist.distance == 1) {
			// No intermediates needed
			sourcet.nbor.add(destt);
			destt.nbor.add(sourcet);
			addLWEdge(sourcet, destt);
			
		} else {
			TaxaItem nextt = sourcet;
			int intermediates = newdist.distance;
			// compute the iupac consensus of the start and finish nodes, and insert put it into each internal node added

//			char[] consensus = Utils.getConsensus(sourcet, destt);
			char [] consensus = null;

			while (intermediates > 1) {

				// Insert intermediates
				intermediates--;

				// Add intermediate
				// [SW] FIXME  this may be a good place to keep track of mapping characters


				TaxaItem newt = new TaxaItem("In" + internalNodeNumber, 0, consensus);
				// TaxaItem newt = new TaxaItem("In" + internalNodeNumber, 0, 0);
				newt.isIntermediate = true;

				internalNodeNumber++;

				//////////////jake add 02/07/01
				alltaxa.add(newt);
				candidates.add(newt);
				newt.id = alltaxa.indexOf(newt);

				// Insert him into the source component
				sourcec.taxa.add(newt);

				// Make him my neighbor
				nextt.nbor.add(newt);
				newt.nbor.add(nextt);
				
				addLWEdge(nextt,newt);

				addIntermediate(newt, nextt, destt, intermediates);

				// Move on to the next intermediate
				nextt = newt;
				
				
				
				 

				if (intermediates == 1) {

					// This is the last intermediate in the chain
					// Now connect the last intermediate in the chain to the destination
					destt.nbor.add(newt);
					newt.nbor.add(destt);
					addLWEdge(newt, destt);
					
					break;
				}
			}
		}
	}


	
	/**
     * @param myNewt
     * @param myDestt
     */
    private void addLWEdge(TaxaItem sourcet, TaxaItem destt) {
        if (destt.isIntermediate ^ sourcet.isIntermediate) {
  			    edges.add(new LWEdge(destt,sourcet, LWEdge.HAPINT,edges.size()));
  			    numHapInt++;
  			} else 
  			    if (!destt.isIntermediate && !sourcet.isIntermediate) {
  			        edges.add(new LWEdge(destt,sourcet, LWEdge.HAPHAP,edges.size()));
  			        numHapHap++;
  			    } else
  			        {
  					        edges.add(new LWEdge(destt,sourcet, LWEdge.INTINT,edges.size()));
  					    }
    }


    /**
     * @param mySourcet source taxa, to be resolved...
     * @param myDestt what is being connected (assumed to be resolved...)
     * 
     */
    private void resolveSeq(TaxaItem mySourcet, TaxaItem myDestt) {
        
        TaxaItem resolvedNbors [] = Utils.getAllResolved(mySourcet, myDestt);
        //      at the end (after all resolved nodes are compared to source)
        // any that are singletons, get put on that branch.. (that is source is assigned to the majority char)
        // any that are non-singletons are set to the consensus
        for (int charNum = 0;  charNum < resolvedNbors[0].characters.length; charNum++) {
            
            char consChar = myDestt.characters[charNum];
                /*if (consensus[x] == null) {
                    // any that have no change, are set to the consensus
                    mySourcet.characters[x] = consChar;
                    continue;
                }*/
                boolean moreThan2 = false; // keep track of how many were different
                int whichBranch = 0; // which branch has a change...(used if it is a singleton)
                char type1;
                char type2 = type1 = resolvedNbors[0].characters[charNum];
                
                int numt1 = 1;
                int numt2 = 0;
                consChar = type1;
                /*if (consChar == 0) {
                    consChar = mySourcet.characters[x];
                }*/
                for (int nborNum = 1; nborNum < resolvedNbors.length; nborNum ++) {
                    if (resolvedNbors[nborNum].characters[charNum] != type1) {
                        if ((type1 != type2) && (resolvedNbors[nborNum].characters[charNum] != type2)) {
                            moreThan2 = true; // more than 2 types, so use the consensus
                        } else {
                            type2 = resolvedNbors[nborNum].characters[charNum]; // just found the 2nd type
                            numt2++;
                            whichBranch = nborNum;
                        }
                        
                    } else {
                        numt1 ++;
                    }
//                    if (resolvedNbors[nborNum].characters[charNum] != 0) { // check for 0's...
                        // do the consensus : see the getConsensus method...
                        if (resolvedNbors[nborNum].characters[charNum] < 65 || resolvedNbors[nborNum].characters[charNum] > 90 || consChar < 65 || consChar> 90) {
                    				consChar = Utils.Z;
                    			} else {
                    				consChar = Utils.matrix[(resolvedNbors[nborNum].characters[charNum] - 65) * 25 + (consChar - 65)];
                    			}
//                        consChar = consensus(consChar,consensus[y][x]);
//                    }

                }
                if (moreThan2) {
                    mySourcet.characters[charNum] = consChar;
                } else {
                    // if  all were type1, sort of impossible (this one node mutated to all others...), but maybe from bad previous try?
                    // if numt1 == 1, then assign 2nd
                    if (numt1 == 1) {
                        /*if (type2 != 0) {
                            mySourcet.characters[charNum] = consensus[x][1];
                        } else {*/
                            mySourcet.characters[charNum] = resolvedNbors[1].characters[charNum];
//                        }
                    } else
                    // if numt2 == 1, then assign 1st
                    if (numt2 == 1) {
                        /*if (type1 != 0) {
                            mySourcet.characters[x] = type1;
                        } else {*/
                            mySourcet.characters[charNum] = resolvedNbors[whichBranch].characters[charNum];
//                        }
                    } else {
                     
                    // otherwise, assign consChar
                        mySourcet.characters[charNum] = consChar;
                    }
                }
//                    consensus[x][y] = resolvedNbors[x].characters[y];
            }
        
        
        // should now have an array with numNbors+1 resolved taxa
        // Q??  would it be better to store a list of all unresolved sites for this taxon, rather than the consensus?
        
        // keep track of the differences from this set... namely, for each position, what differences there are, 
        // and who has which...


        
        // if one of them is diff from all the others at a site,
        //        assign that mutation specifically to that branch (by making mySourcet at that site == the others...)
        //
        
        // when we're done, mark mySourcet as resolved
        mySourcet.resolved = true; // commented out for testing...
        
    }


    /**
	 *  Connect the source taxon to all other taxa with the minimum distance 1) find the other taxa that are at a minimum
	 *  distance 2) If a connection doesnt already exist at the min distance, make it Creation date: (10/26/99 2:50:04 PM)
	 *  Changed 2/16/01
	 *
	 * @param  source  The source taxon to be connected to it's nearest neighbors.
	 * @return         True if source was connected to anything else, false otherwise (if all taxa were greater than
	 *      maxParsimonyDistance away from source)
	 * @created
	 */
	private boolean connectTaxa(TaxaItem source) {

		boolean madeNewConnection = false;

		Distance minDist = new Distance();

		minDist.distance = INFINITY;
		Enumeration enumRealDistFromSourceToOthers = source.realdist.elements();

		// Look for minimum distance between this taxa and all others, and set it into minDist
		//FIXME: keep a record of all min taxa here?  to avoid reiterating next loop?
		while (enumRealDistFromSourceToOthers.hasMoreElements()) {
			Distance dist = (Distance)enumRealDistFromSourceToOthers.nextElement();

			if ((dist.distance < minDist.distance) && (dist.destination != source.id)) {
				minDist.clone(dist);
			}
		}

		//if the minDist.distance is > maxParsimonyDistance than it is not connected to any node
		if (minDist.distance > maxParsimonyDistance) {

			return madeNewConnection;
		}

		// Now add connections to all of the taxa at this min distance
		// Add them one cluster at a time, combining the clusters when you have finished with all the taxa connections within
		// that cluster
		// You only have to connect the source taxa to the other taxa, you dont have to make all possible min distance
		//   connections between clusters

		Enumeration enumComponents = components.elements();
		boolean combineComponents = false;
		while (enumComponents.hasMoreElements()) {
			Component curComponent = (Component)enumComponents.nextElement();

			Enumeration enumCurComponentsTaxa = curComponent.taxa.elements();

			while (enumCurComponentsTaxa.hasMoreElements()) {
				TaxaItem curTaxaItem = (TaxaItem)enumCurComponentsTaxa.nextElement();

				if (curTaxaItem.isIntermediate) {
					continue;
				}

				Distance dist = (Distance)source.realdist.get(curTaxaItem.id);
				if (minDist.distance == dist.distance) {

					combineComponents = true;

					Distance already = (Distance)source.compdist.get(dist.destination);
					// If there is already a connection, do nothing
					if (already.distance <= minDist.distance) {
						if ((debug & 0x4) != 0) {
							TCSIO.printclusters(components, Log);
						}
						continue;
					}

					int limit = source.minRealDist;

					// set limit to the minimum distance from source to any other distance OR the minimum distance from curTaxaItem to any other haplotype, whichever is GREATER
					if (limit < curTaxaItem.minRealDist) {
						limit = curTaxaItem.minRealDist;
					}

					// Vector destCandidates = new Vector();
					// TaxaItem candidateTaxa;

					// Initialize the candidate vector with all of the valid destination taxa
					Component sourceParentComp = (Component)source.parentComponent;
					Component curTaxaItemParentComp = (Component)curTaxaItem.parentComponent;

					Vector destCandidates = new Vector(curTaxaItemParentComp.taxa);

					madeNewConnection |= bestMetric(sourceParentComp, curTaxaItemParentComp, source, curTaxaItem, limit, destCandidates);
				}
			}

			// If we found at least one taxa at the minimum distance, combine them
			if (combineComponents) {
				if ((debug & 0x4) != 0) {

					TCSIO.printcluster(source.parentComponent, Log);
					TCSIO.printcluster(curComponent, Log);
				}
				combineComponents(source.parentComponent, curComponent);
				combineComponents = false;
			}
		}

		return madeNewConnection;
	}


	/**
	 *  Evaluate the metric based on the number of correct distances that can be preserved. We start with the shortest
	 *  connection between the two clusters and move on up, incrementing the metric for every one that works. Creation
	 *  date: (11/4/99 12:11:04 AM)
	 *
	 * @param  allvect           Vector containing all taxa from the src and dst Component
	 * @param  TOO_SMALL_SCORE   Description of Parameter
	 * @param  numIntermediates  Description of the Parameter
	 * @param  limit             Description of the Parameter
	 * @return                   Description of the Return Value
	 * @created
	 */
	private int evaluateMetric(Vector allvect, int TOO_SMALL_SCORE, int numIntermediates, int limit) {

		// This needs to be optimized, for now we will just search for the minimal
		//  real distance and increment a score each time one works
		int score = 0;

		// The reasoning here is that you really only care that the closest
		// taxa is correct.  We weight the others on a sliding basis.

		// We want this small enough that it will only differentiate between insertions
		//  that are the same in other ways, but one has more intermediates
		// We have to look at all of the connections within clusters as well as between


		// Make sure than none of the connections between clusters are
		// less than the limit, also make sure that the connection doesn't
		// make any taxa closer than their minimum


		// multiply this by the difference
		// Go through all of the taxa in the source and dest components
		// Look at all connections that have been made to look for violations
		//, comparing the metricdist with the real dist and count up the number
		// of connections that worked

		score = -(numIntermediates - 1) * NUM_INTERMEDIATE_SCORE;

		Enumeration enumsourcet = allvect.elements();

		while (enumsourcet.hasMoreElements()) {
			TaxaItem sourceitem = (TaxaItem)enumsourcet.nextElement();

			if (sourceitem.isIntermediate) {

				continue;
			}

			Enumeration enumdestt = allvect.elements();

			while (enumdestt.hasMoreElements()) {
				TaxaItem destitem = (TaxaItem)enumdestt.nextElement();

				if (destitem.isIntermediate) {

					continue;
				}

				if (sourceitem.id == destitem.id) {

					continue;
				}

				Distance realdist = (Distance)sourceitem.realdist.get(destitem.id);
				Distance metricdist = (Distance)sourceitem.metricdist.get(destitem.id);

				// Dont bother with connections that are too far away
				if ((realdist.distance > (2 * maxParsimonyDistance)) && (metricdist.distance > (2 * maxParsimonyDistance))) {

					continue;
				}

				//here is where we are having problems,  we are not getting non negative distances for all connections between clusters -jake 05/10/01

				if (realdist.distance == metricdist.distance) {
					score += CORRECT_SCORE;
				} else {

					// Dont allow errors that are closer than our mindist

					if (realdist.distance > metricdist.distance) {

						if ((sourceitem.parentComponent != destitem.parentComponent) && (metricdist.distance <= limit)) {
							score = -INFINITY;

							return score;
						} else {

							double exp_factor;

							// We want the penalty to be really large for short connections and fairly small for big connections
							// The problem is that we still want a connection without "too small" connections to be better, even
							//  if it has a lot fewer correct connections.  For now just treat all errors the same.
							exp_factor = (double)(realdist.distance - metricdist.distance);
							score -= TOO_SMALL_SCORE * exp_factor;
						}

						// Make sure that connections within one of the clusters didnt get worse
						// I think that this should only be a penalty if it gets smaller than the minimum distance.  We wont check it here
						//                    if((compdist.distance < INFINITY) &&
						//                          (compdist.distance > metricdist.distance)) {
						// ALREADY COMMMENTED OUT!
						//                        score -= BAD_GOT_SMALLER;
						//                       }
					} else {

						// Too big
						score -= TOO_BIG_SCORE;

						//metricdist.distance - realdist.distance;
					}
				}

				// Now check to make sure that no minimum distances have been violated
				if (metricdist.distance < sourceitem.minRealDist) {
					score = -INFINITY;

					return score;
					// Dont allow errors that are closer than our mindist
				}
			}

		}

		return score;
	}


	/**
	 *  [DP] Calculates outgroup weights for each haplotype per each network
	 *
	 * @created
	 */

	private void calculateOutgroupWeights() {

		int numNetworks = components.size() + 1;
		double[] total_weight = new double[numNetworks];
		int networkNum = 0;
		int ntaxa = 0;
		Log.dprint("\n\nOUTGROUP WEIGHTS");

		Enumeration enumc = components.elements();
		while (enumc.hasMoreElements()) {
			Component tempc = (Component)enumc.nextElement();
			Enumeration enumt = tempc.taxa.elements();
			networkNum++;

			while (enumt.hasMoreElements()) {
				TaxaItem tempt = (TaxaItem)enumt.nextElement();

				// Check out this node's neighbors
				if (!tempt.isIntermediate) {
					ntaxa++;
					Enumeration enumn = tempt.nbor.elements();

					int sum_nbor_dup = 0;
					int anynbor = 0;

					while (enumn.hasMoreElements()) {
						TaxaItem tempn = (TaxaItem)enumn.nextElement();

						if (!tempn.isIntermediate) {
							sum_nbor_dup += (tempn.numduplicates + 1);

						}

						anynbor++;
					}

					if (anynbor == 1) {
						tempt.isTip = true;
					} else {
						tempt.isTip = false;
					}

					if (tempt.isTip) {
						tempt.weight = (tempt.numduplicates + 1) / 2.0;
					} else {
						tempt.weight = tempt.numduplicates + 1 + sum_nbor_dup;
					}

					total_weight[networkNum] += tempt.weight;
				}
			}
		}

		Enumeration enumcomp = components.elements();
		maxWtaxa = new String[numNetworks];

		double[] maxW = new double[numNetworks];
		networkNum = 0;
		maxW[networkNum] = 0.0;
		// double[] outweights = new double[ntaxa];
		// String[] xlabels = new String[ntaxa];

		while (enumcomp.hasMoreElements()) {
			Component tempcomp = (Component)enumcomp.nextElement();
			Enumeration enumtaxa = tempcomp.taxa.elements();
			networkNum++;

			// int i = 0;
			Log.dprintln("\n*** Network " + networkNum);
			maxW[networkNum] = 0.0;

			while (enumtaxa.hasMoreElements()) {
				TaxaItem temptaxa = (TaxaItem)enumtaxa.nextElement();

				if (!temptaxa.isIntermediate) {
					temptaxa.oweight = temptaxa.weight / total_weight[networkNum];
					Log.dprintln(temptaxa.name + " weigth = " + temptaxa.oweight);
					// outweights[i] = temptaxa.oweight;
					// xlabels[i] = temptaxa.name;
					// i++;

					if (temptaxa.oweight > maxW[networkNum]) {
						maxW[networkNum] = temptaxa.oweight;
						maxWtaxa[networkNum] = temptaxa.name;
					}
				}
			}

			Log.dprintln("Total weight = " + total_weight[networkNum]);
			Log.dprintln("Biggest outgroup probability is " + maxWtaxa[networkNum] + " (" + maxW[networkNum] + ")");
		}
	}



	/**
	 *  Recalculate the metric distances between clusters sourcec and destc when a connection of "length" is made between
	 *  taxa "sourcet" and "destt" We dont actually add the intermediates. Just put the specified length in the distance
	 *  field for the metric and recalculate distances between clusters. Creation date: (11/3/99 11:33:00 PM)
	 *
	 * @param  allvect        Vector containing all taxa from the src and dst Component
	 * @param  sourcebordert  taxa on the border that we are going to connect to a taxa in the dest cluster
	 * @param  destbordert    taxa on the border that we are going to connect to a taxa in the source cluster
	 * @param  length         the distance between the two border taxa
	 * @created               May 18, 2004
	 */
	private void recalcDist(Vector allvect, TaxaItem sourcebordert, TaxaItem destbordert, int length) {

		// change the metricdistance between the src and dest border taxa
		Distance metricDist = (Distance)sourcebordert.metricdist.get(destbordert.id);
		metricDist.distance = length;
		// metricDist = null; // let garbageCollection clean it up now?

		// We need to update distances within the clusters that might have changed as well
		// So we create two vectors with all of the taxa from both clusters in them.


		// Now update all of the distances
		Enumeration enumsourcet = allvect.elements();

		while (enumsourcet.hasMoreElements()) {
			TaxaItem sourceitem = (TaxaItem)enumsourcet.nextElement();
			Enumeration enumdestt = allvect.elements();

			while (enumdestt.hasMoreElements()) {
				TaxaItem destitem = (TaxaItem)enumdestt.nextElement();

				Distance dist = (Distance)sourceitem.metricdist.get(destitem.id);
				Distance compdist = (Distance)sourceitem.compdist.get(destitem.id);
				int newdistance = ((Distance)sourceitem.compdist.get(sourcebordert.id)).distance;

				newdistance += length;

				newdistance += ((Distance)destbordert.compdist.get(destitem.id)).distance;

				// If this change didnt make things closer, leave the old distance in the metric
				if (newdistance < compdist.distance) {
					dist.distance = newdistance;
				} else {
					dist.distance = compdist.distance;
				}

			}
		}
	}



	/**
	 *  bestMetric is designed to produce the best connections between two taxa source and dest it tries to use the nodes
	 *  source and dest intermediates to connect. first calling recalcdist, next evaluateMetric, and finally
	 *  connectComponents if there is a new better connection found Creation date: (12/15/99 8:35:28 AM)
	 *
	 * @param  sourcec         the source haplotype's parent Component
	 * @param  destc           the destination haplotypes parent Component
	 * @param  sourcet         the source haplotype
	 * @param  destt           the destination haplotype
	 * @param  limit           the max allowed distance between any two entities to be connected
	 * @param  destCandidates  a list of all the taxa in destt's parent Component (? at least when this is called from
	 *      connectTaxa)
	 * @return                 Description of the Returned Value
	 * @created
	 */
	private boolean bestMetric(Component sourcec, Component destc, TaxaItem sourcet, TaxaItem destt, int limit, Vector destCandidates) {
		boolean addedConnection = false;

		Enumeration enumSrcCompsTaxa = sourcec.taxa.elements();

		Distance bestDist = new Distance(0, 0, 0);
		int metric = -INFINITY;
		boolean firstTime = true;

		while (enumSrcCompsTaxa.hasMoreElements()) {

			TaxaItem curTaxaFromSrcsComp = (TaxaItem)enumSrcCompsTaxa.nextElement();

			// If this is the origin taxa, or another intermediate in the cluster, it is a candidate
			if ((curTaxaFromSrcsComp == sourcet) || curTaxaFromSrcsComp.isIntermediate) {

				// To everything in the dest cluster
				Enumeration enumDstCandidates = destCandidates.elements();

				while (enumDstCandidates.hasMoreElements()) {

					// If this is the destination taxa, or another intermediate in the cluster, it is a candidate
					TaxaItem curDestCandTaxa = (TaxaItem)enumDstCandidates.nextElement();

					if ((curDestCandTaxa == destt) || curDestCandTaxa.isIntermediate) {

						// Connect the source and destination through these two nodes
						// If the source and destination are in different clusters, we can
						//  treat these existing intermediates as border nodes.  If the two are
						//  in the same cluster, we have to take the distance to the closest one and
						//  treat the closest existing intermediate as the border.

						int total = ((Distance)sourcet.realdist.get(destt.id)).distance;

						// Evaluate the connection of the components using "total" number of intermediates
						// Recalculate distances, the only ones that will change are the
						//  distances where one item was in tempc1 and the other was in tempc2
						//  The distance for each item is the distance from that item to curTaxaFromSrcsComp
						//   + the distance from curTaxaFromSrcsComp to tempt2 + the distance from tempt2 to
						//   the item on the other side.  You never need to do a full shortest path calculation
						// When we are in the same cluster, we can not only add new intermediates, but we can create a shorter path by directly connecting
						if ((debug & 0x4) != 0) {
							TCSIO.printclusters(components, Log);
						}

						Distance nearest = (Distance)sourcet.compdist.get(curTaxaFromSrcsComp.id);
						//nearest holds the distance from the source node to some boundary intermediate node

						Distance other = (Distance)destt.compdist.get(curDestCandTaxa.id);
						//other holds the distance from the destination node to some boundary node in it's cluster

						// If the distance between the endpoints and the intermediates is already too big,  we can't do that!
						total -= nearest.distance + other.distance;

						//if total is less than one then the intermediates that we have choosen are to far apart
						if (total < 1) {
							continue;
						}

						// [SW] both of the following functions were using these so I moved it to here
						Vector allvect = new Vector(sourcec.taxa);
						int srcCnt = sourcec.taxa.size();
						int dstCnt = 0;
						if (sourcec.id != destc.id) {
							allvect.addAll(destc.taxa);
							dstCnt = destc.taxa.size();
						}
						// jake may 17-01
						if (srcCnt > dstCnt) {
							srcCnt *= 10;
						} else {
							srcCnt = dstCnt * 10;
						}

						recalcDist(allvect, curTaxaFromSrcsComp, curDestCandTaxa, total);

						// Evaluate the metric.  You only need to look at pairs distributed
						//  across the two clusters

						int score = evaluateMetric(allvect, srcCnt, total, limit);
						// should we keep this connection?
						if (firstTime || (score > metric)) {

							// Unmark previous connections since they are worse than this one
							firstTime = false;
							bestDist.source = curTaxaFromSrcsComp.id;
							bestDist.destination = curDestCandTaxa.id;
							bestDist.distance = total;
							metric = score;
						}
					}
				}

			}
		}

		TaxaItem bestDistSrcTaxa = (TaxaItem)alltaxa.get(bestDist.source);
		TaxaItem bestDistDstTaxa = (TaxaItem)alltaxa.get(bestDist.destination);

		Distance already = (Distance)bestDistSrcTaxa.compdist.get(bestDistDstTaxa.id);

		// not adding connection
		if (already.distance > bestDist.distance) {
			addedConnection = true;
			// adding connection, intermediates, and updating distances affected.
			connectComponents(sourcec, destc, bestDist, destCandidates);
			Utils.updateDistance(sourcec, destc, bestDist);

		}
		return addedConnection;
	}


	/**
	 *  DP wrote it. SW modified it It appears to be a loop that calls calcPars to integrate the according to the number of
	 *  nucleotides in the sequence. The goal is to set a limit on the maximum distance between two nodes. If the minimum
	 *  distance between a node and any other node is greater than the maxParsimonyDistance found by buildParseTable() then
	 *  it is an unconnected node. Creation date: (1/12/00 10:03:29 PM)
	 *
	 * @param  MaxNucleotides  Description of the Parameter
	 * @param  conLimit        Description of the Parameter
	 * @return                 int
	 * @created
	 */

	private int buildParseTable(int MaxNucleotides, int conLimit) {

		int index = 0;
		double parsval = 1;

		if (frame != null) {
			frame.statustextField.setText("STATUS: Calculating " + conLimit + "% connection limit");
			frame.statustextField.update(frame.statustextField.getGraphics());
		}

		Log.dprintln("\n\n_________________________________________________________");
		Log.dprintln("\n\nPARSIMONY PROBABILITY");

		double percentValue = (conLimit * .01);

		while (parsval > percentValue) {

			// this may go on forever with a connection limit of 0 or close to that...
			index++;
			parsval = Utils.calcPars(index, MaxNucleotides - index, 2000);
			Log.dprintln("For " + index + " step(s),\tP(" + conLimit + "%) = " + parsval);

		}

		Log.dprintln("");
		return (index - 1);
	}


	/**
	 *  Calculates the maximum parsimony distance based on the sequence length, or uses the user defined distance, or
	 *  number of haplotypes. The value is stored in maxParsimonyDistance.
	 *
	 * @created
	 */
	private void calculateConnectionLimit() {

		String steps = "";
		int conLimit = 95;

		if (frame != null) {
			//
			if (frame.manualBox.getState()) {
				steps = frame.manualConnections.getText();
			} else {
				// conLimit = frame.conLimit.getSelectedIndex();
				String conL = frame.conLimit.getSelectedItem();
				conL = conL.substring(0, 2);

				conLimit = Integer.parseInt(conL);

			}

			// System.out.println("conLimit = " + conLimit);
			// need to do something other than this here
		}

		if (steps.equals("")) {

			if (fileReader.doingHaps) {
				// [DP]

				maxParsimonyDistance = fileReader.len;
				Log.dprintln("\rRUN SETTINGS");
				Log.dprintln("Maximum connection steps equal number of SNPs = " + maxParsimonyDistance);
			}

			// Calculate ParsProb and print it
			maxParsimonyDistance = buildParseTable(fileReader.len, conLimit);
			Log.dprintln("\nRUN SETTINGS");
			Log.dprintln("Calculated maximum connection steps at " + conLimit + "% = " + maxParsimonyDistance);

			if (frame != null) {
				frame.calcLimitLabel.setText("Connection Limit = " + maxParsimonyDistance);
				frame.calcLimitLabel.update(frame.calcLimitLabel.getGraphics());
			}
		} else {
			try {
				maxParsimonyDistance = Integer.parseInt(steps);
				if (maxParsimonyDistance <= 0) {
					throw new Exception();
				}
				Log.dprintln("\nRUN SETTINGS");
				Log.dprintln("User specified maximum connection steps = " + maxParsimonyDistance);
			} catch (Exception e) {

				JOptionPane.showMessageDialog(frame, "Error : You must use a positive integer for the Connection limit!\n\tUsing a default value of 40 for the Connection Limit", "TCS warning", JOptionPane.ERROR_MESSAGE);
				maxParsimonyDistance = 40;
				Log.dprintln("\nRUN SETTINGS");
				Log.dprintln("Invalid user specified maximum connection steps.  Default = 40" + maxParsimonyDistance);
			}
		}

	}



	/**
	 *  updates the components vector's mindist.dc. Those components whose mindist pointed to the removed component are
	 *  updated to point to the collapsed component. The collapsed component then looks for its new mindist to all other components.
	 *
	 * @param  collapse  Description of the Parameter
	 * @param  removeid  Description of the Parameter
	 * @created          (11/6/99 10:59:44 AM)
	 */
	private void recalcMinDistance(Component collapse, int removeid) {
		// It doesn't appear very efficient. I think that a comparison between the mindist of the two components that were combined in combine clusters would be more effective.
		// first change all of the other clusters that used to have the removed cluster as their min, to now have the collapsed version as their min.

		Enumeration enumall = components.elements();

		while (enumall.hasMoreElements()) {
			Component tempc1 = (Component)enumall.nextElement();

			if (tempc1.mindist.dc.id == removeid) {
				tempc1.mindist.dc = collapse;
			}
		}

		Distance mindist = new Distance();
		mindist.distance = INFINITY;

		// Now we need to find the min distance for this cluster.
		// Search through all of the other clusters for the taxa that has the min distance from here
		enumall = components.elements();

		// For each of the other clusters
		while (enumall.hasMoreElements()) {
			Component tempc1 = (Component)enumall.nextElement();

			if (tempc1 == collapse) {

				continue;
			}

			Enumeration enumtaxa = tempc1.taxa.elements();

			// For each of the real taxa in this cluster
			while (enumtaxa.hasMoreElements()) {
				TaxaItem tempt1 = (TaxaItem)enumtaxa.nextElement();

				if (tempt1.isIntermediate) {

					continue;
				}

				Enumeration enumcollapse = collapse.taxa.elements();

				// Compare with everything in collapse to find the minimum
				while (enumcollapse.hasMoreElements()) {
					TaxaItem tempt2 = (TaxaItem)enumcollapse.nextElement();

					if (tempt2.isIntermediate) {

						continue;
					}

					Distance nextdist = (Distance)tempt2.realdist.get(tempt1.id);

					if (nextdist.distance < mindist.distance) {
						mindist.clone(nextdist);
						mindist.sc = collapse;
						mindist.dc = tempc1;
					}
				}
			}
		}
		collapse.mindist = mindist;
	}

}
