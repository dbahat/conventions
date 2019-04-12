package amai.org.conventions.secondhand;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import amai.org.conventions.navigation.NavigationActivity;
import sff.org.conventions.R;

public class SecondHandActivity extends NavigationActivity {
	private static final String TAG = SecondHandActivity.class.getCanonicalName();

	private TabLayout tabLayout;
	private ViewPager viewPager;
	private TabAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentInContentContainer(R.layout.activity_second_hand);
		setToolbarTitle(getString(R.string.second_hand));

		tabLayout = findViewById(R.id.second_hand_tabs);
		viewPager = findViewById(R.id.second_hand_pager);

		// Setup adapter
		adapter = new TabAdapter(getSupportFragmentManager());
		adapter.addFragment(new SecondHandSellFragment(), getString(R.string.second_hand_sell));

		if (adapter.getCount() == 1) {
			tabLayout.setVisibility(View.GONE);
		}

		// Setup view pager
		viewPager.setAdapter(adapter);
		viewPager.setOffscreenPageLimit(adapter.getCount()); // Load all fragments for smooth scrolling

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
		tabLayout.setupWithViewPager(viewPager, false);

		viewPager.setCurrentItem(0, false);
		triggerFragmentSelected(viewPager.getCurrentItem());
	}

	private void triggerFragmentSelected(int i) {
		Fragment fragment = adapter.getItem(i);
		if (fragment instanceof OnFragmentSelectedListener) {
			((OnFragmentSelectedListener) fragment).onFragmentSelected(this);
		}
	}

	@Override
	public void setupActionButton(int imageResource, View.OnClickListener listener) {
		super.setupActionButton(imageResource, listener);
	}

	@Override
	protected void removeActionButton() {
		super.removeActionButton();
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
