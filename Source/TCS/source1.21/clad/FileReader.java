package clad;

import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.JOptionPane;
import EDU.auburn.VGJ.gui.GraphWindow;

/**
 *  Description of the Class
 *
 * @author     SW, DP
 * @created    January 20, 2004 methods by DP
 */
public class FileReader {
	/**  Description of the Field */
	public int num;
	/**  Description of the Field */
	public int len;
	/**  Description of the Field */
	public int maxNameLen;
	/**  Description of the Field */
	public Vector components;
	/**  Description of the Field */
	public Vector alltaxa;
	/**  Description of the Field */
	public Vector realtaxa;
	/**  Description of the Field */
	public String missingString;
	/**  Description of the Field */
	public boolean warnMissing;
	/**  Description of the Field */
	public boolean doingHaps = false;
	private TextInputStream in;
	private GraphWindow frame;
	private int[][] distance;
	private boolean lines;
	private boolean printseq = false; // need to have this set to true for Taylor's mutation mapper...
	private Logger Log;


	// working with haps file [DP]

	/**
	 *  Constructor for the FileReader object
	 *
	 * @param  gw   Description of the Parameter
	 * @param  log  Description of the Parameter
	 * @created     May 18, 2004
	 */
	public FileReader(GraphWindow gw, Logger log) {
		frame = gw;
		Log = log;
		components = new Vector();
		alltaxa = new Vector();
		realtaxa = new Vector();

		maxNameLen = 0;
		lines = false;
		// printseq = true;

		missingString = "";
		warnMissing = false;

	}


	/**
	 *  Description of the Method
	 *
	 * @param  infile   Description of the Parameter
	 * @param  gapmode  Description of the Parameter
	 * @created         May 18, 2004
	 */
	public void ReadInputFile(String infile, boolean gapmode) {
		in = new TextInputStream(infile);

		String line = in.readLine().toUpperCase();
		// Check which format do we have, PHYLIP or NEXUS

		if (doingHaps == true) {
			// Check if we want to analyse special haplotype file
			ReadHaps(line);
		}
		// read special HAPS format
		else {
			if (line.startsWith("#NEXUS")) {
				ReadNexus(gapmode);
			}
			// NEXUS format SEQUENTIAL
			else {
				ReadPhylip(line, gapmode);
			}
			// PHYLIP format SEQUENTIAL
		}
		in.close();
	}



	/**
	 *  Description of the Method
	 *
	 * @param  infile  Description of the Parameter
	 * @created        May 18, 2004
	 */
	public void ReadDistanceFile(String infile) {
		in = new TextInputStream(infile);

		String line = in.readLine().toUpperCase();
		if (line.startsWith("#NEXUS")) {
			ReadNexusDis();
		}
		// Distances in NEXUS format, lower matrix
		else {
			ReadPhylipDis(line);
		}
		// Distances in PHYLIP format, lower matrix
	}


