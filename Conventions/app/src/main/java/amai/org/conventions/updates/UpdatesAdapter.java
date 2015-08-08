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
    public void onBindViewHolder(final UpdateViewHolder holder, final int position) {
        UpdateViewModel updateViewModel = updates.get(position);
        holder.setContent(updateViewModel);
        holder.setOnMoreInfoClickListener(new UpdateViewHolder.OnMoreInfoClickListener() {
            @Override
            public void onClicked() {
	            int position = holder.getAdapterPosition();
	            updates.get(position).setCollapsed(false);
                notifyItemChanged(position);
            }
        });
    }

	@Override
    public int getItemCount() {
        return updates.size();
    }

    public void setUpdates(List<Update> newUpdates) {
        List<UpdateViewModel> updateViewModels = new LinkedList<>();
        for (Update update : newUpdates) {
            updateViewModels.add(new UpdateViewModel(update, true /* By default have all items collapsed */));
        }

	    // Only the sizeDiff first items are really inserted. The rest might have been changed.
	    int sizeDiff = newUpdates.size() - updates.size();

        updates = updateViewModels;

	    int position = 0;
	    for (Update update : newUpdates) {
		    if (update.isNew() && sizeDiff > 0) {
			    notifyItemInserted(position);
			    --sizeDiff;
		    } else {
			    notifyItemChanged(position);
		    }
		    ++position;
	    }
    }
}
