package amai.org.conventions.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import amai.org.conventions.R;

/**
 * Allows converting image ids (urls) into the relevant drawable.
 * Images are taken from local resources and not downloaded dynamically since they require re-scaling for mobile devices, and doing so
 * on the client side might result in out-of-memory exceptions.
 */
public class EventToImageResourceIdMapper {

    // Maps the event identifier (in our case, it's URI) to it's image resource id.
    private final Map<String, Integer> eventIdToImageResourceIdMap;

    public EventToImageResourceIdMapper() {
        eventIdToImageResourceIdMap = new HashMap<>();

        eventIdToImageResourceIdMap.put("http://2015.cami.org.il/wp-content/uploads/sites/4/2015/06/שרון-טורנר.jpg", R.drawable.event_history);
        eventIdToImageResourceIdMap.put("http://2015.cami.org.il/wp-content/uploads/sites/4/2015/06/אמנון-לוי.png", R.drawable.event_school);
        eventIdToImageResourceIdMap.put("http://2015.cami.org.il/wp-content/uploads/sites/4/2015/01/button-bg1.jpg", R.drawable.event_simon);
        eventIdToImageResourceIdMap.put("http://2015.cami.org.il/wp-content/uploads/sites/4/2015/07/שירה-אביגד-1024x775.png", R.drawable.event_song);
        eventIdToImageResourceIdMap.put("http://2015.cami.org.il/wp-content/uploads/sites/4/2015/01/דניאל.png", R.drawable.event_anime);
        eventIdToImageResourceIdMap.put("http://2015.cami.org.il/wp-content/uploads/sites/4/2015/06/Pok'mon_Trading_Card_Game-300x154.png", R.drawable.event_pokemon);
        eventIdToImageResourceIdMap.put("http://2015.cami.org.il/wp-content/uploads/sites/4/2015/07/reika-page-5.jpg", R.drawable.event_reika1);
        eventIdToImageResourceIdMap.put("http://2015.cami.org.il/wp-content/uploads/sites/4/2015/07/reika-page-4.jpg", R.drawable.event_reika2);
        eventIdToImageResourceIdMap.put("http://2015.cami.org.il/wp-content/uploads/sites/4/2015/07/reika-page-3.jpg", R.drawable.event_reika3);
        eventIdToImageResourceIdMap.put("http://2015.cami.org.il/wp-content/uploads/sites/4/2015/06/ניבה-קרן-אור-678x1024.jpg", R.drawable.event_bento);
        eventIdToImageResourceIdMap.put("http://2015.cami.org.il/wp-content/uploads/sites/4/2015/06/שירן-איבניצקי.gif", R.drawable.event_political_manga);
        eventIdToImageResourceIdMap.put("http://2015.cami.org.il/wp-content/uploads/sites/4/2015/07/קרן-לין.png", R.drawable.event_armor);
        eventIdToImageResourceIdMap.put("http://2015.cami.org.il/wp-content/uploads/sites/4/2015/07/שרית.jpg", R.drawable.event_sweets);
        eventIdToImageResourceIdMap.put("http://2015.cami.org.il/wp-content/uploads/sites/4/2015/07/דני-פייגלמן.jpg", R.drawable.event_gundam);
        eventIdToImageResourceIdMap.put("http://2015.cami.org.il/wp-content/uploads/sites/4/2015/07/ליעד-באטל-רויאל-683x1024.jpg", R.drawable.event_battle_royale);
        eventIdToImageResourceIdMap.put("http://2015.cami.org.il/wp-content/uploads/sites/4/2015/06/רומן-למפרט.jpg", R.drawable.event_visual_novels);
        eventIdToImageResourceIdMap.put("http://2015.cami.org.il/wp-content/uploads/sites/4/2015/07/יוני.png", R.drawable.event_kiritsugu);
        eventIdToImageResourceIdMap.put("http://2015.cami.org.il/wp-content/uploads/sites/4/2015/06/עומר-בן-יוסף.png", R.drawable.event_food);
        eventIdToImageResourceIdMap.put("http://2015.cami.org.il/wp-content/uploads/sites/4/2015/06/cosplay1-300x169.jpg", R.drawable.event_cosplay1);
        eventIdToImageResourceIdMap.put("http://2015.cami.org.il/wp-content/uploads/sites/4/2015/06/cosplay3-300x168.jpg", R.drawable.event_cosplay3);
        eventIdToImageResourceIdMap.put("http://2015.cami.org.il/wp-content/uploads/sites/4/2015/06/cosplay2-300x168.jpg", R.drawable.event_cosplay2);
        eventIdToImageResourceIdMap.put("http://2015.cami.org.il/wp-content/uploads/sites/4/2015/07/לין-כהן--816x1024.jpg", R.drawable.event_speech);
        eventIdToImageResourceIdMap.put("http://2015.cami.org.il/wp-content/uploads/sites/4/2015/06/רענן-פיקלר.jpg", R.drawable.event_idols);
        eventIdToImageResourceIdMap.put("http://2015.cami.org.il/wp-content/uploads/sites/4/2015/06/פאנל-יאוי-724x1024.jpg", R.drawable.event_yaoi);
        eventIdToImageResourceIdMap.put("http://2015.cami.org.il/wp-content/uploads/sites/4/2015/07/1502446_923383967713249_1082258373542128403_o-678x1024.jpg", R.drawable.event_eatable_cosplay);
        eventIdToImageResourceIdMap.put("http://2015.cami.org.il/wp-content/uploads/sites/4/2015/07/QgCpzHH-1024x741.jpg", R.drawable.event_trivia);

    }

    public List<Integer> getImageResourceIds(List<String> eventImageIds) {
        List<Integer> imageResourceIds = new LinkedList<>();
        for (String eventImageId : eventImageIds) {
            Integer resourceId = getImageResourceId(eventImageId);
            if (resourceId != null) {
                imageResourceIds.add(resourceId);
            }
        }

        return imageResourceIds;
    }

    private Integer getImageResourceId(String eventImageId) {
        return eventIdToImageResourceIdMap.containsKey(eventImageId)
                ? eventIdToImageResourceIdMap.get(eventImageId)
                : null;
    }
}
