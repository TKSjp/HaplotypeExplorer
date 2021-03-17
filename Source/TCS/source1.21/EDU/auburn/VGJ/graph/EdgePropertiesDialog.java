package EDU.auburn.VGJ.graph;

/*
 * File: EdgePropertiesDialog.java
 *
 * 4/11/97   Larry Barowski
 *
*/
import java.awt.Dialog;
import java.awt.Button;
import java.awt.Frame;
import java.awt.Event;
import java.awt.TextField;
import java.awt.TextArea;
import java.awt.Color;
import java.awt.Component;
import java.awt.Choice;
import java.awt.Panel;
import java.util.StringTokenizer;
import java.util.Hashtable;
import java.util.Enumeration;

import EDU.auburn.VGJ.util.DPoint3;
import EDU.auburn.VGJ.util.DDimension;
import EDU.auburn.VGJ.util.DDimension3;
import EDU.auburn.VGJ.gui.MessageDialog;
import EDU.auburn.VGJ.gui.LPanel;
import EDU.auburn.VGJ.gui.InputDialog;


/**
 * A dialog class for changing the properties of an edge. If the edge
 * is null, the dialog is modeless and does not have fields for
 * changing properties, such as label, without defaults. Otherwise, the
 * dialog is modal.
 *	</p>Here is the <a href="../graph/EdgePropertiesDialog.java">source</a>.
 *
 * @author     Larry Barowski
 * @created    July 2, 2003
 */

public class EdgePropertiesDialog extends Dialog
{
	private TextField labelText_;
	private TextArea pointsText_, dataText_;
	private Panel dataPanel_;

	private Edge edge_;
	private Graph graph_;
	private Component[] notDefault_  = new Component[3];
	private int ndCount_             = 0;
	private Frame frame_;
	private Choice style_, data_;
	private Hashtable dataHash_;
	private String currentData_;


	public EdgePropertiesDialog(Frame frame, Edge edge_in, Graph graph_in)
	{
		super(frame, "", true);
		frame_ = frame;

		graph_ = graph_in;
		edge_ = edge_in;
		//  if(edge_ == null)
		//     edge_ = Edge.defaults;

		LPanel p  = new LPanel();
		p.constraints.weighty = 0.0;
		notDefault_[ndCount_++] = p.addLineLabel("Label:", 1);
		labelText_ = p.addTextField(8, 0, -1, 1.0, 0.0, 1, 1);

		p.addLineLabel("Line Style", 1);
		style_ = new Choice();
		for (int i = 0; i < Edge.styleLabels.length; i++)
		{
			style_.addItem(Edge.styleLabels[i]);
		}
		p.addComponent(style_, 0, -1, 1.0, 0.0, 0, 1);

		p.addLineLabel("Points in order x y z:", 0);

		pointsText_ = new TextArea(4, 20);
		pointsText_.setBackground(Color.white);
		p.addComponent(pointsText_, 0, -1, 1.0, 1.0, 3, 0);

		p.constraints.gridwidth = 1;
		p.constraints.weightx = 0.0;

		p.addLineLabel("Data", 1);
		data_ = new Choice();
		dataPanel_ = new Panel();
		dataPanel_.add(data_);
		p.addComponent(dataPanel_, 0, -1, 1.0, 0.0, 0, 1);

		dataText_ = new TextArea(4, 20);
		dataText_.setBackground(Color.white);
		p.constraints.insets.top = 0;
		p.addComponent(dataText_, 0, -1, 1.0, 1.0, 3, 0);

		p.addButtonPanel("Apply Cancel", 0);

		p.finish();
		add("Center", p);
		setEdge(edge_, graph_);
	}


	public boolean action(Event event, Object object)
	{
		if (event.target instanceof Button)
		{
			if ("Apply".equals(object))
			{
				if (setValues_())
				{
					hide();
				}
				return true;
			}
			else if ("Cancel".equals(object))
			{
				hide();
				return true;
			}
		}
		else if (event.target == data_)
		{
			dataHash_.put(currentData_, dataText_.getText());

			String old_data  = currentData_;
			currentData_ = data_.getSelectedItem();
			if (!currentData_.equals("<NEW>"))
			{
				String value  = (String) dataHash_.get(currentData_);
				if (value == null)
				{
					value = new String("");
				}
				dataText_.setText(value);
			}
			else
			{
				new InputDialog(frame_, "Enter name for new data item " + "(must be a letter followed by letters and numbers).", this, 9999);
				if (data_.getSelectedItem().equals("<NEW>"))
				{
					data_.select(old_data);
				}
			}
		}

		return false;
	}


