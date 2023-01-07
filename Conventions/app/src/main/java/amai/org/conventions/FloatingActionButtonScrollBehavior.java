package amai.org.conventions;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import amai.org.conventions.customviews.NestedScrollingFrameLayout;
import amai.org.conventions.utils.Log;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import amai.org.conventions.R;

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

		if (isAnchoredToTop) {
			if (dependency instanceof AppBarLayout) {
				// Move the fab up as much as the app bar layout was changed
				fab.setTranslationY(dependency.getY());
				return true;
			}
			return super.onDependentViewChanged(parent, fab, dependency);
		}

		// Handle both AppBarLayout and SnackbarLayout
		if (dependency instanceof AppBarLayout) {
			CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
			int fabBottomMargin = lp.bottomMargin;

			// Check what is the height of the action button (with the bottom margin)
			int distanceToScroll = fab.getHeight() + fabBottomMargin;

			// Check how much of the app bar layout is off-screen
			float ratio = dependency.getY() / (float) toolbarHeight;
			fab.setTranslationY(-distanceToScroll * ratio);

			fab.setTag(R.id.fab_translation, fab.getTranslationY());
			return true;
		} else if (dependency instanceof Snackbar.SnackbarLayout) {
			// The CoordinatorLayout takes care of changing the action button location according to the snackbar,
			// but there is a bug where for a single frame the action button is shown above the snackbar while the
			// snackbar isn't visible (when the animation starts).
			// Fix this by moving the action button down by the snackbar height, and returning it to its regular location after.
			float lastTranslationY = 0;
			if (fab.getTag(R.id.fab_translation) instanceof Float) {
				lastTranslationY = (float) fab.getTag(R.id.fab_translation);
			}

			boolean fixedTranslation = false;
			if (fab.getTag(R.id.fab_fixed_translation) instanceof Boolean) {
				fixedTranslation = (boolean) fab.getTag(R.id.fab_fixed_translation);
			}

			if (!fixedTranslation) {
				fab.setTranslationY(dependency.getHeight() + lastTranslationY);
				fab.setTag(R.id.fab_fixed_translation, true);
			} else {
				fab.setTranslationY(lastTranslationY);
			}
			return true;
		}

		return super.onDependentViewChanged(parent, fab, dependency);
	}

	@Override
	public void onDependentViewRemoved(@NonNull CoordinatorLayout parent, @NonNull FloatingActionButton fab, @NonNull View dependency) {
		if (dependency instanceof Snackbar.SnackbarLayout) {
			fab.setTag(R.id.fab_fixed_translation, false);
		}
		super.onDependentViewRemoved(parent, fab, dependency);
	}
}
