package amai.org.conventions.secondhand;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

import java.util.List;

import amai.org.conventions.model.SecondHandBuy;
import amai.org.conventions.model.SecondHandItem;
import sff.org.conventions.R;

public class SecondHandSearchItemsAdapter extends BaseAdapter implements ListAdapter {
	private List<SecondHandItem> items;
	private SecondHandBuy secondHandBuy;

	public SecondHandSearchItemsAdapter(List<SecondHandItem> items, SecondHandBuy secondHandBuy) {
		this.secondHandBuy = secondHandBuy;
		setItems(items);
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public SecondHandItem getItem(int i) {
		return items.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final SecondHandItemSearchViewHolder holder;
		if (convertView == null) {
			View secondHandItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.second_hand_item_search, parent, false);
			holder = new SecondHandItemSearchViewHolder(secondHandItemView);
			convertView = secondHandItemView;
			convertView.setTag(holder);
		} else {
			holder = (SecondHandItemSearchViewHolder) convertView.getTag();
		}

		SecondHandItem item = items.get(position);
		holder.setItem(item, secondHandBuy.isFavorite(item));
		holder.setOnFavoriteButtonClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				SecondHandItem item = holder.getItem();
				if (secondHandBuy.isFavorite(item)) {
					secondHandBuy.removeFavoriteItem(item);
				} else {
					secondHandBuy.addFavoriteItem(item);
				}
				holder.setItem(item, secondHandBuy.isFavorite(item));
			}
		});
		return convertView;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	public void setItems(List<SecondHandItem> items) {
		this.items = items;
		notifyDataSetChanged();
	}

	public List<SecondHandItem> getItems() {
		return items;
	}
}
