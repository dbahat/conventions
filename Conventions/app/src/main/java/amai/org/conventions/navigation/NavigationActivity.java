package amai.org.conventions.navigation;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import amai.org.conventions.AboutActivity;
import amai.org.conventions.ApplicationInitializer;
import amai.org.conventions.ArrivalMethodsActivity;
import amai.org.conventions.ConventionsApplication;
import amai.org.conventions.DiscountsActivity;
import amai.org.conventions.FeedbackActivity;
import amai.org.conventions.HomeActivity;
import amai.org.conventions.SplashActivity;
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.WebContentActivity;
import amai.org.conventions.events.activities.EventActivity;
import amai.org.conventions.events.activities.MyEventsActivity;
import amai.org.conventions.events.activities.ProgrammeActivity;
import amai.org.conventions.events.adapters.DayFragmentAdapter;
import amai.org.conventions.map.MapActivity;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.notifications.PlayServicesInstallation;
import amai.org.conventions.notifications.PushNotification;
import amai.org.conventions.notifications.PushNotificationDialogPresenter;
import amai.org.conventions.secondhand.SecondHandActivity;
import amai.org.conventions.settings.SettingsActivity;
import amai.org.conventions.tasks.Task;
import amai.org.conventions.tasks.TasksExecutor;
import amai.org.conventions.updates.UpdatesActivity;
import amai.org.conventions.utils.Dates;
import amai.org.conventions.utils.Log;
import amai.org.conventions.utils.Settings;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;
import sff.org.conventions.R;

import static amai.org.conventions.notifications.PushNotificationDialogPresenter.EXTRA_PUSH_NOTIFICATION;


public abstract class NavigationActivity extends AppCompatActivity {
	public static final String EXTRA_INITIALIZE = "ExtraInitialize";
	public static final String EXTRA_EXIT_ON_BACK = "ExtraExitOnBack";
	public static final String EXTRA_SHOW_HOME_ON_BACK = "ExtraShowHomeOnBack";
	private static final String STATE_TASK_EXECUTOR_SHARED_DATA = "TaskExecutorSharedData";
	protected static final String STATE_DISABLE_PERMISSION_REQUEST = "DisablePermissionRequest";
	protected static final String STATE_DISABLE_NEXT_TASK_EXECUTION = "DisableNextTaskExecution";

	protected static final int SELECT_CURRENT_DATE = -1;

	private TextView navigationToolbarTitle;
	private Toolbar navigationToolbar;
	private boolean exitOnBack;
	private boolean showHomeOnBack;
	private FrameLayout contentContainer;
	private FloatingActionButton actionButton;
	private DrawerLayout navigationDrawer;
	private PushNotification receivedPushNotification;
	private NavigationTopButtonsLayout navigationTopButtonsLayout;
	private boolean disablePermissionRequest = false;
	private boolean nextTaskExecutionDisabled = false;

	private final TasksExecutor<Boolean> permissionTasks = new TasksExecutor<>(
		Boolean.FALSE,
		// First ask generally to show notifications
		new AskForNotificationsPermissionTask(),
		// Ask for specific permissions for event about to start exact alarm
		new AskForExactAlarmsPermissionTask(),
		// Ask to install Google Play Services for push notifications
		new InstallGooglePlayServicesTask()
	);

	private String continueAfterTask = null;

