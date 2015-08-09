package amai.org.conventions;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.facebook.FacebookRequestError;

import amai.org.conventions.events.activities.EventActivity;
import amai.org.conventions.events.activities.ProgrammeActivity;
import amai.org.conventions.model.Convention;
import amai.org.conventions.model.Update;
import amai.org.conventions.navigation.NavigationPages;
import amai.org.conventions.networking.ModelRefresher;
import amai.org.conventions.networking.UpdatesRefresher;
import amai.org.conventions.updates.UpdatesActivity;

public class HomeActivity extends AppCompatActivity {

	private static final int NEW_UPDATES_NOTIFICATION_ID = 75457;
	private NavigationPages navigationPages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_home);

	    navigationPages = new NavigationPages(this);

	    // Initiate async downloading of the updated convention info in the background
	    new AsyncTask<Void, Void, Void>() {
		    @Override
		    protected Void doInBackground(Void... params) {
			    ModelRefresher modelRefresher = new ModelRefresher();
			    modelRefresher.refreshFromServer();

			    return null;
		    }
	    }.execute();



	    // Requests to Facebook must be initialized from the UI thread
	    new Handler().post(new Runnable() {
		   @Override
		   public void run() {
			   final int numberOfUpdatesBeforeRefresh = Convention.getInstance().getUpdates().size();

			   // Refresh and ignore all errors
			   UpdatesRefresher.getInstance(HomeActivity.this).refreshFromServer(null, new UpdatesRefresher.OnUpdateFinishedListener() {
				   @Override
				   public void onSuccess() {
					   int newUpdates = 0;
					   Update latestUpdate = null;
					   for (Update update : Convention.getInstance().getUpdates()) {
						   if (update.isNew()) {
							   ++newUpdates;
							   latestUpdate = update;
						   }
					   }

					   // We don't want to raise the notification if there are no new updates, or if this is the first time updates are downloaded to cache.
					   if (newUpdates > 0 && numberOfUpdatesBeforeRefresh > 0) {
						   String notificationTitle = newUpdates == 1
								   ? getString(R.string.new_update)
								   : getString(R.string.new_updates, newUpdates);

						   String notificationMessage = latestUpdate.getText().substring(0, Math.min(200,latestUpdate.getText().length())) + "...";

						   NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
						   Intent intent = new Intent(HomeActivity.this, UpdatesActivity.class);
						   Notification.Builder notificationBuilder = new Notification.Builder(HomeActivity.this)
								   .setSmallIcon(R.drawable.cami_logo_small_white)
								   .setLargeIcon(ImageHandler.getNotificationLargeIcon(HomeActivity.this))
								   .setContentTitle(notificationTitle)
								   .setContentText(notificationMessage)
								   .setAutoCancel(true)
								   .setContentIntent(PendingIntent.getActivity(HomeActivity.this, 0, intent, 0))
								   .setDefaults(Notification.DEFAULT_VIBRATE);


						   Notification notification = new Notification.BigTextStyle(notificationBuilder)
								   .bigText(notificationMessage)
								   .build();

						   notificationManager.notify(NEW_UPDATES_NOTIFICATION_ID, notification);
					   }
					   else {
						   Toast.makeText(HomeActivity.this, "No updates", Toast.LENGTH_SHORT).show();
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

    public void onNavigationButtonClicked(View view) {
        int position = Integer.parseInt(view.getTag().toString());
	    Class<? extends Activity> activityType = navigationPages.getActivityType(position);
	    Intent intent = new Intent(this, activityType);

	    if (activityType == ProgrammeActivity.class) {
		    Bundle extras = new Bundle();
		    extras.putInt(ProgrammeActivity.EXTRA_DELAY_SCROLLING, 500);
		    intent.putExtras(extras);
	    }

	    ActivityOptions options = ActivityOptions.makeCustomAnimation(this, 0, R.anim.shrink_to_top_right);
	    startActivity(intent, options.toBundle());
    }
}
