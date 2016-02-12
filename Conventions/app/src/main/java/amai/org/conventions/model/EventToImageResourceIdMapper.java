package amai.org.conventions.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import amai.org.conventions.utils.CollectionUtils;

/**
 * Allows converting image ids (urls) into the relevant drawable.
 * Images are taken from local resources and not downloaded dynamically since they require re-scaling for mobile devices, and doing so
 * on the client side might result in out-of-memory exceptions.
 */
public class EventToImageResourceIdMapper {
	public static final String EVENT_GENERIC = "event_generic";

	// Maps the event identifier (in our case, it's URI) to its image resource id.
    private final Map<String, Integer> eventIdToImageResourceIdMap;

    public EventToImageResourceIdMapper() {
        eventIdToImageResourceIdMap = new HashMap<>();
    }

	public void addMapping(String id, int resource) {
		eventIdToImageResourceIdMap.put(id, resource);
	}

	public List<String> getImagesList(List<String> eventImageIds) {
		return CollectionUtils.filter(eventImageIds, new CollectionUtils.Predicate<String>() {
			@Override
			public boolean where(String item) {
				return getImageResourceId(item) != null;
			}
		}, new LinkedList<String>());
	}

    public Integer getImageResourceId(String eventImageId) {
        return eventIdToImageResourceIdMap.containsKey(eventImageId)
                ? eventIdToImageResourceIdMap.get(eventImageId)
                : null;
    }
}
