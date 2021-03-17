package EDU.auburn.VGJ;

import java.applet.Applet;

import java.awt.Button;
import java.awt.Event;
import java.awt.Font;
import java.awt.Frame;

/*
    Original file: VGJ.java
    Date      Author
    2/16/97   Larry Barowski
		5/10/04   Steven Woolley cleaned up and allowed command line parameters
  */
import java.awt.GridLayout;
import java.io.File;
import java.util.Vector;
import javax.swing.JFrame;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import EDU.auburn.VGJ.algorithm.shawn.Spring;
import EDU.auburn.VGJ.algorithm.tree.TreeAlgorithm;
import EDU.auburn.VGJ.graph.Node;
import EDU.auburn.VGJ.gui.GraphWindow;

import clad.TCS;


/**
 *  The VGJ applet. It is a big button that pops up VGJ graph editor windows. </p> Here is the <a href="../VGJ.java">
 *  source</a> .
 *
 * @author    wooo
 */
public class VGJ extends Applet {

	private int appCount_ = 0;
	private boolean isApp_ = false;


	/**
	 *  Description of the Method
	 *
	 * @param  event  Description of the Parameter
	 * @param  what   Description of the Parameter
	 * @return        Description of the Return Value
	 */
	public boolean action(Event event, Object what) {

		if (event.target instanceof Button) {

			if (((String)what).equals("Exit")) {
				System.exit(0);
			} else {
				buildNewTCSWindow();
			}
		}

		return super.action(event, what);
	}


	/**  Description of the Method */
	public void init() {

		Button b = new Button("Start New TCS Analysis");
		b.setFont(new Font("Charcoal", Font.PLAIN, 10));
		if (isApp_) {
			setLayout(new GridLayout(2, 1));
			add(b);
			Button c = new Button("Exit");
			c.setFont(new Font("Charcoal", Font.PLAIN, 10));
			add(c);
		} else {
			setLayout(new GridLayout(1, 1));
			add(b);
		}
		Node.setToolkit(getToolkit());
		if (!isApp_) {
			Node.setContext(getCodeBase());
			GraphWindow.setContext(getCodeBase());
		}
		validate();
		setVisible(true);
	}


	/**
	 *  Description of the Method
	 *
	 * @return    Description of the Return Value
	 */
	private GraphWindow buildNewTCSWindow() {

		// Bring up an undirected graph editor window.
		// The parameter to GraphWindow() indicates directed
		// or undirected.
		GraphWindow graph_editing_window = new GraphWindow(true);
		TreeAlgorithm talg = new TreeAlgorithm('d');
		graph_editing_window.addAlgorithm(talg, "Tree Down");
		talg = new TreeAlgorithm('u');
		graph_editing_window.addAlgorithm(talg, "Tree Up");
		talg = new TreeAlgorithm('l');
		graph_editing_window.addAlgorithm(talg, "Tree Left");
		talg = new TreeAlgorithm('r');
		graph_editing_window.addAlgorithm(talg, "Tree Right");
		Spring spring = new Spring();
		graph_editing_window.addAlgorithm(spring, "Spring tree");
		/*
	   graph_editing_window.addAlgorithmMenu("CGD");
		   CGDAlgorithm calg = new CGDAlgorithm();
	   graph_editing_window.addAlgorithm(calg, "CGD", "CGD");
	   calg = new CGDAlgorithm(true);
	   graph_editing_window.addAlgorithm(calg, "CGD",
		  "show CGD parse tree");
	   graph_editing_window.addAlgorithmMenu("Biconnectivity");
	   BiconnectGraph make_biconnect = new BiconnectGraph(true);
	   graph_editing_window.addAlgorithm (make_biconnect,
		  "Biconnectivity", "Remove Articulation Points");
	   BiconnectGraph check_biconnect = new BiconnectGraph(false);
	   graph_editing_window.addAlgorithm (check_biconnect,
		  "Biconnectivity", "Find Articulation Points");
*/
		/*if (System.getProperty("os.name").startsWith("Mac OS"))
		JOptionPane.showMessageDialog(this,"this is a mac",
	                "TCS info", JOptionPane.WARNING_MESSAGE);	*/
		if (System.getProperty("os.name").startsWith("Mac OS")) {
			TCS.currentOS = "macintosh";
		} else if (System.getProperty("os.name").startsWith("Windows")) {
			TCS.currentOS = "windows";
		} else {
			TCS.currentOS = "unix";
		}

		if (appCount_++ == 0) {
			graph_editing_window.setTitle("TCS " +
					TCS.VERSION +
					"     (Java Version: " +
					System.getProperty("java.version") +
					" from " +
					System.getProperty("java.vendor") +
					")" +
					" -- " + System.getProperty("os.name") +
					" " + System.getProperty("os.version"));
		} else {
			graph_editing_window.setTitle("TCS " +
					TCS.VERSION +
					":" +
					appCount_ +
					"     (Java Version: " +
					System.getProperty("java.version") +
					" from " +
					System.getProperty("java.vendor") +
					")" +
					" -- " + System.getProperty("os.name") +
					" " + System.getProperty("os.version"));
		}

		graph_editing_window.setResizable(true);
		graph_editing_window.pack();
		graph_editing_window.setVisible(true);

		return graph_editing_window;
	}


