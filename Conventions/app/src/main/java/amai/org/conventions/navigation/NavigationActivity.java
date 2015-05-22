package amai.org.conventions.navigation;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import amai.org.conventions.AnimationPopupWindow;
import amai.org.conventions.ArrivalMethodsActivity;
import amai.org.conventions.R;
import amai.org.conventions.events.activities.EventActivity;
import amai.org.conventions.events.activities.MyEventsActivity;
import amai.org.conventions.events.activities.ProgrammeActivity;
import amai.org.conventions.map.MapActivity;


public abstract class NavigationActivity extends AppCompatActivity {

    private Toolbar navigationToolbar;
    private AnimationPopupWindow popup;

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
                    final View view = LayoutInflater.from(NavigationActivity.this).inflate(R.layout.navigation_menu, null);
				    setupImageColors(view);
                    popup = new AnimationPopupWindow(
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

                    popup.showAsDropDown(navigationToolbar, 0, 0);
                }
            }
        });
    }

	private void setupImageColors(View view) {
		int color = R.color.toolbar_color;
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
            toolbar.setNavigationIcon(R.drawable.logo);
        }
    }

    protected void setContentInContentContainer(int layoutResID) {
        FrameLayout contentContainer = (FrameLayout) findViewById(R.id.navigation_content_view_container);
        getLayoutInflater().inflate(layoutResID, contentContainer, true);
    }

    public void onConventionEventClicked(View view) {
        Intent intent = new Intent(this, EventActivity.class);
        intent.putExtra(EventActivity.EXTRA_EVENT_ID, (int) view.getTag());
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

    protected void navigateToActivity(Class<? extends Activity> activityToNavigateTo, boolean shouldClearActivityStack, Bundle extras) {

        dismissPopupIfNeeded();

        // In case we were asked to navigate to the activity we're already in, ignore the request
        if (activityToNavigateTo == this.getClass()) {
            return;
        }

        Intent intent = new Intent(this, activityToNavigateTo);
        if (shouldClearActivityStack) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finish();
        }

        if (extras != null) {
            intent.putExtras(extras);
        }

        startActivity(intent);

        overridePendingTransition(0, 0);
    }

    public void onNavigateToProgramme(View view) {
        navigateToActivity(ProgrammeActivity.class);
    }

    public void onNavigateToMap(View view) {
        navigateToActivity(MapActivity.class);
    }

    public void onNavigateToUpdates(View view) {
        navigateToActivity(MyEventsActivity.class);
    }

    public void onNavigateToArrivalMethods(View view) {
        navigateToActivity(ArrivalMethodsActivity.class);
    }

    private void dismissPopupIfNeeded() {
        if (popup != null && popup.isShowing()) {
            popup.dismissNow();
        }
    }
}
