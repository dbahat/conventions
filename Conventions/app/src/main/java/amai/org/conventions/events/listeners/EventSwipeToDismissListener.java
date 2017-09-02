package amai.org.conventions.events.listeners;

import android.support.v7.widget.RecyclerView;

import com.google.android.gms.analytics.HitBuilders;

import java.util.ArrayList;
import java.util.List;

import amai.org.conventions.ConventionsApplication;
import amai.org.conventions.events.adapters.EventGroupsAdapter;
import amai.org.conventions.events.holders.SwipeableEventViewHolder;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.conventions.Convention;

public class EventSwipeToDismissListener implements Runnable {

	private SwipeableEventViewHolder viewHolder;
	private List<?> eventsList;
	private RecyclerView.Adapter<?> adapter;

	public EventSwipeToDismissListener(SwipeableEventViewHolder viewHolder, List<?> eventsList, RecyclerView.Adapter<?> adapter) {
		this.viewHolder = viewHolder;
		this.eventsList = eventsList;
		this.adapter = adapter;
	}

	@Override
	public void run() {
		ConventionsApplication.sendTrackingEvent(new HitBuilders.EventBuilder()
				.setCategory("Favorites")
				.setAction("Remove")
				.setLabel("SwipeToDismiss")
				.build());

		viewHolder.getModel().setAttending(false);
		Convention.getInstance().getStorage().saveUserInput();

		ConventionsApplication.alarmScheduler.cancelDefaultEventAlarms(viewHolder.getModel());

		int adapterPosition = viewHolder.getAdapterPosition();

		// This could happen if the item has already been removed, the dataset changed or the view recycled
		// (see RecyclerView#getAdapterPosition)
		if (adapterPosition != RecyclerView.NO_POSITION) {
			eventsList.remove(adapterPosition);
			adapter.notifyItemRemoved(adapterPosition);
		}

		if (adapter instanceof EventGroupsAdapter) {
			((EventGroupsAdapter) adapter).updateSlots(adapterPosition, new ArrayList<ConventionEvent>(), true);
		}
	}
}
