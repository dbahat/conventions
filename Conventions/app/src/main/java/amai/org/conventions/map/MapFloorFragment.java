package amai.org.conventions.map;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import amai.org.conventions.R;

/**
 * A fragment showing a single map floor
 */
public class MapFloorFragment extends Fragment {

    private static final String ARGS_FLOOR_NUMBER = "FloorNumber";
    private View mapFloorImage;
    private ImageView upArrowImage;
    private ImageView downArrowImage;

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
        configureMapFloorImage();

        return view;
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
                ((MapFragment) getParentFragment()).onUpArrowClicked();
            }
        });

        downArrowImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MapFragment) getParentFragment()).onDownArrowClicked();
            }
        });
    }

    private void resolveUIElements(View view) {
        mapFloorImage = view.findViewById(R.id.map_floor_image);
        upArrowImage = (ImageView) view.findViewById(R.id.map_floor_up_arrow);
        downArrowImage = (ImageView) view.findViewById(R.id.map_floor_down_arrow);
    }

    private void configureMapFloorImage() {

        int mapFloor = getArguments().getInt(ARGS_FLOOR_NUMBER);
        switch (mapFloor) {
            case 0:
                mapFloorImage.setBackground(getResources().getDrawable(R.drawable.floor1));
                downArrowImage.setVisibility(View.GONE);
                break;
            case 1:
                mapFloorImage.setBackground(getResources().getDrawable(R.drawable.floor2));
                break;
            case 2:
                mapFloorImage.setBackground(getResources().getDrawable(R.drawable.floor3));
                upArrowImage.setVisibility(View.GONE);
                break;
        }
    }
}