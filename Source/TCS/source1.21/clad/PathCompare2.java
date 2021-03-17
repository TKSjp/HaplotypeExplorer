
package clad;

import java.util.Comparator;
public class PathCompare2 implements Comparator {
		public int compare(Object o1, Object o2) throws ClassCastException {
				Path p1 = (Path)o1;
				Path p2 = (Path)o2;

				// sort by type, then by size
				return p2.type - p1.type;

		}

}
