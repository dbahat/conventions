package amai.org.conventions.map;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Picture;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGExternalFileResolver;
import com.caverock.androidsvg.SVGImageView;
import com.caverock.androidsvg.SVGParseException;
import com.manuelpeinado.imagelayout.ImageLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import amai.org.conventions.R;
import amai.org.conventions.events.EventView;
import amai.org.conventions.events.activities.HallActivity;
import amai.org.conventions.model.Convention;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.ConventionEventComparator;
import amai.org.conventions.model.ConventionMap;
import amai.org.conventions.model.Dates;
import amai.org.conventions.model.Floor;
import amai.org.conventions.model.MapLocation;

/**
 * A fragment showing a single map floor
 */
public class MapFloorFragment extends Fragment implements Marker.MarkerListener {

    private static final String ARGS_FLOOR_NUMBER = "FloorNumber";

	private static Map<Integer, SVG> loadedSVGFiles = new HashMap<>();

    private ImageLayout mapFloorImage;
    private ImageView upArrowImage;
    private ImageView downArrowImage;

	private ViewGroup locationDetails;
	private TextView locationTitle;
	private ImageView locationDetailsCloseImage;
	private EventView locationCurrentEvent;
	private EventView locationNextEvent;

    private OnMapArrowClickedListener mapArrowClickedListener;

	private SVGExternalFileResolver resolver;
	private List<Marker> floorMarkers;
	private MapLocation locationToSelect;

