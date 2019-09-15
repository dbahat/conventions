package amai.org.conventions.events.adapters;

import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import amai.org.conventions.events.activities.SectionedGridRecyclerViewAdapterWrapper;
import amai.org.conventions.utils.CollectionUtils;
import androidx.recyclerview.widget.RecyclerView;

public abstract class SectionedRecyclerViewAdapter<T, S, IVH extends RecyclerView.ViewHolder, SVH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<IVH> {

	private List<T> items;
	private List<SectionedGridRecyclerViewAdapterWrapper.Section<S>> sections;

	public SectionedRecyclerViewAdapter(List<T> items) {
		setItemsAndSections(items);
	}
	
	protected abstract S getSection(T item);

	private void setItemsAndSections(List<T> items) {
		Map<S, List<T>> sectionToItemList = CollectionUtils.groupBy(
				items,
				this::getSection,
				(accumulate, currentItem) -> {
					if (accumulate == null) {
						List<T> itemsList = new ArrayList<>();
						itemsList.add(currentItem);
						return itemsList;
					} else {
						accumulate.add(currentItem);
						return accumulate;
					}
				}
		);
		List<Map.Entry<S, List<T>>> sectionToItemElements = new ArrayList<>(sectionToItemList.entrySet());

		List<SectionedGridRecyclerViewAdapterWrapper.Section<S>> sections = new ArrayList<>();
		int indexInList = 0;
		for (Map.Entry<S, List<T>> entry : sectionToItemElements) {
			sections.add(new SectionedGridRecyclerViewAdapterWrapper.Section<>(
					indexInList,
					entry.getKey())
			);
			indexInList += entry.getValue().size();
		}

		List<List<T>> itemsAfterGrouping = CollectionUtils.map(sectionToItemElements, Map.Entry::getValue);
		this.items = CollectionUtils.flatMap(itemsAfterGrouping);
		this.sections = sections;

	}

	public List<SectionedGridRecyclerViewAdapterWrapper.Section<S>> getSections() {
		return sections;
	}

	public abstract SVH onCreateSectionViewHolder(ViewGroup parent, int typeView);

	public abstract void onBindSectionViewHolder(SVH sectionViewHolder, S section);

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemCount() {
		return items.size();
	}

	public List<T> getItems() {
		return items;
	}
}
