package EDU.auburn.VGJ.gui;

/*
  Graph File Viewer & Editing Utility
	 Steven Hansen
	 CSE690
 */

import java.awt.Frame;
import java.awt.Event;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Panel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Button;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Menu;
import java.awt.Color;
import java.awt.Label;
import java.awt.Component;
import java.io.StringBufferInputStream;
import java.io.IOException;
import java.lang.NumberFormatException;
import EDU.auburn.VGJ.graph.Graph;
import EDU.auburn.VGJ.graph.GMLobject;
import EDU.auburn.VGJ.graph.GMLlexer;
import EDU.auburn.VGJ.algorithm.GraphUpdate;
import EDU.auburn.VGJ.graph.ParseError;


/**
 *	GraphEdit is a text editing window for Graphs
 *	</p>Here is the <a href="../gui/GraphEdit.java">source</a>.
 *
 * @author     Steven Hansen
 * @created    July 2, 2003
 */

public class GraphEdit extends Frame
{
	private static TextArea textarea_;
	private static String originalGraphSpec_;
	private static String lastaction          = "";

	private Graph graph_;
	private GraphUpdate update_;
	private TextField text1, text2;
	private Label label1, label2;
	private Button okButton, cancelButton;
	private boolean firstTime_                = true;


	public GraphEdit(Graph graph_in, GraphUpdate update_in)
	{

		setTitle("Text Edit " + update_in.getFrame().getTitle() + " (GML)");

		graph_ = graph_in;

		GMLobject GMLgraph  = new GMLobject("graph", GMLobject.GMLlist);
		graph_.setGMLvalues(GMLgraph);
		GMLgraph.prune();

		update_ = update_in;

		Panel topPanel      = new Panel();
		Panel bottomPanel   = new Panel();
		Panel centerPanel   = new Panel();
		setLayout(new BorderLayout());

		//Set up the menu bar.
		MenuBar mb          = new MenuBar();
		Menu m              = new Menu("Menu");
		m.add(new MenuItem("Apply Changes"));
		m.add(new MenuItem("-"));
		m.add(new MenuItem("Exit"));
		mb.add(m);
		Menu m2             = new Menu("Edit");
		m2.add(new MenuItem("GoToTop"));
		m2.add(new MenuItem("GoToBottom"));
		m2.add(new MenuItem("GoToLine"));
		m2.add(new MenuItem("Search"));
		m2.add(new MenuItem("Replace"));
		mb.add(m2);
		setMenuBar(mb);

		//add editing controls to the top of the window
		okButton = new Button("Apply");
		cancelButton = new Button("Done");
		text1 = new TextField(15);
		text1.setBackground(Color.white);
		text2 = new TextField(15);
		text2.setBackground(Color.white);
		label1 = new Label("Search for:");
		label2 = new Label("    Replace all with:");

		topPanel.add(label1);
		topPanel.add(text1);
		topPanel.add(label2);
		topPanel.add(text2);
		topPanel.add(okButton);
		topPanel.add(cancelButton);
		add("North", topPanel);

		//Add small things at the bottom of the window.
		bottomPanel.add(new Button("Apply Changes"));
		bottomPanel.add(new Button("Exit"));
		add("South", bottomPanel);

		//Add big things to the center area of the window.
		centerPanel.setLayout(new GridLayout(1, 2));
		Panel p             = new Panel();
		p.setLayout(new BorderLayout());
		p.add("North", new Label("Edit Graph Below; Press \"Apply Changes\" to update the graph.", Label.CENTER));
		originalGraphSpec_ = GMLgraph.toString(0);
		textarea_ = new TextArea(originalGraphSpec_, 24, 80);
		textarea_.setBackground(Color.white);
		p.add("Center", textarea_);
		centerPanel.add(p);
		add("Center", centerPanel);

		hideAll_();
	}


