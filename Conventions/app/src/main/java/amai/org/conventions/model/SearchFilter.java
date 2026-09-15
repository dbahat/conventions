package amai.org.conventions.model;

import java.io.Serializable;

import androidx.annotation.StringRes;
import sff.org.conventions.R;

public class SearchFilter<T extends SearchFilter.SearchFilterType> implements Serializable {
    private String name;
    private boolean active;
    private boolean displayActiveAsChecked;
    private T type;

    public SearchFilter<T> withActive(boolean active) {
        this.active = active;
        return this;
    }

    public SearchFilter<T> withDisplayActiveAsChecked(boolean displayActiveAsChecked) {
        this.displayActiveAsChecked = displayActiveAsChecked;
        return this;
    }

    public SearchFilter<T> withName(String name) {
        this.name = name;
        return this;
    }

    public SearchFilter<T> withType(T type) {
        this.type = type;
        return this;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isDisplayActiveAsChecked() {
        return displayActiveAsChecked;
    }

    public T getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        // We can't check the generic parameter at runtime, assuming it's the same
        // but we will return false if not in any case
        SearchFilter<T> filter = (SearchFilter<T>) o;

        if (!name.equals(filter.name)) return false;
        return type == filter.type;

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }

    public interface SearchFilterType {
        int ordinal();
        int getDescriptionStringId();
    }

    public enum EventSearchFilterType implements SearchFilterType {
        Tickets,
        EventLocationType,
        EventType,
        Category,
        Tag;

        @StringRes
        public int getDescriptionStringId() {
            switch (this) {
                case Tickets:
                    return R.string.tickets;
                case EventLocationType:
                    return R.string.search_filter_by_event_location_type;
                case EventType:
                    return R.string.search_filter_by_event_type;
                case Category:
                    return R.string.search_filter_by_category;
                case Tag:
                    return R.string.search_filter_by_tag;
            }

            throw new RuntimeException("missing description for search filter type " + this.toString());
        }
    }

    public enum StandSearchFilterType implements SearchFilterType {
        General,
        Type,
        Tag;

        @StringRes
        public int getDescriptionStringId() {
            switch (this) {
                case General:
                    return R.string.search_filter_general_info;
                case Type:
                    return R.string.search_filter_by_stand_type;
                case Tag:
                    return R.string.search_filter_by_tag;
            }

            throw new RuntimeException("missing description for stand search filter type " + this.toString());
        }
    }
}
