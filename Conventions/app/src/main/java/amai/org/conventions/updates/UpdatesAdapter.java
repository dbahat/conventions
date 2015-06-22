package amai.org.conventions.updates;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.model.Update;

public class UpdatesAdapter extends RecyclerView.Adapter<UpdateViewHolder> {

    private List<UpdateViewModel> updates;

    public UpdatesAdapter() {
        updates = new ArrayList<>();
    }

    @Override
    public UpdateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.update_item, parent, false);
        return new UpdateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UpdateViewHolder holder, final int position) {
        final UpdateViewModel updateViewModel = updates.get(position);
        holder.setContent(updateViewModel);
        holder.setOnMoreInfoClickListener(new UpdateViewHolder.OnMoreInfoClickListener() {
            @Override
            public void onClicked() {
                updateViewModel.setCollapsed(false);

                // NOTE - Currently not invoking NotifyItemChanged(), since this seems to cause the recyclerView to update the item to the wrong size.
                // Updating all the dataset seems to cause it to draw the items in the correct size.
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return updates.size();
    }

    public void setUpdates(List<Update> updates) {
        List<UpdateViewModel> updateViewModels = new LinkedList<>();
        for (Update update : updates) {
            updateViewModels.add(new UpdateViewModel(update, true /* By default have all items collapsed */));
        }

        this.updates = updateViewModels;
        notifyDataSetChanged();
    }
}
