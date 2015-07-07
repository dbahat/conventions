package amai.org.conventions.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import amai.org.conventions.R;

public class EventToImageResourceIdMapper {

    private final Map<Integer, List<Integer>> eventIdToImageResourceIdMap;

    public EventToImageResourceIdMapper() {
        eventIdToImageResourceIdMap = new HashMap<>();
        eventIdToImageResourceIdMap.put(643, withImages(R.drawable.event_ntt));
        eventIdToImageResourceIdMap.put(666, withImages(R.drawable.event_cosplay1));
        eventIdToImageResourceIdMap.put(622, withImages(R.drawable.event_cosplay1, R.drawable.event_cosplay2, R.drawable.event_cosplay3));
        eventIdToImageResourceIdMap.put(657, withImages(R.drawable.event_reika3));
        eventIdToImageResourceIdMap.put(527, withImages(R.drawable.event_reika2));
        eventIdToImageResourceIdMap.put(658, withImages(R.drawable.event_political_manga));
        eventIdToImageResourceIdMap.put(661, withImages(R.drawable.event_idols));
        eventIdToImageResourceIdMap.put(660, withImages(R.drawable.event_yaoi));
        eventIdToImageResourceIdMap.put(525, withImages(R.drawable.event_school));
        eventIdToImageResourceIdMap.put(528, withImages(R.drawable.event_reika1, R.drawable.event_reika2, R.drawable.event_reika3));
        eventIdToImageResourceIdMap.put(523, withImages(R.drawable.event_school));
        eventIdToImageResourceIdMap.put(644, withImages(R.drawable.event_idols));
        eventIdToImageResourceIdMap.put(518, withImages(R.drawable.event_reika2));
        eventIdToImageResourceIdMap.put(526, withImages(R.drawable.event_school));
        eventIdToImageResourceIdMap.put(517, withImages(R.drawable.event_cosplay1));
        eventIdToImageResourceIdMap.put(647, withImages(R.drawable.event_visual_novels));
    }

    public List<Integer> getImageResourceIds(int serverEventId) {
        if (eventIdToImageResourceIdMap.containsKey(serverEventId)) {
            return eventIdToImageResourceIdMap.get(serverEventId);
        }
        return new LinkedList<>();
    }

    private List<Integer> withImages(Integer... images) {
        return Arrays.asList(images);
    }
}
