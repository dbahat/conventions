package amai.org.conventions.events.activities;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Calendar;

import amai.org.conventions.FeedbackActivity;
import amai.org.conventions.R;
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.events.adapters.DayFragmentAdapter;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.networking.ModelRefresher;
import amai.org.conventions.utils.Dates;

public class ProgrammeActivity extends NavigationActivity implements ProgrammeDayFragment.EventsListener {

	public static final String EXTRA_DELAY_SCROLLING = "DelayScrollingExtra";
	public static final int MAX_DAYS_NUMBER = 5;
	private static final String STATE_NAVIGATE_ICON_MODIFIED = "StateNavigateIconModified";
	private static final String STATE_SELECTED_DATE_INDEX = "StateSelectedDateIndex";
	private ViewPager daysPager;

	private Menu menu;
	private boolean navigateToMyEventsIconModified = false;
	private boolean isRefreshing = false;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentInContentContainer(R.layout.activity_programme);
		setToolbarTitle(getResources().getString(R.string.programme_title));
		removeContentContainerForeground();

		setupActionButton(ThemeAttributes.getDrawable(this, R.attr.actionButtonIcon), new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				navigateToActivity(ProgrammeSearchActivity.class, false, null);
			}
		});
		if (ThemeAttributes.getBoolean(this, R.attr.fabOnProgrammeTop)) {
			FloatingActionButton actionButton = getActionButton();
			if (actionButton.getLayoutParams() instanceof CoordinatorLayout.LayoutParams) {
				CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) actionButton.getLayoutParams();
				int fabAlignment = ThemeAttributes.getInteger(this, R.attr.fabOnProgrammeTopAlignment);
				layoutParams.anchorGravity = Gravity.TOP | fabAlignment;
				layoutParams.topMargin = ThemeAttributes.getDimensionSize(this, android.R.attr.actionBarSize) +
						getResources().getDimensionPixelOffset(R.dimen.fab_margin_anchored_to_top);
				actionButton.setLayoutParams(actionButton.getLayoutParams());
			}
		}

		int dateIndexToSelect = savedInstanceState == null ? SELECT_CURRENT_DATE : savedInstanceState.getInt(STATE_SELECTED_DATE_INDEX, SELECT_CURRENT_DATE);
		setupDays(dateIndexToSelect);

		if (savedInstanceState != null && savedInstanceState.getBoolean(STATE_NAVIGATE_ICON_MODIFIED, false)) {
			navigateToMyEventsIconModified = true;
		}

		refreshModel(false, false);
	}

	private void setupDays(int dateIndexToSelect) {
		daysPager = findViewById(R.id.programme_days_pager);
		int delay = getIntent().getIntExtra(EXTRA_DELAY_SCROLLING, 0);
		ProgrammeDayAdapter adapter = new ProgrammeDayAdapter(getSupportFragmentManager(), delay, Convention.getInstance().getEventDates());
		super.setupDaysTabs(findViewById(R.id.programme_days_tabs), daysPager, adapter, dateIndexToSelect);
	}

	@Override
	public boolean onCreateCustomOptionsMenu(Menu menu) {
		this.menu = menu;
		getMenuInflater().inflate(R.menu.programme_menu, menu);

		if (navigateToMyEventsIconModified) {
			MenuItem item = menu.findItem(R.id.programme_navigate_to_my_events);
			changeIconColor(item);
		}

		Convention convention = Convention.getInstance();
		if (!convention.canFillFeedback()) {
			menu.removeItem(R.id.programme_navigate_to_feedback);
		} else if (convention.hasEnded() && !convention.getFeedback().isSent() && !convention.isFeedbackSendingTimeOver()) {
			MenuItem item = menu.findItem(R.id.programme_navigate_to_feedback);
			changeIconColor(item);
		}
		return true;
	}

	@Override
	public void onEventFavoriteChanged(ConventionEvent updatedEvent) {
		if (!navigateToMyEventsIconModified) {
			navigateToMyEventsIconModified = true;
			final MenuItem item = menu.findItem(R.id.programme_navigate_to_my_events);

			// This view is set as an action view. Its layout parameters are both wrap_content so it doesn't
			// need a root view to resolve them.
			@SuppressLint("InflateParams")
			View actionView = getLayoutInflater().inflate(R.layout.my_events_icon, null);
			ImageView myEventsNonAnimatedIcon = (ImageView) actionView.findViewById(R.id.non_animated_icon);

			int accentColor = ThemeAttributes.getColor(ProgrammeActivity.this, R.attr.toolbarIconAccentColor);
			myEventsNonAnimatedIcon.setColorFilter(accentColor, PorterDuff.Mode.SRC_ATOP);

			final ImageView myEventsAnimatedIcon = (ImageView) actionView.findViewById(R.id.icon_to_animate);

			AnimationSet set = new AnimationSet(true);
			set.addAnimation(new ScaleAnimation(1, 2, 1, 2, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f));
			set.addAnimation(new AlphaAnimation(1, 0));
			set.setDuration(800);

			myEventsAnimatedIcon.startAnimation(set);
			item.setActionView(actionView);
			set.setAnimationListener(new Animation.AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					item.setActionView(null);
					changeIconColor(item);
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}
			});
		}
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.programme_navigate_to_my_events:
				navigateToMyEventsIconModified = false;
				navigateToActivity(MyEventsActivity.class);
				return true;
			case R.id.programme_navigate_to_feedback:
				navigateToActivity(FeedbackActivity.class);
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(STATE_NAVIGATE_ICON_MODIFIED, navigateToMyEventsIconModified);
		// We must re-set the current page since the rtl view pager has a bug that it doesn't remember it
		outState.putInt(STATE_SELECTED_DATE_INDEX, daysPager.getCurrentItem());
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onRefresh() {
		isRefreshing = true;
		for (int i = 0; i < daysPager.getAdapter().getCount(); ++i) {
			ProgrammeDayFragment fragment = getDayFragment(i);
			fragment.setRefreshing(true);
		}

		FirebaseAnalytics
				.getInstance(this)
				.logEvent("pull_to_refresh", null);

		refreshModel(true, true);
	}

	private void refreshModel(boolean showError, boolean force) {
		ModelRefresher.getInstance().refreshFromServer(force, new ModelRefresher.OnModelRefreshFinishedListener() {
			@Override
			public void onRefreshFinished(Exception e) {
				isRefreshing = false;
				for (int i = 0; i < daysPager.getAdapter().getCount(); ++i) {
					ProgrammeDayFragment fragment = getDayFragment(i);
					fragment.setRefreshing(false);
					if (e == null) {
						fragment.updateEvents();
					}
				}
				if (e != null && showError) {
					Toast.makeText(ProgrammeActivity.this, R.string.update_refresh_failed, Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private ProgrammeDayFragment getDayFragment(int i) {
		return (ProgrammeDayFragment) daysPager.getAdapter().instantiateItem(daysPager, i);
	}

	private class ProgrammeDayAdapter extends DayFragmentAdapter {
		private final int delayScrolling;

		public ProgrammeDayAdapter(FragmentManager fm, int delayAnimation, Calendar[] eventDates) {
			super(fm, eventDates);
			this.delayScrolling = delayAnimation;
		}

		@Override
		public Fragment getItem(int position) {
			return ProgrammeDayFragment.newInstance(getDate(position), delayScrolling);
		}
	}
}
