package amai.org.conventions.model;

import java.util.HashMap;
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

	// Maps the event identifier (in our case, its URI) to its image resource id.
    private final Map<String, Integer> eventIdToImageResourceIdMap;

    public EventToImageResourceIdMapper() {
        eventIdToImageResourceIdMap = new HashMap<>();
    }

	public void addMapping(String id, int resource) {
		eventIdToImageResourceIdMap.put(id, resource);
	}

	public List<Integer> getImageResourcesList(List<String> eventImageIds) {
		List<String> existingImages = CollectionUtils.filter(eventImageIds, new CollectionUtils.Predicate<String>() {
			@Override
			public boolean where(String eventImageId) {
				return eventIdToImageResourceIdMap.containsKey(eventImageId);
			}
		});

		// In case some events came up without any images at all, add a generic image to them.
		if (existingImages.size() == 0) {
			existingImages.add(EVENT_GENERIC);
		}

		List<Integer> result = CollectionUtils.map(existingImages, new CollectionUtils.Mapper<String, Integer>() {
			@Override
			public Integer map(String item) {
				return getImageResourceId(item);
			}
		});

		return result;
	}

    public int getImageResourceId(String eventImageId) {
	    if (eventIdToImageResourceIdMap.containsKey(eventImageId)) {
		    return eventIdToImageResourceIdMap.get(eventImageId);
	    } else {
		    throw new RuntimeException("Image id not found: " + eventImageId);
	    }
    }
}
