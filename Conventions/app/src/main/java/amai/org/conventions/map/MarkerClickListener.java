package amai.org.conventions.map;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGImageView;
import com.caverock.androidsvg.SVGParseException;

import java.util.List;
import java.util.Map;

import amai.org.conventions.R;
import amai.org.conventions.model.MapLocation;

class MarkerClickListener implements View.OnClickListener {
	private final Marker marker;
	private final List<Marker> floorMarkers;

	public MarkerClickListener(Marker marker, List<Marker> floorMarkers) {
		this.marker = marker;
		this.floorMarkers = floorMarkers;
	}

	@Override
	public void onClick(View v) {
		Toast.makeText(v.getContext(), marker.getLocation().getName(), Toast.LENGTH_SHORT).show();

		// Deselect all markers except the current
		boolean otherMarkerWasSelected = false;
		for (Marker currMarker : floorMarkers) {
			if (currMarker != marker) {
				if (currMarker.isSelected()) {
					otherMarkerWasSelected = true;
					currMarker.deselect();
				}
			}
		}

		// Change current marker state. Only deselect it if it was the only selected marker.
		if (marker.isSelected()) {
			if (!otherMarkerWasSelected) {
				marker.deselect();
			}
		} else {
			marker.select();
		}
	}
}
