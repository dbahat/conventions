package amai.org.conventions.events.listeners;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

import amai.org.conventions.ConventionsApplication;
import amai.org.conventions.R;
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.events.adapters.EventGroupsAdapter;
import amai.org.conventions.events.holders.SwipeableEventViewHolder;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.notifications.PushNotificationTopicsSubscriber;

public class EventSwipeToDismissListener implements SwipeableEventViewHolder.OnEventSwipedListener {

	private SwipeableEventViewHolder viewHolder;
	private List<?> eventsList;
	private RecyclerView.Adapter<?> adapter;

	public EventSwipeToDismissListener(SwipeableEventViewHolder viewHolder, List<?> eventsList, RecyclerView.Adapter<?> adapter) {
		this.viewHolder = viewHolder;
		this.eventsList = eventsList;
		this.adapter = adapter;
	}

	@Override
	public void onEventSwiped(final ConventionEvent event) {
		FirebaseAnalytics
				.getInstance(viewHolder.itemView.getContext())
				.logEvent("swipe_to_dismiss", null);

		viewHolder.getModel().setAttending(false);
		Convention.getInstance().getStorage().saveUserInput();

		ConventionsApplication.alarmScheduler.cancelDefaultEventAlarms(viewHolder.getModel());

		PushNotificationTopicsSubscriber.unsubscribe(event);
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

		try {
			Snackbar snackbar = Snackbar.make(viewHolder.itemView, R.string.event_removed_from_favorites, Snackbar.LENGTH_LONG).setAction(R.string.cancel, new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					ConventionEvent updatedEvent = viewHolder.getModel();
					updatedEvent.setAttending(true);
					ConventionsApplication.alarmScheduler.scheduleDefaultEventAlarms(updatedEvent);
					Convention.getInstance().getStorage().saveUserInput();
					PushNotificationTopicsSubscriber.unsubscribe(event);
					if (adapter instanceof EventGroupsAdapter) {
						((EventGroupsAdapter) adapter).updateEventGroups();
					} else {
						adapter.notifyDataSetChanged();
					}
				}
			});
			snackbar.getView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
			snackbar.setActionTextColor(ThemeAttributes.getColor(viewHolder.itemView.getContext(), R.attr.snackbarActionColor));
			snackbar.show();
		} catch (IllegalArgumentException e) {
			// Could happen when the user moved to a different screen, in that case don't show the snackbar
		}
	}
}
