package amai.org.conventions.events.adapters;

import android.content.res.ColorStateList;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleAdapter;

import java.util.List;

import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.model.SearchFilter;
import sff.org.conventions.R;

public class SearchFiltersAdapter extends BaseAdapter implements StickyGridHeadersSimpleAdapter {

	private List<SearchFilter> searchFilters;
	private OnFilterChangeListener onFilterChangeListener;

	public SearchFiltersAdapter(List<SearchFilter> searchFilters) {
		this.searchFilters = searchFilters;
	}

	public void setAllFilters(boolean enabled) {
		for (SearchFilter filter : searchFilters) {
			filter.withActive(enabled);
		}
		notifyDataSetChanged();
	}

	public void setOnFilterChangeListener(OnFilterChangeListener onFilterChangeListener) {
		this.onFilterChangeListener = onFilterChangeListener;
	}

	@Override
	public long getHeaderId(int position) {
		return searchFilters.get(position).getType().ordinal();
	}

	@Override
	public View getHeaderView(int position, View convertView, ViewGroup parent) {
		HeaderViewHolder viewHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_filter_header, parent, false);
			viewHolder = new HeaderViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (HeaderViewHolder) convertView.getTag();
		}
		viewHolder.bind(searchFilters.get(position).getType());

		return convertView;
	}

	@Override
	public int getCount() {
		return searchFilters.size();
	}

	@Override
	public Object getItem(int position) {
		return searchFilters.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_filter_item, parent, false);
			viewHolder = new ViewHolder(convertView, onFilterChangeListener);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.bind(searchFilters.get(position));
		return convertView;
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {

		private TextView filterName;
		private CheckBox checkBox;
		private SearchFilter searchFilter;

		public ViewHolder(View itemView, final OnFilterChangeListener onFilterChangeListener) {
			super(itemView);

			filterName = (TextView) itemView.findViewById(R.id.search_filter_item_name);
			checkBox = (CheckBox) itemView.findViewById(R.id.search_filter_item_checkbox);

			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					searchFilter.withActive(!searchFilter.isActive());
					checkBox.setChecked(!checkBox.isChecked());
					onFilterChangeListener.onFilterStateChanged(searchFilter);
				}
			});

			ColorStateList checkboxColors = new ColorStateList(
					new int[][]{
							new int[]{-android.R.attr.state_checked},
							new int[]{android.R.attr.state_checked}
					},
					new int[]{
							ContextCompat.getColor(itemView.getContext(), R.color.even_darker_gray),
							ThemeAttributes.getColor(itemView.getContext(), R.attr.colorAccent)
					});
			CompoundButtonCompat.setButtonTintList(this.checkBox, checkboxColors);
		}

		public void bind(SearchFilter searchFilter) {
			this.searchFilter = searchFilter;
			filterName.setText(searchFilter.getName());
			checkBox.setChecked(!searchFilter.isActive());
		}
	}

	public static class HeaderViewHolder extends RecyclerView.ViewHolder {

		private TextView title;

		public HeaderViewHolder(View itemView) {
			super(itemView);

			title = (TextView) itemView.findViewById(R.id.search_filter_header_title);
		}

		public void bind(final SearchFilter.Type searchFilterType) {
			title.setText(searchFilterType.getDescriptionStringId());
		}
	}

	public interface OnFilterChangeListener {
		void onFilterStateChanged(SearchFilter searchFilter);
	}
}
