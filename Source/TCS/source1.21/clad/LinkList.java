package clad;
// package EDU.auburn.VGJ.nesting;
/*created Nov 09, 2000
 *by Jake Derington
 *
*/
import java.util.Vector;
public class LinkList
{

	public Vector names;
	public Vector list;
	public int members;
	public int groupID;
	public int nextlevel;
	public int level;
	public int levelID;
	public int numindiv;
	public int nbors;
	public int confidence;

	public LinkList(int gr, int lev, int lid)
	{
		list = new Vector();  // holds the list of all sub-linkLists level 2 &up
		names = new Vector();  //names if all group members
		members = 0;  //the number of members in a group
		groupID = gr;  //unique group id same as location in list
		level = lev;  //the level of group 1,2,3
		levelID = lid;  //identifier of level .3
		nextlevel = 0;  //if the group has been included in
		//the next level set to 1
		nbors = 0;
		numindiv = 0;
	}

}
