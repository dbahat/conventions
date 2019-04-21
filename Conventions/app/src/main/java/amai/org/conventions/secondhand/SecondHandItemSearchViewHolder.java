package amai.org.conventions.secondhand;

import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.model.SecondHandItem;
import amai.org.conventions.utils.Strings;
import sff.org.conventions.R;

class SecondHandItemSearchViewHolder extends RecyclerView.ViewHolder {
	private static final String TAG = SecondHandItemSearchViewHolder.class.getCanonicalName();
	private SecondHandItem item;
	private final TextView itemIdView;
	private final TextView itemNameView;
	private final TextView itemPriceView;
	private final ImageView favoriteIcon;

	public SecondHandItemSearchViewHolder(View itemView) {
		super(itemView);
		itemIdView = itemView.findViewById(R.id.second_hand_item_id);
		itemNameView = itemView.findViewById(R.id.second_hand_item_name);
		itemPriceView = itemView.findViewById(R.id.second_hand_item_price);
		favoriteIcon = itemView.findViewById(R.id.second_hand_item_favorite);
	}

	public void setOnFavoriteButtonClickListener(View.OnClickListener listener) {
		favoriteIcon.setOnClickListener(listener);
	}

	private void setFavorite(boolean isFavorite) {
		favoriteIcon.setImageDrawable(ThemeAttributes.getDrawable(itemView.getContext(), R.attr.eventFavoriteIcon));
		if (isFavorite) {
			favoriteIcon.setColorFilter(ThemeAttributes.getColor(itemView.getContext(), R.attr.eventFavoriteColor), PorterDuff.Mode.SRC_ATOP);
		} else {
			favoriteIcon.setColorFilter(ThemeAttributes.getColor(itemView.getContext(), R.attr.eventNonFavoriteColor), PorterDuff.Mode.SRC_ATOP);
		}
	}

	public void setItem(SecondHandItem newItem, boolean isFavorite) {
		this.item = newItem;
		String formId = newItem.getFormId();
		String itemId = itemView.getContext().getString(R.string.second_hand_item_id_format,
				Strings.padWithZeros(formId, 3), Strings.padWithZeros(newItem.getNumber(), 2));
		itemIdView.setText(itemId);
		refreshItemNameText();
		if (item.getStatus() == SecondHandItem.Status.UNKNOWN) {
			// Showing the status instead of the price
			itemPriceView.setVisibility(View.VISIBLE);
			itemPriceView.setText(R.string.second_hand_unknown_status);
			itemPriceView.setTextColor(ThemeAttributes.getColor(itemView.getContext(), R.attr.secondHandItemMissingColor));
		} else if (item.getPrice() == -1) {
			itemPriceView.setVisibility(View.GONE);
		} else {
			itemPriceView.setVisibility(View.VISIBLE);
			itemPriceView.setText(itemView.getContext().getString(R.string.second_hand_item_price, item.getPrice()));
			itemPriceView.setTextColor(ThemeAttributes.getColor(itemView.getContext(), R.attr.secondHandItemNotSoldColor));
		}
		int textColor = ThemeAttributes.getColor(itemView.getContext(), R.attr.secondHandFormOpenColor);
		itemNameView.setTextColor(textColor);
		itemPriceView.setTextColor(textColor);
		itemIdView.setTextColor(textColor);
		setFavorite(isFavorite);
	}

	private void refreshItemNameText() {
		String itemName = item.getDescription();
		if (itemName == null) {
			itemName = "";
		}
		if (item.getType() != null && item.getType().length() > 0) {
			itemName += " (" + item.getType() + ")";
		}
		this.itemNameView.setText(itemName);
	}

	public SecondHandItem getItem() {
		return item;
	}
}
