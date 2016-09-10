package amai.org.conventions.events;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.android.gms.analytics.HitBuilders;

import amai.org.conventions.ConventionsApplication;
import sff.org.conventions.R;
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.events.listeners.OnEventFavoriteChangedListener;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.model.ConventionEvent;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class DefaultEventFavoriteChangedListener implements OnEventFavoriteChangedListener {
	private View view;

	public DefaultEventFavoriteChangedListener(View view) {
		this.view = view;
	}

	@Override
	public void onEventFavoriteChanged(final ConventionEvent updatedEvent) {
		// Update the favorite state in the model
		final boolean newAttending = !updatedEvent.isAttending();

		ConventionsApplication.sendTrackingEvent(new HitBuilders.EventBuilder()
				.setCategory("Favorites")
				.setAction(newAttending ? "Add" : "Remove")
				.setLabel("SwipeToEdit")
				.build());

		updatedEvent.setAttending(newAttending);
		if (newAttending) {
			ConventionsApplication.alarmScheduler.scheduleDefaultEventAlarms(updatedEvent);
		} else {
			ConventionsApplication.alarmScheduler.cancelDefaultEventAlarms(updatedEvent);
		}

		// Save the changes
		Convention.getInstance().getStorage().saveUserInput();

		// If the view is a list, notify view to redraw the UI so the new favorite icon state will apply
		// for all views of this event
		notifyDatasetChanged();

		if (newAttending) {
			// Check if the new favorite event conflicts with other events
			if (Convention.getInstance().conflictsWithOtherFavoriteEvent(updatedEvent)) {
				Snackbar snackbar = Snackbar.make(view, R.string.event_added_to_favorites_but_conflicts, Snackbar.LENGTH_LONG).setAction(R.string.cancel, new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						onEventFavoriteChanged(updatedEvent);
					}
				});
				snackbar.getView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
				snackbar.setActionTextColor(ThemeAttributes.getColor(view.getContext(), R.attr.snackbarActionColor));
				snackbar.show();
			} else {
				Snackbar.make(view, R.string.event_added_to_favorites, Snackbar.LENGTH_SHORT).show();
			}
		} else {
			Snackbar.make(view, R.string.event_removed_from_favorites, Snackbar.LENGTH_SHORT).show();
		}
	}

	private void notifyDatasetChanged() {
		if (view instanceof RecyclerView) {
			RecyclerView.Adapter adapter = ((RecyclerView) view).getAdapter();
			adapter.notifyDataSetChanged();
		} else if (view instanceof ListView) {
			ListAdapter adapter = ((ListView) view).getAdapter();
			if (adapter instanceof BaseAdapter) {
				((BaseAdapter) adapter).notifyDataSetChanged();
			}
		} else if (view instanceof StickyListHeadersListView) {
			ListAdapter adapter = ((StickyListHeadersListView) view).getAdapter();
			if (adapter instanceof BaseAdapter) {
				((BaseAdapter) adapter).notifyDataSetChanged();
			}
		}
	}
}
