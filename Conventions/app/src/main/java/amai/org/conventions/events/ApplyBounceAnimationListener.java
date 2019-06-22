package amai.org.conventions.events;

import android.view.View;
import android.view.ViewParent;

import androidx.viewpager.widget.ViewPager;

public class ApplyBounceAnimationListener implements View.OnClickListener {
	@Override
	public void onClick(View view) {
		applyBounceAnimation(view);
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