	public boolean action(Event event, Object arg)
	{
		if (((String) arg).equals("GoToTop"))
		{
			textarea_.select(0, 0);
			return true;
		}
		if (((String) arg).equals("GoToBottom"))
		{
			textarea_.select(textarea_.getText().length(), textarea_.getText().length());
			return true;
		}
		if (((String) arg).equals("GoToLine"))
		{
			hideAll_();
			buttonLabels_();
			label1.setVisible(true);
			label1.setText("Go To Line #:");
			text1.setVisible(true);
			okButton.setVisible(true);
			cancelButton.setVisible(true);
			lastaction = "Line";
			validate();
			pack();
			textarea_.requestFocus();
			return true;
		}
		if (((String) arg).equals("Search"))
		{
			hideAll_();
			buttonLabels_();
			label1.setVisible(true);
			label1.setText("Search for:");
			text1.setVisible(true);
			okButton.setVisible(true);
			cancelButton.setVisible(true);
			lastaction = "Search";
			validate();
			pack();
			text1.setText("");
			textarea_.requestFocus();
			return true;
		}
		if (((String) arg).equals("Replace"))
		{
			hideAll_();
			buttonLabels_();
			label1.setVisible(true);
			label1.setText("Search for:");
			text1.setVisible(true);
			label2.setVisible(true);
			text2.setVisible(true);
			okButton.setVisible(true);
			cancelButton.setVisible(true);
			lastaction = "Replace";
			validate();
			pack();
			text1.setText("");
			text2.setText("");
			textarea_.requestFocus();
			return true;
		}
		if (((String) arg).equals("Apply"))
		{
			if (lastaction.equals("Search"))
			{
				String text     = textarea_.getText();
				String lookfor  = text1.getText().trim();
				int start       = textarea_.getSelectionEnd();
				int where       = text.indexOf(lookfor, start);
				if (where == -1)
				{  // Wrap around.
					where = text.indexOf(lookfor);
				}
				if (where != -1)
				{
					textarea_.select(where, where + lookfor.length());
				}
			}
			else if (lastaction.equals("Replace"))
			{
				String lookfor   = text1.getText();
				int lookfor_len  = lookfor.length();
				if (lookfor_len > 0)
				{
					int where;
					String text          = textarea_.getText();
					String new_text      = "";
					String replace_with  = text2.getText();
					int prev_where       = 0;
					while ((where = text.indexOf(lookfor, prev_where)) != -1)
					{
						new_text += text.substring(prev_where, where);
						new_text += replace_with;
						prev_where = where + lookfor_len;
					}
					if (prev_where < text.length())
					{
						new_text += text.substring(prev_where, text.length());
					}
					textarea_.setText(new_text);
				}
			}
			else if (lastaction.equals("Line"))
			{
				int whichline;
				try
				{
					whichline = Integer.parseInt(text1.getText().trim());
				}
				catch (NumberFormatException e)
				{
					whichline = -1;
				}

				String text    = textarea_.getText();
				int start;
				int end;
				int count;

				start = -1;
				for (count = 0; count < whichline - 1; count++)
				{
					start = text.indexOf("\n", start + 1);
					if (start == -1)
					{
						break;
					}
				}

				if (start != -1 || whichline == 1)
				{
					end = text.indexOf("\n", start + 1);
					if (end == -1)
					{
						end = text.length() - 1;
					}
					textarea_.select(start + 1, end);
				}
				else if (start == -1 && whichline > 0)
				{
					end = text.length();
					start = text.lastIndexOf("\n", end - 1);
					if (start == -1)
					{
						start = 0;
					}
					textarea_.select(start + 1, end);
				}

			}

			textarea_.requestFocus();
			return true;
		}
		else if (((String) arg).equals("Done"))
		{
			hideAll_();
			lastaction = "";

			validate();
			pack();

			textarea_.requestFocus();
			return true;
		}
		else if (((String) arg).equals("Find"))
		{  // Find.

			String lookfor  = textarea_.getSelectedText();
			if (lookfor.length() > 0)
			{
				String text  = textarea_.getText();
				int pos      = textarea_.getSelectionStart();
				int where    = text.indexOf(lookfor, pos + 1);
				if (where == -1)
				{
					// Wrap around.
					where = text.indexOf(lookfor);
				}
				if (where == -1)
				{
					return false;
				}
				textarea_.select(where, where + lookfor.length());
			}
			textarea_.requestFocus();
		}
		else if (((String) arg).equals("Find Backwards"))
		{  // Find backwards.

			String lookfor  = textarea_.getSelectedText();
			if (lookfor.length() > 0)
			{
				String text  = textarea_.getText();
				int pos      = textarea_.getSelectionStart();
				int where    = text.lastIndexOf(lookfor, pos - 1);
				if (where == -1)
				{
					// Wrap around.
					where = text.lastIndexOf(lookfor, text.length() - 1);
				}
				if (where == -1)
				{
					return false;
				}
				textarea_.select(where, where + lookfor.length());
			}

			textarea_.requestFocus();
		}

		if (((String) arg).equals("Exit"))
		{
			dispose();
		}
		if (((String) arg).equals("Apply Changes"))
		{
			// perform a graph update here...need to pass text to graph object

			String text                     = textarea_.getText();
			StringBufferInputStream stream  = new StringBufferInputStream(text);
			GMLlexer lexer                  = new GMLlexer(stream);

			Graph newgraph                  = null;
			try
			{
				GMLobject GMLgraph  = new GMLobject(lexer, null);
				GMLobject GMLtmp;

				// If the GML doesn't contain a graph, assume it is a graph.
				GMLtmp = GMLgraph.getGMLSubObject("graph", GMLobject.GMLlist, false);
				if (GMLtmp != null)
				{
					GMLgraph = GMLtmp;
				}
				newgraph = new Graph(GMLgraph);

				graph_.copy(newgraph);
				update_.update(true);
			}
			catch (ParseError error)
			{
				MessageDialog dg  = new MessageDialog(this, "Error", error.getMessage() + " at line " + lexer.getLineNumber() + " at or near \"" + lexer.getStringval() + "\".", true);
				return true;
			}
			catch (IOException error)
			{
				MessageDialog dg  = new MessageDialog(this, "Error", error.getMessage(), true);
				return true;
			}
			textarea_.requestFocus();
		}
		return true;
	}


	private void buttonLabels_()
	{
		okButton.setLabel("Apply");
		cancelButton.setLabel("Done");
	}


	public boolean handleEvent(Event event)
	{
		if (firstTime_)
		{
			firstTime_ = false;
			textarea_.requestFocus();
		}
		if (event.id == Event.WINDOW_DESTROY)
		{
			dispose();
			return true;
		}
		return super.handleEvent(event);
	}


	private void hideAll_()
	{
		label1.hide();
		text1.hide();
		label2.hide();
		text2.hide();

		okButton.setLabel("Find");
		cancelButton.setLabel("Find Backwards");
	}
}
