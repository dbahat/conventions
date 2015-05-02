package amai.org.conventions;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import amai.org.conventions.model.Convention;
import amai.org.conventions.model.ConventionEvent;


public class HallActivity extends AppCompatActivity {
    private String hallName = "Oranim 1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hall);
        RecyclerView hallEventsList = (RecyclerView) findViewById(R.id.hallEventsList);
        List<ConventionEvent> fullEventsList = Convention.getInstance().getEvents();
        hallEventsList.setAdapter(new EventsViewAdapter(filter(fullEventsList, this.hallName)));
        hallEventsList.setLayoutManager(new LinearLayoutManager(this));
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

    private static ArrayList<ConventionEvent> filter(List<ConventionEvent> fullEventsList, String hallName) {
        ArrayList<ConventionEvent> result = new ArrayList<>();
        for (ConventionEvent event: fullEventsList) {
            if (hallName.equals(event.getHall().getName())) {
                result.add(event);
            }
        }
        return result;
    }
}
