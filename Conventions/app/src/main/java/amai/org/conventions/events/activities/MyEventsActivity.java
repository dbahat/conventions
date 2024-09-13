package amai.org.conventions.events.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import amai.org.conventions.ConventionsApplication;
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.auth.AuthorizationActivity;
import amai.org.conventions.events.adapters.DayFragmentAdapter;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.ConventionEventComparator;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.networking.AuthenticationException;
import amai.org.conventions.networking.RequestConnector;
import amai.org.conventions.notifications.PushNotificationTopicsSubscriber;
import amai.org.conventions.utils.BundleBuilder;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.Dates;
import amai.org.conventions.utils.Log;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ShareCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import sff.org.conventions.BuildConfig;
import sff.org.conventions.R;


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
	private Menu menu;
	private ExecutorService executor;
	private View userIdDialogView;

	private final static float UNKNOWN_BRIGHTNESS = 2; // Brightness can be <0 for preferred or 0-1 for specific brightness
	private float screenBrightness = UNKNOWN_BRIGHTNESS;

	// Authentication result handlers
	private final ActivityResultLauncher<Intent> userDetailsAuthResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::userDetailsAuthResult);
	private final ActivityResultLauncher<Intent> addEventsAuthResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::addEventsAuthResult);
	private final ActivityResultLauncher<Intent> logoutResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::logoutResult);
	private final ActivityResultLauncher<Intent> userIdAuthResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::userIdAuthResult);

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

		executor = Executors.newSingleThreadExecutor();

		nextEventStart = (TextView) findViewById(R.id.nextEventStart);
		nextEventStartBottomLine = findViewById(R.id.nextEventStartBottomLine);

		int dateIndexToSelect = savedInstanceState == null ? SELECT_CURRENT_DATE : savedInstanceState.getInt(STATE_SELECTED_DATE_INDEX, SELECT_CURRENT_DATE);
		setupDays(dateIndexToSelect);

		if (Convention.getInstance().canUserLogin()) {
			setupActionButton(ThemeAttributes.getDrawable(this, R.attr.addActionButtonIcon), view -> addEvents());
		}
	}

	private void handleAuthResult(ActivityResult activityResult, boolean ignoreUserUpdateError, GetProgressMessage getProgressMessage, OnAuthRequestCompleted onSuccess) {
		AuthorizationActivity.SignInResult result = AuthorizationActivity.SignInResult.fromActivityResult(activityResult);
		if (result.userCancelled) {
			return;
		}

		if (result.exception != null) {
			Log.e(TAG, "Could not get user token: " + result.exception.getMessage(), result.exception);
			Toast.makeText(MyEventsActivity.this, R.string.login_failed, Toast.LENGTH_LONG).show();
		} else if (result.email == null) {
			Log.e(TAG, "email did not return from AuthorizationActivity");
			Toast.makeText(MyEventsActivity.this, R.string.login_failed, Toast.LENGTH_LONG).show();
		} else {
			final ProgressDialog progressDialog = new ProgressDialog(MyEventsActivity.this);
			progressDialog.setMessage(getProgressMessage.run(result.email));
			progressDialog.setCancelable(false);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.show();
			executor.submit(() -> {
				boolean stopExecution = false;
				try {
					updateUserDetails(result.email, result.accessToken);
				} catch (Exception e) {
					Log.e(TAG, "Could not update user details: " + e.getMessage(), e);
					if( !ignoreUserUpdateError) {
						stopExecution = true;
					}
				}

				if (!stopExecution) {
					onSuccess.run(result.email, result.accessToken, ignoreErrors(progressDialog::dismiss));
				} else {
					ignoreErrors(progressDialog::dismiss).run();
					// Can't show a toast while there is a progress dialog open
					Toast.makeText(MyEventsActivity.this, R.string.login_failed, Toast.LENGTH_LONG).show();
				}
			});
		}
	}

	private void loginAndShowUserDetails() {
		Intent intent = new Intent(this, AuthorizationActivity.class);
		userDetailsAuthResultLauncher.launch(intent);
	}


	private void userDetailsAuthResult(ActivityResult activityResult) {
		handleAuthResult(activityResult, false, (user) -> getString(R.string.updating_user_details), (user, token, afterRequestCompleted) -> {
			afterRequestCompleted.run();
			FirebaseAnalytics
					.getInstance(MyEventsActivity.this)
					.logEvent("favorites", new BundleBuilder()
							.putString("action", "success")
							.putString("label", "ShowUserDetails")
							.build()
					);
			runOnUiThread(() -> {
				changeIconColor(menu.findItem(R.id.my_events_show_user_id));
				showUserDetailsDialog(null);
			});
		});
	}

	private void addEvents() {
		Intent intent = new Intent(this, AuthorizationActivity.class);
		addEventsAuthResultLauncher.launch(intent);
	}

	private void addEventsAuthResult(ActivityResult activityResult) {
		// Ignore user details update errors and still add the events
		handleAuthResult(activityResult, true, (user) -> getString(R.string.loading_events_for, user), this::addEventsWithToken);
	}

	private Runnable ignoreErrors(Runnable r) {
		return () -> {
			try {
				r.run();
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}
		};
	}

	@WorkerThread
	private void updateUserDetails(String user, String token) throws Exception {
		String oldUser = ConventionsApplication.settings.getUser();
		// No need to update the user data if it's the same user
		if (!Objects.equals(oldUser, user)) {
			// Clear previous user data
			ConventionsApplication.settings.setUser(null);
			ConventionsApplication.settings.setUserId(null);
			Convention.getInstance().getStorage().deleteUserIDQR();

			// Set new user data
			ConventionsApplication.settings.setUser(user);
			saveUserQR(token, user);
			saveUserId(token);
		} else if (ConventionsApplication.settings.getUserId() == null) {
			// Update only the user ID if it was not available before
			saveUserId(token);
		}
	}

	@WorkerThread
	private void addEventsWithToken(String user, String token, Runnable afterRequestCompleted) {
		int newFavoriteEventsNumber1 = 0;
		Exception addEventsException1 = null;
		try {
			newFavoriteEventsNumber1 = addFavoriteEventsFromWebsite(token);
		} catch (Exception e) {
			addEventsException1 = e;
		}

		int newFavoriteEventsNumber = newFavoriteEventsNumber1;
		Exception addEventsException = addEventsException1;

		runOnUiThread(() -> {
			afterRequestCompleted.run();
			FirebaseAnalytics
					.getInstance(MyEventsActivity.this)
					.logEvent("favorites", new BundleBuilder()
							.putString("action", addEventsException == null ? "success" : "failure")
							.putString("label", "AddFromWebsite")
							.build()
					);

			if (addEventsException == null) {
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

				if (user == null || user.isEmpty()) {
					Toast.makeText(MyEventsActivity.this, message, Toast.LENGTH_LONG).show();
				} else {
					changeIconColor(menu.findItem(R.id.my_events_show_user_id));
					showUserDetailsDialog(message);
				}
			} else {
				int messageId = R.string.add_events_failed;
				if (addEventsException instanceof AuthenticationException) {
					messageId = R.string.wrong_user_or_password;
				}
				Log.e(TAG, "Could not add events for logged in user: " + addEventsException.getMessage(), addEventsException);
				Toast.makeText(MyEventsActivity.this, messageId, Toast.LENGTH_LONG).show();
			}
		});
	}

	private void showUserDetailsDialog(String firstLineMessage) {
		String user = ConventionsApplication.settings.getUser();
		if (user != null && !user.isEmpty()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(MyEventsActivity.this);
			View dialogView = View.inflate(builder.getContext(), R.layout.user_id_layout, null);
			TextView messageView = dialogView.findViewById(R.id.message);
			TextView userIdInst = dialogView.findViewById(R.id.show_user_id_inst);
			SwitchMaterial brightnessSwitch = dialogView.findViewById(R.id.increase_brightness_toggle);

			if (firstLineMessage != null && !firstLineMessage.isEmpty()) {
				messageView.setText(firstLineMessage);
			} else {
				messageView.setVisibility(View.GONE);
				dialogView.findViewById(R.id.separator).setVisibility(View.GONE);
			}

			ImageView userIdQRImageView = dialogView.findViewById(R.id.user_id_qr);
			TextView userIdQRErrorText = dialogView.findViewById(R.id.user_id_qr_error);
			InputStream userQRInputStream = Convention.getInstance().getStorage().getUserQRInputStream();
			if (userQRInputStream == null) {
				userIdQRErrorText.setVisibility(View.VISIBLE);
				userIdQRImageView.setVisibility(View.GONE);
			} else {
				userIdQRErrorText.setVisibility(View.GONE);
				userIdQRImageView.setVisibility(View.VISIBLE);
				userIdQRImageView.setImageDrawable(new BitmapDrawable(getResources(), userQRInputStream));
			}

			TextView userView = dialogView.findViewById(R.id.user);
			userView.setText(getString(R.string.inst_user, user));

			setupUserId(dialogView);

			userIdInst.setText(Html.fromHtml(getString(R.string.inst_user_id_in_toolbar), source -> {
				Drawable drawable = null;
				if ("iconUserId".equals(source)) {
					drawable = ThemeAttributes.getDrawable(MyEventsActivity.this, R.attr.iconUserId);
				}
				if (drawable != null) {
					drawable = drawable.mutate();
					drawable.setColorFilter(userIdInst.getCurrentTextColor(), PorterDuff.Mode.SRC_ATOP);
					drawable.setBounds(0, 0, userIdInst.getLineHeight(), userIdInst.getLineHeight());
				}
				return drawable;
			}, null));

			brightnessSwitch.setChecked(false);
			brightnessSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
				if (isChecked) {
					increaseScreenBrightness();
				} else {
					resetScreenBrightness();
				}
			});

			AlertDialog dialog = builder
					.setView(dialogView)
					.setPositiveButton(R.string.close, null)
					.setNegativeButton(R.string.logout, (dialog1, which) -> {
						Intent intent = new Intent(this, AuthorizationActivity.class);
						intent.putExtra(AuthorizationActivity.PARAM_SIGN_OUT, true);
						logoutResultLauncher.launch(intent);
					})
					.create();
			dialog.setOnDismissListener(dialog1 -> resetScreenBrightness());
			dialog.show();

			int logoutButtonColor = ThemeAttributes.getColor(this, R.attr.logoutButtonTextColor);
			if (logoutButtonColor != Color.TRANSPARENT) {
				dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(logoutButtonColor);
			}
		}
	}

	private void increaseScreenBrightness() {
		WindowManager.LayoutParams layout = getWindow().getAttributes();
		Log.i(TAG, "current brightness: " + layout.screenBrightness);
		if (layout.screenBrightness < 1) {
			Log.i(TAG, "setting brightness to 1");
			this.screenBrightness = layout.screenBrightness;
			layout.screenBrightness = 1;
			getWindow().setAttributes(layout);
		}
	}

	private void resetScreenBrightness() {
		Log.i(TAG, "saved brightness: " + this.screenBrightness);
		if (this.screenBrightness != UNKNOWN_BRIGHTNESS) {
			Log.i(TAG, "resetting brightness");
			WindowManager.LayoutParams layout = getWindow().getAttributes();
			layout.screenBrightness = this.screenBrightness;
			this.screenBrightness = UNKNOWN_BRIGHTNESS;
			getWindow().setAttributes(layout);
		}
	}

	private void setupUserId(View dialogView) {
		String userId = ConventionsApplication.settings.getUserId();
		TextView userIdView = dialogView.findViewById(R.id.user_id);
		TextView noUserIdView = dialogView.findViewById(R.id.no_user_id);

		if (userId != null && !userId.isEmpty()) {
			noUserIdView.setVisibility(View.GONE);
			userIdView.setVisibility(View.VISIBLE);
			userIdView.setText(getString(R.string.inst_user_id, userId));
		} else {
			userIdView.setVisibility(View.GONE);
			noUserIdView.setVisibility(View.VISIBLE);

			// Handle the link click
			SpannableStringBuilder spanBuilder = new SpannableStringBuilder(Html.fromHtml(getString(R.string.inst_display_user_id)));
			URLSpan[] spans = spanBuilder.getSpans(0, spanBuilder.length(), URLSpan.class);
			for (URLSpan span : spans) {
				spanBuilder.setSpan(new ClickableSpan() {
					@Override
					public void onClick(@NonNull View widget) {
						// There is only one link, but checking it anyway
						if ("showUserId".equals(span.getURL())) {
							userIdDialogView = dialogView;
							Intent intent = new Intent(MyEventsActivity.this, AuthorizationActivity.class);
							userIdAuthResultLauncher.launch(intent);
						}
					}
				}, spanBuilder.getSpanStart(span), spanBuilder.getSpanEnd(span), spanBuilder.getSpanFlags(span));
				spanBuilder.removeSpan(span);
			}
			noUserIdView.setText(spanBuilder);
			noUserIdView.setMovementMethod(LinkMovementMethod.getInstance());
			noUserIdView.setHighlightColor(Color.TRANSPARENT);
		}
	}

	private void userIdAuthResult(ActivityResult activityResult) {
		handleAuthResult(activityResult, false, user -> getString(R.string.updating_user_details), (user, token, afterRequestCompleted) -> {
			afterRequestCompleted.run();
			runOnUiThread(() -> {
				if (userIdDialogView != null) {
					setupUserId(userIdDialogView);
					userIdDialogView = null;
				}
			});
		});
	}

	private void logoutResult(ActivityResult result) {
		if (result.getResultCode() == RESULT_OK) {
			ConventionsApplication.settings.setUser(null);
			ConventionsApplication.settings.setUserId(null);
			Convention.getInstance().getStorage().deleteUserIDQR();
			resetIconColor(menu.findItem(R.id.my_events_show_user_id));
		} else {
			Log.e(TAG, "could not log out: " + result.getData());
		}
	}

	private int addFavoriteEventsFromWebsite(String token) throws Exception {
		RequestConnector request = new RequestConnector(Convention.getInstance().getUserPurchasedEventsRequest(token));
		InputStreamReader reader = null;
		boolean changed = false;
		int newFavoriteEvents = 0;
		try {
			request.connect();
			int responseCode = request.getRequest().getResponseCode();
			if (BuildConfig.DEBUG && responseCode != 200) {
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(request.getRequest().getErrorStream()));
				StringBuilder responseBuilder = new StringBuilder();
				String output;
				while ((output = bufferedReader.readLine()) != null) {
					responseBuilder.append(output);
				}
				String responseBody = responseBuilder.toString();
				Log.e(TAG, "Could not read user purchased events, response code: " + responseCode + ", response is: " + responseBody);
			}
			if (responseCode == 400 || responseCode == 401) {
				throw new AuthenticationException();
			} else if (responseCode != 200) {
				throw new RuntimeException("Could not read user purchased events, error code: " + responseCode);
			}
			reader = new InputStreamReader((InputStream) request.getRequest().getContent());
			JsonElement root = JsonParser.parseReader(reader);
			JsonArray eventsArray = root.getAsJsonArray();
			for (int i = 0; i < eventsArray.size(); ++i) {
				int eventServerId = eventsArray.get(i).getAsInt();
				ConventionEvent event = Convention.getInstance().findEventByServerId(eventServerId);
				if (event != null && !event.isAttending()) {
					event.setAttending(true);
					changed = true;
					ConventionsApplication.alarmScheduler.scheduleDefaultEventAlarms(event);
					PushNotificationTopicsSubscriber.subscribe(event);
					++newFavoriteEvents;
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

	private void saveUserId(String token) throws Exception {
		RequestConnector request = new RequestConnector(Convention.getInstance().getUserIDRequest(token));
		InputStream inputStream = null;
		BufferedReader reader = null;
		String userId = null;
		try {
			request.connect();
			int responseCode = request.getRequest().getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
				throw new AuthenticationException();
			} else if (responseCode != HttpURLConnection.HTTP_OK) {
				if (BuildConfig.DEBUG) {
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(request.getRequest().getErrorStream()));
					StringBuilder responseBuilder = new StringBuilder();
					String output;
					while ((output = bufferedReader.readLine()) != null) {
						responseBuilder.append(output);
					}
					String responseBody = responseBuilder.toString();
					Log.e(TAG, "Could not read user ID, response is: " + responseBody);
				}

				if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
					// User has not connected to the programme yet, user ID is not available
					return;
				}

				throw new RuntimeException("Could not read user ID, error code: " + responseCode);
			}
			inputStream = (InputStream) request.getRequest().getContent();
			reader = new BufferedReader(new InputStreamReader(inputStream));
			StringBuilder responseBuilder = new StringBuilder();
			String output;
			while ((output = reader.readLine()) != null) {
				responseBuilder.append(output);
			}
			userId = responseBuilder.toString();
			ConventionsApplication.settings.setUserId(userId);
		} catch (Exception e) {
			// Don't throw an error, we still want to show the user details even if there is no ID
			Log.e(TAG, "Could not read user ID", e);
		} finally {
			if (reader != null) {
				reader.close();
			}
			if (inputStream != null) {
				inputStream.close();
			}
			request.disconnect();
		}
	}

	private void saveUserQR(String token, String user) throws Exception {
		// First, delete the existing file (since a new user ID was set, we don't want leftovers from the previous user ID)
		Convention.getInstance().getStorage().deleteUserIDQR();

		RequestConnector request = new RequestConnector(Convention.getInstance().getUserQRRequest(token, user));
		InputStream inputStream = null;
		try {
			// This should be inside the try-catch because it can throw an exception
			request.connect();
			int responseCode = request.getRequest().getResponseCode();
			if (responseCode != 200) {
				if (BuildConfig.DEBUG) {
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(request.getRequest().getErrorStream()));
					StringBuilder responseBuilder = new StringBuilder();
					String output;
					while ((output = bufferedReader.readLine()) != null) {
						responseBuilder.append(output);
					}
					String responseBody = responseBuilder.toString();
					Log.e(TAG, "Could not read user QR, error code: " + responseCode + ", response is: " + responseBody);
				}
				throw new RuntimeException("Could not read user QR, error code: " + responseCode);
			}
			inputStream = (InputStream) request.getRequest().getContent();
			Convention.getInstance().getStorage().saveUserIDQR(inputStream);
		} catch (Exception e) {
			// Don't throw an error, we still want to show the user details even if there is no QR
			Log.e(TAG, "Could not read user QR", e);
		} finally {
			request.disconnect();
		}
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
		this.menu = menu;

		if (Convention.getInstance().canUserLogin()) {
			String user = ConventionsApplication.settings.getUser();
			if (user != null && !user.isEmpty()) {
				changeIconColor(menu.findItem(R.id.my_events_show_user_id));
			}
		} else {
			this.menu.removeItem(R.id.my_events_show_user_id);
		}

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
			case R.id.my_events_show_user_id:
				FirebaseAnalytics
						.getInstance(MyEventsActivity.this)
						.logEvent("show_user_id_clicked", null);
				String user = ConventionsApplication.settings.getUser();
				if (user != null && !user.isEmpty()) {
					showUserDetailsDialog(null);
				} else {
					loginAndShowUserDetails();
				}

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
		public MyEventsDayAdapter(FragmentManager fm, Calendar[] eventDates) {
			super(fm, eventDates);
		}

		@Override
		public Fragment getItem(int position) {
			return MyEventsDayFragment.newInstance(getDate(position));
		}
	}

	private interface OnAuthRequestCompleted {
		void run(String user, String token, Runnable afterRequestCompleted);
	}

	private interface GetProgressMessage {
		String run(String user);
	}
}