	/* method for reading haplotypes in a special format,
	including frecuency and confidence levels of haplotype inferral
	(for example from a programliek PHASE), so they can
	be displayed [DP] */
	/**
	 *  Description of the Method
	 *
	 * @param  line  Description of Parameter
	 * @created      May 18, 2004
	 */
	public void ReadHaps(String line) {
		/* Reads HAPS format:
		ID# 	haplotype 		absfreq confidence
		1 1222111111111211111121 1100 0.987
		2 2121112111111121111111 634 0.984
		3 2121111111111211111121 536 0.983
		4 2121112112111111112121 486 0.983
		5 1222111111111211211121 404 0.985
	*/
		int j;

		int freq;
		char c;
		String name;
		String hap;
		double confidence;
		Component tempc;
		int id = 0;
		TaxaItem temptaxa;

		Log.dprintln("\nData in HAPS format");

		// printseq = false;

		// Read other haps
		while (in.EOF() == false) {
			name = new String();
			hap = new String();

			StringTokenizer reader = new StringTokenizer(line);
			name = reader.nextToken();
			hap = reader.nextToken();
			len = hap.length();
			freq = Integer.parseInt(reader.nextToken());
			confidence = Double.parseDouble(reader.nextToken());

			if (printseq) {
				Log.dprintln(name + " " + hap + " " + freq + " " + confidence);
			}

			temptaxa = new TaxaItem(name, len, id);
			// assuming that the input sequence is totally resolved...
			temptaxa.resolved = true;
			maxNameLen = 4;
			temptaxa.name = name;
			temptaxa.confidence = confidence;
			temptaxa.numduplicates = freq - 1;

			for (j = 0; j < len; j++) {
				c = hap.charAt(j);

				c = Character.toUpperCase(c);
				temptaxa.characters[j] = c;

				if (printseq) {
					System.err.print(c);
				}

				if (c == '\n' || c == '\r') {
					JOptionPane.showMessageDialog(frame, "You have extra returns in sequence \"" + name + "\".",
							"TCS warning", JOptionPane.WARNING_MESSAGE);
					frame.dispose();
					in.close();
					System.exit(0);
				}

				if (c == ' ') {
					JOptionPane.showMessageDialog(frame, "The lenght of sequence \"" + name + "\"" + " is not " + len + ".",
							"TCS warning", JOptionPane.WARNING_MESSAGE);
					frame.dispose();
					in.close();
					System.exit(0);
				}

				if (c != '1' && c != '2' && c != '?') {
					JOptionPane.showMessageDialog(frame, "One of the characters in sequence \"" + name + "\" is not (12?).",
							"TCS warning", JOptionPane.WARNING_MESSAGE);
					frame.dispose();
					in.close();
					System.exit(0);
				}

				if (in.EOF()) {
					JOptionPane.showMessageDialog(frame, "Unexpected end of file was encountered.",
							"TCS warning", JOptionPane.WARNING_MESSAGE);
					frame.dispose();
					in.close();
					System.exit(0);
				}
			}

			// Add this to the component Vector
			tempc = new Component(id);
			tempc.taxa.add(temptaxa);
			temptaxa.parentComponent = tempc;
			alltaxa.add(temptaxa);
			realtaxa.add(temptaxa);
			id++;
			components.add(tempc);
			Log.dprintln("Added name: " + name, 1);

			try {
				line = in.readLine().toUpperCase();
			} catch (Exception e) {
				break;
			}
		}
	}