	public boolean handleEvent(Event event)
	{
		// Avoid having everything destroyed.
		if (event.target instanceof InputDialog)
		{
			String label  = (String) event.arg;

			for (int i = 0; i < label.length(); i++)
			{
				char chr  = label.charAt(i);
				if (!(chr >= 'a' && chr <= 'z') && !(chr >= 'A' && chr <= 'Z') && !(i != 0 && chr >= '0' && chr <= '9'))
				{
					new MessageDialog(frame_, "Error", "Bad format for new data item name.", true);
					return true;
				}
			}

			data_.addItem(label);
			data_.select(data_.countItems() - 1);
			currentData_ = label;
			dataText_.setText("");
			return true;
		}

		if (event.id == Event.WINDOW_DESTROY)
		{
			hide();
			return true;
		}
		return super.handleEvent(event);
	}


	public void setEdge(Edge edge_in, Graph graph_in)
	{
		graph_ = graph_in;
		edge_ = edge_in;
		//  if(edge_ == null)
		//     edge_ = Edge.defaults;

		String title;
		//  if(edge_ == Edge.defaults)
		//     title = "Properties For Newly Created Edges";
		//  else
		title = "Edge " + edge_.tail().id_ + " " + edge_.head().id_;

		setTitle(title);

		style_.select(edge_.getLineStyle());

		//   if(edge_ != Edge.defaults)
		//   {
		labelText_.setText(edge_.getLabel());
		String points_string  = new String();
		DPoint3[] points      = edge_.points();
		for (int i = 0; i < points.length; i++)
		{
			points_string += points[i].x + " " + points[i].y + " " + points[i].z + "\n";
		}
		/*
		//FIXME a hack to display actual seq differences...
		String tailseq = edge_.tail().getSequence();
		String headseq = edge_.head().getSequence();
//		System.out.println("I am here! " + tailseq + headseq);
		if (tailseq.length() == headseq.length()) {
				
				for (int i = 0; i < tailseq.length(); i++) {
						if (tailseq.charAt(i) != headseq.charAt(i)) {
								points_string +=   (i+1) + " ";
						}
				}
		}
		 
		System.out.println(points_string);
		 */
		pointsText_.setText(points_string);

		// Can't remove items from a Choice in Java 1.0.
		dataPanel_.remove(data_);
		data_ = new Choice();
		dataPanel_.add(data_);
		dataHash_ = (Hashtable) edge_.data_.clone();
		data_.addItem("<NEW>");
		for (Enumeration keys = dataHash_.keys(); keys.hasMoreElements(); )
		{
			String key  = (String) keys.nextElement();
			data_.addItem(key);
		}
		if (data_.countItems() == 1)
		{  // Need at least one item.
			data_.addItem("Data");
		}
		data_.select(1);
		currentData_ = data_.getItem(1);

		String value          = (String) dataHash_.get(currentData_);
		if (value == null)
		{
			value = new String("");
		}
		dataText_.setText(value);

		for (int i = 0; i < ndCount_; i++)
		{
			notDefault_[i].setVisible(true);

		}
		//  }
		//  else
		//     for(int i = 0; i < ndCount_; i++)
		//        notDefault_[i].hide();

		pack();
	}


	private boolean setValues_()
	{
		//   if(edge_ != Edge.defaults)
		//   {
		String points_string       = pointsText_.getText();

		// Scan the points string.
		StringTokenizer tokenizer  = new StringTokenizer(points_string, " \t\n\r,", false);
		int num_tokens             = tokenizer.countTokens();
		a :
		if ((num_tokens % 3) != 0)
		{
			new MessageDialog(frame_, "Error", "Expecting " + "sets of three numbers for points", true);
		}
		else
		{
			int count         = num_tokens / 3;
			DPoint3[] points  = new DPoint3[count];

			for (int i = 0; i < count; i++)
			{
				points[i] = new DPoint3();
			}

			String token;
			int index         = 0;
			double val;
			while (tokenizer.hasMoreTokens())
			{
				for (int element = 0; element < 3; element++)
				{
					token = tokenizer.nextToken();
					try
					{
						val = new Double(token).doubleValue();
					}
					catch (Exception e)
					{
						char el  = 'X';
						if (element == 1)
						{
							el = 'Y';
						}
						if (element == 2)
						{
							el = 'Z';
						}
						new MessageDialog(frame_, "Error", "Element " + el + " of point " + index + " is not" + " a number.", true);
						break a;
					}

					if (element == 0)
					{
						points[index].x = val;
					}
					else if (element == 1)
					{
						points[index].y = val;
					}
					else
					{
						points[index].z = val;
					}
				}
				index++;
			}

			dataHash_.put(currentData_, dataText_.getText());

			for (Enumeration keys = dataHash_.keys(); keys.hasMoreElements(); )
			{
				String key    = (String) keys.nextElement();
				String value  = (String) dataHash_.get(key);
				if (value != null && value.length() != 0)
				{
					edge_.data_.put(key, value);
				}
			}

			// Replace the old edge.
			Edge edge         = new Edge(edge_.tail(), edge_.head(), points, false);
			graph_.insertEdge(edge);
			edge.setLabel(labelText_.getText());
			edge.setLineStyle(style_.getSelectedIndex());
			edge.data_ = edge_.data_;

			return true;
		}
		//   }

		return false;
	}
}
