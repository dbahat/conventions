package amai.org.conventions.notifications;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.microsoft.windowsazure.notifications.NotificationsHandler;

public class PushNotificationHandler extends NotificationsHandler {

    @Override
    public void onReceive(Context context, Bundle bundle) {
	    // TODO Maybe send the tag in a custom field in the payload (send to service with EXTRA_TAG)
        Intent intent = new Intent(context, ShowNotificationService.class)
                .putExtra(ShowNotificationService.EXTRA_NOTIFICATION_TYPE, ShowNotificationService.Type.Push)
                .putExtra(ShowNotificationService.EXTRA_MESSAGE, bundle.getString("message"));

        context.startService(intent);
    }

}
