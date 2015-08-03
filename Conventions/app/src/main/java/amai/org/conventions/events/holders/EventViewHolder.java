package amai.org.conventions.events.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import amai.org.conventions.R;
import amai.org.conventions.events.EventView;
import amai.org.conventions.model.ConventionEvent;

public class EventViewHolder extends RecyclerView.ViewHolder {
	private final EventView eventView;

	public EventViewHolder(View itemView) {
		super(itemView);
		this.eventView = (EventView) itemView.findViewById(R.id.eventElement);
	}

	public void setModel(ConventionEvent event) {
		eventView.setShowFavoriteIcon(true);
		eventView.setShowHallName(true);
		eventView.setConflicting(false);
		eventView.setEvent(event);
	}
}