	/**
	 *  Description of the Method
	 *
	 * @param  c   Description of the Parameter
	 * @created    May 18, 2004
	 */
	private void ReadMatrix(char c) {
		/* Reads a matrix of distances in lower diagonal format */
		int popA;
		/* Reads a matrix of distances in lower diagonal format */
		int popB;
		String[] names = new String[num];
		String nname = new String();
		distance = new int[num][num];
		maxNameLen = 0;
		Log.dprintln("\rI am reading this distance matrix:");

		for (popA = 0; popA < num; popA++) {
			names[popA] = in.readWord();
			//reads the seq names from the infile

			if (lines) {
				// we have stolen the first character of the fist name. Let's get it back

				nname = nname.valueOf(c);
				nname = nname.concat(names[0]);
				names[0] = nname;
				lines = false;
			}

			Log.dprint("\r" + names[popA] + "  ");

			if (names[popA] == null) {
				JOptionPane.showMessageDialog(frame, "There was an error reading the sequence names" +
						"\nin the pairwise distance matrix",
						"TCS warning", JOptionPane.WARNING_MESSAGE);
				in.close();
				System.exit(0);
			}

			if (maxNameLen < names[popA].length()) {
				maxNameLen = names[popA].length();
			}

			for (popB = 0; popB < popA; popB++) {
				distance[popA][popB] = distance[popB][popA] = in.readInt();

				if (distance[popA][popB] < 1) {
					JOptionPane.showMessageDialog(frame, "There was an error reading the distance matrix." +
							"\nAre you using absolute distances ?" +
							"\nAre you inputing HAPLOTYPE distances (no zeroes in the matrix)? : " + distance[popA][popB],
							"TCS warning", JOptionPane.WARNING_MESSAGE);
					in.close();
					System.exit(0);
				}
				Log.dprint(" " + distance[popA][popB]);
			}
		}
		// Now convert the matrix to the vectors used in the rest of the program

		Log.dprintln();

		Component tempc;
		TaxaItem temptaxa;
		int cnt;

		for (cnt = 0; cnt < num; cnt++) {
			tempc = new Component(cnt);
			components.add(tempc);
			temptaxa = new TaxaItem(names[cnt], len, cnt);
			//FIXME assume that whatever we are creating this taxa for, it is unresolved sequence-wise, since it doesn't have seq. info
			temptaxa.resolved = false;
			temptaxa.isIntermediate = false;
			tempc.taxa.add(temptaxa);
			temptaxa.parentComponent = tempc;
			alltaxa.add(temptaxa);
			realtaxa.add(temptaxa);
		}
		Component tempc1;
		Component tempc2;
		TaxaItem tempt1;
		TaxaItem tempt2;
		Enumeration enum1 = components.elements();
		Distance mindist = new Distance();
		while (enum1.hasMoreElements()) {
			tempc1 = (Component)enum1.nextElement();
			tempt1 = (TaxaItem)tempc1.taxa.elementAt(0);
			tempc1.mindist.distance = TCS.INFINITY;
			Enumeration enum2 = components.elements();
			mindist.distance = TCS.INFINITY;
			// mindist will hold the minimum distance from c1 to any c2

			while (enum2.hasMoreElements()) {
				tempc2 = (Component)enum2.nextElement();
				tempt2 = (TaxaItem)tempc2.taxa.elementAt(0);
				Distance realdist = new Distance(tempc1.id, tempc2.id, 0);
				// tmpdist has the distance between these two
				// If it is myself, add the distance elements and go to top
				if (tempc1.id == tempc2.id) {
					Distance tmpcdist = new Distance(tempc1.id, tempc2.id, 0, tempc1, tempc1);
					Distance tmptdist = new Distance(tempt1.id, tempt2.id, 0, tempc1, tempc1);
					Distance metricdist = new Distance(tempt1.id, tempt2.id, 0, tempc1, tempc1);
					tmpcdist.dc = tempc1;
					tmpcdist.sc = tempc1;
					tmptdist.dc = tempc1;
					tmptdist.sc = tempc1;
					realdist.dc = tempc1;
					realdist.sc = tempc1;
					tempc1.compdist.add(tmpcdist);
					tempt1.compdist.add(tmptdist);
					tempt1.realdist.add(realdist);
					tempt1.metricdist.add(metricdist);
					continue;
				}

				realdist.distance = distance[tempc1.id][tempc2.id];

				Distance tmpcdist = new Distance(tempc1.id, tempc2.id, realdist.distance, tempc1, tempc2);
				Distance tmptdist = new Distance(tempt1.id, tempt2.id, TCS.INFINITY, tempc1, tempc2);
				Distance metricdist = new Distance(tempt1.id, tempt2.id, TCS.INFINITY, tempc1, tempc2);
				tempc1.compdist.add(tmpcdist);
				tempt1.compdist.add(tmptdist);
				tempt1.realdist.add(realdist);
				tempt1.metricdist.add(metricdist);
				if (realdist.distance < mindist.distance) {
					mindist.distance = realdist.distance;
					// mindist has taxa ids not component ids
					mindist.source = tempt1.id;
					mindist.sc = tempc1;
					mindist.destination = tempt2.id;
					mindist.dc = tempc2;
					Log.dprintln("min distance for " + tempc1.id + " = " + mindist.distance + " from " + mindist.source + " to " + mindist.destination, 0x20);
				}
			}
			Log.dprintln("final min distance for " + tempc1.id + " = " + mindist.distance + " from " + mindist.source + " to " + mindist.destination, 0x20);
			tempc1.mindist.clone(mindist);
			tempt1.minRealDist = mindist.distance;
		}
	}


