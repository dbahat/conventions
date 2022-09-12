package amai.org.conventions;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import amai.org.conventions.utils.Log;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

/**
 * Scroll the action button off screen when the AppBarLayout is scrolled off screen by the same proportions.
 * Scroll the action button up when the snackbar is displayed (this is done by the CoordinatorLayout automatically because we set the anchor view) and
 * fix the CoordinatorLayout behavior which flickers the action button.
 */
public class FloatingActionButtonScrollBehavior extends FloatingActionButton.Behavior {
	private int toolbarHeight;

	public FloatingActionButtonScrollBehavior(Context context, AttributeSet attrs) {
		super();
		this.toolbarHeight = ThemeAttributes.getDimensionSize(context, android.R.attr.actionBarSize);
	}

	@Override
	public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton fab, View dependency) {
		return dependency instanceof AppBarLayout || dependency instanceof Snackbar.SnackbarLayout || super.layoutDependsOn(parent, fab, dependency);
	}

	@Override
	public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton fab, View dependency) {
		boolean isAnchoredToTop = false;
		if (fab.getLayoutParams() instanceof CoordinatorLayout.LayoutParams) {
			isAnchoredToTop = (((CoordinatorLayout.LayoutParams) fab.getLayoutParams()).anchorGravity & Gravity.TOP) == Gravity.TOP;
		}

		if (dependency instanceof AppBarLayout) {
			if (isAnchoredToTop) {
				// Move the fab up as much as the app bar layout was changed
				fab.setTranslationY(dependency.getY());
			} else {
				CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
				int fabBottomMargin = lp.bottomMargin;

				// Check what is the height of the action button (with the bottom margin)
				int distanceToScroll = fab.getHeight() + fabBottomMargin;

				// Check how much of the app bar layout is off-screen
				float ratio = dependency.getY() / (float) toolbarHeight;
				fab.setTranslationY(-distanceToScroll * ratio);
			}
			return true;
		} else if (!isAnchoredToTop && dependency instanceof Snackbar.SnackbarLayout) {
			// The CoordinatorLayout takes care of changing the action button location according to the snackbar,
			// but there is a bug where for a single frame the action button is shown above the snackbar while the
			// snackbar isn't visible (when the animation starts).
			// Fix this by moving the action button down by the snackbar height, and returning it to its regular location after.
			if (dependency.getVisibility() != View.VISIBLE) {
				fab.setTranslationY(dependency.getHeight());
			} else {
				fab.setTranslationY(0);
			}
			return true;
		} else if (!isAnchoredToTop) {
			return super.onDependentViewChanged(parent, fab, dependency);
		}
		return false;
	}
}
