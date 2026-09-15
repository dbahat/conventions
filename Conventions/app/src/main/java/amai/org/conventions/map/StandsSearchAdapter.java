package amai.org.conventions.map;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import amai.org.conventions.model.Stand;
import amai.org.conventions.model.conventions.Convention;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import sff.org.conventions.R;

public class StandsSearchAdapter extends RecyclerView.Adapter<StandSearchViewHolder> {
	private List<Stand> stands;
	private boolean showInactiveIndication;
	private StandSearchViewHolder.OnClickListener onClickListener;
	List<String> keywordsToHighlight;

	public StandsSearchAdapter(List<Stand> stands) {
		this.stands = stands;
		// We only show the inactive indication during the convention
		this.showInactiveIndication = Convention.getInstance().hasStarted() && !Convention.getInstance().hasEnded();
	}

	public void setStands(List<Stand> stands) {
		this.stands = stands;
		notifyDataSetChanged();
	}

	public void setKeywordsHighlighting(List<String> keywords) {
		keywordsToHighlight = keywords;
	}

	public List<Stand> getStands() {
		return stands;
	}

	@NonNull
	@Override
	public StandSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stand_search_view_holder, parent, false);
		return new StandSearchViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull StandSearchViewHolder holder, int position) {
		Stand stand = stands.get(position);
		holder.setStand(stand, showInactiveIndication, keywordsToHighlight, onClickListener);
	}

	@Override
	public int getItemCount() {
		return stands.size();
	}

	public void setOnClickListener(StandSearchViewHolder.OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}
}
