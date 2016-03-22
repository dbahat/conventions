package amai.org.conventions.map;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleAdapter;

import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.model.Stand;

public class StandsAdapter extends BaseAdapter implements StickyGridHeadersSimpleAdapter {
	private List<Stand> stands;
	private boolean colorImages;

	public StandsAdapter(List<Stand> stands, boolean colorImages) {
		this.stands = stands;
		this.colorImages = colorImages;
	}

	public void setStands(List<Stand> stands) {
		this.stands = stands;
	}

	public List<Stand> getStands() {
		return stands;
	}

	@Override
	public int getCount() {
		return stands.size();
	}

	@Override
	public Object getItem(int position) {
		return stands.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final StandViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.stand_view_holder, parent, false);
			holder = new StandViewHolder(convertView, colorImages);
			convertView.setTag(holder);
		} else {
			holder = (StandViewHolder) convertView.getTag();
		}

		Stand stand = stands.get(position);
		holder.setStand(stand);
		return convertView;
	}

	@Override
	public long getHeaderId(int position) {
		return stands.get(position).getType().ordinal();
	}

	@Override
	public View getHeaderView(int position, View convertView, ViewGroup parent) {
		final HeaderViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
			holder = new HeaderViewHolder();
			holder.textView = (TextView) convertView.findViewById(android.R.id.text1);
			holder.textView.setTextColor(ThemeAttributes.getColor(parent.getContext(), R.attr.navigationPopupSelectedColor));
			convertView.setTag(holder);
		} else {
			holder = (HeaderViewHolder) convertView.getTag();
		}

		Stand stand = stands.get(position);
		holder.textView.setText(stand.getType().getTitle());
		return convertView;
	}

	protected class HeaderViewHolder {
		public TextView textView;
	}

}
