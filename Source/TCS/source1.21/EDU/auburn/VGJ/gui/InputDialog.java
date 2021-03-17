package EDU.auburn.VGJ.gui;

/*
 * File: InputDialog.java
 *
 * 5/29/96   Larry Barowski
 *
*/

import java.awt.Dialog;
import java.awt.Button;
import java.awt.Frame;
import java.awt.Event;
import java.awt.TextField;
import java.awt.Component;


/**
 *  A dialog class for user input of a single string.
 *  </p>Here is the <a href="../gui/InputDialog.java">source</a>.
 *
 * @author     Larry Barowski
 * @created    July 3, 2003
 */

public class InputDialog extends Dialog
{
	private int event_id;

	private TextField text;
	private Component postTo_;



	/**
	 * @param  event_id_in  this event will be posted if the user chooses "OK".
	 * @param  frame        Description of the Parameter
	 * @param  title        Description of the Parameter
	 * @param  post_to      Description of the Parameter
	 */
	public InputDialog(Frame frame, String title, Component post_to, int event_id_in)
	{
		super(frame, "Input", true);

		event_id = event_id_in;
		postTo_ = post_to;

		LPanel p  = new LPanel();
		p.addLabel(title, 0, 0, 1.0, 1.0, 1, 0);
		text = p.addTextField(50, 0, 0, 1.0, 1.0, 1, 0);
		p.addButtonPanel("OK Cancel", 0);

		p.finish();
		add("Center", p);
		pack();
		show();
	}


	public boolean action(Event event, Object object)
	{
		if (event.target instanceof Button)
		{
			if ("OK".equals(object))
			{
				hide();
				dispose();
				postTo_.postEvent(new Event((Object) this, event_id, (Object) text.getText()));
				return true;
			}
			else if ("Cancel".equals(object))
			{
				hide();
				dispose();
				return true;
			}
		}
		return super.action(event, object);
	}
}
