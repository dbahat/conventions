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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.customviews.PaintDrawable;
import amai.org.conventions.customviews.PaintableImageView;
import amai.org.conventions.map.MapActivity;
import amai.org.conventions.map.StandViewHolder;
import amai.org.conventions.map.StandsRecyclerAdapter;
import amai.org.conventions.model.ConventionMap;
import amai.org.conventions.model.MapLocation;
import amai.org.conventions.model.Stand;
import amai.org.conventions.model.StandLocation;
import amai.org.conventions.model.StandType;
import amai.org.conventions.model.StandsArea;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.networking.StandsRefresher;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.Dates;
import amai.org.conventions.utils.Log;
import amai.org.conventions.utils.Objects;
import amai.org.conventions.utils.Views;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.text.method.LinkMovementMethodCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import pl.polidea.view.ZoomView;
import sff.org.conventions.R;

public class StandsAreaActivity extends NavigationActivity {
    private static final String TAG = StandsAreaActivity.class.getCanonicalName();

    public static final String EXTRA_STANDS_AREA_NAME = "ExtraStandsAreaName";
    public static final String EXTRA_STAND_NAME = "ExtraStandName";
    public static final String EXTRA_USE_SLIDE_OUT_ANIMATION_ON_BACK = "ExtraUseSlideOutAnimationOnBack";

    public static final String VIEW_NAME_STANDS_LIST = "stands_list";

    private boolean useSlideOutAnimationOnBack;
    private String standsAreaName;
    private String selectedStandName;
    private StandsArea area;
    private ZoomView zoom;
    private FrameLayout imageFrame;
    private ImageView image;
    private ImageView imageHighlight;
    private RecyclerView standsList;
    private StandsRecyclerAdapter standsAdapter;
    private StandsRefresher.OnRefreshFinishedListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = (savedInstanceState != null ? savedInstanceState : getIntent().getExtras());
        standsAreaName = bundle.getString(EXTRA_STANDS_AREA_NAME);
        selectedStandName = bundle.getString(EXTRA_STAND_NAME);
        useSlideOutAnimationOnBack = bundle.getBoolean(EXTRA_USE_SLIDE_OUT_ANIMATION_ON_BACK, false);
        setCloseTransition(0, R.anim.slide_out_bottom, false);

        area = Convention.getInstance().findStandsAreaByName(standsAreaName);
        if (area == null) {
            Log.e(TAG, "Could not find stands area with name " + standsAreaName);
            Toast.makeText(this, getString(R.string.stands_area_not_found), Toast.LENGTH_LONG).show();
            onFinishing();
            finish();
            return;
        }

        setContentInContentContainer(R.layout.activity_stands_area);
        setToolbarTitle(area.getName());

        setupStandsArea();

        // Handle stands list refresh
        listener = new StandsRefresher.OnRefreshFinishedListener() {
            @Override
            public void onSuccess(boolean force) {
                // Update stands list
                if (standsAdapter != null) {
                    List<Stand> stands = getStandsList();
                    standsAdapter.setStands(stands);
                }
            }
        };
        StandsRefresher.getInstance().addListener(listener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listener != null) {
            StandsRefresher.getInstance().removeListener(listener);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (useSlideOutAnimationOnBack) {
            setCloseTransition(0, R.anim.slide_out_bottom, true);
        }
    }

