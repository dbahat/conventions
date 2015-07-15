package amai.org.conventions;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import amai.org.conventions.navigation.NavigationActivity;

public class ArrivalMethodsActivity extends NavigationActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
	private final static double LATITUDE = 31.786372;
	private final static double LONGITUDE = 35.202425;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentInContentContainer(R.layout.activity_arrival_methods);
        setToolbarTitle(getResources().getString(R.string.arrivalMethods));
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.arrival_methods_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.arrival_methods_navigate:
				Intent intent = new Intent(Intent.ACTION_VIEW,
						Uri.parse("geo:" + LATITUDE + "," + LONGITUDE +
									"?q=" + LATITUDE + "," + LONGITUDE +
									"(" + Uri.encode(getString(R.string.arrival_methods_marker_name)) + ")"));
				if (intent.resolveActivity(getPackageManager()) != null) {
					this.startActivity(intent);
				} else {
					Toast.makeText(this, "No activity for intent", Toast.LENGTH_SHORT).show();
				}
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
	    LatLng conventionLocation = new LatLng(LATITUDE, LONGITUDE);

        mMap.addMarker(new MarkerOptions()
                // TODO - return the custom marker after scaling it to a proper size.
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.harucon_logo))
                .position(conventionLocation)
		        // Workaround for Hebrew not being displayed - add unicode RTL character before the string
                .title("\u200e" + getResources().getString(R.string.arrival_methods_marker_name)));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(conventionLocation, 16));
    }
}
