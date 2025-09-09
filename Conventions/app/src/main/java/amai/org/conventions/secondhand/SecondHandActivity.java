package amai.org.conventions.secondhand;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.utils.Views;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import sff.org.conventions.R;

public class SecondHandActivity extends NavigationActivity {
	private static final String TAG = SecondHandActivity.class.getCanonicalName();

	private static final String STATE_SELECTED_PAGE = "StateSelectedPage";

	private TabLayout tabLayout;
	private ViewPager viewPager;
	private TabAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentInContentContainer(R.layout.activity_second_hand);
		setToolbarTitle(getString(R.string.second_hand));
		setBackground(ThemeAttributes.getDrawable(this, R.attr.secondHandBackground));

		tabLayout = findViewById(R.id.second_hand_tabs);
		viewPager = findViewById(R.id.second_hand_pager);

		// Handle edge to edge
		Views.registerApplyInsets(Views.InsetType.NONE, Views.InsetType.NONE, Views.InsetType.PADDING, Views.InsetType.PADDING, tabLayout);

		// Setup adapter
		adapter = new TabAdapter(getSupportFragmentManager());
		adapter.addFragment(new SecondHandBuyFragment(), getString(R.string.second_hand_buy));
		adapter.addFragment(new SecondHandSellFragment(), getString(R.string.second_hand_sell));

		if (adapter.getCount() == 1) {
			tabLayout.setVisibility(View.GONE);
		}

		// Setup view pager
		viewPager.setAdapter(adapter);

		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int i, float v, int i1) {
			}

			@Override
			public void onPageSelected(int i) {
				triggerFragmentSelected(i);
			}

			@Override
			public void onPageScrollStateChanged(int i) {
			}
		});

		// Setup tabs
		Drawable tabIndicator = ThemeAttributes.getDrawable(this, R.attr.selectedTabIndicator);
		if (tabIndicator != null) {
			tabLayout.setSelectedTabIndicator(tabIndicator);
		}
		tabLayout.setupWithViewPager(viewPager, false);

		// Select the first tab by default
		int pageToSelect = savedInstanceState == null ? 0 : savedInstanceState.getInt(STATE_SELECTED_PAGE, 0);
		viewPager.setCurrentItem(pageToSelect, false);
		triggerFragmentSelected(viewPager.getCurrentItem());
	}

	private void triggerFragmentSelected(int i) {
		Fragment fragment = adapter.getItem(i);
		if (fragment instanceof OnFragmentSelectedListener) {
			((OnFragmentSelectedListener) fragment).onFragmentSelected(this);
		}
	}

	@Override
	public void setupActionButton(Drawable image, View.OnClickListener listener) {
		super.setupActionButton(image, listener);
	}

	@Override
	protected void removeActionButton() {
		super.removeActionButton();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// We must set the page because the UI will show the last selected page even if we set it
		// to a different page in onCreate after orientation change. Also, it's a better user experience.
		outState.putInt(STATE_SELECTED_PAGE, viewPager.getCurrentItem());
		super.onSaveInstanceState(outState);
	}

	private class TabAdapter extends FragmentPagerAdapter {
		private final List<Fragment> fragments = new ArrayList<>();
		private final List<String> fragmentTitles = new ArrayList<>();

		public TabAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public Fragment getItem(int position) {
			return fragments.get(position);
		}

		public void addFragment(Fragment fragment, String title) {
			fragments.add(fragment);
			fragmentTitles.add(title);
		}

		@Nullable
		@Override
		public CharSequence getPageTitle(int position) {
			return fragmentTitles.get(position);
		}

		@Override
		public int getCount() {
			return fragments.size();
		}
	}

	public interface OnFragmentSelectedListener {
		void onFragmentSelected(SecondHandActivity context);
	}
}
