package amai.org.conventions;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.widget.TextView;

import java.util.List;

import amai.org.conventions.map.MapActivity;
import amai.org.conventions.model.ConventionMap;
import amai.org.conventions.model.MapLocation;
import amai.org.conventions.model.conventions.Harucon2026Convention;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.Views;
import androidx.core.text.method.LinkMovementMethodCompat;
import fi.iki.kuitsi.listtest.ListTagHandler;

public class AccessibilityActivity extends NavigationActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentInContentContainer(R.layout.activity_accessability);
        setBackground(ThemeAttributes.getDrawable(this, R.attr.infoActivitiesBackground));
        setToolbarTitle(getString(R.string.accessibility));

        // Handle edge to edge
        Views.registerApplyInsets(Views.InsetType.NONE, Views.InsetType.NONE, Views.InsetType.PADDING, Views.InsetType.PADDING, false, findViewById(R.id.accessibility_scroll));
        // The bottom padding must be applied to a view inside the scroll view, otherwise the scrolling doesn't include the padding when scrolled from the last
        // textview and it has a LinkMovementMethod
        Views.registerApplyInsets(Views.InsetType.NONE, Views.InsetType.PADDING, Views.InsetType.NONE, Views.InsetType.NONE, false, findViewById(R.id.scroll_view_bottom_padding));

        handleDeepLinks();

        TextView webContentContainer = findViewById(R.id.web_content);
        if (webContentContainer != null) {
            webContentContainer.setText(Html.fromHtml(getString(R.string.accessibility_content), null, new ListTagHandler()));
            webContentContainer.setMovementMethod(LinkMovementMethodCompat.getInstance());
        }
    }

    private void handleDeepLinks() {
        Uri intentData = getIntent().getData();
        if (intentData != null && intentData.getPath() != null) {
            switch (intentData.getPath().intern()) {
                case "/open-accessibility": {
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivity(intent);
                    break;
                }
                case "/open-map-in-parent-room": {
                    ConventionMap map = Convention.getInstance().getMap();
                    List<MapLocation> locations = map.findLocationsByName(Harucon2026Convention.CHILDREN_ROOM_NAME);
                    int[] locationIds = CollectionUtils.mapToInt(locations, MapLocation::getId);
                    Bundle floorBundle = new Bundle();
                    floorBundle.putIntArray(MapActivity.EXTRA_MAP_LOCATION_IDS, locationIds);
                    navigateToActivity(MapActivity.class, false, floorBundle);
                    break;
                }
                case "/open-map-in-accessible-cashiers": {
                    ConventionMap map = Convention.getInstance().getMap();
                    List<MapLocation> locations = map.findLocationsByName(Harucon2026Convention.ACCESSIBLE_CASHIERS_NAME, false);
                    int[] locationIds = CollectionUtils.mapToInt(locations, MapLocation::getId);
                    Bundle floorBundle = new Bundle();
                    floorBundle.putIntArray(MapActivity.EXTRA_MAP_LOCATION_IDS, locationIds);
                    navigateToActivity(MapActivity.class, false, floorBundle);
                    break;
                }
                case "/open-map": {
                    navigateToActivity(MapActivity.class, false, null);
                    break;
                }
            }
            finish(); // This activity was opened again due to the deep link so close the new instance
        }
    }
}
