package amai.org.conventions.events.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import amai.org.conventions.R;
import amai.org.conventions.events.EventView;
import amai.org.conventions.model.ConventionEvent;

public class EventViewHolder extends RecyclerView.ViewHolder {
	private final EventView eventView;
	private ConventionEvent event;
	private View bottomDividerView;

	public EventViewHolder(View itemView) {
		super(itemView);
		this.eventView = (EventView) itemView.findViewById(R.id.eventElement);

		bottomDividerView = itemView.findViewById(R.id.event_bottom_divider);
	}

	public void setModel(ConventionEvent event) {
		this.event = event;

		eventView.setShowFavoriteIcon(true);
		eventView.setShowHallName(true);
		eventView.setEvent(event, false);
	}

	public EventView getEventView() {
		return eventView;
	}

	public ConventionEvent getEvent() {
		return event;
	}

	public void setBottomDividerVisible(boolean visible) {
		bottomDividerView.setVisibility(visible ? View.VISIBLE : View.GONE);
	}
}