	/**
	 *  Description of the Method
	 *
	 * @param  gapmode  Description of the Parameter
	 * @created         May 18, 2004
	 */
	private void ReadNexus(boolean gapmode) {

		/* Reads NEXUS format SEQUENTIAL   */
		char c;

		char d;
		int i;
		int j;
		int cnt = 0;
		boolean missing = false;
		int IUPACcounter = 0;
		String name = new String();
		Log.dprintln("Data in NEXUS format");

		String token = in.readWord().toUpperCase();
		while (!token.startsWith("NTAX=")) {
			token = in.readWord().toUpperCase();
		}
		StringTokenizer reader = new StringTokenizer(token, " =\n\r\t;");
		reader.nextToken();
		num = Integer.parseInt(reader.nextToken());

		while (!token.startsWith("NCHAR=")) {
			token = in.readWord().toUpperCase();
		}
		reader = new StringTokenizer(token, " =\n\r\t;");
		reader.nextToken();
		len = Integer.parseInt(reader.nextToken());

		Log.dprintln("Number of sequences: " + num);
		Log.dprintln("Length of sequences: " + len);

		while (!token.equals("MATRIX")) {
			// go all the way to the sequences
			token = in.readWord().toUpperCase();
		}

		// get rid of potential stuff after the "Matrix" word and before sequence name
		c = in.readChar();
		while (c == '\r' || c == '\n' || c == '\t') {
			c = in.readChar();
			lines = true;
		}
		// if there is a comment for the position numbers (e.g McClade) [ 1 2 3 4] after "Matrix"
		if (c == '[') {
			while ((c = in.readChar()) != ']') {
				;
			}
		}

		// in case there is more stuff in the way to the sequences
		while (c == '\r' || c == '\n' || c == '\t' || c == ']' || c == ' ') {
			c = in.readChar();
			lines = true;
			Log.dprintln(c);
		}

		for (i = 0; i < num; i++) {
			if (lines) {
				// we have stolen the first character of the fist name. Let's get it back

				String nname = String.valueOf(c);
				d = in.readChar();
				// but if the name is just one character long, we already have it
				if (d != ' ') {
					nname = nname.concat(name.valueOf(d));
					name = in.readWord();
					nname = nname.concat(name);
				}
				name = nname;
				lines = false;
			} else {
				name = in.readWord();
			}

			if (printseq) {
				Log.dprintln("\r" + (i + 1) + ". " + name);
			}

			TaxaItem temptaxa = new TaxaItem(name, len, cnt);
		//FIXME assume that since we are reading this seq. from nexus, it is resolved sequence-wise
			temptaxa.resolved = true;
			temptaxa.isIntermediate = false;

			if (name.length() == 0) {
				name = in.readLine();
			}
			if (maxNameLen < name.length()) {
				maxNameLen = name.length();
			}
			temptaxa.name = name;

			for (j = 0; j < len; j++) {
				// skip white spaces between name and beginning of sequence
				while ((c = in.readChar()) == ' ') {
					;
				}

				c = Character.toUpperCase(c);
				temptaxa.characters[j] = c; // SW NOTE:  I will store this later? After it may be changed to ? from an amb. code?
				if (printseq) {
					Log.dprint(String.valueOf(c));
				}

				if (c == '\n' || c == '\r') {
					JOptionPane.showMessageDialog(frame, "You have extra returns in sequence \"" + name + "\".",
							"TCS warning", JOptionPane.WARNING_MESSAGE);
					in.close();
					frame.dispose();
					System.exit(0);
				}

				if (c == ' ') {
					JOptionPane.showMessageDialog(frame, "The length of sequence \"" + name + "\"" + " is not " + len + ".",
							"TCS warning", JOptionPane.WARNING_MESSAGE);
					in.close();
					System.exit(0);
				}

				// IUPAC codes -> 	M: A/C		S: G/C		H: A/C/T	B: C/G/T
				//					R: A/G		K: G/T		V: A/C/G	X: A/C/G/T = ?
				//					W: A/T		Y: C/T		D: A/G/T	N: A/C/G/T = ?
				if (c == 'R' || c == 'M' || c == 'W' || c == 'S' || c == 'K' || c == 'Y' ||
						c == 'H' || c == 'V' || c == 'D' || c == 'B' || c == 'X' || c == 'N') {
									
						c = '?';
						temptaxa.characters[j] = c; // SW NOTE I store the character as a ? in the sequences for when I do the mapping...				
				if (IUPACcounter == 0)
					{
					JOptionPane.showMessageDialog(frame, "Note that there are IUPAC ambiguity codes in the data: \"" + c + "\"" +
								"\n\nIUPAC codes: " +
								"\nM: A/C    S: G/C    H: A/C/T    B: C/G/T" +
								"\nR: A/G    K: G/T    V: A/C/G    X: A/C/G/T = ?" +
								"\nW: A/T    Y: C/T    D: A/G/T    N: A/C/G/T = ?" +
 							    "\n\nThey will be treated as missing data",
								"TCS information", JOptionPane.INFORMATION_MESSAGE);
					IUPACcounter++;
					}
					
				}

				if (c != 'A' && c != 'C' && c != 'G' && c != 'T' && c != '-' && c != '.' && c != '?') {
					JOptionPane.showMessageDialog(frame, "One of the characters in sequence \"" + name + "\" is not (ACGCacgt.?-)." +
							"\nnor an IUPAC ambiguity code (RMWSKYHBVDXN)",
							"TCS warning", JOptionPane.WARNING_MESSAGE);
					in.close();
					System.exit(0);
				}

				if (in.EOF()) {
					JOptionPane.showMessageDialog(frame, "Unexpected end of file was encountered.",
							"TCS warning", JOptionPane.WARNING_MESSAGE);
					in.close();
					System.exit(0);
				}
			}

			Log.dprintln("Read data: " + temptaxa.characters, 1);
			in.readLine();
			// get rid of the CR
			TaxaItem firsttaxa;
			int numdups = 0;
			char newc;
			char oldc;
			Enumeration enumc = components.elements();
			Component tempc;
			while (enumc.hasMoreElements()) {
				firsttaxa = (TaxaItem)((Component)components.get(0)).taxa.get(0);
				tempc = (Component)enumc.nextElement();
				Log.dprintln("looking at component: " + tempc.id, 1);
				boolean dup = true;
				TaxaItem tempt;
				tempt = (TaxaItem)tempc.taxa.elementAt(0);
				// There should be exactly one here
				Log.dprintln("looking at taxa: " + tempt.id + " named " + tempt.name +
						"and" + temptaxa.id + " named " + temptaxa.name, 1);
				Log.dprintln("comparing " + tempt.characters, 1);
				Log.dprintln("with " + temptaxa.characters, 1);

				//compare the all elements of components TaxaItem w/ the new taxaItem
				for (int k = 0; k < len; k++) {
					newc = temptaxa.characters[k];
					oldc = tempt.characters[k];
					if (newc == '.') {
						newc = firsttaxa.characters[k];
					}
					if (oldc == '.') {
						oldc = firsttaxa.characters[k];
					}
					Log.dprint("[" + newc + "," + oldc + "]", 1);
					if (!gapmode && (newc == '-' || oldc == '-')) {
						continue;
					}
					if (newc == '?' || oldc == '?') {
						missing = true;
						continue;
					}
					if (newc != oldc) {
						dup = false;
						break;
					}
				}

				if (dup && missing) {
					warnMissing = true;
					missingString = missingString.concat(tempt.name + " and " + temptaxa.name +
							" differ only by missing or ambiguous characters\n");
				}

				Log.dprintln(1);
				if (dup) {
					tempt.numduplicates++;
					numdups++;
					tempt.dupnames.add(temptaxa.name);
					Log.dprintln("duplicate name: " + name + " duplicates " + tempt.numduplicates, 1);
					break;
				}
			}
			if (numdups == 0) {
				//this is a new taxa not equal to any other
				// Add this to the component Vector
				tempc = new Component(cnt);
				tempc.taxa.add(temptaxa);
				temptaxa.parentComponent = tempc;
				alltaxa.add(temptaxa);
				realtaxa.add(temptaxa);
				cnt++;
				components.add(tempc);
				Log.dprintln("Added name: " + name, 1);
			}
		}
	}



