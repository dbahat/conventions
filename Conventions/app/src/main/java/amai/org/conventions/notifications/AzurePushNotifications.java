package amai.org.conventions.notifications;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.microsoft.windowsazure.messaging.NotificationHub;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.Log;

public class AzurePushNotifications {
    public static final String SENDER_ID = "458203975509";
    public static final String HUB_NAME = "conventions";
    public static final String HUB_LISTEN_CONNECTION_STRING = "Endpoint=sb://conventions.servicebus.windows.net/;SharedAccessKeyName=DefaultListenSharedAccessSignature;SharedAccessKey=hjyET8pIKJ2VZNFkcuvFDt6tZ/aSS57eMSNIU2WofSM=";

	private static String TAG = AzurePushNotifications.class.getSimpleName();
	private static final String IS_REGISTERED = "isRegisteredToAzureNotificationHub";
	// Topics
	public static final String TOPIC_GENERAL = "cami2016_general";
	public static final String TOPIC_EVENTS = "cami2016_events";
	public static final String TOPIC_COSPLAY = "cami2016_cosplay";
	public static final String TOPIC_BUS = "cami2016_bus";
	public static final String TOPIC_EMERGENCY = "cami2016_emergency";

	private Context context;

	public AzurePushNotifications(Context context) {
		this.context = context;
	}

	public List<String> getNotificationTopics() {
		final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		List<String> topics = CollectionUtils.filter(
				Arrays.asList(TOPIC_GENERAL, TOPIC_EVENTS, TOPIC_COSPLAY, TOPIC_BUS),
				new CollectionUtils.Predicate<String>() {
					@Override
					public boolean where(String item) {
						return sharedPreferences.getBoolean(item, false);
					}
				}, new LinkedList<String>());

		topics.add(TOPIC_EMERGENCY);
		return topics;
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
