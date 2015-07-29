package amai.org.conventions;

import android.content.Context;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

import java.util.HashMap;
import java.util.Map;

import amai.org.conventions.map.AssetsExternalFileResolver;

public class SVGFileLoader {
	private static AssetsExternalFileResolver resolver = new AssetsExternalFileResolver();
	private static Map<Integer, SVG> loadedSVGFiles = new HashMap<>();

	public static SVG loadSVG(Context context, int resource) {
		try {
			if (loadedSVGFiles.containsKey(resource)) {
				return loadedSVGFiles.get(resource);
			}

			SVG svg = SVG.getFromResource(context.getResources(), resource);
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
}
