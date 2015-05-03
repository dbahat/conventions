package amai.org.conventions;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;


public class NavigationActivity extends AppCompatActivity {

    public static final String EXTRA_INITIAL_NAVIGATION_POSITION = "InitialNavigationPosition";

    private ViewPager pager;
    private Spinner middleSpinner;
    private Spinner pageNavigationSpinner;
    private Button actionButton1;
    private Button actionButton2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        setNavigationPager();
        initializeToolbar();
        configureInitialPageIfNeeded();
    }

    private void configureInitialPageIfNeeded() {
        int initialPosition = getIntent().getIntExtra(EXTRA_INITIAL_NAVIGATION_POSITION, -1);
        if (initialPosition != -1) {
            pager.setCurrentItem(initialPosition);
        }
    }

    private void setNavigationPager() {
        // Instantiate a ViewPager and a PagerAdapter.
        pager = (ViewPager) findViewById(R.id.pager);
        final FragmentStatePagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
    }

    private void initializeToolbar() {
        middleSpinner = (Spinner) findViewById(R.id.toolbar_middle_spinner);
        pageNavigationSpinner = (Spinner) findViewById(R.id.toolbar_page_navigation_spinner);
        actionButton1 = (Button) findViewById(R.id.toolbar_action_button_1);
        actionButton2 = (Button) findViewById(R.id.toolbar_action_button_2);

        // Sets the menu to show on the spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.navigation_pages, android.R.layout.simple_spinner_dropdown_item);
        pageNavigationSpinner.setAdapter(adapter);

        pageNavigationSpinner.post(new Runnable() {
            @Override
            public void run() {
                pageNavigationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        pager.setCurrentItem(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        });
    }

    private class MainPagerAdapter extends FragmentStatePagerAdapter {

        public MainPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new MapFragment();
                case 1:
                    return new ProgrammeFragment();
                case 2:
                    return new HallFragment();
            }

            throw new AssertionError("No navigation page for position " + position);
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
