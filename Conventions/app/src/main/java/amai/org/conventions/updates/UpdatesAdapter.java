package amai.org.conventions.updates;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.model.Update;

public class UpdatesAdapter extends RecyclerView.Adapter<UpdateViewHolder> {

    private List<Update> updates;

    public UpdatesAdapter(List<Update> updates) {
        this.updates = updates;
    }

    @Override
    public UpdateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.update_item, parent, false);
        return new UpdateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UpdateViewHolder holder, int position) {
        holder.setContent(updates.get(position));
    }

    @Override
    public int getItemCount() {
        return updates.size();
    }
}
