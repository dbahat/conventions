package amai.org.conventions;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.navigation.NavigationToolbar;


public class ProgrammeActivity extends NavigationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programme);
        NavigationToolbar navigationToolbar = (NavigationToolbar) findViewById(R.id.programme_toolbar);
        navigationToolbar.initialize();
        navigationToolbar.setNavigationPageSelectedListener(this);
    }
}