    @Override
    protected boolean onCreateCustomOptionsMenu(Menu menu) {
        // Only show the options menu if this stands area exists in the map
        List<MapLocation> locations = getStandsAreaMapLocations();
        if (!locations.isEmpty()) {
            getMenuInflater().inflate(R.menu.menu_stands_areas, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return handleOptionsItem(item, Map.of(
            R.id.stands_areas_map, () -> {
                // Show location(s) for this stands area in the map
                List<MapLocation> locations = getStandsAreaMapLocations();
                if (!locations.isEmpty()) {
                    Bundle bundle = new Bundle();
                    int[] locationIds = CollectionUtils.mapToInt(locations, MapLocation::getId);
                    bundle.putIntArray(MapActivity.EXTRA_MAP_LOCATION_IDS, locationIds);

                    navigateToActivity(MapActivity.class, false, bundle);
                }
            }
        ));
    }

    private List<MapLocation> getStandsAreaMapLocations() {
        ConventionMap map = Convention.getInstance().getMap();
        return map.findLocationsByStandsAreaId(area.getId());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_STANDS_AREA_NAME, standsAreaName);
        outState.putString(EXTRA_STAND_NAME, selectedStandName);
        outState.putBoolean(EXTRA_USE_SLIDE_OUT_ANIMATION_ON_BACK, useSlideOutAnimationOnBack);
    }

    private void setupStandsArea() {
        standsList = findViewById(R.id.standsList);
        zoom = findViewById(R.id.stands_area_zoom);
        imageFrame = findViewById(R.id.stands_area_map_frame);
        image = findViewById(R.id.stands_area_map);
        View zoomContainer = findViewById(R.id.stands_area_zoom_container);

        standsList.setTransitionName(VIEW_NAME_STANDS_LIST);

        // Handle edge to edge
        Views.registerApplyInsets(Views.InsetType.NONE, Views.InsetType.NONE, Views.InsetType.PADDING, Views.InsetType.PADDING, false, zoom);
        Views.registerApplyInsets(Views.InsetType.NONE, Views.InsetType.PADDING, Views.InsetType.PADDING, Views.InsetType.PADDING, false, standsList);

        List<Stand> stands = getStandsList();
        standsAdapter = new StandsRecyclerAdapter(stands, area.hasImageResource(), selectedStandName);
        standsList.setLayoutManager(new LinearLayoutManager(this));
        standsList.setAdapter(standsAdapter);

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
            imageFrame.setOnTouchListener(Views.createOnSingleTapConfirmedListener(this, new Runnable() {
                @Override
                public void run() {
                    openStandsMap();
                }
            }));
        }

        standsAdapter.setOnClickListener(new StandViewHolder.OnClickListener() {
            @Override
            public void onItemClicked(Stand stand) {
                zoomToStand(stand);
                selectedStandName = stand.getName();
                standsAdapter.setSelectedStandName(selectedStandName);
                standsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onItemInfoClicked(Stand stand) {
                // Show stand additional info in a new popup
                showStandInfo(StandsAreaActivity.this, stand);
            }
        });
        if (selectedStandName != null) {
            scrollToStand(selectedStandName, true);
        }
    }

    private List<Stand> getStandsList() {
        List<Stand> stands = Convention.getInstance().getStandsByStandArea(area);
        boolean checkActive = Convention.getInstance().hasStarted() && !Convention.getInstance().hasEnded();
        Collections.sort(stands, (lhs, rhs) -> {
            // Show inactive stands at the end (during the convention)
            if (checkActive) {
                if (lhs.isActive() && !rhs.isActive()) {
                    return 1;
                } else if (!lhs.isActive() && rhs.isActive()) {
                    return -1;
                }
            }

            int result = Objects.compareTo(lhs.getSort(), rhs.getSort(), false);
            if (result == 0) {
                result = Objects.compareTo(lhs.getName(), rhs.getName(), false);
            }
            return result;
        });
        return stands;
    }

    public static void showStandInfo(Context context, Stand stand) {
        showStandInfo(context, stand, null);
    }

    public static void showStandInfo(Context context, Stand stand, List<String> keywordsToHighlight) {
        if (context == null) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        Context builderContext = builder.getContext();
        View dialogView = View.inflate(builderContext, R.layout.dialog_stand_info, null);

        boolean isAlwaysActive = true;
        TextView activeDaysView = dialogView.findViewById(R.id.stand_active_days);
        if (!stand.isAlwaysActive()) {
            isAlwaysActive = false;
            List<String> activeDays = CollectionUtils.map(stand.getActiveDays(), date -> Dates.formatDate("EEE dd.MM", date.getDate()));
            activeDaysView.setText(TextUtils.join(", ", activeDays));
            activeDaysView.setVisibility(View.VISIBLE);
        } else {
            activeDaysView.setVisibility(View.GONE);
        }

        TextView locationView = dialogView.findViewById(R.id.stand_location);
        if (stand.getLocationName() == null || stand.getLocationName().trim().isEmpty()) {
            locationView.setText(stand.getStandsArea().getName());
        } else {
            locationView.setText(builderContext.getString(R.string.stand_location, stand.getStandsArea().getName(), stand.getLocationName()));
        }

        // Show "stand is inactive" message if we are during the convention and the stand is not currently active
        TextView inactiveView = dialogView.findViewById(R.id.stand_inactive);
        if (!isAlwaysActive && Convention.getInstance().hasStarted() && !Convention.getInstance().hasEnded() && !stand.isActive()) {
            inactiveView.setVisibility(View.VISIBLE);
        } else {
            inactiveView.setVisibility(View.GONE);
        }

        TextView discountView = dialogView.findViewById(R.id.stand_discount);
        if (stand.hasDiscount()) {
            discountView.setVisibility(View.VISIBLE);
        } else {
            discountView.setVisibility(View.GONE);
        }

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
            websiteView.setText(Html.fromHtml(builderContext.getString(R.string.stand_website, stand.getWebsite())));
            websiteView.setMovementMethod(LinkMovementMethodCompat.getInstance());
        }

        // Add the types and tags as text views inside stand_tags_container
        FlexboxLayout tagsContainer = dialogView.findViewById(R.id.stand_tags_container);
        tagsContainer.removeAllViews();
        List<String> allTags = CollectionUtils.map(stand.getTypes(), StandType::getName);
        if (stand.getTags() != null) {
            allTags.addAll(stand.getTags());
        }

        int tagViewTextColor = ThemeAttributes.getColor(builderContext, R.attr.standTagTextColor);
        int tagViewBackgroundResource = ThemeAttributes.getResourceId(builderContext, R.attr.standTagBackground);
        int paddingTopBottom = builderContext.getResources().getDimensionPixelOffset(R.dimen.stand_tag_padding_top_bottom);
        int paddingStartEnd = builderContext.getResources().getDimensionPixelOffset(R.dimen.stand_tag_padding_start_end);
        int marginTop = builderContext.getResources().getDimensionPixelOffset(R.dimen.stand_tag_margin_top);
        int marginBetween = builderContext.getResources().getDimensionPixelOffset(R.dimen.stand_tag_margin_between);
        boolean first = true;
        int i = 0;
        int lastIndex = allTags.size() - 1;
        List<TextView> tagViews = new ArrayList<>(allTags.size());
        for (String tag : allTags) {
            TextView tagView = new TextView(builderContext);

            TextViewCompat.setTextAppearance(tagView, R.style.StandTag);
            tagView.setTextColor(tagViewTextColor);
            tagView.setBackgroundResource(tagViewBackgroundResource);
            tagView.setText(tag);
            if (first) {
                first = false;
            }
            tagView.setPaddingRelative(paddingStartEnd, paddingTopBottom, paddingStartEnd, paddingTopBottom);
            tagView.setGravity(Gravity.CENTER);
            tagsContainer.addView(tagView);
            tagViews.add(tagView);

            // Set margins
            FlexboxLayout.LayoutParams layoutParams = ((FlexboxLayout.LayoutParams) tagView.getLayoutParams());
            layoutParams.setMargins(0, marginTop, 0, 0);
            int marginEnd = i == lastIndex ? 0 : marginBetween;
            layoutParams.setMarginEnd(marginEnd);
            tagView.setLayoutParams(layoutParams);
            ++i;
        }

        // Highlight keywords in the stand description, types and tags
        if (keywordsToHighlight != null) {
            int highlightColor = ThemeAttributes.getColor(context, R.attr.standKeywordHighlightColor);
            for (String keyword : keywordsToHighlight) {
                if (!keyword.trim().isEmpty()) {
                    Views.tryHighlightKeywordInTextView(descView, keyword, highlightColor);
                    for (TextView tagView : tagViews) {
                        Views.tryHighlightKeywordInTextView(tagView, keyword, highlightColor);
                    }
                }
            }
        }

        builder
            .setTitle(stand.getName())
            .setView(dialogView)
            .setPositiveButton(R.string.close, (dialog, which) -> dialog.dismiss())
            .setCancelable(true)
            .show();
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
            RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(this) {
                @Override
                protected void onStop() {
                    super.onStop();
                    // After we finish scrolling, zoom to the stand (with a small delay so the user can see it's being zoomed in)
                    if (zoomAfterScroll) {
                        new Handler().postDelayed(() -> zoomToStand(finalFoundStand), 400);
                    }
                }

                @Override
                protected int getVerticalSnapPreference() {
                    return SNAP_TO_START;
                }
            };
            smoothScroller.setTargetPosition(foundPosition);
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
        if (image != null && image.getVisibility() == View.VISIBLE) {
            highlightStand(this, area, stand, image);
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

        if (imageView instanceof PaintableImageView && stand.getLocations() != null) {
            List<PaintDrawable> highlights = new ArrayList<>(stand.getLocations().size());
            for (StandLocation location : stand.getLocations()) {
                highlights.add(location.getHighlightPaintDrawable(context));
            }

            ((PaintableImageView) imageView).setPaintDrawables(highlights, area.getImageWidth(), area.getImageHeight());
        }
    }

    private void openStandsMap() {
        ImageZoomDialogFragment.newInstance(area, selectedStandName).show(getSupportFragmentManager(), null);
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
