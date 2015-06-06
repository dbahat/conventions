package amai.org.conventions.events;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;

import amai.org.conventions.R;
import amai.org.conventions.model.ConventionEvent;

/**
 * ViewHolder for an event view that allow swipe to add/remove from favorites
 */
public class SwipeableEventViewHolder extends RecyclerView.ViewHolder {

    private EventView mainEventView;
    private EventView hiddenEventView;

    private SwipeLayout swipeLayout;
    private SimpleSwipeListener listener;


    public SwipeableEventViewHolder(View itemView) {
        super(itemView);

        swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
        mainEventView = (EventView) itemView.findViewById(R.id.main_layout);
        hiddenEventView = (EventView) itemView.findViewById(R.id.hidden_layout);
    }

    public void setModel(ConventionEvent event) {
        mainEventView.setEvent(event);
        mainEventView.setShowFavoriteIcon(true);
        mainEventView.setShowHallName(true);
        mainEventView.setConflicting(false);

        hiddenEventView.setEvent(event);
        hiddenEventView.setShowFavoriteIcon(true);
        hiddenEventView.setShowHallName(true);
        hiddenEventView.setConflicting(false);

        // Set the hidden swipe layout event to have the opposite attending icon state, so that swiping will
        // feel like changing the attending state.
        hiddenEventView.setAttending(!event.isAttending());
    }

    public void addOnSwipeListener(SimpleSwipeListener listener) {
        this.listener = listener;
        swipeLayout.addSwipeListener(listener);
    }

    public void reset() {
        if (listener != null) {
            swipeLayout.removeSwipeListener(listener);
        }
    }
}
