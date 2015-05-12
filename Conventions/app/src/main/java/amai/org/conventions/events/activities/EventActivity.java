package amai.org.conventions.events.activities;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import amai.org.conventions.R;
import amai.org.conventions.map.MapActivity;
import amai.org.conventions.model.Convention;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.navigation.NavigationActivity;

public class EventActivity extends NavigationActivity {

    public static final String EXTRA_EVENT_ID = "EventIdExtra";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentInContentContainer(R.layout.activity_event);

        getNavigationToolbar().setAsActionBar(this);

        int eventId = getIntent().getIntExtra(EXTRA_EVENT_ID, 0);
        setEvent(Convention.getInstance().findById(eventId));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.event_change_favorite_state:
                return true;
            case R.id.event_navigate_to_map:
                navigateToActivity(MapActivity.class);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setEvent(ConventionEvent event) {

        setToolbarTitle(event.getType().toString());

        TextView title = (TextView)findViewById(R.id.event_title);
        title.setText(event.getTitle());
        TextView hallName = (TextView)findViewById(R.id.event_hall_name);
        hallName.setText(event.getHall().getName());
        TextView lecturerName = (TextView)findViewById(R.id.event_lecturer);
        lecturerName.setText(event.getLecturer());
        TextView time = (TextView)findViewById(R.id.event_time);
        time.setText(event.getStartTime().toString() + " " + event.getEndTime().toString());

//	    ImageView imageView = (ImageView) findViewById(R.id.event_image);
//	    imageView.setImageDrawable(getResources().getDrawable(R.drawable.event_ntt));
    }
}
