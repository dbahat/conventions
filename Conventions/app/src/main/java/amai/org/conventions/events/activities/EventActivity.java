package amai.org.conventions.events.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import amai.org.conventions.R;
import amai.org.conventions.events.adapters.ImageAdapter;
import amai.org.conventions.map.MapActivity;
import amai.org.conventions.model.Convention;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.navigation.NavigationActivity;

public class EventActivity extends NavigationActivity {

    public static final String EXTRA_EVENT_ID = "EventIdExtra";

    private ConventionEvent conventionEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentInContentContainer(R.layout.activity_event);

        getNavigationToolbar().setAsActionBar(this);

        int eventId = getIntent().getIntExtra(EXTRA_EVENT_ID, 0);
        conventionEvent = Convention.getInstance().findEventById(eventId);

        setEvent(conventionEvent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event, menu);

        if (conventionEvent.getUserInput().isAttending()) {
            MenuItem favoritesButton = menu.findItem(R.id.event_change_favorite_state);
            favoritesButton.setIcon(getResources().getDrawable(R.drawable.favorite_icon_true));
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.event_change_favorite_state:
                if (conventionEvent.getUserInput().isAttending()) {
                    conventionEvent.getUserInput().setAttending(false);
                    item.setIcon(getResources().getDrawable(R.drawable.favorite_icon_false));
                    Toast.makeText(this, getString(R.string.event_removed_from_favorites), Toast.LENGTH_SHORT).show();
                } else {
                    conventionEvent.getUserInput().setAttending(true);
                    item.setIcon(getResources().getDrawable(R.drawable.favorite_icon_true));
                    Toast.makeText(this, getString(R.string.event_added_to_favorites), Toast.LENGTH_SHORT).show();
                }
                Convention.getInstance().save();
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

	    ViewPager viewPager = (ViewPager) findViewById(R.id.imagesPager);
	    if (event.getImages().size() > 0) {
		    ImageAdapter adapter = new ImageAdapter(this, event.getImages());
		    viewPager.setAdapter(adapter);
	    } else {
		    viewPager.setVisibility(View.GONE);
	    }
    }
}
