package amai.org.conventions.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CollectionUtils {
    public interface Predicate<T> {
        boolean where(T item);
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
}
