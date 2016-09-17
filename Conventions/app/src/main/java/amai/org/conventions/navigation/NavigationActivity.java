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

import amai.org.conventions.ArrivalMethodsActivity;
import amai.org.conventions.ConventionsApplication;
import amai.org.conventions.FeedbackActivity;
import amai.org.conventions.ImageHandler;
import amai.org.conventions.SplashActivity;
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.customviews.AnimationPopupWindow;
import amai.org.conventions.events.activities.EventActivity;
import amai.org.conventions.events.activities.MyEventsActivity;
import amai.org.conventions.events.activities.ProgrammeActivity;
import amai.org.conventions.map.MapActivity;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.settings.SettingsActivity;
import amai.org.conventions.updates.UpdatesActivity;
import amai.org.conventions.utils.Views;
import sff.org.conventions.R;


public abstract class NavigationActivity extends AppCompatActivity {
	public static final String EXTRA_NAVIGATED_FROM_HOME = "ExtraNavigatedFromHome";
	public static final String EXTRA_EXIT_ON_BACK = "ExtraExitOnBack";

	private static boolean showLogoGlow = true;
	private boolean navigatedFromHome;
    private Toolbar navigationToolbar;
    private AnimationPopupWindow popup;
	private boolean exitOnBack;
	private FrameLayout contentContainer;
	private FloatingActionButton actionButton;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

	    navigatedFromHome = getIntent().getBooleanExtra(EXTRA_NAVIGATED_FROM_HOME, false);
	    exitOnBack = getIntent().getBooleanExtra(EXTRA_EXIT_ON_BACK, false);

	    navigationToolbar = (Toolbar) findViewById(R.id.navigation_toolbar);
        setupActionBar(navigationToolbar);
        navigationToolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showLogoGlow = false;
				ConventionsApplication.settings.setNavigationPopupOpened();
				onNavigationButtonClicked();
				if (popup == null || !popup.isShowing()) {
					popup = createNavigationPopup();
					popup.showAsDropDown(navigationToolbar);

					ConventionsApplication.sendTrackingEvent(new HitBuilders.EventBuilder()
							.setCategory("Navigation")
							.setAction("ButtonClicked")
							.build());
				}
			}
		});
    }

	protected void onNavigationButtonClicked() {
		// Children can inherit this
	}

	private boolean shouldShowLogoGlow() {
		return false;
	}

	private AnimationPopupWindow createNavigationPopup() {
		// This view is the root view of the popup window. It's not related to the view hierarchy and its layout
		// parameters are defined by the popup window.
		@SuppressLint("InflateParams")
		final View view = LayoutInflater.from(NavigationActivity.this).inflate(R.layout.navigation_menu, null);

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
		items.add(new NavigationItem(SettingsActivity.class, getString(R.string.settings), ContextCompat.getDrawable(this, R.drawable.ic_settings)));

		ListView navigationItems = (ListView) view.findViewById(R.id.navigation_items);
		navigationItems.setAdapter(new NavigationItemsAdapter(this, items));

		// Set list width - wrap_content doesn't work due to unknown number of items in the list view
		navigationItems.getLayoutParams().width = Views.calculateWrapContentWidth(this, navigationItems.getAdapter());
		navigationItems.setLayoutParams(navigationItems.getLayoutParams());

		final AnimationPopupWindow popup = new AnimationPopupWindow(
		        view,
	            // Sending toolbar width to support Jelly Bean version: the popup window does not align to
	            // the right automatically so we set the width to full screen and inside it, the card view is
	            // wrap_content with layout direction is ltr so it looks ok.
				// Also there is a bug in KitKat where if we set the width to match_parent it takes up the whole
				// width INCLUDING THE BUTTONS BAR and the content gets cut off on the right side. For this
				// reason we also can't just set the offset of the popup window to the far right (since it gets
				// cut off).
				navigationToolbar.getWidth(),
		        ViewGroup.LayoutParams.WRAP_CONTENT,
		        R.anim.drop_down,
		        R.anim.drop_down_reverse);

		// This must be done to enable listening to clicks on the popup window
		popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		popup.setAnimationStyle(0);
		popup.setFocusable(true);
		popup.setOutsideTouchable(true);

		// Now, because the popup is the full width of the screen, we must capture a touch event outside
		// the card view and dismiss it.
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				popup.dismiss();
			}
		});

		// Move the inner view to the top start corner of the toolbar.
		// This is required because inside it is a card view with elevation (which uses padding for
		// the elevation and shadow) so we have to move it to the start point of the popup window.
		view.setY(getResources().getDimension(R.dimen.navigation_popup_window_offset_y));
		view.setX(getResources().getDimension(R.dimen.navigation_popup_window_offset_x));

		return popup;
	}

	@Override
    protected void onDestroy() {
        super.onDestroy();

        dismissPopupIfNeeded();
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
	    Bundle bundle = new Bundle();
        bundle.putString(EventActivity.EXTRA_EVENT_ID, (String) view.getTag());
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

		dismissPopupIfNeeded();

		// In case we were asked to navigate to the activity we're already in, ignore the request
		if (activityToNavigateTo == this.getClass()) {
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

    private void dismissPopupIfNeeded() {
        if (popup != null && popup.isShowing()) {
            popup.dismissNow();
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
