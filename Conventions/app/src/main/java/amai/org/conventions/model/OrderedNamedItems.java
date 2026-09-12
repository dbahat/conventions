package amai.org.conventions.model;

import java.util.List;

public class OrderedNamedItems<T extends OrderedNamedItems.OrderedNamedItem> extends NamedItems<T> {
	public interface OrderedNamedItem extends NamedItems.NamedItem {
		int getOrder();
		void setOrder(int order);
	}

	public OrderedNamedItems(List<T> items) {
		super(items);
		int i = 1;
		for (OrderedNamedItem item : getItems()) {
			item.setOrder(i);
			++i;
		}
	}

	public T add(T item) {
		item.setOrder(getHighestItemOrder() + 1);
		return super.add(item);
	}

	private int getHighestItemOrder() {
		int maxItemOrder = -1;
		for (T item : getItems()) {
			maxItemOrder = Math.max(maxItemOrder, item.getOrder());
		}

		return maxItemOrder;
	}
}
