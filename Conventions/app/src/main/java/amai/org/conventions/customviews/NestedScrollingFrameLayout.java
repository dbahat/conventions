package amai.org.conventions.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import org.jspecify.annotations.Nullable;

import androidx.annotation.NonNull;
import androidx.core.view.NestedScrollingChild3;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.NestedScrollingParent3;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;

public class NestedScrollingFrameLayout extends FrameLayout implements NestedScrollingParent3, NestedScrollingChild3, GestureDetector.OnGestureListener {
	private static int TOUCH_SLOP = -1;
	private final NestedScrollingChildHelper nestedScrollingChildHelper;
	private final NestedScrollingParentHelper nestedScrollingParentHelper;
	private final GestureDetector mDetector;
	private final int[] scrollOffset = new int[2];
	private boolean dummyScroll = false;
	private boolean isScrolling = false;
	private boolean isScrollingVertically = true;

	public NestedScrollingFrameLayout(Context context) {
		this(context, null);
	}

	public NestedScrollingFrameLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public NestedScrollingFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mDetector = new GestureDetector(context, this);
		nestedScrollingChildHelper = new NestedScrollingChildHelper(this);
		nestedScrollingParentHelper = new NestedScrollingParentHelper(this);
		ViewCompat.setNestedScrollingEnabled(this, true);

