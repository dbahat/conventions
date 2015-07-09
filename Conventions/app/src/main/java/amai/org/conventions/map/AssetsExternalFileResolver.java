package amai.org.conventions.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.util.Log;

import com.caverock.androidsvg.SVGExternalFileResolver;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

class AssetsExternalFileResolver extends SVGExternalFileResolver {
	private static final String TAG = AssetsExternalFileResolver.class.getCanonicalName();

	// Cache for created fonts
	private Map<String, Typeface> fonts = new HashMap<>();
	private Context context;

	public AssetsExternalFileResolver(Context context) {
		this.context = context;
	}

	@Override
	public Typeface resolveFont(String fontFamily, int fontWeight, String fontStyle) {
		try {
			if (fonts.containsKey(fontFamily)) {
				return fonts.get(fontFamily);
			}
			Typeface font = Typeface.createFromAsset(context.getAssets(), fontFamily.toLowerCase(Locale.ENGLISH) + ".ttf");
			fonts.put(fontFamily, font);
			return font;
		} catch (Exception e) {
			Log.e(TAG, "error when creating font " + fontFamily.toLowerCase(Locale.ENGLISH) + ".ttf" + " from assets: " + e.getMessage());
			// Cache the failure
			fonts.put(fontFamily, null);
			return null;
		}
	}

	@Override
	public Bitmap resolveImage(String filename) {
		try {
			InputStream istream = context.getAssets().open(filename);
			return BitmapFactory.decodeStream(istream);
		} catch (Exception e) {
			Log.e(TAG, "error when creating bitmap for " + filename + " from assets: " + e.getMessage());
			return null;
		}
	}
}
