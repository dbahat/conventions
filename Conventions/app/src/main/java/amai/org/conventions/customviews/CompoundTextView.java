package amai.org.conventions.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import amai.org.conventions.R;

public class CompoundTextView extends TextView {
	private int mDrawableWidth;
	private int mDrawableHeight;
	private boolean initialized = false;

	public CompoundTextView(Context context) {
		super(context);
		init(context, null, 0, 0);
	}

	public CompoundTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs, 0, 0);
	}

	public CompoundTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs, defStyleAttr, 0);
	}

	public CompoundTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context, attrs, defStyleAttr, defStyleRes);
	}

	private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		initialized = true;
		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CompoundTextView, defStyleAttr, defStyleRes);

		try {
			mDrawableWidth = array.getDimensionPixelSize(R.styleable.CompoundTextView_compoundDrawableWidth, -1);
			mDrawableHeight = array.getDimensionPixelSize(R.styleable.CompoundTextView_compoundDrawableHeight, -1);
		} finally {
			array.recycle();
		}

		if (mDrawableWidth > 0 || mDrawableHeight > 0) {
			Drawable[] drawables = getCompoundDrawables();
			setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3]);
		}
	}

	private void calculateCompoundDrawablesSize(Drawable[] drawables) {
		for (Drawable drawable : drawables) {
			if (drawable == null) {
				continue;
			}
			int width = mDrawableWidth;
			int height = mDrawableHeight;

			Rect bounds = drawable.copyBounds();
			Point withAspectRatio = AspectRatioMeasurer.measure(width, height, drawable, bounds.width(), bounds.height());
			if (withAspectRatio != null) {
				width = withAspectRatio.x;
				height = withAspectRatio.y;
			}

			// If there's a missing width/height take it from the original bounds
			if (width < 0) {
				width = bounds.width();
			}
			if (height < 0) {
				height = bounds.height();
			}

			bounds.right = bounds.left + width;
			bounds.bottom = bounds.top + height;

			drawable.setBounds(bounds);
		}
	}

	@Override
	public void setCompoundDrawablesRelative(Drawable start, Drawable top, Drawable end, Drawable bottom) {
		if (initialized) {
			calculateCompoundDrawablesSize(new Drawable[]{start, top, end, bottom});
		}
		super.setCompoundDrawablesRelative(start, top, end, bottom);
	}

	@Override
	public void setCompoundDrawables(Drawable left, Drawable top, Drawable right, Drawable bottom) {
		if (initialized) {
			calculateCompoundDrawablesSize(new Drawable[]{left, top, right, bottom});
		}
		super.setCompoundDrawables(left, top, right, bottom);
	}
}
