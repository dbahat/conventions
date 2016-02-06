package amai.org.conventions.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import fr.castorflex.android.verticalviewpager.VerticalViewPager;

public class NoSwipeVerticalViewPager extends VerticalViewPager {
	public NoSwipeVerticalViewPager(Context context) {
		super(context);
	}

	public NoSwipeVerticalViewPager(Context context, AttributeSet attributes) {
		super(context, attributes);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		// Never allow swiping to switch between pages
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// Never allow swiping to switch between pages
		return false;
	}
}