	private final ActivityResultLauncher<String> requestNotificationsPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
		// We don't care about the result, if the permissions is blocked the notifications aren't displayed
		continueAfterTask = AskForNotificationsPermissionTask.NAME;
	});

	private final ActivityResultLauncher<Intent> requestExactPermissionsIntentLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
		// We handle the actual result in RescheduleAlarmsReceiver. This is here just so we can continue executing tasks.
		continueAfterTask = AskForExactAlarmsPermissionTask.NAME;
	});


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_navigation);

		exitOnBack = getIntent().getBooleanExtra(EXTRA_EXIT_ON_BACK, false);
		showHomeOnBack = getIntent().getBooleanExtra(EXTRA_SHOW_HOME_ON_BACK, false);

		if (shouldInitialize(savedInstanceState)) {
			new ApplicationInitializer().initialize(this.getApplicationContext());
		}

		if (savedInstanceState != null) {
			permissionTasks.setSharedData(savedInstanceState.getBoolean(STATE_TASK_EXECUTOR_SHARED_DATA, false));
			if (savedInstanceState.getBoolean(STATE_DISABLE_NEXT_TASK_EXECUTION, false)) {
				disableNextTaskExecution();
			}
		}

		navigationToolbar = (Toolbar) findViewById(R.id.navigation_toolbar);
		navigationToolbarTitle = (TextView) findViewById(R.id.navigation_toolbar_title);
		navigationDrawer = (DrawerLayout) findViewById(R.id.navigation_drawer);
		setupActionBar(navigationToolbar);
		navigationToolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onNavigationButtonClicked();
				openNavigationDrawer(true);
			}
		});

		navigationTopButtonsLayout = findViewById(R.id.navigation_drawer_settings_layout);

		initializeNavigationDrawer(); // In case it was already open

		// In case the user opens it manually instead of from the button or it was already open
		navigationDrawer.post(new Runnable() {
			@Override
			public void run() {
				setNavigationDrawerHeight();
			}
		});

		actionButton = (FloatingActionButton) findViewById(R.id.action_button);

		// We will display the notification once during onResume to let child activities override it safely
		// (after their onCreate is called)
		this.receivedPushNotification = getNotificationFromIntent(getIntent());
	}

	protected boolean shouldInitialize(Bundle savedInstanceState) {
		return (getIntent().getBooleanExtra(EXTRA_INITIALIZE, false) &&
			!(savedInstanceState != null && savedInstanceState.getBoolean(EXTRA_INITIALIZE, true)));
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		// Add extras to the original intent so we will focus on the same update on screen rotation
		getIntent().putExtras(intent.getExtras());

		// In case the user clicked a push notification while inside the activity, have the activity
		// show the push notification (as we won't re-create the activity, not to create an extra copy of it on the activity stack)
		showPushNotificationIfReceived(getNotificationFromIntent(intent));
	}

	protected void disableNextTaskExecution() {
		nextTaskExecutionDisabled = true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		showPushNotificationIfReceived(this.receivedPushNotification);
		this.receivedPushNotification = null;

		if (continueAfterTask != null && !nextTaskExecutionDisabled) {
			String taskToContinueAfter = continueAfterTask;
			continueAfterTask = null;
			permissionTasks.executeNextTaskAfter(taskToContinueAfter);
		}
	}

	private PushNotification getNotificationFromIntent(Intent intent) {
		Serializable pushNotification = intent.getSerializableExtra(EXTRA_PUSH_NOTIFICATION);
		if (pushNotification instanceof PushNotification) {
			return (PushNotification) pushNotification;
		}
		return null;
	}

	private void showPushNotificationIfReceived(PushNotification pushNotification) {
		if (pushNotification != null) {
			onPushNotificationReceived(pushNotification);
		}
	}

	protected void onPushNotificationReceived(PushNotification pushNotification) {
		new PushNotificationDialogPresenter().present(this, pushNotification);
	}

	private void openNavigationDrawer(boolean animate) {
		initializeNavigationDrawer();
		navigationDrawer.openDrawer(GravityCompat.START, animate);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(EXTRA_INITIALIZE, false); // Prevent re-initializing
		outState.putBoolean(STATE_TASK_EXECUTOR_SHARED_DATA, permissionTasks.getSharedData()); // So we can continue the flow if the app was restarted
		outState.putBoolean(STATE_DISABLE_PERMISSION_REQUEST, disablePermissionRequest); // If we're waiting for the result, don't request the permissions again
		outState.putBoolean(STATE_DISABLE_NEXT_TASK_EXECUTION, nextTaskExecutionDisabled); // If we started a one-time task, don't continue the flow
		super.onSaveInstanceState(outState);
	}

	// Overriding onCreateOptionsMenu so we can tell when extending classes completed inflating the options menu.
	// In order for extending classes to create an options menu, override onCreateCustomOptionsMenu();
	// Needed so we can take actions based on the number of actions the activity defined (e.g. adjust the toolbar title margin to be centered).
	@Override
	public final boolean onCreateOptionsMenu(Menu menu) {
		return onCreateCustomOptionsMenu(menu);
	}

	// Alternative callback for creating the options menu for derived classes to implement.
	protected boolean onCreateCustomOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	protected void onNavigationButtonClicked() {
		// Children can inherit this
	}

	private void initializeNavigationDrawer() {
		boolean shouldDisplayIcon = ThemeAttributes.getBoolean(this, R.attr.navigationItemsShouldDisplayIcon);

		final List<NavigationItem> items = new ArrayList<>(Arrays.asList(
				new NavigationItem(HomeActivity.class, getString(R.string.home), ContextCompat.getDrawable(this, R.drawable.ic_home_white_36dp), shouldDisplayIcon),
				new NavigationItem(ProgrammeActivity.class, getString(R.string.programme_title), ContextCompat.getDrawable(this, R.drawable.events_list), shouldDisplayIcon),
//				new NavigationItem(WebContentActivity.IconKidsActivity.class, getString(R.string.icon_kids), ContextCompat.getDrawable(this, R.drawable.ic_face_white_24dp), shouldDisplayIcon),
				new NavigationItem(MyEventsActivity.class, getString(R.string.my_events_title), ContextCompat.getDrawable(this, R.drawable.events_list_with_star), shouldDisplayIcon)
		));

		// Only add the map if it's available
		if (Convention.getInstance().getMap().isAvailable()) {
			items.add(new NavigationItem(MapActivity.class, getString(R.string.map), ContextCompat.getDrawable(this, android.R.drawable.ic_dialog_map), shouldDisplayIcon));
		}
		items.add(new NavigationItem(UpdatesActivity.class, getString(R.string.updates), ContextCompat.getDrawable(this, android.R.drawable.stat_notify_sync_noanim), shouldDisplayIcon));
		items.add(new NavigationItem(SecondHandActivity.class, getString(R.string.second_hand), ContextCompat.getDrawable(this, R.drawable.ic_attach_money_white), shouldDisplayIcon));
		items.add(new NavigationItem(ArrivalMethodsActivity.class, getString(R.string.arrival_methods), ContextCompat.getDrawable(this, R.drawable.directions), shouldDisplayIcon));

		if (Convention.getInstance().canFillFeedback()) {
			items.add(new NavigationItem(FeedbackActivity.class, getString(R.string.feedback), ContextCompat.getDrawable(this, R.drawable.feedback_menu_icon), shouldDisplayIcon));
		}
		items.add(new NavigationItem(DiscountsActivity.class, getString(R.string.discounts), ContextCompat.getDrawable(this, R.drawable.ic_card_giftcard_white), shouldDisplayIcon));
//		items.add(new NavigationItem(ActivitiesActivity.class, getString(R.string.activities), ContextCompat.getDrawable(this, R.drawable.ic_card_giftcard_white), shouldDisplayIcon));
		items.add(new NavigationItem(WebContentActivity.AccessibilityActivity.class, getString(R.string.accessibility), ContextCompat.getDrawable(this, R.drawable.baseline_accessibility_new_white_18), null, true, true));
		items.add(new NavigationItem(AboutActivity.class, getString(R.string.about), ContextCompat.getDrawable(this, R.drawable.ic_action_about), shouldDisplayIcon));
//		items.add(new NavigationItem(SettingsActivity.class, getString(R.string.settings), ContextCompat.getDrawable(this, R.drawable.ic_settings), shouldDisplayIcon));

		navigationTopButtonsLayout.setNavigationItems(this, Arrays.asList(
//				new NavigationItem(AboutActivity.class, getString(R.string.about), ContextCompat.getDrawable(this, R.drawable.about), shouldDisplayIcon),
				new NavigationItem(SettingsActivity.class, getString(R.string.settings), ContextCompat.getDrawable(this, R.drawable.settings), shouldDisplayIcon)
		));

		ListView navigationItems = (ListView) findViewById(R.id.navigation_items);
		navigationItems.setAdapter(new NavigationItemsAdapter(this, items));

		setNavigationDrawerHeight();
	}

	private void setNavigationDrawerHeight() {
		// Set the minimum height of the navigation drawer content to the height of its parent, so that the bottom image will really be at the bottom
		ViewGroup navigationDrawerContent = findViewById(R.id.navigation_drawer_content);
		navigationDrawerContent.setMinimumHeight(navigationDrawer.getHeight());
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
		// Removing all previous views before attaching the newly inflated layout to support calling setContentInContentContainer() more then once
		contentContainer.removeAllViews();
		getLayoutInflater().inflate(layoutResID, contentContainer, true);

		if (!hideToolbarOnScroll) {
			AppBarLayout.LayoutParams layoutParams = (AppBarLayout.LayoutParams) navigationToolbar.getLayoutParams();
			layoutParams.setScrollFlags(0);
			navigationToolbar.setLayoutParams(layoutParams);
		}

//		if (useDefaultBackground) {
//			// Set the background in code due to an Android bug that if the background is set in the xml
//			// and then removed, the foreground isn't displayed either and it's not possible to bring it back
//			int colorBackground = ThemeAttributes.getColor(this, android.R.attr.colorBackground);
//			setContentContainerBackground(new ColorDrawable(colorBackground));
//		} else {
//			removeContentContainerBackground();
//		}

		setBackground(ThemeAttributes.getDrawable(this, R.attr.activitiesBackground));

		return contentContainer;
	}

	protected void setBackground(Drawable drawable) {
		if (navigationToolbar.getBackground() == null) {
			setToolbarAndContentContainerBackground(drawable);
			setContentContainerBackground(null);
		} else {
			setContentContainerBackground(drawable);
			setToolbarAndContentContainerBackground(null);
		}
	}

	protected void setToolbarAndContentContainerBackground(Drawable drawable) {
		View toolbarAndContentContainer = findViewById(R.id.toolbarAndContentContainer);
		toolbarAndContentContainer.setBackground(drawable);
	}

	/** Always call {@link #setBackground(Drawable)} after this method */
	protected void setToolbarBackground(Drawable drawable) {
		navigationToolbar.setBackground(drawable);
	}

	protected void setContentContainerBackground(Drawable drawable) {
		if (contentContainer != null) {
			contentContainer.setBackground(drawable);
		}
	}

	protected void removeContentContainerBackground() {
		if (contentContainer != null) {
			contentContainer.setBackground(null);
		}
	}

	protected void removeContentContainerForeground() {
		if (contentContainer != null) {
			contentContainer.setForeground(null);
		}
	}

	protected void setupActionButton(Drawable image, View.OnClickListener listener) {
		actionButton.setImageDrawable(image);
		actionButton.setOnClickListener(listener);
		actionButton.setVisibility(View.VISIBLE);
	}

	protected void removeActionButton() {
		actionButton.setVisibility(View.GONE);
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

	protected void setupDaysTabs(TabLayout daysTabLayout, ViewPager daysPager, DayFragmentAdapter adapter, int dateIndexToSelect) {
		int days = Convention.getInstance().getLengthInDays();
		if (days == 1) {
			daysTabLayout.setVisibility(View.GONE);
		}

		Drawable tabIndicator = ThemeAttributes.getDrawable(this, R.attr.selectedTabIndicator);
		if (tabIndicator != null) {
			daysTabLayout.setSelectedTabIndicator(tabIndicator);
		}

		// Setup view pager
		daysPager.setAdapter(adapter);
		daysPager.setOffscreenPageLimit(days); // Load all dates for smooth scrolling

		// Setup tabs
		daysTabLayout.setupWithViewPager(daysPager, false);

		int selectedDateIndex = dateIndexToSelect;
		// Find the current date's index if requested
		if (dateIndexToSelect == SELECT_CURRENT_DATE) {
			selectedDateIndex = adapter.getItemToDisplayForDate(Dates.toCalendar(Dates.now()));
		}

		// Default - first day
		if (selectedDateIndex < 0) {
			selectedDateIndex = 0;
		}
		daysPager.setCurrentItem(selectedDateIndex, false);
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
			// If the user presses back when the back stack is clear and the current screen is the home activity
			// we want to exit the application.
			// For some reason the splash activity remains on the back stack even with these flags
			// (it's probably a good thing due to the issue described in SplashActivity)
			// so we must explicitly handle the back in this case.
			// This flag only has a meaning if we're navigating to a NavigationActivity.
			if (activityToNavigateTo.equals(HomeActivity.class)) {
				intent.putExtra(EXTRA_EXIT_ON_BACK, true);
			} else if (NavigationActivity.class.isAssignableFrom(activityToNavigateTo)) {
				// When the user presses back and the back stack is clear, and we're not in the home activity,
				// we should go back to the home activity.
				// This flag only has a meaning if we're navigating to a NavigationActivity.
				intent.putExtra(EXTRA_SHOW_HOME_ON_BACK, true);
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
			// We want to exit when pressing back because the SplashActivity is still in the back stack
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
		} else if (showHomeOnBack) {
			navigateToActivity(HomeActivity.class);
		}
		super.onBackPressed();
	}


	/**
	 * Change color of menu item icon to be accented
	 *
	 * @param item the menu item
	 * @return The new color
	 */
	protected int changeIconColor(MenuItem item) {
		if (item == null) {
			return Convention.NO_COLOR;
		}
		Drawable icon = item.getIcon().mutate();
		int accentColor = ThemeAttributes.getColor(this, R.attr.toolbarIconAccentColor);
		icon.setColorFilter(accentColor, PorterDuff.Mode.SRC_ATOP);
		return accentColor;
	}

	/**
	 * Change color of menu item icon to be accented
	 *
	 * @param item the menu item
	 * @return The new color
	 */
	protected void resetIconColor(MenuItem item) {
		if (item == null) {
			return;
		}
		Drawable icon = item.getIcon().mutate();
		icon.setColorFilter(null);
	}

	protected void askForPermissions() {
		/*
		We need to request 2 kinds of permissions: to display notifications, and to schedule exact alarms.
		The app can still work in both cases:
		- If we don't have permissions to display notifications, they will just not be displayed.
		- If we don't have permissions to schedule exact alarms, we will schedule inexact alarms for event about to start.

		For push notifications, the user must have Google Play Services installed, so we also check this and ask to install it if it isn't.

		We don't want to ask the user for permissions if they already decided not to grant it.
		The notifications permission has Android logic to choose when to ask for it, so we can call it every time (it just won't be displayed if it shouldn't be).
		It's denied when the user chooses no in the system grant notification dialog.
		The Android logic is: ask for permissions twice, the second time with rationale. If denied both time don't ask again. (We show the rationale the first time too anyway.)

		For the exact alarms notifications, since it's a special permission, we have to decide when to show it. This will also be twice. It's denied when the user got to the setting page
		and didn't choose to approve the permission, or when the user selected no in the rationale dialog. It's reset when the app starts and already has the permission.
		Since the rationale dialog from the notifications permission is also relevant for the exact alarms, we don't display the exact alarms dialog it it was displayed in the same flow.

		For Google Play Services installation, we also decide when to show it. It will be twice. It's denied when the user chose not to install in the dialog.

		Since the alarms and push notifications are not be displayed anyway unless we can show notifications, we only ask for them when we have permissions
		to display notifications.
		 */
		disablePermissionRequest = true;
		permissionTasks.execute();
	}

	private static void showConfigureNotificationsDialog(final Activity context, Runnable onOk) {
		if (context == null) {
			return;
		}

		new AlertDialog.Builder(context)
			.setTitle(R.string.configure_notifications)
			.setMessage(R.string.configure_notifications_dialog_message)
			.setPositiveButton(R.string.ok, (dialog, which) -> {
				if (onOk != null) {
					onOk.run();
				}
				dialog.dismiss();
			})
			.setCancelable(true)
			.show();

		ConventionsApplication.settings.setSettingsPopupAsDisplayed();
	}

	private static void showExactAlarmsRationaleDialog(final Activity context, Runnable onOk, Runnable onCancel) {
		if (context == null) {
			return;
		}
		new AlertDialog.Builder(context)
			.setTitle(R.string.configure_alarms)
			.setMessage(R.string.configure_alarms_dialog_message)
			.setPositiveButton(R.string.ok, (dialog, which) -> {
				if (onOk != null) {
					onOk.run();
				}
				dialog.dismiss();
			})
			.setNegativeButton(R.string.dont_configure_notifications, (dialog, which) -> {
				if (onCancel != null) {
					onCancel.run();
				}
				dialog.dismiss();
			})
			.setCancelable(false)
			.show();
	}

	private class AskForNotificationsPermissionTask extends Task<Boolean> {
		public final static String NAME = "AskForNotificationsPermissionTask";

		@Override
		public String getName() {
			return NAME;
		}

		@Override
		public void execute(Activity activity) {
			if (ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
				executeNextTask();
				return;
			}

			if (ConventionsApplication.settings.wasSettingsPopupDisplayed() && !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.POST_NOTIFICATIONS)) {
				requestNotificationsPermission(activity);
				return;
			}

			showConfigureNotificationsDialog(activity, () -> {
				setSharedData(true); // Shared data here is whether the configure notifications dialog was displayed
				requestNotificationsPermission(activity);
			});
		}

		private void requestNotificationsPermission(Activity context) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				requestNotificationsPermissionLauncher.launch("android.permission.POST_NOTIFICATIONS");
			} else {
				executeNextTask();
			}
		}
	}

	private static class InstallGooglePlayServicesTask extends Task<Boolean> {
		public final static String NAME = "InstallGooglePlayServicesTask";

		@Override
		public String getName() {
			return NAME;
		}

		@Override
		public void execute(Activity currentActivity) {
			// If we can't show notifications, there is no reason to install google play services here
			// (it is also used for arrival methods, but we ask for it there too)
			if (ContextCompat.checkSelfPermission(currentActivity, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
				executeNextTask();
				return;
			}

			// Since push notifications cannot work without google play services, check for play services existence, and if
			// they don't exist show a proper message to the user.
			new AsyncTask<Void, Void, PlayServicesInstallation.CheckResult>() {
				@Override
				protected PlayServicesInstallation.CheckResult doInBackground(Void... params) {
					return PlayServicesInstallation.checkPlayServicesExist(currentActivity, false);
				}

				@Override
				protected void onPostExecute(PlayServicesInstallation.CheckResult checkResult) {
					// We use the current activity context and not the initial activity because it's possible the user
					// already navigated from it. We can't use a destroyed activity to display a dialog
					// since it causes an exception.
					Context currentContext = ConventionsApplication.getCurrentContext();
					if (!(currentContext instanceof Activity)) {
						return;
					}
					if (checkResult.isUserError()) {
						PlayServicesInstallation.showInstallationDialog(currentContext, checkResult, () -> {
							executeNextTask();
						});
					} else {
						executeNextTask();
					}
				}
			}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}

		@Override
		public void setNextTask(Task<Boolean> nextTask) {
			// To support a next task we have to handle the result of the installation so the next task doesn't run
			// in parallel to another activity and the user can see it
			throw new IllegalArgumentException("Next task after " + NAME + " is not supported");
		}
	}

	protected class AskForExactAlarmsPermissionTask extends Task<Boolean> {
		public final static String NAME = "AskForExactAlarmsPermissionTask";

		@Override
		public String getName() {
			return NAME;
		}

		@Override
		public void execute(Activity currentActivity) {
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
				executeNextTask();
				return;
			}

			// If we can't show notifications, there is no reason to ask for exact alarm permissions
			if (ContextCompat.checkSelfPermission(currentActivity, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
				executeNextTask();
				return;
			}

			AlarmManager alarmManager = (AlarmManager) currentActivity.getSystemService(Context.ALARM_SERVICE);
			if (alarmManager.canScheduleExactAlarms()) {
				// Reset the number of times we asked for exact alarms if the user has granted it
				// (so in case it's revoked, we'll start counting from 0 again)
				ConventionsApplication.settings.resetNumberOfTimesAskedForExactAlarms();
				executeNextTask();
				return;
			}

			// If the user doesn't want reminders for event start, don't ask for the permission (we will ask for it in case they change the settings)
			if (!ConventionsApplication.settings.getSharedPreferences().getBoolean(Convention.getInstance().getId().toLowerCase() + "_event_starting_reminder", false)) {
				executeNextTask();
				return;
			}

			// Check if we should ask for exact alarms permissions
			if (ConventionsApplication.settings.getNumberOfTimesAskedForExactAlarms() >= Settings.MAX_CANCEL_TIMES) {
				executeNextTask();
				return;
			}

			// Don't show the dialog again if already displayed
			if (getSharedData()) { // Shared data here is whether the configure notifications dialog was displayed
				requestScheduleExactAlarmsPermission(currentActivity);
			} else {
				showExactAlarmsRationaleDialog(currentActivity, () -> {
					setSharedData(true); // Shared data here is whether the configure notifications dialog was displayed
					requestScheduleExactAlarmsPermission(currentActivity);
				}, () -> {
					ConventionsApplication.settings.askedForExactAlarms();
					executeNextTask();
				});
			}
		}

		@RequiresApi(api = Build.VERSION_CODES.S)
		private void requestScheduleExactAlarmsPermission(Activity currentActivity) {
			ConventionsApplication.settings.askedForExactAlarms();
			Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
			Uri uri = Uri.fromParts("package", currentActivity.getPackageName(), null);
			intent.setData(uri);
			requestExactPermissionsIntentLauncher.launch(intent);
		}
	}

	protected AskForExactAlarmsPermissionTask newAskForExactAlarmsPermissionTask() {
		return new AskForExactAlarmsPermissionTask();
	}
}
