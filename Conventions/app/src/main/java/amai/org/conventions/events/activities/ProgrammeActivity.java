package amai.org.conventions.events.activities;

import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.NumberPicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.events.ProgrammeConventionEvent;
import amai.org.conventions.events.adapters.SwipeableEventsViewOrHourAdapter;
import amai.org.conventions.events.holders.EventTimeViewHolder;
import amai.org.conventions.model.Convention;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.Dates;
import amai.org.conventions.navigation.NavigationActivity;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView.OnHeaderClickListener;

public class ProgrammeActivity extends NavigationActivity implements OnHeaderClickListener {

    private SwipeableEventsViewOrHourAdapter adapter;
    private StickyListHeadersListView listView;
    private List<ProgrammeConventionEvent> events;
	private Menu menu;
	private boolean navigateToMyEventsIconModified = false;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentInContentContainer(R.layout.activity_programme);
        setToolbarTitle(getResources().getString(R.string.programme_title));

        this.listView = (StickyListHeadersListView) findViewById(R.id.programmeList);
        this.events = getEventList();
        adapter = new SwipeableEventsViewOrHourAdapter(events);
        listView.setAdapter(adapter);
	    adapter.setOnEventFavoriteChangedListener(new Runnable() {
		    @Override
		    public void run() {
			    if (!navigateToMyEventsIconModified) {
				    navigateToMyEventsIconModified = true;
				    final MenuItem item = menu.findItem(R.id.programme_navigate_to_my_events);
				    Drawable icon = item.getIcon().mutate();
				    int accentColor = ThemeAttributes.getColor(ProgrammeActivity.this, R.attr.toolbarIconAccentColor);
				    icon.setColorFilter(accentColor, PorterDuff.Mode.MULTIPLY);

				    View actionView = getLayoutInflater().inflate(R.layout.my_events_icon, null);
				    ImageView myEventsNonAnimatedIcon = (ImageView) actionView.findViewById(R.id.non_animated_icon);
				    myEventsNonAnimatedIcon.setColorFilter(accentColor, PorterDuff.Mode.MULTIPLY);

				    final ImageView myEventsAnimatedIcon = (ImageView) actionView.findViewById(R.id.icon_to_animate);

				    AnimationSet set = new AnimationSet(true);
				    set.addAnimation(new ScaleAnimation(1, 2, 1, 2, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f));
				    set.addAnimation(new AlphaAnimation(1, 0));
				    set.setDuration(500);

				    myEventsAnimatedIcon.startAnimation(set);
				    item.setActionView(actionView);
				    set.setAnimationListener(new Animation.AnimationListener() {
					    @Override
					    public void onAnimationStart(Animation animation) {
					    }

					    @Override
					    public void onAnimationEnd(Animation animation) {
						    item.setActionView(null);
					    }

					    @Override
					    public void onAnimationRepeat(Animation animation) {
					    }
				    });
			    }
		    }
	    });

        listView.setOnHeaderClickListener(this);

