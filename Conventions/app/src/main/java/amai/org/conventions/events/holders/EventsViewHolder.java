package amai.org.conventions.events.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import amai.org.conventions.events.EventView;
import amai.org.conventions.model.ConventionEvent;

public class EventsViewHolder extends RecyclerView.ViewHolder {
    private final EventView eventView;
    private boolean showFavoriteIcon;
    private boolean showHallName;

    public EventsViewHolder(View itemView, int eventElementId, boolean showFavoriteIcon, boolean showHallName) {
        super(itemView);
        this.showFavoriteIcon = showFavoriteIcon;
        this.showHallName = showHallName;
        eventView = (EventView) itemView.findViewById(eventElementId);
    }

	public void setModel(ConventionEvent event, boolean conflicting) {
		eventView.setEvent(event);
		eventView.setShowFavoriteIcon(showFavoriteIcon);
		eventView.setShowHallName(showHallName);
		eventView.setConflicting(conflicting);
	}
}
