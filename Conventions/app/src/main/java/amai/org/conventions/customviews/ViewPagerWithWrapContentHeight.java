package amai.org.conventions.customviews;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

public class ViewPagerWithWrapContentHeight extends ViewPager {
	public ViewPagerWithWrapContentHeight(Context context) {
		super(context);
	}

	public ViewPagerWithWrapContentHeight(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int height = 0;
		for(int i = 0; i < getChildCount(); ++i) {
			View child = getChildAt(i);
			if (child.getVisibility() == GONE) {
				continue;
			}
			child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
			int childHeight = child.getMeasuredHeight();
			if(childHeight > height) {
				height = childHeight;
			}
		}

		heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}
