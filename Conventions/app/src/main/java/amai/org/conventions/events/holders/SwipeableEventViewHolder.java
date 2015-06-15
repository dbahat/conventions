package amai.org.conventions.events.holders;

import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.events.EventView;
import amai.org.conventions.events.adapters.ListPagerAdapter;
import amai.org.conventions.events.listeners.OnSwipeListener;
import amai.org.conventions.model.ConventionEvent;

/**
 * ViewHolder for an event view that allow swipe to add/remove from favorites
 */
public class SwipeableEventViewHolder extends RecyclerView.ViewHolder {

    private ViewPager viewPager;
    private EventView mainEventView;
    private EventView hiddenEventView;
	private EventView hiddenEventView2;

	private ViewPager.OnPageChangeListener listener;
	private ConventionEvent event;

	private int mainViewPosition = 0;
	private boolean dismiss;

	public SwipeableEventViewHolder(View itemView, boolean dismiss) {
        super(itemView);
		this.dismiss = dismiss;

		viewPager = (ViewPager) itemView.findViewById(R.id.swipe_pager);
		mainEventView = (EventView) itemView.findViewById(R.id.main_event_view);
        hiddenEventView = (EventView) itemView.findViewById(R.id.hidden_event_view);
		hiddenEventView2 = (EventView) itemView.findViewById(R.id.hidden_event_view2);

		if (dismiss) {
			hiddenEventView.setVisibility(View.GONE);
			hiddenEventView2.setVisibility(View.GONE);
		}

		List<View> views = new ArrayList<>(3);
        views.add(hiddenEventView);
	    views.add(mainEventView);
		views.add(hiddenEventView2);

        mainViewPosition = 1;
        viewPager.setCurrentItem(mainViewPosition, false);
		viewPager.setOffscreenPageLimit(views.size() - 1);
	    viewPager.setAdapter(new ListPagerAdapter(views));
    }

	public ConventionEvent getModel() {
		return event;
	}

	public void setModel(ConventionEvent event) {
        setModel(event, false);
    }

    public void setModel(ConventionEvent event, boolean conflicting) {
	    this.reset();

	    this.event = event;
	    setEventInEventView(mainEventView, event, conflicting);

	    if (!dismiss) {
	        setEventInEventView(hiddenEventView, event, conflicting);
	        setEventInEventView(hiddenEventView2, event, conflicting);

	        // Set the hidden event views to have the opposite attending icon state, so that swiping will
	        // feel like changing the attending state.
	        hiddenEventView.setAttending(!event.isAttending());
	        hiddenEventView2.setAttending(!event.isAttending());
	    }
    }

	private void setEventInEventView(EventView view, ConventionEvent event, boolean conflicting) {
		view.setShowFavoriteIcon(true);
		view.setShowHallName(true);
		view.setConflicting(conflicting);
		view.setEvent(event);
	}

	public void setOnViewSwipedAction(final Runnable action) {
	    removeOnPageChangeListener();

	    this.listener = new OnSwipeListener(viewPager, mainViewPosition, action, dismiss);
	    viewPager.addOnPageChangeListener(listener);
    }

    public void reset() {
	    removeOnPageChangeListener();
	    viewPager.setCurrentItem(mainViewPosition, false);
    }

	private void removeOnPageChangeListener() {
		if (listener != null) {
		    viewPager.removeOnPageChangeListener(listener);
		}
	}

}
