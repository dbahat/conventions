package amai.org.conventions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class Toolbar extends android.support.v7.widget.Toolbar {

    private Spinner middleSpinner;
    private Spinner pageNavigationSpinner;
    private Button actionButton1;
    private Button actionButton2;
    private OnMiddleSpinnerChangedListener middleSpinnerChangedListener;

    public Toolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void initialize() {
        middleSpinner = (Spinner) findViewById(R.id.toolbar_middle_spinner);
        pageNavigationSpinner = (Spinner) findViewById(R.id.toolbar_page_navigation_spinner);
        actionButton1 = (Button) findViewById(R.id.toolbar_action_button_1);
        actionButton2 = (Button) findViewById(R.id.toolbar_action_button_2);

        configurePageNavigationSpinner();
    }

    public void configureMiddleSpinner(int spinnerValuesArrayResourceId) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                spinnerValuesArrayResourceId, android.R.layout.simple_spinner_dropdown_item);
        middleSpinner.setAdapter(adapter);

        // Have the spinner display the first string in the values array
        middleSpinner.setPrompt(middleSpinner.getAdapter().getItem(0).toString());

        // Configure the spinner interaction
        middleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String newSpinnerTitle = (String) parent.getItemAtPosition(position);
                middleSpinner.setPrompt(newSpinnerTitle);

                if (middleSpinnerChangedListener != null) {
                    middleSpinnerChangedListener.onItemSelected(newSpinnerTitle);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void setOnMiddleSpinnerChangedListener(OnMiddleSpinnerChangedListener listener) {
        middleSpinnerChangedListener = listener;
    }

    private void configurePageNavigationSpinner() {
        // Sets the menu to show on the spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.navigation_pages, android.R.layout.simple_spinner_dropdown_item);
        pageNavigationSpinner.setAdapter(adapter);

        // Configure the spinner interaction
        pageNavigationSpinner.post(new Runnable() {
            @Override
            public void run() {
                pageNavigationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String navigationPageName = (String) parent.getItemAtPosition(position);

                        Intent intent = null;
                        Activity containingActivity = null;
                        switch (navigationPageName) {
                            case "Program":
                                containingActivity = (Activity) getContext();
                                if (!(containingActivity instanceof ProgramActivity)) {
                                    intent = new Intent(containingActivity, ProgramActivity.class);
                                    containingActivity.startActivity(intent);
                                }
                                break;
                            case "Map":
                                containingActivity = (Activity) getContext();
                                if (!(containingActivity instanceof MapActivity)) {
                                    intent = new Intent(containingActivity, MapActivity.class);
                                    containingActivity.startActivity(intent);
                                }
                                break;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        });

    }

    public interface OnMiddleSpinnerChangedListener {
        public void onItemSelected(String itemValue);
    }
}
