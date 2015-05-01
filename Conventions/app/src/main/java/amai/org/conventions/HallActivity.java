package amai.org.conventions;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;

import amai.org.conventions.model.ConventionEvent;


public class HallActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hall);
        RecyclerView hallEventsList = (RecyclerView) findViewById(R.id.hallEventsList);
        hallEventsList.setAdapter(new EventsViewAdapter(getEventsList()));
        hallEventsList.setLayoutManager(new LinearLayoutManager(this));
    }

    private ArrayList<ConventionEvent> getEventsList() {
        ConventionEvent event1 = new ConventionEvent();
        event1.setTitle("Between roses and robots: The anime of Ikuhara, Anno and Miyazaki");
        ConventionEvent event2 = new ConventionEvent();
        event2.setTitle("Tokusatsu character dsign");
        ConventionEvent event3 = new ConventionEvent();
        event3.setTitle("From hard power to soft - from military imperialism to cultural attraction");
        return asList(event1, event2, event3);
    }

    public <T> ArrayList<T> asList(T... instances) {
        return new ArrayList<>(Arrays.asList(instances));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hall, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
