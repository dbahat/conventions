package amai.org.conventions.utils;

import android.app.Activity;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

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
}
