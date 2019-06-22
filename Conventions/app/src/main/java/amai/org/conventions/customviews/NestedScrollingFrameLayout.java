package amai.org.conventions.customviews;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.NestedScrollingChild;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class NestedScrollingFrameLayout extends FrameLayout implements NestedScrollingParent, NestedScrollingChild, GestureDetector.OnGestureListener {
	private static int TOUCH_SLOP = -1;
	private NestedScrollingChildHelper nestedScrollingChildHelper;
	private GestureDetectorCompat mDetector;
	private int[] scrollOffset = new int[2];
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
		mDetector = new GestureDetectorCompat(context, this);
		nestedScrollingChildHelper = new NestedScrollingChildHelper(this);
		setNestedScrollingEnabled(true);

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
	}

	@Override
	public void onNestedScrollAccepted(View child, View target, int axes) {
	}

	@Override
	public int getNestedScrollAxes() {
		return 0;
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
			stopNestedScroll();
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
		startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
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
		dispatchNestedPreScroll(Math.round(distanceX), Math.round(distanceY), consumed, scrollOffset);
		dispatchNestedScroll(Math.round(distanceX) - consumed[0], Math.round(distanceY) - consumed[1], 0, 0, null);
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
		dispatchNestedPreFling(-velocityX, -velocityY);
		// If we send "false" the fling will only scroll the toolbar (and not the child views)
		dispatchNestedFling(-velocityX, -velocityY, true);
		return false;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// Fix issue where this view is not full-size due to bug in AppBarLayout.Behavior onMeasureChild method - the sent
		// measure mode is AT_MOST instead of EXACTLY even for match_parent children of the CoordinatorLayout.
		// This was fixed already in the design library but not in the version we currently use (22.2.1).
		// Note that if any other views use this behavior we should make the same workaround (or better for that case,
		// create a new Behavior that works correctly).
		if (getLayoutParams().height == ViewGroup.LayoutParams.MATCH_PARENT && MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
			heightMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY);
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}
