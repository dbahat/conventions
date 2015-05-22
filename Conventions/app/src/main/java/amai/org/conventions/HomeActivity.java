package amai.org.conventions;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import amai.org.conventions.navigation.NavigationPages;

public class HomeActivity extends AppCompatActivity {

    private NavigationPages navigationPages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        navigationPages = new NavigationPages(this);
    }

    public void onNavigationButtonClicked(View view) {
        // Note - We assume here the UI layout is a ViewGroup with 2 children, where the second one is a text view with the navigation page string
        // resource. If this assumption changes the code here needs to be adjusted accordingly.
	    ViewGroup mainGroup = (ViewGroup) view;
        TextView textView = (TextView) mainGroup.getChildAt(1);

        int position = navigationPages.getPosition(textView.getText().toString());
        Intent intent = new Intent(this, navigationPages.getActivityType(position));
        startActivity(intent);
    }
}
