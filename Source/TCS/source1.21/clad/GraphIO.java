
/**
 *  A helper class to perform the IO operations on the EDU.auburn.VGJ.Graph stuff
 *
 * @author    wooo
 */
package clad;

import java.awt.FileDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringBufferInputStream;

import EDU.auburn.VGJ.graph.GMLlexer;
import EDU.auburn.VGJ.graph.GMLobject;
import EDU.auburn.VGJ.graph.Graph;
import EDU.auburn.VGJ.graph.ParseError;
import EDU.auburn.VGJ.gui.GraphCanvas;
import EDU.auburn.VGJ.gui.GraphWindow;
import EDU.auburn.VGJ.gui.MessageDialog;
import EDU.auburn.VGJ.gui.pictdialog;


/**
 *  Description of the Class
 *
 * @author    wooo
 */
public class GraphIO {

	/**
	 *  Gets the fileName attribute of the GraphWindow object
	 *
	 * @param  label  Description of Parameter
	 * @param  tcs    Description of Parameter
	 * @param  gw     Description of Parameter
	 * @return        The fileName value
	 */
	public static String getFileName(String label, TCS tcs, GraphWindow gw) {
		String filename = null;
		FileDialog fd2;
		// if (fd2 == null) {

		fd2 = new FileDialog(gw, label, FileDialog.LOAD);
		fd2.setVisible(true);

		// } else {
		// 	fd2.setVisible(true);

		// }

		filename = fd2.getFile();

		if (filename == null) {
			fd2 = null;

			return filename;
		}

		filename = fd2.getDirectory() + filename;
		return filename;
	}


	/**
	 *  Description of the Method
	 *
	 * @param  filename      Description of Parameter
	 * @param  GMLfile_      Description of Parameter
	 * @param  gw            Description of Parameter
	 * @param  graph_        Description of Parameter
	 * @param  graphCanvas_  Description of Parameter
	 * @return               Description of the Returned Value
	 */
	public static GMLobject loadFile(String filename, GMLobject GMLfile_, GraphWindow gw, Graph graph_, GraphCanvas graphCanvas_) {

		GMLobject gmlgraph = null;

		try {
			// System.out.println("should be a graph: " + filename);
			File fname = new File(filename);
			int size = (int)fname.length();
			int bytesRead = 0;
			FileInputStream infile = new FileInputStream(fname);
			byte[] data = new byte[size];

			while (bytesRead < size) {
				bytesRead += infile.read(data, bytesRead, size - bytesRead);
			}

			StringBufferInputStream stream = new StringBufferInputStream(new String(data, 0));
			GMLlexer lexer = new GMLlexer(stream);

			try {
				GMLfile_ = new GMLobject(lexer, null);

				//            GMLobject
				gmlgraph = GMLfile_.getGMLSubObject("graph", GMLobject.GMLlist, false);

				if (gmlgraph == null) {
					GMLfile_ = null;
					new MessageDialog(gw, "Error", "File does not contain a graph.", true);

					return null;
				}

				Graph newgraph = null;
				newgraph = new Graph(gmlgraph);
				graph_.copy(newgraph);
				graphCanvas_.update(true);
				// filename = filename;
				// gw.setTitle_();
			} catch (ParseError error) {
				new MessageDialog(gw, "Error", "Invalid graph file!\n\n" + error.getMessage() + " at line " + lexer.getLineNumber() + " at or near \"" + lexer.getStringval() + "\".", true);
//				error.printStackTrace();

			}
		} catch (FileNotFoundException e) {

			/*MessageDialog dg = */
			new MessageDialog(gw, "Error", "Error loading file  \"" + filename + "\".", true);
		} catch (IOException e) {

			/*MessageDialog dg = */
			new MessageDialog(gw, "Error", e.getMessage(), true);
		}
		return GMLfile_;
	}


	/**
	 *  Save the graph to a "standard" graph representation
	 *
	 * @param  writeNet      Description of Parameter
	 * @param  filename      file name to which we save the standard graph representation
	 * @param  graphCanvas_  Description of Parameter
	 * @param  gw            Description of Parameter
	 * @param  GMLfile_      Description of Parameter
	 * @param  graph_        Description of Parameter
	 * @param  tcs           Description of Parameter
	 * @return               Description of the Returned Value
	 */

