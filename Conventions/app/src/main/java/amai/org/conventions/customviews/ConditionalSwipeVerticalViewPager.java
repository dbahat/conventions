package amai.org.conventions.customviews;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;

import fr.castorflex.android.verticalviewpager.VerticalViewPager;

public class ConditionalSwipeVerticalViewPager extends VerticalViewPager {
	private Condition condition;

	public ConditionalSwipeVerticalViewPager(Context context) {
		super(context);
	}

	public ConditionalSwipeVerticalViewPager(Context context, AttributeSet attributes) {
		super(context, attributes);
	}

	public void setCondition(Condition condition) {
		this.condition = condition;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		// Check if we should allow swiping to switch between pages
		boolean enableSwipe = condition == null || condition.shouldSwipe();
		if (!enableSwipe && MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_MOVE) {
			return false;
		}
		return super.onInterceptTouchEvent(event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// Check if we should allow swiping to switch between pages
		boolean enableSwipe = condition == null || condition.shouldSwipe();
		if (!enableSwipe && MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_MOVE) {
			return false;
		}
		return super.onTouchEvent(event);
	}

	public interface Condition {
		boolean shouldSwipe();
	}
}
