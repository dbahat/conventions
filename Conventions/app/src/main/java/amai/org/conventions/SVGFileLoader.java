package amai.org.conventions;

import android.content.Context;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

import java.util.HashMap;
import java.util.Map;

public class SVGFileLoader {
	private static Map<Integer, SVG> loadedSVGFiles = new HashMap<>();

	public static SVG loadSVG(Context context, int resource) throws SVGParseException {
		if (loadedSVGFiles.containsKey(resource)) {
			return loadedSVGFiles.get(resource);
		}

		SVG svg = SVG.getFromResource(context.getResources(), resource);
		svg.setDocumentHeight("100%");
		svg.setDocumentWidth("100%");
		loadedSVGFiles.put(resource, svg);
		return svg;
	}

	public static void releaseCache() {
		// Release all collected images from the map
		loadedSVGFiles = new HashMap<>();
	}
}
