package amai.org.conventions.events.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.customviews.PaintDrawable;
import amai.org.conventions.customviews.PaintableImageView;
import amai.org.conventions.map.StandsRecyclerAdapter;
import amai.org.conventions.model.Stand;
import amai.org.conventions.model.StandLocation;
import amai.org.conventions.model.StandType;
import amai.org.conventions.model.StandsArea;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.Log;
import amai.org.conventions.utils.Objects;
import amai.org.conventions.utils.Strings;
import amai.org.conventions.utils.Views;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.text.method.LinkMovementMethodCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
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
        Convention convention = Convention.getInstance();
        area = convention.findStandsArea(standsAreaID);
        if (area != null) {
            zoom = view.findViewById(R.id.stands_area_zoom);
            imageFrame = view.findViewById(R.id.stands_area_map_frame);
            image = view.findViewById(R.id.stands_area_map);
			View zoomContainer = view.findViewById(R.id.stands_area_zoom_container);

            List<Stand> stands = convention.getStandsByStandArea(area);
            Collections.sort(stands, (lhs, rhs) -> {
                int result = lhs.getType().getOrder() - rhs.getType().getOrder();
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

                int orientation = area.getImageOrientation();
                if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    ViewGroup.LayoutParams layoutParams = image.getLayoutParams();
                    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    image.setLayoutParams(layoutParams);
                } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                    ViewGroup.LayoutParams layoutParams = image.getLayoutParams();
                    layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    image.setLayoutParams(layoutParams);
                }

                zoomContainer.setVisibility(View.VISIBLE);
                zoom.setMaxZoom(3);
                imageFrame.setOnTouchListener(Views.createOnSingleTapConfirmedListener(getActivity(), new Runnable() {
                    @Override
                    public void run() {
                        openStandsMap();
                    }
                }));
            }

            standsAdapter.setOnClickListener(new StandsRecyclerAdapter.OnClickListener() {
                @Override
                public void onItemClicked(int position) {
                    Stand stand = standsAdapter.getStands().get(position);
                    zoomToStand(stand);
                    selectedStandName = stand.getName();
                    standsAdapter.setSelectedStandName(selectedStandName);
                    standsAdapter.notifyDataSetChanged();
                }

                @Override
                public void onItemContextMenu(int position, ContextMenu menu) {
                    menu.add(R.string.stand_info).setOnMenuItemClickListener(item -> {
                        // Show stand additional info in a new popup
                        Stand stand = standsAdapter.getStands().get(position);
                        Context context = getContext();
                        if (context == null) {
                            return true;
                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);

                        View dialogView = View.inflate(builder.getContext(), R.layout.dialog_stand_info, null);

                        TextView descView = dialogView.findViewById(R.id.stand_description);
                        if (stand.getDescription() == null || stand.getDescription().trim().isEmpty()) {
                            descView.setVisibility(View.GONE);
                        } else {
                            descView.setVisibility(View.VISIBLE);
                            descView.setText(stand.getDescription());
                        }

                        TextView websiteView = dialogView.findViewById(R.id.stand_website);
                        if (stand.getWebsite() == null || stand.getWebsite().trim().isEmpty()) {
                            websiteView.setVisibility(View.GONE);
                        } else {
                            websiteView.setVisibility(View.VISIBLE);
                            websiteView.setText(Html.fromHtml(builder.getContext().getString(R.string.stand_website, stand.getWebsite())));
                            websiteView.setMovementMethod(LinkMovementMethodCompat.getInstance());
                        }

                        TextView typesView = dialogView.findViewById(R.id.stand_types);
                        List<String> standTypeNames = CollectionUtils.map(stand.getTypes(), StandType::getName);
                        typesView.setText(builder.getContext().getString(R.string.stand_types, TextUtils.join(", ", standTypeNames)));

                        TextView locationView = dialogView.findViewById(R.id.stand_location);
                        locationView.setText(builder.getContext().getString(R.string.stand_location, stand.getStandsArea().getName(), stand.getLocationName()));

                        builder
                            .setTitle(stand.getName())
                            .setView(dialogView)
                            .setPositiveButton(R.string.close, (dialog, which) -> dialog.dismiss())
                            .setCancelable(true)
                            .show();;
                        return true;
                    });
                }
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
                    // After we finish scrolling, zoom to the stand (with a small delay so the user can see it's being zoomed in)
                    if (zoomAfterScroll) {
                        new Handler().postDelayed(() -> zoomToStand(finalFoundStand), 400);
                    }
                }
            };
            smoothScroller.setTargetPosition(sectionedStandsAdapter.positionToSectionedPosition(foundPosition));
            standsList.getLayoutManager().startSmoothScroll(smoothScroller);
        }
    }

    private void zoomToStand(Stand stand) {
        if (zoom != null) {
            // If the image is smaller than the frame (due to the max height), there will be an offset
            float offsetX = imageFrame.getX();
            float offsetY = imageFrame.getY();
            zoom.smoothZoomTo(zoom.getMaxZoom(),
                    offsetX + (stand.getImageX() / area.getImageWidth() * image.getWidth()),
                    offsetY + (stand.getImageY() / area.getImageHeight() * image.getHeight()));
        }

        // Highlight
        if (image != null) {
            highlightStand(getActivity(), area, stand, image);
        }
    }

    private static void highlightStand(Context context, StandsArea area, Stand stand, ImageView imageView) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return;
        }

		// This could happen if the user exited the activity before this is called (since it runs delayed in some cases)
		if (context == null) {
			return;
		}

        if (imageView instanceof PaintableImageView) {
            List<PaintDrawable> highlights = new ArrayList<>(stand.getLocations().size());
            for (StandLocation location : stand.getLocations()) {
                highlights.add(location.getHighlightPaintDrawable(context));
            }

            ((PaintableImageView) imageView).setPaintDrawables(highlights, area.getImageWidth(), area.getImageHeight());
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

            zoom.setMaxZoom(3);

            StandsArea area = Convention.getInstance().findStandsArea(standsArea);
            if (area != null) {
                image.setImageResource(area.getImageResource());
            }

            final Dialog dialog = new Dialog(getActivity(), R.style.FullScreenDialog);
            dialog.setCancelable(true);
            dialog.setContentView(view);

            // Handle edge to edge
            Views.registerApplyInsets(Views.InsetType.PADDING, Views.InsetType.PADDING, Views.InsetType.PADDING, Views.InsetType.PADDING, false, zoom);

            // Dim background behind the dialog
            Window window = dialog.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.dimAmount = 0.7f;
            window.setAttributes(layoutParams);

            setImmersiveMode(window, true);

            // Highlight the selected stand locations
            if (area != null && standName != null) {
                Stand selectedStand = null;
                for (Stand stand : Convention.getInstance().getStandsByStandArea(area)) {
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
                            highlightStand(activity, area, finalSelectedStand, image);
                        }
                    });
                }
            }

            view.setOnTouchListener(Views.createOnSingleTapConfirmedListener(getActivity(), this::dismiss));

            // Show in landscape mode if the image is wide (or portrait if it's long)
            getActivity().setRequestedOrientation(area.getImageOrientation());
            return dialog;
        }


        @Override
        public void onDismiss(@NonNull DialogInterface dialog) {
            super.onDismiss(dialog);
            // Set the orientation back to normal
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            setImmersiveMode(getActivity().getWindow(), false);
        }

        private void setImmersiveMode(Window window, boolean enabled) {
            WindowInsetsControllerCompat windowInsetsController = WindowCompat.getInsetsController(window, window.getDecorView());
            if (enabled) {
                // Enable immersive mode - hide system bars by default (they can be displayed with swipe)
                windowInsetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
                windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
            } else {
                windowInsetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_DEFAULT);
                windowInsetsController.show(WindowInsetsCompat.Type.systemBars());
            }
        }
    }
}
