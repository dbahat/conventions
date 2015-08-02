package amai.org.conventions.navigation;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.caverock.androidsvg.SVG;

import java.util.LinkedHashMap;
import java.util.Map;

import amai.org.conventions.ArrivalMethodsActivity;
import amai.org.conventions.HomeActivity;
import amai.org.conventions.R;
import amai.org.conventions.SVGFileLoader;
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.customviews.AnimationPopupWindow;
import amai.org.conventions.events.activities.EventActivity;
import amai.org.conventions.events.activities.ProgrammeActivity;
import amai.org.conventions.map.MapActivity;
import amai.org.conventions.updates.UpdatesActivity;


public abstract class NavigationActivity extends AppCompatActivity {
	private static final String EXTRA_SHOW_HOME_SCREEN_ON_BACK = "ExtraShowHomeScreenOnBack";

	private static boolean showLogoGlow = true;
    private Toolbar navigationToolbar;
    private AnimationPopupWindow popup;
	private Map<View, Class<? extends Activity>> navigationMapping;
	private boolean showHomeScreenOnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        navigationToolbar = (Toolbar) findViewById(R.id.navigation_toolbar);
        setupActionBar(navigationToolbar);
        navigationToolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showLogoGlow = false;
				if (popup == null || !popup.isShowing()) {
					popup = createNavigationPopup();
					popup.showAsDropDown(navigationToolbar);
				}
			}
		});

	    showHomeScreenOnBack = getIntent().getBooleanExtra(EXTRA_SHOW_HOME_SCREEN_ON_BACK, false);
    }

	private AnimationPopupWindow createNavigationPopup() {
		final View view = LayoutInflater.from(NavigationActivity.this).inflate(R.layout.navigation_menu, null);

		setupImageColors(view);
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
		popup.setBackgroundDrawable(new BitmapDrawable());
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

		navigationMapping = new LinkedHashMap<>(4);
		navigationMapping.put(view.findViewById(R.id.menu_navigate_to_programme), ProgrammeActivity.class);
		navigationMapping.put(view.findViewById(R.id.menu_navigate_to_map), MapActivity.class);
		navigationMapping.put(view.findViewById(R.id.menu_navigate_to_updates), UpdatesActivity.class);
		navigationMapping.put(view.findViewById(R.id.menu_navigate_to_arrival_methods), ArrivalMethodsActivity.class);

		// Check if we're in one of the navigation views and set it to unclickable
		if (navigationMapping.containsValue(this.getClass())) {
			for (Map.Entry<View, Class<? extends Activity>> entry : navigationMapping.entrySet()) {
				if (entry.getValue() == this.getClass()) {
					int selectedColor = ThemeAttributes.getColor(this, R.attr.navigationPopupSelectedColor);
					ViewGroup navigationView = (ViewGroup) entry.getKey();
					navigationView.setOnClickListener(null);
					for (int i = 0; i < navigationView.getChildCount(); ++i) {
						View child = navigationView.getChildAt(i);
						if (child instanceof TextView) {
							((TextView) child).setTextColor(selectedColor);
						} else if (child instanceof ImageView) {
							((ImageView) child).getDrawable().setColorFilter(selectedColor, PorterDuff.Mode.MULTIPLY);
						}
					}
					break;
				}
			}
		}

		return popup;
	}

	private void setupImageColors(View view) {
		int color = ThemeAttributes.getColor(this, R.attr.navigationPopupNotSelectedColor);
		changeImageColor(view, R.id.events_menu_image, color);
		changeImageColor(view, R.id.map_menu_image, color);
		changeImageColor(view, R.id.updates_menu_image, color);
		changeImageColor(view, R.id.arrival_methods_menu_image, color);
	}

	private void changeImageColor(View view, int resource, int color) {
		ImageView image = (ImageView) view.findViewById(resource);
		image.setColorFilter(color);
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
	        int logoType = ThemeAttributes.getColor(this, R.attr.toolbarLogoType);
	        Drawable drawable = null;
	        switch (logoType) {
		        // bitmap
		        case 0:
			        drawable = ThemeAttributes.getDrawable(this, R.attr.toolbarLogo);
			        // The scaling doesn't work properly for this icon (the width remains the original size)
			        // so we have to resize it manually
			        drawable = resizeBitmap(drawable, ThemeAttributes.getDimentionSize(this, R.attr.actionBarSize));
			        break;
		        // svg
		        case 1:
			        toolbar.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
	                SVG logoSVG = SVGFileLoader.loadSVG(this, ThemeAttributes.getResourceId(this, R.attr.toolbarLogo));
			        drawable = new PictureDrawable(logoSVG.renderToPicture());
			        break;
	        }
            toolbar.setNavigationIcon(drawable);

	        if (showLogoGlow) {
		        for (int i = 0; i < toolbar.getChildCount(); ++i) {
			        View view = toolbar.getChildAt(i);
			        if (view instanceof ImageView && ((ImageView) view).getDrawable() == drawable) {
				        final ImageView image = (ImageView) view;
				        ValueAnimator animator = ValueAnimator.ofInt(255, 100, 255);
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
					        @Override
					        public void onAnimationStart(Animator animation) {
					        }

					        @Override
					        public void onAnimationEnd(Animator animation) {
						        image.setColorFilter(null);
					        }

					        @Override
					        public void onAnimationCancel(Animator animation) {
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

	/**
	 * Resize bitmap height to specified pixels while keeping the aspect ratio
	 */
	private Drawable resizeBitmap(Drawable image, int height) {
		Bitmap originalBitmap = ((BitmapDrawable) image).getBitmap();
		int width = (int) (originalBitmap.getWidth() * height / (float) originalBitmap.getHeight());
		Bitmap bitmapResized = Bitmap.createScaledBitmap(originalBitmap, width, height, false);
		return new BitmapDrawable(getResources(), bitmapResized);
	}

    protected View setContentInContentContainer(int layoutResID) {
        FrameLayout contentContainer = (FrameLayout) findViewById(R.id.navigation_content_view_container);
        getLayoutInflater().inflate(layoutResID, contentContainer, true);
	    return contentContainer;
    }

    public void onConventionEventClicked(View view) {
        Intent intent = new Intent(this, EventActivity.class);
        intent.putExtra(EventActivity.EXTRA_EVENT_ID, (String) view.getTag());
        startActivity(intent);
        overridePendingTransition(0, 0);
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
			overridePendingTransition(R.anim.abc_grow_fade_in_from_bottom, R.anim.abc_shrink_fade_out_from_bottom);
		}
	}

	public void onNavigate(View view) {
		navigateToActivity(navigationMapping.get(view));
	}

    private void dismissPopupIfNeeded() {
        if (popup != null && popup.isShowing()) {
            popup.dismissNow();
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
