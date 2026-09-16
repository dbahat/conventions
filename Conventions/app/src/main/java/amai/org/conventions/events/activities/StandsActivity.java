package amai.org.conventions.events.activities;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.map.MapActivity;
import amai.org.conventions.map.StandsAreasRecyclerAdapter;
import amai.org.conventions.model.ConventionMap;
import amai.org.conventions.model.MapLocation;
import amai.org.conventions.model.Stand;
import amai.org.conventions.model.StandsArea;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.networking.StandsRefresher;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.Objects;
import amai.org.conventions.utils.Views;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import sff.org.conventions.BuildConfig;
import sff.org.conventions.R;

public class StandsActivity extends NavigationActivity implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = StandsActivity.class.getCanonicalName();

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView standsAreasList;
    private StandsAreasRecyclerAdapter standsAreasAdapter;
    private boolean isRefreshingStands;
    private StandsRefresher.OnRefreshFinishedListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentInContentContainer(R.layout.activity_stands);
        setToolbarTitle(getString(R.string.stands_areas));

        standsAreasList = findViewById(R.id.stands_areas_list);
        View standsSearchTitleSection = findViewById(R.id.stands_search_title_section);

        // Handle edge to edge
        Views.registerApplyInsets(Views.InsetType.NONE, Views.InsetType.NONE, Views.InsetType.PADDING, Views.InsetType.PADDING, false, standsSearchTitleSection);
        Views.registerApplyInsets(Views.InsetType.NONE, Views.InsetType.PADDING, Views.InsetType.PADDING, Views.InsetType.PADDING, false, standsAreasList);

        List<StandsArea> standsAreas = getStandsAreas();

        standsAreasAdapter = new StandsAreasRecyclerAdapter(standsAreas);
        standsAreasList.setLayoutManager(new LinearLayoutManager(this));
        standsAreasList.setAdapter(standsAreasAdapter);

        standsAreasAdapter.setOnClickListener(standsArea -> {
			Bundle bundle = new Bundle();
			bundle.putString(StandsAreaActivity.EXTRA_STANDS_AREA_NAME, standsArea.getName());
			navigateToActivity(StandsAreaActivity.class, false, bundle);
		});

        findViewById(R.id.stands_search_interceptor).setOnClickListener(v -> {
            navigateToActivity(StandsSearchActivity.class, false, null);
        });

        setupSwipeRefreshLayout();
        listener = new StandsRefresher.OnRefreshFinishedListener() {
            @Override
            public void onError(Exception error, boolean force) {
                isRefreshingStands = false;
                swipeRefreshLayout.setRefreshing(false);
                if (BuildConfig.DEBUG) {
                    Toast.makeText(StandsActivity.this, "Error refreshing stands: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                } else if (force) {
                    Toast.makeText(StandsActivity.this, R.string.update_failed, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSuccess(boolean force) {
                swipeRefreshLayout.setRefreshing(false);
                isRefreshingStands = false;

                // Update stands areas, in case new ones were added after the refresh
                List<StandsArea> standsAreas = getStandsAreas();
                standsAreasAdapter.setStandsAreas(standsAreas);
            }
        };
        StandsRefresher.getInstance().addListener(listener);

        isRefreshingStands = false;
        refreshStands(false);

        handleDeepLinks();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listener != null) {
            StandsRefresher.getInstance().removeListener(listener);
        }
    }

    private List<StandsArea> getStandsAreas() {
        List<StandsArea> standsAreas = Convention.getInstance().getStandsAreas().getItems();
        Collections.sort(standsAreas, (lhs, rhs) -> Objects.compareTo(lhs.getName(), rhs.getName(), false));

        // Filter out stands areas without stands
        Set<String> noStands = new HashSet<>();
        for (StandsArea area : standsAreas) {
            noStands.add(area.getName());
        }
        for (Stand stand : Convention.getInstance().getStands()) {
            noStands.remove(stand.getStandsArea().getName());
            if (noStands.isEmpty()) {
                break;
            }
        }
        standsAreas = CollectionUtils.filter(standsAreas, standsArea -> !noStands.contains(standsArea.getName()));

        return standsAreas;
    }

    private void setupSwipeRefreshLayout() {
        swipeRefreshLayout = findViewById(R.id.stands_swipe_layout);
        // Only allow to force refresh if we can refresh stands
        if (Convention.getInstance().getStandsURL() == null) {
            swipeRefreshLayout.setEnabled(false);
            return;
        }
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(ThemeAttributes.getColor(this, R.attr.swipeToRefreshColor));
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(ThemeAttributes.getColor(this, R.attr.swipeToRefreshBackgroundColor));
        swipeRefreshLayout.setOnChildScrollUpCallback((parent, child) -> standsAreasList.canScrollVertically(-1));
    }

    public void onRefresh() {
        refreshStands(true);
    }

    private void refreshStands(boolean force) {
        if (isRefreshingStands) {
            return;
        }
        isRefreshingStands = true;
        StandsRefresher.getInstance().refreshFromServer(force);
    }


    @Override
    protected boolean onCreateCustomOptionsMenu(Menu menu) {
        // Only show the options menu if there is a map (assuming a map always contains at least one stands area, if we have stands areas)
        if (Convention.getInstance().getMap().isAvailable()) {
            getMenuInflater().inflate(R.menu.menu_stands_areas, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return handleOptionsItem(item, Map.of(
            R.id.stands_areas_map, () -> {
                // Show all stands areas in the map
                Bundle bundle = new Bundle();
                ConventionMap map = Convention.getInstance().getMap();
                List<MapLocation> standAreasLocations = CollectionUtils.filter(map.getLocations(), MapLocation::areaAnyPlacesStandsAreas);
                int[] locationIds = CollectionUtils.mapToInt(standAreasLocations, MapLocation::getId);
                bundle.putIntArray(MapActivity.EXTRA_MAP_LOCATION_IDS, locationIds);

                navigateToActivity(MapActivity.class, false, bundle);
            }
        ));
    }

    private void handleDeepLinks() {
        Uri intentData = getIntent().getData();
        // The URI looks like: <scheme>://<host>/<path>?name=abc
        if (isStandsIntent(intentData)) {
            String standName = intentData.getQueryParameter("name") == null ? "" : intentData.getQueryParameter("name");
            List<Stand> stands = new ArrayList<>(1);
            if (!standName.isEmpty()) {
                for (Stand stand : Convention.getInstance().getStands()) {
                    if (stand.getName().equals(standName)) {
                        stands.add(stand);
                    }
                }
            }

            if (stands.size() == 1) {
                // Open stands area with the stand selected
                Bundle bundle = new Bundle();
                Stand stand = stands.get(0);
                bundle.putString(StandsAreaActivity.EXTRA_STANDS_AREA_NAME, stand.getStandsArea().getName());
                bundle.putString(StandsAreaActivity.EXTRA_STAND_NAME, stand.getName());
                navigateToActivity(StandsAreaActivity.class, false, bundle);
            } else {
                // Show error message. Since this deep link is opened from outside the app, we show it in a dialog, so the user has time
                // to read the message and understand the problem.
                String message;
                if (stands.isEmpty()) {
                    if ("null".equals(standName)) { // Possibly passed as null from Javascript code
                        standName = "";
                    }
                    message = getString(R.string.stand_not_found, standName);
                } else {
                    message = getString(R.string.too_many_stands_found, standName);
                }

                String finalStandName = standName;
                new AlertDialog.Builder(this)
                    .setTitle(R.string.show_stand)
                    .setMessage(message)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Open search with the name
                            openStandsSearchWithKeywords(finalStandName);
                        }
                    })
                    .show();
            }
        }
    }

    private boolean isStandsIntent(Uri intentData) {
        // This method should be synched with the intent filters for StandsActivity in AndroidManifest.xml
        if (intentData == null) {
            return false;
        }

        // Direct deep link - sff.org.conventions://stands/by-name?name=standName
        if ("stands".equals(intentData.getHost()) && intentData.getPath() != null && intentData.getPath().equals("/by-name")) {
            return true;
        // Test deep link - https://dbahat.github.io/conventions-redirect-test/stands-sff.html?name=standName
        } else if ("dbahat.github.io".equals(intentData.getHost()) && "/conventions-redirect-test/stands-sff.html".equals(intentData.getPath())) {
            return true;
        }
        return false;
    }

    private void openStandsSearchWithKeywords(String standName) {
        Bundle bundle = new Bundle();
        bundle.putString(StandsSearchActivity.EXTRA_KEYWORDS_FILTER, standName);
        navigateToActivity(StandsSearchActivity.class, false, bundle);
    }
}
