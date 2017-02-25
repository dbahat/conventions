package amai.org.conventions;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import amai.org.conventions.events.activities.ProgrammeActivity;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.updates.UpdatesActivity;

public class HomeActivity extends NavigationActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!Convention.getInstance().hasStarted() || Convention.getInstance().hasEnded()) {
			setContentForBeforeConventionDate();
		} else {
			setContentInContentContainer(Convention.getInstance().hasFavorites()
					? R.layout.activity_home_during_convention
					: R.layout.activity_home_during_convention_no_favorites);
		}

		setToolbarAndContentContainerBackground(ContextCompat.getDrawable(this, R.drawable.harucon2017_home_background));
		setToolbarTitle(ContextCompat.getDrawable(this, R.drawable.harucon_2017_title_black));
	}

	private void setContentForBeforeConventionDate() {
		setContentInContentContainer(R.layout.activity_home_not_during_convention, false, false);

		TextView titleView = (TextView)findViewById(R.id.home_content_title);
		TextView contentView = (TextView)findViewById(R.id.home_content);

		if (!Convention.getInstance().hasEnded()) {
			// before the convention started, show the days until it starts.
			// TODO: Add special handling for 1 and 2 days remaining
			contentView.setText(getString(R.string.home_convention_start_time, getDaysUntilConventionStart()));
		} else {

			contentView.setForeground(ThemeAttributes.getDrawable(this, R.attr.selectableItemBackground));
			contentView.setClickable(true);
			contentView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					navigateToActivity(FeedbackActivity.class);
				}
			});
			findViewById(R.id.home_buttons_layout).setVisibility(View.GONE);

			if (Convention.getInstance().getFeedback().isSent() || Convention.getInstance().isFeedbackSendingTimeOver()) {
				// The feedback filling time is over or feedback was sent. Allow the user to see his feedback
				titleView.setText("");
				contentView.setText(R.string.home_show_feedback);
				contentView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 50);
				ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) contentView.getLayoutParams();
				int margins = getResources().getDimensionPixelOffset(R.dimen.home_show_feedback_text_view_margins);
				layoutParams.setMarginStart(margins);
				layoutParams.setMarginEnd(margins);
				contentView.setLayoutParams(layoutParams);
			} else {
				// Ask the user to fill feedback
				titleView.setText(R.string.home_help_us_improve);
				contentView.setText(R.string.home_send_feedback);
			}
		}
	}

	private int getDaysUntilConventionStart() {
		long timeUntilConvention = Convention.getInstance().getStartDate().getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
		Calendar calendarTimeUntilConvention = Calendar.getInstance();
		calendarTimeUntilConvention.setTime(new Date(timeUntilConvention));
		return calendarTimeUntilConvention.get(Calendar.DAY_OF_YEAR);
	}

	public void onGoToProgrammeClicked(View view) {
		navigateToActivity(ProgrammeActivity.class);
	}

	public void onGoToUpdatesClicked(View view) {
		navigateToActivity(UpdatesActivity.class);
	}
}
