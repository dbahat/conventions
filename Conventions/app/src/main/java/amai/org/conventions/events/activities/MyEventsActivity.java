package amai.org.conventions.events.activities;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.events.adapters.DayFragmentAdapter;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.ConventionEventComparator;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.utils.BundleBuilder;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.Dates;
import amai.org.conventions.utils.Views;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ShareCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;


public class MyEventsActivity extends NavigationActivity implements MyEventsDayFragment.EventsListener {
	private static final String TAG = MyEventsActivity.class.getCanonicalName();
	private static final String STATE_SELECTED_DATE_INDEX = "StateSelectedDateIndex";
	private static final int NEXT_EVENT_START_TIME_UPDATE_DELAY = 60000; // 1 minute
	// Handler for updating the next event start text
	private Handler nextEventStartTextRunner = new Handler();
	private Runnable updateNextEventStartTimeText;
	private TextView nextEventStart;
	private View nextEventStartBottomLine;
	private ViewPager daysPager;
	private AlertDialog noEventsDialog;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentInContentContainer(R.layout.activity_my_events);
		setToolbarTitle(getResources().getString(R.string.my_events_title));
		removeContentContainerForeground();

		nextEventStart = (TextView) findViewById(R.id.nextEventStart);
		nextEventStartBottomLine = findViewById(R.id.nextEventStartBottomLine);

		int dateIndexToSelect = savedInstanceState == null ? SELECT_CURRENT_DATE : savedInstanceState.getInt(STATE_SELECTED_DATE_INDEX, SELECT_CURRENT_DATE);
		setupDays(dateIndexToSelect);

