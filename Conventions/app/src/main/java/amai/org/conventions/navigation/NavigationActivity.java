package amai.org.conventions.navigation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public abstract class NavigationActivity extends AppCompatActivity implements NavigationToolbar.OnNavigationPageSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
