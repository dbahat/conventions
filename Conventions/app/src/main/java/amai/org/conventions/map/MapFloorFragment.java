package amai.org.conventions.map;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Picture;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGImageView;
import com.google.android.gms.analytics.HitBuilders;
import com.manuelpeinado.imagelayout.ImageLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import amai.org.conventions.ConventionsApplication;
import amai.org.conventions.ImageHandler;
import amai.org.conventions.R;
import amai.org.conventions.customviews.AspectRatioSVGImageView;
import amai.org.conventions.customviews.InterceptorLinearLayout;
import amai.org.conventions.events.EventView;
import amai.org.conventions.events.activities.HallActivity;
import amai.org.conventions.events.activities.StandsAreaFragment;
import amai.org.conventions.model.Convention;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.ConventionEventComparator;
import amai.org.conventions.model.ConventionMap;
import amai.org.conventions.model.Floor;
import amai.org.conventions.model.Hall;
import amai.org.conventions.model.MapLocation;
import amai.org.conventions.model.Place;
import amai.org.conventions.model.StandsArea;
import amai.org.conventions.utils.Dates;
import pl.polidea.view.ZoomView;

/**
 * A fragment showing a single map floor
 */
public class MapFloorFragment extends Fragment implements Marker.MarkerListener {

    private static final String ARGS_FLOOR_NUMBER = "FloorNumber";
	private static final String ARGS_SHOW_ANIMATION = "ShowAnimation";
	private static final String STATE_SELECTED_LOCATIONS = "StateSelectedLocation";
	private static final String STATE_LOCATION_DETAILS_OPEN = "StateLocationDetailsOpen";
	private static final String STATE_MAP_FLOOR_ZOOM_FACTOR = "StateMapFloorZoomFactor";
	private static final String STATE_MAP_FLOOR_ZOOM_X = "StateMapFloorZoomX";
	private static final String STATE_MAP_FLOOR_ZOOM_Y = "StateMapFloorZoomY";


	private static final float MAX_ZOOM = 2.5f;

	private Floor floor;

	private View progressBar;
	private ZoomView mapZoomView;
    private ImageLayout mapFloorImage;
    private View upArrow;
    private View downArrow;

	private InterceptorLinearLayout locationDetails;
	private TextView locationTitle;
	private ImageView locationDetailsCloseImage;
	private EventView locationCurrentEvent;
	private EventView locationNextEvent;
	private Button gotoStandsListButton;

    private OnMapFloorEventListener mapFloorEventsListener;

	private List<Marker> floorMarkers = new LinkedList<>();
	private MapLocation locationToSelect;
	private Context appContext;
	private MapLocation currentLocationDetails;
	private boolean preventFragmentScroll;

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
        initializeEventsListener();
	    setMapClickListeners();
        configureMapFloorAndRestoreState(savedInstanceState);

