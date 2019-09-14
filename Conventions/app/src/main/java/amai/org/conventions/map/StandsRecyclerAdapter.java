package amai.org.conventions.map;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import amai.org.conventions.R;
import amai.org.conventions.events.activities.SectionedGridRecyclerViewAdapter;
import amai.org.conventions.model.Stand;
import amai.org.conventions.utils.CollectionUtils;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StandsRecyclerAdapter extends RecyclerView.Adapter<StandViewHolder> {
    private final boolean showLocations;
    private List<Stand> stands;
    private List<SectionedGridRecyclerViewAdapter.Section> sections;
    private boolean colorImages;
    private String selectedStandName;
    private OnClickListener onClickListener;

    public StandsRecyclerAdapter(List<Stand> stands, boolean colorImages, boolean showLocations, String selectedStandName, Resources resources) {
        this.colorImages = colorImages;
        this.showLocations = showLocations;
        this.selectedStandName = selectedStandName;
        setStandsAndSections(stands, resources);
    }

    private void setStandsAndSections(List<Stand> stands, Resources resources) {
        Map<Stand.StandType, List<Stand>> standTypeToStandsMap = CollectionUtils.groupBy(
                stands,
                Stand::getType,
                (accumulate, currentItem) -> {
                    if (accumulate == null) {
                        List<Stand> standList = new ArrayList<Stand>();
                        standList.add(currentItem);
                        return standList;
                    } else {
                        accumulate.add(currentItem);
                        return accumulate;
                    }
                }
        );
        List<Map.Entry<Stand.StandType, List<Stand>>> standTypeToStandsElements = new ArrayList<>(standTypeToStandsMap.entrySet());

        List<SectionedGridRecyclerViewAdapter.Section> sections = new ArrayList<>();
        int indexInList = 0;
        for (Map.Entry<Stand.StandType, List<Stand>> entry : standTypeToStandsElements) {
            sections.add(new SectionedGridRecyclerViewAdapter.Section(
                    indexInList,
                    resources.getString(entry.getKey().getTitle())
            ));
            indexInList += entry.getValue().size();
        }

        List<List<Stand>> standsAfterGrouping = CollectionUtils.map(standTypeToStandsElements, Map.Entry::getValue);
        this.stands = CollectionUtils.flatMap(standsAfterGrouping);
        this.sections = sections;
    }

    public List<Stand> getStands() {
        return stands;
    }

    public List<SectionedGridRecyclerViewAdapter.Section> getSections() {
        return sections;
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
        holder.setStand(stand, selectedStandName != null && selectedStandName.equals(stand.getName()));
        holder.itemView.setOnClickListener(view -> onClickListener.onItemClicked(position));
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public int getItemCount() {
        return stands.size();
    }

    public interface OnClickListener {
        void onItemClicked(int position);
    }

    public void setSelectedStandName(String selectedStandName) {
        this.selectedStandName = selectedStandName;
    }
}
