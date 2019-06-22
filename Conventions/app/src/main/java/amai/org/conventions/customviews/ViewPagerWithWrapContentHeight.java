package amai.org.conventions.customviews;

import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import amai.org.conventions.events.adapters.ListPagerAdapter;

public class ViewPagerWithWrapContentHeight extends ViewPager {
	private static final int UNSPECIFIED_HEIGHT_MEASURE_SPEC = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);

	public ViewPagerWithWrapContentHeight(Context context) {
		super(context);
	}

	public ViewPagerWithWrapContentHeight(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int height = 0;
		// Measure existing children
		for (int i = 0; i < getChildCount(); ++i) {
			View child = getChildAt(i);
			int childHeight = getHeight(widthMeasureSpec, child);
			if (childHeight > height) {
				height = childHeight;
			}
		}
		// Measure children which were not yet added to the view pager.
		// This is done because views are only attached when the view pager instantiates them.
		// In case they were already attached we don't need to measure them again.
		// Note that we don't call instantiateItem here to prevent attaching the view,
		// so this call is limited to adapters that are ListPagerAdapter instances
		// (this is not generic but good enough for our case right now)
		PagerAdapter adapter = getAdapter();
		if (adapter instanceof ListPagerAdapter) {
			ListPagerAdapter listPagerAdapter = (ListPagerAdapter) adapter;
			for (int i = 0; i < listPagerAdapter.getCount(); ++i) {
				View child = listPagerAdapter.getView(i);
				if (child.getParent() == null) {
					int childHeight = getHeight(widthMeasureSpec, child);
					if (childHeight > height) {
						height = childHeight;
					}
				}
			}
		}

		heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	private int getHeight(int widthMeasureSpec, View view) {
		if (view.getVisibility() == GONE) {
			return 0;
		}
		view.measure(widthMeasureSpec, UNSPECIFIED_HEIGHT_MEASURE_SPEC);
		return view.getMeasuredHeight();
	}
}
