package amai.org.conventions.secondhand;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.model.SecondHandItem;
import sff.org.conventions.R;

class SecondHandItemViewHolder extends RecyclerView.ViewHolder {
	private SecondHandItem item;
	private final TextView itemIdView;
	private final TextView itemNameView;
	private final TextView itemStatusView;

	public SecondHandItemViewHolder(View itemView) {
		super(itemView);
		itemIdView = itemView.findViewById(R.id.second_hand_item_id);
		itemNameView = itemView.findViewById(R.id.second_hand_item_name);
		itemStatusView = itemView.findViewById(R.id.second_hand_item_status);
	}

	public void setItem(SecondHandItem item, boolean isFormClosed) {
		this.item = item;
		itemIdView.setText(item.getId());
		String itemName = item.getDescription();
		if (itemName == null) {
			itemName = item.getUserDescription();
		}
		if (itemName == null) {
			itemName = itemView.getContext().getString(R.string.second_hand_item_title, item.getNumber());
		}
		if (item.getType() != null && item.getType().length() > 0) {
			itemName += " (" + item.getType() + ")";
		}
		this.itemNameView.setText(itemName);
		int statusColor;
		int titleColor = ThemeAttributes.getColor(itemView.getContext(), R.attr.secondHandFormOpenColor);
		switch (item.getStatus()) {
			case SOLD:
				itemStatusView.setText(R.string.second_hand_item_sold);
				statusColor = ThemeAttributes.getColor(itemView.getContext(), R.attr.secondHandItemSoldColor);
				break;
			case MISSING:
				itemStatusView.setText(R.string.second_hand_item_missing);
				statusColor = ThemeAttributes.getColor(itemView.getContext(), R.attr.secondHandItemMissingColor);
				break;
			default:
				itemStatusView.setText(R.string.second_hand_item_not_sold);
				statusColor = ThemeAttributes.getColor(itemView.getContext(), R.attr.secondHandItemNotSoldColor);
				break;
		}
		if (isFormClosed) {
			titleColor = ThemeAttributes.getColor(itemView.getContext(), R.attr.secondHandFormClosedColor);
			statusColor = ThemeAttributes.getColor(itemView.getContext(), R.attr.secondHandItemDefaultColor);;
		}
		itemNameView.setTextColor(titleColor);
		itemStatusView.setTextColor(statusColor);
	}
}
