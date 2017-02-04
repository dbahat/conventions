package amai.org.conventions;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.notifications.PlayServicesInstallation;
import fi.iki.kuitsi.listtest.ListTagHandler;

public class ArrivalMethodsActivity extends NavigationActivity implements OnMapReadyCallback {

	private GoogleMap mMap; // Might be null if Google Play services APK is not available.
	private View arrivalMethodsRoot;
	private View mapFragment;
	private View noMapLayout;
	private Button installPlayServicesButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentInContentContainer(R.layout.activity_arrival_methods, false, false);
		setToolbarTitle(getResources().getString(R.string.arrival_methods));

		arrivalMethodsRoot = findViewById(R.id.arrival_methods_root);
		mapFragment = findViewById(R.id.map);
		noMapLayout = findViewById(R.id.no_map);
		installPlayServicesButton = (Button) findViewById(R.id.install_play_services);

		TextView arrivalMethodsDescription = (TextView) findViewById(R.id.arrival_methods_description);
		arrivalMethodsDescription.setText(Html.fromHtml(getString(R.string.arrival_method_description), null, new ListTagHandler()));
		arrivalMethodsDescription.setMovementMethod(LinkMovementMethod.getInstance());
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
			case R.id.arrival_methods_navigate: {
				double latitude = Convention.getInstance().getLatitude();
				double longitude = Convention.getInstance().getLongitude();
				Intent intent = new Intent(Intent.ACTION_VIEW,
						Uri.parse("geo:" + latitude + "," + longitude +
								"?q=" + latitude + "," + longitude));
				if (intent.resolveActivity(getPackageManager()) != null) {
					this.startActivity(intent);
				} else {
					Toast.makeText(this, getString(R.string.no_navigation_activity), Toast.LENGTH_LONG).show();
				}
				return true;
			}
			case R.id.arrival_methods_navigate_bus: {
				double latitude = Convention.getInstance().getLatitude();
				double longitude = Convention.getInstance().getLongitude();
				Intent intent = new Intent(Intent.ACTION_VIEW,
						Uri.parse("moovit://directions?dest_lat=" + latitude + "&dest_lon=" + longitude + "&partner_id=" + this.getPackageName()));
				if (intent.resolveActivity(getPackageManager()) == null) {
					intent = new Intent(Intent.ACTION_VIEW,
							Uri.parse("https://web.moovitapp.com/tripplan?customerId=4480&metroId=1&tll=" + latitude + "_" + longitude));
				}
				if (intent.resolveActivity(getPackageManager()) != null) {
					this.startActivity(intent);
				} else {
					Toast.makeText(this, getString(R.string.no_navigation_activity), Toast.LENGTH_LONG).show();
				}
				return true;
			}
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
			final PlayServicesInstallation.CheckResult checkResult = PlayServicesInstallation.checkPlayServicesExist(this, true);
			if (checkResult.isSuccess()) {
				mapFragment.setVisibility(View.VISIBLE);
				noMapLayout.setVisibility(View.GONE);

				// Try to obtain the map from the SupportMapFragment.
				((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
			} else {
				// This will be displayed if the user cancelled or postponed installation when the app first loaded
				// and also if play services was not found due to some other error. In the latter case we might want
				// to show a different error, but I don't know what the cause might be.
				mapFragment.setVisibility(View.GONE);
				noMapLayout.setVisibility(View.VISIBLE);
				installPlayServicesButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						PlayServicesInstallation.resolvePlayServicesError(ArrivalMethodsActivity.this, checkResult);
					}
				});
			}
		}
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		mMap = googleMap;

		// Check if we were successful in obtaining the map.
		if (mMap != null) {
			setUpMap();
			// Clear the background to prevent overdraw after the map finished loading
			mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
				@Override
				public void onMapLoaded() {
					arrivalMethodsRoot.setBackground(null);
				}
			});
		}
	}

	/**
	 * This is where we can add markers or lines, add listeners or move the camera. In this case, we
	 * just add a marker near Africa.
	 * <p/>
	 * This should only be called once and when we are sure that {@link #mMap} is not null.
	 */
	private void setUpMap() {
		LatLng conventionLocation = new LatLng(Convention.getInstance().getLatitude(), Convention.getInstance().getLongitude());

		mMap.addMarker(new MarkerOptions()
				.position(conventionLocation)
				.title(getResources().getString(R.string.arrival_methods_marker_name, Convention.getInstance().getDisplayName())));

		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(conventionLocation, 16));
	}
}
