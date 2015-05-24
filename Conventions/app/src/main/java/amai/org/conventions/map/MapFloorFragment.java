package amai.org.conventions.map;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import amai.org.conventions.R;
import amai.org.conventions.model.Convention;
import amai.org.conventions.model.ConventionMap;
import amai.org.conventions.model.Floor;

/**
 * A fragment showing a single map floor
 */
public class MapFloorFragment extends Fragment {

    private static final String ARGS_FLOOR_NUMBER = "FloorNumber";
    private ImageView mapFloorImage;
    private ImageView upArrowImage;
    private ImageView downArrowImage;
    private OnMapArrowClickedListener mapArrowClickedListener;

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
        mapFloorImage = (ImageView) view.findViewById(R.id.map_floor_image);
        upArrowImage = (ImageView) view.findViewById(R.id.map_floor_up_arrow);
        downArrowImage = (ImageView) view.findViewById(R.id.map_floor_down_arrow);
    }

    private void configureMapFloor() {
        int mapFloor = getArguments().getInt(ARGS_FLOOR_NUMBER);
	    ConventionMap map = Convention.getInstance().getMap();
	    Floor floor = map.findFloorByNumber(mapFloor);

	    mapFloorImage.setImageDrawable(getResources().getDrawable(floor.getImageResource()));

	    boolean isTopFloor = floor.getNumber() == map.getTopFloor().getNumber();
	    upArrowImage.setVisibility(isTopFloor ? View.GONE : View.VISIBLE);

	    boolean isBottomFloor = floor.getNumber() == map.getBottomFloor().getNumber();
	    downArrowImage.setVisibility(isBottomFloor ? View.GONE : View.VISIBLE);
    }

    public interface OnMapArrowClickedListener {
        void onUpArrowClicked();
        void onDownArrowClicked();
    }
}