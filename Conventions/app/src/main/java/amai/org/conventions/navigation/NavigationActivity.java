package amai.org.conventions.navigation;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import amai.org.conventions.AnimationPopupWindow;
import amai.org.conventions.ArrivalMethodsActivity;
import amai.org.conventions.R;
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.events.activities.EventActivity;
import amai.org.conventions.events.activities.ProgrammeActivity;
import amai.org.conventions.map.MapActivity;
import amai.org.conventions.updates.UpdatesActivity;


public abstract class NavigationActivity extends AppCompatActivity {
	private static Map<Integer, SVG> loadedSVGFiles = new HashMap<>();

    private Toolbar navigationToolbar;
    private AnimationPopupWindow popup;
	private Map<View, Class<? extends Activity>> navigationMapping;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        navigationToolbar = (Toolbar) findViewById(R.id.navigation_toolbar);
        setupActionBar(navigationToolbar);
        navigationToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popup == null || !popup.isShowing()) {
	                popup = createNavigationPopup();
	                // Sending toolbar width to support Jellybean version (it does not align to
	                // the right automatically)
                    popup.showAsDropDown(navigationToolbar, navigationToolbar.getWidth(), 0);
                }
            }
        });
    }

	private AnimationPopupWindow createNavigationPopup() {
		final View view = LayoutInflater.from(NavigationActivity.this).inflate(R.layout.navigation_menu, null);
		setupImageColors(view);
		AnimationPopupWindow popup = new AnimationPopupWindow(
		        view,
		        ViewGroup.LayoutParams.WRAP_CONTENT,
		        ViewGroup.LayoutParams.WRAP_CONTENT,
		        R.anim.drop_down,
		        R.anim.drop_down_reverse);

		// This must be done to enable listening to clicks on the popup window
		popup.setBackgroundDrawable(new BitmapDrawable());
		popup.setAnimationStyle(0);
		popup.setFocusable(true);
		popup.setOutsideTouchable(true);

		// Move the inner view to the top start corner of the toolbar.
		// This is required because it's a card view with elevation (which uses padding for the
		// elevation and shadow) so we have to move it to the start point of the popup window.
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
					entry.getKey().setBackgroundColor(ThemeAttributes.getColor(this, R.attr.navigationPopupDisabledBackground));
					entry.getKey().setOnClickListener(null);
					break;
				}
			}
		}

		return popup;
	}

	private void setupImageColors(View view) {
		int color = ThemeAttributes.getColor(this, R.attr.toolbarBackground);
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
	    toolbar.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        this.setSupportActionBar(toolbar);
        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowTitleEnabled(true);
	        try {
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
		                SVG logoSVG = loadSVG(ThemeAttributes.getResourceId(this, R.attr.toolbarLogo));
				        drawable = new PictureDrawable(logoSVG.renderToPicture());
				        break;
		        }
	            toolbar.setNavigationIcon(drawable);
	        } catch (SVGParseException e) {
		        throw new RuntimeException(e);
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

	private SVG loadSVG(int resource) throws SVGParseException {
		if (loadedSVGFiles.containsKey(resource)) {
			return loadedSVGFiles.get(resource);
		}

		SVG svg = SVG.getFromResource(getResources(), resource);
		svg.setDocumentHeight("100%");
		svg.setDocumentWidth("100%");
		loadedSVGFiles.put(resource, svg);
		return svg;
	}

    protected void setContentInContentContainer(int layoutResID) {
        FrameLayout contentContainer = (FrameLayout) findViewById(R.id.navigation_content_view_container);
        getLayoutInflater().inflate(layoutResID, contentContainer, true);
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
        // When navigating using the main popup window, clear the activity stack so the back button would return button would exit the app
        navigateToActivity(activityToNavigateTo, true, null);
    }

    protected void navigateToActivity(Class<? extends Activity> activityToNavigateTo, boolean shouldEndActivityAfterExecution, Bundle extras) {

        dismissPopupIfNeeded();

        // In case we were asked to navigate to the activity we're already in, ignore the request
        if (activityToNavigateTo == this.getClass()) {
            return;
        }

        Intent intent = new Intent(this, activityToNavigateTo);
        if (shouldEndActivityAfterExecution) {
            finish();
        }

        if (extras != null) {
            intent.putExtras(extras);
        }

        startActivity(intent);

        overridePendingTransition(0, 0);
    }

	public void onNavigate(View view) {
		navigateToActivity(navigationMapping.get(view));
	}

    private void dismissPopupIfNeeded() {
        if (popup != null && popup.isShowing()) {
            popup.dismissNow();
        }
    }
}
