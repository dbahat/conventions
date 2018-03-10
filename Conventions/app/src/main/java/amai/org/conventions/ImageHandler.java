package amai.org.conventions;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

import java.util.HashMap;
import java.util.Map;

import amai.org.conventions.map.AssetsExternalFileResolver;
import sff.org.conventions.R;

/**
 * Handles resizing and caching of images which require resizing at runtime.
 */
public class ImageHandler {
	private static final AssetsExternalFileResolver resolver = new AssetsExternalFileResolver();
	private static Map<Integer, SVG> loadedSVGFiles = new HashMap<>();

	public static SVG loadSVG(Context context, int resource) {
		try {
			SVG svg = loadedSVGFiles.get(resource);
			if (svg != null) {
				return svg;
			}

			svg = SVG.getFromResource(context.getResources(), resource);
			resolver.setContext(context);
			setSVGProperties(svg);
			loadedSVGFiles.put(resource, svg);
			return svg;
		} catch (SVGParseException e) {
			throw new RuntimeException(e);
		}
	}

	public static void releaseCache() {
		// Release all collected images from the map
		loadedSVGFiles = new HashMap<>();
	}

	private static void setSVGProperties(SVG svg) throws SVGParseException {
		svg.setDocumentHeight("100%");
		svg.setDocumentWidth("100%");
		svg.registerExternalFileResolver(resolver);
	}

	/**
	 * Resize bitmap height to specified pixels while keeping the aspect ratio
	 */
	private static Bitmap resizeBitmap(Drawable image, int height) {
		Bitmap originalBitmap = ((BitmapDrawable) image).getBitmap();
		int width = (int) (originalBitmap.getWidth() * height / (float) originalBitmap.getHeight());
		// Note: we don't recycle the original drawable because it's inflated against the theme and
		// cached. This causes the next call to re-use the same drawable although it's already recycled.
		return Bitmap.createScaledBitmap(originalBitmap, width, height, false);
	}
}
