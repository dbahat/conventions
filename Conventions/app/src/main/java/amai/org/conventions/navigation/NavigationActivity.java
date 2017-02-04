package amai.org.conventions.navigation;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;

import com.caverock.androidsvg.SVG;
import com.google.android.gms.analytics.HitBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import amai.org.conventions.AboutActivity;
import amai.org.conventions.ArrivalMethodsActivity;
import amai.org.conventions.ConventionsApplication;
import amai.org.conventions.FeedbackActivity;
import amai.org.conventions.HomeActivity;
import amai.org.conventions.ImageHandler;
import amai.org.conventions.R;
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.events.activities.EventActivity;
import amai.org.conventions.events.activities.MyEventsActivity;
import amai.org.conventions.events.activities.ProgrammeActivity;
import amai.org.conventions.map.MapActivity;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.settings.SettingsActivity;
import amai.org.conventions.updates.UpdatesActivity;
import amai.org.conventions.utils.Views;


public abstract class NavigationActivity extends AppCompatActivity {
	public static final String EXTRA_NAVIGATED_FROM_HOME = "ExtraNavigatedFromHome";
	private static final String EXTRA_SHOW_HOME_SCREEN_ON_BACK = "ExtraShowHomeScreenOnBack";

	private static boolean showLogoGlow = true;
	private boolean navigatedFromHome;
    private Toolbar navigationToolbar;
	private boolean showHomeScreenOnBack;
	private FrameLayout contentContainer;
	private FloatingActionButton actionButton;
	private DrawerLayout navigationDrawer;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

	    navigatedFromHome = getIntent().getBooleanExtra(EXTRA_NAVIGATED_FROM_HOME, false);
	    showHomeScreenOnBack = getIntent().getBooleanExtra(EXTRA_SHOW_HOME_SCREEN_ON_BACK, false);

	    navigationToolbar = (Toolbar) findViewById(R.id.navigation_toolbar);
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
    }

	private void openNavigationDrawer(boolean animate) {
		initializeNavigationDrawer();
		navigationDrawer.openDrawer(GravityCompat.START, animate);
		ConventionsApplication.settings.setNavigationPopupOpened();
	}

	private boolean shouldShowLogoGlow() {
		return showLogoGlow && navigatedFromHome && HomeActivity.getNumberOfTimesNavigated() > 1 && !ConventionsApplication.settings.wasNavigationPopupOpened();
	}

	protected void onNavigationButtonClicked() {
		// Children can inherit this
	}

	private void initializeNavigationDrawer() {
		final List<NavigationItem> items = new ArrayList<>(Arrays.asList(
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
            actionBar.setDisplayShowTitleEnabled(true);
	        int logoType = ThemeAttributes.getInteger(this, R.attr.toolbarLogoType);
	        Drawable drawable = null;
	        switch (logoType) {
		        // bitmap
		        case 0:
			        drawable = ImageHandler.getToolbarLogo(this);
			        break;
		        // svg
		        case 1:
			        toolbar.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
	                SVG logoSVG = ImageHandler.loadSVG(this, ThemeAttributes.getResourceId(this, R.attr.toolbarLogo));
			        drawable = new PictureDrawable(logoSVG.renderToPicture());
			        break;
	        }
            toolbar.setNavigationIcon(drawable);

	        if (shouldShowLogoGlow()) {
				// Getting the toolbar imageView by iterating over the toolbar children, since the toolbar imageView has no ID.
		        for (int i = 0; i < toolbar.getChildCount(); ++i) {
			        View view = toolbar.getChildAt(i);
			        if (view instanceof ImageView && ((ImageView) view).getDrawable() == drawable) {
				        final ImageView image = (ImageView) view;
				        final ValueAnimator animator = ValueAnimator.ofInt(255, 100, 255);
				        animator.setInterpolator(new AccelerateDecelerateInterpolator());
				        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
					        @Override
					        public void onAnimationUpdate(ValueAnimator animation) {
						        int value = (int) animation.getAnimatedValue();
						        int mul = Color.argb(0, value, value, value);
						        int add = Color.argb(0, 255 - value, 255 - value, 255 - value);
						        image.setColorFilter(new LightingColorFilter(mul, add));
					        }
				        });
				        animator.addListener(new Animator.AnimatorListener() {
					        // This is necessary because if I call animation.cancel() on animation start
					        // it calls onAnimationStart again, causing an infinite recursion
					        private boolean cancelled = false;

					        @Override
					        public void onAnimationStart(Animator animation) {
						        if (!shouldShowLogoGlow() && !cancelled) {
							        cancelled = true;
							        animation.cancel();
						        }
					        }

					        @Override
					        public void onAnimationEnd(Animator animation) {
						        image.setColorFilter(null);
					        }

					        @Override
					        public void onAnimationCancel(Animator animation) {
						        image.setColorFilter(null);
					        }

					        @Override
					        public void onAnimationRepeat(Animator animation) {
					        }
				        });
				        animator.setDuration(1500).setStartDelay(3000);
				        animator.start();
			        }
		        }
	        }
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
        getSupportActionBar().setTitle(titleText);
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
			// If the user presses back when the back stack is clear we want to show the home screen
			// instead of existing.
			// This flag only has a meaning if we're navigating to a NavigationActivity.
			if (NavigationActivity.class.isAssignableFrom(activityToNavigateTo)) {
				intent.putExtra(EXTRA_SHOW_HOME_SCREEN_ON_BACK, true);
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
		if (showHomeScreenOnBack) {
			navigateToActivity(HomeActivity.class, true, null);
			return;
		}
		super.onBackPressed();
	}
}
