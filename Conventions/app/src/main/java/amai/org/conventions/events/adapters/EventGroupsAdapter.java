package amai.org.conventions.events.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.events.activities.EventsTimeSlot;
import amai.org.conventions.events.activities.MyEventsActivity;
import amai.org.conventions.events.activities.MyEventsDayFragment;
import amai.org.conventions.events.holders.ConflictingEventsViewHolder;
import amai.org.conventions.events.holders.SwipeableEventViewHolder;
import amai.org.conventions.events.listeners.EventSwipeToDismissListener;
import amai.org.conventions.model.ConventionEvent;

public class EventGroupsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private static final int ITEM_VIEW_TYPE_REGULAR = 1;
	private static final int ITEM_VIEW_TYPE_CONFLICTING = 2;
	private static final int ITEM_VIEW_TYPE_FREE_SLOT = 3;

	private final Callback<ArrayList<EventsTimeSlot>> eventGroupsCallback;
	private ArrayList<EventsTimeSlot> eventGroups;
	private Runnable onEventListChangedAction;

	public EventGroupsAdapter(Callback<ArrayList<EventsTimeSlot>> eventGroupsCallback) {
		this.eventGroupsCallback = eventGroupsCallback;
		this.eventGroups = eventGroupsCallback.call();

		registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
			@Override
			public void onItemRangeRemoved(int positionStart, int itemCount) {
				super.onItemRangeRemoved(positionStart, itemCount);

				if (onEventListChangedAction != null) {
					onEventListChangedAction.run();
				}
			}
		});
	}

	public void updateEventGroups() {
		this.eventGroups = eventGroupsCallback.call();
		notifyDataSetChanged();
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
		switch (viewType) {
			case ITEM_VIEW_TYPE_FREE_SLOT: {
				View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.small_text_view, viewGroup, false);
				TextView textView = (TextView) view.findViewById(R.id.small_text);
				((FrameLayout.LayoutParams) textView.getLayoutParams()).gravity = Gravity.CENTER_HORIZONTAL;
				textView.setLayoutParams(textView.getLayoutParams());
				return new FreeTimeSlotViewHolder(view);
			}
			case ITEM_VIEW_TYPE_REGULAR: {
				View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.swipeable_event_view, viewGroup, false);
				return new SwipeableEventViewHolder(view, true);
			}
			case ITEM_VIEW_TYPE_CONFLICTING: {
				View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.conflicting_events_view, viewGroup, false);
				return new ConflictingEventsViewHolder(view, viewGroup.getContext());
			}
		}
		throw new RuntimeException("Unexpected view type " + viewType);
	}

	@Override
	public int getItemCount() {
		return eventGroups.size();
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder eventsViewHolder, final int position) {
		EventsTimeSlot timeSlot = eventGroups.get(position);
		if (eventsViewHolder instanceof SwipeableEventViewHolder) {
			final SwipeableEventViewHolder swipeableEventViewHolder = (SwipeableEventViewHolder) eventsViewHolder;
			swipeableEventViewHolder.setModel(timeSlot.getEvents().get(0));

			EventSwipeToDismissListener listener = new EventSwipeToDismissListener(swipeableEventViewHolder, eventGroups, this);
			swipeableEventViewHolder.setOnViewSwipedAction(listener);

		} else if (eventsViewHolder instanceof ConflictingEventsViewHolder) {
			final ConflictingEventsViewHolder conflictingEventsViewHolder = (ConflictingEventsViewHolder) eventsViewHolder;
			conflictingEventsViewHolder.setModel(timeSlot.getEvents());
			conflictingEventsViewHolder.setEventRemovedListener(new ConflictingEventsViewHolder.OnEventListChangedListener() {
				@Override
				public void onEventRemoved() {
					int adapterPosition = conflictingEventsViewHolder.getAdapterPosition();
					List<ConventionEvent> eventsList = conflictingEventsViewHolder.getModel();

					updateSlots(adapterPosition, eventsList, false);

					if (onEventListChangedAction != null) {
						onEventListChangedAction.run();
					}
				}

				@Override
				public void onEventListChanged() {
					updateEventGroups();
					if (onEventListChangedAction != null) {
						onEventListChangedAction.run();
					}
				}
			});
		} else if (eventsViewHolder instanceof FreeTimeSlotViewHolder) {
			((FreeTimeSlotViewHolder) eventsViewHolder).setModel(timeSlot);
		}
	}

	/**
	 * Updates the events list after removal of an event. Includes re-calculating what are conflicting events and what are the free slots based on the change.
	 *
	 * @param adapterPosition The position of the changed timeslot. In case there are conflicting events, they are treated as a a single slot
	 * @param eventsList      The list of events after the removal. In case there were no conflicting events in the slot, will be empty
	 * @param alreadyRemoved  was the model already updated with the removal
	 */
	public void updateSlots(int adapterPosition, List<ConventionEvent> eventsList, boolean alreadyRemoved) {
		// This could happen if the item has already been removed, the dataset changed or the view recycled
		// (see RecyclerView#getAdapterPosition)
		if (adapterPosition == RecyclerView.NO_POSITION) {
			updateEventGroups();
			return;
		}

		EventsTimeSlot previousEvents = null;
		EventsTimeSlot nextEvents = null;
		boolean hadFreeBefore = false;
		boolean hadFreeAfter = false;

		// This is the position before the changed event group
		int previousPosition = adapterPosition - 1;
		// This is the position after the changed event group
		int nextPosition = alreadyRemoved ? adapterPosition : adapterPosition + 1;

		// Get the event groups before and after the changed group and check if there were free slots between them
		if (previousPosition >= 0) {
			previousEvents = eventGroups.get(previousPosition);
			if (previousEvents.getType() == EventsTimeSlot.EventsTimeSlotType.NO_EVENTS && previousPosition > 0) {
				previousEvents = eventGroups.get(previousPosition - 1);
				hadFreeBefore = true;
			}
		}

		if (nextPosition < eventGroups.size()) {
			nextEvents = eventGroups.get(nextPosition);
			if (nextEvents.getType() == EventsTimeSlot.EventsTimeSlotType.NO_EVENTS && nextPosition < eventGroups.size() - 1) {
				nextEvents = eventGroups.get(nextPosition + 1);
				hadFreeAfter = true;
			}
		}

		// Get the new groups, including possible free slots before and after it (in case they were updated)
		ArrayList<EventsTimeSlot> groups = MyEventsDayFragment.getNonConflictingGroups(previousEvents, eventsList, nextEvents);

		// This is the position in the slots list where we will insert the new items.
		// It should be the smallest position we removed items from.
		int positionToAddFrom = adapterPosition;

		// Remove the original groups and its neighbor free slots starting at the largest position going backwards
		if (hadFreeAfter) {
			eventGroups.remove(nextPosition);
		}
		if (!alreadyRemoved) {
			eventGroups.remove(adapterPosition);
		}
		if (hadFreeBefore) {
			eventGroups.remove(previousPosition);
			// If we removed items from the previous position, that's where we'll add the new items
			positionToAddFrom = previousPosition;
		}
		eventGroups.addAll(positionToAddFrom, groups);

		// Notify the recycler view that there were change. We want to make this as accurate as possible so it will look
		// natural to the user.
		// We first notify the changes in the free slots because the changes in the events group can alter their positions.

		// This is the number of new groups, excluding the free slots before and after the original group.
		int newGroupsSize = groups.size();

		// Check if there is a free slot after the changed events. We only check this if the groups size is more than 1
		// because an "after" slot cannot be in the first index (in case there is only 1 slot and it's a free slot we treat it as
		// a "before" slot). In that case, this must happen before the notifying about the new "before" slot or they will cancel each other
		// (due to notifying "item inserted" then "item removed" on the same index).
		if (groups.size() > 1 && groups.get(groups.size() - 1).getType() == EventsTimeSlot.EventsTimeSlotType.NO_EVENTS) {
			--newGroupsSize;
			// Notify the list: if there was no free slot after the events, it was added. If there was, it was possibly changed.
			// The position here is always the one after the original events group.
			if (!hadFreeAfter) {
				notifyItemInserted(nextPosition);
			} else {
				notifyItemChanged(nextPosition);
			}
		} else if (hadFreeAfter) {
			// If there was a free slot after, it was removed.
			notifyItemRemoved(nextPosition);
		}

		// Check if there is a free slot before the changed events
		if (groups.size() > 0 && groups.get(0).getType() == EventsTimeSlot.EventsTimeSlotType.NO_EVENTS) {
			--newGroupsSize;
			// Notify the list: if there was no free slot before, it was added. If there was, it was possibly changed.
			if (!hadFreeBefore) {
				// The free slot was added at the original group's position (since there was no "previous position")
				notifyItemInserted(adapterPosition);
			} else {
				notifyItemChanged(previousPosition);
			}
		} else if (hadFreeBefore) {
			// If there was a free slot before, it was removed.
			notifyItemRemoved(previousPosition);
		}

		// Notify the list about changes in the original group, This is only relevant if it was a conflicting group
		// that was changed because for a single event that was removed we already notified the list (and it was already removed).

		// If after removal only 1 item remains, the item type has changed
		if (eventsList.size() == 1) {
			// Use remove and insert instead of changed for fade-out and fade-in animation, to make it seem
			// like a bigger change
			notifyItemRemoved(adapterPosition);
			notifyItemInserted(adapterPosition);
		} else if (eventsList.size() > 1) {
			// If the number of groups changed, remove the group and insert the new groups
			if (newGroupsSize != 1) {
				notifyItemRemoved(adapterPosition);
				notifyItemRangeInserted(adapterPosition, newGroupsSize);
			}
		}
	}

	public void setOnEventListChangedAction(final Runnable onEventListChangedAction) {
		this.onEventListChangedAction = onEventListChangedAction;
	}

	@Override
	public int getItemViewType(int position) {
		switch (eventGroups.get(position).getType()) {
			case NO_EVENTS:
				return ITEM_VIEW_TYPE_FREE_SLOT;
			case SINGLE_EVENT:
				return ITEM_VIEW_TYPE_REGULAR;
			default:
				return ITEM_VIEW_TYPE_CONFLICTING;
		}
	}

	public static interface Callback<T> {
		T call();
	}
}
