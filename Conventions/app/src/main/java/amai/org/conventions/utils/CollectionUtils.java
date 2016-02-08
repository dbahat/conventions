package amai.org.conventions.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CollectionUtils {
    public interface Predicate<T> {
        boolean where(T item);
    }

	public interface EqualityPredicate<T> {
		boolean equals(T lhs, T rhs);
	}

	public static <T> List<T> filter(List<T> list, Predicate<T> predicate) {
		return filter(list, predicate, new LinkedList<T>());
	}

    public static <T, K extends List<T>> K filter(List<T> list, Predicate<T> predicate, K newList) {
        for (T item : list) {
            if (predicate.where(item)) {
                newList.add(item);
            }
        }
        return newList;
    }

	public static <T> T findFirst(List<T> list, Predicate<T> predicate) {
		for (T item : list) {
			if (predicate.where(item)) {
				return item;
			}
		}
		return null;
	}

	@SafeVarargs
	public static <T> ArrayList<T> flattenList(List<T>... instancesList) {
		int size = 0;
		for (List<T> list : instancesList) {
			size += list.size();
		}

		ArrayList<T> flattened = new ArrayList<>(size);
		for (List<T> list : instancesList) {
			flattened.addAll(list);
		}

		return flattened;
	}

	/**
	 * Return a list with unique values. Original list is assumed to be sorted for the sake of uniqueness.
	 */
	public static <T> List<T> unique(List<T> list, EqualityPredicate<T> comparator) {
		if (list.size() == 0) {
			return list;
		}

		boolean isFirst = true;
		T previousItem = list.get(0);
		List<T> newList = new LinkedList<>();
		newList.add(previousItem);

		for (T item : list) {
			if (isFirst) {
				isFirst = false;
				continue;
			}
			if (!comparator.equals(previousItem, item)) {
				newList.add(item);
				previousItem = item;
			}
		}
		return newList;
	}
}
