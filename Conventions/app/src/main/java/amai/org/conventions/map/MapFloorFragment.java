package amai.org.conventions.map;


import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Picture;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MotionEventCompat;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGImageView;
import com.manuelpeinado.imagelayout.ImageLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.SVGFileLoader;
import amai.org.conventions.customviews.AspectRatioSVGImageView;
import amai.org.conventions.customviews.InterceptorLinearLayout;
import amai.org.conventions.events.EventView;
import amai.org.conventions.events.activities.HallActivity;
import amai.org.conventions.model.Convention;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.ConventionEventComparator;
import amai.org.conventions.model.ConventionMap;
import amai.org.conventions.model.Floor;
import amai.org.conventions.model.Hall;
import amai.org.conventions.model.MapLocation;
import amai.org.conventions.model.Place;
import amai.org.conventions.utils.Dates;

/**
 * A fragment showing a single map floor
 */
public class MapFloorFragment extends Fragment implements Marker.MarkerListener {

    private static final String ARGS_FLOOR_NUMBER = "FloorNumber";
	private static final String STATE_SELECTED_LOCATIONS = "StateSelectedLocation";
	private static final String STATE_LOCATION_DETAILS_OPEN = "StateLocationDetailsOpen";
	private static final String STATE_MAP_FLOOR_ZOOMED = "StateMapFloorZoomed";

	private static boolean showAnimation = true;

	private View progressBar;
	private HorizontalScrollView scrollView;
    private ImageLayout mapFloorImage;
    private View upArrow;
    private View downArrow;

	private InterceptorLinearLayout locationDetails;
	private TextView locationTitle;
	private ImageView locationDetailsCloseImage;
	private EventView locationCurrentEvent;
	private EventView locationNextEvent;

    private OnMapArrowClickedListener mapArrowClickedListener;

	private List<Marker> floorMarkers = new LinkedList<>();
	private MapLocation locationToSelect;