        final int position = findHourPosition(getHour(Dates.now()));
        if (position != -1) {
            final ViewTreeObserver vto = listView.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                public void onGlobalLayout() {
                    scrollToPosition(position);

                    // Unregister the listener to only call scrollToPosition once
                    listView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        // Always redraw the list during onResume, since it's a fast operation, and this ensures the data is up to date in case the activity got paused.
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	    this.menu = menu;
        getMenuInflater().inflate(R.menu.programme_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.programme_navigate_to_my_events:
	            navigateToMyEventsIconModified = false;
	            item.getIcon().clearColorFilter();
	            navigateToActivity(MyEventsActivity.class);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onHeaderClick(StickyListHeadersListView stickyListHeadersListView, View view, int i, long l, boolean b) {
        EventTimeViewHolder eventTimeViewHolder = (EventTimeViewHolder) view.getTag();
        int selectedTimeSectionHour = eventTimeViewHolder.getCurrentHour();

	    // Setup number picker dialog
	    final NumberPicker numberPicker = new NumberPicker(this);
	    int minValue = events.get(0).getTimeSection().get(Calendar.HOUR_OF_DAY);
	    int maxValue = events.get(events.size() - 1).getTimeSection().get(Calendar.HOUR_OF_DAY);
	    numberPicker.setMinValue(minValue);
	    numberPicker.setMaxValue(maxValue);
	    List<String> values = new ArrayList<>(maxValue - minValue + 1);

	    for (int value = minValue; value <= maxValue; ++value) {
		    String hour = String.valueOf(value);
		    if (hour.length() == 1) {
			    hour = "0" + hour;
		    }
		    hour += ":00";
		    values.add(hour);
	    }
	    numberPicker.setDisplayedValues(values.toArray(new String[values.size()]));
	    numberPicker.setValue(selectedTimeSectionHour);
	    numberPicker.setWrapSelectorWheel(false);
	    numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

	    AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light_Dialog))
			    .setView(numberPicker)
			    .setPositiveButton(R.string.select_hour_ok,
					    new DialogInterface.OnClickListener() {
						    public void onClick(DialogInterface dialog, int whichButton) {
				                int position = findHourPosition(numberPicker.getValue());
				                if (position != -1) {
				                    scrollToPosition(position);
				                }
						    }
					    })
			    .create();
        dialog.show();
    }

    private void scrollToPosition(final int position) {
        listView.smoothScrollToPositionFromTop(position, 0, 500);

        // There is a bug in smoothScrollToPositionFromTop that sometimes it doesn't scroll all the way.
        // More info here : https://code.google.com/p/android/issues/detail?id=36062
        // As a workaround, we listen to when it finished scrolling, and then scroll again to
        // the same position.
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    listView.setOnScrollListener(null);
                    listView.smoothScrollToPositionFromTop(position, 0, 500);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
    }

    private int findHourPosition(int hour) {
        int i = 0;

        for (ProgrammeConventionEvent event : events) {
            if (event.getTimeSection().get(Calendar.HOUR_OF_DAY) >= hour) {
                return i;
            }
            i++;
        }

        // If we got here it means the user selected an hour later then the last event. In this case, return the last position (which is i-1, since it's
        // zero based count)
        return i - 1;
    }

    private List<ProgrammeConventionEvent> getEventList() {
        List<ConventionEvent> events = new ArrayList<>(Convention.getInstance().getEvents());
        List<ProgrammeConventionEvent> programmeEvents = new LinkedList<>();

        for (ConventionEvent event : events) {
            // Convert the event start time to hourly time sections, and duplicate it if needed (e.g. if an event started at 13:30 and ended at 15:00, its
            // time sections are 13:00 and 14:00)
            int eventDurationInHours = getEndHour(event.getEndTime()) - getHour(event.getStartTime()) + 1;
            for (int i = 0; i < eventDurationInHours; i++) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(event.getStartTime());
                calendar.add(Calendar.HOUR_OF_DAY, i);
                calendar.clear(Calendar.MINUTE);
                programmeEvents.add(new ProgrammeConventionEvent(event, calendar));
            }
        }

        Collections.sort(programmeEvents, new Comparator<ProgrammeConventionEvent>() {
            @Override
            public int compare(ProgrammeConventionEvent lhs, ProgrammeConventionEvent rhs) {
                // First compare by sections
                int result = lhs.getTimeSection().compareTo(rhs.getTimeSection());
                // In the same section, compare by hall order
                if (result == 0) {
                    result = lhs.getEvent().getHall().getOrder() - rhs.getEvent().getHall().getOrder();
                }
                // For 2 events in the same hall and section, compare by start time
                if (result == 0) {
                    result = lhs.getEvent().getStartTime().compareTo(rhs.getEvent().getStartTime());
                }

                return result;
            }
        });

        return programmeEvents;
    }

    private static int getHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    private static int getEndHour(Date endTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endTime);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

	    // The first minute of the next hour is considered this hour. For example, an event
	    // ending at 12:00 is only considered to run during hour 11:00.
        return minute > 0 ? hour : hour - 1;
    }
}
