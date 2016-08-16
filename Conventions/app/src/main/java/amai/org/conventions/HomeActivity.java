package amai.org.conventions;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.FacebookRequestError;
import com.microsoft.windowsazure.notifications.NotificationsManager;

import java.util.Calendar;
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
import amai.org.conventions.notifications.PushNotificationTopic;
import amai.org.conventions.settings.SettingsActivity;
import amai.org.conventions.updates.UpdatesActivity;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.Dates;
import amai.org.conventions.utils.Settings;
import amai.org.conventions.utils.Views;

public class HomeActivity extends AppCompatActivity {
	public final static String EXTRA_PUSH_NOTIFICATION_MESSAGE = "EXTRA_PUSH_NOTIFICATION_MESSAGE";
	public final static String EXTRA_PUSH_NOTIFICATION_CATEGORY = "EXTRA_PUSH_NOTIFICATION_CATEGORY";
	public static final String EXTRA_PUSH_NOTIFICATION_ID = "EXTRA_PUSH_NOTIFICATION_ID";

	private static final int NEW_UPDATES_NOTIFICATION_ID = 75457;
	private static int numberOfTimesNavigated = 0;
	private NavigationPages navigationPages;
	private AlertDialog pushNotificationDialog;
	private AlertDialog configureNotificationDialog;
	private ViewGroup mainLayout;

