package amai.org.conventions.notifications;

import com.google.firebase.messaging.FirebaseMessaging;

import java.util.List;

import amai.org.conventions.ConventionsApplication;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.Log;

public class PushNotificationTopicsSubscriber {
    private static final String TAG = PushNotificationTopicsSubscriber.class.getSimpleName();
    private static final String EVENT_TOPIC_FORMAT = "%s_event_%s";

    public static void subscribe(PushNotificationTopic topic) {
        subscribe(topic.getTopic());

        if (!topic.equals(PushNotificationTopic.TOPIC_EVENTS)) {
            return;
        }
        List<ConventionEvent> favoriteEvents = getFavorites();
        for (ConventionEvent event : favoriteEvents) {
            subscribe(event);
        }
    }

    public static void unsubscribe(PushNotificationTopic topic) {
        unsubscribe(topic.getTopic());

        if (!topic.equals(PushNotificationTopic.TOPIC_EVENTS)) {
            return;
        }
        List<ConventionEvent> favoriteEvents = getFavorites();
        for (ConventionEvent event : favoriteEvents) {
            unsubscribe(event);
        }
    }

    public static void subscribe(ConventionEvent conventionEvent) {
        if (!ConventionsApplication.settings.getNotificationTopics().contains(PushNotificationTopic.TOPIC_EVENTS)) {
            return;
        }

        subscribe(getEventTopic(conventionEvent));
    }

    public static void unsubscribe(ConventionEvent conventionEvent) {
        unsubscribe(getEventTopic(conventionEvent));
    }

    private static String getEventTopic(ConventionEvent conventionEvent) {
        return String.format(EVENT_TOPIC_FORMAT, Convention.getInstance().getId().toLowerCase(), conventionEvent.getServerId());
    }

    private static List<ConventionEvent> getFavorites() {
        return CollectionUtils.filter(Convention.getInstance().getEvents(), new CollectionUtils.Predicate<ConventionEvent>() {
            @Override
            public boolean where(ConventionEvent item) {
                return item.isAttending();
            }
        });
    }

    private static void subscribe(String topic) {
        Log.v(TAG, "subscribed to topic " + topic);
        FirebaseMessaging.getInstance().subscribeToTopic(topic);
    }

    private static void unsubscribe(String topic) {
        Log.v(TAG, "unsubscribed to topic " + topic);
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
    }
}
