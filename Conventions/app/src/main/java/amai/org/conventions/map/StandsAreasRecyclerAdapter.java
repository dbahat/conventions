package amai.org.conventions.map;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import amai.org.conventions.model.StandsArea;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import sff.org.conventions.R;

public class StandsAreasRecyclerAdapter extends RecyclerView.Adapter<StandsAreaViewHolder> {
    private List<StandsArea> standsAreas;
    private StandsAreaViewHolder.OnClickListener onClickListener;

    public StandsAreasRecyclerAdapter(List<StandsArea> standsAreas) {
        this.standsAreas = standsAreas;
    }

    public void setStandsAreas(List<StandsArea> standsAreas) {
        this.standsAreas = standsAreas;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StandsAreaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stands_area_view_holder, parent, false);
        return new StandsAreaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StandsAreaViewHolder holder, int position) {
        StandsArea standsArea = standsAreas.get(position);
        holder.setStandsArea(standsArea, onClickListener);
    }

    public void setOnClickListener(StandsAreaViewHolder.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public int getItemCount() {
        return standsAreas.size();
    }
}