		final ViewConfiguration configuration = ViewConfiguration.get(context);
		TOUCH_SLOP = configuration.getScaledTouchSlop();
	}

	@Override
	public boolean isNestedScrollingEnabled() {
		return nestedScrollingChildHelper.isNestedScrollingEnabled();
	}

	@Override
	public void setNestedScrollingEnabled(boolean enabled) {
		nestedScrollingChildHelper.setNestedScrollingEnabled(enabled);
	}

	@Override
	public boolean hasNestedScrollingParent() {
		return nestedScrollingChildHelper.hasNestedScrollingParent();
	}

	@Override
	public boolean startNestedScroll(int axes) {
		return nestedScrollingChildHelper.startNestedScroll(axes);
	}

	@Override
	public void stopNestedScroll() {
		nestedScrollingChildHelper.stopNestedScroll();
	}

	@Override
	public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
		return nestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
	}

	@Override
	public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
		return nestedScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
	}

	@Override
	public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
		return nestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
	}

	@Override
	public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
		return nestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		nestedScrollingChildHelper.onDetachedFromWindow();
	}

	@Override
	public void onStopNestedScroll(View child) {
		nestedScrollingChildHelper.onStopNestedScroll(child);
		nestedScrollingParentHelper.onStopNestedScroll(child);
	}

	@Override
	public void onNestedScrollAccepted(View child, View target, int axes) {
		nestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes);
	}

	@Override
	public int getNestedScrollAxes() {
		return nestedScrollingParentHelper.getNestedScrollAxes();
	}

	@Override
	public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
		return true;
	}

	@Override
	public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
	}

	@Override
	public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
	}

	@Override
	public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
		return true;
	}

	@Override
	public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
		return false;
	}

	@Override
	public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
		// First handle this the toolbar scroll
		scrollOffset[0] = scrollOffset[1] = 0;
		mDetector.onTouchEvent(ev);
		if (ev.getActionMasked() == MotionEvent.ACTION_UP || ev.getActionMasked() == MotionEvent.ACTION_CANCEL) {
			ViewCompat.stopNestedScroll(this);
			isScrolling = false;
		}

		// Fix the current focus of the gesture detector by sending a dummy event
		// If this is not done, the gesture detector will assume an opposite motion event happened
		// and the toolbar will flicker up and down
		if (scrollOffset[1] != 0) {
			dummyScroll = true;
			// This must be a different motion event object because we have to send the original event
			// to our children (or weird things will happen, like scroll operations ending with tap)
			MotionEvent dummyEvent = MotionEvent.obtain(ev);
			dummyEvent.offsetLocation(0, -scrollOffset[1]);
			mDetector.onTouchEvent(dummyEvent);
			dummyScroll = false;
		}

		// Now the children can handle their own scroll
		super.dispatchTouchEvent(ev);
		return true;
	}

	@Override
	public boolean onTouchEvent(@NonNull MotionEvent event) {
		return false;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		ViewCompat.startNestedScroll(this, ViewCompat.SCROLL_AXIS_VERTICAL);
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		if (dummyScroll) {
			return false;
		}

		if (!isScrolling) {
			isScrolling = true;
			isScrollingVertically = Math.abs(distanceY) >= TOUCH_SLOP && Math.abs(distanceY) - Math.abs(distanceX) > 0;
		}

		if (!isScrollingVertically) {
			return false;
		}

		int[] consumed = new int[2];
		// Nested pre-scroll actually scrolls the toolbar. Consumed array contains the amount scrolled in the
		// toolbar and scrollOffset array contains the offset of this view after the scroll (we use it to adjust
		// the gesture detector's current focus point). I don't think it matters if we send the correct consumed values
		// during dispatchNestedScroll, but we already have them so why not.
		ViewCompat.dispatchNestedPreScroll(this, Math.round(distanceX), Math.round(distanceY), consumed, scrollOffset);
		ViewCompat.dispatchNestedScroll(this, Math.round(distanceX) - consumed[0], Math.round(distanceY) - consumed[1], 0, 0, null);
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		if (dummyScroll) {
			return false;
		}

		if (!isScrollingVertically) {
			return false;
		}

		// For some reason the AppBarLayout decides to show the appbar instead of hiding it on fling
		// so we have to send the opposite velocity
		ViewCompat.dispatchNestedPreFling(this, -velocityX, -velocityY);
		// If we send "false" the fling will only scroll the toolbar (and not the child views)
		ViewCompat.dispatchNestedFling(this, -velocityX, -velocityY, true);
		return false;
	}


	@Override
	public void onNestedScroll(@org.jspecify.annotations.NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type, int @org.jspecify.annotations.NonNull [] consumed) {
	}

	@Override
	public boolean onStartNestedScroll(@org.jspecify.annotations.NonNull View child, @org.jspecify.annotations.NonNull View target, int axes, int type) {
		return true;
	}

	@Override
	public void onNestedScrollAccepted(@org.jspecify.annotations.NonNull View child, @org.jspecify.annotations.NonNull View target, int axes, int type) {
		nestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes, type);
	}

	@Override
	public void onStopNestedScroll(@org.jspecify.annotations.NonNull View target, int type) {
		nestedScrollingParentHelper.onStopNestedScroll(target, type);
	}

	@Override
	public void onNestedScroll(@org.jspecify.annotations.NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
	}

	@Override
	public void onNestedPreScroll(@org.jspecify.annotations.NonNull View target, int dx, int dy, int @org.jspecify.annotations.NonNull [] consumed, int type) {
	}

	@Override
	public void dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int @Nullable [] offsetInWindow, int type, int @org.jspecify.annotations.NonNull [] consumed) {
		nestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type, consumed);
	}

	@Override
	public boolean startNestedScroll(int axes, int type) {
		return nestedScrollingChildHelper.startNestedScroll(axes, type);
	}

	@Override
	public void stopNestedScroll(int type) {
		nestedScrollingChildHelper.stopNestedScroll(type);
	}

	@Override
	public boolean hasNestedScrollingParent(int type) {
		return nestedScrollingChildHelper.hasNestedScrollingParent(type);
	}

	@Override
	public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int @Nullable [] offsetInWindow, int type) {
		return nestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type);
	}

	@Override
	public boolean dispatchNestedPreScroll(int dx, int dy, int @Nullable [] consumed, int @Nullable [] offsetInWindow, int type) {
		return nestedScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type);
	}
}