		// Handle edge to edge
		Views.registerApplyInsets(Views.InsetType.NONE, Views.InsetType.NONE, Views.InsetType.PADDING, Views.InsetType.PADDING, false, nextEventStart);
	}

	private void setupDays(int dateIndexToSelect) {
		daysPager = findViewById(R.id.my_events_days_pager);
		MyEventsDayAdapter adapter = new MyEventsDayAdapter(getSupportFragmentManager(), Convention.getInstance().getEventDates());
		super.setupDaysTabs(findViewById(R.id.my_events_days_tabs), daysPager, adapter, dateIndexToSelect);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (updateNextEventStartTimeText != null) {
			nextEventStartTextRunner.removeCallbacks(updateNextEventStartTimeText);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Set up text view for next event start
		final List<ConventionEvent> events = getMyEvents();
		setNextEventStartText(events);
	}

	@Override
	public boolean onCreateCustomOptionsMenu(Menu menu) {
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
				FirebaseAnalytics
						.getInstance(this)
						.logEvent("share_clicked", new BundleBuilder()
								.putString("number_of_events", String.valueOf(getMyEvents().size()))
								.build()
						);

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
		// There is a new solution for excluding intents from API level 24
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			return createSharingIntentAPI24();
		}

		// Get available share intents
		List<Intent> targets = new ArrayList<>();
		Intent template = new Intent(Intent.ACTION_SEND)
				.setType("text/plain");
		List<ResolveInfo> candidates = this.getPackageManager()
				.queryIntentActivities(template, 0);

		// Only add included intents
		// Note that in newer versions, this doesn't add all the intents except what we remove,
		// so the newer API is preferred
		for (ResolveInfo candidate : candidates) {
			String packageName = candidate.activityInfo.packageName;
			if (!shouldExcludeIntent(candidate.activityInfo)) {
				Intent shareIntent = new ShareCompat.IntentBuilder(this)
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

	@RequiresApi(api = Build.VERSION_CODES.N)
	private Intent createSharingIntentAPI24() {
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, formatMyEventsToShare(false));
		sendIntent.putExtra(Intent.EXTRA_HTML_TEXT, formatMyEventsToShare(true));
		sendIntent.setType("text/plain");

		Intent shareIntent = Intent.createChooser(sendIntent, getString(R.string.my_event_share_chooser_dialog_title));

		// Get intents to exclude
		ArrayList<ComponentName> targets = new ArrayList<>();
		for (ResolveInfo candidate : this.getPackageManager().queryIntentActivities(sendIntent, 0)) {
			String packageName = candidate.activityInfo.packageName;
			if (shouldExcludeIntent(candidate.activityInfo)) {
				targets.add(new ComponentName(packageName, candidate.activityInfo.name));
			}
		}

		// Exclude intents
		shareIntent.putExtra(Intent.EXTRA_EXCLUDE_COMPONENTS, targets.toArray(new ComponentName[0]));
		return shareIntent;
	}

	private boolean shouldExcludeIntent(ActivityInfo info) {
		// We have to remove facebook since they don't allow sharing text inside the sharing intent,
		// resulting in an empty share (only a link to the app is displayed)
		return info.packageName.equals("com.facebook.katana");
	}

	private String formatMyEventsToShare(boolean isHtml) {
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append(getString(R.string.my_event_share_title, Convention.getInstance().getDisplayName())).append("\n");
		Date eventDate = null;
		for (ConventionEvent event : getMyEvents()) {
			boolean newDate = (eventDate == null || !Dates.isSameDate(eventDate, event.getStartTime()));
			eventDate = event.getStartTime();

			if (newDate) {
				stringBuilder.append("\n").append(Dates.formatDate("EEEE (dd.MM)", eventDate)).append("\n");
			}

			stringBuilder.append(formatEventToShare(event)).append("\n");
		}
		stringBuilder.append("\n");

		if (isHtml) {
			stringBuilder.append(
					String.format(Dates.getLocale(), "<a href=\"%s\">%s</a>",
							getString(R.string.my_event_share_link),
							getString(R.string.my_event_share_signature, getString(R.string.app_name))));
		} else {
			stringBuilder.append(getString(R.string.my_event_share_signature, getString(R.string.app_name)));
			stringBuilder.append("\n");
			stringBuilder.append(getString(R.string.my_event_share_link));
		}

		return stringBuilder.toString();
	}

	private String formatEventToShare(ConventionEvent event) {
		return String.format(Dates.getLocale(), "%s-%s: %s",
				Dates.formatHoursAndMinutes(event.getStartTime()),
				Dates.formatHoursAndMinutes(event.getEndTime()),
				event.getTitle());
	}

	private void setNextEventStartText(final List<ConventionEvent> events) {
		// Remove existing callback
		if (updateNextEventStartTimeText != null) {
			nextEventStartTextRunner.removeCallbacks(updateNextEventStartTimeText);
		}

		ConventionEvent nextEvent = null;
		Date currTime = Dates.now();
		for (ConventionEvent curr : events) {
			// Don't show ongoing events as the next event, since we don't have to go to them when they start
			if (!Convention.getInstance().isEventOngoing(curr) && curr.getStartTime().after(currTime)) {
				nextEvent = curr;
				break;
			}
		}

		// Only display it if it's on the same day
		boolean displayNextEventStart = false;
		if (nextEvent != null) {
			if (Dates.isSameDate(nextEvent.getStartTime(), currTime)) {
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (noEventsDialog != null && noEventsDialog.isShowing()) {
			noEventsDialog.dismiss();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// We must re-set the current page since the rtl view pager has a bug that it doesn't remember it
		outState.putInt(STATE_SELECTED_DATE_INDEX, daysPager.getCurrentItem());
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onEventListChanged() {
		setNextEventStartText(getMyEvents());
	}

	private class MyEventsDayAdapter extends DayFragmentAdapter {
		public MyEventsDayAdapter(FragmentManager fm, Calendar[] eventDates) {
			super(fm, eventDates);
		}

		@Override
		public Fragment getItem(int position) {
			return MyEventsDayFragment.newInstance(getDate(position));
		}
	}
}
