package amai.org.conventions;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.FacebookRequestError;
import com.microsoft.windowsazure.notifications.NotificationsManager;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import amai.org.conventions.events.activities.ProgrammeActivity;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.model.Update;
import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.navigation.NavigationPages;
import amai.org.conventions.networking.ModelRefresher;
import amai.org.conventions.networking.UpdatesRefresher;
import amai.org.conventions.notifications.AzureNotificationRegistrationService;
import amai.org.conventions.notifications.AzurePushNotifications;
import amai.org.conventions.notifications.PlayServicesInstallation;
import amai.org.conventions.notifications.PushNotificationDialogPresenter;
import amai.org.conventions.notifications.PushNotificationHandler;
import amai.org.conventions.notifications.PushNotificationTopic;
import amai.org.conventions.settings.SettingsActivity;
import amai.org.conventions.updates.UpdatesActivity;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.Dates;
import amai.org.conventions.utils.Settings;
import amai.org.conventions.utils.Views;

public class HomeActivity extends AppCompatActivity {
	public final static String EXTRA_PUSH_NOTIFICATION_MESSAGE = "EXTRA_PUSH_NOTIFICATION_MESSAGE";
	public final static String EXTRA_PUSH_NOTIFICATION_CATEGORY = "EXTRA_PUSH_NOTIFICATION_CATEGORY";
	public static final String EXTRA_PUSH_NOTIFICATION_ID = "EXTRA_PUSH_NOTIFICATION_ID";

	private static int numberOfTimesNavigated = 0;
	private NavigationPages navigationPages;
	private ViewGroup mainLayout;

	public static int getNumberOfTimesNavigated() {
		return numberOfTimesNavigated;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(ThemeAttributes.getResourceId(this, R.attr.homeScreenLayout));
		getWindow().setBackgroundDrawable(null);
		mainLayout = (ViewGroup) findViewById(R.id.home_main_layout);
		setImagesScaling();
		showFeedbackIfNecessary();

		new ApplicationInitializer().initialize(this);

		new PushNotificationDialogPresenter().present(this, getIntent());

		navigationPages = new NavigationPages(this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		new PushNotificationDialogPresenter().present(this, intent);
	}

	public void onNavigationButtonClicked(View view) {
		++numberOfTimesNavigated;
		int position = Integer.parseInt(view.getTag().toString());
		Class<? extends Activity> activityType = navigationPages.getActivityType(position);
		navigateTo(this, activityType);
	}

	private void navigateTo(Context context, Class<? extends Activity> activityType) {
		Intent intent = new Intent(context, activityType);
		Bundle extras = new Bundle();

		// In the rare case that we might be navigating from another activity and not the home screen,
		// don't put this extra or show the animation
		ActivityOptions options = null;
		if (context == this) {
			extras.putBoolean(NavigationActivity.EXTRA_NAVIGATED_FROM_HOME, true);
			options = ActivityOptions.makeCustomAnimation(context, 0, R.anim.shrink_to_top_right);
		}

		if (activityType == ProgrammeActivity.class) {
			extras.putInt(ProgrammeActivity.EXTRA_DELAY_SCROLLING, 500);
		}

		intent.putExtras(extras);
		startActivity(intent, options != null ? options.toBundle() : null);
	}

	private void setImagesScaling() {
		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		int screenWidth = displayMetrics.widthPixels;
		int screenHeight = displayMetrics.heightPixels;

		mainLayout.measure(
				View.MeasureSpec.makeMeasureSpec(screenWidth, View.MeasureSpec.EXACTLY),
				View.MeasureSpec.makeMeasureSpec(screenHeight, View.MeasureSpec.EXACTLY));

		final ImageView imageView = (ImageView) findViewById(R.id.home_background_image);
		Drawable drawable = imageView.getDrawable();
		if (drawable != null) {
			int dwidth = drawable.getIntrinsicWidth();
			int dheight = drawable.getIntrinsicHeight();

			Matrix matrix = new Matrix();

			int vwidth = imageView.getMeasuredWidth() - imageView.getPaddingLeft() - imageView.getPaddingRight();
			int vheight = imageView.getMeasuredHeight() - imageView.getPaddingTop() - imageView.getPaddingBottom();

			float scale;
			float dx = 0, dy = 0;

			if (dwidth * vheight > vwidth * dheight) {
				float cropXCenterOffsetPct = 0.5f;
				scale = (float) vheight / (float) dheight;
				dx = (vwidth - dwidth * scale) * cropXCenterOffsetPct;
			} else {
				float cropYCenterOffsetPct = 0f;
				scale = (float) vwidth / (float) dwidth;
				dy = (vheight - dheight * scale) * cropYCenterOffsetPct;
			}

			matrix.setScale(scale, scale);
			matrix.postTranslate((int) (dx + 0.5f), (int) (dy + 0.5f));

			imageView.setImageMatrix(matrix);
		}
	}

	private void showFeedbackIfNecessary() {
		Calendar oneDayPostConventionDate = Calendar.getInstance();
		oneDayPostConventionDate.setTime(Convention.getInstance().getEndDate().getTime());
		oneDayPostConventionDate.add(Calendar.DATE, 1);
		oneDayPostConventionDate.set(Calendar.HOUR_OF_DAY, 10);

		if (Dates.now().after(oneDayPostConventionDate.getTime())) {
			findViewById(R.id.home_arrival_methods_button).setVisibility(View.GONE);
			findViewById(R.id.home_feedback_button).setVisibility(View.VISIBLE);
		}
	}
}
