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
import amai.org.conventions.utils.Objects;

public class UpdatesAdapter extends RecyclerView.Adapter<UpdateViewHolder> {

	public static final int UPDATE_NOT_FOUND = -1;
	private List<UpdateViewModel> updates;
	private String focusedUpdateId = null;

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

	public void setUpdates(List<Update> updatesToSet, int newItemsNumber) {
		int currentNumberOfUpdates = updates.size();
		List<UpdateViewModel> updateViewModels = new LinkedList<>();
		for (Update update : updatesToSet) {
			updateViewModels.add(new UpdateViewModel(update, true /* By default have all items collapsed */));
		}
		updates = updateViewModels;

		// Prevent inconsistencies when notifying about item change events - make sure
		// there are no too many or too less items inserted
		newItemsNumber = Math.min(newItemsNumber, updatesToSet.size());
		newItemsNumber = Math.max(newItemsNumber, updatesToSet.size() - currentNumberOfUpdates);

		// Only the newItemsNumber first items from updatesToSet are really inserted. The rest might have been changed.
		int position = 0;
		int changedItems = 0;
		for (Update update : updatesToSet) {
			if (update.isNew() && newItemsNumber > 0) {
				notifyItemInserted(position);
				--newItemsNumber;
			} else {
				notifyItemChanged(position);
				++changedItems;
			}
			++position;
		}
		// Items which were in the old updates list but weren't marked as changed were removed
		int removedItems = currentNumberOfUpdates - changedItems;
		if (removedItems > 0) {
			notifyItemRangeRemoved(position, removedItems);
		}

		markFocusedUpdate();
	}

	public int focusOn(String updateId) {
		unmarkFocusedUpdate();
		focusedUpdateId = updateId;
		return markFocusedUpdate();
	}

	private void unmarkFocusedUpdate() {
		if (focusedUpdateId == null) {
			return;
		}
		int position = 0;
		for (UpdateViewModel updateModel : updates) {
			if (Objects.equals(updateModel.getUpdate().getId(), focusedUpdateId)) {
				updateModel.setFocused(false);
				notifyItemChanged(position);
				break;
			}
			++position;
		}
	}

	private int markFocusedUpdate() {
		if (focusedUpdateId == null) {
			return UPDATE_NOT_FOUND;
		}
		int position = 0;
		for (UpdateViewModel updateModel : updates) {
			if (Objects.equals(updateModel.getUpdate().getId(), focusedUpdateId)) {
				updateModel.setCollapsed(false);
				updateModel.setFocused(true);
				notifyItemChanged(position);
				return position;
			}
			++position;
		}
		return UPDATE_NOT_FOUND;
	}
}
