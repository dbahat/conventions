package amai.org.conventions.notifications;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.widget.TextView;

import amai.org.conventions.ConventionsApplication;
import amai.org.conventions.settings.SettingsActivity;
import amai.org.conventions.utils.Settings;
import sff.org.conventions.R;

/**
 * Allows displaying a dialog with the content of a push notification.
 * Needed since the notification area will only show a small part of the notification in case of long notifications.
 */
public class PushNotificationDialogPresenter {
    public final static String EXTRA_PUSH_NOTIFICATION = "EXTRA_PUSH_NOTIFICATION";

    private AlertDialog pushNotificationDialog;

    public void present(final Context context, PushNotification pushNotification) {
        // If we got here from a push notification, show it in a popup
        if (pushNotification != null) {
            // Check if it's really a new push notification in case the last intent will be re-used accidentally
            // (this might be caused if the user pressed back then re-launched the activity from the activity stack).
            // Note: it could also happen in case the user deletes the app data on an older OS without restarting the phone.
            // In that case the preferences are deleted so the last notification id will be the same but we also can't tell
            // if the user already saw this notification. Removing the extra from the intent doesn't work.
            int lastSeenNotification = ConventionsApplication.settings.getLastSeenPushNotificationId();
            if (lastSeenNotification != Settings.NO_PUSH_NOTIFICATION_SEEN_NOTIFICATION_ID && lastSeenNotification == pushNotification.id) {
                return; // Already seen this notification
            }
            ConventionsApplication.settings.setLastSeenPushNotificationId(pushNotification.id);

            // Allow links
            final SpannableString messageWithLinks = new SpannableString(pushNotification.message);
            Linkify.addLinks(messageWithLinks, Linkify.WEB_URLS);

            String pushCategoryTitle = null;
            if (pushNotification.category != null) {
                // Convert to category title
                PushNotificationTopic topic = PushNotificationTopic.getByTopic(pushNotification.category);
                if (topic != null) {
                    pushCategoryTitle = context.getString(topic.getTitleResource());
                }
            }

            pushNotificationDialog = new AlertDialog.Builder(context)
                    .setTitle(pushCategoryTitle == null ? context.getString(R.string.push_notification_title) : pushCategoryTitle)
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
                            context.startActivity(new Intent(context, SettingsActivity.class));
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


}
