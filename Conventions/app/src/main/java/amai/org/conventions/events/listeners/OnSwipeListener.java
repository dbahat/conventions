package amai.org.conventions.events.listeners;

import android.support.v4.view.ViewPager;

public class OnSwipeListener implements ViewPager.OnPageChangeListener {
	private ViewPager viewPager;
	private int mainViewPosition;
	private final Runnable action;
	private boolean dismiss;
	boolean pageSelected;

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
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		// If the page was selected (attending status changed) and scrolling has finished,
		// perform the actual action
		if (state != ViewPager.SCROLL_STATE_IDLE || !pageSelected) {
			return;
		}
		pageSelected = false;
		action.run();

		// Reset the layout state. Remove the listener so it isn't called when the current item is reset.
		viewPager.removeOnPageChangeListener(this);
		viewPager.setCurrentItem(mainViewPosition, false);
		viewPager.addOnPageChangeListener(this);
	}
}
