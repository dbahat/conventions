package amai.org.conventions;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.FacebookRequestError;
import com.microsoft.windowsazure.notifications.NotificationsManager;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import amai.org.conventions.events.activities.ProgrammeActivity;
import amai.org.conventions.model.Convention;
import amai.org.conventions.model.Update;
import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.navigation.NavigationPages;
import amai.org.conventions.networking.ModelRefresher;
import amai.org.conventions.networking.UpdatesRefresher;
import amai.org.conventions.notifications.AzureNotificationRegistrationService;
import amai.org.conventions.notifications.AzurePushNotifications;
import amai.org.conventions.notifications.PlayServicesInstallation;
import amai.org.conventions.notifications.PushNotificationHandler;
import amai.org.conventions.settings.SettingsActivity;
import amai.org.conventions.updates.UpdatesActivity;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.Views;

public class HomeActivity extends AppCompatActivity {
	public final static String EXTRA_PUSH_NOTIFICATION_MESSAGE = "EXTRA_PUSH_NOTIFICATION_MESSAGE";
	public final static String EXTRA_PUSH_NOTIFICATION_TAG = "EXTRA_PUSH_NOTIFICATION_TAG";

	private static final String SETTINGS_POPUP_DISPLAYED = "SETTINGS_POPUP_DISPLAYED";
	private static final int NEW_UPDATES_NOTIFICATION_ID = 75457;
	private static int numberOfTimesNavigated = 0;
	private NavigationPages navigationPages;
	private AlertDialog pushNotificationDialog;
	private AlertDialog configureNotificationDialog;

	public static int getNumberOfTimesNavigated() {
		return numberOfTimesNavigated;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(ThemeAttributes.getResourceId(this, R.attr.homeScreenLayout));
		PreferenceManager.setDefaultValues(this, R.xml.settings_preferences, false);

		showPushNotificationDialog(getIntent());

		navigationPages = new NavigationPages(this);

		// Initiate async downloading of the updated convention info in the background
		new AsyncTask<Void, Void, PlayServicesInstallation.CheckResult>() {
			@Override
			protected PlayServicesInstallation.CheckResult doInBackground(Void... params) {
				ModelRefresher modelRefresher = new ModelRefresher();
				modelRefresher.refreshFromServer();

				PlayServicesInstallation.CheckResult checkResult = PlayServicesInstallation.checkPlayServicesExist(HomeActivity.this, false);
				if (checkResult.isSuccess()) {
					NotificationsManager.handleNotifications(HomeActivity.this, AzurePushNotifications.SENDER_ID, PushNotificationHandler.class);
				}
				return checkResult;
			}

			@Override
			protected void onPostExecute(PlayServicesInstallation.CheckResult checkResult) {
				// We need to check isFinishing to make sure we don't display a dialog after the activity
				// is destroyed since it causes an exception
				if (checkResult.isUserError() && !isFinishing()) {
					PlayServicesInstallation.showInstallationDialog(HomeActivity.this, checkResult);
				} else if (checkResult.isSuccess()) {
					// Start IntentService to register this application with GCM.
					Intent intent = new Intent(HomeActivity.this, AzureNotificationRegistrationService.class);
					startService(intent);

					showConfigureNotificationsDialog();
				}
			}
		}.execute();

		new Handler().post(new Runnable() {
			@Override
			public void run() {
				// Requests to Facebook must be initialized from the UI thread
				final int numberOfUpdatesBeforeRefresh = Convention.getInstance().getUpdates().size();

				// Refresh and ignore all errors
				UpdatesRefresher.getInstance(HomeActivity.this).refreshFromServer(null, true, new UpdatesRefresher.OnUpdateFinishedListener() {
					@Override
					public void onSuccess(int newUpdatesNumber) {
						List<Update> newUpdates = CollectionUtils.filter(Convention.getInstance().getUpdates(), new CollectionUtils.Predicate<Update>() {
							@Override
							public boolean where(Update item) {
								return item.isNew();
							}
						});

						// We don't want to raise the notification if there are no new updates, or if this is the first time updates are downloaded to cache.
						if (newUpdatesNumber > 0 && newUpdates.size() > 0 && numberOfUpdatesBeforeRefresh > 0
								&& UpdatesRefresher.getInstance(HomeActivity.this).shouldEnableNotificationAfterUpdate()) {

							Update latestUpdate = Collections.max(newUpdates, new Comparator<Update>() {
								@Override
								public int compare(Update lhs, Update rhs) {
									return lhs.getDate().compareTo(rhs.getDate());
								}
							});

							String notificationTitle = newUpdates.size() == 1
									? getString(R.string.new_update)
									: getString(R.string.new_updates, newUpdates.size());

							String notificationMessage = latestUpdate.getText().substring(0, Math.min(200, latestUpdate.getText().length())) + "...";

							NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
							Intent intent = new Intent(HomeActivity.this, UpdatesActivity.class);
							Notification.Builder notificationBuilder = new Notification.Builder(HomeActivity.this)
									.setSmallIcon(ThemeAttributes.getResourceId(HomeActivity.this, R.attr.notificationSmallIcon))
									.setLargeIcon(ImageHandler.getNotificationLargeIcon(HomeActivity.this))
									.setContentTitle(notificationTitle)
									.setContentText(notificationMessage)
									.setAutoCancel(true)
									.setContentIntent(PendingIntent.getActivity(HomeActivity.this, 0, intent, 0))
									.setDefaults(Notification.DEFAULT_VIBRATE);


							Notification notification = new Notification.BigTextStyle(notificationBuilder)
									.bigText(notificationMessage)
									.build();

							notificationManager.notify(NEW_UPDATES_NOTIFICATION_ID, notification);
						}
					}

					@Override
					public void onError(FacebookRequestError error) {
					}

					@Override
					public void onInvalidTokenError() {
					}
				});
			}
		});
	}

