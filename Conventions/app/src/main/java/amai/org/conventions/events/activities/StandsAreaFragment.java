package amai.org.conventions.events.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;

import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import sff.org.conventions.R;
import amai.org.conventions.map.StandsAdapter;
import amai.org.conventions.model.Stand;
import amai.org.conventions.model.StandsArea;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.utils.Objects;
import amai.org.conventions.utils.Views;
import pl.polidea.view.ZoomView;

public class StandsAreaFragment extends DialogFragment {
	public static final String ARGUMENT_STANDS_AREA_ID = "ArgumentStandsAreaID";
	public static final String ARGUMENT_STAND_NAME = "ArgumentStandsID";
	private int standsAreaID = -1;
	private String selectedStandName;
	private StandsArea area;

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

		final StickyGridHeadersGridView standsList = (StickyGridHeadersGridView) view.findViewById(R.id.standsList);
		standsList.setAreHeadersSticky(false);
		area = Convention.getInstance().findStandsArea(standsAreaID);
		if (area != null) {
			AdapterView.OnItemClickListener listener = null;
			final ZoomView zoom = (ZoomView) view.findViewById(R.id.stands_area_zoom);
			final ImageView image = (ImageView) view.findViewById(R.id.stands_area_map);

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

			final StandsAdapter adapter = new StandsAdapter(stands, true, area.hasImageResource(), selectedStandName);
			standsList.setAdapter(adapter);

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
				listener = new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						Object item = adapter.getItem(position);
						if (item instanceof Stand && ((Stand) item).hasImageCoordinates()) {
							Stand stand = (Stand) item;
							zoom.smoothZoomTo(zoom.getMaxZoom(),
									stand.getImageX() / area.getImageWidth() * image.getWidth(),
									stand.getImageY() / area.getImageHeight() * image.getHeight());
						}
					}
				};
			}

			standsList.setOnItemClickListener(listener);
			if (selectedStandName != null) {
				int foundPosition = -1;
				int currPosition = 0;
				for (Stand stand : stands) {
					if (selectedStandName.equals(stand.getName())) {
						foundPosition = currPosition;
						break;
					}
					++currPosition;
				}
				if (foundPosition != -1) {
					standsList.smoothScrollToPositionFromTop(foundPosition, 0);
				}
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
			@SuppressLint("InflateParams")
			final View view = LayoutInflater.from(getActivity()).inflate(R.layout.image_zoom, null);
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
