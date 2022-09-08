package amai.org.conventions.events.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.map.StandsRecyclerAdapter;
import amai.org.conventions.model.Stand;
import amai.org.conventions.model.StandLocation;
import amai.org.conventions.model.StandsArea;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.utils.Objects;
import amai.org.conventions.utils.Views;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import pl.polidea.view.ZoomView;

public class StandsAreaFragment extends DialogFragment {
    public static final String ARGUMENT_STANDS_AREA_ID = "ArgumentStandsAreaID";
    public static final String ARGUMENT_STAND_NAME = "ArgumentStandsID";
    private int standsAreaID = -1;
    private String selectedStandName;
    private StandsArea area;
    private ZoomView zoom;
    private FrameLayout imageFrame;
    private ImageView image;
    private ImageView imageHighlight;
    private RecyclerView standsList;
    private StandsRecyclerAdapter standsAdapter;
    // Using RecyclerView with custom adapter since sticky headers GridView didn't
    // properly support scrollToPosition
    private SectionedGridRecyclerViewAdapterWrapper sectionedStandsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = savedInstanceState != null ? savedInstanceState : getArguments();
        standsAreaID = args.getInt(ARGUMENT_STANDS_AREA_ID, -1);
        selectedStandName = args.getString(ARGUMENT_STAND_NAME);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARGUMENT_STANDS_AREA_ID, standsAreaID);
        outState.putString(ARGUMENT_STAND_NAME, selectedStandName);
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
            imageFrame = view.findViewById(R.id.stands_area_map_frame);
            image = view.findViewById(R.id.stands_area_map);
            imageHighlight = view.findViewById(R.id.stands_area_map_highlight);

            List<Stand> stands = new ArrayList<>(area.getStands());
            Collections.sort(stands, (lhs, rhs) -> {
                int result = lhs.getType().compareTo(rhs.getType());
                if (result == 0) {
                    result = Objects.compareTo(lhs.getSort(), rhs.getSort(), false);
                }
                return result;
            });

            standsAdapter = new StandsRecyclerAdapter(stands, true, area.hasImageResource(), selectedStandName);
            standsList.setLayoutManager(new GridLayoutManager(getContext(), 2));

            sectionedStandsAdapter = new SectionedGridRecyclerViewAdapterWrapper<>(standsList, standsAdapter);
            standsList.setAdapter(sectionedStandsAdapter);

            if (area.hasImageResource()) {
                image.setImageResource(area.getImageResource());
                zoom.setVisibility(View.VISIBLE);
                zoom.setMaxZoom(3);
                imageFrame.setOnTouchListener(Views.createOnSingleTapConfirmedListener(getActivity(), new Runnable() {
                    @Override
                    public void run() {
                        openStandsMap();
                    }
                }));
            }

            standsAdapter.setOnClickListener(position -> {
                Stand stand = standsAdapter.getStands().get(position);
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
            RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(getContext()) {
                @Override
                protected void onStop() {
                    super.onStop();
                    // After we finish scrolling, zoom to the stand
                    if (zoomAfterScroll) {
                        zoomToStand(finalFoundStand);
                    }
                }
            };
            smoothScroller.setTargetPosition(sectionedStandsAdapter.positionToSectionedPosition(foundPosition));
            standsList.getLayoutManager().startSmoothScroll(smoothScroller);
        }
    }

    private void zoomToStand(Stand stand) {
        if (zoom != null) {
            zoom.smoothZoomTo(zoom.getMaxZoom(),
                    stand.getImageX() / area.getImageWidth() * image.getWidth(),
                    stand.getImageY() / area.getImageHeight() * image.getHeight());
        }

        // Highlight
        if (imageHighlight != null) {
            highlightStand(getActivity(), area, stand, image.getWidth(), image.getHeight(), imageHighlight);
        }
    }

    private static void highlightStand(Context context, StandsArea area, Stand stand, int imageWidth, int imageHeight, ImageView imageHighlight) {
        if (stand.getLocations().size() == 0) {
            imageHighlight.setVisibility(View.GONE);
        } else {
            ArrayList<Drawable> drawables = new ArrayList<>(stand.getLocations().size());
            for (StandLocation location : stand.getLocations()) {
                // It has to be a different drawable for each layer
                Drawable drawable = ThemeAttributes.getDrawable(context, R.attr.standsMapHighlight);
                drawables.add(drawable);
            }
            LayerDrawable layerDrawable = new LayerDrawable(drawables.toArray(new Drawable[0]));
            int index = 0;
            for (StandLocation location : stand.getLocations()) {
                layerDrawable.setLayerInset(
                        index,
                        (int)(location.getLeft() / area.getImageWidth() * imageWidth),
                        (int) (location.getTop()  / area.getImageHeight() * imageHeight),
                        imageWidth - (int)(location.getRight() / area.getImageWidth() * imageWidth),
                        imageHeight - (int) (location.getBottom()  / area.getImageHeight() * imageHeight)
                );
                ++index;
            }

            imageHighlight.setImageDrawable(layerDrawable);
            imageHighlight.setVisibility(View.VISIBLE);
        }
    }

    private void openStandsMap() {
        ImageZoomDialogFragment.newInstance(area, selectedStandName).show(getActivity().getSupportFragmentManager(), null);
    }

    public static class ImageZoomDialogFragment extends DialogFragment {
        private static final String ARG_STANDS_AREA = "StandsArea";
        private static final String ARG_STAND_NAME = "StandName";
        private int standsArea;
        private String standName;

        public static ImageZoomDialogFragment newInstance(StandsArea area, String selectedStandName) {
            ImageZoomDialogFragment fragment = new ImageZoomDialogFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_STANDS_AREA, area.getId());
            args.putString(ARG_STAND_NAME, selectedStandName);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                standsArea = getArguments().getInt(ARG_STANDS_AREA);
                standName = getArguments().getString(ARG_STAND_NAME);
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
            ImageView highlightImage = view.findViewById(R.id.zoomed_image_highlight);

            zoom.setMaxZoom(3);

            StandsArea area = Convention.getInstance().findStandsArea(standsArea);
            if (area != null) {
                image.setImageResource(area.getImageResource());
            }

            final Dialog dialog = new Dialog(getActivity(), R.style.FullScreenDialog);
            dialog.setCancelable(true);
            dialog.setContentView(view);

            // Dim background behind the dialog
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.dimAmount = 0.7f;
            dialog.getWindow().setAttributes(layoutParams);

            // Highlight the selected stand locations
            if (area != null && standName != null) {
                Stand selectedStand = null;
                for (Stand stand : area.getStands()) {
                    if (stand.getName().equals(standName)) {
                        selectedStand = stand;
                        break;
                    }
                }
                if (selectedStand != null) {
                    final Stand finalSelectedStand = selectedStand;

                    // We need the image size for this
                    dialog.setOnShowListener(dialog1 -> {
                        FragmentActivity activity = getActivity();
                        // This can happen the first time
                        if (activity != null) {
                            // Highlight
                            highlightStand(activity, area, finalSelectedStand, image.getWidth(), image.getHeight(), highlightImage);
                        }
                    });
                }
            }

            view.setOnTouchListener(Views.createOnSingleTapConfirmedListener(getActivity(), this::dismiss));

            // Show in landscape mode if the image is wide (or portrait if it's long)
            if (area.getImageWidth() > area.getImageHeight() * 1.2) {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else if (area.getImageHeight() > area.getImageWidth() * 1.2) {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }

            return dialog;
        }


        @Override
        public void onDismiss(@NonNull DialogInterface dialog) {
            super.onDismiss(dialog);
            // Set the orientation back to normal
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }
}