	public static GMLobject saveStandard(boolean writeNet, String filename, GraphCanvas graphCanvas_, GraphWindow gw, GMLobject GMLfile_, Graph graph_, TCS tcs) {
		GMLobject GMLo;
		if (writeNet) {
			GMLo = loadFile(filename, GMLfile_, gw, graph_, graphCanvas_);
			graphCanvas_.selectRoot();
			gw.applyAlgorithm("Tree Up");
			graphCanvas_.removeGroups();
		}
		try {

			if (GMLfile_ == null) {
				GMLfile_ = new GMLobject(null, GMLobject.GMLfile);
				GMLfile_.addObjectToEnd(new GMLobject("graph", GMLobject.GMLlist));
			}

			GMLobject gmlgraph = GMLfile_.getGMLSubObject("graph", GMLobject.GMLlist, false);
			graph_.setGMLvalues(gmlgraph);
			gmlgraph.prune();

			PrintStream ps = new PrintStream(new FileOutputStream(filename));
			ps.println(GMLfile_.toString(0));
			ps.close();

			if (writeNet) {
				// print out the standard format network for comparing
				String net = saveStandardHelper(gmlgraph, graph_, tcs);

				PrintStream netF = new PrintStream(new FileOutputStream(filename + ".net"));
				netF.println(net);
				netF.close();
			}
			return GMLfile_;
		} catch (Exception e) {
			System.out.println("Error writing Graph/Network File!\n");
			e.printStackTrace();
		}
		return null;
	}


	/**
	 *  Description of the Method
	 *
	 * @param  interactive   Description of Parameter
	 * @param  filename      Description of Parameter
	 * @param  graphCanvas_  Description of Parameter
	 * @param  gw            Description of Parameter
	 * @param  pictDialog_   Description of Parameter
	 * @return               Description of the Returned Value
	 */
	public static pictdialog pictSave(boolean interactive, String filename, GraphCanvas graphCanvas_, GraphWindow gw, pictdialog pictDialog_) {

		if (!interactive) {
			graphCanvas_.selectRoot();
			gw.applyAlgorithm("Tree Down");
		}

		// Now save the graph as a pict, and move on...
		if (pictDialog_ == null) {
			pictDialog_ = new pictdialog(gw, graphCanvas_);
		}

		if (interactive) {
			pictDialog_.pack();
			pictDialog_.setVisible(true);
		} else {
			filename = filename + ".PICT";
			pictDialog_.Save(filename);
		}
		// end else

		return pictDialog_;
	}


	/**
	 *  Description of the Method
	 *
	 * @param  label      Description of Parameter
	 * @param  filename_  Description of Parameter
	 * @param  GMLfile_   Description of Parameter
	 * @param  gw         Description of Parameter
	 * @param  graph_     Description of Parameter
	 * @return            Description of the Returned Value
	 */
	public static String saveGML(String label, String filename_, GMLobject GMLfile_, GraphWindow gw, Graph graph_) {
		String filename = null;
		try {

			if (label.equals("Save Graph As (GML)") || filename_ == null) {

				FileDialog fd;
				// if (fd == null) {

				// try {
				fd = new FileDialog(gw, "Save VGJ File (GML) Graph", FileDialog.SAVE);
				fd.setVisible(true);

				/*} catch (Throwable e) {
					new MessageDialog(gw, "Error", "It appears your VM does not allow file saving.", true);
					return null;
				}*/
				// } else {
				// fd.setVisible(true);

				// }

				filename = fd.getFile();

				if (filename == null) {
					fd = null;

					return null;
				}

				/*// Work around JDK Windows bug.
				if (filename.endsWith(".*.*")) {
					String tmpstr = filename.substring(0, filename.length() - 4);
					filename = tmpstr;
				}*/
				filename = fd.getDirectory() + filename;
			} else {
				filename = filename_;
			}

			if (GMLfile_ == null) {
				GMLfile_ = new GMLobject(null, GMLobject.GMLfile);
				GMLfile_.addObjectToEnd(new GMLobject("graph", GMLobject.GMLlist));
			}

			PrintStream ps = new PrintStream(new FileOutputStream(filename));
			ps.println(GMLfile_.toString(0));
			ps.close();
			// filename_ = filename;


		} catch (IOException e) {

			/*MessageDialog dg = */
			new MessageDialog(gw, "Error", e.getMessage(), true);
		}
		return filename;
	}


