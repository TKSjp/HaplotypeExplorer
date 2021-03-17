package EDU.auburn.VGJ.gui;

/*
 * File: TextOutDialog.java
 *
 * 12/10/96   Larry Barowski
 *
*/

import java.awt.Dialog;
import java.awt.Button;
import java.awt.Frame;
import java.awt.Event;
import java.awt.TextArea;
import java.awt.Color;


/**
 *  A dialog class for output of a single string.
 *  </p>Here is the <a href="../gui/TextOutDialog.java">source</a>.
 *
 * @author     Larry Barowski
 * @created    July 2, 2003
 */

public class TextOutDialog extends Dialog
{
	private TextArea text;



	public TextOutDialog(Frame frame, String title, String text_in, int rows, int columns, boolean modal)
	{
		super(frame, title, modal);
		Construct_(frame, title, text_in, rows, columns, modal);
	}


	public TextOutDialog(Frame frame, String title, String text_in, boolean modal)
	{
		super(frame, title, modal);

		int oldpos  = -1;

		int pos;
		int rows    = 0;
		int columns    = 0;
		while ((pos = text_in.indexOf('\n', oldpos + 1)) != -1)
		{
			if (pos - oldpos > columns)
			{
				columns = pos - oldpos;
			}
			rows++;
			oldpos = pos;
		}
		columns += 2;
		rows += 2;

		if (rows > 35)
		{
			rows = 35;
		}
		if (columns > 80)
		{
			columns = 80;
		}

		Construct_(frame, title, text_in, rows, columns, modal);
	}


	public boolean action(Event event, Object object)
	{
		if (event.target instanceof Button)
		{
			hide();
			dispose();
			return true;
		}
		return false;
	}


	private void Construct_(Frame frame, String title, String text_in, int rows, int columns, boolean modal)
	{
		LPanel p  = new LPanel();
		text = new TextArea(text_in, rows, columns);
		text.setEditable(false);
		text.setBackground(Color.white);
		p.addComponent(text, 0, 0, 1.0, 1.0, 3, 0);
		p.addButtonPanel("OK", 0);

		p.finish();
		add("Center", p);
		pack();
		show();
	}
}
