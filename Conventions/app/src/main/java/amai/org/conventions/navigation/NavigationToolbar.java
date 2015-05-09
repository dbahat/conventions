package amai.org.conventions.navigation;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import amai.org.conventions.R;

/**
 * The top toolbar shown in each activity, allowing to navigate between the applications screens.
 */
public class NavigationToolbar extends Toolbar {
    private Spinner navigationSpinner;
    private TextView title;
    private LinearLayout actionButtonsContainer;

    private NavigationPages navigationPages;

    private OnNavigationPageSelectedListener navigationPageSelectedListener;

    public NavigationToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void initialize(NavigationActivity navigationActivity) {
        resolveUIElements();
        initializeToolbar(navigationActivity);
    }

    public void setNavigationPageSelectedListener(OnNavigationPageSelectedListener navigationPageSelectedListener) {
        this.navigationPageSelectedListener = navigationPageSelectedListener;
    }

    public void clearActionButtons() {
        actionButtonsContainer.removeAllViews();
    }

    public Button addActionButton() {
        Button button = new Button(getContext());
        button.setLayoutParams(new LinearLayout.LayoutParams(
                getResources().getDimensionPixelSize(R.dimen.toolbar_button_width),
                getResources().getDimensionPixelSize(R.dimen.toolbar_button_height)));
        button.setPaddingRelative(
                getResources().getDimensionPixelSize(R.dimen.toolbar_button_padding_start), 0,
                getResources().getDimensionPixelSize(R.dimen.toolbar_button_padding_start), 0);
        actionButtonsContainer.addView(button);
        return button;
    }

    public void setTitle(String titleText) {
        title.setText(titleText);
    }

    private void resolveUIElements() {
        navigationSpinner = (Spinner) findViewById(R.id.toolbar_page_navigation_spinner);
        title = (TextView) findViewById(R.id.toolbar_title);
        actionButtonsContainer = (LinearLayout) findViewById(R.id.toolbar_action_buttons_container);
    }

    private void initializeToolbar(NavigationActivity activity) {

        navigationPages = new NavigationPages(getContext());

        // Sets the menu to show on the spinner. Use a TitleLess adapter since the navigation spinner
        // should only show the navigation page name inside it's drop-down menu.
        TitleLessArrayAdapter adapter = new TitleLessArrayAdapter(getContext(),
                android.R.layout.simple_spinner_dropdown_item, navigationPages.getPagesTitle());
        navigationSpinner.setAdapter(adapter);

        // Set the current screen as the selected one, since spinner by default doesn't fire onItemSelected event when
        // clicking the currently selected activity.
        navigationSpinner.setSelection(navigationPages.getPositionForType(activity.getClass()));

        // Setting the spinner with a delay to ensure onItemSelected won't get invoked when first opening the activity.
        navigationSpinner.post(new Runnable() {
            @Override
            public void run() {
                navigationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (navigationPageSelectedListener != null) {
                            Class activityToNavigateTo = navigationPages.getActivityType(position);
                            navigationPageSelectedListener.onNavigationPageSelected(activityToNavigateTo);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        });
    }

    public interface OnNavigationPageSelectedListener {
        void onNavigationPageSelected(Class<? extends Activity> activityToNavigateTo);
    }

    /**
     * An array adapter that doesn't set any text inside its returned view.
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
