package EDU.auburn.VGJ.gui;

/*
    File: ScrolledPanel.java
    5/29/96  Larry Barowski
  */

import java.awt.Dimension;
import java.awt.Scrollbar;
import java.awt.Event;
import java.awt.Label;

import EDU.auburn.VGJ.util.DDimension;
import EDU.auburn.VGJ.util.DPoint;


/**
 *	This is a panel that contains an OffsetCanvas, a Label, and two Scrollbars.<br>
 *	When the OffsetCanvas is resized, the Scrollars will adjust so
 *	that the image can be completely viewed, and no more (you can
 *	scroll just to the edge of the image). If the OffsetCanvas is
 *	larger than the image, the image gets centered inside it.<br>
 *	the Label displays the current cursor location in the OffsetCanvas, relative
 *	to the contents of the OffsetCanvas.
 *	</p>Here is the <a href="../gui/ScrolledPanel.java">source</a>.
 *
 * @author     Larry Barowski
 * @created    June 5, 2003
 * @see        OffsetCanvas
 */
public class ScrolledPanel extends LPanel
{

	/**
	 *	Event id for size changes.
	 */
	public static int RESIZE            = 49900;

	/**
	 *	Event id for offsetChange (scroll);
	 */
	public static int OFFSET            = 49901;


	public static Scrollbar vscroll_, hscroll_;
	private OffsetCanvas offsetCanvas_;
	private Label label_;

	// Paging a scrollbar will move the image by this fraction of the
	// window size - default is 1/4.
	private double pageFraction_        = 0.25;

	private double xoffset_             = 0.0, yoffset_             = 0.0;

	private DDimension win_size;
	private DDimension img_size;

	private DragFix dragFix_;




	/**
	 *	Create a ScrolledPanel.
	 *
	 * @param  offsetCanvas  the OffsetCanvas that will be scrolled within the created panel.
	 */
	public ScrolledPanel(OffsetCanvas offsetCanvas)
	{
		super();

		dragFix_ = new DragFix(this);

		offsetCanvas_ = offsetCanvas;

		spacing = 0;
		constraints.insets.top = constraints.insets.bottom = 0;
		label_ = addLabel("", 0, -1, 1.0, 0.0, 1, 0);
		addComponent(offsetCanvas_, 1, 0, 1.0, 1.0, 3, 0);
		vscroll_ = new Scrollbar(Scrollbar.VERTICAL);
		addComponent(vscroll_, 0, 0, 0.0, 1.0, 2, 0);
		hscroll_ = new Scrollbar(Scrollbar.HORIZONTAL);
		addComponent(hscroll_, 1, -1, 1.0, 0.0, 1, 0);

		finish();
	}


	public void center()
	{
		hscroll_.setValue(hscroll_.getMaximum() / 2);
		vscroll_.setValue(vscroll_.getMaximum() / 2);

		configure();

		// post an event indicating a scroll
		getParent().postEvent(new Event((Object) this, OFFSET, (Object) this));
	}

	// Calculate new Scrollbar values, maximums, and sizes, and adjust the

	// offset of the OffsetCanvas if necessary.