	public MapFloorFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map_floor, container, false);

        resolveUIElements(view);
	    initializeLocationDetailsClose();
        initializeUpAndDownButtons();
        configureMapFloor();
	    setMainViewClickListener(view);

	    return view;
    }

	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof OnMapArrowClickedListener)) {
            throw new AssertionError("This fragment must be invoked form an activity implementing "
                    + mapArrowClickedListener.getClass().getSimpleName());
        }

        mapArrowClickedListener = (OnMapArrowClickedListener) activity;

	    resolver = new AssetsExternalFileResolver(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mapArrowClickedListener = null;
    }

    public static MapFloorFragment newInstance(int floor) {
        MapFloorFragment fragment = new MapFloorFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_FLOOR_NUMBER, floor);
        fragment.setArguments(args);

        return fragment;
    }

    private void initializeUpAndDownButtons() {
        upArrowImage.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
		        if (mapArrowClickedListener != null) {
			        mapArrowClickedListener.onUpArrowClicked();
		        }
	        }
        });

        downArrowImage.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
		        if (mapArrowClickedListener != null) {
			        mapArrowClickedListener.onDownArrowClicked();
		        }
	        }
        });
    }

    private void resolveUIElements(View view) {
        mapFloorImage = (ImageLayout) view.findViewById(R.id.map_floor_image);
        upArrowImage = (ImageView) view.findViewById(R.id.map_floor_up_arrow);
        downArrowImage = (ImageView) view.findViewById(R.id.map_floor_down_arrow);

	    // Current selected location details
	    locationDetails = (ViewGroup) view.findViewById(R.id.location_details);
	    locationTitle = (TextView) view.findViewById(R.id.location_title);
	    locationDetailsCloseImage = (ImageView) view.findViewById(R.id.location_details_close_image);
	    locationCurrentEvent = (EventView) view.findViewById(R.id.location_current_event);
	    locationNextEvent = (EventView) view.findViewById(R.id.location_next_event);
    }

	private void initializeLocationDetailsClose() {
		locationDetailsCloseImage.setColorFilter(getResources().getColor(android.R.color.black));
		locationDetailsCloseImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setSelectedLocationDetails(null);
			}
		});
	}

    private void configureMapFloor() {
        int mapFloor = getArguments().getInt(ARGS_FLOOR_NUMBER);
	    ConventionMap map = Convention.getInstance().getMap();
	    Floor floor = map.findFloorByNumber(mapFloor);

	    try {
		    // Load svg image for the map floor
		    SVG svg = loadSVG(floor.getImageResource());

		    Picture picture = svg.renderToPicture();
		    mapFloorImage.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		    mapFloorImage.setImageResourceFromDrawable(new PictureDrawable(picture), 100, 100);
		    floorMarkers = new LinkedList<>();

		    // Add markers
		    List<MapLocation> locations = map.findLocationsByFloor(floor);
		    for (final MapLocation location : locations) {
			    // Add drop shadow
			    View markerShadowView = createMarkerShadowView(location);
			    mapFloorImage.addView(markerShadowView);
			    // Add the marker for this location
			    View markerImageView = createMarkerView(location, markerShadowView);
			    mapFloorImage.addView(markerImageView);
		    }
		    // Set initially selected location now after we created all the markers
		    if (locationToSelect != null) {
		        selectLocation(locationToSelect);
			    locationToSelect = null;
		    }

		    // Add up and down arrows
		    boolean isTopFloor = floor.getNumber() == map.getTopFloor().getNumber();
		    upArrowImage.setVisibility(isTopFloor ? View.GONE : View.VISIBLE);

		    boolean isBottomFloor = floor.getNumber() == map.getBottomFloor().getNumber();
		    downArrowImage.setVisibility(isBottomFloor ? View.GONE : View.VISIBLE);
	    } catch (SVGParseException e) {
		    throw new RuntimeException(e);
	    }

    }

	private View createMarkerView(final MapLocation location, View markerShadowView) throws SVGParseException {
		final SVGImageView markerImageView = new SVGImageView(getActivity());

		// Set marker image
		SVG markerSvg = loadSVG(location.getMarkerResource());
		markerImageView.setSVG(markerSvg);

		// Set marker layout parameters and scaling
		ImageLayout.LayoutParams layoutParams = new ImageLayout.LayoutParams();
		// Marker size
		layoutParams.width = location.getFloor().getMarkerWidth();
		layoutParams.height = layoutParams.width * 2;
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
						try {
							if (drawable == null) {
								SVG markerSelectedSvg = loadSVG(location.getSelectedMarkerResource());
								drawable = new PictureDrawable(markerSelectedSvg.renderToPicture());
							}
							return drawable;
						} catch (SVGParseException e) {
							throw new RuntimeException(e);
						}
					}
				});
		floorMarkers.add(marker);
		marker.setOnClickListener(this);

		return markerImageView;
	}

	private View createMarkerShadowView(MapLocation location) throws SVGParseException {
		SVGImageView markerImageView = new SVGImageView(getActivity());

		// Set marker image
		SVG markerSvg = loadSVG(R.raw.marker_shadow);
		markerImageView.setSVG(markerSvg);

		// Set marker layout parameters and scaling
		ImageLayout.LayoutParams layoutParams = new ImageLayout.LayoutParams();
		// Marker size
		layoutParams.width = location.getFloor().getMarkerWidth() + 3;
		layoutParams.height = layoutParams.width * 2;
		// Marker location
		layoutParams.centerX = location.getX();
		layoutParams.bottom = 100 - location.getY() + 3;

		markerImageView.setLayoutParams(layoutParams);
		markerImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
		markerImageView.setAlpha(0.5f);
		return markerImageView;
	}

	public void selectLocation(MapLocation location) {
		// If this fragment is already initialized, select the marker
		if (floorMarkers != null) {
			for (Marker marker : floorMarkers) {
				if (marker.getLocation().getId() == location.getId()) {
					marker.select();
					break;
				}
			}
		} else {
			// Save it for later use
			locationToSelect = location;
		}
	}

	public SVG loadSVG(int resource) throws SVGParseException {
		if (loadedSVGFiles.containsKey(resource)) {
			return loadedSVGFiles.get(resource);
		}

		SVG svg = SVG.getFromResource(getResources(), resource);
		setSVGProperties(svg);
		loadedSVGFiles.put(resource, svg);
		return svg;
	}

	private void setSVGProperties(SVG svg) throws SVGParseException {
		svg.setDocumentHeight("100%");
		svg.setDocumentWidth("100%");
		svg.registerExternalFileResolver(resolver);
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

	private void setMainViewClickListener(View view) {
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				for (Marker marker : floorMarkers) {
					marker.deselect();
				}
				setSelectedLocationDetails(null);
			}
		});
	}

	public void setSelectedLocationDetails(final MapLocation location) {
		if (location == null) {
			locationDetails.setVisibility(View.GONE);
		} else {
			locationDetails.setVisibility(View.VISIBLE);
			locationTitle.setText(location.getName());

			// Get current and next events in this location
			ConventionEvent currEvent = null;
			ConventionEvent nextEvent = null;
			ArrayList<ConventionEvent> events = Convention.getInstance().findEventsByHall(location.getHall().getName());
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
					// Navigate to the hall associated with this event
					Bundle bundle = new Bundle();
					bundle.putString(HallActivity.EXTRA_HALL_NAME, location.getHall().getName());

					Intent intent = new Intent(getActivity(), HallActivity.class);
					intent.putExtras(bundle);
					startActivity(intent);
					getActivity().overridePendingTransition(0, 0);
				}
			});
		}
	}
}