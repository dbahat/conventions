package amai.org.conventions.navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import sff.org.conventions.R;

class NavigationItemsAdapter extends BaseAdapter {
	private NavigationActivity currentActivity;
	private final List<NavigationItem> items;

	public NavigationItemsAdapter(NavigationActivity currentActivity, List<NavigationItem> items) {
		this.currentActivity = currentActivity;
		this.items = items;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final NavigationItemViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.navigation_menu_item, parent, false);
			holder = new NavigationItemViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (NavigationItemViewHolder) convertView.getTag();
		}

		holder.setData(items.get(position), currentActivity);
		return convertView;
	}
}
