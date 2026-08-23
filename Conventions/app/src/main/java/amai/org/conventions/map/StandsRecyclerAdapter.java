package amai.org.conventions.map;

import android.content.res.Resources;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.events.adapters.SectionedRecyclerViewAdapter;
import amai.org.conventions.model.Stand;
import amai.org.conventions.model.StandType;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import sff.org.conventions.R;

public class StandsRecyclerAdapter extends SectionedRecyclerViewAdapter<Stand, StandType, StandViewHolder, StandsRecyclerAdapter.SectionViewHolder> {
    private final boolean showLocations;
    private List<Stand> stands;
    private boolean colorImages;
    private String selectedStandName;
    private OnClickListener onClickListener;

    public StandsRecyclerAdapter(List<Stand> stands, boolean colorImages, boolean showLocations, String selectedStandName) {
        super(stands);
        this.stands = stands;
        this.colorImages = colorImages;
        this.showLocations = showLocations;
        this.selectedStandName = selectedStandName;
    }

    public List<Stand> getStands() {
        return stands;
    }

    @NonNull
    @Override
    public StandViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stand_view_holder, parent, false);
        return new StandViewHolder(view, colorImages, showLocations);
    }

    @Override
    public void onBindViewHolder(@NonNull StandViewHolder holder, int position) {
        Stand stand = stands.get(position);
        holder.setStand(stand, selectedStandName != null && selectedStandName.equals(stand.getName()), null);
        holder.itemView.setOnClickListener(view -> onClickListener.onItemClicked(position));
        holder.itemView.setOnCreateContextMenuListener((ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) -> onClickListener.onItemContextMenu(position, menu));
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    protected StandType getSection(Stand item) {
        return item.getType();
    }

    @Override
    public SectionViewHolder onCreateSectionViewHolder(ViewGroup parent, int typeView) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new SectionViewHolder(view, android.R.id.text1);
    }

    @Override
    public void onBindSectionViewHolder(SectionViewHolder sectionViewHolder, StandType section) {
        Resources resources = sectionViewHolder.title.getResources();
        sectionViewHolder.title.setText(section.getName());
    }

    @Override
    public int getItemCount() {
        return stands.size();
    }

    public interface OnClickListener {
        void onItemClicked(int position);
        void onItemContextMenu(int position, ContextMenu menu);
    }

    public void setSelectedStandName(String selectedStandName) {
        this.selectedStandName = selectedStandName;
    }

    public static class SectionViewHolder extends RecyclerView.ViewHolder {
        public TextView title;

        public SectionViewHolder(View view, int mTextResourceid) {
            super(view);
            title = view.findViewById(mTextResourceid);
            title.setTextColor(ThemeAttributes.getColor(view.getContext(), R.attr.standsTypeTitleColor));
        }
    }
}
