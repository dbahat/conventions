package amai.org.conventions.notifications;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.microsoft.windowsazure.messaging.NotificationHub;

import java.util.Arrays;
import java.util.List;

import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.Log;

public class AzurePushNotifications {
    public static final String SENDER_ID = "458203975509";
    public static final String HUB_NAME = "conventions";
    public static final String HUB_LISTEN_CONNECTION_STRING = "Endpoint=sb://conventions.servicebus.windows.net/;SharedAccessKeyName=DefaultListenSharedAccessSignature;SharedAccessKey=hjyET8pIKJ2VZNFkcuvFDt6tZ/aSS57eMSNIU2WofSM=";

	private static String TAG = AzurePushNotifications.class.getSimpleName();
	public static final String IS_ADVANCED_OPTIONS_ENABLED = "isAdvancedOptionsEnabled";
	private static final String IS_REGISTERED = "isRegisteredToAzureNotificationHub";

	// Topics
	public enum PushNotificationTopic {
		TOPIC_GENERAL("cami2016_general"),
		TOPIC_EVENTS("cami2016_events"),
		TOPIC_COSPLAY("cami2016_cosplay"),
		TOPIC_BUS("cami2016_bus"),
		TOPIC_TEST("cami2016_test"),
		TOPIC_EMERGENCY("cami2016_emergency");

		private final String topic;
		PushNotificationTopic(String topic) {
			this.topic = topic;
		}
		public String getTopic() {
			return topic;
		}
	}

	private Context context;

	public AzurePushNotifications(Context context) {
		this.context = context;
	}

	public List<String> getNotificationTopics() {
		final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		List<PushNotificationTopic> topics = CollectionUtils.filter(
				Arrays.asList(PushNotificationTopic.values()),
				new CollectionUtils.Predicate<PushNotificationTopic>() {
					@Override
					public boolean where(PushNotificationTopic item) {
						return item == PushNotificationTopic.TOPIC_EMERGENCY || sharedPreferences.getBoolean(item.getTopic(), false);
					}
				});
		List<String> topicStrings = CollectionUtils.map(
				topics,
				new CollectionUtils.Mapper<PushNotificationTopic, String>() {
					@Override
					public String map(PushNotificationTopic item) {
						return item.getTopic();
					}
				});
		return topicStrings;
	}

	public boolean isNotificationTopicEnabled(PushNotificationTopic topic) {
		final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		return topic == PushNotificationTopic.TOPIC_EMERGENCY || sharedPreferences.getBoolean(topic.getTopic(), false);
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
		}.execute();
	}

	public void register() throws Exception {
		// If an exception happens while trying to register, this makes sure we'll try it again
		// next time
		setRegistered(false);
		InstanceID instanceID = InstanceID.getInstance(context);
		String token = instanceID.getToken(SENDER_ID, GoogleCloudMessaging.INSTANCE_ID_SCOPE);
		NotificationHub hub = new NotificationHub(HUB_NAME, HUB_LISTEN_CONNECTION_STRING, context);

		List<String> notificationTopics = getNotificationTopics();
		Log.i(TAG, "Attempting to register with Azure notification hub using token " + token + "\ntopics: " + notificationTopics.toString());
		String regID = hub.register(token, notificationTopics.toArray(new String[notificationTopics.size()])).getRegistrationId();
		Log.i(TAG, "Registered Successfully, registration id: " + regID);
		setRegistered(true);
	}

	public interface RegistrationListener {
		void onSuccess();
		void onError();
	}
}
