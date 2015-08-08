package amai.org.conventions;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.facebook.FacebookRequestError;

import amai.org.conventions.events.activities.ProgrammeActivity;
import amai.org.conventions.model.Convention;
import amai.org.conventions.model.Update;
import amai.org.conventions.navigation.NavigationPages;
import amai.org.conventions.networking.ModelRefresher;
import amai.org.conventions.networking.UpdatesRefresher;

public class HomeActivity extends AppCompatActivity {

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
			   // Refresh and ignore all errors
			   UpdatesRefresher.getInstance(HomeActivity.this).refreshFromServer(null, new UpdatesRefresher.OnUpdateFinishedListener() {
				   @Override
				   public void onSuccess() {
					   int newUpdates = 0;
					   for (Update update : Convention.getInstance().getUpdates()) {
						   if (update.isNew()) {
							   ++newUpdates;
						   }
					   }
					   if (newUpdates > 0) {
						   String message;
						   if (newUpdates == 1) {
							   message = getString(R.string.new_update);
						   } else {
							   message = getString(R.string.new_updates, newUpdates);
						   }
						   Toast.makeText(HomeActivity.this, message, Toast.LENGTH_SHORT).show();
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
