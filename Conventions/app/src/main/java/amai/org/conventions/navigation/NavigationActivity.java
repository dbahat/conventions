package amai.org.conventions.navigation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;

import amai.org.conventions.R;
import amai.org.conventions.events.EventView;
import amai.org.conventions.events.activities.EventActivity;


public abstract class NavigationActivity extends AppCompatActivity implements NavigationToolbar.OnNavigationPageSelectedListener {

    private NavigationToolbar navigationToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_navigation);

        navigationToolbar = (NavigationToolbar) findViewById(R.id.navigation_toolbar);
        navigationToolbar.initialize(this);
        navigationToolbar.setNavigationPageSelectedListener(this);
    }

    protected void setContentInContentContainer(int layoutResID) {
        FrameLayout contentContainer = (FrameLayout) findViewById(R.id.navigation_content_view_container);
        getLayoutInflater().inflate(layoutResID, contentContainer, true);
    }

    @Override
    public void onNavigationPageSelected(Class<? extends Activity> activityToNavigateTo) {
        navigateToActivity(activityToNavigateTo);
    }

    public void onConventionEventClicked(View view) {
        Intent intent = new Intent(this, EventActivity.class);
        intent.putExtra(EventActivity.EXTRA_EVENT_ID, (int) view.getTag());
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    protected void setToolbarTitle(String titleText) {
        navigationToolbar.setTitle(titleText);
    }

    protected NavigationToolbar getNavigationToolbar() {
        return navigationToolbar;
    }

    protected void navigateToActivity(Class<? extends Activity> activityToNavigateTo) {
        // When navigating using the main navigation spinner, clear the activity stack
        navigateToActivity(activityToNavigateTo, true);
    }

    protected void navigateToActivity(Class<? extends Activity> activityToNavigateTo, boolean shouldClearActivityStack) {
        Intent intent = new Intent(this, activityToNavigateTo);
        if (shouldClearActivityStack) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finish();
        }
        startActivity(intent);

        // Disable the animation shown when switching activities
        overridePendingTransition(0, 0);
    }
}
