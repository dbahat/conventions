package amai.org.conventions.notifications;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.microsoft.windowsazure.messaging.NotificationHub;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.Log;

public class AzurePushNotifications {
	public static final String SENDER_ID = "458203975509";
	public static final String HUB_NAME = "cami2017";
	public static final String HUB_LISTEN_CONNECTION_STRING = "Endpoint=sb://conventions.servicebus.windows.net/;SharedAccessKeyName=DefaultListenSharedAccessSignature;SharedAccessKey=vd8cDJB4h0Fr+tOCzijpRbUWhggakKZlaJbL/pedf0g=";

	private static String TAG = AzurePushNotifications.class.getSimpleName();
	public static final String IS_ADVANCED_OPTIONS_ENABLED = "isAdvancedOptionsEnabled";
	private static final String IS_REGISTERED = "isRegisteredToAzureNotificationHub";

	private Context context;

	public AzurePushNotifications(Context context) {
		this.context = context;
	}

	public void setAdvancedOptionsEnabled(boolean enabled) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		sharedPreferences.edit().putBoolean(IS_ADVANCED_OPTIONS_ENABLED, enabled).apply();
	}

	public boolean isAdvancedOptionsEnabled() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPreferences.getBoolean(IS_ADVANCED_OPTIONS_ENABLED, false);
	}

	public void setRegistered(boolean registered) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		sharedPreferences.edit().putBoolean(IS_REGISTERED, registered).apply();
	}

	public boolean isRegistered() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPreferences.getBoolean(IS_REGISTERED, false);
	}

	public void registerAsync(final RegistrationListener listener) {
		new AsyncTask<Void, Void, Exception>() {
			@Override
			protected Exception doInBackground(Void... params) {
				try {
					register();
				} catch (Exception e) {
					return e;
				}
				return null;
			}

			@Override
			protected void onPostExecute(Exception exception) {
				if (exception == null) {
					listener.onSuccess();
				} else {
					listener.onError();
				}
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	public void register() throws Exception {
		// If an exception happens while trying to register, this makes sure we'll try it again
		// next time
		setRegistered(false);
		String token = FirebaseInstanceId.getInstance().getToken(SENDER_ID, FirebaseMessaging.INSTANCE_ID_SCOPE);
		NotificationHub hub = new NotificationHub(HUB_NAME, HUB_LISTEN_CONNECTION_STRING, context);

		List<String> notificationTopics = getNotificationTopics();
		if (notificationTopics.contains(PushNotificationTopic.TOPIC_EVENTS.getTopic())) {
			notificationTopics.addAll(getMyEventsTags());
		}
		Log.i(TAG, "Attempting to register with Azure notification hub using token " + token + "\ntags: " + notificationTopics.toString());
		String regID = hub.register(token, notificationTopics.toArray(new String[notificationTopics.size()])).getRegistrationId();
		Log.i(TAG, "Registered Successfully, registration id: " + regID);
		setRegistered(true);
	}

	private List<String> getNotificationTopics() {
		final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		List<PushNotificationTopic> topics = CollectionUtils.filter(
				Arrays.asList(PushNotificationTopic.values()),
				new CollectionUtils.Predicate<PushNotificationTopic>() {
					@Override
					public boolean where(PushNotificationTopic item) {
						return item == PushNotificationTopic.TOPIC_EMERGENCY || sharedPreferences.getBoolean(item.getTopic(), false);
					}
				});
		return CollectionUtils.map(
				topics,
				new CollectionUtils.Mapper<PushNotificationTopic, String>() {
					@Override
					public String map(PushNotificationTopic item) {
						return item.getTopic();
					}
				});
	}

	private List<String> getMyEventsTags() {
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
		return CollectionUtils.map(events, new CollectionUtils.Mapper<ConventionEvent, String>() {
			@Override
			public String map(ConventionEvent item) {
				return String.valueOf(Convention.getInstance().getId().toLowerCase() + "_event_" + item.getServerId());
			}
		});
	}

	public interface RegistrationListener {
		void onSuccess();

		void onError();

		class DoNothing implements RegistrationListener {

			@Override
			public void onSuccess() {

			}

			@Override
			public void onError() {

			}
		}
	}
}
