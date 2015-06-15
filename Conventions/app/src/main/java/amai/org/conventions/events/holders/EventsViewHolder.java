package amai.org.conventions.events.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import amai.org.conventions.R;
import amai.org.conventions.events.EventView;
import amai.org.conventions.model.ConventionEvent;

public class EventsViewHolder extends RecyclerView.ViewHolder {
    private final EventView eventView;
    private boolean showFavoriteIcon;
    private boolean showHallName;

    public EventsViewHolder(View itemView, boolean showFavoriteIcon, boolean showHallName) {
        super(itemView);
        this.showFavoriteIcon = showFavoriteIcon;
        this.showHallName = showHallName;
        this.eventView = (EventView) itemView.findViewById(R.id.eventElement);
    }

	public void setModel(ConventionEvent event, boolean conflicting) {
		eventView.setShowFavoriteIcon(showFavoriteIcon);
		eventView.setShowHallName(showHallName);
		eventView.setConflicting(conflicting);
		eventView.setEvent(event);
	}
}
