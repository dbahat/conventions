package amai.org.conventions;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import amai.org.conventions.navigation.NavigationPages;
import amai.org.conventions.networking.ModelRefresher;

public class HomeActivity extends AppCompatActivity {

    private NavigationPages navigationPages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        navigationPages = new NavigationPages(this);

        // Initiate async downloading of the updated convention info in the background
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                ModelRefresher modelRefresher = new ModelRefresher();
                modelRefresher.refreshFromServer();

                return null;
            }
        }.execute();
    }

    public void onNavigationButtonClicked(View view) {
        int position = Integer.parseInt(view.getTag().toString());
        Intent intent = new Intent(this, navigationPages.getActivityType(position));
        startActivity(intent);
    }
}
