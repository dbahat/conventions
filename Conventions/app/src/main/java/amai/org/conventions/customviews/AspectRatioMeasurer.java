package amai.org.conventions.customviews;

import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;

public class AspectRatioMeasurer {
	private static final int WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT;

	public static Point measure(int requestedWidth, int requestedHeight, Drawable drawable, int measuredWidth, int measuredHeight) {
		// Not calculating according to image aspect ratio in the following cases:
		// 1. No image
		// 2. No width or height
		// 3. Both width and height were specified (none of them are wrap content)
		// 4. Both width and height are wrap content (it will always have the aspect ratio)
		if (drawable == null || drawable.getIntrinsicWidth() == 0 || drawable.getIntrinsicHeight() == 0 ||
				(requestedWidth != WRAP_CONTENT && requestedHeight != WRAP_CONTENT) ||
				(requestedWidth == WRAP_CONTENT && requestedHeight == WRAP_CONTENT)) {
			return null;
		}

		int width;
		int height;
		if (requestedWidth != WRAP_CONTENT) {
			width = measuredWidth;
			height = Math.round(width * drawable.getIntrinsicHeight() / (float) drawable.getIntrinsicWidth());
		} else {
			height = measuredHeight;
			width = Math.round(height * drawable.getIntrinsicWidth() / (float) drawable.getIntrinsicHeight());
		}

		return new Point(width, height);
	}
}
