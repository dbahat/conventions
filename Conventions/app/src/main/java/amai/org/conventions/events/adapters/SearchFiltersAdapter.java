package amai.org.conventions.events.adapters;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.events.activities.SectionedGridRecyclerViewAdapter;
import amai.org.conventions.model.SearchFilter;
import amai.org.conventions.utils.CollectionUtils;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.CompoundButtonCompat;
import androidx.recyclerview.widget.RecyclerView;
import sff.org.conventions.R;

public class SearchFiltersAdapter extends RecyclerView.Adapter<SearchFiltersAdapter.ViewHolder> {

	private List<SearchFilter> searchFilters;
	private OnFilterChangeListener onFilterChangeListener;
	private List<SectionedGridRecyclerViewAdapter.Section> sections;

	public SearchFiltersAdapter(List<SearchFilter> searchFilters, Resources resources) {
		setFiltersAndSections(searchFilters, resources);
	}

	private void setFiltersAndSections(List<SearchFilter> searchFilters, Resources resources) {
		Map<SearchFilter.Type, List<SearchFilter>> searchFilterTypeToSearchFilterList = CollectionUtils.groupBy(
				searchFilters,
				SearchFilter::getType,
				(accumulate, currentItem) -> {
					if (accumulate == null) {
						List<SearchFilter> searchFiltersList = new ArrayList<>();
						searchFiltersList.add(currentItem);
						return searchFiltersList;
					} else {
						accumulate.add(currentItem);
						return accumulate;
					}
				}
		);
		List<Map.Entry<SearchFilter.Type, List<SearchFilter>>> searchFilterTypeToSearchFilterElements = new ArrayList<>(searchFilterTypeToSearchFilterList.entrySet());

		List<SectionedGridRecyclerViewAdapter.Section> sections = new ArrayList<>();
		int indexInList = 0;
		for (Map.Entry<SearchFilter.Type, List<SearchFilter>> entry : searchFilterTypeToSearchFilterElements) {
			sections.add(new SectionedGridRecyclerViewAdapter.Section(
					indexInList,
					resources.getString(entry.getKey().getDescriptionStringId())
			));
			indexInList += entry.getValue().size();
		}

		List<List<SearchFilter>> searchFiltersAfterGrouping = CollectionUtils.map(searchFilterTypeToSearchFilterElements, Map.Entry::getValue);
		this.searchFilters = CollectionUtils.flatMap(searchFiltersAfterGrouping);
		this.sections = sections;

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

	public List<SectionedGridRecyclerViewAdapter.Section> getSections() {
		return sections;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_filter_item, parent, false);
		return new ViewHolder(view, onFilterChangeListener);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		holder.bind(searchFilters.get(position));
	}

	@Override
	public int getItemCount() {
		return searchFilters.size();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {

		private TextView filterName;
		private CheckBox checkBox;
		private SearchFilter searchFilter;

		public ViewHolder(View itemView, final OnFilterChangeListener onFilterChangeListener) {
			super(itemView);

			filterName = (TextView) itemView.findViewById(R.id.search_filter_item_name);
			checkBox = itemView.findViewById(R.id.search_filter_item_checkbox);

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
							ThemeAttributes.getColor(itemView.getContext(), R.attr.searchDrawerAccentColor)
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
