package amai.org.conventions.events.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;

import amai.org.conventions.R;
import amai.org.conventions.events.EventView;
import amai.org.conventions.model.ConventionEvent;

/**
 * ViewHolder for an event view that allow swipe to remove from favorites
 */
public class DismissibleEventViewHolder extends RecyclerView.ViewHolder {

    private EventView mainEventView;
    private SwipeLayout swipeLayout;

    private SimpleSwipeListener listener;
	private ConventionEvent event;

    public DismissibleEventViewHolder(View itemView) {
        super(itemView);
        swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
        mainEventView = (EventView) itemView.findViewById(R.id.dismissible_event);
    }

	public void setModel(ConventionEvent event) {
        setModel(event, false);
    }

    public void setModel(ConventionEvent event, boolean conflicting) {
	    this.reset();
	    this.event = event;
        mainEventView.setEvent(event);
        mainEventView.setShowFavoriteIcon(true);
        mainEventView.setShowHallName(true);
        mainEventView.setConflicting(conflicting);
    }

    public void addOnSwipeListener(SimpleSwipeListener listener) {
        this.listener = listener;
        swipeLayout.addSwipeListener(listener);
    }

	public ConventionEvent getModel() {
		return event;
	}

    public void reset() {
	    this.event = null;
        if (listener != null) {
            swipeLayout.removeSwipeListener(listener);
        }
        swipeLayout.close(false, true);
    }
}
