package amai.org.conventions;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.EventType;
import amai.org.conventions.model.Hall;


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
        try {
            Hall oranim1 = new Hall("Oranim 1");

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:ss");

            ConventionEvent event1 = new ConventionEvent();
            event1.setTitle("Between roses and robots: The anime of Ikuhara, Anno and Miyazaki");
            event1.setAttending(true);
            event1.setLecturer("Liron Afriat");
            event1.setStartTime(dateFormat.parse("05.03.2015 13:00"));
            event1.setEndTime(dateFormat.parse("05.03.2015 14:00"));
            event1.setHall(oranim1);
            event1.setType(EventType.Lecture);

            ConventionEvent event2 = new ConventionEvent();
            event2.setTitle("Tokusatsu character design");
            event2.setAttending(false);
            event2.setStartTime(dateFormat.parse("05.03.2015 14:00"));
            event2.setLecturer("Liad Bar-Shilton");
            event2.setEndTime(dateFormat.parse("05.03.2015 15:00"));
            event2.setHall(oranim1);
            event2.setType(EventType.Workshop);

            ConventionEvent event3 = new ConventionEvent();
            event3.setTitle("From hard power to soft - from military imperialism to cultural attraction");
            event3.setAttending(false);
            event3.setLecturer("Shiran Ivnizki");
            event3.setStartTime(dateFormat.parse("05.03.2015 15:00"));
            event3.setEndTime(dateFormat.parse("05.03.2015 16:30"));
            event3.setHall(oranim1);
            event3.setType(EventType.Special);

            return asList(event1, event2, event3);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
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
