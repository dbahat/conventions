package amai.org.conventions.events.activities;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.tabs.TabLayout;

import java.util.Calendar;

import amai.org.conventions.events.adapters.DayFragmentAdapter;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.utils.Dates;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import sff.org.conventions.R;


public class HallActivity extends NavigationActivity {
	public static final String EXTRA_HALL_NAME = "ExtraHallName";
	public static final String EXTRA_USE_SLIDE_OUT_ANIMATION_ON_BACK = "ExtraUseSlideOutAnimationOnBack";

	private static final String STATE_SELECTED_DATE_INDEX = "StateSelectedDateIndex";

	private ViewPager daysPager;

	private boolean useSlideOutAnimationOnBack;
	private String hallName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		hallName = getIntent().getStringExtra(EXTRA_HALL_NAME);
		useSlideOutAnimationOnBack = getIntent().getBooleanExtra(EXTRA_USE_SLIDE_OUT_ANIMATION_ON_BACK, false);

		setContentInContentContainer(R.layout.activity_hall);
		setToolbarTitle(hallName);
		removeContentContainerForeground();

		int dateIndexToSelect = savedInstanceState == null ? SELECT_CURRENT_DATE : savedInstanceState.getInt(STATE_SELECTED_DATE_INDEX, SELECT_CURRENT_DATE);
		setupDays(dateIndexToSelect);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (useSlideOutAnimationOnBack) {
			overridePendingTransition(0, R.anim.slide_out_bottom);
		}
	}

	private void setupDays(int dateIndexToSelect) {
		daysPager = findViewById(R.id.hall_days_pager);
		HallDayAdapter adapter = new HallDayAdapter(getSupportFragmentManager(), Convention.getInstance().getEventDates());
		super.setupDaysTabs(findViewById(R.id.hall_days_tabs), daysPager, adapter, dateIndexToSelect);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// We must re-set the current page since the rtl view pager has a bug that it doesn't remember it
		outState.putInt(STATE_SELECTED_DATE_INDEX, daysPager.getCurrentItem());
		super.onSaveInstanceState(outState);
	}

	private class HallDayAdapter extends DayFragmentAdapter {
		public HallDayAdapter(FragmentManager fm, Calendar[] eventDates) {
			super(fm, eventDates);
		}

		@Override
		public Fragment getItem(int position) {
			return HallDayFragment.newInstance(hallName, getDate(position));
		}
	}

}