	public static int getNumberOfTimesNavigated() {
		return numberOfTimesNavigated;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(ThemeAttributes.getResourceId(this, R.attr.homeScreenLayout));
		getWindow().setBackgroundDrawable(null);
		mainLayout = (ViewGroup) findViewById(R.id.home_main_layout);
		setImagesScaling();
		showFeedbackIfNecessary();
		PreferenceManager.setDefaultValues(this, R.xml.settings_preferences, false);

		showPushNotificationDialog(getIntent());

		navigationPages = new NavigationPages(this);

		// Initiate async downloading of the updated convention info in the background
		new AsyncTask<Void, Void, PlayServicesInstallation.CheckResult>() {
			@Override
			protected PlayServicesInstallation.CheckResult doInBackground(Void... params) {
				ModelRefresher modelRefresher = new ModelRefresher();
				modelRefresher.refreshFromServer();

				Context appContext = HomeActivity.this.getApplicationContext();
				PlayServicesInstallation.CheckResult checkResult = PlayServicesInstallation.checkPlayServicesExist(appContext, false);
				if (checkResult.isSuccess()) {
					NotificationsManager.handleNotifications(appContext, AzurePushNotifications.SENDER_ID, PushNotificationHandler.class);
				}
				return checkResult;
			}

			@Override
			protected void onPostExecute(PlayServicesInstallation.CheckResult checkResult) {
				// We use the current activity context and not HomeActivity because it's possible the user
				// already navigated from it. We can't use a destroyed activity to display a dialog
				// since it causes an exception.
				Context currentContext = ConventionsApplication.getCurrentContext();
				if (currentContext == null) {
					return;
				}
				if (checkResult.isUserError()) {
					PlayServicesInstallation.showInstallationDialog(currentContext, checkResult);
				} else if (checkResult.isSuccess()) {
					// Start IntentService to register this application with GCM.
					Intent intent = new Intent(currentContext, AzureNotificationRegistrationService.class);
					startService(intent);
					showConfigureNotificationsDialog(currentContext);
				}
			}
		}.execute();

		new Handler().post(new Runnable() {
			@Override
			public void run() {
				// Requests to Facebook must be initialized from the UI thread
				final int numberOfUpdatesBeforeRefresh = Convention.getInstance().getUpdates().size();
				final Context appContext = HomeActivity.this.getApplicationContext();

				// Refresh and ignore all errors
				UpdatesRefresher.getInstance(appContext).refreshFromServer(null, true, new UpdatesRefresher.OnUpdateFinishedListener() {
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
								&& UpdatesRefresher.getInstance(appContext).shouldEnableNotificationAfterUpdate()) {

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

							// The user might have already navigated away from the home activity
							Context currentContext = ConventionsApplication.getCurrentContext();
							if (currentContext == null) {
								return;
							}
							NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
							Intent intent = new Intent(currentContext, UpdatesActivity.class);
							Notification.Builder notificationBuilder = new Notification.Builder(currentContext)
									.setSmallIcon(ThemeAttributes.getResourceId(currentContext, R.attr.notificationSmallIcon))
									.setLargeIcon(ImageHandler.getNotificationLargeIcon(currentContext))
									.setContentTitle(notificationTitle)
									.setContentText(notificationMessage)
									.setAutoCancel(true)
									.setContentIntent(PendingIntent.getActivity(currentContext, 0, intent, 0))
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

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		showPushNotificationDialog(intent);
	}

	private void showPushNotificationDialog(Intent intent) {
		// If we got here from a push notification, show it in a popup
		String pushMessage = intent.getStringExtra(EXTRA_PUSH_NOTIFICATION_MESSAGE);
		if (pushMessage != null) {
			// Check if it's really a new push notification in case the last intent will be re-used accidently
			// (this might be caused if the user pressed back then re-launched the app from the recents).
			// Note: it could also happen in case the user deletes the app data on an older OS without restarting the phone.
			// In that case the preferences are deleted so the last notification id will be the same but we also can't tell
			// if the user already saw this notification. Removing the extra from the intent doesn't work.
			int lastSeenNotification = ConventionsApplication.settings.getLastSeenPushNotificationId();
			int notificationId = intent.getIntExtra(EXTRA_PUSH_NOTIFICATION_ID, Settings.NO_PUSH_NOTIFICATION_SEEN);
			if (lastSeenNotification != Settings.NO_PUSH_NOTIFICATION_SEEN && lastSeenNotification == notificationId) {
				return; // Already seen this notification
			}
			ConventionsApplication.settings.setLastSeenPushNotificationId(notificationId);

			// Allow links
			final SpannableString messageWithLinks = new SpannableString(pushMessage);
			Linkify.addLinks(messageWithLinks, Linkify.WEB_URLS);

			String pushCategoryTitle = null;
			String pushCategory = intent.getStringExtra(EXTRA_PUSH_NOTIFICATION_CATEGORY);
			if (pushCategory != null) {
				// Convert to category title
				PushNotificationTopic topic = PushNotificationTopic.getByTopic(pushCategory);
				if (topic != null) {
					pushCategoryTitle = getString(topic.getTitleResource());
				}
			}

			pushNotificationDialog = new AlertDialog.Builder(this)
					.setTitle(pushCategoryTitle == null ? getString(R.string.push_notification_title) : pushCategoryTitle)
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
							navigateTo(HomeActivity.this, SettingsActivity.class);
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

	private void showConfigureNotificationsDialog(final Context context) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		if (!ConventionsApplication.settings.wasSettingsPopupDisplayed()) {
			configureNotificationDialog = new AlertDialog.Builder(context)
					.setTitle(R.string.configure_notifications)
					.setMessage(R.string.configure_notifications_dialog_message)
					.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							configureNotificationDialog.hide();
						}
					})
					.setNeutralButton(R.string.change_settings, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							configureNotificationDialog.hide();
							navigateTo(context, SettingsActivity.class);
						}
					})
					.setCancelable(true)
					.show();
			ConventionsApplication.settings.setSettingsPopupAsDisplayed();
		}

	}

	public void onNavigationButtonClicked(View view) {
		++numberOfTimesNavigated;
		int position = Integer.parseInt(view.getTag().toString());
		Class<? extends Activity> activityType = navigationPages.getActivityType(position);
		navigateTo(this, activityType);
	}

	private void navigateTo(Context context, Class<? extends Activity> activityType) {
		Intent intent = new Intent(context, activityType);
		Bundle extras = new Bundle();

		// In the rare case that we might be navigating from another activity and not the home screen,
		// don't put this extra or show the animation
		ActivityOptions options = null;
		if (context == this) {
			extras.putBoolean(NavigationActivity.EXTRA_NAVIGATED_FROM_HOME, true);
			options = ActivityOptions.makeCustomAnimation(context, 0, R.anim.shrink_to_top_right);
		}

		if (activityType == ProgrammeActivity.class) {
			extras.putInt(ProgrammeActivity.EXTRA_DELAY_SCROLLING, 500);
		}

		intent.putExtras(extras);
		startActivity(intent, options != null ? options.toBundle() : null);
	}

	private void setImagesScaling() {
		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		int screenWidth = displayMetrics.widthPixels;
		int screenHeight = displayMetrics.heightPixels;

		mainLayout.measure(
				View.MeasureSpec.makeMeasureSpec(screenWidth, View.MeasureSpec.EXACTLY),
				View.MeasureSpec.makeMeasureSpec(screenHeight, View.MeasureSpec.EXACTLY));

		final ImageView imageView = (ImageView) findViewById(R.id.home_background_image);
		Drawable drawable = imageView.getDrawable();
		if (drawable != null) {
			int dwidth = drawable.getIntrinsicWidth();
			int dheight = drawable.getIntrinsicHeight();

			Matrix matrix = new Matrix();

			int vwidth = imageView.getMeasuredWidth() - imageView.getPaddingLeft() - imageView.getPaddingRight();
			int vheight = imageView.getMeasuredHeight() - imageView.getPaddingTop() - imageView.getPaddingBottom();

			float scale;
			float dx = 0, dy = 0;

			if (dwidth * vheight > vwidth * dheight) {
				float cropXCenterOffsetPct = 0.5f;
				scale = (float) vheight / (float) dheight;
				dx = (vwidth - dwidth * scale) * cropXCenterOffsetPct;
			} else {
				float cropYCenterOffsetPct = 0f;
				scale = (float) vwidth / (float) dwidth;
				dy = (vheight - dheight * scale) * cropYCenterOffsetPct;
			}

			matrix.setScale(scale, scale);
			matrix.postTranslate((int) (dx + 0.5f), (int) (dy + 0.5f));

			imageView.setImageMatrix(matrix);
		}
	}

	private void showFeedbackIfNecessary() {
		Calendar oneDayPostConventionDate = Calendar.getInstance();
		oneDayPostConventionDate.setTime(Convention.getInstance().getDate().getTime());
		oneDayPostConventionDate.add(Calendar.DATE, 1);
		oneDayPostConventionDate.set(Calendar.HOUR_OF_DAY, 10);

		if (Dates.now().after(oneDayPostConventionDate.getTime())) {
			findViewById(R.id.home_arrival_methods_button).setVisibility(View.GONE);
			findViewById(R.id.home_feedback_button).setVisibility(View.VISIBLE);
		}
	}

	public void onAboutClicked(View view) {
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
