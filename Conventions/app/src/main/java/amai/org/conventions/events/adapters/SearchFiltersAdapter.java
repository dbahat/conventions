package amai.org.conventions.events.adapters;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.events.activities.SectionedGridRecyclerViewAdapterWrapper;
import amai.org.conventions.model.SearchFilter;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.CompoundButtonCompat;
import androidx.recyclerview.widget.RecyclerView;
import sff.org.conventions.R;

public class SearchFiltersAdapter extends SectionedRecyclerViewAdapter<SearchFilter, SearchFilter.Type, SearchFiltersAdapter.ViewHolder, SearchFiltersAdapter.SectionViewHolder> {

	private List<SearchFilter> searchFilters;
	private OnFilterChangeListener onFilterChangeListener;
	private List<SectionedGridRecyclerViewAdapterWrapper.Section> sections;

	public SearchFiltersAdapter(List<SearchFilter> searchFilters) {
		super(searchFilters);
	}

	@Override
	protected SearchFilter.Type getSection(SearchFilter item) {
		return item.getType();
	}

	public void setOnFilterChangeListener(OnFilterChangeListener onFilterChangeListener) {
		this.onFilterChangeListener = onFilterChangeListener;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_filter_item, parent, false);
		return new ViewHolder(view, onFilterChangeListener);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		holder.bind(getItems().get(position));
	}

	@Override
	public SectionViewHolder onCreateSectionViewHolder(ViewGroup parent, int typeView) {
		final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_filter_header, parent, false);
		return new SectionViewHolder(view, R.id.search_filter_header_title);
	}

	@Override
	public void onBindSectionViewHolder(SectionViewHolder sectionViewHolder, SearchFilter.Type section) {
		Resources resources = sectionViewHolder.title.getResources();
		sectionViewHolder.title.setText(resources.getString(section.getDescriptionStringId()));
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {

		private TextView filterName;
		private CheckBox checkBox;
		private SearchFilter searchFilter;

		public ViewHolder(View itemView, final OnFilterChangeListener onFilterChangeListener) {
			super(itemView);

			filterName = (TextView) itemView.findViewById(R.id.search_filter_item_name);
			checkBox = itemView.findViewById(R.id.search_filter_item_checkbox);

			itemView.setOnClickListener(view -> {
				searchFilter.withActive(!searchFilter.isActive());
				checkBox.setChecked(!checkBox.isChecked());
				onFilterChangeListener.onFilterStateChanged(searchFilter);
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

	public static class SectionViewHolder extends RecyclerView.ViewHolder {
		public TextView title;

		public SectionViewHolder(View view, int mTextResourceid) {
			super(view);
			title = view.findViewById(mTextResourceid);
		}
	}

	public interface OnFilterChangeListener {
		void onFilterStateChanged(SearchFilter searchFilter);
	}
}
