package amai.org.conventions.events.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import amai.org.conventions.ConventionsApplication;
import amai.org.conventions.events.adapters.DayFragmentAdapter;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.ConventionEventComparator;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.Dates;
import amai.org.conventions.utils.Log;
import sff.org.conventions.BuildConfig;
import sff.org.conventions.R;


public class MyEventsActivity extends NavigationActivity implements MyEventsDayFragment.EventsListener {
	private static final String TAG = MyEventsActivity.class.getCanonicalName();
	private static final String STATE_SELECTED_DATE_INDEX = "StateSelectedDateIndex";
	private static final int SELECT_CURRENT_DATE = -1;
	private static final int NEXT_EVENT_START_TIME_UPDATE_DELAY = 60000; // 1 minute
	// Handler for updating the next event start text
	private Handler nextEventStartTextRunner = new Handler();
	private Runnable updateNextEventStartTimeText;
	private TextView nextEventStart;
	private View nextEventStartBottomLine;
	private TabLayout daysTabLayout;
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
		removeForeground();

		nextEventStart = (TextView) findViewById(R.id.nextEventStart);
		nextEventStartBottomLine = findViewById(R.id.nextEventStartBottomLine);

		int dateIndexToSelect = savedInstanceState == null ? SELECT_CURRENT_DATE : savedInstanceState.getInt(STATE_SELECTED_DATE_INDEX, SELECT_CURRENT_DATE);
		setupDays(dateIndexToSelect);