	/**
	 *  Description of the Method
	 *
	 * @created    May 18, 2004
	 */
	private void ReadNexusDis() {
		char c;
		// char ch;
		// String name = new String();
		Log.dprintln("\rReading NEXUS distance file\r");

		//this function parses out the header of the nex files

		String token = in.readWord().toUpperCase();
		while (!token.startsWith("NTAX=")) {
			token = in.readWord().toUpperCase();
		}
		StringTokenizer reader = new StringTokenizer(token, " =\n\r\t;");
		reader.nextToken();
		num = Integer.parseInt(reader.nextToken());

		while (!token.startsWith("NCHAR=")) {
			token = in.readWord().toUpperCase();
		}
		reader = new StringTokenizer(token, " =\n\r\t;");
		reader.nextToken();
		len = Integer.parseInt(reader.nextToken());

		if (Character.isDigit((char)num)) {
			JOptionPane.showMessageDialog(frame, "ReadNexusDis > Problems reading the number of the sequences! : " + num,
					"TCS warning", JOptionPane.WARNING_MESSAGE);
			in.close();
			System.exit(0);
		}

		if (Character.isDigit((char)len)) {
			JOptionPane.showMessageDialog(frame, "ReadNexusDis > Problems reading the length of the sequences! : " + len,
					"TCS warning", JOptionPane.WARNING_MESSAGE);
			in.close();
			System.exit(0);
		}

		Log.dprintln("Number of sequences: " + num);
		Log.dprintln("Length of sequences: " + len);

		while (!token.equals("MATRIX")) {
			// go all the way to the distances
			token = in.readWord().toUpperCase();
		}

		// get rid of potential stuff after the "Matrix" word and before sequence name
		c = in.readChar();
		while (c == '\r' || c == '\n' || c == '\t') {
			c = in.readChar();
			lines = true;
		}
		// if there is a comment for the position numbers (e.g McClade) [ 1 2 3 4] after "Matrix"
		if (c == '[') {
			while ((c = in.readChar()) != ']') {
				;
			}
		}
		// in case there are more things in the way to the sequences
		while (c == '\r' || c == '\n' || c == '\t' || c == ']' || c == ' ') {
			c = in.readChar();
			lines = true;
			Log.dprintln(c);
		}

		ReadMatrix(c);
		in.close();
	}


