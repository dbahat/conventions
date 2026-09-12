package amai.org.conventions.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import amai.org.conventions.utils.CollectionUtils;

public class NamedItems<T extends NamedItems.NamedItem> {
	public interface NamedItem {
		String getName();
	}

	private final List<T> items;
	private final Map<String, T> nameToItem;

	public NamedItems(List<T> items) {
		// The items list can be modified, and we don't know if the sent items list is mutable,
		// so we copy it to a new array list
		this.items = new ArrayList<>(items);
		this.nameToItem = new HashMap<>();
		for (T item : items) {
			nameToItem.put(item.getName().toLowerCase(), item);
		}
	}

	public List<T> getItems() {
		return items;
	}

	public T findByName(final String name) {
		return nameToItem.get(name.toLowerCase());
	}

	public T add(T item) {
		items.add(item);
		String key = item.getName().toLowerCase();
		if (!nameToItem.containsKey(key)) {
			nameToItem.put(key, item);
		}
		return item;
	}
}
