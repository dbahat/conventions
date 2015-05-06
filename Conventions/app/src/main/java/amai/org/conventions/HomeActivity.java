package amai.org.conventions;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.navigation.NavigationPages;
import amai.org.conventions.navigation.PrimaryNavigationPages;


public class HomeActivity extends AppCompatActivity {

    private NavigationPages navigationPages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        navigationPages = new PrimaryNavigationPages(this);
    }

    public void onNavigationButtonClicked(View view) {
        // Note - We assume here the UI layout is a FrameLayout with 2 children, where the second one is a text view with the navigation page string
        // resource. If this assumption changes the code here needs to be adjusted accordingly.
        FrameLayout frameLayout = (FrameLayout) view;
        TextView textView = (TextView) frameLayout.getChildAt(1);

        Intent intent = new Intent(this, NavigationActivity.class);
        intent.putExtra(NavigationActivity.EXTRA_INITIAL_NAVIGATION_POSITION, navigationPages.getPosition(textView.getText().toString()));
        startActivity(intent);
    }
}
