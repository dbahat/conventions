package amai.org.conventions.navigation;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import amai.org.conventions.R;


public class NavigationActivity extends AppCompatActivity {

    public static final String EXTRA_INITIAL_NAVIGATION_POSITION = "InitialNavigationPosition";

    private ViewPager pager;
    private TextView title;
    private Spinner pageNavigationSpinner;
    private Button actionButton1;
    private Button actionButton2;
    private NavigationPages navigationPages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        navigationPages = new PrimaryNavigationPages(this);

        setNavigationPager();
        initializeToolbar();
        configureInitialPageIfNeeded();

        hideTitle();
    }

    public void setTitle(String titleText) {
        title.setText(titleText);
        title.setVisibility(View.VISIBLE);
    }

    public void hideTitle() {
        title.setVisibility(View.GONE);
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
        final FragmentStatePagerAdapter adapter = new NavigationAdapter(getSupportFragmentManager(), navigationPages);
        pager.setAdapter(adapter);
    }

    private void initializeToolbar() {
        title = (TextView) findViewById(R.id.toolbar_title);
        pageNavigationSpinner = (Spinner) findViewById(R.id.toolbar_page_navigation_spinner);
        actionButton1 = (Button) findViewById(R.id.toolbar_action_button_1);
        actionButton2 = (Button) findViewById(R.id.toolbar_action_button_2);

        // Sets the menu to show on the spinner. Use a TitleLess adapter since the navigation spinner
        // should only show the navigation page name inside it's drop-down menu.
        TitleLessArrayAdapter adapter = new TitleLessArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, navigationPages.getPagesTitle());
        pageNavigationSpinner.setAdapter(adapter);

        // Setting the spinner with a delay to ensure onItemSelected won't get invoked when first opening the activity.
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

    /**
     * An array adapter that doesn't set any text inside it's returned view.
     * Should be used by spinner controls that shows text only in the drop-down text box.
     */
    private static class TitleLessArrayAdapter extends ArrayAdapter<CharSequence> {

        public TitleLessArrayAdapter(Context context, int resource, CharSequence[] objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            ((TextView) view).setText("");
            return view;
        }
    }
}
