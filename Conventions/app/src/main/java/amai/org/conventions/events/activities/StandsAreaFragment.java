package amai.org.conventions.events.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import amai.org.conventions.R;
import amai.org.conventions.map.StandsRecyclerAdapter;
import amai.org.conventions.model.Stand;
import amai.org.conventions.model.StandsArea;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.Objects;
import amai.org.conventions.utils.Views;
import pl.polidea.view.ZoomView;

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;

public class StandsAreaFragment extends DialogFragment {
    public static final String ARGUMENT_STANDS_AREA_ID = "ArgumentStandsAreaID";
    public static final String ARGUMENT_STAND_NAME = "ArgumentStandsID";
    private int standsAreaID = -1;
    private String selectedStandName;
    private StandsArea area;
    private ZoomView zoom;
    private ImageView image;
    private RecyclerView standsList;
    private StandsRecyclerAdapter standsAdapter;
    // Using RecyclerView with custom adapter since sticky headers GridView didn't
    // properly support scrollToPosition,
    private StandsSectionedGridRecyclerViewAdapter mSectionedAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        standsAreaID = getArguments().getInt(ARGUMENT_STANDS_AREA_ID, -1);
        selectedStandName = getArguments().getString(ARGUMENT_STAND_NAME);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = inflater.inflate(R.layout.activity_stands_area, container, false);

        standsList = view.findViewById(R.id.standsList);
        area = Convention.getInstance().findStandsArea(standsAreaID);
        if (area != null) {
            zoom = view.findViewById(R.id.stands_area_zoom);
            image = view.findViewById(R.id.stands_area_map);

            List<Stand> stands = new ArrayList<>(area.getStands());
            Collections.sort(stands, new Comparator<Stand>() {
                @Override
                public int compare(Stand lhs, Stand rhs) {
                    int result = lhs.getType().compareTo(rhs.getType());
                    if (result == 0) {
                        result = Objects.compareTo(lhs.getLocationName(), rhs.getLocationName(), false);
                    }
                    return result;
                }
            });

//            Collections.sort(stands, (stand, stand2) -> stand.getType().ordinal() - stand2.getType().ordinal());

            Map<Stand.StandType, List<Stand>> standTypeToStandsMap = CollectionUtils.groupBy(
                    stands,
                    Stand::getType,
                    (accumulate, currentItem) -> {
                        if (accumulate == null) {
                            List<Stand> standList = new ArrayList<Stand>();
                            standList.add(currentItem);
                            return standList;
                        } else {
                            accumulate.add(currentItem);
                            return accumulate;
                        }
                    }
            );
            List<Map.Entry<Stand.StandType, List<Stand>>> standTypeToStandsElements = new ArrayList<>(standTypeToStandsMap.entrySet());

            List<StandsSectionedGridRecyclerViewAdapter.Section> sections = new ArrayList<>();
            int indexInList = 0;
            for (Map.Entry<Stand.StandType, List<Stand>> entry : standTypeToStandsElements) {
                sections.add(new StandsSectionedGridRecyclerViewAdapter.Section(
                        indexInList,
                        getResources().getString(entry.getKey().getTitle())
                ));
                indexInList += entry.getValue().size();
            }

            List<List<Stand>> standsAfterGrouping = CollectionUtils.map(standTypeToStandsElements, Map.Entry::getValue);
            List<Stand> standsSortedToSections = CollectionUtils.flatMap(standsAfterGrouping);

            standsAdapter = new StandsRecyclerAdapter(standsSortedToSections, true, area.hasImageResource(), selectedStandName);
            standsList.setLayoutManager(new GridLayoutManager(getContext(), 2));

            mSectionedAdapter = new
                    StandsSectionedGridRecyclerViewAdapter(getContext(),
                    android.R.layout.simple_list_item_1, android.R.id.text1, standsList, standsAdapter);

            StandsSectionedGridRecyclerViewAdapter.Section[] sectionArray = new StandsSectionedGridRecyclerViewAdapter.Section[sections.size()];
            mSectionedAdapter.setSections(sections.toArray(sectionArray));

            standsList.setAdapter(mSectionedAdapter);

            if (area.hasImageResource()) {
                image.setImageResource(area.getImageResource());
                zoom.setVisibility(View.VISIBLE);
                zoom.setMaxZoom(3);
                image.setOnTouchListener(Views.createOnSingleTapConfirmedListener(getActivity(), new Runnable() {
                    @Override
                    public void run() {
                        openStandsMap();
                    }
                }));
            }

            standsAdapter.setOnClickListener(position -> {
                Stand stand = standsSortedToSections.get(position);
                zoomToStand(stand);
                selectedStandName = stand.getName();
                standsAdapter.setSelectedStandName(selectedStandName);
                standsAdapter.notifyDataSetChanged();
            });
            if (selectedStandName != null) {
                scrollToStand(selectedStandName, true);
            }
        }

