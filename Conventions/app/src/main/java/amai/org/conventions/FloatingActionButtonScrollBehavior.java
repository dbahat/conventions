package amai.org.conventions;

import android.content.Context;
import android.graphics.Rect;
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
	private final Rect visibilityRect = new Rect();

	public FloatingActionButtonScrollBehavior(Context context, AttributeSet attrs) {
		super();
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
			float ratio = dependency.getY() / (float) dependency.getHeight();
			fab.setTranslationY(-distanceToScroll * ratio);

			fab.setTag(R.id.fab_translation, fab.getTranslationY());
			return true;
		} else if (dependency instanceof Snackbar.SnackbarLayout) {
			// When the animation begins, the snackbar isn't visible in the first frame and its translationY is 0,
			// so we shouldn't move the fab (it will flicker)
			if (dependency.getVisibility() != View.VISIBLE) {
				return false;
			}

			// Handling the snackbar manually because in edge to edge mode, the fab might have extra margins due to bottom insets,
			// which causes a big margin between it and the snackbar when managed by the CoordinatorLayout.
			// We set dodgeInsetEdges not to include bottom for this reason.

			// Don't move the fab unless it's fully visible (otherwise the snackbar appears to drag it from the bottom)
			boolean isVisible = fab.getGlobalVisibleRect(visibilityRect);
			if (!isVisible || visibilityRect.height() == 0 || visibilityRect.height() < fab.getMeasuredHeight()) {
				return false;
			}

			// Get fab bottom margin (without insets). This is the distance it should have from the snackbar.
			CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
			int fabBottomMargin = lp.bottomMargin;
			if (fab.getTag(R.id.inset_margins) instanceof Rect) {
				fabBottomMargin -= ((Rect) fab.getTag(R.id.inset_margins)).bottom;
			}

			// Get the current distance between the fab bottom and snackbar top.
			// fab bottom is calculated according to its position without considering the snackbar movements, only possible app bar changes.
			float lastTranslationY = 0;
			if (fab.getTag(R.id.fab_translation) instanceof Float) {
				lastTranslationY = (float) fab.getTag(R.id.fab_translation);
			}
			float fabBottom = fab.getTop() + fab.getMeasuredHeight() + lastTranslationY;
			float snackbarTop = dependency.getY();

			// If the current distance is less than the margin, set translation to the difference
			float moveBy = 0;
			if (snackbarTop - fabBottom < fabBottomMargin) {
				moveBy = fabBottomMargin - (snackbarTop - fabBottom);
			}

			fab.setTranslationY(lastTranslationY - moveBy);
			return true;
		}

		return super.onDependentViewChanged(parent, fab, dependency);
	}
}