	//FIXME:  Put this in another class!
	/**
	 *  Description of the Method
	 *
	 * @param  line     Description of the Parameter
	 * @param  gapmode  Description of the Parameter
	 * @created         May 18, 2004
	 */
	private void ReadPhylip(String line, boolean gapmode) {
		char c;
		/* Reads PHYLIP format SEQUENTIAL (max length of the names = 10) */
		char ch;
		int j;
		TaxaItem firsttaxa;
		boolean missing = false;
		int IUPACcounter = 0;
		int cnt = 0;
		boolean cr = false;

		Log.dprintln("\nData in PHYLIP format");

		StringTokenizer reader = new StringTokenizer(line);
		num = Integer.parseInt(reader.nextToken());
		len = Integer.parseInt(reader.nextToken());
		Log.dprintln("Number of sequences: " + num);
		Log.dprintln("Length of sequences: " + len);

		// Add the components
		for (int i = 0; i < num; i++) {
			StringBuffer label = new StringBuffer();

			for (int k = 0; k < 10; k++) {
				ch = in.readChar();
				if (ch == '\n' || ch == '\r') {
					cr = true;
					break;
				}
				label.append(ch);
			}

			String name = label.toString();

			if (printseq) {
				Log.dprintln("\n" + (i + 1) + ". " + name);
			}

			TaxaItem temptaxa = new TaxaItem(name, len, cnt);
			//FIXME assume that since we are reading this seq. from nexus, it is resolved sequence-wise
			temptaxa.resolved = true;

			temptaxa.isIntermediate = false;
			if (name.length() == 0) {
				name = in.readLine();
			}
			if (maxNameLen < name.length()) {
				maxNameLen = name.length();
			}
			temptaxa.name = name;

			for (j = 0; j < len; j++) {
				c = in.readChar();
				if (c == '\n' || c == '\r') {
					// there is a CR after the name
					c = in.readChar();
				}

				c = Character.toUpperCase(c);
				/* DP 240201 */
				temptaxa.characters[j] = c;

				/*if (printseq) {
					System.err.print(c);
				}*/

				if (c == '\n' || c == '\r') {
					JOptionPane.showMessageDialog(frame, "You have extra returns in sequence \"" + name + "\".",
							"TCS warning", JOptionPane.WARNING_MESSAGE);
					in.close();
					System.exit(0);
				}

				if (c == ' ') {
					JOptionPane.showMessageDialog(frame, "The length of sequence \"" + name + "\"" + " is not " + len + ".",
							"TCS warning", JOptionPane.WARNING_MESSAGE);
					in.close();
					System.exit(0);
				}


	
				// IUPAC codes -> 	M: A/C		S: G/C		H: A/C/T	B: C/G/T
				//					R: A/G		K: G/T		V: A/C/G	X: A/C/G/T = ?
				//					W: A/T		Y: C/T		D: A/G/T	N: A/C/G/T = ?
				if (c == 'R' || c == 'M' || c == 'W' || c == 'S' || c == 'K' || c == 'Y' ||
						c == 'H' || c == 'V' || c == 'D' || c == 'B' || c == 'X' || c == 'N') {
									
						c = '?';
						temptaxa.characters[j] = c; // SW NOTE I store the character as a ? in the sequences for when I do the mapping...			
				
				if (IUPACcounter == 0)
					{
					JOptionPane.showMessageDialog(frame, "Note that there are IUPAC ambiguity codes in the data: \"" + c + "\"" +
								"\n\nIUPAC codes: " +
								"\nM: A/C    S: G/C    H: A/C/T    B: C/G/T" +
								"\nR: A/G    K: G/T    V: A/C/G    X: A/C/G/T = ?" +
								"\nW: A/T    Y: C/T    D: A/G/T    N: A/C/G/T = ?" + 
								"\n\nThey will be treated as missing data",
								"TCS information", JOptionPane.INFORMATION_MESSAGE);
					IUPACcounter++;
					}
					
				}

				if (c != 'A' && c != 'C' && c != 'G' && c != 'T' && c != '-' && c != '.' && c != '?') {
					JOptionPane.showMessageDialog(frame, "One of the characters in sequence \"" + name + "\" is not (ACGCacgt.?-)." +
							"\nnor an IUPAC ambiguity code (RMWSKYHBVDXN)",
							"TCS warning", JOptionPane.WARNING_MESSAGE);
					in.close();
					System.exit(0);
				}

				if (in.EOF()) {
					JOptionPane.showMessageDialog(frame, "Unexpected end of file was encountered.",
							"TCS warning", JOptionPane.WARNING_MESSAGE);
					in.close();
					System.exit(0);
				}
			}
			if (printseq) {
			    Log.dprint(new String(temptaxa.characters));
			}
			Log.dprintln("Read data: " + temptaxa.characters, 1);
			in.readLine();
			// get rid of the CR
			int numdups = 0;
			char newc;
			char oldc;
			Enumeration enumc = components.elements();
			Component tempc;
			while (enumc.hasMoreElements()) {
				firsttaxa = (TaxaItem)((Component)components.get(0)).taxa.get(0);
				tempc = (Component)enumc.nextElement();
				Log.dprintln("looking at component: " + tempc.id, 1);
				boolean dup = true;
				TaxaItem tempt;
				tempt = (TaxaItem)tempc.taxa.elementAt(0);
				// There should be exactly one here
				Log.dprintln("looking at taxa: " + tempt.id + " named " + tempt.name +
						"and" + temptaxa.id + " named " + temptaxa.name, 1);
				Log.dprintln("comparing " + tempt.characters, 1);
				Log.dprintln("with " + temptaxa.characters, 1);

				for (int k = 0; k < len; k++) {
					newc = temptaxa.characters[k];
					oldc = tempt.characters[k];
					if (newc == '.') {
						newc = firsttaxa.characters[k];
					}
					if (oldc == '.') {
						oldc = firsttaxa.characters[k];
					}
					Log.dprint("[" + newc + "," + oldc + "]", 1);
					if (!gapmode && (newc == '-' || oldc == '-')) {
						continue;
					}
					if (newc == '?' || oldc == '?') {
						missing = true;
						continue;
					}
					if (newc != oldc) {
						dup = false;
						break;
					}
				}

				if (dup && missing) {
					warnMissing = true;
					missingString = missingString.concat(tempt.name + " and " + temptaxa.name + " differ only by missing or ambiguous characters\r");
				}

				Log.dprintln(1);
				if (dup == true) {
					tempt.numduplicates++;
					numdups++;
					tempt.dupnames.add(temptaxa.name);
					Log.dprintln("duplicate name: " + name + " duplicates " + tempt.numduplicates, 1);
					break;
				}
			}
			if (numdups == 0) {
				// Add this to the component Vector
				tempc = new Component(cnt);
				tempc.taxa.add(temptaxa);
				temptaxa.parentComponent = tempc;
				alltaxa.add(temptaxa);
				realtaxa.add(temptaxa);
				cnt++;
				components.add(tempc);
				Log.dprintln("Added name: " + name, 1);
			}
		}
	}



	/**
	 *  Description of the Method
	 *
	 * @param  line  Description of the Parameter
	 * @created      May 18, 2004
	 */
	private void ReadPhylipDis(String line) {
		Log.dprintln("\rReading PHYLIP distance file\r");
		StringTokenizer reader = new StringTokenizer(line);
		num = Integer.parseInt(reader.nextToken());
		len = Integer.parseInt(reader.nextToken());

		if (Character.isDigit((char)num)) {
			JOptionPane.showMessageDialog(frame, "ReadPhylipDis > Problems reading the number of the sequences! : "
					+ num, "TCS warning", JOptionPane.WARNING_MESSAGE);
			in.close();
			System.exit(0);
		}

		if (Character.isDigit((char)len)) {
			JOptionPane.showMessageDialog(frame, "ReadPhylipDis > Problems reading the length of the sequences! : "
					+ len, "TCS warning", JOptionPane.WARNING_MESSAGE);
			in.close();
			System.exit(0);
		}

		Log.dprintln("Number of sequences: " + num);
		Log.dprintln("Length of sequences: " + len);
		ReadMatrix('a');
		in.close();
	}

}
// end of class
