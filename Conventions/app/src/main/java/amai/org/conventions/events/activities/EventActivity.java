package amai.org.conventions.events.activities;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import amai.org.conventions.AspectRatioImageView;
import amai.org.conventions.R;
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.map.MapActivity;
import amai.org.conventions.model.Convention;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.ConventionMap;
import amai.org.conventions.model.Dates;
import amai.org.conventions.model.MapLocation;
import amai.org.conventions.navigation.NavigationActivity;
import uk.co.chrisjenx.paralloid.views.ParallaxScrollView;


public class EventActivity extends NavigationActivity {

    public static final String EXTRA_EVENT_ID = "EventIdExtra";

    private ConventionEvent conventionEvent;
	private LinearLayout imagesLayout;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentInContentContainer(R.layout.activity_event);

		imagesLayout = (LinearLayout) findViewById(R.id.images_layout);

        int eventId = getIntent().getIntExtra(EXTRA_EVENT_ID, 0);
        conventionEvent = Convention.getInstance().findEventById(eventId);
        setEvent(conventionEvent);

	    final View mainLayout = findViewById(R.id.event_main_layout);
	    final ParallaxScrollView scrollView = (ParallaxScrollView) findViewById(R.id.parallax_scroll);
	    final View backgroundView = imagesLayout;
	    final View detailBoxes = findViewById(R.id.event_detail_boxes);

	    mainLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
		    @Override
		    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
			    // Set parallax
			    int foregroundHeight = detailBoxes.getMeasuredHeight();
			    int backgroundHeight = backgroundView.getMeasuredHeight();
			    int screenHeight = mainLayout.getMeasuredHeight();
			    float maxParallax = 1;

			    // If background height is bigger than screen size, scrolling should be until background full height is reached.
			    // If it's smaller, scrolling should be until background is scrolled out of the screen.
			    int backgroundToScroll;
			    if (backgroundHeight < screenHeight) {
				    backgroundToScroll = backgroundHeight;
				    maxParallax = 0.7f;
			    } else {
				    backgroundToScroll = backgroundHeight - screenHeight;

				    // If foreground height is smaller than background height (and background should be scrolled),
				    // increase foreground height to allow scrolling until the end of the background and see all the images.
				    if (backgroundToScroll > 0 && foregroundHeight < backgroundHeight) {
					    detailBoxes.setMinimumHeight(backgroundHeight);
					    // Update height to calculate the parallax factor
				        foregroundHeight = backgroundHeight;
				    }
			    }
			    int foregroundToScroll = foregroundHeight - screenHeight;

