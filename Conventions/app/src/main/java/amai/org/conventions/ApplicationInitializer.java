package amai.org.conventions;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import amai.org.conventions.model.SecondHandForm;
import amai.org.conventions.model.SecondHandSell;
import amai.org.conventions.model.Update;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.networking.ModelRefresher;
import amai.org.conventions.networking.UpdatesRefresher;
import amai.org.conventions.notifications.PushNotification;
import amai.org.conventions.notifications.PushNotificationTopic;
import amai.org.conventions.notifications.PushNotificationTopicsSubscriber;
import amai.org.conventions.secondhand.SecondHandActivity;
import amai.org.conventions.updates.UpdatesActivity;
import amai.org.conventions.utils.CollectionUtils;
import androidx.core.app.NotificationCompat;
import sff.org.conventions.R;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

public class ApplicationInitializer {
    private static final int NEW_UPDATES_NOTIFICATION_ID = 75457;
    private static final int SECOND_HAND_SOLD_FORMS_NOTIFICATION_ID = 75458;

    public void initialize(final Context context) {
        refreshModel();
        for (PushNotificationTopic topic : ConventionsApplication.settings.getNotificationTopics()) {
            PushNotificationTopicsSubscriber.subscribe(topic);
        }
        for (PushNotification.Channel channel : PushNotification.Channel.values()) {
            registerNotificationChannel(context, channel);
        }

        refreshUpdatesAndNotifyIfNewUpdatesAreAvailable(context);
        refreshSecondHand(context);
    }

    private void registerNotificationChannel(Context context, PushNotification.Channel channel) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }

        NotificationChannel notificationChannel = new NotificationChannel(
                channel.toString(),
                context.getString(channel.getDisplayName()),
                NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.setDescription(context.getString(channel.getDescription()));

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private void refreshModel() {
        ModelRefresher.getInstance().refreshFromServer(false, new ModelRefresher.OnModelRefreshFinishedListener() {});
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

					// Notify with the number of new updates we got now, not all unread updates
					String notificationTitle = newUpdatesNumber == 1
                            ? context.getString(R.string.new_update)
							: context.getString(R.string.new_updates, newUpdatesNumber);

					String notificationMessage = latestUpdate.getText();
					if (latestUpdate.getText().length() > 200) {
						notificationMessage = latestUpdate.getText().substring(0, 199) + "…";
					}

                    // The user might have already navigated away from the home activity
                    Context currentContext = ConventionsApplication.getCurrentContext();
                    if (currentContext == null) {
                        return;
                    }
                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    Intent intent = new Intent(currentContext, UpdatesActivity.class);
                    int flags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE : PendingIntent.FLAG_UPDATE_CURRENT;
                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(currentContext, PushNotification.Channel.Notifications.toString())
                            .setSmallIcon(ThemeAttributes.getResourceId(currentContext, R.attr.notificationSmallIcon))
                            .setContentTitle(notificationTitle)
                            .setContentText(notificationMessage)
                            .setAutoCancel(true)
							.setContentIntent(PendingIntent.getActivity(currentContext, 0, intent, flags))
                            .setDefaults(Notification.DEFAULT_VIBRATE);


                    Notification notification = new NotificationCompat.BigTextStyle(notificationBuilder)
                            .bigText(notificationMessage)
                            .build();

                    if (notificationManager != null) {
                        notificationManager.notify(NEW_UPDATES_NOTIFICATION_ID, notification);
                    }
                }
            }

            @Override
            public void onError(Exception error) {
            }
        });
    }

    private void refreshSecondHand(final Context context) {
        final SecondHandSell secondHandSell = Convention.getInstance().getSecondHandSell();
        new AsyncTask<Void, Void, Boolean>() {
            private Set<String> openFormsWithUnsoldItems = new HashSet<>();
            @Override
            protected Boolean doInBackground(Void... params) {
                for (SecondHandForm form : secondHandSell.getForms()) {
                    if (!form.isClosed() && form.areAllItemsSold()) {
                        openFormsWithUnsoldItems.add(form.getId());
                    }
                }
                return secondHandSell.refresh(false);
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    // Show notification if there are new open forms whose items are all sold
                    boolean showNotification = false;
                    for (SecondHandForm form : secondHandSell.getForms()) {
                        if (!openFormsWithUnsoldItems.contains(form.getId()) && !form.isClosed() &&
                                form.areAllItemsSold()) {
                            showNotification = true;
                            break;
                        }
                    }
                    if (showNotification) {
                        Context currentContext = ConventionsApplication.getCurrentContext();
                        if (currentContext == null) {
                            return;
                        }
                        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        if (notificationManager == null) {
                            return;
                        }
                        String notificationMessage = secondHandSell.getSoldFormsMessage(currentContext);

                        Intent intent = new Intent(currentContext, SecondHandActivity.class);
                        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(currentContext, PushNotification.Channel.Notifications.toString())
                                .setSmallIcon(ThemeAttributes.getResourceId(currentContext, R.attr.notificationSmallIcon))
                                .setContentTitle(currentContext.getString(R.string.second_hand_sold_forms_notification_title))
                                .setContentText(notificationMessage)
                                .setAutoCancel(true)
                                .setContentIntent(PendingIntent.getActivity(currentContext, 0, intent, 0))
                                .setDefaults(Notification.DEFAULT_VIBRATE);


                        Notification notification = new NotificationCompat.BigTextStyle(notificationBuilder)
                                .bigText(notificationMessage)
                                .build();

                        notificationManager.notify(SECOND_HAND_SOLD_FORMS_NOTIFICATION_ID, notification);
                    }
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


}
