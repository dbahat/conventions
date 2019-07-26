package amai.org.conventions.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CollectionUtils {
	public interface Predicate<T> {
		boolean where(T item);
	}

	public interface EqualityPredicate<T> {
		boolean equals(T lhs, T rhs);
	}

	public interface Mapper<T, K> {
		K map(T item);
	}

	public interface MapperToInt<T> {
		int map(T item);
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

	public static <T, K> List<K> map(List<T> list, Mapper<T, K> mapper) {
		List<K> mapped = new LinkedList<>();
		for (T item : list) {
			mapped.add(mapper.map(item));
		}
		return mapped;
	}

	public static <T> List<T> flatMap(List<List<T>> lists) {
		List<T> flatList = new LinkedList<>();
		for (List<T> list : lists) {
			flatList.addAll(list);
		}
		return flatList;
	}

	public static <T> int[] mapToInt(List<T> list, MapperToInt<T> mapperToInt) {
		int[] result = new int[list.size()];
		int i = 0;
		for (T item : list) {
			result[i] = mapperToInt.map(item);
			++i;
		}
		return result;
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

	/**
	 * Returns whether all members of the list are either null or of the specific type
	 */
	public static boolean contains(List<?> list, Class<?> type) {
		for (Object member : list) {
			if (member != null && !(type.isInstance(member))) {
				return false;
			}
		}
		return true;
	}

	public static <AggKey, AggVal, T> Map<AggKey, AggVal> groupBy(List<T> list, Mapper<T, AggKey> mapper, Aggregator<T, AggVal> aggregator) {
		Map<AggKey, AggVal> map = new HashMap<>();
		for (T item : list) {
			AggKey key = mapper.map(item);
			if (map.containsKey(key)) {
				AggVal currentValue = map.get(key);
				map.put(key, aggregator.aggregate(currentValue, item));
			} else {
				map.put(key, aggregator.aggregate(null, item));
			}
		}

		return map;
	}


	public static interface Aggregator<L, M> {
		M aggregate(M accumulate, L currentItem);
	}
}