	    return view;
    }

	public Floor getFloor() {
		return floor;
	}

	public void toggleMapZoom() {
		changeMapZoom(!isMapZoomedIn());
	}

	private void changeMapZoom(boolean zoomIn) {
		if (zoomIn) {
			// Find a selected marker and center it
			ImageView view = null;
			for (Marker marker : floorMarkers) {
				if (marker.isSelected()) {
					view = marker.getImageView();
					break;
				}
			}
			if (view == null) {
				mapZoomView.smoothZoomTo(MAX_ZOOM, mapZoomView.getWidth() / 2, mapZoomView.getHeight() / 2);
			} else {
				mapZoomView.smoothZoomTo(MAX_ZOOM, view.getX(), view.getY());
			}
		} else {
			mapZoomView.smoothZoomTo(1.0f, mapZoomView.getWidth() / 2, mapZoomView.getHeight() / 2);
		}
	}

	public boolean canSwipeToChangeFloor() {
		// Allow to swipe between floors when map is not zoomed in and location details is not open
		return !isMapZoomedIn() && !preventFragmentScroll;
	}

	public boolean isMapZoomedIn() {
		// If the fragment is still being initialized the view zoom will be null.
		// This could happen while the map activity is loading and its onCreateOptionsMenu is called
		// before this fragment's onCreateView.
		return mapZoomView != null && mapZoomView.getZoom() > 1.0f;
	}

	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof OnMapFloorEventListener)) {
            throw new AssertionError("This fragment must be invoked form an activity implementing "
                    + mapFloorEventsListener.getClass().getSimpleName());
        }

        mapFloorEventsListener = (OnMapFloorEventListener) activity;

		// The activity might be detached during the lifecycle while we still need a context.
		// The application context is always valid.
		appContext = activity.getApplicationContext();
    }

    @Override
    public void onDetach() {
	    super.onDetach();
        mapFloorEventsListener = null;
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
		outState.putFloat(STATE_MAP_FLOOR_ZOOM_FACTOR, mapZoomView.getZoom());
		outState.putFloat(STATE_MAP_FLOOR_ZOOM_X, mapZoomView.getZoomFocusX() / mapZoomView.getWidth());
		outState.putFloat(STATE_MAP_FLOOR_ZOOM_Y, mapZoomView.getZoomFocusY() / mapZoomView.getHeight());

		super.onSaveInstanceState(outState);
	}

	public static MapFloorFragment newInstance(int floor, boolean showAnimation) {
        MapFloorFragment fragment = new MapFloorFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_FLOOR_NUMBER, floor);
		args.putBoolean(ARGS_SHOW_ANIMATION, showAnimation);
        fragment.setArguments(args);

        return fragment;
    }

    private void initializeEventsListener() {
        upArrow.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
		        if (mapFloorEventsListener != null) {
			        mapFloorEventsListener.onUpArrowClicked();
		        }
	        }
        });

        downArrow.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
		        if (mapFloorEventsListener != null) {
			        mapFloorEventsListener.onDownArrowClicked();
		        }
	        }
        });

	    mapZoomView.setZoomListener(new ZoomView.ZoomViewListener() {
		    @Override
		    public void onZoomStarted(float zoom, float zoomx, float zoomy) {
		    }

		    @Override
		    public void onZooming(float zoom, float zoomx, float zoomy) {
		    }

		    @Override
		    public void onZoomEnded(float zoom, float zoomx, float zoomy) {
			    if (mapFloorEventsListener != null) {
				    mapFloorEventsListener.onZoomChanged();
			    }
		    }
	    });
    }

    private void resolveUIElements(View view) {
	    progressBar = view.findViewById(R.id.floor_loading_progress_bar);
	    mapZoomView = (ZoomView) view.findViewById(R.id.map_zoom_view);
        mapFloorImage = (ImageLayout) view.findViewById(R.id.map_floor_image);
        upArrow = view.findViewById(R.id.map_floor_up);
        downArrow = view.findViewById(R.id.map_floor_down);

	    // Current selected location details
	    locationDetails = (InterceptorLinearLayout) view.findViewById(R.id.location_details);
	    locationTitle = (TextView) view.findViewById(R.id.location_title);
	    locationDetailsCloseImage = (ImageView) view.findViewById(R.id.location_details_close_image);
	    locationCurrentEvent = (EventView) view.findViewById(R.id.location_current_event);
	    locationNextEvent = (EventView) view.findViewById(R.id.location_next_event);
	    gotoStandsListButton = (Button) view.findViewById(R.id.goto_stands_list_button);
    }

	private void initializeLocationDetails() {
		locationDetailsCloseImage.setColorFilter(ContextCompat.getColor(getContext(), android.R.color.black));
		locationDetailsCloseImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideLocationDetails();
			}
		});

		InterceptorLinearLayout.AllTouchEventsListener touchListener = new InterceptorLinearLayout.AllTouchEventsListener() {
			public final int TOUCH_SLOP = ViewConfiguration.get(appContext).getScaledTouchSlop();
			float startPointY;
			boolean isDragging = false;
			boolean performedDragUpAction = false;

			@Override
			public boolean onInterceptTouchEvent(MotionEvent event) {
				switch (MotionEventCompat.getActionMasked(event)) {
					case MotionEvent.ACTION_DOWN:
						// If the user started a scroll on the event details, don't let the view pager
						// we're in scroll to the floor below/above
						handleTouchEventsStarted(event);
						resetMotionEventParameters(event);
						break;
					case MotionEvent.ACTION_CANCEL:
					case MotionEvent.ACTION_UP:
						// Touch events finished, let the view pager and children intercept events again.
						handleTouchEventsFinished(event);
						break;
					case MotionEvent.ACTION_MOVE:
						if (isDragging || movePassesThreshold(event)) {
							// The view is being dragged, so it should handle the event (and not its children)
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
						handleTouchEventsStarted(event);
						resetMotionEventParameters(event);
						break;
					case MotionEvent.ACTION_CANCEL:
						// It's cancel so don't perform a click
						handleTouchEventsFinished(event);
						break;
					case MotionEvent.ACTION_UP:
						handleTouchEventsFinished(event);
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
						hideLocationDetails();
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
				if (event.getPointerCount() == 1) {
					isDragging = false;
					performedDragUpAction = false;
					startPointY = event.getY();
				}
			}

			private void handleTouchEventsFinished(MotionEvent event) {
				// Touch events finished, let the view pager intercept events again
				if (event.getPointerCount() == 1) {
					preventFragmentScroll = false;
					isDragging = false;
				}
			}

			private void handleTouchEventsStarted(MotionEvent event) {
				// If the user started a scroll on the event details, don't let the view pager
				// we're in scroll to the floor below/above
				preventFragmentScroll = true;
			}
		};

		// Intercept and handle touch events on locationDetails so we can handle drag as well
		// as click on the view and its children
		locationDetails.setOnInterceptTouchEventListener(touchListener);
		locationDetails.setOnTouchListener(touchListener);
	}

    private void configureMapFloorAndRestoreState(final Bundle savedInstanceState) {
        int mapFloor = getArguments().getInt(ARGS_FLOOR_NUMBER);
	    final boolean showAnimation = getArguments().getBoolean(ARGS_SHOW_ANIMATION);
	    getArguments().remove(ARGS_SHOW_ANIMATION); // Only show the animation once
	    final ConventionMap map = Convention.getInstance().getMap();
	    floor = map.findFloorByNumber(mapFloor);

	    // Add up and down arrows
	    boolean isTopFloor = floor.getNumber() == map.getTopFloor().getNumber();
	    upArrow.setVisibility(isTopFloor ? View.GONE : View.VISIBLE);

	    boolean isBottomFloor = floor.getNumber() == map.getBottomFloor().getNumber();
	    downArrow.setVisibility(isBottomFloor ? View.GONE : View.VISIBLE);

	    // We have to measure for the animations to work
	    downArrow.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

	    mapZoomView.setMaxZoom(MAX_ZOOM);

	    // Load images in background
	    new AsyncTask<Void, Void, Boolean>() {
		    SVG svg = null;
		    List<MapLocation> locations = null;

		    @Override
		    protected Boolean doInBackground(Void... params) {
			    // Load svg image for the map floor
			    svg = ImageHandler.loadSVG(appContext, floor.getImageResource());

			    // Find location markers and load their svg images (the views are created in the UI thread)
			    locations = map.findLocationsByFloor(floor);
			    for (final MapLocation location : locations) {
			        // We don't save the result because it's saved in a cache for quicker access in the UI thread
				    ImageHandler.loadSVG(appContext, location.getMarkerResource());
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
			    mapFloorImage.setImageResourceFromDrawable(new PictureDrawable(picture), floor.getImageWidth(), floor.getImageHeight());

			    Animation animation = null;
			    if (showAnimation) {
				    animation = AnimationUtils.loadAnimation(appContext, R.anim.drop_and_fade_in_from_top);
				    animation.setStartOffset(100);
			    }

			    // Add markers
			    for (final MapLocation location : locations) {
				    // Add the marker for this location
				    View markerImageView = createMarkerView(location);
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
			    mapZoomView.setVisibility(View.VISIBLE);
		    }
	    }.execute();
    }

	private void restoreState(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			ArrayList<Integer> selectedLocations = savedInstanceState.getIntegerArrayList(STATE_SELECTED_LOCATIONS);
			MapLocation selectedLocation = null;
			if (selectedLocations != null) {
				for (int locationId : selectedLocations) {
					for (Marker marker : floorMarkers) {
						if (marker.getLocation().getId() == locationId) {
							selectedLocation = marker.getLocation();
							marker.select(false);
							break;
						}
					}
				}
			}

			boolean locationDetailsOpen = savedInstanceState.getBoolean(STATE_LOCATION_DETAILS_OPEN);
			if (locationDetailsOpen && selectedLocation != null) {
				// If the details are open there should only be one location selected so we take the last one
				// Don't show animation when restoring
				showLocationDetails(selectedLocation, false);
			}

			float zoomFactor = savedInstanceState.getFloat(STATE_MAP_FLOOR_ZOOM_FACTOR, 1.0f);
			if (zoomFactor > 1.0f) {
				float zoomX = savedInstanceState.getFloat(STATE_MAP_FLOOR_ZOOM_X) * mapZoomView.getWidth();
				float zoomY = savedInstanceState.getFloat(STATE_MAP_FLOOR_ZOOM_Y) * mapZoomView.getHeight();
				mapZoomView.zoomTo(zoomFactor, zoomX / zoomFactor, zoomY / zoomFactor);
			}
		}
	}

	private View createMarkerView(final MapLocation location) {
		// We send the appContext here and not the activity due to reasons stated in onAttach,
		// this means any activity-specific information (like theme, layout direction etc) is not
		// passed to this image view. It doesn't make any difference in this case since we don't use
		// such information.
		final SVGImageView markerImageView = new AspectRatioSVGImageView(appContext);

		// Set marker image
		SVG markerSvg = ImageHandler.loadSVG(appContext, location.getMarkerResource());
		markerImageView.setSVG(markerSvg);
		// Setting layer type to none to avoid caching the marker views bitmap when in zoomed-out state.
		// The caching causes the marker image looks pixelized in zoomed-in state.
		// This has to be done after calling setSVG.
		markerImageView.setLayerType(View.LAYER_TYPE_NONE, null);

		// Set marker layout parameters and scaling
		ImageLayout.LayoutParams layoutParams = new ImageLayout.LayoutParams();
		// Marker size
		layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
		layoutParams.height = location.getMarkerHeight();

		// Marker location
		layoutParams.centerX = location.getX();
		layoutParams.bottom = floor.getImageHeight() - location.getY();

		markerImageView.setLayoutParams(layoutParams);
		markerImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

		// On click handler
		Marker marker = new Marker(location, markerImageView, markerImageView.getDrawable(),
				new Marker.DrawableProvider() {
					Drawable drawable = null;
					@Override
					public Drawable getDrawable() {
						if (drawable == null) {
							SVG markerSelectedSvg = ImageHandler.loadSVG(appContext, location.getSelectedMarkerResource());
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
		if (!floorMarkers.isEmpty()) {
			for (Marker marker : floorMarkers) {
				if (marker.getLocation().getId() == location.getId()) {
					marker.select(true);
					break;
				}
			}
		} else {
			// Save it for later use
			locationToSelect = location;
		}
	}

	public void resetState() {
		for (Marker marker : floorMarkers) {
			marker.deselect();
		}
		hideLocationDetails();
		changeMapZoom(false);
	}

	public void selectMarkersWithNameAndFloor(List<MapLocation> locations) {
		if (locations == null || locations.isEmpty()) {
			for (Marker marker : floorMarkers) {
				marker.deselect();
			}
			return;
		}

		MapLocation locationToDisplay = null;

		// Select sent locations and deselect everything else.
		// Don't deselect then reselect the same marker.
		int numOfSelectedMarkers = 0;
		MapLocationSearchEquality comparator = new MapLocationSearchEquality();
		for (Marker marker : floorMarkers) {
			boolean selected = false;
			for (MapLocation location : locations) {
				if (comparator.equals(location, marker.getLocation())) {
					marker.select();
					selected = true;
					++numOfSelectedMarkers;
					locationToDisplay = location;
					break;
				}
			}
			if (!selected) {
				marker.deselect();
			}
		}

		// Show the selected location (in case there's only 1 - it can be more than 1 if there are
		// several places with the same name in this floor)
		if (numOfSelectedMarkers == 1) {
			showLocationDetails(locationToDisplay, true);
		}
	}

	public void selectMarkerByLocation(MapLocation location) {
		int numOfSelectedMarkers = 0;
		for (Marker marker : floorMarkers) {
			boolean selected = false;
			if (location != null && marker.getLocation().getId() == location.getId()) {
				marker.select();
				selected = true;
				++numOfSelectedMarkers;
			}
			if (!selected) {
				marker.deselect();
			}
		}

		if (numOfSelectedMarkers == 1) {
			showLocationDetails(location, true);
		}
	}

    public interface OnMapFloorEventListener {
        void onUpArrowClicked();
        void onDownArrowClicked();
	    void onZoomChanged();

	    /**
	     * Location details top changed ("top" meaning distance from bottom).
	     * This method is called during animation.
	     */
	    void onLocationDetailsTopChanged(int top, MapFloorFragment floor);
    }

	@Override
	public void onClick(Marker marker) {
		ConventionsApplication.sendTrackingEvent(new HitBuilders.EventBuilder()
				.setCategory("Map")
				.setAction("MarkerClicked")
				.setLabel(marker.getLocation().getName())
				.build());

		// Deselect all markers except the clicked marker
		for (Marker currMarker : floorMarkers) {
			if (currMarker != marker) {
				currMarker.deselect();
			}
		}

		// Ensure clicked marker is selected
		marker.select();
		showLocationDetails(marker.getLocation(), true);
	}

	private void setMapClickListeners() {
		mapFloorImage.setOnTouchListener(new View.OnTouchListener() {
			private GestureDetector gestureDetector = new GestureDetector(appContext, new GestureDetector.SimpleOnGestureListener() {
				@Override
				public boolean onSingleTapConfirmed(MotionEvent e) {
					for (Marker marker : floorMarkers) {
						marker.deselect();
					}
					hideLocationDetails();
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

	private void hideLocationDetails() {
		if (currentLocationDetails == null) {
			return;
		}
		currentLocationDetails = null;

		if (locationDetails.getVisibility() != View.GONE) {
			Animator animator = animateLocationDetailsTranslationY(0, 1, new AccelerateInterpolator());
			animator.addListener(new Animator.AnimatorListener() {
				@Override
				public void onAnimationStart(Animator animation) {
				}

				@Override
				public void onAnimationEnd(Animator animation) {
					locationDetails.setVisibility(View.GONE);
					locationDetails.setTranslationY(0);
				}

				@Override
				public void onAnimationCancel(Animator animation) {
				}

				@Override
				public void onAnimationRepeat(Animator animation) {
				}
			});
			animator.start();
		}
	}

	public int getMapHiddenPortionHeight() {
		return (downArrow == null) ? 0 :
				(downArrow.getVisibility() == View.VISIBLE ? downArrow.getMeasuredHeight() : 0) +
				(locationDetails.getVisibility() == View.VISIBLE ? locationDetails.getMeasuredHeight() : 0);
	}

	@NonNull
	private ObjectAnimator animateLocationDetailsTranslationY(float fromFraction, float toFraction, TimeInterpolator interpolator) {
		// Using ObjectAnimator and not xml animation so we can listen to the animation update and notify the listener
		// (which updates the location of the floating action button(
		final int detailsHeight = locationDetails.getMeasuredHeight();
		ObjectAnimator animator = ObjectAnimator.ofFloat(locationDetails, "translationY", fromFraction * detailsHeight, toFraction * detailsHeight);
		animator.setInterpolator(interpolator);

		final int baseline = (downArrow.getVisibility() == View.VISIBLE ? downArrow.getMeasuredHeight() : 0);
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				float value = (float) animation.getAnimatedValue();
				if (mapFloorEventsListener != null) {
					mapFloorEventsListener.onLocationDetailsTopChanged(detailsHeight - (int) value + baseline, MapFloorFragment.this);
				}
			}
		});
		animator.setDuration(400);
		return animator;
	}

	private void showLocationDetails(final MapLocation location, boolean animate) {
		// Don't change location details if it's already the displayed location
		// because we don't want the animation to be displayed in this case
		if (location == currentLocationDetails || location == null) {
			return;
		}
		currentLocationDetails = location;

		boolean isVisible = locationDetails.getVisibility() == View.VISIBLE;
		if (isVisible && animate) {
			// Hide the location details with animation before opening it
			Animator hideAnimator = animateLocationDetailsTranslationY(0, 1, new DecelerateInterpolator());

			hideAnimator.addListener(new Animator.AnimatorListener() {
				@Override
				public void onAnimationStart(Animator animation) {
				}

				@Override
				public void onAnimationEnd(Animator animation) {
					setLocationDetails(location);
					createShowLocationDetailsAnimator().start();
				}

				@Override
				public void onAnimationCancel(Animator animation) {
				}

				@Override
				public void onAnimationRepeat(Animator animation) {
				}
			});
			hideAnimator.start();
		} else {
			setLocationDetails(location);
			if (animate) {
				createShowLocationDetailsAnimator().start();
			} else {
				locationDetails.post(new Runnable() {
					@Override
					public void run() {
						if (mapFloorEventsListener != null) {
							mapFloorEventsListener.onLocationDetailsTopChanged(getMapHiddenPortionHeight(), MapFloorFragment.this);
						}
					}
				});
			}
		}
	}

	private Animator createShowLocationDetailsAnimator() {
		final Animator showAnimator = animateLocationDetailsTranslationY(1, 0, new AccelerateInterpolator());
		showAnimator.addListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				locationDetails.setTranslationY(0);
			}

			@Override
			public void onAnimationCancel(Animator animation) {
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}
		});
		return showAnimator;
	}

	private void setLocationDetails(final MapLocation location) {
		locationDetails.setVisibility(View.VISIBLE);
		locationTitle.setText(location.getName());
		locationDetails.setOnClickListener(null);

		// Get current and next events in this location
		setupHallLocation(location);

		// Check if it's a stands area
		setupStandsLocation(location);

		// We have to measure for the animations to work (we can't define percentage in ObjectAnimator)
		locationDetails.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
	}

	private void setupStandsLocation(final MapLocation location) {
		// Only show button if there is more than 1 stand
		if (location.getPlace() instanceof StandsArea && ((StandsArea) location.getPlace()).getStands().size() > 1) {
			gotoStandsListButton.setVisibility(View.VISIBLE);
			gotoStandsListButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// Show the list of stands in a dialog
					Place place = location.getPlace();
					StandsAreaFragment standsFragment = new StandsAreaFragment();

					Bundle args = new Bundle();
					args.putInt(StandsAreaFragment.ARGUMENT_STANDS_AREA_ID, ((StandsArea) place).getId());
					standsFragment.setArguments(args);

					standsFragment.show(getFragmentManager(), null);
				}
			});
		} else {
			gotoStandsListButton.setVisibility(View.GONE);
		}
	}

	private void setupHallLocation(final MapLocation location) {
		ConventionEvent currEvent = null;
		ConventionEvent nextEvent = null;
		boolean isHall = location.getPlace() instanceof Hall;
		if (isHall) {
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

		if (isHall) {
			locationDetails.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// Navigate to the hall associated with this location (only if it's a hall)
					Place place = location.getPlace();
					Bundle animationBundle = ActivityOptions.makeCustomAnimation(appContext, R.anim.slide_in_bottom, 0).toBundle();
					Bundle bundle = new Bundle();
					bundle.putString(HallActivity.EXTRA_HALL_NAME, place.getName());
					bundle.putBoolean(HallActivity.EXTRA_USE_SLIDE_OUT_ANIMATION_ON_BACK, true);

					Intent intent = new Intent(getActivity(), HallActivity.class);
					intent.putExtras(bundle);
					getActivity().startActivity(intent, animationBundle);
				}
			});
		}
	}
}