	/**
	 *  Description of the Method
	 *
	 * @param  gw            Description of Parameter
	 * @param  GMLfile_      Description of Parameter
	 * @param  graph_        Description of Parameter
	 * @param  graphCanvas_  Description of Parameter
	 * @return               Description of the Returned Value
	 */
	public static String openGMLGraph(GraphWindow gw, GMLobject GMLfile_, Graph graph_, GraphCanvas graphCanvas_) {
		String filename;
		FileDialog fd1;
		// if (fd1 == null) {

		// try {
		fd1 = new FileDialog(gw, "Open VGJ File (GML) graph", FileDialog.LOAD);
		fd1.setVisible(true);

		/*} catch (Throwable e) {
			new MessageDialog(gw, "Error", "It appears your VM does not allow file loading.", true);
			return null;
		}*/
		// } else {
		// fd1.setVisible(true);

		// }

		filename = fd1.getFile();

		if (filename == null) {
			fd1 = null;

			return null;
		}

		filename = fd1.getDirectory() + filename;
		// GMLo = loadFile(filename, GMLfile_, gw, graph_, graphCanvas_);

		return filename;
	}


	/**
	 *  Description of the Method
	 *
	 * @param  GMLo    Description of Parameter
	 * @param  graph_  Description of Parameter
	 * @param  tcs     Description of Parameter
	 * @return         Description of the Returned Value
	 */
	private static String saveStandardHelper(GMLobject GMLo, Graph graph_, TCS tcs) {

		// From the Graph
		// get all of the nodes (print the number)
		// print the coordinates of all of the nodes
		// print all the edges
		// save this to a file...
		// String outputString  = "";
		GMLobject nodegml;
		GMLobject gml;
		GMLobject GMLtmp;
		gml = GMLo;

		// If the GML doesn't contain a graph, assume it is a graph.
		GMLtmp = gml.getGMLSubObject("graph", GMLobject.GMLlist, false);

		if (GMLtmp != null) {
			gml = GMLtmp;
		}

		int nodeCount = graph_.numberOfNodes();
		String outputString = "";
		int existingNodeCount = nodeCount;

		// also output the
		double[] xs = new double[nodeCount];
		double[] ys = new double[nodeCount];
		double xoffset = 0.0;
		double yoffset = 0.0;

		// go through and get all nodes...
		// int nodeCount        = 0;

		for (nodegml = gml.getGMLSubObject("node", GMLobject.GMLlist, false); nodegml != null; nodegml = gml.getNextGMLSubObject()) {
			// put them into an array, and then read them out one at a time
			//also, keep track of the most negative x,y so that we can add that offset

			// nodeCount++;
			int id = -1;

			Integer tmpint;
			tmpint = (Integer)nodegml.getValue("id", GMLobject.GMLinteger);
			id = tmpint.intValue();

			// graphics, center, {x, y}
			Double x;
			Double y;
			GMLobject graphics;
			GMLobject center;

			String label;

			label = (String)nodegml.getValue("label", GMLobject.GMLstring);
			if (label.equals(" ")) {
				existingNodeCount--;
			}

			graphics = nodegml.getGMLSubObject("graphics", GMLobject.GMLlist, false);
			// System.out.println("graphics = " + graphics);
			center = graphics.getGMLSubObject("center", GMLobject.GMLlist, false);
			// System.out.println("center = " + center);
			x = (Double)center.getValue("x", GMLobject.GMLreal);
			y = (Double)center.getValue("y", GMLobject.GMLreal);

			// System.out.println("x = " + x);
			// System.out.println("y = " + y);

			// outputString += x + " " + y + "\n";
			// save them in the arrays in the proper order

			//get the list perhaps?

			double tempx = x.doubleValue();
			double tempy = y.doubleValue();
			xs[id] = tempx;
			ys[id] = tempy;

			if (tempx < xoffset) {
				xoffset = tempx;
			}

			if (tempy < yoffset) {
				yoffset = tempy;
			}
			// keep track of the most negative x and y

			//I need to put these in order numerically I think... but maybe it doesn't matter?

		}

		for (int r = 0; r < nodeCount; r++) {

			outputString += (xs[r] - xoffset) + " " + (ys[r] - yoffset) + " " + tcs.getHaplotypeList(r) + "\n";
		}

		GMLobject edgegml;

		// now get all edges
		// is there an easy way to know how many real exist?  " "
		for (edgegml = gml.getGMLSubObject("edge", GMLobject.GMLlist, false); edgegml != null; edgegml = gml.getNextGMLSubObject()) {

			Integer source;
			Integer target;
			source = (Integer)edgegml.getValue("source", GMLobject.GMLinteger);
			target = (Integer)edgegml.getValue("target", GMLobject.GMLinteger);

			int s;

			int t;
			s = source.intValue() + 1;
			t = target.intValue() + 1;
			if (source != null && target != null) {
				outputString += s + " " + t + "\n";
			}
		}

		// System.out.println("Network = \n" + outputString);
		outputString = nodeCount + " " + existingNodeCount + "\n" + outputString;
		return outputString;
	}
}
