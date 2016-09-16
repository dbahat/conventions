package amai.org.conventions.map;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import amai.org.conventions.model.EventType;
import amai.org.conventions.utils.CollectionUtils;

/**
 * Aggregates multiple event types into small subset of aggregated types
 */
public class AggregatedEventTypes {
    private List<AggregatedType> aggregatedEventTypes;

    public AggregatedEventTypes() {
        aggregatedEventTypes = Arrays.asList(
                new AggregatedType("הרצאות", Arrays.asList(
                        new EventType("הרצאה"),
                        new EventType("פאנל"))),
                new AggregatedType("משחקים", Arrays.asList(
                        new EventType("משחק שולחני"),
                        new EventType("משחק תפקידים חי"),
                        new EventType("מיוחד"),
                        new EventType("טורניר"))),
                new AggregatedType("הקרנות ומופעים", Arrays.asList(
                        new EventType("הקרנה"),
                        new EventType("מופע")))
        );
    }

	public List<AggregatedType> getAggregatedEventTypes() {
		return aggregatedEventTypes;
	}

	public String get(EventType eventType) {
        for (AggregatedType aggregatedType : aggregatedEventTypes) {
            if (aggregatedType.contains(eventType)) {
                return aggregatedType.getName();
            }
        }

        // If we couldn't find an aggregated type, return the description of the existing event
        return eventType.getDescription();
    }

    public List<EventType> get(final String aggregatedEventType) {
        AggregatedType aggregatedType = CollectionUtils.findFirst(aggregatedEventTypes, new CollectionUtils.Predicate<AggregatedType>() {
            @Override
            public boolean where(AggregatedType item) {
                return item.getName().equals(aggregatedEventType);
            }
        });

        if (aggregatedType != null) {
            return aggregatedType.getEventTypes();
        }

        return Collections.singletonList(new EventType(aggregatedEventType));
    }

    public List<EventType> get(List<String> aggregatedEventTypes) {
        List<EventType> eventTypes = new LinkedList<>();

        for (String searchCategory : aggregatedEventTypes) {
            eventTypes.addAll(get(searchCategory));
        }

        return eventTypes;
    }

    public static class AggregatedType {
        private List<EventType> eventTypes;
        private String name;

        public AggregatedType(String name, List<EventType> eventTypes) {
            this.name = name;
            this.eventTypes = eventTypes;
        }

        public boolean contains(EventType eventType) {
            return eventTypes.contains(eventType);
        }

        public String getName() {
            return name;
        }

        public List<EventType> getEventTypes() {
            return eventTypes;
        }
    }
}
