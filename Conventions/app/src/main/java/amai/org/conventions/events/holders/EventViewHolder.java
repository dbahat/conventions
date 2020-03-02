package amai.org.conventions.events.holders;

import android.view.View;

import amai.org.conventions.events.EventView;
import amai.org.conventions.model.ConventionEvent;
import androidx.recyclerview.widget.RecyclerView;
import sff.org.conventions.R;

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
		eventView.setEvent(event);
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
