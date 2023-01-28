package amai.org.conventions.events;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import androidx.viewpager.widget.ViewPager;

public class ViewPagerBounceAnimator {

	public static void apply(ViewPager pager) {
		apply(pager, 150, 250);
	}

	public static void apply(final ViewPager pager, final int offset, final int delay) {

		final AnimatorSet set = new AnimatorSet();

		ValueAnimator.AnimatorUpdateListener listener = new ValueAnimator.AnimatorUpdateListener() {
			private float previousDragValue = 0;

			public void onAnimationUpdate(ValueAnimator animation) {
				float value = (float) animation.getAnimatedValue();
				if (value != previousDragValue && pager.getCurrentItem() >= 0) {
					// If the view is touched while being dragged and we don't call beginFakeDrag() before
					// it there is a NullPointerException in android code - see https://github.com/JakeWharton/ViewPagerIndicator/pull/257
					pager.beginFakeDrag();

					// Since there is no way to animate fake viewPager dragging, simulate it by starting an animation sequence, and inform the
					// pager to fake drag by a small margin during each animation frame.
					float amountToDrag = value - previousDragValue;
					pager.fakeDragBy(amountToDrag);
					previousDragValue = value;
				}
			}
		};

		set.setDuration(delay);

		// Open, close, half-open, half-close for a bounce effect
		set.playSequentially(
				ValueAnimator.ofFloat(0, offset),
				ValueAnimator.ofFloat(offset, 0),
				ValueAnimator.ofFloat(0, offset / 4),
				ValueAnimator.ofFloat(offset / 4, 0));

		// Register the listener to all the animation sequences
		for (Animator animator : set.getChildAnimations()) {
			((ValueAnimator) animator).addUpdateListener(listener);
		}

		set.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(Animator animation) {
				pager.beginFakeDrag();
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				pager.endFakeDrag();
			}
		});

		set.start();
	}
}
