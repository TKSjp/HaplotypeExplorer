package clad;


/**
 *  Logging help
 *
 * @author    [MC]
 */
public class Logger {
	private TextOutputStream logfile;

	private int printout = 1;


	/**
	 *  Sets the logFile attribute of the Logger object
	 *
	 * @param  logFileName  The new logFile value
	 */
	public void setLogFile(String logFileName) {

		logfile = new TextOutputStream(logFileName);
	}


	/**  Description of the Method */
	public void close() {
		logfile.close();
	}



	/**
	 *  Insert the method's description here. Creation date: (11/8/99 9:24:24 AM)
	 *
	 * @param  str  java.lang.String
	 */
	public void dprint(String str) {

		if (printout == 1) {

			if (TCS.debug != 0) {

				if (TCS.log) {
					logfile.print(str);
				} else {
					System.out.print(str);
				}
			}
		}
	}


	/**
	 *  Insert the method's description here. Creation date: (11/8/99 9:24:24 AM)
	 *
	 * @param  str    java.lang.String
	 * @param  level  Description of the Parameter
	 */
	public void dprint(String str, int level) {

		if (printout == 1) {

			if ((TCS.debug & level) != 0) {

				if (TCS.log) {
					logfile.print(str);
				} else {
					System.out.print(str);
				}
			}
		}
	}


	/**  Insert the method's description here. Creation date: (11/8/99 9:24:24 AM) */
	public void dprintln() {

		if (printout == 1) {

			if (TCS.debug != 0) {

				if (TCS.log) {
					logfile.println();
				} else {
					System.out.println();
				}
			}
		}
	}


	/**
	 *  Insert the method's description here. Creation date: (11/8/99 9:24:24 AM)
	 *
	 * @param  level  Description of the Parameter
	 */
	public void dprintln(int level) {

		if (printout == 1) {

			if ((TCS.debug & level) != 0) {

				if (TCS.log) {
					logfile.println();
				} else {
					System.out.println();
				}
			}
		}
	}


	/**
	 *  Insert the method's description here. Creation date: (11/8/99 9:24:24 AM)
	 *
	 * @param  str  java.lang.String
	 */
	public void dprintln(String str) {

		if (printout == 1) {

			if (TCS.debug != 0) {

				if (TCS.log) {
					logfile.println(str);
				} else {
					System.out.println(str);
				}
			}
		}
	}


	/**
	 *  Insert the method's description here. Creation date: (11/8/99 9:24:24 AM)
	 *
	 * @param  str    java.lang.String
	 * @param  level  Description of the Parameter
	 */
	public void dprintln(String str, int level) {

		if (printout == 1) {

			if ((TCS.debug & level) != 0) {

				if (TCS.log) {
					logfile.println(str);
				} else {
					System.out.println(str);
				}
			}
		}
	}

}
