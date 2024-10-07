package amai.org.conventions.events.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import amai.org.conventions.events.activities.EventsTimeSlot;
import amai.org.conventions.events.activities.MyEventsDayFragment;
import amai.org.conventions.events.holders.ConflictingEventsViewHolder;
import amai.org.conventions.events.holders.SwipeableEventViewHolder;
import amai.org.conventions.events.listeners.EventSwipeToDismissListener;
import amai.org.conventions.model.ConventionEvent;
import sff.org.conventions.R;

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
				return new SwipeableEventViewHolder(view);
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
			conflictingEventsViewHolder.setSeparatorsDisplayed(
				position > 0,
				// Only show the bottom separator if the next group isn't also conflicting (since it will draw its own top separator if it is)
				position < getItemCount() - 1 && eventGroups.get(position + 1).getType() != EventsTimeSlot.EventsTimeSlotType.CONFLICTING_EVENTS
			);
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

		// Logic:
		// 1. Calculate new time slots:
		//    * Find previous and next event groups with regular events to calculate the new free time slots
		//    * Calculate new groups + free time slots for the changed events list
		// 2. Update list:
		//    * Remove previous and next free time slots
		//    * Add first free time slot after the previous events, if it exists
		//    * Add the rest of the slots instead of the changed events group
		// 3. Notify changes:
		//    * Free time after the changed group was added/changed/removed
		//    * Free time before the changed group (i.e. after the previous events) was added/changed/removed
		//    * Conflicting events list before the changed group (or before the free time between them), in case its separator should be updated
		//      (i.e. it's now the last group, or the changed group was conflicting before and now the first returned slot is not conflicting)
		//    * Conflicting events list after the changed group (or after the free time between them), in case its separator should be updated (i.e. it's now the first group)
		//    * The changed group itself, in case its not empty and either its type or number of groups changed
		//      (removed event in conflicting list is handled inside the conflicting list adapter)

		EventsTimeSlot previousEvents = null;
		EventsTimeSlot nextEvents = null;
		EventsTimeSlot.EventsTimeSlotType beforeType = null;
		EventsTimeSlot.EventsTimeSlotType beforePreviousType = null;
		EventsTimeSlot.EventsTimeSlotType afterType = null;
		EventsTimeSlot.EventsTimeSlotType afterNextType = null;

		// This is the position before the changed event group
		int previousPosition = adapterPosition - 1;
		// This is the position after the changed event group
		int nextPosition = alreadyRemoved ? adapterPosition : adapterPosition + 1;

		// This is the position of the free time slot before the changed group and after any non-ongoing event, if there is one.
		// This time slot should be replaced with the new free time slot which may be returned after the change.
		// If there is no free time slot before, this is the position where the possible new one should be added.
		int freeTimeSlotBeforePosition = -1;
		boolean freeTimeSlotBeforePositionIsFreeTime = false;

		// Get the event groups before and after the changed group and check if there were free slots between them
		if (previousPosition >= 0) {
			previousEvents = eventGroups.get(previousPosition);
			beforeType = previousEvents.getType();

			// We need this to know if we should update the conflicting events separators
			if (previousPosition > 0) {
				beforePreviousType = eventGroups.get(previousPosition - 1).getType();
			}

			// Get the time slot with events which aren't all ongoing so we can use it to calculate the free time slots
			// for the updated event groups
			int i = 1;
			while (previousEvents.getType() == EventsTimeSlot.EventsTimeSlotType.NO_EVENTS || previousEvents.areAllEventsOngoing()) {
				if (freeTimeSlotBeforePosition < 0 && previousEvents.getType() == EventsTimeSlot.EventsTimeSlotType.NO_EVENTS) {
					freeTimeSlotBeforePosition = previousPosition - i + 1; // The position of previousEvents
					freeTimeSlotBeforePositionIsFreeTime = true;
				}
				if (previousPosition >= i) {
					previousEvents = eventGroups.get(previousPosition - i);
					++i;
				} else {
					previousEvents = null;
					++i; // For the free time slot before position calculation (even though there shouldn't be a new free time slot before the group if there are no previous events)
					break;
				}
			}
			if (freeTimeSlotBeforePosition < 0) {
				freeTimeSlotBeforePosition = previousPosition - i + 2; // After previousEvents
			}
		} else {
			// There shouldn't be a free time slot before it if this is the first group, but if there is, it should be added before the group
			freeTimeSlotBeforePosition = 0;
		}

		if (nextPosition < eventGroups.size()) {
			nextEvents = eventGroups.get(nextPosition);
			afterType = nextEvents.getType();

			// We need this to know if we should update the conflicting events separator
			if (nextPosition + 1 < eventGroups.size()) {
				afterNextType = eventGroups.get(nextPosition + 1).getType();
			}

			// Get the time slot with events which aren't all ongoing so we can use it to calculate the free time slots
			// for the updated event groups
			int i = 1;
			while (nextEvents.getType() == EventsTimeSlot.EventsTimeSlotType.NO_EVENTS || nextEvents.areAllEventsOngoing()) {
				if (nextPosition + i < eventGroups.size()) {
					nextEvents = eventGroups.get(nextPosition + i);
					++i;
				} else {
					nextEvents = null;
					break;
				}
			}
		}

		// Get the new groups, including possible free slots before and after it (in case they were updated)
		ArrayList<EventsTimeSlot> groups = MyEventsDayFragment.getNonConflictingGroups(previousEvents, eventsList, nextEvents);

		// This is the position in the slots list where we will insert the new items
		int positionToAddFrom = adapterPosition;

		// Remove the original groups and its neighbor free slots starting at the largest position going backwards
		if (afterType == EventsTimeSlot.EventsTimeSlotType.NO_EVENTS) {
			eventGroups.remove(nextPosition);
		}
		if (!alreadyRemoved) {
			eventGroups.remove(adapterPosition);
		}
		boolean hasNewFreeTimeSlotBefore = groups.size() > 0 && groups.get(0).getType() == EventsTimeSlot.EventsTimeSlotType.NO_EVENTS;
		if (freeTimeSlotBeforePositionIsFreeTime) {
			eventGroups.remove(freeTimeSlotBeforePosition);
			--positionToAddFrom;
		}
		// If there is a new free time slot, add it in the free time slot before position
		if (hasNewFreeTimeSlotBefore) {
			eventGroups.add(freeTimeSlotBeforePosition, groups.get(0));
			groups.remove(0);
			++positionToAddFrom;
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
		boolean removedFreeSlotAfter = false;
		if (groups.size() > 1 && groups.get(groups.size() - 1).getType() == EventsTimeSlot.EventsTimeSlotType.NO_EVENTS) {
			--newGroupsSize;
			// Notify the list: if there was no free slot after the events, it was added. If there was, it was possibly changed.
			// The position here is always the one after the original events group.
			if (afterType != EventsTimeSlot.EventsTimeSlotType.NO_EVENTS) {
				notifyItemInserted(nextPosition);
			} else {
				notifyItemChanged(nextPosition);
			}
		} else if (afterType == EventsTimeSlot.EventsTimeSlotType.NO_EVENTS) {
			// If there was a free slot after, it was removed.
			notifyItemRemoved(nextPosition);
			removedFreeSlotAfter = true;
		}

		// Check if there is a free slot before the changed events
		boolean removedFreeSlotBefore = false;
		if (hasNewFreeTimeSlotBefore) {
			// Notify the list: if there was no free slot before, it was added. If there was, it was possibly changed.
			if (!freeTimeSlotBeforePositionIsFreeTime) {
				notifyItemInserted(freeTimeSlotBeforePosition);
			} else {
				notifyItemChanged(freeTimeSlotBeforePosition);
			}
		} else if (freeTimeSlotBeforePositionIsFreeTime) {
			// If there was a free slot before, it was removed.
			notifyItemRemoved(freeTimeSlotBeforePosition);
			removedFreeSlotBefore = true;
		}

		// Notify the list about a possible change in the previous and next items in case they are conflicting
		// (due to the custom conflicting events separator between conflicting events group and other groups).
		// In case the previous or next position was a removed free time, the one before/after it should be notified.
		if (beforeType == EventsTimeSlot.EventsTimeSlotType.CONFLICTING_EVENTS) {
			notifyItemChanged(previousPosition);
		} else if (removedFreeSlotBefore && freeTimeSlotBeforePosition == previousPosition && beforePreviousType == EventsTimeSlot.EventsTimeSlotType.CONFLICTING_EVENTS) {
			notifyItemChanged(previousPosition - 1);
		}
		if (afterType == EventsTimeSlot.EventsTimeSlotType.CONFLICTING_EVENTS) {
			notifyItemChanged(nextPosition);
		} else if (removedFreeSlotAfter && afterNextType == EventsTimeSlot.EventsTimeSlotType.CONFLICTING_EVENTS) {
			// The position here is the same as the removed slot because it was removed and this item is after it
			notifyItemChanged(nextPosition);
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
