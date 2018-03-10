package amai.org.conventions.events.listeners;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.android.gms.analytics.HitBuilders;

import java.util.ArrayList;
import java.util.List;

import amai.org.conventions.ConventionsApplication;
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.events.adapters.EventGroupsAdapter;
import amai.org.conventions.events.holders.SwipeableEventViewHolder;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.conventions.Convention;
import sff.org.conventions.R;

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
		ConventionsApplication.sendTrackingEvent(new HitBuilders.EventBuilder()
				.setCategory("Favorites")
				.setAction("Remove")
				.setLabel("SwipeToDismiss")
				.build());

		viewHolder.getModel().setAttending(false);
		Convention.getInstance().getStorage().saveUserInput();

		ConventionsApplication.alarmScheduler.cancelDefaultEventAlarms(viewHolder.getModel());

		// TODO Renew push notification registration based on the new favorites state
//		AzurePushNotifications notifications = new AzurePushNotifications(viewHolder.itemView.getContext());
//		notifications.registerAsync(new AzurePushNotifications.RegistrationListener.DoNothing());

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

		Snackbar snackbar = Snackbar.make(viewHolder.itemView, R.string.event_removed_from_favorites, Snackbar.LENGTH_LONG).setAction(R.string.cancel, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ConventionEvent updatedEvent = viewHolder.getModel();
				updatedEvent.setAttending(true);
				ConventionsApplication.alarmScheduler.scheduleDefaultEventAlarms(updatedEvent);
				Convention.getInstance().getStorage().saveUserInput();
				// TODO - Remove from per-event category here
//				AzurePushNotifications notifications = new AzurePushNotifications(viewHolder.itemView.getContext());
//				notifications.registerAsync(new AzurePushNotifications.RegistrationListener.DoNothing());
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
	}
}
