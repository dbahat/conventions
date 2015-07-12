package amai.org.conventions.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class InterceptorLinearLayout extends LinearLayout {
	public interface OnInterceptTouchListener {
		boolean onInterceptTouchEvent(MotionEvent event);
	}

	public interface AllTouchEventsListener extends OnInterceptTouchListener, OnTouchListener{
	}

	private OnInterceptTouchListener listener = null;


	public InterceptorLinearLayout(Context context) {
		super(context);
	}

	public InterceptorLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public InterceptorLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public void setOnInterceptTouchEventListener(OnInterceptTouchListener listener) {
		this.listener = listener;
	}

	@Override
	public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
		super.requestDisallowInterceptTouchEvent(disallowIntercept);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		if (listener != null && listener.onInterceptTouchEvent(event)) {
			return true;
		}
		return super.onInterceptTouchEvent(event);
	}
}
