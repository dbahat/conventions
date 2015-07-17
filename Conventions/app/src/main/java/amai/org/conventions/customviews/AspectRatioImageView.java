package amai.org.conventions.customviews;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

public class AspectRatioImageView extends ImageView {
	public AspectRatioImageView(Context context) {
		super(context);
	}

	public AspectRatioImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AspectRatioImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		Drawable drawable = getDrawable();
		int wrapContent = ViewGroup.LayoutParams.WRAP_CONTENT;
		int requestedWidth = getLayoutParams().width;
		int requestedHeight = getLayoutParams().height;

		// Not calculating according to image aspect ratio in the following cases:
		// 1. No image
		// 2. No width or height
		// 3. Both width and height were specified (none of them are wrap content)
		// 4. Both width and height are wrap content (it will always have the aspect ratio)
		if (drawable == null || drawable.getIntrinsicWidth() == 0 || drawable.getIntrinsicHeight() == 0 ||
				(requestedWidth != wrapContent && requestedHeight != wrapContent) ||
				(requestedWidth == wrapContent && requestedHeight == wrapContent)) {
			return;
		}

		int width;
		int height;
		if (requestedWidth != wrapContent) {
			width = getMeasuredWidth();
			height = (int) (width * drawable.getIntrinsicHeight() / (float) drawable.getIntrinsicWidth());
		} else {
			height = getMeasuredHeight();
			width = (int) (height * drawable.getIntrinsicWidth() / (float) drawable.getIntrinsicHeight());
		}
		setMeasuredDimension(width, height);
	}
}
