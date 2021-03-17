package EDU.auburn.VGJ.gui;

/*
	File: DragFix.java
	3/7/97    Larry Barowski
*/

import java.awt.Event;
import java.awt.Component;


/**
 *	Drag fix queues events and eliminates repeated mouse
 *      drag events and scrollbar events (most annoying on Win95).
 *      Be sure the Component doesn't post an event to itself
 *      after every mouse drag or scrollbar event, or this will do
 *      no good. I suggest getParent().postEvent() instead.</p>
 *
 *      The constructor for the Component that uses it should have
 *      (to be safe, as the first line):</p>
 *
 *      <PRE>dragFix_ = new DragFix(this);  // dragFix_ is a member variable.</PRE>
 *
 *
 *      <br><br>The handleEvent() function should look like this:</p>
 *
 *      <PRE>public boolean handleEvent(Event e)
 *      {
 *         if(e.id == DragFix.QUEUED)
 *         {
 *            deal with (Event)e.arg
 *            if necessary, super.handleEvent((Event)e.arg);
 *            if necessary, getParent().postEvent((Event)e.arg);
 *            return true;
 *         }
 *         dragFix_.queueEvent(e);
 *         return true;
 *      }</PRE>
 *
 *     <br><br>and removeNotify should look like this, to kill the thread
 *     immediately - otherwise it will be there (asleep) until
 *     finalize() gets called (if ever):</p>
 *
 *     <PRE>public synchronized void removeNotify()
 *     {
 *        dragFix_.killThread();
 *        super.removeNotify();
 *     }</pre></p><br><br>
 *
 *
 *
 *	</p>Here is the <a href="../gui/DragFix.java">source</a>.
 *
 * @author     Larry Barowski
 * @created    July 3, 2003
 */
public class DragFix implements Runnable
{  // More than enough, in most cases.
	private final static int eventQueueLimit_  = 20000;

	public final static int QUEUED             = 179583;
	private Component user_;
	private Thread thread_;

	private Event[] eventQueue_                = null;
	private int eventQueueHead_                = 0, eventQueueTail_                = 0;
	private int eventQueueSize_                = 20;
	private Object lock_                       = new Object();


	public DragFix(Component user)
	{
		user_ = user;
		eventQueue_ = new Event[eventQueueSize_ + 1];

		thread_ = new Thread(this);
		thread_.start();
	}


	protected void finalize()
	{
		killThread();
	}


	public void killThread()
	{
		thread_.stop();
	}


	/**
	 * Queue or dequeue an event.
	 *
	 * @param  e  Description of the Parameter
	 * @return    Description of the Return Value
	 */
	private Event Queue(Event e)
	{
		synchronized (lock_)
		{
			if (e == null)
			{  // Dequeue

				if (eventQueueHead_ != eventQueueTail_)
				{  // Not empty.

					eventQueueTail_ = (eventQueueTail_ + 1) % eventQueueSize_;
					return eventQueue_[eventQueueTail_];
				}
				return null;  // To indicate empty.
			}

			// Add to queue.
			if (eventQueueHead_ != eventQueueTail_ && e.target ==
					eventQueue_[eventQueueHead_].target)
			{
				int newid  = eventQueue_[eventQueueHead_].id;
				if ((e.id == Event.MOUSE_DRAG &&
						newid == Event.MOUSE_DRAG) ||
						((e.id == Event.SCROLL_ABSOLUTE ||
						e.id == Event.SCROLL_LINE_DOWN ||
						e.id == Event.SCROLL_LINE_UP ||
						e.id == Event.SCROLL_PAGE_DOWN ||
						e.id == Event.SCROLL_PAGE_UP) &&
						(newid == Event.SCROLL_ABSOLUTE ||
						newid == Event.SCROLL_LINE_DOWN ||
						newid == Event.SCROLL_LINE_UP ||
						newid == Event.SCROLL_PAGE_DOWN ||
						newid == Event.SCROLL_PAGE_UP)))
				{
					// Eliminate the previous mouse drag or scroll.
					// This assumes all scroll handling will use
					// absolute positions, instead of "clicks".
					eventQueue_[eventQueueHead_] = e;
					return null;
				}
			}

			eventQueue_[(eventQueueHead_ + 1) % eventQueueSize_] = e;
			eventQueueHead_ = (eventQueueHead_ + 1) % eventQueueSize_;

			lock_.notify();

			if ((eventQueueHead_ + 1) % eventQueueSize_ ==
					eventQueueTail_)
			{
				// Queue is full, grow it.
				int new_size       = eventQueueSize_ * 2;
				if (new_size > eventQueueLimit_)
				{
					throw new Error("DragFix event queue size limit " + "exceeded.");
				}

				Event[] new_queue  = new Event[new_size + 1];
				int i              = 0;
				int j;
				for (j = eventQueueTail_; j != eventQueueHead_;
						j = (j + 1) % eventQueueSize_, i++)
				{
					new_queue[i] = eventQueue_[j];
				}
				new_queue[i] = eventQueue_[j];

				eventQueue_ = new_queue;
				eventQueueHead_ = i;
				eventQueueTail_ = 0;
				eventQueueSize_ = new_size;
			}
			return null;
		}
	}


	/**
	 * Queue or ignore an event. Call this from handleEvent()
	 * and return true.
	 *
	 * @param  e  Description of the Parameter
	 */
	public void queueEvent(Event e)
	{
		Queue(e);
	}


	/**
	 * Process queued events.
	 */
	public synchronized void run()
	{
		Event e;
		while (true)
		{

			// Process events until the queue is empty.
			while ((e = Queue(null)) != null)
			{
				user_.postEvent(new Event(e, QUEUED, e));
			}

			// Now that it is empty, wait for the next event.
			synchronized (lock_)
			{
				try
				{
					lock_.wait();
				}
				catch (InterruptedException ex)
				{
				}
			}
		}
	}
}
