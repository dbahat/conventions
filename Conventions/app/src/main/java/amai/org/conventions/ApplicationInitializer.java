package amai.org.conventions;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;

import com.microsoft.windowsazure.notifications.NotificationsManager;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import amai.org.conventions.model.Update;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.networking.ModelRefresher;
import amai.org.conventions.networking.UpdatesRefresher;
import amai.org.conventions.notifications.AzurePushNotifications;
import amai.org.conventions.notifications.PlayServicesInstallation;
import amai.org.conventions.notifications.PushNotificationHandler;
import amai.org.conventions.settings.SettingsActivity;
import amai.org.conventions.updates.UpdatesActivity;
import amai.org.conventions.utils.CollectionUtils;
import sff.org.conventions.R;

public class ApplicationInitializer {
	private static final int NEW_UPDATES_NOTIFICATION_ID = 75457;

	public void initialize(final Context context) {
		PreferenceManager.setDefaultValues(context, R.xml.settings_preferences, false);
		refreshModel();
		initializeAzureNotificationHubIfPossible(context);
		refreshUpdatesAndNotifyIfNewUpdatesAreAvailable(context);
	}

	private void refreshModel() {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... voids) {
				ModelRefresher modelRefresher = new ModelRefresher();
				modelRefresher.refreshFromServer(false);
				return null;
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

	}

	// Since push notifications cannot work without google play services, first check for play services existence, and if
	// they don't exist show a proper message to the user instead.
	// After initialization, inform the user he can change push notification settings in a dialog (one time).
	private void initializeAzureNotificationHubIfPossible(final Context context) {
		new AsyncTask<Void, Void, PlayServicesInstallation.CheckResult>() {
			private AlertDialog configureNotificationDialog;

			@Override
			protected PlayServicesInstallation.CheckResult doInBackground(Void... params) {
				PlayServicesInstallation.CheckResult checkResult = PlayServicesInstallation.checkPlayServicesExist(context, false);
				if (checkResult.isSuccess()) {
					NotificationsManager.handleNotifications(context, AzurePushNotifications.SENDER_ID, PushNotificationHandler.class);
				}
				return checkResult;
			}

			@Override
			protected void onPostExecute(PlayServicesInstallation.CheckResult checkResult) {
				// We use the current activity context and not the initial activity because it's possible the user
				// already navigated from it. We can't use a destroyed activity to display a dialog
				// since it causes an exception.
				Context currentContext = ConventionsApplication.getCurrentContext();
				if (currentContext == null) {
					return;
				}
				if (checkResult.isUserError()) {
					PlayServicesInstallation.showInstallationDialog(currentContext, checkResult);
				} else if (checkResult.isSuccess()) {
					AzurePushNotifications notifications = new AzurePushNotifications(context);
					if (!notifications.isRegistered()) {
						notifications.registerAsync(new AzurePushNotifications.RegistrationListener.DoNothing());
					}
					showConfigureNotificationsDialog(currentContext);
				}
			}

			private void showConfigureNotificationsDialog(final Context context) {
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
									Intent intent = new Intent(context, SettingsActivity.class);
									context.startActivity(intent);
								}
							})
							.setCancelable(true)
							.show();
					ConventionsApplication.settings.setSettingsPopupAsDisplayed();
				}

			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	private void refreshUpdatesAndNotifyIfNewUpdatesAreAvailable(final Context context) {
		// Updates refresher must be called from the UI thread
		final int numberOfUpdatesBeforeRefresh = Convention.getInstance().getUpdates().size();

		// Refresh and ignore all errors
		final UpdatesRefresher updatesRefresher = UpdatesRefresher.getInstance(context);
		updatesRefresher.refreshFromServer(true, false, new UpdatesRefresher.OnUpdateFinishedListener() {
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
						&& updatesRefresher.shouldEnableNotificationAfterUpdate()) {

					Update latestUpdate = Collections.max(newUpdates, new Comparator<Update>() {
						@Override
						public int compare(Update lhs, Update rhs) {
							return lhs.getDate().compareTo(rhs.getDate());
						}
					});

					String notificationTitle = newUpdatesNumber == 1
							? context.getString(R.string.new_update)
							: context.getString(R.string.new_updates, newUpdatesNumber);

					String notificationMessage = latestUpdate.getText().substring(0, Math.min(200, latestUpdate.getText().length())) + "...";

					// The user might have already navigated away from the home activity
					Context currentContext = ConventionsApplication.getCurrentContext();
					if (currentContext == null) {
						return;
					}
					NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
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
			public void onError(Exception error) {
			}
		});
	}
}