	private void configure()
	{
		double hmax;
		double vmax;
		double pageh;
		double pagew;

		Dimension tmpD  = offsetCanvas_.size();

		win_size = new DDimension(tmpD.width, tmpD.height);
		img_size = offsetCanvas_.contentsSize();

		// offsetCanvas_ controls the new offset as well as the size, so it
		// can remain stationary in an OffsetCanvas-dependent way.
		DPoint tmpdim   = offsetCanvas_.getOffset();

		xoffset_ = tmpdim.x;
		yoffset_ = tmpdim.y;


		// Scrolls can move an amount of image size - window size (so the whole image
		// can be seen by scrolling). If window is larger than image, no scrolling
		// is allowed.

		hmax = Math.max(img_size.width - win_size.width, 0.0);
		vmax = Math.max(img_size.height - win_size.height, 0.0);

		// Scroll positions remain the same unless they are now out of range.
		if (xoffset_ > (double) hmax)
		{
			xoffset_ = (double) hmax;
		}
		if (yoffset_ > (double) vmax)
		{
			yoffset_ = (double) vmax;
		}

		// Meaning of these values is different on SPARC and PC. Only way to make them close
		// to the same is to make visible equal to one.

		// On a SPARC, maximum is the maximum value of a Scrollbar.
		// On a PC, maximum - slider size is the maximum value.

		// With this setup, we can scroll one extra pixel on a SPARC (but feedback from
		// offsetCanvas_.setOffsets() can correct this), and the right amount on a PC.

		//	hscroll_.setValues(xoffset_, Math.min(win_size.width, img_size.width), 0, hmax);
		//	vscroll_.setValues(yoffset_, Math.min(win_size.height, img_size.height), 0, vmax);

		hscroll_.setValues((int) xoffset_, 1, 0, (int) hmax + 1);
		vscroll_.setValues((int) yoffset_, 1, 0, (int) vmax + 1);

		// page increment is pageFraction_ of screen size but at least one
		pageh = Math.max((win_size.width * pageFraction_), 1.0);
		pagew = Math.max((win_size.height * pageFraction_), 1.0);
		hscroll_.setBlockIncrement((int) pageh);
		vscroll_.setBlockIncrement((int) pagew);

		// Offsets will be equal to scroll amounts, unless the window is larger than the
		// image, in which case the image is centered in the window.
		if (win_size.width > img_size.width || win_size.height > img_size.height)
		{
			if (win_size.width > img_size.width)
			{
				xoffset_ = -(win_size.width - img_size.width + 1.0) / 2.0;
			}
			if (win_size.height > img_size.height)
			{
				yoffset_ = -(win_size.height - img_size.height + 1.0) / 2.0;
			}
		}

		// For some reason, a redraw here will cause an infinite loop. the size of
		// offsetCanvas_ must be unstable during a resize.
		offsetCanvas_.setOffsets(xoffset_, yoffset_, false);
	}


	public DDimension getContentSize()
	{
		return new DDimension(img_size.width, img_size.height);
	}


	public DDimension getOffset()
	{
		return new DDimension(xoffset_, yoffset_);
	}


	public DDimension getPortSize()
	{
		return new DDimension(win_size.width, win_size.height);
	}


	public boolean handleEvent(Event e)
	{
		if (e.id == DragFix.QUEUED)
		{
		    
			Event event  = (Event) e.arg;
			if (event.target instanceof Scrollbar &&
					(event.id == Event.SCROLL_ABSOLUTE ||
					event.id == Event.SCROLL_LINE_DOWN ||
					event.id == Event.SCROLL_LINE_UP ||
					event.id == Event.SCROLL_PAGE_DOWN ||
					event.id == Event.SCROLL_PAGE_UP ||
					event.id == Event.SCROLL_END))
			{
			    
				// Scrollbar event
				// Adjust the offset of the OffsetCanvas

				xoffset_ = (double) hscroll_.getValue();
				yoffset_ = (double) vscroll_.getValue();
				offsetCanvas_.setOffsets(xoffset_, yoffset_, true);

				// post an event indicating a scroll
				getParent().postEvent(new Event((Object) this, OFFSET, (Object) this));

				return true;
			}
			else if (event.target instanceof OffsetCanvas)
			{
				if (event.id == OffsetCanvas.RESIZE)
				{
					// RESIZE event from canvas.

					configure();

					// post an event indicating a size change
					getParent().postEvent(new Event((Object) this, RESIZE, (Object) this));
					return true;
				}
				else if (event.id == OffsetCanvas.LABEL)
				{
					String string  = (String) event.arg;

					label_.setText(string);
				}
			}
			super.handleEvent((Event) e.arg);
			getParent().postEvent((Event) e.arg);
			return true;
		}

		dragFix_.queueEvent(e);
		return true;
	}


	public synchronized void removeNotify()
	{
		dragFix_.killThread();
		super.removeNotify();
	}


	/**
	 *	Scroll to any given location.
	 *
	 * @param  x  horizontal offset to scroll to
	 * @param  y  vertical offset to scroll to
	 */
	public void scrollTo(double x, double y)
	{
		if (x < 0.0)
		{
			x = 0.0;
		}
		if (y < 0.0)
		{
			y = 0.0;
		}

		if (x > (double) hscroll_.getMaximum())
		{
			x = (double) hscroll_.getMaximum();
		}
		if (y > (double) vscroll_.getMaximum())
		{
			y = (double) vscroll_.getMaximum();
		}

		xoffset_ = x;
		yoffset_ = y;

		hscroll_.setValue((int) x);
		vscroll_.setValue((int) y);

		offsetCanvas_.setOffsets(x, y, true);
	}


	/**
	 *	Set the amount of paging for the Scrollbars.
	 *
	 * @param  fraction  fraction of window size that the contents will move when "paged" -
	 * the default is 1/4
	 */
	public void setPageFraction(double fraction)
	{
		pageFraction_ = fraction;
	}
}
