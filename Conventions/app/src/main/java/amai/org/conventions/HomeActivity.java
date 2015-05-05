package amai.org.conventions;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void onMapSelected(View view) {
        Intent intent = new Intent(this, NavigationActivity.class);
        intent.putExtra(NavigationActivity.EXTRA_INITIAL_NAVIGATION_POSITION, 0);
        startActivity(intent);
    }

    public void onProgramSelected(View view) {
        Intent intent = new Intent(this, NavigationActivity.class);
        intent.putExtra(NavigationActivity.EXTRA_INITIAL_NAVIGATION_POSITION, 1);
        startActivity(intent);
    }

    public void onUpdatesSelected(View view) {
        Intent intent = new Intent(this, NavigationActivity.class);
        intent.putExtra(NavigationActivity.EXTRA_INITIAL_NAVIGATION_POSITION, 2);
        startActivity(intent);
    }

    public void onArrivalMethodsSelected(View view) {
        Intent intent = new Intent(this, NavigationActivity.class);
        intent.putExtra(NavigationActivity.EXTRA_INITIAL_NAVIGATION_POSITION, 3);
        startActivity(intent);
    }
}
