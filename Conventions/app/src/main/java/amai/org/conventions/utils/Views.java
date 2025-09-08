package amai.org.conventions.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Insets;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.method.LinkMovementMethod;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import sff.org.conventions.R;

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

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			final WindowMetrics metrics = wm.getCurrentWindowMetrics();

			// Gets insets size.
			// Should be in sync with the implementation in registerApplyInsets
			final WindowInsets windowInsets = metrics.getWindowInsets();
			Insets insets = windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
			int insetsWidth = insets.right + insets.left;
			int insetsHeight = insets.top + insets.bottom;

			// Calculate size with insets (i.e. only the size the app layout actually uses)
			final Rect bounds = metrics.getBounds();
			Point size = new Point(bounds.width() - insetsWidth, bounds.height() - insetsHeight);
			return size;
		}

		// Old implementation - remove when minSdk is at least 30. This is not accurate but good enough for our needs.
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return size;
	}

	public enum InsetType { PADDING, MARGIN, NONE; }
	public static void registerApplyInsets(InsetType applyToTop, InsetType applyToBottom, InsetType applyToSides, View... views) {
		for (View view : views) {
			registerApplyInsetsForView(view, applyToTop, applyToBottom, applyToSides);
		}
	}

	public static void registerApplyInsetsForView(View view, InsetType applyToTop, InsetType applyToBottom, InsetType applyToSides) {
		// Only apply the listener once for each view
		if (view == null || view.getTag(R.id.inset_listener_applied) == Boolean.TRUE) {
			return;
		}

		// Get original margins
		ViewGroup.LayoutParams originalLayoutParams = view.getLayoutParams();
		ViewGroup.MarginLayoutParams marginParamsCopy = null;

		Rect marginsCopy = new Rect();
		if (originalLayoutParams instanceof ViewGroup.MarginLayoutParams) {
			ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) originalLayoutParams;
			marginsCopy = new Rect(marginParams.leftMargin, marginParams.topMargin, marginParams.rightMargin, marginParams.bottomMargin);
		}
		Rect originalMargins = marginsCopy;

		int origHeight = view.getLayoutParams().height;
		int origWidth = view.getLayoutParams().width;
		int origTopPadding = view.getPaddingTop();
		int origBottomPadding = view.getPaddingBottom();
		int origLeftPadding = view.getPaddingLeft();
		int origRightPadding = view.getPaddingRight();

		// Apply system bar insets to the root view
		ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
			// displayCutout is the top part of the screen where the camera is. It's included in the system bars in portrait mode.
			// In landscape mode the app looks ok drawing behind it in all screens, so we ignore it.
			// Should be in sync with the implementation in getScreenSize
			androidx.core.graphics.Insets systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());

			// Apply new margins
			ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
			if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
				ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;

				Rect insetMargins = new Rect();
				insetMargins.left = (applyToSides == InsetType.MARGIN ? systemInsets.left : 0);
				insetMargins.right = (applyToSides == InsetType.MARGIN ? systemInsets.right : 0);
				insetMargins.top = (applyToTop == InsetType.MARGIN ? systemInsets.top : 0);
				insetMargins.bottom = (applyToBottom == InsetType.MARGIN ? systemInsets.bottom : 0);

				// Add insets to original margins
				marginLayoutParams.leftMargin = originalMargins.left + insetMargins.left;
				marginLayoutParams.rightMargin = originalMargins.right + insetMargins.right;
				marginLayoutParams.topMargin = originalMargins.top + insetMargins.top;
				marginLayoutParams.bottomMargin = originalMargins.bottom + insetMargins.bottom;
				v.setLayoutParams(layoutParams);
				v.setTag(R.id.inset_margins, insetMargins);
			}
			if (applyToTop == InsetType.PADDING || applyToBottom == InsetType.PADDING || applyToSides == InsetType.PADDING) {
				Rect insetPadding = new Rect();
				insetPadding.left = applyToSides == InsetType.PADDING ? systemInsets.left : 0;
				insetPadding.right = applyToSides == InsetType.PADDING ? systemInsets.right : 0;
				insetPadding.top = applyToTop == InsetType.PADDING ? systemInsets.top : 0;
				insetPadding.bottom = applyToBottom == InsetType.PADDING ? systemInsets.bottom : 0;

				int paddingLeft = origLeftPadding + insetPadding.left;
				int paddingRight = origRightPadding + insetPadding.right;
				int paddingTop = origTopPadding + insetPadding.top;
				int paddingBottom = origBottomPadding + insetPadding.bottom;
				v.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
				v.setTag(R.id.inset_padding, insetPadding);
				if (origHeight >= 0) {
					v.getLayoutParams().height = origHeight + insetPadding.top + insetPadding.bottom;
					v.setLayoutParams(v.getLayoutParams());
				}
				if (origWidth >= 0) {
					v.getLayoutParams().width = origWidth + insetPadding.left + insetPadding.right;
					v.setLayoutParams(v.getLayoutParams());
				}
			}

			return insets;
		});
		view.setTag(R.id.inset_listener_applied, Boolean.TRUE);
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
