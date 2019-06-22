package amai.org.conventions.model;

import androidx.annotation.StringRes;

import java.io.Serializable;

import amai.org.conventions.R;

public class SearchFilter implements Serializable {
    private String name;
    private boolean active;
    private Type type;

    public SearchFilter withActive(boolean active) {
        this.active = active;
        return this;
    }

    public SearchFilter withName(String name) {
        this.name = name;
        return this;
    }

    public SearchFilter withType(Type type) {
        this.type = type;
        return this;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }

    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SearchFilter filter = (SearchFilter) o;

        if (!name.equals(filter.name)) return false;
        return type == filter.type;

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }


    public enum Type {
        Tickets,
        EventType,
        Category,
        Tag;

        @StringRes
        public int getDescriptionStringId() {
            switch (this) {
                case Tickets:
                    return R.string.tickets;
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
}
