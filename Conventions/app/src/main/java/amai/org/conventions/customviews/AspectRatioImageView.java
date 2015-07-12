package amai.org.conventions.customviews;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
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
		if (drawable == null || drawable.getIntrinsicWidth() == 0) {
			return;
		}

		int width;
		int height;
		if (getWidth() != ViewGroup.LayoutParams.WRAP_CONTENT) {
			width = getMeasuredWidth();
			height = (int) (width * drawable.getIntrinsicHeight() / (float) drawable.getIntrinsicWidth());
		} else {
			height = getMeasuredHeight();
			width = (int) (height * drawable.getIntrinsicWidth() / (float) drawable.getIntrinsicHeight());
		}
		setMeasuredDimension(width, height);
	}
}
