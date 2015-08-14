package amai.org.conventions.events.listeners;

import android.support.v7.widget.RecyclerView;

import com.google.android.gms.analytics.HitBuilders;

import java.util.List;

import amai.org.conventions.ConventionsApplication;
import amai.org.conventions.events.holders.SwipeableEventViewHolder;
import amai.org.conventions.model.Convention;

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

		eventsList.remove(viewHolder.getAdapterPosition());
		adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
	}
}