	/**
	 *  The main program for the VGJ class
	 *
	 * @param  args  The command line arguments
	 */
	public static void main(String[] args) {

		/*
		    JOptionPane.showMessageDialog(aframe,"TCS is a program for the estimation of haplotype cladograms." +
		    "\nWritten by Mark Clement and David Posada" +
		    "\nDepartments of Computer Science (MC) and Zoology (DP), " +
		    "\nBrigham Young University, Provo, Utah, USA." +
		    "\n\nTCS uses the freeware VGJ (see docs)" +
		    "\n\nPLEASE REALIZE THAT THIS IS AN ALPHA VERSION." +
		    "\nTHIS MEANS THAT BUGS ARE LIKELY..." +
		    "\nIT IS RECOMMEND THAT YOU CHECK THE RESULTS",
		    "TCS " + VERSION ,  JOptionPane.INFORMATION_MESSAGE,openIcon);
		  */
		/* Look and feel [DP]*/
		try {
			//UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			UIManager.getSystemLookAndFeelClassName();
		} catch (Exception e) {}

		// String directoryName;

		if (args.length != 0) {
			runCommandLineVersion(args);
		} else {

			//CheckExpiration (frame);
			JFrame frame = new JFrame("TCS" + TCS.VERSION);
			VGJ vgj = new VGJ();
			vgj.isApp_ = true;
			vgj.init();
			vgj.start();

			//vgj.buildWindow_();
			frame.getContentPane().add("Center", vgj);
			frame.setResizable(true);
			//frame.pack();
			frame.setSize(190, 120);
			frame.validate();
			frame.setVisible(true);
		}
	}


	/* DP added this in case we want the program to expire */
	/**
	 *  Description of the Method
	 *
	 * @param  theframe  Description of Parameter
	 */
	public static void CheckExpiration(Frame theframe) {
		java.util.Calendar now = java.util.Calendar.getInstance();

		if ((now.get(now.MONTH) != now.JUNE && now.get(now.MONTH) != now.JULY) || now.get(now.YEAR) != 2003) {
			JOptionPane.showMessageDialog(theframe, "Program has expired! \n    Bye...",
					"TCS warning", JOptionPane.WARNING_MESSAGE);
			theframe.dispose();
			System.exit(0);
		}
	}


	/**
	 *  Run's the command line version of TCS (for batch runs)
	 *
	 * @param  args  The arguments passed from the command line
	 */
	private static void runCommandLineVersion(String[] args) {
		boolean saveStandard = false;
		boolean saveAsPict = false;
		String fileToRun = args[0];
		if (fileToRun.equals("-toPict")) {
			saveAsPict = true;
			fileToRun = args[1];

		} else {
			if (fileToRun.equals("-saveStand")) {
				saveStandard = true;
				fileToRun = args[1];

			}
		}

		File sourceDirectory = new File(fileToRun);

		File[] files = null;
		GraphWindow gw = null;
		if (!saveAsPict) {
			// System.out.println("Will use phylip files");
			System.out.println("Will use phylip and graph files");
			files = filterFiles(sourceDirectory, ".phylip");
		} else {
			System.out.println("Will use graph files");
			files = filterFiles(sourceDirectory, ".graph");

		}
		gw = buildWindow_();
		gw.hide();

		// do this for each file...
		for (int x = 0; x < files.length; x++) {
			String file = sourceDirectory.getAbsolutePath() + "/" + files[x].getName();
			System.out.println("TCS processing: " + file);
			if (saveAsPict) {
				gw.pictSave(file);

			} else {
				if (saveStandard) {
					gw.saveStandard(file);
				} else {
					int maxdist = 0;
					//here I need to use the input parameters if there are any...
					if (args.length > 1) {
						String nextArg = args[1];
						maxdist = Integer.parseInt(nextArg);
					}

					buildWindow_(file, gw, maxdist);
				}
			}

		}
		System.exit(0);
	}


	/**
	 *  Capture all sourceDirectory files matching the filter criteria.
	 *
	 * @param  sourceDirectory  Description of the Parameter
	 * @param  suffix           Description of the Parameter
	 * @return                  Description of the Return Value
	 */
	private static File[] filterFiles(File sourceDirectory, String suffix) {

		//get a list of all files in the source directory
		String[] allFileNames = sourceDirectory.list();

		//filter the files
		Vector filteredFiles = new Vector();
		for (int i = 0; i < allFileNames.length; i++) {
			File file = new File(sourceDirectory, allFileNames[i]);

			if (file.isFile() && file.canRead() && file.getName().endsWith(suffix)) {
				filteredFiles.addElement(file);
			}
		}

		//put the filtered files in an array for more convenient access later
		File[] files = new File[filteredFiles.size()];
		for (int i = 0; i < filteredFiles.size(); i++) {
			files[i] = (File)filteredFiles.elementAt(i);
		}

		return files;
	}



	/**
	 *  perpares the graphWindow for running TCS with command line only
	 *
	 * @param  nextFile  Description of the Parameter
	 * @param  gw        Description of the Parameter
	 * @param  maxdist   Description of the Parameter
	 */
	private static void buildWindow_(String nextFile, GraphWindow gw, int maxdist) {
		gw.open(nextFile);
		if (maxdist > 0) {
			gw.setMaxDistance(maxdist);
		}
		gw.runTCS();

		gw.saveStandard(nextFile + ".graph");

		gw.dispose();
	}


	/**
	 *  Brings up a TCS graph editor window.
	 *
	 * @return    GraphWindow created.
	 */
	private static GraphWindow buildWindow_() {

		GraphWindow graph_editing_window = new GraphWindow(true);

		TreeAlgorithm talg = new TreeAlgorithm('d');
		graph_editing_window.addAlgorithm(talg, "Tree Down");
		talg = new TreeAlgorithm('u');
		graph_editing_window.addAlgorithm(talg, "Tree Up");
		talg = new TreeAlgorithm('l');
		graph_editing_window.addAlgorithm(talg, "Tree Left");
		talg = new TreeAlgorithm('r');
		graph_editing_window.addAlgorithm(talg, "Tree Right");

		graph_editing_window.setResizable(true);
		graph_editing_window.pack();
		graph_editing_window.setVisible(true);

		return graph_editing_window;
	}

}
