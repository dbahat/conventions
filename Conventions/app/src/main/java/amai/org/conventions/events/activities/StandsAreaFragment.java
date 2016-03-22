package amai.org.conventions.events.activities;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.map.StandsAdapter;
import amai.org.conventions.model.Convention;
import amai.org.conventions.model.Stand;
import amai.org.conventions.model.StandsArea;

public class StandsAreaFragment extends DialogFragment {
	public static final String ARGUMENT_STANDS_AREA_ID = "ArgumentStandsAreaID";
	private int standsAreaID = -1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		standsAreaID = getArguments().getInt(ARGUMENT_STANDS_AREA_ID, -1);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

		View view = inflater.inflate(R.layout.activity_stands_area, container, false);

		StickyGridHeadersGridView standsList = (StickyGridHeadersGridView) view.findViewById(R.id.standsList);
		standsList.setAreHeadersSticky(false);
		StandsArea area = Convention.getInstance().findStandsArea(standsAreaID);
		if (area != null) {
			List<Stand> stands = new ArrayList<>(area.getStands());
			Collections.sort(stands, new Comparator<Stand>() {
				@Override
				public int compare(Stand lhs, Stand rhs) {
					return lhs.getType().compareTo(rhs.getType());
				}
			});

			standsList.setAdapter(new StandsAdapter(stands, true));
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
}
