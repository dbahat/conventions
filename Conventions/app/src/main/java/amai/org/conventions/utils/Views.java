package amai.org.conventions.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.method.LinkMovementMethod;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.lang.reflect.Field;

public class Views {
	public static Point findCoordinates(ViewGroup parentView, View childView) {
		// getX() and getY() (and also getTop(), getBottom() etc) return the
		// coordinates of the view inside its parent. If a view is not directly inside
		// the scroll view, we need to accumulate the coordinates of all the parents.
		Point coordinates = new Point();
		while (childView != parentView) {
			coordinates.x += childView.getX();
			coordinates.y += childView.getY();
			if (!(childView.getParent() instanceof View)) {
				// Not inside parent view
				break;
			}

			childView = (View) childView.getParent();
		}
		return coordinates;
	}

	public static void hideKeyboardOnClickOutsideEditText(final Activity activity, View view) {
		//Set up touch listener for non-text box views to hide keyboard.
		if (!(view instanceof EditText)) {
			view.setOnTouchListener(new View.OnTouchListener() {
				public boolean onTouch(View v, MotionEvent event) {
					hideKeyboard(activity, v);
					return false;
				}
			});
		}

		//If a layout container, iterate over children and seed recursion.
		if (view instanceof ViewGroup) {
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				View innerView = ((ViewGroup) view).getChildAt(i);
				hideKeyboardOnClickOutsideEditText(activity, innerView);
			}
		}
	}

	private static void hideKeyboard(Activity activity, View view) {
		InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	public static void enableLinkClicks(ViewGroup parentView) {
		for (int i = 0; i < parentView.getChildCount(); ++i) {
			if (parentView.getChildAt(i) instanceof TextView) {
				((TextView) parentView.getChildAt(i)).setMovementMethod(LinkMovementMethod.getInstance());
			}
		}
	}

	/**
	 * Calculates the width of the widest view in an adapter, for use when you need to wrap_content on a ListView.
	 * Used for ListViews with a known (and small) number of items.
	 */
	public static int calculateWrapContentWidth(Context context, ListAdapter adapter) {
		int maxWidth = 0;
		View view = null;
		FrameLayout fakeParent = new FrameLayout(context);
		for (int i = 0, count = adapter.getCount(); i < count; ++i) {
			view = adapter.getView(i, view, fakeParent);
			view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
			int width = view.getMeasuredWidth();
			if (width > maxWidth) {
				maxWidth = width;
			}
		}
		return maxWidth;
	}

	public static Point getScreenSize(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return size;
	}

	public static View.OnTouchListener createOnSingleTapConfirmedListener(final Context context, final Runnable action) {
		return new View.OnTouchListener() {
			private GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
				@Override
				public boolean onSingleTapConfirmed(MotionEvent e) {
					action.run();
					return true;
				}
			});

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				gestureDetector.onTouchEvent(event);
				return true;
			}
		};
	}

	// Radial gradients don't support percentage before Lollipop, so we set the gradient radius in pixels instead
	// and use it to multiply against the view size in runtime.
	// Also we take the bigger size of the height and width instead of the smaller size because it makes more sense.
	public static void fixRadialGradient(final View view) {
		view.post(new Runnable() {
			@Override
			public void run() {
				// We don't check the gradient type here because this API was only added in SDK version 24
				// but gradientRadius doesn't do anything anyway for the other types so we can assume it's
				// a radial gradient if it has a value
				if (view.getBackground() != null && view.getBackground() instanceof GradientDrawable) {
					GradientDrawable gradient = (GradientDrawable) view.getBackground();
					float gradientRadius = gradient.getGradientRadius();
					if (gradientRadius > 0) {
						int viewSize;
						if (view.getMeasuredHeight() < view.getMeasuredWidth()) {
							viewSize = view.getMeasuredWidth();
						} else {
							viewSize = view.getMeasuredHeight();
						}
						gradient.mutate();
						gradient.setGradientRadius(gradientRadius * viewSize);
					}
				}
			}
		});
	}
}
