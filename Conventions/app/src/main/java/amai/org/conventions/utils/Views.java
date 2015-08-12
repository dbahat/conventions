package amai.org.conventions.utils;

import android.graphics.Point;
import android.view.View;
import android.view.ViewGroup;

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
}
