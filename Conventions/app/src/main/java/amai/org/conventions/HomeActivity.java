package amai.org.conventions;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.Calendar;

import amai.org.conventions.events.activities.ProgrammeActivity;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.navigation.NavigationPages;
import amai.org.conventions.notifications.PushNotificationDialogPresenter;
import amai.org.conventions.utils.Dates;

public class HomeActivity extends AppCompatActivity {
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
