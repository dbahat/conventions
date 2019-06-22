package amai.org.conventions.events.listeners;

import androidx.viewpager.widget.ViewPager;

public class OnSwipeListener implements ViewPager.OnPageChangeListener {
	private final Runnable action;
	private ViewPager viewPager;
	private int mainViewPosition;
	private boolean dismiss;
	private boolean pageSelected;
	private Runnable delayedSwipeActionPerformer = new Runnable() {
		@Override
		public void run() {
			if (pageSelected) {
				performSwipeAction();
			}
		}
	};

	public OnSwipeListener(ViewPager viewPager, int mainViewPosition, Runnable action, boolean dismiss) {
		this.viewPager = viewPager;
		this.mainViewPosition = mainViewPosition;
		this.action = action;
		this.dismiss = dismiss;
		pageSelected = false;
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		if (dismiss) {
			int viewWidth = viewPager.getMeasuredWidth();

			// Depending on the side we're swiping to, the scroll could be attributed to a different page.
			// So we calculate the offset from the main view position in absolute terms.
			positionOffsetPixels = Math.abs(positionOffsetPixels - (mainViewPosition - position) * viewWidth);

			// The closer we are to offset 0, the closest the progress should be to 1 (opaque). The progress is non-linear
			// to make it appear smoother.
			float progress = 1 - (positionOffsetPixels * 1.5f / (float) viewWidth);
			viewPager.setAlpha(progress);
		}
	}

	@Override
	public void onPageSelected(int p) {
		// Only keep track that the page changed since this method is called before the scroll finished
		// so we can't perform UI updates here (because it will be immediate and the event view will appear
		// to be stuck)
		pageSelected = true;

		// In certain cases, onPageScrollStateChanged is not called after onPageSelected. If it does not happen
		// within half a second (which is enough time for the scroll to finish), perform the action anyway.
		viewPager.postDelayed(delayedSwipeActionPerformer, 500);
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		// If the page was selected (attending status changed) and scrolling has finished,
		// perform the actual action
		if (state != ViewPager.SCROLL_STATE_IDLE || !pageSelected) {
			return;
		}
		performSwipeAction();
	}

	private synchronized void performSwipeAction() {
		// Since this can be called from 2 places, making sure they aren't trying to call at the same time
		if (!pageSelected) {
			return;
		}
		// Remove callback in case it was not called and it will try to run later
		viewPager.removeCallbacks(delayedSwipeActionPerformer);

		pageSelected = false;
		action.run();

		// Reset the layout state. Remove the listener so it isn't called when the current item is reset.
		viewPager.removeOnPageChangeListener(this);
		viewPager.setCurrentItem(mainViewPosition, false);
		viewPager.addOnPageChangeListener(this);
	}
}
