package amai.org.conventions;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import java.util.List;

import amai.org.conventions.map.MapActivity;
import amai.org.conventions.model.ConventionMap;
import amai.org.conventions.model.MapLocation;
import amai.org.conventions.model.conventions.Harucon2020Convention;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.utils.CollectionUtils;
import fi.iki.kuitsi.listtest.ListTagHandler;

public class AccessibilityActivity extends NavigationActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentInContentContainer(R.layout.activity_accessability);
        setToolbarTitle(getString(R.string.accessibility));

        handleDeepLinks();

        TextView webContentContainer = findViewById(R.id.web_content);
        if (webContentContainer != null) {
            webContentContainer.setText(Html.fromHtml(getString(R.string.accessibility_content), null, new ListTagHandler()));
            webContentContainer.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    private void handleDeepLinks() {
        Uri intentData = getIntent().getData();
        if (intentData != null && intentData.getHost() != null) {
            switch (intentData.getHost().intern()) {
                case "open-accessibility": {
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivity(intent);
                    break;
                }
                case "open-map-in-parent-room": {
                    ConventionMap map = Convention.getInstance().getMap();
                    List<MapLocation> locations = map.findLocationsByName(Harucon2020Convention.PARENTS_ROOM_NAME);
                    int[] locationIds = CollectionUtils.mapToInt(locations, MapLocation::getId);
                    Bundle floorBundle = new Bundle();
                    floorBundle.putIntArray(MapActivity.EXTRA_MAP_LOCATION_IDS, locationIds);
                    navigateToActivity(MapActivity.class, false, floorBundle);
                    break;
                }
                case "open-map-in-accessible-cashiers": {
                    ConventionMap map = Convention.getInstance().getMap();
                    List<MapLocation> locations = map.findLocationsByName("נגישה", false);
                    int[] locationIds = CollectionUtils.mapToInt(locations, MapLocation::getId);
                    Bundle floorBundle = new Bundle();
                    floorBundle.putIntArray(MapActivity.EXTRA_MAP_LOCATION_IDS, locationIds);
                    navigateToActivity(MapActivity.class, false, floorBundle);
                    break;
                }
                case "open-map": {
                    navigateToActivity(MapActivity.class, false, null);
                    break;
                }
            }
        }
    }
}