        Button dismissButton = (Button) view.findViewById(R.id.stands_dismiss);
        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

    private void scrollToStand(String selectedStandName, boolean zoomAfterScroll) {
        int foundPosition = -1;
        int currPosition = 0;
        Stand foundStand = null;
        for (Stand stand : standsAdapter.getStands()) {
            if (selectedStandName.equals(stand.getName())) {
                foundPosition = currPosition;
                foundStand = stand;
                break;
            }
            ++currPosition;
        }
        if (foundPosition != -1) {
            Stand finalFoundStand = foundStand;
            // After we finish scrolling, zoom to the stand
            if (zoomAfterScroll) {
                standsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        if (newState == SCROLL_STATE_IDLE) {
                            zoomToStand(finalFoundStand);
                            standsList.removeOnScrollListener(this);
                        }
                    }

                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    }
                });
            }

            RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(getContext());
            smoothScroller.setTargetPosition(mSectionedAdapter.positionToSectionedPosition(foundPosition));

            standsList.getLayoutManager().startSmoothScroll(smoothScroller);
        }
    }

    private void zoomToStand(Stand stand) {
        if (zoom != null) {
            zoom.smoothZoomTo(zoom.getMaxZoom(),
                    stand.getImageX() / area.getImageWidth() * image.getWidth(),
                    stand.getImageY() / area.getImageHeight() * image.getHeight());
        }
    }

    private void openStandsMap() {
        ImageZoomDialogFragment.newInstance(area.getImageResource()).show(getActivity().getSupportFragmentManager(), null);

    }

    public static class ImageZoomDialogFragment extends DialogFragment {
        private static final String ARG_IMAGE_RESOURCE = "ImageResource";
        private int imageResource;

        public static ImageZoomDialogFragment newInstance(int imageResource) {
            ImageZoomDialogFragment fragment = new ImageZoomDialogFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_IMAGE_RESOURCE, imageResource);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                imageResource = getArguments().getInt(ARG_IMAGE_RESOURCE);
            }
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            super.onCreateDialog(savedInstanceState);

            // This view is the root view of the dialog. It's not related to the view hierarchy and its layout
            // parameters are defined by the dialog.
            @SuppressLint("InflateParams") final View view = LayoutInflater.from(getActivity()).inflate(R.layout.image_zoom, null);
            ZoomView zoom = (ZoomView) view.findViewById(R.id.image_zoom_view);
            ImageView image = (ImageView) view.findViewById(R.id.zoomed_image);

            zoom.setMaxZoom(3);
            image.setImageResource(imageResource);

            final Dialog dialog = new Dialog(getActivity(), R.style.FullScreenDialog);
            dialog.setCancelable(true);
            dialog.setContentView(view);

            // Dim background behind the dialog
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.dimAmount = 0.7f;
            dialog.getWindow().setAttributes(layoutParams);

            view.setOnTouchListener(Views.createOnSingleTapConfirmedListener(getActivity(), new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                }
            }));

            return dialog;
        }
    }
}
