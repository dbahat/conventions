package amai.org.conventions.notifications;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.microsoft.windowsazure.messaging.NotificationHub;

import java.util.Arrays;
import java.util.List;

import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.Log;

public class AzurePushNotifications {
    public static final String SENDER_ID = "377234495790";
    public static final String HUB_NAME = "conventions";
    public static final String HUB_LISTEN_CONNECTION_STRING = "Endpoint=sb://sff-conventions.servicebus.windows.net/;SharedAccessKeyName=DefaultListenSharedAccessSignature;SharedAccessKey=dCrJSDNwpotcGHGwLo4EKObl8cAtVVagaUO3CtPuMSo=";

	private static String TAG = AzurePushNotifications.class.getSimpleName();
	public static final String IS_ADVANCED_OPTIONS_ENABLED = "isAdvancedOptionsEnabled";
	private static final String IS_REGISTERED = "isRegisteredToAzureNotificationHub";

	private Context context;

	public AzurePushNotifications(Context context) {
		this.context = context;
	}

	public List<String> getNotificationTopics() {
		// TODO move to ConventionApplication.settings for the next convention
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
		String token = FirebaseInstanceId.getInstance().getToken(SENDER_ID, FirebaseMessaging.INSTANCE_ID_SCOPE);
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
