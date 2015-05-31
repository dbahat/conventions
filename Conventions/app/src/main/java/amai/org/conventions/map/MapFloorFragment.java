package amai.org.conventions.map;


import android.app.Activity;
import android.graphics.Picture;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGExternalFileResolver;
import com.caverock.androidsvg.SVGImageView;
import com.caverock.androidsvg.SVGParseException;
import com.manuelpeinado.imagelayout.ImageLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import amai.org.conventions.R;
import amai.org.conventions.model.Convention;
import amai.org.conventions.model.ConventionMap;
import amai.org.conventions.model.Floor;
import amai.org.conventions.model.MapLocation;

/**
 * A fragment showing a single map floor
 */
public class MapFloorFragment extends Fragment {

    private static final String ARGS_FLOOR_NUMBER = "FloorNumber";

	private static Map<Integer, SVG> loadedSVGFiles = new HashMap<>();

    private ImageLayout mapFloorImage;
    private ImageView upArrowImage;
    private ImageView downArrowImage;
    private OnMapArrowClickedListener mapArrowClickedListener;

	private SVGExternalFileResolver resolver;

    public MapFloorFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map_floor, container, false);

        resolveUIElements(view);
        initializeUpAndDownButtons();
        configureMapFloor();

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

		    // Add markers
		    List<MapLocation> locations = map.findLocationsByFloor(floor);
		    for (final MapLocation location : locations) {
			    // Add drop shadow
			    mapFloorImage.addView(createMarkerShadowView(location));
			    // Add the marker for this location
			    View markerImageView = createMarkerView(location);
			    mapFloorImage.addView(markerImageView);
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

	private View createMarkerView(final MapLocation location) throws SVGParseException {
		SVGImageView markerImageView = new SVGImageView(getActivity());

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
		markerImageView.setOnClickListener(new View.OnClickListener() {
			MapLocation currLocation = location;
			@Override
			public void onClick(View v) {
				Toast.makeText(v.getContext(), currLocation.getName(), Toast.LENGTH_SHORT).show();
			}
		});
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

	private SVG loadSVG(int resource) throws SVGParseException {
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

}