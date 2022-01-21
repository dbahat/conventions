package amai.org.conventions.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import amai.org.conventions.R;

public class FrameLayoutWithMaxHeight extends FrameLayout {
	private int maxHeight;

	public FrameLayoutWithMaxHeight(@NonNull Context context) {
		super(context);
	}

	public FrameLayoutWithMaxHeight(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs, 0, 0);
	}

	public FrameLayoutWithMaxHeight(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs, defStyleAttr, 0);
	}

	public FrameLayoutWithMaxHeight(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context, attrs, defStyleAttr, defStyleRes);
	}

	private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		if (attrs == null) {
			return;
		}
		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FrameLayoutWithMaxHeight, defStyleAttr, defStyleRes);
		try {
			maxHeight = array.getDimensionPixelSize(R.styleable.FrameLayoutWithMaxHeight_maxHeight, -1);
		} finally {
			array.recycle();
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		if (maxHeight >= 0) {
			switch (heightMode) {
				case MeasureSpec.UNSPECIFIED:
					heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST);
					break;
				case MeasureSpec.AT_MOST:
					if (heightSize > maxHeight) {
						heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST);
					}
					break;
				case MeasureSpec.EXACTLY:
					if (heightSize > maxHeight) {
						heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.EXACTLY);
					}
					break;
			}
		}

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}
