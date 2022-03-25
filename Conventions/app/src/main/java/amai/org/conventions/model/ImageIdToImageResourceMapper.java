package amai.org.conventions.model;

import androidx.annotation.DrawableRes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import amai.org.conventions.BuildConfig;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.Log;

/**
 * Allows converting image ids (urls) into the relevant drawable.
 * Images are taken from local resources and not downloaded dynamically since they require re-scaling for mobile devices, and doing so
 * on the client side might result in out-of-memory exceptions.
 */
public class ImageIdToImageResourceMapper {
	public static final String EVENT_GENERIC = "event_generic";
	private static final String TAG = ImageIdToImageResourceMapper.class.getCanonicalName();

	// Maps the image identifier to its image resource id.
	private final Map<String, Integer> imageIdToImageResourceIdMap;
	// Maps a logo image identifier to image resource id
	private final Map<String, Integer> imageIdToLogoImageResourceIdMap;
	private final Set<String> excludeIds;

	public ImageIdToImageResourceMapper() {
		imageIdToImageResourceIdMap = new HashMap<>();
		imageIdToLogoImageResourceIdMap = new HashMap<>();
		excludeIds = new HashSet<>();
	}

	public ImageIdToImageResourceMapper addMapping(String id, @DrawableRes int resource) {
		imageIdToImageResourceIdMap.put(id, resource);
		if (BuildConfig.DEBUG && excludeIds.contains(id)) {
			Log.e(TAG, "Image added to both excluded and mapped lists: " + id);
		}
		if (BuildConfig.DEBUG && imageIdToLogoImageResourceIdMap.containsKey(id)) {
			Log.e(TAG, "Logo image added to both logo and mapped lists: " + id);
		}
		return this;
	}

	public void addLogoMapping(String id, int resource) {
		imageIdToLogoImageResourceIdMap.put(id, resource);
		if (BuildConfig.DEBUG && excludeIds.contains(id)) {
			Log.e(TAG, "Logo image added to both excluded and logo lists: " + id);
		}
		if (BuildConfig.DEBUG && imageIdToImageResourceIdMap.containsKey(id)) {
			Log.e(TAG, "Logo image added to both logo and mapped lists: " + id);
		}
	}

	public void addExcludedIds(String... args) {
		excludeIds.addAll(Arrays.asList(args));
	}

	public void addExcludedId(String id) {
		excludeIds.add(id);
		if (BuildConfig.DEBUG && imageIdToImageResourceIdMap.containsKey(id)) {
			Log.e(TAG, "Image added to both excluded and mapped lists: " + id);
		}
		if (BuildConfig.DEBUG && imageIdToLogoImageResourceIdMap.containsKey(id)) {
			Log.e(TAG, "Image added to both excluded and logo lists: " + id);
		}
	}

	public List<Integer> getImageResourcesList(List<String> imageIds) {
		List<String> existingImages = CollectionUtils.filter(imageIds, new CollectionUtils.Predicate<String>() {
			@Override
			public boolean where(String imageId) {
				boolean imageExists = imageIdToImageResourceIdMap.containsKey(imageId) && !excludeIds.contains(imageId);
				if (BuildConfig.DEBUG && !imageExists && !excludeIds.contains(imageId) && !imageIdToLogoImageResourceIdMap.containsKey(imageId)) {
					Log.i(TAG, "Unknown image: " + imageId);
				}
				return imageExists;
			}
		});

		// In case some events came up without any images at all, add a generic image to them.
		if (existingImages.size() == 0 && imageIdToImageResourceIdMap.containsKey(EVENT_GENERIC)) {
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

	public List<Integer> getLogoResources(List<String> imageIds) {
		List<String> existingImages = CollectionUtils.filter(imageIds, new CollectionUtils.Predicate<String>() {
			@Override
			public boolean where(String imageId) {
				boolean imageExists = imageIdToLogoImageResourceIdMap.containsKey(imageId) && !excludeIds.contains(imageId);
				if (BuildConfig.DEBUG && !imageExists && !excludeIds.contains(imageId) && !imageIdToImageResourceIdMap.containsKey(imageId)) {
					Log.i(TAG, "Unknown image: " + imageId);
				}
				return imageExists;
			}
		});

		List<Integer> result = CollectionUtils.map(existingImages, new CollectionUtils.Mapper<String, Integer>() {
			@Override
			public Integer map(String item) {
				return getLogoImageResourceId(item);
			}
		});

		return result;
	}

	private int getImageResourceId(String imageId) {
		if (imageIdToImageResourceIdMap.containsKey(imageId)) {
			return imageIdToImageResourceIdMap.get(imageId);
		} else {
			throw new RuntimeException("Image id not found: " + imageId);
		}
	}

	private int getLogoImageResourceId(String imageId) {
		if (imageIdToLogoImageResourceIdMap.containsKey(imageId)) {
			return imageIdToLogoImageResourceIdMap.get(imageId);
		} else {
			throw new RuntimeException("Logo image id not found: " + imageId);
		}
	}
}