		setupActionButton(R.drawable.ic_add_white, new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				View dialogView = getLayoutInflater().inflate(R.layout.login_layout, null, false);
				final EditText userNameTextView = dialogView.findViewById(R.id.user_name);
				final EditText passwordTextView = dialogView.findViewById(R.id.password);
				AlertDialog dialog = new AlertDialog.Builder(MyEventsActivity.this)
						.setTitle(R.string.add_events)
						.setMessage(R.string.add_events_instructions)
						.setView(dialogView)
						.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								final String user = userNameTextView.getText().toString();
								final String password = passwordTextView.getText().toString();
								final ProgressDialog progressDialog = new ProgressDialog(MyEventsActivity.this);
								progressDialog.setMessage(getString(R.string.loading_events));
								progressDialog.setCancelable(false);
								progressDialog.setCanceledOnTouchOutside(false);
								progressDialog.show();
								new AsyncTask<Void, Void, Exception>() {
									private int newFavoriteEventsNumber = 0;
									@Override
									protected Exception doInBackground(Void... params) {
										try {
											newFavoriteEventsNumber = addFavoriteEventsFromWebsite(user, password);
											return null;
										} catch (Exception e) {
											return e;
										}
									}

									@Override
									protected void onPostExecute(Exception exception) {
										progressDialog.dismiss();
										ConventionsApplication.sendTrackingEvent(new HitBuilders.EventBuilder()
												.setCategory("Favorites")
												.setAction(exception == null ? "success" : "failure")
												.setLabel("AddFromWebsite")
												.build());

										if (exception == null) {
											// Refresh favorite events in all days
											for (int i = 0; i < daysPager.getAdapter().getCount(); ++i) {
												MyEventsDayFragment fragment = (MyEventsDayFragment) daysPager.getAdapter().instantiateItem(daysPager, i);
												fragment.updateDataset();
											}
											setNextEventStartText(getMyEvents());
											String message;
											if (newFavoriteEventsNumber == 0) {
												message = getString(R.string.no_events_added);
											} else if (newFavoriteEventsNumber == 1) {
												message = getString(R.string.one_event_added);
											} else {
												message = getString(R.string.several_events_added, newFavoriteEventsNumber);
											}
											Toast.makeText(MyEventsActivity.this, message, Toast.LENGTH_LONG).show();
										} else {
											int messageId = R.string.add_events_failed;
											if (exception instanceof AuthenticationException) {
												messageId = R.string.wrong_user_or_password;
											}
											Log.e(TAG, exception.getMessage(), exception);
											Toast.makeText(MyEventsActivity.this, messageId, Toast.LENGTH_LONG).show();
										}
									}
								}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
							}
						})
						.setNegativeButton(R.string.cancel, null)
						.create();
				dialog.setOnShowListener(new DialogInterface.OnShowListener() {
					@Override
					public void onShow(DialogInterface dialogInterface) {
						userNameTextView.requestFocus();
					}
				});
				dialog.show();
			}
		});
	}

	private int addFavoriteEventsFromWebsite(String user, String password) throws IOException {
		HttpURLConnection request = Convention.getInstance().getUserPurchasedEventsRequest(user, password);
		request.connect();
		InputStreamReader reader = null;
		boolean changed = false;
		int newFavoriteEvents = 0;
		try {
			int responseCode = request.getResponseCode();
			if (responseCode == 400 || responseCode == 401) {
				throw new AuthenticationException();
			} else if (responseCode != 200) {
				if (BuildConfig.DEBUG) {
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(request.getErrorStream()));
					StringBuilder responseBuilder = new StringBuilder();
					String output;
					while ((output = bufferedReader.readLine()) != null) {
						responseBuilder.append(output);
					}
					String responseBody = responseBuilder.toString();
					Log.e(TAG, "Could not read user purchased events, response is: " + responseBody);
				}
				throw new RuntimeException("Could not read user purchased events, error code: " + responseCode);
			}
			reader = new InputStreamReader((InputStream) request.getContent());
			JsonParser jp = new JsonParser();
			JsonElement root = jp.parse(reader);
			JsonArray eventsArray = root.getAsJsonArray();
			for (int i = 0; i < eventsArray.size(); ++i) {
				int eventServerId = eventsArray.get(i).getAsInt();
				ConventionEvent event = Convention.getInstance().findEventByServerId(eventServerId);
				if (event != null && !event.isAttending()) {
					event.setAttending(true);
					++newFavoriteEvents;
				}
				// TODO this should be inside the if !event.isAttending(), but due to a bug
				// in version 2.2.1 the alarms were not scheduled and the events were not registered
				// for notifications
				if (event != null) {
					changed = true;
					ConventionsApplication.alarmScheduler.scheduleDefaultEventAlarms(event);
					// TODO - Register / un-register the event from the per-event category here
//					AzurePushNotifications notifications = new AzurePushNotifications(this);
//					notifications.registerAsync(new AzurePushNotifications.RegistrationListener.DoNothing());
				}
			}
			if (changed) {
				Convention.getInstance().getStorage().saveUserInput();
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
			request.disconnect();
		}
		return newFavoriteEvents;
	}

	private void setupDays(int dateIndexToSelect) {
		daysTabLayout = (TabLayout) findViewById(R.id.my_events_days_tabs);
		daysPager = (ViewPager) findViewById(R.id.my_events_days_pager);
		Calendar startDate = Convention.getInstance().getStartDate();
		Calendar endDate = Convention.getInstance().getEndDate();

		int days = (int) ((endDate.getTime().getTime() - startDate.getTime().getTime()) / Dates.MILLISECONDS_IN_DAY) + 1;
		if (days == 1) {
			daysTabLayout.setVisibility(View.GONE);
		}

		// Setup view pager
		daysPager.setAdapter(new MyEventsDayAdapter(getSupportFragmentManager(), Convention.getInstance().getStartDate(), days));
		daysPager.setOffscreenPageLimit(days); // Load all dates for smooth scrolling

		// Setup tabs
		daysTabLayout.setupWithViewPager(daysPager, false);

		int selectedDateIndex = dateIndexToSelect;
		// Find the current date's index if requested
		if (dateIndexToSelect == SELECT_CURRENT_DATE) {
			Calendar currDate = Calendar.getInstance();
			Calendar today = Dates.toCalendar(Dates.now());
			int i = 0;
			for (currDate.setTime(startDate.getTime()); !currDate.after(endDate); currDate.add(Calendar.DATE, 1), ++i) {
				if (Dates.isSameDate(currDate, today)) {
					selectedDateIndex = i;
				}
			}
		}

		// Default - first day
		if (selectedDateIndex < 0) {
			selectedDateIndex = 0;
		}
		daysPager.setCurrentItem(selectedDateIndex, false);
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

		stringBuilder.append(getString(R.string.my_event_share_title, Convention.getInstance().getDisplayName())).append("\n");
		Date eventDate = null;
		for (ConventionEvent event : getMyEvents()) {
			boolean newDate = (eventDate == null || !Dates.isSameDate(eventDate, event.getStartTime()));
			eventDate = event.getStartTime();

			if (newDate) {
				SimpleDateFormat sdf = new SimpleDateFormat("EEEE (dd.MM)", Dates.getLocale());
				stringBuilder.append("\n").append(sdf.format(eventDate)).append("\n");
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
		return String.format(Dates.getLocale(), "%s: %s",
				Dates.formatHoursAndMinutes(event.getStartTime()),
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
			if (curr.getStartTime().after(currTime)) {
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
		public MyEventsDayAdapter(FragmentManager fm, Calendar startDate, int days) {
			super(fm, startDate, days);
		}

		@Override
		public Fragment getItem(int position) {
			return MyEventsDayFragment.newInstance(getDate(position));
		}
	}

	private static class AuthenticationException extends RuntimeException {}
}
