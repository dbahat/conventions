package amai.org.conventions;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

/**
 * Scroll the action button off screen when the AppBarLayout is scrolled off screen by the same proportions.
 * Scroll the action button up when the snackbar is displayed (this is done by inheriting FloatingActionButton.Behavior
 * and calling super methods).
 */
public class FloatingActionButtonScrollBehavior extends FloatingActionButton.Behavior {
	private int toolbarHeight;

	public FloatingActionButtonScrollBehavior(Context context, AttributeSet attrs) {
		super();
		this.toolbarHeight = ThemeAttributes.getDimensionSize(context, android.R.attr.actionBarSize);
	}

	@Override
	public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton fab, View dependency) {
		return dependency instanceof AppBarLayout || super.layoutDependsOn(parent, fab, dependency);
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
		} else if (!isAnchoredToTop) {
			return super.onDependentViewChanged(parent, fab, dependency);
		}
		return false;
	}
}
