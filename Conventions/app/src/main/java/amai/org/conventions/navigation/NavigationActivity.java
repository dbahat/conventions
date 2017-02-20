package amai.org.conventions.navigation;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import amai.org.conventions.AboutActivity;
import amai.org.conventions.ApplicationInitializer;
import amai.org.conventions.ArrivalMethodsActivity;
import amai.org.conventions.ConventionsApplication;
import amai.org.conventions.FeedbackActivity;
import amai.org.conventions.HomeActivity;
import amai.org.conventions.R;
import amai.org.conventions.SplashActivity;
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.events.activities.EventActivity;
import amai.org.conventions.events.activities.MyEventsActivity;
import amai.org.conventions.events.activities.ProgrammeActivity;
import amai.org.conventions.map.MapActivity;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.notifications.PushNotificationDialogPresenter;
import amai.org.conventions.settings.SettingsActivity;
import amai.org.conventions.updates.UpdatesActivity;


public abstract class NavigationActivity extends AppCompatActivity {
	public static final String EXTRA_INITIALIZE = "ExtraInitialize";
	public static final String EXTRA_EXIT_ON_BACK = "ExtraExitOnBack";

	private TextView navigationToolbarTitle;
	private Toolbar navigationToolbar;
	private boolean exitOnBack;
	private FrameLayout contentContainer;
	private FloatingActionButton actionButton;
	private DrawerLayout navigationDrawer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_navigation);

		exitOnBack = getIntent().getBooleanExtra(EXTRA_EXIT_ON_BACK, false);

		if (getIntent().getBooleanExtra(EXTRA_INITIALIZE, false) &&
				!(savedInstanceState != null && savedInstanceState.getBoolean(EXTRA_INITIALIZE, true))) {
			new ApplicationInitializer().initialize(this.getApplicationContext());
		}

		navigationToolbar = (Toolbar) findViewById(R.id.navigation_toolbar);
		navigationToolbarTitle = (TextView) findViewById(R.id.navigation_toolbar_title);
		navigationDrawer = (DrawerLayout) findViewById(R.id.navigation_drawer);
		setupActionBar(navigationToolbar);
		navigationToolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onNavigationButtonClicked();
				ConventionsApplication.sendTrackingEvent(new HitBuilders.EventBuilder()
						.setCategory("Navigation")
						.setAction("ButtonClicked")
						.build());
				openNavigationDrawer(true);
			}
		});

		initializeNavigationDrawer(); // In case it was already open

		new PushNotificationDialogPresenter().present(this, getIntent());
	}

	private void openNavigationDrawer(boolean animate) {
		initializeNavigationDrawer();
		navigationDrawer.openDrawer(GravityCompat.START, animate);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(EXTRA_INITIALIZE, false); // Prevent re-initializing
		super.onSaveInstanceState(outState);
	}

	// Overriding onCreateOptionsMenu so we can tell when extending classes completed inflating the options menu.
	// In order for extending classes to create an options menu, override onCreateCustomOptionsMenu();
	// Needed so we can take actions based on the number of actions the activity defined (e.g. adjust the toolbar title margin to be centered).
	@Override
	public final boolean onCreateOptionsMenu(Menu menu) {
		boolean onCreateCustomOptionsMenuResult = onCreateCustomOptionsMenu(menu);

		// Since we use a custom toolbar title centered between the action items, it's position is expected to shift based on the number
		// of action items to it's start / end:
		// On the title's start, we'll always have the navigation action.
		// On the title's end, we'll have a changing amount of action items.
		//
		// If the number of items to the title's start/end isn't symmetrical, the title won't appear centered, so we need to add margin to it's
		// start to "balance" the space taken by the action items.
		// For example:
		// If the menu has no items, it means we need to add a margin to the title's end by 1 action item size
		// If the menu has 2 items, it means we need to add a margin to the title's start by 2 action time size
		//
		// NOTE -
		// The calculation here assumes there's no overflow menu in the action bar. Such a menu causes a problem, since it causes the number of
		// views in the ActionBar to be different then the number of MenuItems.
		// Since we almost never use the overflow menu in the app, leaving this as a known issue to be handled by specific activities if required.
		int numberOfActionItemsToShiftTitleStartMargin = menu.size() - 1;
		int startMarginToAdd = getResources().getDimensionPixelSize(R.dimen.action_bar_item_width) * numberOfActionItemsToShiftTitleStartMargin;

		FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams)navigationToolbarTitle.getLayoutParams();
		// Only adjust the margin if the title is set to the center. This can be modified by calling setToolbarTitleGravity().
		if (layoutParams.gravity == Gravity.CENTER) {
			layoutParams.setMarginStart(startMarginToAdd);
			navigationToolbarTitle.setLayoutParams(layoutParams);
		}

		// Change the color of all menu items to fit the theme color. Done since the action bar doesn't seem to expose such a configurable attribute.
		for (int i=0; i<menu.size(); i++) {
			Drawable icon = menu.getItem(i).getIcon();
			if (icon != null) {
				icon.mutate().setColorFilter(ThemeAttributes.getColor(this, R.attr.toolbarIconColor), PorterDuff.Mode.MULTIPLY);
			}
		}

		return onCreateCustomOptionsMenuResult;
	}

	// Alternative callback for creating the options menu for derived classes to implement.
	protected boolean onCreateCustomOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	protected void onNavigationButtonClicked() {
		// Children can inherit this
	}

	private void initializeNavigationDrawer() {
		final List<NavigationItem> items = new ArrayList<>(Arrays.asList(
				new NavigationItem(HomeActivity.class, getString(R.string.home), ContextCompat.getDrawable(this, R.drawable.events_list)),
				new NavigationItem(ProgrammeActivity.class, getString(R.string.programme_title), ContextCompat.getDrawable(this, R.drawable.events_list)),
				new NavigationItem(MyEventsActivity.class, getString(R.string.my_events_title), ContextCompat.getDrawable(this, R.drawable.events_list_with_star))
		));

		// Only add the map if it's available
		if (Convention.getInstance().getMap().isAvailable()) {
			items.add(new NavigationItem(MapActivity.class, getString(R.string.map), ContextCompat.getDrawable(this, android.R.drawable.ic_dialog_map)));
		}
		items.add(new NavigationItem(UpdatesActivity.class, getString(R.string.updates), ContextCompat.getDrawable(this, android.R.drawable.stat_notify_sync_noanim)));
		items.add(new NavigationItem(ArrivalMethodsActivity.class, getString(R.string.arrival_methods), ContextCompat.getDrawable(this, R.drawable.directions)));

		if (Convention.getInstance().canFillFeedback()) {
			items.add(new NavigationItem(FeedbackActivity.class, getString(R.string.feedback), ContextCompat.getDrawable(this, R.drawable.feedback_menu_icon)));
		}
		items.add(new NavigationItem(AboutActivity.class, getString(R.string.about), ContextCompat.getDrawable(this, R.drawable.ic_action_about)));
		items.add(new NavigationItem(SettingsActivity.class, getString(R.string.settings), ContextCompat.getDrawable(this, R.drawable.ic_settings)));

		ListView navigationItems = (ListView) findViewById(R.id.navigation_items);
		navigationItems.setAdapter(new NavigationItemsAdapter(this, items));
	}

	private void setupActionBar(Toolbar toolbar) {
		this.setSupportActionBar(toolbar);
		ActionBar actionBar = this.getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(false);
			actionBar.setDisplayShowTitleEnabled(false);
			toolbar.setNavigationIcon(ThemeAttributes.getDrawable(this, R.attr.toolbarLogo));
		}
	}


	protected View setContentInContentContainer(int layoutResID) {
		return setContentInContentContainer(layoutResID, true, true);
	}

	protected View setContentInContentContainer(int layoutResID, boolean useDefaultBackground) {
		return setContentInContentContainer(layoutResID, useDefaultBackground, true);
	}

	protected View setContentInContentContainer(int layoutResID, boolean useDefaultBackground, boolean hideToolbarOnScroll) {
		contentContainer = (FrameLayout) findViewById(R.id.navigation_content_view_container);
		getLayoutInflater().inflate(layoutResID, contentContainer, true);

		if (!hideToolbarOnScroll) {
			AppBarLayout.LayoutParams layoutParams = (AppBarLayout.LayoutParams) navigationToolbar.getLayoutParams();
			layoutParams.setScrollFlags(0);
			navigationToolbar.setLayoutParams(layoutParams);
		}

		if (useDefaultBackground) {
			// Set the background in code due to an Android bug that if the background is set in the xml
			// and then removed, the foreground isn't displayed either and it's not possible to bring it back
			contentContainer.setBackgroundColor(ThemeAttributes.getColor(this, android.R.attr.colorBackground));
		}

		return contentContainer;
	}

	protected void setToolbarAndContentContainerBackground(Drawable drawable) {
		View toolbarAndContentContainer = findViewById(R.id.toolbarAndContentContainer);
		toolbarAndContentContainer.setBackground(drawable);
	}

	protected void setBackgroundColor(int color) {
		if (contentContainer != null) {
			contentContainer.setBackgroundColor(color);
		}
	}

	protected void removeBackground() {
		if (contentContainer != null) {
			contentContainer.setBackground(null);
		}
	}

	protected void removeForeground() {
		if (contentContainer != null) {
			contentContainer.setForeground(null);
		}
	}

	protected void setupActionButton(int imageResource, View.OnClickListener listener) {
		actionButton = (FloatingActionButton) findViewById(R.id.action_button);
		actionButton.setImageResource(imageResource);
		actionButton.setOnClickListener(listener);
		actionButton.setVisibility(View.VISIBLE);
	}

	protected void showActionButton(FloatingActionButton.OnVisibilityChangedListener listener) {
		if (actionButton != null) {
			actionButton.show(listener);
		}
	}

	protected void hideActionButton(FloatingActionButton.OnVisibilityChangedListener listener) {
		if (actionButton != null) {
			actionButton.hide(listener);
		}
	}

	public FloatingActionButton getActionButton() {
		return actionButton;
	}

	public void onConventionEventClicked(View view) {
		navigateToEvent((String) view.getTag());
	}

	protected void navigateToEvent(String id) {
		Bundle bundle = new Bundle();
		bundle.putString(EventActivity.EXTRA_EVENT_ID, id);
		addCustomEventActivityParameters(bundle);
		navigateToActivity(EventActivity.class, false, bundle);
		overridePendingTransition(0, 0);
	}

	protected void addCustomEventActivityParameters(Bundle bundle) {
	}

	protected void setToolbarTitle(String titleText) {
		navigationToolbarTitle.setText(titleText);
	}

	protected void setToolbarTitle(Drawable drawable) {
		navigationToolbarTitle.setBackground(drawable);
	}

	protected void setToolbarGravity(int gravity) {
		FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) navigationToolbarTitle.getLayoutParams();
		layoutParams.gravity = gravity;
		navigationToolbarTitle.setLayoutParams(layoutParams);
	}

	protected void navigateToActivity(Class<? extends Activity> activityToNavigateTo) {
		// When navigating using the main popup window, clear the activity stack so the back button would return to the home screen
		navigateToActivity(activityToNavigateTo, true, null);
	}

	protected void navigateToActivity(Class<? extends Activity> activityToNavigateTo, boolean clearBackStack, Bundle extras) {
		closeDrawerIfNeeded();

		// In case we were asked to navigate to the activity we're already in, ignore the request
		if (activityToNavigateTo == this.getClass() && extras == null) {
			return;
		}

		Intent intent = new Intent(this, activityToNavigateTo);
		if (clearBackStack) {
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			// If the user presses back when the back stack is clear we want to exit the application.
			// For some reason the splash activity remains on the back stack even with these flags
			// (it's probably a good thing due to the issue described in SplashActivity)
			// so we must explicitly handle the back in this case.
			// This flag only has a meaning if we're navigating to a NavigationActivity.
			if (NavigationActivity.class.isAssignableFrom(activityToNavigateTo)) {
				intent.putExtra(EXTRA_EXIT_ON_BACK, true);
			}
			finish();
		}

		if (extras != null) {
			intent.putExtras(extras);
		}

		startActivity(intent);

		if (clearBackStack) {
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		} else {
			overridePendingTransition(R.anim.grow_fade_in_from_bottom, R.anim.shrink_fade_out_from_bottom);
		}
	}

	private void closeDrawerIfNeeded() {
		if (navigationDrawer.isDrawerOpen(GravityCompat.START)) {
			navigationDrawer.closeDrawer(GravityCompat.START, false);
		}
	}

	@Override
	public void onBackPressed() {
		if (exitOnBack) {
			// We want to exist when pressing back because the SplashActivity is still in the back stack
			// and will be displayed if we don't tell it to finish. See SplashActivity onCreate for the
			// reason it's in the back stack instead of being finished.
			Bundle bundle = new Bundle();
			bundle.putBoolean(SplashActivity.EXTRA_FINISH, true);
			Intent intent = new Intent(this, SplashActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			finish();
			intent.putExtras(bundle);
			startActivity(intent);
			return;
		}
		super.onBackPressed();
	}
}
