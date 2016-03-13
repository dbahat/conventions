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

/**
 * Handles resizing and caching of images which require resizing at runtime.
 */
public class ImageHandler {
    private static AssetsExternalFileResolver resolver = new AssetsExternalFileResolver();
    private static Map<Integer, SVG> loadedSVGFiles = new HashMap<>();
    private static Bitmap notificationLargeIcon;
    private static Drawable toolbarLogo;

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

    public static synchronized Bitmap getNotificationLargeIcon(Context context) {
        if (notificationLargeIcon != null) {
            return notificationLargeIcon;
        }

        // Resizing the notification icon, since we don't wish to include an image per screen density, and the notification area doesn't auto-resize
        // the large icon.
        notificationLargeIcon = resizeBitmap(
                ThemeAttributes.getDrawable(context, R.attr.notificationLargeIcon),
                context.getResources().getDimensionPixelSize(R.dimen.notification_large_icon_size));
        return notificationLargeIcon;
    }

    public static Drawable getToolbarLogo(Context context) {
        if (toolbarLogo != null) {
            return toolbarLogo;
        }

        // The scaling doesn't work properly for the toolbar icon (the width remains the original size)
        // so we have to resize it manually
        Bitmap bitmapResized = resizeBitmap(
		        ThemeAttributes.getDrawable(context, R.attr.toolbarLogo),
		        ThemeAttributes.getDimensionSize(context, R.attr.actionBarSize));
        toolbarLogo = new BitmapDrawable(context.getResources(), bitmapResized);
        return toolbarLogo;
    }

    public static void releaseCache() {
        // Release all collected images from the map
        loadedSVGFiles = new HashMap<>();
        notificationLargeIcon = null;
        toolbarLogo = null;
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
	    Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, false);
	    if (originalBitmap != scaledBitmap) {
	        originalBitmap.recycle();
	    }
	    return scaledBitmap;
    }
}