	public MapFloorFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
	    setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map_floor, container, false);

        resolveUIElements(view);
	    initializeLocationDetails();
        initializeUpAndDownButtons();
	    setMapClickListeners(mapFloorImage);
        configureMapFloorAndRestoreState(savedInstanceState);
	    return view;
    }

	private void changeMapZoom(boolean zoomIn) {
		ViewGroup.LayoutParams layoutParams = mapFloorImage.getLayoutParams();
		layoutParams.width = zoomIn ? ViewGroup.LayoutParams.WRAP_CONTENT : ViewGroup.LayoutParams.MATCH_PARENT;
		mapFloorImage.setLayoutParams(layoutParams);

		if (zoomIn) {
			scrollView.post(new Runnable() {
				@Override
				public void run() {
					// Find a selected marker and center it
					ImageView view = null;
					for (Marker marker : floorMarkers) {
						if (marker.isSelected()) {
							view = marker.getImageView();
							break;
						}
					}

					if (view == null) {
						// If no views are selected, show the beginning (right side) of the map.
						// This is necessary because the HorizontalScrollView displays the left side
						// by default regardless of the layout direction.
						scrollView.fullScroll(View.FOCUS_RIGHT);
					} else {
						int scrollViewWidth = scrollView.getMeasuredWidth();
						int scrollX = (view.getLeft() - (scrollViewWidth / 2)) + (view.getWidth() / 2);
						scrollView.smoothScrollTo(scrollX, scrollView.getScrollY());
					}
				}
			});
		}
	}

	private boolean isMapZoomedIn() {
		return mapFloorImage.getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT;
	}

	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof OnMapArrowClickedListener)) {
            throw new AssertionError("This fragment must be invoked form an activity implementing "
                    + mapArrowClickedListener.getClass().getSimpleName());
        }

        mapArrowClickedListener = (OnMapArrowClickedListener) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mapArrowClickedListener = null;
    }

	@Override
	public void onSaveInstanceState(Bundle outState) {
		ArrayList<Integer> selectedLocationIDs = new ArrayList<>();
		for (Marker marker : floorMarkers) {
			if (marker.isSelected()) {
				selectedLocationIDs.add(marker.getLocation().getId());
			}
		}
		outState.putIntegerArrayList(STATE_SELECTED_LOCATIONS, selectedLocationIDs);
		outState.putBoolean(STATE_LOCATION_DETAILS_OPEN, locationDetails.getVisibility() == View.VISIBLE);
		outState.putBoolean(STATE_MAP_FLOOR_ZOOMED, isMapZoomedIn());

		super.onSaveInstanceState(outState);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_map, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.map_floor_zoom_to_fit:
				changeMapZoom(!isMapZoomedIn());
				return true;
		}
		return super.onOptionsItemSelected(item);
	}


	public static MapFloorFragment newInstance(int floor) {
        MapFloorFragment fragment = new MapFloorFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_FLOOR_NUMBER, floor);
        fragment.setArguments(args);

        return fragment;
    }

    private void initializeUpAndDownButtons() {
        upArrow.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
		        if (mapArrowClickedListener != null) {
			        mapArrowClickedListener.onUpArrowClicked();
		        }
	        }
        });

        downArrow.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
		        if (mapArrowClickedListener != null) {
			        mapArrowClickedListener.onDownArrowClicked();
		        }
	        }
        });
    }

    private void resolveUIElements(View view) {
	    progressBar = view.findViewById(R.id.floor_loading_progress_bar);
	    scrollView = (HorizontalScrollView) view.findViewById(R.id.horizontal_scroll_view);
        mapFloorImage = (ImageLayout) view.findViewById(R.id.map_floor_image);
        upArrow = view.findViewById(R.id.map_floor_up);
        downArrow = view.findViewById(R.id.map_floor_down);

	    // Current selected location details
	    locationDetails = (InterceptorLinearLayout) view.findViewById(R.id.location_details);
	    locationTitle = (TextView) view.findViewById(R.id.location_title);
	    locationDetailsCloseImage = (ImageView) view.findViewById(R.id.location_details_close_image);
	    locationCurrentEvent = (EventView) view.findViewById(R.id.location_current_event);
	    locationNextEvent = (EventView) view.findViewById(R.id.location_next_event);
    }

	private void initializeLocationDetails() {
		locationDetailsCloseImage.setColorFilter(getResources().getColor(android.R.color.black));
		locationDetailsCloseImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setSelectedLocationDetails(null);
			}
		});

		InterceptorLinearLayout.AllTouchEventsListener touchListener = new InterceptorLinearLayout.AllTouchEventsListener() {
			public final int TOUCH_SLOP = ViewConfiguration.get(getActivity()).getScaledTouchSlop();
			float startPointY;
			boolean isDragging = false;
			boolean performedDragUpAction = false;

			@Override
			public boolean onInterceptTouchEvent(MotionEvent event) {
				switch (MotionEventCompat.getActionMasked(event)) {
					case MotionEvent.ACTION_DOWN:
						// If the user started a scroll on the event details, don't let the view pager
						// we're in intercept it (so it doesn't scroll to the floor below/above)
						locationDetails.getParent().requestDisallowInterceptTouchEvent(true);
						resetMotionEventParameters(event);
						break;
					case MotionEvent.ACTION_CANCEL:
					case MotionEvent.ACTION_UP:
						// Touch event finished, let the view pager intercept events again.
						// We will only reach here is we didn't intercept this event.
						locationDetails.getParent().requestDisallowInterceptTouchEvent(false);

						// Touch event ended, don't handle the event. Children who are listening will handle it.
						isDragging = false;
						break;
					case MotionEvent.ACTION_MOVE:
						if (isDragging || movePassesThreshold(event)) {
							// The view is being dragged, so it should handle the event
							isDragging = true;
							return true;
						}
						break;
				}

				// Don't intercept any touch event except dragging
				return false;
			}

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (MotionEventCompat.getActionMasked(event)) {
					case MotionEvent.ACTION_DOWN:
						// If the user started a scroll on the event details, don't let the view pager
						// we're in intercept it (so it doesn't scroll to the floor below/above).
						// We get here only in rare cases (usually onInterceptTouchEvent should consume down event).
						locationDetails.getParent().requestDisallowInterceptTouchEvent(true);
						resetMotionEventParameters(event);
						break;
					case MotionEvent.ACTION_CANCEL:
					case MotionEvent.ACTION_UP:
						// Touch event finished, let the view pager intercept events again
						v.getParent().requestDisallowInterceptTouchEvent(false);
						// Handle click event for locationDetails (it isn't called because we consume
						// all motion events)
						handleClick(event);
						break;
					case MotionEvent.ACTION_MOVE:
						// Handle drag event
						handleMove(event);
						break;
				}
				// Always consume the events that arrive here
				return true;
			}

			private void handleMove(MotionEvent event) {
				if (movePassesThreshold(event)) {
					// Check if we moved down or up
					if (startPointY - event.getY() > 0) {
						// This could be called more than once because it takes a while to
						// navigate to the hall while the user moves their finger
						if (!performedDragUpAction) {
							performedDragUpAction = true;
							locationDetails.callOnClick();
						}
					} else {
						setSelectedLocationDetails(null);
					}
				}
			}

			private void handleClick(MotionEvent event) {
				if (!movePassesThreshold(event)) {
					locationDetails.callOnClick();
				}
			}

			private boolean movePassesThreshold(MotionEvent event) {
				return Math.abs(startPointY - event.getY()) > TOUCH_SLOP;
			}

			private void resetMotionEventParameters(MotionEvent event) {
				isDragging = false;
				performedDragUpAction = false;
				startPointY = event.getY();
			}
		};

		// Intercept and handle touch events on locationDetails so we can handle drag as well
		// as click on the view and its children
		locationDetails.setOnInterceptTouchEventListener(touchListener);
		locationDetails.setOnTouchListener(touchListener);
	}

    private void configureMapFloorAndRestoreState(final Bundle savedInstanceState) {
        int mapFloor = getArguments().getInt(ARGS_FLOOR_NUMBER);
	    final ConventionMap map = Convention.getInstance().getMap();
	    final Floor floor = map.findFloorByNumber(mapFloor);

	    // Add up and down arrows
	    boolean isTopFloor = floor.getNumber() == map.getTopFloor().getNumber();
	    upArrow.setVisibility(isTopFloor ? View.GONE : View.VISIBLE);

	    boolean isBottomFloor = floor.getNumber() == map.getBottomFloor().getNumber();
	    downArrow.setVisibility(isBottomFloor ? View.GONE : View.VISIBLE);

	    // Load images in background
	    new AsyncTask<Void, Void, Boolean>() {
		    SVG svg = null;
		    List<MapLocation> locations = null;

		    @Override
		    protected Boolean doInBackground(Void... params) {
			    // Load svg image for the map floor
			    svg = SVGFileLoader.loadSVG(getActivity(), floor.getImageResource());

			    // Find location markers and load their svg images (the views are created in the UI thread)
			    locations = map.findLocationsByFloor(floor);
			    for (final MapLocation location : locations) {
			        // We don't save the result because it's saved in a cache for quicker access in the UI thread
				    SVGFileLoader.loadSVG(getActivity(), location.getMarkerResource());
			    }

			    return true;
		    }

		    @Override
		    protected void onPostExecute(Boolean successful) {
			    // Check the background method finished successfully
			    if (!successful) {
				    return;
			    }

			    mapFloorImage.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
	            Picture picture = svg.renderToPicture();
			    mapFloorImage.setImageResourceFromDrawable(new PictureDrawable(picture), 100, 100);

			    Animation animation = null;
			    if (showAnimation) {
			        showAnimation = false;
				    animation = AnimationUtils.loadAnimation(getActivity(), R.anim.drop_and_fade_in_from_top);
				    animation.setStartOffset(100);
			    }

			    // Add markers
			    for (final MapLocation location : locations) {
				    // Add the marker for this location
				    View markerImageView = createMarkerView(location, null);
				    mapFloorImage.addView(markerImageView);
				    if (animation != null) {
				        markerImageView.startAnimation(animation);
				    }
			    }

			    // Set initially selected location now after we created all the markers
			    if (locationToSelect != null) {
			        selectLocation(locationToSelect);
				    locationToSelect = null;
			    }

	            restoreState(savedInstanceState);

			    progressBar.setVisibility(View.GONE);
			    scrollView.setVisibility(View.VISIBLE);
		    }
	    }.execute();
    }

	private void restoreState(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			ArrayList<Integer> selectedLocations = savedInstanceState.getIntegerArrayList(STATE_SELECTED_LOCATIONS);
			MapLocation selectedLocation = null;
			for (int locationId : selectedLocations) {
				for (Marker marker : floorMarkers) {
					if (marker.getLocation().getId() == locationId) {
						selectedLocation = marker.getLocation();
						marker.select(false);
						break;
					}
				}
			}

			boolean locationDetailsOpen = savedInstanceState.getBoolean(STATE_LOCATION_DETAILS_OPEN);
			if (locationDetailsOpen && selectedLocation != null) {
				// If the details are open there should only be one location selected so we take the last one
				setSelectedLocationDetails(selectedLocation);
			}

			if (savedInstanceState.getBoolean(STATE_MAP_FLOOR_ZOOMED)) {
				changeMapZoom(true);
			}
		}
	}

	private View createMarkerView(final MapLocation location, View markerShadowView) {
		final SVGImageView markerImageView = new AspectRatioSVGImageView(getActivity());

		// Set marker image
		SVG markerSvg = SVGFileLoader.loadSVG(getActivity(), location.getMarkerResource());
		markerImageView.setSVG(markerSvg);

		// Set marker layout parameters and scaling
		ImageLayout.LayoutParams layoutParams = new ImageLayout.LayoutParams();
		// Marker size
		layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
		layoutParams.height = location.getFloor().getMarkerHeight();
		// Marker location
		layoutParams.centerX = location.getX();
		layoutParams.bottom = 100 - location.getY();

		markerImageView.setLayoutParams(layoutParams);
		markerImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

		// On click handler
		Marker marker = new Marker(location, markerImageView, markerShadowView, markerImageView.getDrawable(),
				new Marker.DrawableProvider() {
					Drawable drawable = null;
					@Override
					public Drawable getDrawable() {
						if (drawable == null) {
							SVG markerSelectedSvg = SVGFileLoader.loadSVG(getActivity(), location.getSelectedMarkerResource());
							drawable = new PictureDrawable(markerSelectedSvg.renderToPicture());
						}
						return drawable;
					}
				});
		floorMarkers.add(marker);
		marker.setOnClickListener(this);

		return markerImageView;
	}

	public void selectLocation(MapLocation location) {
		// If this fragment is already initialized, select the marker
		if (floorMarkers != null) {
			for (Marker marker : floorMarkers) {
				if (marker.getLocation().getId() == location.getId()) {
					marker.select(false);
					break;
				}
			}
		} else {
			// Save it for later use
			locationToSelect = location;
		}
	}

    public interface OnMapArrowClickedListener {
        void onUpArrowClicked();
        void onDownArrowClicked();
    }

	@Override
	public void onClick(Marker marker) {
		// Deselect all markers except the clicked marker
		for (Marker currMarker : floorMarkers) {
			if (currMarker != marker) {
				currMarker.deselect();
			}
		}

		// Ensure clicked marker is selected
		marker.select();
		setSelectedLocationDetails(marker.getLocation());
	}

	private void setMapClickListeners(View view) {
		view.setOnTouchListener(new View.OnTouchListener() {
			private GestureDetector gestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
				@Override
				public boolean onDoubleTap(MotionEvent e) {
					changeMapZoom(!isMapZoomedIn());
					return true;
				}

				@Override
				public boolean onSingleTapConfirmed(MotionEvent e) {
					for (Marker marker : floorMarkers) {
						marker.deselect();
					}
					setSelectedLocationDetails(null);
					return true;
				}
			});

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				gestureDetector.onTouchEvent(event);
				return true;
			}
		});
	}

	public void setSelectedLocationDetails(final MapLocation location) {
		if (location == null) {
			if (locationDetails.getVisibility() != View.GONE) {
				Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.abc_slide_out_bottom);
				locationDetails.startAnimation(animation);
				animation.setAnimationListener(new Animation.AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {
					}

					@Override
					public void onAnimationEnd(Animation animation) {
						locationDetails.setVisibility(View.GONE);
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}
				});
			}
		} else {
			locationDetails.setVisibility(View.VISIBLE);
			locationTitle.setText(location.getName());

			// Get current and next events in this location
			ConventionEvent currEvent = null;
			ConventionEvent nextEvent = null;
			ArrayList<ConventionEvent> events = Convention.getInstance().findEventsByHall(location.getPlace().getName());
			Collections.sort(events, new ConventionEventComparator());
			for (ConventionEvent event : events) {
				Date now = Dates.now();
				if (currEvent == null && event.getStartTime().before(now) && event.getEndTime().after(now)) {
					currEvent = event;
				}
				if (nextEvent == null && event.getStartTime().after(now)) {
					nextEvent = event;
				}
				if (currEvent != null && nextEvent != null) {
					break;
				}
			}

			if (currEvent == null) {
				locationCurrentEvent.setVisibility(View.GONE);
			} else {
				locationCurrentEvent.setVisibility(View.VISIBLE);
				locationCurrentEvent.setEvent(currEvent);
			}

			if (nextEvent == null) {
				locationNextEvent.setVisibility(View.GONE);
			} else {
				locationNextEvent.setVisibility(View.VISIBLE);
				locationNextEvent.setEvent(nextEvent);
			}

			locationDetails.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// Navigate to the hall associated with this location (only if it's a hall)
					Place place = location.getPlace();
					if (place instanceof Hall) {
						Bundle animationBundle = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.abc_slide_in_bottom, 0).toBundle();
						Bundle bundle = new Bundle();
						bundle.putString(HallActivity.EXTRA_HALL_NAME, place.getName());

						Intent intent = new Intent(getActivity(), HallActivity.class);
						intent.putExtras(bundle);
						getActivity().startActivity(intent, animationBundle);
					}
				}
			});
		}
	}
}