package clad;

/*created Nov 09, 2000
 *by Jake Derington
 */

public class NameList
{

	public int groupID;  //the id of last group the nodewas placed in
	public int indivs;  //the # of individuals in each node
	public int inter;  //0 = node 1 = intermediate

	//this class is used for a list of names
	public String name;  //the name of a node
	public int pos;  //pos if dna taxa in taxa list


	public NameList(String nm, int gr, int ind, int in, int p)
	{
		name = nm;
		groupID = gr;
		indivs = ind;
		inter = in;
		pos = p;
	}
}
