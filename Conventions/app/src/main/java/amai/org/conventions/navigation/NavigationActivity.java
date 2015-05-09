package amai.org.conventions.navigation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import amai.org.conventions.R;


public abstract class NavigationActivity extends AppCompatActivity implements NavigationToolbar.OnNavigationPageSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_navigation);

        NavigationToolbar navigationToolbar = (NavigationToolbar) findViewById(R.id.navigation_toolbar);
        navigationToolbar.initialize();
        navigationToolbar.setNavigationPageSelectedListener(this);
    }

    protected void setContentInContentContainer(int layoutResID) {
        FrameLayout contentContainer = (FrameLayout) findViewById(R.id.navigation_content_view_container);
        getLayoutInflater().inflate(layoutResID, contentContainer, true);
    }

    @Override
    public void onNavigationPageSelected(Class<? extends Activity> activityToNavigateTo) {
        Intent intent = new Intent(this, activityToNavigateTo);
        startActivity(intent);

        // Disable the animation shown when switching activities
        overridePendingTransition(0, 0);

        // Remove the current activity from memory
        finish();
    }
}
