package amai.org.conventions.events;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewParent;

public class ApplyBounceAnimationListener implements View.OnClickListener, View.OnLongClickListener {
	@Override
	public void onClick(View view) {
		applyBounceAnimation(view);
	}

	@Override
	public boolean onLongClick(View view) {
		applyBounceAnimation(view);
		return true;
	}

	private void applyBounceAnimation(View view) {
		ViewParent parent = view.getParent();
		while (parent != null && !(parent instanceof EventView)) {
			parent = parent.getParent();
		}
		if (parent != null) {
			// Only go 1 level up from the EventView so we don't accidentally animate some top-level view pager
			parent = parent.getParent();
		}
		if (parent instanceof ViewPager) {
			ViewPager pager = (ViewPager) parent;

			// In case the user clicks the button again while the bounce animation is still running, ignore the press
			if (pager.isFakeDragging()) {
				return;
			}
			ViewPagerAnimator.applyBounceAnimation(pager);
		}
	}
}