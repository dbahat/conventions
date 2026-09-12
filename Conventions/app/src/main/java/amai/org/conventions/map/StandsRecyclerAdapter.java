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

public class StandsRecyclerAdapter extends RecyclerView.Adapter<StandViewHolder> {
    private final boolean showLocations;
    private List<Stand> stands;
    private String selectedStandName;
    private boolean showInactiveIndication;
    private StandViewHolder.OnClickListener onClickListener;

    public StandsRecyclerAdapter(List<Stand> stands, boolean showLocations, String selectedStandName) {
        this.stands = stands;
        this.showLocations = showLocations;
        this.selectedStandName = selectedStandName;
        // We only show the inactive indication during the convention
        this.showInactiveIndication = Convention.getInstance().hasStarted() && !Convention.getInstance().hasEnded();
    }

    public List<Stand> getStands() {
        return stands;
    }

    @NonNull
    @Override
    public StandViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stand_view_holder, parent, false);
        return new StandViewHolder(view, showLocations);
    }

    @Override
    public void onBindViewHolder(@NonNull StandViewHolder holder, int position) {
        Stand stand = stands.get(position);
        holder.setStand(stand, selectedStandName != null && selectedStandName.equals(stand.getName()), showInactiveIndication, onClickListener);
    }

    public void setOnClickListener(StandViewHolder.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public int getItemCount() {
        return stands.size();
    }

    public void setSelectedStandName(String selectedStandName) {
        this.selectedStandName = selectedStandName;
    }
}