			    // Set parallax scrolling if both foreground and background should be scrolled
			    if (backgroundToScroll > 0 && foregroundToScroll > 0) {
				    float scrollFactor = backgroundToScroll / (float) foregroundToScroll;
				    // If scroll factor is bigger than 1, set it to 1 so the background doesn't move too fast.
				    // This could happen only in case the background is smaller than screen size so we can
				    // still see all the images.
				    scrollView.parallaxViewBy(backgroundView, Math.min(scrollFactor, maxParallax));
			    }
		    }
	    });

		// Set images background color according to last image's color palette
		if (imagesLayout.getChildCount() > 0) {
			final View imagesBackground = findViewById(R.id.images_background);
			ImageView lastImage = (ImageView) imagesLayout.getChildAt(imagesLayout.getChildCount() - 1);
			if (lastImage.getDrawable() instanceof BitmapDrawable) {
				Bitmap bitmap = ((BitmapDrawable) lastImage.getDrawable()).getBitmap();

				Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
					@Override
					public void onGenerated(Palette palette) {
						Palette.Swatch swatch = palette.getMutedSwatch();
						if (swatch == null) {
							// Try vibrant swatch
							swatch = palette.getDarkVibrantSwatch();
						}
						if (swatch != null) {
							imagesBackground.setBackgroundColor(swatch.getRgb());
						}
					}
				});
			}
		}
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event, menu);

        if (conventionEvent.getUserInput().isAttending()) {
            MenuItem favoritesButton = menu.findItem(R.id.event_change_favorite_state);
            favoritesButton.setIcon(getResources().getDrawable(android.R.drawable.btn_star_big_on));
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.event_change_favorite_state:
                if (conventionEvent.getUserInput().isAttending()) {
                    conventionEvent.getUserInput().setAttending(false);
                    item.setIcon(getResources().getDrawable(R.drawable.star_with_plus));
                    item.setTitle(getResources().getString(R.string.event_add_to_favorites));
                    Toast.makeText(this, getString(R.string.event_removed_from_favorites), Toast.LENGTH_SHORT).show();
                } else {
                    conventionEvent.getUserInput().setAttending(true);
                    item.setIcon(getResources().getDrawable(android.R.drawable.btn_star_big_on));
                    item.setTitle(getResources().getString(R.string.event_remove_from_favorites));
                    Toast.makeText(this, getString(R.string.event_added_to_favorites), Toast.LENGTH_SHORT).show();
                }
                Convention.getInstance().save();
                return true;
            case R.id.event_navigate_to_map:
                // Navigate to the map floor associated with this event
                Bundle floorBundle = new Bundle();
	            ConventionMap map = Convention.getInstance().getMap();
	            List<MapLocation> locations = map.findLocationsByHall(conventionEvent.getHall());
	            MapLocation location = map.findClosestLocation(locations);
	            floorBundle.putInt(MapActivity.EXTRA_MAP_LOCATION_ID, location.getId());

                navigateToActivity(MapActivity.class, false, floorBundle);
                return true;
            case R.id.event_navigate_to_hall:
                // Navigate to the hall associated with this event
                Bundle bundle = new Bundle();
                bundle.putString(HallActivity.EXTRA_HALL_NAME, conventionEvent.getHall().getName());

                navigateToActivity(HallActivity.class, false, bundle);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

	private void setEvent(ConventionEvent event) {

        setToolbarTitle(event.getType().getDescription());

        TextView title = (TextView) findViewById(R.id.event_title);
        title.setText(event.getTitle());
        TextView hallName = (TextView) findViewById(R.id.event_hall_name);
        hallName.setText(event.getHall().getName());
        TextView lecturerName = (TextView) findViewById(R.id.event_lecturer);
        String lecturer = event.getLecturer();
        if (lecturer == null) {
            lecturerName.setVisibility(View.GONE);
        } else {
            lecturerName.setText(lecturer);
        }
        TextView time = (TextView) findViewById(R.id.event_time);

        String formattedEventTime = String.format("%s - %s (%s)",
                Dates.formatHoursAndMinutes(event.getStartTime()),
                Dates.formatHoursAndMinutes(event.getEndTime()),
                Dates.toHumanReadableTimeDuration(event.getEndTime().getTime() - event.getStartTime().getTime()));
        time.setText(formattedEventTime);

		// Add images to the layout
		List<Integer> images = event.getImages();
		boolean first = true;
		for (int imageId : images) {
			ImageView imageView = new AspectRatioImageView(this);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			int topMargin = 0;
			if (first) {
				first = false;
			} else {
				topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
			}
			layoutParams.setMargins(0, topMargin, 0, 0);
			imageView.setLayoutParams(layoutParams);
			imageView.setImageResource(imageId);
			imagesLayout.addView(imageView);
		}

        TextView description = (TextView) findViewById(R.id.event_description);
        String eventDescription = event.getDescription();
        if (eventDescription == null) {
            eventDescription = "<p>" + event.getTitle() + "</p>";
	        if (event.getLecturer() != null && !event.getLecturer().isEmpty()) {
		        eventDescription += "<p><b>" + "מאת: " + "</b>" + event.getLecturer() + "</p>";
	        }
        }
        description.setText(Html.fromHtml(eventDescription));
    }
}