	private void showPushNotificationDialog(Intent intent) {
		// If we got here from a push notification, show it in a popup
		String pushMessage = intent.getStringExtra(EXTRA_PUSH_NOTIFICATION_MESSAGE);
		if (pushMessage != null) {
			// Allow links
			final SpannableString messageWithLinks = new SpannableString(pushMessage);
			Linkify.addLinks(messageWithLinks, Linkify.WEB_URLS);

			String pushTitle = intent.getStringExtra(EXTRA_PUSH_NOTIFICATION_TAG);

			pushNotificationDialog = new AlertDialog.Builder(this)
					.setTitle(pushTitle == null ? getString(R.string.push_notification_title) : pushTitle)
					.setMessage(messageWithLinks)
					.setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							pushNotificationDialog.hide();
						}
					})
					.setNeutralButton(R.string.change_settings, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							pushNotificationDialog.hide();
							navigateTo(SettingsActivity.class);
						}
					})
					.setCancelable(true)
					.show();
			// Make the view clickable so it can follow links
			// Using only the spannable text doesn't allow it...
			View messageView = pushNotificationDialog.findViewById(android.R.id.message);
			if (messageView instanceof TextView) {
				TextView textView = (TextView) messageView;
				textView.setMovementMethod(LinkMovementMethod.getInstance());
			}
		}
	}

	private void showConfigureNotificationsDialog() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		if (!sharedPreferences.getBoolean(SETTINGS_POPUP_DISPLAYED, false)) {
			configureNotificationDialog = new AlertDialog.Builder(this)
					.setTitle(R.string.configure_notifications)
					.setMessage(R.string.configure_notifications_dialog_message)
					.setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							configureNotificationDialog.hide();
						}
					})
					.setNeutralButton(R.string.change_settings, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							configureNotificationDialog.hide();
							navigateTo(SettingsActivity.class);
						}
					})
					.setCancelable(true)
					.show();
			sharedPreferences.edit().putBoolean(SETTINGS_POPUP_DISPLAYED, true).apply();
		}

	}

	public void onNavigationButtonClicked(View view) {
		++numberOfTimesNavigated;
		int position = Integer.parseInt(view.getTag().toString());
		Class<? extends Activity> activityType = navigationPages.getActivityType(position);
		navigateTo(activityType);
	}

	private void navigateTo(Class<? extends Activity> activityType) {
		Intent intent = new Intent(this, activityType);
		Bundle extras = new Bundle();
		extras.putBoolean(NavigationActivity.EXTRA_NAVIGATED_FROM_HOME, true);

		if (activityType == ProgrammeActivity.class) {
			extras.putInt(ProgrammeActivity.EXTRA_DELAY_SCROLLING, 500);
		}

		intent.putExtras(extras);
		ActivityOptions options = ActivityOptions.makeCustomAnimation(this, 0, R.anim.shrink_to_top_right);
		startActivity(intent, options.toBundle());
	}

	public void onAboutClicked(View view) {
		ViewGroup mainLayout = (ViewGroup) findViewById(R.id.home_main_layout);
		Point coordinates;

		// Top
		View topView = findViewById(ThemeAttributes.getResourceId(this, R.attr.aboutTopView));
		coordinates = Views.findCoordinates(mainLayout, topView);
		int top = coordinates.y;

		// Bottom
		int bottomViewId = ThemeAttributes.getResourceId(this, R.attr.aboutBottomView);
		int height;
		if (bottomViewId == 0) {
			height = ViewGroup.LayoutParams.WRAP_CONTENT;
		} else {
			View bottomView = findViewById(bottomViewId);
			coordinates = Views.findCoordinates(mainLayout, bottomView);
			int bottom = coordinates.y + bottomView.getMeasuredHeight();
			height = bottom - top;
		}

		// Left
		View leftView = findViewById(ThemeAttributes.getResourceId(this, R.attr.aboutLeftView));
		coordinates = Views.findCoordinates(mainLayout, leftView);
		int left = coordinates.x;

		// Right
		View rightView = findViewById(ThemeAttributes.getResourceId(this, R.attr.aboutRightView));
		coordinates = Views.findCoordinates(mainLayout, rightView);
		int right = coordinates.x + rightView.getMeasuredWidth();
		int width = right - left;

		AboutFragment aboutFragment = new AboutFragment();
		aboutFragment.setLocation(left, top, width, height);
		aboutFragment.show(getSupportFragmentManager(), null);
	}
}
