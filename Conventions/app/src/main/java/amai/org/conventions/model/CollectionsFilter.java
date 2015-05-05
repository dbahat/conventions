package amai.org.conventions.model;

import java.util.List;

/**
 * Created by David on 05/05/2015.
 */
public class CollectionsFilter {
    public static interface Predicate<T> {
        boolean where(T item);
    }

    public static <T, K extends List<T>> K filter(List<T> list, Predicate<T> predicate, K newList) {
        for (T item : list) {
            if (predicate.where(item)) {
                newList.add(item);
            }
        }
        return newList;
    }
}
