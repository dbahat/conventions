package amai.org.conventions.events.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import amai.org.conventions.ConventionsApplication;
import amai.org.conventions.R;
import amai.org.conventions.events.adapters.EventGroupsAdapter;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.ConventionEventComparator;
import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.Dates;


public class MyEventsActivity extends NavigationActivity {

    // Handler for updating the next event start text
    private Handler nextEventStartTextRunner = new Handler();
    private Runnable updateNextEventStartTimeText;
	private static final int NEXT_EVENT_START_TIME_UPDATE_DELAY = 60000; // 1 minute

    private TextView nextEventStart;
	private View nextEventStartBottomLine;
	private RecyclerView eventsList;
	private View emptyView;
	private AlertDialog noEventsDialog;
	private EventGroupsAdapter adapter;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentInContentContainer(R.layout.activity_my_events);
        setToolbarTitle(getResources().getString(R.string.my_events_title));

        nextEventStart = (TextView) findViewById(R.id.nextEventStart);
		nextEventStartBottomLine = findViewById(R.id.nextEventStartBottomLine);
		emptyView = findViewById(R.id.my_events_empty);
	    eventsList = (RecyclerView) findViewById(R.id.myEventsList);
	    eventsList.setLayoutManager(new LinearLayoutManager(this));
    }

	@Override
	protected void onResume() {
		super.onResume();
		updateDataset();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (updateNextEventStartTimeText != null) {
			nextEventStartTextRunner.removeCallbacks(updateNextEventStartTimeText);
		}
	}

	private void updateDataset() {
		final List<ConventionEvent> events = getMyEvents();

		// Set up text view for next event start
		setNextEventStartText(events);

		// Set up events list
		ArrayList<EventsTimeSlot> nonConflictingGroups = getNonConflictingGroups(null, events, null);
		if (adapter == null) {
			adapter = new EventGroupsAdapter(nonConflictingGroups);
		} else {
			adapter.updateEventGroups(nonConflictingGroups);
		}
		eventsList.setAdapter(adapter);

		updateVisibility(nonConflictingGroups.size(), eventsList, emptyView);

		// Register for dataset update events, in case we need to return the empty layout view after all items were dismissed.
		adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
			@Override
			public void onItemRangeRemoved(int positionStart, int itemCount) {
				super.onItemRangeRemoved(positionStart, itemCount);
				updateVisibility(adapter.getItemCount(), eventsList, emptyView);
			}

			@Override
			public void onChanged() {
				super.onChanged();
				updateVisibility(adapter.getItemCount(), eventsList, emptyView);
			}
		});

		adapter.setOnEventRemovedAction(new Runnable() {
			@Override
			public void run() {
				setNextEventStartText(getMyEvents());
			}
		});
	}

	private void updateVisibility(int datasetSize, RecyclerView eventsList, View emptyView) {
		if (datasetSize > 0) {
			eventsList.setVisibility(View.VISIBLE);
			emptyView.setVisibility(View.GONE);
		} else {
			eventsList.setVisibility(View.GONE);
			emptyView.setVisibility(View.VISIBLE);
		}
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_events_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.my_events_navigate_to_programme:
                navigateToActivity(ProgrammeActivity.class);

                return true;
			case R.id.my_events_share:
				ConventionsApplication.sendTrackingEvent(new HitBuilders.EventBuilder()
						.setCategory("MyEvents")
						.setAction("ShareClicked")
						.setValue(getMyEvents().size())
						.build());

				if (getMyEvents().size() > 0) {
					startActivity(createSharingIntent());
				} else {
					noEventsDialog = new AlertDialog.Builder(this)
							.setMessage(R.string.share_no_events)
							.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									noEventsDialog.dismiss();
								}
							})
							.create();
					noEventsDialog.show();
				}

				return true;
        }

        return super.onOptionsItemSelected(item);
    }

	private Intent createSharingIntent() {
		// get available share intents
		List<Intent> targets = new ArrayList<>();
		Intent template = new Intent(Intent.ACTION_SEND)
				.setType("text/plain");
		List<ResolveInfo> candidates = this.getPackageManager()
				.queryIntentActivities(template, 0);

		// remove facebook, since they don't allow sharing text inside the sharing intent, causing the app to appear broken.
		for (ResolveInfo candidate : candidates) {
			String packageName = candidate.activityInfo.packageName;
			if (!packageName.equals("com.facebook.katana")) {
				Intent shareIntent = ShareCompat.IntentBuilder
						.from(this)
						.setText(formatMyEventsToShare(false))
						.setHtmlText(formatMyEventsToShare(true))
						.setType("text/plain")
						.getIntent()
						.setPackage(packageName);

				targets.add(shareIntent);
			}
		}
		return Intent
				.createChooser(targets.remove(0), getString(R.string.my_event_share_chooser_dialog_title))
				.putExtra(Intent.EXTRA_INITIAL_INTENTS, targets.toArray(new Parcelable[targets.size()]));
	}

	private String formatMyEventsToShare(boolean isHtml) {
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append(getString(R.string.my_event_share_title, Convention.getInstance().getDisplayName()));
		stringBuilder.append("\n");
		for (ConventionEvent event : getMyEvents()) {
			stringBuilder.append(formatEventToShare(event));
			stringBuilder.append("\n");
		}
		stringBuilder.append("\n");

		if (isHtml) {
			stringBuilder.append(
					String.format(Dates.getLocale(), "<a href=\"%s\">%s</a>",
							getString(R.string.my_event_share_link),
							getString(R.string.my_event_share_signature, Convention.getInstance().getDisplayName())));
		} else {
			stringBuilder.append(getString(R.string.my_event_share_signature, Convention.getInstance().getDisplayName()));
			stringBuilder.append("\n");
			stringBuilder.append(getString(R.string.my_event_share_link));
		}

		return stringBuilder.toString();
	}

	private String formatEventToShare(ConventionEvent event) {
		return String.format(Dates.getLocale(), "%s: %s",
				Dates.formatHoursAndMinutes(event.getStartTime()),
				event.getTitle());
	}

    public static List<ConventionEvent> getMyEvents() {
	    ArrayList<ConventionEvent> events = CollectionUtils.filter(
			    Convention.getInstance().getEvents(),
			    new CollectionUtils.Predicate<ConventionEvent>() {
				    @Override
				    public boolean where(ConventionEvent event) {
					    return event.isAttending();
				    }
			    },
			    new ArrayList<ConventionEvent>()
	    );
	    Collections.sort(events, new ConventionEventComparator());
	    return events;
    }

    private void setNextEventStartText(final List<ConventionEvent> events) {
	    // Remove existing callback
	    if (updateNextEventStartTimeText != null) {
		    nextEventStartTextRunner.removeCallbacks(updateNextEventStartTimeText);
	    }

        ConventionEvent nextEvent = null;
        Date currTime = Dates.now();
        for (ConventionEvent curr : events) {
            if (curr.getStartTime().after(currTime)) {
                nextEvent = curr;
                break;
            }
        }

        // Only display it if it's on the same day
        boolean displayNextEventStart = false;
        if (nextEvent != null) {
            Calendar startTime = Calendar.getInstance();
	        startTime.setTime(nextEvent.getStartTime());
            Calendar now = Calendar.getInstance();
            now.setTime(currTime);
            if (startTime.get(Calendar.DATE) == now.get(Calendar.DATE) &&
                    startTime.get(Calendar.MONTH) == now.get(Calendar.MONTH) &&
                    startTime.get(Calendar.YEAR) == now.get(Calendar.YEAR)) {
                displayNextEventStart = true;
            }
        }

        if (displayNextEventStart) {
            nextEventStart.setVisibility(View.VISIBLE);
	        nextEventStartBottomLine.setVisibility(View.VISIBLE);
            nextEventStart.setText(getString(R.string.next_event_start,
                    Dates.toHumanReadableTimeDuration(nextEvent.getStartTime().getTime() - currTime.getTime()),
                    nextEvent.getHall().getName()));

            if (updateNextEventStartTimeText == null) {
                updateNextEventStartTimeText = new Runnable() {
                    @Override
                    public void run() {
	                    MyEventsActivity.this.setNextEventStartText(events);
                    }
                };
            }
            nextEventStartTextRunner.postDelayed(updateNextEventStartTimeText, NEXT_EVENT_START_TIME_UPDATE_DELAY);
        } else {
	        nextEventStart.setVisibility(View.GONE);
	        nextEventStartBottomLine.setVisibility(View.GONE);
        }
    }

	/**
	 * Split events to conflicting groups. A conflict between 2 events happens when one of events starts
	 * after the other event's start time and before its end time.
	 * @param events - list of events sorted by start time
	 * @return a list of event groups. Each event group is a list of events, with the same sort order as
	 * sent, where each event conflicts with at least one other event in the group. Events from different
	 * groups do not conflict with each other. The groups are ordered by the first event's start time.
	 */
	public static ArrayList<EventsTimeSlot> getNonConflictingGroups(EventsTimeSlot previous, List<ConventionEvent> events, EventsTimeSlot next) {
		ArrayList<EventsTimeSlot> nonConflictingTimeSlots = new ArrayList<>();

		Date currGroupEndTime = (previous != null ? previous.getEndTime() : null);
		EventsTimeSlot currSlot = previous;
		for (ConventionEvent event : events) {
			// Non-conflicting event - it's either the first event or it starts after
			// (or at the same time as) the current group ends.
			if (currSlot == null || !event.getStartTime().before(currGroupEndTime)) {
				// If we have a previous group, add it to the groups list
				if (currSlot != null) {
					if (currSlot != previous) {
						nonConflictingTimeSlots.add(currSlot);
					}

					// If there are at least 30 minutes between this group and the next, add a free time slot
					if (event.getStartTime().getTime() - currGroupEndTime.getTime() >= 30 * Dates.MILLISECONDS_IN_MINUTE) {
						EventsTimeSlot freeSlot = new EventsTimeSlot(currGroupEndTime, event.getStartTime());
						nonConflictingTimeSlots.add(freeSlot);
					}
				}
				currSlot = new EventsTimeSlot();
				currGroupEndTime = null;
			}
			currSlot.addEvent(event);
			if (currGroupEndTime == null || event.getEndTime().after(currGroupEndTime)) {
				currGroupEndTime = event.getEndTime();
			}
		}

		// Add the last group
		if (currSlot != null) {
			if (currSlot != previous) {
				nonConflictingTimeSlots.add(currSlot);
			}

			if (next != null && next.getStartTime().getTime() - currGroupEndTime.getTime() >= 30 * Dates.MILLISECONDS_IN_MINUTE) {
				EventsTimeSlot freeSlot = new EventsTimeSlot(currGroupEndTime, next.getStartTime());
				nonConflictingTimeSlots.add(freeSlot);
			}
		}

		return nonConflictingTimeSlots;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (noEventsDialog != null && noEventsDialog.isShowing()) {
			noEventsDialog.dismiss();
		}
	}
}
